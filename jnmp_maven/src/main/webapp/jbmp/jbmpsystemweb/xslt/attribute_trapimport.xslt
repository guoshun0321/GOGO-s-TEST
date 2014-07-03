<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabTrapTable" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkAllTrap',this.checked)"
						id="chkCheckAllTrap"></input>
				</td>
				<td sortfield="TRAP_NAME" width="253px" align="left">
					<b>英文名称</b>
				</td>
				<td sortfield="TRAP_OID" width="253px" align="left">
					<b>OID</b>
				</td>
				<td sortfield="NAME_CN" width="253px" align="left">
					<b>中文名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editTrap('{TRAP_ID}')">
					<td align="center">
						<input type="checkbox" name="chkAllTrap"
							onclick="$('chkCheckAllTrap').checked=false;" value="{TRAP_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="TRAP_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="TRAP_OID" />
					</td>
					<td align="left">
						<xsl:value-of select="NAME_CN" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_TrapCountView"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>