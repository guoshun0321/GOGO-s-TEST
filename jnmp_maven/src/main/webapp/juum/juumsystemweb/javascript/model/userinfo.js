//=============================================================================
//YuKeiWeb UUM_USER --Xiaomin Lee 2008-12-24 11:43:14
//=============================================================================
JetsenWeb.registerNamespace("JetsenWeb.JUUM");
JetsenWeb.require("js_modelbase");

JetsenWeb.JUUM.UserInfo = function()
{
	this.__typeName = "JetsenWeb.JDVN.UserInfo";
	this.__modelName = "UUM_USER";
	//ID
	this.ID = "0";
	//LOGIN_NAME
	this.LOGIN_NAME = "";
	//USER_NAME
	this.USER_NAME = "";
	//PASSWORD
	this.PASSWORD = "";
	//SEX
	this.SEX = "0";
	//DUTY_TITLE
	this.DUTY_TITLE = "";
	//ADDRESS
	this.ADDRESS = "";
	//EMAIL
	this.EMAIL = "";
	//OFFICE_PHONE
	this.OFFICE_PHONE = "";
	//HOME_PHONE
	this.HOME_PHONE = "";
	//MOBILE_PHONE
	this.MOBILE_PHONE = "";
	//DESCRIPTION
	this.DESCRIPTION = "";
	//STATE
	this.STATE = "0";
	//CREATE_TIME
	this.CREATE_TIME = "";
	//USER_CARD
	this.USER_CARD = "";
	//BIRTHDAY
	this.BIRTHDAY = "2008-12-24";
	//RIGHT_LEVEL
	this.RIGHT_LEVEL = "0";
	///USER_TYPE
	this.USER_TYPE = "0";
	//HOME_PATH
	this.HOME_PATH = "";
	//PATH_SIZE
	this.PATH_SIZE = "0";
	//APP_PARAM
	this.APP_PARAM = "";
	//FIELD_1
	this.FIELD_1 = "";
	//FIELD_2
	this.FIELD_2 = "";
	
	this.GROUP_USER = "";
	this.ROLE_USER = "";
	this.MODIFY_PW = "0";
}

JetsenWeb.JUUM.UserInfo.prototype = new JetsenWeb.ModelBase();

