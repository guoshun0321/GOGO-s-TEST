﻿//=============================================================================
//  JUUMSystemWeb application
//=============================================================================


JetsenWeb.registerNamespace("JetsenWeb.Application");

JetsenWeb.valideLogin();

JetsenWeb.require(JetsenWeb.appPath+"../jetsenclient/javascript/uum_public.js",true);

JetsenWeb.importCss("jetsen");//"#style"

JetsenWeb.Application.getTypeMapping = function(/*string*/ typeName){
    switch(typeName){
        case "":
            break;
    }
    return typeName;
}

//webservice验证错误===========================================================
JetsenWeb.Service.prototype.onvalideerror = function(errorStr)
{   
    if(JetsenWeb.alertAndCall)
    {
        JetsenWeb.alertAndCall(errorStr,"JetsenWeb.gotoLogin();")
    }
    else
    {
        jetsennet.alert(errorStr);   
        JetsenWeb.gotoLogin();
    } 
    return true;
}