package jetsennet.jsmp.nav.syn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.monitor.MonitorServlet;
import jetsennet.jsmp.nav.syn.DataSynchronized;

public class DataSynFrame extends BaseMgrFrm
{

    private JMenuItem startSynMenuItem;
    private JMenuItem stopSynMenuItem;
    /**
     * 数据同步
     */
    private DataSynchronized ds;
    /**
     * 监控
     */
    private MonitorServlet monitor;
    /**
     * 同步
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSynFrame.class);

    public DataSynFrame(String title)
    {
        super(title);
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        // 同步
        startSynMenuItem = new JMenuItem();
        startSynMenuItem.setText("开始同步");
        startSynMenuItem.setEnabled(true);
        startSynMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    startSyn(e);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        });

        stopSynMenuItem = new JMenuItem();
        stopSynMenuItem.setText("结束同步");
        stopSynMenuItem.setEnabled(false);
        stopSynMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    stopSyn(e);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        });

        JMenu extendMenu = new JMenu();
        extendMenu.setText("同步");
        extendMenu.add(startSynMenuItem);
        extendMenu.add(stopSynMenuItem);
        addMenu(extendMenu);
    }

    private void startSyn(ActionEvent e)
    {
        if (ds == null)
        {
            ds = new DataSynchronized();
            ds.start();
        }
        if (monitor == null)
        {
            try
            {
                monitor = new MonitorServlet();
                monitor.init();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        startSynMenuItem.setEnabled(false);
        stopSynMenuItem.setEnabled(true);
    }

    private void stopSyn(ActionEvent e)
    {
        if (ds != null)
        {
            ds.close();
            ds = null;
        }
        if (monitor != null)
        {
            monitor.destroy();
            monitor = null;
        }
        startSynMenuItem.setEnabled(true);
        stopSynMenuItem.setEnabled(false);
    }

    @Override
    protected void stop()
    {
        stopSyn(null);
    }

    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                DataSynFrame mgr = new DataSynFrame("数据同步");
                mgr.setVisible(true);
            }
        });
    }

}
