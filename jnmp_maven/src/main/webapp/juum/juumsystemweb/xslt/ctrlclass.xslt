<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="/">
		<table  border="0" cellspacing="0" cellpadding="1" width="98%" class="webfx-columnlist-body"  ID="tabWorkTb">
			<tr class="webfx-columnlist-head">
				<td width="60px" align="left">
					<b>编号</b>
				</td>
				<td width="60px" align="left">
					<b>父编号</b>
				</td>
				<td width="100px" align="center">
					<b>类别</b>
				</td>
				<td style="min-width:100px" align="left">
					<b>名称</b>
				</td>
				<!--<td width="80px" align="center">
					<b>层次关系</b>
				</td>-->
				<td style="min-width:100px" align="left">
					<b>显示数据</b>
				</td>
				<td style="min-width:100px" align="left">
					<b>描述</b>
				</td>
				<td align="center" width="50px">
					<b>编辑</b>
				</td>
				<td align="center" width="50px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr  height="20" id="tabWorkTbrow_{position()}" onDblClick="editControlClass('{CLASS_ID}')" >
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CLASS_ID" />
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="PARENT_ID" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test ='CLASS_TYPE = 100'>机构行政划分</xsl:when>
							<xsl:when test ='CLASS_TYPE = 101'>机构性质</xsl:when>
						</xsl:choose>
					</td>
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CLASS_NAME" />
					</td>
					<!--<td >
						<xsl:value-of disable-output-escaping="yes" select="CLASS_LAYER" />
					</td>-->
					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="VIEW_NAME" />
					</td>

					<td align="left">
						<xsl:value-of disable-output-escaping="yes" select="CLASS_DESC" />
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editControlClass('{CLASS_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="deleteControlClass('{CLASS_ID}','{PARENT_ID}');">
							<img border="0" src="images/drop.gif"/>
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_rowCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>