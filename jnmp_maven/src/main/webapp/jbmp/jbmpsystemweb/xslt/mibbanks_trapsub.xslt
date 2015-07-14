<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabTrapTableSub" border="0" cellspacing="0"
			cellpadding="2" width="99%">
			<tr>
				<td sortfield="TRAP_NAME" align="left" width="180px">
					<b>英文名称</b>
				</td>
				<td sortfield="TRAP_OID" align="left" width="180px">
					<b>OID</b>
				</td>
				<td sortfield="NAME_CN" align="left" width="180px">
					<b>中文名称</b>
				</td>
				<td align="center" width="45px">
					<b>编辑</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editTrapSub('{TRAP_ID}')">
					<td align="left">
						<xsl:value-of select="TRAP_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="TRAP_OID" />
					</td>
					<td align="left">
						<xsl:value-of select="NAME_CN" />
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editTrapSub('{TRAP_ID}')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="delTrapSub('{TRAP_ID}')">
							<img border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_Count"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>