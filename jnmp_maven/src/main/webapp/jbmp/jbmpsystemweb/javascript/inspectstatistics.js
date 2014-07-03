JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
	"js_validate", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;

var gReportTimePage = new JetsenWeb.UI.PageBar("ReportTime");
gReportTimePage.onpagechange = function() {
	loadReportTime($("hid_REPORT_ID").value);
};
gReportTimePage.orderBy = "";
gReportTimePage.onupdate = function() {
	$("divReportTimePage").innerHTML = this.generatePageControl();
};
var gReportTimeCondition = new JetsenWeb.SqlConditionCollection();
var gReportTimeGridList = new JetsenWeb.UI.GridList();
gReportTimeGridList.ondatasort = function(sortfield, desc) {
	gReportTimePage.setOrderBy(sortfield, desc);
};

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
	//objGroupInit();
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
/*
// 初始化对象组
function objGroupInit() 
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "og");
	
	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE",
			"1,6", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));

	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "GROUP_ID",
		QueryTable : gQueryTable,
		Conditions : condition,
		ResultFields : "DISTINCT GROUP_ID,GROUP_NAME"
	});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
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
				cboObjGroup.options.add(new Option(objGroupInfo["GROUP_NAME"],
						objGroupInfo["GROUP_ID"]));
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
*/
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
				cboObject.options.add(new Option(objectInfo["OBJ_NAME"],
						objectInfo["OBJ_ID"]));
			}
		}

		//attributeInit($("cbo_Object").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

// 导出数据
function exportData() { 
	if ($("cbo_Object").value == "") {
		jetsennet.alert("请选择对象！");
		return;
	}
	if ($("chkDate").checked == false) {
		jetsennet.alert("请选择查询时间！");
		return;
	}
	url = getReportWebPath()+"/inspectstatistics.jsp?flag=1";
	url += "&OBJ_ID=" + $("cbo_Object").value;
	url += "&GROUP_ID=" + $("cbo_ObjGroup").value;
	url += "&downCOLL_TIME="
			+ parseDate($("txtSDate").value + " " + $("txtSTime").value)
					.getTime();
	url += "&upCOLL_TIME="
			+ parseDate($("txtEDate").value + " " + $("txtETime").value)
					.getTime();

	$("frmTableInfo").src = url;
}

// 初始化报表参数
function initParamFormat() {
	paramFormat = JetsenWeb.Xml.serializer( {
		OBJ_ID : $("cbo_Object").value
	}, "Format");
}

/**
 * 设置定制报表参数
 * @param taskId 用于判断是否为编辑状态
 * @return
 */
function setCustomReportParam(taskId) {
	var param = "";
	if ($("chk_paramOrign").checked) {
		if (getSelectedValue($("cbo_ObjGroup")) != "") {
			param += "所属系统：" + getSelectedText($("cbo_ObjGroup")) + "\n";
		}
		if (getSelectedValue($("cbo_Object")) != "") {
			
			param += "对象名称：" + getSelectedText($("cbo_Object")) + "\n";
		}
	}else if(taskId !="")
	{
		/*
		 * 编辑状态下：从数据库中获取定制报表参数（BMP_REPORTTIME表FIELD_1）
		 * dbCustomReportParam 参见javascript/report_comm.js
		 */
		param = dbCustomReportParam;
	}
	$("txt_customReportParam").value = param;
}