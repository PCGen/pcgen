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
	TEMPLATE - VIEW WEAPON NUMBER

	Returns the number of weapons that can
	be shown on the front page
====================================
====================================-->
	<xsl:template name="view.weapon.num">
		<xsl:variable name="featureheight">
			<xsl:call-template name="features.left">
				<xsl:with-param name="features" select="/character/class_features/*"/>
			</xsl:call-template>
		</xsl:variable>
		<!-- 145 is the number of mm available to weapons and features
		 28mm is the size of a single large ranged weapon block
		 20mm is the size of a single large weapon block
		 24mm is the size of a single simple weapon block
		-->
		<!-- This should be made more complicated so that it determines the
			 size of each weapon block in turn so that a correct cumulative
		 height can be determined -->
		<!--
		This does not seem to work very well.

		<xsl:value-of select="floor( (140-$featureheight) div 28) "/>	-->

<!--		For now, just make it 3 weapons max.	-->
		
		<xsl:value-of select="3"/>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - Martial Arts ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/martialarts">
		<!-- START Martial Arts Attack Table -->
		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">Martial Arts</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="5pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- STOP Martial Arts Attack Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Spirit Weapon Melee ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/spiritweaponmelee">
		<!-- START Spirit Weapon Melee Attack Table -->
		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">Spirit Weapon - Melee</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="5pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- STOP Spirit Weapon Melee Attack Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Spirit Weapon Ranged ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/spiritweaponranged">
		<!-- START Martial Arts Attack Table -->
		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">Spirit Weapon - Ranged</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">RANGE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="range"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="5pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- STOP Spirit Weapon Ranged Attack Table -->
	</xsl:template>
<!--
====================================
====================================
	TEMPLATE - Unarmed ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/unarmed">
		<!-- START Unarmed Attack Table -->
		<!--<xsl:choose>
			<xsl:when test="(weapons/naturalattack) &lt; 1">	-->
		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">UNARMED</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
						<xsl:message>Test</xsl:message>
						<fo:table-cell number-rows-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.hilight'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="total"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-rows-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.hilight'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="damage"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-rows-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.hilight'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="critical"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-rows-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.hilight'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
								<xsl:value-of select="reach"/>
							</fo:block>
						</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell >
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="5pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			<xsl:choose>
				<xsl:when test="string-length(special_property) &gt; 0">	
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" font-weight="bold">
							<xsl:text>Special Properties:</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="4">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.border'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-right="2pt">
							<fo:inline> </fo:inline><xsl:value-of select="special_property"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				</xsl:when>
			</xsl:choose>	
		<!--	</xsl:when>		
		</xsl:choose>-->
				<xsl:if test="flurry_level &gt; 0">
		<!-- Start Flurry Attack Table 
				Currently set up using the Monk Workaround -->
				<fo:table-row>
					<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">Flurry of Blows</fo:block>
					</fo:table-cell>
					<fo:table-cell  number-rows-spanned="1" number-columns-spanned="4">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
				</fo:table-row>
					<!--
						This section determines the output of the "Flurry of Blows"
						fab value grabs the base unarmed to hit value from the base.xml.ftl file
						fab_# allow for precise control over each iteration, these modify the final result
						We currently allow up to 9 attack iterations
						flurry_attacks controls how many attacks are visible on the OS.
					-->
					<xsl:variable name="flurrylvl" select="flurry_level"/>
					<xsl:variable name="fab" select="to_hit"/>
					<xsl:variable name="fab_1" select="fab_1"/>
					<xsl:variable name="fab_2" select="fab_2"/>
					<xsl:variable name="fab_3" select="fab_3"/>
					<xsl:variable name="fab_4" select="fab_4"/>
					<xsl:variable name="fab_5" select="fab_5"/>
					<xsl:variable name="fab_6" select="fab_6"/>
					<xsl:variable name="fab_7" select="fab_7"/>
					<xsl:variable name="fab_8" select="fab_8"/>
					<xsl:variable name="fab_9" select="fab_9"/>
				<fo:table-row>
						<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="4">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.hilight'"/>
							</xsl:call-template>
							<fo:block font-size="8pt">
									<xsl:value-of select="format-number($fab + $fab_1,'+#;-#')"/>/
									<xsl:value-of select="format-number($fab + $fab_2,'+#;-#')"/>
									<xsl:if test="(flurry_attacks &gt; 2)">
											/<xsl:value-of select="format-number($fab + $fab_3,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 3)">
											/<xsl:value-of select="format-number($fab + $fab_4,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 4)">
											/<xsl:value-of select="format-number($fab + $fab_5,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 5)">
											/<xsl:value-of select="format-number($fab + $fab_6,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 6)">
											/<xsl:value-of select="format-number($fab + $fab_7,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 7)">
											/<xsl:value-of select="format-number($fab + $fab_8,'+#;-#')"/>
									</xsl:if>
									<xsl:if test="(flurry_attacks &gt; 8)">
											/<xsl:value-of select="format-number($fab + $fab_9,'+#;-#')"/>
									</xsl:if>
							</fo:block>
						</fo:table-cell>
				</fo:table-row>
				</xsl:if>
		<!-- STOP Flurry Attack Table -->
		</fo:table-body>
	</fo:table>
	</xsl:template>


		<!--
