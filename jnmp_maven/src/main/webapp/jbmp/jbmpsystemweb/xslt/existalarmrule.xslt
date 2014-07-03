<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabExistAlarm"  border="0" cellspacing="0" cellpadding="2" width="98%">
			<tr>
				<td style="width:50px"></td>
				<td sortfield="ALARM_ID" align="left" width="120px">
					<b>规则编号</b>
				</td>
				<td sortfield="ALARM_NAME" align="left" width="120px">
					<b>规则名称</b>
				</td>
				<td align="left" width="280px">
					<b>指标/属性名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr>
				 	<td align="center">
			    	 	<input type="radio" name="existAlarmRadio" value="{ALARM_ID}@{ALARM_NAME}"></input>
			    	 </td>
					<td>
						<xsl:value-of select="ALARM_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>		
					<td>
						<xsl:value-of select="OBJATTR_NAME"></xsl:value-of>  
						<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
					</td>		
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_existAlarmCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
