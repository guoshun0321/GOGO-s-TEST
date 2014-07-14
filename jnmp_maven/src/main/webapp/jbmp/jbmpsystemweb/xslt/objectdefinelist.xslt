<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="objInsTable1" border="0" cellspacing="0" cellpadding="2"
			width="98%">
			<colgroup>
				<col width="30px" align="left"></col>
				<col width="80px" align="left"></col>
				<col></col>
				<col></col>
				<col></col>
				<col width="45px" align="center"></col>
				<col width="45px" align="center"></col>
			</colgroup>
			<tr>
				<td align="center" width="30px">
					<input type="checkbox"
						onclick="JetsenWeb.Form.checkAllItems('checkObjAttr2Alarm100',this.checked)"
						id="chkCheckAllObjAttr100"></input>
				</td>
				<td sortfield="A.OBJATTR_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="OBJATTR_NAME" align="left">
					<b>名称</b>
				</td>
				<td align="left">
					<b>值</b>
				</td>
				<td sortfield="IS_VISIBLE" align="center">
					<b>是否可见</b>
				</td>
				<td align="center">
					<b>编辑</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"
					ondblclick="editSelfDefObjAttr('{OBJATTR_ID}','{OBJATTR_NAME}','{IS_VISIBLE}','{STR_VALUE}','100')">
					<td align="center">
						<input type="checkbox" name="checkObjAttr2Alarm100"
							onclick="$('chkCheckAllObjAttr100').checked=false;" value="{OBJATTR_ID}"></input>
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_ID" />
					</td>
					<td align="left">
						<xsl:value-of select="OBJATTR_NAME" />
					</td>
					<td align="left">
						<xsl:value-of select="STR_VALUE" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="IS_VISIBLE=1">
								可见
							</xsl:when>
							<xsl:when test="IS_VISIBLE=0">
								不可见
							</xsl:when>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)"
							onclick="editSelfDefObjAttr('{OBJATTR_ID}','{OBJATTR_NAME}','{IS_VISIBLE}','{STR_VALUE}','100')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<a hreg="javascript:void(0)"
							onclick="deleteObjAttr('{OBJATTR_ID}','100')">
							<img style="cursor:pointer" border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
