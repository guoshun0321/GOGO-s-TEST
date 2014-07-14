/************************************************************************
日 期：2012-1-16
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.ins;

import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.ins.helper.AbsAttrsIns;
import jetsennet.jbmp.ins.helper.AttrsInsCustomed;
import jetsennet.jbmp.ins.helper.AttrsInsIndex;
import jetsennet.jbmp.ins.helper.AttrsInsSnmpTable;
import jetsennet.jbmp.ins.helper.InsRstHandleDef;

/**
 * @author 郭祥
 */
public class ObjInsSnmpIndex extends AbsObjIns
{

    private String index;

    /**
     * @param index 参数
     */
    public ObjInsSnmpIndex(String index)
    {
        this.index = index;
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        super.init();
        for (AbsAttrsIns ins : inss)
        {
            if (ins instanceof AttrsInsIndex)
            {
                ((AttrsInsIndex) ins).setIndex(index);
            }
        }
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

        AttrsInsIndex iins = new AttrsInsIndex(index);
        inss.add(iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_CONFIG, iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_MONITOR, iins);
        type2ins.put(AttribClassEntity.CLASS_LEVEL_PERF, iins);

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
        type2handle.put(AttribClassEntity.COLL_TYPE_COMMEN, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_SYSLOG, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_CONFIG, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_MONITOR, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_PERF, dirh);
        type2handle.put(AttribClassEntity.CLASS_LEVEL_TABLE, dirh);

    }
}
