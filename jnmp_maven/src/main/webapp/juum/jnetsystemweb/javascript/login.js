function login()
{    
    var name = JetsenWeb.Util.trim($("txtUserName").value);
    var password = JetsenWeb.Util.trim($("txtPassword").value);
    if(name==""){
    	jetsennet.alert("请输入用户名！");
    	document.getElementById("jetsen-alert_content").style.height = 114;
    	document.getElementById("jetsen-alert").style.height = "";
        return;
    }
    if(password==""){
        jetsennet.alert("请输入密码！");
        document.getElementById("jetsen-alert_content").style.height = 114;
        document.getElementById("jetsen-alert").style.height = "";
        return;
    }
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);       
    ws.onerror = function(ex){jetsennet.error(ex);
    document.getElementById("jetsen-error_content").style.height = 114;
    document.getElementById("jetsen-error").style.height = "";
    }
    ws.oncallback = function(sResult){          
	    loginSuccess(sResult);	        
    }
    ws.call("uumUserLogin",[name,password]);
}
function loginSuccess(sResult)
{
    var userInfoXmlDoc = new JetsenWeb.XmlDoc();				
    userInfoXmlDoc.loadXML(sResult.resultVal);	
    
    JetsenWeb.Util.cookie("UserLoginId",$("txtUserName").value,{ expires:7});	    

    var userInfo = new JetsenWeb.UserProfile();
    userInfo.fromXml(userInfoXmlDoc);
    var userParam = valueOf(userInfoXmlDoc.documentElement.selectSingleNode("UserParam"),"text","");
    if(userParam!="" && !autoLogin)
    {
        try{
            var paramDoc = new JetsenWeb.XmlDoc();
            paramDoc.loadXML(userParam);
            if(paramDoc!=null && paramDoc.documentElement)
            {
                userInfo.PageTheme = valueOf(paramDoc.documentElement.selectSingleNode("PageTheme"), "text", "");
                var mainPage = valueOf(paramDoc.documentElement.selectSingleNode("PageStyle"), "text", "");
                if (mainPage) {
                    gDefaultPage = mainPage;
                }
            }
        }catch(e){}
    }
    else
    {
    	try{
            var paramDoc = new JetsenWeb.XmlDoc();
            paramDoc.loadXML(userParam);
            if(paramDoc!=null && paramDoc.documentElement)
            {
                userInfo.PageTheme = valueOf(paramDoc.documentElement.selectSingleNode("PageTheme"), "text", "");
                var mainPage = valueOf(paramDoc.documentElement.selectSingleNode("PageStyle"), "text", "");
                if (mainPage) {
                    gDefaultPage = mainPage;
                }
                
                gDefaultPage += "?url=" + autoDefaultPage;
            }
        }catch(e){}
    }
    //编导用户
    if(userInfo.UserType=="110")
    {            
        selectColumn(userInfo); 
        return;
    }
    
    JetsenWeb.setUserInfo(userInfo);
    var pageUrl = JetsenWeb.queryString("url");             
    if(JetsenWeb.LinkType == JetsenWeb.UserCardLink && !JetsenWeb.Util.isNullOrEmpty(pageUrl)){
        var _userCard = JetsenWeb.createUserCard();   
        if(!JetsenWeb.Util.isNullOrEmpty(_userCard)){  
            if(pageUrl.indexOf("?")>=0)
                pageUrl = pageUrl+"&userCard="+_userCard;
            else
                pageUrl = pageUrl+"?userCard="+_userCard;
            window.location = gDefaultPage+"?url="+escape(pageUrl);
            return;
        }
    }
    window.location = gDefaultPage; 
}
function selectColumn(userInfo)
{
    JetsenWeb.require("js_window");
    JetsenWeb.require("js_sql");
    var sqlQuery = new JetsenWeb.SqlQuery();
    
    var condition = new JetsenWeb.SqlConditionCollection();
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",ResultFields:"ID,NAME",PageInfo:null,QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USERGROUP.USER_ID",userInfo.UserId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.Numeric));  
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE","1",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));  
    sqlQuery.Conditions = condition;
    
    JetsenWeb.Application.userInfo = userInfo;
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.cacheLevel = 2;
    ws.oncallback = function(ret)
    {
         var objColumns = JetsenWeb.Xml.toObject(ret.resultVal,"Record");
         if(objColumns==null)
         {
            jetsennet.alert("找不到相关栏目!");
            return;
         }
        if(objColumns.length==1)
        {
            userInfo.ColumnId = objColumns[0].ID;               
            JetsenWeb.setUserInfo(userInfo);               
            window.location = "default.htm?column="+escape(objColumns[0].NAME);                            
        }
        else
        {
            $('divSelectColumn').innerHTML = "";
            var columnHtml = [];
            for(var i=0;i<objColumns.length;i++)
            {
                columnHtml.push("<div><input type='radio' value='"+objColumns[i].ID+"' name='rad_Column' "+(i==0?"checked":"")+"/>"+objColumns[i].NAME+"</div>");
            }
            $('divSelectColumn').innerHTML = columnHtml.join("");
         
            var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-column"),{title:"选择栏目",submitBox:true,cancelBox:true,size:{width:400,height:300},maximizeBox:false,minimizeBox:false});    
            dialog.controls =["divSelectColumn"];               
            dialog.onsubmit = function()
            {         
                var _isCheck = false;
                var _columnName = "";
                var _objs = document.getElementsByName("rad_Column");   
                if(_objs!=null)
                {
                    for(var i=0;i<_objs.length;i++)
                    {
                        if(_objs[i].checked == true)
                        {
                            _isCheck = true;
                            _columnName = _objs[i].parentNode.innerText;
                            userInfo.ColumnId = _objs[i].value;
                            break;
                        }
                    }
                }  
                if(_isCheck==false)
                {
                    jetsennet.alert("请选择栏目!");
                    return;
                }                    
                JetsenWeb.setUserInfo(userInfo);                
                window.location = gDefaultPage+"?column="+escape(_columnName); 
            };
            dialog.showDialog();                       
         }
    };
    ws.onerror = function(ex){jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}
