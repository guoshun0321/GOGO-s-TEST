<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectAttr">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectAttr',this.checked)"
						id="chkCheckAllSelectAttr"></input>
		</td>
        <td align="left" sortfield="OBJATTR_NAME">
          <b>对象属性名称</b>
        </td>
        <td align="left" sortfield="OBJ_NAME">
          <b>所属对象名称</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectAttr"
					onclick="$('chkCheckAllSelectAttr').checked=false;" value="{OBJATTR_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="OBJATTR_NAME" />
                </xsl:attribute>
				<xsl:attribute name ="fatherID">
                	<xsl:value-of disable-output-escaping="yes" select="OBJ_ID" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="OBJATTR_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="OBJ_NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AttrCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>