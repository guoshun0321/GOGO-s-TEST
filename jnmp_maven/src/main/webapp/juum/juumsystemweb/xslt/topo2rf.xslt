<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabUserTopo2RF"  border="0" cellspacing="0" cellpadding="2" width="99%" >
      <colgroup>
        <col width="auto"></col>
      </colgroup>
      <tr>
      	<td width="30px" align="center">
			<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkAllObjectChild',this.checked)" id="chkCheckAllChild"></input>
		</td>
        <td sortfield="MAP_NAME">
          <b>拓扑图名称</b>
        </td> 
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr ondblclick="">
		  <td align="center">
			  <input type="checkbox" name="chkAllObjectChild" onclick="$('chkCheckAllChild').checked=false;" value="{MAP_ID}">
			  	<xsl:attribute name ="MapName">
                	<xsl:value-of disable-output-escaping="yes" select="MAP_NAME" />
                </xsl:attribute>
			  </input>
		  </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="MAP_NAME" />
          </td>          
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_Topo2RFCount"></input>
  </xsl:template>
</xsl:stylesheet>
