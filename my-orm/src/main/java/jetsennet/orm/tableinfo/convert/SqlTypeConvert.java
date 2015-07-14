package jetsennet.orm.tableinfo.convert;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

public class SqlTypeConvert
{

    public static final Object convert(Object source, Class<?> targetType)
    {
        Object retval = null;
        if (source instanceof java.util.Date)
        {
            java.util.Date jDate = (java.util.Date) source;
            if (targetType.isAssignableFrom(Date.class))
            {
                retval = new Date(jDate.getTime());
            }
            else if (targetType.isAssignableFrom(Time.class))
            {
                retval = new Time(jDate.getTime());
            }
            else if (targetType.isAssignableFrom(Timestamp.class))
            {
                retval = new Timestamp(jDate.getTime());
            }
        }
        else
        {
            retval = source;
        }
        return retval;
    }

    public static final Object convert(Object source, int targetType)
    {
        Object retval = null;
        if (source instanceof java.util.Date)
        {
            java.util.Date jDate = (java.util.Date) source;
            if (targetType == Types.DATE)
            {
                retval = new Date(jDate.getTime());
            }
            else if (targetType == Types.TIME)
            {
                retval = new Time(jDate.getTime());
            }
            else if (targetType == Types.TIMESTAMP)
            {
                retval = new Timestamp(jDate.getTime());
            }
        }
        else
        {
            retval = source;
        }
        return retval;
    }

    //    public Object reverse()
    //    {
    //
    //    }

}
