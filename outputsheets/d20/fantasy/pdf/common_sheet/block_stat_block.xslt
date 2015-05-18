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
	TEMPLATE - ABILITY BLOCK
====================================
====================================-->
	<xsl:template match="abilities">
		<!-- BEGIN Ability Block -->
		<fo:table table-layout="fixed">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.22 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- 0.29*$pagePrintableWidth total -->
			<fo:table-column column-width="1mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.13 * (0.29 * $pagePrintableWidth - 9)" />mm</xsl:attribute>
			</fo:table-column>

	<xsl:choose>
		<xsl:when test="/character/house_var/oldstyleabilitystatblockdisplay &gt; 0">
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY NAME</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE MOD</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY MOD</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP MOD</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="ability">
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">
								<xsl:value-of select="name/short"/>
							</fo:block>
							<fo:block line-height="4pt" font-size="4pt">
								<xsl:value-of select="name/long"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.base.score'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="base"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.base.modifier'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="basemod"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.score'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="no_temp_score"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.modifier'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="no_temp_modifier"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.score'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="score"/>
							</fo:block>
							</xsl:if>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell height="4pt">	<!-- Temp Score and Mod-->
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.modifier'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="modifier"/>
							</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
						<fo:table-cell/>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
	</xsl:when>
<xsl:otherwise>
<!--><xsl:if test="/character/house_var/oldstyleabilitystatblockdisplay &lt; 1">-->
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="4.5pt" font-size="4pt">ABILITY NAME</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">EQUIPPED SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="4.5pt" font-size="4pt">PENALTY</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="ability">
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<fo:table-cell>
						<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">
								<xsl:value-of select="name/short"/>
							</fo:block>
							<fo:block line-height="4pt" font-size="4pt">
								<xsl:value-of select="name/long"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
						<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.base.score'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="base"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					
						<xsl:if test="no_temp_score != base">
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'stat.base.score'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="no_temp_score"/>
								</fo:block>
							</fo:table-cell>
						</xsl:if>
						<xsl:if test="no_temp_score = base">
							<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.modifier'"/>
							</xsl:call-template>
							</fo:table-cell>
						</xsl:if>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.base.modifier'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="no_temp_modifier"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
		<!--><fo:table-cell height="4pt"/>-->
						<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.score'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="score"/>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.modifier'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="modifier"/>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2pt">
											<xsl:message>Test END</xsl:message>
						<fo:table-cell>
							<fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
	</xsl:otherwise>
	</xsl:choose>
<!-->		</xsl:if>-->
		</fo:table>
		<!-- END Ability Block -->
	</xsl:template>


</xsl:stylesheet>