====================================
====================================
	TEMPLATE - Natural Weapon ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/naturalattack">
		<!-- START Natural Attack Table -->

		<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="9pt">
							<xsl:value-of select="name"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
				<xsl:choose>
					<xsl:when test="string-length(critmult) &gt; 0">
						<fo:block font-size="6pt">CRIT / MULT</fo:block>
					</xsl:when>
					<xsl:otherwise>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</xsl:otherwise>
				</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
				<xsl:choose>
					<xsl:when test="(range) &gt; 0">
						<fo:block font-size="6pt">RANGE</fo:block>
					</xsl:when>
					<xsl:otherwise>
						<fo:block font-size="6pt">REACH</fo:block>
					</xsl:otherwise>
				</xsl:choose>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
						<xsl:if test="tohit &gt; 0">+</xsl:if>
							<xsl:value-of select="tohit"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="threat"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
					<xsl:choose>
							<xsl:when test="(range) &gt; 0">
								<xsl:value-of select="range"/>
								<xsl:value-of select="distance_unit"/>
							</xsl:when>
							<xsl:otherwise>
							<xsl:value-of select="reach"/>
								<xsl:value-of select="distance_unit"/>
							</xsl:otherwise>
						</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
<!-- New Row -->
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="5pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			<xsl:choose>
				<xsl:when test="string-length(notes) &gt; 1">
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" font-weight="bold">
							<xsl:text>Special Properties:</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="4">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.border'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-right="2pt">
							<fo:inline> </fo:inline><xsl:value-of select="notes"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
			<fo:table-row>
											<xsl:message>Test</xsl:message>
				<fo:table-cell><fo:block/></fo:table-cell>

			</fo:table-row>
		</fo:table-body>
	</fo:table>
<!-- New Table For Spacing -->
<xsl:choose>
	<xsl:when test="range &gt; 0">
			<!-- First Row for Thrown Weapons - Increment of 5 + Short range <xsl:for-each select="rangeincrement">		</xsl:for-each>-->
		<xsl:call-template name="range.distance.thrown">
			<xsl:with-param name="distance" select="range"/>
			<xsl:with-param name="tohit" select="tohit"/>
			<xsl:with-param name="damage" select="damage"/>
		</xsl:call-template>
	</xsl:when>
	<xsl:otherwise/>
