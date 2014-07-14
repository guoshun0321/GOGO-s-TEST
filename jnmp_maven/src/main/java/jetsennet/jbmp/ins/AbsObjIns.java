/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 对象实例化抽象类
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.ObjGroupDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.ins.helper.AbsAttrsIns;
import jetsennet.jbmp.ins.helper.AbsInsRstHandle;
import jetsennet.jbmp.ins.helper.AttrsInsDef;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.ins.helper.InsRstHandleUnsave;
import jetsennet.jbmp.util.InsUtil;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * 实例化抽象类
 * @author 郭祥
 */
public abstract class AbsObjIns
{

    /**
     * 是否自动实例化
     */
    private boolean isAuto;
    /**
     * 实例化方式集合
     */
    protected ArrayList<AbsAttrsIns> inss;
    /**
     * 实例化方式，属性类型为索引。-1对应的为默认实例化方式。
     */
    protected Map<Integer, AbsAttrsIns> type2ins;
    /**
     * 实例化结果处理方式集合
     */
    protected ArrayList<AbsInsRstHandle> handles;
    /**
     * 实例化结果处理方式，属性类型为索引。
     */
    protected Map<Integer, AbsInsRstHandle> type2handle;
    /**
     * 默认类型
     */
    private static final int TYPE_DEFAULT = -1;
    /**
     * 日志
     */
    public final Logger logger = Logger.getLogger(AbsObjIns.class);

    /**
     * 构造函数
     */
    public AbsObjIns()
    {
        inss = new ArrayList<AbsAttrsIns>();
        type2ins = new HashMap<Integer, AbsAttrsIns>();
        handles = new ArrayList<AbsInsRstHandle>();
        type2handle = new HashMap<Integer, AbsInsRstHandle>();

        // 默认实例化方式
        AttrsInsDef dins = new AttrsInsDef();
        inss.add(dins);
        type2ins.put(TYPE_DEFAULT, dins);

        // 默认结果处理方式
        InsRstHandleUnsave uirh = new InsRstHandleUnsave();
        handles.add(uirh);
        type2handle.put(TYPE_DEFAULT, uirh);

        this.init();
    }

    /**
     * 初始化
     */
    protected void init()
    {
        this.initInsClasses();
        this.initRstHandleClasses();
    }

    /**
     * 自动实例化，传入需要实例化的对象属性，实例化属性并保存到数据库
     * @param mo 被实例化对象
     * @param attrs 需要实例化的属性
     * @param collId 采集器ID
     * @param groupId 采集组ID
     * @param infos 附加信息
     * @param userId 用户id
     * @param isLocal 是否本地实例化
     * @throws InstanceException 异常
     */
    public void autoIns(MObjectEntity mo, List<AttributeEntity> attrs, InsConfig config, int collId, int groupId, int userId,
            Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        // 对象实例化前的操作
        this.beforeAutoIns(mo, attrs, collId, groupId, userId, infos);
        if (attrs != null && !attrs.isEmpty())
        {
            // 调用实例化方法
            AttrsInsResult result = this.getInsResult(mo, attrs, collId, infos, isLocal);

            // 处理实例化结果
            if (result != null && result.getOutput() != null)
            {
                ArrayList<ObjAttribEntity> oas = result.getOutput();
                for (ObjAttribEntity oa : oas)
                {
                    AbsInsRstHandle handle = this.getRstHandle(oa.getAttribType());
                    handle.addObjAttr(oa);
                }

                if (handles != null && !handles.isEmpty())
                {
                    for (AbsInsRstHandle handle : handles)
                    {
                        handle.handle();
                    }
                }
            }
        }

        // 实例化子对象
        if (config.isInsSub())
        {
            this.autoInsSub(mo, collId, infos, isLocal);
        }
    }

    /**
     * 实例化。传入需要实例化的属性，传出实例化结果。
     * @param mo 对象
     * @param attrs 属性
     * @param collId 采集
     * @param infos 信息
     * @param isLocal 是否本地实例化
     * @return 结果
     * @throws InstanceException 异常
     */
    public AttrsInsResult getInsResult(MObjectEntity mo, List<AttributeEntity> attrs, int collId, Map<String, String> infos, boolean isLocal)
            throws InstanceException
    {
        if (mo == null || attrs == null || attrs.isEmpty())
        {
            return null;
        }
        AttrsInsResult retval = new AttrsInsResult();
        ArrayList<AttrsInsResult> results = new ArrayList<AttrsInsResult>();
        for (AttributeEntity attr : attrs)
        {
            if (attr != null)
            {
                AbsAttrsIns ins = this.getIns(attr.getAttribType());
                if (ins != null)
                {
                    ins.addAttr(attr);
                }
            }
        }
        if (!inss.isEmpty())
        {
            for (AbsAttrsIns ins : inss)
            {
                ins.setAuto(this.isAuto);
                ins.ins(mo, collId, infos, isLocal);
                AttrsInsResult result = ins.getResult();
                results.add(result);
            }
            retval = InsUtil.mergeResult(results);
        }
        return retval;
    }

    /**
     * 自动实例化子对象
     * @param mo 对象
     * @param collId 采集器
     * @param infos 信息
     * @param isLocal 是否本地实例化
     */
    public void autoInsSub(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal)
    {
    }

    /**
     * 初始化实例化方式，确定不同类型属性的实例化方式
     */
    protected void initInsClasses()
    {
    }

    /**
     * 获取实例化方式
     * @param type
     * @return
     */
    protected AbsAttrsIns getIns(int type)
    {
        AbsAttrsIns retval = type2ins.get(type);
        return retval == null ? type2ins.get(TYPE_DEFAULT) : retval;
    }

    /**
     * 实例化结果处理
     */
    protected void initRstHandleClasses()
    {
    }

    /**
     * 实例化结果处理
     * @param type
     * @return
     */
    protected AbsInsRstHandle getRstHandle(int type)
    {
        AbsInsRstHandle retval = type2handle.get(type);
        return retval == null ? type2handle.get(TYPE_DEFAULT) : retval;
    }

    /**
     * 自动实例化之前的动作
     */
    protected void beforeAutoIns(MObjectEntity mo, List<AttributeEntity> attrs, int collId, int groupId, int userId, Map<String, String> infos)
            throws InstanceException
    {
        try
        {
            // 更新对象的监控组关系
            ObjGroupDal ogdal = ClassWrapper.wrapTrans(ObjGroupDal.class);
            ogdal.insertIntoIpGroup(mo, userId);
            // 把对象加入到采集组
            Obj2GroupDal o2gdal = ClassWrapper.wrapTrans(Obj2GroupDal.class);
            if (groupId > 0)
            {
                // 新建对象时为了防止重复插入表，在插入之前，先删除；其中删除的数据可能不存在，则是空删除
                o2gdal.delete(new SqlCondition("GROUP_ID", Integer.toString(groupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("OBJ_ID", Integer.toString(mo.getObjId()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("USE_TYPE", Integer.toString(Obj2GroupEntity.USE_TYPE_DEF), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric));
                o2gdal.insert(new Obj2GroupEntity(mo.getObjId(), groupId, Obj2GroupEntity.USE_TYPE_DEF));
            }
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
    }

    public boolean isAuto()
    {
        return isAuto;
    }

    public void setAuto(boolean isAuto)
    {
        this.isAuto = isAuto;
    }
}
