<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 500px;" ID="tabSelectSubType">
      <tr>
        <td align="center" width="30px">
          <input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chk_SelectSubType',this.checked)" id="chkCheckAllType"></input>
        </td>
        <td align="left">
          <b>子对象类型</b>
        </td>
        <td align="left">
          <b>描述</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
            <input type="checkbox" value="{CLASS_ID}" name="chk_SelectSubType" onclick="$('chkCheckAllType').checked=false;" >
              <xsl:attribute name ="itemName">
                <xsl:value-of disable-output-escaping="yes" select="CLASS_NAME" />
              </xsl:attribute>
            </input>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="CLASS_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="CLASS_DESC" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>