</xsl:choose>
		<!-- STOP Natural Attack Table -->
	</xsl:template>

	<xsl:template name="range.distance.thrown">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<xsl:param name="distance"/>
		<xsl:param name="damage"/>
		<xsl:param name="tohit" select="''"/>
			<fo:table table-layout="fixed" width="100%" space-before="2mm">
			<fo:table-column column-width="5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									Rng
								</fo:block>
							</fo:table-cell>
					<!-->	<xsl:for-each select="range">	-->
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:value-of select="number($distance)"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
				<!-->		</xsl:for-each> -->
							<!-- 2nd -->
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:value-of select="$distance*2"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:value-of select="$distance*3"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:value-of select="$distance*4"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:value-of select="$distance*5"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
						<fo:table-row>
											<xsl:message>Test</xsl:message>
								<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									TH
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:if test="($tohit) &gt; 0">+</xsl:if>
									<xsl:value-of select="$tohit"/>
								</fo:block>
							</fo:table-cell>
							<!-- 2nd -->
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
								<xsl:if test="($tohit)-2 &gt; 0">+</xsl:if>
									<xsl:value-of select="number($tohit)-2"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
								<xsl:if test="($tohit)-4 &gt; 0">+</xsl:if>
									<xsl:value-of select="number($tohit)-4"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
								<xsl:if test="($tohit)-6 &gt; 0">+</xsl:if>
									<xsl:value-of select="number($tohit)-6"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
							<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
								<fo:block font-size="7pt" padding-right="2pt">
									<xsl:if test="($tohit)-8 &gt; 0">+</xsl:if>
									<xsl:value-of select="($tohit)-8"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</fo:table-body>
		</fo:table>
		<!-- STOP Spirit Weapon Melee Attack Table -->
	</xsl:template>

						<xsl:template name="range.ranged">
								<xsl:param name="distance"/>
								<xsl:param name="damage"/>
								<xsl:param name="tohit" select="''"/>
							<fo:table-cell number-columns-spanned="1">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
								</xsl:call-template>
								<fo:block font-size="8pt" padding-right="2pt">
									<xsl:value-of select="$distance*6"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="1">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
								</xsl:call-template>
								<fo:block font-size="8pt" padding-right="2pt">
									<xsl:value-of select="$distance*7"/>
									<xsl:value-of select="distance_unit"/>
								</fo:block>
							</fo:table-cell>
						</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - First 3 weapons
====================================
====================================-->
	<xsl:template match="weapons">
		<xsl:param name="first_weapon" select="0"/>
		<xsl:param name="last_weapon" select="0"/>
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		
		<xsl:for-each select="weapon">
			<xsl:if test="(position() &gt;= $first_weapon) and (position() &lt;= $last_weapon)">
				<xsl:apply-templates select="common">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="melee">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="ranges">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="simple">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="flurry">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="common" mode="special_properties">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="position() &gt;= $first_weapon">
			<fo:block font-size="5pt" space-before="2mm" color="black">
				<fo:inline font-weight="bold">*</fo:inline>: weapon is equipped
			</fo:block>
			<fo:block font-size="5pt" space-before="1pt" color="black">
				<fo:inline font-weight="bold">1H-P</fo:inline>: One handed, in primary hand.
				<fo:inline font-weight="bold">1H-O</fo:inline>: One handed, in off hand.
				<fo:inline font-weight="bold">2H</fo:inline>: Two handed.
				<fo:inline font-weight="bold">2W-P-(OH)</fo:inline>: 2 weapons, primary hand (off hand weapon is heavy).
				<fo:inline font-weight="bold">2W-P-(OL)</fo:inline>: 2 weapons, primary hand (off hand weapon is light).
				<fo:inline font-weight="bold">2W-OH</fo:inline>: 2 weapons, off hand.
			</fo:block>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Weapons - common
