//=============================================================================
//系统配置
//=============================================================================

jetsennet.registerNamespace("jetsennet.Application");

jetsennet["IS_DEBUG"] = false;
jetsennet["DEFAULT_THEME"] = "jsnet";
jetsennet["LOGIN_URL"] = "/juum/jnetsystemweb/login.htm";
jetsennet["MAINPAGE_URL"] = "default.htm";
jetsennet["PORTAL_SERVICE"] = "../../services/UUMSystemService?wsdl";
jetsennet["DEFAULT_JS_LOAD"] = ["js_service","js_user","js_sql"];

//=============================================================================
//当调用服务方法需要Soap Header时，从此处读取值 
//以满足多种语言生成的服务需要(主要是命名空间)
//=============================================================================
jetsennet.Application.soapHeaderReader = function(itemName,itemType,methodName,location)
{
    var itemValue = "";
    switch(itemName)
    {
        case "UserId":
        case "userId":
            itemValue = jetsennet.Application.userInfo.UserId;
            break;
        case "LoginId":
        case "loginId":
            itemValue = jetsennet.Application.userInfo.LoginId;
            break;
        case "UserToken":
        case "userToken":
            itemValue = jetsennet.Application.userInfo.UserToken;
            break;
    }
    if(itemValue==null || itemValue=="")
        return jetsennet.Service.getMethodDefaultParamValue(itemType);
    return itemValue;
};