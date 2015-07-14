<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabReportTime" border="0" cellspacing="0"
			cellpadding="1" width="99%">
			<tr>
				<td width="80px" align="left" sortfield="TASK_NAME">
					<b>任务名称</b>
				</td>
				<td width="110px" align="left" sortfield="NAME">
					<b>报表类型</b>
				</td>
				<td width="60px" align="center" sortfield="TASK_TYPE">
					<b>任务类型</b>
				</td>
				<td width="100px" align="center" sortfield="START_TIME">
					<b>开始日期</b>
				</td>
				<td width="100px" align="center" sortfield="END_TIME">
					<b>结束日期</b>
				</td>
				<td align="left" width="85px" sortfield="COLL_TIMESPAN">
					<b>生成周期(秒)</b>
				</td>
				<td width="60px" align="center" sortfield="FILE_FORMAT">
					<b>报表格式</b>
				</td>
				<td width="60px" align="center" sortfield="IS_MAIL">
					<b>是否邮件</b>
				</td>
				<td width="60px" align="center" sortfield="TASK_STATE">
					<b>状态</b>
				</td>
				<td width="60px" align="center">
					<b>报表参数</b>
				</td>
				<td align="center" width="35px">
					<b>编辑</b>
				</td>
				<td align="center" width="35px">
					<b>提交</b>
				</td>
				<td align="center" width="35px">
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20" ondblclick="editReportTime('{TASK_ID}')">
					<td align="left">
						<xsl:value-of select="TASK_NAME"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="TASK_TYPE=1">
								周期任务
							</xsl:when>
							<xsl:when test="TASK_TYPE=2">
								单次任务
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<span style="line-height:14px">
							<xsl:value-of select="translate(substring(START_TIME,0,11),'T',' ')"></xsl:value-of>
							<xsl:if test="TASK_TYPE=1">
								<br />
								（
								<xsl:value-of select="WEEK_MASK"></xsl:value-of>
								<xsl:value-of select="HOUR_MASK"></xsl:value-of>
								）
							</xsl:if>
						</span>
					</td>
					<td align="center">
						<xsl:value-of select="translate(substring(END_TIME,0,11),'T',' ')"></xsl:value-of>
					</td>
					<td align="left">
						<xsl:value-of select="COLL_TIMESPAN"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="FILE_FORMAT=0">
								excel
							</xsl:when>
							<xsl:when test="FILE_FORMAT=1">
								word
							</xsl:when>
							<xsl:when test="FILE_FORMAT=2">
								pdf
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="IS_MAIL=0">
								否
							</xsl:when>
							<xsl:when test="IS_MAIL=1">
								是
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="TASK_STATE=0">
								新建任务
							</xsl:when>
							<xsl:when test="TASK_STATE=1">
								准备完毕
							</xsl:when>
							<xsl:when test="TASK_STATE=2">
								执行中
							</xsl:when>
							<xsl:when test="TASK_STATE=10">
								执行成功
							</xsl:when>
							<xsl:when test="TASK_STATE=11">
								执行失败
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="viewReportParam('{FIELD_1}')">
							<img border="0" src="images/window.gif"/>
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="editReportTime('{TASK_ID}')">
							<img border="0" src="images/edit.gif"/>
						</a>
					</td>
					<td align="center">
						<xsl:if test="TASK_STATE=0">
							<a href="javascript:void(0)" onclick="commitReportTime('{TASK_ID}')">
								<img border="0" src="images/commit.gif" />
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<img style="cursor:pointer"   title="删除" src="images/drop.gif" onclick="deleteReportTime('{TASK_ID}');"/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_ReportTimeCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
