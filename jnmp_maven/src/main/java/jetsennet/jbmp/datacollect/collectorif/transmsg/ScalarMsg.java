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
 * 标量数据。 SNMP 传入：对象属性。对象属性里面的公式为exp:(OID:(1.3.4.5.0)) 传出：计算值。取不到值时为NULL。 注意：传出、传入值一一对应
 * @author 郭祥
 */
public class ScalarMsg implements Serializable
{

    /**
     * 输入
     */
    private ArrayList<ObjAttribEntity> inputs;
    /**
     * 输出值
     */
    private ArrayList<OutputEntity> outputs;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    /**
     * 构造方法
     */
    public ScalarMsg()
    {
    }

    /**
     * 添加传入的名称
     * @param oa 参数
     */
    public void addInput(ObjAttribEntity oa)
    {
        if (oa == null)
        {
            throw new NullPointerException();
        }
        if (inputs == null)
        {
            inputs = new ArrayList<ObjAttribEntity>();
        }
        inputs.add(oa);
    }

    /**
     * 添加传出的名称
     * @param value 值
     */
    public void addOutputE(String value)
    {
        if (outputs == null)
        {
            outputs = new ArrayList<OutputEntity>();
        }
    }

    /**
     * @param values 值
     */
    public void addOutput(List<String> values)
    {
        if (values == null)
        {
            return;
        }
        for (String value : values)
        {
            OutputEntity out = OutputEntity.genOutput(value);
            outputs.add(out);
        }
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
        OutputEntity out = new OutputEntity();
        out.exp = exp;
        out.value = value;
        out.name = name;
        outputs.add(out);
    }

    /**
     * @param out 参数
     */
    public void addOutput(OutputEntity out)
    {
        if (outputs == null)
        {
            outputs = new ArrayList<OutputEntity>();
        }
        outputs.add(out);
    }

    /**
     * 获取全部对象属性
     * 
     * @return 包含的对象属性，如果无对象属性则返回空List
     */
    public List<ObjAttribEntity> getObjAttribs()
    {
        List<ObjAttribEntity> retval = new ArrayList<ObjAttribEntity>();
        if (inputs != null)
        {
            retval.addAll(inputs);
        }
        return retval;
    }

    /**
     * 使用采集结果填充TransMsg
     * 
     * @param valMap
     */
    public void fillWithResult(Map<ObjAttribEntity, Object> valMap)
    {
        for (ObjAttribEntity input : inputs)
        {
            Object obj = valMap.get(input);
            if (obj != null && obj instanceof CollData)
            {
                CollData collData = (CollData) obj;
                this.addOutputE(collData.value);
            }
        }
    }

    public ArrayList<ObjAttribEntity> getInputs()
    {
        return inputs;
    }

    public void setInputs(ArrayList<ObjAttribEntity> inputs)
    {
        this.inputs = inputs;
    }

    public ArrayList<OutputEntity> getOutputs()
    {
        return outputs;
    }

    public void setOutputs(ArrayList<OutputEntity> outputs)
    {
        this.outputs = outputs;
    }
}
