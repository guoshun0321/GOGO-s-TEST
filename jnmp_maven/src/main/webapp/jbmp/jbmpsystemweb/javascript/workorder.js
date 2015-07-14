JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gWorkOrderPage = new JetsenWeb.UI.PageBar("WorkOrder");
gWorkOrderPage.onpagechange = function () { loadWorkOrder(); };
gWorkOrderPage.orderBy = "ORDER BY CREATE_TIME,ORDER_ID DESC";
gWorkOrderPage.onupdate = function () {
    $("divWorkOrderPage").innerHTML = this.generatePageControl();
};
var gWorkOrderCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gWorkOrderPage.setOrderBy(sortfield, desc);
};

var gEventSqlQuery = new JetsenWeb.SqlQuery();
var gEventQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDER", "w");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=w.CHECK_USERID",JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMEVENT","ae","ae.ALARMEVT_ID=w.EVENT_ID",JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB","r","r.OBJATTR_ID=ae.OBJATTR_ID",JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","o.OBJ_ID=r.OBJ_ID",JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gEventSqlQuery, { IsPageResult: 1, KeyId: "ORDER_ID", ResultFields:"w.*,OBJ_NAME,OBJATTR_NAME,ALARM_LEVEL,u.USER_NAME", PageInfo: gWorkOrderPage, QueryTable: gEventQueryTable });
var gEventCondition = new JetsenWeb.SqlConditionCollection();

var gLogSqlQuery = new JetsenWeb.SqlQuery();
var gLogQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDER", "w");
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=w.CHECK_USERID",JetsenWeb.TableJoinType.Inner));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMEVENTLOG","ae","ae.ALARMEVT_ID=w.EVENT_ID",JetsenWeb.TableJoinType.Inner));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB","r","r.OBJATTR_ID=ae.OBJATTR_ID",JetsenWeb.TableJoinType.Inner));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","o.OBJ_ID=r.OBJ_ID",JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gLogSqlQuery, { IsPageResult: 1, KeyId: "ORDER_ID", ResultFields:"w.*,OBJ_NAME,OBJATTR_NAME,ALARM_LEVEL,u.USER_NAME", PageInfo: gWorkOrderPage, QueryTable: gLogQueryTable });
var gLogCondition = new JetsenWeb.SqlConditionCollection();

var gUnionSqlQuery = new JetsenWeb.SqlQuery();
JetsenWeb.extend(gUnionSqlQuery, { IsPageResult : 1, KeyId : "", PageInfo : gWorkOrderPage, ResultFields : "w.*,OBJ_NAME,OBJATTR_NAME,ALARM_LEVEL,u.USER_NAME" });

var gSqlQuery = gUnionSqlQuery;

var gAlarmEventPage = new JetsenWeb.UI.PageBar("AlarmEvent");
gAlarmEventPage.onpagechange = function () { loadAlarmEvent(); };
gAlarmEventPage.orderBy = "ORDER BY COLL_TIME DESC";
gAlarmEventPage.onupdate = function () {
    $("divAlarmEventPage").innerHTML = this.generatePageControl();
};
var gAlarmEventGridList = new JetsenWeb.UI.GridList();
gAlarmEventGridList.ondatasort = function (sortfield, desc) {
	gAlarmEventPage.setOrderBy(sortfield, desc);
};

var curAlarmEventId = 0;// 选择绑定的事件ID
var orderStates = ["初始化", "已分派", "已处理", "已关闭"];

//加载=====================================================================================
function loadWorkOrder()
{
	gUnionSqlQuery.OrderString = gWorkOrderPage.orderBy;
	gUnionSqlQuery.Conditions = gWorkOrderCondition;

	gEventSqlQuery.OrderString = "";
	gEventSqlQuery.Conditions = gEventCondition;

	gLogSqlQuery.OrderString = "";
	gLogSqlQuery.Conditions = gLogCondition;

	var orderState = $("cboOrderState").value;

//	if (orderState == "")
//	{
		gEventSqlQuery.UnionQuery = new JetsenWeb.UnionQuery(gLogSqlQuery, JetsenWeb.QueryUnionType.UnionAll);
		gUnionSqlQuery.QueryTable = JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : gEventSqlQuery.toXml(), AliasName : "aeu" });
		gUnionSqlQuery.Conditions.SqlConditions = [];
		gUnionSqlQuery.ResultFields = "*";
		gSqlQuery = gUnionSqlQuery;
