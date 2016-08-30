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
	TEMPLATE - HP
====================================
====================================-->
	<xsl:template match="character" mode="hp_table">	
		<fo:table table-layout="fixed" width="100%">
			<xsl:choose>
				<xsl:when test="hit_points/usealternatedamage = 0">
					<fo:table-column column-width="12mm" />
					<!-- TITLE -->
					<fo:table-column column-width="2mm"/>
					<!-- space -->
					<fo:table-column column-width="8mm" />
					<!-- TOTAL HP -->
					<fo:table-column column-width="2mm"/>
					<!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.28 * (0.71 * $pagePrintableWidth - 31)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- WOUNDS -->
					<fo:table-column column-width="2mm"/>
					<!-- space-->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.28 * (0.71 * $pagePrintableWidth - 31)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- SUBDUAL -->
					<fo:table-column column-width="2mm"/>
					<!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.71 * $pagePrintableWidth - 31)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- DR -->
					<fo:table-column column-width="3mm"/>
					<!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.27 * (0.71 * $pagePrintableWidth - 31)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- SPEED -->
					<fo:table-body>
						<fo:table-row>
											<xsl:message>Test</xsl:message>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell>
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">WOUNDS/CURRENT HP</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">SUBDUAL DAMAGE</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">DAMAGE REDUCTION</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="6pt">SPEED</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
											<xsl:message>Test</xsl:message>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">HP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">hit points</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="hit_points/points"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.current'"/>
								</xsl:call-template>
								<fo:block font-size="10pt"/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.subdual'"/>
								</xsl:call-template>
								<fo:block font-size="10pt"/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'damage.reduction'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="hit_points/damage_reduction"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell>

							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'speed'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="basics/move/all"/>
									<xsl:value-of select="basics/move/move/maneuverability"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</xsl:when>
				<xsl:otherwise>	
					<fo:table-column column-width="12mm" />
					<!-- TITLE Vitality -->
					<fo:table-column column-width="2mm"/>
					<!-- space -->
					<fo:table-column column-width="8mm" />
					<!-- TOTAL Vitality -->
					<fo:table-column column-width="2mm"/><!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.18 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- WOUNDS -->
					<fo:table-column column-width="2mm"/><!-- space-->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.18 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- SUBDUAL -->
					<fo:table-column column-width="2mm"/><!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.11 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- TITLE Wound points-->
					<fo:table-column column-width="2mm"/><!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- TOTAL Wound points -->
					<fo:table-column column-width="2mm"/><!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.18 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- DR -->
					<fo:table-column column-width="2mm"/><!-- space -->
					<fo:table-column>
						<xsl:attribute name="column-width"><xsl:value-of select="0.27 * (0.71 * $pagePrintableWidth - 34)" />mm</xsl:attribute>
					</fo:table-column>
					<!-- SPEED -->
					<fo:table-body>
						<fo:table-row>
											<xsl:message>Test</xsl:message>
							<fo:table-cell><fo:block/></fo:table-cell><!-- TITLE Vitality -->
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell>	<!-- TOTAL Vitality -->
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">WOUNDS/CURRENT HP</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">SUBDUAL DAMAGE</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell><fo:block/></fo:table-cell><!-- TITLE Wound points -->
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell>	<!-- TOTAL Wound points -->
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">DAMAGE REDUCTION</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="6pt">SPEED</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
											<xsl:message>Test</xsl:message>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">VP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">Vitality</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="hit_points/points"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.current'"/>
								</xsl:call-template>
								<fo:block font-size="10pt"/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.subdual'"/>
								</xsl:call-template>
								<fo:block font-size="10pt"/>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">WP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">Wound Points</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="10pt">
									<xsl:value-of select="hit_points/alternate"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'damage.reduction'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="hit_points/damage_reduction"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell><fo:block/></fo:table-cell><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'speed'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="basics/move/all"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</xsl:otherwise>
			</xsl:choose>
		</fo:table>
		<!-- END HP Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - AC TABLE
