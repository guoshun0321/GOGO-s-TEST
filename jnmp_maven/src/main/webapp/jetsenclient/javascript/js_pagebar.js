JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_pagebar"));
JetsenWeb.UI.PageBars = {};
JetsenWeb.UI.PageBar = function(barName) {
	this.barName = barName ? barName : "pageBar";
	this.__typeName = "JetsenWeb.UI.PageBar";
	this.pageSize = 20;
	this.currentPage = 1;
	this.rowCount = 0;
	this.pageCount = 0;
	this.orderBy = "";
	this.bDisplayNoPage = false;
	this.bDisplayPageNum = true;
	this.bDisplayPageInfo = true;
	this.bDisplayPrevNext = true;
	this.bDisplayFirstLast = true;
	this.navigationSize = 6;
	this.customPage = true;
	this.customSizeList = [ 10, 20, 30, 50, 100 ];
	this.onpagechange = null;
	this.onupdate = null;
	this.gotoPage = true;
	this.FIRST_PAGE = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-first.gif"
			+ "' border=0 onclick='JetsenWeb.UI.PageBars[\"" + this.barName
			+ "\"].firstPage()' title='首页' style='cursor:pointer'/>&nbsp;";
	this.PREV_PAGE = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-prev.gif"
			+ "' border=0 onclick='JetsenWeb.UI.PageBars[\"" + this.barName
			+ "\"].prevPage()' title='上一页'  style='cursor:pointer'/>&nbsp;";
	this.NEXT_PAGE = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-next.gif"
			+ "' border=0 onclick='JetsenWeb.UI.PageBars[\"" + this.barName
			+ "\"].nextPage()' title='下一页'  style='cursor:pointer'/>&nbsp;";
	this.LAST_PAGE = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-last.gif"
			+ "' border=0 onclick='JetsenWeb.UI.PageBars[\"" + this.barName
			+ "\"].lastPage()' title='尾页'  style='cursor:pointer'/>";
	this.FIRST_PAGE2 = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-first-disabled.gif"
			+ "' border=0 />&nbsp;";
	this.PREV_PAGE2 = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-prev-disabled.gif"
			+ "' border=0 />&nbsp;";
	this.NEXT_PAGE2 = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-next-disabled.gif"
			+ "' border=0 />&nbsp;";
	this.LAST_PAGE2 = "&nbsp;<img align=\"absmiddle\" src='"
			+ JetsenWeb.baseThemeUrl + "images/pagebar/page-last-disabled.gif"
			+ "' border=0 />";
	JetsenWeb.UI.PageBars[this.barName] = this
};
JetsenWeb.UI.PageBar.prototype.toXml = function() {
	return JetsenWeb.Xml.serialize({
		PageSize : this.pageSize,
		CurrentPage : this.currentPage,
		RowCount : this.rowCount,
		PageCount : this.pageCount,
		OrderBy : this.orderBy
	}, "PageInfo")
};
JetsenWeb.UI.PageBar.prototype.fromXml = function(xml) {
	var pageObj = JetsenWeb.Xml.toObject(xml);
	if (pageObj != null) {
		this.pageSize = parseInt(valueOf(pageObj, "PageSize", "20"));
		this.currentPage = parseInt(valueOf(pageObj, "CurrentPage", "1"));
		this.rowCount = parseInt(valueOf(pageObj, "RowCount", "0"));
		this.pageCount = parseInt(valueOf(pageObj, "PageCount", "0"));
		this.orderBy = valueOf(pageObj, "OrderBy", "")
	}
};
JetsenWeb.UI.PageBar.prototype.setOrderBy = function(sortfield, desc) {
	if (!JetsenWeb.Util.isNullOrEmpty(sortfield)) {
		this.currentPage = 1;
		var sortfields = sortfield.split(",");
		this.orderBy = "";
		for ( var i = 0; i < sortfields.length; i++) {
			if (this.orderBy == "") {
				this.orderBy += desc ? " ORDER BY " + sortfields[i] + " desc"
						: " ORDER BY " + sortfields[i]
			} else {
				this.orderBy += desc ? " , " + sortfields[i] + " desc" : " , "
						+ sortfields[i]
			}
		}
		if (this.onpagechange != null) {
			this.onpagechange()
		}
	}
};
JetsenWeb.UI.PageBar.prototype.nextPage = function() {
	if (this.currentPage < this.pageCount) {
		this.currentPage++;
		if (this.onpagechange != null) {
			this.onpagechange()
		}
	}
};
JetsenWeb.UI.PageBar.prototype.prevPage = function() {
	if (this.currentPage > 1) {
		this.currentPage--;
		if (this.onpagechange != null) {
			this.onpagechange()
		}
	}
};
JetsenWeb.UI.PageBar.prototype.firstPage = function() {
	if (this.currentPage != 1 && this.pageCount > 1) {
		this.currentPage = 1;
		if (this.onpagechange != null) {
			this.onpagechange()
		}
	}
};
JetsenWeb.UI.PageBar.prototype.lastPage = function() {
	if (this.currentPage != this.pageCount && this.pageCount > 1) {
		this.currentPage = this.pageCount;
		if (this.onpagechange != null) {
			this.onpagechange()
		}
	}
};
JetsenWeb.UI.PageBar.prototype.refresh = function() {
	if (this.onpagechange != null) {
		this.onpagechange()
	}
};
JetsenWeb.UI.PageBar.prototype.setCurrentPage = function(page) {
	if (this.currentPage == page || page > this.pageCount || page < 1) {
		return
	}
	this.currentPage = page;
	if (this.onpagechange != null) {
		this.onpagechange()
	}
};
JetsenWeb.UI.PageBar.prototype.setRowCount = function(count) {
	count = count ? count : 0;
	var intRowCount = parseInt(count);
	this.rowCount = intRowCount;
	this.pageCount = Math.ceil(intRowCount / this.pageSize);
	if (this.onupdate != null) {
		this.onupdate()
	}
};
//修改控件，添加跳转框。
JetsenWeb.UI.PageBar.prototype.generatePageControl = function() {
	var strRet = "";
	if (this.bDisplayNoPage == false && this.pageCount <= 0)
	    return strRet;
	if (this.bDisplayPageInfo) {
	    strRet = "第" + this.currentPage;
	    strRet += "/" + this.pageCount;
	    strRet += "页 共";
	    strRet += this.rowCount + "条记录"
	}
	if (this.currentPage <= this.pageCount && this.currentPage > 1) {
	    strRet += this.bDisplayFirstLast ? this.FIRST_PAGE : "";
	    strRet += this.bDisplayPrevNext ? this.PREV_PAGE : ""
	} else {
	    strRet += this.bDisplayFirstLast ? this.FIRST_PAGE2 : "";
	    strRet += this.bDisplayPrevNext ? this.PREV_PAGE2 : ""
	}
	if (this.pageCount == 0 || !this.bDisplayPageNum)
	    strRet += "";
	else {
	    var start = 1;
	    var end = this.pageCount;
	    if (this.pageCount > this.navigationSize) {
	        end = this.currentPage + parseInt(this.navigationSize / 2);
	        if (end > this.pageCount) {
	            end = this.pageCount
	        }
	        if (end < this.navigationSize)
	            end = this.navigationSize;
	        start = end - this.navigationSize + 1
	    }
	    if (start > 1) {
	        strRet += "&nbsp;<a href='javascript:JetsenWeb.UI.PageBars[\"" + this.barName + "\"].setCurrentPage(1);'>1..</a>&nbsp;"
	    }
	    for (var i = start; i <= end; i++) {
	        if (i == this.currentPage) {
	            strRet += "&nbsp;[" + i + "]&nbsp;"
	        } else {
	            strRet += "&nbsp;<a href='javascript:JetsenWeb.UI.PageBars[\"" + this.barName + "\"].setCurrentPage(" + i + ");'>" + i + "</a>&nbsp;"
	        }
	    }
	    if (end < this.pageCount) {
	        strRet += "&nbsp;<a href='javascript:JetsenWeb.UI.PageBars[\"" + this.barName + "\"].setCurrentPage(" + this.pageCount + ");'>.." + this.pageCount + "</a>"
	    }
	}
	if (this.currentPage < this.pageCount) {
	    strRet += this.bDisplayPrevNext ? this.NEXT_PAGE : "";
	    strRet += this.bDisplayFirstLast ? this.LAST_PAGE : ""
	} else {
	    strRet += this.bDisplayPrevNext ? this.NEXT_PAGE2 : "";
	    strRet += this.bDisplayFirstLast ? this.LAST_PAGE2 : ""
	}
	if(this.gotoPage){
		strRet += "&nbsp;&nbsp;<input id='gotoPage_" + this.barName + "' style='width:20px;ime-mode:Disabled;vertical-align:middle;' onKeypress=\"if(event.keyCode){return (/[\\d]/.test(String.fromCharCode(event.keyCode)))}else{return (/[\\d]/.test(String.fromCharCode(event.which)))}\">&nbsp;&nbsp;";
		strRet += "<a href='javascript:JetsenWeb.UI.PageBars[\"" + this.barName + "\"].gotoP()'>GO</a>";
	}
	if (this.customPage) {
	    strRet += "&nbsp;&nbsp;页大小&nbsp;<span class='pagebar-current' onclick='JetsenWeb.UI.PageBars[\"" + this.barName + "\"].showCustomPage(this);' onmouseover='this.className=\"pagebar-scurrent\"' onmouseout='this.className=\"pagebar-current\"'>" + this.pageSize + "</span>"
	}
	return strRet;
};

