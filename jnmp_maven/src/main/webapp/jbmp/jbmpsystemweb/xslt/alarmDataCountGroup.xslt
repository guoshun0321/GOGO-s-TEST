<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 99%;" ID="tabSelectGroup">
      <tr>
       <td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chk_SelectGroup',this.checked)"
						id="chkCheckAllSelectGroup"></input>
		</td>
        <td align="left" sortfield="GROUP_NAME">
          <b>对象组名称</b>
        </td>
        <td align="left" sortfield="GROUP_TYPE">
          <b>对象组类型</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="center">
			<input type="checkbox" name="chk_SelectGroup"
					onclick="$('chkCheckAllSelectGroup').checked=false;" value="{GROUP_ID}">
				<xsl:attribute name ="itemName">
                	<xsl:value-of disable-output-escaping="yes" select="GROUP_NAME" />
                </xsl:attribute>
			</input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="GROUP_NAME" />
          </td>
          <td align="left">
            <xsl:choose>
				<xsl:when test="GROUP_TYPE=1">
					系统
				</xsl:when>
				<xsl:when test="GROUP_TYPE=3">
					采集组
				</xsl:when>
				<xsl:when test="GROUP_TYPE=4">
					网段
				</xsl:when>
				<xsl:when test="GROUP_TYPE=0">
					一般组
				</xsl:when>
			</xsl:choose>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
		<input type="hidden" value="{TotalCount}" id="hid_GroupCount"></input>
	</xsl:for-each>
  </xsl:template>
</xsl:stylesheet>