====================================
====================================-->
	<xsl:template match="common">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<fo:block keep-with-next.within-page="always" keep-together.within-column="always">
		<fo:table table-layout="fixed" width="100%" border-collapse="collapse" space-before="2mm">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 48" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-body>
			
				<fo:table-row keep-with-next.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Name row (including Hand, Type, Size and Crit -->
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="10pt">
							<xsl:variable name="name" select="substring-before(name/short,'(')"/>
							<xsl:variable name="description" select="substring-after(name/short,'(')"/>
							<xsl:value-of select="$name"/>
							<xsl:if test="string-length($name) = 0">
								<xsl:value-of select="name/short"/>
							</xsl:if>
							<xsl:if test="string-length($description) &gt; 0">
								<fo:inline font-size="6pt">
									<xsl:text>(</xsl:text>
									<xsl:value-of select="$description"/>
								</fo:inline>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">HAND</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TYPE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">SIZE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Hand, Type, Size and Crit -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
					<xsl:choose>
						<xsl:when test="string-length(hand) &lt; 9">
						<fo:block font-size="7pt">
							<xsl:value-of select="hand"/>
						</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-size="5pt">
								<xsl:value-of select="hand"/>
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="7pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="7pt">
							<xsl:value-of select="size"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="7pt">
							<xsl:value-of select="critical/range"/>
							<xsl:text>/x</xsl:text>
							<xsl:value-of select="critical/multiplier"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="7pt">
						<xsl:choose>	
						<xsl:when test="string-length(range/distance) &gt; 1">
							<xsl:value-of select="range"/> 
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="reach"/> 
							<xsl:value-of select="reachunit"/>
						</xsl:otherwise>
						</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</fo:block>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - weapons - special properties
====================================
====================================-->

	<xsl:template match="common" mode="special_properties">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<fo:block keep-with-previous.within-page="always" keep-together.within-column="always">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="20mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 20" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>
											<xsl:message>Test</xsl:message>
				<xsl:if test="special_properties != ''">
					<fo:table-cell  number-columns-spanned="2" >
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'equipment.border'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Special Properties: <xsl:value-of select="special_properties"/></fo:block>
					</fo:table-cell>
				</xsl:if>
				<xsl:if test="special_properties = ''">
					<fo:table-cell number-columns-spanned="2"><fo:block/></fo:table-cell>
				</xsl:if>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</fo:block>
	</xsl:template>
	
	<!--
====================================
====================================
	TEMPLATE - weapons - simple
====================================
====================================-->
	<xsl:template match="simple">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<xsl:call-template name="simple_weapon">
			<xsl:with-param name="to_hit" select="to_hit"/>
			<xsl:with-param name="damage" select="damage"/>
			<xsl:with-param name="column_width" select="$column_width"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="simple_weapon">
		<xsl:param name="to_hit" select="''"/>
		<xsl:param name="damage" select="''"/>
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth"/>
		<fo:block keep-with-previous.within-page="always" keep-together.within-column="always">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-page="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">DAMAGE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row  keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
<!-- DATA-73 Temporary Work Around -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:choose>
								<xsl:when test="contains(name, 'Flurry')">
									<xsl:variable name="flurry_base" select="number(substring(substring-before($to_hit,'/'),2))"/>
									<xsl:variable name="monk_level" select="number(substring-before(substring-after(class,'Mnk'),' '))"/>
									<xsl:choose>
										<xsl:when test="($monk_level &gt;= 8) and ($monk_level &lt;= 10)">
											<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>
										</xsl:when>
										<xsl:when test="($monk_level &gt;= 11) and ($monk_level &lt;= 14)">
											<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 10,'+#;-#')"/>
										</xsl:when>
										<xsl:when test="$monk_level = 15">
											<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 10,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 10,'+#;-#')"/>
										</xsl:when>
										<xsl:when test="$monk_level &gt;= 16">
											<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 5,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 10,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 10,'+#;-#')"/>/<xsl:value-of select="format-number($flurry_base - 15,'+#;-#')"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$to_hit"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$to_hit"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
					
					
<!-- DATA-73 Replacing This Block>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="$to_hit"/>
						</fo:block>
					</fo:table-cell>
<End DATA-73 Work Around -->
					
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="$damage"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</fo:block>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - weapons - melee - single tohit/damage
====================================
====================================-->
	<xsl:template name="weapon.complex.tohit">
		<xsl:param name="title"/>
		<xsl:param name="tohit"/>
		<xsl:param name="damage"/>
		
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
			<fo:block font-size="5pt" font-weight="bold" space-before="1pt">
				<xsl:value-of select="$title"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
			<fo:block font-size="7pt" space-before="1pt">
				<xsl:value-of select="$tohit"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
			<fo:block font-size="7pt" space-before="1pt">
				<xsl:value-of select="$damage"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - weapons - melee
