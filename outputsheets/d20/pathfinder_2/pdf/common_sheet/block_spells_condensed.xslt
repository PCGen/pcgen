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
	TEMPLATE - SPELLS
====================================
====================================-->
	<xsl:template match="spells">
		<!-- BEGIN Spells Pages -->
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:page-sequence>	
				<xsl:attribute name="master-reference">Portrait</xsl:attribute>
				<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body" font-size="8pt">
					<xsl:apply-templates select="memorized_spells"/>	
					<xsl:apply-templates select="spells_innate/racial_innate"/>	
					<xsl:apply-templates select="spells_innate/class_innate"/>	
					<xsl:apply-templates select="known_spells"/>	
				</fo:flow>
			</fo:page-sequence>	
		</xsl:if>
		<!-- END Spells Pages -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Racial Innate
====================================
====================================-->
	<xsl:template match="racial_innate">
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:block>
				<fo:table table-layout="fixed" width="100%">
					<xsl:call-template name="spells.known.header.row">
						<xsl:with-param name="columnOne" select="''"/>
						<xsl:with-param name="title" select="'Innate Racial Spells'"/>
					</xsl:call-template>
					<fo:table-body>
						<xsl:apply-templates select="spell" mode="details">	<!-- > was <xsl:apply-templates select="spell" mode="innate_details">	-->
							<xsl:with-param name="columnOne" select="'Times'"/>
						</xsl:apply-templates>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - INNATE CLASS SPELLS
====================================
====================================-->
	<xsl:template match="class_innate">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:for-each select="spellbook">
				<fo:table table-layout="fixed" width="100%" space-before="5mm">
					<xsl:call-template name="spells.known.header.row">
						<xsl:with-param name="columnOne" select="''"/>
						<xsl:with-param name="title" select="concat(@name, ' Spell-like Abilities')"/>
					</xsl:call-template>
					<fo:table-body>
						<xsl:apply-templates select="spell" mode="details"><!-- > was <xsl:apply-templates select="spell" mode="innate_details">	-->
							<xsl:with-param name="columnOne" select="'Times'"/>
						</xsl:apply-templates>
					</fo:table-body>
				</fo:table>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - KNOWN SPELLS
====================================
====================================-->
	<xsl:template match="known_spells">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:apply-templates select="class" mode="spells.known"/>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - KNOWN SPELLS - SINGLE CLASS
====================================
====================================-->
	<xsl:template match="class" mode="spells.known">
		<xsl:if test="count(.//spell) &gt; 0">
	<!--> This is causing the new page creation		<fo:block break-before="page"/>	-->
			<fo:table table-layout="fixed" width="100%">
				<xsl:variable name="titletext">
					<xsl:choose>
						<xsl:when test="@spellcastertype = 'Psionic'">
							<xsl:value-of select="concat(@spelllistclass, ' Powers')"/>
						</xsl:when>
						<xsl:when test="@spellcastertype = 'Infusion'">
							<xsl:value-of select="concat(@spelllistclass, ' Infusions')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(@spelllistclass, ' Spells')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="columnOne">
					<xsl:choose>
						<xsl:when test="@spellcastertype = 'Psionic'">PowerPoints</xsl:when>
						<xsl:otherwise>Boxes</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="columnOneTitle">
					<xsl:choose>
						<xsl:when test="@spellcastertype = 'Psionic'">Power Points</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="spells.known.header.row">
					<xsl:with-param name="columnOne" select="$columnOneTitle"/>
					<xsl:with-param name="title" select="$titletext"/>
					<xsl:with-param name="details" select="'false'"/>
				</xsl:call-template>
				<fo:table-body>
					<fo:table-row height="2mm">
											<xsl:message>Test</xsl:message>
						<fo:table-cell><fo:block/></fo:table-cell>
					</fo:table-row>
					<fo:table-row>
											<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="7">
							<xsl:apply-templates select="." mode="spell.level.table"/>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2mm">
											<xsl:message>Test</xsl:message>
						<fo:table-cell><fo:block/></fo:table-cell>
					</fo:table-row>
					<xsl:apply-templates select="level" mode="known.spells">
						<xsl:with-param name="columnOne" select="$columnOne"/>
						<xsl:with-param name="columnOneTitle" select="$columnOneTitle"/>
					</xsl:apply-templates>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELL.LEVEL.TABLE)
