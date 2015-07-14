<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table  id="tabCtrlword" border="0" cellspacing="0" cellpadding="1" width="100%" class="webfx-columnlist-body">
			<tr height="20">
				<td width="100px" align="left">
					<b>编号</b>
				</td>
				<td align="left">
					<b>名称</b>
				</td>
				<td align="left">
					<b>描述</b>
				</td>
				<td align="center" width="50px">
					<b>编辑</b>
				</td>
				<td align="center" width="50px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr  height="20"  onDblClick="editCtrlword('{CW_ID}')" >
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CW_ID" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CW_NAME" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CW_DESC" />
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editCtrlword('{CW_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="deleteCtrlword('{CW_ID}');">
							<img border="0" src="images/drop.gif"/>
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>