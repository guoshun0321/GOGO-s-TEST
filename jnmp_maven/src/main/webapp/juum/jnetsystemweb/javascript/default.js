//=============================================================================
//  Default.htm Pgae
//=============================================================================
function setWindowTitle(title)
{        
    $('spanWindowName').innerHTML = title;
}
function setFrameUrl()
{
    var strUrl = JetsenWeb.queryString("url");        
    if(!JetsenWeb.Util.isNullOrEmpty(strUrl) && strUrl.toLowerCase().indexOf("jnetsystemweb")<0)
    {
        document.getElementById("JetsenMain").src = strUrl;
    }
}
//获取用户权限
function getPortalFunction()
{	   
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    //ws.async = false;
    ws.soapheader = JetsenWeb.Application.getAuthenticationHeader();
    ws.oncallback = function(sResult)
    {		    	   
		var userFunctions = JetsenWeb.Xml.toObject(sResult.resultVal,"Table");       
        if (userFunctions && userFunctions.length>0)
        {			    			
	        createNavigation(userFunctions);				    
        }
        else	 
        {
	        $("divMenu").innerHTML="您没有任何权限!"
        }
    }
    ws.onerror = function(ex){  jetsennet.error(ex);}
    ws.call("uumGetUserFunctionTree",[JetsenWeb.Application.userInfo.UserId]);       
}
//取得权限树
function createNavigation(userFunctions)
{            	    
    gAccordion = new JetsenWeb.UI.Accordion($("divMenu"),80);
	gAccordion.onselectedindexchanged = function(index){JetsenWeb.Util.cookie("AccordionIndex",index);};
	
	for(var i=0;i<userFunctions.length;i++)
	{
	    if(userFunctions[i].PARENT_ID=="0" && userFunctions[i].ID!="0")
	    {
		    var pageUrl = userFunctions[i].PARAM;			    
		    if (!JetsenWeb.Util.isNullOrEmpty(pageUrl))
		    {
		        var queryString = 	JetsenWeb.getValideQueryString(getSubUrl(pageUrl));					   
		        if(pageUrl.indexOf('?')>=0){
                    pageUrl += "&"+queryString;
                }
                else{
                    pageUrl += "?"+queryString;
                }
                pageUrl += "&sysid="+userFunctions[i].ID;
		    }
		    else{
		        //continue;
		    };			
		        
	        var functionItems = getUserFunctionItems(userFunctions[i].ID,userFunctions[i].NAME,pageUrl,userFunctions);
	        if(functionItems.items.length>0)
	        {
	            var functionTree = new JetsenWeb.UI.Tree("tree"+functionItems.id);	           
                addFunctionItems(functionTree,functionItems.items);    
	            var folderContent = functionTree.render();	
	            
	            var folderIcon = userFunctions[i].PARAM;
	            folderIcon = folderIcon?"images/icons/"+folderIcon:"images/icons/defaulticon.gif";
	            
                gAccordion.addfolder("<img src='"+folderIcon+"' align='absmiddle'/>&nbsp;&nbsp;&nbsp;&nbsp;"+userFunctions[i].NAME,folderContent);         
            }	
        }		    
	}	
	var cookieIndex = JetsenWeb.Util.cookie("AccordionIndex");
	if(cookieIndex && cookieIndex!="")
	{
	    cookieIndex = parseInt(cookieIndex);		   
	}		
	cookieIndex = (cookieIndex && cookieIndex>=0 && cookieIndex<gAccordion.menuItems.length)?cookieIndex:0;
    if(gAccordion.menuItems.length>cookieIndex)
    {
        gAccordion.showfolderX(cookieIndex);
    }	   	
}  
//取得子权限树
function  getUserFunctionItems(id,name,url,userFuntions)
{
    var functionItem = {itemName:name,itemAction:url,items:[],id:id};//new JetsenWeb.UI.MenuItem(name,url,200,"","",id);
    var items = userFuntions;
    if(items && items.length)
    {
        for(var i=0;i<items.length;i++)
		{
		    if(items[i].PARENT_ID==id && items[i].ID!="0")
		    {			        
			    var pageUrl = items[i].PARAM;
			    if (!JetsenWeb.Util.isNullOrEmpty(pageUrl))
			    {	
			        var _queryString = 	JetsenWeb.getValideQueryString(getSubUrl(pageUrl));		       
			        if(pageUrl.indexOf('?')>=0){
                        pageUrl += "&"+_queryString;
                    }
                    else{
                        pageUrl += "?"+_queryString;
                    }
                    pageUrl += "&sysid="+items[i].ID;
			    }
			    else{
			        //continue;
			    };
			    functionItem.items.push(getUserFunctionItems(items[i].ID,items[i].NAME,pageUrl,userFuntions));
			}
		}
    }
    return functionItem;
}
 //获取部分路径地址