====================================
====================================-->
	<xsl:template match="class" mode="spell.level.table">
		<fo:table table-layout="fixed" width="100%" border-collapse="collapse">
			<fo:table-column column-width="proportional-column-width(2)"/>
			<fo:table-column column-width="proportional-column-width(2)"/>
			<xsl:for-each select="level">
				<fo:table-column column-width="proportional-column-width(1)"/>
			</xsl:for-each>
			<fo:table-column column-width="proportional-column-width(2)"/>
			<fo:table-body>
				<xsl:apply-templates select="." mode="spell.level.count"/>
				<xsl:if test="@memorize='false'">
					<xsl:apply-templates select="." mode="spell.level.known"/>
				</xsl:if>
<!-->				<xsl:apply-templates select="." mode="spell.level.known"/>-->
				<xsl:apply-templates select="." mode="spell.level.cast"/>
				<xsl:if test="@concentration != ''">
					<xsl:apply-templates select="." mode="spell.concentration"/>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELL.LEVEL.COUNT)
====================================
====================================-->
	<xsl:template match="class" mode="spell.level.count">
		<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
			<fo:table-cell><fo:block/></fo:table-cell>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt"> LEVEL</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
					</xsl:call-template>
					<fo:block space-before="2pt" space-after="1pt" font-size="6pt">
						<xsl:value-of select="@number"/>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell><fo:block/></fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELL.LEVEL.KNOWN)
====================================
====================================-->
	<xsl:template match="class" mode="spell.level.known">
		<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
			<fo:table-cell><fo:block/></fo:table-cell>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt"> KNOWN</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.known'"/>
					</xsl:call-template>
					<fo:block font-size="6pt" space-before="2pt" space-after="1pt">
						<xsl:choose>
							<xsl:when test="@known &gt; 0">
								<xsl:value-of select="@known"/>
							</xsl:when>
							<xsl:otherwise>
								&#x2014;
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell><fo:block/></fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELL.LEVEL.CAST)
====================================
====================================-->
	<xsl:template match="class" mode="spell.level.cast">
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<fo:table-cell><fo:block/></fo:table-cell>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt">PER DAY</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.perday'"/>
					</xsl:call-template>
					<fo:block font-size="6pt" space-before="2pt" space-after="1pt">
						<xsl:choose>
							<xsl:when test="@cast != 0">
								<xsl:value-of select="@cast"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="@number = 0 and @known != 0">
										at will
									</xsl:when>
									<xsl:otherwise>
										&#x2014;
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell><fo:block/></fo:table-cell>
		</fo:table-row>
	</xsl:template>

	
	<!-- New Section for Concentration
====================================
====================================
	TEMPLATE - SPELL CONCENTRATION
====================================
====================================-->
	<xsl:template match="class" mode="spell.concentration">
		<fo:table-row keep-with-next.within-column="always">	
											<xsl:message>Test</xsl:message>
			<fo:table-cell><fo:block/></fo:table-cell>
			<fo:table-cell>	
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
		<!-->	xsl:use-attribute-sets="spelllist.known.header">-->
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="2pt" space-after="1pt">Concentration</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/> 
				</xsl:call-template>
		<!--	 xsl:use-attribute-sets="spelllist.known.header centre">	-->
				<fo:block space-before="2pt" space-after="1pt" font-size="6pt"><xsl:value-of select="@concentration"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - KNOWN SPELL LEVEL
