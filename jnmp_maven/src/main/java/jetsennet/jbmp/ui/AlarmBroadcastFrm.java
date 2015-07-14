package jetsennet.jbmp.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jetsennet.jbmp.protocols.tts.AlarmBroadcast;
import jetsennet.jbmp.ui.BaseMgrFrm;

import org.apache.log4j.Logger;

public class AlarmBroadcastFrm extends BaseMgrFrm
{

	/**
	 * 开始菜单
	 */
	JMenuItem startMenuItem;
	/**
	 * 结束菜单
	 */
	JMenuItem stopMenuItem;

	private static final Logger logger = Logger.getLogger(AlarmBroadcastFrm.class);

	/**
	 * @param title
	 */
	public AlarmBroadcastFrm(String title)
	{
		super(title);
	}

	@Override
	protected void initComponents()
	{
		super.initComponents();

		// 开始
		startMenuItem = new JMenuItem();
		startMenuItem.setText("开始");
		startMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				doStart(evt);

			}
		});

		// 结束
		stopMenuItem = new JMenuItem();
		stopMenuItem.setText("结束");
		stopMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				doStop(evt);
			}
		});
		JMenu cfgMenu = new JMenu();
		cfgMenu.setText("语音播报");
		cfgMenu.add(startMenuItem);
		cfgMenu.add(stopMenuItem);
		addMenu(cfgMenu);
		startMenuItem.setEnabled(true);
		stopMenuItem.setEnabled(false);
	}

	private void doStart(java.awt.event.ActionEvent evt)
	{
		try
		{
			AlarmBroadcast.getInstance().start("flush-tcp-notify.xml");
			startMenuItem.setEnabled(false);
			stopMenuItem.setEnabled(true);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}

	private void doStop(java.awt.event.ActionEvent evt)
	{
		try
		{
			AlarmBroadcast.getInstance().stop();
			startMenuItem.setEnabled(true);
			stopMenuItem.setEnabled(false);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}

	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				AlarmBroadcastFrm mgr = new AlarmBroadcastFrm("语音播报");
				mgr.setVisible(true);
			}
		});
	}

}
