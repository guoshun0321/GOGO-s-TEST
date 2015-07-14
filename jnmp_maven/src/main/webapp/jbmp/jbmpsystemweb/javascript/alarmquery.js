JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gFunctions = { check : false }; // 权限：报警处理
var gAlarmEventPage = new JetsenWeb.UI.PageBar("AlarmEvent");
gAlarmEventPage.onpagechange = function() {
	loadAlarmEvent();
};
gAlarmEventPage.orderBy = "Order By ALARMEVT_ID Desc";
gAlarmEventPage.onupdate = function() {
	$('divAlarmEventPage').innerHTML = this.generatePageControl();
};
var gAlarmEventCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gAlarmEventPage.setOrderBy(sortfield, desc);
};
var gEventSqlQuery = new JetsenWeb.SqlQuery();
var gEventQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENT", "ae");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb
		.extend(
				gEventSqlQuery,
				{
					IsPageResult : 1,
					KeyId : "",
					PageInfo : gAlarmEventPage,
					QueryTable : gEventQueryTable,
					ResultFields : "r.OBJATTR_NAME,o.OBJ_NAME,ae.COLL_VALUE,ae.ALARMEVT_ID,ae.EVENT_STATE,ae.ALARM_LEVEL,ae.COLL_TIME,ae.RESUME_TIME,ae.EVENT_DURATION,ae.ALARM_COUNT,ae.EVENT_DESC,ae.CHECK_USER,ae.CHECK_TIME" });
var gEventCondition = new JetsenWeb.SqlConditionCollection();

var gLogSqlQuery = new JetsenWeb.SqlQuery();
var gLogQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENTLOG", "ae");
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb
		.extend(
				gLogSqlQuery,
				{
					IsPageResult : 1,
					KeyId : "",
					PageInfo : gAlarmEventPage,
					QueryTable : gLogQueryTable,
					ResultFields : "r.OBJATTR_NAME,o.OBJ_NAME,ae.COLL_VALUE,ae.ALARMEVT_ID,ae.EVENT_STATE,ae.ALARM_LEVEL,ae.COLL_TIME,ae.RESUME_TIME,ae.EVENT_DURATION,ae.ALARM_COUNT,ae.EVENT_DESC,ae.CHECK_USER,ae.CHECK_TIME" });
var gLogCondition = new JetsenWeb.SqlConditionCollection();

var gUnionSqlQuery = new JetsenWeb.SqlQuery();
JetsenWeb.extend(gUnionSqlQuery, { IsPageResult : 1, KeyId : "", PageInfo : gAlarmEventPage,
	ResultFields : "s.GROUP_NAME,og.GROUP_NAME,r.OBJATTR_NAME,o.OBJ_NAME,ae.*" });

var gSqlQuery = gUnionSqlQuery;

var fields = [ { FIELD_NAME : "ae.ALARMEVT_ID", DISPLAY_NAME : "编号" }, { FIELD_NAME : "o.OBJ_NAME", DISPLAY_NAME : "报警对象" },
		{ FIELD_NAME : "r.OBJATTR_NAME", DISPLAY_NAME : "指标名称" }, { FIELD_NAME : "ae.EVENT_STATE", DISPLAY_NAME : "报警状态" },
		{ FIELD_NAME : "ae.ALARM_LEVEL", DISPLAY_NAME : "报警等级" }, { FIELD_NAME : "ae.COLL_TIME", DISPLAY_NAME : "报警时间" },
		{ FIELD_NAME : "ae.RESUME_TIME", DISPLAY_NAME : "恢复时间" }, { FIELD_NAME : "ae.EVENT_DURATION", DISPLAY_NAME : "持续时间" },
		{ FIELD_NAME : "ae.ALARM_COUNT", DISPLAY_NAME : "报警次数" }, { FIELD_NAME : "ae.EVENT_DESC", DISPLAY_NAME : "报警描述" },
		{ FIELD_NAME : "ae.CHECK_USER", DISPLAY_NAME : "操作人" }, { FIELD_NAME : "ae.CHECK_TIME", DISPLAY_NAME : "操作时间" },
		{ FIELD_NAME : "ae.CHECK_DESC", DISPLAY_NAME : "意见" } ];

