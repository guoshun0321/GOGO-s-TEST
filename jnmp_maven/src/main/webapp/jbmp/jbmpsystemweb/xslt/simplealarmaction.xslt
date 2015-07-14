<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAction"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td align="center" width="30px">
		         	<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkAction',this.checked)" id="chkCheckAllSimplealarmaction"></input>
		        </td>
				<td sortfield="ACTION_NAME">
					<b>动作名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
		            	<input type="checkbox" name="chkAction" onclick="$('chkCheckAllSimplealarmaction').checked=false;preventEvent(event);" value="{ACTION_ID}"></input>
		            </td>
					<td>
						<xsl:value-of select="ACTION_NAME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmActionCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
