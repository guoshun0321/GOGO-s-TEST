JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe","js_jetsentree" ]);

var chosenObjAttrArr = new Array();//存放已经选择的指标对象的数组
var cacheObjAttrArr = new Array();//备份数组：如果已经进行了操作又点取消，需要把chosenObjAttrArr还原成开始的值。

var gObjAttrPage = new JetsenWeb.UI.PageBar("divObjAttrRecordPage");
gObjAttrPage.orderBy = "";
gObjAttrPage.onupdate = function() 
{
	$("divObjAttrRecordPage").innerHTML = this.generatePageControl();
};
gObjAttrPage.onpagechange = function() 
{
	loadObjAttribute();
};
var gObjAttrGridList = new JetsenWeb.UI.GridList("gObjAttrGridList");
gObjAttrGridList.ondatasort = function(sortfield,desc)
{
	gObjAttrPage.setOrderBy(sortfield,desc);
};

var gObjAttrSqlQuery = new JetsenWeb.SqlQuery();
var gObjAttrQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB","oa");
gObjAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "b", "oa.ATTRIB_ID = b.ATTRIB_ID", JetsenWeb.TableJoinType.Inner));
gObjAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "c", "c.CLASS_ID = b.CLASS_ID", JetsenWeb.TableJoinType.Left));
gObjAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","oa.OBJ_ID = o.OBJ_ID",JetsenWeb.TableJoinType.Left));
gObjAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBALARM","a","oa.OBJATTR_ID = a.OBJATTR_ID",JetsenWeb.TableJoinType.Left));
gObjAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM","al","a.ALARM_ID = al.ALARM_ID",JetsenWeb.TableJoinType.Left));
var gObjAttrCondition = new JetsenWeb.SqlConditionCollection();
JetsenWeb.extend(gObjAttrSqlQuery,{isPageResult:1,KeyId:"",PageInfo:gObjAttrPage,QueryTable:gObjAttrQueryTable,
	ResultFields:"o.OBJ_NAME,oa.OBJATTR_NAME,oa.OBJATTR_ID,oa.ATTRIB_ID,oa.OBJ_ID,al.ALARM_NAME,al.ALARM_ID,oa.COLL_TIMESPAN"});

var chosenObjAttrGridList = new JetsenWeb.UI.GridList("chosenObjAttrGridList");

