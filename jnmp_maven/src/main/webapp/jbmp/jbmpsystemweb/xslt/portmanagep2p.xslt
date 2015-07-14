<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabP2pObject" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col width="100px" align="left"></col>
				<col width="100px" align="left"></col>
				<col width="100px" align="left"></col>
				<col width="100px" align="left"></col>
				<col width="100px" align="left"></col>
				<col width="100px" align="left"></col>
				<col width="45px" align="center"></col>
			</colgroup>
			<tr>
				<td width="30px" align="center">
					<input type="checkbox" onclick="JetsenWeb.Form.checkAllItems('chkP2pObject',this.checked)"
						id="chkP2pAll"></input>
				</td>
				<td sortfield="PORTA_NAME" align="left">
					<b>A对象名称</b>
				</td>
				<td sortfield="PORTA_IP" align="left">
					<b>A对象IP地址</b>
				</td>
				<td sortfield="PORTA_PARENT" align="left">
					<b>A对象所属设备名</b>
				</td>
				<td sortfield="PORTB_NAME" align="left">
					<b>B对象名称</b>
				</td>
				<td sortfield="PORTB_IP" align="left">
					<b>B对象IP地址</b>
				</td>
				<td sortfield="PORTB_PARENT" align="left">
					<b>B对象所属设备名</b>
				</td>
				<td align="center" width="45px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="$('chkP2p{ID}').click()">
					<td align="center">
						<input id="chkP2p{ID}" type="checkbox" name="chkP2pObject"
							onclick="$('chkP2pAll').checked=false;" value="{ID}">
						</input>
					</td>
					<td align="left">
						<xsl:value-of select="PORTA_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="PORTA_IP" />
					</td>
					<td align="left">
						<xsl:value-of select="PORTA_PARENT" />
					</td>
					<td align="left">
						<xsl:value-of select="PORTB_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="PORTB_IP" />
					</td>
					<td align="left">
						<xsl:value-of select="PORTB_PARENT" />
					</td>
					<td align="center">
						<xsl:if test="REL_TYPE != 1">
							<a hreg="javascript:void(0)"
								onclick="deleteP2p('{ID}')">
								<img style="cursor:pointer" border="0" src="images/drop.gif" />
							</a>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_P2pObjectCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>