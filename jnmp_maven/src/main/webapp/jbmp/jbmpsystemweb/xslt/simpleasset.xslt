<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAsset"  border="0" cellspacing="0" cellpadding="2" width="97.3%" >
			<colgroup>
				<col width="60px"></col>
				<col width="150px"></col>
				<col width="auto"></col>
				<col width="80px"></col>
				<col width="90px"></col>
				<col width="90px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td align="left">
					<b>资产编号</b>
				</td>
				<td align="left">
					<b>设备名称</b>
				</td>
				<td align="left">
					<b>产品型号</b>
				</td>
				<td align="left">
					<b>规格</b>
				</td>
				<td align="center">
					<b>采购日期</b>
				</td>
				<td align="left">
					<b>归属部门</b>
				</td>
				<td align="center">
					<b>责任人</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td>
						<xsl:value-of select="ASSET_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TYPE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="STYLE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="PURCHASE_DATE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="BELONG_DEPARTMENT"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="PERSON_LIABLE"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AssetCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>