<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmConfig"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<tr>
				<td sortfield="ALARM_NAME" align="left">
					<b>规则名称</b>
				</td>
				<td sortfield="FIELD_1" width="120px" align="left">
					<b>规则类型</b>
				</td>
				<td sortfield="CHECK_NUM" width="120px" align="left">
					<b>检查次数</b>
				</td>
				<td sortfield="OVER_NUM" align="left">
					<b>越限次数</b>
				</td>
				<td sortfield="ALARM_TYPE" align="left">
					<b>报警类型</b>
				</td>
				<td sortfield="ALARM_DESC" align="left">
					<b>报警描述</b>
				</td>
				<td align="center" width="70px">
					<b>报警级别</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"  ondblclick="editAlarmConfig('{ALARM_ID}')" >
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="CHECK_SPAN=1 and CHECK_NUM=1 and OVER_NUM=1">
								Trap
							</xsl:when>
							<xsl:otherwise>
								一般
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="CHECK_NUM"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OVER_NUM"></xsl:value-of>
					</td>				
					<td>
						<xsl:value-of select="TYPE_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ALARM_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="级别" src="images/warnlevel.gif" onclick="viewAlarmLevel('{ALARM_ID}','{ALARM_NAME}','{CHECK_SPAN}','{CHECK_NUM}','{OVER_NUM}')"/>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editAlarmConfig('{ALARM_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<xsl:if test="ALARM_ID &gt; 1000">
							<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteAlarmConfig('{ALARM_ID}');"/>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmConfigCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
