<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabValueDetailTable" border="0" cellspacing="0"
			cellpadding="2" width="98%">
			<tr>
				<td sortfield="VALUE_ID" style="width:85px;" align="left">
					<b>编号</b>
				</td>
				<td sortfield="ATTRIB_VALUE" style="width:150px;" align="left">
					<b>属性值</b>
				</td>
				<td sortfield="VALUE_NAME" style="width:232px;" align="left">
					<b>英文描述</b>
				</td>
				<td sortfield="VALUE_DESC" style="width:232px;" align="left">
					<b>中文描述</b>
				</td>
				<td align="center" style="width:45px;">
					<b>编辑</b>
				</td>
				<td align="center" style="width:45px;">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editEnum('{VALUE_ID}', 1)">
					<td align="left">
						<xsl:value-of select="VALUE_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="ATTRIB_VALUE" />
					</td>
					<td align="left">
						<xsl:value-of select="VALUE_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="VALUE_DESC" />
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editEnum('{VALUE_ID}', 1)">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td>
						<img style="cursor:pointer" title="删除" src="images/drop.gif"
							onclick="delEnum('{VALUE_ID}', 1)" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_EnumDetailCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>