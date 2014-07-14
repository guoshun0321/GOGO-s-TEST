JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
	"js_validate", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;

var paramFormat; // 报表参数
var reportType = "inspectstatistics"; // 报表类型

// 初始化===================================================================================
function pageInit() 
{
	// 查询两小时内的巡检报告
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		enableResize : false,
		splitTitle : "divListTitle",
		splitSize : 27
	});

	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divInspectReportList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 0
		}
	}));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), {
		size : {
			width : 0,
			height : 60
		}
	}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	loadSystem($("cbo_ObjGroup"),false);
	objectInit();
}

function windowResized() 
{
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}
function objectInit() 
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "b.GROUP_ID = c.GROUP_ID", JetsenWeb.TableJoinType.Left));

	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_TYPE",
			1, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	if ($("cbo_ObjGroup").value != "")
	{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_ID",
			$("cbo_ObjGroup").value, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}

	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "a.OBJ_ID",
		QueryTable : gQueryTable,
		Conditions : condition,
		ResultFields : "DISTINCT a.OBJ_ID,a.OBJ_NAME"
	});

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) 
	{
		var cboObject = $("cbo_Object");
		cboObject.length = 0;
		cboObject.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var objectInfo = records[i];
				var option = new Option(objectInfo["OBJ_NAME"],objectInfo["OBJ_ID"])
				option.title = objectInfo["OBJ_NAME"];
				cboObject.options.add(option);
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function getAllObj(){
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "b.GROUP_ID = c.GROUP_ID", JetsenWeb.TableJoinType.Left));

	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_TYPE", 1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	var groupOptions = $("cbo_ObjGroup").options;
	var groupIds = "";
	for(var i = 0; i < groupOptions.length; i++){
		if(groupOptions[i].value != ""){
			groupIds += groupOptions[i].value + ",";
		}
	}
	if(groupIds.length == 0){
		return "";
	}
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_ID", groupIds.substring(0, groupIds.length-1), JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));

	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "a.OBJ_ID",
		QueryTable : gQueryTable,
		Conditions : condition,
		ResultFields : "DISTINCT a.OBJ_ID,a.OBJ_NAME"
	});

	var objIds="";
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) 
	{
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				objIds += records[i].OBJ_ID + ",";
			}
		}
	};
	ws.onerror = function(ex) {jetsennet.error(ex);};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	if(objIds == ""){
		return "";
	}
	return objIds.substring(0, objIds.length-1);
}
// 导出数据
function exportData() {
	if ($("cbo_TimeInterval").value == "") {
		jetsennet.alert("请选择时间间隔！");
		return;
	}
	//chrome无法禁止输入法，故加此判断
	if (!/^[\d]+$/.test($("cbo_TimeInterval").value)) {
		jetsennet.alert("时间间隔只能是整数！");
		return;
	}
	if(parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime() > parseDate($("txtEDate").value + " " + $("txtETime").value).getTime()){
		jetsennet.alert("结束时间必须大于起始时间！");
		return;
	}
	
	url = "../../../nmpreport/inspectionreport.jsp?flag=1";
	if($("cbo_ObjGroup").value != ""){
		url += "&groupIds=" + $("cbo_ObjGroup").value;
	}else{
		var groupOptions = $("cbo_ObjGroup").options;
		var groupIds = "";
		for(var i = 0; i < groupOptions.length; i++){
			if(groupOptions[i].value != ""){
				groupIds += groupOptions[i].value + ",";
			}
		}
		if(groupIds.length == 0){
			jetsennet.alert("没有对象组！");
			return;
		}
		url += "&groupIds=" + groupIds.substring(0, groupIds.length-1);
	}
	if($("cbo_Object").value != ""){
		url += "&objIds=" + $("cbo_Object").value;
	}else{
		if($("cbo_ObjGroup").value != ""){
			var objOptions = $("cbo_Object").options;
			var objIds = "";
			for(var i = 0; i < objOptions.length; i++){
				if(objOptions[i].value != ""){
					objIds += objOptions[i].value + ",";
				}
			}
			if(objIds.length == 0){
				jetsennet.alert("没有对象！");
				return;
			}
			url += "&objIds=" + objIds.substring(0, objIds.length-1);
		}else{
			var objIds = getAllObj();
			if(objIds == ""){
				jetsennet.alert("没有对象！");
				return;
			}
			url += "&objIds=" + objIds;
		}
	}
	url += "&timeInterval=" + $("cbo_TimeInterval").value * 60 * 1000;
	url += "&startTime="
			+ parseDate($("txtSDate").value + " " + $("txtSTime").value)
					.getTime();
	url += "&endTime="
			+ parseDate($("txtEDate").value + " " + $("txtETime").value)
					.getTime();
	$("frmTableInfo").src = url;
}