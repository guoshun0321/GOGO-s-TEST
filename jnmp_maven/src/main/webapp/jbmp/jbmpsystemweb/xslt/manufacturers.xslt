<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabMan"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td sortfield="MAN_NAME" align="left">
					<b>厂商名称</b>
				</td>
				<td sortfield="MAN_DESC" align="left">
					<b>描述</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editMan('{MAN_ID}')">
					<td align="left">
						<xsl:value-of select="MAN_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="MAN_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editMan('{MAN_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteMan('{MAN_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ManCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
