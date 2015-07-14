JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree" ]);
var gFrame;
var AC_IMG_PATH = "./images/acIcon/";
var gWindowSizeChangedInterVal;
var gAlarmConfigPage = new JetsenWeb.UI.PageBar("AlarmConfig");
gAlarmConfigPage.onpagechange = function() {
	loadAlarmConfig();
};
gAlarmConfigPage.orderBy = "order by s.ALARM_ID";
gAlarmConfigPage.onupdate = function() {
	$('divAlarmConfigPage').innerHTML = this.generatePageControl();
};
var gAlarmConfigCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gAlarmConfigPage.setOrderBy(sortfield, desc);
};

var gAlarmLevelGridList = new JetsenWeb.UI.GridList();

var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARM", "s");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMTYPE", "t", "t.TYPE_ID=s.ALARM_TYPE", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, KeyId : "s.ALARM_ID", ResultFields : "s.*,t.TYPE_NAME", PageInfo : gAlarmConfigPage,
	QueryTable : gQueryTable });

var gActionGridList = new JetsenWeb.UI.GridList();
var gActionSqlQuery = new JetsenWeb.SqlQuery();
var gActionCondition = new JetsenWeb.SqlConditionCollection();
var gActionQueryTable = JetsenWeb.createQueryTable("BMP_ACTION", "");
JetsenWeb.extend(gActionSqlQuery, { KeyId : "", QueryTable : gActionQueryTable, ResultFields : "ACTION_ID,ACTION_NAME" });

var curTypeId = 0;

var actions = {};

