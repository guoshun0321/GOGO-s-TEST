/**********************************************************************
 * 日 期： 2012-7-10
 * 作 者:  梁洪杰
 * 版 本： v1.0
 * 描 述:  CollMgrFrm.java
 * 历 史： 2012-7-10 创建
 *********************************************************************/
package jetsennet.jbmp.ui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.jrobin.core.RrdDb;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.rrd.RrdHelper;
import jetsennet.jbmp.datacollect.collectorif.ClusterManager;
import jetsennet.jbmp.datacollect.scheduler.CollManager;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.syslog.Syslogd;
import jetsennet.jbmp.trap.receiver.TrapReceiver;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集管理窗口
 */
public class CollMgrFrm extends BaseMgrFrm
{
    private ClusterManager ac;
    private JMenuItem startClusterMenuItem;
    private JMenuItem stopClusterMenuItem;
    private JMenuItem startPerfMenuItem;
    private JMenuItem stopPerfMenuItem;
    private JMenuItem viewPerfMenuItem;
    private JMenuItem clearPerfMenuItem;
    private JMenuItem startTrapMenuItem;
    private JMenuItem stopTrapMenuItem;
    private JMenuItem startSyslogMenuItem;
    private JMenuItem stopSyslogMenuItem;
    private JMenuItem startExtendMenuItem;
    private JMenuItem stopExtendMenuItem;

