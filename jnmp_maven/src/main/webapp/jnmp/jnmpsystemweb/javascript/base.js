
function loadSystem(controlId,noEmpty)
{
    var control = $(controlId);
    control.options.length = 0;
    if(!noEmpty)
    {
        control.options[0] = new Option("请选择","");
    }
	var groupCodes = getGroupCodes();
	if (groupCodes == null)
	{
//		return;
	}
//	var allSystem = hasAllSystemRights();

    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP","");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"GROUP_NAME,GROUP_ID"});

    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE",100,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Less,JetsenWeb.SqlParamType.Numeric));
//    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_ID",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));
//    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_STATE",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
//				if (!allSystem)
//				{
//					for (var j = 0; j < groupCodes.length; j++)
//					{
//						if (groupCodes[j] == typeObjs[i].SYS_CODE)
//						{
//							var option = new Option(typeObjs[i].GROUP_NAME,typeObjs[i].GROUP_ID);
//							control.options[control.options.length] = option;
//							break;
//						}
//					}
//					continue;
//				}
                var option = new Option(typeObjs[i].GROUP_NAME,typeObjs[i].GROUP_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}
function loadDevice(controlId,sysId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(sysId && sysId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SYS_ID",sysId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("NMP_DEVICE","");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"DEV_NAME,DEV_ID"});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var option = new Option(valueOf(typeObjs[i],"DEV_NAME",""),typeObjs[i].DEV_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}

//获取指定设备类型的设备
function loadDevice(controlId,sysId,type)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(sysId && sysId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SYS_ID",sysId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    if(type && type!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("DEV_TYPE",type,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("NMP_DEVICE","");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"DEV_NAME,DEV_ID"});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var option = new Option(valueOf(typeObjs[i],"DEV_NAME",""),typeObjs[i].DEV_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}

function loadSIChannel(controlId,sysId,mType,noEmpty)
{
    var control = $(controlId);
    control.options.length = 0;
    if (!noEmpty)
    {
		control.options[0] = new Option("请选择","");
	}

    var condition = new JetsenWeb.SqlConditionCollection();
    if(sysId && sysId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SYS_ID",sysId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    if(mType && mType!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.MONITOR_TYPE",mType,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
    }
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("DMP_SICHANNEL","c");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"c.CHAN_NAME,c.SICH_NAME,c.SICH_ID"});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var sichName = valueOf(typeObjs[i],"SICH_NAME","");
                var orSichName = valueOf(typeObjs[i],"CHAN_NAME","");
                if(orSichName!="" && sichName!=orSichName)
                {
                    sichName = sichName+"("+orSichName+")";
                }
                var option = new Option(sichName,typeObjs[i].SICH_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}
function loadFRequence(controlId,sysId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("FREQ_STATE",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    if(sysId && sysId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SYS_ID",sysId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("DMP_FREQUENCE","f");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"f.FREQ_ID,f.FREQ_NUM"});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var option = new Option(valueOf(typeObjs[i],"FREQ_NUM",""),typeObjs[i].FREQ_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}

// 根据类型加载受控词
function loadCtrlWordByType(selectElement,type,isIDNamePair, noEmpty)
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_CTRLWORD","");
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CW_TYPE",type,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery,{KeyId:"CW_ID",QueryTable:gQueryTable,Conditions:condition,ResultFields:"CW_ID,CW_NAME"});
	var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{
		selectElement.length = 0;
		
		if (!noEmpty)
		{
			selectElement.options.add(new Option("请选择",""));
		}
		
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records)
		{
			var length = records.length;
			for(var i = 0; i < length; i++)
			{
				var cwInfo = records[i];
				selectElement.options.add(new Option(cwInfo["CW_NAME"], isIDNamePair ? cwInfo["CW_ID"] : cwInfo["CW_NAME"]));
			}
		}
	};
	ws.onerror = function(ex){ jetsennet.error(ex + "！");};
	ws.call("dmpObjQuery",[gSqlQuery.toXml()]);
}

//加载SI频道到多选列表
function searchTSChannelList(listId, tsId, tschName, gGridList, sortfield, desc, showDel)
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("DMP_OBJTSCHANNEL","c");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("DMP_OBJTSSTREAM","s","c.TS_ID=s.TS_ID",JetsenWeb.TableJoinType.Inner));
	JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"c.TSCH_ID,c.TSCH_NAME",OrderString:"Order By c.TSCH_NAME"});
    
	
	var condition = new JetsenWeb.SqlConditionCollection();
	if (!showDel)
	{
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TS_STATE",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TSCH_STATE",1,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));
	}
	if (tsId && tsId != "")
	{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("s.TS_ID",tsId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	}
	if (tschName && tschName != "")
	{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.TSCH_NAME",tschName,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));
	}
	if (sortfield)
	{
		sqlQuery.OrderString = "Order By " + sortfield;
	}
	if (desc)
	{
		sqlQuery.OrderString += " DESC";
	}
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(ret)
	{
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		for (var i = 0; i < nodes.length; i++)
		{
			var groupName = valueOf(nodes[i].selectSingleNode("GROUP_NAME"), "text", "");
			var tschName = valueOf(nodes[i].selectSingleNode("TSCH_NAME"), "text", "");
			if(tschName != "" && groupName != "")
			{
				nodes[i].selectSingleNode("TSCH_NAME").text = groupName + "-" + tschName;
			}
		}
		if (!gGridList)
		{
			gGridList = new JetsenWeb.UI.GridList();
			gGridList.rowSelection = false;
			gGridList.ondatasort = function(sortfield, desc)
			{
				searchTSChannelList(listId, tsId, chanName, gGridList, sortfield, desc, showDel);
			};
		}
		$(listId).innerHTML = JetsenWeb.Xml._transformXML("xslt/simpletschannel.xslt", xmlDoc);
		gGridList.bind($(listId), $("tabTSChannel"));
	}
	ws.onerror = function(ex){ jetsennet.error(ex + "！");};
	ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}

