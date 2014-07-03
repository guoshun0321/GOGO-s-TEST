<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="selectattributeid" border="0" cellspacing="0"
			cellpadding="2" width="99%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col align="left"></col>
				<col align="left"></col>
			</colgroup>
			<tr>
				<td align="center" width="30px;">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('selectAttribute',this.checked)"
						id="chkCheckAllSelectAttribute"></input>
				</td>
				<td sortfield="ATTRIB_NAME" align="left">
					<b>属性名称</b>
				</td>
				<td sortfield="ATTRIB_VALUE" align="left">
					<b>属性值</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="selectAttribute"
							onclick="$('chkCheckAllSelectAttribute').checked=false;" value="{ATTRIB_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="ATTRIB_VALUE"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_MonitorAttributeCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
