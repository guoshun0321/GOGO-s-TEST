<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabChosenAttrList"  border="0" cellspacing="0" cellpadding="2" width="97%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="checkAllAttr(this.checked,'down');" id="checkAllChosenAttr"/>
				</td>
				<td align="left"  style="width: 240px">
					<b>属性名称</b>
				</td>
				<td align="left"  style="width: 250px">
					<b>资源类型</b>
				</td>
				<td align="left"  style="width: 240px">
					<b>规则名称</b>
				</td>
				<td align="center"  style="width: 140px">
					<b>采集间隔</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" >
					 <td align="center">
			    	 	<input type="checkbox" name="checkChosenAttr" onclick="$('checkAllChosenAttr').checked=false;" value="{ATTRIB_ID}"></input>
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
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="delAttr('{ATTRIB_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_AttrCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