====================================
====================================-->

	<xsl:template match="level" mode="known.spells">
		<xsl:param name="columnOne" select="'Boxes'"/>
		<xsl:param name="columnOneTitle" select="''"/>
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
				<fo:table-cell number-columns-spanned="7" padding-top="1pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.header'"/>
					</xsl:call-template>
					<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="@cast"/>
									</xsl:call-template>
					<fo:block font-size="12pt">
						LEVEL <xsl:value-of select="@number"/> / Per Day:<xsl:value-of select="@cast"/> / Caster Level:<xsl:value-of select="spell/basecasterlevel"/>
					<xsl:if test="concentration != ''">
							<fo:inline> / </fo:inline>
						<fo:inline font-style="italic" font-weight="bold">Concentration:</fo:inline>
						<xsl:value-of select="concentration"/>
					</xsl:if>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<xsl:call-template name="spells.header.column.titles">
				<xsl:with-param name="columnOne" select="$columnOneTitle"/>
			</xsl:call-template>
			<xsl:apply-templates select="spell" mode="details">
				<xsl:with-param name="columnOne" select="$columnOne"/>
				<xsl:sort select="name"/>
			</xsl:apply-templates>
			<fo:table-row height="1mm">
											<xsl:message>Test</xsl:message>
				<fo:table-cell><fo:block/></fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>



	<!-- This is the INFORMATION right above the Spells Output
====================================
====================================
	TEMPLATE - KNOWN SPELL HEADER ROW
====================================
====================================-->
	<xsl:template name="spells.known.header.row">
		<xsl:param name="title" select="''"/>
		<xsl:param name="columnOne" select="''"/>
		<xsl:param name="details" select="'true'"/>
<!--	THIS BEGINS THE SECTION		-->
		<fo:table-column column-width="11mm"/>
		<!--	Check Boxes =5 total displayed in 11mm	-->
		<fo:table-column>
			<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth - 134" />mm</xsl:attribute>	<!-- was -153 now moved 6 over so it's 147 minus 13 equal 134-->
		</fo:table-column>
		<!-- name ^ BIG AREA!-->
		<fo:table-column column-width="38mm"/> <!-- Was Save, Now School -->
		<!-- TIME-->
<!--	<fo:table-column column-width="9mm"/>	-->
		
		<fo:table-column column-width="18mm"/>	<!-- Time -->
		<!-- DURATION -->
		<fo:table-column column-width="34mm"/>	<!-- Duration -->
		<!-- RANGE -->
		<fo:table-column column-width="18mm"/>	<!-- Range -->
		<!-- range -->
		<fo:table-column column-width="18mm"/>	<!-- Source -->
		<!-- comp -->
<!--		<fo:table-column column-width="13mm"/>	-->	<!--  -->
		<!-- SR 		<fo:table-column column-width="15mm"/>	-->
		<!-- school -->
<!--		<fo:table-column column-width="6mm"/>	  -->
		<!-- source		TOTAL 7 Columns -->

		<!-- Titles Columns Goes Here -->
		<fo:table-header>
			<fo:table-row>
											<xsl:message>Test</xsl:message>
				<fo:table-cell number-columns-spanned="7" padding-top="1pt">	
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.header'"/>
					</xsl:call-template>
					<fo:block font-size="12pt">
						<xsl:value-of select="$title"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<xsl:if test="$details = 'true'">
				<xsl:call-template name="spells.header.column.titles">
					<xsl:with-param name="columnOne" select="$columnOne"/>
				</xsl:call-template>
			</xsl:if>
		</fo:table-header>
		<fo:table-footer>
			<fo:table-row>
											<xsl:message>Test</xsl:message>
				<fo:table-cell number-columns-spanned="7" padding-top="1pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.footer'"/>
					</xsl:call-template>
					<fo:block font-size="5pt">* =Domain/Speciality Spell
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-footer>
	</xsl:template>
<!-- No Messages populating below this point -->
	
	<!--
====================================	
====================================
	TEMPLATE - KNOWN SPELL HEADER COLUMN TITLES
