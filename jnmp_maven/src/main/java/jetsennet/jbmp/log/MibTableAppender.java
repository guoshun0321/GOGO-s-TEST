/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import jetsennet.jbmp.ui.LoggerTableModel;

/**
 * @author Guo
 */
public class MibTableAppender extends AppenderSkeleton
{

    private SwingReg reg;
    private String regSwingName;

    /**
     * 构造方法
     */
    public MibTableAppender()
    {
        reg = SwingReg.getInstance();
    }

    /**
     * 构造方法
     * @param layout 参数
     */
    public MibTableAppender(Layout layout)
    {
        setLayout(layout);
        activateOptions();
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
        Object area = reg.getObject(getRegSwingName());
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
