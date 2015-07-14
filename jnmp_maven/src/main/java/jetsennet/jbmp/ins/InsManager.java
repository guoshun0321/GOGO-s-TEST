/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 对象属性实例化
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.buffer.SnmpNodeBuffer;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.util.InsUtil;

import org.apache.log4j.Logger;

/**
 * 对象属性实例化，单例
 * 
 * @author 郭祥
 */
public final class InsManager
{

    /**
     * 实例化配置
     */
    private static InsConfigColl fac = InsConfigColl.getInstance();
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(InsManager.class);

    // 单例
    private static InsManager instance = new InsManager();

    private InsManager()
    {
    }

    public static InsManager getInstance()
    {
        return instance;
    }

    /**
     * 对象自动实例化
     * @param mo 目标对象
     * @param attrs 对象的属性集合。为null时，获取该对象的全部属性。
     * @param groupId 采集组ID
     * @param collId 采集器ID
     * @param userId 用户id
     * @param infos 附加信息
     * @param isLocal 是否在本地进行实例化
     * @throws InstanceException 异常
     */
    public void autoIns(MObjectEntity mo, List<AttributeEntity> attrs, int groupId, int collId, int userId, Map<String, String> infos, boolean isLocal)
            throws InstanceException
    {
        if (mo == null)
        {
            throw new InstanceException("实例化模块：传入对象为NULL。");
        }
        try
        {
            SnmpNodeBuffer.get();

            InsConfig config = fac.get(mo.getClassType());
            AbsObjIns cur = InsConfigColl.ensureObjInsClass(config.getInsClass());
            cur.setAuto(true);

            if (attrs == null)
            {
                attrs = InsUtil.ensureInsAttr(mo.getClassId());
            }
            if (attrs != null && !attrs.isEmpty())
            {
                cur.autoIns(mo, attrs, config, collId, groupId, userId, infos, isLocal);
            }
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
        finally
        {
            SnmpNodeBuffer.close();
        }
    }

    /**
     * 对象自动实例化，默认用户ID为-1
     * @param mo 目标对象
     * @param attrs 对象的属性集合。为null时，获取该对象的全部属性。
     * @param groupId 采集组ID
     * @param collId 采集器ID
     * @param infos 附加信息
     * @param isLocal 是否在本地进行实例化
     * @throws InstanceException 异常
     */
    public void autoIns(MObjectEntity mo, List<AttributeEntity> attrs, int groupId, int collId, Map<String, String> infos, boolean isLocal)
            throws InstanceException
    {
        this.autoIns(mo, attrs, groupId, collId, -1, infos, isLocal);
    }

    /**
     * 对象实例化，返回实例化结果不存数据库
     * @param mo 目标对象
     * @param attrs 对象的属性集合。为null时，获取该对象的全部属性。
     * @param collId 采集器id
     * @param infos 信息
     * @param isLocal 是否本地实例化
     * @return 结果
     * @throws InstanceException 异常
     */
    public AttrsInsResult getInsResult(MObjectEntity mo, List<AttributeEntity> attrs, int collId, Map<String, String> infos, boolean isLocal)
            throws InstanceException
    {
        if (mo == null)
        {
            throw new InstanceException("实例化模块：传入对象为NULL。");
        }
        logger.debug("开始获取对象实例化数据。");
        AttrsInsResult retval = null;
        try
        {
            SnmpNodeBuffer.get();

            if (!mo.isSubObj())
            {
                // 对象
                InsConfig config = fac.get(mo.getClassType());
                AbsObjIns cur = InsConfigColl.ensureObjInsClass(config.getInsClass());
                cur.setAuto(false);

                if (attrs != null && !attrs.isEmpty())
                {
                    retval = cur.getInsResult(mo, attrs, collId, infos, isLocal);
                }
            }
            else
            {
                // 子对象
                InsConfig config = fac.getSub(mo.getClassType());
                if (config != InsConfigColl.getInstance().getDefConfSub())
                {
                    AbsSubObjIns cur = InsConfigColl.ensureSubObjInsClass(config.getInsClass());

                    if (attrs != null && !attrs.isEmpty())
                    {
                        retval = cur.getInsResult(mo, attrs, collId, infos, isLocal);
                    }
                }
                else
                {
                    // 2013-01-05 gx 针对北京马维士提出的需求，手动选择SNMP子对象对象属性时，列出全部可能子对象对象属性
                    config = InsConfigColl.getInstance().getDefConf();
                    AbsObjIns cur = InsConfigColl.ensureObjInsClass(config.getInsClass());
                    cur.setAuto(false);

                    if (attrs != null && !attrs.isEmpty())
                    {
                        retval = cur.getInsResult(mo, attrs, collId, infos, isLocal);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new InstanceException(ex);
        }
        finally
        {
            SnmpNodeBuffer.close();
        }
        return retval;
    }

    /**
     * 获取子对象信息
     * 
     * @param objId 对象id
     * @param classId 参数
     * @param collId 采集id
     * @return 结果
     * @throws InstanceException 异常
     */
    public SubObjInsInfo getSubInsInfo(int objId, int classId, int collId, boolean isLocal) throws InstanceException
    {
        SubObjInsInfo retval = null;
        try
        {
            AbsSubObjIns ins = SubObjInsManager.getInstance().ensureInsClass(classId);
            retval = ins.getSubInfo(objId, classId, collId, isLocal);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new InstanceException(ex);
        }
        return retval;
    }

    /**
     * 实例化选择的子对象
     * 
     * @param info 附件信息
     * @param collId 采集id
     * @param userId 用户id
     * @throws InstanceException 异常
     */
    public void insSelSub(SubObjInsInfo info, int userId, int collId) throws InstanceException
    {
        try
        {
            AbsSubObjIns ins = SubObjInsManager.getInstance().ensureInsClass(info.getClassId());
            ins.ins(info, collId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new InstanceException(ex);
        }
    }
}
