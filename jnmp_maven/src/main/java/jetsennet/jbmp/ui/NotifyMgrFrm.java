/**********************************************************************
 * 日 期： 2012-7-10
 * 作 者:  梁洪杰
 * 版 本： v1.0
 * 描 述:  ConfigMgrFrm.java
 * 历 史： 2012-7-10 创建
 *********************************************************************/
package jetsennet.jbmp.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jetsennet.jbmp.alarmaction.AlarmActionBase;

/**
 * 通知管理窗口
 */
public class NotifyMgrFrm extends BaseMgrFrm
{
    private JMenuItem startAlarmNotifyMenuItem;
    private JMenuItem stopAlarmNotifyMenuItem;

    /**
     * @param title 标题
     */
    public NotifyMgrFrm(String title)
    {
        super(title);
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        startAlarmNotifyMenuItem = new JMenuItem();
        startAlarmNotifyMenuItem.setText("启动报警通知");
        startAlarmNotifyMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startNotify(evt);
            }
        });
        stopAlarmNotifyMenuItem = new JMenuItem();
        stopAlarmNotifyMenuItem.setText("停止报警通知");
        stopAlarmNotifyMenuItem.setEnabled(false);
        stopAlarmNotifyMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopNotify(evt);
            }
        });
        JMenu alarmNotifyMenu = new JMenu();
        alarmNotifyMenu.setText("通知");
        alarmNotifyMenu.add(startAlarmNotifyMenuItem);
        alarmNotifyMenu.add(stopAlarmNotifyMenuItem);
        addMenu(alarmNotifyMenu);
    }

    private void startNotify(java.awt.event.ActionEvent evt)
    {
        try
        {
            AlarmActionBase.getInstance().start("flush-tcp-notify.xml");
            startAlarmNotifyMenuItem.setEnabled(false);
            stopAlarmNotifyMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopNotify(java.awt.event.ActionEvent evt)
    {
        AlarmActionBase.getInstance().stop();
        startAlarmNotifyMenuItem.setEnabled(true);
        stopAlarmNotifyMenuItem.setEnabled(false);
    }

    protected void stop()
    {
        AlarmActionBase.getInstance().stop();
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                NotifyMgrFrm mgr = new NotifyMgrFrm("通知管理");
                mgr.setVisible(true);
            }
        });
    }
}
