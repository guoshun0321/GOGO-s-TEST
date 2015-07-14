package jetsennet.orm.executor.resultset;

import java.util.Date;

import jetsennet.util.SafeDateFormater;

public abstract class AbsResultSetHandle<T> implements IResultSetHandle<T>
{

    /**
     * 取值偏移位置
     */
    protected int offset = 0;
    /**
     * 最大取值量
     */
    protected int max = Integer.MAX_VALUE;

    protected String result2String(Object obj)
    {
        String retval = null;
        if (obj != null)
        {
            if (obj instanceof Date)
            {
                retval = SafeDateFormater.format((Date) obj);
            }
            else
            {
                retval = obj.toString();
            }
        }
        return retval;
    }

    @Override
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    @Override
    public AbsResultSetHandle<T> setMax(int max)
    {
        this.max = max;
        return this;
    }
}
