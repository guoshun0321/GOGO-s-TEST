<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width:100%;" ID="tabUserGroup">     
      <tr>
        <td width="150px" align="left">
          <b>分组名称</b>
        </td>
        <td width="80px" align="center">
          <b>分组类型</b>
        </td>
        <td width="150px" align="left">
          <b>分组代号</b>
        </td>        
        <td align="left">
          <b>描述信息</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>编辑</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>删除</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="left">            
              <xsl:value-of disable-output-escaping="no" select="NAME" />
          </td>
          <td align="center">
            <xsl:if test ="TYPE=0">部门</xsl:if>
            <xsl:if test ="TYPE=1">栏目</xsl:if>
            <xsl:if test ="TYPE=2">分组</xsl:if>
            <xsl:if test ="TYPE=3">频道</xsl:if>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="GROUP_CODE" />
          </td>          
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="DESCRIPTION" />
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editUserGroup('{ID}','{PARENT_ID}');">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deleteGroup({ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>