// 加载=====================================================================================
function loadAlarmEvent() {
	gUnionSqlQuery.OrderString = gAlarmEventPage.orderBy;
	gUnionSqlQuery.Conditions = gAlarmEventCondition;

	gEventSqlQuery.OrderString = "";
	gEventSqlQuery.Conditions = gEventCondition;

	gLogSqlQuery.OrderString = "";
	gLogSqlQuery.Conditions = gLogCondition;

	var checkType = $("cboChecked").value;

	if (checkType == "") {
		gEventSqlQuery.UnionQuery = new JetsenWeb.UnionQuery(gLogSqlQuery, JetsenWeb.QueryUnionType.UnionAll);
		gUnionSqlQuery.QueryTable = JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : gEventSqlQuery.toXml(), AliasName : "aeu" });
		gUnionSqlQuery.Conditions.SqlConditions = [];
		gUnionSqlQuery.ResultFields = "*";
		gSqlQuery = gUnionSqlQuery;
	} else if (checkType == "2" || checkType == "3") {
		gLogSqlQuery.OrderString = gAlarmEventPage.orderBy;
		gSqlQuery = gLogSqlQuery;
	} else if (checkType == "0" || checkType == "1") {
		gEventSqlQuery.UnionQuery = null;
		gEventSqlQuery.OrderString = gAlarmEventPage.orderBy;
		gSqlQuery = gEventSqlQuery;
	}

	// var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for ( var i = 0; i < nodes.length; i++) {
			var collTime = parseFloat(valueOf(nodes[i].selectSingleNode("COLL_TIME"), "text", "0"));
			var resumeTime = parseFloat(valueOf(nodes[i].selectSingleNode("RESUME_TIME"), "text", "0"));
			var duration = parseFloat(valueOf(nodes[i].selectSingleNode("EVENT_DURATION"), "text", "0"));
			if (collTime != 0) {
				nodes[i].selectSingleNode("COLL_TIME").text = new Date(collTime).toDateTimeString();
			}
			if (resumeTime != 0) {
				nodes[i].selectSingleNode("RESUME_TIME").text = new Date(resumeTime).toDateTimeString();
			}
			if (duration != 0) {
				nodes[i].selectSingleNode("EVENT_DURATION").text = new Date(duration - 8 * 60 * 60 * 1000).toTimeString();
			}
		}
		$('divAlarmEventList').innerHTML = JetsenWeb.Xml._transformXML("xslt/alarmevent.xslt", xmlDoc);
		gGridList.bind($('divAlarmEventList'), $('tabAlarmEvent'));
		gAlarmEventPage.setRowCount($('hid_AlarmEventCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	// ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	// if (checkType == "") {
	// ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'o.OBJ_ID', '2']);
	// } else if (checkType == "2" || checkType == "3"){
	// ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'o.OBJ_ID', '2']);
	// }else if (checkType == "0" || checkType == "1"){
	// ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'o.OBJ_ID', '2']);
	// }
	ws.call("nmpPermissionsQuery", [ gSqlQuery.toXml(), 'o.OBJ_ID', '2' ]);
}
function searchAlarmEvent() {
	gEventQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENT", "ae");
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Inner));
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	if ($("cbo_ObjGroup").value != "") {
		gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
		gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
	}

	gEventSqlQuery.QueryTable = gEventQueryTable;

	gLogQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENTLOG", "ae");
	gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Inner));
	gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
	gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	if ($("cbo_ObjGroup").value != "") {
		gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
		gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
	}

	gLogSqlQuery.QueryTable = gLogQueryTable;

	gAlarmEventCondition.SqlConditions = [];
	gEventCondition.SqlConditions = [];
	gLogCondition.SqlConditions = [];

	if ($("cbo_ObjGroup").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("og.GROUP_ID", $("cbo_ObjGroup").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("og.GROUP_ID", $("cbo_ObjGroup").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("og.GROUP_ID", $("cbo_ObjGroup").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkSDate").checked && $("txtSDate").value != "" && $("txtSTime").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtSDate").value + " " + $("txtSTime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtSDate").value + " " + $("txtSTime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtSDate").value + " " + $("txtSTime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkEDate").checked && $("txtEDate").value != "" && $("txtETime").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtEDate").value + " " + $("txtETime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtEDate").value + " " + $("txtETime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtEDate").value + " " + $("txtETime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
	}
	if ($("txtCheckUser").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("CHECK_USER", $("txtCheckUser").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("CHECK_USER", $("txtCheckUser").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("CHECK_USER", $("txtCheckUser").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($("txtAlarmEvtId").value != "") {
		/*
		 * var type="^[0-9]*[1-9][0-9]*$"; var re = new RegExp(type);
		 * if($("txtAlarmEvtId").value.match(re)==null) { jetsennet.alert(
		 * "请输入大于零的整数！"); return; }
		 */

		if (isNaN($("txtAlarmEvtId").value)) {
			jetsennet.alert("编号应为数字！");
			return;
		}
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", $("txtAlarmEvtId").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", $("txtAlarmEvtId").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", $("txtAlarmEvtId").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($("txtObjName").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($("cboAlarmLevel").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_LEVEL", $("cboAlarmLevel").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_LEVEL", $("cboAlarmLevel").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_LEVEL", $("cboAlarmLevel").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if ($("cboChecked").value != "") { // && $("cboChecked").value != "2"
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("EVENT_STATE", $("cboChecked").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("EVENT_STATE", $("cboChecked").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("EVENT_STATE", $("cboChecked").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	gAlarmEventPage.currentPage = 1;
	loadAlarmEvent();
}
// 删除=====================================================================================
function deleteAlarmEvent(keyId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadAlarmEvent();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ALARMEVENT", keyId ]);
}
// 初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	// 查询两小时内的报警
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();

	$("cboChecked").value = "0";

	objGroupInit();
	// attributeInit();
	searchAlarmEvent();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });

	frameContent.addControl(new JetsenWeb.UI.PageItem("divAlarmEventList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 90 } }));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	gFunctions.check = true;

	pageInitByFunction();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}
// 确认报警事件
function confirmAlarm() {
	var alarmEventIds = JetsenWeb.Form.getCheckedValues("chkAlarmEvent");
	if (alarmEventIds.length == 0) {
		jetsennet.alert("请选择要确认的报警！");
		return;
	}
	jetsennet.confirm("确定确认报警？", function() {
		var oAlarmEvent = {};
		oAlarmEvent["EVENT_STATE"] = 1;
		oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
		oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;

		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadAlarmEvent();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpConfirmAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmEventIds.join(",") ]);
		return true;
	});
}
// 清除报警事件
function removeAlarm() {
	var alarmEventIds = JetsenWeb.Form.getCheckedValues("chkAlarmEvent");
	if (alarmEventIds.length == 0) {
		jetsennet.alert("请选择要清除的报警！");
		return;
	}
	jetsennet.confirm("确定清除报警？", function() {
		var oAlarmEvent = {};
		oAlarmEvent["EVENT_STATE"] = 2;
		oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
		oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;

		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadAlarmEvent();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpRemoveAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmEventIds.join(",") ]);
		return true;
	});
}
// 处理报警事件
function checkAlarm() {
	var alarmEventIds = JetsenWeb.Form.getCheckedValues("chkAlarmEvent");
	if (alarmEventIds.length == 0) {
		jetsennet.alert("请选择要处理的报警！");
		return;
	}
	var areaElements = JetsenWeb.Form.getElements("divCheckAlarmEvent");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 500, height : 160 }, title : "报警处理" });
	dialog.controls = [ "divCheckAlarmEvent" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oAlarmEvent = {};
			oAlarmEvent["EVENT_STATE"] = 3;
			oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
			oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;
			oAlarmEvent["CHECK_DESC"] = $("txt_CHECK_DESC").value;

			var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadAlarmEvent();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpCheckAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmEventIds.join(",") ]);
		}
	};
	var txtCheckDesc = $("txt_CHECK_DESC");
	txtCheckDesc.disabled = false;
	dialog.showDialog();
	txtCheckDesc.focus();
}

