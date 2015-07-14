package jetsennet.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class SafeDataFormaterTest extends TestCase
{

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_STR = "1927-12-11 11:22:38";
    private Date DATE;

    public SafeDataFormaterTest()
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            DATE = format.parse(DATE_STR);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testFormat()
    {
        String str = SafeDateFormater.format(DATE, DATE_FORMAT);
        assertEquals(DATE_STR, str);
    }

    public void testParse()
    {
        Date date = SafeDateFormater.parse(DATE_STR);
        assertEquals(date.toString(), DATE.toString());
    }

}
