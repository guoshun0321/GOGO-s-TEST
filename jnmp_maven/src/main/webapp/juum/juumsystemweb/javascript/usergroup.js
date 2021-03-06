
//用户组脚本===================================================================
function loadUserGroup()
{
    $("divParentGroupTree").innerHTML = "";
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"ID,NAME,PARENT_ID",OrderString:"Order By PARENT_ID",
           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
        
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        var gParentGroupTree = JetsenWeb.UI.Tree.createTree("parent-tree",ret.resultVal,{parentId:"0",parentField:"PARENT_ID",itemName:"Record",textField:"NAME",valueField:"ID",showCheck:false,paramFields:"ID,PARENT_ID,NAME"});
        gParentGroupTree.addItem(new JetsenWeb.UI.TreeItem("没有父级",null,null,null,{ID:0,NAME:""}));
        gParentGroupTree.onclick = function(item){ 
            $("txtParentGroup").value = valueOf(item.treeParam,"NAME","");
            $("hidParentId").value  = valueOf(item.treeParam,"ID","");
        };
        $("divParentGroupTree").appendChild(gParentGroupTree.render());
        loadGroup();
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}
//用户组
function loadGroup()
{		       
	var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",ResultFields:"ID,NAME,PARENT_ID,TYPE,GROUP_CODE,DESCRIPTION",PageInfo:null,OrderString:"Order By ID desc",
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions = [JetsenWeb.SqlCondition.create("ID","0",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric)];
    sqlQuery.Conditions = condition;       
        
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(sResult)
    {			    
    	renderGrid(sResult.resultVal);
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]); 
}		

/*
 *显示表格
 */
function renderGrid(xml){
	$("divContainer").innerHTML = "";

	myGridList.columns = [
	 { index: 0, fieldName: "NAME", width:280, name: "分组名称"},
	 { index: 1, fieldName: "TYPE", align: "center", name: "分组类型"},
     { index: 2, fieldName: "GROUP_CODE", width:250, name: "分组代号"},
     { index: 3, fieldName: "DESCRIPTION", width: 480, name: "描述信息" },
     { index: 4, fieldName: "ID,PARENT_ID", width: 45, align: "center", name: "编辑" },
     { index: 5, fieldName: "ID", width: 45, align: "center", name: "删除"}];

	myGridList.columns[1].format = function (val,vals){
		if(val == 0)
		{
			return "部门";
		}
		else if(val == 1)
		{
			return "栏目";
		}
		else if(val == 2)
		{
			return "分组";
		}
		else
		{
			return "频道";
		}
	}
	myGridList.columns[4].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"editUserGroup('"+vals[0]+"','"+vals[1]+"')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	myGridList.columns[5].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"deleteGroup('"+vals[0]+"')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}
	myGridList.parentId = 0;
	myGridList.idField = "ID";
	myGridList.parentField = "PARENT_ID";
	myGridList.treeControlIndex = 0;
	myGridList.treeOpenLevel = 1;
	myGridList.dataSource = xml;
	myGridList.render("divContainer");
	myGridList.colorSelectedRows();
	myGridList.ondoubleclick = function(row,col){
		var rowId = row.id;
		editUserGroup($(rowId).getAttribute('rid'),$(rowId).getAttribute('rgroup'));
	}
}

