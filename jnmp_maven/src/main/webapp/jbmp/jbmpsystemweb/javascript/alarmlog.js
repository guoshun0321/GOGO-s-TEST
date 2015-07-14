JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gAlarmEventPage = new JetsenWeb.UI.PageBar("AlarmEvent");
gAlarmEventPage.onpagechange = function() {
	loadAlarmEvent();
};
gAlarmEventPage.orderBy = "Order By OPERATE_TIME Desc";
gAlarmEventPage.gotoPage = true;
gAlarmEventPage.onupdate = function() {
	$('divAlarmEventPage').innerHTML = this.generatePageControl();
};
var gAlarmEventCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gAlarmEventPage.setOrderBy(sortfield, desc);
};
var gEventSqlQuery = new JetsenWeb.SqlQuery();
var gEventQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENT", "ae");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r",
		"r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS",
		"ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o",
		"o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g",
		"o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og",
		"og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMLOG", "p",
		"p.ALARMEVT_ID=ae.ALARMEVT_ID", JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER", "u",
		"p.USERID=u.ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gEventSqlQuery, {
	IsPageResult : 1,
	KeyId : "",
	PageInfo : gAlarmEventPage,
	QueryTable : gEventQueryTable,
	ResultFields : "r.OBJATTR_NAME,o.OBJ_NAME,p.ID,P.OPERATE_TIME ,p.OPERATE,u.USER_NAME,u.LOGIN_NAME,ae.ALARMEVT_ID,ae.EVENT_STATE," +
			"ae.ALARM_LEVEL,ae.COLL_TIME,ae.RESUME_TIME,ae.EVENT_DURATION,ae.ALARM_COUNT,ae.EVENT_DESC,ae.CHECK_USER,ae.CHECK_TIME "
});
var gEventCondition = new JetsenWeb.SqlConditionCollection();

var gLogSqlQuery = new JetsenWeb.SqlQuery();
var gLogQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENTLOG", "ae");
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r",
		"r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac",
		"r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o",
		"o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "o2g",
		"o2g.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "og",
		"og.GROUP_ID=o2g.GROUP_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMLOG", "p",
		"p.ALARMEVT_ID=ae.ALARMEVT_ID", JetsenWeb.TableJoinType.Inner));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER", "u",
		"p.USERID=u.ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gLogSqlQuery, {
	IsPageResult : 1,
	KeyId : "",
	PageInfo : gAlarmEventPage,
	QueryTable : gLogQueryTable,
	ResultFields : "r.OBJATTR_NAME,o.OBJ_NAME,p.ID,P.OPERATE_TIME ,p.OPERATE,u.USER_NAME,u.LOGIN_NAME,ae.ALARMEVT_ID,ae.EVENT_STATE," +
			"ae.ALARM_LEVEL,ae.COLL_TIME,ae.RESUME_TIME,ae.EVENT_DURATION,ae.ALARM_COUNT,ae.EVENT_DESC,ae.CHECK_USER,ae.CHECK_TIME "
});
var gLogCondition = new JetsenWeb.SqlConditionCollection();

var gUnionSqlQuery = new JetsenWeb.SqlQuery();
JetsenWeb.extend(gUnionSqlQuery, {
	IsPageResult : 1,
	KeyId : "",
	PageInfo : gAlarmEventPage,
	ResultFields : "s.GROUP_NAME,og.GROUP_NAME,r.OBJATTR_NAME,o.OBJ_NAME,ae.*"
});

var gSqlQuery = gUnionSqlQuery;

var fields = [ {
	FIELD_NAME : "ae.ALARMEVT_ID",
	DISPLAY_NAME : "编号"
}, {
	FIELD_NAME : "u.USER_NAME",
	DISPLAY_NAME : "处理人"
}, {
	FIELD_NAME : "p.OPERATE",
	DISPLAY_NAME : "处理方式"
}, {
	FIELD_NAME : "p.OPERATE_TIME",
	DISPLAY_NAME : "操作时间"
}, {
	FIELD_NAME : "o.OBJ_NAME",
	DISPLAY_NAME : "报警对象"
}, {
	FIELD_NAME : "r.OBJATTR_NAME",
	DISPLAY_NAME : "报警类型"
}, {
	FIELD_NAME : "ae.EVENT_STATE",
	DISPLAY_NAME : "当前状态"
}, {
	FIELD_NAME : "ae.ALARM_LEVEL",
	DISPLAY_NAME : "报警等级"
}, {
	FIELD_NAME : "ae.COLL_TIME",
	DISPLAY_NAME : "报警时间"
}, {
	FIELD_NAME : "ae.RESUME_TIME",
	DISPLAY_NAME : "恢复时间"
}, {
	FIELD_NAME : "ae.EVENT_DURATION",
	DISPLAY_NAME : "持续时间"
}, {
	FIELD_NAME : "ae.EVENT_DESC",
	DISPLAY_NAME : "报警描述"
},{
	FIELD_NAME : "ae.CHECK_DESC",
	DISPLAY_NAME : "意见"
} ];

