package jetsennet.jnmp.ins;

import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.ins.AbsObjIns;
import jetsennet.jbmp.ins.helper.AttrsInsCustomed;
import jetsennet.jbmp.ins.helper.InsRstHandleDef;

/**
 * @author？
 */
public class ObjInsApp extends AbsObjIns
{

    /**
     * 构造方法
     */
    public ObjInsApp()
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
        type2ins.put(AttribClassEntity.CLASS_LEVEL_PERF, cins);
        type2ins.put(AttribClassEntity.COLL_TYPE_COMMEN, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, cins);
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
        type2handle.put(AttribClassEntity.CLASS_LEVEL_PERF, dirh);
        type2handle.put(AttribClassEntity.COLL_TYPE_COMMEN, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, dirh);
    }
}
