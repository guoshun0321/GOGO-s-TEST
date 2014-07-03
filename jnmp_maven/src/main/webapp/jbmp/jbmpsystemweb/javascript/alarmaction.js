JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe"]);
        var gFrame;
        var gWindowSizeChangedInterVal;
        var gAlarmActionPage = new JetsenWeb.UI.PageBar("AlarmAction");
        gAlarmActionPage.onpagechange = function () { loadAlarmAction(); };
        gAlarmActionPage.orderBy = "";
        gAlarmActionPage.onupdate = function () {
            $('divAlarmActionPage').innerHTML = this.generatePageControl();
        };
        var gAlarmActionCondition = new JetsenWeb.SqlConditionCollection();
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.ondatasort = function (sortfield, desc) {
            gAlarmActionPage.setOrderBy(sortfield, desc);
        };

        var gAlarmLevelPage = new JetsenWeb.UI.PageBar("AlarmLevel");
        gAlarmLevelPage.onpagechange = function () { loadAlarmLevel($("hid_ALARM_ID".value)); };
        gAlarmLevelPage.orderBy = "";
        gAlarmLevelPage.onupdate = function () {
            $("divAlarmLevelPage").innerHTML = this.generatePageControl();
        };
        var gAlarmLevelCondition = new JetsenWeb.SqlConditionCollection();
        var gAlarmLevelGridList = new JetsenWeb.UI.GridList();
        gAlarmLevelGridList.ondatasort = function (sortfield, desc) {
            gAlarmLevelPage.setOrderBy(sortfield, desc);
        };

        var gSqlQuery = new JetsenWeb.SqlQuery();
        var gQueryTable = JetsenWeb.createQueryTable("BMP_ACTION", "");
        //gQueryTable.addJoinTable(JetsenWeb.createJoinTable("TAB_NAME","t","t.KEY=s.KEY",JetsenWeb.TableJoinType.Inner));
        JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "ACTION_ID", PageInfo: gAlarmActionPage, QueryTable: gQueryTable });

        //加载=====================================================================================
        function loadAlarmAction() {
            gSqlQuery.OrderString = gAlarmActionPage.orderBy;
            gSqlQuery.Conditions = gAlarmActionCondition;

            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	var xmlDoc = new JetsenWeb.XmlDoc();
				xmlDoc.loadXML(ret.resultVal);
				var nodes = xmlDoc.documentElement.selectNodes("Record");
				var weekdays = [ "周一", "周二", "周三", "周四", "周五", "周六", "周日" ];
				var weekMasks = [ 64, 32, 16, 8, 4, 2, 1 ];
				for ( var i = 0; i < nodes.length; i++) {
					var weekMask = valueOf(nodes[i].selectSingleNode("WEEK_MASK"),
							"text", "");
					var hourMask = valueOf(nodes[i].selectSingleNode("HOUR_MASK"),
							"text", "");
					if (weekMask && hourMask && weekMask.indexOf("1") != -1 && hourMask.indexOf("1") != -1) {
						weekMask = parseInt(weekMask, 2);
						var weekTip = "每";
						var token = false;
						for ( var j = 0; j < 7; j++) {
							if ((weekMask & weekMasks[j]) > 0) {
								if (token) {
									weekTip += "、";
								}
								token = true;
								weekTip += weekdays[j];
							}
						}
						nodes[i].selectSingleNode("WEEK_MASK").text = weekTip;
								
						var sHour = hourMask.indexOf("1");
						if(hourMask.indexOf("0", sHour) != -1)
						{
							var eHour = hourMask.indexOf("0", sHour) - 1;
							if(hourMask.indexOf("1", hourMask.indexOf("0", sHour)) != -1)
							{
								var sHour1 = hourMask.indexOf("1", hourMask.indexOf("0", sHour));
								var eHour1 = hourMask.lastIndexOf("1");
								
								nodes[i].selectSingleNode("HOUR_MASK").text = sHour
								+ "点-" + eHour + "点" + "、" + sHour1 + "点-" + eHour1 + "点";
							}
							else
							{
								nodes[i].selectSingleNode("HOUR_MASK").text = sHour
								+ "点-" + eHour + "点";
							}
						}
						else
						{
							nodes[i].selectSingleNode("HOUR_MASK").text = sHour
								+ "点-" + 23 + "点";
						}

					}
					else
					{
						nodes[i].selectSingleNode("WEEK_MASK").text = "";
						nodes[i].selectSingleNode("HOUR_MASK").text = "";
					}
				}
            
                $('divAlarmActionList').innerHTML = JetsenWeb.Xml._transformXML("xslt/alarmaction.xslt", xmlDoc);
                gGridList.bind($('divAlarmActionList'), $('tabAlarmAction'));
                gAlarmActionPage.setRowCount($('hid_AlarmActionCount').value);
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
        }
        function searchAlarmAction() {
            gAlarmActionCondition.SqlConditions = [];
            if ($('txt_Key').value != "") {
                gAlarmActionCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("ACTION_NAME", $('txt_Key').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
            }
            gAlarmActionPage.currentPage = 1;
            loadAlarmAction();
        }
        //删除=====================================================================================
        function deleteAlarmAction(keyId) {
        	jetsennet.confirm("确定删除？", function () 
        	{
        		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                ws.soapheader = JetsenWeb.Application.authenticationHeader;
                ws.oncallback = function (ret) {
                    loadAlarmAction();
                };
                ws.onerror = function (ex) { jetsennet.error(ex); };
                ws.call("bmpObjDelete", ["BMP_ACTION", keyId]);
                
                return true;
            });
        }
        
        function getHourMask() {
			var hourMask = "";
			var startValue = $("txtSHour").value;
			var endValue = $("txtEHour").value;
			var startValue1 = $("txtSHour1").value;
			var endValue1 = $("txtEHour1").value;
			var startHour = startValue == "" ? 0 : Number(startValue);
			var endHour = endValue == "" ? 23 : Number(endValue);
			var startHour1 = startValue1 == "" ? 0 : Number(startValue1);
			var endHour1 = endValue1 == "" ? 23 : Number(endValue1);
			if (isNaN(startHour) || isNaN(endHour) || startHour < 0 || startHour > 23
					|| endHour < 0 || endHour > 23 || startValue.indexOf(".") != -1
					|| endValue.indexOf(".") != -1 || startHour > endHour) {
				jetsennet.alert("动作时间有误！");	
				return null;
			}
			if (isNaN(startHour1) || isNaN(endHour1) || startHour1 < 0 || startHour1 > 23
					|| endHour1 < 0 || endHour1 > 23 || startValue1.indexOf(".") != -1
					|| endValue1.indexOf(".") != -1 || startHour1 > endHour1) {
				jetsennet.alert("动作时间有误！");
				return null;
			}
			
			if(startValue == "" && startValue1 != "")
			{
				jetsennet.alert("动作时间请按顺序输入！");
				return null;
			}
			
			if((startValue1 != "" && endValue1 != "") && startHour1 <= endHour)
			{
				jetsennet.alert("动作时间不能重叠！");
				return null;
			}
			
			if(startValue1 != "" && (startHour1 - endHour) == 1)
			{
				jetsennet.alert("请合并动作时间！");
				return null;
			}
			
			if(startValue1 != "")
			{
				for ( var i = 0; i < 24; i++) {
					hourMask += ((i >= startHour && i <= endHour) || (i >= startHour1 && i <= endHour1)) ? "1" : "0";
				}
			}
			else
			{
				for ( var i = 0; i < 24; i++) {
					hourMask += (i >= startHour && i <= endHour) ? "1" : "0";
				}
			}
			
			return hourMask;
		}
        
        //新增=====================================================================================
        function newAlarmAction() {
            var areaElements = JetsenWeb.Form.getElements('divAlarmAction');
            JetsenWeb.Form.resetValue(areaElements);
            JetsenWeb.Form.clearValidateState(areaElements);
            assignTypeChange("2");
            var chkWeeks = document.getElementsByName("chkWeek");
            for (var i = 0; i < chkWeeks.length; i++) {
                chkWeeks[i].checked = false;
            }
            showRemindWordCount($("txt_ACTION_DESC").value,$('remindWord'),"30");
            showRemindWordCount($("txt_ASSIGN_PARAM").value,$('remindWord2'),"60");
            var dialog = new JetsenWeb.UI.Window("new-object-win");
            JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 550, height: 480 }, title: "新建报警动作" });
            dialog.controls = ["divAlarmAction"];
            dialog.onsubmit = function () {
                if (JetsenWeb.Form.Validate(areaElements, true)) {
                	if($("cbo_ASSIGN_OBJID").value == "")
                	{
                		jetsennet.alert("请选择指派对象！");
						return;
                	}
                
                	if(($("txtSHour").value == "" && $("txtEHour").value != "") || ($("txtSHour").value != "" && $("txtEHour").value == "") 
                	                          || ($("txtSHour1").value == "" && $("txtEHour1").value != "") || ($("txtSHour1").value != "" && $("txtEHour1").value == ""))
                	{
                		jetsennet.alert("动作时间有误！");
						return;
                	}
                            
                	var weekMask = 0;
					var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
					if (($("txtSHour").value != "" || $("txtSHour1").value != "") && weekdays.length == 0) {
						jetsennet.alert("请选择周几进行动作！");
						return;
					}
					if (($("txtSHour").value == "" && $("txtSHour1").value == "") && weekdays.length > 0) {
						jetsennet.alert("请选择动作时间段！");
						return;
					}
					if(parseInt(getBytesCount($("txt_ACTION_DESC").value))>60){
		            	jetsennet.alert("动作描述不能超过30个文字！");
		            	return;
		            }
					if(parseInt(getBytesCount($("txt_ASSIGN_PARAM").value))>120){
		            	jetsennet.alert("指派参数不能超过60个文字！");
		            	return;
		            }
					for ( var i = 0; i < weekdays.length; i++) {
						weekMask |= parseInt(weekdays[i]);
					}
					
					var hourMask = "";
					if(($("txtSHour").value == "" && $("txtSHour1").value == "") && weekdays.length == 0)
                	{
                		for ( var i = 0; i < 24; i++) {
							hourMask += "0";
						}
                	}
                	else
                	{
                		hourMask = getHourMask();
						if (!hourMask) {
							return;
						}
                	}
					
                    var objAlarmAction = {
                        ACTION_NAME: $("txt_ACTION_NAME").value
                      //, ACTION_TYPE: getSelectedValue($("cbo_ACTION_TYPE"))
                      , ACTION_PARAM: $("txt_ACTION_PARAM").value
                      , ACTION_TYPE: attributeOf($('cbo_ACTION_TYPE'),"selectedValue","")
                      , ACTION_DESC: $("txt_ACTION_DESC").value
                      , ASSIGN_TYPE: $("cbo_ASSIGN_TYPE").value
                      , ASSIGN_OBJID: $("cbo_ASSIGN_OBJID").value
                      , ASSIGN_PARAM: $("txt_ASSIGN_PARAM").value
                      , WEEK_MASK : JetsenWeb.Util.padLeft(weekMask.toString(2), 7, "0")
					  , HOUR_MASK : hourMask
                      , CREATE_USER: JetsenWeb.Application.userInfo.UserName
                    };
                    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                    ws.soapheader = JetsenWeb.Application.authenticationHeader;
                    ws.oncallback = function (ret) {
                        JetsenWeb.UI.Windows.close("new-object-win");
                        loadAlarmAction();
                    };
                    ws.onerror = function (ex) { jetsennet.error(ex); };
                    ws.call("bmpObjInsert", ["BMP_ACTION", JetsenWeb.Xml.serializer(objAlarmAction, "BMP_ACTION")]);
                }
            };
            dialog.showDialog();
            $("txt_ACTION_NAME").focus();
        }
        //编辑=====================================================================================
        function editAlarmAction(keyId) {
            var areaElements = JetsenWeb.Form.getElements('divAlarmAction');
            JetsenWeb.Form.resetValue(areaElements);
            JetsenWeb.Form.clearValidateState(areaElements);
            var condition = new JetsenWeb.SqlConditionCollection();
            condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ACTION_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

            var sqlQuery = new JetsenWeb.SqlQuery();
            JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "ACTION_ID", PageInfo: null, QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_ACTION" }) });
            sqlQuery.Conditions = condition;

            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                var objAlarmAction = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
                $("txt_ACTION_NAME").value = valueOf(objAlarmAction, "ACTION_NAME", "");
                //setSelectedValue($("cbo_ACTION_TYPE"), valueOf(objAlarmAction, "ACTION_TYPE", ""));
                JetsenWeb.UI.DropDownList['cbo_ACTION_TYPE'].setValue(objAlarmAction.ACTION_TYPE);
                $("txt_ACTION_PARAM").value = valueOf(objAlarmAction, "ACTION_PARAM", "");
                $("txt_ACTION_DESC").value = valueOf(objAlarmAction, "ACTION_DESC", "");
                setSelectedValue($("cbo_ASSIGN_TYPE"), valueOf(objAlarmAction, "ASSIGN_TYPE", ""));
                assignTypeChange($("cbo_ASSIGN_TYPE").value);
                setSelectedValue($("cbo_ASSIGN_OBJID"), valueOf(objAlarmAction, "ASSIGN_OBJID", ""));
                $("txt_ASSIGN_PARAM").value = valueOf(objAlarmAction, "ASSIGN_PARAM", "");
                showRemindWordCount($("txt_ACTION_DESC").value,$('remindWord'),"30");
                showRemindWordCount($("txt_ASSIGN_PARAM").value,$('remindWord2'),"60");
                var weekMask = valueOf(objAlarmAction, "WEEK_MASK", "");
                if (weekMask) {
					var chkWeeks = document.getElementsByName("chkWeek");
					for ( var i = 0; i < chkWeeks.length; i++) {
						chkWeeks[i].checked = (parseInt(weekMask, 2) & parseInt(chkWeeks[i].value)) > 0;
					}
				}
				else 
				{
					var chkWeeks = document.getElementsByName("chkWeek");
					for ( var i = 0; i < chkWeeks.length; i++) {
						chkWeeks[i].checked = false;
					}
				}
				
				var hourMask = valueOf(objAlarmAction, "HOUR_MASK", "");
				if (hourMask.indexOf("1") != -1) {
					$("txtSHour").value = hourMask.indexOf("1");
					if(hourMask.indexOf("0", $("txtSHour").value) != -1)
					{
						$("txtEHour").value = hourMask.indexOf("0", $("txtSHour").value) - 1;
						if(hourMask.indexOf("1", hourMask.indexOf("0", $("txtSHour").value)) != -1)
						{
							$("txtSHour1").value = hourMask.indexOf("1", hourMask.indexOf("0", $("txtSHour").value));
							$("txtEHour1").value = hourMask.lastIndexOf("1");
						}
						else
						{
							$("txtSHour1").value = "";
							$("txtEHour1").value = "";
						}
					}
					else
					{
						$("txtEHour").value = 23;
						$("txtSHour1").value = "";
						$("txtEHour1").value = "";
					}
				}
				else
				{
					$("txtSHour").value = "";
					$("txtEHour").value = "";
					$("txtSHour1").value = "";
					$("txtEHour1").value = "";
				}
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjQuery", [sqlQuery.toXml()]);

            var dialog = new JetsenWeb.UI.Window("edit-object-win");
            JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 550, height: 480 }, title: "编辑报警动作" });
            dialog.controls = ["divAlarmAction"];
            dialog.onsubmit = function () {
                if (JetsenWeb.Form.Validate(areaElements, true)) {
                	if($("cbo_ASSIGN_OBJID").value == "")
                	{
                		jetsennet.alert("请选择指派对象！");
						return;
                	}
                
                	if(($("txtSHour").value == "" && $("txtEHour").value != "") || ($("txtSHour").value != "" && $("txtEHour").value == "") 
                	                          || ($("txtSHour1").value == "" && $("txtEHour1").value != "") || ($("txtSHour1").value != "" && $("txtEHour1").value == ""))
                	{
                		jetsennet.alert("动作时间有误！");
						return;
                	}
                            
                	var weekMask = 0;
					var weekdays = JetsenWeb.Form.getCheckedValues("chkWeek");
					if (($("txtSHour").value != "" || $("txtSHour1").value != "") && weekdays.length == 0) {
						jetsennet.alert("请选择周几进行动作！");
						return;
					}
					if (($("txtSHour").value == "" && $("txtSHour1").value == "") && weekdays.length > 0) {
						jetsennet.alert("请选择动作时间段！");
						return;
					}
					if(parseInt(getBytesCount($("txt_ACTION_DESC").value))>60){
		            	jetsennet.alert("动作描述不能超过30个文字！");
		            	return;
		            }
					if(parseInt(getBytesCount($("txt_ASSIGN_PARAM").value))>120){
		            	jetsennet.alert("指派参数不能超过60个文字！");
		            	return;
		            }
					for ( var i = 0; i < weekdays.length; i++) {
						weekMask |= parseInt(weekdays[i]);
					}
					
					var hourMask = "";
					if(($("txtSHour").value == "" && $("txtSHour1").value == "") && weekdays.length == 0)
                	{
                		for ( var i = 0; i < 24; i++) {
							hourMask += "0";
						}
                	}
                	else
                	{
                		hourMask = getHourMask();
						if (!hourMask) {
							//jetsennet.alert("动作时间有误！");
							return;
						}
                	}
                
                    var oAlarmAction = {
                        ACTION_ID: keyId
					  , ACTION_NAME: $("txt_ACTION_NAME").value
                      //, ACTION_TYPE: getSelectedValue($("cbo_ACTION_TYPE"))
					  , ACTION_TYPE: attributeOf($('cbo_ACTION_TYPE'),"selectedValue","")
                      , ACTION_PARAM: $("txt_ACTION_PARAM").value
                      , ACTION_DESC: $("txt_ACTION_DESC").value
                      , ASSIGN_TYPE: $("cbo_ASSIGN_TYPE").value
                      , ASSIGN_OBJID: $("cbo_ASSIGN_OBJID").value
                      , ASSIGN_PARAM: $("txt_ASSIGN_PARAM").value
                      , WEEK_MASK : JetsenWeb.Util.padLeft(weekMask.toString(2), 7, "0")
					  , HOUR_MASK : hourMask
                    };

                    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                    ws.soapheader = JetsenWeb.Application.authenticationHeader;
                    ws.oncallback = function (ret) {
                        JetsenWeb.UI.Windows.close("edit-object-win");
                        loadAlarmAction();
                    };
                    ws.onerror = function (ex) { jetsennet.error(ex); };
                    ws.call("bmpObjUpdate", ["BMP_ACTION", JetsenWeb.Xml.serializer(oAlarmAction, "BMP_ACTION")]);
                }
            };
            dialog.showDialog();
        }
        //初始化===================================================================================
        function pageInit() {
            searchAlarmAction();
            if(parent.document.getElementById("spanWindowName")!=null){
            parent.document.getElementById("spanWindowName").innerHTML = document.title;
            }
            assignTypeChange($("cbo_ASSIGN_TYPE").value);

            gFrame = new JetsenWeb.UI.PageFrame("divPageFrame");
            gFrame.splitType = 1;
            gFrame.fixControlIndex = 0;
            gFrame.enableResize = false;
            gFrame.splitTitle = "divListTitle";
            gFrame.splitSize = 27;

            var frameTop = new JetsenWeb.UI.PageItem("divTop");
            frameTop.size = { width: 0, height: 30};
            var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType: 1, fixControlIndex: 1, showSplit: false });
            frameContent.addControl(new JetsenWeb.UI.PageItem("divAlarmActionList"));
            frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));

            gFrame.addControl(frameTop);
            gFrame.addControl(frameContent);

            window.onresize = function () {
                if (gWindowSizeChangedInterVal != null)
                    window.clearTimeout(gWindowSizeChangedInterVal);
                gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
            };
            windowResized();
            JetsenWeb.UI.DropDownList.initOptions('cbo_ACTION_TYPE');
        }

        function windowResized() {
            var size = JetsenWeb.Util.getWindowViewSize();
            gFrame.size = { width: size.width, height: size.height };
            gFrame.resize();
        }

        // 加载组列表
        function loadGroup() {
            var gSqlQuery = new JetsenWeb.SqlQuery();
            var gQueryTable = JetsenWeb.createQueryTable("UUM_USERGROUP", "");
            var condition = new JetsenWeb.SqlConditionCollection();
            condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Than, JetsenWeb.SqlParamType.Numeric));
            JetsenWeb.extend(gSqlQuery, { IsPageResult: 0, KeyId: "ID", QueryTable: gQueryTable, Conditions: condition, ResultFields: "ID,NAME" });
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.async = false;
            ws.oncallback = function (ret) {
            	 var cboObj = $("cbo_ASSIGN_OBJID");
                 cboObj.length = 0;
                 cboObj.options.add(new Option("请选择", ""));
            
                var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
                if (records) {
                    var length = records.length;
                    for (var i = 0; i < length; i++) {
                        cboObj.options.add(new Option(records[i]["NAME"], records[i]["ID"]));
                    }
                }
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
        }

        // 加载角色列表
        function loadRole() {
            var gSqlQuery = new JetsenWeb.SqlQuery();
            var gQueryTable = JetsenWeb.createQueryTable("UUM_ROLE", "");
            JetsenWeb.extend(gSqlQuery, { IsPageResult: 0, KeyId: "ID", QueryTable: gQueryTable, ResultFields: "ID,NAME" });
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.async = false;
            ws.oncallback = function (ret) {
            	 var cboObj = $("cbo_ASSIGN_OBJID");
                 cboObj.length = 0;
                 cboObj.options.add(new Option("请选择", ""));
            
                var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
                if (records) {
                    var length = records.length;
                    for (var i = 0; i < length; i++) {
                        cboObj.options.add(new Option(records[i]["NAME"], records[i]["ID"]));
                    }
                }
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
        }

        function assignTypeChange(type) {
            switch (type) {
                case "2":
                    loadGroup();
                    break;
                case "3":
                    loadRole();
                    break;
            }
        }
        

      //得到字符串字节数
      function getBytesCount(str) 
      { 
      	var bytesCount = 0; 
      	if (str != null) 
      	{ 
      		for (var i = 0; i < str.length; i++) 
      		{ 
      			var c = str.charAt(i); 
      			if (/^[\u0000-\u00ff]$/.test(c)) 
      			{ 
      				bytesCount += 1; 
      			} 
      			else 
      			{ 
      				bytesCount += 2; 
      			} 
      		} 
      	} 
      	return bytesCount; 
      }

      //textarea 作文字控制
      function showRemindWordCount(textValue,remindWordHtml,wordCount){
      	var countNum = 2*parseInt(wordCount);
      	remindWordHtml.innerHTML = parseInt((countNum-parseInt(getBytesCount(textValue)))/2);
      	if(countNum<parseInt(getBytesCount(textValue))){
      		remindWordHtml.style.color = "red";
      		remindWordHtml.innerHTML = 0;
      	}else{
      		remindWordHtml.style.color = "black";
      	}
      }