====================================
====================================-->
	<xsl:template name="spells.header.column.titles">
		<xsl:param name="columnOne" select="''"/>
		
		<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'spelllist.levelheader'"/>
			</xsl:call-template>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">
					<xsl:value-of select="$columnOne"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Name</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">School</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Time</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Duration</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Range</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="right" font-size="5pt" font-weight="bold">Source</fo:block>		<!--> Source / Now target is taking both blocks-->
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPELL DETAILS
====================================
====================================-->
	<xsl:template match="spell" mode="details">
		<xsl:param name="columnOne" select="'Times'"/>
		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="basecasterlevel" select="../../@spellcasterlevel">
		</xsl:variable>
		<xsl:variable name="baseconcentration" select="../../@concentration">
		</xsl:variable>
		<fo:table-row keep-with-next.within-column="always" keep-together="always">
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/></xsl:call-template>
			<xsl:choose>
				<xsl:when test="$columnOne = 'Times'">
					<xsl:choose>
						<xsl:when test="times_memorized &gt;= 0">
							<fo:table-cell padding-top="0pt">
								<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
									<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="times_memorized"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</xsl:when>
						<xsl:otherwise>
							<fo:table-cell padding-top="1pt" text-align="start">
								<fo:block text-align="start" font-size="7pt">At Will</fo:block>
							</fo:table-cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$columnOne = 'Boxes'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="5"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:when test="$columnOne = 'PowerPoints'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="8pt">
							<xsl:variable name="ppcount" select="((../@number)*2)-1"/>
							<xsl:choose>
								<xsl:when test="number($ppcount) &gt; 0">
									<xsl:value-of select="$ppcount"/>
								</xsl:when>
								<xsl:otherwise>0/1</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
			</xsl:choose>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="7pt" font-weight="bold">
				<fo:inline font-style="italic">	<xsl:value-of select="bonusspell"/>	</fo:inline>
						<xsl:choose>
						<xsl:when test="string-length(source/sourcelink)!=0">
							<fo:basic-link color="blue" text-decoration="underline">
								<xsl:attribute name="external-destination">
									<xsl:value-of select="source/sourcelink"/>
								</xsl:attribute>
								<xsl:value-of select="name"/>
							</fo:basic-link>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="name"/>
						</xsl:otherwise>
					</xsl:choose>
						<xsl:if test="casterlevel != $basecasterlevel">				
							(CL:<xsl:value-of select="casterlevel"/>)
						</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
						<fo:block text-align="start" font-size="5pt" font-weight="bold">
<!-->							<fo:inline font-style="italic">School: </fo:inline>	-->
							<xsl:value-of select="school/fullschool"/>
						</fo:block>
			</fo:table-cell>

			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="castingtime"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="duration"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="range"/>
				</fo:block>
			</fo:table-cell>

			<fo:table-cell padding-top="1pt">
				<fo:block text-align="right" font-size="5pt" number-columns-spanned="1">
					<xsl:value-of select="source/sourceshort"/>
					<xsl:text>:</xsl:text>
					<xsl:value-of select="source/sourcepage"/>
				</fo:block>
			</fo:table-cell>	
		</fo:table-row>

<!-- Second Row = For Spell Descriptions -->
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/>
			</xsl:call-template>
			<fo:table-cell padding-top="1pt" number-columns-spanned="7">
