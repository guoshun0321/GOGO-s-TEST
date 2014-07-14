JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gWorkOrderPage = new JetsenWeb.UI.PageBar("WorkOrder");
gWorkOrderPage.onpagechange = function () { loadWorkOrder(); };
gWorkOrderPage.orderBy = "";
gWorkOrderPage.pageSize = 10;
var gWorkOrderCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gWorkOrderPage.setOrderBy(sortfield, desc);
};

var gEventSqlQuery = new JetsenWeb.SqlQuery();
var gEventQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDER", "w");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=w.CHECK_USERID",JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMEVENT","ae","ae.ALARMEVT_ID=w.EVENT_ID",JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gEventSqlQuery, { IsPageResult: 1, KeyId: "ORDER_ID", ResultFields:"w.*,u.USER_NAME", PageInfo: gWorkOrderPage, QueryTable: gEventQueryTable });
var gEventCondition = new JetsenWeb.SqlConditionCollection();

var gLogSqlQuery = new JetsenWeb.SqlQuery();
var gLogQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDER", "w");
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=w.CHECK_USERID",JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMEVENTLOG","ae","ae.ALARMEVT_ID=w.EVENT_ID",JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gLogSqlQuery, { IsPageResult: 1, KeyId: "ORDER_ID", ResultFields:"w.*,u.USER_NAME", PageInfo: gWorkOrderPage, QueryTable: gLogQueryTable });
var gLogCondition = new JetsenWeb.SqlConditionCollection();

var gUnionSqlQuery = new JetsenWeb.SqlQuery();
JetsenWeb.extend(gUnionSqlQuery, { IsPageResult : 1, KeyId : "", PageInfo : gWorkOrderPage, ResultFields : "w.*,u.USER_NAME" });

var gSqlQuery = gUnionSqlQuery;

var curAlarmEventId = 0;// 选择绑定的事件ID
var orderStates = ["初始化", "已分派", "已处理", "已关闭"];

