JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe","js_jetsentree" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gAlarmRuleGridList = new JetsenWeb.UI.GridList("gAlarmRuleGridList");
var sourceTypeArr = new Array();//存放资源类型的数组
var generalAlarmConfig = null;

// 初始化
function pageInit() 
{
	var mainDiv = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("mainDiv"), { splitType: 1, fixControlIndex:1, showSplit: false,splitBorder : 0});
	mainDiv.addControl(new JetsenWeb.UI.PageItem("divMainRecordList"));
	mainDiv.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divMainBottom"),{size:{ width: 0, height:30}}));
	
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType: 1,fixControlIndex:0, showSplit: false,splitBorder : 0});
	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divListTitle"), { size: { width: 0, height: 30} }));
	gFrame.addControl(mainDiv);
	
	//加载属性弹框的布局
	attrFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divAttrFrame"), { splitType: 1, showSplit: false});
	var attrTopDiv = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("attrTopDiv"), {splitType : 1, fixControlIndex : 1, showSplit : false,splitBorder : 0});
	var attrTopMain = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("attrTopMain"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	attrTopMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("attrTopSearch"),{size:{width:0,height:58}}));
	attrTopMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("attributeRecordList"),{size:{width:0,height:210}}));
	attrTopDiv.addControl(attrTopMain);
	attrTopDiv.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAttributeBottom"), {size : {width : 0, height : 30}}));

	var attrDownDiv = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("attrDownDiv"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	var attrDownMain = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("attrDownMain"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	attrDownMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("attrChoosenList"),{size:{width:0,height:210}}));
	attrDownMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAttrChoosenBottom"), {size : {width : 0, height : 30}}));
	attrDownDiv.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("attrDownTitle"),{size:{width:0,height:28}}));
	attrDownDiv.addControl(attrDownMain);

	attrFrame.addControl(JetsenWeb.extend(attrTopDiv, {size : {width : 0, height : 295}}));
	attrFrame.addControl(attrDownDiv);
	attrFrame.size = { width : 1000, height : 600 };
	attrFrame.resize();

	//加载指标弹框的布局
	objAttrFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrFrame"), { splitType: 1, showSplit: false});
	var objAttrTopDiv = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("objAttrTopDiv"), {splitType : 1, fixControlIndex : 1, showSplit : false,splitBorder : 0});
	var objAttrTopMain = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("objAttrTopMain"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	objAttrTopMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("objAttrTopSearch"),{size : { width: 0 ,height:58}}));
	objAttrTopMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("objAttrRecordList"),{size:{width:0,height:210}}));
	objAttrTopDiv.addControl(objAttrTopMain);
	objAttrTopDiv.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divObjAttrBottom"), {size : {width : 0, height : 30}}));

	var objAttrDownDiv = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("objAttrDownDiv"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	var objAttrDownMain = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("objAttrDownMain"), {splitType : 1, fixControlIndex : 0, showSplit : false,splitBorder : 0});
	objAttrDownMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("objAttrChosenList"),{size:{width:0,height:210}}));
	objAttrDownMain.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divObjAttrChosenBottom"), {size : {width : 0, height : 30}}));
	objAttrDownDiv.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("objAttrDownTitle"),{size : {width : 0, height : 28}}));
	objAttrDownDiv.addControl(objAttrDownMain);

	objAttrFrame.addControl(JetsenWeb.extend(objAttrTopDiv, {size : {width : 0, height : 295}}));
	objAttrFrame.addControl(objAttrDownDiv);
	objAttrFrame.size = { width : 1000, height : 600 };
	objAttrFrame.resize();
	
	if(JetsenWeb.isIE())
        document.attachEvent("onkeydown",keyDown);
    else
        document.addEventListener("keydown",keyDown,false);
    window.onresize = function () 
    {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 1000);
    };
    windowResized();
    
	$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt","<RecordSet></RecordSet>");
    gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
    
    generalAlarmConfig = new jbmp.alarm.AlarmConfig("divGAlarmConfig", "报警规则设置");
    generalAlarmConfig.init();
}

function keyDown() //禁用回车按钮，不然回车回关闭dialog。
{
	 if(JetsenWeb.getEvent().keyCode==13)
	 {
		 return false;
	 }
}

