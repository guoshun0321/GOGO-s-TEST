//获取配置信息和表格类型的属性分组=====================================================================================
function getAttribClass(classId, noEmpty)
{
	var control = $('cbo_CLASS');
	control.options.length = 0;
	if (!noEmpty)
		control.options[0] = new Option("请选择", "");

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("A.CLASS_LEVEL","101,106",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric,true));
	if(classId!="")
	{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("B.PARENT_ID",classId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	}
	
	
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "A");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS","B","A.CLASS_ID=B.CLASS_ID",JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "CLASS_ID",
		PageInfo : null,
		QueryTable : queryTable,
		ResultFields : "A.CLASS_ID,A.CLASS_NAME"
	});
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(resultXml) {
		var typeObjs = JetsenWeb.Xml
				.toObject(resultXml.resultVal, "Record");
		if (typeObjs != null) {
			for ( var i = 0; i < typeObjs.length; i++) {
				var option = new Option(valueOf(typeObjs[i], "CLASS_NAME",
						""), valueOf(typeObjs[i], "CLASS_ID", ""));
				control.options[control.options.length] = option;
			}
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex + "！");
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

//根据属性分类过滤配置信息和表格信息下的对象属性
function onAttribClassChanged()
{
	var classId = $('cbo_CLASS').value;
	
	getObjAttribute(curObjId, '101,106', classId);
}

//根据属性类型过滤Trap信息、信号信息、Syslog信息
function onAttribTypeChange()
{
	var type = $('cbo_TYPE').value;
	getObjAttribute(curObjId, type, '');
}

//删除对象属性
function deleteObjAttr(objattrId, attrType)
{
	jetsennet.confirm("确定删除?", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	if(attrType == '101,106')
    	{
    		onAttribClassChanged();
    	}
    	if(attrType == '104,105,107')
    	{
    		onAttribTypeChange();
    	}
    	else
    	{
    		refreshObjAttrList(attrType);
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjDelete", ["BMP_OBJATTRIB", objattrId]);
	return true;
	});
}

//编辑自定义对象属性
function editSelfDefObjAttr(objattrId, objattrName, isVisible, strValue, attrType)
{
	var areaElements = JetsenWeb.Form.getElements('divEditSelfDefObjattr');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
     
	setSelectedValue($("sel_IS_VISIBLE2"), isVisible);
	$("txt_STR_VALUE").value = strValue;
    var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "");
	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJATTR_ID", objattrId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId : "", QueryTable : queryTable, Conditions : conditions });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objClassIdentify = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("hid_OBJATTR_ID2").value = valueOf(objClassIdentify, "OBJATTR_ID", "");
		$("txt_OBJATTR_NAME2").value = valueOf(objClassIdentify, "OBJATTR_NAME", "");
//		$("txt_STR_VALUE").value = valueOf(objClassIdentify, "ATTRIB_VALUE", "");
		$("txt_OBJATTR_PARM0").value = valueOf(objClassIdentify, "ATTRIB_PARAM", "");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
//	$("hid_OBJATTR_ID2").value = objattrId;
//	$("txt_OBJATTR_NAME2").value = objattrName;

	
	var dialog = new JetsenWeb.UI.Window("show-divEditSelfDefObjattr-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 180 }, title: "编辑对象属性" });
	dialog.controls = ["divEditSelfDefObjattr"];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjAttr = {
					OBJATTR_ID: $("hid_OBJATTR_ID2").value
					, OBJ_ID: curObjId
					, OBJATTR_NAME: $("txt_OBJATTR_NAME2").value
					, STR_VALUE: $("txt_STR_VALUE").value
					, IS_VISIBLE: $("sel_IS_VISIBLE2").value
					, ATTRIB_PARAM: $("txt_OBJATTR_PARM0").value
					
	            };
			
			 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	         ws.soapheader = JetsenWeb.Application.authenticationHeader;
	         ws.oncallback = function (ret) {
	        	 refreshObjAttrList(attrType);
	             JetsenWeb.UI.Windows.close("show-divEditSelfDefObjattr-win");
	         };
	         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
		}
	};
	dialog.showDialog();
}

