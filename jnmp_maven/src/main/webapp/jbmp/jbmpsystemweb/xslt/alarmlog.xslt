<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmLog" border="0" cellspacing="0"
			cellpadding="2" width="98%">
			<tr>
				<td align="center" width="30px">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkAlarmLog',this.checked)"
						id="chkCheckAllAlarmLog"></input>
				</td>
				<td sortfield="ALARMEVT_ID" align="left" width="55px">
					<b>编号</b>
				</td>
				<td align="left" width="60px" sortfield="USER_NAME">
					<b>处理人</b>
				</td>
				<td align="left" width="60px" sortfield="OPERATE">
					<b>处理方式</b>
				</td>
				<td align="center" width="130px" sortfield="OPERATE_TIME">
					<b>操作时间</b>
				</td>
				<td width="110px" sortfield="OBJ_NAME" align="left">
					<b>报警对象</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left" width="100px">
					<b>报警类型</b>
				</td>
				<td width="60px" align="center" sortfield="EVENT_STATE">
					<b>当前状态</b>
				</td>
				<td width="60px" align="center" sortfield="ALARM_LEVEL">
					<b>报警等级</b>
				</td>
				<td width="130px" align="center" sortfield="COLL_TIME">
					<b>报警时间</b>
				</td>
				<td width="130px" align="center" sortfield="RESUME_TIME">
					<b>恢复时间</b>
				</td>
				<td width="60px" align="center" sortfield="EVENT_DURATION">
					<b>持续时间</b>
				</td>
				<td width="140px" align="left" sortfield="EVENT_DESC">
					<b>报警描述</b>
				</td>
				<td align="center" width="45px">
					<b>意见</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="chkAlarmLog"
							onclick="$('chkCheckAllAlarmLog').checked=false;preventEvent(event);"
							value="{ID}"></input>
					</td>
					<td>
						<xsl:value-of select="ALARMEVT_ID"></xsl:value-of>
					</td>
					<td align="left" style="line-height:14px">
							<xsl:value-of select="USER_NAME"></xsl:value-of>
					</td>
					<td align="left" style="line-height:14px">
							<xsl:value-of select="OPERATE"></xsl:value-of>
					</td>
					<td align="center" style="line-height:14px">
							<xsl:value-of select="translate(substring(OPERATE_TIME,0,20),'T',' ')"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td width="100px">
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="EVENT_STATE=1">
								已确认
							</xsl:when>
							<xsl:when test="EVENT_STATE=2">
								已清除
							</xsl:when>
							<xsl:when test="EVENT_STATE=3">
								已处理
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
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
							<xsl:when test="ALARM_LEVEL=50">
								离线报警
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
					<td style="line-height:14px" align="center">
						<xsl:if test="RESUME_TIME!=0">
							<xsl:value-of select="RESUME_TIME"></xsl:value-of>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test="EVENT_DURATION!=0">
							<xsl:value-of select="EVENT_DURATION"></xsl:value-of>
						</xsl:if>
					</td>
					<td>
						<xsl:value-of select="EVENT_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:if test="EVENT_STATE=3">
							<a href="javascript:void(0)" onclick="viewCheckDesc('{ALARMEVT_ID}');preventEvent(event);">
								<img border="0" src="images/idea.gif" />
							</a>
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
