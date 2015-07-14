<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectAlarmType">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectAlarmType',this.checked)"
						id="chkCheckAllSelectAlarmType"></input>
		</td>
        <td align="left" sortfield="TYPE_NAME">
          <b>报警类型</b>
        </td>
        <td align="left" sortfield="TYPE_DESC">
          <b>描述</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectAlarmType"
					onclick="$('chkCheckAllSelectAlarmType').checked=false;" value="{TYPE_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="TYPE_NAME" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="TYPE_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="TYPE_DESC" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AlarmTypeCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>