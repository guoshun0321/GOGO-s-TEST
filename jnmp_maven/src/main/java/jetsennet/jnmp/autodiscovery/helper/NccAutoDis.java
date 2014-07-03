package jetsennet.jnmp.autodiscovery.helper;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.EditNode;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.util.ConvertUtil;

/**
 * @author ？
 */
public class NccAutoDis
{

    private MObjectDal modal;
    private static final Logger logger = Logger.getLogger(NccAutoDis.class);

    /**
     * 构造方法
     */
    public NccAutoDis()
    {
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
    }

    /**
     * @param mo 对象
     * @return 结果
     */
    public String scanNccDev(MObjectEntity mo)
    {
        String retval = null;
        SnmpTable table = this.fillTable(mo);
        if (table == null)
        {
            return retval;
        }
        ArrayList<MObjectEntity> mos = this.genObjs(this.parseTable(table), mo);
        return retval;
    }

    /**
     * @param mo 对象
     * @return 结果
     */
    public SnmpTable fillTable(MObjectEntity mo)
    {
        SnmpTable table = CommonSnmpTable.nccDeviceTable();
        try
        {
            SnmpTableUtil.initSnmpTable(table, null, mo);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            return null;
        }
        return table;
    }

    private ArrayList<NccDevEntity> parseTable(SnmpTable table)
    {
        if (table == null)
        {
            return null;
        }
        ArrayList<NccDevEntity> nccs = new ArrayList<NccDevEntity>();
        int rowNum = table.getRowNum();
        for (int i = 0; i < rowNum; i++)
        {
            ArrayList<EditNode> nodes = table.getRow(i);
            NccDevEntity temp = new NccDevEntity();
            temp.dDeviceTableIndex = ConvertUtil.stringToInt(nodes.get(0).getEditValue(), 0);
            temp.dDeviceLabel = nodes.get(1).getEditValue();
            temp.dDisplayLabel = nodes.get(2).getEditValue();
            temp.dDeviceType = ConvertUtil.stringToInt(nodes.get(3).getEditValue(), 0);
            temp.dDeviceAddress = nodes.get(4).getEditValue();
            temp.dDeviceState = ConvertUtil.stringToInt(nodes.get(5).getEditValue(), 0);
            nccs.add(temp);
        }
        return nccs;
    }

    private ArrayList<MObjectEntity> genObjs(ArrayList<NccDevEntity> nccs, MObjectEntity parent)
    {
        ArrayList<MObjectEntity> retval = new ArrayList<MObjectEntity>();
        for (NccDevEntity ncc : nccs)
        {
            MObjectEntity temp = parent.copy();
            temp.setParentId(parent.getObjId());
            temp.setObjId(0);
            // dDeviceLabel用于标识一个ncc管理的设备
            temp.setField1(ncc.dDeviceLabel);
            temp.setObjName(ncc.dDisplayLabel);
            temp.setIpAddr(ncc.dDeviceAddress);
            this.setDevType(temp, ncc.dDeviceType);
        }
        return retval;
    }

    private void setDevType(MObjectEntity mo, int type)
    {
    }

    private class NccDevEntity
    {

        int dDeviceTableIndex;
        String dDeviceLabel;
        String dDisplayLabel;
        int dDeviceType;
        String dDeviceAddress;
        int dDeviceState;
    }
}
