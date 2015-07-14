<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabTrapEvent" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<tr>
				<td sortfield="TRAPEVT_ID" align="left" width="45px">
					<b>编号</b>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>对象名称</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>Trap名称</b>
				</td>
				<td width="130px" align="center" sortfield="COLL_TIME">
					<b>接收时间</b>
				</td>
				<td width="130px" align="center" sortfield="TRAP_TIME">
					<b>Trap时间</b>
				</td>
				<td width="300px" align="left" sortfield="TRAP_OID">
					<b>OID</b>
				</td>
				<td align="center" width="70px">
					<b>Trap内容</b>
				</td>
				<td align="center" width="70px">
					<b>报警事件</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="left">
						<xsl:value-of select="TRAPEVT_ID"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
					</td>
					<td style="line-height:14px" align="center">
						<xsl:if test="COLL_TIME!=0">
							<xsl:value-of select="COLL_TIME"></xsl:value-of>
						</xsl:if>
					</td>
					<td style="line-height:14px" align="center">
						<xsl:if test="TRAP_TIME!=0">
							<xsl:value-of select="TRAP_TIME"></xsl:value-of>
						</xsl:if>
					</td>
					<td align="left">
						<xsl:value-of select="TRAP_OID"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="viewTrapValue('{TRAPEVT_ID}');preventEvent(event);">
							<img border="0" src="images/window.gif" />
						</a>
					</td>
					<td align="center">
						<xsl:if test="ALARMEVT_ID!=-1">
							<a href="javascript:void(0)" onclick="viewAlarmEvent('{ALARMEVT_ID}');preventEvent(event);">
								<img border="0" src="images/window.gif" />
							</a>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_TrapEventCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>