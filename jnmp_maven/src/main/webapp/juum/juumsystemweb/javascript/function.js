/*
 *加载数据
 */
function loadData()
{
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"ID,PARENT_ID,NAME,PARAM,STATE,DESCRIPTION,VIEW_POS",          
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_FUNCTION"})});
   
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions = [JetsenWeb.SqlCondition.create("ID","0",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric)];
    sqlQuery.Conditions = condition;

    sqlQuery.OrderString = pInfo.orderBy;
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
    	renderGrid(ret.resultVal);
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
	 { index: 0, fieldName: "ID", width:30, align: "center", isCheck: true, checkName: "chkFunction"},
     { index: 1, fieldName: "NAME", width:200, name: "名称"},
	 { index: 2, fieldName: "ID", width:100, name: "权限编号"},
	 { index: 3, fieldName: "VIEW_POS", width: 65, name: "排序号" },
     { index: 4, fieldName: "STATE", width: 60,align: "center", name: "状态" },
     { index: 5, fieldName: "PARAM", width:450 , name: "参数" },
     { index: 6, fieldName: "DESCRIPTION", width:205, name: "描述" },
     { index: 7, fieldName: "ID", width: 45, align: "center", name: "编辑" },
     { index: 8, fieldName: "ID", width: 45, align: "center", name: "删除"}];

	myGridList.columns[4].format = function (val,vals){
		if(val == 0)  
		{
			return "启用";
		}
		else
		{
			return "禁用";
		}
	}
	myGridList.columns[7].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"editFunction('"+vals[0]+"')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	myGridList.columns[8].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"deleteFunction('"+vals[0]+"')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}
	myGridList.parentId = 0;
	myGridList.idField = "ID";
	myGridList.parentField = "PARENT_ID";
	myGridList.treeControlIndex = 1;
	myGridList.treeOpenLevel = 0;
	myGridList.dataSource = xml;
	myGridList.render("divContainer");
	myGridList.colorSelectedRows();
	myGridList.ondoubleclick = function(row,col){
		var rowId = row.id;
		var splitleng = rowId.split("-");
		editFunction(splitleng[splitleng.length-1]);
	}
	
	var chkFunctions = document.getElementsByName("chkFunction");
    for (var i = 0; i < chkFunctions.length; i++)
    {
	   	jetsennet.addEvents(chkFunctions[i], "click", function() {
	   		changeButtonStateByCheckboxState("chkFunction", "btnDelete");
	   	});
    }
    jetsennet.addEvents($("chkFunction-all"), "click", function() {
		changeButtonStateByCheckboxState("chkFunction", "btnDelete");
	});
}


/*
 *增加权限
 */
function newFunction()
{
    var areaElements = JetsenWeb.Form.getElements('divFunction');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);         
    $('txt_ID').disabled = false;
    $('hidParentId').value = "0";
    
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog,{submitBox:true,maximizeBox: false, minimizeBox: false,cancelBox:true,windowStyle:1,size:{width:550,height:0},title:"新建系统权限",showScroll:false});           
    dialog.controls = ["divFunction"];
    dialog.onclosed = function()
    {
   	    document.getElementById("divFunctionTree").style.display = "none";
   	    var popframeid = document.getElementById("divFunctionTree").popframeid;
   	    if(document.getElementById(popframeid)!=null&&document.getElementById(popframeid)!=""){
   	    document.getElementById(popframeid).style.display = "none";
   	    }
    	JetsenWeb.UI.Windows.close("new-object-win");	
    }
    dialog.onsubmit = function()
    { 
        if(JetsenWeb.Form.Validate(areaElements,true)){
        	
        	if (functionIsExists($("txt_ID").value))
		    {
		    	var txtId = $("txt_ID");
		    	txtId.setAttribute("isvalidateerror","1");
		    	txtId.title = "权限编号重复";
		    	txtId.style.borderColor = jetsennet.form.validateErrorBorderColor;
		    	txtId.style.backgroundColor = jetsennet.form.validateErrorBgColor;
	        	return;
		    }
            
            var objXml = "<Function><ID>"+$('txt_ID').value+"</ID><PARENT_ID>"+$('hidParentId').value+"</PARENT_ID><NAME>"+JetsenWeb.Xml.xmlEscape($('txt_Name').value)+"</NAME>"
            objXml+= "<PARAM>"+JetsenWeb.Xml.xmlEscape($('txt_Param').value)+"</PARAM>";
            objXml+= "<DESCRIPTION>"+JetsenWeb.Xml.xmlEscape($('txt_Desc').value)+"</DESCRIPTION>";
            objXml+= "<STATE>"+attributeOf($('txtState'),"selectedValue","")+"</STATE>";
            objXml+= "<VIEW_POS>"+$('txt_ViewPos').value+"</VIEW_POS>";
            objXml+= "<TYPE>"+attributeOf($('txtType'),"selectedValue","")+"</TYPE></Function>";
            
		    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("new-object-win");	       
                loadData();
                loadParentFunction();	                
            }
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjInsert",["UUM_FUNCTION",objXml]);
	    }             
    };
    dialog.showDialog();
}

