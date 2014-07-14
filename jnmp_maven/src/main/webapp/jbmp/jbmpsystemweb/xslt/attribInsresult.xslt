<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="InstanceResultTable" border="0" cellspacing="0"
			cellpadding="2" width="98.5%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col></col>
				<col></col>
				<col width="300px"></col>
			</colgroup>
			<tr>
				<td align="center" width="30px">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('checkInstanceResult',this.checked)"
						id="chkCheckAllAttribInsresult"></input>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>名称</b>
				</td>
				<td sortfield="ATTRIB_VALUE" align="left">
					<b>值</b>
				</td>
				<td sortfield="ATTRIB_PARAM" align="left" width="300px">
					<b>参数</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editElement('{OBJ_ID}')">
					<td align="center">
						<input type="checkbox" name="checkInstanceResult"
							onclick="$('chkCheckAllAttribInsresult').checked=false;" value="{OBJATTR_ID}"></input>
					</td>
					<td>
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="ATTRIB_VALUE='null'">
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="ATTRIB_VALUE" />
							</xsl:otherwise>
						</xsl:choose>				
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="ATTRIB_PARAM='null'">
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="ATTRIB_PARAM" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>