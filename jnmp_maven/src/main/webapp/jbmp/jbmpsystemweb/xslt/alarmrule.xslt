<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabMainRecord"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" onclick="checkAllRecord(this.checked);" id="checkAllRecord"/>
				</td>
				<td align="left" id="td1">
					<b id="attrOrObjAttr">属性名称/指标名称</b>
				</td>
				<td  align="left">
					<b id="sourceTypeOrObjName">资源类型/对象名称</b>
				</td>
				<td align="left">
					<b>规则名称</b>
				</td>
				<td align="center">
					<b>采集间隔</b>
				</td>
				<td style="width: 70px" align="center">
					<b>报警规则</b>
				</td>
				<td style="width: 70px" align="center">
					<b>设置间隔</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" >
					 <td align="center">
			    	 	<xsl:choose>
							<xsl:when test="FLAG='Attribute'">
								<input type="checkbox" name="checkRecord" onclick="$('checkAllRecord').checked=false;" value="{ATTRIB_ID}@{ALARM_ID}"/>
							</xsl:when>
							<xsl:when test="FLAG='ObjAttribute'">
								<input type="checkbox" name="checkRecord" onclick="$('checkAllRecord').checked=false;" value="{OBJATTR_ID}@{ALARM_ID}"/>
							</xsl:when>
						</xsl:choose>
			    	 </td>
					<td>
						<xsl:choose>
							<xsl:when test="FLAG='Attribute'">
								<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
							</xsl:when>
							<xsl:when test="FLAG='ObjAttribute'">
								<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="FLAG='Attribute'">
								<xsl:value-of select="CLASS_NAME"></xsl:value-of>
							</xsl:when>
							<xsl:when test="FLAG='ObjAttribute'">
								<xsl:value-of select="OBJ_NAME"></xsl:value-of>
							</xsl:when>
						</xsl:choose>
					</td>	
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>			
					<td>
						<xsl:value-of select="COLL_TIMESPAN"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="FLAG='Attribute'">
								<img style="cursor:pointer" title="报警规则" src="images/viewdata.png" onclick="setAlarmRule('{ALARM_ID}','{ATTRIB_ID}');"/>
							</xsl:when>
							<xsl:when test="FLAG='ObjAttribute'">
								<img style="cursor:pointer" title="报警规则" src="images/viewdata.png" onclick="setAlarmRule('{ALARM_ID}','{OBJATTR_ID}');"/>
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="FLAG='Attribute'">
								<img style="cursor:pointer" title="设置间隔" src="images/window.gif" onclick="setCollectInterval('{ATTRIB_ID}','0');"/>
							</xsl:when>
							<xsl:when test="FLAG='ObjAttribute'">
								<img style="cursor:pointer" title="设置间隔" src="images/window.gif" onclick="setCollectInterval('{OBJATTR_ID}','1');"/>
							</xsl:when>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