/*
 *加载树形父权限
 */
function loadParentFunction()
{    
    $('divFunctionTree').innerHTML = "";
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE",1,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"ID,NAME,PARENT_ID",OrderString:"Order By PARENT_ID,VIEW_POS",
           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_FUNCTION"})});
     
    sqlQuery.Conditions = condition;
        
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        gFunctionTree = JetsenWeb.UI.Tree.createTree("parent-tree",ret.resultVal,{parentId:"0",parentField:"PARENT_ID",itemName:"Record",textField:"NAME",valueField:"ID",showCheck:false,paramFields:"ID,PARENT_ID,NAME"});
        gFunctionTree.addItem(new JetsenWeb.UI.TreeItem("没有父级",null,null,null,{ID:0,NAME:""}));
        gFunctionTree.onclick = function(item){ 
            $('txt_Function').value = valueOf(item.treeParam,"NAME","");
            $('hidParentId').value  = valueOf(item.treeParam,"ID","");
        };
        $('divFunctionTree').appendChild(gFunctionTree.render());
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}

/*
 *删除权限
 */
function deleteFunction(Id)
{
    var checkIds = [];
    if(Id)
    {
        checkIds = [Id];
    }
    else
    {
        checkIds = JetsenWeb.Form.getCheckedValues("chkFunction");
    }
    
    if(checkIds.length==0)
    {
        jetsennet.alert("请选择要删除的项！");
        return;
    }
    
    $('chkDeleteAll').checked = false;
    /*var dialog = new JetsenWeb.UI.Window("delete-object-win");
    JetsenWeb.extend(dialog,{submitBox:true,cancelBox:true,maximizeBox: false, minimizeBox: false,size:{width:350,height:150},title:"确定删除？"});           
    dialog.controls = ["divDelete"];
    dialog.onsubmit = function()
    {        
        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function(ret)
        {                       	                
            loadData();     
            JetsenWeb.UI.Windows.close("delete-object-win");
        }
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.call("uumObjDelete",["UUM_FUNCTION","<Request>"+($('chkDeleteAll').checked==true?"<Recursive>1</Recursive>":"")+"<Item><Id>"+checkIds.join("</Id></Item><Item><Id>")+"</Id></Item></Request>"]);
    };
    dialog.showDialog();*/
    
    jetsennet.confirm("确定删除？", function () 
	{
		 var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	     ws.soapheader = JetsenWeb.Application.authenticationHeader;
	     ws.oncallback = function(ret)
	     {                       	                
	         loadData();     
	         JetsenWeb.UI.Windows.close("delete-object-win");
	     }
	     ws.onerror = function(ex){ jetsennet.error(ex);};
	     ws.call("uumObjDelete",["UUM_FUNCTION","<Request>"+($('chkDeleteAll').checked==true?"<Recursive>1</Recursive>":"")+"<Item><Id>"+checkIds.join("</Id></Item><Item><Id>")+"</Id></Item></Request>"]);
        
         return true;
    });
}


/*
 *编辑权限
 */