//加载=====================================================================================
function loadAlarmEvent() {
	gUnionSqlQuery.OrderString = gAlarmEventPage.orderBy;
	gUnionSqlQuery.Conditions = gAlarmEventCondition;

	gEventSqlQuery.OrderString = "";
	gEventSqlQuery.Conditions = gEventCondition;

	gLogSqlQuery.OrderString = "";
	gLogSqlQuery.Conditions = gLogCondition;

	var checkType = $("cboChecked").value;

	if (checkType == "") {
		gEventSqlQuery.UnionQuery = new JetsenWeb.UnionQuery(gLogSqlQuery,
				JetsenWeb.QueryUnionType.UnionAll);
		gUnionSqlQuery.QueryTable = JetsenWeb.extend(
				new JetsenWeb.QueryTable(), {
					TableName : gEventSqlQuery.toXml(),
					AliasName : "aeu"
				});
		gUnionSqlQuery.Conditions.SqlConditions = [];
		gUnionSqlQuery.ResultFields = "*";
		gSqlQuery = gUnionSqlQuery;
	} else if (checkType == "2" || checkType == "3") {
		gLogSqlQuery.OrderString = gAlarmEventPage.orderBy;
		gSqlQuery = gLogSqlQuery;
	} else if (checkType == "0" || checkType == "1") {
		gEventSqlQuery.UnionQuery = null;
		gEventSqlQuery.OrderString = gAlarmEventPage.orderBy;
		gSqlQuery = gEventSqlQuery;
	}

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for ( var i = 0; i < nodes.length; i++) {
			var collTime = parseFloat(valueOf(nodes[i]
					.selectSingleNode("COLL_TIME"), "text", "0"));
			var resumeTime = parseFloat(valueOf(nodes[i]
					.selectSingleNode("RESUME_TIME"), "text", "0"));
			var duration = parseFloat(valueOf(nodes[i]
					.selectSingleNode("EVENT_DURATION"), "text", "0"));
			if (collTime != 0) {
				nodes[i].selectSingleNode("COLL_TIME").text = new Date(collTime)
						.toDateTimeString();
			}
			if (resumeTime != 0) {
				nodes[i].selectSingleNode("RESUME_TIME").text = new Date(
						resumeTime).toDateTimeString();
			}
			if (duration != 0) {
				nodes[i].selectSingleNode("EVENT_DURATION").text = new Date(
						duration - 8 * 60 * 60 * 1000).toTimeString();
			}
		}
		$('divAlarmEventList').innerHTML = JetsenWeb.Xml._transformXML(
				"xslt/alarmlog.xslt", xmlDoc);
		gGridList.bind($('divAlarmEventList'), $('tabAlarmLog'));
		gAlarmEventPage.setRowCount($('hid_AlarmEventCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function searchAlarmEvent() {
	gEventQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENT", "ae");
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r",
		"r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS",
		"ac", "r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o",
		"o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMLOG", "p",
		"p.ALARMEVT_ID=ae.ALARMEVT_ID", JetsenWeb.TableJoinType.Inner));
gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER", "u",
		"p.USERID=u.ID", JetsenWeb.TableJoinType.Left));
if ($("cbo_ObjGroup").value != "") {
			gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable(
					"BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID",
					JetsenWeb.TableJoinType.Left));
			gEventQueryTable.addJoinTable(JetsenWeb.createJoinTable(
					"BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID",
					JetsenWeb.TableJoinType.Left));
		}

gEventSqlQuery.QueryTable = gEventQueryTable;

    gLogQueryTable = JetsenWeb.createQueryTable("BMP_ALARMEVENTLOG", "ae");
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "r",
		"r.OBJATTR_ID=ae.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac",
		"r.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o",
		"o.OBJ_ID=ae.OBJ_ID", JetsenWeb.TableJoinType.Left));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARMLOG", "p",
		"p.ALARMEVT_ID=ae.ALARMEVT_ID", JetsenWeb.TableJoinType.Inner));
gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER", "u",
		"p.USERID=u.ID", JetsenWeb.TableJoinType.Left));
