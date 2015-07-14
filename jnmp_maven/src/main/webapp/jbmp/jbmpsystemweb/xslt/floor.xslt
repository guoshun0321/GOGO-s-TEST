<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabUserFloor"  border="0" cellspacing="0" cellpadding="2" width="99%" >
      <colgroup>
        <col width="auto"></col>
        <col width="auto"></col>
        <col width="auto"></col>
        <col width="auto"></col>
        <col width="45px"></col>
        <col width="45px"></col>
        <col width="45px"></col>
      </colgroup>
      <tr>  
        <td sortfield="FLOOR_NAME">
          <b>楼层名称</b>
        </td> 
        <td sortfield="FLOOR_ALIAS">
          <b>楼层别名</b>
        </td> 
        <td sortfield="FLOOR_ATTACH">
          <b>所在建筑物</b>
        </td> 
        <td sortfield="FLOOR_NUM">
          <b>楼层</b>
        </td> 
        <td align="center" style="width: 45px;">
          <b>房间</b>
        </td>
        <td align="center" style="width: 45px;">
          <b>编辑</b>
        </td>
        <td align="center" style="width: 45px;">
          <b>删除</b>      
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr ondblclick="editFloor({FLOOR_ID});">
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="FLOOR_NAME" />
          </td>          
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="FLOOR_ALIAS" />
          </td>          
          <td align="left">
            <xsl:choose>
				<xsl:when test="FLOOR_ATTACH=0">
					主楼
				</xsl:when>
				<xsl:when test="FLOOR_ATTACH=1">
					裙楼
				</xsl:when>
			</xsl:choose>
          </td>          
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="FLOOR_NUM" />
          </td>          
           
          <td align="center">
            <a href="javascript:void(0)" onclick="showRoom({FLOOR_ID})">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editFloor({FLOOR_ID});">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deleteFloor({FLOOR_ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_FloorCount"></input>
  </xsl:template>
</xsl:stylesheet>
