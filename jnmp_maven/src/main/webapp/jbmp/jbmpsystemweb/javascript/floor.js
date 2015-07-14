JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker", "js_jetsentree"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gFloorPage = new JetsenWeb.UI.PageBar("Floor");
gFloorPage.onpagechange = function () { loadFloor(); };
gFloorPage.orderBy = "order by FLOOR_ATTACH, FLOOR_NUM";
gFloorPage.onupdate = function () 
{
    $('divFloorPage').innerHTML = this.generatePageControl();
};
var gFloorCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) 
{
    gFloorPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("NMP_FLOOR", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "FLOOR_ID", PageInfo: gFloorPage, QueryTable: gQueryTable });

function pageInit() 
{
    parent.document.getElementById("spanWindowName").innerHTML = document.title;
    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitTitle: "divListTitle", splitSize: 27 });
    var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameContent.addControl(new JetsenWeb.UI.PageItem("divFloorList"));
    frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size: { width: 0, height:30} }));
    gFrame.addControl(frameContent);
    window.onresize = function () 
    {
       if (gWindowSizeChangedInterVal != null)
       {
           window.clearTimeout(gWindowSizeChangedInterVal);
       }
       gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
    searchFloor();			
}

function windowResized()
{
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

//得到对象
var curFloorAttach;
var curFloorNum;
function getFloor(id)
{  
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult: 0, KeyId: "FLOOR_ID", PageInfo: null, ResultFields: "", QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), {TableName: "NMP_FLOOR"})});
		        
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("FLOOR_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(FloorsResult)
    {
        var Floor = JetsenWeb.Xml.toObject(FloorsResult.resultVal, "Record")[0];
        curFloorAttach = valueOf(Floor, "FLOOR_ATTACH", "");
        curFloorNum = valueOf(Floor, "FLOOR_NUM", "");
        
        $("FLOOR_NAME").value = valueOf(Floor, "FLOOR_NAME", "");
        $("FLOOR_ALIAS").value = valueOf(Floor, "FLOOR_ALIAS", "");
        setFloorAttach(curFloorAttach);
        $("FLOOR_NUM").value = curFloorNum;
        $("FIELD_1").value = valueOf(Floor, "FIELD_1", "");
        $("FIELD_2").value = valueOf(Floor, "FIELD_2", "");
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("nmpObjQuery",[sqlQuery.toXml()]);
}
		
//加载
function loadFloor() 
{
    gSqlQuery.OrderString = gFloorPage.orderBy;
    gSqlQuery.Conditions = gFloorCondition;     
    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) 
    {
        $("divFloorList").innerHTML = JetsenWeb.Xml.transformXML("xslt/floor.xslt", ret.resultVal);
        gGridList.bind($("divFloorList"), $("tabUserFloor"));
        gFloorPage.setRowCount($("hid_FloorCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("nmpObjQuery", [gSqlQuery.toXml()]);
}

function searchFloor()
{
    gFloorCondition.SqlConditions = [];
    if($('FLOOR_NAME_SEARCH').value!="")
    {
        gFloorCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("FLOOR_NAME", $('FLOOR_NAME_SEARCH').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));	       
    }
    if($('FLOOR_ALIAS_SEARCH').value!="")
    {
        gFloorCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("FLOOR_ALIAS", $('FLOOR_ALIAS_SEARCH').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));	       
    }
    gFloorPage.currentPage = 1;
    loadFloor();
}
       
//新建
function newFloor()
{
    var areaElements = JetsenWeb.Form.getElements('divFloor');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements); 
    setFloorNum();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("add-Floor"), { title: "新建", submitBox: true, cancelBox: true, size: { width: 500, height: 250 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divFloor"];
    dialog.onsubmit = function ()
    {
       if (JetsenWeb.Form.Validate(areaElements, true)) 
       {
    	   var floorAttach = getFloorAttach();
    	   var floorNum = $("FLOOR_NUM").value;
    	   
//    	   if(floorIsExist(floorAttach, floorNum)){
//    		   JetsenWeb.alert("楼层已经存在！");
//    		   return;
//    	   }
    	   
		  var FloorXml = JetsenWeb.Xml.serialize({
		      FLOOR_NAME:$("FLOOR_NAME").value,
		      FLOOR_ALIAS:$("FLOOR_ALIAS").value,
		      FLOOR_ATTACH:floorAttach,
		      FLOOR_NUM:floorNum
		  },"NMP_FLOOR"); 
		  var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
		  ws.soapheader = JetsenWeb.Application.authenticationHeader;
		  ws.onerror = function(ex){ jetsennet.alert(ex);};
		  ws.oncallback = function(sResult)
		  {				
		      JetsenWeb.UI.Windows.close("add-Floor");	
		      loadFloor();
		  }
		  ws.call("nmpObjInsert",["NMP_FLOOR",FloorXml]);     
        }
    };
    dialog.showDialog();
}

//编辑
function editFloor(id)
{
    var areaElements = JetsenWeb.Form.getElements('divFloor');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    setFloorNum();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("add-Floor"), { title: "编辑", submitBox: true, cancelBox: true, size: { width: 500, height: 250 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divFloor"];
    getFloor(id);
    dialog.onsubmit = function () 
    {
       if (JetsenWeb.Form.Validate(areaElements, true)) 
       {
    	   var floorAttach = getFloorAttach();
    	   var floorNum = $("FLOOR_NUM").value;
    	   
//    	   if(!(curFloorAttach == floorAttach && curFloorNum == floorNum) && floorIsExist(floorAttach, floorNum)){
//    		   JetsenWeb.alert("楼层已经存在！");
//    		   return;
//    	   }
    	   
           var FloorXml = JetsenWeb.Xml.serialize({
                FLOOR_ID:id,
                FLOOR_NAME:$("FLOOR_NAME").value,
                FLOOR_ALIAS:$("FLOOR_ALIAS").value,
                FLOOR_ATTACH:floorAttach,
  		      	FLOOR_NUM:floorNum
           },"NMP_FLOOR");
           var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
           ws.soapheader = JetsenWeb.Application.authenticationHeader;
           ws.onerror = function(ex){ jetsennet.alert(ex);};
           ws.oncallback = function(sResult)
           {				
               JetsenWeb.UI.Windows.close("add-Floor");	
               loadFloor();
           }
           ws.call("nmpObjUpdate",["NMP_FLOOR",FloorXml]);   
       }
    };
    dialog.showDialog();
}

//删除
function deleteFloor(id)
{
    jetsennet.confirm("确定删除？", function () 
    {
        var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.oncallback = function(sResult)
        {	        
           loadFloor();
        }	
        ws.call("nmpObjDelete",["NMP_FLOOR",id]);     
        return true;
    });
}

function setFloorNum(){
	var floorAttach = getFloorAttach();
	var options = document.getElementById('FLOOR_NUM').options;
	if(floorAttach == 0){
		if(options.length != 32){
			options.length = 0;
			options.add(new Option("-1", -1));
			for(var i = 1; i <= 31; i++)
				options.add(new Option("" + i, i));
		}
	}else{
		if(options.length != 4){
			options.length = 0;
			for(var i = 1; i <= 4; i++)
				options.add(new Option("" + i, i));
		}
	}
}

function getFloorAttach(){
	var radio = document.getElementsByName('FLOOR_ATTACH');
	if(radio == null)
		return 0;
	else{
		for(var i = 0; i < radio.length; i++){
			if(radio[i].checked)
				return radio[i].value;
		}
		return 0;
	}
}

function setFloorAttach(floorNum){
	var radio = document.getElementsByName('FLOOR_ATTACH');
	if(radio != null){
		for(var i = 0; i < radio.length; i++){
			if(radio[i].value == floorNum)
				radio[i].checked = true;
			else
				radio[i].checked = false;
		}
	}
}

function floorIsExist(floorAttach, floorNum){
	var flag = false;
	
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("FLOOR_ATTACH", floorAttach, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("FLOOR_NUM", floorNum, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	
	var sqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("NMP_FLOOR", "");
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "FLOOR_ID", PageInfo: null, ResultFields: "FLOOR_NAME", QueryTable: gQueryTable });
    sqlQuery.Conditions = conditionCollection;

    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.async = false;
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var result = JetsenWeb.xml.toObject(ret.resultVal,"Record");
    	if(result == null){
    		flag = false;
    	}else{
    		flag = true;
    	}
    }
    ws.call("nmpObjQuery", [sqlQuery.toXml()]);
    
    return flag;
}