====================================
====================================-->
	<xsl:template match="melee">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 1"/>
		<fo:block keep-with-previous.within-page="always" keep-together.within-column="always">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="8mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.35 * ($column_width - 19)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($column_width - 19)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="11mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.35 * ($column_width - 19)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.15 * ($column_width - 19)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- To hit and Damage titles -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block/>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold" space-before="1pt">To Hit</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold" space-before="1pt">Dam</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block/>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold" space-before="1pt">To Hit</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold" space-before="1pt">Dam</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="not(w1_h1_p/to_hit = /character/export/invalidtext/tohit and w1_h1_p/damage = /character/export/invalidtext/damage and w2_p_oh/to_hit = /character/export/invalidtext/tohit and w2_p_oh/damage = /character/export/invalidtext/damage)">
					<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
						<!-- 1HP, 2WP-OH -->
						<xsl:call-template name="weapon.complex.tohit">
							<xsl:with-param name="title" select="'1H-P'"/>
							<xsl:with-param name="tohit" select="w1_h1_p/to_hit"/>
							<xsl:with-param name="damage" select="w1_h1_p/damage" />
						</xsl:call-template>
						<xsl:call-template name="weapon.complex.tohit">
							<xsl:with-param name="title" select="'2W-P-(OH)'"/>
							<xsl:with-param name="tohit" select="w2_p_oh/to_hit"/>
							<xsl:with-param name="damage" select="w2_p_oh/damage" />
						</xsl:call-template>
					</fo:table-row>
				</xsl:if>
				<xsl:if test="not(w1_h1_o/to_hit = /character/export/invalidtext/tohit and w1_h1_o/damage = /character/export/invalidtext/damage and w2_p_ol/to_hit = /character/export/invalidtext/tohit and w2_p_ol/damage = /character/export/invalidtext/damage)">
					<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
						<!-- 1HO, 2WPOL -->
						<xsl:call-template name="weapon.complex.tohit">
							<xsl:with-param name="title" select="'1H-O'"/>
							<xsl:with-param name="tohit" select="w1_h1_o/to_hit"/>
							<xsl:with-param name="damage" select="w1_h1_o/damage" />
						</xsl:call-template>
						<xsl:call-template name="weapon.complex.tohit">
							<xsl:with-param name="title" select="'2W-P-(OL)'"/>
							<xsl:with-param name="tohit" select="w2_p_ol/to_hit"/>
							<xsl:with-param name="damage" select="w2_p_ol/damage" />
						</xsl:call-template>
					</fo:table-row>
				</xsl:if>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- 2H, OH -->
					<xsl:call-template name="weapon.complex.tohit">
						<xsl:with-param name="title" select="'2H'"/>
						<xsl:with-param name="tohit" select="w1_h2/to_hit"/>
						<xsl:with-param name="damage" select="w1_h2/damage" />
					</xsl:call-template>
					<xsl:call-template name="weapon.complex.tohit">
						<xsl:with-param name="title" select="'2W-OH'"/>
						<xsl:with-param name="tohit" select="w2_o/to_hit"/>
						<xsl:with-param name="damage" select="w2_o/damage" />
					</xsl:call-template>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</fo:block>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - weapons ranged
====================================
====================================-->
	<xsl:template match="ranges">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<fo:block keep-with-previous.within-page="always" keep-together.within-column="always">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.2 * ($column_width - 5)" />mm</xsl:attribute>
			</fo:table-column>
<xsl:choose>
<xsl:when test="ammunition">
</xsl:when>
<xsl:otherwise>
<!-- Begin Regular Ranged Weapon Section -->
			<fo:table-body>
				<xsl:choose>
				<xsl:when test="count(./range) = 0">
					<!--  Don't output table rows if there are no ranges -->
				</xsl:when>
				<xsl:otherwise>
	<!-->			<xsl:if test="range[position() &gt; 5]">	-->
	<!-->		<xsl:if test="range[position() &gt; 5 or ../../common/range &gt; 10]">	-->
	<!-->			<xsl:if test="count(./ranges/range) = 6 or count(./ranges/range) = 11">	-->
					<xsl:if test="count(./range) = 6 or count(./range) = 11">
					<fo:table-row keep-with-next.within-page="always">
											<xsl:message>Test</xsl:message>
						<xsl:for-each select="range[position() &lt; 2]">	
							<fo:table-cell number-columns-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
							<fo:block font-size="7pt" font-weight="bold">
							Range: <xsl:value-of select="distance"/>
							</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
							<fo:block font-size="7pt" font-weight="bold">
							To Hit: <xsl:value-of select="to_hit"/> 
							</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="2">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
							<fo:block font-size="7pt" font-weight="bold">
							Damage: <xsl:value-of select="damage"/>
							</fo:block>
							</fo:table-cell>
						</xsl:for-each>	
					</fo:table-row>
				</xsl:if>	

