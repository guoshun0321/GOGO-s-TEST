<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAttribType" border="0" cellspacing="0"
			cellpadding="2" width="100%">
			<colgroup>
				<col width="45px" align="left"></col>
				<col width="150px" align="left"></col>
				<col align="left"></col>
				<col width="45px" align="center"></col>
				<col width="45px" align="center"></col>
			</colgroup>
			<tr>
				<td sortfield="TYPE_ID" width="45px" align="left">
					<b>编号</b>
				</td>
				<td sortfield="TYPE_NAME" width="150px" align="left">
					<b>名称</b>
				</td>
				<td sortfield="TYPE_DESC" align="left">
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
				<tr height="20" ondblclick="editType('{TYPE_ID}');">
					<td>
						<xsl:value-of select="TYPE_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TYPE_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TYPE_DESC"></xsl:value-of>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="编辑" src="images/edit.gif"
							onclick="editType('{TYPE_ID}');" />
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif"
							onclick="deleteType('{TYPE_ID}');" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_TypeCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
