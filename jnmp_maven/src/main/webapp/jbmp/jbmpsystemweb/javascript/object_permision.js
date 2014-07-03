/**
	针对四川项目进行权限修改时，添加的js
**/
// 已经选择的组
var selGroups = {};

var enableGroup = {};

function showGroupSet(isBatch) {

	// 是否为批量修改模式
	enableGroup = {};
	setBatch(isBatch);

	var groupSelect = $("showGroupOption");

	var dialog = new JetsenWeb.UI.Window("set-group-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 801, height : 350 }, title : "组设置" });
	dialog.controls = [ "divGroupShow" ];
	dialog.onsubmit = function() {
		groupSelect.options.length = 0;
		for ( var key in selGroups) {
			if (typeof (selGroups[key]) != "function") {
				var tempValue = selGroups[key];
				var groupOption = new Option(tempValue, key);
				groupSelect.options.add(groupOption);
			}
		}
		JetsenWeb.UI.Windows.close("set-group-win");
	}
	dialog.showDialog();

	// 0,1,3,4
	fillGroupSet(1);
	fillGroupSet(0);
	fillGroupSet(4);
	fillGroupSet(3);

	selGroups = {};
	var len = groupSelect.options.length;
	for ( var i = 0; i < len; i++) {
		var groupOpValue = groupSelect.options[i].value;
		var groupOpName = groupSelect.options[i].text;
		selGroups[groupOpValue] = groupOpName;
		$("cbGroup" + groupOpValue).checked = true;
	}
}

/**
 * 设置是否批量处理
 * 
 * @param isBatch
 * @return
 */
function setBatch(isBatch) {
	if (!isBatch) {
		isBatch = false;
	}
	if (isBatch) {
		$("cbG1").style.display = "";
		$("cbG0").style.display = "";
		$("cbG4").style.display = "";
		$("cbG3").style.display = "";
		$("cbG1").checked = true;
		$("cbG0").checked = true;
		$("cbG4").checked = true;
		$("cbG3").checked = true;
		$("spanG1").style.display = "";
		$("spanG0").style.display = "";
		$("spanG4").style.display = "";
		$("spanG3").style.display = "";
		enableGroup[1] = true;
		enableGroup[0] = true;
		enableGroup[4] = true;
		enableGroup[3] = true;
	} else {
		$("cbG1").style.display = "none";
		$("cbG0").style.display = "none";
		$("cbG4").style.display = "none";
		$("cbG3").style.display = "none";
		$("spanG1").style.display = "none";
		$("spanG0").style.display = "none";
		$("spanG4").style.display = "none";
		$("spanG3").style.display = "none";
	}
}

function fillGroupSet(groupType) {
	var userId = jetsennet.Application.userInfo.UserId;

	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { KeyId : "GROUP_ID", QueryTable : queryTable, ResultFields : "GROUP_ID,GROUP_TYPE,GROUP_NAME" });

	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", groupType, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	sqlQuery.Conditions = conditions;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(sResult) {
		renderGroupSet(sResult.resultVal, groupType)
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

var sysGridList = new JetsenWeb.UI.GridList("groupSys-grid");
var normalGridList = new JetsenWeb.UI.GridList("groupNormal-grid");
var netGridList = new JetsenWeb.UI.GridList("groupNet-grid");
var collGridList = new JetsenWeb.UI.GridList("groupColl-grid");

/*
 *显示表格
 */
function renderGroupSet(xml, groupType) {

	var groupDiv = "divGroupShowCont" + groupType;
	$(groupDiv).innerHTML = "";

	var gridList = null;
	if (groupType == 1) {
		gridList = sysGridList;
	} else if (groupType == 0) {
		gridList = normalGridList;
	} else if (groupType == 4) {
		gridList = netGridList;
	} else if (groupType == 3) {
		gridList = collGridList;
	}

	gridList.columns = [ { index : 0, fieldName : "GROUP_ID, GROUP_NAME, GROUP_TYPE", width : 30, name : "" },
			{ index : 1, fieldName : "GROUP_NAME", width : 145, name : "组名称" } ];

	gridList.columns[0].format = function(val, vals) {
		if (isAdminAccount) {
			val = "<input type=\"checkbox\" id=\"cbGroup" + vals[0] + "\" onclick=\"selGroup(" + vals[0] + ",\'" + vals[1] + "\'," + vals[2] + ")\">";
		} else {
			val = "<input type=\"checkbox\" disabled=\"true\" id=\"cbGroup" + vals[0] + "\">";
		}
		return val;
	}

	gridList.idField = "GROUP_ID";
	gridList.dataSource = xml;
	gridList.render(groupDiv);
	gridList.colorSelectedRows();
}

function selGroup(groupId, groupName, groupType) {
	var selGroup = $("cbGroup" + groupId);
	var selDiv = $("divGroupShowCont" + groupType);
	if (selGroup.checked) {
		if (groupType == 1 || groupType == 3) {
			var tags = $(selDiv).getElementsByTagName("input");
			for ( var i = 0; i < tags.length; i++) {
				tags[i].checked = false;
				var tempId = tags[i].id.substring(7);
				delete selGroups[tempId];
			}
			selGroup.checked = true;
			selGroups[groupId] = groupName;
		} else {
			selGroups[groupId] = groupName;
		}
	} else {
		delete selGroups[groupId];
	}
}

/**
 * 批量设置
 * @return
 */
function batchSet() {

	// 是否为批量修改模式
	enableGroup = {};
	setBatch(true);

	var checkObjIds;
	var ObjIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
	var ObjInGroupIds = JetsenWeb.Form.getCheckedValues("chkAllGroupObject");
	if (ObjIds.length == 0 && ObjInGroupIds == 0) {
		jetsennet.alert("请选择要操作的对象！");
		return;
	}
	if (ObjIds.length > 0) {
		checkObjIds = ObjIds;
	} else {
		checkObjIds = ObjInGroupIds;
	}

	var groupSelect = $("showGroupOption");

	var dialog = new JetsenWeb.UI.Window("batch-group-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 801, height : 350 }, title : "组设置" });
	dialog.controls = [ "divGroupShow" ];
	dialog.onsubmit = function() {
		var reqS = genGroupBatchSetS();
		reqS = checkObjIds.join(",") + ";" + reqS;
//		alert(reqS);

		var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(sResult) {
			jetsennet.alert("组设置成功！");
			JetsenWeb.UI.Windows.close("batch-group-win");

		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("batchSetGroup", [ reqS ]);

		refreshTreeGroup();

		JetsenWeb.UI.Windows.close("set-group-win");
	}
	dialog.showDialog();

	// 0,1,3,4
	fillGroupSet(1);
	fillGroupSet(0);
	fillGroupSet(4);
	fillGroupSet(3);

	selGroups = {};
}

/**
 * 组设置——是否生效操作
 */
function enableGroupSet(type) {
	if ($("cbG" + type).checked) {
		enableGroup[type] = true;
		var inputs = $("divGroupShowCont" + type).getElementsByTagName("input");
		for ( var i = 0; i < inputs.length; i++) {
			var input = inputs[i];
			if (input.checked) {
				var tempId = input.id.substring(7);
				selGroups[tempId] = tempId;
			}
			input.disabled = false;
		}
	} else {
		enableGroup[type] = false;
		var inputs = $("divGroupShowCont" + type).getElementsByTagName("input");
		for ( var i = 0; i < inputs.length; i++) {
			var input = inputs[i];
			if (input.checked) {
				var tempId = input.id.substring(7);
				delete selGroups[tempId];
			}
			input.disabled = true;
		}
	}
}

function genGroupBatchSetS() {
	var typeStr = "";
	var groupStr = "";
	var typeArray = [ 1, 0, 3, 4 ];
	for ( var j = 0; j < typeArray.length; j++) {
		var type = typeArray[j];
		if (enableGroup[type]) {
			typeStr += type;
			typeStr += ",";
			var inputs = $("divGroupShowCont" + type).getElementsByTagName("input");
			for ( var i = 0; i < inputs.length; i++) {
				var input = inputs[i];
				if (input.checked) {
					var tempId = input.id.substring(7);
					groupStr += tempId;
					groupStr += ",";
				}
			}
		}
	}
	if (typeStr.length > 0) {
		typeStr = typeStr.substring(0, typeStr.length - 1);
	}
	if (groupStr.length > 0) {
		groupStr = groupStr.substring(0, groupStr.length - 1);
	}
	return typeStr + ";" + groupStr;
}

/**
 * 更改底部button的可视性
 * 
 * @param state1
 * @param state2
 * @param state3
 * @param state4
 * @param state5
 * @param state6
 * @return
 */
function changeBottomButton(state1, state2, state3, state4, state5, state6) {
	$("deleteObjectMany").style.display = state1;
	$("toMonitorObjAttr").style.display = state2;
	$("addObjectTo").style.display = state3;
	$("groupDelete").style.display = state4;
	$("instanceEquiment").style.display = state5;
	if (isAdminAccount) {
		$("btnGroupSet1").style.display = state6;
	} else {
		$("btnGroupSet1").style.display = "none";
	}
}

function showBatchAlarmConfig(type_value) {
	var type = type_value;
	var selectType = $('cbo_TYPE').value;
	var objattrId2alarm = null;
	if (type == "102") {
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
	} else if (type == "103") {
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm103");
	} else if (type == "104,105,107") {
		if (selectType == "104") {
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm104");
			type = "104";
		} else if (selectType == "105") {
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm105");
			type = "105";
		} else if (selectType == "107") {
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm107");
			type = "107";
		} else {
			objattrId2alarm = "error";
		}

	}

	if (objattrId2alarm == null || objattrId2alarm.length == 0) {
		jetsennet.alert("请选择要设置规则的对象属性！");
		return;
	} else if (objattrId2alarm == "error") {
		jetsennet.alert("请选择分类");
		return;
	}

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var callback = function() {
			var name = curTabNum;
	        if("1" == name) {
	            // 自定义信息
	        	$('insObjDiv11').innerHTML = "";
	        	getOAList(); 
	        }else if("2" == name) {
	        	// 获取配置信息
	            $('insConfigDiv').innerHTML = "";
	            getObjAttribute(curObjId, '101,106', '');
	        }else if("3" == name) {
	        	// 获取监控信息
	            $('insInspectDiv').innerHTML = "";
	            getObjAttribute(curObjId, '102', '');
	        }else if("4" == name) {
	        	// 获取性能指标信息
	            $('insPerformDiv').innerHTML = "";
	            getObjAttribute(curObjId, '103', '');
	        }else if("5" == name) {
	        	// 获取Trap信息、信号信息、Syslog信息
	            $('insTrapDiv').innerHTML = "";
	            onAttribTypeChange();
	        }
		}
		var alarmIds = ret.resultVal;
		if (alarmIds == "") {
			alert("指标无对应报警规则，请联系管理员！");
		} else if (alarmIds.indexOf(",") > 0) {
			generalAlarmConfig.showBatchConfigDialog(alarmIds, callback);
		} else {
			generalAlarmConfig.showConfigDialog(alarmIds, callback);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("getObjAttrAlarmId", [ objattrId2alarm.join(",") ]);
}