//加载=====================================================================================
function loadWorkOrder() {
	gUnionSqlQuery.OrderString = gWorkOrderPage.orderBy;
	gUnionSqlQuery.Conditions = gWorkOrderCondition;

	gEventSqlQuery.OrderString = "";
	gEventSqlQuery.Conditions = gEventCondition;

	gLogSqlQuery.OrderString = "";
	gLogSqlQuery.Conditions = gLogCondition;

	gEventSqlQuery.UnionQuery = new JetsenWeb.UnionQuery(gLogSqlQuery, JetsenWeb.QueryUnionType.UnionAll);
	gUnionSqlQuery.QueryTable = JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : gEventSqlQuery.toXml(), AliasName : "aeu" });
	gUnionSqlQuery.Conditions.SqlConditions = [];
	gUnionSqlQuery.ResultFields = "*";
	gSqlQuery = gUnionSqlQuery;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $("divWorkOrderList").innerHTML = JetsenWeb.Xml.transformXML("xslt/simpleworkorder.xslt", ret.resultVal);
        gGridList.bind($("divWorkOrderList"), $("tabWorkOrder"));
        gWorkOrderPage.setRowCount($("hid_WorkOrderCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
function searchWorkOrder() {
    gWorkOrderCondition.SqlConditions = [];
    gEventCondition.SqlConditions = [];
    gLogCondition.SqlConditions = [];
    
    gWorkOrderCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", JetsenWeb.Application.userInfo.UserId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", JetsenWeb.Application.userInfo.UserId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("w.CHECK_USERID", JetsenWeb.Application.userInfo.UserId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
   
    loadWorkOrder();
}
//删除=====================================================================================
function deleteWorkOrder(keyId) {
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
//初始化===================================================================================
function pageInit() {
    searchWorkOrder();
    loadUser();

    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType: 1,fixControlIndex: 0, splitBorder: 0, showSplit: false, enableResize: false});

    var frameTop = new JetsenWeb.UI.PageItem("divTop");
    frameTop.size = { width: 0, height: 27 };

    gFrame.addControl(frameTop);
    gFrame.addControl(new JetsenWeb.UI.PageItem("divWorkOrderList"));

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}

function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

// 查看工单处理详情
function viewWorkOrderProcess(orderId, orderState, eventId, orderDesc) {
	var areaElements = JetsenWeb.Form.getElements("divWorkOrderProcessContent");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("divWorkOrderProcessList").innerHTML = "数据加载中...";

    var dialog = new JetsenWeb.UI.Window("view-object-win");
    JetsenWeb.extend(dialog, { cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 800, height: 400 }, title: "工单详情", cancelButtonText: "关闭" });
    dialog.controls = ["divWorkOrderProcessContent"];
    dialog.onsubmit = function () {
        return false;
    };
    $("spanOrderState").innerHTML = orderStates[orderState];
    $("hid_ORDER_ID").value = orderId;
    $("hid_ORDER_DESC").value = orderDesc;
    $("hid_EVENT_ID").value = eventId;
    $("btnUpdateWorkOrder").disabled = orderState == 3;

    dialog.showDialog();
    loadWorkOrderProcess();
}

// 加载工单详情列表
function loadWorkOrderProcess() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_WORKORDERPROCESS", "p");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","uf","uf.ID=p.FROM_USERID",JetsenWeb.TableJoinType.Left));
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","ut","ut.ID=p.TO_USERID",JetsenWeb.TableJoinType.Left));
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("ORDER_ID", $("hid_ORDER_ID").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    JetsenWeb.extend(gSqlQuery, { KeyId: "", QueryTable: gQueryTable, Conditions: conditions, ResultFields: "PROCESS_DESC,uf.USER_NAME AS FROM_USERNAME,ut.USER_NAME AS TO_USERNAME,p.CREATE_TIME,PROCESS_TYPE" });
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
function updateWorkOrder() {
	var areaElements = JetsenWeb.Form.getElements("divWorkOrderProcess");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    var dialog = new JetsenWeb.UI.Window("update-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 230 }, title: "处理工单" });
    dialog.controls = ["divWorkOrderProcess"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var objWorkOrderProcess = {
                PROCESS_DESC: $("txt_PROCESS_DESC").value
              , ORDER_ID: $("hid_ORDER_ID").value
              , FROM_USERID: JetsenWeb.Application.userInfo.UserId
              , TO_USERID: getSelectedValue($("cbo_PROCESS_USER"))
              , PROCESS_TYPE: getSelectedValue($("cboChangeOrderState"))
            };
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	var objWorkOrder = {
                    ORDER_ID: $("hid_ORDER_ID").value
                  , CHECK_USERID: getSelectedValue($("cbo_PROCESS_USER"))
                  , ORDER_STATE: getSelectedValue($("cboChangeOrderState"))
                };
                $("spanOrderState").innerHTML = orderStates[objWorkOrder["ORDER_STATE"]];
                if (objWorkOrder["ORDER_STATE"] == "2" && $("txt_PROCESS_DESC").value != "") {
                	$("hid_ORDER_DESC").value = $("txt_PROCESS_DESC").value;
                	objWorkOrder["ORDER_DESC"] = $("hid_ORDER_DESC").value;
                }
                var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                ws.soapheader = JetsenWeb.Application.authenticationHeader;
                ws.oncallback = function (ret) {
                	JetsenWeb.UI.Windows.close("update-object-win");
                    loadWorkOrder();
                    loadWorkOrderProcess();
                    if (objWorkOrder["ORDER_STATE"] == "3") {
                    	$("btnUpdateWorkOrder").disabled = true;
                    	checkAlarmEvent();
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
function loadUser() {
    var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("UUM_USER", "");
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("STATE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    JetsenWeb.extend(gSqlQuery, { KeyId: "", QueryTable: gQueryTable, Conditions: condition, ResultFields: "ID,USER_NAME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        var cboProcessUser = $("cbo_PROCESS_USER");
        cboProcessUser.length = 0;
        cboProcessUser.options.add(new Option("请选择", ""));
        var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (records) {
            var length = records.length;
            for (var i = 0; i < length; i++) {
                var userInfo = records[i];
                cboProcessUser.options.add(new Option(userInfo["USER_NAME"], userInfo["ID"]));
            }
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 处理报警事件
function checkAlarmEvent() {
    var oAlarmEvent = {};
    oAlarmEvent["EVENT_STATE"] = 3;
    oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
    oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;
    oAlarmEvent["CHECK_DESC"] = $("hid_ORDER_DESC").value;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpCheckAlarmEvent", ["BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), $("hid_EVENT_ID").value]);
}

// 报警事件列表单击事件
function onAlarmEventClick(eventId) {
	curAlarmEventId  = eventId;
}

// 报警事件列表双击事件
function onAlarmEventDoubleClick(eventId) {
	curAlarmEventId  = eventId;
	JetsenWeb.UI.Windows.close("view-object-win");
    $("txt_ALARMEVENT").value = curAlarmEventId;
}