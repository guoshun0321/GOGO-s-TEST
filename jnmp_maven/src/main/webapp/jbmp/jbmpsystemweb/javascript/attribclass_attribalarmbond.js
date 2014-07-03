//获取配置信息和表格类型的属性分组=====================================================================================
function getAttribClass(classId, noEmpty) {
	var control = $('cbo_CLASS');
	control.options.length = 0;
	if (!noEmpty)
		control.options[0] = new Option("请选择", "");

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("A.CLASS_LEVEL", "101,106", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In,
			JetsenWeb.SqlParamType.Numeric, true));
	if (classId != "") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("B.PARENT_ID", classId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "A");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "B", "A.CLASS_ID=B.CLASS_ID", JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "CLASS_ID", PageInfo : null, QueryTable : queryTable,
		ResultFields : "A.CLASS_ID,A.CLASS_NAME" });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(resultXml) {
		var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal, "Record");
		if (typeObjs != null) {
			for ( var i = 0; i < typeObjs.length; i++) {
				var option = new Option(valueOf(typeObjs[i], "CLASS_NAME", ""), valueOf(typeObjs[i], "CLASS_ID", ""));
				control.options[control.options.length] = option;
			}
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex + "！");
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

// 解除关联
function bindNoAlarm(type) {

	var objattrId2alarm = null;
	if (type == "102" || type == "103" || type == "104" || type == "105" || type == "107") {
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("chkAllObject");
	}

	if (objattrId2alarm == null || objattrId2alarm.length == 0) {
		jetsennet.alert("请选择要解除报警关联的属性！");
		return;
	}
	var isNullItem = 0;
	for ( var i = 0; i < objattrId2alarm.length; i++) {
		var e = $("hiddenObjAttr2Alarm" + objattrId2alarm[i]);
		if (e.value == 0) {
			isNullItem += 1;
		}
	}

	if (isNullItem == objattrId2alarm.length) {
		jetsennet.alert("所选项没有关联报警！");
		return;
	}

	jetsennet.confirm("确定删除报警关联？", function() {
		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			searchAttrs();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex + "！");
		};
		ws.call("bmpUpdateAttribAlarm", [ 0, objattrId2alarm.join(",") ]);

		return true;
	});
}

// 关联告警对话框
function attrib2alarm(type) {
	var objattrId2alarm = null;
	if (type == "102" || type == "103" || type == "104" || type == "105" || type == "107") {
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("chkAllObject");
	}

	if (objattrId2alarm == null || objattrId2alarm.length == 0) {
		jetsennet.alert("请选择要关联报警的属性！");
		return;
	}
	loadAlarm(type);
	var areaElements = JetsenWeb.Form.getElements("divObjAttrib2Alarm");

	JetsenWeb.Form.checkAllItems('chkAllAlarms', false);
	$("lblAlarmDesc").innerText = "";
	$("lblCheckNum").innerText = "";
	$("lblOverNum").innerText = "";
	//	$("lblCheckSpan").innerText = "";
	loadAlarmLevel("");

	var dialog = new JetsenWeb.UI.Window("edit-divObjAttrib2Alarm-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true,
		size : { width : 700, height : 350 }, title : "选择报警规则" });
	dialog.controls = [ "divObjAttrib2Alarm" ];
	dialog.onsubmit = function() {
		var curAlarmId = getSingleCheckedValues("chkAllAlarms");
		if (curAlarmId == null) {
			jetsennet.alert("请选择报警！");
			return;
		}
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				searchAttrs();
				JetsenWeb.UI.Windows.close("edit-divObjAttrib2Alarm-win");
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex + "！");
			};
			ws.call("bmpUpdateAttribAlarm", [ curAlarmId, objattrId2alarm.join(",") ]);
		} else {
			JetsenWeb.UI.Windows.close("edit-divObjAttrib2Alarm-win");
		}

	};

	dialog.showDialog();
}

