<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabKnowledgeType"  border="0" cellspacing="0" cellpadding="2" width="96%" >
			<colgroup>
				<col width="30px"></col>
				<col width="45px"></col>
				<col width="150px"></col>
				<col width="auto"></col>
			</colgroup>
			<tr>
				<td align="center" width="30px">
		         	<input id="chkCheckAllKnowledgeType" type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkKnowledgeType',this.checked)"></input>
		        </td>
				<td sortfield="TYPE_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="TYPE_NAME" align="left">
					<b>类别名称</b>
				</td>
				<td sortfield="TYPE_DESC" align="left">
					<b>类别描述</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
		            	<input type="checkbox" name="chkKnowledgeType" onclick="$('chkCheckAllKnowledgeType').checked=false;preventEvent(event);" value="{TYPE_ID}"></input>
		            </td>
					<td>
						<xsl:value-of select="TYPE_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TYPE_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="TYPE_DESC"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_KnowledgeTypeCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>