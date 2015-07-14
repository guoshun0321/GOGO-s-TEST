// 初始化权限
function initFunction()
{
	var fields = "ID,PARAM,STATE";
	var parentId = JetsenWeb.queryString("sysid");
	if(parentId != "")
	{
		var treeXmlDoc = new JetsenWeb.XmlDoc();
		var treeResult = JetsenWeb.Application.getFunctionByUserId(fields,parentId);
		treeXmlDoc.loadXML(treeResult);

		for (var property in gFunctions)
		{
			//safari event not to setter
			if(!(IS_SAFARI && property == "event"))
			{
				gFunctions[property] = treeXmlDoc.selectSingleNode("DataSource/UUM_FUNCTION[PARAM='" + property + "' and STATE=0]") != null;
			}
		}  
	}
}