//编辑没有轮询间隔的对象属性，一般有配置信息、表格信息
function editNoSpanObjAttr(objattrId, objattrName, dataEncoding, isVisible, attrType)
{
	var areaElements = JetsenWeb.Form.getElements('divEditNoTimeSpanObjattr');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
	$("hid_OBJATTR_ID0").value = objattrId;
	$("txt_OBJATTR_NAME0").value = objattrName;
	setSelectedValue($("sel_DATA_ENCODING0"), dataEncoding);
	setSelectedValue($("sel_IS_VISIBLE0"), isVisible);
	
	var dialog = new JetsenWeb.UI.Window("show-EidtNoSpanObjAttr-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 180 }, title: "编辑对象属性" });
	dialog.controls = ["divEditNoTimeSpanObjattr"];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjAttr = {
					OBJATTR_ID: $("hid_OBJATTR_ID0").value
					, OBJATTR_NAME: $("txt_OBJATTR_NAME0").value
					, DATA_ENCODING: $("sel_DATA_ENCODING0").value
					, IS_VISIBLE: $("sel_IS_VISIBLE0").value
	            };
			
			 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	         ws.soapheader = JetsenWeb.Application.authenticationHeader;
	         ws.oncallback = function (ret) {
	        	 refreshObjAttrList(attrType);
	             JetsenWeb.UI.Windows.close("show-EidtNoSpanObjAttr-win");
	         };
	         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
		}
	};
	dialog.showDialog();
}

//编辑有对象值的对象属性，一般有trap、信号
function editValueObjAttr(objattrId, objattrName, objattrValue, attrType)
{
	var areaElements = JetsenWeb.Form.getElements('divEditValueObjattr');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
  
	$("hid_OBJATTR_ID3").value = objattrId;
	$("txt_OBJATTR_NAME3").value = objattrName;
	$("txt_OBJATTR_VALUE3").value = objattrValue;
	
	var dialog = new JetsenWeb.UI.Window("show-EidtNoSpanObjAttr-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 180 }, title: "编辑对象属性" });
	dialog.controls = ["divEditValueObjattr"];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjAttr = {
					OBJATTR_ID: $("hid_OBJATTR_ID3").value
					, OBJATTR_NAME: $("txt_OBJATTR_NAME3").value
					, ATTRIB_VALUE: $("txt_OBJATTR_VALUE3").value
	            };
			
			 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	         ws.soapheader = JetsenWeb.Application.authenticationHeader;
	         ws.oncallback = function (ret) {
	        	 refreshObjAttrList(attrType);
	             JetsenWeb.UI.Windows.close("show-EidtNoSpanObjAttr-win");
	         };
	         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
		}
	};
	dialog.showDialog();
}

//编辑有轮询间隔的对象属性，一般有监测信息、性能信息
function editTimeSpanObjAttr(objattrId, objattrName, dataEncoding, isVisible, timeSpan, attrType)
{
	var areaElements = JetsenWeb.Form.getElements('divEditTimeSpanObjattr');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("textParamName").innerHTML = "参数：";
	$("hid_OBJATTR_ID1").value = objattrId;
	$("txt_OBJATTR_NAME1").value = objattrName;
	$("sel_DATA_ENCODING1").value = dataEncoding;
//	setSelectedValue($("sel_DATA_ENCODING1"), dataEncoding);
	setSelectedValue($("sel_IS_VISIBLE1"), isVisible);
	$("txt_COLL_TIMESPAN1").value = timeSpan;
	
	var dialog = new JetsenWeb.UI.Window("show-EidtTimeSpanObjAttr-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 180 }, title: "编辑对象属性" });
	dialog.controls = ["divEditTimeSpanObjattr"];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjAttr = {
					OBJATTR_ID: $("hid_OBJATTR_ID1").value
					, OBJATTR_NAME: $("txt_OBJATTR_NAME1").value
					, DATA_ENCODING: $("sel_DATA_ENCODING1").value
					, IS_VISIBLE: $("sel_IS_VISIBLE1").value
					, COLL_TIMESPAN: getSelectedValue($("txt_COLL_TIMESPAN1"))
	            }; 
			 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	         ws.soapheader = JetsenWeb.Application.authenticationHeader;
	         ws.oncallback = function (ret) {
	        	 refreshObjAttrList(attrType);
	             JetsenWeb.UI.Windows.close("show-EidtTimeSpanObjAttr-win");
	         };
	         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
		}
	};
	dialog.showDialog();
}

function editTimeSpanObjAttr(objattrId, attrType)
{
	var type ;
	var areaElements = JetsenWeb.Form.getElements('divEditTimeSpanObjattr');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "");
	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJATTR_ID", objattrId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId : "", QueryTable : queryTable, Conditions : conditions });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var objClassIdentify = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("hid_OBJATTR_ID1").value = valueOf(objClassIdentify, "OBJATTR_ID", "");
		$("txt_OBJATTR_NAME1").value = valueOf(objClassIdentify, "OBJATTR_NAME", "");	
		$("sel_DATA_ENCODING1").value = valueOf(objClassIdentify, "DATA_ENCODING", "")