    /**
     * @param title 标题
     */
    public CollMgrFrm(String title)
    {
        super(title);
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        // 集群管理
        startClusterMenuItem = new JMenuItem();
        startClusterMenuItem.setText("启动集群");
        startClusterMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startAlarm(evt);
            }
        });
        stopClusterMenuItem = new JMenuItem();
        stopClusterMenuItem.setText("停止集群");
        stopClusterMenuItem.setEnabled(false);
        stopClusterMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Object[] options = { "确定", "取消" };
                int result =
                    JOptionPane.showOptionDialog(null, "停止集群操作将会同时停止性能采集、Trap接收、Syslog接收和外部接收，是否继续？", "停止集群", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (result == 0)
                {
                    stopAlarm(e);
                }
            }
        });
        JMenu clusterMenu = new JMenu();
        clusterMenu.setText("集群");
        clusterMenu.add(startClusterMenuItem);
        clusterMenu.add(stopClusterMenuItem);
        addMenu(clusterMenu);

        // 性能采集管理
        startPerfMenuItem = new JMenuItem();
        startPerfMenuItem.setText("启动性能采集");
        startPerfMenuItem.setEnabled(false);
        startPerfMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startPerf(evt);
            }
        });
        stopPerfMenuItem = new JMenuItem();
        stopPerfMenuItem.setText("停止性能采集");
        stopPerfMenuItem.setEnabled(false);
        stopPerfMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopPerf(evt);
            }
        });
        viewPerfMenuItem = new JMenuItem();
        viewPerfMenuItem.setText("查看性能数据");
        viewPerfMenuItem.setEnabled(true);
        viewPerfMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                viewPerf(evt);
            }
        });
        clearPerfMenuItem = new JMenuItem();
        clearPerfMenuItem.setText("检查性能数据文件");
        clearPerfMenuItem.setEnabled(true);
        clearPerfMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                clearPerf(evt);
            }
        });
        JMenu perfMenu = new JMenu();
        perfMenu.setText("性能");
        perfMenu.add(startPerfMenuItem);
        perfMenu.add(stopPerfMenuItem);
        perfMenu.add(viewPerfMenuItem);
        perfMenu.add(clearPerfMenuItem);
        addMenu(perfMenu);

        // trap管理
        startTrapMenuItem = new JMenuItem();
        startTrapMenuItem.setText("启动Trap接收");
        startTrapMenuItem.setEnabled(false);
        startTrapMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startTrap(evt);
            }
        });
        stopTrapMenuItem = new JMenuItem();
        stopTrapMenuItem.setText("停止Trap接收");
        stopTrapMenuItem.setEnabled(false);
        stopTrapMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopTrap(evt);
            }
        });
        JMenu trapMenu = new JMenu();
        trapMenu.setText("Trap");
        trapMenu.add(startTrapMenuItem);
        trapMenu.add(stopTrapMenuItem);
        addMenu(trapMenu);

        // syslog管理
        startSyslogMenuItem = new JMenuItem();
        startSyslogMenuItem.setText("启动Syslog接收");
        startSyslogMenuItem.setEnabled(false);
        startSyslogMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startSyslog(evt);
            }
        });
        stopSyslogMenuItem = new JMenuItem();
        stopSyslogMenuItem.setText("停止Syslog接收");
        stopSyslogMenuItem.setEnabled(false);
        stopSyslogMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopSyslog(evt);
            }
        });
        JMenu syslogMenu = new JMenu();
        syslogMenu.setText("Syslog");
        syslogMenu.add(startSyslogMenuItem);
        syslogMenu.add(stopSyslogMenuItem);
        addMenu(syslogMenu);

        // 外部接口管理
        startExtendMenuItem = new JMenuItem();
        startExtendMenuItem.setText("启动外部接收");
        startExtendMenuItem.setEnabled(false);
        startExtendMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startExtend(evt);
            }
        });
        stopExtendMenuItem = new JMenuItem();
        stopExtendMenuItem.setText("停止外部接收");
        stopExtendMenuItem.setEnabled(false);
        stopExtendMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopExtend(evt);
            }
        });
        JMenu extendMenu = new JMenu();
        extendMenu.setText("外部系统");
        extendMenu.add(startExtendMenuItem);
        extendMenu.add(stopExtendMenuItem);
        addMenu(extendMenu);
    }

    private void startAlarm(java.awt.event.ActionEvent evt)
    {
        try
        {
            if (ac == null)
            {
                ac = new ClusterManager();
            }
            ac.start();
            startClusterMenuItem.setEnabled(false);
            stopClusterMenuItem.setEnabled(true);
            startPerfMenuItem.setEnabled(true);
            startTrapMenuItem.setEnabled(true);
            startSyslogMenuItem.setEnabled(true);
            startExtendMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopAlarm(java.awt.event.ActionEvent evt)
    {
        stop();
        startClusterMenuItem.setEnabled(true);
        stopClusterMenuItem.setEnabled(false);
        startPerfMenuItem.setEnabled(false);
        stopPerfMenuItem.setEnabled(false);
        startTrapMenuItem.setEnabled(false);
        stopTrapMenuItem.setEnabled(false);
        startSyslogMenuItem.setEnabled(false);
        stopSyslogMenuItem.setEnabled(false);
        startExtendMenuItem.setEnabled(false);
        stopExtendMenuItem.setEnabled(false);
    }

    private void startPerf(java.awt.event.ActionEvent evt)
    {
        try
        {
            CollManager.getInstance().start();
            startPerfMenuItem.setEnabled(false);
            stopPerfMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopPerf(java.awt.event.ActionEvent evt)
    {
        CollManager.getInstance().stop();
        startPerfMenuItem.setEnabled(true);
        stopPerfMenuItem.setEnabled(false);
    }

    private void viewPerf(java.awt.event.ActionEvent evt)
    {
        String root = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, CollConstants.DEFAULT_RRD_ROOT);
        JFileChooser fileChooser = new JFileChooser(new File(root));
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.showOpenDialog(this);
        File file = fileChooser.getSelectedFile();
        if (file == null)
        {
            return;
        }
        try
        {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            dotIndex = dotIndex > -1 ? dotIndex : fileName.length();
            RrdDb rrdDb = RrdHelper.getInstance().getRrdDb(fileName.substring(0, dotIndex));
            RrdDataDlg dlg = new RrdDataDlg(this, false, rrdDb);
            dlg.showDialog();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    private void clearPerf(java.awt.event.ActionEvent evt)
    {
        String root = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, CollConstants.DEFAULT_RRD_ROOT);
        File rootDir = new File(root);
        DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class, MObjectEntity.class);

        logger.info("开始检查性能数据文件");
        for (File file : rootDir.listFiles())
        {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            dotIndex = dotIndex > -1 ? dotIndex : fileName.length();
            String objId = fileName.substring(0, dotIndex);
            try
            {
                MObjectEntity obj = (MObjectEntity) dal.get(Integer.parseInt(objId));
                if (obj == null)
                {
                    logger.info("对象:" + objId + "不存在,删除对应的性能数据文件:" + fileName);
                    file.delete();
                }
                else
                {
                    logger.info("对象:" + objId + "存在,名称为:" + obj.getObjName());
                }
            }
            catch (Exception e)
            {
                logger.error(e.getMessage());
            }
        }
        logger.info("检查性能数据文件完成");

    }

    private void startTrap(java.awt.event.ActionEvent evt)
    {
        try
        {
            TrapReceiver.getInstance().start();
            startTrapMenuItem.setEnabled(false);
            stopTrapMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopTrap(java.awt.event.ActionEvent evt)
    {
        TrapReceiver.getInstance().stop();
        startTrapMenuItem.setEnabled(true);
        stopTrapMenuItem.setEnabled(false);
    }

    private void startSyslog(java.awt.event.ActionEvent evt)
    {
        try
        {
            Syslogd.getInstance().start();
            startSyslogMenuItem.setEnabled(false);
            stopSyslogMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopSyslog(java.awt.event.ActionEvent evt)
    {
        Syslogd.getInstance().stop();
        startSyslogMenuItem.setEnabled(true);
        stopSyslogMenuItem.setEnabled(false);
    }

    private void startExtend(java.awt.event.ActionEvent evt)
    {
        try
        {
//            Extifd.getInstance().start();
            startExtendMenuItem.setEnabled(false);
            stopExtendMenuItem.setEnabled(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    private void stopExtend(java.awt.event.ActionEvent evt)
    {
//        Extifd.getInstance().stop();
        startExtendMenuItem.setEnabled(true);
        stopExtendMenuItem.setEnabled(false);
    }

    protected void stop()
    {
        CollManager.getInstance().stop();
        TrapReceiver.getInstance().stop();
        Syslogd.getInstance().stop();
//        Extifd.getInstance().stop();
        if (ac != null)
        {
            ac.stop();
        }
        RrdHelper.getInstance().destroy();
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
                CollMgrFrm mgr = new CollMgrFrm("采集管理");
                mgr.setVisible(true);
            }
        });
    }
}
