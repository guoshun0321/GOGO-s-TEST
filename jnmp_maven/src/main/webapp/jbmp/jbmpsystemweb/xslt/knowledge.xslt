<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:param name="CurrentUserId" />
	<xsl:param name="IsAdmin" />
	<xsl:template match="/">
		<table id="tabKnowledge"  border="0" cellspacing="0" cellpadding="2" width="99.5%" >
			<colgroup>
				<col width="45px"></col>
				<col width="150px"></col>
				<col width="auto"></col>
				<col width="45px"></col>
				<col width="130px"></col>
				<col width="130px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="KNOWLEDGE_ID" align="left">
					<b>编号</b>
				</td>
				<td sortfield="KNOWLEDGE_TITLE" align="left">
					<b>标题</b>
				</td>
				<td sortfield="KNOWLEDGE_SUMMARY" align="left">
					<b>摘要</b>
				</td>
				<td sortfield="USER_NAME" align="left">
					<b>作者</b>
				</td>
				<td sortfield="k.CREATE_TIME" align="center">
					<b>发表时间</b>
				</td>
				<td sortfield="k.UPDATE_TIME" align="center">
					<b>更新时间</b>
				</td>
				<td sortfield="CLICK_COUNT" align="center">
					<b>点击数</b>
				</td>
				<td sortfield="COMMENT_COUNT" align="center">
					<b>评论数</b>
				</td>
				<td style="width: 45px" align="center">
					<b>详细</b>
				</td>
				<td style="width: 45px" align="center">
					<b>上传</b>
				</td>
				<td style="width: 45px" align="center">
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<xsl:if test="$CurrentUserId=CREATE_USERID or $IsAdmin=1">
						<xsl:attribute name ="ondblclick">
							editKnowledge('<xsl:value-of select="KNOWLEDGE_ID"></xsl:value-of>')
						</xsl:attribute>
					</xsl:if>
					<td>
						<xsl:value-of select="KNOWLEDGE_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="KNOWLEDGE_TITLE"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="KNOWLEDGE_SUMMARY"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="USER_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="substring(CREATE_TIME, 1, 19)"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="substring(UPDATE_TIME, 1, 19)"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="CLICK_COUNT"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="COMMENT_COUNT"></xsl:value-of>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="loadKnowledgeDetail('{KNOWLEDGE_ID}', '{CLICK_COUNT}', '{CREATE_USERID}')">
							<img border="0" src="images/window.gif"/>
						</a>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="$CurrentUserId=CREATE_USERID or $IsAdmin=1">
								<img border="0" title="上传" src="images/upload.gif">
									<xsl:attribute name="onclick">
										uploadFile('<xsl:value-of select="KNOWLEDGE_ID"></xsl:value-of>')
									</xsl:attribute>
									<xsl:attribute name="style">
										cursor:hand
									</xsl:attribute>
								</img>
							</xsl:when>
							<xsl:otherwise>
								<img border="0" title="上传" src="images/upload_gray.gif"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="$CurrentUserId=CREATE_USERID or $IsAdmin=1">
								<img border="0" title="编辑" src="images/edit.gif">
									<xsl:attribute name="onclick">
										editKnowledge('<xsl:value-of select="KNOWLEDGE_ID"></xsl:value-of>')
									</xsl:attribute>
									<xsl:attribute name="style">
										cursor:hand
									</xsl:attribute>
								</img>
							</xsl:when>
							<xsl:otherwise>
								<img border="0" title="编辑" src="images/edit_gray.gif"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="$CurrentUserId=CREATE_USERID or $IsAdmin=1">
								<img border="0" title="删除" src="images/drop.gif">
									<xsl:attribute name="onclick">
										deleteKnowledge('<xsl:value-of select="KNOWLEDGE_ID"></xsl:value-of>');
									</xsl:attribute>
									<xsl:attribute name="style">
										cursor:hand
									</xsl:attribute>
								</img>
							</xsl:when>
							<xsl:otherwise>
								<img border="0" title="删除" src="images/drop_gray.gif"/>
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_KnowledgeCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>