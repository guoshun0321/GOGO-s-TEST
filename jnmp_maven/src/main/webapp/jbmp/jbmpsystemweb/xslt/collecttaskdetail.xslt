<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:template match="/">
		<table id="tabCollectTaskInfo"  border="0" cellspacing="0" cellpadding="2" width="94%" >
			<tr>
				<td width="80px" align="left">
					<b>编号</b>
				</td>
				<td align="left">
					<b>名称</b>
				</td>
				<td width="60px" align="center">
					<b>状态</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>启动</b>
				</td>
				<td style="width: 45px" align="center" >
					<b>停止</b>
				</td>
			</tr>
			<xsl:for-each select="RecordSet/Record">
				<tr height="20">
					<td>
						<xsl:value-of select="OBJ_ID"></xsl:value-of>
					</td>
					<td>
						<xsl:value-of select="OBJ_NAME"></xsl:value-of>
					</td>
					<td align="center">
						<xsl:choose>
							<xsl:when test="COLL_STATE=0">
								停止
							</xsl:when>
							<xsl:when test="COLL_STATE=1">
								启动
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="center">
						<xsl:if test ="COLL_STATE=0">
							<a href="javascript:void(0)" onclick="startObjectTask('{OBJ_ID}')">
								<img border="0" title="启动" src="images/start.png"/>
							</a>
						</xsl:if>
					</td>
					<td align="center">
						<xsl:if test ="COLL_STATE=1">
							<a href="javascript:void(0)" onclick="stopObjectTask('{OBJ_ID}')">
								<img border="0" title="停止" src="images/stop.png"/>
							</a>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
