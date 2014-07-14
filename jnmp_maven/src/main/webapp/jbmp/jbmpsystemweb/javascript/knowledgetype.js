JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var AC_IMG_PATH = "./images/acIcon/";
var gGridList = new JetsenWeb.UI.GridList("objgroup-grid");
var gKnowledgeTypePage = new JetsenWeb.UI.PageBar("KnowledgeType");
gKnowledgeTypePage.onpagechange = function() {
	loadKnowledgeType();
};
gKnowledgeTypePage.orderBy = "order by TYPE_ID asc";
gKnowledgeTypePage.onupdate = function() {
	$("divKnowledgeTypePage").innerHTML = this.generatePageControl();
};
var gKnowledgeTypeCondition = new JetsenWeb.SqlConditionCollection();
gGridList.ondatasort = function(sortfield, desc) {
	gKnowledgeTypePage.setOrderBy(sortfield, desc);
};

var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGETYPE", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", ResultFields : "*", PageInfo : null, QueryTable : gQueryTable });

//加载=====================================================================================
function loadKnowledgeType() {
	gSqlQuery.OrderString = gKnowledgeTypePage.orderBy;
	gSqlQuery.Conditions = gKnowledgeTypeCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		//        $("divKnowledgeTypeList").innerHTML = JetsenWeb.Xml.transformXML("xslt/knowledgetype.xslt", ret.resultVal);
		//        gGridList.bind($("divKnowledgeTypeList"), $("tabKnowledgeType"));
		//        gKnowledgeTypePage.setRowCount($("hid_KnowledgeTypeCount").value);
		renderGrid(ret.resultVal);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}

/*
 *显示表格
 */
function renderGrid(xml) {
	$("divKnowledgeTypeList").innerHTML = "";

	gGridList.columns = [ { index : 0, fieldName : "TYPE_NAME", width : 300, name : "类别名称" },
			{ index : 1, fieldName : "TYPE_DESC", width : 795, name : "类别描述" },
			{ index : 2, fieldName : "TYPE_ID", width : 45, align : "center", name : "编辑" },
			{ index : 3, fieldName : "TYPE_ID", width : 45, align : "center", name : "删除" } ];

	gGridList.columns[2].format = function(val, vals) {
		val = "<a href='javascript:void(0)' onclick=\"editKnowledgeType('" + vals[0]
				+ "')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	gGridList.columns[3].format = function(val, vals) {
		val = "<a href='javascript:void(0)' onclick=\"deleteKnowledgeType('" + vals[0]
				+ "')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}
	gGridList.parentId = "0";
	gGridList.idField = "TYPE_ID";
	gGridList.parentField = "PARENT_ID";
	gGridList.treeControlIndex = 0;
	gGridList.treeOpenLevel = 1;
	gGridList.dataSource = xml;
	gGridList.render("divKnowledgeTypeList");
	gGridList.colorSelectedRows();
	gGridList.ondoubleclick = function(row, col) {
		var rowId = row.id;
		var splitleng = rowId.split("-");
		editKnowledgeType(splitleng[splitleng.length - 1]);
	}
}

function searchKnowledgeType() {
	gKnowledgeTypeCondition.SqlConditions = [];
	loadKnowledgeType();
}
//删除=====================================================================================
function deleteKnowledgeType(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadKnowledgeType();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_KNOWLEDGETYPE", keyId ]);
		return true;
	});
}
//新增=====================================================================================
function newKnowledgeType() {
	var areaElements = JetsenWeb.Form.getElements("divKnowledgeType");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	loadParentType();
	showRemindWordCount($("txt_TYPE_DESC").value, $('remindWord'), "60");

	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 280 }, title : "新建知识库类别" });
	dialog.controls = [ "divKnowledgeType" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objKnowledgeType = { TYPE_NAME : $("txt_TYPE_NAME").value, TYPE_DESC : $("txt_TYPE_DESC").value, PARENT_ID : $("hidParentId").value };

			if (parseInt(getBytesCount($("txt_TYPE_DESC").value)) > 120) {
				jetsennet.alert("类别描述不能超过60个文字！");
				return;
			}

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadKnowledgeType();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_KNOWLEDGETYPE", JetsenWeb.Xml.serializer(objKnowledgeType, "BMP_KNOWLEDGETYPE") ]);
		}
	};
	dialog.showDialog();
}
//编辑=====================================================================================
function editKnowledgeType(keyId) {
	var areaElements = JetsenWeb.Form.getElements("divKnowledgeType");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_KNOWLEDGETYPE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objKnowledgeType = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_TYPE_NAME").value = valueOf(objKnowledgeType, "TYPE_NAME", "");
		$("txt_TYPE_DESC").value = valueOf(objKnowledgeType, "TYPE_DESC", "");
		loadParentType(valueOf(objKnowledgeType, "PARENT_ID", ""), keyId);
		showRemindWordCount($("txt_TYPE_DESC").value, $('remindWord'), "60");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 280 }, title : "编辑知识库类别" });
	dialog.controls = [ "divKnowledgeType" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oKnowledgeType = { TYPE_ID : keyId, TYPE_NAME : $("txt_TYPE_NAME").value, TYPE_DESC : $("txt_TYPE_DESC").value,
				PARENT_ID : $("hidParentId").value };
			if (parseInt(getBytesCount($("txt_TYPE_DESC").value)) > 120) {
				jetsennet.alert("类别描述不能超过60个文字！");
				return;
			}

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadKnowledgeType();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_KNOWLEDGETYPE", JetsenWeb.Xml.serializer(oKnowledgeType, "BMP_KNOWLEDGETYPE") ]);
		}
	};
	dialog.showDialog();
}
//初始化===================================================================================
function pageInit() {
	searchKnowledgeType();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, showSplit : false });
	gFrame.splitTitle = "divListTitle";

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType : 1, fixControlIndex : 1, showSplit : false,
		splitBorder : 0 });
	frameContent.addControl(new JetsenWeb.UI.PageItem("divKnowledgeTypeList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divListTitle"), { size : { width : 0, height : 27 } }));
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

