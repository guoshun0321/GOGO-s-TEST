<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabClassSet"  border="0" cellspacing="0" cellpadding="2" width="97%" >
			<tr>
				<td sortfield="CLASS_NAME" style="width: 180px" align="left">
					<b>名称</b>
				</td>
				<td sortfield="CLASS_TYPE" align="left">
					<b>别名</b>
				</td>
				<td sortfield="CLASS_LEVEL" style="width: 80px" align="center">
					<b>类型</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editClassSet('{CLASS_ID}')">
					<td>
						<xsl:value-of select="CLASS_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="CLASS_TYPE"></xsl:value-of>
					</td>
		            <td align="center">
		              <xsl:choose>
		                <xsl:when test="CLASS_LEVEL=100">
		                自定义属性
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=101">
		                配置属性
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=102">
		               监测指标
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=103">
		               性能指标
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=104">
		      Trap属性
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=105">
		               信号属性
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=106">
		               配置属性
		                </xsl:when>
		                <xsl:when test="CLASS_LEVEL=107">
		      Syslog属性
		                </xsl:when>
		                <xsl:otherwise>
		               其他
		                </xsl:otherwise>
		              </xsl:choose>
		            </td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editClassSet('{CLASS_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteClassSet('{CLASS_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ClassSetCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
