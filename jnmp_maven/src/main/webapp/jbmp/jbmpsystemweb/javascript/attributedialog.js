JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe","js_jetsentree" ]);

var chosenAttrArr = new Array();//存放已经选择的属性对象的数组
var cacheAttributeArr = new Array();//备份数组：如果已经进行了操作又点取消，需要把chosenAttrArr还原成开始的值。

var gAttrPage = new JetsenWeb.UI.PageBar("divAttrRecordPage");
gAttrPage.orderBy = "";
gAttrPage.onupdate = function() 
{
	$("divAttrRecordPage").innerHTML = this.generatePageControl();
};
gAttrPage.onpagechange = function() 
{
	loadAttribute();
};
var gAttrGridList = new JetsenWeb.UI.GridList("gAttrGridList");
gAttrGridList.ondatasort = function(sortfield,desc)
{
	gAttrPage.setOrderBy(sortfield,desc);
};

var gAttrSqlQuery = new JetsenWeb.SqlQuery();
var gAttrQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE","a");
gAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "b", "a.ATTRIB_ID = b.ATTRIB_ID", JetsenWeb.TableJoinType.Inner));
gAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "c", "c.CLASS_ID = b.CLASS_ID", JetsenWeb.TableJoinType.Left));
gAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS","bc","b.CLASS_ID = bc.CLASS_ID",JetsenWeb.TableJoinType.Left));
gAttrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM","al","a.ALARM_ID = al.ALARM_ID",JetsenWeb.TableJoinType.Left));
var gAttrCondition = new JetsenWeb.SqlConditionCollection();
JetsenWeb.extend(gAttrSqlQuery,{isPageResult:1,KeyId:"",PageInfo:gAttrPage,QueryTable:gAttrQueryTable,
	ResultFields:"a.ATTRIB_ID,a.ATTRIB_NAME,bc.CLASS_NAME,bc.CLASS_ID,a.ATTRIB_TYPE,a.COLL_TIMESPAN,al.ALARM_NAME,a.ALARM_ID"});

var choosenAttrGridList = new JetsenWeb.UI.GridList("choosenAttrGridList");

