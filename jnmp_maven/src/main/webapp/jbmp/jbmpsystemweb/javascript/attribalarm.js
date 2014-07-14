JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
			"js_validate", "js_pageframe" ]);
	var gFrame;
	var gWindowSizeChangedInterVal;
	var gObjAttribPage = new JetsenWeb.UI.PageBar("ObjAttrib");
	gObjAttribPage.onpagechange = function() {
		//loadObjAttrib();
	};
	gObjAttribPage.orderBy = "Order By oa.OBJATTR_ID";
	gObjAttribPage.onupdate = function() {
		$('divObjAttribAlarmPage').innerHTML = this.generatePageControl();
	};
	var gObjAttribCondition = new JetsenWeb.SqlConditionCollection();
	var gGridList = new JetsenWeb.UI.GridList();
	gGridList.ondatasort = function(sortfield, desc) {
		gObjAttribPage.setOrderBy(sortfield, desc);
	};
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "oa");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "o",
			"oa.OBJ_ID=o.OBJ_ID", JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE", "ab",
			"ab.ATTRIB_ID=oa.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBALARM", "am",
			"am.OBJATTR_ID=oa.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM", "r",
			"r.ALARM_ID=am.ALARM_ID", JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(gSqlQuery, {
		IsPageResult : 1,
		KeyId : "",
		PageInfo : gObjAttribPage,
		QueryTable : gQueryTable,
		ResultFields : "oa.*,o.OBJ_NAME,ab.ATTRIB_NAME,r.ALARM_ID,r.ALARM_NAME"
	});
	
	
	var attribListPage = new JetsenWeb.UI.PageBar("attribListPage");
	attribListPage.orderBy = "ORDER BY A.OBJATTR_ID";
	attribListPage.onpagechange = function()
	{
		queryObjAttrib();
	}
	attribListPage.onupdate = function() 
	{
		$('divAttribListPage').innerHTML = this.generatePageControl();
	}
	var myGridList = new JetsenWeb.UI.GridList();
	myGridList.ondatasort = function(sortfield, desc) {
		attribListPage.setOrderBy(sortfield, desc);
	};
	
	var gAlarmLevelCondition = new JetsenWeb.SqlConditionCollection();
	
	
	var obj; //保存加载的第一个对象
	var currentObj = null;// 当前选中的对象
	var isMultiple = false;
	var currentAttrib;

	//加载=====================================================================================
	function loadObjAttrib() {
		gSqlQuery.OrderString = gObjAttribPage.orderBy;
		gSqlQuery.Conditions = gObjAttribCondition;

		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			var xmlDoc = new JetsenWeb.XmlDoc();
			xmlDoc.loadXML(ret.resultVal);

			$('divObjAttribAlarmList').innerHTML = JetsenWeb.Xml._transformXML(
					"xslt/attribalarm.xslt", xmlDoc);
			gGridList.bind($('divObjAttribAlarmList'), $('tabObjAttrib'));
			gObjAttribPage.setRowCount($('hid_ObjAttribCount').value);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	}
	function searchObjAttrib(objId)
    {
        gObjAttribCondition.SqlConditions = [];
        if (objId)
        {
			gObjAttribCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("oa.OBJ_ID",objId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
        }
        else
        {
		if ($("cboObjectType").value != "")
		{
			gObjAttribCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.CLASS_ID",$("cboObjectType").value,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
			gObjAttribCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("oa.OBJ_ID",obj,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
		}
        }
        gObjAttribPage.currentPage = 1;
        loadObjAttrib();
    }
	
	//初始化===================================================================================
	function pageInit() {
		attributeClassInit();
		loadAttribute("-1", "-1");
		searchObjAttribWithAttrId(-1);
		parent.document.getElementById("spanWindowName").innerHTML = document.title;
		gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
			splitType : 0,
			fixControlIndex : 0,
			enableResize : true
		});

		var gLeftFrame = new JetsenWeb.UI.PageFrame("divLeftFrame");
        gLeftFrame.splitType = 1;
        gLeftFrame.fixControlIndex = 0;
        gLeftFrame.enableResize = true;
        gLeftFrame.showSplit = 0;
        gLeftFrame.size = { width: 230, height: 0 };

        var frameSearch = new JetsenWeb.UI.PageItem("divSearchObject");
        frameSearch.size = { width: 0, height: 90 };
        var frameChan = new JetsenWeb.UI.PageItem("divObject");

        gLeftFrame.addControl(frameSearch);
        gLeftFrame.addControl(frameChan);
		
        var gRitghtFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divRight"), {
			splitType : 1,
			fixControlIndex : 1,
			enableResize : false,
			splitBorder:0,
			showSplit : false
		});
        
		var gRightTitle = JetsenWeb.extend(new JetsenWeb.UI.PageItem("divListTitle"), { size: { width: 0, height: 27} });
		var gRightContentList = new JetsenWeb.UI.PageItem("divObjAttribAlarmList");
		var RightContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divRightContent"), {
			splitType : 1,
			fixControlIndex : 0,
			enableResize : false,
			splitBorder:0,
			showSplit : false
		});
		RightContent.addControl(gRightTitle);
		RightContent.addControl(gRightContentList);
		gRitghtFrame.addControl(RightContent);
        gRitghtFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));
        gFrame.addControl(gLeftFrame);
        gFrame.addControl(gRitghtFrame);
        
		window.onresize = function() {
			if (gWindowSizeChangedInterVal != null)
				window.clearTimeout(gWindowSizeChangedInterVal);
			gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
		};
		windowResized();
		//加载告警
		//loadAlarm();
		var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true});
	    alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"),{ size: { width: 210, height: 0} }));
	    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
	    alarmContent.size = { width: 680, height: 285 };
	    alarmContent.resize();
	}

	function loadSNMPAlarm(controlId, noEmpty) {
		var control = $(controlId);
		control.options.length = 0;
		if (!noEmpty)
			control.options[0] = new Option("请选择", "");

		var condition = new JetsenWeb.SqlConditionCollection();
		//condition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.ALARM_TYPE", "1006", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

		var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_ALARM", "t");
		JetsenWeb.extend(sqlQuery, {
			IsPageResult : 0,
			KeyId : "ALARM_ID",
			PageInfo : null,
			QueryTable : queryTable,
			ResultFields : "t.ALARM_NAME,t.ALARM_ID"
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
					var option = new Option(valueOf(typeObjs[i], "ALARM_NAME",
							""), valueOf(typeObjs[i], "ALARM_ID", ""));
					control.options[control.options.length] = option;
				}
			}
		}
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	}

	function windowResized() {
		var size = JetsenWeb.Util.getWindowViewSize();
		gFrame.size = {
			width : size.width,
			height : size.height
		};
		gFrame.resize();
	}

	function bindObjAttrib2Alarm() {
		var objattrIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
		if (objattrIds.length == 0) {
			jetsennet.alert("请选择要关联报警的项！");
			return;
		}
		
		var areaElements = JetsenWeb.Form.getElements("divObjAttrib2Alarm");
		JetsenWeb.Form.checkAllItems('chkAllAlarms', false);

		//$('hid_OBJATTR_ID').value = objattrIds;
//		$("lblAlarmDesc").innerText = "";
		$("lblCheckNum").innerText = "";
		$("lblOverNum").innerText = "";
//		$("lblCheckSpan").innerText = "";
		loadAlarm();
		loadAlarmLevel("");

		var dialog = new JetsenWeb.UI.Window("edit-object-win");
		JetsenWeb.extend(dialog, {
			submitBox : true,
			cancelBox : true,
			windowStyle : 1,
			maximizeBox : true,
			minimizeBox : true,
			size : {
				width : 700,
				height : 370
			},
			title : "选择报警规则"
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
					searchObjAttribWithAttrId(currentAttrib);
					JetsenWeb.UI.Windows.close("edit-object-win");
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex + "！");
				};
				ws.call("bmpAddAttribAlarm", [ curAlarmId, objattrIds.join(",") ]);
			} else {
				JetsenWeb.UI.Windows.close("edit-object-win");
			}
		};

		dialog.showDialog();
	}

	// 加载告警级别列表
	function loadAlarmLevel(alarmId) {
		if (alarmId == "") {
			return;
		}
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("BMP_ALARMLEVEL", "l");

		var gAlarmLevelCondition = new JetsenWeb.SqlConditionCollection();
		gAlarmLevelCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"l.ALARM_ID", alarmId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
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
			jetsennet.error(ex + "！");
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
		gAlarmLevelCondition.SqlConditions = [];
	}

	//解除关联的告警
	function bindNoAlarm(objattrIds, objectIds, multiple) {
		objattrIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
		var isNullItem = 0;
		if (objattrIds.length == 0) {
			jetsennet.alert("请选择要解除关联的项！");
			return;
		}
		
		for(var i=0; i<objattrIds.length; i++)
		{
			var e = $("hiddenObjAttr2Alarm" + objattrIds[i]);
			if(e.value == "")
			{
				isNullItem += 1;
			}
		}
		if(isNullItem == objattrIds.length)
		{
			jetsennet.alert("所选项没有关联报警！");
			return;
		}
		
		objattrIds = objattrIds.join(",");
		jetsennet.confirm("确定解除关联？", function ()
		{
			var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				searchObjAttribWithAttrId(currentAttrib);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpDeleteAttribAlarm", [ objattrIds ]);
			return true;
		});
	}
	//加载对象类别
	function attributeClassInit() {
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
		gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "ac",
				"a.CLASS_ID=ac.CLASS_ID", JetsenWeb.TableJoinType.Inner));
		var condition = new JetsenWeb.SqlConditionCollection();
		JetsenWeb.extend(gSqlQuery, {
			IsPageResult : 0,
			KeyId : "CLASS_ID",
			QueryTable : gQueryTable,
			Conditions : condition,
			ResultFields : "DISTINCT a.CLASS_ID,a.CLASS_NAME"
		});
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var cboObjectType = $("cboObjectType");
			cboObjectType.length = 0;
			cboObjectType.options.add(new Option("请选择", "-1"));

			var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if (records) {
				var length = records.length;
				for ( var i = 0; i < length; i++) {
					var objTypeInfo = records[i];
					cboObjectType.options.add(new Option(objTypeInfo

					["CLASS_NAME"], objTypeInfo["CLASS_ID"]));
				}
			}
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	}
	
	function setCurrentObject(objId, objName) {
		currentObj = {id: objId, name: objName};
	}

	//查询属性
	var classType;
	var attribType;
	var textSearch;
	var attribSqlQuery = new JetsenWeb.SqlQuery();
	var attribQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "a");
	attribQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "b", "a.ATTRIB_ID = b.ATTRIB_ID", JetsenWeb.TableJoinType.Inner));
	attribQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "c", "c.CLASS_ID = b.CLASS_ID", JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(attribSqlQuery, { QueryTable: attribQueryTable, ResultFields: "a.*" });
	var attribQueryCondition = new JetsenWeb.SqlConditionCollection();
	function loadAttribute(classType, attribType, textSearch) {
		
		attribQueryCondition.SqlConditions = [];
		if(classType != "-1") {
			attribQueryCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID", classType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
		}
		if(attribType != "-1") {
			attribQueryCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", attribType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
		}else {
			attribQueryCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", "102,103,104,105", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Number));
		}
		if(textSearch) {
			attribQueryCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_NAME", textSearch, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
		}
		attribSqlQuery.Conditions = attribQueryCondition;

		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {
            $('divObject').innerHTML = JetsenWeb.Xml.transformXML("xslt/simpleattribute.xslt", ret.resultVal);
            new JetsenWeb.UI.GridList().bind($('divObject'), $('tabAttribute'));
        };
        ws.onerror = function (ex) { jetsennet.error(ex); };
        ws.call("bmpObjQuery", [attribSqlQuery.toXml()]);
	}

	//选择对象类别和属性类别
	function selectOption() {
		classType = getSelectedValue($("cboObjectType"));
		attribType = getSelectedValue($("cboAttribType"));
		textSearch = $("txtObject").value;
		loadAttribute(classType, attribType, textSearch);
		//$("txtObject").value = "";
	}

	//点击属性之后查询对象属性
	function searchObjAttribWithAttrId(attribId) {
		currentAttrib = attribId;
		attribListPage.currentPage = 1;
		queryObjAttrib();
		
	}
	
	function queryObjAttrib()
	{
		var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "A");
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","E","A.OBJ_ID=E.OBJ_ID",JetsenWeb.TableJoinType.Inner));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE","C","A.ATTRIB_ID=C.ATTRIB_ID",JetsenWeb.TableJoinType.Inner));
		//queryTable.addJoinTable(JetsenWeb.createJoinTable("","C","A.ATTRIB_ID=C.ATTRIB_ID",JetsenWeb.TableJoinType.Inner));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBALARM","B","A.OBJATTR_ID=B.OBJATTR_ID",JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM","D","B.ALARM_ID=D.ALARM_ID",JetsenWeb.TableJoinType.Left));
		JetsenWeb.extend(sqlQuery, { IsPageResult : 1, PageInfo : attribListPage, QueryTable: queryTable, ResultFields: " A.*,D.ALARM_ID,D.ALARM_NAME,E.OBJ_NAME" });
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions = [];
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_ID", currentAttrib, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
		sqlQuery.Conditions = condition;
		sqlQuery.OrderString = attribListPage.orderBy;

