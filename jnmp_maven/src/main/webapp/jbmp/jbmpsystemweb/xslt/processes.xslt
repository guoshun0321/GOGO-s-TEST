<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
>
  <xsl:template match="/">
    <table id="procListTable"  border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr>
      	<td align="center" width="45px">
          选择
        </td>
        <td align="left">
          进程名称
        </td>
        <td align="left">
          CPU时间
        </td>
        <td align="left">
          占用内存
        </td>
        <td align="left">
          路径
        </td>
        <td align="left">
          运行参数
        </td>
        
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr height="20">
          <td align="center">
            <xsl:if test="ISIN=0">
              <input type="checkbox" onchange="proSelChange(this,'{NAME}');" />
            </xsl:if>
          </td>
          <td align="left">
            <xsl:value-of select="NAME"/>
          </td>
          <td align="left">
            <xsl:value-of select="CPU"/>
          </td>
          <td align="left">
            <xsl:value-of select="MEM"/>
          </td> 
          <td align="left">
            <xsl:value-of select="PATH"/>
          </td>
          <td align="left">
            <xsl:value-of select="PARAM"/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>
