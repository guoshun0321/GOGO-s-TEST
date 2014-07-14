JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gGroup2Coll = {};
var gCollectTaskPage = new JetsenWeb.UI.PageBar("CollectTask");
gCollectTaskPage.onpagechange = function() {
	loadCollectTask();
};
gCollectTaskPage.orderBy = "";
gCollectTaskPage.onupdate = function() {
	$('divCollectTaskPage').innerHTML = this.generatePageControl();
};
var gCollectTaskCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gCollectTaskPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_COLLECTTASK", "c");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "o", "o.GROUP_ID=c.GROUP_ID", JetsenWeb.TableJoinType.Inner));
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "r", "r.COLL_ID=c.COLL_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, KeyId : "TASK_ID", PageInfo : gCollectTaskPage, QueryTable : gQueryTable,
	ResultFields : "c.*,r.COLL_NAME,o.GROUP_NAME" });

// 加载=====================================================================================
function loadCollectTask() {
	gSqlQuery.OrderString = gCollectTaskPage.orderBy;
	gSqlQuery.Conditions = gCollectTaskCondition;

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		var weekdays = [ "周一", "周二", "周三", "周四", "周五", "周六", "周日" ];
		// var weekMasks = [1,2,3,4,5,6,7];
		for ( var i = 0; i < nodes.length; i++) {
			var taskType = valueOf(nodes[i].selectSingleNode("TASK_TYPE"), "text", "");
			if (taskType == "1") // 周期任务
			{
				var weekMask = valueOf(nodes[i].selectSingleNode("WEEK_MASK"), "text", "");
				var hourMask = valueOf(nodes[i].selectSingleNode("HOUR_MASK"), "text", "");
				var weekTip = "每";
				var token = false;
				for ( var j = 0; j < 7; j++) {
					if (weekMask != "" && (weekMask.charAt(j).toString() != "0")) {
						if (token) {
							weekTip += "、";
						}
						token = true;
						weekTip += weekdays[j].toString();
					}
				}
				nodes[i].selectSingleNode("WEEK_MASK").text = weekTip;

				if (hourMask && hourMask != "") {

					var arr = hourMask.split(",");
					var str = "";
					var firstNum = -1;
					var secondNum = -1;

					for ( var m = 0; m < arr.length; m++) {
						if (arr[m] != "00") {
							if (firstNum < 0) {
								firstNum = m;
							} else {
								secondNum = m;
							}
						} else {
							if (firstNum >= 0) {
								break;
							}
						}
					}

					if (firstNum < 0) {
						str = "";
					} else {
						if (secondNum < 0) {
							secondNum = firstNum;
						}
						str = firstNum + " 点 - " + secondNum + " 点";
					}
					
					nodes[i].selectSingleNode("HOUR_MASK").text = str;
				}
			}
		}
		$('divCollectTaskList').innerHTML = JetsenWeb.Xml._transformXML("xslt/collecttask.xslt", xmlDoc);
		gGridList.bind($('divCollectTaskList'), $('tabCollectTask'));
		gCollectTaskPage.setRowCount($('hid_CollectTaskCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	//	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [ gSqlQuery.toXml(), 'o.GROUP_ID', '1' ]);
}
function searchCollectTask() {
	gCollectTaskCondition.SqlConditions = [];
	gCollectTaskCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("TASK_STATE", 10, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));

	if ($("txtGrpName").value != "") {
		gCollectTaskCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_NAME", $("txtGrpName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}

	if ($('cboCollType').value != "") {
		gCollectTaskCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.TASK_TYPE", $('cboCollType').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}

	if ($('cboCollector').value != "") {
		gCollectTaskCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.COLL_ID", $('cboCollector').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}

	gCollectTaskPage.currentPage = 1;
	loadCollectTask();
}
// 删除=====================================================================================
function deleteCollectTask(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadCollectTask();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_COLLECTTASK", keyId ]);
		return true;
	});
}
// 新增=====================================================================================
function newCollectTask() {
	var areaElements = JetsenWeb.Form.getElements('divCollectTask');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$('txt_START_DATE').value = new Date().toDateString();
	$('txt_END_DATE').value = new Date().toDateString();

	var chkWeeks = document.getElementsByName("chkWeek");
	for ( var i = 0; i < chkWeeks.length; i++) {
		chkWeeks[i].checked = false;
	}

	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 500, height : 240 }, title : "新建采集任务" });
	dialog.controls = [ "divCollectTask" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if ($("cbo_GROUP_ID").value == "") {
				jetsennet.alert("请选择采集组！");
				return;
			}
			if ($("cbo_COLL_ID").value == "") {
				jetsennet.alert("请选择采集器！");
				return;
			}

			var weekMask = "";
			var hourMask = "";
			var startTime = "1900-01-01 00:00:00";
			var endTime = "1900-01-01 00:00:00";
			if ($("cbo_TASK_TYPE").value == "1") {
				var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
				if (weekdays.length == 0) {
					jetsennet.alert("请选择周几采集！");
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
					jetsennet.alert("采集起止时间有误！");
					return;
				}
			} else {
				startTime = $("txt_START_DATE").value + " " + $("txt_START_TIME").value;
				endTime = $("txt_END_DATE").value + " " + $("txt_END_TIME").value;
				var sd = $("txt_START_DATE").value.split("-");
				var st = $("txt_START_TIME").value.split(":");
				var startDate = new Date(sd[0], sd[1], sd[2], st[0], st[1], st[2]);
				var ed = $("txt_END_DATE").value.split("-");
				var et = $("txt_END_TIME").value.split(":");
				var endDate = new Date(ed[0], ed[1], ed[2], et[0], et[1], et[2]);
				if (endDate < startDate) {
					jetsennet.alert("开始时间不应晚于结束时间！");
					return;
				}
			}

			var objCollectTask = { COLL_ID : $("cbo_COLL_ID").value, GROUP_ID : $("cbo_GROUP_ID").value, TASK_TYPE : $("cbo_TASK_TYPE").value,
				TASK_STATE : 0, START_TIME : startTime, END_TIME : endTime, WEEK_MASK : weekMask, HOUR_MASK : hourMask,
				CREATE_USER : JetsenWeb.Application.userInfo.UserName };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				if (ret.resultVal == "-2") {
					jetsennet.alert("该对象已存在周期任务，不能新增！");
					return;
				} else {
					JetsenWeb.UI.Windows.close("new-object-win");
					loadCollectTask();
				}
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_COLLECTTASK", JetsenWeb.Xml.serializer(objCollectTask, "BMP_COLLECTTASK") ]);
		}
	};
	changeTaskType($("cbo_TASK_TYPE").options[0].value);
	dialog.showDialog();
}
// 编辑=====================================================================================
function editCollectTask(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divCollectTask');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TASK_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_COLLECTTASK", "c");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TASK_ID", PageInfo : null, QueryTable : gQueryTable, ResultFields : "c.*" });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objCollectTask = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("cbo_GROUP_ID").value = valueOf(objCollectTask, "GROUP_ID", "");
		$("cbo_COLL_ID").value = valueOf(objCollectTask, "COLL_ID", "");
		$("cbo_TASK_TYPE").value = valueOf(objCollectTask, "TASK_TYPE", "");
		changeTaskType($("cbo_TASK_TYPE").value);

		if ($("cbo_TASK_TYPE").value == "1") {
			var weekMask = valueOf(objCollectTask, "WEEK_MASK", "");
			if (weekMask) {
				var chkWeeks = document.getElementsByName("chkWeek");
				for ( var i = 0; i < chkWeeks.length; i++) {
					chkWeeks[i].checked = (parseInt(weekMask.charAt(i)) == parseInt(chkWeeks[i].value));
				}
			}

			var hourMask = valueOf(objCollectTask, "HOUR_MASK", "");
			if (hourMask) {
				var arr = hourMask.split(",");

				var flag1 = 0;
				var flag2 = 0;
				var len = 0; // 长度
				var str1 = "";
				var str2 = "";

				for ( var m = 0; m < arr.length; m++) {
					if (arr[m] == "00") {
						flag2 = 0;
						if (flag2 != flag1)// 由1变0时
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
						if (flag2 != flag1)// 由0变1
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
		} else {
			$("txt_START_DATE").value = valueOf(objCollectTask, "START_TIME", "").substring(0, 10).replace('T', ' ');
			$("txt_END_DATE").value = valueOf(objCollectTask, "END_TIME", "").substring(0, 10).replace('T', ' ');
			$("txt_START_TIME").value = valueOf(objCollectTask, "START_TIME", "").substring(11, 19).replace('T', ' ');
			$("txt_END_TIME").value = valueOf(objCollectTask, "END_TIME", "").substring(11, 19).replace('T', ' ');
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 500, height : 240 }, title : "编辑采集任务" });
	dialog.controls = [ "divCollectTask" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if ($("cbo_GROUP_ID").value == "") {
				jetsennet.alert("请选择采集组！");
				return;
			}
			if ($("cbo_COLL_ID").value == "") {
				jetsennet.alert("请选择采集器！");
				return;
			}

			var weekMask = "";
			var hourMask = "";
			var startTime = "1900-01-01 00:00:00";
			var endTime = "1900-01-01 00:00:00";

			if ($("cbo_TASK_TYPE").value == "1") {
				var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
				if (weekdays.length == 0) {
					jetsennet.alert("请选择周几采集！");
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
					jetsennet.alert("采集起止时间有误！");
					return;
				}
			} else {
				startTime = $("txt_START_DATE").value + " " + $("txt_START_TIME").value;
				endTime = $("txt_END_DATE").value + " " + $("txt_END_TIME").value;
				var sd = $("txt_START_DATE").value.split("-");
				var st = $("txt_START_TIME").value.split(":");
				var startDate = new Date(sd[0], sd[1], sd[2], st[0], st[1], st[2]);
				var ed = $("txt_END_DATE").value.split("-");
				var et = $("txt_END_TIME").value.split(":");
				var endDate = new Date(ed[0], ed[1], ed[2], et[0], et[1], et[2]);
				if (endDate < startDate) {
					jetsennet.alert("开始时间不应晚于结束时间！");
					return;
				}
			}

			var oCollectTask = { TASK_ID : keyId, Group_ID : $("cbo_GROUP_ID").value, COLL_ID : $("cbo_COLL_ID").value,
				TASK_TYPE : $("cbo_TASK_TYPE").value, WEEK_MASK : weekMask, HOUR_MASK : hourMask, START_TIME : startTime, END_TIME : endTime };

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				if (ret.resultVal == "-2") {
					jetsennet.alert("编辑采集任务失败！该对象已经存在有效的周期采集任务，不能启动！");
				} else if (ret.resultVal == "11") {
					jetsennet.alert("编辑采集任务失败！该采集任务与该对象的其他任务时间重叠！");
				} else {
					JetsenWeb.UI.Windows.close("edit-object-win");
					loadCollectTask();
				}
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_COLLECTTASK", JetsenWeb.Xml.serializer(oCollectTask, "BMP_COLLECTTASK") ]);
		}
	};
	dialog.showDialog();
}
function changeTaskType(type) {
	var win = JetsenWeb.UI.Windows.getById("new-object-win");
	if (!win) {
		win = JetsenWeb.UI.Windows.getById("edit-object-win");
		$("cbo_GROUP_ID").disabled = true;
	} else {
		$("cbo_GROUP_ID").disabled = false;
	}
	if (type == "1") // 周期任务
	{
		$("trWeek").style.display = "";
		$("trHour").style.display = "";

		$("trStartTime").style.display = "none";
		$("trEndTime").style.display = "none";
	} else {
		$("trWeek").style.display = "none";
		$("trHour").style.display = "none";

		$("trStartTime").style.display = "";
		$("trEndTime").style.display = "";
	}
}
function changeGroup(group) {
	var colId = gGroup2Coll[group];
	$("cbo_COLL_ID").value = colId;
}

