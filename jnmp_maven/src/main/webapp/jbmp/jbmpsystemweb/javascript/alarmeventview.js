JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
//对象组控件
pageGroup = new JetsenWeb.UI.PageBar("pageGroup");
pageGroup.orderBy = "ORDER BY GROUP_ID";
pageGroup.changePageSize(100);
pageGroup.onpagechange = function () { getSelectGroupData(); }
pageGroup.onupdate = function () {
    $('divSelectGroupPage').innerHTML = this.generatePageControl();
}

var groupGridList = new JetsenWeb.UI.GridList();
groupGridList.ondatasort = function (sortfield, desc) {
    pageGroup.setOrderBy(sortfield, desc);
}
//报警规则控件
pageAlarmRule = new JetsenWeb.UI.PageBar("pageAlarmRule");
pageAlarmRule.orderBy = "";
pageAlarmRule.changePageSize(100);
pageAlarmRule.onpagechange = function () { getSelectAlarmRuleData(); }
pageAlarmRule.onupdate = function () {
    $('divSelectAlarmRulePage').innerHTML = this.generatePageControl();
}

var alarmRuleGridList = new JetsenWeb.UI.GridList();
alarmRuleGridList.ondatasort = function (sortfield, desc) {
	pageAlarmRule.setOrderBy(sortfield, desc);
}

//查询报表
function searchReport() {
	if(parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime() > parseDate($("txtEDate").value + " " + $("txtETime").value).getTime()){
		jetsennet.alert("结束时间必须大于起始时间！");
		return;
	}
	var reportProPath;
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	reportProPath = ret.value;
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("getReportProPath");
    
	document.getElementById("reportInfo").reportSrc = reportProPath + "/alarmEventView.jsp?flag=1";
	var selectGroup = $("showGroupOption");
    var lenGroup = selectGroup.options.length;
    
    if(lenGroup > 0){
    	var groupStr = "";
    	for (var i = 0; i < lenGroup; i++) {
    		groupStr += selectGroup.options[i].value + ',';
    	}
        document.getElementById("reportInfo").groupIds = groupStr.substring(0, groupStr.length-1);
    }else{
    	JetsenWeb.alert("请选择对象组！");
    	return;
    }
    
    if ($("txtSDate").value != "" && $("txtSTime").value != "" 
    	&& $("txtEDate").value != "" && $("txtETime").value != "") {
    	var startTimeStr = $("txtSDate").value + " " + $("txtSTime").value + '';
    	var endTimeStr = $("txtEDate").value + " " + $("txtETime").value + '';
		document.getElementById("reportInfo").startTime = startTimeStr;
		document.getElementById("reportInfo").endTime = endTimeStr;
	}else{
		JetsenWeb.alert("请选择时间！");
		return;
	}
    document.getElementById("reportInfo").src = "alarmeventviewPost.htm";
}

// 初始化===================================================================================
function pageInit() {
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

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReport1List"));
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
			height : 30
		}
	}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