<!--	FIRST ROW 	-->
				<xsl:message><xsl:value-of select="count(./range)"/></xsl:message>

			<xsl:if test="count(./range) = 6 or count(./range) = 11">
		<!-->		<xsl:if test="range[position() &gt; 6]">	-->
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>				<xsl:message><xsl:value-of select="count(./range)"/></xsl:message>

					<!-- Distances -->
					<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block/>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">
								<xsl:value-of select="distance"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Range To-Hits -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block space-before="1pt">
								<xsl:attribute name="font-size">
									<xsl:choose>
										<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
										<xsl:otherwise>7pt</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:value-of select="to_hit"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Damages -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block font-size="7pt" space-before="1pt">
								<xsl:value-of select="damage"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				</xsl:if>
<!-- For Thrown Weapons		-->

<!-- First Row - only 5 Increments or 10 Increments	-->

			<xsl:if test="count(./range) = 5 or count(./range) = 10">
		<!-->		<xsl:if test="range[position() &gt; 6]">	-->
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Distances -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block/>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">
								<xsl:value-of select="distance"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Range To-Hits -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block space-before="1pt">
								<xsl:attribute name="font-size">
									<xsl:choose>
										<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
										<xsl:otherwise>7pt</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:value-of select="to_hit"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Damages -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block font-size="7pt" space-before="1pt">
								<xsl:value-of select="damage"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				</xsl:if>
<!-- First Row 5 or 10 Increments	-->

<!-- Second Row 11 Increments	-->
				<xsl:if test="count(./range) = 11">
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Distances -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block/>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">
								<xsl:value-of select="distance"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Range To-Hits -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block space-before="1pt">
								<xsl:attribute name="font-size">
									<xsl:choose>
										<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
										<xsl:otherwise>7pt</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:value-of select="to_hit"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Damages -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block font-size="7pt" space-before="1pt">
								<xsl:value-of select="damage"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
			</xsl:if>

<!-- End second Row 11 Increments	-->

<!-- Second Row 10 Increments	-->
				<xsl:if test="count(./range) = 10">
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Distances -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">
								<xsl:value-of select="distance"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Range To-Hits -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block space-before="1pt">
								<xsl:attribute name="font-size">
									<xsl:choose>
										<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
										<xsl:otherwise>7pt</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:value-of select="to_hit"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Damages -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block font-size="7pt" space-before="1pt">
								<xsl:value-of select="damage"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
			</xsl:if>
<!-- End Second Row 10 Increments	-->
			</xsl:otherwise>
			</xsl:choose>
			</fo:table-body>

