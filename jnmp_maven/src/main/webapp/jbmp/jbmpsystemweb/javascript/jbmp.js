jetsennet.registerNamespace("jbmp.util");

jbmp.util.isAdmin = function(userId) {
	var result = false;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		result = (ret.resultVal == "false" ? false : true);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("isAdmin", [ userId ]);
	return result;
}