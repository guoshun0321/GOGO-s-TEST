/**********************************************************************
 * 日 期： 2012-7-10
 * 作 者:  梁洪杰
 * 版 本： v1.0
 * 描 述:  BackupMgrFrm.java
 * 历 史： 2012-7-10 创建
 *********************************************************************/
package jetsennet.jbmp.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jetsennet.jbmp.backup.BackupManager;

/**
 * 数据转储窗口
 */
public class BackupMgrFrm extends BaseMgrFrm
{
    private JMenuItem startBackupMenuItem;
    private JMenuItem stopBackupMenuItem;
    private JMenuItem backupNowMenuItem;

    /**
     * @param title 标题
     */
    public BackupMgrFrm(String title)
    {
        super(title);
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        startBackupMenuItem = new JMenuItem();
        startBackupMenuItem.setText("启动数据转储");
        startBackupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startBackup(evt);
            }
        });
        stopBackupMenuItem = new JMenuItem();
        stopBackupMenuItem.setText("停止数据转储");
        stopBackupMenuItem.setEnabled(false);
        stopBackupMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopBackup(evt);
            }
        });
        backupNowMenuItem = new JMenuItem();
        backupNowMenuItem.setText("立即转储");
        backupNowMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                backupNow(evt);
            }
        });
        JMenu buckupMenu = new JMenu();
        buckupMenu.setText("转储");
        buckupMenu.add(startBackupMenuItem);
        buckupMenu.add(stopBackupMenuItem);
        buckupMenu.add(backupNowMenuItem);
        addMenu(buckupMenu);
    }

    private void startBackup(java.awt.event.ActionEvent evt)
    {
        BackupManager.getInstance().start();
        startBackupMenuItem.setEnabled(false);
        stopBackupMenuItem.setEnabled(true);
    }

    private void stopBackup(java.awt.event.ActionEvent evt)
    {
        BackupManager.getInstance().stop();
        startBackupMenuItem.setEnabled(true);
        stopBackupMenuItem.setEnabled(false);
    }

    private void backupNow(java.awt.event.ActionEvent evt)
    {
        BackupManager.getInstance().backupNow();
    }

    protected void stop()
    {
        BackupManager.getInstance().stop();
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
                BackupMgrFrm mgr = new BackupMgrFrm("数据转储");
                mgr.setVisible(true);
            }
        });
    }
}