// 将“yyyy-MM-dd HH:mm:ss”或者“yyyy-MM-dd”字符串转换成Date
function parseDate(dateString)
{
	var dateReg = /^([1-9]\d{3})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\d|3[01])( ((0?|1)\d|2[0-3]):((0?|[1-5])\d):((0?|[1-5])\d))?$/;
	if (!dateReg.test(dateString))
	{
		return null;
	}
	var childgroups = dateString.match(dateReg);
	var execFuncs = ["", "setFullYear", "setMonth", "setDate", "", "setHours", "", "setMinutes", "", "setSeconds", ""];
	var date = new Date(0);
	for (var i = 0; i < childgroups.length; i++)
	{
		if (execFuncs[i] != "" && childgroups[i] != "")
		{
			var num = Number(childgroups[i]);
			if (execFuncs[i] == "setMonth")
			{
				num -= 1;
			}
			date[execFuncs[i]](num);
		}
	}
	return date;
}

// 阻止事件的继续传播
function preventEvent(e)
{
	if (e && e.stopPropagation)
	{
		e.stopPropagation();
	}
	else
	{
		e = window.event;
		e.cancelBubble = true;
	}
}

// 停止VLC的播放
function stopVlc(vlcIds)
{
	if (typeof(vlcIds) == "string")
	{
		document.getElementById(vlcIds).stop();
	}
	else if (vlcIds.length)
	{
		for (var i = 0; i < vlcIds.length; i++)
		{
			document.getElementById(vlcIds[i]).stop();
		}
	}
}

// 获取当前用户组代号
function getGroupCodes()
{
	var groupCodes = null;
	var groupIds = JetsenWeb.Application.userInfo.UserGroups;
	if (groupIds != "")
	{
		groupCodes = [];
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("UUM_USERGROUP","");
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",groupIds,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));
		JetsenWeb.extend(gSqlQuery,{IsPageResult:0,KeyId:"ID",QueryTable:gQueryTable,Conditions:condition,ResultFields:"GROUP_CODE"});
		var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret)
		{
			var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if (records)
			{
				var length = records.length;
				for(var i = 0; i < length; i++)
				{
					groupCodes.push(records[i]["GROUP_CODE"]);
				}
			}
		};
		ws.onerror = function(ex){ jetsennet.error(ex + "！");};
		ws.call("dmpObjQuery",[gSqlQuery.toXml()]);
	}
	return groupCodes;
}

