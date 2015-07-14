<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabTrapTable" border="0" cellspacing="0" cellpadding="2"
			width="97%">
			<tr>
				<td sortfield="TRAP_NAME" align="left" width="200px">
					<b>英文名称</b>
				</td>
				<td sortfield="TRAP_OID" align="left" width="200px">
					<b>OID</b>
				</td>
				<td sortfield="NAME_CN" align="left"  width="210px">
					<b>中文名称</b>
				</td>
				<td align="center" width="75px">
					<b>子节点</b>
				</td>
				<td align="center" width="45px">
					<b>编辑</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editTrap('{TRAP_ID}')">
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
						<a href="javascript:void(0)" onclick="showSub('{TRAP_NAME}', '{TRAP_ID}')">
							<img border="0" src="images/sub.gif" />
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editTrap('{TRAP_ID}')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="delTrap('{TRAP_ID}')">
							<img border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_TrapCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>