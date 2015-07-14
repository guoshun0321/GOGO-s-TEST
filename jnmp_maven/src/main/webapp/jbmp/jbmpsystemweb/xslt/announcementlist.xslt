<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmAction"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<colgroup>
				<col width="80px"></col>
				<col width="auto"></col>
				<col width="100px"></col>
				<col width="130px" align="center"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="ANNOUNCEMENT_ID">
					<b>公告编号</b>
				</td>
				<td sortfield="ANNOUNCEMENT_TITLE">
					<b>公告标题</b>
				</td>
				<td sortfield="CREATE_USER">
					<b>创建人</b>
				</td>
				<td sortfield="CREATE_TIME" align="center">
					<b>创建时间</b>
				</td>
				<td align="center">
					<b>预览</b>
				</td>
				<td align="center">
					<b>编辑</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"  ondblclick="editAnnouncement('{ANNOUNCEMENT_ID}')" >
					<td>
						<xsl:value-of select="ANNOUNCEMENT_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ANNOUNCEMENT_TITLE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="CREATE_USER"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="CREATE_TIME"></xsl:value-of>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="priviewAnnouncement('{ANNOUNCEMENT_ID}')">
							<img border="0" src="images/yulan.gif"/>
						</a>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="editAnnouncement('{ANNOUNCEMENT_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td>
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteAnnouncement('{ANNOUNCEMENT_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_pageCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
