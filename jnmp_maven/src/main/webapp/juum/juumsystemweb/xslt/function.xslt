<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 100%" ID="tabFunction">
      <tr>
        <td align="center" width="30px">
          <input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkFunction',this.checked)" id="chkCheckAllFunction"></input>
        </td>
        <td style="width:80px;" align="left" sortfield="ID">
          <b>权限编号</b>
        </td>
        <td style="width:160px;" align="left" sortfield="NAME">
          <b>名称</b>
        </td>       
        <td style="width:60px;" align="center" sortfield="STATE">
          <b>状态</b>
        </td>
        <td sortfield="PARAM">
          <b>参数</b>
        </td>
        <td style="width:150px;" align="left" >
          <b>描述</b>
        </td>
        <td style="width:65px;" align="center" sortfield="VIEW_POS">
          <b>排序号</b>
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
          <td align="center">
            <input type="checkbox" name="chkFunction" onclick="$('chkCheckAllFunction').checked=false;" value="{ID}"></input>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="no" select="ID" />
          </td>
          <td align="left">
            <a href="javascript:void(0)" style="text-decoration: underline;" onclick="getFunctionsByParentId({ID})">
              <xsl:value-of disable-output-escaping="no" select="NAME" />
            </a >
          </td>
          <td align="center">
            <xsl:if test ="STATE=1">
              禁用
            </xsl:if>
            <xsl:if test ="STATE=0">
              启用
            </xsl:if>
          </td>
          <td align="left">
            <div class="cell-overflow-hidden">
              <xsl:value-of disable-output-escaping="no" select="PARAM" />
            </div>
          </td>
          <td align="left">
            <div class="cell-overflow-hidden">
              <xsl:value-of disable-output-escaping="no" select="DESCRIPTION" />
            </div>
          </td>
          <td align="center">
            <div class="cell-overflow-hidden">
              <xsl:value-of disable-output-escaping="no" select="VIEW_POS" />
            </div>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editFunction({ID});">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deleteFunction({ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_TotalCount"></input>
  </xsl:template>
</xsl:stylesheet>