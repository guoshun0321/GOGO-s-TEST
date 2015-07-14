jetsennet.registerNamespace("jnmp_js.index");

var BMP_SYSTEM_SERVICE = JetsenWeb.appPath + "../../services/BMPSystemService?wsdl";
var BMP_RESOURCE_SERVICE = JetsenWeb.appPath + "../../services/BMPResourceService?wsdl";
var NMP_PERMISSIONS_SERVICE = JetsenWeb.appPath + "../../services/NMPPermissionsService?wsdl";

var curAlarmDlg = null;

jnmp_js.index.alarmWin = function(control, alarmLight, name, userId) {
	var that = this;
	this.name = name;
	curAlarmDlg = this.name;
	this.control = control;
	this.alarmLight = $(alarmLight);
	this.chkAuto = $("chkAlarmEventAuto");
	this.lightPosition = jetsennet.util.getPosition(this.alarmLight, 0);

	// 窗口大小、位置
	this.width = 602;
	this.height = 200;
	this.top = this.lightPosition.top - this.height;
	this.left = this.lightPosition.left - this.width;
	this.windowState = 1; // 0，显示状态；1，隐藏状态

	this.timerHandle = -1;
	this.checkSpan = 2000; // 轮询间隔时间
	this.maxAlarmId = -1; // 当前最大报警事件ID，初始为0
	this.lastClose = -1; // 最后一次关闭的时间
	this.isAuto = true;

	// 用户信息
	this.userId = userId;
	this.isAdmin = false;
	this.objIds = "";

	// 窗口信息
	this.gridList = new JetsenWeb.UI.GridList("index-alarm-grid");
	this.gridList.columns = [ { index : 0, fieldName : "COLL_TIME", width : 100, name : "报警时间" },
			{ index : 1, fieldName : "OBJ_NAME", width : 100, name : "对象名称" }, { index : 2, fieldName : "OBJATTR_NAME", width : 100, name : "指标名称" },
			{ index : 3, fieldName : "EVENT_DESC", width : 195, name : "报警描述" },
			{ index : 4, fieldName : "ALARMEVT_ID", width : 80, name : "报警处理", align : "center" } ];
	this.gridList.columns[4].format = function(val, vals) {
		var id = vals[0];
		val = "<a href='javascript:void(0)' onclick=\"" + that.name + ".handleAlarmDlg(" + id
				+ ")\"><img src='images/window.gif' border='0' style='cursor:pointer' title='处理'/></a>"
		return val;
	}
	this.gridList.idField = "ALARMEVT_ID";

	// 数据信息
	this.alarmDate = null; // 报警事件信息
	this.lastMaxAlarmId = -1; // 当前最大报警事件ID

	this.initUserInfo();
}

jnmp_js.index.alarmWin.prototype.initUserInfo = function() {
	var that = this;

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		that.isAdmin = (ret.resultVal == "false" ? false : true);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("isAdmin", [ this.userId ]);

	if (!this.isAdmin) {
		ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			that.objIds = ret.resultVal;
		}
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpGetUserGroup", [ this.userId ]);
	}
}

jnmp_js.index.alarmWin.prototype.changeAuto = function() {
	if (this.isAuto) {
		this.isAuto = false;
	} else {
		this.isAuto = true;
	}
}

jnmp_js.index.alarmWin.prototype.close = function() {
	var now = new Date();
	this.lastClose = now.getTime();
	this.windowState = 1;
}

// 首次自动触发
jnmp_js.index.alarmWin.prototype.start = function() {
	this.timerHandle = setTimeout(this.name + ".checkAlarm()", this.checkSpan);
}

