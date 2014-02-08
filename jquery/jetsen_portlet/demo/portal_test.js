var portal = null;

window.onload = function() {
	var config = getConfig();
	var containDiv = $("#portlet");
	portal = new jetsennet.ui.portal(containDiv, config);
	portal.addPortlet(portal.rows[0], {x : 0, y : 'last'}, newPortlet(100, "title", "content"));
	portal.addPortlet(portal.rows[0], {x : 1, y : 'last'}, newPortlet(100, "title", "content"));
	portal.addPortlet(portal.rows[0], {x : 2, y : 'last'}, newPortlet(100, "title", "content"));
	portal.addPortlet(portal.rows[1], {x : 0, y : 'last'}, newPortlet(100, "title", "content"));
	portal.addPortlet(portal.rows[1], {x : 1, y : 'last'}, newPortlet(100, "title", "content"));

	portal.refreshPosition();
	
	// for(row in portal.rows) {
	// 	var indexs = portal.rows[row].portlet('index');	
	// 	console.log(indexs);
	// }

	console.log(JSON.stringify(portal.portalConfig));
	console.log(JSON.stringify(portal.portlets));
	
}

function getConfig() {
	var config = {};
	config.fill_type = $("#portal_fill_type").val();
	config.total_width = $("#protal_total_width").val();
	config.layout = eval("(" + $("#protal_layout").val() + ")");
	return config;
}

function newPortlet(height, title, content) {
	var portlet = {};
	portlet.title = title;
	portlet.content = {};
	portlet.content.type = 'text';
	portlet.content.text = function() {
		return content;
	}
	portlet.content.style = {};
	portlet.content.style.height = height;
	return portlet;
}