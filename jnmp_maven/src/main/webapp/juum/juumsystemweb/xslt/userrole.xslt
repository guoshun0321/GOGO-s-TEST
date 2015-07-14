<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width:99.5%;" ID="tabUserRole">     
      <tr>
        <td style="width: 180px;" align="left">
          <b>角色名称</b>          
        </td>        
        <td align="left">
          <b>描述信息</b>        
        </td>
        <td style="width: 90px" align="center" >
          <b>房间拓扑</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>编辑</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>删除</b>
        </td>
      </tr>

      <xsl:for-each select="RecordSet/Record">
        <tr ondblclick="editUserRole({ID});">
          <td align="left">           
            <xsl:value-of disable-output-escaping="yes" select="NAME" />
          </td>          
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="DESCRIPTION" />
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editTopo2RF('{ID}');">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editUserRole('{ID}');">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deleteRole({ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>

  </xsl:template>
</xsl:stylesheet>