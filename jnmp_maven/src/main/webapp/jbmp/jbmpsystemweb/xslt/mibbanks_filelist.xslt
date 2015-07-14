<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabMibFileList" border="0" cellspacing="0" cellpadding="2"
			width="99%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col></col>
				<col width="100px"></col>
				<col width="45px" align="center"></col>
			</colgroup>	
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkAllObject',this.checked)"
						id="chkCheckAllAttribute"></input>
				</td>
				<td width="590px">
					<b>文件名</b>
				</td>
				<td width="120px">
					<b>大小（字节）</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="chkAllObject"
							onclick="$('chkCheckAllAttribute').checked=false;" value="{FILE_NAME}"></input>
					</td>
					<td>
						<xsl:value-of select="FILE_NAME" />
					</td>
					<td>
						<xsl:value-of select="FILE_SIZE" />
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="delMibFile('{FILE_NAME}')">
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