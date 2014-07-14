
JetsenWeb.registerNamespace("JetsenWeb.UI.IP");
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_ip"));
function isByte(str)
{
    if (str.replace(/[0-9]/gi, "") == "")
    {
    	if (parseInt(str, 10)>=0 && parseInt(str, 10)<=255)
        {
            return true;
        }
    }
    return false;
}

IP=function(id)
{
    this.id = id;
    this.element = document.getElementById(this.id);
}
IP.ipObjs={};

IP.prototype=
{
    init:function() 
	{
        var str = "";
	    str += "<div style=\"float:left;width:36px;\"><input style=\"float:left;width:25px;\" type=\"text\" size=\"3\" maxlength=\"3\" id=\"" + this.id + "_ipFld_1\"" +
	           " onkeydown=\"onIPFldKeyDown('" + this.id + "', 1, event)\" onchange=\"onIPFldChange('" + this.id + "', 1, event)\" ></div>" +
	           "<div style=\"float:left;width:5px;\">.</div>" +
	           "<div style=\"float:left;width:36px;\"><input style=\"float:left;width:25px;\" type=\"text\" size=\"3\" maxlength=\"3\" id=\"" + this.id + "_ipFld_2\"" +
	           " onkeydown=\"onIPFldKeyDown('" + this.id + "', 2, event)\" onchange=\"onIPFldChange('" + this.id + "', 2, event)\" ></div>" +
	           "<div style=\"float:left;width:5px;\">.</div>" +
	           "<div style=\"float:left;width:36px;\"><input style=\"float:left;width:25px;\" type=\"text\" size=\"3\" maxlength=\"3\" id=\"" + this.id + "_ipFld_3\"" +
	           " onkeydown=\"onIPFldKeyDown('" + this.id + "', 3, event)\" onchange=\"onIPFldChange('" + this.id + "', 3, event)\" ></div>" +
	           "<div style=\"float:left;width:5px;\">.</div>" +
	           "<div style=\"float:left;width:36px;\"><input style=\"float:left;width:25px;\" type=\"text\" size=\"3\" maxlength=\"3\" id=\"" + this.id + "_ipFld_4\"" +
	           " onkeydown=\"onIPFldKeyDown('" + this.id + "', 4, event)\" onchange=\"onIPFldChange('" + this.id + "', 4, event)\" ></div>";
	    this.element.innerHTML = str;
	    IP.ipObjs[this.id] = this;
    },
    getValue:function() 
	{
        var fld_1 = document.getElementById(this.id + "_ipFld_1").value;
	    var fld_2 = document.getElementById(this.id + "_ipFld_2").value;
	    var fld_3 = document.getElementById(this.id + "_ipFld_3").value;
	    var fld_4 = document.getElementById(this.id + "_ipFld_4").value;
	    if (isByte(fld_1) && isByte(fld_2) && isByte(fld_3) && isByte(fld_4))
	    {
	        return parseInt(fld_1, 10) + "." + parseInt(fld_2, 10) + "." + parseInt(fld_3, 10) + "." + parseInt(fld_4, 10);
	    }
	    else
	    {
	        return "";
	    }
    },
    setValue:function(initValue) 
	{
        var ipFldArr = initValue.split(".");
	    if (ipFldArr.length != 4)
	    {
	        return false;
	    }
	    else if (!isByte(ipFldArr[0]) || !isByte(ipFldArr[1]) || !isByte(ipFldArr[2]) || !isByte(ipFldArr[3]))
	    {
	        return false;
	    }
	    
	    document.getElementById(this.id + "_ipFld_1").value = ipFldArr[0];
	    document.getElementById(this.id + "_ipFld_2").value = ipFldArr[1];
	    document.getElementById(this.id + "_ipFld_3").value = ipFldArr[2];
	    document.getElementById(this.id + "_ipFld_4").value = ipFldArr[3];
	   
	    return true;
    },
    setDisabled:function(isEnable)
    {
	    document.getElementById(this.id + "_ipFld_1").disabled = isEnable;
	    document.getElementById(this.id + "_ipFld_2").disabled = isEnable;
	    document.getElementById(this.id + "_ipFld_3").disabled = isEnable;
	    document.getElementById(this.id + "_ipFld_4").disabled = isEnable;
    }
};

function onIPFldChange(id, curFldIndex, oEvent)
{
        var ipFldElem = document.getElementById(id + "_ipFld_" + curFldIndex);
        var ipFldValue = ipFldElem.value;
        if (!isByte(ipFldValue))
        {
            ipFldElem.value = "";
        }
}

function onIPFldKeyDown(id, curFldIndex, oEvent)
{
    if(window.event) 
	{
        if (event.keyCode == 37)
	    {
	        this.prevIPFld(id, curFldIndex);
	        event.returnValue = false;
	    }
	    else if (event.keyCode==39 || event.keyCode==190 || oEvent.keyCode==110)
	    {
	        this.nextIPFld(id, curFldIndex);
	        event.returnValue = false;
	    }
    }
    else 
	{        
        if (oEvent.keyCode == 37)
	    {
	        this.prevIPFld(id, curFldIndex);
	        oEvent.preventDefault();
	    }
	    else if (oEvent.keyCode==39 || oEvent.keyCode==190 || oEvent.keyCode==110)
	    {
	        this.nextIPFld(id, curFldIndex);
	        oEvent.preventDefault();
	    }
    }
}

function prevIPFld(id, curFldIndex)
{
    if (curFldIndex > 1)
    {
        document.getElementById(id + "_ipFld_" + (--curFldIndex)).select();
    }
}

function nextIPFld(id, curFldIndex)
{
    if (curFldIndex < 4)
    {
        document.getElementById(id + "_ipFld_" + (++curFldIndex)).select();
    }
}
