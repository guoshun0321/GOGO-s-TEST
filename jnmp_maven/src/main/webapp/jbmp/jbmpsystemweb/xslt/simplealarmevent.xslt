<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmEvent" border="0" cellspacing="0" cellpadding="2" width="98.2%">
			<colgroup>
				<col width="45px"></col>
				<col width="auto"></col>
				<col width="100px"></col>
				<col width="60px"></col>
				<col width="130px"></col>
			</colgroup>
			<tr>
				<td sortfield="ALARMEVT_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>报警对象</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>报警类型</b>
				</td>
				<td align="center" sortfield="ALARM_LEVEL">
					<b>报警级别</b>
				</td>
				<td align="center" sortfield="COLL_TIME">
					<b>报警时间</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" onclick="onAlarmEventClick({ALARMEVT_ID})" ondblclick="onAlarmEventDoubleClick({ALARMEVT_ID})">
					<td>
						<xsl:value-of select="ALARMEVT_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td width="100px">
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="ALARM_LEVEL=10">
								警告报警
							</xsl:when>
							<xsl:when test="ALARM_LEVEL=20">
								一般报警
							</xsl:when>
							<xsl:when test="ALARM_LEVEL=30">
								重要报警
							</xsl:when>
							<xsl:when test="ALARM_LEVEL=40">
								严重报警
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td style="line-height:14px" align="center">
						<xsl:if test="COLL_TIME!=0">
							<xsl:value-of select="COLL_TIME"></xsl:value-of>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmEventCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
