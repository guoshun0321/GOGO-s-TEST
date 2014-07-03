//***********************************************//
//自动发现类型新增步骤：
//1、修改tabTaskType()函数
//2、修改getParams()函数
//3、修改xslt/autodisctask.xslt文件
//***********************************************//
JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
// 页面控件，表示当前页
var gFrame;
// 页面大小重置函数句柄
var gWindowSizeChangedInterVal;
// 属性分类
var collTaskArray;
// 开始IP
var startIp;
// 介绍IP
var endIp;
// 分页
var gPage = new JetsenWeb.UI.PageBar("PageBarAutoDis");
gPage.onpagechange = function() {
	search();
};
gPage.orderBy = "ORDER BY a.TASK_ID";
gPage.onupdate = function() {
	$('divTaskListPage').innerHTML = this.generatePageControl();
};
// 表格渲染
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gPage.setOrderBy(sortfield, desc);
};
// 查询条件
var gCondition = new JetsenWeb.SqlConditionCollection();
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_AUTODISTASK", "a");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "b", "a.COLL_ID=b.COLL_ID", JetsenWeb.TableJoinType.Left));
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "a.COLL_ID=c.NUM_VAL1", JetsenWeb.TableJoinType.Left));
gSqlQuery.GroupFields = "a.TASK_ID,a.COLL_ID,a.BEGIN_IP,a.END_IP,a.STATUS,a.COMMUNITY,a.FIELD_1,a.FIELD_2,a.TIME_POINT,a.WEEK_MASK,a.EXE_TYPE,a.TASK_TYPE,a.ADD_INFO,a.IS_AUTOINS,b.COLL_NAME";
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, KeyId : "a.TASK_ID", PageInfo : gPage, QueryTable : gQueryTable, 
	ResultFields : "a.TASK_ID,a.COLL_ID,a.BEGIN_IP,a.END_IP,a.STATUS,a.COMMUNITY,a.FIELD_1,a.FIELD_2,a.TIME_POINT,a.WEEK_MASK,a.EXE_TYPE,a.TASK_TYPE,a.ADD_INFO,a.IS_AUTOINS,b.COLL_NAME" });

// 页面初始化
window.onload = function() {
	// 构造页面
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });

	frameContent.addControl(new JetsenWeb.UI.PageItem("divTaskList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 30 } }));
	gFrame.addControl(frameContent);
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	// 初始化
	initSelCollId();
	initAutoDisConfig();
	loadMainTable();
	startIp = new IP('txt_BEGIN_IP');
	endIp = new IP('txt_END_IP');
	startIp.init();
	endIp.init();

	// 页面大小变化的动作
	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
}
// 调节窗口大小
function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}
// 初始化采集器下拉框
function initSelCollId() {
	initCollTask();
	if (collTaskArray == null) {
		return;
	}
	var sel = $("sel_COLL_ID");
	var sel1 = $("sel_COLL_ID1");
	for (i = 0; i < collTaskArray.length; i++) {
		var varItem = new Option(collTaskArray[i].value["COLL_NAME"], collTaskArray[i].key);
		sel.options.add(varItem);
		var varItem1 = new Option(collTaskArray[i].value["COLL_NAME"], collTaskArray[i].key);
		sel1.options.add(varItem1);
	}
}
// 获取全部采集器
function initCollTask() {
	var queryTable = JetsenWeb.createQueryTable("BMP_COLLECTOR", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "b","a.coll_id = b.num_val1", JetsenWeb.TableJoinType.LEFT));
	var sqlQuery = new JetsenWeb.SqlQuery();