/**
 * 新建的时候显示父类别
 * 当显示父类型的时候，不要显示自己那项，防止将自己设置成父类型
 * @return
 */
function loadParentType(parentId, typeId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_KNOWLEDGETYPE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	if (typeId) {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", typeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
				JetsenWeb.SqlParamType.Numeric));
	}
	sqlQuery.Conditions = condition;
	sqlQuery.OrderString = "order by TYPE_ID asc";

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		$("divParentTypeTree").innerHTML = "";
		var rootName = "";
		var gGroupTree = new JetsenWeb.UI.Tree(rootName, "javascript:void(0)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
		gGroupTree.showTop = false;
		gGroupTree.setBehavior("classic");
		var treeNodes = { "0" : gGroupTree };
		var treeNodeChildCounts = {};

		var typeObjs = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		if (typeObjs != null) {
			var typeObjMap = {};
			for ( var i = 0; i < typeObjs.length; i++) {
				typeObjMap[typeObjs[i]["TYPE_ID"]] = typeObjs[i];
				var nodeParentId = valueOf(typeObjs[i], "PARENT_ID", "");
				if (nodeParentId == "0") {
					treeNodeChildCounts["0"] = treeNodeChildCounts["0"] ? treeNodeChildCounts["0"] + 1 : 1;
				} else {
					treeNodeChildCounts[nodeParentId] = treeNodeChildCounts[nodeParentId] ? treeNodeChildCounts[nodeParentId] + 1 : 1;
				}
			}
			genTypeTree(typeObjs, typeObjMap, treeNodeChildCounts, treeNodes);
			//			for ( var i = 0; i < typeObjs.length; i++) {
			//				var typeId = valueOf(typeObjs[i], "TYPE_ID", "");
			//				var typeName = valueOf(typeObjs[i], "TYPE_NAME", "");
			//				treeNodes[typeId] = new JetsenWeb.UI.TreeItem(valueOf(typeObjs[i], "TYPE_NAME", ""), "javascript:$('txtParentType').value = '" + typeName + "'; $('hidParentId').value = '" + typeId + "'; void(0);",
			//						null, AC_IMG_PATH + (treeNodeChildCounts[typeId] > 0 ? "defaulticon.gif" : "pcde_002.gif"), AC_IMG_PATH + (treeNodeChildCounts[typeId] > 0 ? "defaulticon.gif" : "pcde_002.gif"));
			//				if (valueOf(typeObjs[i], "PARENT_ID", "") == "0")
			//				{
			//					treeNodes["0"].add(treeNodes[typeId]);
			//				}
			//				else
			//				{
			//					var parentNode = treeNodes[valueOf(typeObjs[i], "PARENT_ID", "")];
			//					if (parentNode)
			//					{
			//						parentNode.add(treeNodes[typeId]);
			//					}
			//				}
			//			}
		}
		treeNodes["0"].add(new JetsenWeb.UI.TreeItem("没有父级", "javascript:$('txtParentType').value = ''; $('hidParentId').value = '0'; void(0);",
				null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif"));
		$("divParentTypeTree").innerHTML = gGroupTree;
		$("hidParentId").value = parentId ? parentId : "0";
		getParentNameByParentId(parentId);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function genTypeTree(typeObjs, typeObjMap, treeNodeChildCounts, treeNodes) {
	for ( var i = 0; i < typeObjs.length; i++) {
		ensureTypeNode(typeObjs[i], typeObjMap, treeNodeChildCounts, treeNodes);
	}
}

function ensureTypeNode(typeObj, typeObjMap, treeNodeChildCounts, treeNodes) {
	var typeId = typeObj["TYPE_ID"];
	var typeName = typeObj["TYPE_NAME"];
	var parentId = typeObj["PARENT_ID"];
	if (treeNodes[typeId]) {
		return treeNodes[typeId];
	}

	var imgPath = AC_IMG_PATH + (treeNodeChildCounts[typeId] > 0 ? "defaulticon.gif" : "pcde_002.gif");
	var op = "javascript:$('txtParentType').value = '" + typeName + "'; $('hidParentId').value = '" + typeId + "'; void(0);";
	var curNode = new JetsenWeb.UI.TreeItem(typeName, op, null, imgPath, imgPath);
	treeNodes[typeId] = curNode;

	if (parentId == "0") {
		treeNodes["0"].add(curNode);
	} else {
		var pTypeObj = typeObjMap[parentId];
		if (pTypeObj) {
			var parentNode = ensureTypeNode(pTypeObj, typeObjMap, treeNodeChildCounts, treeNodes);
			parentNode.add(curNode);
		}
	}
	return curNode; 
}

function getParentNameByParentId(id) {
	if (id && id != 0) {
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));

		var sqlQuery = new JetsenWeb.SqlQuery();
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null, ResultFields : "TYPE_NAME",
			QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_KNOWLEDGETYPE" }) });

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
