JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gAssetPage = new JetsenWeb.UI.PageBar("Asset");
gAssetPage.onpagechange = function () { searchAsset(); };
gAssetPage.orderBy = "";
gAssetPage.onupdate = function () {
    
};
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gAssetPage.setOrderBy(sortfield, desc);
};

var attribIds = [ 50052, 50053, 50054, 50055, 50056, 50057, 50058, 50059, 50060, 50061, 50062, 50063
                  , 50064, 50065, 50066, 50067, 50068, 50069, 50070, 50071 ];
var attribNames = [ "ASSET_ID", "TYPE", "SERIAL_NUMBER", "STYLE", "PURCHASE_DATE", "REPAIR_STARTDATE"
                    , "REPAIR_ENDDATE", "ITEM_NAME", "POSITION", "MACHINE_ID", "MANUFACTURER"
                    , "INTEGRATOR", "LINK_MAN", "CONTACT_NUMBER", "BELONG_DEPARTMENT", "PERSON_LIABLE"
                    , "STATE", "BELONG_SYSTEM", "MANAGE_IP", "SYSTEM_NAME" ];
var conditions = [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ];

//加载=====================================================================================
function loadAsset() {
    var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $("divAssetList").innerHTML = JetsenWeb.Xml.transformXML("xslt/simpleasset.xslt", ret.resultVal);
        gGridList.bind($("divAssetList"), $("tabAsset"));
        gAssetPage.setRowCount($("hid_AssetCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpGetAttribsAndValues", [attribIds.join(","), attribNames.join(","), conditions.join(","), conditions["OBJ_NAME"], $("hidTop").value]);
}
function searchAsset() {
	conditions = [ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ];
	if ($("txtAssetId").value != "") {
		for (var i = 0; i < attribNames.length; i++) {
			if (attribNames[i] === "ASSET_ID") {
				conditions[i] = $("txtAssetId").value;
				break;
			}
		}
	}
	conditions["OBJ_NAME"] = $("txtObjName").value;
    loadAsset();
}
//初始化===================================================================================
function pageInit() {
	
    searchAsset();
    $("hidTop").value = "-1";

    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType: 1,fixControlIndex: 0, splitBorder: 0, showSplit: true, enableResize: false, splitTitle : "divListTitle", splitSize : 27});

    var frameTop = new JetsenWeb.UI.PageItem("divTop");
    frameTop.size = { width: 0, height: 30 };

    gFrame.addControl(frameTop);
    gFrame.addControl(new JetsenWeb.UI.PageItem("divAssetList"));

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}

function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}