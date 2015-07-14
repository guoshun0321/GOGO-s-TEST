/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jsmp.nav.syn.ui;

import java.util.HashMap;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Guo
 */
public class Log4UI extends AppenderSkeleton
{
    private static HashMap<String, Object> regMap = new HashMap<String, Object>();

    private String regSwingName;

    /**
     * 构造方法
     */
    public Log4UI()
    {
    }

    /**
     * 构造方法
     * @param layout 参数
     */
    public Log4UI(Layout layout)
    {
        setLayout(layout);
        activateOptions();
    }

    public static void reg(String key, Object cmp)
    {
        regMap.put(key, cmp);
    }

    /**
     * Does nothing.
     */
    @Override
    public void activateOptions()
    {
    }

    @Override
    public void append(LoggingEvent le)
    {
        Object area = regMap.get(getRegSwingName());
        if (area != null && area instanceof LoggerTableModel)
        {
            LoggerTableModel model = (LoggerTableModel) area;
            model.add(le);
        }
    }

    @Override
    public boolean requiresLayout()
    {
        return true;
    }

    @Override
    public void close()
    {
    }

    @Override
    public void setLayout(Layout layout)
    {
        this.layout = layout;
    }

    /**
     * @return the regSwingName
     */
    public String getRegSwingName()
    {
        return regSwingName;
    }

    /**
     * @param regSwingName the regSwingName to set
     */
    public void setRegSwingName(String regSwingName)
    {
        this.regSwingName = regSwingName;
    }
}
