<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width:98%;" ID="tabUser">
      <tr>
        <td style="width: 180px;" align="left">
          <b>单位名称</b>
        </td>
        <td style="width: 100px" align="center">
          <b>类型</b>
        </td>
        <td style="width: 180px" align="left">
          <b>所属区域</b>
        </td>
        <td style="width: 100px" align="center">
          <b>联系电话</b>
        </td>
        <td style="width: 180px" align="left">
          <b>地址</b>
        </td>
        <td style="width: 50px" align="center">
          <b>通过</b>
        </td>
        <td style="width: 50px" align="center" >
          <b>打回</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr >
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="USER_NAME" />
          </td>
          <td align="center">
            <xsl:choose>
              <xsl:when test ="USER_TYPE=10">
                机构
              </xsl:when>
              <xsl:when test ="USER_TYPE=50">
                文化厅
              </xsl:when>
              <xsl:otherwise>
                机构
              </xsl:otherwise>
            </xsl:choose>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="FIELD_3" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="OFFICE_PHONE" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ADDRESS" />
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="approveUser({ID},'1');">
              <img border="0" src="images/commit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="approveUser({ID},'-1');" >
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>

  </xsl:template>
</xsl:stylesheet>