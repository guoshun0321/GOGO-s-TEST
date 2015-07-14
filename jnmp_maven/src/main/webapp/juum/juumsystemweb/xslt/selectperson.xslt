<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="3" style="width: 500px;" ID="tabSelectPerson">
      <tr>
        <td style="width:40px;" align="center">
          <b>选择</b>
        </td>
        <td align="left">
          <b>编号</b>
        </td>
        <td align="left">
          <b>姓名</b>
        </td>
      </tr>
      <xsl:for-each select="DataSource/UUM_PERSON">
        <tr height="22" >
          <td align="center">
            <input type="checkbox" value="{PERSON_ID}" name="chk_SelectPerson">
              <xsl:attribute name ="itemName">
                <xsl:value-of disable-output-escaping="yes" select="NAME" />
              </xsl:attribute>
            </input>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="no" select="PERSON_ID" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="DataSource/UUM_PERSON1">
      <input type="hidden" value="{TotalCount}" id="hid_SPTotalCount"></input>
    </xsl:for-each >
  </xsl:template>
</xsl:stylesheet>