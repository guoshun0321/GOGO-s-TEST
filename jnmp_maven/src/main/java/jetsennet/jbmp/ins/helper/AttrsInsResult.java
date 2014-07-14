/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 属性实例化结果
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * 属性集实例化结果
 * @author 郭祥
 */
public class AttrsInsResult
{

    /**
     * 传入的属性
     */
    private ArrayList<AttributeEntity> inputAttrs;
    /**
     * 传入的属性，属性ID做索引
     */
    private Map<Integer, AttributeEntity> id2attrs;
    /**
     * 传出的对象属性
     */
    private ArrayList<ObjAttribEntity> output;
    /**
     * 正确实例化结果
     */
    private Map<AttributeEntity, ObjAttribEntity[]> insResult;
    /**
     * 实例化不成功的属性及其原因
     */
    private Map<AttributeEntity, String> errResult;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AttrsInsResult.class);

    /**
     * 构造函数
     */
    public AttrsInsResult()
    {
        inputAttrs = new ArrayList<AttributeEntity>();
        id2attrs = new HashMap<Integer, AttributeEntity>();
        output = new ArrayList<ObjAttribEntity>();
        insResult = new HashMap<AttributeEntity, ObjAttribEntity[]>();
        errResult = new HashMap<AttributeEntity, String>();
    }

    /**
     * 从结果中获取给定属性ID的数据
     * @param attribId 属性ID
     * @return null，结果中无给定属性的值；STRING，实例化错误；ObjAttribEntity[]，实例化结果
     */
    public Object getByAttribId(int attribId)
    {
        if (insResult != null)
        {
            Set<AttributeEntity> keys = insResult.keySet();
            for (AttributeEntity key : keys)
            {
                if (key.getAttribId() == attribId)
                {
                    return insResult.get(key);
                }
            }
        }
        if (errResult != null)
        {
            Set<AttributeEntity> keys = errResult.keySet();
            for (AttributeEntity key : keys)
            {
                if (key.getAttribId() == attribId)
                {
                    return errResult.get(key);
                }
            }
        }
        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="添加属性">
    /**
     * 添加需要实例化的属性
     * @param attr 参数
     */
    public void addInput(AttributeEntity attr)
    {
        inputAttrs.add(attr);
        id2attrs.put(attr.getAttribId(), attr);
    }

    /**
     * @param inputs 参数
     */
    public void addInput(ArrayList<AttributeEntity> inputs)
    {
        for (AttributeEntity ae : inputs)
        {
            this.addInput(ae);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="添加正确实例化的结果">
    /**
     * 正确实例化的结果
     * @param attr 属性
     * @param oas 参数
     */
    public void addResult(AttributeEntity attr, ObjAttribEntity[] oas)
    {
        if (insResult == null)
        {
            insResult = new HashMap<AttributeEntity, ObjAttribEntity[]>();
        }
        insResult.put(attr, oas);
        output.addAll(Arrays.asList(oas));
    }

    /**
     * 正确实例化的结果
     * @param attr 属性
     * @param oa 参数
     */
    public void addResult(AttributeEntity attr, ObjAttribEntity oa)
    {
        ObjAttribEntity[] oas = new ObjAttribEntity[1];
        oas[0] = oa;
        this.addResult(attr, oas);
    }

    /**
     * @param attrId 参数
     * @param oa 参数
     */
    public void addResult(int attrId, ObjAttribEntity oa)
    {
        AttributeEntity attr = id2attrs.get(attrId);
        if (attr == null)
        {
            logger.error("找不到ID：<" + attrId + ">对应的属性。");
        }
        else
        {
            ObjAttribEntity[] oas = new ObjAttribEntity[1];
            oas[0] = oa;
            this.addResult(attr, oas);
        }
    }

    /**
     * @param attrId 参数
     * @param oas 参数
     */
    public void addResult(int attrId, ObjAttribEntity[] oas)
    {
        AttributeEntity attr = id2attrs.get(attrId);
        if (attr == null)
        {
            logger.error("找不到ID：<" + attrId + ">对应的属性。");
        }
        else
        {
            this.addResult(attr, oas);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="添加实例化不成功的结果">
    /**
     * 实例化不成功的结果
     * @param attr 参数
     * @param err 参数
     */
    public void addErr(AttributeEntity attr, String err)
    {
        if (errResult == null)
        {
            errResult = new HashMap<AttributeEntity, String>();
        }
        errResult.put(attr, err);
    }

    /**
     * @param attrId 参数
     * @param err 参数
     */
    public void addErr(int attrId, String err)
    {
        AttributeEntity attr = id2attrs.get(attrId);
        if (attr == null)
        {
            logger.error("找不到ID：<" + attrId + ">对应的属性。");
        }
        else
        {
            this.addErr(attr, err);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the inputAttrs
     */
    public ArrayList<AttributeEntity> getInputAttrs()
    {
        return inputAttrs;
    }

    /**
     * @param inputAttrs the inputAttrs to set
     */
    public void setInputAttrs(ArrayList<AttributeEntity> inputAttrs)
    {
        this.inputAttrs = inputAttrs;
    }

    /**
     * @return the insResult
     */
    public Map<AttributeEntity, ObjAttribEntity[]> getInsResult()
    {
        return insResult;
    }

    /**
     * @param insResult the insResult to set
     */
    public void setInsResult(Map<AttributeEntity, ObjAttribEntity[]> insResult)
    {
        this.insResult = insResult;
    }

    /**
     * @return the errResult
     */
    public Map<AttributeEntity, String> getErrResult()
    {
        return errResult;
    }

    /**
     * @param errResult the errResult to set
     */
    public void setErrResult(Map<AttributeEntity, String> errResult)
    {
        this.errResult = errResult;
    }

    /**
     * @return the output
     */
    public ArrayList<ObjAttribEntity> getOutput()
    {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ArrayList<ObjAttribEntity> output)
    {
        this.output = output;
    }

    public Map<Integer, AttributeEntity> getId2attrs()
    {
        return id2attrs;
    }

    public void setId2attrs(Map<Integer, AttributeEntity> id2attrs)
    {
        this.id2attrs = id2attrs;
    }
    // </editor-fold>
}
