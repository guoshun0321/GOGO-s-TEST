<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabField" border="0" cellspacing="0" cellpadding="1" width="93%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkField',this.checked)" id="chkFieldCheckAll" checked="true"></input>
				</td>
				<td>
					<b>列名</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr>
					<td align="center">
						<input type="checkbox" name="chkField" onclick="$('chkFieldCheckAll').checked=false;preventEvent(event);" value="{FIELD_NAME}" checked="true"></input>
					</td>
					<td>
						<xsl:value-of select="DISPLAY_NAME"></xsl:value-of>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
