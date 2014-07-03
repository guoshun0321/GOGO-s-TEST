<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table cellspacing="0" border="0" cellpadding="2" style="width: 98%;"
			ID="tabSystemLog">
			<tr>
				<td width="130px" align="left" sortfield="USER_NAME">
					<b>操作用户</b>
				</td>
				<td align="left" sortfield="DESCRIPTION">
					<b>描述</b>
				</td>
				<td width="140px" align="center" sortfield="LOG_TIME">
					<b>操作时间</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr>
					<td align="left">
						<xsl:value-of select="USER_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="DESCRIPTION" />
					</td>
					<td align="center">
						<xsl:value-of select="translate(substring(LOG_TIME,0,20),'T',' ')" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_TotalCount"></input>
	</xsl:template>
</xsl:stylesheet>