//加载=====================================================================================
function loadAlarmConfig() {
	gSqlQuery.OrderString = gAlarmConfigPage.orderBy;
	gSqlQuery.Conditions = gAlarmConfigCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divAlarmConfigList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmconfig.xslt", ret.resultVal);
		gGridList.bind($('divAlarmConfigList'), $('tabAlarmConfig'));
		gAlarmConfigPage.setRowCount($('hid_AlarmConfigCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function searchAlarmConfig() {
	gAlarmConfigCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gAlarmConfigCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_NAME", $('txt_Key').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if (curTypeId != 0) {
		gAlarmConfigCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_TYPE", curTypeId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
	}
	gAlarmConfigPage.currentPage = 1;
	loadAlarmConfig();
}
//删除=====================================================================================
function deleteAlarmConfig(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadAlarmConfig();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_ALARM", keyId ]);
		return true;
	});
}
//新增=====================================================================================
function newAlarmConfig() {
	var areaElements = JetsenWeb.Form.getElements('divAlarmConfig');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	$('CHECK_SPAN').disabled = "";
	isTrap($('CHECK_SPAN'));

	for ( var i = 0; i < $("cbo_ALARM_TYPE").options.length; i++) {
		if ($("cbo_ALARM_TYPE").options[i].value == curTypeId) {
			$("cbo_ALARM_TYPE").options[i].selected = true;
		}
	}
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 350 }, title : "新建报警规则" });
	showRemindWordCount($("txt_ALARM_DESC").value, $('remindWord'), "30");

	dialog.controls = [ "divAlarmConfig" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objAlarmConfig = { ALARM_NAME : $("txt_ALARM_NAME").value, ALARM_TYPE : getSelectedValue($("cbo_ALARM_TYPE")),
				CHECK_SPAN : getSelectedValue($("CHECK_SPAN")), CHECK_NUM : $("txt_CHECK_NUM").value, OVER_NUM : $("txt_OVER_NUM").value,
				ALARM_DESC : $("txt_ALARM_DESC").value, CREATE_USER : JetsenWeb.Application.userInfo.UserName };

			if (objAlarmConfig["CHECK_NUM"] == "0") {
				jetsennet.alert("检查次数必须大于0！");
				return;
			}
			if (objAlarmConfig["OVER_NUM"] == "0") {
				jetsennet.alert("越限次数必须大于0！");
				return;
			}
			if (parseInt(objAlarmConfig["OVER_NUM"]) > parseInt(objAlarmConfig["CHECK_NUM"])) {
				jetsennet.alert("越限次数必须小于等于检查次数！");
				return;
			}
			if (parseInt(getBytesCount($("txt_ALARM_DESC").value)) > 60) {
				jetsennet.alert("报警描述不能超过30个文字！");
				return;
			}
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadAlarmConfig();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ALARM", JetsenWeb.Xml.serializer(objAlarmConfig, "BMP_ALARM") ]);
		}
	};
	dialog.showDialog();
	$("txt_ALARM_NAME").focus();
}
//编辑=====================================================================================
function editAlarmConfig(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divAlarmConfig');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	//规则类型不能修改
	$('CHECK_SPAN').disabled = "disabled";

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ALARM_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARM" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objAlarmConfig = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_ALARM_NAME").value = valueOf(objAlarmConfig, "ALARM_NAME", "");
		setSelectedValue($("cbo_ALARM_TYPE"), valueOf(objAlarmConfig, "ALARM_TYPE", "-1"));
		setSelectedValue($("CHECK_SPAN"), valueOf(objAlarmConfig, "CHECK_SPAN", "0"));
		$("txt_CHECK_NUM").value = valueOf(objAlarmConfig, "CHECK_NUM", "");
		$("txt_OVER_NUM").value = valueOf(objAlarmConfig, "OVER_NUM", "");
		$("txt_ALARM_DESC").value = valueOf(objAlarmConfig, "ALARM_DESC", "");
		showRemindWordCount($("txt_ALARM_DESC").value, $('remindWord'), "30");
		isTrap($('CHECK_SPAN'));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 350 }, title : "编辑报警规则" });
	dialog.controls = [ "divAlarmConfig" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oAlarmConfig = { ALARM_ID : keyId, ALARM_NAME : $("txt_ALARM_NAME").value, ALARM_TYPE : getSelectedValue($("cbo_ALARM_TYPE")),
				CHECK_SPAN : getSelectedValue($("CHECK_SPAN")), CHECK_NUM : $("txt_CHECK_NUM").value, OVER_NUM : $("txt_OVER_NUM").value,
				ALARM_DESC : $("txt_ALARM_DESC").value };

			if (oAlarmConfig["CHECK_NUM"] == "0") {
				jetsennet.alert("检查次数必须大于0！");
				return;
			}
			if (oAlarmConfig["OVER_NUM"] == "0") {
				jetsennet.alert("越限次数必须大于0！");
				return;
			}
			if (parseInt(oAlarmConfig["OVER_NUM"]) > parseInt(oAlarmConfig["CHECK_NUM"])) {
				jetsennet.alert("越限次数必须小于等于检查次数！");
				return;
			}
			if (parseInt(getBytesCount($("txt_ALARM_DESC").value)) > 60) {
				jetsennet.alert("报警描述不能超过30个文字！");
				return;
			}
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadAlarmConfig();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ALARM", JetsenWeb.Xml.serializer(oAlarmConfig, "BMP_ALARM") ]);
		}
	};
	dialog.showDialog();
}
//初始化===================================================================================
function pageInit() {
	searchAlarmConfig();
	loadTree();
	if (parent.document.getElementById("spanWindowName") != null) {
		parent.document.getElementById("spanWindowName").innerHTML = document.title;
	}
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 0, fixControlIndex : 0, splitBorder : 0, showSplit : true,
		enableResize : true });

	var frameRgiht = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaRight"), { splitType : 1, fixControlIndex : 0, splitBorder : 0,
		showSplit : false });

	var frameTop = new JetsenWeb.UI.PageItem("divTop");
	frameTop.size = { width : 0, height : 27 };
	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType : 1, fixControlIndex : 1, showSplit : false });
	frameContent.addControl(new JetsenWeb.UI.PageItem("divContent"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	var frameLeft = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaLeft"), { splitType : 1, fixControlIndex : 0, splitBorder : 0,
		size : { width : 210, height : 0 }, showSplit : false });
	frameLeft.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divLeftTitle"), { size : { width : 0, height : 27 } }));
	frameLeft.addControl(new JetsenWeb.UI.PageItem("divTree"));

	frameRgiht.addControl(frameTop);
	frameRgiht.addControl(frameContent);

	gFrame.addControl(frameLeft);
	gFrame.addControl(frameRgiht);

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

