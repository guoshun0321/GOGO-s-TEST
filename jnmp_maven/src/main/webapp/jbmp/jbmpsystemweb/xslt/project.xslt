<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabProject"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<colgroup>
				<col width="60px"></col>
				<col width="150px"></col>
				<col width="60px"></col>
				<col width="80px"></col>
				<col width="auto"></col>
				<col width="90px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
				<col width="45px"></col>
			</colgroup>
			<tr>
				<td sortfield="PROJECT_NUM" align="left">
					<b>项目编号</b>
				</td>
				<td sortfield="PROJECT_NAME" align="left">
					<b>项目名称</b>
				</td>
				<td sortfield="PROJECT_TYPE" align="center">
					<b>项目类别</b>
				</td>
				<td sortfield="REPORT_TIME" align="center">
					<b>申报时间</b>
				</td>
				<td sortfield="PROJECT_CONTENT" align="left">
					<b>项目内容</b>
				</td>
				<td sortfield="BUDGET_MONEY" align="left">
					<b>预算总额</b>
				</td>
				<td align="center">
					<b>附件</b>
				</td>
				<td align="center">
					<b>上传</b>
				</td>
				<td align="center">
					<b>编辑</b>
				</td>
				<td align="center">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<xsl:attribute name ="ondblclick">
						editProject('<xsl:value-of select="PROJECT_ID"></xsl:value-of>')
					</xsl:attribute>
					<td>
						<xsl:value-of select="PROJECT_NUM"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="PROJECT_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="PROJECT_TYPE=1">
								内部项目
							</xsl:when>
							<xsl:when test="PROJECT_TYPE=2">
								新项目
							</xsl:when>
							<xsl:when test="PROJECT_TYPE=3">
								升级项目
							</xsl:when>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="substring(REPORT_TIME, 0, 11)"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="PROJECT_CONTENT"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="BUDGET_MONEY"></xsl:value-of>
					</td>
					<td>
						<xsl:choose>
							<xsl:when test="string-length(PROJECT_ATTACHMENT)&gt;0">
								<a href="javascript:void(0);" onclick="showAttachmentList('{PROJECT_ATTACHMENT}', '{PROJECT_ATTACHMENT_PATH}', {PROJECT_ID}); return false;">下载</a>
							</xsl:when>
							<xsl:otherwise>
								无
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<img border="0" title="上传" src="images/upload.gif" onclick="uploadFile('{PROJECT_ID}')" style="cursor: pointer;"/>
					</td>
					<td align="center">
						<img border="0" title="编辑" src="images/edit.gif" onclick="editProject('{PROJECT_ID}')" style="cursor: pointer;"/>
					</td>
					<td align="center">
						<img border="0" title="删除" src="images/drop.gif" onclick="deleteProject('{PROJECT_ID}')" style="cursor: pointer;"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ProjectCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>