//查询指标(对象属性)
function searchObjAttribute()
{
	gObjAttrCondition.SqlConditions = [];
	if($("objName").value != "")
	{
		if($("chkObjName").checked)
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME",$("objName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.OBJ_NAME",$("objName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	if($("objAttrName").value != "")
	{
		if($("chkObjAttrName").checked)
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("oa.OBJATTR_NAME",$("objAttrName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("oa.OBJATTR_NAME",$("objAttrName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	if($("sourceTypeInObjAttrPage").value != "")
		gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID",$("sourceTypeInObjAttrPage").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	else
		gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID",sourceTypeArr.toString(),JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
	if($("alarmStateInObjAttr").value != "")
		gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.IS_VALID",$("alarmStateInObjAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	
	if($("alarmNameInObjAttr").value != "")
	{
		if($("chkAlarmNameInObjAttr").checked)
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.ALARM_NAME",$("alarmNameInObjAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			gObjAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.ALARM_NAME",$("alarmNameInObjAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	
	gObjAttrPage.currentPage = 1;
	loadObjAttribute();
}

//加载指标(对象属性)
function loadObjAttribute()
{
	gObjAttrSqlQuery.OrderString = gObjAttrPage.orderBy;
	gObjAttrSqlQuery.Conditions = gObjAttrCondition;
	
	var ws = new jetsennet.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret)
	{
		$("objAttrRecordList").innerHTML = jetsennet.xml.transformXML("xslt/objattrdialog.xslt",ret.resultVal);
		gObjAttrGridList.bind($("objAttrRecordList"),$("tabObjAttrList"));
		gObjAttrPage.setRowCount($("hid_objAttrCount").value);
	};
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.call("bmpObjQuery",[gObjAttrSqlQuery.toXml()]);
}

//选择指标添加到下面已选择属性列表中。(指标ID，图片对象，指标名称，对象名称，规则名称，采集间隔)
//指标名称不能直接采用传值的方式，而应该通过其他方式(不经过js的方式)，比如把值设置给对象的value，在这里再取出来
//如果通过js方法的方式传值，如果数据有 以 反斜杠(\)结尾的时候，就会报js错误。Bug027544。
function addObjAttr(objAttrId,imgObj,objName,alarmName,alarmId,collSpan)
{
	var objAttrObj = {};
	objAttrObj["OBJATTR_ID"] = objAttrId;
	objAttrObj["OBJATTR_NAME"] = imgObj.value;
	objAttrObj["OBJ_NAME"]  = objName;
	objAttrObj["ALARM_NAME"] = alarmName;
	objAttrObj["ALARM_ID"] = alarmId;
	objAttrObj["COLL_TIMESPAN"] = collSpan;
	objAttrObj["FLAG"] = "ObjAttribute";
	for(var i=0; i<chosenObjAttrArr.length; i++)
	{
		if(objAttrObj["OBJATTR_ID"] == chosenObjAttrArr[i]["OBJATTR_ID"])
		{
			jetsennet.alert("该属性已添加到下面列表中！");
			return;
		}
	}
	chosenObjAttrArr.push(objAttrObj);
	bindChosenObjAttr();
}

//选择多个添加到下面 已选择属性列表中。
function addMoreObjAttr()
{
	var obj = document.getElementsByName("checkObjAttr");
	var strInfos = "";
	for(var i=0; i<obj.length; i++)
	{
		if(obj[i].checked == true)
			strInfos += obj[i].value + "$_$";//不要使用逗号来作为分隔符，有的值中已经带有逗号造成错误。比如：145-进程(System Idle Process,1)。
	}
	if(strInfos == "")
	{
		jetsennet.alert("还未选择要添加的指标！");
		return;
	}
	var chosenObjAttrInfo = strInfos.substring(0, strInfos.length-3).split("$_$");
	var saveObjAttrArr = new Array();//保存此次选择的attr对象，用于判断是否有选择重复的。
	for(var i=0; i<chosenObjAttrInfo.length; i++)
	{
		var attrInfo = chosenObjAttrInfo[i].split("@");
		var tempObjAttr = {};
		tempObjAttr["OBJATTR_ID"] = attrInfo[0];
		tempObjAttr["OBJATTR_NAME"] = attrInfo[1];
		tempObjAttr["OBJ_NAME"] = attrInfo[2];
		tempObjAttr["ALARM_NAME"] = attrInfo[3];
		tempObjAttr["ALARM_ID"] = attrInfo[4];
		tempObjAttr["COLL_TIMESPAN"] = attrInfo[5];
		tempObjAttr["FLAG"] = "ObjAttribute";//用于在主页面显示数据的时候判断是 属性还是指标
		saveObjAttrArr.push(tempObjAttr);
	}
	if(chosenObjAttrArr.length == 0)
	{
		chosenObjAttrArr = saveObjAttrArr;
	}
	else
	{
		var alertFlag = false;
		for(var i=0; i<saveObjAttrArr.length; i++)
		{
			var count = 0;
			for(var j=0; j<chosenObjAttrArr.length; j++)
			{
				if(saveObjAttrArr[i]["OBJATTR_ID"] != chosenObjAttrArr[j]["OBJATTR_ID"])
					count = count + 1;;
			}
			if(count == chosenObjAttrArr.length)//因为每次比较的两个OBJATTR_ID不相同的话，count增加1，如果count和已选择的数组长度相同，说明每次比较都不相同。
				chosenObjAttrArr.push(saveObjAttrArr[i]);//每次比较都不相同，说明之前未选择，则把该选项添加到 已选择的数组中。
			else
				alertFlag = true;//相等说明选择要添加到下方的选项中有已经添加过的，需要弹框。
		}
		if(alertFlag)
			jetsennet.alert("选择的指标部分已经添加到下方，本次只添加未重复的选项！")
	}
	
	bindChosenObjAttr();
	
	$("checkAllObjAttr").checked = false;//添加完毕之后清除checkbox的选中状态
	for(var i = 0; i < obj.length; i++)
		obj[i].checked = false;
}


//删除已选择的属性
function delObjAttr(objAttrId)
{
	for(var i=0; i<chosenObjAttrArr.length; i++)
	{
		if(objAttrId == chosenObjAttrArr[i]["OBJATTR_ID"])
		{
			removeObjInArr(chosenObjAttrArr,chosenObjAttrArr[i]);
			break;//不进行break，删除元素之后还会进行循环。
		}
	}
	bindChosenObjAttr();
}

//批量删除已选择的属性
function delMoreObjAttr()
{
	var obj = document.getElementsByName("checkChosenObjAttr");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";
	if(strIds == "")
	{
		jetsennet.alert("还未选择要删除的指标！");
		return;
	}
	var idArr = strIds.substring(0, strIds.length-1).split(",");
	for(var i=0; i<idArr.length; i++)
	{
		for(var j=0; j<chosenObjAttrArr.length; j++)
		{
			if(idArr[i] == chosenObjAttrArr[j]["OBJATTR_ID"])
			{
				removeObjInArr(chosenObjAttrArr,chosenObjAttrArr[j]);
				break;
			}
		}
	}
	bindChosenObjAttr();
}

//将已选择的指标绑定到页面中
function bindChosenObjAttr()
{
	var retXml = getXmlByArr(chosenObjAttrArr);
	$("objAttrChosenList").innerHTML = JetsenWeb.Xml.transformXML("xslt/chosenobjattr.xslt",retXml);
	chosenObjAttrGridList.bind($("objAttrChosenList"),$("tabChosenObjAttr"));
}

//全选框(position用来区别是上面的全选框还是下面的)
function checkAllObjAttr(isChecked,position)
{
	var obj;
	if(position == "up")
		obj = document.getElementsByName("checkObjAttr");
	else if(position == "down")
		obj = document.getElementsByName("checkChosenObjAttr");
	if(obj != null)
	{
		for(var i = 0; i < obj.length; i++)
			obj[i].checked = isChecked;
	}
}

//保存模板
function saveObjAttrTemplate()
{
	if(chosenObjAttrArr.length == 0)
	{
		jetsennet.alert("还未选择指标！");
		return;
	}
	var dialog = new JetsenWeb.UI.Window("save-objattrtemplate-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:180},showScroll:false});
	dialog.title = "保存模板";
	dialog.controls = ["saveObjAttrTemplateDiv"];
	
	var areaElements = JetsenWeb.Form.getElements("saveObjAttrTemplateDiv");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	
	dialog.onsubmit = function()
	{
		if(JetsenWeb.Form.Validate(areaElements,true))
		{
			var template = {
				TEMPLATE_NAME:$("objAttrTemplateName").value,
				TEMPLATE_TYPE: 1,
				TEMPLATE_INFO: getXmlByArr(chosenObjAttrArr),
				CREATE_TIME:new Date()
			};
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.onerror = function(ex){jetsennet.alert(ex);};
			ws.oncallback = function(ret)
			{
				JetsenWeb.UI.Windows.close("save-objattrtemplate-win");
			};
			ws.call("bmpObjInsert",["BMP_ALARMCONFIGTEMPLATE",JetsenWeb.xml.serialize(template,"BMP_ALARMCONFIGTEMPLATE")]);
		}
	};
	dialog.showDialog();
}

var objAttrTemplateGridList = new JetsenWeb.UI.GridList("objAttrTemplateGridList");
objAttrTemplateGridList.ondatasort = function(sortfield,desc)
{
	var orderBy = "";
	if(desc)
		orderBy = " ORDER BY TEMPLATE_NAME DESC";
	else
		orderBy = " ORDER BY TEMPLATE_NAME ASC";
	loadObjAttrTemplate(orderBy);
};
//导入模板
function importObjAttrTemplate()
{
	loadObjAttrTemplate();
	var dialog = new JetsenWeb.UI.Window("import-objattrtemplate-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:500},showScroll:false});
	dialog.title = "导入模板";
	dialog.controls = ["importObjAttrTemplateDiv"];
	dialog.onsubmit = function()
	{
		var templates = document.getElementsByName("templateRadio");
		if(templates)
		{
			for(var i=0; i<templates.length; i++)
			{
				if(templates[i].checked)
				{
					var info = templates[i].value.split("@")[1];
					chosenObjAttrArr = JetsenWeb.Xml.toObject(info, "Record");
					bindChosenObjAttr();
					break;
				}
			}
		}
		JetsenWeb.UI.Windows.close("import-objattrtemplate-win");
	};
	dialog.showDialog();
}

var objAttrTemplateSqlQuery = new JetsenWeb.SqlQuery();
var objAttrTemplateQueryTable = JetsenWeb.createQueryTable("BMP_ALARMCONFIGTEMPLATE","t");
var objAttrTemplatecondition = new JetsenWeb.SqlConditionCollection();
//加载模板
function loadObjAttrTemplate(orderBy)
{
	objAttrTemplateSqlQuery.OrderString = orderBy;
	objAttrTemplatecondition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.TEMPLATE_TYPE","1",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(objAttrTemplateSqlQuery,{isPageResult:0,KeyID:"",PageInfo:null,QueryTable:objAttrTemplateQueryTable,Conditions:objAttrTemplatecondition,
		ResultFields:"t.TEMPLATE_NAME,t.TEMPLATE_ID,t.TEMPLATE_INFO,t.TEMPLATE_TYPE"});
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.oncallback = function(ret)
	{
		$("divObjAttrTemplateList").innerHTML = JetsenWeb.Xml.transformXML("xslt/objattrimporttemplate.xslt",ret.resultVal);
		objAttrTemplateGridList.bind($("divObjAttrTemplateList"),$("tabObjAttrTemplate"));
	};
	ws.call("bmpObjQuery",[objAttrTemplateSqlQuery.toXml()]);
}

//导入模板弹框中的查询
function searchObjAttrTemplate(orderBy)
{
	objAttrTemplatecondition.SqlConditions = [];
	if($("searchObjAttrTemplateInput").value != "")
		objAttrTemplatecondition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.TEMPLATE_NAME",$("searchObjAttrTemplateInput").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	loadObjAttrTemplate(orderBy);
}

//确定按钮，将已经选择的指标设置到主页面中(alarmrule.htm)
function saveObjAttrToPage()
{
	$("hideTypeInput").value = "1";
	cacheObjAttrArr = chosenObjAttrArr.concat();
	//把原来主页的列表中数据 和 已选择的属性数据 清除掉
	$("divMainRecordList").innerHTML = "";
	chosenAttrArr = [];
	cacheAttributeArr = [];
	//将已经选择的指标设置到主页面中
	var retXml = getXmlByArr(chosenObjAttrArr);
	$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",retXml);
	setTitle("1");
	gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
	$("hideShowXml").value = retXml;
	jetsennet.ui.Windows.close('show-objattr-win');
}

//取消按钮(关闭窗口)
function cancelChosenObjAttr()
{
	chosenObjAttrArr = cacheObjAttrArr.concat();//如果已经进行了操作，又取消，需要把choosenObjAttr还原成开始的值。
	jetsennet.ui.Windows.close('show-objattr-win');
}