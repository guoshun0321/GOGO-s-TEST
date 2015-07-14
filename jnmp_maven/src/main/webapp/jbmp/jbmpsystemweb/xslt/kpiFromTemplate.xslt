<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="kaiListFromTemp" border="0" cellspacing="0" cellpadding="2"
			width="95%">
			<colgroup>
				<col width="90px"></col>
				<col width="90px"></col>
				<col width="60px"></col>
			</colgroup>
			<tr>
				<td sortfield="OBJ_NAME" align="left">
					<b>资源</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>指标</b>
				</td>
				<td sortfield="CHARTID" align="left">
					<b>所属图表</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" width="40px"
					align="center">
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="CHARTID" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>