var sourceTypeFlag = false;
//加载属性
function popAttributeDialog()
{	
	$("hidePopDialog").value = "0";
	if(!sourceTypeFlag)//只在第一次点击弹框的时候，进行查询资源类型的操作
	{
		attributeClassInit();
		sourceTypeFlag = true;
	}
	//重置查询条件
	$("sourceTypeInAttrPage").value = "";
	$("attributeType").value = "";
	$("alarmStateInAttr").value = "";
	$("attributeName").value = "";
	$("chkAttrName").checked = false;
	
	searchAttribute();
	bindChosenAttr();
	
	var dialog = new JetsenWeb.UI.Window("show-attribute-win");
	JetsenWeb.extend(dialog,{submitBox:false,cancelBox:false,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:1000,height:600},showScroll:false});
	dialog.title = "加载属性";
	dialog.controls = ["attributeDiv"];
	dialog.onclosed = function()
	{
		chosenAttrArr = cacheAttributeArr.concat();//关闭窗口的时候，清除掉已作的改动
		JetsenWeb.UI.Windows.close("show-attribute-win");	
	}
	dialog.showDialog();
	if(chosenAttrArr.length > 7)//删除第一个再添加进去，防止已选择属性列表中出现滚动条不出现的情况。
	{
		var attrId = chosenAttrArr[0]["ATTRIB_ID"];
		var attrName = chosenAttrArr[0]["ATTRIB_NAME"];
		var className = chosenAttrArr[0]["CLASS_NAME"];
		var alarmName = chosenAttrArr[0]["ALARM_NAME"];
		var alarmId = chosenAttrArr[0]["ALARM_ID"];
		var collSpan = chosenAttrArr[0]["COLL_TIMESPAN"];
		
		delAttr(attrId);
		addAttr(attrId,attrName,className,alarmName,alarmId,collSpan)
	}
}

//加载指标(对象属性)
function popObjAttrDialog()
{
	$("hidePopDialog").value = "1";
	if(!sourceTypeFlag)//只在第一次点击弹框的时候，进行查询资源类型的操作
	{
		attributeClassInit();
		sourceTypeFlag = true;
	}
	//重置查询条件
	$("sourceTypeInObjAttrPage").value = "";
	$("objName").value = "";
	$("objAttrName").value = "";
	$("alarmStateInObjAttr").value = "";
	$("chkObjName").checked = false;
	$("chkObjAttrName").checked = false;
	
	searchObjAttribute();
    bindChosenObjAttr();
	var dialog = new JetsenWeb.UI.Window("show-objattr-win");
	JetsenWeb.extend(dialog,{submitBox:false,cancelBox:false,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:1000,height:600},showScroll:false});
	dialog.title = "加载指标";
	dialog.controls = ["objAttrDiv"];
	dialog.onclosed = function()
	{
		chosenObjAttrArr = cacheObjAttrArr.concat();//关闭窗口的时候，清除掉已作的改动
		JetsenWeb.UI.Windows.close("show-objattr-win");	
	}
	dialog.showDialog();
	if(chosenObjAttrArr.length > 7)//删除第一个再添加进去，防止已选择属性列表中出现滚动条不出现的情况。
	{
		var imgObj = {};
		var objAttrId = chosenObjAttrArr[0]["OBJATTR_ID"];
		imgObj.value = chosenObjAttrArr[0]["OBJATTR_NAME"];
		var objName = chosenObjAttrArr[0]["OBJ_NAME"];
		var alarmName = chosenObjAttrArr[0]["ALARM_NAME"];
		var alarmId = chosenObjAttrArr[0]["ALARM_ID"];
		var collSpan = chosenObjAttrArr[0]["COLL_TIMESPAN"];
		
		delObjAttr(objAttrId);
		addObjAttr(objAttrId,imgObj,objName,alarmName,alarmId,collSpan)
	}
}

