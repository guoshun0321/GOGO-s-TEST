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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.util.UIUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集器配置对话框
 * @author 梁洪杰
 */
public class RrdCfgDlg extends JDialog
{
    private static final Logger logger = Logger.getLogger(RrdCfgDlg.class);
    private JLabel rrdDirLbl;
    private JTextField rrdDirFld;
    private JButton okBtn;
    private JButton cancleBtn;

    /**
     * @param parent 参数
     * @param modal 参数
     */
    public RrdCfgDlg(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        setTitle("RRD设置");
        rrdDirLbl = new JLabel("RRD保存目录:");
        rrdDirFld = new JTextField();
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
                    logger.info("RRD设置成功");
                }
                catch (Exception e1)
                {
                    logger.error("RRD设置异常", e1);
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
        con.add(rrdDirLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0),
            0, 0));
        con.add(rrdDirFld, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0,
            10), 0, 0));
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
            logger.error("RRD设置异常", e1);
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
        String rrdRoot = rrdDirFld.getText();
        if (rrdRoot == null)
        {
            rrdRoot = CollConstants.DEFAULT_RRD_ROOT;
        }
        if (rrdRoot.charAt(rrdRoot.length() - 1) != '/')
        {
            rrdRoot = rrdRoot + "/";
        }
        XmlCfgUtil.setStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, rrdRoot);
    }

    private void initData() throws Exception
    {
        String rrdRoot = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, CollConstants.DEFAULT_RRD_ROOT);
        rrdDirFld.setText(rrdRoot);
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
                RrdCfgDlg dialog = new RrdCfgDlg(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
