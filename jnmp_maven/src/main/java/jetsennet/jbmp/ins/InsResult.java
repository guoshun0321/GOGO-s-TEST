/************************************************************************
日 期：2011-12-01
作 者: 郭祥
版 本：v1.3
描 述: 实例化结果
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.ArrayList;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * @author 郭祥
 */
public class InsResult
{

    private ArrayList<AttributeEntity> rightAttr;
    private ArrayList<AttributeEntity> wrongAttr;
    private ArrayList<ObjAttribEntity> results;

    /**
     * 构造函数
     * @param rightAttr 参数
     * @param wrongAttr 参数
     * @param results 参数
     */
    public InsResult(ArrayList<AttributeEntity> rightAttr, ArrayList<AttributeEntity> wrongAttr, ArrayList<ObjAttribEntity> results)
    {
        this.rightAttr = rightAttr;
        this.wrongAttr = wrongAttr;
        this.results = results;
    }

    /**
     * @param attrs 参数
     */
    public InsResult(ArrayList<AttributeEntity> attrs)
    {
        rightAttr = new ArrayList<AttributeEntity>();
        wrongAttr = new ArrayList<AttributeEntity>();
        results = new ArrayList<ObjAttribEntity>();
        rightAttr.addAll(attrs);
    }

    /**
     * 构造函数
     */
    public InsResult()
    {
        rightAttr = new ArrayList<AttributeEntity>();
        wrongAttr = new ArrayList<AttributeEntity>();
        results = new ArrayList<ObjAttribEntity>();
    }

    /**
     * @param attr 属性
     */
    public void addWrong(AttributeEntity attr)
    {
        rightAttr.remove(attr);
        wrongAttr.add(attr);
    }

    // <editor-fold defaultstate="collapsed" desc="自动生成的数据访问">
    /**
     * @return the rightAttr
     */
    public ArrayList<AttributeEntity> getRightAttr()
    {
        return rightAttr;
    }

    /**
     * @param rightAttr the rightAttr to set
     */
    public void setRightAttr(ArrayList<AttributeEntity> rightAttr)
    {
        this.rightAttr = rightAttr;
    }

    /**
     * @return the wrongAttr
     */
    public ArrayList<AttributeEntity> getWrongAttr()
    {
        return wrongAttr;
    }

    /**
     * @param wrongAttr the wrongAttr to set
     */
    public void setWrongAttr(ArrayList<AttributeEntity> wrongAttr)
    {
        this.wrongAttr = wrongAttr;
    }

    /**
     * @return the results
     */
    public ArrayList<ObjAttribEntity> getResults()
    {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(ArrayList<ObjAttribEntity> results)
    {
        this.results = results;
    }
    // </editor-fold>
}
