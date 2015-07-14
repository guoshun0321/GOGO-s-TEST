JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe", "js_jetsentree" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var AC_IMG_PATH = "./images/acIcon/";
var gGridList = new JetsenWeb.UI.GridList("attribclass-grid");
var gClassIdentifyGridList = new JetsenWeb.UI.GridList();
var gClassSetGridList = new JetsenWeb.UI.GridList();
var gCurSelectType = { TYPE_ID : "", TYPE_NAME : "", TYPE_LEVEL : "0" };

// 初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, showSplit : false });
	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divListTitle"), { size : { width : 0, height : 27 } }));
	gFrame.addControl(new JetsenWeb.UI.PageItem("divContainer"));

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	loadTypes(0);
	loadTypeByParent();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}
// 加载
function loadType() {
	$('divTree').innerHTML = "";
	gTypeTree = new JetsenWeb.UI.Tree("所有分类", "javascript:loadTypeByParent('-1')");
	gTypeTree.showTop = true;
	gTypeTree.setBehavior("classic");

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARMTYPE", "aa");
	sqlQuery.OrderString = "ORDER BY aa.TYPE_ID";
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.TYPE_ID", ResultFields : "aa.*", PageInfo : null, QueryTable : queryTable });

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var classes = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (classes != null) {
			var attrNode = new Array();
			for (i = 0; i < classes.length; i++) {
				var attrCls = classes[i];
				var node = null;
				node = new JetsenWeb.UI.TreeItem(attrCls["TYPE_NAME"], "javascript:loadTypeByParent('" + attrCls["TYPE_ID"] + "')");
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
		$("divTree").innerHTML = gTypeTree;
		gTypeTree.expandAll();
		//loadTypeByParent(gCurSelectType.TYPE_ID, gCurSelectType.TYPE_NAME, gCurSelectType.TYPE_LEVEL);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// 加载父对象列表
function loadTypes(ignoreId) {
	$("divParentTypeTree").innerHTML = "";

	var condition = new JetsenWeb.SqlConditionCollection();
	if (ignoreId) {
		condition.SqlConditions = [ JetsenWeb.SqlCondition.create("TYPE_ID", ignoreId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric) ];
	}

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null, ResultFields : "TYPE_ID,TYPE_NAME,PARENT_ID",
		OrderString : "Order By PARENT_ID", QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARMTYPE" }) });

	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var gParentTypeTree = JetsenWeb.UI.Tree.createTree("parent-tree", ret.resultVal, { parentId : "-1", parentField : "PARENT_ID",
			itemName : "Record", textField : "TYPE_NAME", valueField : "TYPE_ID", showCheck : false, paramFields : "TYPE_ID,PARENT_ID,TYPE_NAME" });
		gParentTypeTree.addItem(new JetsenWeb.UI.TreeItem("没有父级", null, null, null, { TYPE_ID : -1, TYPE_NAME : "" }));
		gParentTypeTree.onclick = function(item) {
			$("txtParentType").value = valueOf(item.treeParam, "TYPE_NAME", "");
			$("hidParentId").value = valueOf(item.treeParam, "TYPE_ID", "-1");
		};
		$("divParentTypeTree").appendChild(gParentTypeTree.render());
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function loadTypeByParent(classId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARMTYPE", "aa");
	var condition = new JetsenWeb.SqlConditionCollection();
	if (classId != -1 && classId != "-1") {
		condition.SqlConditions = [ JetsenWeb.SqlCondition.create("aa.PARENT_ID", classId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric) ];
	}
	sqlQuery.Conditions = condition;
	sqlQuery.OrderString = "ORDER BY aa.CREATE_TIME ASC";
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.TYPE_ID", ResultFields : "aa.*", PageInfo : null, QueryTable : queryTable });

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		renderGrid(sResult.resultVal);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

/*
 *显示表格
 */
function renderGrid(xml) {
	$("divContainer").innerHTML = "";

	gGridList.columns = [
			//{ index: 0, fieldName: "TYPE_ID", width: 100, name: "编号"},
			{ index : 0, fieldName : "TYPE_NAME", width : 200, name : "名称" }, { index : 1, fieldName : "TYPE_DESC", width : 910, name : "描述" },
			{ index : 2, fieldName : "TYPE_ID", width : 45, align : "center", name : "编辑" },
			{ index : 3, fieldName : "TYPE_ID", width : 45, align : "center", name : "删除" } ];

	gGridList.columns[2].format = function(val, vals) {
		val = "<a href='javascript:void(0)' onclick=\"editType('" + vals[0]
				+ "')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	gGridList.columns[3].format = function(val, vals) {
		val = "<a href='javascript:void(0)' onclick=\"deleteType('" + vals[0]
				+ "')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}

	gGridList.parentId = -1;
	gGridList.idField = "TYPE_ID";
	gGridList.parentField = "PARENT_ID";
	gGridList.treeControlIndex = 0;
	gGridList.treeOpenLevel = 1;
	gGridList.dataSource = xml;
	gGridList.render("divContainer");
	gGridList.colorSelectedRows();
	gGridList.ondoubleclick = function(row, col) {
		var rowId = row.id;
		var splitleng = rowId.split("-");
		editType(splitleng[splitleng.length - 1]);
	}
}
// 新增=====================================================================================
function newType() {
	loadTypes(0);
	var areaElements = JetsenWeb.Form.getElements('divType');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("hidParentId").value = "-1";
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 220 }, title : "新建报警类型" });
	showRemindWordCount($("txt_TYPE_DESC").value, $('remindWord'), "60");
	dialog.controls = [ "divType" ];
	dialog.onclosed = function() {
		document.getElementById("divParentTypeTree").style.display = "none";
		var popframeid = document.getElementById("divParentTypeTree").popframeid;
		if (document.getElementById(popframeid) != null && document.getElementById(popframeid) != "") {
			document.getElementById(popframeid).style.display = "none";
		}
		JetsenWeb.UI.Windows.close("new-object-win");
	}
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oType = { TYPE_NAME : $("txt_TYPE_NAME").value, PARENT_ID : $("hidParentId").value, TYPE_DESC : $("txt_TYPE_DESC").value,
				CREATE_USER : JetsenWeb.Application.userInfo.UserName };
			if (parseInt(getBytesCount($("txt_TYPE_DESC").value)) > 120) {
				jetsennet.alert("描述不能超过60个文字！");
				return;
			}
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadTypeByParent();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ALARMTYPE", JetsenWeb.Xml.serializer(oType, "BMP_ALARMTYPE") ]);
		}
	};
	dialog.showDialog();
}
// 编辑=====================================================================================
function editType(keyId) {
	loadTypes(keyId);
	var areaElements = JetsenWeb.Form.getElements('divType');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARMTYPE", "");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null, QueryTable : queryTable });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objType = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_TYPE_NAME").value = valueOf(objType, "TYPE_NAME", "");
		$("hidParentId").value = valueOf(objType, "PARENT_ID", "-1");
		getParentNameByParentId(valueOf(objType, "PARENT_ID", "-1"));
		$("txt_TYPE_DESC").value = valueOf(objType, "TYPE_DESC", "");
		showRemindWordCount($("txt_TYPE_DESC").value, $('remindWord'), "60");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 220 }, title : "编辑报警类型" });
	dialog.controls = [ "divType" ];
	dialog.onclosed = function() {
		document.getElementById("divParentTypeTree").style.display = "none";
		var popframeid = document.getElementById("divParentTypeTree").popframeid;
		if (document.getElementById(popframeid) != null && document.getElementById(popframeid) != "") {
			document.getElementById(popframeid).style.display = "none";
		}
		JetsenWeb.UI.Windows.close("edit-object-win");
	}
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oType = { TYPE_ID : keyId, TYPE_NAME : $("txt_TYPE_NAME").value, PARENT_ID : $("hidParentId").value,
				TYPE_DESC : $("txt_TYPE_DESC").value, CREATE_USER : JetsenWeb.Application.userInfo.UserName };
			if (parseInt(getBytesCount($("txt_TYPE_DESC").value)) > 120) {
				jetsennet.alert("描述不能超过60个文字！");
				return;
			}
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadTypeByParent();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ALARMTYPE", JetsenWeb.Xml.serializer(oType, "BMP_ALARMTYPE") ]);
		}
	};
	dialog.showDialog();
}
// 删除=====================================================================================
function deleteType(keyId) {
	jetsennet.confirm("确定删除？", function() {
		jetsennet.confirm("将删除该分类下所有分类？", function() {
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				loadTypeByParent();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjDelete", [ "BMP_ALARMTYPE", keyId ]);

			return true;
		});
	});
}

function getParentNameByParentId(Id) {
	if (Id != -1) {
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", Id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));

		var sqlQuery = new JetsenWeb.SqlQuery();
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null, ResultFields : "TYPE_NAME",
			QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ALARMTYPE" }) });

		sqlQuery.Conditions = condition;

		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var obj = JetsenWeb.Xml.toObject(ret.resultVal).Record;
			if (obj != null) {
				$("txtParentType").value = obj.TYPE_NAME;
			}
		}
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
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
			alert("callback");
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