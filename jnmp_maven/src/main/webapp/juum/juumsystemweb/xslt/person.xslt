<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
    <table cellspacing="0" cellpadding="1" style="width: 100%;" ID="tabPerson">
      <tr>
        <td align="center" width="30px">
          <input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkPerson',this.checked)" id="chkCheckAllPerson"></input>
        </td>
        <td width="80px" align="left">
          <b>姓名</b>
        </td>
        <td width="80px" align="left">
          <b>代号</b>
        </td>
        <td width="50px" align="center">
          <b>性别</b>
        </td>
        <td width="120px" align="center">
          <b>职务</b>
        </td>
        <td width="120px" align="left">
          <b>办公电话</b>
        </td>
        <td width="80px" align="left">
          <b>入职日期</b>
        </td>
        <td align="left">
          <b>邮箱</b>
        </td>
        <td width="60px" align="center">
          <b>状态</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>编辑</b>
        </td>
        <td style="width: 45px" align="center" >
          <b>删除</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr ondblclick="editPerson('{ID}');">
          <td align="center">
            <input type="checkbox" name="chkPerson" onclick="$('chkCheckAllPerson').checked=false;" value="{ID}"></input>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="NAME" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="USER_CODE" />
          </td>
          <td align="center">
            <xsl:if test ="SEX=0">
              男
            </xsl:if>
            <xsl:if test ="SEX=1">
              女
            </xsl:if>
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="DUTY_TITLE" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="OFFICE_PHONE" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="substring(JOIN_DATE,1,10)" />
          </td>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="EMAIL" />
          </td>
          <td>
            <xsl:if test ="STATE=0">
              活动
            </xsl:if>
            <xsl:if test ="STATE=1">
              冻结
            </xsl:if>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editPerson('{ID}');">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deletePerson({ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_PersonCount"></input>
  </xsl:template>
</xsl:stylesheet>