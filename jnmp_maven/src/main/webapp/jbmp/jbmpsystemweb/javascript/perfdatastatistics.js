JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker",
		"js_xtree", "js_autocomplete" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gFunctions = {
	check : false
};

var gReportTimePage = new JetsenWeb.UI.PageBar("ReportTime");
gReportTimePage.onpagechange = function() {
	loadReportTime($("hid_REPORT_ID").value);
};
gReportTimePage.orderBy = "";
gReportTimePage.onupdate = function() {
	$("divReportTimePage").innerHTML = this.generatePageControl();
};
var gReportTimeCondition = new JetsenWeb.SqlConditionCollection();
var gReportTimeGridList = new JetsenWeb.UI.GridList();
gReportTimeGridList.ondatasort = function(sortfield, desc) {
	gReportTimePage.setOrderBy(sortfield, desc);
};

// 对象分类树
var gTree;
var attribClassArray;

var paramFormat; // 报表参数
var reportType = "perfdatastatistics"; // 报表类型

// 初始化===================================================================================
function pageInit() {
	initFunction();

	// 查询两小时内的性能数据
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
//	$("txtEDate").value = endTime.toDateString();
//	$("txtETime").value = endTime.toTimeString();
//	$("txtSDate").value = startTime.toDateString();
//	$("txtSTime").value = startTime.toTimeString();

	// loadAttribClass();
	// attributeInit();
	// exportData();
	// searchAlarmEvent();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		enableResize : false,
		splitTitle : "divListTitle",
		splitSize : 27
	});

	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divPerfDataList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 0
		}
	}));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), {
		size : {
			width : 0,
			height : 60
		}
	}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	refreshAttribClassTree();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

// 刷新对象分类树
function refreshAttribClassTree() {
	attribClassArray = null;
	genAttribClassTree();
	$("divAttribClassTree").innerHTML = gTree;
	$('txt_AttribClass').value = "请选择";
	objectInit();
	attributeInit();
}

// 生成对象分类树
function genAttribClassTree() {
	if (!attribClassArray) {
		getAllAttribClass();
	}
	gTree = new JetsenWeb.UI.Tree("对象分类",
			"javascript:showObjList('', '', '请选择')");
	gTree.showTop = true;
	gTree.setBehavior("classic");

	if (attribClassArray) {
		var attribClassNode = new Array();
		for (i = 0; i < attribClassArray.length; i++) {
			var attribClassCls = attribClassArray[i];
			var node = null;

			node = new JetsenWeb.UI.TreeItem(attribClassCls["CLASS_NAME"],
					"javascript:showObjList('" + attribClassCls["CLASS_TYPE"]
							+ "', '" + attribClassCls["CLASS_ID"] + "', '"
							+ attribClassCls["CLASS_NAME"] + "')");
			if (attribClassCls["PARENT_ID"] == null
					|| attribClassCls["PARENT_ID"] == "") {
				gTree.add(node);
			} else {
				for (j = 0; j < attribClassNode.length; j++) {
					if (attribClassArray[j]["CLASS_ID"] == attribClassCls["PARENT_ID"]) {
						attribClassNode[j].add(node);
						break;
					}
				}
			}
			attribClassNode.push(node);
		}
	}
}

// 获取全部对象分类
function getAllAttribClass() {
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "b",
			"a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));

	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.OrderString = "ORDER BY a.CLASS_ID";
	JetsenWeb.extend(sqlQuery, {
		KeyId : "CLASS_ID",
		QueryTable : queryTable,
		ResultFields : "a.CLASS_ID,PARENT_ID,CLASS_NAME,CLASS_TYPE"
	});

	var queryConditions = new JetsenWeb.SqlConditionCollection();
	// queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_ID",
	// "10000", JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.Less,
	// JetsenWeb.SqlParamType.Numeric));
	// queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL",
	// "1", JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal,
	// JetsenWeb.SqlParamType.Numeric));
	// queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL",
	// "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
	// JetsenWeb.SqlParamType.Numeric));
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create(
			"CLASS_LEVEL", "100", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Less, JetsenWeb.SqlParamType.Numeric));

	sqlQuery.Conditions = queryConditions;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		attribClassArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

// 树节点点击
function showObjList(classType, classId, className) {
	$('txt_AttribClass').value = className;

	objectInit(classType);
}

function objectInit(classType) {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b",
			"a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_TYPE",
			classType, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));

	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "OBJ_ID",
		QueryTable : gQueryTable,
		Conditions : condition,
		ResultFields : "DISTINCT OBJ_ID,OBJ_NAME"
	});
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var cboObject = $("cbo_Object");
		cboObject.length = 0;
		cboObject.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var objectInfo = records[i];
				var option = new Option(objectInfo["OBJ_NAME"],
						objectInfo["OBJ_ID"])
				option.title = objectInfo["OBJ_NAME"];
				cboObject.options.add(option);
			}
		}

		attributeInit($("cbo_Object").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'OBJ_ID', '2']);
}

