package jetsennet.jsmp.nav.syn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSynFrame extends BaseMgrFrm
{

    private JMenuItem startSynMenuItem;
    private JMenuItem stopSynMenuItem;
    /**
     * 启动线程
     */
    private SetupThread st;
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
                    startSynEvent(e);
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
                    stopSynEvent(e);
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

    private void startSynEvent(ActionEvent e)
    {
        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                if (st == null)
                {
                    st = new SetupThread(DataSynFrame.this);
                }
                st.start();
            }
        });
        t.start();
    }

    private void stopSynEvent(ActionEvent e)
    {
        if (st != null)
        {
            st.stop();
        }
    }

    public void startSyn()
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                startSynMenuItem.setEnabled(false);
                stopSynMenuItem.setEnabled(true);
            }
        });
    }

    public void stopSyn()
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                startSynMenuItem.setEnabled(true);
                stopSynMenuItem.setEnabled(false);
            }
        });
    }

    @Override
    protected void stop()
    {
        stopSynEvent(null);
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