//用户组管理===========================================================
function editUserGroup(groupId,parentId)
{
    $('selMember').options.length = 0;
    var areaElements = JetsenWeb.Form.getElements('divUserGroup');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);         
 
    var sqlQuery = new JetsenWeb.SqlQuery();    
        JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
                QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
        
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", groupId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = condition;
        
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
    ws.oncallback = function(sResult)
    {     	   
		var objGroup = JetsenWeb.Xml.toObject(sResult.resultVal).Record;	
		el("txtGroupName").value= objGroup.NAME;
		el("txtGroupCode").value= valueOf(objGroup,"GROUP_CODE","");
		el("txtDescription").value= valueOf(objGroup,"DESCRIPTION","");	
		var GroupType;
		if(valueOf(objGroup,"TYPE","0")==0){
			GroupType='部门';
		}else if(valueOf(objGroup,"TYPE","0")==1){
			GroupType='栏目';
        }else if(valueOf(objGroup,"TYPE","0")==2){
        	GroupType='分组';
        }else if(valueOf(objGroup,"TYPE","0")==3){
        	GroupType='频道';
        }
		el("ddlGroupType").value= GroupType;	
		$("hidParentId").value = parentId;
        getParentNameByParentId(parentId);
        showRemindWordCount($("txtDescription").value,$('remindWord'),"60");
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
	ws.call("uumObjQuery",[sqlQuery.toXml()]); 
	
    getGroupUser(groupId);
    
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("new-group"),{title:"编辑用户组",submitBox:true,cancelBox:true,windowStyle:1,size:{width:500,height:450},maximizeBox:false,minimizeBox:false,showScroll:false});     
    dialog.controls =["divUserGroup"];
    dialog.onclosed = function()
    {
   	    document.getElementById("divParentGroupTree").style.display = "none";
   	    var popframeid = document.getElementById("divParentGroupTree").popframeid;
   	    if(document.getElementById(popframeid)!=null&&document.getElementById(popframeid)!=""){
   	    document.getElementById(popframeid).style.display = "none";
   	    }
    	JetsenWeb.UI.Windows.close("new-group");	
    }
    dialog.onsubmit = function(){      
        
        if(JetsenWeb.Form.Validate(areaElements,true))
        {                  
            var len =el("selMember").options.length;
            var userIds = "";
	        for(var i=0;i<len;i++)
	        {
	            if(userIds!="")
	                userIds += ","
		       userIds += el("selMember").options[i].value;		       
	        }
	        var ddlGroupType;
	        if($("ddlGroupType").value=='部门'){
	        	ddlGroupType=0;
	        }else if($("ddlGroupType").value=='栏目'){
	        	ddlGroupType=1;
	        }else if($("ddlGroupType").value=='分组'){
	        	ddlGroupType=2;
	        }else if($("ddlGroupType").value=='频道'){
	        	ddlGroupType=3;
	        }
	        var objGroup = {ID:groupId,
	            NAME:$("txtGroupName").value,
	            TYPE:ddlGroupType,
	            GROUP_CODE:$("txtGroupCode").value,
	            PARENT_ID:$("hidParentId").value,
	            DESCRIPTION:$("txtDescription").value,
	            GROUP_USER:userIds};	        
	                           
	        if(parseInt(getBytesCount($("txtDescription").value))>120){
            	jetsennet.alert("描述信息不能超过60个文字！");
            	return;
            }

            var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("new-group");
                loadGroup();  
                loadUserGroup();             
            }
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjUpdate",["UUM_USERGROUP",JetsenWeb.Xml.serialize(objGroup,"Request")]);
        }             
    };
    dialog.showDialog(); 
}
function newUserGroup()
{
    var areaElements = JetsenWeb.Form.getElements('divUserGroup');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);         
    $('selMember').options.length =0;
    $("hidParentId").value = 0;
    showRemindWordCount($("txtDescription").value,$('remindWord'),"60");
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("edit-group"),{title:"新建用户组",submitBox:true,cancelBox:true,windowStyle:1,size:{width:500,height:450},maximizeBox:false,minimizeBox:false,showScroll:false});     
    dialog.controls =["divUserGroup"];   
    dialog.onclosed = function()
    {
   	    document.getElementById("divParentGroupTree").style.display = "none";
   	    var popframeid = document.getElementById("divParentGroupTree").popframeid;
   	    if(document.getElementById(popframeid)!=null&&document.getElementById(popframeid)!=""){
   	    document.getElementById(popframeid).style.display = "none";
   	    }
    	JetsenWeb.UI.Windows.close("edit-group");	
    }
    dialog.onsubmit = function()
    {              
        if(JetsenWeb.Form.Validate(areaElements,true))
        {                             
            var len =el("selMember").options.length;
            var userIds = "";
	        for(var i=0;i<len;i++)
	        {
	            if(userIds!="")
	                userIds += ","
		       userIds += el("selMember").options[i].value;		       
	        }	
	        var ddlGroupType;
	        if($("ddlGroupType").value=='部门'){
	        	ddlGroupType=0;
	        }else if($("ddlGroupType").value=='栏目'){
	        	ddlGroupType=1;
	        }else if($("ddlGroupType").value=='分组'){
	        	ddlGroupType=2;
	        }else if($("ddlGroupType").value=='频道'){
	        	ddlGroupType=3;
	        }
	        var objGroup = {
	            NAME:$("txtGroupName").value,
	            TYPE:ddlGroupType,
	            GROUP_CODE:$("txtGroupCode").value,
	            PARENT_ID:$("hidParentId").value,
	            DESCRIPTION:$("txtDescription").value,
	            GROUP_USER:userIds};
	        
	        if(parseInt(getBytesCount($("txtDescription").value))>120){
            	jetsennet.alert("描述信息不能超过60个文字！");
            	return;
            }
	        
             var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("edit-group");
                loadGroup();     
                loadUserGroup();           
            }
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjInsert",["UUM_USERGROUP",JetsenWeb.Xml.serialize(objGroup,"Request")]);
        }             
    };
    dialog.showDialog(); 
}
function searchSelectUserData()
{
    gPageSelectUser.currentPage = 1;
    getSelectUserData();
}
function getSelectUserData()
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
        JetsenWeb.extend(sqlQuery,{IsPageResult:1,KeyId:"ID",gPageSelectUser:null,ResultFields:"",               
                QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USER"})});
    sqlQuery.OrderString = gPageSelectUser.orderBy;
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",getAllValues($("selMember")),JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotIn,JetsenWeb.SqlParamType.Numeric));
    if($('txtLoginName').value!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("LOGIN_NAME",$('txtLoginName').value,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));  
    } 
    if($('txtUserName').value!="")
    {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("USER_NAME",$('txtUserName').value,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.ILike,JetsenWeb.SqlParamType.String));  
    }
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        $('divSelectUserList').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectuser.xslt",ret.resultVal);
        //var o = new JetsenWeb.UI.GridList();
        gGridList.bind($('divSelectUserList'), $('tabSelectUser'));	
        gPageSelectUser.setRowCount($('hid_SUTotalCount').value);                
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}
function selectUser()
{
	searchSelectUserData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-user"),{title:"选择用户",submitBox:true,cancelBox:true,windowStyle:1,size:{width:500,height:331},maximizeBox:false,minimizeBox:false});     
    dialog.controls =["divSelectUser"]; 
    dialog.onsubmit = function()
    {     
        var obj = document.getElementsByName("chk_SelectUser");
        for(var i=0;i<obj.length;i++)
        {
	        if(obj[i].checked)
		        AddUserItem(obj[i].value,obj[i].getAttribute("itemName"));
        }		
        return true; 
    };
    dialog.showDialog();
    $('txtLoginName').value = "";
    $('txtUserName').value = "";
    $('divSelectUserList').innerHTML = "";
    $('divSelectUserPage').innerHTML = "";
    //getSelectUserData();
}
//添加用户成员
function AddUserItem(userID,userName)
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
		
