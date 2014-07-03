JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree" ]);

jetsennet.registerNamespace("jbmp.alarm");

jbmp.alarm.AlarmConfig = function(contDiv, title) {
	this.contDiv = contDiv;
	this.title = title;
	this.gridList = new JetsenWeb.UI.GridList();

	this.isBatch = false; // 是否为批量模式
	this.newLevelId = -1; // 新建的level的初始ID。新建的level的level_id为负数，已存在的level的level_id为正数
	this.callback = null;

	this.alarmId = -1; // 默认的报警规则ID
	this.alarmType = 0; // 0，一般；1，Trap
	this.alarmInfo = null; // 报警规则
	this.levelMap = {}; // 一般报警级别Map
	this.trapLevel = {}; // Trap报警级别Map
	this.actionMap = {}; // 报警动作
	this.curActions = []; // 当前报警动作
	this.curLevelId = 0;
}

jbmp.alarm.AlarmConfig.prototype.init = function() {
	var callback = function(str) {
		var gAlarmDiv = document.createElement("div");
		gAlarmDiv.setAttribute("style", "display: none;");
		gAlarmDiv.innerHTML = str;
		document.body.appendChild(gAlarmDiv);
	}
	this.loadTempHTML("../../jbmp/jbmpsystemweb/alarmConfigFrag.htm", callback)
}

jbmp.alarm.AlarmConfig.prototype.loadTempHTML = function(uri, callback) {
	var request = new jetsennet.HttpRequest(callback, uri, 1);
	request.async = false;
	request.onerror = function(ex) {
		throw ex;
	};
	request.send();
	request.clear();
	request = null;
}

/**
 * 编辑报警规则
 * 
 * @param alarmId
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.showConfigDialog = function(alarmId, callback) {
	this.clear();
	if (alarmId) {
		this.alarmId = alarmId;
	}
	this.callback = callback;
	var dialog = new JetsenWeb.UI.Window("general-alarm-config");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : false, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 722, height : 486 }, title : this.title });
	dialog.controls = [ this.contDiv ];
	this.initAlarmInfo();
	this.initLevelList();
	dialog.showDialog();
}

/**
 * 批量编辑报警规则
 * 
 * @param alarmId
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.showBatchConfigDialog = function(alarmId, callback) {
	this.clear();
	this.isBatch = true;
	if (alarmId) {
		this.alarmId = alarmId;
	}
	this.callback = callback;
	this.alarmInfo = {};
	var dialog = new JetsenWeb.UI.Window("general-alarm-config");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : false, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 722, height : 486 }, title : this.title });
	dialog.controls = [ this.contDiv ];
	this.initAlarmInfo();
	this.initLevelList();
	dialog.showDialog();
}

/**
 * 加载报警规则信息
 * 
 * @param alarmId
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.initAlarmInfo = function() {
	var that = this;
	var areaElements = JetsenWeb.Form.getElements('divGAlarmRuleDetail');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	if (this.isBatch) {
		$("txtGCheckNum").value = 1;
		$("txtGOverNum").value = 1;
		$("areaGAlarmDesc").value = "无需输入";
		$("areaGAlarmDesc").disabled = true;
		$("areaGAlarmDesc").readOnly = true;
		$("spanGAlarmName").style.display = "none";
	} else {
		$("areaGAlarmDesc").value = "";
		$("areaGAlarmDesc").disabled = false;
		$("areaGAlarmDesc").readOnly = false;
		$("spanGAlarmName").style.display = "";
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", this.alarmId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

		var sqlQuery = new JetsenWeb.SqlQuery();
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ALARM_ID", PageInfo : null,
			QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARM" }) });
		sqlQuery.Conditions = condition;

		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			that.alarmInfo = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
			var checkSpan = that.alarmInfo["CHECK_SPAN"];
			that.alarmType = valueOf(that.alarmInfo, "CHECK_SPAN", "0")

			$("txtGAlarmName").value = valueOf(that.alarmInfo, "ALARM_NAME", "");
			$("areaGAlarmDesc").value = valueOf(that.alarmInfo, "ALARM_DESC", "");
			showRemindWordCount($("areaGAlarmDesc").value, $('areaGAlarmDescRemindWord'), "30");

			$("txtGCheckNum").value = valueOf(that.alarmInfo, "CHECK_NUM", "");
			$("txtGOverNum").value = valueOf(that.alarmInfo, "OVER_NUM", "");

			setSelectedValue($("selGAlarmType"), that.alarmType);
			
			var isValidAlarm = valueOf(that.alarmInfo, "IS_VALID", "1");
			if(isValidAlarm == "0") {
				$("cbGAlarmValid").checked = true;
			} else {
				$("cbGAlarmValid").checked = false;
			}
			that.isTrap();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	}
}

/**
 * 加载报警级别列表
 * 
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.initLevelList = function() {
	if (!this.isBatch) {
		var that = this;
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMLEVEL", "");
		var conditions = new JetsenWeb.SqlConditionCollection();
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", this.alarmId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		JetsenWeb.extend(gSqlQuery, { KeyId : "", QueryTable : gQueryTable, Conditions : conditions });
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			if (that.alarmType == 0) {
				$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_normal.xslt", ret.resultVal);
			} else {
				$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_trap.xslt", ret.resultVal);
			}
			that.gridList.bind($("divGAlarmLevelLst"), $("tabAlarmLevel"));
			var levelLst = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if (levelLst != null) {
				for ( var i = 0; i < levelLst.length; i++) {
					var level = levelLst[i];
					var levelId = level["LEVEL_ID"];
					if (that.alarmType == 0) {
						that.levelMap[levelId] = level;
					} else {
						that.trapLevel[levelId] = level;
					}
					// 加载报警动作信息
					that.loadActionByLevel(levelId);
				}
			}
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	} else {
		if (this.alarmType == 0) {
			$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_normal.xslt", "<RecordSet></RecordSet>");
		} else {
			$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_trap.xslt", "<RecordSet></RecordSet>");
		}
		this.gridList.bind($("divGAlarmLevelLst"), $("tabAlarmLevel"));
	}
}

/**
 * 新建报警级别
 */
