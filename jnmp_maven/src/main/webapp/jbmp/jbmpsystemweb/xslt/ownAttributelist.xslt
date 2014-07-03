<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="definedattributeList" border="0" cellspacing="0"
			cellpadding="2" width="100%">
			<tr>
				<td sortfield="OBJATTR_ID" align="left" width="80px">
					<b>对象属性编号</b>
				</td>
				<td sortfield="OBJ_ID" align="left" width="80px">
					<b>对象编号</b>
				</td>
				<td sortfield="ATTRIB_ID" align="left" width="80px">
					<b>属性表编号</b>
				</td>
				<td sortfield="ATTRIB_VALUE" align="left">
					<b>属性值</b>
				</td>
				<td sortfield="ATTRIB_TYPE" align="left">
					<b>属性类型</b>
				</td>
				<td sortfield="ATTRIB_PARAM" align="left">
					<b>指标参数</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>名称</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td>
						<xsl:value-of select="OBJATTR_ID" />
					</td>
					<td>
						<xsl:value-of select="OBJ_ID" />
					</td>
					<td>
						<xsl:value-of select="ATTRIB_ID" />
					</td>
					<td>
						<xsl:value-of select="ATTRIB_VALUE" />
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="ATTRIB_TYPE=100">
								自定义属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=101">
								配置属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=102">
								监测指标
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=103">
								性能指标
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=104">
								trap属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=105">
								信号属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=106">
								表格数据
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="ATTRIB_PARAM" />
					</td>
					<td>
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>