</xsl:otherwise>
</xsl:choose>









				<xsl:if test="ammunition">	
			<fo:table-body>
					<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="6">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">Ammunition: <xsl:value-of select="ammunition/name"/>
								<xsl:if test="string(ammunition/special_properties) != ''">
									(<xsl:value-of select="ammunition/special_properties"/>)
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<xsl:choose>
					<xsl:when test="count(./range) = 0">
						<!--  Don't output table rows if there are no ranges -->
					</xsl:when>
					<xsl:otherwise>
		<!-->			<xsl:if test="range[position() &gt; 5]">	-->
		<!-->		<xsl:if test="range[position() &gt; 5 or ../../common/range &gt; 10]">	-->
		<!-->			<xsl:if test="count(./ranges/range) = 6 or count(./ranges/range) = 11">	-->
						<xsl:if test="count(./range) = 6 or count(./range) = 11">
						<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
							<xsl:for-each select="range[position() &lt; 2]">	
								<fo:table-cell number-columns-spanned="2">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
								</xsl:call-template>
								<fo:block font-size="7pt" font-weight="bold">
								Range: <xsl:value-of select="distance"/>
								</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
								</xsl:call-template>
								<fo:block font-size="7pt" font-weight="bold">
								To Hit: <xsl:value-of select="./ammunition/to_hit"/> 
								</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'weapon.title'"/>
								</xsl:call-template>
								<fo:block font-size="7pt" font-weight="bold">
								Damage: <xsl:value-of select="./ammunition/damage"/>
								</fo:block>
								</fo:table-cell>
							</xsl:for-each>	
						</fo:table-row>
					</xsl:if>	
			<xsl:if test="count(./range) = 6 or count(./range) = 11">
		<!-->		<xsl:if test="range[position() &gt; 6]">	-->
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>				<xsl:message><xsl:value-of select="count(./range)"/></xsl:message>

					<!-- Distances -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">
								<xsl:value-of select="distance"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Range To-Hits -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block space-before="1pt">
								<xsl:attribute name="font-size">
									<xsl:choose>
										<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
										<xsl:otherwise>7pt</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:value-of select="./ammunition/to_hit"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				<fo:table-row keep-with-previous.within-page="always">
											<xsl:message>Test</xsl:message>
					<!-- Damages -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
					</fo:table-cell>
					<xsl:for-each select="range[position() &gt; 1 and position() &lt; 7]">
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
							<fo:block font-size="7pt" space-before="1pt">
								<xsl:value-of select="./ammunition/damage"/>
							</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>
				</xsl:if>
<!-- For Thrown Weapons		-->

<!-- First Row - only 5 Increments or 10 Increments	-->

				<xsl:if test="count(./range) = 5 or count(./range) = 10">
			<!-->		<xsl:if test="range[position() &gt; 6]">	-->
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Distances -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
								<fo:block font-size="5pt" font-weight="bold">
									<xsl:value-of select="distance"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Range To-Hits -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block space-before="1pt">
									<xsl:attribute name="font-size">
										<xsl:choose>
											<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
											<xsl:otherwise>7pt</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:value-of select="./ammunition/to_hit"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Damages -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 0 and position() &lt; 6]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block font-size="7pt" space-before="1pt">
									<xsl:value-of select="./ammunition/damage"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					</xsl:if>
	<!-- First Row 5 or 10 Increments	-->

	<!-- Second Row 11 Increments	-->
					<xsl:if test="count(./range) = 11">
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Distances -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
								<fo:block font-size="5pt" font-weight="bold">
									<xsl:value-of select="distance"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Range To-Hits -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block space-before="1pt">
									<xsl:attribute name="font-size">
										<xsl:choose>
											<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
											<xsl:otherwise>7pt</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:value-of select="./ammunition/to_hit"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Damages -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 6 and position() &lt; 12]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block font-size="7pt" space-before="1pt">
									<xsl:value-of select="./ammunition/damage"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
				</xsl:if>

			<!-- End second Row 11 Increments	-->

				<!-- Second Row 10 Increments	-->
					<xsl:if test="count(./range) = 10">
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Distances -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
								<fo:block font-size="5pt" font-weight="bold">
									<xsl:value-of select="distance"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Range To-Hits -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">TH</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block space-before="1pt">
									<xsl:attribute name="font-size">
										<xsl:choose>
											<xsl:when test="string-length(to_hit) &gt; 15">6pt</xsl:when>
											<xsl:otherwise>7pt</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:value-of select="./ammunition/to_hit"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
					<fo:table-row keep-with-previous.within-page="always">
												<xsl:message>Test</xsl:message>
						<!-- Damages -->
						<fo:table-cell>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
							<fo:block font-size="6pt" font-weight="bold">Dam</fo:block>
						</fo:table-cell>
						<xsl:for-each select="range[position() &gt; 5 and position() &lt; 11]">
							<fo:table-cell>
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
								<fo:block font-size="7pt" space-before="1pt">
									<xsl:value-of select="./ammunition/damage"/>
								</fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
				</xsl:if>
			</xsl:otherwise>
			</xsl:choose>
			</fo:table-body>
			</xsl:if>
<!-- END AMMO Section	-->
		</fo:table>
		</fo:block>
	</xsl:template>



</xsl:stylesheet>
