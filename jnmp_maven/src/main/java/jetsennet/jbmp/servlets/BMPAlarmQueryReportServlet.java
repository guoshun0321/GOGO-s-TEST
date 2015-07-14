/************************************************************************
 * 日 期：2012-05-04 
 * 作 者: 李志超
 * 版 本：v1.3 
 * 描 述: 报警查询报表
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.util.StringUtil;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author 李志超
 */
public class BMPAlarmQueryReportServlet extends HttpServlet
{
    private Map<String, String> titleMapping;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("报警查询报表.xls", "UTF-8"));
        initTitleMapping();
        String flag = request.getParameter("flag");
        String groupId = request.getParameter("og.GROUP_ID");
        String collTimeStart = request.getParameter("COLL_TIME_START");
        String collTimeEnd = request.getParameter("COLL_TIME_END");
        String checkUser = request.getParameter("CHECK_USER");
        String alarmId = request.getParameter("ALARMEVT_ID");
        String objName = request.getParameter("o.OBJ_NAME");
        String alarmLevel = request.getParameter("ALARM_LEVEL");
        String eventState = request.getParameter("EVENT_STATE");
        String attribId = request.getParameter("r.ATTRIB_ID");
        String attribType = request.getParameter("ac.CLASS_ID");
        String resultFieldString = request.getParameter("ResultFields");
        String conditionGroupId = "";
        String conditionCollTimeStart = "";
        String conditionCollTimeEnd = "";
        String conditionCheckUser = "";
        String conditionAlarmId = "";
        String conditionObjName = "";
        String conditionAlarmLevel = "";
        String conditionEventState = "";
        String conditionAttribId = "";
        String conditionAttribType = "";
        if ("1".equals(flag))
        {
        	if (groupId != null && !"".equals(groupId))
        	{
        		conditionGroupId = MessageFormat.format("AND og.GROUP_ID={0}", groupId);
        	}
            if (collTimeStart != null && !"".equals(collTimeStart))
            {
                conditionCollTimeStart = MessageFormat.format("AND COLL_TIME>={0}", collTimeStart);
            }
            if (collTimeEnd != null && !"".equals(collTimeEnd))
            {
                conditionCollTimeEnd = MessageFormat.format("AND COLL_TIME<={0}", collTimeEnd);
            }
            if (checkUser != null && !"".equals(checkUser))
            {
                conditionCheckUser = MessageFormat.format("AND CHECK_USER LIKE ''%{0}%''", checkUser);
            }
            if (alarmId != null && !"".equals(alarmId))
            {
                conditionAlarmId = MessageFormat.format("AND ALARMEVT_ID LIKE ''%{0}%''", alarmId);
            }
            if (objName != null && !"".equals(objName))
            {
                conditionObjName = MessageFormat.format("AND o.OBJ_NAME LIKE ''%{0}%''", objName);
            }
            if (alarmLevel != null && !"".equals(alarmLevel))
            {
                conditionAlarmLevel = MessageFormat.format("AND ALARM_LEVEL={0}", alarmLevel);
            }
            if (eventState != null && !"".equals(eventState))
            {
                conditionEventState = MessageFormat.format("AND EVENT_STATE={0}", eventState);
            }
            if (attribId != null && !"".equals(attribId))
            {
                conditionAttribId = MessageFormat.format("AND r.ATTRIB_ID={0}", attribId);
            }
            if (attribType != null && !"".equals(attribType))
            {
                if ("1".equals(attribType))
                {
                    conditionAttribType = MessageFormat.format("AND ac.CLASS_ID={0}", 30);
                }
                if ("2".equals(attribType))
                {
                    conditionAttribType = MessageFormat.format("AND ac.CLASS_ID={0}", 18);
                }
                if ("3".equals(attribType))
                {
                    conditionAttribType = MessageFormat.format("AND ac.CLASS_ID={0}", 24);
                }
                if ("4".equals(attribType))
                {
                    conditionAttribType = MessageFormat.format("AND ac.CLASS_ID>={0}", 2000);
                }

            }
        }
        String sql = "";
        // EVENT_STATE为null或空字符串时，导出BMP_ALARMEVENT和BMP_ALARMEVENTLOG中符合条件的数据
        if(StringUtil.isNullOrEmpty(eventState))
        {
        	sql = MessageFormat.format(
    				"SELECT * FROM " +
    					"(SELECT {0} FROM BMP_ALARMEVENT ae " +
        					"INNER JOIN BMP_OBJATTRIB r ON r.OBJATTR_ID=ae.OBJATTR_ID " +
        					"LEFT JOIN BMP_ATTRIB2CLASS ac ON r.ATTRIB_ID=ac.ATTRIB_ID " +
        					"INNER JOIN BMP_OBJECT o ON o.OBJ_ID=ae.OBJ_ID " +
        					((groupId != null && !"".equals(groupId)) ? "LEFT JOIN BMP_OBJ2GROUP o2g ON o2g.OBJ_ID=o.OBJ_ID LEFT JOIN BMP_OBJGROUP og ON og.GROUP_ID=o2g.GROUP_ID " : "") +
    					"WHERE 2=2 {1} {2} {3} {4} {5} {6} {7} {8} {9} {10} " +
    					"UNION ALL " +
    					"SELECT {0} FROM BMP_ALARMEVENTLOG ae " +
        					"INNER JOIN BMP_OBJATTRIB r ON r.OBJATTR_ID=ae.OBJATTR_ID " +
        					"LEFT JOIN BMP_ATTRIB2CLASS ac ON r.ATTRIB_ID=ac.ATTRIB_ID " +
        					"INNER JOIN BMP_OBJECT o ON o.OBJ_ID=ae.OBJ_ID " +
        					((groupId != null && !"".equals(groupId)) ? "LEFT JOIN BMP_OBJ2GROUP o2g ON o2g.OBJ_ID=o.OBJ_ID LEFT JOIN BMP_OBJGROUP og ON og.GROUP_ID=o2g.GROUP_ID " : "") +
    					"WHERE 2=2 {1} {2} {3} {4} {5} {6} {7} {8} {9} {10} " +
    				") aeu {11}",
    				(resultFieldString != null && !"".equals(resultFieldString)) ? resultFieldString : "*", conditionGroupId, conditionCollTimeStart,
					conditionCollTimeEnd, conditionCheckUser, conditionAlarmId, conditionObjName, conditionAlarmLevel, conditionEventState,
					conditionAttribId, conditionAttribType, resultFieldString.indexOf("ALARMEVT_ID") != -1 ? "ORDER BY ALARMEVT_ID DESC" : "");
        }
        //EVENT_STATE为0或1时，导出BMP_ALARMEVENT中符合条件的数据
        else if("0".equals(eventState) || "1".equals(eventState))
        {
        	sql = MessageFormat.format(
					"SELECT {0} FROM BMP_ALARMEVENT ae " +
    					"INNER JOIN BMP_OBJATTRIB r ON r.OBJATTR_ID=ae.OBJATTR_ID " +
    					"LEFT JOIN BMP_ATTRIB2CLASS ac ON r.ATTRIB_ID=ac.ATTRIB_ID " +
    					"INNER JOIN BMP_OBJECT o ON o.OBJ_ID=ae.OBJ_ID " +
    					((groupId != null && !"".equals(groupId)) ? "LEFT JOIN BMP_OBJ2GROUP o2g ON o2g.OBJ_ID=o.OBJ_ID LEFT JOIN BMP_OBJGROUP og ON og.GROUP_ID=o2g.GROUP_ID " : "") +
					"WHERE 2=2 {1} {2} {3} {4} {5} {6} {7} {8} {9} {10} {11}",
    				(resultFieldString != null && !"".equals(resultFieldString)) ? resultFieldString : "*", conditionGroupId, conditionCollTimeStart,
					conditionCollTimeEnd, conditionCheckUser, conditionAlarmId, conditionObjName, conditionAlarmLevel, conditionEventState,
					conditionAttribId, conditionAttribType, resultFieldString.indexOf("ALARMEVT_ID") != -1 ? "ORDER BY ALARMEVT_ID DESC" : "");
        }
        //EVENT_STATE为2或3时，导出BMP_ALARMEVENTLOG中符合条件的数据
        else if("2".equals(eventState) || "3".equals(eventState))
        {
        	sql = MessageFormat.format(
					"SELECT {0} FROM BMP_ALARMEVENTLOG ae " +
    					"INNER JOIN BMP_OBJATTRIB r ON r.OBJATTR_ID=ae.OBJATTR_ID " +
    					"LEFT JOIN BMP_ATTRIB2CLASS ac ON r.ATTRIB_ID=ac.ATTRIB_ID " +
    					"INNER JOIN BMP_OBJECT o ON o.OBJ_ID=ae.OBJ_ID " +
    					((groupId != null && !"".equals(groupId)) ? "LEFT JOIN BMP_OBJ2GROUP o2g ON o2g.OBJ_ID=o.OBJ_ID LEFT JOIN BMP_OBJGROUP og ON og.GROUP_ID=o2g.GROUP_ID " : "") +
					"WHERE 2=2 {1} {2} {3} {4} {5} {6} {7} {8} {9} {10} {11}",
    				(resultFieldString != null && !"".equals(resultFieldString)) ? resultFieldString : "*", conditionGroupId, conditionCollTimeStart,
					conditionCollTimeEnd, conditionCheckUser, conditionAlarmId, conditionObjName, conditionAlarmLevel, conditionEventState,
					conditionAttribId, conditionAttribType, resultFieldString.indexOf("ALARMEVT_ID") != -1 ? "ORDER BY ALARMEVT_ID DESC" : "");
        }
        
        WritableWorkbook wb = null;
        try
        {
            DefaultDal<AlarmEventEntity> dalAlarmEvent = new DefaultDal<AlarmEventEntity>();
            List<Map<String, Object>> alarmEventList = dalAlarmEvent.getMapLst(sql);
            wb = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet ws = wb.createSheet("报警查询报表", 0);
            createTitle(resultFieldString, ws);
            for (int i = 0; i < alarmEventList.size(); i++)
            {
                createData(resultFieldString, ws, alarmEventList.get(i), i + 1);
            }
            wb.write();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (wb != null)
            {
                try
                {
                    wb.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    /**
     * 初始化信息
     * @return
     */
    private void initTitleMapping()
    {
        if (titleMapping == null)
        {
            titleMapping = new HashMap<String, String>();
        }
        titleMapping.put("ae.ALARMEVT_ID", "编号");
        titleMapping.put("o.OBJ_NAME", "报警对象");
        titleMapping.put("r.OBJATTR_NAME", "报警类型");
        titleMapping.put("ae.EVENT_STATE", "报警状态");
        titleMapping.put("ae.ALARM_LEVEL", "报警等级");
        titleMapping.put("ae.COLL_TIME", "报警时间");
        titleMapping.put("ae.RESUME_TIME", "恢复时间");
        titleMapping.put("ae.EVENT_DURATION", "持续时间");
        titleMapping.put("ae.ALARM_COUNT", "报警次数");
        titleMapping.put("ae.EVENT_DESC", "报警描述");
        titleMapping.put("ae.CHECK_USER", "操作人");
        titleMapping.put("ae.CHECK_TIME", "操作时间");
        titleMapping.put("ae.CHECK_DESC", "意见");
    }

    /**
     * 创建表头
     * @param resultFields
     * @param ws
     * @return
     */
    private void createTitle(String resultFields, WritableSheet ws) throws Exception
    {
        String[] resultFieldList = resultFields.split(",");
        WritableCellFormat format = new WritableCellFormat(new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD));
        format.setAlignment(Alignment.CENTRE);
        format.setVerticalAlignment(VerticalAlignment.CENTRE);
        for (int i = 0; i < resultFieldList.length; i++)
        {
            String title = titleMapping.get(resultFieldList[i]);
            ws.setColumnView(i, 15);
            WritableCell titleCell = new Label(i, 0, title, format);
            ws.addCell(titleCell);
        }
    }

    /**
     * 创建数据
     * @param resultFields
     * @param ws
     * @param data
     * @param row
     * @return
     */
    private void createData(String resultFields, WritableSheet ws, Map<String, Object> data, int row) throws Exception
    {
        String[] resultFieldList = resultFields.split(",");
        for (int i = 0; i < resultFieldList.length; i++)
        {
            WritableCellFormat format = new WritableCellFormat(new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD));
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            format.setWrap(true);
            int prefixSplitIndex = resultFieldList[i].indexOf(".");
            Object dataValue = data.get(prefixSplitIndex == -1 ? resultFieldList[i] : resultFieldList[i].substring(prefixSplitIndex + 1));
            if (dataValue == null)
            {
                continue;
            }
            WritableCell dataCell = null;
            if (dataValue instanceof java.lang.Number)
            {
                int dataValueInt = ((java.lang.Number) dataValue).intValue();
                if ("ae.EVENT_STATE".equals(resultFieldList[i]))
                {
                    String[] eventStates = { "未确认", "已确认", "已清除", "已处理" };
                    format.setAlignment(Alignment.CENTRE);
                    dataCell = new Label(i, row, eventStates[dataValueInt], format);
                }
                else if ("ae.ALARM_LEVEL".equals(resultFieldList[i]))
                {
                    Map<String, String> alarmLevels = new HashMap<String, String>();
                    alarmLevels.put("10", "警告报警");
                    alarmLevels.put("20", "一般报警");
                    alarmLevels.put("30", "重要报警");
                    alarmLevels.put("40", "严重报警");
                    alarmLevels.put("50", "离线报警");
                    format.setAlignment(Alignment.CENTRE);
                    dataCell = new Label(i, row, alarmLevels.get(dataValue.toString()), format);
                }
                else if ("ae.COLL_TIME".equals(resultFieldList[i]) || "ae.RESUME_TIME".equals(resultFieldList[i])
                    || "ae.CHECK_TIME".equals(resultFieldList[i]))
                {
                    long dataValueLong = ((java.lang.Number) dataValue).longValue();
                    if (dataValueLong > 0)
                    {
                        format.setAlignment(Alignment.CENTRE);
                        dataCell = new Label(i, row, String.format("%1$tF %1$tT", dataValueLong), format);
                    }
                    else
                    {
                        continue;
                    }
                }
                else if ("ae.EVENT_DURATION".equals(resultFieldList[i]))
                {
                    if (dataValueInt <= 0)
                    {
                        continue;
                    }
                    else
                    {
                        format.setAlignment(Alignment.CENTRE);
                        dataCell = new Label(i, row, String.format("%tT", (long) (dataValueInt - 8 * 60 * 60 * 1000)), format);
                    }
                }
                else
                {
                    dataCell = new Number(i, row, dataValueInt, format);
                }
            }
            else if (dataValue instanceof Date)
            {
                format.setAlignment(Alignment.CENTRE);
                dataCell = new Label(i, row, String.format("%1$tF %1$tT", (Date) dataValue), format);
            }
            else
            {
                format.setAlignment(Alignment.LEFT);
                dataCell = new Label(i, row, dataValue.toString(), format);
            }
            ws.addCell(dataCell);
        }
    }
}
