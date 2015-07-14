<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabWorkOrderProcess"  border="0" cellspacing="0" cellpadding="2" width="99%" >
			<colgroup>
				<col width="60px"></col>
				<col width="75px"></col>
				<col width="75px"></col>
				<col width="auto"></col>
				<col width="130px"></col>
			</colgroup>
			<tr>
				<td sortfield="PROCESS_TYPE" align="center">
					<b>处理类型</b>
				</td>
				<td sortfield="FROM_USERNAME" align="left">
					<b>当前操作人</b>
				</td>
				<td sortfield="TO_USERNAME" align="left">
					<b>被分派对象</b>
				</td>
				<td sortfield="PROCESS_DESC" align="left">
					<b>处理描述</b>
				</td>
				<td sortfield="CREATE_TIME" align="center">
					<b>处理时间</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<xsl:choose>
							<xsl:when test="PROCESS_TYPE=1">
								已分派
							</xsl:when>
							<xsl:when test="PROCESS_TYPE=2">
								已处理
							</xsl:when>
							<xsl:when test="PROCESS_TYPE=3">
								已关闭
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="FROM_USERNAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TO_USERNAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="PROCESS_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="CREATE_TIME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_WorkOrderProcessCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
