<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:xalan="http://xml.apache.org/xalan"
	>

	<xsl:import href="../../../../xsltsl-1.1/stdlib.xsl"/>
	<xsl:import href="../inc_pagedimensions.xslt"/>

	<!--
====================================
====================================
	TEMPLATE - BIO
====================================
====================================-->
	<xsl:template name="bio.entry">
		<xsl:param name="title"/>
		<xsl:param name="value"/>
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell padding-top="1pt" height="9pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio'"/>
				</xsl:call-template>
				<fo:block font-size="9pt">
					<xsl:value-of select="$value"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell padding-top="0.5pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio.title'"/>
				</xsl:call-template>
				<fo:block font-size="6pt"><xsl:value-of select="$title"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - BIO
====================================
====================================-->
	<xsl:template match="basics" mode="bio">
		<!-- BEGIN BIO Pages -->
<!-->		<xsl:if test="string-length(translate(normalize-space(concat(description,bio)), ' ', '')) &gt; 0">	-->
			<fo:page-sequence>
				<xsl:attribute name="master-reference">Portrait</xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body" font-size="8pt">
					<fo:block font-size="14pt" break-before="page" span="all">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<xsl:value-of select="name"/>
						<xsl:if test="string-length(followerof) &gt; 0">- <xsl:value-of select="followerof"/>
						</xsl:if>
					</fo:block>
					<fo:block>
						<fo:table table-layout="fixed">
							<xsl:choose>
								<xsl:when test="string-length(portrait) &gt; 0">
									<fo:table-column>
										<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 2" />mm</xsl:attribute>
									</fo:table-column>
									<fo:table-column column-width="2mm"/>
									<fo:table-column>
										<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 2" />mm</xsl:attribute>
									</fo:table-column>
								</xsl:when>
								<xsl:otherwise>
									<fo:table-column>
										<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth" />mm</xsl:attribute>
									</fo:table-column>
								</xsl:otherwise>
							</xsl:choose>
							<fo:table-body>
								<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell>
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio'"/>
										</xsl:call-template>
										<fo:block font-size="9pt">
											<xsl:value-of select="race"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-rows-spanned="36"/>
									<xsl:if test="string-length(portrait/portrait) &gt; 0">
										<fo:table-cell display-align="center" number-rows-spanned="36">
											<xsl:call-template name="attrib">
												<xsl:with-param name="attribute" select="'picture'"/>
											</xsl:call-template>
											<fo:block start-indent="1mm" height="100mm">
												<xsl:variable name="portrait_file" select="portrait/portrait"/>
												<fo:external-graphic src="file:{$portrait_file}" content-width="92mm" content-height="scale-to-fit" scaling="uniform"/>
											</fo:block>
										</fo:table-cell>
									</xsl:if>
								</fo:table-row>
								<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell padding-top="1pt">
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio.title'"/>
										</xsl:call-template>
										<fo:block font-size="6pt">RACE</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'AGE'"/>
									<xsl:with-param name="value" select="age"/>
								</xsl:call-template>
		<!--	Remove VISION TEST					<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell padding-top="1pt" height="9pt">
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio'"/>
										</xsl:call-template>
										<fo:block font-size="9pt">
											Vision Test:
											<xsl:value-of select="vision/all"/>
											<xsl:if test="vision/all = ''">Normal</xsl:if>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>	-->
								<fo:table-row>
											<xsl:message>Test</xsl:message>
									<fo:table-cell padding-top="0.5pt">
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio.title'"/>
										</xsl:call-template>
										<fo:block font-size="6pt"></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'GENDER'"/>
									<xsl:with-param name="value" select="gender/long"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'VISION'"/>
									<xsl:with-param name="value" select="vision/all"/>	
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'ALIGNMENT'"/>
									<xsl:with-param name="value" select="alignment/long"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'DOMINANT HAND'"/>
									<xsl:with-param name="value" select="handed"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'HEIGHT'"/>
									<xsl:with-param name="value" select="height/total"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'WEIGHT'"/>
									<xsl:with-param name="value" select="weight/weight_unit"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'EYE COLOUR'"/>
									<xsl:with-param name="value" select="eyes/color"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'SKIN COLOUR'"/>
									<xsl:with-param name="value" select="skin/color"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'HAIR / HAIR STYLE'"/>
									<xsl:with-param name="value" select="concat(hair/color, ', ', hair/length)"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'PHOBIAS'"/>
									<xsl:with-param name="value" select="phobias"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'PERSONALITY TRAITS'"/>
									<xsl:with-param name="value">
										<xsl:for-each select="personality/trait">
											<xsl:if test="position() &gt; 1">, </xsl:if>
											<xsl:value-of select="."/>
										</xsl:for-each>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'INTERESTS'"/>
									<xsl:with-param name="value" select="interests"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'SPOKEN STYLE / CATCH PHRASE'"/>
									<xsl:with-param name="value" select="concat(speechtendency, ', ', catchphrase)"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'RESIDENCE'"/>
									<xsl:with-param name="value" select="residence"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'LOCATION'"/>
									<xsl:with-param name="value" select="location"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'REGION'"/>
									<xsl:with-param name="value" select="region"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'DEITY'"/>
									<xsl:with-param name="value" select="deity/name"/>	
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'Race Type'"/>
									<xsl:with-param name="value" select="race/racetype"/>
								</xsl:call-template>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'Race Sub Type'"/>
									<xsl:with-param name="value" select="race/racesubtype"/>
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="14pt" font-weight="bold" space-before="5mm" span="all">
						Description:
					</fo:block>
					<fo:block font-size="9pt" text-indent="5mm" span="all">
						<xsl:call-template name="paragraghlist">
							<xsl:with-param name="tag" select="'description'"/>
						</xsl:call-template>
					</fo:block>
					<fo:block font-size="14pt" font-weight="bold" span="all">
						Biography:
					</fo:block>
					<fo:block font-size="9pt" text-indent="5mm" span="all">
						<xsl:call-template name="paragraghlist">
							<xsl:with-param name="tag" select="'bio'"/>
						</xsl:call-template>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
