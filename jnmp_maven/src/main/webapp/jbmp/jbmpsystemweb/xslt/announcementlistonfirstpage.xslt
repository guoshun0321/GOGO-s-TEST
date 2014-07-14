<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="announcementListOnFirPage"  border="0" cellspacing="0" cellpadding="2" width="99%" >
			<tr>
				<td sortfield="ANNOUNCEMENT_TITLE" align="left">
					<b>公告栏标题</b>
				</td>
				<td sortfield="CREATE_TIME" align="center">
					<b>发布时间</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="showTheAnnouncement('{ANNOUNCEMENT_ID}')">
					<td>
						<a href="javascript:void(0)" onclick="showTheAnnouncement('{ANNOUNCEMENT_ID}')">
							<xsl:value-of select="ANNOUNCEMENT_TITLE"></xsl:value-of>
						</a>
					</td>
					<td align="center">
						<xsl:value-of select="CREATE_TIME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_pageCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