//设置采集时间间隔。 id:属性id或者对象属性id；flag：0 属性，1 对象属性
function setCollectInterval(id,flag)
{
	$("collectIntervalSelect").value = "300";
	
	var dialog = new JetsenWeb.UI.Window("set-collectinterval-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:180},showScroll:false});
	dialog.title = "设置采集间隔";
	dialog.controls = ["setCollectIntervalDiv"];
	dialog.onsubmit = function()
	{
		var sql = "";
		if(flag == "0")
			sql = "UPDATE BMP_ATTRIBUTE SET COLL_TIMESPAN = " +  $("collectIntervalSelect").value + " WHERE ATTRIB_ID = " + id;
		else if(flag == "1")
			sql = "UPDATE BMP_OBJATTRIB SET COLL_TIMESPAN = " +  $("collectIntervalSelect").value + " WHERE OBJATTR_ID = " + id;
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret)
		{
			var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
			var tempArr = [];
			if(flag == "0")
			{
				for(var i=0; i<records.length; i++)
				{
					if(records[i]["ATTRIB_ID"] == id)
					{
						records[i]["COLL_TIMESPAN"] = $("collectIntervalSelect").value;
					}
					tempArr.push(records[i]);
				}
				chosenAttrArr = tempArr.concat();
				cacheAttributeArr = tempArr.concat();
			}else if(flag == "1")
			{
				for(var i=0; i<records.length; i++)
				{
					if(records[i]["OBJATTR_ID"] == id)
					{
						records[i]["COLL_TIMESPAN"] = $("collectIntervalSelect").value;
					}
					tempArr.push(records[i]);
				}
				chosenObjAttrArr = tempArr.concat();
				cacheObjAttrArr = tempArr.concat();
			}
			var str = getXmlByArr(records);
			$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
			gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
			$("hideShowXml").value = str;
			JetsenWeb.UI.Windows.close("set-collectinterval-win");
		};
		ws.onerror = function(ex) {jetsennet.error(ex);};
		ws.call("bmpUpdateBySql", [ sql ]);
	};
	dialog.showDialog();
}

//关联已经有的报警规则。
function refExistedAlarmBatch()
{
	var obj = document.getElementsByName("checkRecord");
	var strInfos = "";
	for(var i=0; i<obj.length; i++)
	{
		if(obj[i].checked == true)
			strInfos += obj[i].value + ",";
	}
	if(strInfos == "")
	{
		jetsennet.alert("还未选择要关联已有报警规则的项！");
		return;
	}
	$("hideSelectedInfo").value = strInfos.substring(0, strInfos.length-1).split(",");
	
	searchExistAlarm();
	
	var dialog = new JetsenWeb.UI.Window("ref-existalarm-win");
	JetsenWeb.extend(dialog,{submitBox:false,cancelBox:false,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:700,height:500},showScroll:false});
	dialog.title = "关联已有报警规则";
	dialog.controls = ["refExistAlarmDiv"];
	dialog.showDialog();
}

var existAlarmGridList = new JetsenWeb.UI.GridList("existAlarmGridList");
var existAlarmPage = new JetsenWeb.UI.PageBar("divExistAlarmPage");
existAlarmPage.onpagechange = function() 
{
	loadExistAlarm();
};
existAlarmPage.orderBy = "";
existAlarmPage.onupdate = function() 
{
	$("divExistAlarmPage").innerHTML = this.generatePageControl();
};
existAlarmGridList.ondatasort = function(sortfield, desc) 
{
	existAlarmPage.setOrderBy(sortfield, desc);
};

var existAlarmCondition = new JetsenWeb.SqlConditionCollection();
//查询已有报警规则
function searchExistAlarm()
{
	existAlarmCondition.SqlConditions = [];
	if($("attrOrObjattrName").value != "")
	{
		var subCondition = JetsenWeb.SqlCondition.create();
		subCondition.SqlLogicType = JetsenWeb.SqlLogicType.And;
    	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.ATTRIB_NAME", $("attrOrObjattrName").value, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("d.OBJATTR_NAME", $("attrOrObjattrName").value, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    	existAlarmCondition.SqlConditions.push(subCondition);
	}
	if($("existAlarmNameTxt").value != "")
	{
		if($("chkExistAlarmName").checked)
			existAlarmCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ALARM_NAME", $("existAlarmNameTxt").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			existAlarmCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ALARM_NAME", $("existAlarmNameTxt").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	existAlarmPage.currentPage = 1;
	loadExistAlarm();
}
//加载已有报警规则
function loadExistAlarm()
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARM", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE", "b", "a.ALARM_ID = b.ALARM_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBALARM", "c", "a.ALARM_ID = c.ALARM_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "d", "d.OBJATTR_ID = c.OBJATTR_ID", JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, KeyId : "", PageInfo : existAlarmPage, QueryTable : queryTable, ResultFields : "a.ALARM_ID,a.ALARM_NAME,d.OBJATTR_NAME,b.ATTRIB_NAME"});
	sqlQuery.OrderString = existAlarmPage.orderBy;
	sqlQuery.Conditions = existAlarmCondition;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{
		$("existAlarmList").innerHTML = JetsenWeb.Xml.transformXML("xslt/existalarmrule.xslt",ret.resultVal);
		existAlarmGridList.bind($("existAlarmList"),$("tabExistAlarm"));
		existAlarmPage.setRowCount(el("hid_existAlarmCount").value)
	};
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.call("bmpObjQuery",[ sqlQuery.toXml() ]);
}

