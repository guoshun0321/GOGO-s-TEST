/************************************************************************
日 期：2011-12-28
作 者: 郭祥
版 本：v1.3
描 述: snmp对象实例化
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.helper.AttrsInsCustomed;
import jetsennet.jbmp.ins.helper.AttrsInsSnmp;
import jetsennet.jbmp.ins.helper.AttrsInsSnmpTable;
import jetsennet.jbmp.ins.helper.InsRstHandleDef;

import org.apache.log4j.Logger;

/**
 * snmp对象实例化
 * @author 郭祥
 */
public class ObjInsSnmp extends AbsObjIns
{

    public final Logger logger = Logger.getLogger(ObjInsSnmp.class);

    /**
     * 构造函数
     */
    public ObjInsSnmp()
    {
    }

    /**
     * 初始化实例化方式
     */
    @Override
    protected void initInsClasses()
    {
        super.initInsClasses();

        AttrsInsCustomed cins = new AttrsInsCustomed();
        inss.add(cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CUSTOM, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_TRAP, cins);
        type2ins.put(AttribClassEntity.COLL_TYPE_COMMEN, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, cins);

        AttrsInsSnmp sins = new AttrsInsSnmp();
        inss.add(sins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CONFIG, sins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_MONITOR, sins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_PERF, sins);

        AttrsInsSnmpTable stins = new AttrsInsSnmpTable();
        inss.add(stins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_TABLE, stins);
    }

    /**
     * 实例化结果处理
     */
    @Override
    protected void initRstHandleClasses()
    {
        super.initRstHandleClasses();
        InsRstHandleDef dirh = new InsRstHandleDef();
        handles.add(dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_CUSTOM, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_TRAP, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_CONFIG, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_MONITOR, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_PERF, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_TABLE, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, dirh);
        type2handle.put(AttribClassEntity.COLL_TYPE_COMMEN, dirh);
    }

    /**
     * 实例化子对象
     * @param mo 对象
     * @param collId 采集id
     * @param infos 附件信息
     * @param isLocal 是否本地实例化
     */
    @Override
    public void autoInsSub(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal)
    {
        try
        {
            SubObjInsThread sub = new SubObjInsThread(mo, collId, infos, isLocal);
            SubObjInsPool.getInstance().submit(sub);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 子对象实例化线程
     * @author 郭祥
     */
    class SubObjInsThread implements Runnable
    {

        private int collId;
        private MObjectEntity mo;
        private Map<String, String> infos;
        private boolean isLocal;

        public SubObjInsThread(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal)
        {
            this.mo = mo;
            this.collId = collId;
            this.infos = infos;
            this.isLocal = isLocal;
        }

        @Override
        public void run()
        {
            try
            {
                AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
                List<AttribClassEntity> subs = acdal.getSub(mo.getClassId());

                for (AttribClassEntity sub : subs)
                {
                    if (sub.getClassLevel() == AttribClassEntity.CLASS_LEVEL_SUB)
                    {
                        AbsSubObjIns ins = SubObjInsManager.getInstance().ensureInsClass(sub.getClassType());
                        SubObjInsInfo info = ins.getSubInfo(mo.getObjId(), sub.getClassId(), collId, isLocal);
                        ins.ins(info, collId);
                    }
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

}
