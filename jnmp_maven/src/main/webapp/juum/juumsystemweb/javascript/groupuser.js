//=============================================================================
//  User.js application
//=============================================================================
        

 //加载用户=====================================================================================
    function loadUser()
    {    
        var sqlQuery = new JetsenWeb.SqlQuery();    
        JetsenWeb.extend(sqlQuery,{IsPageResult:1,KeyId:"ID",PageInfo:gUserPage,ResultFields:"",               
                QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USER"})});
        sqlQuery.Conditions = gUserCondition;
        sqlQuery.OrderString = gUserPage.orderBy;
        
        
        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function(ret)
        {
             $('divContainer').innerHTML = JetsenWeb.Xml.transformXML("xslt/groupuser.xslt",ret.resultVal);
             gUserGrid.bind($('divContainer'), $('tabUser'));
             gUserPage.setRowCount($('hid_UserCount').value);
        }
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.call("uumObjQuery",[sqlQuery.toXml()]);		
    }
    function searchUser()
    {
        gUserCondition.SqlConditions = [];
        if($('txtUserName').value!="")
        {
             var _txtCondition = new JetsenWeb.SqlCondition();
            _txtCondition.SqlLogicType = JetsenWeb.SqlLogicType.And;
            _txtCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("LOGIN_NAME",$('txtUserName').value,JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Like,JetsenWeb.SqlParamType.String));
            _txtCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("USER_NAME",$('txtUserName').value,JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Like,JetsenWeb.SqlParamType.String));
            gUserCondition.SqlConditions.push(_txtCondition);
        }
        if(JetsenWeb.Application.userInfo.UserGroups!="")
        {
            gUserCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USER.GROUP_ID",JetsenWeb.Application.userInfo.UserGroups,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.String));
        }
        gUserPage.currentPage = 1;
        loadUser();
    }	
