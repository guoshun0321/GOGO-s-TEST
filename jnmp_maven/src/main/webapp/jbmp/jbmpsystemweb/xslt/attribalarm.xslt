<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
>
	<xsl:template match="/">
		<table id="tabObjAttrib" border="0" cellspacing="0" cellpadding="2" width="100%">
			<tr>
				<td sortfield="OBJATTR_ID" align="left" width="80px">
					<b>编号</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>名称</b>
				</td>
				<td sortfield="ATTRIB_NAME" align="left">
					<b>属性</b>
				</td>
				<td sortfield="COLL_TYPE" align="left">
					<b>采集状态</b>
				</td>
				<td sortfield="ALARM_NAME" width="150px" align="left">
					<b>报警类型</b>
				</td>
				<td align="left" width="80px">
					<b>解除关联</b>
				</td>
				<td align="center" width="30px">
					<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkAlarmEvent',this.checked)" id="chkCheckAllAttribalarm"></input>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" onclick="$('chkAlarmEvent{OBJATTR_ID}').checked = !$('chkAlarmEvent{OBJATTR_ID}').checked">
					<td width="80px">
						<xsl:value-of select="OBJATTR_ID"/>
					</td>
					<td>
						<xsl:value-of select="OBJATTR_NAME"/>
					</td>
					<td>
						<xsl:value-of select="ATTRIB_NAME"/>
					</td>
					<td width="100px">
						<xsl:choose>
							<xsl:when test="COLL_TYPE=0">
								不采集
							</xsl:when>
							<xsl:when test="COLL_TYPE=1">
								采集
							</xsl:when>
							<xsl:when test="COLL_TYPE=2">
								单次未采集
							</xsl:when>
							<xsl:when test="COLL_TYPE=10">
								单次已采集
							</xsl:when>
							<xsl:otherwise>

							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td width="150px">
						<font color='#283cb4'>
							<xsl:value-of select="ALARM_NAME"/>
						</font>
					</td>
					<td align="center">
						<xsl:if test="ALARM_ID!=''">
							<a href="javascript:void(0)" onclick="bindNoAlarm('{OBJATTR_ID}', '{OBJ_ID}')">
								<img border="0" src="images/drop.gif"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<input id="chkAlarmEvent{OBJATTR_ID}" type="checkbox" name="chkAlarmEvent" onclick="$('chkCheckAllAttribalarm').checked=false;preventEvent(event);" value="{OBJATTR_ID}"></input>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ObjAttribCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>