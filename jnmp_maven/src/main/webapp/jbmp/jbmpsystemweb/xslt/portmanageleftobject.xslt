<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabLeftObject" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col width="70px" align="left"></col>
				<col width="240px" align="left"></col>
				<col width="240px" align="left"></col>
			</colgroup>
			<tr>
				<td width="30px" align="center">
				</td>
				<td sortfield="OBJ_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>A对象名称</b>
				</td>
				<td sortfield="IP_ADDR" align="left">
					<b>A对象IP地址</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="$('chkLeft{OBJ_ID}').click()">
					<td align="center">
						<input id="chkLeft{OBJ_ID}" type="checkbox" name="chkLeftObject"
							onclick="uncheckAllItems('chkLeftObject',this)" value="{OBJ_ID}">
						</input>
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="IP_ADDR" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_LeftObjectCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>