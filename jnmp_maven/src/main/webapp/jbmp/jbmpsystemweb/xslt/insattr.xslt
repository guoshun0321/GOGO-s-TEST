<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="insedAttrTable" border="0" cellspacing="0" cellpadding="2" width="98%">
			<tr>
				<td sortfield="ATTRIB_ID" style="width:80px;" align="left">
					<b>编号</b>
				</td>
				<td sortfield="ATTRIB_NAME" style="width:200px;" align="left">
					<b>属性名称</b>
				</td>
				<td align="left" style="width:550px;">
					<b>属性参数</b>
				</td>
				<td align="center" style="width:45px;">
					<b>选择</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="left">
						<xsl:value-of select="ATTRIB_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="ATTRIB_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="ATTRIB_PARAM" />
					</td>
					<td align="center">
						<input type="checkbox" onchange="selectInsedAttr(this,'{ATTRIB_ID}');" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>