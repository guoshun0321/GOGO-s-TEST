// 设置下拉框的选中值
function setSelectedValue(select, value)
{
	if (!isComboBox(select))
	{
		return;
	}
	var length = select.length;
	var options = select.options;
	for (var i = 0; i < length; i++)
	{
		if (options[i].value == value)
		{
			select.selectedIndex = i;
			return;
		}
	}
}

// 获取下拉框的选中值
function getSelectedValue(select)
{
	if (isComboBox(select))
	{
		return select.length > 0 ? select.options[select.selectedIndex].value : null;
	}
	return null;
}

function getSelectedAttr(select, attrName)
{
	if (isComboBox(select))
	{
		return select.length > 0 ? select.options[select.selectedIndex][attrName] : null;
	}
	return null;
}

// 获取下拉框的选中文本
function getSelectedText(select)
{
	if (isComboBox(select))
	{
		return select.options[select.selectedIndex].text;
	}
	return null;
}

// 删除下拉框的选中项
function removeSelectedOptions(select)
{
	var removedOptions = [];
	if (isComboBox(select))
	{
		var length = select.length;
		if (length != 0 && select.selectedIndex == -1)
		{
			jetsennet.alert("请选择要删除的项！");
			return removedOptions;
		}
		var options = select.options;
		for (var i = length - 1; i >= 0; i--)
		{
			if (options[i].selected)
			{
				removedOptions.push(options[i]);
				select.remove(i);
			}
		}
	}
	return removedOptions;
}

// 获取下拉框的所有值数组
function getAllValues(select)
{
	var values = [];
	if (isComboBox(select))
	{
		var length = select.length;
		var options = select.options;
		for (var i = 0; i < length; i++)
		{
			values.push(options[i].value);
		}
	}
	return values;
}

// 获取下拉框的所有文本数组
function getAllTexts(select)
{
	var texts = [];
	if (isComboBox(select))
	{
		var length = select.length;
		var options = select.options;
		for (var i = 0; i < length; i++)
		{
			texts.push(options[i].text);
		}
	}
	return texts;
}

//下拉框指定位置插入项
function insertAt(select, text, value, index)
{
	if (isComboBox(select))
	{
		var option = document.createElement("option");
		option.value = value;
		option.innerText = text;
		select.insertBefore(option, select.options[index]);
	}
}

//清空下拉框（主要是删除分组）
function clearSelect(select)
{
	if (isComboBox(select))
	{
		select.length = 0;
		for (var i = select.childNodes.length - 1; i >= 0; i--) {
			select.removeChild(select.childNodes[i]);
		}
	}
}

// 判断对象是否是下拉框
function isComboBox(select)
{
	if (select)
	{
		if (select.tagName == "SELECT")
		{
			return true;
		}
	}
	return false;
}

//移动下拉框的选中项
function moveSelectedOptions(srcSelect, destSelect)
{
	var movedOptions = [];
	if (isComboBox(srcSelect) && isComboBox(destSelect))
	{
		var length = srcSelect.length;
		if (length != 0 && srcSelect.selectedIndex == -1)
		{
			jetsennet.alert("请选择要移动的项！");
			return movedOptions;
		}
		var options = srcSelect.options;
		for (var i = length - 1; i >= 0; i--)
		{
			if (options[i].selected)
			{
				movedOptions.push(options[i]);
				select.remove(i);
				destSelect.options.add(options[i]);
			}
		}
	}
	return movedOptions;
}

function contain(arrs, val) 
{
    for(arr in arrs) 
    {
        if(arrs[arr] == val) 
        {
            return true;
        }
    }
    return false;
}

function validateIP(ipstr) 
{
    var ipReg = new RegExp("^((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))$","g");
    return ipReg.test(ipstr);
}

String.prototype.startWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
		return false;
	if(this.substr(0,str.length)==str)
		return true;
	else
		return false;
	return true;
}

function getSingleCheckedValues(objName) {
    var retval;
    var objs = document.getElementsByName(objName);
    if(objs == null || objs.length == 0) {
    	return null;
    }
    for (var i = 0; i < objs.length; i++) {
        if (objs[i].checked == true) {
            return objs[i].value;
        }
    }
    return null;
}