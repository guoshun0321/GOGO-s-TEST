<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
>
  <xsl:template match="/">
    <table id="autoDiscTable"  border="0" cellspacing="0" cellpadding="2" width="99%" >
      <tr>
      	<td align="center" width="30px">
          <input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkMsgLog',this.checked)" id="chkCheckAllObject2"></input>
        </td>
        <td sortfield="OBJ_NAME" align="left">
          	对象名称
        </td>
        <td sortfield="IP" align="left">
			IP地址
        </td>
        <td sortfield="CLASS_NAME" align="left">
			类别
        </td>
        <td sortfield="COLL_NAME" align="left">
			采集器
        </td>
        <td align="center" sortfield="OBJ_ID">
          	对象是否存在
        </td>
        <td align="center" sortfield="OBJ_STATUS">
          	对象状态
        </td>
        <td align="center" width="45px">
        	新建
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr height="20">
          <td align="center">
         	<xsl:choose>
         		<xsl:when test="CLASS_ID!=99999">
         			<input type="checkbox" name="chkMsgLog" onclick="$('chkCheckAllObject2').checked=false;" value="{OBJ_ID}">
	         			<xsl:attribute name ="itemIp">
	                		<xsl:value-of disable-output-escaping="yes" select="IP" />
	                	</xsl:attribute>
	                	<xsl:attribute name ="itemClassName">
	                		<xsl:value-of disable-output-escaping="yes" select="CLASS_NAME" />
	                	</xsl:attribute>
         			</input>
         		</xsl:when>
         	</xsl:choose>
          </td>
          <td align="left">
          	<xsl:value-of select="OBJ_NAME"/>
          </td>
          <td align="left">
            <xsl:value-of select="IP"/>
          </td>
          <td align="left">
            <xsl:value-of select="CLASS_NAME"/>
          </td>
          <td align="left">
            <xsl:value-of select="COLL_NAME"/>
          </td>
          <td align="center">
          	<xsl:choose>
          		<xsl:when test="IDNUM = ''">
          			
          		</xsl:when>
          		<xsl:otherwise>
          			已创建
          		</xsl:otherwise>
          	</xsl:choose>
          </td>
          <td align="center">
          	<xsl:choose>
          		<xsl:when test="OBJ_STATUS=0">
          			可用
          		</xsl:when>
          		<xsl:when test="OBJ_STATUS=1">
          			上一次新增
          		</xsl:when>
          		<xsl:when test="OBJ_STATUS=2">
          			上一次删除
          		</xsl:when>
          		<xsl:when test="OBJ_STATUS=3">
          			上一次更改
          		</xsl:when>
          		<xsl:when test="OBJ_STATUS=4">
          			不可用
          		</xsl:when>
          	</xsl:choose>
          </td>
          <td align="center">
   			<a href="javascript:void(0)" onclick="newObjectElement('{OBJ_ID}', '{COLL_ID}','{CLASS_ID}')">
       			<img border="0" src="images/new.gif"/>
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
