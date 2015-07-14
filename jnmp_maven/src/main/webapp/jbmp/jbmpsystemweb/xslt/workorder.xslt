<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabWorkOrder"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<colgroup>
				<col width="45px"></col>
				<col width="150px"></col>
				<col width="100px"></col>
				<col width="60px"></col>
				<col width="130px"></col>
				<col width="60px"></col>
				<col width="70px"></col>
				<col width="auto"></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="ORDER_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>报警对象</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>报警类型</b>
				</td>
				<td sortfield="ALARM_LEVEL" align="center">
					<b>报警级别</b>
				</td>
				<td sortfield="CREATE_TIME" align="center">
					<b>生成时间</b>
				</td>
				<td sortfield="ORDER_STATE" align="center">
					<b>工单状态</b>
				</td>
				<td sortfield="USER_NAME" align="left">
					<b>当前处理人</b>
				</td>
				<td sortfield="ORDER_DESC" align="left">
					<b>工单描述</b>
				</td>
				<td style="width: 45px" align="center">
					<b>详情</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="viewWorkOrderProcess('{ORDER_ID}', {ORDER_STATE}, '{EVENT_ID}', '{ORDER_DESC}')">
					<td>
						<xsl:value-of select="ORDER_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td>
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
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:value-of select="translate(substring(CREATE_TIME,0,20),'T',' ')"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="ORDER_STATE=0">
								初始化
							</xsl:when>
							<xsl:when test="ORDER_STATE=1">
								已分派
							</xsl:when>
							<xsl:when test="ORDER_STATE=2">
								已处理
							</xsl:when>
							<xsl:when test="ORDER_STATE=3">
								已关闭
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="USER_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ORDER_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="viewWorkOrderProcess('{ORDER_ID}', {ORDER_STATE}, '{EVENT_ID}', '{ORDER_DESC}')">
							<img border="0" src="images/window.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteWorkOrder('{ORDER_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_WorkOrderCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
