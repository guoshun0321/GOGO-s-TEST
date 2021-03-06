JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_jetsentree"));
JetsenWeb.registerNamespace("JetsenWeb.UI");
JetsenWeb.importCss("jetsenui");
JetsenWeb.UI.Trees = {};
JetsenWeb.UI.Tree = function(treeName) {
	this.__typeName = "JetsenWeb.UI.Tree";
	this.treeName = treeName ? treeName : "JetsenTree";
	this.treeItems = [];
	this.selectedIndex = null;
	this.onselect = null;
	this.onunselect = null;
	this.onclick = null;
	this.oncheckchanged = null;
	this.oncontextmenu = null;
	JetsenWeb.UI.Trees[this.treeName] = this
};
JetsenWeb.UI.Tree.prototype.click = function(treeItem) {
	if (JetsenWeb.isFunction(this.onclick))
		this.onclick(treeItem)
};
JetsenWeb.UI.Tree.prototype.checkChanged = function(treeItem) {
	if (JetsenWeb.isFunction(this.oncheckchanged))
		this.oncheckchanged(treeItem)
};
JetsenWeb.UI.Tree.prototype.contextmenu = function(treeItem) {
	if (JetsenWeb.isFunction(this.oncontextmenu))
		this.oncontextmenu(treeItem)
};
JetsenWeb.UI.Tree.prototype.addItem = function(item) {
	item.parent = this;
	this.treeItems.push(item)
};
JetsenWeb.UI.Tree.prototype.hide = function() {
	for ( var i = 0; i < this.treeItems.length; i++) {
		this.treeItems[i].hideSubTree()
	}
};
JetsenWeb.UI.Tree.prototype.dispose = function() {
	JetsenWeb.UI.Trees[this.treeName] = null
};
JetsenWeb.UI.Tree.prototype.getTreeAllItems = function() {
	var arrItems = [];
	var curTree = JetsenWeb.UI.Trees[this.treeName];
	if (curTree) {
		for ( var p in curTree) {
			if (!(IS_SAFARI && p == "event") && curTree[p] != null)
				arrItems.push(curTree[p])
		}
	}
	return arrItems
};
JetsenWeb.UI.Tree.prototype.getItem = function(fun, bDeep) {
	for ( var i = 0; i < this.treeItems.length; i++) {
		if (this.treeItems[i].__typeName == "JetsenWeb.UI.TreeItem") {
			if (fun(this.treeItems[i]))
				return this.treeItems[i];
			else if (bDeep) {
				var item = this.treeItems[i].getItem(fun, bDeep);
				if (item)
					return item
			}
		}
	}
	return null
};
JetsenWeb.UI.Tree.prototype.getItemByIndex = function(index) {
	var item = JetsenWeb.UI.Trees[this.treeName].trees[index];
	if (item != null) {
		return item
	}
};
JetsenWeb.UI.Tree.prototype.select = function(index) {
	if (JetsenWeb.UI.Trees[this.treeName].selectedIndex == index)
		return;
	var item = JetsenWeb.UI.Trees[this.treeName].trees[index];
	if (item != null) {
		item.select()
	}
};
JetsenWeb.UI.Tree.prototype.unselect = function(index) {
	var item = JetsenWeb.UI.Trees[this.treeName].trees[index];
	if (item != null) {
		item.unselect()
	}
};
JetsenWeb.UI.Tree.prototype.treeSelected = function(index) {
	if (this.onselect && typeof this.onselect == "function")
		this.onselect(index)
};
JetsenWeb.UI.Tree.prototype.treeUnSelected = function(index) {
	if (this.onunselect && typeof this.onunselect == "function")
		this.onunselect(index)
};
JetsenWeb.UI.Tree.prototype.render = function() {
	JetsenWeb.UI.Trees[this.treeName].trees = {};
	var itemLen = this.treeItems.length;
	if (itemLen > 0) {
		var treeControl = document.createElement("DIV");
		treeControl.className = "jetsen-tree";
		for ( var i = 0; i < itemLen; i++) {
			this.treeItems[i].parent = this;
			if (this.treeItems[i].__typeName == "JetsenWeb.UI.TreeItem") {
				var subControl = this.treeItems[i].render(this.treeName, i);
				subControl.style.paddingLeft = "5px";
				treeControl.appendChild(subControl)
			}
		}
		document.body.appendChild(treeControl);
		return treeControl
	}
	return null
};
JetsenWeb.UI.Tree.createTree = function(treeName, treeXml, options) {
	var xmlDoc = new JetsenWeb.XmlDoc();
	xmlDoc.loadXML(treeXml);
	var tree = new JetsenWeb.UI.Tree(treeName);
	var treeOptions = JetsenWeb.extend( {
		parentId : "0",
		parentField : "ParentId",
		itemName : "Record",
		textField : "Name",
		valueField : "Id",
		showCheck : false
	}, options);
	var paramFields = treeOptions.paramFields ? treeOptions.paramFields
			.split(",") : [];
	var nodes = xmlDoc.documentElement.selectNodes(treeOptions.itemName + "["
			+ treeOptions.parentField + "='" + treeOptions.parentId + "']");
	for ( var i = 0; i < nodes.length; i++) {
		var valId = valueOf(nodes[i].selectSingleNode(treeOptions.valueField),
				"text", "");
		if (valId != treeOptions.parentId) {
			var treeParam = {};
			for ( var j = 0; j < paramFields.length; j++) {
				treeParam[paramFields[j]] = valueOf(nodes[i]
						.selectSingleNode(paramFields[j]), "text", "")
			}
			var subTree = new JetsenWeb.UI.TreeItem(valueOf(nodes[i]
					.selectSingleNode(treeOptions.textField), "text", ""),
					null, null, null, treeParam);
			if (treeOptions.showCheck) {
				subTree.showCheck = true;
			}
			subTree.checkValue = valId;
			JetsenWeb.UI.Tree.createSubTree(xmlDoc, subTree,
					treeOptions.itemName, valId, treeOptions.parentField,
					treeOptions.textField, treeOptions.valueField, paramFields,
					treeOptions.showCheck);
			tree.addItem(subTree)
		}
	}
	return tree
};
JetsenWeb.UI.Tree.createSubTree = function(xmlDoc, treeNode, itemName,
		parentId, parentField, textField, valueField, paramFields, showCheck) {
	var nodes = xmlDoc.documentElement.selectNodes(itemName + "[" + parentField
			+ "='" + parentId + "']");
	for ( var i = 0; i < nodes.length; i++) {
		var valId = valueOf(nodes[i].selectSingleNode(valueField), "text", "");
		if (valId != parentId) {
			var treeParam = {};
			for ( var j = 0; j < paramFields.length; j++) {
				treeParam[paramFields[j]] = valueOf(nodes[i]
						.selectSingleNode(paramFields[j]), "text", "")
			}
			var treeItem = new JetsenWeb.UI.TreeItem(valueOf(nodes[i]
					.selectSingleNode(textField), "text", ""), null, null,
					null, treeParam);
			if (showCheck) {
				treeItem.showCheck = true;
			}
			treeItem.checkValue = valId;
			JetsenWeb.UI.Tree.createSubTree(xmlDoc, treeItem, itemName, valId,
					parentField, textField, valueField, paramFields, showCheck);
			treeNode.addItem(treeItem)
		}
	}
};
JetsenWeb.UI.TreeItem = function(treeText, action, description, target,
		treeParam) {
	this.__typeName = "JetsenWeb.UI.TreeItem";
	this.treeText = treeText;
	this.action = action;
	this.target = target;
	this.description = description;
	this.treeItems = [];
	this.treeIndex = "";
	this.itemIndex = "";
	this.control = null;
	this.treeParam = treeParam;
	this.isOpen = false;
	this.isSelected = false;
	this.linkControl = null;
	this.treeName = "";
	this.isRenderItem = false;
	this.allwaysRenderItem = false;
	this.showCheck = false;
	this.checked = false;
	this.cascadeCheck = true;
	this.checkValue = "";
	this.onclick = null;
	this.oncheckchanged = null;
	this.onopen = null;
	this.oncontextmenu = null;
	this.openIcon = JetsenWeb.baseThemeUrl
			+ "images/treeview/treeview_icon_folder_opened.gif";
	this.closeIcon = JetsenWeb.baseThemeUrl
			+ "images/treeview/treeview_icon_folder_closed.gif";
	this.fileIcon = JetsenWeb.baseThemeUrl
			+ "images/treeview/treeview_icon_file.gif";
	this.clickChange = false;
};
JetsenWeb.UI.TreeItem.prototype.addItem = function(item) {
	item.parent = this;
	this.treeItems.push(item)
};
JetsenWeb.UI.TreeItem.prototype.click = function() {
	this.select();
	if (JetsenWeb.isFunction(this.onclick))
		this.onclick();
	JetsenWeb.UI.Trees[this.treeName].click(this)
};
JetsenWeb.UI.TreeItem.prototype.checkChanged = function() {
	if (JetsenWeb.isFunction(this.oncheckchanged))
		this.oncheckchanged();
	var treeName = this.treeName;
	var parentItem = this.parent;
	while (!treeName && parentItem) {
		treeName = parentItem.treeName;
		parentItem = parentItem.parent
	}
	JetsenWeb.UI.Trees[treeName].checkChanged(this)
};
JetsenWeb.UI.TreeItem.prototype.contextmenu = function() {
	if (JetsenWeb.isFunction(this.oncontextmenu))
		this.oncontextmenu();
	JetsenWeb.UI.Trees[this.treeName].contextmenu(this)
};
JetsenWeb.UI.TreeItem.prototype.render = function(treeName, index) {
	var uiOwner = this;
	this.treeName = treeName ? treeName : this.treeName;
	var treeControl = document.createElement("div");
	treeControl.className = "jetsen-tree";
	treeControl.title = this.description ? this.description : this.treeText;
	treeControl.id = treeName + "-" + index;
	this.treeIndex = treeControl.id;
	this.itemIndex = index;
	JetsenWeb.UI.Trees[treeName].trees[treeName + "-" + index] = this;
	var treeIcon = document.createElement("img");
	treeIcon.id = this.treeIndex + "-img";
	treeIcon.style.cursor = "pointer";
	treeIcon.src = this.treeItems.length > 0 ? (this.isOpen ? this.openIcon
			: this.closeIcon) : this.fileIcon;
	treeControl.appendChild(treeIcon);
	treeIcon.onclick = function() {
		uiOwner.openOrClose();
		JetsenWeb.cancelEvent()
	};
	if (this.showCheck) {
		var treeCheck = document.createElement("input");
		treeCheck.name = "chk_" + treeName;
		treeCheck.id = this.treeIndex + "-chk";
		treeCheck.type = "checkbox";
		treeCheck.value = this.checkValue;
		if (this.checked) {
			treeCheck.defaultChecked = true;
			treeCheck.checked = true
		}
		treeControl.appendChild(treeCheck);
		treeCheck.onclick = function() {
			uiOwner.checked = this.checked == true;
			if (uiOwner.cascadeCheck) {
				if(uiOwner.clickChange){
					uiOwner.setParentCheck();
					if(!uiOwner.checked){
						uiOwner.setSubCheck(uiOwner.checked)
					}
				}else{
					if (uiOwner.checked) {
						uiOwner.checkParentCheck()
					} else {
						uiOwner.cancelParentCheck()
					}
					uiOwner.setSubCheck(uiOwner.checked)
				}
			}
			uiOwner.checkChanged();
			JetsenWeb.cancelEvent(true)
		}
	}
	var treeLink = document.createElement("A");
	treeLink.id = this.treeIndex + "-a";
	treeLink.innerHTML = this.treeText;
	if (this.target != null && this.target != "")
		treeLink.target = this.target;
	if (this.action == null || this.action == "") {
		treeLink.href = "javascript:void(0);"
	} else if (JetsenWeb.Util.left(this.action, 10).equal("javascript")) {
		treeLink.href = this.action
	} else {
		treeLink.href = this.action
	}
	treeLink.oncontextmenu = function() {
		uiOwner.contextmenu()
	};
	treeLink.onclick = function() {
		uiOwner.click()
	};
	treeLink.ondblclick = function() {
		uiOwner.openOrClose();
		uiOwner.select()
	};
	treeLink.onfocus = function() {
	};
	treeLink.onmouseup = function() {
		if (JetsenWeb.isFunction(uiOwner.onmouseup))
			uiOwner.onmouseup()
	};
	treeControl.appendChild(treeLink);
	this.control = treeControl;
	this.linkControl = treeLink;
	if (this.treeItems.length > 0) {
		treeControl.className = this.isOpen ? "open" : "close";
		if (this.isOpen || this.allwaysRenderItem)
			this.renderItem()
	} else {
		treeControl.className = "file"
	}
	treeLink.onmouseover = function() {
		if (!uiOwner.isSelected) {
			this.style.textDecoration = "underline"
		}
	};
	treeLink.onmouseout = function() {
		if (!uiOwner.isSelected) {
			this.style.textDecoration = "none"
		}
	};
	return treeControl
};
JetsenWeb.UI.TreeItem.prototype.renderItem = function() {
	if (this.isRenderItem)
		return;
	this.isRenderItem = true;
	var itemLength = this.showCheck ? 3 : 2;
	while (this.control.childNodes.length > itemLength) {
		var oldLength = this.control.childNodes.length;
		for ( var i = 0; i < oldLength; i++) {
			var item = this.control.childNodes[i];
			if (item.id != this.treeIndex + "-a"
					&& item.id != this.treeIndex + "-img"
					&& item.id != this.treeIndex + "-chk") {
				this.control.removeChild(item);
				break
			}
		}
	}
	var newLength = this.treeItems.length;
	if (newLength == 0) {
		var subImgControl = $(this.treeIndex + "-img");
		if (subImgControl != null)
			subImgControl.src = this.fileIcon;
		return
	}
	for ( var i = 0; i < newLength; i++) {
		var treeItem = this.treeItems[i];
		if (treeItem.__typeName == "JetsenWeb.UI.TreeItem") {
			if (treeItem.treeText == null || treeItem.treeText == "")
				continue;
			treeItem.parent = this;
			treeItem.isRenderItem = false;
			this.control.appendChild(treeItem.render(this.treeName,
					this.itemIndex + "-" + i))
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.clear = function() {
	var i = 0;
	var curTree = JetsenWeb.UI.Trees[this.treeName];
	var treeItem = curTree.trees[this.treeIndex + "-" + i];
	while (treeItem) {
		treeItem = null;
		i++;
		treeItem = curTree.trees[this.treeIndex + "-" + i]
	}
	var itemLength = this.showCheck ? 3 : 2;
	while (this.control.childNodes.length > itemLength) {
		var oldLength = this.control.childNodes.length;
		for ( var i = 0; i < oldLength; i++) {
			var item = this.control.childNodes[i];
			if (item.id != this.treeIndex + "-a"
					&& item.id != this.treeIndex + "-img"
					&& item.id != this.treeIndex + "-chk") {
				this.control.removeChild(item);
				break
			}
		}
	}
	this.treeItems = []
};
JetsenWeb.UI.TreeItem.prototype.getItem = function(fun, bDeep) {
	for ( var i = 0; i < this.treeItems.length; i++) {
		if (this.treeItems[i].__typeName == "JetsenWeb.UI.TreeItem") {
			if (fun(this.treeItems[i]))
				return this.treeItems[i];
			else if (bDeep) {
				var item = this.treeItems[i].getItem(fun, bDeep);
				if (item != null)
					return item
			}
		}
	}
	return null
};
JetsenWeb.UI.TreeItem.prototype.setCheck = function(isCheck) {
	if (!this.showCheck)
		return;
	var isChecked = isCheck ? true : false;
	var oldChecked = this.checked == true;
	this.checked = isChecked;
	var checkControl = $(this.treeIndex + "-chk");
	if (checkControl) {
		checkControl.checked = isChecked
	}
	if (this.cascadeCheck && !this.clickChange) {
		if (!isChecked) {
			this.cancelParentCheck()
		} else {
			this.checkParentCheck()
		}
		this.setSubCheck(isChecked)
	}
	if (oldChecked != isChecked) {
		this.checkChanged()
	}
};
JetsenWeb.UI.TreeItem.prototype.setSubCheck = function(isCheck) {
	for ( var i = 0; i < this.treeItems.length; i++) {
		var treeItem = this.treeItems[i];
		if (treeItem.__typeName == "JetsenWeb.UI.TreeItem") {
			if (treeItem.showCheck) {
				var oldChecked = treeItem.checked == true;
				treeItem.checked = isCheck;
				var checkCon = $(treeItem.treeIndex + "-chk");
				if (checkCon)
					checkCon.checked = isCheck;
				if (oldChecked != isCheck) {
					treeItem.checkChanged()
				}
			}
			if (treeItem.cascadeCheck)
				treeItem.setSubCheck(isCheck)
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.setParentCheck = function() {
	if (this.parent && this.parent.showCheck && this.parent.cascadeCheck) {
		var isChange = this.parent.checked == false;
		this.parent.checked = true;
		var checkCon = $(this.parent.treeIndex + "-chk");
		if (checkCon)
			checkCon.checked = true;
		this.parent.setParentCheck();
		if (isChange != false) {
			this.parent.checkChanged()
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.cancelParentCheck = function() {
	if (this.parent && this.parent.showCheck && this.parent.cascadeCheck) {
		var oldChecked = this.parent.checked == true;
		this.parent.checked = false;
		var checkCon = $(this.parent.treeIndex + "-chk");
		if (checkCon)
			checkCon.checked = false;
		this.parent.cancelParentCheck();
		if (oldChecked != false) {
			this.parent.checkChanged()
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.checkParentCheck = function() {
	if (this.parent && this.parent.showCheck && this.parent.cascadeCheck) {
		var isCheckAll = true;
		for ( var i = 0; i < this.parent.treeItems.length; i++) {
			var treeItem = this.parent.treeItems[i];
			if (treeItem.__typeName == "JetsenWeb.UI.TreeItem") {
				if (treeItem.showCheck) {
					if (treeItem.checked != true) {
						isCheckAll = false;
						break
					}
				}
			}
		}
		if (isCheckAll) {
			var oldChecked = this.parent.checked == true;
			this.parent.checked = true;
			var checkCon = $(this.parent.treeIndex + "-chk");
			if (checkCon)
				checkCon.checked = true;
			this.parent.checkParentCheck();
			if (oldChecked != true) {
				this.parent.checkChanged()
			}
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.checkRender = function() {
	if (this.parent) {
		if (!this.parent.isRenderItem) {
			if (this.parent.__typeName == "JetsenWeb.UI.TreeItem") {
				this.parent.checkRender();
				this.parent.renderItem()
			}
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.checkDisplay = function() {
	if (this.parent) {
		if (this.parent.__typeName == "JetsenWeb.UI.TreeItem") {
			this.parent.checkDisplay();
			if (!this.parent.isOpen) {
				this.parent.showSubTree()
			}
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.select = function() {
	this.checkRender();
	this.checkDisplay();
	if (JetsenWeb.UI.Trees[this.treeName].selectedIndex == this.treeIndex)
		return;
	if (JetsenWeb.UI.Trees[this.treeName].selectedIndex != null) {
		var oldSelected = JetsenWeb.UI.Trees[this.treeName].trees[JetsenWeb.UI.Trees[this.treeName].selectedIndex];
		if (oldSelected != null) {
			oldSelected.unselect()
		}
	}
	this.linkControl.className = "jetsen-tree-selected";
	this.isSelected = true;
	JetsenWeb.UI.Trees[this.treeName].selectedIndex = this.treeIndex;
	JetsenWeb.UI.Trees[this.treeName].treeSelected(this.treeIndex)
};
JetsenWeb.UI.TreeItem.prototype.unselect = function() {
	this.linkControl.className = "";
	this.linkControl.style.textDecoration = "none";
	this.isSelected = false;
	JetsenWeb.UI.Trees[this.treeName].treeUnSelected(this.treeIndex);
	JetsenWeb.UI.Trees[this.treeName].selectedIndex = null
};
JetsenWeb.UI.TreeItem.prototype.openOrClose = function() {
	this.renderItem();
	if (this.isOpen) {
		this.isOpen = false;
		this.hideSubTree()
	} else {
		this.isOpen = true;
		this.showSubTree();
		if (this.onopen && typeof this.onopen == "function") {
			this.onopen()
		}
	}
};
JetsenWeb.UI.TreeItem.prototype.showSubTree = function() {
	if (this.treeItems.length == 0 || this.treeIndex == null
			|| this.treeIndex == "")
		return;
	this.isOpen = true;
	var subControl = $(this.treeIndex);
	if (subControl != null)
		subControl.className = "open";
	var subImgControl = $(this.treeIndex + "-img");
	if (subImgControl != null)
		subImgControl.src = this.openIcon
};
JetsenWeb.UI.TreeItem.prototype.hideSubTree = function() {
	if (this.treeItems.length == 0 || this.treeIndex == null
			|| this.treeIndex == "")
		return;
	this.isOpen = false;
	var subControl = $(this.treeIndex);
	if (subControl != null)
		subControl.className = "close";
	var subImgControl = $(this.treeIndex + "-img");
	if (subImgControl != null)
		subImgControl.src = this.closeIcon
};