<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:template match="/">
    <table id="tabUserPicture"  border="0" cellspacing="0" cellpadding="2" width="98%" >
      <colgroup>
        <col width="autopx"></col>
        <col width="80px"></col>
        <col width="80px"></col>
        <col width="45px"></col>
      </colgroup>
      <tr>  
        <td sortfield="PICTURE_NAME">
          <b>图片名称</b>
        </td> 
        <td sortfield="PICTURE_PATH" align="center">
          <b>图片预览</b>
        </td> 
		<td style="width: 80px" align="center">
			<b>设为默认图片</b>
		</td>
        <td align="center" style="width: 60px;">
          <b>删除</b>      
        </td>
      </tr>
      <xsl:for-each select="RecordSet/Record">
        <tr>
          <td align="left">
            <xsl:value-of disable-output-escaping="yes" select="PICTURE_NAME" />
          </td>   
        <td align="center">
				<img border="0" src="../../{PICTURE_PATH}" width="30" height="30"/>
		</td>      
 		<td align="center">
			<a href="javascript:void(0)" onclick="setDefaultPicture('{PICTURE_ID}')">
				<img border="0" src="images/setdefault.gif"/>
			</a>
		</td>                    
          <td align="center">
            <a href="javascript:void(0)" onclick="deletePicture({PICTURE_ID});">
              <img border="0" src="images/drop.gif"/>
            </a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
    <input type="hidden" value="{RecordSet/Record1/TotalCount}" id="hid_PictureCount"></input>
  </xsl:template>
</xsl:stylesheet>