<!-->		</xsl:if>	-->
		<!-- END BIO Pages -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CHARACTER NOTES
====================================
====================================-->
	<xsl:template match="notes" mode="bio">
		<!-- BEGIN CHARACTER NOTES Pages -->
		<xsl:if test="count(.//note) &gt; 0">
			<fo:page-sequence master-reference="Portrait 2 Column">
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body" font-size="8pt">
					<fo:block font-size="14pt" font-weight="bold" space-after.optimum="2mm" break-before="page" span="all">
						Notes:
					</fo:block>
					<xsl:for-each select="note">
						<fo:block font-size="12pt" space-after.optimum="2mm" space-before.optimum="5mm">
							<xsl:value-of select="name"/>:
						</fo:block>
						<fo:block font-size="9pt" text-indent="5mm">
							<xsl:call-template name="paragraghlist">
								<xsl:with-param name="tag" select="'value'"/>
							</xsl:call-template>
						</fo:block>
					</xsl:for-each>
				</fo:flow>
			</fo:page-sequence>
		</xsl:if>
		<!-- END CHARACTER NOTES Pages -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Campaign History
====================================
====================================-->
	<xsl:template match="campaign_histories" mode="bio">
		<!-- BEGIN Campaign History Pages -->
		<xsl:if test="count(.//campaign_history) &gt; 0">
			<fo:page-sequence master-reference="Portrait 2 Column">
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body" font-size="8pt">
					<xsl:for-each select="campaign_history">
						<fo:block font-size="12pt" space-after.optimum="2mm" space-before.optimum="5mm">
							<xsl:value-of select="campaign"/>:
							<xsl:value-of select="adventure"/>:
							<xsl:value-of select="party"/>:
							<xsl:value-of select="date"/>:
							<xsl:value-of select="xp"/>:
							<xsl:value-of select="gm"/>:
							<xsl:value-of select="text"/>:
						</fo:block>
					</xsl:for-each>
				</fo:flow>
			</fo:page-sequence>
		</xsl:if>
		<!-- END Campaign History Pages -->
	</xsl:template>


</xsl:stylesheet>