//获取组的用户信息
function getGroupUser(groupID)
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
        JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
                QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USER"})});
        
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USER.GROUP_ID",groupID,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
        
	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(rolesResult)
	{
		var _roleXmlDoc = new JetsenWeb.XmlDoc();
		_roleXmlDoc.async = false;
		_roleXmlDoc.loadXML(rolesResult.resultVal);
		
		var _roleNodes = _roleXmlDoc.documentElement.selectNodes("Record");
		if(_roleNodes.length==0)
		{
			return;
		}
		for(var i=0;i<_roleNodes.length;i++)
		{
			var objNewOption = document.createElement("option");
			el("selMember").options.add(objNewOption);
			objNewOption.value =   JetsenWeb.Xml.getText(_roleNodes[i].selectSingleNode("ID"));
			objNewOption.innerHTML=   JetsenWeb.Xml.getText(_roleNodes[i].selectSingleNode("LOGIN_NAME"));
		}
	}
	ws.onerror = function(ex){ jetsennet.error(ex);};
	ws.call("uumObjQuery",[sqlQuery.toXml()]); 
}

//删除用户组
function deleteGroup(groupId)
{
	jetsennet.confirm("确定删除？", function () {
    	
	    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function(sResult)
	    {
		    if (sResult.resultVal!=null)
		    {			    
			    loadGroup();
			    loadUserGroup();
			    //jetsennet.alert("删除成功！");
		    }
	    }
	    ws.onerror = function(ex)
	    { 
	    	jetsennet.error(ex);
	    };	
		ws.call("uumObjDelete",["UUM_USERGROUP",groupId]);     
		
	    return true;
	     });

}
//select Item 删除方法
function selectOptionsDel(selCtrl)
{
    var _itemCount = selCtrl.options.length;	
    var selectedItemCount = 0;
    if (_itemCount>0)
    {
	    for(var i=_itemCount-1;i>=0;i--)
	    {
		    if(selCtrl.options[i].selected)
		    {	
			    selCtrl.removeChild(selCtrl.options[i]);
			    selectedItemCount++;
		    }
	    }
	    if(selectedItemCount == 0) {
	    	jetsennet.alert("请选择要删除的项！");
	    }
    }else
    {
    	jetsennet.alert("请选择要删除的项！");
    }
}

function getParentNameByParentId(Id)
{
	if (Id != 0)
	{
		var condition = new JetsenWeb.SqlConditionCollection();
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",Id,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	    
	    var sqlQuery = new JetsenWeb.SqlQuery();    
	    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"NAME",               
	           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
	     
	    sqlQuery.Conditions = condition;
	    
	    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function(ret)
	    {
	        var obj = JetsenWeb.Xml.toObject(ret.resultVal).Record;
	        if (obj != null)
	        {           
	        	$("txtParentGroup").value = obj.NAME;
	        }            
	    }
	    ws.onerror = function(ex){ jetsennet.error(ex);};
	    ws.call("uumObjQuery",[sqlQuery.toXml()]);
	}
}



//得到字符串字节数
function getBytesCount(str) 
{ 
	var bytesCount = 0; 
	if (str != null) 
	{ 
		for (var i = 0; i < str.length; i++) 
		{ 
			var c = str.charAt(i); 
			if (/^[\u0000-\u00ff]$/.test(c)) 
			{ 
				bytesCount += 1; 
			} 
			else 
			{ 
				bytesCount += 2; 
			} 
		} 
	} 
	return bytesCount; 
}

//textarea 作文字控制
function showRemindWordCount(textValue,remindWordHtml,wordCount){
	var countNum = 2*parseInt(wordCount);
	remindWordHtml.innerHTML = parseInt((countNum-parseInt(getBytesCount(textValue)))/2);
	if(countNum<parseInt(getBytesCount(textValue))){
		remindWordHtml.style.color = "red";
		remindWordHtml.innerHTML = 0;
	}else{
		remindWordHtml.style.color = "black";
	}
}