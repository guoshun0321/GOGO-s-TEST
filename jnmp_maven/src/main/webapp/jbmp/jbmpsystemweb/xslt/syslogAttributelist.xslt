<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="syslogattributeList" border="0" cellspacing="0"
			cellpadding="2" width="99.5%">
			<colgroup>
				<col width="30px" align="left"></col>
				<col width="80px"></col>
				<col></col>
				<col width="80px" align="center"></col>
				<col width="80px"></col>
				<col width="150px"></col>
				<col width="45px" align="center"></col>
				<col width="45px" align="center"></col>
			</colgroup>
			<tr>
				<td align="center" width="30px">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('checkObjAttr2Alarm107',this.checked)"
						id="chkCheckAllObjAttr7"></input>
				</td>
				<td sortfield="A.OBJATTR_ID" width="80px" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>名称</b>
				</td>
				<td sortfield="ATTRIB_TYPE" align="center">
					<b>类型</b>
				</td>
				<td sortfield="DATA_ENCODING" align="center">
					<b>数据编码</b>
				</td>
				<td sortfield="ALARM_NAME" align="left">
					<b>报警规则</b>
				</td>
				<td align="center" width="45px">
					<b>编辑</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editTimeSpanObjAttr('{OBJATTR_ID}','107')">
					<td align="center">
						<input type="checkbox" name="checkObjAttr2Alarm107"
							onclick="$('chkCheckAllObjAttr7').checked=false;" value="{OBJATTR_ID}"></input>
						<input type="hidden" id="hiddenObjAttr2Alarm{OBJATTR_ID}"
							value="{ALARM_NAME}" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_ID" />
					</td>
					<td>
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="ATTRIB_TYPE=104">
								Trap属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=105">
								信号属性
							</xsl:when>
							<xsl:when test="ATTRIB_TYPE=107">
								Syslog属性
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:value-of select="DATA_ENCODING" />
					</td>
					<td align="left">
						<xsl:if test="IS_VALID = 0">
							<xsl:value-of select="ALARM_NAME" />
						</xsl:if>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editTimeSpanObjAttr('{OBJATTR_ID}','107')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<a hreg="javascript:void(0)"
							onclick="if(confirm('确定删除？'))deleteObjAttr('{OBJATTR_ID}','107')">
							<img style="cursor:pointer" border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>