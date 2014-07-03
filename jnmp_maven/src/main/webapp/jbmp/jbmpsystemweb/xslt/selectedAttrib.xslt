<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="attrbTableList" border="0" cellspacing="0" cellpadding="2"
			width="99.5%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col width="350px"></col>
				<col width="350px"></col>
				<col width="80px"></col>
				<col width="45px" aligen="center"></col>
			</colgroup>
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkAllObject',this.checked)"
						id="chkCheckAll"></input>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>资源</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>指标</b>
				</td>
				<td sortfield="CHARTID" align="left">
					<b>所属图表</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" width="40px"
					align="center">
					<td align="center">
						<input type="checkbox" name="chkAllObject"
							onclick="$('chkCheckAll').checked=false;" value="{OBJATTR_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="CHARTID" />
					</td>
					<td align="center" width="35px">
						<a hreg="javascript:void(0)" onclick="delAttrb('{OBJATTR_ID}')">
							<img style="cursor:pointer" border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>