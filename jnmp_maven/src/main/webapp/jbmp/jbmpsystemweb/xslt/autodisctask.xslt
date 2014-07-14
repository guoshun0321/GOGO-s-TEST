<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="autoDiscTaskTable" border="0" cellspacing="0"
			cellpadding="2" width="99.5%">
			<tr>
				<td align="left" width="80px" sortfield="TASK_ID">
					<B>编号</B>
				</td>
				<td align="left" sortfield="TASK_TYPE">
					<B>任务类型</B>
				</td>
				<td align="left" sortfield="BEGIN_IP">
					<B>开始IP</B>
				</td>
				<td align="left" sortfield="END_IP">
					<B>结束IP</B>
				</td>
				<td align="left" sortfield="ADD_INFO">
					<B>附加信息</B>
				</td>
				<td align="left" sortfield="COLL_NAME">
					<B>采集器名称</B>
				</td>
				<td align="center" sortfield="IS_AUTOINS">
					<B>自动实例化</B>
				</td>
				<td align="center" width="100px" sortfield="STATUS">
					<B>状态</B>
				</td>
				<td align="center" width="45px">
					<B>操作</B>
				</td>
				<td align="center" width="45px">
					<B>编辑</B>
				</td>
				<td align="center" width="45px">
					<B>删除</B>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20"
					ondblclick="editTask('{TASK_ID}', '{COLL_ID}', '{TASK_TYPE}', '{BEGIN_IP}', '{END_IP}', '{ADD_INFO}', '{STATUS}', '{COMMUNITY}', '{TIME_POINT}', '{WEEK_MASK}', '{EXE_TYPE}', '{IS_AUTOINS}')">
					<td align="left">
						<xsl:value-of select="TASK_ID" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="TASK_TYPE=0">
								SNMP自动发现
							</xsl:when>
							<xsl:when test="TASK_TYPE=1">
								金数信码流、节目发现
							</xsl:when>
							<xsl:when test="TASK_TYPE=2">
								HTTP服务器自动发现
							</xsl:when>
							<xsl:when test="TASK_TYPE=3">
								FTP服务器自动发现
							</xsl:when>
							<xsl:when test="TASK_TYPE=4">
								数码视讯自动发现
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="TASK_TYPE" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td>
						<xsl:value-of select="BEGIN_IP" />
					</td>
					<td>
						<xsl:value-of select="END_IP" />
					</td>
					<td>
						<xsl:value-of select="ADD_INFO" />
					</td>
					<td>
						<xsl:value-of select="COLL_NAME" />
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="IS_AUTOINS=0">
								否
							</xsl:when>
							<xsl:otherwise>
								是
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="STATUS=0">
								就绪
							</xsl:when>
							<xsl:when test="STATUS=1">
								开始
							</xsl:when>
							<xsl:when test="STATUS=2">
								下发成功
							</xsl:when>
							<xsl:when test="STATUS=3">
								下发失败
							</xsl:when>
							<xsl:when test="STATUS=4">
								执行中
							</xsl:when>
							<xsl:when test="STATUS=5">
								执行成功
							</xsl:when>
							<xsl:when test="STATUS=6">
								执行失败
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:if test="EXE_TYPE = 0">
							<xsl:choose>
								<xsl:when test="STATUS=0">
									<a href="javascript:void(0)" onclick="operat('{TASK_ID}', '{COLL_ID}')">
										<img border="0" src="images/play.gif" />
									</a>
								</xsl:when>
								<xsl:when test="STATUS=3">
									<a href="javascript:void(0)" onclick="operat('{TASK_ID}', '{COLL_ID}')">
										<img border="0" src="images/play.gif" />
									</a>
								</xsl:when>
								<xsl:when test="STATUS=5">
									<a href="javascript:void(0)" onclick="operat('{TASK_ID}', '{COLL_ID}')">
										<img border="0" src="images/play.gif" />
									</a>
								</xsl:when>
								<xsl:when test="STATUS=6">
									<a href="javascript:void(0)" onclick="operat('{TASK_ID}', '{COLL_ID}')">
										<img border="0" src="images/play.gif" />
									</a>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</td>
					<td align="center">
						<a href="javascript:void(0)"
							onclick="editTask('{TASK_ID}', '{COLL_ID}', '{TASK_TYPE}', '{BEGIN_IP}', '{END_IP}', '{ADD_INFO}', '{STATUS}', '{COMMUNITY}', '{TIME_POINT}', '{WEEK_MASK}', '{EXE_TYPE}', '{IS_AUTOINS}')">
							<img border="0" src="images/edit.gif" />
						</a>
					</td>
					<td align="center">
						<a href="javascript:void(0)" onclick="deleteTask('{TASK_ID}')">
							<img border="0" src="images/drop.gif" />
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="RecordSet/Record1">
			<input type="hidden" value="{TotalCount}" id="hid_pageCount"></input>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
