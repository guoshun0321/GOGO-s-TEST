/************************************************************************
日 期：2012-04-28
作 者: 梁洪杰
版 本：v1.3
描 述: 采集器配置对话框
历 史：
 ************************************************************************/
package jetsennet.jbmp.ui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.Collector;
import jetsennet.jbmp.dataaccess.CollectorDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.CollectorEntity;
import jetsennet.jbmp.exception.UIException;
import jetsennet.jbmp.util.UIUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集器配置对话框
 * @author 梁洪杰
 */
public class ClusterCfgDlg extends JDialog
{
    private static final Logger logger = Logger.getLogger(ClusterCfgDlg.class);

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String CONFIG_FILE = "configuration.properties";
    private static final String COLL_FILE = "flush-tcp-collector";
    private static final String NOTIFY_FILE = "flush-tcp-notify";
    private static final String WEB_FILE = "flush-tcp-web";
    private static final String TRAP_FILE = "trap";
    private static final String SYSLOG_FILE = "syslog";
    private static final String EXTIF_FILE = "extif";

    private static final String CLUSTER_NAME = "cluster.name";
    private static final String PING_LOCALHOST = "ping.localhost";

    private Properties props = new Properties();
    private JLabel collNameLbl;
    private JTextField collNameFld;
    private JLabel collectorLbl;
    private JComboBox collectorCmb;
    private JLabel collectorIpLbl;
    private JTextField collectorIpFld;
    private JLabel collectorPortLbl;
    private JTextField collectorPortFld;
    private JLabel webIpLbl;
    private JTextField webIpFld;
    private JLabel webPortLbl;
    private JTextField webPortFld;
    private JLabel notifyIpLbl;
    private JTextField notifyIpFld;
    private JLabel notifyPortLbl;
    private JTextField notifyPortFld;
    private JButton okBtn;
    private JButton cancleBtn;