function setParam(){
	var dialog = new JetsenWeb.UI.Window("set-divParam-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 200 }, title: "设置条件" });
    dialog.controls = ["divParam"];
    dialog.onsubmit = function () {
    	var flag = 0;
    	var groupOption = $("showGroupOption");
    	if(groupOption.length <= 0){
    		groupOption.style.borderColor = "rgb(153, 51, 102)";
    		flag++;
    	}else{
    		groupOption.style.borderColor = "";
    	}
    	if(flag == 0){
    		JetsenWeb.alert("设置成功！");
    		this.close();
    	}
    };
    dialog.showDialog();
}
//添加对象组
function groupAdd(){
	searchSelectGroupData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-group"), { title: "选择对象组", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectGroup"];
    dialog.onsubmit = function () {
        var obj = document.getElementsByName("chk_SelectGroup");
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked)
                AddGroupItem(obj[i].value, obj[i].getAttribute("itemName"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddGroupItem(groupID, groupName) {
	var select = $("showGroupOption");
    var len = select.options.length;
    var objNewOption = document.createElement("option");
    select.options.add(objNewOption);
    objNewOption.value = groupID;
    objNewOption.innerHTML = groupName;
}
function searchSelectGroupData() {
    pageGroup.currentPage = 1;
    getSelectGroupData();
}
function getSelectGroupData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "GROUP_ID", PageInfo: pageGroup, ResultFields: "",
        QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_OBJGROUP" })
    });

    sqlQuery.OrderString = pageGroup.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    if($("groupChecked").value != ""){
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", $("groupChecked").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.String));
	}else{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", "1,3,4,0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
	}
    if ($("txtGroupName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_NAME", $("txtGroupName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    //去除已选择的组
    var selectGroup = $("showGroupOption");
    var lenGroup = selectGroup.options.length;
    if(lenGroup > 0){
    	var str = "";
	    for (var i = 0; i < lenGroup; i++) {
	    	str += selectGroup.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectGroupList').innerHTML = JetsenWeb.Xml.transformXML("xslt/performanceanalysisgroup.xslt", ret.resultVal);
        groupGridList.bind($('divSelectGroupList'), $('tabSelectGroup'));
        pageGroup.setRowCount($('hid_GroupCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'GROUP_ID', '1']);
}
//报警规则
function alarmRuleAdd(){
	searchSelectAlarmRuleData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-alarmRule"), { title: "选择报警规则", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectAlarmRule"];
    dialog.onsubmit = function () {
        var alarmRule = document.getElementsByName("chk_SelectAlarmRule");
        for (var i = 0; i < alarmRule.length; i++) {
            if (alarmRule[i].checked)
                AddAlarmRuleItem(alarmRule[i].value, alarmRule[i].getAttribute("itemName"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddAlarmRuleItem(alarmRuleID, alarmRuleName) {
	var select = $("showAlarmRuleOption");
    var alarmRuleNewOption = document.createElement("option");
    select.options.add(alarmRuleNewOption);
    alarmRuleNewOption.value = alarmRuleID;
    alarmRuleNewOption.innerHTML = alarmRuleName;
}
function searchSelectAlarmRuleData() {
    pageAlarmRule.currentPage = 1;
    getSelectAlarmRuleData();
}
// 报警规则列表
function getSelectAlarmRuleData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var alarmRuleQueryTable = JetsenWeb.createQueryTable("BMP_ALARM");
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "ALARM_ID", PageInfo: pageAlarmRule, ResultFields: "*",
        QueryTable: alarmRuleQueryTable
    });

    sqlQuery.OrderString = pageAlarmRule.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    
    // 去除不生效规则
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("IS_VALID", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    
    var txtAlarmRuleName = $('txtAlarmRuleName').value;
    if (txtAlarmRuleName != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_NAME", txtAlarmRuleName, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    
    //去除已选择的
    var selectAlarmRule = $("showAlarmRuleOption");
    var lenAlarmRule = selectAlarmRule.options.length;
    if(lenAlarmRule > 0){
    	var str = "";
	    for (var i = 0; i < lenAlarmRule; i++) {
	    	str += selectAlarmRule.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectAlarmRuleList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisalarmrule.xslt", ret.resultVal);
        alarmRuleGridList.bind($('divSelectAlarmRuleList'), $('tabSelectAlarmRule'));
        pageAlarmRule.setRowCount($('hid_AlarmRuleCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//删除选项
function selectOptionsDel(selCtrl)
{
    var itemCount = selCtrl.options.length;
    var selectedItemCount = 0;
    if (itemCount>0)
    {
	    for(var i=itemCount-1;i>=0;i--)
	    {
		    if(selCtrl.options[i].selected)
		    {	
			    selCtrl.removeChild(selCtrl.options[i]);
			    selectedItemCount++;
		    }
	    }
    }
    if(selectedItemCount == 0) {
    	jetsennet.alert("请选择要删除的项！");
    }
}
//将“yyyy-MM-dd HH:mm:ss”或者“yyyy-MM-dd”字符串转换成Date
function parseDate(dateString)
{
	var dateReg = /^([1-9]\d{3})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\d|3[01])( ((0?|1)\d|2[0-3]):((0?|[1-5])\d):((0?|[1-5])\d))?$/;
	if (!dateReg.test(dateString))
	{
		return null;
	}
	var childgroups = dateString.match(dateReg);
	var execFuncs = ["", "setFullYear", "setMonth", "setDate", "", "setHours", "", "setMinutes", "", "setSeconds", ""];
	var date = new Date(0);
	for (var i = 0; i < childgroups.length; i++)
	{
		if (execFuncs[i] != "" && childgroups[i] != "")
		{
			var num = Number(childgroups[i]);
			if (execFuncs[i] == "setMonth")
			{
				num -= 1;
			}
			date[execFuncs[i]](num);
		}
	}
	return date;
}