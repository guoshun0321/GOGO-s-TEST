/**********************************************************************
 * 日 期： 2012-7-10
 * 作 者:  梁洪杰
 * 版 本： v1.0
 * 描 述:  BaseMgrFrm.java
 * 历 史： 2012-7-10 创建
 *********************************************************************/
package jetsennet.jsmp.nav.syn.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 基础管理窗口类
 */
public class BaseMgrFrm extends javax.swing.JFrame
{

	private LoggerTableModel logModel;
	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	private JMenuItem logMenuItem;
	private JMenuBar jMenuBar1;
	private JPanel jPanel1;
	private JPanel logPanel;
	private JScrollPane logScrollPane;
	private JTable logTable;
	private JTabbedPane tabPanel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(BaseMgrFrm.class);

	/**
	 * @param title 标题
	 */
	public BaseMgrFrm(String title)
	{
		super(title);
		before();
		initComponents();
		after();
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				logger.info("正在准备关闭...");
				stop();
				logger.info("准备关闭");
				System.exit(0);
			}
		});
	}

	//    private void setLookAndFeel()
	//    {
	//        LookAndFeel look = new SubstanceBusinessBlueSteelLookAndFeel();
	//        JFrame.setDefaultLookAndFeelDecorated(look.getSupportsWindowDecorations());
	//        JDialog.setDefaultLookAndFeelDecorated(look.getSupportsWindowDecorations());
	//        try
	//        {
	//            UIManager.setLookAndFeel(look);
	//        }
	//        catch (UnsupportedLookAndFeelException e)
	//        {
	//            e.printStackTrace();
	//        }
	//        this.dispose();
	//        this.setUndecorated(look.getSupportsWindowDecorations());
	//        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	//        SwingUtilities.updateComponentTreeUI(this);
	//    }

	protected void before()
	{
	}

	protected void initComponents()
	{
		logMenuItem = new JMenuItem();
		logMenuItem.setText("清空日志");
		logMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				clearLog(evt);
			}
		});
		exitMenuItem = new JMenuItem();
		exitMenuItem.setText("退出");
		exitMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Object[] options = { "确定", "取消" };
				int result =
					JOptionPane.showOptionDialog(null,
						"确认退出？",
						"退出",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]);
				if (result == 0)
				{
					logger.info("正在准备关闭...");
					stop();
					logger.info("准备关闭");
					dispose();
					System.exit(0);
				}
			}
		});
		fileMenu = new JMenu();
		fileMenu.setText("文件");
		fileMenu.add(logMenuItem);
		fileMenu.add(exitMenuItem);

		jMenuBar1 = new JMenuBar();
		jMenuBar1.add(fileMenu);
		setJMenuBar(jMenuBar1);

		logTable = new JTable();
		logModel = new LoggerTableModel();
		logTable.setModel(logModel);
		logScrollPane = new JScrollPane();
		logScrollPane.setViewportView(logTable);
		logScrollPane.setPreferredSize(new Dimension(790, 500));
		logPanel = new JPanel();
		logPanel.setLayout(new GridBagLayout());
		tabPanel = new JTabbedPane();
		tabPanel.setName("log");
		tabPanel.addTab("日志", logPanel);
		jPanel1 = new JPanel();
		jPanel1.setLayout(new GridBagLayout());
		Container con = getContentPane();
		con.setLayout(new GridBagLayout());
		logPanel.add(logScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
			0,
			0,
			0), 0, 0));
		jPanel1.add(tabPanel, new GridBagConstraints(0,
			0,
			1,
			1,
			1.0,
			1.0,
			GridBagConstraints.EAST,
			GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0,
			0));
		con.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	protected void addMenu(JMenu menu)
	{
		this.jMenuBar1.add(menu);
	}

	protected void after()
	{
		Log4UI.reg("Test", logModel);
		logModel.setParentScr(logScrollPane);
		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		DefaultTableCellRenderer render = new LogTableCellRenderer();
		for (int i = 0; i < logTable.getColumnCount(); i++)
		{
			logTable.getColumn(logTable.getColumnName(i)).setCellRenderer(render);
		}
		configColumns(logTable.getColumnModel());
		this.pack();
		this.setLocationRelativeTo(null);
	}

	protected void configColumns(TableColumnModel tcModel)
	{
		tcModel.getColumn(0).setPreferredWidth(120);
		tcModel.getColumn(1).setPreferredWidth(40);
		tcModel.getColumn(2).setPreferredWidth(300);
		tcModel.getColumn(3).setPreferredWidth(300);
	}

	protected void stop()
	{
	}

	private void clearLog(java.awt.event.ActionEvent evt)
	{
		logModel.clear();
		configColumns(logTable.getColumnModel());
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
				BaseMgrFrm mgr = new BaseMgrFrm("捷成监控系统");
				mgr.setVisible(true);
			}
		});
	}

	private class LogTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private Color dc = null;

		public LogTableCellRenderer()
		{
			synchronized (LogTableCellRenderer.class)
			{
				if (dc == null)
				{
					dc = this.getBackground();
				}
			}
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			LoggerTableModel model = (LoggerTableModel) table.getModel();
			LoggingEvent event = model.get(row);
			if (event != null)
			{
				if (event.getLevel().toInt() == Level.ERROR.toInt())
				{
					setBackground(Color.red);
				}
				else if (event.getLevel().toInt() == Level.WARN.toInt())
				{
					setBackground(Color.yellow);
				}
				else if (event.getLevel().toInt() == Level.DEBUG.toInt())
				{
					setBackground(Color.GRAY);
				}
				else
				{
					setBackground(dc);
				}
			}
			Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			return com;
		}
	}
}
