<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAttribute"  border="0" cellspacing="0" cellpadding="2" width="90%" >
			<tr>
				<td sortfield="ATTRIB_NAME" align="left">
					<b>属性名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr onclick="searchObjAttribWithAttrId('{ATTRIB_ID}');" style="cursor:pointer;">
					<td align="left">
						<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
