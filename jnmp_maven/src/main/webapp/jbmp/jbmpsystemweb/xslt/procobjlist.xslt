<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
>
  <xsl:template match="/">
    <table id="procobjtable" border="0" cellspacing="0" cellpadding="2" width="99.5%">
      <tr>
        <td sortfield="OBJ_ID" align="left" width="80px">
          <b>编号</b>
        </td>
        <td sortfield="OBJ_NAME" align="left">
          <b>对象名称</b>
        </td>
        <td sortfield="IP_ADDR" align="left">
          <b>IP地址</b>
        </td>
        <td sortfield="CLASS_TYPE" align="left">
          <b>类别</b>
        </td>
        <td sortfield="OBJ_STATE" align="center">
          <b>对象状态</b>
        </td>
        <td align="center" width="100px">
          <b>对象属性</b>
        </td>
        <td align="center" width="45px">
          <b>编辑</b>
        </td>
        <td align="center" width="45px">
          <b>删除</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr height="20" ondblclick="editSubElement('{OBJ_ID}', '{CLASS_ID}', '{CLASS_TYPE}')">
          <td align="left">
            <xsl:value-of select="OBJ_ID"/>
          </td>
          <td align="left">
            <xsl:value-of select="OBJ_NAME"/>
          </td>
          <td align="left">
            <xsl:value-of select="IP_ADDR"/>
          </td>
          <td align="left">
            <xsl:value-of select="CLASS_NAME"/>
          </td>
          <td align="center">
            <xsl:choose>
              <xsl:when test="OBJ_STATE=0">管理</xsl:when>
              <xsl:when test="OBJ_STATE=1">维护</xsl:when>
            </xsl:choose>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="getAttribClass('{CLASS_ID}', false);edirObjectAttrib('{OBJ_ID}','{OBJ_NAME}','{CLASS_ID}')">
              <img border="0" src="images/window.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editSubElement('{OBJ_ID}', '{CLASS_ID}', '{CLASS_TYPE}')">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
          	<a href="javascript:void(0)" onclick="deleteSubElement('{OBJ_ID}')">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
      <input type="hidden" value="{TotalCount}" id="hidCount"></input>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>