<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabSysconfig"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td sortfield="NAME" align="left">
					<b>参数名称</b>
				</td>
				<td sortfield="DATA" align="left">
					<b>参数值</b>
				</td>
				<td sortfield="DESCRIPTION" align="left">
					<b>参数描述</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editSysconfig('{NAME}')">
					<td align="left">
						<xsl:value-of select="NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="DATA"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="DESCRIPTION"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editSysconfig('{NAME}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteSysconfig('{NAME}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_SysconfigCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
