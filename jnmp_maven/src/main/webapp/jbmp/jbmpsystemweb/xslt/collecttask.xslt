<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabCollectTask"  border="0" cellspacing="0" cellpadding="2" width="98%" >
			<tr>
				<td align="left" sortfield="TASK_ID" width="45px">
					<b>编号</b>
				</td>
				<td align="left" sortfield="GROUP_NAME">
					<b>采集组</b>
				</td>
				<td align="left" sortfield="COLL_NAME">
					<b>采集器</b>
				</td>
				<td width="80px" align="center" sortfield="TASK_TYPE">
					<b>任务类型</b>
				</td>
				<td width="180px" align="left" sortfield="START_TIME">
					<b>开始时间/采集周期</b>
				</td>
				<td width="180px" align="left" sortfield="END_TIME">
					<b>结束时间/周期时间</b>
				</td>
				<td width="60px" align="center" sortfield="TASK_STATE">
					<b>状态</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>启动</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>停止</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>详细</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>编辑</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>删除</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"  ondblclick="if('{TASK_STATE}'==0)editCollectTask('{TASK_ID}')" >
					<td>
						<xsl:value-of select="TASK_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="GROUP_NAME"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="COLL_NAME"></xsl:value-of>
					</td>
					<td align="center" >
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
					<td>
						<span style="line-height:14px">
							<xsl:if test ="TASK_TYPE=2 and translate(substring(START_TIME,0,20),'T',' ')!='1900-01-01 00:00:00'">
								<xsl:value-of select="translate(substring(START_TIME,0,20),'T',' ')"></xsl:value-of>
							</xsl:if>
							<xsl:if test="TASK_TYPE=1">
								<br />
								<xsl:value-of select="WEEK_MASK"></xsl:value-of>
							</xsl:if>
						</span>
					</td>
					<td>
						<xsl:if test ="TASK_TYPE=2 and translate(substring(END_TIME,0,20),'T',' ')!='1900-01-01 00:00:00'">
							<xsl:value-of select="translate(substring(END_TIME,0,20),'T',' ')"></xsl:value-of>
						</xsl:if>
						<xsl:if test="TASK_TYPE=1">
							<br />
							<xsl:value-of select="HOUR_MASK"></xsl:value-of>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="TASK_STATE=0">
								停止
							</xsl:when>
							<xsl:when test="TASK_STATE=1">
								启动
							</xsl:when>
							<xsl:when test="TASK_STATE=2">
								完成
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:if test ="TASK_STATE=0">
							<a href="javascript:void(0)" onclick="startCollectTask('{TASK_ID}')">
								<img border="0" title="启动" src="images/start.png"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test ="TASK_STATE=1">
							<a href="javascript:void(0)" onclick="stopCollectTask('{TASK_ID}')">
								<img border="0" title="停止" src="images/stop.png"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test ="TASK_STATE=1">
							<a href="javascript:void(0)" onclick="viewCollectTaskInfo('{TASK_ID}')">
								<img border="0" title="详细状态" src="images/window.gif"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test ="TASK_STATE=0">
							<a href="javascript:void(0)" onclick="editCollectTask('{TASK_ID}')">
								<img border="0" title="编辑" src="images/edit.gif"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test ="TASK_STATE=0 or TASK_STATE=1 or TASK_STATE=2">
							<img style="cursor:pointer" title="删除" src="images/drop.gif" onclick="deleteCollectTask('{TASK_ID}');"/>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_CollectTaskCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