function getSubUrl(url)
{
    var tempArr = url.split("\?");   
    tempArr = tempArr[0].split("/"); 
    return tempArr[tempArr.length-2]+"/"+tempArr[tempArr.length-1];
};
//获取子权限
function getSubItems(funcItems,Id)
{
    var items = [];
    for(var i=0;i<funcItems.length;i++)
    {
        if(funcItems[i].PARENT_ID==Id && funcItems[i].ID!="0")
        {
            items.push(funcItems[i]);
        }
    }
    if(items.length>0)
        return items;
    return null;
}
//是否有子权限
function hasFunctionItem(item,funcItems)
{
    if(item.HAS_RIGHT)
        return true;
        
    var items = getSubItems(funcItems,item.ID);
    
    if(items==null)
        return false;
        
    for(var i=0;i<items.length;i++)
    {
        if(hasFunctionItem(items[i],funcItems))
            return true;
    }
    return false;
}
//生成子权限树
function addFunctionItems(tree,items)
{  
    if(tree && items)
    {  
        for(var i=0;i<items.length;i++)
        {            
            var subItem = new JetsenWeb.UI.TreeItem(items[i].itemName,items[i].itemAction);              
            subItem.onclick = function(){ setWindowTitle(this.treeText);};
            subItem.fileIcon = "images/tree-doc.gif"; 
            subItem.closeIcon = "images/tree-close.gif";
            subItem.openIcon = "images/tree-open.gif";
            tree.addItem(subItem);  
            
            if(items[i].items && items[i].items.length>0)
            {
                addFunctionItems(subItem,items[i].items);
            }                                              
        }
    }     
} 
function showModifyInfo()
{        
    $('trPassword').style.display = "none";
    $('trModifyPw').style.display = "";
    $('chkModifyPw').checked = false;
    
    var userInfo = {};
    userInfo.ID = JetsenWeb.Application.userInfo.UserId;
    
     var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USER"})});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",JetsenWeb.Application.userInfo.UserId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
        
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.async = false;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult)
	{						
	    var objUser = JetsenWeb.Xml.toObject(sResult.resultVal).Record;
	    userInfo.LOGIN_NAME = objUser.LOGIN_NAME;
	    userInfo.USER_TYPE = objUser.USER_TYPE;
		$("txtUserName").value = valueOf(objUser,"USER_NAME","");
		$("txtPassword").value = valueOf(objUser,"PASSWORD","");
		$("txtPassword2").value = valueOf(objUser,"PASSWORD","");
		$("txtDesc").value = valueOf(objUser, "DESCRIPTION", "");
		var userParam = valueOf(objUser, "APP_PARAM", "");
		try {
		    var paramDoc = new JetsenWeb.XmlDoc();
		    paramDoc.loadXML(userParam);
		    if (paramDoc != null && paramDoc.documentElement) {
		        $('ddl_PageTheme').value = valueOf(paramDoc.documentElement.selectSingleNode("PageTheme"), "text", "");
		        $('ddl_PageStyle').value = valueOf(paramDoc.documentElement.selectSingleNode("PageStyle"), "text", "");
		    }
		} catch (e) { }
		
//			$("txtHomePath").value = valueOf(objUser,"HOME_PATH","");
//			$("txtPathSize").value = valueOf(objUser,"QUOTA_SIZE","");
//			$("txtUsedSize").value = valueOf(objUser,"QUOTA_USED","");
	}
	ws.onerror = function(ex){ jetsennet.error(ex);_state = false;};
	ws.call("uumObjQuery",[sqlQuery.toXml()]);
				
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("user-info"),{title:"个人资料",okButtonText:"更新",submitBox:true,cancelBox:true,size:{width:550,height:240},maximizeBox:false,minimizeBox:false});   
    dialog.controls = ["divUserInfo"];
    dialog.onsubmit = function()
    {
        if($('chkModifyPw').checked==true)
        {
            if($('txtPassword').value=="")
            {
                jetsennet.alert('请输入用户密码!');
                $('txtPassword').focus();
                return;
            }
            if($('txtPassword').value!=$('txtPassword2').value)
            {
                jetsennet.alert('确认密码不正确，请重新输入!');
                $('txtPassword2').focus();
                return;
            }
        }	        
        userInfo.MODIFY_PW = $('chkModifyPw').checked==true?"1":"0";		        
	    userInfo.USER_NAME = $('txtUserName').value;
	    userInfo.PASSWORD = $('txtPassword').value;
	    userInfo.DESCRIPTION = $('txtDesc').value;
	    userInfo.APP_PARAM = JetsenWeb.Xml.serialize({ PageTheme: $('ddl_PageTheme').value, PageStyle: $('ddl_PageStyle').value }, "Param");
		    					    
        var ws2 = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);		
	    ws2.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws2.oncallback = function(sResult)
	    {						
	        $("divLoginName").innerHTML = userInfo.USER_NAME;
	        JetsenWeb.UI.Windows.close("user-info");
	        jetsennet.alert("资料已修改!");
	    }
	    ws2.onerror = function(ex){ jetsennet.error(ex);};
	    ws2.call("uumModifyUserInfo",[JetsenWeb.Xml.serialize(userInfo,"UUM_USER")]);		
    };
    //dialog.position = {top:100,left:180};
    dialog.showDialog();           
}	