//关联已有的报警规则
function refSelectedAlarm()
{
	var radio = jetsennet.form.getCheckedValues("existAlarmRadio");
	if(radio == null || radio.length == 0)
	{
		jetsennet.alert("还未选择要关联的报警规则！");
		return;
	}
	var alarmId = radio[0].split("@")[0];
	var alarmName = radio[0].split("@")[1];
	var selectInfo = $("hideSelectedInfo").value.split(",");
	var idArr = new Array();//要关联报警的属性或指标。
	var idType = "";
	if($("hideTypeInput").value == "0")//属性
	{
		for(var i=0; i<selectInfo.length; i++)
			idArr.push(selectInfo[i].split("@")[0]);
		idType = "attribute";
	}
	else if($("hideTypeInput").value == "1")//指标
	{
		var objattrIds = new Array();
		for(var i=0; i<selectInfo.length; i++)
			idArr.push(selectInfo[i].split("@")[0]);
		idType = "objattr";
	}
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{
		var newAlarmIdArr = ret.resultVal.split(",");
		var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
		var tempArr = [];
		if($("hideTypeInput").value == "0")
		{
			for(var i=0; i<records.length; i++)
			{
				for(var j=0; j<idArr.length; j++)
				{
					if(records[i]["ATTRIB_ID"] == idArr[j])
					{
						records[i]["ALARM_NAME"] = alarmName;
						records[i]["ALARM_ID"] = newAlarmIdArr[j];
						break;
					}
				}
				tempArr.push(records[i]);
			}
			chosenAttrArr = tempArr.concat();
			cacheAttributeArr = tempArr.concat();
		}else if($("hideTypeInput").value == "1")
		{
			for(var i=0; i<records.length; i++)
			{
				for(var j=0; j<idArr.length; j++)
				{
					if(records[i]["OBJATTR_ID"] == idArr[j])
					{
						records[i]["ALARM_NAME"] = alarmName;
						records[i]["ALARM_ID"] = newAlarmIdArr[j];
						break;
					}
				}
				tempArr.push(records[i]);
			}
			chosenObjAttrArr = tempArr.concat();
			cacheObjAttrArr = tempArr.concat();
		}
		var str = getXmlByArr(records);
		$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
		gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
		$("hideShowXml").value = str;
		JetsenWeb.UI.Windows.close("ref-existalarm-win");
	};
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.call("relateExistAlarm",[ idArr.join(",") , idType , alarmId]);
}

