
//用户组脚本===================================================================

var gPageSelectUser = new JetsenWeb.UI.PageBar("pageSelectPerson");
gPageSelectUser.onpagechange = function(){getSelectUserData();}
gPageSelectUser.onupdate = function(){
    document.getElementById('divSelectPersonPage').innerHTML = this.generatePageControl();
}			

//初始化===============================================================
function init()
{
    var _type = JetsenWeb.queryString("Type");
    
    var _parentId = JetsenWeb.queryString("PID");
    _parentId = _parentId==""?0:parseInt(_parentId);
    loadGroup(_parentId);//showModiGroupWin	
    loadNavigation(_parentId);  		
}

loadNavigation = function(Id)
{
    if(Id=="0")
    {
        $('divNavigation').innerHTML = "<img src='images/navigation.gif' border='0'/>所有组";
    }
    else
    {
        var _ws1 = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
        _ws1.soapheader = JetsenWeb.Application.authenticationHeader;
        _ws1.async = false;
        _ws1.oncallback = function(ret)
        {
            var obj = JetsenWeb.Xml.toObject(ret.resultVal);
            if(obj!=null && obj.UUM_PERSONGROUP!=null)
            {              
                var strTemp = "";
                if(obj.UUM_PERSONGROUP.PARENT_ID!="0")
                {
                    strTemp += "<img src='images/navigation.gif' border='0'/><a href='PersonGroup.htm?PID=0'>所有组</a>";
                }
                strTemp += "<img src='images/navigation.gif' border='0'/><a href='PersonGroup.htm?PID="+obj.UUM_PERSONGROUP.PARENT_ID+"'>"+obj.UUM_PERSONGROUP.PARENT_NAME+"</a>";
                strTemp+="<img src='images/navigation.gif' border='0'/>"+obj.UUM_PERSONGROUP.NAME; 
                $('divNavigation').innerHTML  = strTemp;            
            }            
        }
        _ws1.onerror = function(ex){ jetsennet.error(ex);};
        _ws1.call("uumObjRead",["UUM_PERSONGROUP",Id]);
    }
}
//用户组
function loadGroup(parentId)
{		       
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID",parentId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));
    
    var ws = new JetsenWeb.Service(USERMANGER_WSDL);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(sResult)
    {			    
        el("container").innerHTML = JetsenWeb.Xml.transformXML("xslt/persongroup.xslt",sResult.resultVal);		       
        var o = new JetsenWeb.UI.GridList();
        var rc = null;
        rc = o.bind(document.getElementById('container'), document.getElementById('tabBody'));	         
		
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjLoad",["UUM_PERSONGROUP",conditionCollection.toXml(),""]); 
	$("txtGroupParentId").value = parentId;
}		

//用户组管理===========================================================
editPersonGroup = function(groupId,parentId)
{
    $('selMember').options.length = 0;
    var areaElements = JetsenWeb.Form.getElements('divPersonGroup');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);         
 
    getGroup(groupId);
    getGroupPerson(groupId);
    var _dialog = new JetsenWeb.UI.ConfirmDialog();
    _dialog.width = 500;
    _dialog.title = "编辑分组";       
    _dialog.contentId = "divPersonGroup";
    _dialog.onsubmit = function(){      
        
        if(JetsenWeb.Form.Validate(areaElements,true))
        {                                     
             var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Dialogs.close($('divPersonGroup').dialogId);
                loadGroup(parentId);             
            }
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjUpdate",["UUM_PERSONGROUP",buildGroupXmlData(parentId,groupId)]);
        }             
    };
    _dialog.show(); 
}
addPersonGroup = function(parentId)
{
    var areaElements = JetsenWeb.Form.getElements('divPersonGroup');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);         
    $('selMember').options.length =0;
    var _dialog = new JetsenWeb.UI.ConfirmDialog();
    _dialog.width = 500;
    _dialog.title = "新建分组";       
    _dialog.contentId = "divPersonGroup";
    _dialog.onsubmit = function(){      
        
        if(JetsenWeb.Form.Validate(areaElements,true))
        {                                     
             var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Dialogs.close($('divPersonGroup').dialogId);
                loadGroup(parentId);               
            }
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjInsert",["UUM_PERSONGROUP",buildGroupXmlData(parentId,"")]);
        }             
    };
    _dialog.show(); 
}
getSelectPersonData = function()
{
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        $('divSelectPersonList').innerHTML = JetsenWeb.Xml.transformXML("xslt/SelectPerson.xslt",ret.resultVal);
        var o = new JetsenWeb.UI.GridList();
        var rc = o.bind($('divSelectPersonList'), $('tabSelectPerson'));	
        gPageSelectUser.setRowCount($('hid_SPTotalCount').value);                
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjLoad",["UUM_PERSON","",gPageSelectUser.toXml()]);
}
function selectPerson()
{
    var _dialog2 = new JetsenWeb.UI.ConfirmDialog();
    _dialog2.width = 520;
    _dialog2.title = "选择人员";       
    _dialog2.contentId = "divSelectPerson";
    _dialog2.onsubmit = function(){     
        var obj = document.getElementsByName("chk_SelectPerson");
        for(var i=0;i<obj.length;i++)
        {
	        if(obj[i].checked)
		        AddPersonItem(obj[i].value,obj[i].getAttribute("itemName"));
        }		
        return true; 
    };
    _dialog2.show();
    getSelectPersonData();
}
//添加成员
function AddPersonItem(userID,userName)
{
	var len =$("selMember").options.length;
	for(var i=0;i<len;i++)
	{
		if($("selMember").options[i].value == userID)
			return;
	}		
	var objNewOption = document.createElement("option");
	$("selMember").options.add(objNewOption);
	objNewOption.value =  userID;
	objNewOption.innerHTML = userName;			
}

