<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectUser">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectUser',this.checked)"
						id="chkCheckAllSelectUser"></input>
		</td>
        <td align="left" sortfield="USER_NAME">
          <b>用户姓名</b>
        </td>
        <td align="left" sortfield="LOGIN_NAME">
          <b>登录名称</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectUser"
					onclick="$('chkCheckAllSelectUser').checked=false;" value="{ID}">
					<xsl:attribute name ="itemName">
                <xsl:value-of disable-output-escaping="yes" select="LOGIN_NAME" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="USER_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="LOGIN_NAME" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_SUTotalCount"></input>
  </xsl:template>
</xsl:stylesheet>