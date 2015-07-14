<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 97%;" ID="tabSelectAttr">
    <colgroup>
		<col width="205px" align="left"></col>
	</colgroup>
      <tr>
        <td align="left" sortfield="GROUP_NAME">
          <b>资源组</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr onclick="queryObject('{GROUP_ID}', '{GROUP_NAME}')">
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="GROUP_NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AttrCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>