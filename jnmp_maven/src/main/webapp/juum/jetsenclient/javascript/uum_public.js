//lixiaomin
//=============================================================================
// 仅用于用户管理子系统
//=============================================================================	 

JetsenWeb.require("js_sql");

//统一用户管理服务
var UUM_SYSTEM_SERVICE = JetsenWeb.appPath+"../../services/UUMSystemService?wsdl";

var UUM_EXTND_SERVICE = JetsenWeb.appPath+"../../services/ExtendUUMSystemService?wsdl";

var MAM_SYSTEM_SERVICE   = JetsenWeb.appPath+"../../services/MAMSystemService?wsdl";

var BMP_SYSTEM_SERVICE = JetsenWeb.appPath + "../../services/BMPSystemService?wsdl";
//四川权限
var NMP_PERMISSIONS_SERVICE = JetsenWeb.appPath + "../../services/NMPPermissionsService?wsdl";

JetsenWeb.Application.getSysConfig = function(configNames)
{ 
    var configs = {};
	var sst = new JetsenWeb.Service(MAM_SYSTEM_SERVICE);
	sst.soapheader = JetsenWeb.Application.authenticationHeader;
	sst.async = false;	
	sst.displayLoading = false;
	sst.oncallback = function(ret)
	{
		var obj = JetsenWeb.Xml.toObject(ret.resultVal);
		if(obj!=null && obj["Config"]!=null)
		{
		    obj["Config"] = obj["Config"].length?obj["Config"]:[obj["Config"]];
		    for(var i=0;i<obj["Config"].length;i++)
		    {
		        configs[obj["Config"][i].NAME] = obj["Config"][i].DATA;
		    }
		}
	}
	sst.onerror = function(ex){};
	sst.call("mamGetSysConfig",[configNames]);
	return configs;
}