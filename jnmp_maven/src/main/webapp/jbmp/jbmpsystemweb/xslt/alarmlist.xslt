<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmList"  border="0" cellspacing="0" cellpadding="2" width="100%" >
			<tr>
			<td width="30px;"></td>
				<td sortfield="ALARM_NAME" width="150px;" align="left">
					<b>规则名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" onclick="selectAlarm('{ALARM_ID}','{''}','{CHECK_NUM}','{OVER_NUM}','{CHECK_SPAN}')">
					<td >
						<input type="radio" name="chkAllAlarms"  value="{ALARM_ID}"
							onclick="selectAlarm('{ALARM_ID}','{''}','{CHECK_NUM}','{OVER_NUM}','{CHECK_SPAN}')"></input>	
					</td>
					<td>
						<xsl:value-of select="ALARM_NAME"> </xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmListCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
