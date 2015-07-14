/**
 * 日 期： 2011-12-2
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  RrdTableModel.java
 * 历 史： 2011-12-2 创建
 */
package jetsennet.jbmp.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.jrobin.core.ConsolFuns;
import org.jrobin.core.FetchData;
import org.jrobin.core.FetchRequest;
import org.jrobin.core.RrdDb;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.UIUtil;

/**
 * Rrd数据展示对话框
 */
public class RrdDataDlg extends JDialog
{
    private static final Logger LOG = Logger.getLogger(RrdDataDlg.class);

    private JLabel fileNameLbl;
    private JComboBox dayCmb;
    private JButton refreshBtn;
    private JButton descBtn;
    private JScrollPane rrdScrollPane;
    private JTable rrdTable;
    private RrdTableModel rrdModel;

    /**
     * @param parent 参数
     * @param modal 参数
     * @param rrdDb 参数
     * @throws Exception 异常
     */
    public RrdDataDlg(java.awt.Frame parent, boolean modal, final RrdDb rrdDb) throws Exception
    {
        super(parent, modal);
        setTitle("Rrd数据查看器");
        Container con = getContentPane();
        con.setLayout(new GridBagLayout());
        fileNameLbl =
            new JLabel("文件:"
                + rrdDb.getPath()
                + "    "
                + "采集间隔:"
                + rrdDb.getRrdDef().getStep()
                + "秒"
                + "    "
                + "列数:"
                + rrdDb.getDsCount()
                + "    "
                + "时间:");
        dayCmb = new JComboBox(new String[] { "一小时", "一天", "七天", "一个月" });
        dayCmb.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshTable();
            }
        });
        refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshTable();
            }
        });
        descBtn = new JButton("查看指标信息");
        descBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JDialog dlg = new JDialog(RrdDataDlg.this, "指标信息");
                Container container = dlg.getContentPane();
                container.setLayout(new GridBagLayout());
                JTable objInfoTable = new JTable();
                try
                {
                    ObjectInfoModel objInfoModel = new ObjectInfoModel(rrdDb);
                    objInfoTable.setModel(objInfoModel);
                }
                catch (Exception e1)
                {
                    LOG.error(e1.getMessage());
                }
                JScrollPane objInfoPane = new JScrollPane();
                objInfoPane.setViewportView(objInfoTable);
                objInfoPane.setPreferredSize(new Dimension(400, 300));
                container.add(objInfoPane, new GridBagConstraints(0,
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

                dlg.setModal(false);
                dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dlg.pack();
                UIUtil.setLocation(dlg);
                dlg.setVisible(true);
            }
        });
        rrdScrollPane = new JScrollPane();
        rrdTable = new JTable();
        rrdTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rrdModel = new RrdTableModel(rrdDb);
        rrdTable.setModel(rrdModel);
        resetTable(3600);
        rrdScrollPane.setViewportView(rrdTable);
        rrdScrollPane.setPreferredSize(new Dimension(600, 500));
        con.add(fileNameLbl, new GridBagConstraints(0,
            0,
            1,
            1,
            0.0,
            0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(10, 10, 10, 0),
            0,
            0));
        con.add(dayCmb, new GridBagConstraints(1,
            0,
            1,
            1,
            0.0,
            0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(10, 0, 10, 10),
            0,
            0));
        con.add(refreshBtn, new GridBagConstraints(2,
            0,
            1,
            1,
            0.0,
            0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(10, 0, 10, 10),
            0,
            0));
        con.add(descBtn, new GridBagConstraints(3,
            0,
            1,
            1,
            0.0,
            0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(10, 10, 10, 10),
            0,
            0));
        con.add(rrdScrollPane, new GridBagConstraints(0,
            1,
            4,
            1,
            1.0,
            1.0,
            GridBagConstraints.EAST,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0),
            0,
            0));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    rrdDb.close();
                }
                catch (IOException e1)
                {
                    LOG.error(e1.getMessage(), e1);
                }
            }
        });
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

    private void refreshTable()
    {
        switch (dayCmb.getSelectedIndex())
        {
        case 0:
            resetTable(3600);
            break;
        case 1:
            resetTable(3600 * 24);
            break;
        case 2:
            resetTable(3600 * 24 * 7);
            break;
        case 3:
            resetTable(3600 * 24 * 30);
            break;
        case 4:
            resetTable(3600 * 24 * 365L);
            break;
        default:
            break;
        }
    }

    private void resetTable(long time)
    {
        try
        {
            rrdModel.setFetSize(time);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage());
        }
        boolean first = true;
        Enumeration<TableColumn> columns = rrdTable.getColumnModel().getColumns();
        while (columns.hasMoreElements())
        {
            if (first)
            {
                first = false;
                columns.nextElement().setPreferredWidth(130);
            }
            else
            {
                columns.nextElement().setPreferredWidth(60);
            }
        }
    }

    private class ObjectInfoModel extends AbstractTableModel
    {
        private String[][] values;

        public ObjectInfoModel(RrdDb rrd) throws Exception
        {
            DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class, ObjAttribEntity.class);
            String[] columns = rrd.getDsNames();
            values = new String[columns.length][3];
            for (int i = 0; i < columns.length; i++)
            {
                values[i] = new String[3];
                values[i][0] = columns[i];
                ObjAttribEntity oa = (ObjAttribEntity) dal.get(Integer.parseInt(columns[i]));
                if (oa != null)
                {
                    values[i][1] = oa.getObjattrName();
                    values[i][2] = String.valueOf(oa.getCollTimespan());
                }
                else
                {
                    values[i][1] = "不存在";
                    values[i][2] = "";
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount()
        {
            return this.values.length;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return values[rowIndex][columnIndex];
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount()
        {
            return 3;
        }

        @Override
        public String getColumnName(int column)
        {
            switch (column)
            {
            case 0:
                return "ID";
            case 1:
                return "指标名称";
            case 2:
                return "采集周期(秒)";
            default:
                break;
            }
            return "";
        }
    }

    /**
     * 显示rrd文件数据的表格Model
     */
    private class RrdTableModel extends AbstractTableModel
    {
        private RrdDb rrdDb;
        private String[] dsNames;
        private double[][] values;
        private long[] timestamps;
        private int rowCount;

        public RrdTableModel(RrdDb rrd) throws Exception
        {
            super();
            this.rrdDb = rrd;
            this.dsNames = rrdDb.getDsNames();
        }

        public void setFetSize(long size) throws Exception
        {
            long endTime = rrdDb.getLastUpdateTime();
            long startTime = endTime - size;
            startTime = startTime > 0 ? startTime : 0;
            FetchRequest request = rrdDb.createFetchRequest(ConsolFuns.CF_LAST, startTime, endTime);
            FetchData fetchData = request.fetchData();
            this.values = fetchData.getValues();
            this.timestamps = fetchData.getTimestamps();
            this.rowCount = this.timestamps.length;
            fireTableStructureChanged();
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount()
        {
            return dsNames.length + 1;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount()
        {
            return this.rowCount;
        }

        /*
         * (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (columnIndex == 0)
            {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return df.format(timestamps[rowCount - 1 - rowIndex] * 1000);
            }
            double value = values[columnIndex - 1][rowCount - 1 - rowIndex];
            if (Double.isNaN(value))
            {
                return "";
            }
            return value;
        }

        @Override
        public String getColumnName(int column)
        {
            if (column == 0)
            {
                return "时间";
            }
            return dsNames[column - 1];
        }
    }
}