//	sqlQuery.OrderString = "ORDER BY a.COLL_ID";
	sqlQuery.GroupFields = " a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2 "; 
	JetsenWeb.extend(sqlQuery, { KeyId : "COLL_ID", QueryTable : queryTable, ResultFields : " a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2 " });
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;

	ws.oncallback = function(ret) {
		var tasks = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		collTaskArray = new Array();
		if (tasks == null || tasks.length == 0) {
			return;
		}
		for ( var i = 0; i < tasks.length; i++) {
			var task = tasks[i];
			collTaskArray.push( { key : task["COLL_ID"], value : task });
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'b.GROUP_ID', '1']);
}
// 获取自动发现任务配置
function initAutoDisConfig() {
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;

	ws.oncallback = function(ret) {
		var configs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		var sel = $("sel_TASK_TYPE");
		sel.length = 0;
		if (configs == null || configs.length == 0) {
			return;
		}
		for ( var i = 0; i < configs.length; i++) {
			var config = configs[i];
			var varItem = new Option(config["discription"], config["type"]);
			varItem.setAttribute("dis", config["display"]);
			sel.options.add(varItem);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpGetAutoDisConfig", []);
}
// 查找
function search() {
	loadMainTable();
}
// 加载页面
function loadMainTable() {
	// 排序
	gSqlQuery.OrderString = gPage.orderBy;
	// 条件
	gCondition.SqlConditions = [];
	var collId = getSelectedValue($("sel_COLL_ID"))
	if (collId != -1) {
		gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.COLL_ID", collId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	gSqlQuery.Conditions = gCondition;
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divTaskList').innerHTML = JetsenWeb.Xml.transformXML("xslt/autodisctask.xslt", ret.resultVal);
		gGridList.bind($('divTaskList'), $('autoDiscTaskTable'));
		gPage.setRowCount($('hid_pageCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'c.GROUP_ID', '1']);
}
// 新增
function newTask() {
	var areaElements = JetsenWeb.Form.getElements('divAutoTask');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("txt_COMMUNITY").value = "public";
	// IE在判断默认值时
	setSelectedValue($("sel_TASK_TYPE"), 0);
	setSelectedValue($("sel_EXE_TYPE"), 0);
	tabTaskType();
	$("sel_EXE_TYPE").disabled = false;
	$("sel_TASK_TYPE").disabled = false;
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 550, height : 330 }, title : "新建任务" });
	dialog.controls = [ "divAutoTask" ];
	dialog.onsubmit = function() {
		var obj = getParams();
		if (obj == null) {
			return;
		}
		if (JetsenWeb.Form.Validate(areaElements, true)) {

			obj.STATUS = 0;
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadMainTable();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_AUTODISTASK", JetsenWeb.Xml.serializer(obj, "BMP_AUTODISTASK") ]);
		}
	};
	dialog.showDialog();
	$("txt_BEGIN_IP").focus();
}
// 获取数据
function getParams() {
	var type = getSelectedValue($("sel_TASK_TYPE"));
	var sip = startIp.getValue();
	var eip = endIp.getValue();
	var info = $("txt_ADD_INFO").value;
	var isAutoIns = 0;
	var chkAutoIns = document.getElementsByName("chkAutoIns")[0];
	if (chkAutoIns.checked) {
		isAutoIns = 1;
	}
	var params = { COLL_ID : getSelectedValue($("sel_COLL_ID1")), TASK_TYPE : type, IS_AUTOINS : isAutoIns };
	if (type == 0) { // SNMP
		if (!validateIPSection(sip, eip)) {
			return null;
		}
		params["BEGIN_IP"] = sip;
		params["END_IP"] = eip;
		params["COMMUNITY"] = $("txt_COMMUNITY").value;
		params["ADD_INFO"] = $("txt_COMMUNITY").value;
	} else if (type == 2) { // FTP
		if (!validateIPSection(sip, eip)) {
			return null;
		}
		if (info != "" && !validatePort(info)) {
			jetsennet.alert("附加信息必须为(0-65536)之间的数字");
			return null;
		}
		params["BEGIN_IP"] = sip;
		params["END_IP"] = eip;
		params["COMMUNITY"] = "";
		params["ADD_INFO"] = info;
	} else if (type == 3) { // HTTP
		if (!validateIPSection(sip, eip)) {
			return null;
		}
		if (info != "" && !validatePort(info)) {
			jetsennet.alert("附加信息必须为(0-65536)之间的数字");
			return null;
		}
		params["BEGIN_IP"] = sip;
		params["END_IP"] = eip;
		params["COMMUNITY"] = "";
		params["ADD_INFO"] = info;
	} else if (type == 1) { // 金数信节目，中国有线项目
		params["BEGIN_IP"] = "";
		params["END_IP"] = "";
		params["COMMUNITY"] = "";
		params["ADD_INFO"] = info;
	} else if (type == 4) { // 数码视讯，中国有线项目
		params["BEGIN_IP"] = "";
		params["END_IP"] = "";
		params["COMMUNITY"] = "";
		params["ADD_INFO"] = info;
	}
	// 采集方式
	var exeType = getSelectedValue($("sel_EXE_TYPE"));
	params["EXE_TYPE"] = exeType;
	if (exeType == 0) {
		params["TIME_POINT"] = "";
		params["WEEK_MASK"] = "";
	} else {
		var weekMask = "";
		var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
		if (weekdays.length == 0) {
			jetsennet.alert("请选择周几报警！");
			return null;
		}
		var timePoint = $("txt_TimePoint").value;
		if (timePoint == "") {
			jetsennet.alert("请填写采集时间！");
			return null;
		}

		var chkWeeks = document.getElementsByName("chkWeek");
		for ( var i = 0; i < chkWeeks.length; i++) {
			if (chkWeeks[i].checked) {
				weekMask += (i + 1).toString();
			} else {
				weekMask += "0";
			}
		}
		params["TIME_POINT"] = timePoint;
		params["WEEK_MASK"] = weekMask;
	}
	return params;
}
// 编辑
function editTask(iTaskId, iCollId, iTaskType, iBeginIp, iEndIp, iAddInfo, iStatus, iCommunity, iTimePoint, iWeekMask, iExeType, iIsAutoIns) {
	var areaElements = JetsenWeb.Form.getElements('divAutoTask');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	setParams(iCollId, iTaskType, iBeginIp, iEndIp, iAddInfo, iCommunity, iExeType);
	if (iExeType == 1 && iWeekMask) {
		var chkWeeks = document.getElementsByName("chkWeek");
		for ( var i = 0; i < chkWeeks.length; i++) {
			chkWeeks[i].checked = (parseInt(iWeekMask.charAt(i)) == parseInt(chkWeeks[i].value));
		}
		$("txt_TimePoint").value = iTimePoint;
	}
	var chkAutoIns = document.getElementsByName("chkAutoIns");
	if (iIsAutoIns == 0) {
		chkAutoIns[0].checked = false;
	} else {
		chkAutoIns[0].checked = true;
	}
	$("sel_TASK_TYPE").disabled = true;
	$("sel_EXE_TYPE").disabled = true;
	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 550, height : 330 }, title : "编辑任务" });
	dialog.controls = [ "divAutoTask" ];
	dialog.onsubmit = function() {
		var obj = getParams();
		if (obj == null) {
			return;
		}
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			obj.TASK_ID = iTaskId;
			obj.STATUS = iStatus;
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadMainTable();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_AUTODISTASK", JetsenWeb.Xml.serializer(obj, "BMP_AUTODISTASK") ]);
		}
	};
	dialog.showDialog();
}
// 加载数据
function setParams(iCollId, iTaskType, iBeginIp, iEndIp, iAddInfo, iCommunity, iExeType) {
	setSelectedValue($("sel_TASK_TYPE"), iTaskType);
	setSelectedValue($("sel_EXE_TYPE"), iExeType);
	tabTaskType();
	setSelectedValue($("sel_COLL_ID1"), iCollId);
	startIp.setValue(iBeginIp);
	endIp.setValue(iEndIp);
	$("txt_ADD_INFO").value = iAddInfo;
	$("txt_COMMUNITY").value = iCommunity;
}
// 删除
function deleteTask(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadMainTable();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_AUTODISTASK", keyId ]);

		return true;
	});
}
// 自动发现操作
function operat(taskId, collId) {
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadMainTable();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
		loadMainTable();
	};
	ws.call("bmpAutoDisc", [ taskId, collId ]);
}
// 切换类型
function tabTaskType() {
	var sel = $("sel_TASK_TYPE");
	var taskType = getSelectedValue(sel);
	var disType = sel.options[sel.selectedIndex].getAttribute("dis");
	var inschk = $("chk_AutoIns");
	inschk.disabled = false;
	inschk.checked = false;
	if (taskType == 0) {
		$("trSnmp1").style.display = "";
		$("trSnmp2").style.display = "";
		$("trSnmp3").style.display = "";
		$("trInfo").style.display = "none";
	} else if (taskType == 1 || taskType == 4) {
		$("trSnmp1").style.display = "none";
		$("trSnmp2").style.display = "none";
		$("trSnmp3").style.display = "none";
		$("trInfo").style.display = "";
		inschk.disabled = true;
		inschk.checked = true;
	} else if (taskType == 2) {
		$("trSnmp1").style.display = "";
		$("trSnmp2").style.display = "";
		$("trSnmp3").style.display = "none";
		$("trInfo").style.display = "";
		$("txt_ADD_INFO").value = 80;
	} else if (taskType == 3) {
		$("trSnmp1").style.display = "";
		$("trSnmp2").style.display = "";
		$("trSnmp3").style.display = "none";
		$("trInfo").style.display = "";
		$("txt_ADD_INFO").value = 21;
	}
	var exeType = getSelectedValue($("sel_EXE_TYPE"));
	if (exeType == 0) {
		$("trWeek").style.display = "none";
		$("trHour").style.display = "none";
	} else {
		$("trWeek").style.display = "";
		$("trHour").style.display = "";
	}
}
function typeChange() {
	tabTaskType();
}
function exeTypeChange() {
	tabTaskType();
}