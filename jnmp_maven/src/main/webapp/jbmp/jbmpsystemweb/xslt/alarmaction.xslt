<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAlarmAction"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<colgroup>
				<col></col>
				<col width="100px"></col>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="ACTION_NAME" align="left">
					<b>动作名称</b>
				</td>
				<td sortfield="ACTION_TYPE" align="center" style="width: 100px" >
					<b>动作类型</b>
				</td>
				<td sortfield="ACTION_PARAM" align="left">
					<b>动作参数</b>
				</td>
				<td sortfield="ACTION_DESC" align="left">
					<b>动作描述</b>
				</td>
				<td sortfield="WEEK_MASK" align="left">
					<b>动作周期</b>
				</td>
				<td sortfield="HOUR_MASK" align="left">
					<b>动作时间</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"  ondblclick="editAlarmAction('{ACTION_ID}')" >
					<td>
						<xsl:value-of select="ACTION_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="ACTION_TYPE=1">
								邮件通知
							</xsl:when>
							<xsl:when test="ACTION_TYPE=10">
								短信通知
							</xsl:when>
							<xsl:when test="ACTION_TYPE=20">
								生成工单
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="ACTION_PARAM"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ACTION_DESC"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="WEEK_MASK"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="HOUR_MASK"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editAlarmAction('{ACTION_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteAlarmAction('{ACTION_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AlarmActionCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
