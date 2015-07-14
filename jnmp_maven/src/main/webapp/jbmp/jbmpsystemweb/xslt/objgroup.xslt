<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabObjGroup"  border="0" cellspacing="0" cellpadding="2" width="100%" >
      <tr>
        <td sortfield="GROUP_NAME" align="left">
          <b>名称</b>
        </td>
        <td sortfield="GROUP_TYPE" align="center">
          <b>类型</b>
        </td>
        <td sortfield="GROUP_CODE" align="left">
          <b>编码</b>
        </td>
        <td width="400px" sortfield="GROUP_DESC" align="left">
          <b>描述</b>
        </td>
        <td width="140px" sortfield="CREATE_TIME" align="center">
          <b>创建时间</b>
        </td>       
        <td style="width: 45px" align="center">
          <b>编辑</b>
        </td>
        <td style="width: 45px" align="center">
          <b>删除</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr height="20" ondblclick="editObjGroup('{GROUP_ID}');">
          <td align="left">
            <xsl:value-of select="GROUP_NAME"></xsl:value-of>
          </td>
          <td align="center">
            <xsl:choose>
              <xsl:when test="GROUP_TYPE=1">
                系统
              </xsl:when>
              <xsl:when test="GROUP_TYPE=2">
                设备组
              </xsl:when>
              <xsl:when test="GROUP_TYPE=3">
                采集组
              </xsl:when>
              <xsl:when test="GROUP_TYPE=4">
                网段
              </xsl:when>
              <xsl:otherwise>
                一般组
              </xsl:otherwise>
            </xsl:choose>
          </td>
          <td align="left">
            <xsl:value-of select="GROUP_CODE"></xsl:value-of>
          </td>
          <td align="left">
            <xsl:value-of select="GROUP_DESC"></xsl:value-of>
          </td>
          <td align="center">
            <xsl:value-of select="translate(substring(CREATE_TIME,0,20),'T',' ')"></xsl:value-of>
          </td>
		  <td align="center">
			<img style="cursor:pointer" title="编辑" src="images/edit.gif"
				onclick="editObjGroup('{GROUP_ID}');" />
		  </td>
		  <td align="center">
			<img style="cursor:pointer" title="删除" src="images/drop.gif"
				onclick="deleteObjGroup('{GROUP_ID}');" />
		  </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
      <input type="hidden" value="{TotalCount}" id="hid_ObjGroupCount"></input>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
