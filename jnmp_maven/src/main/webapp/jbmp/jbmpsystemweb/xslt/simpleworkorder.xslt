<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabWorkOrder"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<colgroup>
				<col width="auto"></col>
				<col width="70px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="ORDER_DESC" align="left">
					<b>工单描述</b>
				</td>
				<td sortfield="USER_NAME" align="left">
					<b>当前处理人</b>
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
					<td title="{ORDER_DESC}">
						<xsl:value-of select="ORDER_DESC"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="USER_NAME"></xsl:value-of>
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