//批量设置采集间隔
function setMoreCollectInterval()
{
	var obj = document.getElementsByName("checkRecord");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";//obj[i].value的格式为ATTRIB_ID@ALARM_ID或者OBJATTR_ID@ALARM_ID，后面的ALARM_ID当批量设置报警规则的时候需要用到
	
	if(strIds == "")
	{
		jetsennet.alert("还未选择要设置采集间隔的项！");
		return;
	}
	$("collectIntervalSelect").value = "300";
	var dialog = new JetsenWeb.UI.Window("set-collectinterval-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:180},showScroll:false});
	dialog.title = "设置采集间隔";
	dialog.controls = ["setCollectIntervalDiv"];
	dialog.onsubmit = function()
	{
		var valueArr = strIds.substring(0, strIds.length-1).split(",");
		var idArr = [];
		for(var i=0; i<valueArr.length; i++)
			idArr.push(valueArr[i].split("@")[0]);
		var sql = "";
		if($("hideTypeInput").value == "0")
			sql = "UPDATE BMP_ATTRIBUTE SET COLL_TIMESPAN = " +  $("collectIntervalSelect").value + " WHERE ATTRIB_ID IN (" + idArr.toString() + ")";
		else if($("hideTypeInput").value == "1")
			sql = "UPDATE BMP_OBJATTRIB SET COLL_TIMESPAN = " +  $("collectIntervalSelect").value + " WHERE OBJATTR_ID IN (" + idArr.toString() + ")";
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret)
		{
			var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
			var tempArr = [];
			if($("hideTypeInput").value == "0")
			{
				for(var i=0; i<records.length; i++)
				{
					for(var j=0; j<idArr.length; j++)
					{
						if(records[i]["ATTRIB_ID"] == idArr[j])
						{
							records[i]["COLL_TIMESPAN"] = $("collectIntervalSelect").value;
							break;
						}
					}
					tempArr.push(records[i]);
				}
				chosenAttrArr = tempArr.concat();
				cacheAttributeArr = tempArr.concat();
			}else if($("hideTypeInput").value == "1")
			{
				for(var i=0; i<records.length; i++)
				{
					for(var j=0; j<idArr.length; j++)
					{
						if(records[i]["OBJATTR_ID"] == idArr[j])
						{
							records[i]["COLL_TIMESPAN"] = $("collectIntervalSelect").value;
							break;
						}
					}
					tempArr.push(records[i]);
				}
				chosenObjAttrArr = tempArr.concat();
				cacheObjAttrArr = tempArr.concat();
			}
			var str = getXmlByArr(records);
			$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
			gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
			$("hideShowXml").value = str;
			JetsenWeb.UI.Windows.close("set-collectinterval-win");
		};
		ws.onerror = function(ex) {jetsennet.error(ex);};
		ws.call("bmpUpdateBySql", [ sql ]);
			
	};
	dialog.showDialog();
}

//设置报警规则
function setAlarmRule(alarmId,id)
{
	privateId = id;
	generalAlarmConfig.showConfigDialog(alarmId,callBack);
}
var privateId;	   //存放属性id或者指标id，设置报警规则回调的时候需要用到
function callBack()//设置报警规则的回调方法，用于设置之后刷新页面规则名称的值
{
	var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
	var tempArr = [];
	if($("hideTypeInput").value == "0")
	{
		var tempAlarmId;
		for(var i=0; i<records.length; i++)
		{
			if(records[i]["ATTRIB_ID"] == privateId)
			{
				tempAlarmId = records[i]["ALARM_ID"];//进行设置报警规则的指标所关联的ALARM_ID.
				break;
			}
		}
		//如果有多个属性关联的同一个alarmId，当修改该alarmId对应的ALARM_NAME的时候，其他关联该alarmId的属性也跟着改变
		for(var i=0; i<records.length; i++)
		{
			if(records[i]["ALARM_ID"] == tempAlarmId)
				records[i]["ALARM_NAME"] = $("txtGAlarmName").value;
			tempArr.push(records[i]);
		}
		chosenAttrArr = tempArr.concat();
		cacheAttributeArr = tempArr.concat();
	}else if($("hideTypeInput").value == "1")
	{
		var tempAlarmId;
		for(var i=0; i<records.length; i++)
		{
			if(records[i]["OBJATTR_ID"] == privateId)
			{
				tempAlarmId = records[i]["ALARM_ID"];//进行设置报警规则的指标所关联的ALARM_ID.
				break;
			}
		}
		//指标会有多个指标关联的同一个alarmId，当修改该alarmId对应的ALARM_NAME的时候，其他关联该alarmId的指标也跟着改变
		for(var i=0; i<records.length; i++)
		{
			if(records[i]["ALARM_ID"] == tempAlarmId)
				records[i]["ALARM_NAME"] = $("txtGAlarmName").value;
			tempArr.push(records[i]);
		}
		chosenObjAttrArr = tempArr.concat();
		cacheObjAttrArr = tempArr.concat();
	}
	var str = getXmlByArr(records);
	$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
	gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
	$("hideShowXml").value = str;
}

var privateIdArr;//用于存放批量设置的指标或属性id
//批量设置报警规则的回调函数
function batchCallBack()
{
	var tempArr = [];
	var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
	if($("hideTypeInput").value == "0")
	{
		for(var i=0; i<records.length; i++)
		{
			for(var j=0; j<privateIdArr.length; j++)
			{
				if(records[i]["ATTRIB_ID"] == privateIdArr[j])
				{
					records[i]["ALARM_NAME"] = $("txtGAlarmName").value;
					break;
				}
			}
			tempArr.push(records[i]);
		}
		chosenAttrArr = tempArr.concat();
		cacheAttributeArr = tempArr.concat();
	}else if($("hideTypeInput").value == "1")
	{
		for(var i=0; i<records.length; i++)
		{
			for(var j=0; j<privateIdArr.length; j++)
			{
				if(records[i]["OBJATTR_ID"] == privateIdArr[j])
				{
					records[i]["ALARM_NAME"] = $("txtGAlarmName").value;
					break;
				}
			}
			tempArr.push(records[i]);
		}
		chosenObjAttrArr = tempArr.concat();
		cacheObjAttrArr = tempArr.concat();
	}
	var str = getXmlByArr(records);
	$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
	gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
	$("hideShowXml").value = str;
	JetsenWeb.UI.Windows.close("set-collectinterval-win");
}

