<%@ Page Language="C#" AutoEventWireup="true" CodeFile="InsertImage.aspx.cs" Inherits="HtmlBox_InsertImage" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head runat="server">    
    <title>插入圖片</title>
		<meta content="Microsoft Visual Studio .NET 7.1" name="GENERATOR">
		<meta content="C#" name="CODE_LANGUAGE">
		<meta content="JavaScript" name="vs_defaultClientScript">
		<meta content="http://schemas.microsoft.com/intellisense/ie5" name="vs_targetSchema">
		<LINK href="style.css" rel="stylesheet">
		<base target="_self"/>
		<script language="javascript">
		function Trim(str){	str=String(str);var reg=/^\s*/;	str=str.replace(reg,"");reg=/\s*$/;	str=str.replace(reg,"");return str;	}
		function IsDigit(){return ((event["keyCode"]>=0x30)&&(event["keyCode"]<=0x39));}
		function changeImage(){	
		    document.all.showimg.filters.item(0).Apply();/*blendTrans,revealTrans*/
		    document.all.showimg.src='../<%=strPath2.Replace("\\","/")%>'+document.all.lbox_ImgName.value;document.all.showimg.filters.item(0).Play();  }
		function selectcolor(){var winColor = window.showModalDialog("InsertColor.htm","","dialogwidth:280px;dialogheight:300px;status:no");if(winColor!=null){document.all.txt_bordercolor.value = winColor;}}
		function Insert(){
		if(Trim(document.all.txt_text.value)=="") {jetsennet.alert('請輸入圖片替代文字!');return;}
		if(document.all.lbox_ImgName.selectedIndex<0){return;}
		var str = "<img src='"+document.all.showimg.src.replace(new RegExp("^[a-z]*:[/][/][^/]*",""),"")+"' align='"+document.all.ddl_align.value+"'";
		var border="";	var height="";	var width="";	var text="";
		if(Trim(document.all.txt_Border.value)!="")	
		{	   
		   if(Trim(document.all.txt_bordercolor.value)!="")
				 border="BORDER: "+document.all.txt_bordercolor.value+" "+document.all.txt_Border.value +"px solid";
			else
				 border="BORDER: black "+document.all.txt_Border.value +"px solid";
		}		  
		if(Trim(document.all.txt_Width.value)!="")
		  width="width:"+document.all.txt_Width.value;
		if(Trim(document.all.txt_Height.value)!="")
		   height="Height:"+document.all.txt_Height.value;
		if(Trim(document.all.txt_text.value)!="")
		   text="alt="+document.all.txt_text.value;		
		str = str+" style='"+border+";"+width+";"+height+";' "+text+" />";
		top["returnValue"]=str ;
		top.close() ;}
		function Cancel(){top["returnValue"]=null ;	top.close() ;}
		function btn_uploadvalid(){if(Trim(document.all.File1.value)!=""){var fname1 = document.all.File1.value;	fname1=fname1.substr(fname1.length-4,4).toLowerCase();if(fname1!=".gif" && fname1!=".jpg" && fname1!=".bmp" && fname1!="jpeg"){  jetsennet.alert('請上傳檔案類為GIF/JPG/BMP/JPEG的文件!'); 	document.all.File1.focus();	return false;}	}else{return false;}return true;}
		</script>
</head>
<body onload="changeImage();">
		<form id="Form1" method="post" runat="server">
			<table cellSpacing="0" cellPadding="5" width="550" align="center" border="0">
				<tr id="td1">
					<td style="WIDTH: 1%"><asp:listbox id="lbox_ImgName" runat="server" Height="305px" Width="150px"></asp:listbox></td>
					<td>
						<div style="BORDER-RIGHT: 1.5pt inset; PADDING-RIGHT: 5px; BORDER-TOP: 1.5pt inset; PADDING-LEFT: 5px; PADDING-BOTTOM: 5px; VERTICAL-ALIGN: top; OVERFLOW: auto; BORDER-LEFT: 1.5pt inset; WIDTH: 400px; PADDING-TOP: 5px; BORDER-BOTTOM: 1.5pt inset; HEIGHT: 300px; BACKGROUND-COLOR: white; TEXT-ALIGN: center"><IMG id="showimg" style="FILTER: blendTrans(duration=1,transition=3)" src="" name="showimg">
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2"><asp:button id="btn_refresh" runat="server" Text="重新整理" OnClick="btn_refresh_Click"></asp:button></td>
				</tr>
				<tr>
					<td colSpan="2">
						<fieldset id="fieldsetUpload" style="WIDTH: 550px"><legend>上傳
							</legend>
							<table class="normal" cellSpacing="10" cellPadding="0" width="100%" border="0">
								<tr>
									<td style="WIDTH: 463px"><INPUT id="File1" style="WIDTH: 452px; HEIGHT: 19px" type="file" size="56" name="File1"
											runat="server"><FONT face="新細明體">&nbsp;</FONT>
									</td>
									<td align="right"><asp:button id="btn_upload" runat="server" Text="上傳" OnClick="btn_upload_Click"></asp:button></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td colSpan="2">
						<fieldset id="fieldsetInsert" style="WIDTH: 550px"><legend>插入圖片
							</legend>
							<table class="normal" cellSpacing="10" cellPadding="0" width="100%" border="0">
								<tr>
									<td noWrap>寬度:</td>
									<td style="WIDTH: 56px"><INPUT onkeypress="event.returnValue=IsDigit();" id="txt_Width" type="text" size="6" name="txt_Width">
									</td>
									<td noWrap>邊框:</td>
									<td><INPUT maxlength="1" onkeypress="event.returnValue=IsDigit();" id="txt_Border" type="text"
											size="6" name="txt_Border">
									</td>
									<td noWrap>高度:</td>
									<td><INPUT onkeypress="event.returnValue=IsDigit();" id="txt_Height" type="text" size="6" name="txt_Height">
									</td>
									<td noWrap>邊框顏色:</td>
									<td><input id="txt_bordercolor" readOnly type="text" size="6" name="txt_bordercolor">&nbsp;<input style="BORDER-RIGHT: 1px solid; BORDER-TOP: 1px solid; BORDER-LEFT: 1px solid; WIDTH: 38px; BORDER-BOTTOM: 1px solid; HEIGHT: 20px"
											onclick="selectcolor();" type="button" value="color...">
									</td>
								</tr>
								<tr>
									<td>對齊方式:</td>
									<td colspan="2"><select id="ddl_align"><option value='absbottom'>absbottom</option>
											<option value='absmiddle' selected>absmiddle</option>
											<option value='baseline'>baseline</option>
											<option value='bottom'>bottom</option>
											<option value='left'>left</option>
											<option value='middle'>middle</option>
											<option value='right'>right</option>
											<option value='texttop'>texttop</option>
											<option value="top">top</option>
										</select></td>
									<td noWrap colspan="2">替代文字:</td>
									<td colSpan="3"><input size="45" name="txt_text" id="txt_text"></td>
								</tr>
								<tr>
									<td align="right" colspan="8"><input onclick="Insert();" type="button" value=" 插入 ">&nbsp;<input onclick="Cancel();" type="button" value="取消"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
		</form>
	</body>
</HTML>