// 查看处理意见
function viewCheckDesc(eventId) {
	var areaElements = JetsenWeb.Form.getElements("divCheckAlarmEvent");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID", eventId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ALARMEVT_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARMEVENTLOG" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objCheckDesc = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_CHECK_DESC").value = valueOf(objCheckDesc, "CHECK_DESC", "");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false, size : { width : 500, height : 160 },
		title : "报警处理", cancelButtonText : "关闭" });
	dialog.controls = [ "divCheckAlarmEvent" ];
	dialog.onsubmit = function() {
		return false;
	};
	$("txt_CHECK_DESC").disabled = true;
	dialog.showDialog();
}

// 导出数据
function exportData(fieldsString) {
    var formExportData = $("formExportData");
    formExportData.reset();
    if ($("cbo_ObjGroup").value != "")
    {
		$("og.GROUP_ID").value = $("cbo_ObjGroup").value;
    }
	if ($("chkSDate").checked && $("txtSDate").value != "" && $("txtSTime").value != "")
	{
		$("COLL_TIME_START").value = parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime();
	}
	if ($("chkEDate").checked && $("txtEDate").value != "" && $("txtETime").value != "")
	{
		$("COLL_TIME_END").value = parseDate($("txtEDate").value + " " + $("txtETime").value).getTime();
	}
	if ($("txtCheckUser").value != "")
	{
		$("CHECK_USER").value = $("txtCheckUser").value;
	}
	if ($("txtAlarmEvtId").value != "")
	{
		$("ALARMEVT_ID").value = $("txtAlarmEvtId").value;
	}
	if ($("txtObjName").value != "")
	{
		$("o.OBJ_NAME").value = $("txtObjName").value;
	}
	if ($("cboAlarmLevel").value != "")
	{
		$("ALARM_LEVEL").value = $("cboAlarmLevel").value;
	}
	if ($("cboChecked").value != "")
	{
		$("EVENT_STATE").value = $("cboChecked").value;
	}
    if (fieldsString && fieldsString != "")
    {
    	$("ResultFields").value = decodeURIComponent(fieldsString);
	}
    formExportData.submit();
}