//获取某个用户组信息
function getGroup(groupID)
{
	var ws = new JetsenWeb.Service(USERMANGER_WSDL);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(sResult)
    {     	   
		var _groupXmlDoc = new JetsenWeb.XmlDoc();
		_groupXmlDoc.async = false;
		_groupXmlDoc.loadXML(sResult.resultVal);
	
		var _roleNode=_groupXmlDoc.documentElement.selectSingleNode("UUM_PERSONGROUP");
		if (_roleNode.selectSingleNode("NAME")!=null)
		{
			el("txtGroupName").value= JetsenWeb.Xml.getText(_roleNode.selectSingleNode("NAME"));
		}
		if (_roleNode.selectSingleNode("DESCRIPTION")!=null)
		{	
			el("txtDescription").value= JetsenWeb.Xml.getText(_roleNode.selectSingleNode("DESCRIPTION"));		
		}		
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
	ws.call("uumObjRead",["UUM_PERSONGROUP",groupID]); 
}

//获取组的用户信息
function getGroupPerson(groupID)
{
	var ws = new JetsenWeb.Service(USERMANGER_WSDL);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(rolesResult)
	{
	    var obj = JetsenWeb.Xml.toObject(rolesResult.resultVal,"UUM_PERSONTOGROUP");
	    if(obj!=null)
	    {		
		    for(var i=0;i<obj.length;i++)
		    {
			    var objNewOption = document.createElement("option");
			    el("selMember").options.add(objNewOption);
			    objNewOption.value =   obj[i].PERSON_ID;
			    objNewOption.innerHTML=   obj[i].NAME;
		    }
		}
	}
	ws.onerror = function(ex){ jetsennet.error(ex);};
	ws.call("uumObjRead",["UUM_GROUPTOPERSON",groupID]); 
}


//生成用户组xml
function buildGroupXmlData(parentGroupId,groupId)
{
	var _groupXmlDoc = new JetsenWeb.XmlDoc();
    _groupXmlDoc.async = false;
    
    var rootNode=_groupXmlDoc.createElement("UUM_PERSONGROUP");     
    
    if(groupId!="")
    {
		var idNode = _groupXmlDoc.createElement("ID");
		idNode.text = groupId;
		rootNode.appendChild(idNode);
    }
    
    if(parentGroupId!="")
    {
		var parentIdNode = _groupXmlDoc.createElement("PARENT_ID");
		parentIdNode.text = parentGroupId;
		rootNode.appendChild(parentIdNode);
    }
    
    var groupNameNode = _groupXmlDoc.createElement("NAME");
    groupNameNode.text = el("txtGroupName").value;
    if(groupNameNode.text=="")
    {
		jetsennet.alert("组名称不能为空！","wrong");
		return;
    }
    rootNode.appendChild(groupNameNode);
   
    
    var descriptionNode = _groupXmlDoc.createElement("DESCRIPTION");       
    descriptionNode.text = el("txtDescription").value;
    rootNode.appendChild(descriptionNode);   
    
    
    var personIds = "";	
    var len =el("selMember").options.length;
	for(var i=0;i<len;i++)
	{    
	    if(personIds!="")
	        personIds +=  ",";
		personIds += el("selMember").options[i].value;
	}	
	
    var persongroupNode = _groupXmlDoc.createElement("PERSONTOGROUP");       
    persongroupNode.text = personIds;
    rootNode.appendChild(persongroupNode);   
    jetsennet.alert(rootNode.xml);
    return rootNode.xml;
}


//删除用户组
function deleteGroup(groupId)
{
	var ws = new JetsenWeb.Service(USERMANGER_WSDL);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult)
	{
		if (sResult.resultVal!=null)
		{
			jetsennet.alert("删除成功！");
			loadGroup($("txtGroupParentId").value);
		}
	}
	ws.onerror = function(ex){ jetsennet.error(ex);};
	jetsennet.confirm("删除分组,将自动删除关联信息,不可恢复,确认吗？", function () 
	{
		ws.call("uumObjDelete",["UUM_PERSONGROUP",groupId]);   
		return true;
	});
}
