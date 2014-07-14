<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabSyslog"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<tr>
				<td align="left" width="140px" sortfield="IP_ADDR">
					<b>IP地址</b>
				</td>
				<td align="left" sortfield="CONTENT">
					<b>消息</b>
				</td>
				<td align="center" width="140px" sortfield="COLL_TIME">
					<b>接收时间</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td>
						<xsl:value-of select="IP_ADDR"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="CONTENT"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="COLL_TIME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_SyslogCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