// 分类树
function loadTree() {
	var sel = $("cbo_ALARM_TYPE");
	sel.length = 0;
	sel.options.add(new Option("请选择", -1));

	$('divTree').innerHTML = "";
	gTypeTree = new JetsenWeb.UI.Tree("所有分类", "javascript:treeAction(0)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
	gTypeTree.showTop = true;
	gTypeTree.setBehavior("classic");

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARMTYPE", "aa");
	sqlQuery.OrderString = "ORDER BY aa.CREATE_TIME ASC";
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.TYPE_ID", ResultFields : "aa.*", PageInfo : null, QueryTable : queryTable });

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var classes = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (classes != null) {
			var attrNode = new Array();
			for (i = 0; i < classes.length; i++) {
				var attrCls = classes[i];

				var classId = attrCls["TYPE_ID"];
				var varItem = new Option(attrCls["TYPE_NAME"], classId);
				sel.options.add(varItem);

				var node = null;
				node = new JetsenWeb.UI.TreeItem(attrCls["TYPE_NAME"], "javascript:treeAction(" + attrCls["TYPE_ID"] + ")", null, AC_IMG_PATH
						+ "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
				if (attrCls["PARENT_ID"] == -1) {
					gTypeTree.add(node);
				} else {
					for (j = 0; j < attrNode.length; j++) {
						if (classes[j]["TYPE_ID"] == attrCls["PARENT_ID"]) {
							attrNode[j].add(node);
							break;
						}
					}
				}
				attrNode.push(node);
			}
		}
		var unknown = new JetsenWeb.UI.TreeItem("未分类报警", "javascript:treeAction(-1)", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH
				+ "pcde_002.gif");
		gTypeTree.add(unknown);
		$("divTree").innerHTML = gTypeTree;
		gTypeTree.expandAll();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function treeAction(classId) {
	curTypeId = classId;
	searchAlarmConfig();
}

//记录当前规则类型
var ruleType = 0;
// 查看报警级别
function viewAlarmLevel(alarmId, alarmName, checkSpan, checkNum, OverNum) {
	if (checkSpan == 1 && checkNum == 1 && OverNum == 1) {
		ruleType = 1;
	} else {
		ruleType = 0;
	}

	var areaElements = JetsenWeb.Form.getElements("divAlarmLevel");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("divAlarmLevelList").innerHTML = "数据加载中...";

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true, size : { width : 800, height : 401 },
		title : "报警级别", cancelButtonText : "关闭" });
	dialog.controls = [ "divAlarmLevel" ];
	dialog.onsubmit = function() {
		return false;
	};
	$("hid_ALARM_ID").value = alarmId;
	$("spanAlarmConfig").innerHTML = alarmName;

	loadAlarmLevel(alarmId);
	dialog.showDialog();
}

// 新增报警级别
function newAlarmLevel() {
	var areaElements = JetsenWeb.Form.getElements("divAlarmLevelEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	//Trap类型要多填一个OID
	if (ruleType == 1) {
		$('tr_VAR_NAME').style.display = "";
		$('txt_VAR_NAME').setAttribute("validatetype", "NotEmpty");
	} else {
		$('tr_VAR_NAME').style.display = "none";
		$('txt_VAR_NAME').setAttribute("validatetype", "");
	}

	var chkWeeks = document.getElementsByName("chkWeek");
	for ( var i = 0; i < chkWeeks.length; i++) {
		chkWeeks[i].checked = true;
	}
	showRemindWordCount($("txt_LEVEL_DESC").value, $('remindWord2'), "30");
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 600, height : 500 }, title : "新建报警级别" });
	dialog.controls = [ "divAlarmLevelEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (!/^(([\d]+(.[\d]+)+)*|[\w ]+)$/.test($('txt_VAR_NAME').value)) {
				jetsennet.alert("请输入正确格式的OID！");
				return;
			}
			if (checkThreshold() == false) {
				return;
			}
			var weekMask = "";
			var hourMask = "";
			var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
			if (weekdays.length == 0) {
				jetsennet.alert("请选择周几报警！");
				return;
			}

			var chkWeeks = document.getElementsByName("chkWeek");
			for ( var i = 0; i < chkWeeks.length; i++) {
				if (chkWeeks[i].checked) {
					weekMask += (i + 1).toString();
				} else {
					weekMask += "0";
				}
			}

			hourMask = getHourMask();
			if (!hourMask) {
				jetsennet.alert("起止时间（0-23）有误！");
				return;
			}

			if (parseInt(getBytesCount($("txt_LEVEL_DESC").value)) > 60) {
				jetsennet.alert("级别描述不能超过30个文字！");
				return;
			}
			var alarmId = $("hid_ALARM_ID").value;
			var objAlarmLevel = {
				ALARM_ID : alarmId,
				ALARM_LEVEL : getSelectedValue($("cbo_ALARM_LEVEL"))
				//, SUB_LEVEL: $("txt_SUB_LEVEL").value
				, CONDITION : getSelectedValue($("cbo_CONDITION")), THRESHOLD : $("txt_THRESHOLD").value, LEVEL_NAME : $("txt_LEVEL_NAME").value,
				LEVEL_DESC : $("txt_LEVEL_DESC").value, WEEK_MASK : weekMask, HOUR_MASK : hourMask,
				ACTION_IDS : getAllValues($("cbo_ACTION")).join(","), VAR_NAME : $("txt_VAR_NAME").value };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadAlarmLevel(alarmId);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ALARMLEVEL", JetsenWeb.Xml.serializer(objAlarmLevel, "BMP_ALARMLEVEL") ]);
		}
	};
	$("cbo_ACTION").length = 0;

	dialog.showDialog();
	//$("txt_SUB_LEVEL").focus();
}
function getHourMask() {
	var hourMask = "";
	if (($("txtSHour") != null) && ($("txtEHour") != null)) {
		var startValue = $("txtSHour").value;
		var endValue = $("txtEHour").value;
		var startHour = startValue == "" ? 0 : Number(startValue);
		var endHour = endValue == "" ? 23 : Number(endValue);
		if (isNaN(startHour) || isNaN(endHour) || startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23 || startValue.indexOf(".") != -1
				|| endValue.indexOf(".") != -1 || startHour > endHour) {
			return null;
		}

		for ( var i = 0; i < 24; i++) {
			if (i >= startHour && i <= endHour) {
				if ((i + 1) < 10) {
					hourMask += "0" + (i + 1).toString() + ",";
				} else if ((i + 1) == 24) {
					hourMask += (i + 1).toString();
				} else {
					hourMask += (i + 1).toString() + ",";
				}
			} else {
				if ((i + 1) == 24) {
					hourMask += "00";
				} else {
					hourMask += "00,";
				}
			}
		}
	} else {
		return null;
	}

	return hourMask;
}
// 编辑报警级别
function editAlarmLevel(levelId) {
	var areaElements = JetsenWeb.Form.getElements("divAlarmLevelEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	//Trap类型要多填一个OID
	if (ruleType == 1) {
		$('tr_VAR_NAME').style.display = "";
		$('txt_VAR_NAME').setAttribute("validatetype", "NotEmpty");
	} else {
		$('tr_VAR_NAME').style.display = "none";
		$('txt_VAR_NAME').setAttribute("validatetype", "");
	}

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("LEVEL_ID", levelId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "LEVEL_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARMLEVEL" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objAlarmLevel = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		var weekMask = valueOf(objAlarmLevel, "WEEK_MASK", "");
		if (weekMask) {
			var chkWeeks = document.getElementsByName("chkWeek");
			for ( var i = 0; i < chkWeeks.length; i++) {
				chkWeeks[i].checked = (parseInt(weekMask.charAt(i)) == parseInt(chkWeeks[i].value));
			}
		}

		var hourMask = valueOf(objAlarmLevel, "HOUR_MASK", "");
		if (hourMask) {
			var arr = hourMask.split(",");

			var flag1 = 0;
			var flag2 = 0;
			var len = 0; //长度
			var str1 = "";
			var str2 = "";

			for ( var m = 0; m < arr.length; m++) {
				if (arr[m] == "00") {
					flag2 = 0;
					if (flag2 != flag1)//由1变0时
					{
						if (len != 1) {
							str2 = (m - 1).toString();
						} else {
							str2 = str1;
						}
					}
					len = 0;
					flag1 = flag2;
				} else {
					flag2 = 1;
					if (flag2 != flag1)//由0变1
					{
						str1 = m.toString();
					}

					len++;
					flag1 = flag2;

					if (m == 23) {
						str2 = (m).toString();
					}
				}
			}

			if ($("txtSHour") != null)
				$("txtSHour").value = str1;
			if ($("txtEHour") != null)
				$("txtEHour").value = str2;
		}
		$("hid_ALARM_ID").value = valueOf(objAlarmLevel, "ALARM_ID", "");
		setSelectedValue($("cbo_ALARM_LEVEL"), valueOf(objAlarmLevel, "ALARM_LEVEL", ""));
		//$("txt_SUB_LEVEL").value = valueOf(objAlarmLevel, "SUB_LEVEL", "");
		setSelectedValue($("cbo_CONDITION"), valueOf(objAlarmLevel, "CONDITION", ""));
		$("txt_THRESHOLD").value = valueOf(objAlarmLevel, "THRESHOLD", "");
		$("txt_LEVEL_NAME").value = valueOf(objAlarmLevel, "LEVEL_NAME", "");
		$("txt_LEVEL_DESC").value = valueOf(objAlarmLevel, "LEVEL_DESC", "");
		$("txt_VAR_NAME").value = valueOf(objAlarmLevel, "VAR_NAME", "");
		loadActionByLevel(levelId);
		showRemindWordCount($("txt_LEVEL_DESC").value, $('remindWord2'), "30");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 600, height : 500 }, title : "编辑报警级别" });
	dialog.controls = [ "divAlarmLevelEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (!/^(([\d]+(.[\d]+)+)*|[\w ]+)$/.test($('txt_VAR_NAME').value)) {
				jetsennet.alert("请输入正确格式的OID！");
				return;
			}
			if (checkThreshold() == false) {
				return;
			}
			var weekMask = "";
			var hourMask = "";
			var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
			if (weekdays.length == 0) {
				jetsennet.alert("请选择周几报警！");
				return;
			}
			if (parseInt(getBytesCount($("txt_LEVEL_DESC").value)) > 60) {
				jetsennet.alert("级别描述不能超过30个文字！");
				return;
			}
			var chkWeeks = document.getElementsByName("chkWeek");
			for ( var i = 0; i < chkWeeks.length; i++) {
				if (chkWeeks[i].checked) {
					weekMask += (i + 1).toString();
				} else {
					weekMask += "0";
				}
			}

			hourMask = getHourMask();
			if (!hourMask) {
				jetsennet.alert("起止时间（0-23）有误！");
				return;
			}
			var oAlarmLevel = { LEVEL_ID : levelId,
				ALARM_ID : $("hid_ALARM_ID").value,
				ALARM_LEVEL : getSelectedValue($("cbo_ALARM_LEVEL"))
				//, SUB_LEVEL: $("txt_SUB_LEVEL").value
				, CONDITION : getSelectedValue($("cbo_CONDITION")), THRESHOLD : $("txt_THRESHOLD").value, LEVEL_NAME : $("txt_LEVEL_NAME").value,
				LEVEL_DESC : $("txt_LEVEL_DESC").value, WEEK_MASK : weekMask, HOUR_MASK : hourMask,
				ACTION_IDS : getAllValues($("cbo_ACTION")).join(","), VAR_NAME : $("txt_VAR_NAME").value };

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadAlarmLevel($("hid_ALARM_ID").value);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ALARMLEVEL", JetsenWeb.Xml.serializer(oAlarmLevel, "BMP_ALARMLEVEL") ]);
		}
	};
	dialog.showDialog();
}
// 删除报警级别
function deleteAlarmLevel(levelId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadAlarmLevel($("hid_ALARM_ID").value);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_ALARMLEVEL", levelId ]);
		return true;
	});
}

