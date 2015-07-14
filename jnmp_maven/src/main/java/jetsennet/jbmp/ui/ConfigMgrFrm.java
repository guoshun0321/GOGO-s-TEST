/**********************************************************************
 * 日 期： 2012-7-10
 * 作 者:  梁洪杰
 * 版 本： v1.0
 * 描 述:  ConfigMgrFrm.java
 * 历 史： 2012-7-10 创建
 *********************************************************************/
package jetsennet.jbmp.ui;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jetsennet.jbmp.exception.UIException;

/**
 * 配置管理窗口
 */
public class ConfigMgrFrm extends BaseMgrFrm
{
    /**
     * @param title 标题
     */
    public ConfigMgrFrm(String title)
    {
        super(title);
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        JMenuItem dbSettingMenuItem = new JMenuItem();
        dbSettingMenuItem.setText("数据库设置");
        dbSettingMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                dbSetting(evt);
            }
        });
        JMenuItem colSettingMenuItem = new JMenuItem();
        colSettingMenuItem.setText("集群设置");
        colSettingMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clusterSetting(evt);
            }
        });
        JMenuItem rrdSettingMenuItem = new JMenuItem();
        rrdSettingMenuItem.setText("RRD设置");
        rrdSettingMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rrdSetting(evt);
            }
        });
        JMenu cfgMenu = new JMenu();
        cfgMenu.setText("设置");
        //        cfgMenu.add(dbSettingMenuItem);
        cfgMenu.add(colSettingMenuItem);
        //        cfgMenu.add(rrdSettingMenuItem);
        addMenu(cfgMenu);
    }

    private void rrdSetting(ActionEvent evt)
    {
        RrdCfgDlg rDlg = new RrdCfgDlg(this, true);
        rDlg.showDialog();
    }

    private void dbSetting(java.awt.event.ActionEvent evt)
    {
        DBTestDialog test = new DBTestDialog(this, true);
        test.showDialog();
    }

    private void clusterSetting(java.awt.event.ActionEvent evt)
    {
        try
        {
            ClusterCfgDlg rDig = new ClusterCfgDlg(this, true);
            rDig.showDialog();
        }
        catch (UIException ex)
        {
            logger.error(ex.getMessage(), ex);
        }
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
                ConfigMgrFrm mgr = new ConfigMgrFrm("系统设置");
                mgr.setVisible(true);
            }
        });
    }
}
