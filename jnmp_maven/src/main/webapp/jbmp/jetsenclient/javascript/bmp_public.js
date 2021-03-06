//=============================================================================
// 仅用于BMP
//=============================================================================	 

//var NMP_SYSTEM_SERVICE     = JetsenWeb.appPath + "../../services/NMPSystemService?wsdl";
//var DMP_SYSTEM_SERVICE     = JetsenWeb.appPath + "../../services/DMPSystemService?wsdl";
//var NMP_SNMP_SERVICE = JetsenWeb.appPath + "../../services/NMPSnmpService?wsdl";
var BMP_SYSTEM_SERVICE     = JetsenWeb.appPath + "../../services/BMPSystemService?wsdl";
var BMP_RESOURCE_SERVICE = JetsenWeb.appPath + "../../services/BMPResourceService?wsdl";
// 四川权限
var NMP_PERMISSIONS_SERVICE = JetsenWeb.appPath + "../../services/NMPPermissionsService?wsdl";
//四川权限
var BMPSC_SYSTEM_SERVICE     = JetsenWeb.appPath + "../../services/BMPSCSystemService?wsdl";
var NMP_SYSTEM_SERVICE     = JetsenWeb.appPath + "../../services/NMPSystemService?wsdl";

JetsenWeb.require("js_sql");

//读取某个配置节
JetsenWeb.Application.getAppConfig = function(sectionNames)
{
	var ret;
	var sst = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
	sst.soapheader = JetsenWeb.Application.authenticationHeader;
	sst.async = false;	
	sst.displayLoading = false;
	sst.oncallback = function($)
	{
		ret = JetsenWeb.Xml.toObject($.resultVal);
	}
	sst.onerror = function(ex){};
	sst.call("nmpGetAppConfig",[sectionNames]);
	return ret;
};
JetsenWeb.Application.getSysConfig = function(configNames)
{
    var configs = {};
	var sst = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
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
	sst.call("nmpGetSysConfig",[configNames]);
	return configs;
};
//根据用户获取权限集
JetsenWeb.Application.getFunctionByUserId = function(fields,rightIds)
{
    rightIds = new String(rightIds);
    var conditionCollection = new JetsenWeb.SqlConditionCollection();
    var conditions = JetsenWeb.SqlCondition.create("","",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String);
	conditionCollection.SqlConditions.push(conditions);
    var _rights = rightIds.split(',');
    for(var i=0;i<_rights.length;i++)
    {
        conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID",_rights[i],JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
        conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",_rights[i],JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    }
	var ret;
	var ssv = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
	ssv.soapheader = JetsenWeb.Application.authenticationHeader;
	ssv.async = false;
	ssv.cacheLevel = 2;
	ssv.displayLoading = false;
	ssv.oncallback = function(r)
	{
		ret = r.resultVal;
	}
	ssv.onerror = function(ex){};
	ssv.call("nmpGetFunctionByUserId",[JetsenWeb.Application.userInfo.UserId,fields,conditionCollection.toXml()]);
	return ret;
}