    /**
     * @param parent 参数
     * @param modal 参数
     * @throws UIException 异常
     */
    public ClusterCfgDlg(java.awt.Frame parent, boolean modal) throws UIException
    {
        super(parent, modal);
        setTitle("集群设置");
        collNameLbl = new JLabel("集群名称:");
        collNameFld = new JTextField();
        webIpLbl = new JLabel("Web服务器IP:");
        webIpFld = new JTextField();
        webPortLbl = new JLabel("集群端口:");
        webPortFld = new JTextField();
        collectorLbl = new JLabel("采集器:");
        collectorCmb = new JComboBox();
        collectorIpLbl = new JLabel("采集器IP:");
        collectorIpFld = new JTextField();
        collectorPortLbl = new JLabel("集群端口:");
        collectorPortFld = new JTextField();
        notifyIpLbl = new JLabel("通知服务器IP:");
        notifyIpFld = new JTextField();
        notifyPortLbl = new JLabel("集群端口:");
        notifyPortFld = new JTextField();
        okBtn = new JButton("确定");
        okBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    okAction();
                    dispose();
                    logger.info("集群设置成功");
                }
                catch (Exception e1)
                {
                    logger.error("集群设置异常", e1);
                }
            }
        });
        cancleBtn = new JButton("取消");
        cancleBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });

        Container con = getContentPane();
        con.setLayout(new GridBagLayout());
        con.add(collNameLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0),
            0, 0));
        con.add(collNameFld, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10,
            0, 10), 0, 0));
        con.add(new JSeparator(), new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(webIpLbl, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0,
            0));
        con.add(webIpFld, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0,
            10), 0, 0));
        con.add(webPortLbl, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0),
            0, 0));
        con.add(webPortFld, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10,
            0, 10), 0, 0));
        con.add(new JSeparator(), new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(collectorLbl, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(10, 10, 0, 0), 0, 0));
        con.add(collectorCmb, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10,
            0, 10), 0, 0));
        con.add(collectorIpLbl, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0,
            0), 0, 0));
        con.add(collectorIpFld, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(collectorPortLbl, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10,
            0, 0), 0, 0));
        con.add(collectorPortFld, new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(new JSeparator(), new GridBagConstraints(0, 8, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(notifyIpLbl, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0),
            0, 0));
        con.add(notifyIpFld, new GridBagConstraints(1, 9, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10,
            0, 10), 0, 0));
        con.add(notifyPortLbl, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0,
            0), 0, 0));
        con.add(notifyPortFld, new GridBagConstraints(1, 10, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,
            10, 0, 10), 0, 0));
        con.add(new JSeparator(), new GridBagConstraints(0, 11, 3, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(
            10, 10, 0, 10), 0, 0));
        con.add(okBtn, new GridBagConstraints(1, 12, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0,
            0));
        con.add(cancleBtn, new GridBagConstraints(2, 12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(10, 10, 10, 10), 0, 0));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        try
        {
            initData();
        }
        catch (Exception e1)
        {
            logger.error("采集器设置异常", e1);
        }
    }

    /**
     * 显示框
     */
    public void showDialog()
    {
        this.pack();
        UIUtil.setLocation(this);
        this.setVisible(true);
    }

    protected void okAction() throws Exception
    {
        // 设置集群名称
        String collName = collNameFld.getText();
        if (collName != null && collName.length() != 0)
        {
            Map<String, String> map = new HashMap<String, String>();
            map.put(CLUSTER_NAME, collName);
            replace(new File(this.getClass().getClassLoader().getResource(CONFIG_FILE).toURI().getPath()), map);
        }
        
        // 更新集群信息
        String collectorIp = collectorIpFld.getText();
        if (collectorIp != null && collectorIp.length() != 0)
        {
            // 自动生成时，更新数据库信息
            CollectorEntity entity = (CollectorEntity) collectorCmb.getSelectedItem();
            if (entity.getCollId() < 0)
            {
                entity.setCollName(collectorIp);
            }
            entity.setIpAddr(collectorIp);
            Collector cdao = ClassWrapper.wrap(Collector.class);
            cdao.insertOrUpdate(entity);
            XmlCfgUtil.setStringValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_ID_CFG, Integer.toString(entity.getCollId()));
            XmlCfgUtil.setStringValue(COLL_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", collectorIp);
            XmlCfgUtil.setStringValue(SYSLOG_FILE, "server" + XmlCfgUtil.CFG_PRE + "ip" + XmlCfgUtil.CFG_POS, collectorIp);
            XmlCfgUtil.setStringValue(TRAP_FILE, "server" + XmlCfgUtil.CFG_PRE + "ip" + XmlCfgUtil.CFG_POS, collectorIp);
            XmlCfgUtil.setStringValue(EXTIF_FILE, "server" + XmlCfgUtil.CFG_PRE + "ip" + XmlCfgUtil.CFG_POS, collectorIp);
            Map<String, String> map = new HashMap<String, String>();
            map.put(PING_LOCALHOST, collectorIp);
            replace(new File(this.getClass().getClassLoader().getResource(CONFIG_FILE).toURI().getPath()), map);
        }
        String collectorPort = collectorPortFld.getText();
        if (collectorPort != null && collectorPort.length() != 0)
        {
            XmlCfgUtil.setStringValue(COLL_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", collectorPort);
        }
        String webIp = webIpFld.getText();
        if (webIp != null && webIp.length() != 0)
        {
            XmlCfgUtil.setStringValue(WEB_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", webIp);
        }
        String webPort = webPortFld.getText();
        if (webPort != null && webPort.length() != 0)
        {
            XmlCfgUtil.setStringValue(WEB_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", webPort);
        }
        String notifyIp = notifyIpFld.getText();
        if (notifyIp != null && notifyIp.length() != 0)
        {
            XmlCfgUtil.setStringValue(NOTIFY_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", notifyIp);
        }
        String notifyPort = notifyPortFld.getText();
        if (notifyPort != null && notifyPort.length() != 0)
        {
            XmlCfgUtil.setStringValue(NOTIFY_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", notifyPort);
        }
        String initial_hosts = "";
        if (webIp != null && webIp.length() != 0 && webPort != null && webPort.length() != 0)
        {
            initial_hosts += webIp + "[" + webPort + "]";
        }
        if (collectorIp != null && collectorIp.length() != 0 && collectorPort != null && collectorPort.length() != 0)
        {
            initial_hosts += "," + collectorIp + "[" + collectorPort + "]";
        }
        if (notifyIp != null && notifyIp.length() != 0 && notifyPort != null && notifyPort.length() != 0)
        {
            initial_hosts += "," + notifyIp + "[" + notifyPort + "]";
        }
        if (initial_hosts.length() != 0)
        {
            XmlCfgUtil.setStringValue(COLL_FILE, "j", "urn:org:jgroups", "j:TCPPING", "initial_hosts", "${jgroups.tcpping.initial_hosts:"
                + initial_hosts + "}");
            XmlCfgUtil.setStringValue(WEB_FILE, "j", "urn:org:jgroups", "j:TCPPING", "initial_hosts", "${jgroups.tcpping.initial_hosts:"
                + initial_hosts + "}");
            XmlCfgUtil.setStringValue(NOTIFY_FILE, "j", "urn:org:jgroups", "j:TCPPING", "initial_hosts", "${jgroups.tcpping.initial_hosts:"
                + initial_hosts + "}");
        }
    }

    private void replace(File file, Map<String, String> map) throws Exception
    {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        Map<String, String> newMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            newMap.put(entry.getKey() + "=" + props.getProperty(entry.getKey()).replaceAll("\\:", "\\\\:").replaceAll("\\=", "\\\\="), entry.getKey()
                + "=" + entry.getValue());
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String tmp = null;
        while ((tmp = in.readLine()) != null)
        {
            String line = newMap.get(tmp);
            if (line != null)
            {
                sb.append(line).append("\r\n");
            }
            else
            {
                sb.append(tmp).append("\r\n");
            }
        }
        in.close();
        PrintWriter out = null;
        out = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
        out.println(sb.toString());
        out.close();
    }

    private void initData() throws Exception
    {
        try
        {
            props.load(this.getClass().getResourceAsStream("/" + CONFIG_FILE));
        }
        catch (IOException e)
        {
            logger.error(e);
        }
        String collName = props.getProperty(CLUSTER_NAME);
        if (collName != null)
        {
            collNameFld.setText(collName);
        }
        CollectorEntity newEntity = new CollectorEntity();
        newEntity.setCollId(-1);
        newEntity.setCollName("自动创建");
        newEntity.setIpAddr("127.0.0.1");
        newEntity.setCollType("0");
        newEntity.setCreateTime(new Date());
        collectorCmb.addItem(newEntity);
        CollectorDal cdao = ClassWrapper.wrapTrans(CollectorDal.class);
        List<CollectorEntity> collectorLst = cdao.getAll();
        for (CollectorEntity entiry : collectorLst)
        {
            collectorCmb.addItem(entiry);
        }
        int id = XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_ID_CFG, 0);
        for (int i = 0; i < collectorCmb.getItemCount(); i++)
        {
            if (((CollectorEntity) collectorCmb.getItemAt(i)).getCollId() == id)
            {
                collectorCmb.setSelectedIndex(i);
                break;
            }
        }
        collectorCmb.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                collectorIpFld.setText(((CollectorEntity) collectorCmb.getSelectedItem()).getIpAddr());
            }
        });
        String collectorIp = XmlCfgUtil.getStringValue(COLL_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", null);
        if (collectorIp != null)
        {
            collectorIpFld.setText(collectorIp);
        }
        String collectorPort = XmlCfgUtil.getStringValue(COLL_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", null);
        if (collectorPort != null)
        {
            collectorPortFld.setText(collectorPort);
        }
        String webIp = XmlCfgUtil.getStringValue(WEB_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", null);
        if (webIp != null)
        {
            webIpFld.setText(webIp);
        }
        String webPort = XmlCfgUtil.getStringValue(WEB_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", null);
        if (webIp != null)
        {
            webPortFld.setText(webPort);
        }
        String notifyIp = XmlCfgUtil.getStringValue(NOTIFY_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_addr", null);
        if (notifyIp != null)
        {
            notifyIpFld.setText(notifyIp);
        }
        String notifyPort = XmlCfgUtil.getStringValue(NOTIFY_FILE, "j", "urn:org:jgroups", "j:TCP", "bind_port", null);
        if (webIp != null)
        {
            notifyPortFld.setText(notifyPort);
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
                try
                {
                    ClusterCfgDlg dialog = new ClusterCfgDlg(new javax.swing.JFrame(), true);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter()
                    {
                        public void windowClosing(java.awt.event.WindowEvent e)
                        {
                            System.exit(0);
                        }
                    });
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                }
                catch (UIException ex)
                {
                    logger.error(ex);
                }
            }
        });
    }
}
