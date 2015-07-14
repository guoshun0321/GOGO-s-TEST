<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:param name="DelReportFile" />
	<xsl:template match="/">
		<table id="tabReportFile" border="0" cellspacing="0" cellpadding="1" width="98%">
			<tr>
				<td align="center" width="30px">
					<input type="checkbox" id="chkCheckAllReportFile"
						onclick="JetsenWeb.Form.checkAllItems('chkReportFile',this.checked)" ></input>
				</td>
				<td sortfield="FILE_NAME" width="400">
					<b>文件名称</b>
				</td>
				<td align="left" sortfield="TASK_NAME" width="200">
					<b>任务名称</b>
				</td>
				<td align="left" sortfield="NAME" width="150">
					<b>报表类型</b>
				</td>
				<td align="center" sortfield="f.CREATE_TIME" width="130">
					<b>生成时间</b>
				</td>
				<td width="60px" align="center" sortfield="FILE_STATE">
					<b>状态</b>
				</td>
				<td align="center" width="50px">
					<b>下载</b>
				</td>
				<td align="center" width="50px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td align="center">
						<input type="checkbox" name="chkReportFile"
							onclick="$('chkCheckAllReportFile').checked=false;" value="{FILE_ID}"></input>
					</td>
					<td style="line-height:14px">
						<xsl:value-of select="FILE_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="TASK_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:value-of select="translate(substring(CREATE_TIME,0,20),'T',' ')"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="FILE_STATE=0">
								新建任务
							</xsl:when>
							<xsl:when test="FILE_STATE=2">
								执行中
							</xsl:when>
							<xsl:when test="FILE_STATE=10">
								执行成功
							</xsl:when>
							<xsl:when test="FILE_STATE=11">
								<span style="color:red">执行失败</span>
							</xsl:when>
							<xsl:otherwise></xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:if test="FILE_STATE=10">
							<a href="{PATH}" target="_blank">
								<img style="cursor:pointer" title="下载" src="images/download.gif"
									border="0"></img>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<img style="cursor:pointer" title="删除" src="images/drop.gif"
							onclick="deleteFile('{FILE_ID}');" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ReportFileCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>