//		setSelectedValue($("sel_DATA_ENCODING1"), valueOf(objClassIdentify, "DATA_ENCODING", ""));
		setSelectedValue($("sel_IS_VISIBLE1"), valueOf(objClassIdentify, "IS_VISIBLE", ""));
		$("txt_COLL_TIMESPAN1").value = valueOf(objClassIdentify, "COLL_TIMESPAN", "");
		type = valueOf(objClassIdentify, "ATTRIB_TYPE", "");
		if(type == '104'){
			$("textParamName").innerHTML = "Trap标识：";
			$("txt_OBJATTR_PARM1").value = valueOf(objClassIdentify, "ATTRIB_VALUE", "");		
		}else{
			$("textParamName").innerHTML = "参数：";
			$("txt_OBJATTR_PARM1").value = valueOf(objClassIdentify, "ATTRIB_PARAM", "");		
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("show-EidtTimeSpanObjAttr-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 210 }, title: "编辑对象属性" });
	dialog.controls = ["divEditTimeSpanObjattr"];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjAttr = {
					OBJATTR_ID: $("hid_OBJATTR_ID1").value
					, OBJATTR_NAME: $("txt_OBJATTR_NAME1").value
					, DATA_ENCODING: $("sel_DATA_ENCODING1").value
					, IS_VISIBLE: $("sel_IS_VISIBLE1").value
					, COLL_TIMESPAN: getSelectedValue($("txt_COLL_TIMESPAN1"))
	            }; 
			 if(attrType == '104'){
				 oObjAttr.ATTRIB_VALUE = $("txt_OBJATTR_PARM1").value;
			 }else{
				 oObjAttr.ATTRIB_PARAM = $("txt_OBJATTR_PARM1").value;
			 }
			 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	         ws.soapheader = JetsenWeb.Application.authenticationHeader;
	         ws.oncallback = function (ret) {
	        	 refreshObjAttrList(attrType);
	             JetsenWeb.UI.Windows.close("show-EidtTimeSpanObjAttr-win");
	         };
	         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
		}
	};
	dialog.showDialog();
}


//解除关联
function bindNoAlarm(type_value)
{
	var type = type_value;
	var selectType = $('cbo_TYPE').value;
	var objattrId2alarm = null;
	if(type=="102")
	{
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
	}
	else if(type=="103")
	{
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm103");
	}
	else if(type == "104,105,107")
	{
		if(selectType == "104")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm104");
			type = "104";
		}
		else if(selectType == "105")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm105");
			type = "105";
		}
		else if(selectType == "107")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm107");
			type = "107";
		}
		else
		{
			objattrId2alarm = "error";
		}
	}
	
	if(objattrId2alarm==null || objattrId2alarm.length==0)
	{
		jetsennet.alert("请选择要解除的对象属性！");
		return;
	}
	else if(objattrId2alarm == "error")
	{
		jetsennet.alert("请选择分类");
		return;
	}
		
	var isNullItem = 0;
	for(var i=0; i<objattrId2alarm.length; i++)
	{
		var e = $("hiddenObjAttr2Alarm" + objattrId2alarm[i]);
		if(e.value == 0)
		{
			isNullItem += 1;
		}
	}	
	if(isNullItem == objattrId2alarm.length)
	{
		jetsennet.alert("所选项没有关联报警！");
		return;
	}

	jetsennet.confirm("确定解除关联？", function () 
	{
		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			refreshObjAttrList(type);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpDeleteAttribAlarm",[objattrId2alarm.join(",")]);
		return true;
	});
}

//关联告警对话框
function attrib2alarm(type_value)
{
	var type = type_value;
	var selectType = $('cbo_TYPE').value;
	var objattrId2alarm = null;
	if(type=="102")
	{
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
	}
	else if(type=="103")
	{
		objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm103");
	}
	else if(type == "104,105,107")
	{
		if(selectType == "104")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm104");
			type = "104";
		}
		else if(selectType == "105")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm105");
			type = "105";
		}
		else if(selectType == "107")
		{
			objattrId2alarm = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm107");
			type = "107";
		}
		else
		{
			objattrId2alarm = "error";
		}
		
	}
	
	if(objattrId2alarm==null || objattrId2alarm.length==0)
	{
		jetsennet.alert("请选择要关联的对象属性！");
		return;
	}
	else if(objattrId2alarm == "error")
	{
		jetsennet.alert("请选择分类");
		return;
	}
	loadAlarm(type,false);
	var areaElements = JetsenWeb.Form.getElements("divObjAttrib2Alarm");

	JetsenWeb.Form.checkAllItems('chkAllAlarms',false);
