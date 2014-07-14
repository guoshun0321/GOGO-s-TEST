<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabUserRoom"  border="0" cellspacing="0" cellpadding="2" width="99%" >
      <colgroup>
        <col width="auto"></col>
        <col width="auto"></col>
        <col width="45px"></col>
        <col width="45px"></col>
        <col width="45px"></col>
      </colgroup>
      <tr>  
        <td sortfield="ROOM_NAME">
          <b>房间名</b>
        </td> 
        <td sortfield="ROOM_ALIAS">
          <b>房间别名</b>
        </td> 
        <td align="center" style="width: 45px;">
          <b>拓扑图</b>
        </td>
        <td align="center" style="width: 45px;">
          <b>编辑</b>
        </td>
        <td align="center" style="width: 45px;">
          <b>删除</b>      
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr ondblclick="editRoom({ROOM_ID});">
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ROOM_NAME" />
          </td>          
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="ROOM_ALIAS" />
          </td>          
           
          <td align="center">
            <a href="javascript:void(0)" onclick="showTopo({ROOM_ID}, 0);">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="editRoom({ROOM_ID});">
              <img border="0" src="images/edit.gif"/>
            </a>
          </td>
          <td align="center">
            <a href="javascript:void(0)" onclick="deleteRoom({ROOM_ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_RoomCount"></input>
  </xsl:template>
</xsl:stylesheet>