====================================
====================================-->
	<xsl:template match="armor_class">	
		<fo:table table-layout="fixed" width="100%" space-before="2pt">
			<fo:table-column column-width="12mm"/>	<!--	1	-->
			<!-- TITLE -->
			<fo:table-column column-width="1mm"/>	<!--	2	-->
			<!-- space -->
			<fo:table-column column-width="8mm"/>	<!--	3	-->
			<!-- TOTAL AC -->
			<fo:table-column column-width="1mm"/>	<!--	4	-->
			<!-- : -->
			<fo:table-column column-width="8mm"/>	<!--	5	-->
			<!-- FLAT -->
			<fo:table-column column-width="1mm"/>	<!--	6	-->
			<!-- : -->
			<fo:table-column column-width="8mm"/>	<!--	7	-->
			<!-- TOUCH -->
			<fo:table-column column-width="2mm"/>	<!--	8	-->
			<!-- = -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	9	-->
			<!-- BASE -->
			<fo:table-column column-width="2mm"/>	<!--	10	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>	<!--	11	-->
			</fo:table-column>
			<!-- armour -->
			<fo:table-column column-width="2mm"/>	<!--	12	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	13	-->
			<!-- armour -->
			<fo:table-column column-width="2mm"/>	<!--	14	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	15	-->
			<!-- stat -->
			<fo:table-column column-width="2mm"/>	<!--	16	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	17	-->
			<!--size -->
			<fo:table-column column-width="2mm"/>	<!--	18	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	19	-->
			<!-- natural armour-->
			<fo:table-column column-width="2mm"/>	<!--	20	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.09 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	21	-->
			<!-- deflection -->
			<fo:table-column column-width="2mm"/>	<!--	22	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.09 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	23	-->
			<!-- Dodge -->
			<fo:table-column column-width="2mm"/>	<!--	24	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	25	-->
			<!-- misc-->
			<fo:table-column column-width="2mm"/>	<!--	26	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	27	-->
			<!-- miss chance -->
			<fo:table-column column-width="2mm"/>	<!--	28	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	29	-->
			<!-- arcane spell failure -->
			<fo:table-column column-width="2mm"/>	<!--	30	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	31	-->
			<!-- armour check-->
			<fo:table-column column-width="2mm"/>	<!--	32	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>	<!--	33	-->
			<!-- SR <33 columns> -->
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">AC</fo:block>
						<fo:block line-height="4pt" font-size="4pt">armor class</fo:block>
					</fo:table-cell>	<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>	<!--	3	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">=</fo:block>
					</fo:table-cell>	<!--	8	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="base"/>
						</fo:block>
					</fo:table-cell>	<!--	9	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	10	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="armor_bonus"/>
						</fo:block>
					</fo:table-cell>	<!--	11	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	12	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="shield_bonus"/>
						</fo:block>
					</fo:table-cell>	<!--	13	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	14	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	<!--	15	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	16	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="natural"/>
						</fo:block>
					</fo:table-cell>	<!--	19	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	20	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="misc"/>
						</fo:block>
					</fo:table-cell>	<!--	21	-->
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell><fo:block/></fo:table-cell>
<!-->	<fo:table-cell><fo:block/></fo:table-cell>	-->
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>	<!--	3	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	4	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE</fo:block>
					</fo:table-cell>	<!--	9	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	10	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARMOR BONUS</fo:block>
					</fo:table-cell>	<!--	11	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	12	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SHIELD BONUS</fo:block>
					</fo:table-cell>	<!--	13	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	14	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">STAT</fo:block>
					</fo:table-cell>	<!--	15	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	18	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="3pt">NATURAL ARMOR</fo:block>
					</fo:table-cell>	<!--	19	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	20	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="3pt">MISC</fo:block>
					</fo:table-cell>	<!--	21	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	22	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	23	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	24	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	25	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	26	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	27	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	28	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	29	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	30	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	31	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	32	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	33	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	16	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	17	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	5	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	6	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	7	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	8	-->
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END AC Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Initiative TABLE + Misc
====================================
====================================-->
	<xsl:template match="initiative">
		<!-- BEGIN ini-base table -->
		<fo:table table-layout="fixed">		<!--space-before="2pt"-->
			<!-- 0.26 * $pagePrintableWidth - mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.49 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>		<!--	1	-->
			<fo:table-column column-width="2mm"/>		<!--	2	-->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>		<!--	3	-->
			<fo:table-column column-width="2mm"/>		<!--	4	-->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>		<!--	5	-->
			<fo:table-column column-width="2mm"/>		<!--	6	-->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>		<!--	7	-->
			<fo:table-column column-width="4mm"/>		<!--	8	-->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	9	-->
			<!-- miss chance -->
			<fo:table-column column-width="2mm"/>		<!--	10	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	11	-->
			<!-- arcane spell failure -->
			<fo:table-column column-width="2mm"/>		<!--	12	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	13	-->
			<!-- armour check-->
			<fo:table-column column-width="2mm"/>		<!--	14	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>		<!--	15	-->
			<!-- SR -->
			<fo:table-body>
				<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">INITIATIVE</fo:block>
						<fo:block line-height="4pt" font-size="4pt">modifier</fo:block>
					</fo:table-cell>		<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	2	-->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>		<!--	3	-->
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">=</fo:block>
					</fo:table-cell>		<!--	4	-->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.general'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="dex_mod"/>
						</fo:block>
					</fo:table-cell>		<!--	5	-->
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>		<!--	6	-->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.general'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="misc_mod"/>
						</fo:block>
					</fo:table-cell>		<!--	7	-->
					<!--	15	-->
				</fo:table-row>
