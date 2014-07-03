<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabAttribClass"  border="0" cellspacing="0" cellpadding="2" width="98%" >
      <tr>
        <td sortfield="CLASS_NAME" align="left" width="150">
          <b>名称</b>
        </td>
        <td sortfield="CLASS_TYPE" align="left" width="150">
          <b>别名</b>
        </td>
        <td sortfield="CLASS_LEVEL" align="center">
          <b>类型</b>
        </td>
        <td sortfield="CLASS_GROUP" align="center">
          <b>监控类别</b>
        </td>
        <td width="300px" sortfield="CLASS_DESC" align="left">
          <b>描述</b>
        </td>
        <td align="center" width="75px">
          <b>属性集</b>
        </td>
        <td align="center" width="75px">
          <b>属性</b>
        </td>
		<td style="width: 45px" align="center">
		  <b>标识</b>
		</td>
        <td style="width: 45px" align="center">
          <b>编辑</b>
        </td>
        <td style="width: 45px" align="center">
          <b>删除</b>
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr height="20">
          <td>
            <xsl:value-of select="CLASS_NAME"></xsl:value-of>
          </td>
          <td>
            <xsl:value-of select="CLASS_TYPE"></xsl:value-of>
          </td>
          <td align="center">
            <xsl:choose>
              <xsl:when test="CLASS_LEVEL=0">
                分类
              </xsl:when>
              <xsl:when test="CLASS_LEVEL=1">
                对象
              </xsl:when>
              <xsl:when test="CLASS_LEVEL=2">
               子对象
              </xsl:when>
              <xsl:otherwise>
               对象
              </xsl:otherwise>
            </xsl:choose>
          </td>
          <td align="center">
            <xsl:choose>
              <xsl:when test="CLASS_GROUP=0">
                设备
              </xsl:when>
              <xsl:when test="CLASS_GROUP=10">
                信号
              </xsl:when>
              <xsl:when test="CLASS_GROUP=20">
               节目
              </xsl:when>
              <xsl:otherwise>
               设备
              </xsl:otherwise>
            </xsl:choose>
          </td>
          <td>
            <xsl:value-of select="CLASS_DESC"></xsl:value-of>
          </td>
		  <td align="center">
			<a href="javascript:void(0)" onclick="viewClassSet('{CLASS_ID}','{CLASS_NAME}')">
			  <img border="0" src="images/attribset.gif"/>
			</a>
		  </td>
		  <td align="center">
			<a href="javascript:void(0)" onclick="viewClassSet('{CLASS_ID}','{CLASS_NAME}')">
			  <img border="0" src="images/attribset.gif"/>
			</a>
		  </td>
		  <td align="center">
			<a href="javascript:void(0)" onclick="viewClassIdentify('{CLASS_ID}','{CLASS_NAME}')">
			  <img border="0" src="images/mark.gif"/>
			</a>
		  </td>
		  <td align="center">
			<img style="cursor:pointer" title="编辑" src="images/edit.gif"
				onclick="editClass('{CLASS_ID}');" />
		  </td>
		  <td align="center">
			<img style="cursor:pointer" title="删除" src="images/drop.gif"
				onclick="deleteClass('{CLASS_ID}');" />
		  </td>
        </tr>
      </xsl:for-each>
    </table>
    <xsl:for-each select="RecordSet/Record1">
      <input type="hidden" value="{TotalCount}" id="hid_ClassCount"></input>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
