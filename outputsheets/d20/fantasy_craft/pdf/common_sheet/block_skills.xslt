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
	TEMPLATE - VIEW SKILLS NUMBER

	Returns the number of skills that can
	be shown on the front page
====================================
====================================-->
	<xsl:template name="view.skills.num">
		<xsl:variable name="featureheight">
			<xsl:call-template name="features.right">
				<xsl:with-param name="features" select="/character/class_features/*"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="floor( (200-$featureheight) div 3.6) - 2"/>
	</xsl:template>


<!-- Begin Skills -->
	<xsl:template name="skills.empty">
		<xsl:param name="pos"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$pos mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row height="9pt">
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade)"/></xsl:call-template>
			<fo:table-cell><fo:block/></fo:table-cell>

			<fo:table-cell><fo:block/></fo:table-cell>

			<fo:table-cell number-columns-spanned="2"><fo:block/></fo:table-cell>
			<fo:table-cell><fo:block/></fo:table-cell>

			<fo:table-cell number-columns-spanned="2"><fo:block/></fo:table-cell>
			<fo:table-cell>
				<fo:block>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade, '.total')"/></xsl:call-template>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">=</fo:block>
			</fo:table-cell>
			<fo:table-cell><fo:block/></fo:table-cell>

			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell><fo:block/></fo:table-cell>

			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell><fo:block/></fo:table-cell>

		</fo:table-row>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - SKILLS TABLE
====================================
====================================-->
	<xsl:template match="skills">
		<xsl:param name="first_skill" select="0"/>
		<xsl:param name="last_skill" select="0"/>
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth"/>
		<!-- begin skills table -->
		
		<xsl:if test="count(skill) &gt;= $first_skill">
			<xsl:variable name="columns">
				<fo:table-column column-width="4mm"/>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 42" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="6mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="6mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="5mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="5mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="1mm"/>
				<fo:table-column column-width="6mm"/>
			</xsl:variable>

			<fo:table table-layout="fixed" width="100%" border-collapse="collapse">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.border'"/></xsl:call-template>
				<xsl:copy-of select="$columns"/>
				<fo:table-body>
					<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
						<fo:table-cell><fo:block/></fo:table-cell>

					</fo:table-row>
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.header'"/></xsl:call-template>
						<fo:table-cell><fo:block/></fo:table-cell>
						<fo:table-cell number-columns-spanned="2" border-top-width="1pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="0pt">
							<fo:block text-align="left" space-before.optimum="4pt" line-height="4pt" font-size="5pt">
								<xsl:text>TOTAL SKILLPOINTS: </xsl:text>
								<xsl:choose>
								<xsl:when test="skillpoints/eclipse_total &gt; 0">	
									<xsl:value-of select="skillpoints/eclipse_total"/>
								</xsl:when>
								<xsl:otherwise>
								<xsl:value-of select="skillpoints/total"/>
								<xsl:if test="skillpoints/unused &gt; 0">
									<xsl:text> (UNUSED: </xsl:text>
									<xsl:value-of select="skillpoints/unused"/>
									<xsl:text>)</xsl:text>
								</xsl:if>
								</xsl:otherwise>
								</xsl:choose>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="4">
							<fo:block text-align="end" line-height="10pt" font-weight="bold" font-size="10pt">SKILLS</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="10">
								<fo:block text-align="end" space-before.optimum="4pt" line-height="4pt" font-size="5pt">
									<xsl:text>MAX RANKS: </xsl:text>
									<xsl:value-of select="max_class_skill_level"/>/<xsl:value-of select="max_cross_class_skill_level"/>
								</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.header'"/></xsl:call-template>
						<fo:table-cell><fo:block/></fo:table-cell>
						<fo:table-cell number-columns-spanned="2">
							<fo:block font-weight="bold" font-size="8pt">
								SKILL NAME
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3">
							<fo:block font-size="3pt">
								KEY ABILITY
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3">
							<fo:block text-align="center" font-size="3pt">
								SKILL MODIFIER
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3">
							<fo:block text-align="center" font-size="3pt">
								ABILITY MODIFIER
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3">
							<fo:block text-align="center" font-size="3pt">
								RANKS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="2">
							<fo:block text-align="center" font-size="3pt">
								MISC MODIFIER
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>




			<fo:table table-layout="fixed" width="100%" border-collapse="collapse">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.border'"/></xsl:call-template>
				<xsl:copy-of select="$columns"/>
				<fo:table-body>
					<xsl:for-each select="skill">
						<xsl:if test="position() &gt;= $first_skill and position() &lt;= $last_skill">
							<xsl:variable name="shade">
								<xsl:choose>
									<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
									<xsl:otherwise>lightline</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<fo:table-row>
											<xsl:message>Test</xsl:message>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade)"/></xsl:call-template>
								<fo:table-cell>
									<fo:block font-size="6pt" font-family="ZapfDingbats">
										<xsl:if test="translate( substring(untrained,1,1), 'Y', 'y')='y'">
											&#x2713;
										</xsl:if>
										<xsl:if test="translate( substring(exclusive,1,1), 'Y', 'y')='y'">
											&#x2717;
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<xsl:choose>
									<!-->	<xsl:when test="string-length(name) &lt; 40">-->
										<xsl:when test="not(contains(type, 'SkillUse')) and string-length(name) &lt; 40">
											<fo:block space-before.optimum="1pt" font-size="7pt">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="contains(type, 'SkillUse') and string-length(name) &lt; 40">
											<fo:block space-before.optimum="1pt" font-size="7pt" font-style="italic">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="not(contains(type, 'SkillUse')) and string-length(name) &lt; 45">
											<fo:block space-before.optimum="1pt" font-size="6pt">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="contains(type, 'SkillUse') and string-length(name) &lt; 45">
											<fo:block space-before.optimum="1pt" font-size="6pt" font-style="italic">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="contains(type, 'SkillUse') and string-length(name) &gt; 44">
											<fo:block space-before.optimum="1pt" font-size="4pt" font-style="italic">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:otherwise>
											<fo:block space-before.optimum="1pt" font-size="4pt">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"><fo:block/></fo:table-cell>
								<fo:table-cell>
									<fo:block space-before.optimum="1pt" font-size="8pt">
										<xsl:value-of select="ability"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"><fo:block/></fo:table-cell>
								<fo:table-cell>
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade, '.total')"/></xsl:call-template>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:choose>
											<xsl:when test="contains($skillmastery,name)">
												<xsl:value-of select="concat(skill_mod,'*')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="skill_mod"/>
											</xsl:otherwise>
										</xsl:choose>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">=</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:value-of select="ability_mod"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">
										<xsl:if test="ranks &gt; 0">+</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:if test="ranks &gt; 0">
											<xsl:if test="contains(type, 'SkillUse')">[</xsl:if>
											<xsl:choose>
												<xsl:when test="round(ranks) = ranks">
													<xsl:value-of select="round(ranks)"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="ranks"/>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:if test="contains(type, 'SkillUse')">]</xsl:if>
										</xsl:if>
										