//	}
//	else if (orderState == "0" || orderState == "1" || orderState == "2")
//	{
//		gEventSqlQuery.UnionQuery = null;
//		gEventSqlQuery.OrderString = gWorkOrderPage.orderBy;
//		gSqlQuery = gEventSqlQuery;
//	}
//	else if (orderState == "3")
//	{
//		gLogSqlQuery.OrderString = gWorkOrderPage.orderBy;
//		gSqlQuery = gLogSqlQuery;
//	}
	gSqlQuery.OrderString = gWorkOrderPage.orderBy;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $("divWorkOrderList").innerHTML = JetsenWeb.Xml.transformXML("xslt/workorder.xslt", ret.resultVal);
        gGridList.bind($("divWorkOrderList"), $("tabWorkOrder"));
        gWorkOrderPage.setRowCount($("hid_WorkOrderCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
function searchWorkOrder()
{
    gWorkOrderCondition.SqlConditions = [];
    gEventCondition.SqlConditions = [];
    gLogCondition.SqlConditions = [];
    
    if (getSelectedValue($("cboUser")) != "")
    {
    	gWorkOrderCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", getSelectedValue($("cboUser")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", getSelectedValue($("cboUser")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", getSelectedValue($("cboUser")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    if (getSelectedValue($("cboOrderState")) != "")
    {
        gWorkOrderCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ORDER_STATE", getSelectedValue($("cboOrderState")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ORDER_STATE", getSelectedValue($("cboOrderState")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ORDER_STATE", getSelectedValue($("cboOrderState")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    loadWorkOrder();
}
//删除=====================================================================================
function deleteWorkOrder(keyId)
{
	jetsennet.confirm("确定删除？", function () {
	    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function (ret) {
	        loadWorkOrder();
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex); };
	    ws.call("bmpObjDelete", ["BMP_WORKORDER", keyId]);
	    return true;
    });
}
//新增=====================================================================================
function newWorkOrder()
{
    var areaElements = JetsenWeb.Form.getElements("divWorkOrder");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    showRemindWordCount($("txt_ORDER_DESC").value,$('remindWord'),"60");
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 245 }, title: "新建工单" });
    dialog.controls = ["divWorkOrder"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true))
        {
        	var objWorkOrder = {
                ORDER_DESC: $("txt_ORDER_DESC").value
              , EVENT_ID: $("txt_ALARMEVENT").value
              , CHECK_USERID: $("cbo_USER").value
              , ORDER_STATE: 0
            };
        	if(parseInt(getBytesCount($("txt_ORDER_DESC").value))>120){
            	jetsennet.alert("工单描述不能超过60个文字！");
            	return;
            }
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("new-object-win");
                loadWorkOrder();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjInsert", ["BMP_WORKORDER", JetsenWeb.Xml.serializer(objWorkOrder, "BMP_WORKORDER")]);
        }
    };
    dialog.showDialog();
}
//初始化===================================================================================
function pageInit()
{
    searchWorkOrder();
    loadUser();

    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType: 1,fixControlIndex: 0, splitBorder: 0, showSplit: true, enableResize: false, splitTitle : "divListTitle", splitSize : 27});

    var frameTop = new JetsenWeb.UI.PageItem("divTop");
    frameTop.size = { width: 0, height: 30 };
    var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameContent.addControl(new JetsenWeb.UI.PageItem("divWorkOrderList"));
    frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));

    gFrame.addControl(frameTop);
    gFrame.addControl(frameContent);

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
    parent.document.getElementById("spanWindowName").innerHTML = document.title;
}