<!-- Set Up Alternate FONT SIZE		<xsl:if test="string-length(effect) &gt; 100">-->
				<fo:block text-align="start" font-size="5pt">
					<xsl:if test="string-length(components) &gt; 0">
						<fo:inline font-weight="bold">[<xsl:value-of select="components"/>]</fo:inline>
						<fo:inline> </fo:inline>
					</xsl:if>
					<fo:inline font-weight="bold"> TARGET: </fo:inline><xsl:value-of select="target"/>
					<fo:inline>; </fo:inline>
					<fo:inline font-style="italic" font-weight="bold" font-size="5pt">EFFECT: </fo:inline>
					<fo:inline font-size="5pt"><xsl:value-of select="effect"/></fo:inline>
					<xsl:if test="string-length(spell_resistance) &gt; 0 or dc &gt; 0"><fo:inline> [</fo:inline>
							<xsl:if test="string-length(spell_resistance) &gt; 0">
								<fo:inline font-weight="bold">SR:</fo:inline>
								<xsl:value-of select="spell_resistance"/>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="dc &gt; 0">
									<fo:inline>; </fo:inline><fo:inline font-weight="bold">DC:</fo:inline> <xsl:value-of select="dc"/> 
									<fo:inline>, </fo:inline> <xsl:value-of select="saveinfo"/>
								</xsl:when>
								<xsl:when test="/character/house_var/spelldisplaydc &gt; 0">
									<fo:inline>; </fo:inline><fo:inline font-weight="bold">DC: N/A</fo:inline>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
							<fo:inline>] </fo:inline>
						</xsl:if>
						<xsl:if test="concentration != $baseconcentration">
							<fo:inline>; </fo:inline>
							<fo:inline font-style="italic" font-weight="bold">CONCENTRATION:</fo:inline>
							<xsl:value-of select="concentration"/>
						</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - SPELL INNATE DETAILS
====================================
====================================-->
<!--	This is to display the Racial and Class Innate and make use of the x/y format	-->
	<xsl:template match="spell" mode="innate_details">
		<xsl:param name="columnOne" select="'Times'"/>
		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="basecasterlevel" select="../../@spellcasterlevel">
		</xsl:variable>
		<xsl:variable name="baseconcentration" select="../../@concentration">
		</xsl:variable>
		<fo:table-row keep-with-next.within-column="always" keep-together="always">
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib"
				><xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/>
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="$columnOne = 'Times'">
					<xsl:choose>
						<xsl:when test="times_memorized &gt;= 0">
							<fo:table-cell padding-top="0pt">
								<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
									<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="times_memorized"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</xsl:when>
						<xsl:otherwise>
							<fo:table-cell padding-top="1pt" text-align="start">
								<fo:block text-align="start" font-size="7pt">At Will</fo:block>
							</fo:table-cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$columnOne = 'Boxes'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="5"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:when test="$columnOne = 'PowerPoints'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="8pt">
							<xsl:variable name="ppcount" select="((../@number)*2)-1"/>
							<xsl:choose>
								<xsl:when test="number($ppcount) &gt; 0">
									<xsl:value-of select="$ppcount"/>
								</xsl:when>
								<xsl:otherwise>0/1</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
			</xsl:choose>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="7pt" font-weight="bold">
				<fo:inline font-style="italic">	<xsl:value-of select="bonusspell"/>	</fo:inline>
						<xsl:choose>
						<xsl:when test="times_memorized &gt;= 1">
						(<xsl:value-of select="times_memorized"/>/<xsl:value-of select="times_unit"/>) 
						</xsl:when>
						</xsl:choose>
						<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
						<fo:block text-align="start" font-size="5pt" font-weight="bold">
							<xsl:value-of select="school/fullschool"/>
						</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
				<xsl:if test="string-length(castingtime) &lt; 15">
					<xsl:value-of select="castingtime"/>
				</xsl:if>
				<xsl:if test="string-length(castingtime) &gt; 14">
					See below
				</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="duration"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="range"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="right" font-size="5pt" number-columns-spanned="1">
					<xsl:value-of select="source/sourceshort"/>
					<xsl:text>:</xsl:text>
					<xsl:value-of select="source/sourcepage"/>
				</fo:block>
			</fo:table-cell>	
		</fo:table-row>