//		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {
            $('divObjAttribAlarmList').innerHTML = JetsenWeb.Xml.transformXML("xslt/objattributelist.xslt", ret.resultVal);
            myGridList.bind($('divObjAttribAlarmList'), $('inspectattributeList'));
            attribListPage.setRowCount($('hidCount').value);
        };
        ws.onerror = function (ex) { jetsennet.error(ex); };
        ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'E.OBJ_ID', '2']);
//        ws.call("bmpObjQuery", [sqlQuery.toXml()]);
	}

	//批量设置采集时间间隔
	function setCollTime() {
		var objattrIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
		if (objattrIds.length == 0) {
			jetsennet.alert("请选择要设置的项！");
			return;
		}
		
		var dialog = new JetsenWeb.UI.Window("set-coll-time");
		JetsenWeb.extend(dialog, {
			submitBox : true,
			cancelBox : true,
			windowStyle : 1,
			maximizeBox : false,
			minimizeBox : false,
			size : {
				width : 350,
				height : 120
			},
			title : "采集间隔设置"
		});
		dialog.controls = [ "divCollTimeSapn" ];
		dialog.showDialog();
		dialog.onsubmit = function() {
			var txt_colltime = getSelectedValue($("txt_colltime"));
			if(txt_colltime == null || txt_colltime == "") {
				jetsennet.alert("请填写时间间隔！");
				return;
			}

			var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	        ws.soapheader = JetsenWeb.Application.authenticationHeader;
	        ws.oncallback = function (ret) {
	            JetsenWeb.UI.Windows.close("set-coll-time");
	            queryObjAttrib();
	        };
	        ws.onerror = function (ex) { jetsennet.error(ex); };
	        ws.call("bmpSetCollTime", [objattrIds.join(","), txt_colltime]);
		}
	}

	// 加载告警
	function loadAlarm() {

		var condition = new JetsenWeb.SqlConditionCollection();
		// 只取报警ID为1000以上的报警
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.ALARM_ID", "1000", JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.Numeric));
		if($("cboAttribType").value == '103'){
			condition.SqlConditions.push(JetsenWeb.SqlCondition.create(
					"t.CHECK_SPAN", 1, JetsenWeb.SqlLogicType.And,
					JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));
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
			$("divAlarmList").innerHTML = JetsenWeb.Xml.transformXML(
					"xslt/alarmlist.xslt", ret.resultVal);
			var gAlarmListGrid = new JetsenWeb.UI.GridList();
			gAlarmListGrid.bind($("divAlarmList"), $("tabAlarmList"));
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex + "！");
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
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

//		$("lblAlarmDesc").innerText = desc;
		$("lblCheckNum").innerText = checkNum;
		$("lblOverNum").innerText = overNum;
//		$("lblCheckSpan").innerText = checkSpan;
		loadAlarmLevel(alarmId);
	}