//批量设置报警规则
function setBatchAlarmRule()
{
	obj = document.getElementsByName("checkRecord");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";
	if(strIds == "")
	{
		jetsennet.alert("还未选择要设置的项！");
		return;
	}
	var valueArr = strIds.substring(0, strIds.length-1).split(",");
	if(valueArr.length == 1)//如果只选择了一个报警，那么还是进行单个报警规则的设置
	{
		privateId = valueArr[0].split("@")[0];
		generalAlarmConfig.showConfigDialog(valueArr[0].split("@")[1],callBack);
	}
	else if(valueArr.length > 1)//批量报警规则设置（页面中规则名称、报警描述不能修改）
	{
		var alarmIdArr = [];
		privateIdArr = [];
		for(var i=0; i<valueArr.length; i++)
		{
			alarmIdArr.push(valueArr[i].split("@")[1]);
			privateIdArr.push(valueArr[i].split("@")[0]);
		}
		var strAlarmIds = "";
		for(var i=0; i<alarmIdArr.length; i++)
		{
			if(alarmIdArr[i] == "")
				continue;
			else
				strAlarmIds += alarmIdArr[i] + ",";
		}
		generalAlarmConfig.showBatchConfigDialog(strAlarmIds.substring(0, strAlarmIds.length-1),batchCallBack);
	}
}

//批量设置开启报警或者关闭报警(0:开启报警，1：关闭报警)
function setBatchAlarmSwitch(value)
{
	obj = document.getElementsByName("checkRecord");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";
	if(strIds == "")
	{
		jetsennet.alert("还未选择要设置的项！");
		return;
	}
	var valueArr = strIds.substring(0,strIds.length-1).split(",");
	var alarmIdArr = [];
	for(var i=0; i<valueArr.length; i++)
		alarmIdArr.push(valueArr[i].split("@")[1]);
	var strAlarmIds = "";
	for(var i=0; i<alarmIdArr.length; i++)
	{
		if(alarmIdArr[i] == "")
			continue;
		else
			strAlarmIds += alarmIdArr[i] + ",";
	}
	if(strAlarmIds == "")
	{
		jetsennet.alert("所选项均未关联报警规则！");
		return;
	}
	var sql = "UPDATE BMP_ALARM SET IS_VALID = '" + value + "' WHERE ALARM_ID IN (" + strAlarmIds.substring(0, strAlarmIds.length-1) + ")";
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.oncallback = function(ret)
	{
		$("checkAllRecord").checked = false;//添加完毕之后清除checkbox的选中状态
		for(var i = 0; i < obj.length; i++)
			obj[i].checked = false;
	};
	ws.call("bmpUpdateBySql",[sql]);
	
}

//加载对象类别(资源类型)
function attributeClassInit() 
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "ac","a.CLASS_ID=ac.CLASS_ID", JetsenWeb.TableJoinType.Inner));
	var condition = new JetsenWeb.SqlConditionCollection();
	JetsenWeb.extend(sqlQuery, {IsPageResult : 0,KeyId : "CLASS_ID",QueryTable : queryTable,Conditions : condition,
		ResultFields : "DISTINCT a.CLASS_ID,a.CLASS_NAME"});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{
		//把资源类型设置到两个弹框的页面中。
		var sourceType1 = $("sourceTypeInAttrPage");
		var sourceType2 = $("sourceTypeInObjAttrPage");
		sourceType1.length = 0;
		sourceType1.options.add(new Option("请选择", ""));
		sourceType2.length = 0;
		sourceType2.options.add(new Option("请选择", ""));
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records) 
		{
			for ( var i = 0; i < records.length; i++) 
			{
				var tempOption1 = new Option(records[i]["CLASS_NAME"], records[i]["CLASS_ID"]);
				tempOption1.title = records[i]["CLASS_NAME"];
				var tempOption2 = new Option(records[i]["CLASS_NAME"], records[i]["CLASS_ID"]);
				tempOption2.title = records[i]["CLASS_NAME"];
				sourceType1.options.add(tempOption1);
				sourceType2.options.add(tempOption2);
				sourceTypeArr.push(records[i]["CLASS_ID"]);
			}
		}
	};
	ws.onerror = function(ex) {jetsennet.error(ex);};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

