<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectAlarmRule">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectAlarmRule',this.checked)"
						id="chkCheckAllSelectAlarmRule"></input>
		</td>
        <td align="left" sortfield="ALARM_NAME">
          <b>报警规则</b>
        </td>
        <td align="left" sortfield="ALARM_DESC">
          <b>描述</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectAlarmRule"
					onclick="$('chkCheckAllSelectAlarmRule').checked=false;" value="{ALARM_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="ALARM_NAME" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ALARM_NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ALARM_DESC" />
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AlarmRuleCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>