<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabMonitorAttribute" border="0" cellspacing="0"
			cellpadding="2" width="97%">
			<colgroup>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col></col>
				<col width="45px" align="center"></col>
				<col width="45px" align="center"></col>
			</colgroup>
			<tr>
				<td sortfield="ATTRIB_NAME" align="left">
					<b>属性名称</b>
				</td>
				<td sortfield="ATTRIB_VALUE" align="left">
					<b>标识</b>
				</td>
				<td sortfield="ATTRIB_PARAM" align="left">
					<b>参数</b>
				</td>
				<td sortfield="DATA_TYPE" align="center">
					<b>数据类型</b>
				</td>
				<td sortfield="DATA_UNIT" align="left">
					<b>数据单位</b>
				</td>
				<td sortfield="VIEW_TYPE" align="center">
					<b>显示类型</b>
				</td>
				<td sortfield="COLL_TIMESPAN" align="center">
					<b>采集间隔</b>
				</td>
				<td sortfield="ALARM_NAME" align="left">
					<b>关联报警</b>
				</td>
				<td align="center" width="45px">
					<b>编辑</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editMonitorAttribute('{ATTRIB_ID}')">
					<td>
						<xsl:value-of select="ATTRIB_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ATTRIB_VALUE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="ATTRIB_PARAM"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="DATA_TYPE=0">
								未知类型
							</xsl:when>
							<xsl:when test="DATA_TYPE=1">
								整型
							</xsl:when>
							<xsl:when test="DATA_TYPE=2">
								字符型
							</xsl:when>
							<xsl:when test="DATA_TYPE=3">
								日期型
							</xsl:when>
							<xsl:when test="DATA_TYPE=4">
								浮点型
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="left">
						<xsl:value-of select="DATA_UNIT"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="VIEW_TYPE='PIE'">
								饼图
							</xsl:when>
							<xsl:when test="VIEW_TYPE='LINE'">
								线图
							</xsl:when>
							<xsl:when test="VIEW_TYPE='COLUMN'">
								柱状图
							</xsl:when>
							<xsl:when test="VIEW_TYPE='LIST'">
								列表
							</xsl:when>
							<xsl:when test="VIEW_TYPE='LABEL'">
								标签
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:if test="COLL_TIMESPAN &lt; 60">
							<xsl:value-of select="COLL_TIMESPAN" />
							秒
						</xsl:if>
						<xsl:if test="COLL_TIMESPAN &gt; 60">
							<xsl:value-of select="COLL_TIMESPAN div 60" />
							分钟
						</xsl:if>
						<xsl:if test="COLL_TIMESPAN=60">
							<xsl:value-of select="COLL_TIMESPAN div 60" />
							分钟
						</xsl:if>
					</td>
					<td>
						<xsl:value-of select="ALARM_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editMonitorAttribute('{ATTRIB_ID}')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif"
							onclick="deleteMonitorAttribute('{ATTRIB_ID}');" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_Count"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