// 是否有所有前端权限
function hasAllSystemRights()
{
	var groupCodes = getGroupCodes();
	if (groupCodes == null)
	{
		return false;
	}
	for (var i = 0; i < groupCodes.length; i++)
	{
		if (groupCodes[i] == "AllSystem")
		{
			return true;
		}
	}
	return false;
}

//包括子对象 (yl 2011-01-14)
function loadObject(controlId,devId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(devId && devId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("d.DEV_ID",devId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    
        var sqlQuery = new JetsenWeb.SqlQuery();
        var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT","o");     
        queryTable.addJoinTable(JetsenWeb.createJoinTable("NMP_OBJ2DEVICE","d","d.OBJ_ID=o.OBJ_ID",JetsenWeb.TableJoinType.Left));
        JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"o.OBJ_NAME,o.OBJ_ID,o.PARENT_ID"});
        sqlQuery.Conditions = condition;

        var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
        ws.async = false;
        ws.cacheLevel = 2;      
        ws.oncallback = function(resultXml)
        {		
            var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
            if(typeObjs!=null)
            {
                for(var i=0;i<typeObjs.length;i++)
                {
                    if(typeObjs[i].PARENT_ID=='0')
                    {
                         var option = new Option(valueOf(typeObjs[i],"OBJ_NAME",""),typeObjs[i].OBJ_ID);
                         option.style.backgroundColor="#c1bfbf";
                         control.options[control.options.length] = option;
                         
                        for(var j=0;j<typeObjs.length;j++)
                        {
                            if(typeObjs[j].PARENT_ID==typeObjs[i].OBJ_ID )
                            {
                                var suboption = new Option("    "+valueOf(typeObjs[j],"OBJ_NAME",""),typeObjs[j].OBJ_ID);
                                control.options[control.options.length] = suboption;
                            }
                        }
                    }
                }
            }		      
        }
        ws.onerror = function(ex){ jetsennet.error(ex + "！");};
        ws.call("dmpObjQuery",[sqlQuery.toXml()]);
    }
    
}

//不包括子对象 (yl 2011-01-14)
function loadObjectWithoutSubObj(controlId,groupId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(groupId && groupId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("g.GROUP_ID",groupId,JetsenWeb.SqlLogicType.And,groupId == "0" ? JetsenWeb.SqlRelationType.IsNull : JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("o.PARENT_ID","0",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
   
        var sqlQuery = new JetsenWeb.SqlQuery();
        var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT","o");     
        queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP","g","g.OBJ_ID=o.OBJ_ID",JetsenWeb.TableJoinType.Left));
        JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"o.OBJ_NAME,o.OBJ_ID"});
        sqlQuery.Conditions = condition;

        var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
        ws.async = false;
        ws.cacheLevel = 2;      
        ws.oncallback = function(resultXml)
        {		
            var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
            if(typeObjs!=null)
            {
                for(var i=0;i<typeObjs.length;i++)
                {
                    var option = new Option(valueOf(typeObjs[i],"OBJ_NAME",""),typeObjs[i].OBJ_ID);
                    control.options[control.options.length] = option;
                }
            }		      
        }
        ws.onerror = function(ex){ jetsennet.error(ex + "！");};
        ws.call("dmpObjQuery",[sqlQuery.toXml()]);
    }
}

