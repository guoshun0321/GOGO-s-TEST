<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabChosenObjAttr"  border="0" cellspacing="0" cellpadding="2" width="97%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="checkAllObjAttr(this.checked,'down');" id="checkAllChosenObjAttr"/>
				</td>
				<td align="left"  style="width: 240px">
					<b>指标名称</b>
				</td>
				<td align="left"  style="width: 250px">
					<b>对象名称</b>
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
			    	 	<input type="checkbox" name="checkChosenObjAttr" onclick="$('checkAllChosenObjAttr').checked=false;" value="{OBJATTR_ID}"></input>
			    	 </td>
					<td>
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>	
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>			
					<td>
						<xsl:value-of select="COLL_TIMESPAN"></xsl:value-of>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="delObjAttr('{OBJATTR_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