//查询属性
function searchAttribute()
{
	gAttrCondition.SqlConditions = [];
	if($("attributeName").value != "")
	{
		if($("chkAttrName").checked)//精确查询
			gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_NAME",$("attributeName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_NAME",$("attributeName").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	if($("sourceTypeInAttrPage").value != "")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID",$("sourceTypeInAttrPage").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	else
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID",sourceTypeArr.toString(),JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
	if($("alarmStateInAttr").value != "")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.IS_VALID",$("alarmStateInAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	if($("alarmNameInAttr").value != "")
	{
		if($("chkAlarmNameInAttr").checked)
			gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.ALARM_NAME",$("alarmNameInAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
		else
			gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("al.ALARM_NAME",$("alarmNameInAttr").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	
	gAttrPage.currentPage = 1;
	loadAttribute();
}

//加载属性
function loadAttribute()
{
	
	if($("attributeType").value == "")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE","102,103,104,105,107,999",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
	else if($("attributeType").value == "102")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE","102",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	else if($("attributeType").value == "103")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE","103",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	else if($("attributeType").value == "999")
		gAttrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE","104,105,107,999",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
	
	gAttrSqlQuery.OrderString = gAttrPage.orderBy;
	gAttrSqlQuery.Conditions = gAttrCondition;
	
	var ws = new jetsennet.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret)
	{
		$("attributeRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attributedialog.xslt",ret.resultVal);
		gAttrGridList.bind($("attributeRecordList"),$("tabAttrList"));
		gAttrPage.setRowCount($("hid_AttrCount").value);
	};
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.call("bmpObjQuery",[gAttrSqlQuery.toXml()]);
}

//选择属性添加到下面已选择属性列表中。(属性ID，属性名称，资源类型，规则名称，采集间隔)
function addAttr(attrId,attrName,className,alarmName,alarmId,collSpan)
{
	var attrObj = {};
	attrObj["ATTRIB_ID"] = attrId;
	attrObj["ATTRIB_NAME"] = attrName;
	attrObj["CLASS_NAME"]  = className;
	attrObj["ALARM_NAME"] = alarmName;
	attrObj["ALARM_ID"] = alarmId;
	attrObj["COLL_TIMESPAN"] = collSpan;
	attrObj["FLAG"] = "Attribute";//用于在主页面显示数据的时候判断是 属性还是指标
	for(var i=0; i<chosenAttrArr.length; i++)
	{
		if(attrObj["ATTRIB_ID"] == chosenAttrArr[i]["ATTRIB_ID"])
		{
			jetsennet.alert("该属性已添加到下面列表中！");
			return;
		}
	}
	chosenAttrArr.push(attrObj);
	var retXml = getXmlByArr(chosenAttrArr);
	$("attrChoosenList").innerHTML = JetsenWeb.Xml.transformXML("xslt/chosenattribute.xslt",retXml);
	choosenAttrGridList.bind($("attrChoosenList"),$("tabChosenAttrList"));
}

//选择多个添加到下面 已选择属性列表中。
function addMoreAttr()
{
	var obj = document.getElementsByName("checkAttr");
	var strInfos = "";
	for(var i=0; i<obj.length; i++)
	{
		if(obj[i].checked == true)
			strInfos += obj[i].value + "$_$";
	}
	if(strInfos == "")
	{
		jetsennet.alert("还未选择要添加的项！");
		return;
	}
	var chosenAttrInfo = strInfos.substring(0, strInfos.length-3).split("$_$");
	var saveAttrObj = new Array();//保存此次选择的attr对象，用于判断是否有选择重复的。
	for(var i=0; i<chosenAttrInfo.length; i++)
	{
		var attrInfo = chosenAttrInfo[i].split("@");
		var tempObj = {};
		tempObj["ATTRIB_ID"] = attrInfo[0];
		tempObj["ATTRIB_NAME"] = attrInfo[1];
		tempObj["CLASS_NAME"] = attrInfo[2];
		tempObj["ALARM_NAME"] = attrInfo[3];
		tempObj["ALARM_ID"] = attrInfo[4];
		tempObj["COLL_TIMESPAN"] = attrInfo[5];
		tempObj["FLAG"] = "Attribute";
		saveAttrObj.push(tempObj);
	}
	
	if(chosenAttrArr.length == 0)
	{
		chosenAttrArr = saveAttrObj;
	}
	else
	{
		var alertFlag = false;
		for(var i=0; i<saveAttrObj.length; i++)
		{
			var count = 0;
			for(var j=0; j<chosenAttrArr.length; j++)
			{
				if(saveAttrObj[i]["ATTRIB_ID"] != chosenAttrArr[j]["ATTRIB_ID"])
					count = count + 1;;
			}
			if(count == chosenAttrArr.length)//因为每次比较的两个属性ID不相同的话，count增加1，如果count和已选择的数组长度相同，说明每次比较都不相同。
				chosenAttrArr.push(saveAttrObj[i]);//每次比较都不相同，则把该选项添加到 已选择的数组中。
			else
				alertFlag = true;//相等说明选择要添加到下方的选项中有已经添加过的，需要弹框。
		}
		if(alertFlag)
			jetsennet.alert("选择的属性部分已经添加到下方，本次只添加未重复的选项！")
	}
	
	bindChosenAttr();
	
	$("checkAllAttr").checked = false;//添加完毕之后清除checkbox的选中状态
	for(var i = 0; i < obj.length; i++)
		obj[i].checked = false;
}


//删除已选择的属性
function delAttr(attrId)
{
	for(var i=0; i<chosenAttrArr.length; i++)
	{
		if(attrId == chosenAttrArr[i]["ATTRIB_ID"])
		{
			removeObjInArr(chosenAttrArr,chosenAttrArr[i]);
			break;//不进行break，删除元素之后还会进行循环。
		}
	}
	bindChosenAttr();
}

//批量删除已选择的属性
function delMoreAttr()
{
	var obj = document.getElementsByName("checkChosenAttr");
	var strIds = "";
	for(var i=0; i<obj.length; i++)
		if(obj[i].checked == true)
			strIds += obj[i].value + ",";
	if(strIds == "")
	{
		jetsennet.alert("还未选择要删除的项！");
		return;
	}
	var idArr = strIds.substring(0, strIds.length-1).split(",");
	for(var i=0; i<idArr.length; i++)
	{
		for(var j=0; j<chosenAttrArr.length; j++)
		{
			if(idArr[i] == chosenAttrArr[j]["ATTRIB_ID"])
			{
				removeObjInArr(chosenAttrArr,chosenAttrArr[j]);
				break;
			}
		}
	}
	bindChosenAttr();
}

//将已选择的属性绑定到页面中
function bindChosenAttr()
{
	var retXml = getXmlByArr(chosenAttrArr);
	$("attrChoosenList").innerHTML = JetsenWeb.Xml.transformXML("xslt/chosenattribute.xslt",retXml);
	choosenAttrGridList.bind($("attrChoosenList"),$("tabChosenAttrList"));
}

//全选框(position用来区别是上面的全选框还是下面的)
function checkAllAttr(isChecked,position)
{
	var obj;
	if(position == "up")
		obj = document.getElementsByName("checkAttr");
	else if(position == "down")
		obj = document.getElementsByName("checkChosenAttr");
	if(obj != null)
	{
		for(var i = 0; i < obj.length; i++)
			obj[i].checked = isChecked;
	}
}

//确定按钮，将已经选择的属性设置到主页面中
function saveAttrToPage()
{
	$("hideTypeInput").value = "0";
	cacheAttributeArr = chosenAttrArr.concat();
	//把原来主页的列表中数据  和  已选择的指标数据  清除掉
	$("divMainRecordList").innerHTML = "";
	chosenObjAttrArr = [];
	cacheObjAttrArr = [];
	var retXml = getXmlByArr(chosenAttrArr);
	//将已经选择的属性设置到主页面
	$("divMainRecordList").innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmrule.xslt",retXml);
	setTitle("0");
	gAlarmRuleGridList.bind($("divMainRecordList"),$("tabMainRecord"));
	$("hideShowXml").value = retXml;
	jetsennet.ui.Windows.close('show-attribute-win');
}

//保存模板
function saveAttrTemplate()
{
	if(chosenAttrArr.length == 0)
	{
		jetsennet.alert("还未选择属性！");
		return;
	}
	var dialog = new JetsenWeb.UI.Window("save-attributetemplate-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:180},showScroll:false});
	dialog.title = "保存模板";
	dialog.controls = ["saveAttributeTemplateDiv"];
	
	var areaElements = JetsenWeb.Form.getElements("saveAttributeTemplateDiv");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	
	dialog.onsubmit = function()
	{
		if(JetsenWeb.Form.Validate(areaElements,true))
		{
			var template = {
				TEMPLATE_NAME:$("attributeTemplateName").value,
				TEMPLATE_TYPE: 0, //0 表示属性；1 表示指标（对象属性）
				TEMPLATE_INFO: getXmlByArr(chosenAttrArr),
				CREATE_TIME:new Date()
			};
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.onerror = function(ex){jetsennet.alert(ex);};
			ws.oncallback = function(ret)
			{
				JetsenWeb.UI.Windows.close("save-attributetemplate-win");
			};
			ws.call("bmpObjInsert",["BMP_ALARMCONFIGTEMPLATE",JetsenWeb.xml.serialize(template,"BMP_ALARMCONFIGTEMPLATE")]);
		}
	};
	dialog.showDialog();
}

var attributeTemplateGridList = new JetsenWeb.UI.GridList("attributeTemplateGridList");
attributeTemplateGridList.ondatasort = function(sortfield,desc)
{
	var orderBy = "";
	if(desc)
		orderBy = " ORDER BY TEMPLATE_NAME DESC";
	else
		orderBy = " ORDER BY TEMPLATE_NAME ASC";
	loadAttributeTemplate(orderBy);
};
//导入模板
function importAttrTemplate(orderBy)
{
	loadAttributeTemplate(orderBy);
	var dialog = new JetsenWeb.UI.Window("import-attributetemplate-win");
	JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,windowStyle:1,maximizeBox:false,minimizeBox:false,size:{width:360,height:500},showScroll:false});
	dialog.title = "导入模板";
	dialog.controls = ["importAttributeTemplateDiv"];
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
					chosenAttrArr = JetsenWeb.Xml.toObject(info, "Record");
					bindChosenAttr();
					break;
				}
			}
		}
		JetsenWeb.UI.Windows.close("import-attributetemplate-win");
	};
	dialog.showDialog();
}