function loadAttrib(controlId,objId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(objId && objId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("oa.OBJ_ID",objId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    
        var sqlQuery = new JetsenWeb.SqlQuery();
        var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB","oa");     
        queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE","a","oa.ATTRIB_ID=a.ATTRIB_ID",JetsenWeb.TableJoinType.Left));
        JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"distinct a.ATTRIB_NAME,a.ATTRIB_ID"});
        sqlQuery.Conditions = condition;
        
        var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
        ws.async = false;
        ws.cacheLevel = 2;      
        ws.oncallback = function(resultXml)
        {		
            var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
            if(typeObjs!=null)
            {
                for(var i=0;i<typeObjs.length;i++)
                {
                    var option = new Option(valueOf(typeObjs[i],"ATTRIB_NAME",""),typeObjs[i].ATTRIB_ID);
                    control.options[control.options.length] = option;
                }
            }		      
        }
        ws.onerror = function(ex){ jetsennet.error(ex + "！");};
        ws.call("dmpObjQuery",[sqlQuery.toXml()]);
    }
}

//自定义 (yl 2011-01-14)
function loadTableObject(controlId,noEmpty,loadTable,loadTextField,loadValueField)
{
    var control = $(controlId);
    control.options.length = 0;
    if(!noEmpty)
        control.options[0] = new Option("请选择","");

    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable(loadTable,"t");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"t."+loadTextField+",t."+loadValueField});
    
    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var option = new Option(valueOf(typeObjs[i],loadTextField,""),valueOf(typeObjs[i],loadValueField,""));
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("nmpObjQuery",[sqlQuery.toXml()]);
}
// 获取SNMP设备
function loadSNMPDevice(controlId,sysId)
{
    var control = $(controlId);
    control.options.length = 0;
    control.options[0] = new Option("请选择","");

    var condition = new JetsenWeb.SqlConditionCollection();
    if(sysId && sysId!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SYS_ID",sysId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("DEV_TYPE",1,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("NMP_DEVICE","");     
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"",PageInfo:null,QueryTable:queryTable,ResultFields:"DEV_NAME,DEV_ID"});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;	  
    ws.async = false;
    ws.cacheLevel = 2;      
    ws.oncallback = function(resultXml)
    {		
        var typeObjs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        if(typeObjs!=null)
        {
            for(var i=0;i<typeObjs.length;i++)
            {
                var option = new Option(valueOf(typeObjs[i],"DEV_NAME",""),typeObjs[i].DEV_ID);
                control.options[control.options.length] = option;
            }
        }		      
    }
    ws.onerror = function(ex){ jetsennet.error(ex + "！");};
    ws.call("dmpObjQuery",[sqlQuery.toXml()]);
}

// 加载码流
function loadTSStream(selectElement, noEmpty)
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("DMP_OBJTSSTREAM","s");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT","o","s.TS_ID=o.OBJ_ID",JetsenWeb.TableJoinType.Inner));
	JetsenWeb.extend(gSqlQuery,{KeyId:"TS_ID",QueryTable:gQueryTable,ResultFields:"TS_ID,OBJ_NAME"});
	var ws = new JetsenWeb.Service(DMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{
		selectElement.length = 0;
		
		if (!noEmpty)
		{
			selectElement.options.add(new Option("请选择",""));
		}
		
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records)
		{
			var length = records.length;
			for(var i = 0; i < length; i++)
			{
				var cwInfo = records[i];
				selectElement.options.add(new Option(cwInfo["OBJ_NAME"], cwInfo["TS_ID"]));
			}
		}
	};
	ws.onerror = function(ex){ jetsennet.error(ex + "！");};
	ws.call("dmpObjQuery",[gSqlQuery.toXml()]);
}

Array.prototype.contains = function (element) {
	var i = this.length;
	while (i--) {
		if (this[i] === element) {
			return true;
		}
	}
	return false;
}

Array.prototype.addDistinct = function (element) {
	if (!this.contains(element)) {
		this.push(element);
	}
}

Array.prototype.indexOf = function (element) {
	var i = 0;
	for (var i = 0; i < this.length; i++) {
		if (this[i] === element) {
			return i;
		}
	}
	return -1;
}

Array.prototype.lastIndexOf = function (element) {
	var i = this.length;
	while (i--) {
		if (this[i] === element) {
			return i;
		}
	}
	return -1;
}