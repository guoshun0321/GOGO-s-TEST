<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="objTableList" border="0" cellspacing="0" cellpadding="2"
			width="99%">
			<colgroup>
				<col width="30px" align="center"></col>
				<col width="100px"></col>
				<col width="180px"></col>
				<col></col>
				<col></col>
				<col width="80px"></col>
				<col width="80px"></col>
				<col width="75px"></col>
				<col width="45px" aligen="center"></col>
				<col width="45px" aligen="center"></col>
			</colgroup>
			<tr>
				<td width="30px" align="center">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('chkAllObjectChild',this.checked)"
						id="chkCheckAllChild"></input>
				</td>
				<td sortfield="a.OBJ_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJ_NAME" align="left">
					<b>对象名称</b>
				</td>
				<td sortfield="IP_ADDR" align="left">
					<b>IP地址</b>
				</td>
				<td sortfield="CLASS_NAME" align="left">
					<b>类别</b>
				</td>
				<td sortfield="RECEIVE_ENABLE" align="center">
					<b>采集状态</b>
				</td>
				<td sortfield="MAXLEVEL" align="center">
					<b>报警等级</b>
				</td>
				<td align="center" width="70px">
					<b>对象属性</b>
				</td>
				<td align="center">
					<b>面板</b>
				</td>
				<td align="center">
					<b>编辑</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editElement('{OBJ_ID}','{CLASS_ID}','{CLASS_TYPE}')" width="40px"
					align="center">
					<td align="center">
						<input type="checkbox" name="chkAllObjectChild"
							onclick="$('chkCheckAllChild').checked=false;" value="{OBJ_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJ_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="IP_ADDR" />
					</td>
					<td align="left">
						<xsl:value-of select="CLASS_NAME" />
					</td>
					<td align="left">
						<xsl:choose>
							<xsl:when test="RECEIVE_ENABLE =0">
								正常
							</xsl:when>
							<xsl:when test="RECEIVE_ENABLE =1">
								失败
							</xsl:when>
							<xsl:when test="RECEIVE_ENABLE =2">
								未知
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="left">
						<xsl:choose>
							<xsl:when test="MAXLEVEL =10">
								<img border="0" src="images/alarm10.gif" />
							</xsl:when>
							<xsl:when test="MAXLEVEL =20">
								<img border="0" src="images/alarm20.gif" />
							</xsl:when>
							<xsl:when test="MAXLEVEL =30">
								<img border="0" src="images/alarm30.gif" />
							</xsl:when>
							<xsl:when test="MAXLEVEL =40">
								<img border="0" src="images/alarm40.gif" />
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)"
							onclick="getAttribClass('{CLASS_ID}', false); edirObjectAttrib('{OBJ_ID}','{OBJ_NAME}','{CLASS_ID}')">
							<img border="0" src="images/attrib.gif" />
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="toMonitor('{OBJ_ID}','{OBJ_NAME}','{CLASS_ID}');return false;">
								<img border="0" src="images/viewdata.png" width="16px" height="16px" />
						</a>
				    </td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editElement('{OBJ_ID}','{CLASS_ID}','{CLASS_TYPE}','10')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center" width="35px">
						<a hreg="javascript:void(0)" onclick="deleteSubElement('{OBJ_ID}')">
							<img style="cursor:pointer" border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hidCountChild"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>