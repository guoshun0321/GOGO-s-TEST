<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table cellspacing="0" cellpadding="2" style="width: 99%" ID="tabFunction">
			<tr>
				<td align="center" width="30px">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkFunction',this.checked)"
						id="chkCheckAllGroup"></input>
				</td>
				<td style="width:80px;" align="left">
					<b>编号</b>
				</td>
				<td align="left">
					<b>名称</b>
				</td>
				<td style="width:100px;" align="center">
					<b>类型</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr>
					<td align="center">
						<input type="checkbox" name="chkFunction"
							onclick="$('chkCheckAllGroup').checked=false;groupSelChange(this, '{GROUP_ID}');"
							value="{GROUP_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="no" select="GROUP_ID" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="no" select="GROUP_NAME" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="GROUP_TYPE=0">
								一般组
							</xsl:when>
							<xsl:when test="GROUP_TYPE=1">
								系统
							</xsl:when>
							<xsl:when test="GROUP_TYPE=3">
								采集组
							</xsl:when>
							<xsl:when test="GROUP_TYPE=4">
								网段
							</xsl:when>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>