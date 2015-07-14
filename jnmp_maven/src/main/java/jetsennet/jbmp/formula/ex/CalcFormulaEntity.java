/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula.ex;

/**
 * @author GuoXiang
 */
public class CalcFormulaEntity
{

    /**
     * 值
     */
    public final String value;
    /**
     * 类型
     */
    public final int type;
    
    public final Object obj;

    /**
     * 构造函数
     * @param value 值
     * @param type 类型
     * @param obj 对象
     */
    public CalcFormulaEntity(String value, int type, Object obj)
    {
        this.value = value;
        this.type = type;
        this.obj = obj;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        sb.append("<");
        sb.append(type);
        sb.append(">");
        return sb.toString();
    }
}
