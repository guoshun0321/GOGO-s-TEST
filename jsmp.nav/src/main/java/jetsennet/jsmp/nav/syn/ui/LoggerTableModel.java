/************************************************************************
日 期：2012-04-28
作 者: 郭祥
版 本：v1.3
描 述: swing表格模型,用于展现日志信息
历 史：
 ************************************************************************/
package jetsennet.jsmp.nav.syn.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 日志数据的swing表格模型
 * @author 郭祥
 */
public class LoggerTableModel extends AbstractTableModel
{

    private static int MAXROW = 300;
    private static int SUB = 100;
    private ArrayList<LoggingEvent> events;
    private String[] headNames = { "时间", "级别", "类", "信息" };
    private JScrollPane parentScr = null;

    /**
     * 构造函数
     */
    public LoggerTableModel()
    {
        events = new ArrayList<LoggingEvent>(MAXROW);
    }

    /**
     * @param row 行
     * @return 结果
     */
    public LoggingEvent get(int row)
    {
        if (row >= 0 && row < events.size())
        {
            return events.get(row);
        }
        else
        {
            return null;
        }
    }

    /**
     * @param le 参数
     */
    public void add(LoggingEvent le)
    {
        if (events.size() > MAXROW)
        {
            synchronized (this)
            {
                LoggingEvent[] eventArray1 = new LoggingEvent[(MAXROW - SUB) + 1];
                LoggingEvent[] eventArray2 = events.toArray(new LoggingEvent[events.size()]);
                System.arraycopy(eventArray2, SUB, eventArray1, 0, (MAXROW - SUB) + 1);
                events.clear();
                events.addAll(Arrays.asList(eventArray1));
                eventArray1 = null;
                eventArray2 = null;
            }
            System.gc();
        }
        events.add(le);
        fireTableDataChanged();
        if (parentScr != null)
        {
            final JScrollBar bar = parentScr.getVerticalScrollBar();
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    bar.setValue(bar.getMaximum() - bar.getVisibleAmount() + 100);
                }
            });
        }
    }

    /**
     * 清空
     */
    public void clear()
    {
        events.clear();
        fireTableStructureChanged();
    }

    @Override
    public String getColumnName(int column)
    {
        return headNames[column];
    }

    public int getRowCount()
    {
        return events.size();
    }

    public int getColumnCount()
    {
        return headNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Object result = null;
        int size = events.size();
        if (rowIndex >= 0 && rowIndex < size)
        {
            LoggingEvent evt = events.get(rowIndex);
            if (columnIndex == 0)
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                result = format.format(new Date(evt.getTimeStamp()));
            }
            else if (columnIndex == 1)
            {
                result = evt.getLevel().toString();
            }
            else if (columnIndex == 2)
            {
                result = evt.getLogger().getName();
            }
            else if (columnIndex == 3)
            {
                result = evt.getMessage();
                if (evt.getThrowableInformation() != null)
                {
                    if (result == null)
                    {
                        result = "";
                    }
                    if (!"".equals(result))
                    {
                        result = result + "\n";
                    }
                    result = result + evt.getThrowableInformation().getThrowable().getMessage();
                }
            }
        }
        return result;
    }

    /**
     * @return the parentScr
     */
    public JScrollPane getParentScr()
    {
        return parentScr;
    }

    /**
     * @param parentScr the parentScr to set
     */
    public void setParentScr(JScrollPane parentScr)
    {
        this.parentScr = parentScr;
    }
}
