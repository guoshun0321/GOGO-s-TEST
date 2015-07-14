<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="inspectattributeList" border="0" cellspacing="0"
			cellpadding="2" width="98%">
			<colgroup>
				<col width="30px"></col>
				<col width="80px"></col>
				<col></col>
				<col></col>
				<col width="100px"></col>
				<col></col>
			</colgroup>
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('checkObjAttr2Alarm102',this.checked)"
						id="chkCheckAllObjAttr"></input>
				</td>
				<td sortfield="A.OBJATTR_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="E.OBJ_NAME" align="left">
					<b>对象名称</b>
				</td>
				<td sortfield="A.OBJATTR_NAME" align="left">
					<b>指标名称</b>
				</td>
				<td sortfield="A.COLL_TIMESPAN" align="center">
					<b>采集间隔</b>
				</td>
				<td sortfield="D.ALARM_NAME" align="left">
					<b>报警规则</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="checkObjAttr2Alarm102"
							onclick="$('chkCheckAllObjAttr').checked=false;" value="{OBJATTR_ID}"></input>
						
							
						<input type="hidden" id="hiddenObjAttr2Alarm{OBJATTR_ID}" value="{ALARM_ID}"/>
						
						
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_NAME" />
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
					<td align="left">
						<xsl:value-of select="ALARM_NAME" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>