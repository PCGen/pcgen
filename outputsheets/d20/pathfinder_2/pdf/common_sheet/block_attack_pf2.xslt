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
	TEMPLATE - AC TABLE
====================================
====================================-->
	<xsl:template match="spellattackanddc">	
		<fo:table table-layout="fixed" width="100%" space-before="2pt">
			<fo:table-column column-width="14mm"/>	<!--	1	-->
			<!-- TITLE -->
			<fo:table-column column-width="2mm"/>	<!--	2	-->
			<!-- space -->
			<fo:table-column column-width="8mm"/>	<!--	3	-->
			<!-- TOTAL -->
			<fo:table-column column-width="10mm"/>	<!--	4	-->
			<!--  -->
			<fo:table-column column-width="14mm"/>	<!--	5	-->
			<!-- TITLE -->
			<fo:table-column column-width="2mm"/>	<!--	6	-->
			<!-- space -->
			<fo:table-column column-width="8mm"/>	<!--	7	-->
			<!-- TOTAL -->

			<!-- NEW ROW - VALUES -->
			<fo:table-body>
				<!--	Arcane	-->
				<fo:table-row>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Arcane</fo:block>
						<fo:block line-height="4pt" font-size="4pt">Spell Attack</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<fo:table-cell><fo:block/></fo:table-cell>	
					<!--	2 / SPACE	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="arcane_spellattack"/>
						</fo:block>
					</fo:table-cell>	
					<!--	3 / TOTAL	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	4	SPACER -->
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Arcane</fo:block>
						<fo:block line-height="4pt" font-size="4pt">DC</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<!--	5 / base AC value	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	6	+	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	
					<!--	7	Stat Mod	-->
				</fo:table-row>
				<!-- SPACER // NEW ROW -->
				<fo:table-row height="0.5pt">
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
				<!--	Divine	-->
				<!-- NEW ROW for Text -->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Divine</fo:block>
						<fo:block line-height="4pt" font-size="4pt">Spell Attack</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<fo:table-cell><fo:block/></fo:table-cell>	
					<!--	2 / SPACE	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="arcane_spellattack"/>
						</fo:block>
					</fo:table-cell>	
					<!--	3 / TOTAL	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	4	SPACER -->
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Divine</fo:block>
						<fo:block line-height="4pt" font-size="4pt">DC</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<!--	5 / base AC value	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	6	+	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	
					<!--	7	Stat Mod	-->
				</fo:table-row>
				<!-- SPACER // NEW ROW -->
				<fo:table-row height="0.5pt">
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
				<!--	Occult	-->

				<!-- NEW ROW for Text -->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Occult</fo:block>
						<fo:block line-height="4pt" font-size="4pt">Spell Attack</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<fo:table-cell><fo:block/></fo:table-cell>	
					<!--	2 / SPACE	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="arcane_spellattack"/>
						</fo:block>
					</fo:table-cell>	
					<!--	3 / TOTAL	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	4	SPACER -->
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Occult</fo:block>
						<fo:block line-height="4pt" font-size="4pt">DC</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<!--	5 / base AC value	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	6	+	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	
					<!--	7	Stat Mod	-->
				</fo:table-row>
				<!-- SPACER // NEW ROW -->
				<fo:table-row height="0.5pt">
					<fo:table-cell><fo:block/></fo:table-cell>
				</fo:table-row>
				<!--	Primal	-->
				<!-- NEW ROW for Text -->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Primal</fo:block>
						<fo:block line-height="4pt" font-size="4pt">Spell Attack</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<fo:table-cell><fo:block/></fo:table-cell>	
					<!--	2 / SPACE	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="arcane_spellattack"/>
						</fo:block>
					</fo:table-cell>	
					<!--	3 / TOTAL	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	4	SPACER -->
					<fo:table-cell>	
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">Primal</fo:block>
						<fo:block line-height="4pt" font-size="4pt">DC</fo:block>
					</fo:table-cell>		<!--	1	TITLE	-->
					<!--	5 / base AC value	-->
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="6pt"> </fo:block>
					</fo:table-cell>	
					<!--	6	+	-->
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="10pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>	
					<!--	7	Stat Mod	-->
				</fo:table-row>

			</fo:table-body>
		</fo:table>
		<!-- END AC Table -->
	</xsl:template>



</xsl:stylesheet>
