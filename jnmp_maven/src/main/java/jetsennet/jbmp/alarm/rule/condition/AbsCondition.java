/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 报警条件
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

/**
 * 报警条件抽象类
 * @author 郭祥
 */
public abstract class AbsCondition
{

    protected String[] params;

    /**
     * 设置参数
     * @param params 参数
     */
    public void setParams(String[] params)
    {
        this.params = params;
    }

    /**
     * 验证该条件的设置是否正确，在参数设置（setParams）后调用。
     * @return 结果
     */
    public abstract boolean validate();

    /**
     * 比较数据是否满足条件。满足，返回true；不满足，返回false。
     * @param obj 对象
     * @return 结果
     */
    public abstract boolean compare(Object obj);

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("条件：");
        sb.append(this.desc());
        sb.append("；");
        sb.append("参数：");
        if (params == null || params.length == 0)
        {
            sb.append("无");
        }
        else
        {
            for (int i = 0; i < params.length; i++)
            {
                sb.append(params[i]);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    public String toString(String value)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("当前值为：").append(value);
        sb.append("；阀值范围：(").append(this.desc());
        if (params == null || params.length == 0)
        {
            sb.append("无");
        }
        else
        {
            for (int i = 0; i < params.length; i++)
            {
                sb.append(params[i]).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 描述
     * @return
     */
    protected abstract String desc();
}
