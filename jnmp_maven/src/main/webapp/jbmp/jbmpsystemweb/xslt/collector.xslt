<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabCollector"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td align="left" sortfield="COLL_NAME">
					<b>采集器名称</b>
				</td>
				<td align="left" sortfield="IP_ADDR">
					<b>IP地址</b>
				</td>
				<td style="width: 150px" align="center" sortfield="CREATE_TIME">
					<b>创建时间</b>
				</td>
				<td style="width: 100px" align="center" sortfield="CREATE_TIME">
					<b>是否在线</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>编辑</b>
		        </td>
		        <td style="width: 45px" align="center" >
					<b>删除</b>
		        </td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editCollector('{COLL_ID}')">
					<td>
						<xsl:value-of select="COLL_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="IP_ADDR"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="CREATE_TIME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="IS_ONLINE = 0">
								<img style="cursor:pointer" title="不在线" src="images/CollectorOff.png"/>
							</xsl:when>
							<xsl:when test="IS_ONLINE = 1">
								<img style="cursor:pointer" title="在线" src="images/CollectorOn.png"/>
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editCollector('{COLL_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteCollector('{COLL_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_CollectorCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