//	$("lblAlarmDesc").innerText = "";
	$("lblCheckNum").innerText = "";
	$("lblOverNum").innerText = "";
//	$("lblCheckSpan").innerText = "";
	loadAlarmLevel("");

	
	var dialog = new JetsenWeb.UI.Window("edit-divObjAttrib2Alarm-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 700,
			height : 350
		},
		title : "报警关联"
	});
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
				JetsenWeb.UI.Windows.close("edit-divObjAttrib2Alarm-win");
				refreshObjAttrList(type);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpAddAttribAlarm",[curAlarmId, objattrId2alarm.join(",")]);
		} else {
			JetsenWeb.UI.Windows.close("edit-divObjAttrib2Alarm-win");
		}

	};

	dialog.showDialog();
	
}

//加载告警
function loadAlarm(controlId, noEmpty) {

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.ALARM_ID", "10", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));

	if(controlId == '103'){
		  var condition2 = new JetsenWeb.SqlCondition();
		  condition2.SqlLogicType = jetsennet.SqlLogicType.And;
		  condition2.SqlConditions=[];
		  condition2.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"t.CHECK_SPAN", 1, JetsenWeb.SqlLogicType.Or,
				JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));
		  condition2.SqlConditions.push(JetsenWeb.SqlCondition.create(
					"t.CHECK_SPAN", "", JetsenWeb.SqlLogicType.And,
					JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
		  condition.SqlConditions.push(condition2);
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARM", "t");
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "ALARM_ID",
		PageInfo : null,
		QueryTable : queryTable,
		ResultFields : "t.*"
	});
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
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function alarmChanged() {
	var alarmId = $('cbo_Alarm').value;

	if (alarmId != null && alarmId != "") {
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"ALARM_ID", alarmId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));

		var sqlQuery = new JetsenWeb.SqlQuery();
		JetsenWeb.extend(sqlQuery, {
			IsPageResult : 0,
			KeyId : "ALARM_ID",
			PageInfo : null,
			QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), {
				TableName : "BMP_ALARM"
			})
		});
		sqlQuery.Conditions = condition;

		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			var objAlarm = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
//			$("lblAlarmDesc").innerText = valueOf(objAlarm, "ALARM_DESC", "");
			$("lblCheckNum").innerText = valueOf(objAlarm, "CHECK_NUM", "");
			$("lblOverNum").innerText = valueOf(objAlarm, "OVER_NUM", "");
//			$("lblCheckSpan").innerText = valueOf(objAlarm, "CHECK_SPAN", "");

			loadAlarmLevel(alarmId);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	} else {
//		$("lblAlarmDesc").innerText = "";
		$("lblCheckNum").innerText = "";
		$("lblOverNum").innerText = "";
//		$("lblCheckSpan").innerText = "";

		loadAlarmLevel("");
	}
}

//加载告警级别列表
function loadAlarmLevel(alarmId) {
	if(alarmId == "") {
		return;
	}
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMLEVEL", "l");
	
	var gAlarmLevelCondition = new JetsenWeb.SqlConditionCollection();
	gAlarmLevelCondition.SqlConditions.push(JetsenWeb.SqlCondition
			.create("l.ALARM_ID", alarmId, JetsenWeb.SqlLogicType.And,
					JetsenWeb.SqlRelationType.Equal,
					JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery, {
		KeyId : "",
		QueryTable : gQueryTable,
		Conditions : gAlarmLevelCondition,
		ResultFields : "l.*"
	});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var gAlarmLevelGridList2 = new JetsenWeb.UI.GridList();
		$("divAlarmLevel").innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/alarmlevelview.xsl", ret.resultVal);
		gAlarmLevelGridList2.bind($("divAlarmLevel"), $("tabAlarmLevel"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
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

//	$("lblAlarmDesc").innerText = desc;
	$("lblCheckNum").innerText = checkNum;
	$("lblOverNum").innerText = overNum;
//	$("lblCheckSpan").innerText = checkSpan;
	loadAlarmLevel(alarmId);
}











