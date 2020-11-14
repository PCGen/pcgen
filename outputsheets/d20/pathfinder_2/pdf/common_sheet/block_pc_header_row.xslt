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
	TEMPLATE - CHARACTER HEADER
====================================
====================================-->
	<xsl:template match="basics">	
		<!-- Character Header -->
		<xsl:choose>		<!-- Determine which header to use -->
			<xsl:when test="rules/society/os > 0">		
				<!-- society Header -->
				<fo:table table-layout="fixed" width="100%">
				<xsl:attribute name="width"><xsl:value-of select="$pagePrintableWidth" />mm</xsl:attribute>
				<xsl:choose>
					<xsl:when test="string-length(portrait/portrait) &gt; 0">
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column column-width="2mm"/>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
						</fo:table-column>
					</xsl:when>
					<xsl:otherwise>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Class -->
						<fo:table-column column-width="2mm"/>
						<!---->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Experience -->
						<fo:table-column column-width="2mm"/>
						<!-- -->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Race -->
						<fo:table-column column-width="2mm"/>
						<!-- -->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Size -->
						<fo:table-column column-width="2mm"/>
						<!-- -->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Height -->
						<fo:table-column column-width="2mm"/>
						<!-- -->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Weight -->
						<fo:table-column column-width="2mm"/>
						<!-- -->
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
						</fo:table-column>
						<!-- Vision -->
						<fo:table-column column-width="2mm"/>
					</xsl:otherwise>
				</xsl:choose>	
				<fo:table-body>
					<fo:table-row>
												<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="10pt" font-weight="bold">
								<xsl:value-of select="name"/>
								<xsl:if test="string-length(followerof) &gt; 0">	- <xsl:value-of select="followerof"/>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-columns-spanned="3" font-weight="bold">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="10pt">
								<xsl:value-of select="playername"/>
								
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="deity/name"/>	
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="region"/>	
							</fo:block>
						</fo:table-cell>

						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell padding-top="2.5pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="alignment/long"/>	
							</fo:block>
						</fo:table-cell>
						<xsl:if test="string-length(portrait/portrait_thumb) &gt; 0">
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell number-rows-spanned="6">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'picture'"/>
								</xsl:call-template>
								<fo:block>
									<xsl:variable name="portrait_file" select="portrait/portrait_thumb"/>
									<fo:external-graphic src="file:{$portrait_file}" content-width="22mm" content-height="scale-to-fit" scaling="uniform">
										<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
									</fo:external-graphic>
								</fo:block>
							</fo:table-cell>
						</xsl:if>
					</fo:table-row>
					<fo:table-row>
												<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">Character Name</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">Player Name</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-columns-spanned="1">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">Deity</fo:block>		
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-columns-spanned="1">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">Region</fo:block>	
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">Alignment</fo:block>	
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
					</fo:table-row>
	<!-- Second Row -->
					<fo:table-row>
												<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
	<!-->						<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="classes/shortform"/>
							</fo:block>-->
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:variable name = "classcount" ><xsl:value-of select="count(classes/class)"/></xsl:variable>
								<xsl:for-each select = "classes/class">
									<xsl:sort select="sequence"/>
									<xsl:variable name="classname">
										<xsl:value-of select="translate(name, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
									</xsl:variable>
									<xsl:variable name="archetypecount">
										<xsl:value-of select="count(/character/basics/archetypes/archetype[contains(type,$classname)])"/>
									</xsl:variable>
									<xsl:value-of select="name"/>
										<xsl:if test="$archetypecount &gt; 0">
											<xsl:text> (</xsl:text>
											<xsl:for-each select="/character/basics/archetypes/archetype[contains(type,$classname)]">
											<xsl:value-of select="name"/>
											<xsl:if test="position() &lt; $archetypecount">, </xsl:if>
											</xsl:for-each>
												<xsl:text>)</xsl:text>
										</xsl:if>
										<xsl:text> </xsl:text>
										<xsl:value-of select="level"/>
										<xsl:if test="position() &lt; $classcount">, </xsl:if>
								</xsl:for-each>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="race"/>
								<xsl:if test="string-length(race/raceextra) &gt; 0">
									(<xsl:value-of select="race/raceextra"/>)
								</xsl:if>
								<xsl:if test="string-length(race/racetype) &gt; 0"> / 
									<xsl:value-of select="race/racetype"/>
								</xsl:if>
								<xsl:if test="string-length(race/racesubtype) &gt; 0"> / 
									<xsl:value-of select="race/racesubtype"/>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="size/long"/>
								<xsl:if test="face/short != ''"> / <xsl:value-of select="face/short"/></xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="height/total"/> / 
								<xsl:value-of select="weight/weight_unit"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
	<!-- SPACE -->
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="rules/society/id_number"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

					</fo:table-row>
					<fo:table-row>
												<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">CLASS</fo:block>
						</fo:table-cell>
						
					
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-columns-spanned="3">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">RACE</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">SIZE / FACE</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">HEIGHT / WEIGHT</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">CHARACTER ID</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

					</fo:table-row>
	<!--	Third Row-->
					<fo:table-row>
												<xsl:message>Test</xsl:message>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="classes/levels_total"/>
								<xsl:if test="classes/levels_total != classes/levels_ecl">/<xsl:value-of select="classes/levels_ecl"/>
								</xsl:if>
								(<xsl:value-of select="cr"/>)
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="experience/current"/> / <xsl:value-of select="experience/next_level"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="age"/>
								<xsl:if test="birthday != ''"> (<xsl:value-of select="birthday"/>)</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="gender/long"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="eyes/color"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="hair/color"/>
									<xsl:if test="hair/color != '' and hair/length !=''">, <xsl:value-of select="hair/length"/></xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" padding-top="3pt">	
								<xsl:value-of select="rules/society/faction"/>	
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

					</fo:table-row>

	<!-- Third ROW Text-->
					<fo:table-row>
												<xsl:message>Test END</xsl:message>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">
								<xsl:text>Character Level</xsl:text>
								<xsl:if test="classes/levels_total != classes/levels_ecl">
									<xsl:text>/ECL</xsl:text>
								</xsl:if>
								<xsl:text> (CR)</xsl:text>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">EXP/NEXT LEVEL</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">AGE</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">GENDER</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">EYES</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">HAIR</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'bio.title'"/>
							</xsl:call-template>
							<fo:block font-size="6pt" padding-top="1pt">FACTION</fo:block>	
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

					</fo:table-row>	
				</fo:table-body>
			</fo:table>
				</xsl:when>
				<xsl:otherwise>
					<fo:table table-layout="fixed" width="100%">
			<xsl:attribute name="width"><xsl:value-of select="$pagePrintableWidth" />mm</xsl:attribute>
			<xsl:choose>
				<xsl:when test="string-length(portrait/portrait) &gt; 0">
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
					<fo:table-column column-width="2mm"/>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
					</fo:table-column>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Class -->
					<fo:table-column column-width="2mm"/>
					<!---->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Experience -->
					<fo:table-column column-width="2mm"/>
					<!-- -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Race -->
					<fo:table-column column-width="2mm"/>
					<!-- -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Size -->
					<fo:table-column column-width="2mm"/>
					<!-- -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Height -->
					<fo:table-column column-width="2mm"/>
					<!-- -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.14 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Weight -->
					<fo:table-column column-width="2mm"/>
					<!-- -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($pagePrintableWidth - 12)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- Vision -->
					<fo:table-column column-width="2mm"/>
				</xsl:otherwise>
			</xsl:choose>	
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="name"/>
							<xsl:if test="string-length(followerof) &gt; 0">	- <xsl:value-of select="followerof"/>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell number-columns-spanned="3" font-weight="bold">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="playername"/>
							
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="deity/name"/>	
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="region"/>	
						</fo:block>
					</fo:table-cell>

					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell padding-top="2.5pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="alignment/long"/>	
						</fo:block>
					</fo:table-cell>
					<xsl:if test="string-length(portrait/portrait_thumb) &gt; 0">
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell number-rows-spanned="6">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'picture'"/>
							</xsl:call-template>
							<fo:block>
								<xsl:variable name="portrait_file" select="portrait/portrait_thumb"/>
								<fo:external-graphic src="file:{$portrait_file}" content-width="22mm" content-height="scale-to-fit" scaling="uniform">
									<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
					</xsl:if>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Character Name</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Player Name</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Deity</fo:block>		
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Region</fo:block>	
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Alignment</fo:block>	
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
				</fo:table-row>
