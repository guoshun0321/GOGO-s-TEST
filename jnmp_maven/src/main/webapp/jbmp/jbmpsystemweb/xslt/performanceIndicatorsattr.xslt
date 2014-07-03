<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 97%;" ID="tabSelectAttr">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectAttr',this.checked)"
						id="chkCheckAllSelectAttr"></input>
		</td>
        <td align="left" sortfield="ATTRIB_NAME">
          <b>性能指标名称</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectAttr"
					onclick="$('chkCheckAllSelectAttr').checked=false;" value="{ATTRIB_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="ATTRIB_NAME" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ATTRIB_NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AttrCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>