// 加载告警
function loadAlarm(type) {

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.ALARM_ID", "1, 10", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn,
			JetsenWeb.SqlParamType.Numeric));
	if (type == '103') {
		var condition2 = new JetsenWeb.SqlCondition();
		condition2.SqlLogicType = jetsennet.SqlLogicType.And;
		condition2.SqlConditions = [];
		condition2.SqlConditions.push(JetsenWeb.SqlCondition.create("t.CHECK_SPAN", 1, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.NotEqual,
				JetsenWeb.SqlParamType.Numeric));
		condition2.SqlConditions.push(JetsenWeb.SqlCondition.create("t.CHECK_SPAN", "", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.IsNull,
				JetsenWeb.SqlParamType.Numeric));
		condition.SqlConditions.push(condition2);
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARM", "t");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ALARM_ID", PageInfo : null, QueryTable : queryTable, ResultFields : "t.*" });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(ret) {
		$("divAlarmList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlist.xslt", ret.resultVal);
		var gAlarmListGrid = new JetsenWeb.UI.GridList();
		gAlarmListGrid.bind($("divAlarmList"), $("tabAlarmList"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex + "！");
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

// 加载告警级别列表
function loadAlarmLevel(alarmId) {
	if (alarmId == "") {
		return;
	}
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMLEVEL", "l");

	var gAlarmLevelCondition = new JetsenWeb.SqlConditionCollection();
	gAlarmLevelCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("l.ALARM_ID", alarmId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery, { KeyId : "", QueryTable : gQueryTable, Conditions : gAlarmLevelCondition, ResultFields : "l.*" });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var gAlarmLevelGridList2 = new JetsenWeb.UI.GridList();
		$("divAlarmLevel").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevelview.xsl", ret.resultVal);
		gAlarmLevelGridList2.bind($("divAlarmLevel"), $("tabAlarmLevel"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex + "！");
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	gAlarmLevelCondition.SqlConditions = [];
}

function selectAlarm(alarmId, desc, checkNum, overNum, checkSpan) {

	var objs = document.getElementsByName("chkAllAlarms");
	var curckb = null;
	if (objs != null && objs.length != 0) {
		for ( var i = 0; i < objs.length; i++) {
			if (objs[i].value == alarmId) {
				curckb = objs[i];
				break;
			}
		}
	}
	if (curckb != null) {
		var cha = curckb.checked;
		JetsenWeb.Form.checkAllItems('chkAllAlarms', false);
		curckb.checked = !cha;
	}

	$("lblAlarmDesc").innerText = desc;
	$("lblCheckNum").innerText = checkNum;
	$("lblOverNum").innerText = overNum;
	//	$("lblCheckSpan").innerText = checkSpan;
	loadAlarmLevel(alarmId);
}

// 报警设置
function showBatchAlarmConfig() {
	var type = curClassType;
	var objattrId2alarm = null;
	if (type == "102" || type == "103" || type == "104" || type == "105" || type == "107") {
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("chkAllObject");
	}

	if (objattrId2alarm == null || objattrId2alarm.length == 0) {
		jetsennet.alert("请选择要解除报警关联的属性！");
		return;
	}

	var alarmIds = "";
	for ( var i = 0; i < objattrId2alarm.length; i++) {
		var e = $("hiddenObjAttr2Alarm" + objattrId2alarm[i]);
		var eValue = e.value;
		if (eValue != "") {
			alarmIds += eValue;
			alarmIds += ",";
		}
	}
	if (alarmIds.length > 0) {
		alarmIds = alarmIds.substring(0, alarmIds.length - 1);
	}

	var callback = function() {
		searchAttrs();
	}

	if (alarmIds == "") {
		alert("指标无对应报警规则，请联系管理员！");
	} else if (alarmIds.indexOf(",") > 0) {
		generalAlarmConfig.showBatchConfigDialog(alarmIds, callback);
	} else {
		generalAlarmConfig.showConfigDialog(alarmIds, callback);
	}
}