// 启动采集任务
function startCollectTask(taskId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadCollectTask();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpStartCollectTask", [ taskId ]);
}

// 停止采集任务
function stopCollectTask(taskId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadCollectTask();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpStopCollectTask", [ taskId ]);
}

// 查看任务详细状态
function viewCollectTaskInfo(taskId) {
	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true, size : { width : 400, height : 400 },
		title : "任务详细状态", cancelButtonText : "关闭" });
	dialog.controls = [ "divCollectTaskInfo" ];
	dialog.onsubmit = function() {
		return false;
	};

	loadCollectTaskInfo(taskId);
	$("hid_TASK_ID").value = taskId;

	dialog.showDialog();
}

// 加载任务状态
function loadCollectTaskInfo(taskId) {
	$("divCollectTaskInfoList").innerHTML = "数据加载中...";
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divCollectTaskInfoList").innerHTML = JetsenWeb.Xml.transformXML("xslt/collecttaskdetail.xslt", ret.resultVal);
		var collectTaskInfoList = new JetsenWeb.UI.GridList();
		collectTaskInfoList.bind($("divCollectTaskInfoList"), $("tabCollectTaskInfo"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpGetCollectTaskInfo", [ taskId ]);
}

// 启动采集任务
function startObjectTask(objId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadCollectTaskInfo($("hid_TASK_ID").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpStartObjectTask", [ $("hid_TASK_ID").value, objId ]);
}

// 停止采集任务
function stopObjectTask(objId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadCollectTaskInfo($("hid_TASK_ID").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpStopObjectTask", [ $("hid_TASK_ID").value, objId ]);
}

// 初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	var groupControl = $("cbo_GROUP_ID");
	loadCollGrp(groupControl);

	//	loadTableObject($('cbo_COLL_ID'), false, "BMP_COLLECTOR", "COLL_NAME","COLL_ID");
	getCollName($('cbo_COLL_ID'), false, "BMP_COLLECTOR", "COLL_NAME", "COLL_ID");
	//	loadTableObject($('cboCollector'), false, "BMP_COLLECTOR", "COLL_NAME","COLL_ID");
	getCollName($('cboCollector'), false, "BMP_COLLECTOR", "COLL_NAME", "COLL_ID");

	searchCollectTask();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });

	frameContent.addControl(new JetsenWeb.UI.PageItem("divCollectTaskList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 30 } }));
	gFrame.addControl(frameContent);

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

function loadCollGrp(controlId, noEmpty) {
	var control = $(controlId);
	control.options.length = 0;
	if (!noEmpty) {
		control.options[0] = new Option("请选择", "");
	}

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "", PageInfo : null, QueryTable : queryTable,
		ResultFields : "GROUP_NAME,GROUP_ID,NUM_VAL1" });

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", 3, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(resultXml) {
		var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal, "Record");
		if (typeObjs != null) {
			for ( var i = 0; i < typeObjs.length; i++) {
				var option = new Option(typeObjs[i].GROUP_NAME, typeObjs[i].GROUP_ID);
				control.options[control.options.length] = option;
				gGroup2Coll[typeObjs[i].GROUP_ID] = typeObjs[i].NUM_VAL1;
			}
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [ sqlQuery.toXml(), 'GROUP_ID', '1' ]);
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