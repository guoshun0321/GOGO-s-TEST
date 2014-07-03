<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAttrTemplate"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td style="width:30px"></td>
				<td align="left" sortfield="TEMPLATE_NAME">
					<b>模板名称</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" >
					 <td align="center">
			    	 	<input type="radio" name="templateRadio" value="{TEMPLATE_ID}@{TEMPLATE_INFO}"></input>
			    	 </td>
					<td>
						<xsl:value-of select="TEMPLATE_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="delAlarmConfigTemplate('{TEMPLATE_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
