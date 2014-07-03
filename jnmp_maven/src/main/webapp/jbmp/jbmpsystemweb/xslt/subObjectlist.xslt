<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="subObjectList" border="0" cellspacing="0"
			cellpadding="2" width="99.5%">
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkSubObjectName',this.checked)"
						id="chkSubObject"></input>
				</td>
				<td align="center" sortfield="ID">
					编号
				</td>
				<td sortfield="name" align="left">
					<b>名称</b>
				</td>
				<td sortfield="info" align="left">
					<b>附带信息</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="chkSubObjectName"
							onclick="$('chkSubObject').checked=false;" value="{ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="ID" />
					</td>
					<td align="left">
						<xsl:value-of select="name" />
					</td>
					<td align="left">
						<xsl:value-of select="info" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>