// 弹出选择导出列
function chooseField() {
	if ($("txtAlarmEvtId").value != "") {
		if (isNaN($("txtAlarmEvtId").value)) {
			jetsennet.alert("编号应为数字！");
			return;
		}
	}

	var dialog = new JetsenWeb.UI.Window("choose-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, submitBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 300, height : 420 }, title : "选择导出列" });
	dialog.controls = [ "divFieldList" ];
	dialog.onsubmit = function() {
		exportData(JetsenWeb.Form.getCheckedValues("chkField"));
		JetsenWeb.UI.Windows.close("choose-object-win");
	};
	dialog.showDialog();
	$("divFieldList").innerHTML = JetsenWeb.Xml.transformXML("xslt/choosefield.xslt", getFieldsString());
	var fieldList = new JetsenWeb.UI.GridList();
	fieldList.rowSelection = false;
	fieldList.bind($("divFieldList"), $("tabField"));
}

// 获取显示列的XML
function getFieldsString() {
	var fieldsString = "<RecordSet>";
	for ( var i = 0; i < fields.length; i++) {
		fieldsString += JetsenWeb.Xml.serializer(fields[i], "Record");
	}
	fieldsString += "</RecordSet>";
	return fieldsString;
}

// 根据权限初始化界面
function pageInitByFunction() {
	$("btnCheck").disabled = !gFunctions.check;
}

// 根据报警状态显示相应的按钮
function btnInitByEventState() {
	var cboType = $("cboChecked").value;
	if (cboType == "" || cboType == "0") {
		$("btnConfirm").disabled = false;
		$("btnRemove").disabled = false;
		$("btnCheck").disabled = false;
		$("btnConfirm").className = "button4";
		$("btnRemove").className = "button4";
		$("btnCheck").className = "button4";
	} else if (cboType == "1") {
		$("btnConfirm").disabled = true;
		$("btnRemove").disabled = false;
		$("btnCheck").disabled = false;
		$("btnConfirm").className = "disablebutton";
		$("btnRemove").className = "button4";
		$("btnCheck").className = "button4";
	} else if (cboType == "2" || cboType == "3") {
		$("btnConfirm").disabled = true;
		$("btnRemove").disabled = true;
		$("btnCheck").disabled = true;
		$("btnConfirm").className = "disablebutton";
		$("btnRemove").className = "disablebutton";
		$("btnCheck").className = "disablebutton";
	}
}

// 初始化对象组
function objGroupInit() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "og");

	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", "1,6", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In,
			JetsenWeb.SqlParamType.Numeric));

	JetsenWeb.extend(gSqlQuery, { IsPageResult : 0, KeyId : "GROUP_ID", QueryTable : gQueryTable, Conditions : condition,
		ResultFields : "DISTINCT GROUP_ID,GROUP_NAME" });
	// var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var cboObjGroup = $("cbo_ObjGroup");
		cboObjGroup.length = 0;
		cboObjGroup.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var objGroupInfo = records[i];
				cboObjGroup.options.add(new Option(objGroupInfo["GROUP_NAME"], objGroupInfo["GROUP_ID"]));
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	// ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [ gSqlQuery.toXml(), 'GROUP_ID', '1' ]);
}