//删除用户=====================================================================================
function deleteUser(keyId)
{
    if(keyId==1)
    {
        jetsennet.alert("系统不允许删除此用户！");
        return false;
    }
    
    var checkIds = [];
    if(keyId)
    {
        checkIds = [keyId];
    }
    else
    {
        checkIds = JetsenWeb.Form.getCheckedValues("chkUser");
    }
    
    if(checkIds.length==0)
    {
        jetsennet.alert("请选择要删除的项！");
        return;
    }    

    jetsennet.confirm("确定删除？", function () 
    {
        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function(ret)
        {
            loadUser();
        }
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.call("uumObjDelete",["UUM_USER","<Request><Item><Id>"+checkIds.join("</Id></Item><Item><Id>")+"</Id></Item></Request>"]);		
    return true;
    });
}
//新建用户=====================================================================================
function  newUser()
{
    gTabPane.select(0);
    var areaElements = JetsenWeb.Form.getElements('divUser');
    
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);
    
    $('tr_password').style.display = "";
    $('tr_ModifyPw').style.display = "none";
    $('txt_LOGIN_NAME').disabled = false;
    el("selectGroup").options.length = 0;
    el("selectRole").options.length = 0;
    $('chk_USER_TYPE').disabled = false;	  
    
    //登陆用户只属于一个分组时,在添加用户时加上用户所属默认分组
    if (JetsenWeb.Application.userInfo.UserGroups.split(",").length == 1){
         setUserDefaultGroup();
    }    
    
    var _dialog = new JetsenWeb.UI.ConfirmDialog();
    _dialog.title = "新建用户";
    _dialog.width = 600;
    _dialog.contentId = "divUser";
    _dialog.onsubmit = function(){
        
	    if(JetsenWeb.Form.Validate(areaElements,true))
	    {
	        if($('txt_PASSWORD').value!=$('txt_PASSWORD2').value)
	        {
	            jetsennet.alert('确认密码不正确，请重新输入！');
	            $('txt_PASSWORD2').focus();
	            return;
	        }
	        
	        var _userGroupIds = "";
            var len =el("selectGroup").options.length;
            for(var i=0;i<len;i++)
            {
                if(_userGroupIds!="")
                    _userGroupIds += ",";
                _userGroupIds += el("selectGroup").options[i].value;		
            }
            if (_userGroupIds == ""){
                jetsennet.alert("必须指定用户所在组！");return;
            }
            var _userRoleIds = "";
            var len =el("selectRole").options.length;
            for(var i=0;i<len;i++)
            {
                if(_userRoleIds!="")
                    _userRoleIds += ",";
	            _userRoleIds += el("selectRole").options[i].value;		
            }
            
	        var _userInfo = {
		        LOGIN_NAME:$('txt_LOGIN_NAME').value,
		        USER_NAME:$('txt_USER_NAME').value,
		        PASSWORD:$('txt_PASSWORD').value,			       
		        DESCRIPTION:$('txt_DESCRIPTION').value,
		        STATE:$('txt_STATE').value,
		        USER_CODE:$('txt_USER_CODE').value,			   			      
		        RIGHT_LEVEL:$('txt_RIGHT_LEVEL').value,
		        HOME_PATH:$('txt_HOME_PATH').value,
		        QUOTA_SIZE:$('txt_PATH_SIZE').value,
		        APP_PARAM:$('txt_APP_PARAM').value,
		        USER_TYPE:$('chk_USER_TYPE').checked==true?"110":"0",
		        EMAIL:$("txt_EMAIL").value,
		        BIRTHDAY:$('txt_BIRTHDAY').value,
		        FIELD_1:$('txt_FIELD_1').value,
		        DUTY_TITLE:$('txt_DUTY_TITLE').value,			        
		        OFFICE_PHONE:$('txt_OFFICE_PHONE').value,
		        MOBILE_PHONE:$('txt_MOBILE_PHONE').value,
		        USER_CARD:$('txt_USER_CARD').value,
		        FIELD_4:$('txt_FIELD_4').value,
		        SEX:$('sel_SEX').value,
		        GROUPID:gGroupId,//实际上没有意义，后台未处理
                GROUP_USER:_userGroupIds,
                ROLE_USER:_userRoleIds};
            
	        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	        ws.soapheader = JetsenWeb.Application.authenticationHeader;
	        ws.oncallback = function(ret)
	        {
	            JetsenWeb.UI.Dialogs.close($('divUser').dialogId);
	            loadUser();
	        };		        
	        ws.onerror = function(ex){ jetsennet.error(ex);};
	        ws.call("uumObjInsert",["UUM_USER",JetsenWeb.Xml.serialize(_userInfo,"UUM_USER")]);
	    } 
    };
    _dialog.show(); 
}
//编辑用户=================================================================
function  editUser(keyId)
{
    gTabPane.select(0);
    var areaElements = JetsenWeb.Form.getElements('divUser');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);
    
    $('tr_password').style.display = "none";
    $('tr_ModifyPw').style.display = "";
    $('chk_ModifyPw').checked = false;
    $('txt_LOGIN_NAME').disabled = true;
    $('chk_USER_TYPE').disabled = true;
        
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USER"})});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",keyId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
   
        var objUser = JetsenWeb.Xml.toObject(ret.resultVal).Record;
        $("txt_LOGIN_NAME").value = valueOf(objUser,"LOGIN_NAME","");
		$("txt_USER_NAME").value = valueOf(objUser,"USER_NAME","");
		$("txt_PASSWORD").value = valueOf(objUser,"PASSWORD","");
		$("txt_PASSWORD2").value = valueOf(objUser,"PASSWORD","");			
		$("txt_DESCRIPTION").value = valueOf(objUser,"DESCRIPTION","");
		$("txt_STATE").value = valueOf(objUser,"STATE","0");
		$("txt_USER_CODE").value = valueOf(objUser,"USER_CODE","");				
		$("txt_HOME_PATH").value = valueOf(objUser,"HOME_PATH","");
		$("txt_PATH_SIZE").value = valueOf(objUser,"QUOTA_SIZE","0");
		$("txt_APP_PARAM").value = valueOf(objUser,"APP_PARAM","");
	    $("txt_EMAIL").value = valueOf(objUser,"EMAIL","");
		$("txt_BIRTHDAY").value = valueOf(objUser,"BIRTHDAY","").substr(0,10);
		$("txt_FIELD_1").value = valueOf(objUser,"FIELD_1","");
		$('chk_USER_TYPE').checked=objUser.USER_TYPE=="110";
		$("txt_DUTY_TITLE").value = valueOf(objUser,"DUTY_TITLE","");
		$('txt_RIGHT_LEVEL').value = valueOf(objUser,"RIGHT_LEVEL","0");
		$('txt_OFFICE_PHONE').value =  valueOf(objUser,"OFFICE_PHONE","");
		$('txt_MOBILE_PHONE').value =  valueOf(objUser,"MOBILE_PHONE","");
		$('sel_SEX').value =  valueOf(objUser,"SEX","0");
		$('txt_USER_CARD').value =  valueOf(objUser,"USER_CARD","0");
		$('txt_FIELD_4').value =  valueOf(objUser,"FIELD_4","0");
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
    
	getUserRole(keyId);
	getUserGroup(keyId);
	
    var _dialog = new JetsenWeb.UI.ConfirmDialog();
    _dialog.title = "编辑用户资料";
    _dialog.width = 600;
    _dialog.contentId = "divUser";
    _dialog.onsubmit = function(){
	    if(JetsenWeb.Form.Validate(areaElements,true))
	    {
	        if($('chk_ModifyPw').checked==true)
            {
                if($('txt_PASSWORD').value=="")
                {
                    jetsennet.alert('请输入用户密码！');
                    $('txt_PASSWORD').focus();
                    return;
                }
                if($('txt_PASSWORD').value!=$('txt_PASSWORD2').value)
                {
                    jetsennet.alert('确认密码不正确，请重新输入！');
                    $('txt_PASSWORD2').focus();
                    return;
                }
            }	
            var _userGroupIds = "";
            var len =el("selectGroup").options.length;
            for(var i=0;i<len;i++)
            {
                if(_userGroupIds!="")
                    _userGroupIds += ",";
	            _userGroupIds += el("selectGroup").options[i].value;		
            }	
            if (_userGroupIds == ""){
                jetsennet.alert("必须指定用户所在组！");return;
            }
            var _userRoleIds = "";
            var len =el("selectRole").options.length;
            for(var i=0;i<len;i++)
            {
                if(_userRoleIds!="")
                    _userRoleIds += ",";
	            _userRoleIds += el("selectRole").options[i].value;		
            }
                    
	        var _userInfo = {		            
	            ID:keyId,
		        LOGIN_NAME:$('txt_LOGIN_NAME').value,
		        USER_NAME:$('txt_USER_NAME').value,
		        PASSWORD:$('txt_PASSWORD').value,
		        MODIFY_PW:$('chk_ModifyPw').checked==true?"1":"0",			        
		        DESCRIPTION:$('txt_DESCRIPTION').value,
		        STATE:$('txt_STATE').value,
		        USER_CODE:$('txt_USER_CODE').value,	
		        RIGHT_LEVEL:$('txt_RIGHT_LEVEL').value,
		        HOME_PATH:$('txt_HOME_PATH').value,
		        QUOTA_SIZE:$('txt_PATH_SIZE').value,
		        APP_PARAM:$('txt_APP_PARAM').value,		
		        USER_TYPE:$('chk_USER_TYPE').checked==true?"110":"0",
		        EMAIL:$("txt_EMAIL").value,
		        BIRTHDAY:$('txt_BIRTHDAY').value,
		        FIELD_1:$('txt_FIELD_1').value,
		        OFFICE_PHONE:$('txt_OFFICE_PHONE').value,
		        MOBILE_PHONE:$('txt_MOBILE_PHONE').value,
		        DUTY_TITLE:$('txt_DUTY_TITLE').value,
		        USER_CARD:$('txt_USER_CARD').value,
		        FIELD_4:$('txt_FIELD_4').value,
		        SEX:$('sel_SEX').value,
                GROUP_USER:_userGroupIds,
                ROLE_USER:_userRoleIds};
            
	        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	        ws.soapheader = JetsenWeb.Application.authenticationHeader;
	        ws.oncallback = function(ret)
	        {
	            JetsenWeb.UI.Dialogs.close($('divUser').dialogId);
	            loadUser();
	        }
	        ws.onerror = function(ex){ jetsennet.error(ex);};
	        ws.call("uumObjUpdate",["UUM_USER",JetsenWeb.Xml.serialize(_userInfo,"UUM_USER")]);
	    } 
    };
    _dialog.show(); 
}