JetsenWeb.UI.PageBar.prototype.gotoP = function(){
	var pageNum = parseInt(document.getElementById("gotoPage_" + this.barName).value);
	if(!isNaN(pageNum)){
		if(pageNum >= 1 && pageNum <= this.pageCount){
			JetsenWeb.UI.PageBars[this.barName].setCurrentPage(pageNum);
		}else{
			JetsenWeb.alert("请输入1~" + this.pageCount + "的页数！");
		}
	}else{
		JetsenWeb.alert("请输入正确的页数！");
	}
}

JetsenWeb.UI.PageBar.prototype.showCustomPage = function(obj) {
	var customElement = $(this.barName + "-custom");
	if (customElement == null) {
		customElement = document.createElement("DIV");
		customElement.id = this.barName + "-custom";
		customElement.className = 'pagebar-custom';
		document.body.appendChild(customElement)
	}
	var customContent = [];
	for ( var i = 0; i < this.customSizeList.length; i++) {
		var customSize = this.customSizeList[i];
		customContent
				.push("<div onclick='JetsenWeb.UI.PageBars[\""
						+ this.barName
						+ "\"].changePageSize("
						+ customSize
						+ ");' onmouseover='this.className=\"pagebar-custom-sitem\"' onmouseout='this.className=\"pagebar-custom-item\"' >"
						+ customSize + "</div>")
	}
	customElement.innerHTML = customContent.join("");
	JetsenWeb.popup(customElement, obj)
};
JetsenWeb.UI.PageBar.prototype.changePageSize = function(pageSize) {
	if (this.pageSize == pageSize)
		return;
	this.currentPage = 1;
	this.pageSize = pageSize;
	this.refresh()
};