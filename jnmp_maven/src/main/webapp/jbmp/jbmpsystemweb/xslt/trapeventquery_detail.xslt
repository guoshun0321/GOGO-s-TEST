<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabTrapEventDetail" border="0" cellspacing="0"
			cellpadding="2" width="470px">
			<colgroup>
				<col width="150px"></col>
				<col width="150px"></col>
				<col></col>
			</colgroup>
			<tr>
				<td sortfield="OID" align="left">
					<b>OID</b>
				</td>
				<td align="left">
					<b>名称</b>
				</td>
				<td align="left">
					<b>值</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="left">
						<xsl:value-of select="OID" />
					</td>
					<td align="left">
						<xsl:value-of select="OID_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="OID_VALUE" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>