<!-- Second Row -->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
<!-->						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="classes/shortform"/>
						</fo:block>-->
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:variable name = "classcount" ><xsl:value-of select="count(classes/class)"/></xsl:variable>
							<xsl:for-each select = "classes/class">
								<xsl:sort select="sequence"/>
								<xsl:variable name="classname">
									<xsl:value-of select="translate(name, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
								</xsl:variable>
								<xsl:variable name="archetypecount">
									<xsl:value-of select="count(/character/basics/archetypes/archetype[contains(type,$classname)])"/>
								</xsl:variable>
								<xsl:value-of select="name"/>
									<xsl:if test="$archetypecount &gt; 0">
										<xsl:text> (</xsl:text>
										<xsl:for-each select="/character/basics/archetypes/archetype[contains(type,$classname)]">
										<xsl:value-of select="name"/>
										<xsl:if test="position() &lt; $archetypecount">, </xsl:if>
										</xsl:for-each>
											<xsl:text>)</xsl:text>
									</xsl:if>
									<xsl:text> </xsl:text>
									<xsl:value-of select="level"/>
									<xsl:if test="position() &lt; $classcount">, </xsl:if>
							</xsl:for-each>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="race"/>
							<xsl:if test="string-length(race/raceextra) &gt; 0">
								(<xsl:value-of select="race/raceextra"/>)
							</xsl:if>
							<xsl:if test="string-length(race/racetype) &gt; 0"> / 
								<xsl:value-of select="race/racetype"/>
							</xsl:if>
							<xsl:if test="string-length(race/racesubtype) &gt; 0"> / 
								<xsl:value-of select="race/racesubtype"/>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="size/long"/>
							<xsl:if test="face/short != ''"> / <xsl:value-of select="face/short"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="height/total"/> / 
							<xsl:value-of select="weight/weight_unit"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="vision/all"/>
							<xsl:if test="vision/all = ''">Normal</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">CLASS</fo:block>
					</fo:table-cell>
					
				
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">RACE</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">SIZE / FACE</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">HEIGHT / WEIGHT</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">VISION</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
<!--	Third Row-->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="classes/levels_total"/>
							<xsl:if test="classes/levels_total != classes/levels_ecl">/<xsl:value-of select="classes/levels_ecl"/>
							</xsl:if>
							(<xsl:value-of select="cr"/>)
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="experience/current"/> / <xsl:value-of select="experience/next_level"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="age"/>
							<xsl:if test="birthday != ''"> (<xsl:value-of select="birthday"/>)</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="gender/long"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="eyes/color"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="hair/color"/>
								<xsl:if test="hair/color != '' and hair/length !=''">, <xsl:value-of select="hair/length"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">	
							<xsl:if test="poolpoints/cost &gt; 0"> <xsl:value-of select="poolpoints/cost"/> </xsl:if>	
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>

<!-- Third ROW Text-->
				<fo:table-row>
											<xsl:message>Test END</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">
							<xsl:text>Character Level</xsl:text>
							<xsl:if test="classes/levels_total != classes/levels_ecl">
								<xsl:text>/ECL</xsl:text>
							</xsl:if>
							<xsl:text> (CR)</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">EXP/NEXT LEVEL</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">AGE</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">GENDER</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">EYES</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">HAIR</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Points</fo:block>	
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>	
			</fo:table-body>
		</fo:table>	<!-- Default Standard Fantasy -->
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - STANDARD HEADER
====================================
====================================-->

	<xsl:template match="header_standard">
		
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - society HEADER
====================================
====================================-->

	<!-- society Header -->
	<xsl:template match="header_pfs">

	</xsl:template>


</xsl:stylesheet>
