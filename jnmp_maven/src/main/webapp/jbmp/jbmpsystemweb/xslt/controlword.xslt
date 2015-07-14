<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabControlWord"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<tr>
				<td sortfield="CW_TYPE" align="left">
					<b>受控类型</b>
				</td>
				<td sortfield="CW_NAME" align="left">
					<b>受控词名称</b>
				</td>
				<td sortfield="CW_DESC" align="left">
					<b>参数或描述</b>
				</td>
		        <td align="center" width="45px">
		          <b>编辑</b>
		        </td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"  ondblclick="dbclickControlWord('{OPER_TYPE}','{CW_ID}','{CW_TYPE}','{CW_NAME}','{CW_DESC}');" >
					<td align="left">
						<xsl:choose>
							<xsl:when test="CW_TYPE=1">
								监控属性类型
							</xsl:when>
							<xsl:when test="CW_TYPE=2">
								采集数据显示类型
							</xsl:when>
							<xsl:when test="CW_TYPE=1001">
								确认报警
							</xsl:when>
							<xsl:when test="CW_TYPE=1002">
								报警级别
							</xsl:when>
							<xsl:when test="CW_TYPE=1003">
								故障原因
							</xsl:when>
							<xsl:when test="CW_TYPE=10">
								频道类别
							</xsl:when>
							<xsl:when test="CW_TYPE=100">
								违规类型
							</xsl:when>
							<xsl:when test="CW_TYPE=101">
								通道类型
							</xsl:when>
						<!--	<xsl:when test="CW_TYPE=400">
								信号类型
							</xsl:when>  -->
						</xsl:choose>
					</td>
					<td align="left">
						<xsl:value-of select="CW_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="CW_DESC"></xsl:value-of>
					</td>
          			<td align="center">
          				<xsl:choose>
							<xsl:when test="OPER_TYPE='12'">
								
							</xsl:when>
							<xsl:when test="OPER_TYPE='13'">
								
							</xsl:when>
							<xsl:otherwise>
								<a href="javascript:void(0)" onclick="editControlWord('{CW_ID}','{CW_TYPE}','{CW_NAME}','{CW_DESC}')">
					              <xsl:attribute name="CW_DESC">
					                <xsl:value-of select="CW_DESC"></xsl:value-of>
					              </xsl:attribute>
					              <xsl:attribute name="CW_NAME">
					                <xsl:value-of select="CW_NAME"></xsl:value-of>
					              </xsl:attribute>
					              <img border="0" src="images/edit.gif"/>
					            </a>
							</xsl:otherwise>
						</xsl:choose>
          			</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="OPER_TYPE='11'">
								
							</xsl:when>
							<xsl:when test="OPER_TYPE='13'">
								
							</xsl:when>
							<xsl:otherwise>
								<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteControlWord('{CW_ID}');"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ControlWordCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
