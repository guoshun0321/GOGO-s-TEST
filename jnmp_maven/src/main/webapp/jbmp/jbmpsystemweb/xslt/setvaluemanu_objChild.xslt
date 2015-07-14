<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="objTableList" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<colgroup>
				<col width="100px" align="left"></col>
				<col width="180px" align="left"></col>
				<col></col>
				<col></col>
				<col width="80px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td  align="left">
					<b>编号</b>
				</td>
				<td  align="left">
					<b>对象名称</b>
				</td>
				<td  align="left">
					<b>IP地址</b>
				</td>
				<td  align="left">
					<b>类别</b>
				</td>
				<td  align="center">
					<b>对象状态</b>
				</td>
				<td align="center" width="45px">
					<b>设置</b>
				</td>

			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editElement('{OBJ_ID}','{CLASS_ID}','{CLASS_TYPE}')">
					<td align="left">
						<xsl:value-of select="OBJ_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="IP_ADDR" />
					</td>
					<td align="left">
						<xsl:value-of select="CLASS_NAME" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="OBJ_STATE=0">
								管理
							</xsl:when>
							<xsl:when test="OBJ_STATE=1">
								维护
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="assign('{OBJ_ID}')">
							<img border="0" src="images/attrib.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCountChild"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>