//添加组===================================================================
function AddGroupItem(groupID,groupName)
{
 
	var len =$("selectGroup").options.length;
	for(var i=0;i<len;i++)
	{
		if($("selectGroup").options[i].value == groupID)
		{
			return;
		}
	}		
	var objNewOption = document.createElement("option");
	$("selectGroup").options.add(objNewOption);
	objNewOption.value =  groupID;
	objNewOption.innerHTML = groupName;		
}	
function selectUserGroup()
{
    var _dialog2 = new JetsenWeb.UI.ConfirmDialog();
    _dialog2.width = 520;
    _dialog2.title = "选择用户组";       
    _dialog2.contentId = "divSelectUserGroup";
    _dialog2.onsubmit = function(){     
        var obj = document.getElementsByName("chk_SelectUserGroup");
        for(var i=0;i<obj.length;i++)
        {
	        if(obj[i].checked)
		        AddGroupItem(obj[i].value,obj[i].getAttribute("itemName"));
        }		
        return true; 
    };
    _dialog2.show();
    getSelectUserGroupData();
}
function getSelectUserGroupData()
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"", QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});       
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",0,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));        
    
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",JetsenWeb.Application.userInfo.UserGroups,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        $('divSelectUserGroupList').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectusergroup.xslt",ret.resultVal);
        var o = new JetsenWeb.UI.GridList();
        var rc = o.bind($('divSelectUserGroupList'), $('tabSelectUserGroup'));	                     
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}
//添加角色=================================================================
function AddRoleItem(roleID,roleName)
{
	var len =$("selectRole").options.length;
	for(var i=0;i<len;i++)
	{
		if($("selectRole").options[i].value == roleID)
		{
			return;
		}
	}		
	var objNewOption = document.createElement("option");
	$("selectRole").options.add(objNewOption);
	objNewOption.value =  roleID;
	objNewOption.innerHTML = roleName;		
}
function selectUserRole()
{
    var _dialog = new JetsenWeb.UI.ConfirmDialog();
    _dialog.width = 520;
    _dialog.title = "选择用户角色";       
    _dialog.contentId = "divSelectUserRole";
    _dialog.onsubmit = function(){     
        var obj = document.getElementsByName("chk_SelectUserRole");
        for(var i=0;i<obj.length;i++)
        {
	        if(obj[i].checked)
		        AddRoleItem(obj[i].value,obj[i].getAttribute("itemName"));
        }		
        return true; 
    };
    _dialog.show();
    getSelectUserRoleData();
}
getSelectUserRoleData = function()
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_ROLE"})}); 
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("NAME","管理员",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotLike,JetsenWeb.SqlParamType.String));
    sqlQuery.Conditions = condition;
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        $('divSelectUserRoleList').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectuserrole.xslt",ret.resultVal);
        var o = new JetsenWeb.UI.GridList();
        var rc = o.bind($('divSelectUserRoleList'), $('tabSelectUserRole'));	                     
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}