jbmp.alarm.AlarmConfig.prototype.newLevel = function() {
	this.curActions = [];
	var that = this;
	var areaElements = JetsenWeb.Form.getElements("divGAlarmLevelEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	//Trap类型要多填一个OID
	if (this.alarmType == 1) {
		$('tr_VAR_NAME').style.display = "";
		$('txt_VAR_NAME').setAttribute("validatetype", "NotEmpty");
	} else {
		$('tr_VAR_NAME').style.display = "none";
		$('txt_VAR_NAME').setAttribute("validatetype", "");
	}
	// 勾选中所有星期
	var chkWeeks = document.getElementsByName("chkWeek");
	for ( var i = 0; i < chkWeeks.length; i++) {
		chkWeeks[i].checked = true;
	}
	showRemindWordCount($("txt_LEVEL_DESC").value, $('alarmLevelRemindWord'), "30");
	$("cbo_ACTION").length = 0;
	this.curLevelId = this.newLevelId--;

	var dialog = new JetsenWeb.UI.Window("general-level-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 600, height : 500 }, title : "新建报警级别" });
	dialog.controls = [ "divGAlarmLevelEdit" ];
	dialog.onsubmit = function() {
		if (that.validLevel()) {
			var weekMask = "";
			var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
			var chkWeeks = document.getElementsByName("chkWeek");
			for ( var i = 0; i < chkWeeks.length; i++) {
				if (chkWeeks[i].checked) {
					weekMask += (i + 1).toString();
				} else {
					weekMask += "0";
				}
			}

			var hourMask = "";
			hourMask = that.getHourMask();
			
			var levelId = that.curLevelId;

			var objAlarmLevel = { 
					LEVEL_ID : levelId, 
					ALARM_ID : that.alarmId, 
					ALARM_LEVEL : getSelectedValue($("cbo_ALARM_LEVEL")),
					SUB_LEVEL : 0, 
					CONDITION : getSelectedValue($("cbo_gCONDITION")), 
					THRESHOLD : $("txt_THRESHOLD").value,
					LEVEL_NAME : $("txt_LEVEL_NAME").value, 
					LEVEL_DESC : $("txt_LEVEL_DESC").value, 
					WEEK_MASK : weekMask, 
					HOUR_MASK : hourMask,
					ACTION_IDS : getAllValues($("cbo_ACTION")).join(","), 
					VAR_NAME : $("txt_VAR_NAME").value };
			if (that.alarmType == 0) {
				that.levelMap[that.curLevelId] = objAlarmLevel;
			} else {
				that.trapLevel[that.curLevelId] = objAlarmLevel;
			}
			that.actionMap[levelId] = that.curActions;
			that.renderLevelLst();

			JetsenWeb.UI.Windows.close("general-level-win");
		}
	};
	dialog.showDialog();
}

/**
 * 删除报警级别
 */
jbmp.alarm.AlarmConfig.prototype.delLevel = function(levelId) {
	if (this.alarmType == 0) {
		delete this.levelMap[levelId];
	} else {
		delete this.trapLevel[levelId];
	}
	this.renderLevelLst();
}

/**
 * 编辑报警级别
 */
jbmp.alarm.AlarmConfig.prototype.editLevel = function(levelId) {
	this.curActions = this.actionMap[levelId];
	var that = this;
	var areaElements = JetsenWeb.Form.getElements("divGAlarmLevelEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	//Trap类型要多填一个OID
	if (this.alarmType == 1) {
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
	showRemindWordCount($("txt_LEVEL_DESC").value, $('alarmLevelRemindWord'), "30");
	this.curLevelId = levelId;

	$("cbo_ACTION").length = 0;
	this.setLevelInfo(levelId);

	var dialog = new JetsenWeb.UI.Window("gen-edit-level-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 600, height : 500 }, title : "编辑报警级别" });
	dialog.controls = [ "divGAlarmLevelEdit" ];
	dialog.onsubmit = function() {
		if (that.validLevel()) {
			var weekMask = "";
			var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
			var chkWeeks = document.getElementsByName("chkWeek");
			for ( var i = 0; i < chkWeeks.length; i++) {
				if (chkWeeks[i].checked) {
					weekMask += (i + 1).toString();
				} else {
					weekMask += "0";
				}
			}

			var hourMask = "";
			hourMask = that.getHourMask();

			var objAlarmLevel = { 
					LEVEL_ID : that.curLevelId, 
					ALARM_ID : that.alarmId, 
					ALARM_LEVEL : getSelectedValue($("cbo_ALARM_LEVEL")),
					SUB_LEVEL : 0, 
					CONDITION : getSelectedValue($("cbo_gCONDITION")), 
					THRESHOLD : $("txt_THRESHOLD").value,
					LEVEL_NAME : $("txt_LEVEL_NAME").value, 
					LEVEL_DESC : $("txt_LEVEL_DESC").value, 
					WEEK_MASK : weekMask, HOUR_MASK : hourMask,
					ACTION_IDS : getAllValues($("cbo_ACTION")).join(","), 
					VAR_NAME : $("txt_VAR_NAME").value };
			if (that.alarmType == 0) {
				that.levelMap[that.curLevelId] = objAlarmLevel;
			} else {
				that.trapLevel[that.curLevelId] = objAlarmLevel;
			}
			that.renderLevelLst();

			JetsenWeb.UI.Windows.close("gen-edit-level-win");
		}
	};
	dialog.showDialog();
}

/**
 * 渲染level列表
 */
jbmp.alarm.AlarmConfig.prototype.renderLevelLst = function() {
	var levelLength = 0;
	var levelS = "";
	levelS += "<RecordSet>";
	var levels = null;
	if (this.alarmType == 0) {
		levels = this.levelMap;
	} else {
		levels = this.trapLevel;
	}
	for ( var key in levels) {
		var level = levels[key];
		if (typeof (level) != "function") {
			levelS += JetsenWeb.Xml.serializer(level, "Record");
			levelLength++;
		}
	}
	levelS += "<Record1>";
	levelS += "<TotalCount>";
	levelS += levelLength;
	levelS += "</TotalCount>";
	levelS += "</Record1>";
	levelS += "</RecordSet>";

	if (this.alarmType == 0) {
		$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_normal.xslt", levelS);
	} else {
		$("divGAlarmLevelLst").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmlevel_trap.xslt", levelS);
	}
	this.gridList.bind($("divGAlarmLevelLst"), $("tabAlarmLevel"));
}

/**
 * 清除上次编辑的痕迹
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.clear = function() {
	this.alarmId = -1;
	this.alarmType = 0;
	this.alarmInfo = null;
	this.levelMap = {};
	this.trapLevel = {};
	this.curLevelId = 0;
	this.isBatch = false;
	this.callback = null;
	this.curActions = [];
}

/**
 * 提交
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.submit = function() {
	var that = this;
	var areaElements = JetsenWeb.Form.getElements("divGAlarmRuleDetail");
	if (JetsenWeb.Form.Validate(areaElements, true)) {
		var xml = this.genResultXml();
		var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			that.close();
			if (that.callback) {
				that.callback();
			}
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		if (this.isBatch) {
			ws.call("batchSetAlarmConfig", [ xml, this.alarmId ]);
		} else {
			ws.call("setAlarmConfig", [ xml ]);
		}
	}
}

jbmp.alarm.AlarmConfig.prototype.close = function() {
	JetsenWeb.UI.Windows.close("general-alarm-config");
}

/**
 * 生成提交给后台进行处理的报警规则xml
 */
jbmp.alarm.AlarmConfig.prototype.genResultXml = function() {
	var alarmS = "";
	alarmS += "<AlarmConfig>";
	alarmS += "<Alarm>";
	this.alarmInfo["CHECK_SPAN"] = this.alarmType;
	this.alarmInfo["CHECK_NUM"] = $("txtGCheckNum").value;
	this.alarmInfo["OVER_NUM"] = $("txtGOverNum").value;
	if($("cbGAlarmValid").checked) {
		this.alarmInfo["IS_VALID"] = "0";
	} else {
		this.alarmInfo["IS_VALID"] = "1";
	}
	
	this.alarmInfo["ALARM_NAME"] = $("txtGAlarmName").value;
	if (!this.isBatch) {
		this.alarmInfo["ALARM_DESC"] = $("areaGAlarmDesc").value;
	}
	
	alarmS += JetsenWeb.Xml.serializer(this.alarmInfo, "Record");
	alarmS += "</Alarm>";
	alarmS += "<Levels>";
	var levels = null;
	if (this.alarmType == 0) {
		levels = this.levelMap;
	} else {
		levels = this.trapLevel;
	}
	for ( var key in levels) {
		var level = levels[key];
		if (typeof (level) != "function") {
			var actionList = [];
			var actions = this.actionMap[key];
			var length = actions.length; 
			for(var i = 0; i < length; i++) {
				actionList.push(actions[i]["id"]);
			}
			level["ACTION_ID"] = actionList.join(",");
			alarmS += JetsenWeb.Xml.serializer(level, "Record");
		}
	}
	alarmS += "</Levels>";
	alarmS += "</AlarmConfig>";
	return alarmS;
}

/**
 * 判断level的数据是否正确
 */
jbmp.alarm.AlarmConfig.prototype.validLevel = function() {
	var areaElements = JetsenWeb.Form.getElements("divGAlarmLevelEdit");

	if (JetsenWeb.Form.Validate(areaElements, true)) {

		if (!/^(([\d]+(.[\d]+)+)*|[\w ]+)$/.test($('txt_VAR_NAME').value)) {
			jetsennet.alert("请输入正确格式的OID！");
			return false;
		}
		if (this.checkThreshold() == false) {
			return false;
		}
		var weekMask = "";
		var hourMask = "";
		var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
		if (weekdays.length == 0) {
			jetsennet.alert("请选择周几报警！");
			return false;
		}

		var chkWeeks = document.getElementsByName("chkWeek");
		for ( var i = 0; i < chkWeeks.length; i++) {
			if (chkWeeks[i].checked) {
				weekMask += (i + 1).toString();
			} else {
				weekMask += "0";
			}
		}

		hourMask = this.getHourMask();
		if (!hourMask) {
			jetsennet.alert("起止时间（0-23）有误！");
			return false;
		}

		if (parseInt(getBytesCount($("txt_LEVEL_DESC").value)) > 60) {
			jetsennet.alert("级别描述不能超过30个文字！");
			return false;
		}
		var alarmId = $("hid_GALARM_ID").value;
		var objAlarmLevel = { ALARM_ID : alarmId, ALARM_LEVEL : getSelectedValue($("cbo_ALARM_LEVEL")),
			CONDITION : getSelectedValue($("cbo_gCONDITION")), THRESHOLD : $("txt_THRESHOLD").value, LEVEL_NAME : $("txt_LEVEL_NAME").value,
			LEVEL_DESC : $("txt_LEVEL_DESC").value, WEEK_MASK : weekMask, HOUR_MASK : hourMask, ACTION_IDS : getAllValues($("cbo_ACTION")).join(","),
			VAR_NAME : $("txt_VAR_NAME").value };

		return true;
	} else {
		return false;
	}
}

/**
 * 获取时间掩码
 */
jbmp.alarm.AlarmConfig.prototype.getHourMask = function() {
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

/**
 * 检查阀值的合法性
 * @return
 */
jbmp.alarm.AlarmConfig.prototype.checkThreshold = function() {
	var condition = $("cbo_gCONDITION");
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

/**
 * 设置level信息
 */
jbmp.alarm.AlarmConfig.prototype.setLevelInfo = function(levelId) {
	var objAlarmLevel;
	if(this.alarmType == "0" || this.alarmType == 0)
		objAlarmLevel = this.levelMap[levelId];
	else if(this.alarmType == "1" || this.alarmType == 1)
		objAlarmLevel = this.trapLevel[levelId];
		
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
	$("hid_GALARM_ID").value = valueOf(objAlarmLevel, "ALARM_ID", "");
	setSelectedValue($("cbo_ALARM_LEVEL"), valueOf(objAlarmLevel, "ALARM_LEVEL", ""));
	setSelectedValue($("cbo_gCONDITION"), valueOf(objAlarmLevel, "CONDITION", ""));
	$("txt_THRESHOLD").value = valueOf(objAlarmLevel, "THRESHOLD", "");
	$("txt_LEVEL_NAME").value = valueOf(objAlarmLevel, "LEVEL_NAME", "");
	$("txt_LEVEL_DESC").value = valueOf(objAlarmLevel, "LEVEL_DESC", "");
	$("txt_VAR_NAME").value = valueOf(objAlarmLevel, "VAR_NAME", "");
	
	showRemindWordCount($("txt_LEVEL_DESC").value, $('alarmLevelRemindWord'), "30");
	
	if(this.curActions) {
		var cboAction = $("cbo_ACTION");
		cboAction.length = 0;
		
		var length = this.curActions.length;
		for(var i = 0; i < length; i++) {
			var action = this.curActions[i];
			cboAction.options.add(new Option(action["name"], action["id"]));
		}
	}
}
jbmp.alarm.AlarmConfig.prototype.changeRuleType = function() {
	this.alarmType = getSelectedValue($("selGAlarmType"));
	this.isTrap();
	this.renderLevelLst();
}

jbmp.alarm.AlarmConfig.prototype.isTrap = function() {
	if (this.alarmType == 1) {
		$('txtGCheckNum').disabled = "disabled";
		$('txtGCheckNum').value = 1;
		$('txtGOverNum').disabled = "disabled";
		$('txtGOverNum').value = 1;
	} else {
		$('txtGCheckNum').disabled = "";
		$('txtGOverNum').disabled = "";
	}
}

//查询可用的报警动作
jbmp.alarm.AlarmConfig.prototype.searchAction = function() {
	var that = this;
	var areaElements = JetsenWeb.Form.getElements("divGActionContent");
	JetsenWeb.Form.resetValue(areaElements);

	$("divActionList").innerHTML = "数据加载中...";
	var dialog = new JetsenWeb.UI.Window("view-action-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true,
		size : { width : 300, height : 400 }, title : "选择报警动作" });
	dialog.controls = [ "divGActionContent" ];
	dialog.onsubmit = function() {
		var cboAction = $("cbo_ACTION");
		var actionIds = JetsenWeb.Form.getCheckedValues("chkAction");
		var length = actionIds.length;
		for ( var i = 0; i < length; i++) {
			cboAction.options.add(new Option(actions[actionIds[i]], actionIds[i]));
		}
		that.refreshCurActions();
		JetsenWeb.UI.Windows.close("view-action-win");
	};
	dialog.showDialog();
	this.actionInit();
}

//初始化报警动作列表
jbmp.alarm.AlarmConfig.prototype.actionInit = function() {
	var gActionGridList = new JetsenWeb.UI.GridList();
	var gActionSqlQuery = new JetsenWeb.SqlQuery();
	var gActionCondition = new JetsenWeb.SqlConditionCollection();
	var gActionQueryTable = JetsenWeb.createQueryTable("BMP_ACTION", "");
	JetsenWeb.extend(gActionSqlQuery, { KeyId : "", QueryTable : gActionQueryTable, ResultFields : "ACTION_ID,ACTION_NAME" });
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

jbmp.alarm.AlarmConfig.prototype.removeSelectedActions = function() {
	var rmActions = [];
	var select = $('cbo_ACTION');
	var length = select.length;
	var options = select.options;
	for (var i = length - 1; i >= 0; i--)
	{
		if (options[i].selected)
		{
			rmActions.push(options[i]);
			select.remove(i);
		}
	}
	if (rmActions.length == 0) {
		jetsennet.alert("请选择要删除的报警动作！");
	}
	this.refreshCurActions();
}

jbmp.alarm.AlarmConfig.prototype.refreshCurActions = function() {
	this.curActions.length = 0;
	var select = $('cbo_ACTION');
	var options = select.options;
	var length = select.length;
	for (var i = 0; i < length; i++)
	{
		var id = options[i].value;
		var name = options[i].text;
		this.curActions.push({"id" : id, "name" : name});
	}
}

//加载报警级别对应的报警动作
jbmp.alarm.AlarmConfig.prototype.loadActionByLevel = function(levelId) {
	var that = this;
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
		that.actionMap[levelId] = [];
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var actionInfo = records[i];
				var actionId = actionInfo["ACTION_ID"];
				var actionName = actionInfo["ACTION_NAME"];
				that.actionMap[levelId].push({"id" : actionId, "name" : actionName});
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}