<!-- Third Row = For Spell Descriptions -->
		<fo:table-row>
											<xsl:message>Test</xsl:message>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/>
			</xsl:call-template>
			<fo:table-cell padding-top="1pt" number-columns-spanned="6">
				<!-- Set Up Alternate FONT SIZE		<xsl:if test="string-length(effect) &gt; 100">-->
					<fo:block text-align="start" font-size="5pt">
					<xsl:if test="string-length(castingtime) &gt; 14">
						<fo:inline font-weight="bold"> TIME: </fo:inline><xsl:value-of select="castingtime"/>
						<fo:inline>; </fo:inline>
					</xsl:if>
					<xsl:if test="string-length(components) &gt; 0">
						<fo:inline font-weight="bold">[<xsl:value-of select="components"/>]</fo:inline>
						<fo:inline> </fo:inline>
					</xsl:if>
						<fo:inline font-weight="bold"> TARGET: </fo:inline><xsl:value-of select="target"/>
						<fo:inline>; </fo:inline>
						<fo:inline font-style="italic" font-weight="bold" font-size="5pt">EFFECT: </fo:inline>
						<xsl:if test="string-length(effect) &gt; 150">
							<fo:inline font-size="7pt"><xsl:value-of select="effect"/></fo:inline>
						</xsl:if>
						<xsl:if test="string-length(effect) &lt; 151">
							<fo:inline font-size="5pt"><xsl:value-of select="effect"/></fo:inline>
						</xsl:if>
						<xsl:if test="string-length(spell_resistance) &gt; 0 or dc &gt; 0"><fo:inline> [</fo:inline>
							<xsl:if test="string-length(spell_resistance) &gt; 0">
								<fo:inline font-weight="bold">SR:</fo:inline>
								<xsl:value-of select="spell_resistance"/>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="dc &gt; 0">
									<fo:inline>; </fo:inline><fo:inline font-weight="bold">DC:</fo:inline> <xsl:value-of select="dc"/> 
									<fo:inline>, </fo:inline> <xsl:value-of select="saveinfo"/>
								</xsl:when>
								<xsl:when test="/character/house_var/spelldisplaydc &gt; 0">
									<fo:inline>; </fo:inline><fo:inline font-weight="bold">DC: N/A</fo:inline>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
							<fo:inline>] </fo:inline>
						</xsl:if>
						(<fo:inline font-weight="bold">Caster Level:</fo:inline>
						<xsl:value-of select="casterlevel"/>)
		<!-->			<xsl:if test="concentration != ''">	-->
					<xsl:if test="concentration != $baseconcentration">
						<fo:inline>; </fo:inline>
						<fo:inline font-style="italic" font-weight="bold">Concentration:</fo:inline>
						<xsl:value-of select="concentration"/>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - MEMORIZED SPELLS
