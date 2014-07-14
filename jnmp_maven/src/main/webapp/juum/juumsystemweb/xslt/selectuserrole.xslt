<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 500px;" ID="tabSelectUserRole">
      <tr>
        <td align="center" width="30px">
          <input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chk_SelectUserRole',this.checked)" id="chkCheckAllRole"></input>
        </td>
        <td align="left">
          <b>用户角色</b>
        </td>
        <td align="left">
          <b>描述</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
            <input type="checkbox" value="{ID}" name="chk_SelectUserRole" onclick="$('chkCheckAllRole').checked=false;" >
              <xsl:attribute name ="itemName">
                <xsl:value-of disable-output-escaping="yes" select="NAME" />
              </xsl:attribute>
            </input>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="DESCRIPTION" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>