if ($("cbo_ObjGroup").value != "") {
			gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable(
					"BMP_OBJ2GROUP", "o2g", "o2g.OBJ_ID=o.OBJ_ID",
					JetsenWeb.TableJoinType.Left));
			gLogQueryTable.addJoinTable(JetsenWeb.createJoinTable(
					"BMP_OBJGROUP", "og", "og.GROUP_ID=o2g.GROUP_ID",
					JetsenWeb.TableJoinType.Left));
		}
		
		gLogSqlQuery.QueryTable = gLogQueryTable;
		
	gAlarmEventCondition.SqlConditions = [];
	gEventCondition.SqlConditions = [];
	gLogCondition.SqlConditions = [];

	if ($("cbo_ObjGroup").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"og.GROUP_ID", $("cbo_ObjGroup").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"og.GROUP_ID", $("cbo_ObjGroup").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"og.GROUP_ID", $("cbo_ObjGroup").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	if ($("chkSDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtSDate").value+" " + $("txtSTime").value
				, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtSDate").value +" " + $("txtSTime").value
				, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtSDate").value +" " + $("txtSTime").value
						, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("chkEDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtEDate").value +" " + $("txtETime").value
						, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtEDate").value+" " + $("txtETime").value
				, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"OPERATE_TIME", $("txtEDate").value +" " + $("txtETime").value
				, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("txtCheckUser").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"u.USER_NAME", $("txtCheckUser").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"u.USER_NAME", $("txtCheckUser").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"u.USER_NAME", $("txtCheckUser").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}
	if ($("txtAlarmEvtId").value != "") {
		 /*var   type="^[0-9]*[1-9][0-9]*$";   
	     var   re   =   new   RegExp(type);   
	     if($("txtAlarmEvtId").value.match(re)==null)   
	     {   
	    	 jetsennet.alert( "请输入大于零的整数！");   
	         return;   
	     }*/   

		if(isNaN($("txtAlarmEvtId").value))
		{
			jetsennet.alert("编号应为数字！");
			return;
		}
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ae.ALARMEVT_ID", $("txtAlarmEvtId").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ae.ALARMEVT_ID", $("txtAlarmEvtId").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ae.ALARMEVT_ID", $("txtAlarmEvtId").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}
	if ($("txtObjName").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"o.OBJ_NAME", $("txtObjName").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"o.OBJ_NAME", $("txtObjName").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"o.OBJ_NAME", $("txtObjName").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}
	if ($("cboAlarmLevel").value != "") {
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ALARM_LEVEL", $("cboAlarmLevel").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ALARM_LEVEL", $("cboAlarmLevel").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ALARM_LEVEL", $("cboAlarmLevel").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	if ($("cboChecked").value != "") { // && $("cboChecked").value != "2"
		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"EVENT_STATE", $("cboChecked").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"EVENT_STATE", $("cboChecked").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"EVENT_STATE", $("cboChecked").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	if ($("cbo_Attribute").value != "") {
        gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("r.ATTRIB_ID", $("cbo_Attribute").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("r.ATTRIB_ID", $("cbo_Attribute").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
        gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("r.ATTRIB_ID", $("cbo_Attribute").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
	if ($("cbo_AttrType").value != "") {
		var attrType = $("cbo_AttrType").value;
		var arrrClassId;
    	switch (attrType)
    	{
	    	case "1":
	    		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 30, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	            gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 30, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	            gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 30, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	    		break;
	    	case "2":
	    		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 12, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	            gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 12, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	            gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 12, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	    		break;
	    	case "3":
	    		gAlarmEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 2000, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
	            gEventCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 2000, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
	            gLogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ac.CLASS_ID", 2000, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
	    		break;
    	}
	}
	gAlarmEventPage.currentPage = 1;
	loadAlarmEvent();
}
//删除=====================================================================================
function deleteAlarmEvent(keyId) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadAlarmEvent();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ALARMEVENT", keyId ]);
}
//初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	// 查询两小时内的报警
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();

	$("cboChecked").value = "";

	objGroupInit();
	//attributeInit();
	searchAlarmEvent();

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
			height : 30
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

	attributeInit();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

// 查看处理意见
function viewCheckDesc(eventId) {
	var areaElements = JetsenWeb.Form.getElements("divCheckAlarmEvent");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARMEVT_ID",
			eventId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "ALARMEVT_ID",
		PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), {
			TableName : "BMP_ALARMEVENTLOG"
		})
	});
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objCheckDesc = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_CHECK_DESC").value = valueOf(objCheckDesc, "CHECK_DESC", "");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, {
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 500,
			height : 160
		},
		title : "报警处理",
		cancelButtonText : "关闭"
	});
	dialog.controls = [ "divCheckAlarmEvent" ];
	dialog.onsubmit = function() {
		return false;
	};
//	$("txt_CHECK_DESC").readOnly = true;
	$("txt_CHECK_DESC").disabled = true;
	dialog.showDialog();
}

