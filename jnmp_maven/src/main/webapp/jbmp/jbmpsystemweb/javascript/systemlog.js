JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_tooltip",
		"js_timeeditor", "js_datepicker", "js_window", "js_pageframe" ]);
// LOG
var gFrame;
var gWindowSizeChangedInterVal;
var gLogSqlCollection = new JetsenWeb.SqlConditionCollection();
var gLogPage = new JetsenWeb.UI.PageBar("gLogPage");
gLogPage.onpagechange = function() {
	getSystemLog();
}
gLogPage.orderBy = "ORDER BY LOG_TIME DESC";
var myGridList = new JetsenWeb.UI.GridList();
myGridList.ondatasort = function(sortfield, desc) {
	gLogPage.setOrderBy(sortfield, desc);
}
gLogPage.onupdate = function() {
	document.getElementById('divLogPage').innerHTML = this
			.generatePageControl();
}
function getSystemLog() {
	var _sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(_sqlQuery, {
		IsPageResult : 1,
		KeyId : "ID",
		PageInfo : gLogPage,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), {
			TableName : "NET_OPERATORLOG"
		})
	});
	_sqlQuery.Conditions = gLogSqlCollection;
	_sqlQuery.OrderString = gLogPage.orderBy;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.displayLoading = false;
	ws.oncallback = function($) {
		var retVal = JetsenWeb.Xml.transformXML("xslt/systemlog.xslt",
				$.resultVal);
		if (!JetsenWeb.Util.isNullOrEmpty(retVal)) {
			el('divSystemLogList').innerHTML = retVal;
			// var grid = new JetsenWeb.UI.GridList();
			myGridList.bind(el('divSystemLogList'), el('tabSystemLog'));
			gLogPage.setRowCount(el('hid_TotalCount').value);
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ _sqlQuery.toXml() ]);
}
function searchSystemLog() {
	gLogSqlCollection = new JetsenWeb.SqlConditionCollection();
	var startTime = parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime();
	var endTime = parseDate($("txtEDate").value + " " + $("txtETime").value).getTime();
	if($("chkDate").checked && illegalityTime(startTime, endTime)){
		return;
	}
	if ($('txtCreator').value != "") {
		gLogSqlCollection.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"USER_NAME", $('txtCreator').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($('txt_Key').value != "") {
		gLogSqlCollection.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"DESCRIPTION", $('txt_Key').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($("chkDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		gLogSqlCollection.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"LOG_TIME", $("txtSDate").value + " " + $("txtSTime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("chkDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		gLogSqlCollection.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"LOG_TIME", $("txtEDate").value + " " + $("txtETime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	gLogPage.currentPage = 1;
	getSystemLog();
}
checkAllLog = function(isCheck) {
	var obj = document.getElementsByName("chkSystemLog");
	if (obj != null) {
		for ( var i = 0; i < obj.length; i++) {
			obj[i].checked = isCheck;
		}
	}
}
function deleteSystemLog() {

	var dialog = new jetsennet.ui.Window("new-object-win");   
	jetsennet.extend(dialog, { maximizeBox: false, minimizeBox: false, windowStyle: 1, submitBox: true, cancelBox: true,size: { width: 300, height: 0 }, title: "日志清除" });   
	dialog.controls = ["delSystemLogDiv"];
	dialog.onsubmit=function(){
		var startTime="";
		var endTime="";
		var startTimeInt="";
		var endTimeInt="";
		
		if($("delStartDate").value == "" && $("delChkSDate").checked){
			jetsennet.alert("勾选了开始日期的复选框，必须输入开始日期!")
			return;
		}
		if($("delEndDate").value == "" && $("delchkEDate").checked){
			jetsennet.alert("勾选了结束日期的复选框，必须输入结束日期!")
			return;
		}
		if($("delStartDate").value != ""&& $("delChkSDate").checked){
			startTime = $("delStartDate").value+ " " + $("delSTime").value;
			startTimeInt = parseDate($("delStartDate").value+ " " + $("delSTime").value).getTime();
		 }
		if($("delEndDate").value != "" && $("delchkEDate").checked){
			endTime = $("delEndDate").value+ " " + $("delETime").value;
			endTimeInt = parseDate($("delEndDate").value+ " " + $("delETime").value).getTime();
		 }
		
		if(startTimeInt!="" && endTimeInt!="" && illegalityTime(startTimeInt, endTimeInt)){
			jetsennet.alert("开始日期必须小于结束日期!");
			return;
		}
		
		
	     if(!$("delChkSDate").checked)
	    {
	    	 startTime="";
	    }
	    if(!$("delchkEDate").checked )
	    {
	    	endTime="";
	    }
	    jetsennet.confirm("确定删除？", function() {
	    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function(ret)
	    {
	    	if(ret.errorCode=='0'){
	    		jetsennet.alert("删除成功！");
	    	}
	    	dialog.close();
	    	searchSystemLog();
	    };
	    ws.onerror = function(ex){ alert(ex);};
	    ws.call("dmpObjTimeSlotDelete",["NET_OPERATORLOG",startTime,endTime]);
	    return true;
	    });
	};
	dialog.showDialog();  

}
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	var date = new Date();
	$("txtEDate").value = date.toDateString();
	$("txtETime").value = date.toTimeString();
	date.setHours(date.getHours() - 2);
	$("txtSDate").value = date.toDateString();
	$("txtSTime").value = date.toTimeString();

	searchSystemLog();

	gFrame = new JetsenWeb.UI.PageFrame("divPageFrame");
	gFrame.splitType = 1;
	gFrame.splitTitle = "divListTitle";
	gFrame.splitSize = 27;
	var _frameTop = new JetsenWeb.UI.PageItem("divTop");
	_frameTop.size = {
		width : 0,
		height : 30
	};
	var _frameContent = new JetsenWeb.UI.PageFrame("divContent");
	_frameContent.splitType = 1;
	_frameContent.fixControlIndex = 1;
	_frameContent.showSplit = false;
	_frameContent.addControl(new JetsenWeb.UI.PageItem("divSystemLogList"));
	_frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
		}
	}));
	gFrame.addControl(_frameTop);
	gFrame.addControl(_frameContent);
	gFrame.fixControlIndex = 0;
	gFrame.enableResize = false;

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	$("delETime").value = "23:59:59";
    $("delSTime").value = "00:00:00";
}

function windowResized() {
	var _size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : _size.width,
		height : _size.height
	};
	gFrame.resize();
}