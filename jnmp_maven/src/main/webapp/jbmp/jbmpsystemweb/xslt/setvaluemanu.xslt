<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabSetValueManu" border="0" cellspacing="0"
			cellpadding="2" width="98%">
			<colgroup>
				<col width="80px" align="left"></col>
				<col align="left"></col>
				<col width="180px" align="center"></col>
			</colgroup>
			<tr>
				<td width="80px" align="left">
					<b>编号</b>
				</td>
				<td align="left">
					<b>名称</b>
				</td>
				<td width="180px" align="center">
					<b>值</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td>
						<xsl:value-of select="OBJATTR_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<input type="text" id="setValue_{OBJATTR_ID}" validatetype="Integer"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
