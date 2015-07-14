<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabAttrList"  border="0" cellspacing="0" cellpadding="2" width="97%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="checkAllAttr(this.checked,'up');" id="checkAllAttr"/>
				</td>
				<td sortfield="ATTRIB_NAME" align="left">
					<b>属性名称</b>
				</td>
				<td sortfield="bc.CLASS_NAME" align="left">
					<b>资源类型</b>
				</td>
				<td sortfield="ALARM_NAME" align="left">
					<b>规则名称</b>
				</td>
				<td sortfield="COLL_TIMESPAN" align="center">
					<b>采集间隔</b>
				</td>
				<td style="width: 45px" align="center">
					<b>添加</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" >
					 <td align="center">
			    	 	<input type="checkbox" name="checkAttr" onclick="$('checkAllAttr').checked=false;" value="{ATTRIB_ID}@{ATTRIB_NAME}@{CLASS_NAME}@{ALARM_NAME}@{ALARM_ID}@{COLL_TIMESPAN}"></input>
			    	 </td>
					<td>
						<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="CLASS_NAME"></xsl:value-of>
					</td>	
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>			
					<td>
						<xsl:value-of select="COLL_TIMESPAN"></xsl:value-of>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="添加" src="images/favorite.gif" onclick="addAttr('{ATTRIB_ID}','{ATTRIB_NAME}','{CLASS_NAME}','{ALARM_NAME}','{ALARM_ID}','{COLL_TIMESPAN}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AttrCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
