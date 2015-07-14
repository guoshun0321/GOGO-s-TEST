<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabClassIdentify"  border="0" cellspacing="0" cellpadding="2" width="99%" >
			<tr>
				<td sortfield="SNMP_SYSOID" align="left">
					<b>标识名</b>
				</td>
				<td sortfield="SNMP_VALUE" align="left">
					<b>标识值</b>
				</td>
				<td sortfield="CONDITION" style="width: 80px" align="center">
					<b>识别方式</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editClassIdentify('{TYPE_ID}')">
					<td align="left">
						<xsl:value-of select="SNMP_SYSOID"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="SNMP_VALUE"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="CONDITION='EX'">
								存在
							</xsl:when>
							<xsl:when test="CONDITION='EQ'">
								等于
							</xsl:when>
							<xsl:when test="CONDITION='LK'">
								Like
							</xsl:when>
							<xsl:when test="CONDITION='IN'">
								In
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editClassIdentify('{TYPE_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteClassIdentify('{TYPE_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ClassIdentifyCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