====================================
====================================-->
	<xsl:template match="memorized_spells">
		<xsl:if test="count(.//spell) &gt; 0">
	<!-->		<fo:block break-before="page">	-->
			<fo:block>
				<xsl:apply-templates mode="spells.memorized">
					<xsl:sort select="@type"/>
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</fo:block>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPELLS MEMORIZED HEADER
====================================
====================================-->
	<xsl:template name="spells.memorized.header">
		<xsl:param name="title" select="'Unknown'"/>
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>											<xsl:message>Test</xsl:message>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spells.memorized.header'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="$title"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - RACIAL_INNATE_MEMORIZED (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="racial_innate_memorized" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:call-template name="spells.memorized.header">
				<xsl:with-param name="title" select="'Innate'"/>
			</xsl:call-template>
			<fo:table table-layout="fixed" width="100%" space-after="5mm">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.80 * $pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-body>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS_INNATE_MEMORIZED (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="class_innate_memorized" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:apply-templates mode="spells.memorized.innate"/>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPELLBOOK (SPELLS.MEMORIZED.INNATE)
====================================
====================================-->
	<xsl:template match="spellbook" mode="spells.memorized.innate">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:call-template name="spells.memorized.header">
				<xsl:with-param name="title" select="concat(@name, ' Spell-like Abilities')"/>
			</xsl:call-template>
			<fo:table table-layout="fixed" width="100%">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.80 * $pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-body>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPELLBOOK (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="spellbook" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table table-layout="fixed" width="100%" space-before="4mm">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth div 5" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-body>
					<fo:table-row>											<xsl:message>Test Spells Memorized Header</xsl:message>
						<fo:table-cell padding-top="1pt" number-columns-spanned="5">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'spells.memorized.header'"/>
							</xsl:call-template>
							<fo:block font-size="10pt">
								<xsl:value-of select="@type"/>: <xsl:value-of select="@name"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="class" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table-row>											<xsl:message>Test "Spells Memorized Section"</xsl:message>
				<fo:table-cell padding-top="1pt" number-columns-spanned="5">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spells.memorized.header'"/>
					</xsl:call-template>
					<fo:block font-size="8pt">
						<xsl:value-of select="@spelllistclass"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>													<xsl:message>Test Levels 4-</xsl:message>
				<xsl:apply-templates select="level[@number &lt; 5]" mode="spells.memorized"/>
			</fo:table-row>
			<xsl:if test="count(.//level) &gt; 5">
				<fo:table-row>													<xsl:message>Test Levels 5+</xsl:message>
					<xsl:apply-templates select="level[@number &gt;= 5 and @number &lt; 10]" mode="spells.memorized"/>
				</fo:table-row>
			</xsl:if>
			<xsl:if test="count(.//level) &gt; 10">
				<fo:table-row>													<xsl:message>Test Levels 10+</xsl:message>
					<xsl:apply-templates select="level[@number &gt;= 10 and @number &lt; 15]" mode="spells.memorized"/>
				</fo:table-row>
			</xsl:if>
			<xsl:if test="count(.//level) &gt; 15">
				<fo:table-row>													<xsl:message>Test Levels 15+</xsl:message>
					<xsl:apply-templates select="level[@number &gt;= 15 and @number &lt; 20]" mode="spells.memorized"/>
				</fo:table-row>
			</xsl:if>
			<xsl:if test="count(.//level) &gt; 20">
				<fo:table-row>													<xsl:message>Test Levels 20+</xsl:message>
					<xsl:apply-templates select="level[@number &gt;= 20 and @number &lt; 25]" mode="spells.memorized"/>
				</fo:table-row>
			</xsl:if>
			<xsl:if test="count(.//level) &gt; 25">
				<fo:table-row>													<xsl:message>Test Levels 25+</xsl:message>
					<xsl:apply-templates select="level[@number &gt;= 25 and @number &lt; 30]" mode="spells.memorized"/>
				</fo:table-row>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - LEVEL (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="level" mode="spells.memorized">
		<fo:table-cell padding-top="1pt">
			<fo:block font-size="5pt">
				<xsl:if test="count(.//spell) &gt; 0">
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $pagePrintableWidth div 5" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.80 * $pagePrintableWidth div 5" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-body>
							<fo:table-row>				<xsl:message>Test Spells Memorized Level Grant</xsl:message>
								<fo:table-cell padding-top="1pt" number-columns-spanned="2">
									<xsl:call-template name="attrib">
										<xsl:with-param name="attribute" select="'spells.memorized.level'"/>
									</xsl:call-template>
									<fo:block font-size="7pt">
										Level <xsl:value-of select="@number"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<xsl:apply-templates mode="spells.memorized"/>
						</fo:table-body>
					</fo:table>
				</xsl:if>
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPELLS (SPELLS.MEMORIZED)
====================================
====================================-->
	<xsl:template match="spell" mode="spells.memorized">
		<fo:table-row>					<xsl:message>Test END - Spells Memorized</xsl:message>
			<xsl:choose>
				<xsl:when test="times_memorized &gt;= 0">
					<fo:table-cell padding-top="0pt" text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spells.memorized'"/>
						</xsl:call-template>
						<fo:block font-size="7pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="times_memorized"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spells.memorized'"/>
						</xsl:call-template>
						<fo:block font-size="6pt">At Will</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-cell>
				<fo:block font-size="7pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spells.memorized'"/>
					</xsl:call-template>
					<xsl:value-of select="bonusspell"/>
					<xsl:value-of select="name"/>
					<xsl:choose>
						<xsl:when test="dc &gt;= 0">
							 (DC:<xsl:value-of select="dc"/>)
						</xsl:when>
					</xsl:choose>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>



</xsl:stylesheet>
