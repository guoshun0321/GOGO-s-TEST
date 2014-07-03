JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gFunctions = {
	check : false
}; // 权限：报警处理

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

var paramFormat; // 报表参数
var reportType = "alarmeventstatisticsnew"; // 报表类型

// 初始化===================================================================================
function pageInit() {
	initFunction();

	// 查询两小时内的报警
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();

	$("cboChecked").value = "0";
	loadSystem($("cbo_ObjGroup"),false);
	//objGroupInit();
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

	frameContent.addControl(new JetsenWeb.UI.PageItem("divAlarmEventList"));
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

	gFunctions.check = true;

	pageInitByFunction();

}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}
/*
// 初始化对象组
function objGroupInit() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "og");

	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", "1,6",
			JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In,
			JetsenWeb.SqlParamType.Numeric));

	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 0,
		KeyId : "GROUP_ID",
		QueryTable : gQueryTable,
		Conditions : condition,
		ResultFields : "DISTINCT GROUP_ID,GROUP_NAME"
	});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var cboObjGroup = $("cbo_ObjGroup");
		cboObjGroup.length = 0;
		cboObjGroup.options.add(new Option("请选择", ""));

		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) {
			var length = records.length;
			for ( var i = 0; i < length; i++) {
				var objGroupInfo = records[i];
				cboObjGroup.options.add(new Option(objGroupInfo["GROUP_NAME"],
						objGroupInfo["GROUP_ID"]));
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
*/
// 导出数据
function exportData() { // fieldsString
	url = getReportWebPath()+"/alarmeventstatisticsnew.jsp?flag=1";

	if ($("cboChecked").value != "") {
		url += "&EVENT_STATE=" + $("cboChecked").value;
	}
	if ($("cbo_ObjGroup").value != "") {
		url += "&og.GROUP_ID=" + $("cbo_ObjGroup").value;
	}else{
		url += "&og.GROUP_ID="+getGroupId();
	}
	if ($("chkSDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		url += "&downCOLL_TIME="
				+ parseDate($("txtSDate").value + " " + $("txtSTime").value)
						.getTime();
	}
	if ($("chkEDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		url += "&upCOLL_TIME="
				+ parseDate($("txtEDate").value + " " + $("txtETime").value)
						.getTime();
	}
	if ($("txtCheckUser").value != "") {
		url += "&CHECK_USER=" + escape(encodeURI($("txtCheckUser").value));
	}
	if ($("txtObjName").value != "") {
		url += "&o.OBJ_NAME=" + escape(encodeURI($("txtObjName").value));
	}
	if ($("cboAlarmLevel").value != "") {
		url += "&ALARM_LEVEL=" + $("cboAlarmLevel").value;
	}
	// if (fieldsString && fieldsString != "") {
	// url += "&ResultFields=" + escape(fieldsString);
	// }

	// window.open(url, "");
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
	// $("btnCheck").disabled = !gFunctions.check;
}

// 初始化报表参数
function initParamFormat() {
	paramFormat = JetsenWeb.Xml.serializer( {
		EVENT_STATE : $("cboChecked").value,
		GROUP_ID : $("cbo_ObjGroup").value,
		CHECK_USER : escape(encodeURI($("txtCheckUser").value)),
		OBJ_NAME : escape(encodeURI($("txtObjName").value)),
		ALARM_LEVEL : $("cboAlarmLevel").value
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
		if (getSelectedValue($("cbo_ObjGroup")) != "") {
			param += "所属系统：" + getSelectedText($("cbo_ObjGroup")) + "\n";
		}
		if (getSelectedValue($("cboChecked")) != "") {
			
			param += "报警状态：" + getSelectedText($("cboChecked")) + "\n";
		}
		if ($("txtObjName").value != "") {
			param += "报警对象：" + $("txtObjName").value + "\n";
		}
		if ($("cboAlarmLevel").value != "") {
			param += "报警级别：" + getSelectedText($("cboAlarmLevel")) + "\n";
		}
		if ($("txtCheckUser").value != "") {
			param += "操作人：" + $("txtCheckUser").value + "\n";
		}
		/*if ($("chkSDate").checked) {
			param += "开始时间：" + $("txtSDate").value + " " + $("txtSTime").value
					+ "\n";
		}
		if ($("chkEDate").checked) {
			param += "结束时间：" + $("txtEDate").value + " " + $("txtETime").value
					+ "\n";
		}*/
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

function getGroupId() {
	var sqlQuery2 = new JetsenWeb.SqlQuery();
	sqlQuery2.GroupFields = " GROUP_ID ";
	var groupQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
    JetsenWeb.extend(sqlQuery2, { IsPageResult: 1, KeyId : "GROUP_ID",  ResultFields: " GROUP_ID", QueryTable: groupQueryTable});
    var condition2 = new JetsenWeb.SqlConditionCollection();	
    sqlQuery2.Conditions = condition2;

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    var groupId="";
    ws.oncallback = function (ret) {   	
         var objs = JetsenWeb.Xml.toObject(ret.resultVal,"Record");
	     if(objs && objs.length>0)
	        {	    	    
	            for(var i=0;i<objs.length;i++)
	            {
	            	groupId =groupId+objs[i].GROUP_ID+",";
	            }
	        }
	     groupId = groupId.substring(0, groupId.length-1);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);   
	ws.call("nmpPermissionsQuery", [sqlQuery2.toXml(), 'GROUP_ID', '1']);
    return groupId;	
}