<!--				<fo:table-row height="0.5pt"/>	-->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	2	-->
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>		<!--	3	-->
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	4	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">DEX MODIFIER</fo:block>
					</fo:table-cell>		<!--	5	-->
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	6	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC MODIFIER</fo:block>
					</fo:table-cell>		<!--	7	-->
					<!-- New Stuff	-->
					<fo:table-cell><fo:block/></fo:table-cell>		<!--	8	-->
					<!--	15	-->
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END ini-base table -->
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - 5e AC TABLE
====================================
====================================-->
	<xsl:template match="new_ac">	
		<fo:table table-layout="fixed" space-before="2pt">
			<fo:table-column column-width="12mm"/>	<!--	1	-->
			<!-- TITLE -->
			<fo:table-column column-width="1mm"/>	<!--	2	-->
			<!-- space -->
			<fo:table-column column-width="8mm"/>	<!--	3	-->
			<!-- TOTAL AC -->
			<fo:table-column column-width="1mm"/>	<!--	4	-->
			<!-- = -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	5	-->
			<!-- BASE -->
			<fo:table-column column-width="2mm"/>	<!--	6	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>	<!--	11	-->
			</fo:table-column>						<!--	7	-->
			<!-- armour -->
			<fo:table-column column-width="2mm"/>	<!--	8	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	9	-->
			<!-- armour -->
			<fo:table-column column-width="2mm"/>	<!--	10	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	11	-->
			<!-- stat -->
			<fo:table-column column-width="2mm"/>	<!--	12	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	13	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	14	-->
			<!-- natural armour-->
			<fo:table-column column-width="2mm"/>	<!--	15	-->
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	16	-->
			<!-- misc-->
			<fo:table-column column-width="2mm"/>	<!--	17	-->
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>						<!--	18	-->
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">AC</fo:block>
						<fo:block line-height="4pt" font-size="4pt">armor class</fo:block>
					</fo:table-cell>	<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>	<!--	3	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">=</fo:block>
					</fo:table-cell>	<!--	4	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="base"/>
						</fo:block>
					</fo:table-cell>	<!--	5	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	6	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="armor_bonus"/>
						</fo:block>
					</fo:table-cell>	<!--	7	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	8	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="shield_bonus"/>
						</fo:block>
					</fo:table-cell>	<!--	9	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	10	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	<!--	11	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt">+</fo:block>
					</fo:table-cell>	<!--	12	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="natural"/>
						</fo:block>
					</fo:table-cell>	<!--	13	-->
					
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="10pt">
							<xsl:value-of select="misc"/>
						</fo:block>
					</fo:table-cell>	<!--	14	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	15	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->

				<!-->	<fo:table-cell><fo:block/></fo:table-cell>	-->
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	1	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>	<!--	3	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	4	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE</fo:block>
					</fo:table-cell>	<!--	5	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	6	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARMOR BONUS</fo:block>
					</fo:table-cell>	<!--	7	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	8	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SHIELD BONUS</fo:block>
					</fo:table-cell>	<!--	9	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	10	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">STAT</fo:block>
					</fo:table-cell>	<!--	11	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	12	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="3pt">NATURAL ARMOR</fo:block>
					</fo:table-cell>	<!--	13	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	14	-->
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC</fo:block>
					</fo:table-cell>	<!--	15	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->
					<fo:table-cell><fo:block/></fo:table-cell>	<!--	2	-->

		<!-->			<fo:table-cell><fo:block/></fo:table-cell> -->	<!--	34	-->

				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END 5e AC Table -->
	</xsl:template>





	<!--