var attributeTemplateSqlQuery = new JetsenWeb.SqlQuery();
var attributeTemplateQueryTable = JetsenWeb.createQueryTable("BMP_ALARMCONFIGTEMPLATE","t");
var attributeTemplatecondition = new JetsenWeb.SqlConditionCollection();
//加载模板
function loadAttributeTemplate(orderBy)
{
	attributeTemplatecondition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.TEMPLATE_TYPE","0",JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	attributeTemplateSqlQuery.OrderString = orderBy;
	JetsenWeb.extend(attributeTemplateSqlQuery,{isPageResult:0,KeyID:"",PageInfo:null,QueryTable:attributeTemplateQueryTable,Conditions:attributeTemplatecondition,
		ResultFields:"t.TEMPLATE_NAME,t.TEMPLATE_ID,t.TEMPLATE_INFO,t.TEMPLATE_TYPE"});
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.onerror = function(ex){jetsennet.alert(ex);};
	ws.oncallback = function(ret)
	{
		$("divAttributeTemplateList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attrimporttemplate.xslt",ret.resultVal);
		attributeTemplateGridList.bind($("divAttributeTemplateList"),$("tabAttrTemplate"));
	};
	ws.call("bmpObjQuery",[attributeTemplateSqlQuery.toXml()]);
}

//导入模板弹框中的查询
function searchAttributeTemplate(orderBy)
{
	attributeTemplatecondition.SqlConditions = [];
	if($("searchAttributeTemplateInput").value != "")
		attributeTemplatecondition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.TEMPLATE_NAME",$("searchAttributeTemplateInput").value,JetsenWeb.SqlLogicType.AND,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	loadAttributeTemplate(orderBy);
}

//取消按钮(关闭窗口)
function cancelChosenAttr()
{
	chosenAttrArr = cacheAttributeArr.concat();//如果已经进行了操作，又取消，需要把chosenAttrArr还原成开始的值。
	jetsennet.ui.Windows.close('show-attribute-win');
}