// 加载报警级别列表
function loadAlarmLevel(alarmId) {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMLEVEL", "");
	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", alarmId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery, { KeyId : "", QueryTable : gQueryTable, Conditions : conditions });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		if (ruleType == 0) {
			$("divAlarmLevelList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel.xslt", ret.resultVal);
		} else {
			$("divAlarmLevelList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmtraplevel.xslt", ret.resultVal);
		}
		gAlarmLevelGridList.bind($("divAlarmLevelList"), $("tabAlarmLevel"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

// 查询可用的报警动作
function searchAction() {
	var areaElements = JetsenWeb.Form.getElements("divActionContent");
	JetsenWeb.Form.resetValue(areaElements);

	$("divActionList").innerHTML = "数据加载中...";
	var dialog = new JetsenWeb.UI.Window("view-action-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true,
		size : { width : 300, height : 400 }, title : "选择报警动作" });
	dialog.controls = [ "divActionContent" ];
	dialog.onsubmit = function() {
		var cboAction = $("cbo_ACTION");
		var actionIds = JetsenWeb.Form.getCheckedValues("chkAction");
		var length = actionIds.length;
		for ( var i = 0; i < length; i++) {
			cboAction.options.add(new Option(actions[actionIds[i]], actionIds[i]));
		}
		JetsenWeb.UI.Windows.close("view-action-win");
	};
	dialog.showDialog();
	actionInit();
}

// 初始化报警动作列表
function actionInit() {
	gActionCondition.SqlConditions = [];
	var actionIds = getAllValues($("cbo_ACTION")).join(",");
	if (actionIds != "") {
		gActionCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ACTION_ID", actionIds, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));
	}
	gActionSqlQuery.Conditions = gActionCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.cacheLevel = 2;
	ws.oncallback = function(ret) {
		actions = {};
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var actionInfo = records[i];
				actions[actionInfo["ACTION_ID"]] = actionInfo["ACTION_NAME"];
			}
		}
		$("divActionList").innerHTML = JetsenWeb.Xml.transformXML("xslt/simplealarmaction.xslt", ret.resultVal);
		gActionGridList.bind($("divActionList"), $("tabAction"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gActionSqlQuery.toXml() ]);
}

