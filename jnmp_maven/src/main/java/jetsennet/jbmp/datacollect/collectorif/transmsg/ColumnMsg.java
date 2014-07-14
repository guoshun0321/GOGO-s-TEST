/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.datacollect.collectorif.transmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * 表格型数据的一列 SNMP 传入：对象属性。对象属性里面的公式为exp:(OID:(1.3.4.5)+OID:(1.3.6.1.0))。 其中，已“.0”结尾的为标量。非“.0”结尾的为矢量，对象属性的field1字段为索引。 取值时注意相同索引的列同时取值。
 * 传出：索引，计算值。无法获取索引时，index列返回NULL。单元格无法取值时，output单元格为NULL。
 * @author GuoXiang
 */
public class ColumnMsg implements Serializable
{

    /**
     * 输入
     */
    private ObjAttribEntity input;
    /**
     * 结果输出
     */
    private ArrayList<OutputEntity> outputs;
    private static final long serialVersionUID = -1L;

    /**
     * 构造函数
     * @param input 参数
     */
    public ColumnMsg(ObjAttribEntity input)
    {
        this.input = input;
    }

    /**
     * @param exp 参数
     * @param value 值
     * @param name 名称
     */
    public void addOutput(String exp, String value, String name)
    {
        if (outputs == null)
        {
            outputs = new ArrayList<OutputEntity>();
        }
        outputs.add(OutputEntity.genOutput(value, name, exp));
    }

    /**
     * 使用采集结果填充TransMsg
     * 
     * @param valMap
     */
    public void fillWithResult(Map<ObjAttribEntity, Object> valMap)
    {
        Object obj = valMap.get(input);
        if (obj != null && obj instanceof List)
        {
            List<String> lst = (List<String>) obj;
            for (String str : lst)
            {
                this.addOutput("", str, "");
            }
        }
    }

    /**
     * 清除
     */
    public void clearOutput()
    {
        if (outputs != null)
        {
            outputs.clear();
        }
    }

    public ObjAttribEntity getInput()
    {
        return input;
    }

    public ArrayList<OutputEntity> getOutputs()
    {
        return outputs;
    }

}
