package jetsennet.jnmp.ins;

import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.ins.AbsObjIns;
import jetsennet.jbmp.ins.helper.AttrsInsCustomed;
import jetsennet.jbmp.ins.helper.InsRstHandleDef;
import jetsennet.jnmp.ins.helper.AttrsInsInspurSec;

/**
 * @author李巍 浪潮机房监控属性实例化
 */
public class ObjInsInspurSec extends AbsObjIns
{

    /**
     * 构造方法
     */
    public ObjInsInspurSec()
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
        type2ins.put(AttribClassEntity.COLL_TYPE_COMMEN, cins);

        AttrsInsInspurSec iins = new AttrsInsInspurSec();
        inss.add(iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CUSTOM, iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_PERF, iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, iins);
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
