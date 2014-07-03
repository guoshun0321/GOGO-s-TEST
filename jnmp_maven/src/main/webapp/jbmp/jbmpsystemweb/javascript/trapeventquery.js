JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;

var gTrapEventPage = new JetsenWeb.UI.PageBar("TrapEvent");
gTrapEventPage.onpagechange = function() {
	loadTrapEvent();
};
gTrapEventPage.orderBy = "Order By COLL_TIME Desc";
gTrapEventPage.onupdate = function() {
	$('divTrapEventPage').innerHTML = this.generatePageControl();
};
var gTrapEventCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gTrapEventPage.setOrderBy(sortfield, desc);
};
var gEventSqlQuery = new JetsenWeb.SqlQuery();
var gEventQueryTable = JetsenWeb.createQueryTable("BMP_TRAPEVENT", "te");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=te.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=te.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gEventSqlQuery, { IsPageResult : 1, KeyId : "", PageInfo : gTrapEventPage, QueryTable : gEventQueryTable,
	ResultFields : "r.OBJATTR_NAME,o.OBJ_NAME,te.*" });

var alarmStates = {
		"0" : "未确认",
		"1" : "已确认",
		"2" : "已清除",
		"3" : "已处理"
}

//加载=====================================================================================
function loadTrapEvent() {
	gEventSqlQuery.OrderString = gTrapEventPage.orderBy;
	gEventSqlQuery.Conditions = gTrapEventCondition;

	//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for ( var i = 0; i < nodes.length; i++) {
			var collTime = parseFloat(valueOf(nodes[i].selectSingleNode("COLL_TIME"), "text", "0"));
			var trapTime = parseFloat(valueOf(nodes[i].selectSingleNode("TRAP_TIME"), "text", "0"));
			if (collTime != 0) {
				nodes[i].selectSingleNode("COLL_TIME").text = new Date(collTime).toDateTimeString();
			}
			if (trapTime != 0) {
				var day = parseInt(trapTime / (24 * 3600));
				var hour = parseInt((trapTime - day * 24 * 3600) / 3600);
				var minute = parseInt((trapTime - day * 24 * 3600 - hour * 3600) / 60);
				var second = trapTime % 60;
				nodes[i].selectSingleNode("TRAP_TIME").text = day + "天" + hour + "时" + minute + "分" + second + "秒";
			}
		}
		$('divTrapEventList').innerHTML = JetsenWeb.Xml._transformXML("xslt/trapeventquery.xslt", xmlDoc);
		gGridList.bind($('divTrapEventList'), $('tabTrapEvent'));
		gTrapEventPage.setRowCount($('hid_TrapEventCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	//	ws.call("bmpObjQuery", [ gEventSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [ gEventSqlQuery.toXml(), 'o.OBJ_ID', '2' ]);
}
function searchTrapEvent() {
	gEventQueryTable = JetsenWeb.createQueryTable("BMP_TRAPEVENT", "te");
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r", "r.OBJATTR_ID=te.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
	gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o", "o.OBJ_ID=te.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	if ($("cbo_ObjGroup").value != "") {
		gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
		gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
	}

	gEventSqlQuery.QueryTable = gEventQueryTable;

	gTrapEventCondition.SqlConditions = [];

	if ($("cbo_ObjGroup").value != "") {
		gTrapEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("og.GROUP_ID", $("cbo_ObjGroup").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkSDate").checked && $("txtSDate").value != "" && $("txtSTime").value != "") {
		gTrapEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtSDate").value + " " + $("txtSTime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));

	}
	if ($("chkEDate").checked && $("txtEDate").value != "" && $("txtETime").value != "") {
		gTrapEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_TIME", parseDate($("txtEDate").value + " " + $("txtETime").value)
				.getTime(), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));

	}
	if ($("txtObjName").value != "") {
		gTrapEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));

	}

	gTrapEventPage.currentPage = 1;
	loadTrapEvent();
}
//删除=====================================================================================
//function deleteTrapEvent(keyId) {
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
//	ws.soapheader = JetsenWeb.Application.authenticationHeader;
//	ws.oncallback = function(ret) {
//		loadTrapEvent();
//	};
//	ws.onerror = function(ex) {
//		jetsennet.error(ex);
//	};
//	ws.call("bmpObjDelete", [ "BMP_TRAPEVENT", keyId ]);
//}
//初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	// 查询两小时内的报警
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();

	objGroupInit();
	//attributeInit();
	searchTrapEvent();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });

	frameContent.addControl(new JetsenWeb.UI.PageItem("divTrapEventList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 63 } }));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}

// 初始化报警属性
function attributeInit() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "a");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "a.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Inner));
	var condition = new JetsenWeb.SqlConditionCollection();
	if ($("cbo_AttrType").value != "") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", $("cbo_AttrType").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	}
	JetsenWeb.extend(gSqlQuery, { IsPageResult : 0, KeyId : "ATTRIB_ID", QueryTable : gQueryTable, Conditions : condition,
		ResultFields : "DISTINCT a.ATTRIB_ID,ATTRIB_NAME" });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var cboAttribute = $("cbo_Attribute");
		cboAttribute.length = 0;
		cboAttribute.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var attribInfo = records[i];
				cboAttribute.options.add(new Option(attribInfo["ATTRIB_NAME"], attribInfo["ATTRIB_ID"]));
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

// 查看TRAP内容
function viewTrapValue(trapevtId) {
	var areaElements = JetsenWeb.Form.getElements("divViewTrapValue");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAPEVT_ID", trapevtId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TRAPEVT_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPEVENT" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		$('divTrapDetail').innerHTML = JetsenWeb.Xml._transformXML("xslt/trapeventquery_detail.xslt", xmlDoc);
		var detailGridList = new JetsenWeb.UI.GridList();
		detailGridList.bind($('divTrapDetail'), $('tabTrapEventDetail'));
		//		gTrapEventPage.setRowCount($('hid_TrapEventCount').value);		
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpGetTrapDetailInfo", [ trapevtId ]);

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false, size : { width : 500, height : 340 },
		title : "Trap内容", cancelButtonText : "关闭" });
	dialog.controls = [ "divViewTrapValue" ];
	dialog.onsubmit = function() {
		return false;
	};
	dialog.showDialog();
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
	//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
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
	//	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [ gSqlQuery.toXml(), 'GROUP_ID', '1' ]);
}

function viewAlarmEvent(alarmEvtId) {
	var areaElements = JetsenWeb.Form.getElements("divAlarmDesc");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var alarm = eval("(" + ret.resultVal + ")");
		$("txtAlarmId").value = alarm["ALARMEVT_ID"];
		$("txtAlarmState").value = alarmStates[alarm["EVENT_STATE"]];
		$("areaAlarmDesc").value = alarm["EVENT_DESC"];
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("ensureAlarmEvent", [ alarmEvtId ]);

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false, size : { width : 410, height : 200 },
		title : "报警事件", cancelButtonText : "关闭" });
	dialog.controls = [ "divAlarmDesc" ];
	dialog.onsubmit = function() {
		return false;
	};
	dialog.showDialog();
}