// 更新数据
jnmp_js.index.alarmWin.prototype.updateDate = function() {
	var that = this;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		if (ret.resultVal) {
			// 报警数据
			that.alarmDate = ret.resultVal;
			var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record1");
			if (retObj != null && retObj[0] != null) {
				// 最大报警ID
				var maxAlarm = retObj[0]["MAX_ALARM"];
				if (maxAlarm >= that.lastMaxAlarmId) {
					that.lastMaxAlarmId = maxAlarm;
				}
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpIndexLastAlarm", [ 30, this.isAdmin, this.objIds ]);
}

// 更新列表
jnmp_js.index.alarmWin.prototype.updateList = function() {
	if (this.alarmDate != null && this.alarmDate != "") {
		$("divAlarmEventLst").innerHTML = "";
		this.gridList.dataSource = this.alarmDate;
		this.gridList.render("divAlarmEventLst");
		this.gridList.colorSelectedRows();
	}
}

// 显示窗口
jnmp_js.index.alarmWin.prototype.showPage = function() {
	var that = this;
	var winName = "index-alarm-win";
	var dialog = new JetsenWeb.UI.Window(winName);
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : false, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : this.width, height : this.height }, title : "报警事件" });
	dialog.position = { left : this.left, top : this.top }
	dialog.controls = [ this.control ];
	dialog.onclosed = function() {
		that.close();
	}
	dialog.show();
	this.windowState = 0;
}

//自动触发
jnmp_js.index.alarmWin.prototype.checkAlarm = function() {
	this.updateDate();
	if (this.lastMaxAlarmId > this.maxAlarmId && this.isAuto) {
		this.maxAlarmId = this.lastMaxAlarmId;
		if (this.windowState == 1) {
			this.showPage();
		}
		this.updateList();
	}
	this.timerHandle = setTimeout(this.name + ".checkAlarm()", this.checkSpan);
}

// 手动触发
jnmp_js.index.alarmWin.prototype.manualShow = function() {
	this.updateDate();
	this.maxAlarmId = this.lastMaxAlarmId;
	if (this.windowState == 1) {
		this.showPage();
	}
	this.updateList();
}

// 报警处理
jnmp_js.index.alarmWin.prototype.handleAlarmDlg = function(alarmId) {
	var that = this;
	$("areaEventHandle").value = "";
	var winName = "handle-alarm-win";
	var dialog = new JetsenWeb.UI.Window(winName);
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 402, height : 190 }, title : "报警事件处理" });
	dialog.controls = [ "divAlarmEventHandle" ];
	dialog.onsubmit = function() {
		handleAlarm(alarmId);
	}
	dialog.onclosed = function() {
		that.close();
	}
	dialog.showDialog();
	changeHandleType(0);
}

function handleAlarm(alarmId) {
	var oAlarmEvent = {};
	oAlarmEvent["CHECK_USERID"] = JetsenWeb.Application.userInfo.UserId;
	oAlarmEvent["CHECK_USER"] = JetsenWeb.Application.userInfo.UserName;

	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		alarmWin.updateList();
		JetsenWeb.UI.Windows.close("handle-alarm-win");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};

	if ($("radioEventHandle0").checked) {
		oAlarmEvent["EVENT_STATE"] = 1;
		ws.call("bmpConfirmAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmId ]);
	} else if ($("radioEventHandle1").checked) {
		oAlarmEvent["EVENT_STATE"] = 2;
		ws.call("bmpRemoveAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmId ]);
	} else {
		oAlarmEvent["EVENT_STATE"] = 3;
		oAlarmEvent["CHECK_DESC"] = $("areaEventHandle").value;
		if (oAlarmEvent["CHECK_DESC"].length == 0) {
			jetsennet.alert("请输入处理意见！");
			return;
		}
		ws.call("bmpCheckAlarmEvent", [ "BMP_ALARMEVENT", JetsenWeb.Xml.serializer(oAlarmEvent, "BMP_ALARMEVENT"), alarmId ]);
	}
}

function changeHandleType(type) {
	$("radioEventHandle0").checked = false;
	$("radioEventHandle1").checked = false;
	$("radioEventHandle2").checked = false;
	$("radioEventHandle" + type).checked = true;
	if (type == 2) {
		var text = $("areaEventHandle").value;
		if (text == " ") {
			$("areaEventHandle").value = "";
		}
		$("areaEventHandle").disabled = false;
		$("areaEventHandle").readonly = false;
	} else {
		var text = $("areaEventHandle").value;
		if (text == "") {
			$("areaEventHandle").value = " ";
		}
		$("areaEventHandle").disabled = true;
		$("areaEventHandle").readonly = true;
	}
}