function windowResized()
{
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

// 查看工单处理详情
function viewWorkOrderProcess(orderId, orderState, eventId, orderDesc)
{
	var areaElements = JetsenWeb.Form.getElements("divWorkOrderProcessContent");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("divWorkOrderProcessList").innerHTML = "数据加载中...";

    var dialog = new JetsenWeb.UI.Window("view-object-win");
    JetsenWeb.extend(dialog, { cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 800, height: 400 }, title: "工单详情", cancelButtonText: "关闭" });
    dialog.controls = ["divWorkOrderProcessContent"];
    dialog.onsubmit = function () {
        return false;
    };
    $("spanOrderState").innerHTML = orderStates[orderState];
    $("hid_ORDER_ID").value = orderId;
    $("hid_ORDER_DESC").value = orderDesc;
    $("hid_EVENT_ID").value = eventId;
    $("btnUpdateWorkOrder").disabled = orderState == 3;

    loadWorkOrderProcess();
    dialog.showDialog();
}

// 加载工单详情列表
function loadWorkOrderProcess()
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDERPROCESS", "p");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","uf","uf.ID=p.FROM_USERID",JetsenWeb.TableJoinType.Left));
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","ut","ut.ID=p.TO_USERID",JetsenWeb.TableJoinType.Left));
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("ORDER_ID", $("hid_ORDER_ID").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    JetsenWeb.extend(gSqlQuery, { KeyId: "", QueryTable: gQueryTable, Conditions: conditions, ResultFields: "PROCESS_DESC,uf.USER_NAME AS FROM_USERNAME,ut.USER_NAME AS TO_USERNAME,p.CREATE_TIME,PROCESS_TYPE", OrderString: "ORDER BY p.CREATE_TIME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $("divWorkOrderProcessList").innerHTML = JetsenWeb.Xml.transformXML("xslt/workorderprocess.xslt", ret.resultVal);
        new JetsenWeb.UI.GridList().bind($("divWorkOrderProcessList"), $("tabWorkOrderProcess"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 修改工单状态
function updateWorkOrder()
{
	var areaElements = JetsenWeb.Form.getElements("divWorkOrderProcess");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    showRemindWordCount($("txt_PROCESS_DESC").value,$('remindWord2'),"60");
    var dialog = new JetsenWeb.UI.Window("update-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 245 }, title: "处理工单" });
    dialog.controls = ["divWorkOrderProcess"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true))
        {
        	var objWorkOrderProcess = {
                PROCESS_DESC: $("txt_PROCESS_DESC").value
              , ORDER_ID: $("hid_ORDER_ID").value
              , FROM_USERID: JetsenWeb.Application.userInfo.UserId
              , TO_USERID: getSelectedValue($("cbo_PROCESS_USER"))
              , PROCESS_TYPE: getSelectedValue($("cboChangeOrderState"))
            };
        	if(parseInt(getBytesCount($("txt_PROCESS_DESC").value))>120){
            	jetsennet.alert("处理描述不能超过60个文字！");
            	return;
            }
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	var objWorkOrder = {
                    ORDER_ID: $("hid_ORDER_ID").value
                  , CHECK_USERID: getSelectedValue($("cbo_PROCESS_USER"))
                  , ORDER_STATE: getSelectedValue($("cboChangeOrderState"))
                };
                $("spanOrderState").innerHTML = orderStates[objWorkOrder["ORDER_STATE"]];
                if (objWorkOrder["ORDER_STATE"] == "2" && $("txt_PROCESS_DESC").value != "")
                {
                	$("hid_ORDER_DESC").value = $("txt_PROCESS_DESC").value;
                	objWorkOrder["ORDER_DESC"] = $("hid_ORDER_DESC").value;
                }
                var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                ws.soapheader = JetsenWeb.Application.authenticationHeader;
                ws.oncallback = function (ret) {
                	JetsenWeb.UI.Windows.close("update-object-win");
                    loadWorkOrder();
                    loadWorkOrderProcess();
                    // 若在关闭问题单
                    if (objWorkOrder["ORDER_STATE"] == "3")
                    {
                    	$("btnUpdateWorkOrder").disabled = true;
                    	checkAlarmEvent();
                    	addAlarmEventKnowledge($("hid_EVENT_ID").value);
                    }
                };
                ws.onerror = function (ex) { jetsennet.error(ex); };
                ws.call("bmpObjUpdate", ["BMP_WORKORDER", JetsenWeb.Xml.serializer(objWorkOrder, "BMP_WORKORDER")]);
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjInsert", ["BMP_WORKORDERPROCESS", JetsenWeb.Xml.serializer(objWorkOrderProcess, "BMP_WORKORDERPROCESS")]);
        }
    };
    dialog.showDialog();
}

// 加载用户列表
function loadUser()
{
    var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("UUM_USER", "");
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("STATE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    JetsenWeb.extend(gSqlQuery, { KeyId: "", QueryTable: gQueryTable, Conditions: condition, ResultFields: "ID,USER_NAME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var cboSearchUser = $("cboUser");
        var cboUser = $("cbo_USER");
        var cboProcessUser = $("cbo_PROCESS_USER");
        cboSearchUser.length = 0;
        cboUser.length = 0;
        cboProcessUser.length = 0;
        cboSearchUser.options.add(new Option("请选择", ""));
        cboProcessUser.options.add(new Option("请选择", ""));
        var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (records)
        {
            var length = records.length;
            for (var i = 0; i < length; i++)
            {
                var userInfo = records[i];
                cboSearchUser.options.add(new Option(userInfo["USER_NAME"], userInfo["ID"]));
                cboUser.options.add(new Option(userInfo["USER_NAME"], userInfo["ID"]));
                cboProcessUser.options.add(new Option(userInfo["USER_NAME"], userInfo["ID"]));
            }
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 选择报警事件
function chooseAlarmEvent()
{
	var areaElements = JetsenWeb.Form.getElements("divAlarmEventContent");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("divAlarmEventList").innerHTML = "数据加载中...";

    var dialog = new JetsenWeb.UI.Window("view-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 600, height: 400 }, title: "选择报警事件" });
    dialog.controls = ["divAlarmEventContent"];
    dialog.onsubmit = function () {
    	JetsenWeb.UI.Windows.close("view-object-win");
        $("txt_ALARMEVENT").value = curAlarmEventId;
    };
    loadAlarmEvent();
	dialog.showDialog();
}

// 加载报警事件
function loadAlarmEvent()
{
	var eventIds = getWorkOrderEvent();
	
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENT", "ae");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB","r","r.OBJATTR_ID=ae.OBJATTR_ID",JetsenWeb.TableJoinType.Inner));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","o.OBJ_ID=r.OBJ_ID",JetsenWeb.TableJoinType.Inner));
    JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, PageInfo: gAlarmEventPage, KeyId: "ALARMEVT_ID", QueryTable: gQueryTable, ResultFields: "ALARMEVT_ID,OBJ_NAME,OBJATTR_NAME,ALARM_LEVEL,COLL_TIME", OrderString: gAlarmEventPage.orderBy });
    
    if (eventIds.length > 0)
    {
    	var condition = new JetsenWeb.SqlConditionCollection();
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", eventIds.join(","), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));
        gSqlQuery.Conditions = condition;
    }
    
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for ( var i = 0; i < nodes.length; i++)
		{
			var collTime = parseFloat(valueOf(nodes[i].selectSingleNode("COLL_TIME"), "text", "0"));
			if (collTime != 0)
			{
				nodes[i].selectSingleNode("COLL_TIME").text = new Date(collTime).toDateTimeString();
			}
		}
        $("divAlarmEventList").innerHTML = JetsenWeb.Xml._transformXML("xslt/simplealarmevent.xslt", xmlDoc);
        gAlarmEventGridList.bind($("divAlarmEventList"), $("tabAlarmEvent"));
        gAlarmEventPage.setRowCount($("hid_AlarmEventCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 处理报警事件
function checkAlarmEvent()
{
    var oAlarmEvent = {};
    oAlarmEvent["EVENT_STATE"] = 3;
    oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
    oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;
    oAlarmEvent["CHECK_DESC"] = $("hid_ORDER_DESC").value;

    var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.async = false;
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpCheckAlarmEvent", ["BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), $("hid_EVENT_ID").value]);
}

// 添加报警相关的知识库文章
function addAlarmEventKnowledge(eventId)
{
	var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", eventId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROCESS_TYPE", 2, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENTLOG", "ae");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB","r","r.OBJATTR_ID=ae.OBJATTR_ID",JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","o.OBJ_ID=r.OBJ_ID",JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_WORKORDER","w","w.EVENT_ID=ae.ALARMEVT_ID",JetsenWeb.TableJoinType.Inner));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_WORKORDERPROCESS","wp","wp.ORDER_ID=w.ORDER_ID",JetsenWeb.TableJoinType.Inner));
    JetsenWeb.extend(gSqlQuery, { KeyId: "ALARMEVT_ID", QueryTable: gQueryTable, Conditions: condition, ResultFields: "OBJ_NAME,OBJATTR_NAME,ALARM_LEVEL,COLL_TIME,FROM_USERID,PROCESS_DESC", OrderString: "ORDER BY wp.CREATE_TIME DESC" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var alarmLevels = {
    		10: "警告报警"
    		, 20: "一般报警"
    		, 30: "重要报警"
    		, 40: "严重报警"
    	};
    	var objAlarmEventList = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if (!objAlarmEventList)
    	{
    		return;
    	}
    	var objAlarmEvent = objAlarmEventList[0];
    	var objName = valueOf(objAlarmEvent, "OBJ_NAME", "");
    	var objAttrName = valueOf(objAlarmEvent, "OBJATTR_NAME", "");
    	var alarmLevel = valueOf(objAlarmEvent, "ALARM_LEVEL", "");
    	var collTime = valueOf(objAlarmEvent, "COLL_TIME", "");
    	var processDesc = valueOf(objAlarmEvent, "PROCESS_DESC", "");
    	var title = "报警处理相关文章";
    	var summary = "";
    	if (objName != "")
    	{
    		summary += "对象“" + objName + "”";
    	}
    	if (collTime != "")
    	{
    		summary += "于" + new Date(parseFloat(collTime)).toDateTimeString() + "发生的";
    	}
    	if (objAttrName != "")
    	{
    		summary += "类型为“" + objAttrName + "”"
    	}
    	if (alarmLevel != "")
    	{
    		summary += alarmLevels[alarmLevel];
    	}
    	if (summary == "")
    	{
    		summary += "报警";
    	}
    	summary += "处理相关文章";
    	addKnowledge(title, summary, processDesc, valueOf(objAlarmEvent, "FROM_USERID", "1"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 添加知识库文章
function addKnowledge(title, summary, content, userId)
{
	var date = new Date();
	var objKnowledge = {
        KNOWLEDGE_TITLE: title
      , KNOWLEDGE_SUMMARY: summary
      , KNOWLEDGE_CONTENT: "@" + content + "@"
      , CREATE_USERID: userId
      , CREATE_TIME: date.toDateTimeString()
      , UPDATE_TIME: date.toDateTimeString()
      , CLICK_COUNT: 0
      , COMMENT_COUNT: 0
    };
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjInsert", ["BMP_KNOWLEDGE", JetsenWeb.Xml.serializer(objKnowledge, "BMP_KNOWLEDGE")]);
}

function processTypeChange()
{
	var processType = getSelectedValue($("cboChangeOrderState"));
	$("spanDescNotEmpty").style.display = processType == "2" ? "inline" : "none";
	$("txt_PROCESS_DESC").setAttribute("validatetype", processType == "2" ? "NotEmpty" : "");
}

// 报警事件列表单击事件
function onAlarmEventClick(eventId)
{
	curAlarmEventId  = eventId;
}

// 报警事件列表双击事件
function onAlarmEventDoubleClick(eventId)
{
	curAlarmEventId  = eventId;
	JetsenWeb.UI.Windows.close("view-object-win");
    $("txt_ALARMEVENT").value = curAlarmEventId;
}

// 获取已生成工单的报警列表
function getWorkOrderEvent()
{
	var eventIds = [];
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDER", "w");
    JetsenWeb.extend(gSqlQuery, { IsPageResult: 0, PageInfo: null, KeyId: "ORDER_ID", QueryTable: gQueryTable, ResultFields: "EVENT_ID" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var eventIdObjs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if (eventIdObjs)
    	{
    		for (var i = 0; i < eventIdObjs.length; i++)
    		{
    			eventIds.push(eventIdObjs[i]["EVENT_ID"]);
    		}
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
    return eventIds;
}