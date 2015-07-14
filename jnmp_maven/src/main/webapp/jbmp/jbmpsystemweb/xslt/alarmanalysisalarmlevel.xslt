<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectAlarmLevel">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectAlarmLevel',this.checked)"
						id="chkCheckAllSelectAlarmLevel"></input>
		</td>
        <td width="750px" align="left" sortfield="ALARM_LEVEL">
          <b>报警等级</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectAlarmLevel"
					onclick="$('chkCheckAllSelectAlarmLevel').checked=false;" value="{ALARM_LEVEL}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="ALARM_LEVEL" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:choose>
				<xsl:when test="ALARM_LEVEL=0">
					正常
				</xsl:when>
				<xsl:when test="ALARM_LEVEL=10">
					警告报警
				</xsl:when>
				<xsl:when test="ALARM_LEVEL=20">
					一般报警
				</xsl:when>
				<xsl:when test="ALARM_LEVEL=30">
					重要报警
				</xsl:when>
				<xsl:when test="ALARM_LEVEL=40">
					严重报警
				</xsl:when>
				<xsl:when test="ALARM_LEVEL=50">
					离线报警
				</xsl:when>
			</xsl:choose>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_AlarmLevelCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>