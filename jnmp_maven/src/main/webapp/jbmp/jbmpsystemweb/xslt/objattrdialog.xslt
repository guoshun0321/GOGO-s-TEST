<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabObjAttrList"  border="0" cellspacing="0" cellpadding="2" width="97%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="checkAllObjAttr(this.checked,'up');" id="checkAllObjAttr"/>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>指标名称</b>
				</td>
				<td sortfield="o.OBJ_NAME" align="left">
					<b>对象名称</b>
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
			    	 	<input type="checkbox" name="checkObjAttr" onclick="$('checkAllObjAttr').checked=false;" value="{OBJATTR_ID}@{OBJATTR_NAME}@{OBJ_NAME}@{ALARM_NAME}@{ALARM_ID}@{COLL_TIMESPAN}"></input>
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
						<img style="cursor:pointer" title="添加" src="images/favorite.gif" onclick="addObjAttr('{OBJATTR_ID}',this,'{OBJ_NAME}','{ALARM_NAME}','{ALARM_ID}','{COLL_TIMESPAN}');" value="{OBJATTR_NAME}"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_objAttrCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