//设置用户的组信息=========================================================
function setUserDefaultGroup()
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USERGROUP.USER_ID",JetsenWeb.Application.userInfo.UserId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
                    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(sResult)
    {
	    var _groupNodes = JetsenWeb.Xml.toObject(sResult.resultVal,"Record");
	   
	    if(_groupNodes==null)
	    {
		    return;
	    }
	    for(var i=0;i<_groupNodes.length;i++)
	    {
	        var objNewOption = document.createElement("option");
	        el("selectGroup").options.add(objNewOption);
	        objNewOption.value =   _groupNodes[i].ID;
	        objNewOption.innerHTML=   _groupNodes[i].NAME;
	    }
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]); 
}



//获取用户的组信息=========================================================
function getUserGroup(userID)
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USERGROUP.USER_ID",userID,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
                    
    el("selectGroup").options.length = 0;
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(sResult)
    {
	    var _groupNodes = JetsenWeb.Xml.toObject(sResult.resultVal,"Record");
	   
	    if(_groupNodes==null)
	    {
		    return;
	    }
	    for(var i=0;i<_groupNodes.length;i++)
	    {
            
	        var objNewOption = document.createElement("option");
	        el("selectGroup").options.add(objNewOption);
	        objNewOption.value =   _groupNodes[i].ID;
	        objNewOption.innerHTML=   _groupNodes[i].NAME;
	    }
	    
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]); 
}

//获取用户的角色信息=======================================================
function getUserRole(userID)
{   
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_ROLE"})});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_ROLE.USER_ID",userID,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
    
    el("selectRole").options.length = 0;
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(sResult)
    {		
        var _roleNodes = JetsenWeb.Xml.toObject(sResult.resultVal,"Record");
	    if(_roleNodes==null)
	    {
		    return;
	    }	 
	    for(var i=0;i<_roleNodes.length;i++)
	    {
		    var objNewOption = document.createElement("option");
		    el("selectRole").options.add(objNewOption);
		    objNewOption.value =   _roleNodes[i].ID;
		    objNewOption.innerHTML=   _roleNodes[i].NAME;
	    }
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]); 
}
//select Item 删除方法
function selectOptionsDel(selCtrl)
{
    var _itemCount = selCtrl.options.length;	
    if (_itemCount>0)
    {
	    for(var i=_itemCount-1;i>=0;i--)
	    {
		    if(selCtrl.options[i].selected)
		    {	
			    selCtrl.removeChild(selCtrl.options[i]);
		    }
	    }
    }
}