// 弹出选择导出列
function chooseField() {
	if ($("txtAlarmEvtId").value != "")
	{
		if(isNaN($("txtAlarmEvtId").value))
		{
			jetsennet.alert("编号应为数字！");
			return;
		}
	}
	
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


// 初始化对象组
function objGroupInit() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "og");
	
	var condition = new JetsenWeb.SqlConditionCollection();

	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE",
			"1,6", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));

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

// 初始化报警属性
function attributeInit() {
    var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "a");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "ac", "a.ATTRIB_ID=ac.ATTRIB_ID", JetsenWeb.TableJoinType.Inner));
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "c", "ac.CLASS_ID=c.CLASS_ID", JetsenWeb.TableJoinType.Inner));
    var condition = new JetsenWeb.SqlConditionCollection();
    var attrType = $("cbo_AttrType").value;
    if (attrType != "") {
    	switch (attrType)
    	{
	    	case "1":
	    		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 30, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	    		break;
	    	case "2":
	    		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 12, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	    		break;
	    	case "3":
	    		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 2000, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
	    		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_LEVEL", "102,103,104,105,106,107", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	    		break;
    	}
    }
    else
    {
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 30, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 12, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    	var subCondition = JetsenWeb.SqlCondition.create();
    	subCondition.SqlLogicType = JetsenWeb.SqlLogicType.Or;
    	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_ID", 2000, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
    	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.CLASS_LEVEL", "102,103,104,105,106,107", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
    	condition.SqlConditions.push(subCondition);
    }
    JetsenWeb.extend(gSqlQuery, { IsPageResult: 0, KeyId: "ATTRIB_ID", QueryTable: gQueryTable, Conditions: condition, ResultFields: "DISTINCT a.ATTRIB_ID,ATTRIB_NAME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var cboAttribute = $("cbo_Attribute");
        cboAttribute.length = 0;
        cboAttribute.options.add(new Option("请选择", ""));

        var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (records) {
            var length = records.length;
            for (var i = 0; i < length; i++) {
                var attribInfo = records[i];
                cboAttribute.options.add(new Option(attribInfo["ATTRIB_NAME"], attribInfo["ATTRIB_ID"]));
            }
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

function deleteAlarmLogMany()
{
	var alarmEventIds = JetsenWeb.Form.getCheckedValues("chkAlarmLog");
	if(alarmEventIds.length == 0) {
		jetsennet.alert("请选择要删除的日志记录！");
		return;
	}
   jetsennet.confirm("确定删除？", function () 
		   {
			   var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			   ws.soapheader = JetsenWeb.Application.authenticationHeader;
			   ws.async = false;
			   ws.onerror = function(ex){ jetsennet.alert(ex);};
			   ws.oncallback = function(sResult)
			   {	
				   loadAlarmEvent();
			   }
			   ws.call("bmpObjDeleteMany",["BMP_ALARMLOG",alarmEventIds]);  
			   return true;
		 }
   );
}



//导出数据
function exportData(fieldsString) {
    var formExportData = $("formExportData");
    formExportData.reset();
    if ($("cbo_ObjGroup").value != "")
    {
		$("og.GROUP_ID").value = $("cbo_ObjGroup").value;
    }
	if ($("chkSDate").checked && $("txtSDate").value != "" && $("txtSTime").value != "")
	{
		$("COLL_TIME_START").value = $("txtSDate").value + " " + $("txtSTime").value;
	}
	if ($("chkEDate").checked && $("txtEDate").value != "" && $("txtETime").value != "")
	{
		$("COLL_TIME_END").value = $("txtEDate").value + " " + $("txtETime").value;
	}
	if ($("txtCheckUser").value != "")
	{
		$("CHECK_USER").value = $("txtCheckUser").value;
	}
	if ($("txtAlarmEvtId").value != "")
	{
		$("ALARMEVT_ID").value = $("txtAlarmEvtId").value;
	}
	if ($("txtObjName").value != "")
	{
		$("o.OBJ_NAME").value = $("txtObjName").value;
	}
	if ($("cboAlarmLevel").value != "")
	{
		$("ALARM_LEVEL").value = $("cboAlarmLevel").value;
	}
	if ($("cboChecked").value != "")
	{
		$("EVENT_STATE").value = $("cboChecked").value;
	}
	if ($("cbo_Attribute").value != "")
	{
		$("r.ATTRIB_ID").value = $("cbo_Attribute").value;
	}
	if ($("cbo_AttrType").value != "") 
	{
		$("ac.CLASS_ID").value = $("cbo_AttrType").value;
    }
    if (fieldsString && fieldsString != "")
    {
    	$("ResultFields").value = decodeURIComponent(fieldsString);
	}
    formExportData.submit();
}
