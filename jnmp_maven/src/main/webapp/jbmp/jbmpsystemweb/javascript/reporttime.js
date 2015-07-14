JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gSysConfigs = {};
var gFunctions = {
	delReportTime : false
}; // 权限

var gReportTimePage = new JetsenWeb.UI.PageBar("ReportTime");
gReportTimePage.onpagechange = function() {
	loadReportTime();
};
gReportTimePage.orderBy = "order by NAME desc";
gReportTimePage.onupdate = function() {
	$('divReportTimePage').innerHTML = this.generatePageControl();
};
var gReportTimeCondition = new JetsenWeb.SqlConditionCollection();
var gReportTimeGridList = new JetsenWeb.UI.GridList();
gReportTimeGridList.ondatasort = function(sortfield, desc) {
	gReportTimePage.setOrderBy(sortfield, desc);
};

var paramFormat = ""; // 报表参数
// 加载=====================================================================================
function searchReportTime1() {
	gReportTimeCondition.SqlConditions = [];
	if ($('txt_TaskKey').value != "") {
		gReportTimeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"TASK_NAME", $('txt_TaskKey').value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}
	if ($('cbo_ReportType').value != "") {
		gReportTimeCondition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("r.ID", $('cbo_ReportType').value,
						JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.Numeric));
	}
	if ($('cboTaskType').value != "") {
		gReportTimeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"TASK_TYPE", $('cboTaskType').value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkSDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		gReportTimeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"START_TIME", $("txtSDate").value + " " + $("txtSTime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("chkEDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		gReportTimeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"START_TIME", $('txtEDate').value + " " + $("txtETime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	gReportTimePage.currentPage = 1;
	loadReportTime();
}

// 初始化有定时任务的报表类型
function reportTypeInit() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_REPORT", "r");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_REPORTTIME", "t",
			"t.REPORT_ID=r.ID", JetsenWeb.TableJoinType.Inner));
	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "ID",
		QueryTable : gQueryTable,
		ResultFields : "DISTINCT ID,NAME"
	});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	// ws.async = false;
	ws.oncallback = function(ret) {
		var cboReportType = $("cbo_ReportType");
		cboReportType.length = 0;
		cboReportType.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var reportTypeInfo = records[i];
				var option = new Option(reportTypeInfo["NAME"],
						reportTypeInfo["ID"]);
				option.title = reportTypeInfo["NAME"];
				cboReportType.options.add(option);
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

// 初始化===================================================================================
function pageInit() {
	//initFunction();
	parent.parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		splitTitle : "divListTitle",
		splitSize : 27
	});
	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReportTimeList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
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

	loadReportTime();
	reportTypeInit();
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	//$("txtSTime").value = startTime.toTimeString();
	// $("divReportTimeList").innerHTML = JetsenWeb.Xml.transformXML(
	// "xslt/reporttime.xslt", "<RecordSet/>");
	// gReportTimeGridList.bind($("divReportTimeList"), $("tabReportTime"));
	// gReportTimePage.setRowCount($("hid_ReportTimeCount").value);
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}