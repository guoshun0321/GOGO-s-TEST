package jetsennet.jbmp.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jetsennet.jbmp.datacollect.datasource.BFSSAgent;
import jetsennet.jbmp.datacollect.datasource.DataCollAgent;
import jetsennet.jbmp.trap.receiver.TrapPduHandleAgent;
import jetsennet.jbmp.trap.receiver.TrapReceiver;

import org.apache.log4j.Logger;

public class CollAgentFrm extends BaseMgrFrm {

	/**
	 * 开始菜单
	 */
	JMenuItem startMenuItem;
	/**
	 * 结束菜单
	 */
	JMenuItem stopMenuItem;

	private static final Logger logger = Logger.getLogger(CollAgentFrm.class);

	/**
	 * @param title
	 */
	public CollAgentFrm(String title) {
		super(title);
	}

	@Override
	protected void initComponents() {
		super.initComponents();

		// 开始
		startMenuItem = new JMenuItem();
		startMenuItem.setText("开始");
		startMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doStart(evt);

			}
		});

		// 结束
		stopMenuItem = new JMenuItem();
		stopMenuItem.setText("结束");
		stopMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doStop(evt);
			}
		});
		JMenu cfgMenu = new JMenu();
		cfgMenu.setText("操作");
		cfgMenu.add(startMenuItem);
		cfgMenu.add(stopMenuItem);
		addMenu(cfgMenu);
		startMenuItem.setEnabled(true);
		stopMenuItem.setEnabled(false);
	}

	private void doStart(java.awt.event.ActionEvent evt) {
		try {
			logger.info("代理采集器准备启动。");
			logger.info("USB通讯模块启动。");
			BFSSAgent.getInstance().start();

			logger.info("代理采集器数据（性能数据采集）启动。");
			DataCollAgent.getInstance().start();

			logger.info("代理采集器数据（Trap接收）启动。");
			TrapReceiver.getInstance().setTrapProcess(new TrapPduHandleAgent());
			TrapReceiver.getInstance().start();
			logger.info("代理采集器启动。");

			startMenuItem.setEnabled(false);
			stopMenuItem.setEnabled(true);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void doStop(java.awt.event.ActionEvent evt) {
		try {
			logger.info("代理采集器准备停止。");
			logger.info("USB通讯模块停止。");
			BFSSAgent.getInstance().stop();

			logger.info("代理采集器数据（性能数据采集）启动。");
			DataCollAgent.getInstance().stop();

			logger.info("代理采集器数据（Trap接收）停止。");
			TrapReceiver.getInstance().stop();
			logger.info("代理采集器停止。");
			startMenuItem.setEnabled(true);
			stopMenuItem.setEnabled(false);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				CollAgentFrm mgr = new CollAgentFrm("采集代理");
				mgr.setVisible(true);
			}
		});
	}
}