//批量删除页面 指标/属性列表中数据
function delRecord()
{
	obj = document.getElementsByName("checkRecord");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";
	if(strIds == "")
	{
		jetsennet.alert("还未选择要删除的项！");
		return;
	}
	var valueArr = strIds.substring(0, strIds.length-1).split(",");
	var idArr = [];
	for(var i=0; i<valueArr.length; i++)
		idArr.push(valueArr[i].split("@")[0]);
	var records = JetsenWeb.Xml.toObject($("hideShowXml").value,"Record");
	if($("hideTypeInput").value == "0")
	{
		for(var i=0; i<idArr.length; i++)
		{
			for(var j=0; j<records.length; j++)
			{
				if(idArr[i] == records[j]["ATTRIB_ID"])
				{
					removeObjInArr(records,records[j]);
					break;
				}
			}
		}
		chosenAttrArr = records.concat();
		cacheAttributeArr = records.concat();
		var str = getXmlByArr(records);
		$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
		setTitle("0");
		gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
		$("hideShowXml").value = str;
	}
	else if($("hideTypeInput").value == "1")
	{
		for(var i=0; i<idArr.length; i++)
		{
			for(var j=0; j<chosenObjAttrArr.length; j++)
			{
				if(idArr[i] == chosenObjAttrArr[j]["OBJATTR_ID"])
				{
					removeObjInArr(records,records[j]);
					break;
				}
			}
		}
		chosenObjAttrArr = records.concat();
		cacheObjAttrArr = records.concat();
		var str = getXmlByArr(records);
		$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",str);
		setTitle("1");
		gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
		$("hideShowXml").value = str;
	}
}

//全选框
function checkAllRecord(isChecked)
{
	obj = document.getElementsByName("checkRecord");
	if(obj != null)
	{
		for(var i = 0; i < obj.length; i++)
			obj[i].checked = isChecked;
	}
}

function windowResized() 
{
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}

//通过数组组装用于页面展示的XML
function getXmlByArr(arr)
{
	var str = "<RecordSet>";
	if(arr && arr.length>0)
	{
		for(var i=0; i<arr.length; i++)
			str += JetsenWeb.Xml.serializer(arr[i],"Record");
	}
	str += "</RecordSet>";
	return str;
}

//删除模板
function delAlarmConfigTemplate(templateId)
{
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.oncallback = function(ret)
	{
		if($("hidePopDialog").value == "0")
			loadAttributeTemplate();
		else if($("hidePopDialog").value == "1")
			loadObjAttrTemplate();
	};
	ws.call("bmpObjDelete",["BMP_ALARMCONFIGTEMPLATE",templateId]);
}

//从数组中删除某个元素
function removeObjInArr(arr,obj)
{
	var count;
	for(var i=0; i<arr.length; i++)
		if(arr[i] == obj)
			count = i;
	arr.splice(count,1);
}

//设置列表Title：选择属性时，显示属性名称；选择指标时，显示指标名称；列表为空时，显示指标/属性列表
function setTitle(flag)
{
	if(flag == "0")//属性
	{
		if(chosenAttrArr.length > 0)
		{
			$("attrOrObjAttr").innerHTML = "属性名称";
			$("sourceTypeOrObjName").innerHTML = "资源类型";
		}
		else
		{
			$("attrOrObjAttr").innerHTML = "属性名称/指标名称";
			$("sourceTypeOrObjName").innerHTML = "资源类型/对象名称";
		}
	}
	if(flag == "1")//指标
	{
		if(chosenObjAttrArr.length > 0 )
		{
			$("attrOrObjAttr").innerHTML = "指标名称";
			$("sourceTypeOrObjName").innerHTML = "对象名称";
		}
		else
		{
			$("attrOrObjAttr").innerHTML = "属性名称/指标名称";
			$("sourceTypeOrObjName").innerHTML = "资源类型/对象名称";
		}
	}
}