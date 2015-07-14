package jetsennet.jbmp.ins;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.ins.helper.AttrsInsCustomed;
import jetsennet.jbmp.ins.helper.AttrsInsHarrisCard;
import jetsennet.jbmp.ins.helper.InsRstHandleDef;

public class ObjInsHarrisCard extends AbsObjIns
{

    public final Logger logger = Logger.getLogger(ObjInsHarrisCard.class);

    /**
     * 构造函数
     */
    public ObjInsHarrisCard()
    {
    }

    /**
     * 初始化实例化方式
     */
    @Override
    protected void initInsClasses()
    {
        super.initInsClasses();
        
        AttrsInsHarrisCard ains = new AttrsInsHarrisCard();
        inss.add(ains);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_PERF, ains);

        AttrsInsCustomed cins = new AttrsInsCustomed();
        inss.add(cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CUSTOM, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_TRAP, cins);
        type2ins.put(AttribClassEntity.COLL_TYPE_COMMEN, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CONFIG, cins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_MONITOR, cins);        
        type2ins.put(AttribClassEntity.CLASS_LEVEL_TABLE, cins);
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

}