function editFunction(Id)
{   
    var areaElements = JetsenWeb.Form.getElements('divFunction');
    JetsenWeb.Form.resetValue(areaElements); 
    JetsenWeb.Form.clearValidateState(areaElements);    
    $('txt_ID').disabled = true;
    JetsenWeb.UI.DropDownList.initOptions('txtState');
    JetsenWeb.UI.DropDownList.initOptions('txtType');    
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",Id,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_FUNCTION"})});
     
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(ret)
    {
        var obj = JetsenWeb.Xml.toObject(ret.resultVal).Record;
        if(obj!=null)
        {           
            $('txt_ID').value = obj.ID;
            $('txt_Name').value = valueOf(obj,"NAME",""); 
            $('txt_Param').value =valueOf(obj,"PARAM","") ;
            $('txt_Desc').value =valueOf(obj,"DESCRIPTION",""); 
            JetsenWeb.UI.DropDownList['txtState'].setValue(obj.STATE);
            JetsenWeb.UI.DropDownList['txtType'].setValue(obj.TYPE);    
            $('txt_ViewPos').value = valueOf(obj,"VIEW_POS","0"); 
            $('hidParentId').value = obj.PARENT_ID;
            getParentNameByParentId(obj.PARENT_ID);
        }            
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
          
    var dialog = new JetsenWeb.UI.Window("edit-object-win");
    JetsenWeb.extend(dialog,{submitBox:true,maximizeBox: false, minimizeBox: false,cancelBox:true,windowStyle:1,size:{width:550,height:0},title:"编辑系统权限",showScroll:false});           
    dialog.controls = ["divFunction"];
    dialog.onclosed = function()
    {
   	    document.getElementById("divFunctionTree").style.display = "none";
   	    var popframeid = document.getElementById("divFunctionTree").popframeid;
   	    if(document.getElementById(popframeid)!=null&&document.getElementById(popframeid)!=""){
   	    document.getElementById(popframeid).style.display = "none";
   	    }
    	JetsenWeb.UI.Windows.close("edit-object-win");	
    }
    dialog.onsubmit = function()
    {             
        if($('hidParentId').value == Id)
        {
            jetsennet.alert("所属功能不为能自身,请重新选择所属功能！");
            return false;
        }
        if(JetsenWeb.Form.Validate(areaElements,true)){
        
            var objXml = "<Function><ID>"+Id+"</ID><PARENT_ID>"+$('hidParentId').value+"</PARENT_ID><NAME>"+JetsenWeb.Xml.xmlEscape($('txt_Name').value)+"</NAME>"
            objXml+= "<PARAM>"+JetsenWeb.Xml.xmlEscape($('txt_Param').value)+"</PARAM>";
            objXml+= "<DESCRIPTION>"+JetsenWeb.Xml.xmlEscape($('txt_Desc').value)+"</DESCRIPTION>";
            objXml+= "<STATE>"+attributeOf($('txtState'),"selectedValue","")+"</STATE>";
            objXml+= "<VIEW_POS>"+$('txt_ViewPos').value+"</VIEW_POS>";
            objXml+= "<TYPE>"+attributeOf($('txtType'),"selectedValue","")+"</TYPE></Function>";
            
		    var ws2 = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws2.soapheader = JetsenWeb.Application.authenticationHeader;
            ws2.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("edit-object-win");	                	                
                loadData();     	              
            }
            ws2.onerror = function(ex){ jetsennet.error(ex);};
            ws2.call("uumObjUpdate",["UUM_FUNCTION",objXml]);
	    }             
    };
    dialog.showDialog();
}

function getParentNameByParentId(Id){
	if(Id!=0){
		var condition = new JetsenWeb.SqlConditionCollection();
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",Id,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	    
	    var sqlQuery = new JetsenWeb.SqlQuery();    
	    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,ResultFields:"",               
	           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_FUNCTION"})});
	     
	    sqlQuery.Conditions = condition;
	    
	    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function(ret)
	    {
	        var obj = JetsenWeb.Xml.toObject(ret.resultVal).Record;
	        if(obj!=null)
	        {           
	         $('txt_Function').value = obj.NAME;
	        }            
	    }
	    ws.onerror = function(ex){ jetsennet.error(ex);};
	    ws.call("uumObjQuery",[sqlQuery.toXml()]);
	}
}

// 判断权限是否已经存在
function functionIsExists(id, editId)
{
	var existsCount = 0;
	
	var queryTable = JetsenWeb.createQueryTable("UUM_FUNCTION","");
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0, KeyId:"ID", PageInfo:null, QueryTable:queryTable, ResultFields:"COUNT(*) AS COUNT"});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    if (editId)
    {
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", editId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));
    }
    sqlQuery.Conditions = condition;        
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.getAuthenticationHeader();
    ws.async = false;
    ws.oncallback = function(ret)
    {
    	existsCount = parseInt(JetsenWeb.Xml.toObject(ret.resultVal,"Record")[0]["COUNT"]);
    };
    ws.onerror = function(ex){jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
    
    return existsCount > 0;
}