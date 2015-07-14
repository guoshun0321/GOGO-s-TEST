JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gSysConfigs = {};
var gFunctions = {
	delReportFile : false
}; // 权限：删除报表文件

var gReportFilePage = new JetsenWeb.UI.PageBar("ReportFile");
gReportFilePage.onpagechange = function() {
	loadReportFile();
};
gReportFilePage.orderBy = "order by f.CREATE_TIME desc";
gReportFilePage.onupdate = function() {
	$('divReportFilePage').innerHTML = this.generatePageControl();
};
var gReportFileCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gReportFilePage.setOrderBy(sortfield, desc);
};
var gReportFileSqlQuery = new JetsenWeb.SqlQuery();
var gReportFileQueryTable = JetsenWeb.createQueryTable("BMP_REPORTFILE", "f");
gReportFileQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_REPORTTIME",
		"t", "t.TASK_ID=f.TASK_ID", JetsenWeb.TableJoinType.Inner));
gReportFileQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_REPORT", "r",
		"r.ID=t.REPORT_ID", JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gReportFileSqlQuery, {
	IsPageResult : 1,
	KeyId : "",
	PageInfo : gReportFilePage,
	QueryTable : gReportFileQueryTable,
	ResultFields : "f.*,t.TASK_NAME,r.NAME"
});

// 加载=====================================================================================
function loadReportFile() {
	gReportFileSqlQuery.OrderString = gReportFilePage.orderBy;
	gReportFileSqlQuery.Conditions = gReportFileCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for ( var i = 0; i < nodes.length; i++) {
			var filePath = valueOf(nodes[i].selectSingleNode("FILE_PATH"),
					"text", "");
			var fileName = valueOf(nodes[i].selectSingleNode("FILE_NAME"),
					"text", "");
			var path = filePath + fileName;
			path = path.replaceAll("\\\\", "\/");
			path = path.replaceAll("c:/output", "http://localhost/viewreport");
			var pathNode = xmlDoc.createElement("PATH");
			pathNode.appendChild(xmlDoc.createTextNode(path));
			nodes[i].appendChild(pathNode);
		}
		$('divReportFileList').innerHTML = JetsenWeb.Xml._transformXML(
				"xslt/reportfile.xslt", xmlDoc);
		gGridList.bind($('divReportFileList'), $('tabReportFile'));
		gReportFilePage.setRowCount($('hid_ReportFileCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gReportFileSqlQuery.toXml() ]);
}
function searchReportFile() {
	gReportFileCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gReportFileCondition.SqlConditions
				.push(JetsenWeb.SqlCondition.create("f.FILE_NAME",
						$('txt_Key').value, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.ILike,
						JetsenWeb.SqlParamType.String));
	}
	if ($('txt_TaskKey').value != "") {
		gReportFileCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"t.TASK_NAME", $('txt_TaskKey').value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}
	if ($('cbo_ReportType').value != "") {
		gReportFileCondition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("r.ID", $('cbo_ReportType').value,
						JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkSDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		gReportFileCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"f.CREATE_TIME", $("txtSDate").value + " "
						+ $("txtSTime").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("chkEDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		gReportFileCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"f.CREATE_TIME", $('txtEDate').value + " "
						+ $("txtETime").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	gReportFilePage.currentPage = 1;
	loadReportFile();
}

//单选删除*2012.09.02*===============================================
function deleteFile(keyId){
	jetsennet.confirm("确定删除？", function(){
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadReportFile();
		};
		ws.onerror = function(ex) { jetsennet.error(ex); };
		ws.call("bmpObjDelete", [ "BMP_REPORTFILE",keyId ]);
		return true;
	});	
}

//单选删除*2012.09.02*===============================================
function deleteFiles(){
	var checkStrs = document.getElementsByName("chkReportFile");
	var objArr = checkStrs.length;
	var checkIds = "";
	for(var i=0; i<checkStrs.length; i++){
		if(checkStrs[i].checked == true){
			checkIds += checkStrs[i].value + ",";
		}
	}
	if(checkIds == ""){
		jetsennet.alert("请选择要删除的项！");
		return;
	}
	deleteFile(checkIds.substring(0, checkIds.length-1));
}

function viewReport(path, file) {
	//jetsennet.alert(path);
	var filePath = path + file;

	var width = 600;
	var height = 400;
	var left = (window.screen.width - width) / 2;
	var top = (window.screen.height - height) / 2;
	window
			.open(
					escape(filePath),
					"",
					"left="
							+ left
							+ ",top="
							+ top
							+ ",width="
							+ width
							+ ",height="
							+ height
							+ ",toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no");

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
				var option = new Option(reportTypeInfo["NAME"],reportTypeInfo["ID"]);
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

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReportFileList"));
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

	reportTypeInit();
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();
	$("divReportFileList").innerHTML = JetsenWeb.Xml.transformXML(
			"xslt/reportfile.xslt", "<RecordSet/>");
	gGridList.bind($('divReportFileList'), $('tabReportFile'));
	
	searchReportFile()
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}