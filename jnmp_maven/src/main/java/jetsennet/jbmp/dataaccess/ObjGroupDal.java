/************************************************************************
日 期：2011-11-28
作 者:
版 本：v1.3
描 述: 对象组的数据库操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.business.RoleGroup;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.entity.ObjGroupEntity;
import jetsennet.jbmp.util.IPv4AddressUtil;
import jetsennet.jbmp.util.StringUtil;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.apache.log4j.Logger;

/**
 * 对象组的数据库操作
 * @author
 */
public class ObjGroupDal extends DefaultDal<ObjGroupEntity>
{

    private static final Logger logger = Logger.getLogger(ObjGroupDal.class);

    /**
     * 构造函数
     */
    public ObjGroupDal()
    {
        super(ObjGroupEntity.class);
    }

    /**
     * 把对象添加到网段组，如果该网段组不存在则添加新的网段组
     * @param mo 对象
     * @param userId 用户id
     */
    @Transactional
    public void insertIntoIpGroup(MObjectEntity mo, int userId)
    {
        long mipn = IPv4AddressUtil.stringToLong(mo.getIpAddr());
        if (mipn < 0)
        {
            return;
        }
        try
        {
            // 获取用户对应的角色
            ArrayList<Integer> roles = null;
            Role2GroupDal r2gdal = ClassWrapper.wrapTrans(Role2GroupDal.class);
            RoleGroup rgdal = ClassWrapper.wrapTrans(RoleGroup.class);
            if (userId > 0 && r2gdal.checkExist())
            {
                roles = rgdal.getRoleId(userId);
            }

            Obj2GroupDal ogdal = new Obj2GroupDal();
            List<ObjGroupEntity> groups = this.getByGroupType(ObjGroupEntity.GROUP_TYPE_IPSEGMENT);
            ArrayList<Integer> groupIds = new ArrayList<Integer>();
            List<Obj2GroupEntity> ogs = new ArrayList<Obj2GroupEntity>();
            for (ObjGroupEntity group : groups)
            {
                groupIds.add(group.getGroupId());
                long beginIpn = IPv4AddressUtil.stringToLong(group.getField4());
                long endIpn = IPv4AddressUtil.stringToLong(group.getField5());
                if (beginIpn < 0 || endIpn < 0)
                {
                    logger.error("监控组:" + group.getGroupId() + "，开始IP:<" + group.getField4() + ">和结束IP:<" + group.getField5() + ">不合格");
                }
                else
                {
                    if (mipn >= beginIpn && mipn <= endIpn)
                    {
                        ogs.add(new Obj2GroupEntity(mo.getObjId(), group.getGroupId(), Obj2GroupEntity.USE_TYPE_DEF));
                        if (roles != null && roles.size() > 0)
                        {
                            rgdal.insert(group.getGroupId(), roles);
                        }
                    }
                }
            }
            if (ogs != null && ogs.size() > 0)
            {
                // 更新对象所属的网段组
                ogdal.insert(mo.getObjId(), ogs);
            }
            else
            {
                ObjGroupEntity oge = this.newIpSegment(mo.getIpAddr());
                if (oge != null)
                {
                    this.insert(oge);
                    ogdal.insert(new Obj2GroupEntity(mo.getObjId(), oge.getGroupId(), Obj2GroupEntity.USE_TYPE_DEF));
                    if (roles != null && roles.size() > 0)
                    {
                        rgdal.insert(oge.getGroupId(), roles);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 根据类型获取全部属性分类
     * @param groupType 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<ObjGroupEntity> getByGroupType(int groupType) throws Exception
    {
        SqlCondition cond =
            new SqlCondition("GROUP_TYPE", Integer.toString(groupType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.getLst(cond);
    }

    /**
     * 根据GROUPCODE获取全部属性分类
     * @param groupType 参数
     * @param groupCode 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ObjGroupEntity getByGroupCodeAndType(String groupCode, int groupType) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("GROUP_CODE", groupCode, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("GROUP_TYPE", Integer.toString(groupType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return this.get(conds);
    }

    /**
     * 刷新对象和所属网段组的关系
     */
    @Transactional
    public void refreshIpGroup()
    {
        try
        {
            MObjectDal modal = new MObjectDal();
            Obj2GroupDal ogdal = new Obj2GroupDal();
            List<MObjectEntity> mos = modal.getAll();
            List<ObjGroupEntity> groups = this.getByGroupType(ObjGroupEntity.GROUP_TYPE_IPSEGMENT);
            ArrayList<Integer> groupIds = new ArrayList<Integer>();
            List<Obj2GroupEntity> ogs = new ArrayList<Obj2GroupEntity>();
            for (ObjGroupEntity group : groups)
            {
                groupIds.add(group.getGroupId());
                long beginIpn = IPv4AddressUtil.stringToLong(group.getField4());
                long endIpn = IPv4AddressUtil.stringToLong(group.getField5());
                if (beginIpn < 0 || endIpn < 0)
                {
                    logger.error("监控组:" + group.getGroupId() + "开始IP:<" + group.getField4() + ">和结束IP:<" + group.getField5() + ">不合格");
                    continue;
                }
                for (MObjectEntity mo : mos)
                {
                    String mips = mo.getIpAddr();
                    long mipn = IPv4AddressUtil.stringToLong(mips);
                    if (mipn > 0 && mipn >= beginIpn && mipn <= endIpn)
                    {
                        ogs.add(new Obj2GroupEntity(mo.getObjId(), group.getGroupId(), Obj2GroupEntity.USE_TYPE_DEF));
                    }
                }
            }
            ogdal.refreshIpGroupRel(groupIds, ogs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 新建网段
     * @param ip
     */
    private ObjGroupEntity newIpSegment(String ip)
    {
        if (!IPv4AddressUtil.isLegalAddress(ip))
        {
            return null;
        }
        ObjGroupEntity retval = new ObjGroupEntity();
        ip = ip.substring(0, ip.lastIndexOf("."));
        String begin = ip + ".1";
        String end = ip + ".255";
        String xname = ip + ".x";
        retval.setGroupType(ObjGroupEntity.GROUP_TYPE_IPSEGMENT);
        retval.setGroupName(xname);
        retval.setGroupDesc("自动生成网段组。开始IP：" + begin + "，结束IP：" + end);
        retval.setGroupState(ObjGroupEntity.GROUP_STATE_DEFAULT);
        retval.setCreateUser("SYSTEM");
        retval.setField4(begin);
        retval.setField5(end);
        return retval;
    }

    /**
     * 根据采集器ID获取采集组ID，无法获取采集组时返回-1
     * @param collId 参数
     * @return 结果
     */
    @Transactional
    public int getGroupIdByCollId(int collId)
    {
        SqlCondition[] conds =
            new SqlCondition[] {
                new SqlCondition("NUM_VAL1", Integer.toString(collId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("GROUP_TYPE", Integer.toString(ObjGroupEntity.GROUP_TYPE_COLLECT), SqlLogicType.And, SqlRelationType.Equal,
                    SqlParamType.Numeric) };
        try
        {
            ObjGroupEntity og = this.get(conds);
            if (og != null)
            {
                return og.getGroupId();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return -1;
    }

    /**
     * 保存拓扑图时调用
     * @param groupId 对象组id
     * @param groupType 对象组类型
     * @param groupName 对象组名称
     * @param groupState 对象组状态
     * @param username 用户名称
     * @throws Exception 异常
     */
    public void saveObjGroupFromTOPO(int groupId, int groupType, String groupName, int groupState, String username) throws Exception
    {
        ObjGroupEntity entity = new ObjGroupEntity();
        entity.setGroupId(groupId);
        entity.setGroupType(groupType);
        entity.setGroupName(groupName);
        entity.setGroupState(groupState);
        entity.setCreateUser(username);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        insert(entity, false);
    }

    /**
     * 批量设置组
     * @param objIds
     * @param types
     * @param type2group
     */
    @Transactional
    public void batchSetGroup(List<Integer> objIds, List<Integer> types, List<Integer> groupIds)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            for (Integer objId : objIds)
            {
                sb.append(objId).append(",");
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            String objIdStr = sb.toString();

            sb = new StringBuilder();
            for (Integer type : types)
            {
                sb.append(type).append(",");
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            String typeStr = sb.toString();

            String delStr =
                "DELETE FROM BMP_OBJ2GROUP WHERE OBJ_ID IN (" + objIdStr
                    + ") AND GROUP_ID IN (SELECT GROUP_ID FROM BMP_OBJGROUP WHERE GROUP_TYPE IN (" + typeStr + "))";
            delete(delStr);

            Obj2GroupDal o2gDal = new Obj2GroupDal();
            for (int objId : objIds)
            {
                for (int groupId : groupIds)
                {
                    Obj2GroupEntity o2g = new Obj2GroupEntity();
                    o2g.setGroupId(groupId);
                    o2g.setObjId(objId);
                    o2g.setUseType(Obj2GroupEntity.USE_TYPE_DEF);
                    o2gDal.insert(o2g);
                }
            }
        }
        catch (Exception ex)
        {

        }
    }
}
