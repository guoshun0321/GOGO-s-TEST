<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table id="tabMibBank" cellspacing="0" cellpadding="2" style="width:99.5%;">
			<tr>
				<td sortfield="MIB_NAME" style="width:200px" align="left">
					<b>MIB库名称</b>
				</td>
				<td sortfield="MIB_ALIAS" style="width:200px" align="left">
					<b>别名</b>
				</td>
				<td sortfield="MIB_DESC" style="width:auto" align="left">
					<b>描述</b>
				</td>
				<td style="width: 45px" align="center">
					<b>文件</b>
				</td>
				<td style="width: 45px" align="center">
					<b>详细</b>
				</td>
				<td style="width: 45px" align="center">
					<b>Trap</b>
				</td>
				<td style="width: 45px" align="center">
					<b>枚举</b>
				</td>
				<td style="width: 45px" align="center">
					<b>导出</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr ondblclick="editMan('{MIB_ID}')">
					<td>
						<xsl:value-of select="MIB_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="MIB_ALIAS"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="MIB_DESC"></xsl:value-of>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="fileList('{MIB_ID}')">
							<img border="0" src="images/file.gif" />
						</a>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="mibDetails('{MIB_ID}', '{MIB_ALIAS}')">
							<img border="0" src="images/window.gif" />
						</a>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="mibTrap('{MIB_ID}', '{MIB_NAME}', '{MIB_ALIAS}')">
							<img border="0" src="images/trap.gif" />
						</a>
					</td>
					<td>
						<a href="javascript:void(0)" onclick="mibEnum('{MIB_ID}', '{MIB_ALIAS}')">
							<img border="0" src="images/meiju.gif" />
						</a>
					</td>
					<td>
						<img style="cursor:pointer" title="导出" src="images/export.gif"
							onclick="exportXml('{MIB_ID}', '{MIB_NAME}')" />
					</td>
					<td>
						<a href="javascript:void(0)" onclick="editMan('{MIB_ID}')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td>
						<img style="cursor:pointer" title="删除" src="images/drop.gif"
							onclick="del('{MIB_ID}')" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidListCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
