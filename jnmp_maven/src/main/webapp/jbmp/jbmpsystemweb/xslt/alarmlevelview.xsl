<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmLevel"  border="0" cellspacing="0" cellpadding="2" width="96%" >
			<tr>
				<td sortfield="LEVEL_NAME" align="left" width="30%">
					<b>级别名称</b>
				</td>
				<td sortfield="ALARM_LEVEL" align="center">
					<b>报警等级</b>
				</td>
				<td sortfield="CONDITION" align="center">
					<b>报警条件</b>
				</td>
				<td sortfield="THRESHOLD" align="left">
					<b>阈值</b>
				</td>
				<td width="30%" sortfield="LEVEL_DESC" align="left">
					<b>级别描述</b>
				</td>				
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editAlarmLevel('{LEVEL_ID}')">
					<td align="left">
						<xsl:value-of select="LEVEL_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="ALARM_LEVEL=0">
								正常
							</xsl:when>
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
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="CONDITION='LT'">
								小于
							</xsl:when>
							<xsl:when test="CONDITION='LE'">
								小于等于
							</xsl:when>
							<xsl:when test="CONDITION='EQ'">
								等于
							</xsl:when>
							<xsl:when test="CONDITION='NOTEQ'">
								不等于
							</xsl:when>
							<xsl:when test="CONDITION='GT'">
								大于
							</xsl:when>
							<xsl:when test="CONDITION='GE'">
								大于等于
							</xsl:when>
							<xsl:when test="CONDITION='LK'">
								Like
							</xsl:when>
							<xsl:when test="CONDITION='UNLK'">
								Unlike
							</xsl:when>
							<xsl:when test="CONDITION='EX'">
								存在
							</xsl:when>
							<xsl:when test="CONDITION='IN'">
								区间内
							</xsl:when>
							<xsl:when test="CONDITION='NOTIN'">
								区间外
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="THRESHOLD"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="LEVEL_DESC"></xsl:value-of>
					</td>					
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmLevelCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
