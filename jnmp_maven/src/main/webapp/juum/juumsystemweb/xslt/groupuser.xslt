<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table cellspacing="0" cellpadding="1" style="width:98%;" ID="tabUser">
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkUser',this.checked)" id="chkCheckAllGroupUser"></input>
				</td>
				<td width="120px" sortfield="USER_NAME" align="left">
					<b>用户姓名</b>
				</td>
				<td sortfield="LOGIN_NAME" align="left">
					<b>登录名</b>
				</td>
				<td width="80px" align="left" sortfield="CREATE_TIME">
					<b>创建时间</b>
				</td>
				<td width="60px" align="center" sortfield="STATE">
					<b>状态</b>
				</td>
				<td style="width: 40px" align="center" >
					<b>编辑</b>
				</td>
				<td style="width: 40px" align="center" >
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr ondblclick="editUser('{ID}');">
					<td align="center">
						<input type="checkbox" name="chkUser" onclick="$('chkCheckAllGroupUser').checked=false;" value="{ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="USER_NAME" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="LOGIN_NAME" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="substring(CREATE_TIME,1,10)" />
					</td>
					<td align="center">
						<xsl:if test ="STATE=0">在岗</xsl:if>
						<xsl:if test ="STATE=1">借调</xsl:if>
						<xsl:if test ="STATE=2">挂职</xsl:if>
						<xsl:if test ="STATE=3">病休</xsl:if>
						<xsl:if test ="STATE=4">学习</xsl:if>
						<xsl:if test ="STATE=5">进入公司</xsl:if>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editUser('{ID}');">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="deleteUser({ID});">
							<img border="0" src="images/drop.gif"/>
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_UserCount"></input>
	</xsl:template>
</xsl:stylesheet>