function keyDown()
{
    if(JetsenWeb.getEvent().keyCode==13)
	    login();
}
	
var autoLogin = false;
var autoDefaultPage = "";

function pageInit()
{    
	$("txtUserName").value = jetsennet.queryString("id");
	$("txtPassword").value = jetsennet.queryString("pw");
	
	var sso_userName = JetsenWeb.Util.cookie("sso_userName");
	var sso_password = JetsenWeb.Util.cookie("sso_password");
	if(sso_userName != "" && sso_password != "")
	{
		$("txtUserName").value = sso_userName;
		$("txtPassword").value = sso_password;
	}
	if ($("txtUserName").value != "" && $("txtPassword").value != "")
	{
		autoLogin = true;
		var pageUrl = JetsenWeb.queryString("url"); 
		if(JetsenWeb.LinkType == JetsenWeb.UserCardLink && JetsenWeb.isLogin())
	    {
			if(!JetsenWeb.Util.isNullOrEmpty(pageUrl)){
	            var _userCard = JetsenWeb.createUserCard();
	            if(!JetsenWeb.Util.isNullOrEmpty(_userCard)){  
	                if(pageUrl.indexOf("?")>=0)
	                    pageUrl = pageUrl+"&userCard="+_userCard;
	                else
	                    pageUrl = pageUrl+"?userCard="+_userCard;
	            }	           
	        }  
	    }
		autoDefaultPage = escape(pageUrl);
		
		var name = jetsennet.util.trim($("txtUserName").value);
		var password = jetsennet.util.trim($("txtPassword").value);
	    
		var ws = new jetsennet.Service(UUM_SYSTEM_SERVICE);
		ws.async = false;
		ws.oncallback = function (sResult) {
			loginSuccess(sResult);
		};
		ws.onerror = function (ex) { jetsennet.error(ex); };
		ws.call("uumUserLogin", [name, password]);
	}
	
    if(JetsenWeb.isIE())
    {
        document.attachEvent("onkeydown",keyDown);
    }
    else
    {
        document.addEventListener("keydown",keyDown,false);
    }
    
    $('txtUserName').value = JetsenWeb.Util.cookie("UserLoginId");
    
    var isOut = JetsenWeb.queryString("out");
    if(isOut && isOut=="1"){
        JetsenWeb.setUserInfo();
        return;
    }
    if(JetsenWeb.LinkType == JetsenWeb.UserCardLink && JetsenWeb.isLogin())
    {
        var pageUrl = JetsenWeb.queryString("url");             
        if(!JetsenWeb.Util.isNullOrEmpty(pageUrl)){
            var _userCard = JetsenWeb.createUserCard();
            if(!JetsenWeb.Util.isNullOrEmpty(_userCard)){  
                if(pageUrl.indexOf("?")>=0)
                    pageUrl = pageUrl+"&userCard="+_userCard;
                else
                    pageUrl = pageUrl+"?userCard="+_userCard;
                window.location = gDefaultPage+"?url="+escape(pageUrl);
                return;
            }	           
        }  
    }
}