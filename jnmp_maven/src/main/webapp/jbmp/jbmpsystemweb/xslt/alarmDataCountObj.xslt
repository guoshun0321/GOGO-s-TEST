<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectObj">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectObj',this.checked)"
						id="chkCheckAllSelectObj"></input>
		</td>
        <td align="left" sortfield="OBJ_NAME">
          <b>对象名称</b>
        </td>
        <td align="left" sortfield="GROUP_NAME">
          <b>所属组名称</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectObj"
					onclick="$('chkCheckAllSelectObj').checked=false;" value="{OBJ_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="OBJ_NAME" />
                </xsl:attribute>
				<xsl:attribute name ="fatherID">
                	<xsl:value-of disable-output-escaping="yes" select="GROUP_ID" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="OBJ_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="GROUP_NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_ObjCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>