// 验证是否为正整数
function validateNumber(num) {
	var regStr = "^[0-9]+$";
	var reg = RegExp(regStr);
	if(num.search(reg) != -1) {
		return true;
	} else {
		return false;
	}
}
// 验证端口
function validatePort(num) {
	if(validateNumber(num)) {
		if(num < 65536 && num > 0) {
			return true;
		}
	}
	return false;
}
// 验证起始IP，结束IP
function validateIPSection(sip, eip) {
	if (sip == "") {
		jetsennet.alert("开始IP未填写完！");
		return false;
	}
	if (eip == "") {
		jetsennet.alert("结束IP未填写完！");
		return false;
	}
	if (!validateIP(sip)) {
		jetsennet.alert("开始IP不正确！");
		return false;
	}
	if (!validateIP(eip)) {
		jetsennet.alert("结束IP不正确！");
		return false;
	}
	var temp = compareIp(sip, eip);
	if (temp == 0) {
		jetsennet.alert("开始IP不能大于结束IP！");
		return false;
	}
	return true;
}

//比较IP大小，需要保证传入的ip非空，且未标准形式
//ip1 > ip2 返回 0， ip1 < ip2 返回 1， ip1 = ip2 返回2
function compareIp(ip1, ip2)
{
	ip1Array = ip1.split(".");
	ip2Array = ip2.split(".");
	for(var i = 0; i < 4; i++) {
		var temp1 = parseInt(ip1Array[i]);
		var temp2 = parseInt(ip2Array[i]);
		if(temp1 > temp2) {
			return 0;
		} else if(temp1 < temp2) {
			return 1;
		}
	}
	return 2;
}