====================================
====================================
	TEMPLATE - encumbrance TABLE
====================================
====================================-->
	<xsl:template name="encumbrance">
		<!-- BEGIN encumbrance table -->
<!--	<xsl:if test="/character/equipment/total/load != 'Light'">	-->
		<fo:table table-layout="fixed" width="100%">
			<!-- 0.26 * $pagePrintableWidth - 2 mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.50 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.50 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
					<fo:block line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">Encumbrance</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="/character/equipment/total/load"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
<!--	</xsl:if>	-->
		<!-- END encumberance table -->
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - Proficiency Bonus TABLE
====================================
====================================-->
	<xsl:template name="proficiency_bonus">
		<!-- BEGIN encumbrance table -->
<!--	<xsl:if test="/character/equipment/total/load != 'Light'">	-->
		<fo:table table-layout="fixed">
			<!-- 0.26 * $pagePrintableWidth - 2 mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.50 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.50 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
					<fo:block line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">Proficiency Bonus</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="/character/proficiency_bonus"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
<!--	</xsl:if>	-->
		<!-- END Proficiency Bonus table -->
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Resistance TABLE
====================================
====================================-->
	<xsl:template name="resistances">
		<!-- BEGIN Resistance table -->
		<fo:table table-layout="fixed" width="100%">
			<!-- 0.26 * $pagePrintableWidth - 2 mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.50 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row height="2pt">
											<xsl:message>Test</xsl:message>
					<fo:table-cell><fo:block/></fo:table-cell>

				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
					<fo:block line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">Res</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="8pt">
							<xsl:value-of select="resistance"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
<!--	</xsl:if>	-->
		<!-- END Resistance table -->
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - SAVES TABLE
====================================
====================================-->
	<xsl:template match="saving_throws">
		<!-- BEGIN Saves table -->
		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="82mm"/>	<!-- Saves Row -->
			<fo:table-column column-width="2mm"/>	<!-- Spacer -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 86" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:apply-templates select="." mode="saves"/>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END Saves table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SAVES TABLE
====================================
====================================-->
	<xsl:template match="saving_throws" mode="saves">
		<!-- BEGIN Saves table -->
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="23mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">PROFICIENT</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">SAVING THROWS</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">PROFICIENCY</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MAGIC</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>

					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC</fo:block>
					</fo:table-cell>
					<fo:table-cell><fo:block/></fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="saving_throw">
					<fo:table-row space-before="2pt">
											<xsl:message>Test</xsl:message>
						<fo:table-cell>
							<fo:block text-align="center" font-size="6pt" font-family="ZapfDingbats">		<!-- Investigate how to make this work; nothing is appearing -->
								<xsl:choose>								
								<xsl:when test="prof > 1">
									&#x25A0;
								</xsl:when>
								<xsl:otherwise>
									&#x274F;
								</xsl:otherwise>
								</xsl:choose>
	<!--		&#x2713;	-->
							</fo:block>
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'saves.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">
					<!--			<xsl:value-of select="translate(name/long, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>	-->
								<xsl:value-of select="name/long"/>
							</fo:block>
					<!--		<fo:block line-height="4pt" font-size="4pt">(<xsl:value-of select="ability"/>)</fo:block>	-->
						</fo:table-cell>
						<fo:table-cell><fo:block/></fo:table-cell>

						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'saves.total'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="10pt">
								<xsl:value-of select="total"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">=</fo:block>
						</fo:table-cell>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="base"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="abil_mod"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="magic_mod"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="misc_mod"/></xsl:call-template>
						<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'border.temp'"/>
								</xsl:call-template>
							<fo:block/>
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
		</fo:table>
	</xsl:template>
	<xsl:template name="saves.entry">
		<xsl:param name="value"/>
		<fo:table-cell>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'saves'"/>
			</xsl:call-template>
			<fo:block space-before.optimum="2pt" font-size="10pt">
				<xsl:value-of select="$value"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
		</fo:table-cell>
	</xsl:template>




</xsl:stylesheet>
