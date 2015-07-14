<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmList"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td sortfield="ALARM_NAME" width="150px;" align="left">
					<b>模板名称</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" onclick="selectTemplate('{TEMPLATE_ID}')">
					<td>
						<xsl:value-of select="TEMPLATE_NAME"> </xsl:value-of>
					</td>
					<td align="center" width="35px">
						<a hreg="javascript:void(0)" onclick="delTemplate('{TEMPLATE_ID}')">
							<img style="cursor:pointer" border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmListCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