// 加载报警级别对应的报警动作
function loadActionByLevel(levelId) {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMACTION", "aa");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ACTION", "a", "a.ACTION_ID=aa.ACTION_ID", JetsenWeb.TableJoinType.Inner));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("LEVEL_ID", levelId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery, { KeyId : "", QueryTable : gQueryTable, Conditions : condition, ResultFields : "a.ACTION_ID,a.ACTION_NAME" });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var cboAction = $("cbo_ACTION");
		cboAction.length = 0;
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var actionInfo = records[i];
				cboAction.options.add(new Option(actionInfo["ACTION_NAME"], actionInfo["ACTION_ID"]));
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

// 检查阈值输入合法性
function checkThreshold() {
	var condition = $("cbo_CONDITION");
	if (condition.value == 'LT' || condition.value == 'LE' || condition.value == 'EQ' || condition.value == 'NOTEQ' || condition.value == 'GT'
			|| condition.value == 'GE') {
		if (!/^-?\d+$/.test($("txt_THRESHOLD").value)) {
			jetsennet.alert("阈值输入非法！只能为整数！");
			return false;
		} else {
			return true;
		}
	}
	if (condition.value == 'LK' || condition.value == 'UNLK') {
		if (!/^[\w ]+$/.test($("txt_THRESHOLD").value)) {
			jetsennet.alert("阈值输入非法！");
			return false;
		} else {
			return true;
		}
	}
	if (condition.value == 'EX') {
		if (!/^[\w ]+(,[\w ]+)*$/.test($("txt_THRESHOLD").value)) {
			jetsennet.alert("阈值输入非法！");
			return false;
		} else {
			return true;
		}
	}
	if (condition.value == 'IN') {
		if (!/^(-?\d+),(-?\d+)$/.test($("txt_THRESHOLD").value)) {
			jetsennet.alert("阈值输入非法！格式为xx,xx！xx均为整数！");
			return false;
		} else {
			return true;
		}
	}
	if (condition.value == 'NOTIN') {
		if (!/^(-?\d+),(-?\d+)$/.test($("txt_THRESHOLD").value)) {
			jetsennet.alert("阈值输入非法！格式为xx,xx！xx均为整数！");
			return false;
		} else {
			return true;
		}
	}
	return true;
}

function removeSelectedActions() {
	var rmActions = removeSelectedOptions($('cbo_ACTION'));
	if (rmActions.length == 0) {
		jetsennet.alert("请选择要删除的报警动作！");
	}
}

//当规则类型为Trap（值为1）时，检查次数和越限次数默认为1且不能修改。
function isTrap(select) {
	if (select.value == 1) {
		$('txt_CHECK_NUM').disabled = "disabled";
		$('txt_CHECK_NUM').value = 1;
		$('txt_OVER_NUM').disabled = "disabled";
		$('txt_OVER_NUM').value = 1;
	} else {
		$('txt_CHECK_NUM').disabled = "";
		$('txt_OVER_NUM').disabled = "";
	}
}