// 初始化对象性能属性
function attributeInit() {
	if ($("cbo_Object").value != "") {
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
		// gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS",
		// "a2c", "a.ATTRIB_ID=a2c.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
		// gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS",
		// "ac", "a2c.CLASS_ID=ac.CLASS_ID", JetsenWeb.TableJoinType.Left));
		var condition = new JetsenWeb.SqlConditionCollection();

		condition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("a.ATTRIB_TYPE", "103", JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.Numeric));
		condition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("a.OBJ_ID", $("cbo_Object").value,
						JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.Numeric));

		JetsenWeb.extend(gSqlQuery, {
			IsPageResult : 0,
			KeyId : "OBJATTR_ID",
			QueryTable : gQueryTable,
			Conditions : condition,
			ResultFields : "DISTINCT a.OBJATTR_ID,a.OBJATTR_NAME"
		});
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var cboAttribute = $("cbo_Attribute");
			cboAttribute.length = 0;
			cboAttribute.options.add(new Option("请选择", ""));

			var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if (records) {
				var length = records.length;
				for ( var i = 0; i < length; i++) {				
					var attribInfo = records[i];
					var option = new Option(attribInfo["OBJATTR_NAME"],attribInfo["OBJATTR_ID"])
//					cboAttribute.options.add(new Option(
//							attribInfo["OBJATTR_NAME"],
//							attribInfo["OBJATTR_ID"]));
					option.title = attribInfo["OBJATTR_NAME"];
					cboAttribute.options.add(option);
				}
			}
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	} else {
		var cboAttribute = $("cbo_Attribute");
		cboAttribute.length = 0;
		cboAttribute.options.add(new Option("请选择", ""));
	}
}

// 导出数据
function exportData() { // fieldsString
	if ($("cbo_Object").value == "") {
		jetsennet.alert("请选择对象！");
		return;
	}
	if ($("cbo_Attribute").value == "") {
		jetsennet.alert("请选择对象属性！");
		return;
	}
	/*if ($("chkDate").checked == false) {
		jetsennet.alert("请选择查询时间！");
		return;
	}*/
	
//	var startTime = parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime();
//	var endTime = parseDate($("txtEDate").value + " " + $("txtETime").value).getTime();
//	if(startTime > endTime){
//		jetsennet.alert("开始时间不能大于结束时间!");
//		return;
//	}
	var obj_year4 = document.getElementById("year4"); //定位id
	var index_year4 = obj_year4.selectedIndex; // 选中索引
	var value_year4 = obj_year4.options[index_year4].value; // 选中值
	var obj_month4 = document.getElementById("month4"); //定位id
	var index_month4 = obj_month4.selectedIndex; // 选中索引
	var value_month4 = obj_month4.options[index_month4].value; // 选中值
	var obj_day4 = document.getElementById("day4"); //定位id
	var index_day4 = obj_day4.selectedIndex; // 选中索引
	var value_day4 = obj_day4.options[index_day4].value; // 选中值
	var queryTime = value_year4 +" " + value_month4+" " + value_day4;
	url = getReportWebPath() + "/perfdatastatistics.jsp?flag=1";
	url += "&OBJ_ID=" + $("cbo_Object").value;
	url += "&OBJATTR_ID=" + $("cbo_Attribute").value;
//	url += "&downCOLL_TIME=" + startTime;
//	url += "&upCOLL_TIME=" + endTime;
	url += "&queryTime=" + queryTime;
	$("frmTableInfo").src = url;
}

// 弹出选择导出列
function chooseField() {
	var dialog = new JetsenWeb.UI.Window("choose-object-win");
	JetsenWeb.extend(dialog, {
		cancelBox : true,
		submitBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 300,
			height : 280
		},
		title : "选择导出列"
	});
	dialog.controls = [ "divFieldList" ];
	dialog.onsubmit = function() {
		exportData(JetsenWeb.Form.getCheckedValues("chkField"));
		JetsenWeb.UI.Windows.close("choose-object-win");
	};
	dialog.showDialog();
	$("divFieldList").innerHTML = JetsenWeb.Xml.transformXML(
			"xslt/choosefield.xslt", getFieldsString());
	var fieldList = new JetsenWeb.UI.GridList();
	fieldList.rowSelection = false;
	fieldList.bind($("divFieldList"), $("tabField"));
}

// 获取显示列的XML
function getFieldsString() {
	var fieldsString = "<RecordSet>";
	for ( var i = 0; i < fields.length; i++) {
		fieldsString += JetsenWeb.Xml.serializer(fields[i], "Record");
	}
	fieldsString += "</RecordSet>";
	return fieldsString;
}

// 根据权限初始化界面
function pageInitByFunction() {
	$("btnCheck").disabled = !gFunctions.check;
}

// 初始化报表参数
function initParamFormat() {
	paramFormat = JetsenWeb.Xml.serializer( {
		OBJ_ID : $("cbo_Object").value,
		OBJATTR_ID : $("cbo_Attribute").value
	}, "Format");
}

/**
 * 设置定制报表参数
 * @param taskId 用于判断是否为编辑状态
 * @return
 */
function setCustomReportParam(taskId) {
	var param = "";
	if ($("chk_paramOrign").checked) {
		if (getSelectedValue($("cbo_Object")) != "") {
			param += "对象名称：" + getSelectedText($("cbo_Object")) + "\n";
		}
		if (getSelectedValue($("cbo_Attribute")) != "") {
			
			param += "对象属性：" + getSelectedText($("cbo_Attribute")) + "\n";
		}
	}else if(taskId !="")
	{
		/*
		 * 编辑状态下：从数据库中获取定制报表参数（BMP_REPORTTIME表FIELD_1）
		 * dbCustomReportParam 参见javascript/report_comm.js
		 */
		param = dbCustomReportParam;
	}
	$("txt_customReportParam").value = param;
}