<!-->										<xsl:if test="ranks>0">
											<xsl:value-of select="ranks"/>
											</xsl:if>-->
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">
										<xsl:if test="misc_mod!=0">+</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:if test="misc_mod!=0">
											<xsl:value-of select="misc_mod"/>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
					</xsl:for-each>
					<xsl:call-template name="skills.empty"><xsl:with-param name="pos" select="count(skill)+1"/></xsl:call-template>
					<xsl:call-template name="skills.empty"><xsl:with-param name="pos" select="count(skill)+2"/></xsl:call-template>
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="17" padding-top="1pt">
							<fo:block text-align="center" font-size="6pt">
								<fo:inline font-family="ZapfDingbats">&#x2713;</fo:inline>: can be used untrained.
								<fo:inline font-family="ZapfDingbats">&#x2717;</fo:inline>: exclusive skills.
								*: Skill Mastery.
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
<!-- This is going to be the new Skill Info Section-->	
<!-->			<xsl:if test="count(conditional_modifiers/skillbonus) &gt; 0">
				<fo:table-body border-collapse="collapse" padding="0.5pt">
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.border'"/></xsl:call-template>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="17" padding-top="1pt">
							<fo:block text-align="center" font-size="8pt" font-weight="bold">Conditional Modifiers:</fo:block>
								<xsl:for-each select="conditional_modifiers/skillbonus">
									<fo:block font-size="8pt" space-before.optimum="2pt"><xsl:value-of select="description"/></fo:block>
								</xsl:for-each>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</xsl:if>-->
<!-- End New Skill Info Section-->
			</fo:table>
		</xsl:if>
		<!-- END Skills table-->
	</xsl:template>

<!-- This is a Separate Skill Info
====================================
====================================
	TEMPLATE - Skills Info TABLE
====================================
====================================-->
	<xsl:template match="skillinfo">
		<!-- BEGIN Skills table -->
		<xsl:if test="count(conditional_modifiers/skillbonus) &gt; 0">
<!-->	Attempting to get shading to work. So far, nothing.
		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade)"/></xsl:call-template>
-->
		<fo:table table-layout="fixed" width="100%" space-before="2mm" padding="0.5pt">
			<fo:table-column column-width="86mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="30mm"/>
				<fo:table-body>
					<fo:table-row>
											<xsl:message>Test END</xsl:message>
						<fo:table-cell padding-top="1pt" border-width="0.5pt" border-style="solid">
							<fo:block text-align="center" font-size="8pt" font-weight="bold">Conditional Skill Modifiers:</fo:block>
								<xsl:for-each select="conditional_modifiers/skillbonus">
									<fo:block font-size="8pt" space-before.optimum="1pt"><xsl:value-of select="description"/></fo:block>
								</xsl:for-each>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
		</fo:table>
		</xsl:if>
		<!-- END Skills table -->
	</xsl:template>


</xsl:stylesheet>
