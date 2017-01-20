<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:Psionics="my:Psionics"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs Psionics">

	<xsl:import href="fantasy_common.xsl"/>

	<xsl:output indent="yes"/>

	<xsl:variable name="vAttribs_tree">
		<myAttribs:myAttribs>
			<xsl:copy-of select="$vAttribs/*"/>
		</myAttribs:myAttribs>
	</xsl:variable>
	<xsl:variable name="vAttribs_all" select="xalan:nodeset($vAttribs_tree)"/>
	<xsl:variable name="pageHeight">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/height, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/height, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/height, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/height, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/height, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/height, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains(/character/export/paperinfo/name, 'Letter')">
						<xsl:value-of select="25.4 * 11.0"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="297"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageWidth">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/width, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/width, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/width, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/width, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/width, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/width, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains(/character/export/paperinfo/name, 'Letter')">
						<xsl:value-of select="25.4 * 8.5"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="210"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginTop">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/top, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/top, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/top, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/top, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginBottom">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/bottom, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/bottom, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/bottom, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/bottom, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginLeft">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/left, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/left, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/left, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/left, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pageMarginRight">
		<xsl:choose>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'in')">
				<xsl:value-of select="25.4 * substring-before(/character/export/paperinfo/margins/right, 'in')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'cm')">
				<xsl:value-of select="10 * substring-before(/character/export/paperinfo/margins/right, 'cm')"/>
			</xsl:when>
			<xsl:when test="contains(/character/export/paperinfo/margins/right, 'mm')">
				<xsl:value-of select="substring-before(/character/export/paperinfo/margins/right, 'mm')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="10"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="pagePrintableWidth">
		<xsl:value-of select="($pageWidth - $pageMarginLeft - $pageMarginRight)"/>
	</xsl:variable>
	<xsl:variable name="pagePrintableHeight">
		<xsl:value-of select="($pageHeight - $pageMarginTop - $pageMarginBottom)"/>
	</xsl:variable>
	<xsl:variable name="skillmastery">
		<xsl:for-each select="/character/special_qualities/special_quality">
			<xsl:if test="substring(name,1,13)='Skill Mastery'">
				<xsl:value-of select="associated"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>



	<!-- Include all of the output attributes -->
	<!-- vAttribs will be set up in the stylesheet that calls this one -->
	<xsl:template name="attrib">
		<xsl:param name="attribute"/>
		<xsl:copy-of select="$vAttribs_all/*/*[name() = $attribute]/@*"/>
		<xsl:for-each select="$vAttribs_all/*/*[name() = $attribute]/subattrib/@*">
			<xsl:variable name="bar" select="name()"/>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="$bar"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="page.layouts">
			<!--	PAGE DEFINITIONS	-->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Portrait 2 Column">
					<xsl:attribute name="page-height"><xsl:value-of select="$pageHeight" />mm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$pageWidth" />mm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$pageMarginTop" />mm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$pageMarginBottom" />mm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$pageMarginLeft" />mm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$pageMarginRight" />mm</xsl:attribute>
					<fo:region-body region-name="body" column-count="2" column-gap="2mm" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="4.4mm"/>
				</fo:simple-page-master>
				<fo:simple-page-master master-name="Portrait">
					<xsl:attribute name="page-height"><xsl:value-of select="$pageHeight" />mm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$pageWidth" />mm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$pageMarginTop" />mm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$pageMarginBottom" />mm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$pageMarginLeft" />mm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$pageMarginRight" />mm</xsl:attribute>
					<fo:region-body region-name="body" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="4.4mm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PAGE FOOTER
====================================
====================================-->
	<xsl:template name="page.footer">
		<fo:static-content flow-name="footer" font-family="sans-serif">
			<xsl:call-template name="page.footer.content"/>
		</fo:static-content>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PAGE FOOTER
====================================
====================================-->
	<xsl:template name="page.footer.content">
		<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
		<fo:table table-layout="fixed">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.25 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.25 * $pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next="always" keep-together="always">
					<fo:table-cell text-align="start"  wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block font-size="12pt">Character: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/name"/></fo:inline></fo:block>
						<fo:block font-size="12pt">Player: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/playername"/></fo:inline></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center" wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block text-align="center" font-size="12pt">PCGen Character Template by Andrew Maitland (LegacyKing) and Stefan Radermacher (Zaister), based on work by Frugal, ROG, Arcady, Barak, Dimrill, &amp; Dekker.</fo:block>
						<fo:block text-align="center" font-size="12pt">Created using <fo:basic-link external-destination="http://pcgen.org/" show-destination="true" color="blue" text-decoration="underline">PCGen</fo:basic-link> <xsl:value-of select="/character/export/version"/> on <xsl:value-of select="/character/export/date"/><xsl:text> at </xsl:text><xsl:value-of select="/character/export/time"/></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block font-size="12pt">
						Level:<xsl:value-of select="/character/basics/classes/levels_total"/> (CR:<xsl:value-of select="/character/basics/cr"/>)</fo:block>
						<fo:block font-size="12pt">Page <fo:page-number/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--		Start the character		-->
	<xsl:template match="character">
		<!-- calculate the number of weapons and skills on the first page -->
		<xsl:variable name="first_page_weapon_count">
			<xsl:call-template name="view.weapon.num"/>
		</xsl:variable>
		<xsl:variable name="first_page_skills_count">
			<xsl:call-template name="view.skills.num"/>
		</xsl:variable>
		<xsl:message>Number of weapons on first page = <xsl:value-of select="$first_page_weapon_count"/></xsl:message>
		<xsl:message>Number of skills on first page = <xsl:value-of select="$first_page_skills_count"/></xsl:message>
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<xsl:call-template name="page.layouts"/>
			<!--
				Start the first page
				-->
			<fo:page-sequence>
				<xsl:attribute name="master-reference">Portrait</xsl:attribute>
				<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<!--	CHARACTER BODY STARTS HERE !!!	-->
				<fo:flow flow-name="body"  font-size="12pt">
					<!--	CHARACTER HEADER	-->
					<fo:block span="all" space-after.optimum="3pt">
						<xsl:apply-templates select="basics"/>
					</fo:block>
					<fo:block span="all">
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.29 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.26 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.45 * $pagePrintableWidth" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell number-rows-spanned="1" border-width="1pt" border-color="red">
										<xsl:apply-templates select="abilities"/>
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2" border-width="1pt" border-color="red">
										<xsl:apply-templates select="." mode="hp_table"/>
										<xsl:apply-templates select="armor_class"/>
										<xsl:apply-templates select="initiative"/>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
								<!-->		<xsl:apply-templates select="basics/bab" mode="bab"/>	-->
										<xsl:call-template name="encumbrance"/>
									</fo:table-cell>
									<fo:table-cell number-rows-spanned="2">
										<xsl:apply-templates select="skills">
											<xsl:with-param name="first_skill" select="0"/>
											<xsl:with-param name="last_skill" select="$first_page_skills_count"/>
											<xsl:with-param name="column_width" select="0.45 * $pagePrintableWidth"/>
										</xsl:apply-templates>
										<xsl:apply-templates select="skillinfo"/>
										<xsl:apply-templates select="class_features/bardic_music"/>
										<xsl:apply-templates select="class_features/turning[@kind='UNDEAD']">
											<xsl:with-param name="column_width" select="0.45 * $pagePrintableWidth"/>
										</xsl:apply-templates>
										<xsl:apply-templates select="class_features/eclipse_channeling"/>
										<xsl:apply-templates select="class_features/channel_energy"/>
										<xsl:apply-templates select="checklists"/>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2">
										<xsl:apply-templates select="saving_throws"/>
										<xsl:apply-templates select="attack" mode="ranged_melee"/>
										<xsl:apply-templates select="weapons/martialarts"/>
										<xsl:apply-templates select="weapons/unarmed"/>
										<xsl:apply-templates select="weapons/naturalattack"/>
										<xsl:apply-templates select="weapons/spiritweaponmelee"/>
										<xsl:apply-templates select="weapons/spiritweaponranged"/>
										<xsl:apply-templates select="weapons">
											<xsl:with-param name="first_weapon" select="1"/>
											<xsl:with-param name="last_weapon" select="$first_page_weapon_count"/>
											<xsl:with-param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
										</xsl:apply-templates>
										<xsl:apply-templates select="protection"/>
										<xsl:apply-templates select="class_features/rage"/>
										<xsl:apply-templates select="class_features/wildshape"/>
										<xsl:apply-templates select="class_features/stunning_fist"/>
										<xsl:apply-templates select="class_features/ki_pool"/>
										<xsl:apply-templates select="class_features/wholeness_of_body"/>
										<xsl:apply-templates select="class_features/layonhands"/>
										<xsl:apply-templates select="class_features/psionics"/>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
			<!--
				Start the Second page
				-->
			<fo:page-sequence>
				<xsl:attribute name="master-reference">Portrait 2 Column</xsl:attribute>
				<xsl:attribute name="font-family"><xsl:value-of select="$PCGenFont"/></xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body"  font-size="12pt">
					<fo:block>
						<xsl:apply-templates select="weapons">
							<xsl:with-param name="first_weapon" select="$first_page_weapon_count+1"/>
							<xsl:with-param name="last_weapon" select="9999"/>
							<xsl:with-param name="column_width" select="0.5 * $pagePrintableWidth - 1"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="skills">
							<xsl:with-param name="first_skill" select="$first_page_skills_count+1"/>
							<xsl:with-param name="last_skill" select="9999"/>
							<xsl:with-param name="column_width" select="0.5 * $pagePrintableWidth - 1"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="class_features/turning[@kind!='UNDEAD']">
							<xsl:with-param name="column_width" select="0.5 * $pagePrintableWidth - 1"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="equipment" />
						<xsl:apply-templates select="weight_allowance"/>
						<xsl:call-template name="money"/>
						<xsl:apply-templates select="misc/magics"/>
						<xsl:apply-templates select="languages"/>
						<xsl:apply-templates select="misc/companions"/>
						<xsl:apply-templates select="archetypes"/>	
						<xsl:apply-templates select="animal_tricks"/>	
						<xsl:apply-templates select="special_abilities"/>
						<xsl:apply-templates select="traits"/>
						<xsl:apply-templates select="afflictions"/>
						<xsl:apply-templates select="racial_traits"/>
						<xsl:apply-templates select="special_attacks"/>
						<xsl:apply-templates select="special_qualities"/>
						<xsl:apply-templates select="intelligent_items"/>
						<xsl:apply-templates select="talents"/>	
						<!-- Eclipse Section - Having it's own section is creating an additional blank page -->
						<xsl:apply-templates select="charcreations"/>
						<xsl:apply-templates select="disadvantages"/>
						<xsl:apply-templates select="spellcasteroutputs"/>
						<xsl:apply-templates select="eclipse_abilities"/>
						<xsl:apply-templates select="martial_arts"/>
						<xsl:apply-templates select="mystic_artists"/>
						<xsl:apply-templates select="witchcrafts"/>
						<xsl:apply-templates select="channelings"/>
						<xsl:apply-templates select="dominions"/>
						<xsl:apply-templates select="path_dragons"/>	
						<!-- McWoD Edition Style -->
						<xsl:apply-templates select="vampire_disciplines"/>
						<xsl:apply-templates select="demon_cants"/>
						<xsl:apply-templates select="werewolf_rites"/>
						<xsl:apply-templates select="mage_gnosises"/>	
						<!-- End McWoD Edition Style -->
						<!-- Saga Edition Style -->
						<xsl:apply-templates select="force_techniques"/>
						<xsl:apply-templates select="force_powers"/>
						<xsl:apply-templates select="force_secrets"/>	
						<!-- End Saga Edition Style -->
						<!-- 4th Edition Style -->	
						<xsl:apply-templates select="powers_classfeatures"/>
						<xsl:apply-templates select="powers_featpowers"/>
						<xsl:apply-templates select="powers_atwills"/>
						<xsl:apply-templates select="powers_encounters"/>
						<xsl:apply-templates select="powers_dailies"/>
						<xsl:apply-templates select="powers_utilities"/>	
						<!-- End 4th Edition Style -->
						<xsl:apply-templates select="salient_divine_abilities"/>
						<xsl:apply-templates select="feats"/>
						<xsl:apply-templates select="domains"/>
						<xsl:apply-templates select="weapon_proficiencies"/>
<!-->						<xsl:apply-templates select="proficiency_specials"/>-->
						<xsl:apply-templates select="templates"/>
						<xsl:apply-templates select="tempbonuses"/>
						<xsl:apply-templates select="prohibited_schools"/>
						<xsl:apply-templates select="companions"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
<!-->	ADITIONAL PAGES for Spells 
			<fo:page-sequence>
				<xsl:attribute name="master-reference">Portrait</xsl:attribute>
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body"  font-size="12pt">
					<fo:block span="all" space-after.optimum="3pt">
						<xsl:apply-templates select="spells"/>
					</fo:block> -->


<!--		ADDITIONAL PAGES	-->
			<xsl:apply-templates select="spells"/>
			<xsl:apply-templates select="basics" mode="bio"/>
			<xsl:apply-templates select="basics/notes" mode="bio"/>
		</fo:root>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CHARACTER HEADER
====================================
====================================-->
	<xsl:template match="basics">
		<!-- Character Header -->
		<fo:table table-layout="fixed">
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
					<!--  -->
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
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:value-of select="name"/>
							<xsl:if test="string-length(followerof) &gt; 0">	- <xsl:value-of select="followerof"/>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3" font-weight="bold">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="playername"/>
							
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="deity/name"/>	
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="region"/>	
						</fo:block>
					</fo:table-cell>

					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="alignment/long"/>	
						</fo:block>
					</fo:table-cell>
					<xsl:if test="string-length(portrait/portrait_thumb) &gt; 0">
						<fo:table-cell/>
						<fo:table-cell number-rows-spanned="6">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'picture'"/>
							</xsl:call-template>
							<fo:block>
								<xsl:variable name="portrait_file" select="portrait/portrait_thumb"/>
								<fo:external-graphic src="file:{$portrait_file}">
									<xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 14) div 8" />mm</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
					</xsl:if>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">Character Name</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">Player Name</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">Deity</fo:block>		
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">Region</fo:block>	
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">Alignment</fo:block>	
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
				</fo:table-row>

<!-- Second Row -->
				<fo:table-row>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
<!-->						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="classes/shortform"/>
						</fo:block>-->
						<fo:block font-size="12pt" padding-top="3pt">
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
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="race"/>
							<xsl:if test="string-length(race/racetype) &gt; 0"> / 
								<xsl:value-of select="race/racetype"/>
							</xsl:if>
							<xsl:if test="string-length(race/racesubtype) &gt; 0"> / 
								<xsl:value-of select="race/racesubtype"/>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="size/long"/>
							<xsl:if test="face/face != ''"> / <xsl:value-of select="face/face"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="height/total"/> / 
							<xsl:value-of select="weight/weight_unit"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="vision/all"/>
							<xsl:if test="vision/all = ''">Normal</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">CLASS</fo:block>
					</fo:table-cell>
					
				
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">RACE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">SIZE / FACE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">HEIGHT / WEIGHT</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">VISION</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>

<!--	Third Row  -->
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="classes/levels_total"/>
							<xsl:if test="classes/levels_total != classes/levels_ecl">/<xsl:value-of select="classes/levels_ecl"/>
							</xsl:if>
							(<xsl:value-of select="cr"/>)
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="experience/current"/> / <xsl:value-of select="experience/next_level"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="age"/>
							<xsl:if test="birthday != ''"> (<xsl:value-of select="birthday"/>)</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="gender/long"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="eyes/color"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="hair/color"/>
								<xsl:if test="hair/color != '' and hair/length !=''">, <xsl:value-of select="hair/length"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">	
							<xsl:if test="poolpoints/cost &gt; 0"> <xsl:value-of select="poolpoints/cost"/> </xsl:if>	
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>

<!-- Third ROW Text-->
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">
							<xsl:text>Character Level</xsl:text>
							<xsl:if test="classes/levels_total != classes/levels_ecl">
								<xsl:text>/ECL</xsl:text>
							</xsl:if>
							<xsl:text> (CR)</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">EXP  /   NEXT LEVEL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">AGE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">GENDER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">EYES</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt">HAIR</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="1pt"></fo:block>	
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>	
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURES

  Returns the size in MM the class
  features take up on the LHS of the
  first page
====================================
====================================-->
	<xsl:template name="features.left">
		<xsl:param name="features"/>
		<xsl:param name="RunningTotal" select="0"/>
		<xsl:choose>
			<xsl:when test="not($features)">
				<!--  No more Items so return Running Total -->
				<xsl:copy-of select="$RunningTotal"/>
			</xsl:when>
			<xsl:otherwise>
				<!--  Call template for remaining Items -->
				<xsl:variable name="ClassLength">
					<xsl:choose>
						<xsl:when test="name($features[1]) = 'rage'">25</xsl:when>
						<xsl:when test="name($features[1]) = 'wildshape'">25</xsl:when>
						<xsl:when test="name($features[1]) = 'stunning_fist'">14</xsl:when>
						<xsl:when test="name($features[1]) = 'ki_pool'">11</xsl:when>
						<xsl:when test="name($features[1]) = 'wholeness_of_body'">14</xsl:when>
						<xsl:when test="name($features[1]) = 'psionics'">56</xsl:when>
						<xsl:when test="name($features[1]) = 'layonhands'">14</xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="features.left">
					<xsl:with-param name="features" select="$features[position() &gt; 1]"/>
					<xsl:with-param name="RunningTotal" select="$ClassLength + $RunningTotal"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURES

  Returns the size in MM the class
  features take up on the RHS of the
  first page
====================================
====================================-->
	<xsl:template name="features.right">
		<xsl:param name="features"/>
		<xsl:param name="RunningTotal" select="0"/>
		<xsl:variable name="bardic_music">
			<xsl:choose>
				<xsl:when test="count($features/bardic_music) &gt; 0">18</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="turning">
			<xsl:choose>
				<xsl:when test="count($features/turning) &gt; 0">44</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="eclipse_channeling">
			<xsl:choose>
				<xsl:when test="count($features/eclipse_channeling) &gt; 0">44</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="channel_energy">
			<xsl:choose>
				<xsl:when test="count($features/channel_energy) &gt; 0">14</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:copy-of select="$bardic_music + $turning + $eclipse_channeling + $channel_energy"/>
	</xsl:template>
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
		This does not seem to work very well.	-->

		<xsl:value-of select="floor((140-$featureheight)div 28) "/>

<!--		For now, just make it 3 weapons max.
		
		<xsl:value-of select="4"/>	-->
	</xsl:template>
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
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">
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
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="base"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.base.modifier'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="basemod"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.score'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="no_temp_score"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.modifier'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="no_temp_modifier"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.score'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="score"/>
							</fo:block>
							</xsl:if>
						</fo:table-cell>
						<fo:table-cell/>
<!-- Temp Score and Mod-->
						<fo:table-cell height="4pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.temp.modifier'"/>
							</xsl:call-template>
							<xsl:if test="score != no_temp_score">
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="modifier"/>
							</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2pt">
						<fo:table-cell/>
					</fo:table-row>
				</xsl:for-each>
			
			</fo:table-body>
	</xsl:when>

<xsl:otherwise>
<!--><xsl:if test="/character/house_var/oldstyleabilitystatblockdisplay &lt; 1">-->
			<fo:table-body>
				<fo:table-row>
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
						<fo:table-cell>
						<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'stat.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">
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
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="base"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					
						<xsl:if test="no_temp_score != base">
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'stat.base.score'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="12pt">
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
							<fo:block space-before.optimum="2pt" font-size="12pt">
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
								<fo:block space-before.optimum="2pt" font-size="12pt">
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
								<fo:block space-before.optimum="2pt" font-size="12pt">
									<xsl:value-of select="modifier"/>
								</fo:block>
							</xsl:if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2pt"><fo:table-cell><fo:block></fo:block></fo:table-cell></fo:table-row>
				</xsl:for-each>
			</fo:table-body>
	</xsl:otherwise>
	</xsl:choose>
<!-->		</xsl:if>-->
		</fo:table>
		<!-- END Ability Block -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - HP
====================================
====================================-->
	<xsl:template match="character" mode="hp_table">
		<fo:table table-layout="fixed">
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
					<!-- space  -->
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
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell>
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">WOUNDS/CURRENT HP</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">SUBDUAL DAMAGE</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">DAMAGE REDUCTION</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="12pt">SPEED</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">HP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">hit points</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="12pt">
									<xsl:value-of select="hit_points/points"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.current'"/>
								</xsl:call-template>
								<fo:block font-size="12pt"/>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.subdual'"/>
								</xsl:call-template>
								<fo:block font-size="12pt"/>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'damage.reduction'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:value-of select="hit_points/damage_reduction"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell/>
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'speed'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
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
					<fo:table-column column-width="2mm"/><!-- space  -->
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
							<fo:table-cell/><!-- TITLE Vitality -->
							<fo:table-cell/><!-- space -->
							<fo:table-cell>	<!-- TOTAL Vitality -->
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">WOUNDS/CURRENT HP</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">SUBDUAL DAMAGE</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell/><!-- TITLE Wound points -->
							<fo:table-cell/><!-- space -->
							<fo:table-cell>	<!-- TOTAL Wound points -->
								<fo:block/>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="4pt">DAMAGE REDUCTION</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="after">
								<fo:block text-align="center" font-size="12pt">SPEED</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">VP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">Vitality</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="12pt">
									<xsl:value-of select="hit_points/points"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.current'"/>
								</xsl:call-template>
								<fo:block font-size="12pt"/>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.subdual'"/>
								</xsl:call-template>
								<fo:block font-size="12pt"/>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.title'"/>
								</xsl:call-template>
								<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">WP</fo:block>
								<fo:block line-height="4pt" font-size="4pt">Wound Points</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'hp.total'"/>
								</xsl:call-template>
								<fo:block space-before.optimum="2pt" font-size="12pt">
									<xsl:value-of select="hit_points/alternate"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'damage.reduction'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:value-of select="hit_points/damage_reduction"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell/><!-- space -->
							<fo:table-cell display-align="center">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'speed'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
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
		<fo:table table-layout="fixed" space-before="2pt">
			<fo:table-column column-width="12mm"/>
			<!-- TITLE -->
			<fo:table-column column-width="1mm"/>
			<!-- space -->
			<fo:table-column column-width="8mm"/>
			<!-- TOTAL AC -->
			<fo:table-column column-width="1mm"/>
			<!-- : -->
			<fo:table-column column-width="8mm"/>
			<!-- FLAT -->
			<fo:table-column column-width="1mm"/>
			<!-- : -->
			<fo:table-column column-width="8mm"/>
			<!-- TOUCH -->
			<fo:table-column column-width="2mm"/>
			<!-- = -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- BASE -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- armour -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- armour -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- stat -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!--  size -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- natural armour-->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.09 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- deflection -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.09 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- Dodge -->
			<fo:table-column column-width="2mm"/>
			<!-- + -->

			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- misc   -->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- miss chance -->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- arcane spell failure -->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- armour check-->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- SR -->
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">AC</fo:block>
						<fo:block line-height="4pt" font-size="4pt">armor class</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.total'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">:</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.flatfooted'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="flat"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">:</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac.touch'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="touch"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">=</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="base"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="armor_bonus"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="shield_bonus"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="stat_mod"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="size_mod"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="natural"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="deflection"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="dodge"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="12pt">
							<xsl:value-of select="morale"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="12pt">
							<xsl:value-of select="insight"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="12pt">
							<xsl:value-of select="sacred"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="12pt">
							<xsl:value-of select="profane"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<fo:block text-align="center" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac'"/>
						</xsl:call-template>
						<fo:block text-align="center" font-size="12pt">
							<xsl:value-of select="misc"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="12pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="12pt">FLAT</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="12pt">TOUCH</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARMOR BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SHIELD BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">STAT</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SIZE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="3pt">NATURAL ARMOR</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="3pt">DEFLEC- TION</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">DODGE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">Morale</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">Insight</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">Sacred</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">Profane</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC</fo:block>
					</fo:table-cell>

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
		<fo:table table-layout="fixed" space-before="2pt">
			<!-- 0.26 * $pagePrintableWidth - mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.49 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.17 * (0.26 * $pagePrintableWidth - 8)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="4mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- miss chance -->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- arcane spell failure -->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- armour check-->
			<fo:table-column column-width="2mm"/>
			<!-- space -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.08 * (0.71 * $pagePrintableWidth - 69)" />mm</xsl:attribute>
			</fo:table-column>
			<!-- SR -->
			<fo:table-body>
				<fo:table-row height="2pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">INITIATIVE</fo:block>
						<fo:block line-height="4pt" font-size="4pt">modifier</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="12pt">=</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.general'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="12pt">
							<xsl:value-of select="dex_mod"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="12pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.general'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="12pt">
							<xsl:value-of select="misc_mod"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'miss_chance'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<!-- Miss chance -->
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spell_failure'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="spell_failure"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'ac_check'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="check_penalty"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spell_resistance'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="spell_resistance"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">DEX MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC MODIFIER</fo:block>
					</fo:table-cell>
					<!-- New Stuff	-->
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISS CHANCE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">Arcane Spell Failure</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARMOR CHECK PENALTY</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SPELL RESIST</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END ini-base table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Base Attack TABLE
====================================
====================================-->
	<xsl:template match="bab" mode="bab">
		<!-- BEGIN ini-base table -->
		<fo:table table-layout="fixed">
			<!-- 0.26 * $pagePrintableWidth - 2 mm -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.44 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.56 * (0.26 * $pagePrintableWidth - 4)" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row height="2pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bab.title'"/>
						</xsl:call-template>
						<fo:block line-height="10pt" font-weight="bold" font-size="7.5pt">BASE ATTACK</fo:block>
						<fo:block line-height="4pt" font-size="4pt">bonus</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bab.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="12pt">
							<xsl:call-template name="process.attack.string">
								<xsl:with-param name="bab" select="."/>
                                <xsl:with-param name="maxrepeat" select="4"/>
							</xsl:call-template>
<!-- What is this?-->							<!--xsl:value-of select="../../attack/melee/base_attack_bonus"/-->
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END ini-base table -->
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - encumbrance TABLE
====================================
====================================-->
	<xsl:template name="encumbrance">
		<!-- BEGIN encumbrance table -->
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
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.title'"/>
						</xsl:call-template>
					<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">Encumbrance</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'initiative.total'"/>
						</xsl:call-template>
						<fo:block space-before.optimum="2pt" font-size="12pt">
							<xsl:value-of select="/character/equipment/total/load"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END encumbrance table -->
	</xsl:template>

	<xsl:template name="skills.empty">
		<xsl:param name="pos"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$pos mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row height="9pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade)"/></xsl:call-template>
			<fo:table-cell/>
			<fo:table-cell/>
			<fo:table-cell number-columns-spanned="2"/>
			<fo:table-cell/>
			<fo:table-cell number-columns-spanned="2"/>
			<fo:table-cell>
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade, '.total')"/></xsl:call-template>
			</fo:table-cell>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">=</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
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

			<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.border'"/></xsl:call-template>
				<xsl:copy-of select="$columns"/>
				<fo:table-body>
					<fo:table-row height="2pt">
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.header'"/></xsl:call-template>
						<fo:table-cell></fo:table-cell>
						<fo:table-cell number-columns-spanned="2" border-top-width="1pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="0pt">
							<fo:block text-align="left" space-before.optimum="4pt" line-height="4pt" font-size="12pt">
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
							<fo:block text-align="end" line-height="10pt" font-weight="bold" font-size="12pt">SKILLS</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="10">
								<fo:block text-align="end" space-before.optimum="4pt" line-height="4pt" font-size="12pt">
									<xsl:text>MAX RANKS: </xsl:text>
									<xsl:value-of select="max_class_skill_level"/>/<xsl:value-of select="max_cross_class_skill_level"/>
								</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'skills.header'"/></xsl:call-template>
						<fo:table-cell></fo:table-cell>
						<fo:table-cell number-columns-spanned="2">
							<fo:block font-weight="bold" font-size="12pt">
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




			<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
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
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade)"/></xsl:call-template>
								<fo:table-cell>
									<fo:block font-size="12pt" font-family="ZapfDingbats">
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
											<fo:block space-before.optimum="1pt" font-size="12pt">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="contains(type, 'SkillUse') and string-length(name) &lt; 40">
											<fo:block space-before.optimum="1pt" font-size="12pt" font-style="italic">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="not(contains(type, 'SkillUse')) and string-length(name) &lt; 45">
											<fo:block space-before.optimum="1pt" font-size="12pt">
												<xsl:value-of select="name"/>
											</fo:block>
										</xsl:when>
										<xsl:when test="contains(type, 'SkillUse') and string-length(name) &lt; 45">
											<fo:block space-before.optimum="1pt" font-size="12pt" font-style="italic">
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
								<fo:table-cell number-columns-spanned="2"/>
								<fo:table-cell>
									<fo:block space-before.optimum="1pt" font-size="12pt">
										<xsl:value-of select="ability"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"/>
								<fo:table-cell>
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('skills.', $shade, '.total')"/></xsl:call-template>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
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
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">=</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
										<xsl:value-of select="ability_mod"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2">
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">
										<xsl:if test="ranks &gt; 0">+</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
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
									<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="12pt">
										<xsl:if test="misc_mod!=0">+</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
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
						<fo:table-cell number-columns-spanned="17" padding-top="1pt">
							<fo:block text-align="center" font-size="12pt">
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
							<fo:block text-align="center" font-size="12pt" font-weight="bold">Conditional Modifiers:</fo:block>
								<xsl:for-each select="conditional_modifiers/skillbonus">
									<fo:block font-size="12pt" space-before.optimum="2pt"><xsl:value-of select="description"/></fo:block>
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

		<fo:table table-layout="fixed" space-before="2mm" padding="0.5pt">
			<fo:table-column column-width="86mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="30mm"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell padding-top="1pt" border-width="0.5pt" border-style="solid">
							<fo:block text-align="center" font-size="12pt" font-weight="bold">Conditional Modifiers:</fo:block>
								<xsl:for-each select="conditional_modifiers/skillbonus">
									<fo:block font-size="12pt" space-before.optimum="1pt"><xsl:value-of select="description"/></fo:block>
								</xsl:for-each>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
		</fo:table>
		</xsl:if>
		<!-- END Skills table -->
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - SAVES TABLE
====================================
====================================-->
	<xsl:template match="saving_throws">
		<!-- BEGIN Saves table -->
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="82mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 86" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<xsl:apply-templates select="." mode="saves"/>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell padding-start="1pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'border'"/>
						</xsl:call-template>
						<fo:block font-size="4pt">Conditional Modifiers</fo:block>
						<fo:block font-size="4pt">
							<xsl:value-of select="conditional_modifiers"/>
						</fo:block>
					</fo:table-cell>
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
		<fo:table table-layout="fixed">
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
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">SAVING THROWS</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE SAVE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MAGIC</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">EPIC</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="saving_throw">
					<fo:table-row space-before="2pt">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'saves.title'"/>
							</xsl:call-template>
							<fo:block line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">
								<xsl:value-of select="translate(name/long, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
							</fo:block>
							<fo:block line-height="4pt" font-size="4pt">(<xsl:value-of select="ability"/>)</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'saves.total'"/>
							</xsl:call-template>
							<fo:block space-before.optimum="2pt" font-size="12pt">
								<xsl:value-of select="total"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="12pt">=</fo:block>
						</fo:table-cell>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="base"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="abil_mod"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="magic_mod"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="misc_mod"/></xsl:call-template>
						<xsl:call-template name="saves.entry"><xsl:with-param name="value" select="epic_mod"/></xsl:call-template>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'border.temp'"/>
							</xsl:call-template>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2pt">
						<fo:table-cell/>
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
			<fo:block space-before.optimum="2pt" font-size="12pt">
				<xsl:value-of select="$value"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block text-align="center" space-before.optimum="5pt" font-size="12pt">+</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - ATTACK TABLE
====================================
====================================-->
	<xsl:template match="attack" mode="ranged_melee">
<!-- BEGIN Attack table -->
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 84" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="3mm"/>
			<fo:table-column column-width="21mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="5mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="5mm"/>
			<fo:table-body>
				<xsl:call-template name="to_hit.header" />
				<xsl:apply-templates select="melee" mode="to_hit">
					<xsl:with-param name="title" select="'MELEE'"/>
				</xsl:apply-templates>
				<fo:table-row height="2.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<xsl:apply-templates select="ranged" mode="to_hit">
					<xsl:with-param name="title" select="'RANGED'"/>
				</xsl:apply-templates>
				<fo:table-row height="2.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<xsl:apply-templates select="grapple" mode="to_hit">
					<xsl:with-param name="title" select="'GRAPPLE'"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="cmb" mode="to_hit">
					<xsl:with-param name="title" select="'CMB'"/>
				</xsl:apply-templates>
			</fo:table-body>
		</fo:table>
		<xsl:apply-templates select="cmb" mode="moves"/>
<!-- END Attack table -->
	</xsl:template>
	<xsl:template name="to_hit.header">
		<xsl:param name="dalign" select="'after'"/>
		<fo:table-row>
			<fo:table-cell/>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'TOTAL'"/><xsl:with-param name="font.size" select="'6pt'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'BASE ATTACK BONUS'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'STAT'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'SIZE'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'MISC'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'EPIC'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'TEMP'"/></xsl:call-template>
		</fo:table-row>
	</xsl:template>
	<xsl:template name="attack.header.entry">
		<xsl:param name="title"/>
		<xsl:param name="font.size" select="'4pt'"/>
		<fo:table-cell/>
		<fo:table-cell display-align="after">
			<fo:block text-align="center" font-size="12pt">
				<xsl:attribute name="font-size"><xsl:value-of select="$font.size"/></xsl:attribute>
				<xsl:value-of select="$title"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="melee|ranged|grapple|cmb" mode="to_hit">
		<xsl:param name="title"/>
		<fo:table-row>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'tohit.title'"/>
				</xsl:call-template>
				<fo:block space-before.optimum="0.5pt" line-height="10pt" font-weight="bold" font-size="12pt" space-before="1pt">
					<xsl:value-of select="$title"/>
				</fo:block>
				<fo:block line-height="4pt" font-size="4pt">attack bonus</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
			<xsl:choose>
				<xsl:when test="contains(title, 'CMB' )">
					<xsl:call-template name="iterative.attack.entry">
						<xsl:with-param name="value" select="total"/>
						<xsl:with-param name="bab" select="bab"/>
						<xsl:with-param name="separator" select="'='"/>
                        <xsl:with-param name="fontsize" select="8"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="attack.entry">
						<xsl:with-param name="value" select="total"/>
<!--						<xsl:with-param name="bab" select="bab"/>	-->
						<xsl:with-param name="separator" select="'='"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="base_attack_bonus"/></xsl:call-template>
			<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="stat_mod"/></xsl:call-template>
			<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="size_mod"/></xsl:call-template>
			<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="misc_mod"/></xsl:call-template>
			<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="epic_mod"/></xsl:call-template>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'border.temp'"/>
				</xsl:call-template>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

<!-- Begin CMB different moves -->
	<xsl:template match="cmb" mode="moves">
		<!-- BEGIN CMB table -->
		<fo:table table-layout="fixed" >
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="1mm"/>
			<fo:table-column column-width="19mm"/>
			<fo:table-column column-width="1mm"/>
			<fo:table-column column-width="19mm"/>
			<fo:table-column column-width="1mm"/>
			<fo:table-column column-width="19mm"/>
			<fo:table-column column-width="1mm"/>
			<fo:table-column column-width="19mm"/>
			<fo:table-column column-width="1mm"/>
<!--			<fo:table-column column-width="13mm"/> -->
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="(0.55 * $pagePrintableWidth - 96) * 0.5" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="(0.55 * $pagePrintableWidth - 96) * 0.5" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="1mm"/>

			<fo:table-body>
				<xsl:call-template name="cmb.moves_header" />
				<fo:table-row>		
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'tohit.title'" />
						</xsl:call-template>
						<fo:block space-before.optimum="0.5pt" line-height="8pt" font-weight="bold" font-size="12pt" space-before="1pt">
							<xsl:value-of select="'Offense'"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<xsl:call-template name="iterative.attack.entry"><xsl:with-param name="value" select="grapple_attack"/><xsl:with-param name="bab" select="bab"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="iterative.attack.entry"><xsl:with-param name="value" select="trip_attack"/><xsl:with-param name="bab" select="bab"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="iterative.attack.entry"><xsl:with-param name="value" select="disarm_attack"/><xsl:with-param name="bab" select="bab"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="iterative.attack.entry"><xsl:with-param name="value" select="sunder_attack"/><xsl:with-param name="bab" select="bab"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="bullrush_attack"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="overrun_attack"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
<!--					<xsl:call-template name="iterative.attack.entry"><xsl:with-param name="value" select="total"/><xsl:with-param name="separator" select="''"/></xsl:call-template> -->
				</fo:table-row>
			
				<fo:table-row height="2.5pt">
					<fo:table-cell/>
				</fo:table-row>
<!-- Defense entries -->
				<fo:table-row>		
					<fo:table-cell display-align="center">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'tohit.title'" />
						</xsl:call-template>
						<fo:block space-before.optimum="0.5pt" line-height="8pt" font-weight="bold" font-size="12pt" space-before="1pt">
							<xsl:value-of select="'Defense'"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="grapple_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="trip_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="disarm_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="sunder_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="bullrush_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="overrun_defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template>
<!--					<xsl:call-template name="attack.entry"><xsl:with-param name="value" select="defense"/><xsl:with-param name="separator" select="''"/></xsl:call-template> -->
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="cmb.moves_header">
		<fo:table-row>
			<fo:table-cell/>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'GRAPPLE'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'TRIP'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'DISARM'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'SUNDER'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'BULL RUSH'"/></xsl:call-template>
			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'OVERRUN'"/></xsl:call-template>
<!--			<xsl:call-template name="attack.header.entry"><xsl:with-param name="title" select="'BASE'"/><xsl:with-param name="font.size" select="'6pt'"/></xsl:call-template> -->
		</fo:table-row>
	</xsl:template>

	<xsl:template name="attack.entry">
		<xsl:param name="value" />
		<xsl:param name="separator" select="'+'"/>
		<fo:table-cell>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'tohit'"/>
			</xsl:call-template>
			<fo:block space-before.optimum="3pt" font-size="12pt">
				<xsl:value-of select="$value"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell border-bottom="0pt" border-top="0pt">
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'tohit'"/>
			</xsl:call-template>
			<fo:block space-before.optimum="5pt" font-size="12pt">
				<xsl:value-of select="$separator"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	<xsl:template name="iterative.attack.entry">
		<xsl:param name="value" />
		<xsl:param name="bab" />
		<xsl:param name="separator" select="'+'"/>
        <xsl:param name="fontsize" select="'6pt'"/>
		<fo:table-cell>
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'tohit'"/>
			</xsl:call-template>
			<fo:block space-before.optimum="3pt" font-size="12pt">
                <xsl:attribute name="font-size"><xsl:value-of select="$fontsize"/></xsl:attribute>
				<xsl:call-template name="process.attack.string">
					<xsl:with-param name="attack" select="$value"/>
					<xsl:with-param name="bab" select="$bab"/>
                    <xsl:with-param name="maxrepeat" select="4"/> 
				</xsl:call-template>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell border-bottom="0pt" border-top="0pt">
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'tohit'"/>
			</xsl:call-template>
			<fo:block space-before.optimum="5pt" font-size="12pt">
				<xsl:value-of select="$separator"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - Martial Arts ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/martialarts">
		<!-- START Martial Arts Attack Table -->
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">Martial Arts</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
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
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">Spirit Weapon - Melee</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
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
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">Spirit Weapon - Ranged</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">RANGE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="range"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
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
		<xsl:choose>
		<xsl:when test="(weapons/naturalattack) &lt; 1">
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">UNARMED</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="total"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="critical"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell >
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</xsl:when>
		<xsl:otherwise/>
		</xsl:choose>
		
		<!-- STOP Unarmed Attack Table -->
	</xsl:template>


		<!--
====================================
====================================
	TEMPLATE - Natural Weapon ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/naturalattack">
		<!-- START Natural Attack Table -->

		<fo:table table-layout="fixed" space-before="2mm" keep-with-next.within-column="always">
			<fo:table-column column-width="27mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 77" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">
							<xsl:value-of select="name"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
				<xsl:choose>
					<xsl:when test="critmult &gt; 0">
						<fo:block font-size="12pt">CRIT / MULT</fo:block>
					</xsl:when>
					<xsl:otherwise>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</xsl:otherwise>
				</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
<!-->	<xsl:for-each select="naturalattack">-->
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="tohit"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="damage"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="threat"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reach"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			<xsl:choose>
				<xsl:when test="string-length(notes) &gt; 1">
				<fo:table-row>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:text>Special Properties:</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="4">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.border'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-right="2pt">
							<fo:inline> </fo:inline><xsl:value-of select="notes"/>
						</fo:block>
					</fo:table-cell>

				</fo:table-row>
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
			<!-->	</xsl:for-each>-->

			</fo:table-body>
		</fo:table>
		<!-- STOP Spirit Weapon Melee Attack Table -->
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
				<xsl:apply-templates select="common" mode="special_properties">
					<xsl:with-param name="column_width" select="$column_width"/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:for-each>
		<xsl:if test="position() &gt;= $first_weapon">
			<fo:block font-size="12pt" space-before="2mm" color="black">
				<fo:inline font-weight="bold">*</fo:inline>: weapon is equipped
			</fo:block>
			<fo:block font-size="12pt" space-before="1pt" color="black">
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
		<fo:table table-layout="fixed" space-before="2mm" keep-with-next="always" keep-together="always">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 48" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<!-- Name row (including Hand, Type, Size and Crit -->
					<fo:table-cell number-rows-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-weight="bold" font-size="12pt">
							<xsl:variable name="name" select="substring-before(name/short,'(')"/>
							<xsl:variable name="description" select="substring-after(name/short,'(')"/>
							<xsl:value-of select="$name"/>
							<xsl:if test="string-length($name) = 0">
								<xsl:value-of select="name/short"/>
							</xsl:if>
							<xsl:if test="string-length($description) &gt; 0">
								<fo:inline font-size="12pt">
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
						<fo:block font-size="12pt">HAND</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TYPE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">SIZE</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">CRITICAL</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REACH</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<!-- Hand, Type, Size and Crit -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
					<xsl:choose>
						<xsl:when test="string-length(hand) &lt; 9">
						<fo:block font-size="12pt">
							<xsl:value-of select="hand"/>
						</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-size="12pt">
								<xsl:value-of select="hand"/>
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="type"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="size"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="critical/range"/>
							<xsl:text>/x</xsl:text>
							<xsl:value-of select="critical/multiplier"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reach"/> 
							<xsl:value-of select="reachunit"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - weapons - special properties
====================================
====================================-->
	
	<xsl:template match="common" mode="special_properties">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<fo:table table-layout="fixed" keep-with-next="always" keep-together.within-column="always">
			<fo:table-column column-width="20mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 20" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
				<xsl:if test="special_properties != ''">
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">Special Properties</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" space-before="1pt">
							<xsl:value-of select="special_properties"/>
						</fo:block>
					</fo:table-cell>
				</xsl:if>
				<xsl:if test="special_properties = ''">
					<fo:table-cell number-columns-spanned="2" />
				</xsl:if>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
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
		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.5 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">TOTAL ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">DAMAGE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
<!-- DATA-73 Temporary Work Around -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
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
						<fo:block font-size="12pt">
							<xsl:value-of select="$to_hit"/>
						</fo:block>
					</fo:table-cell>
<End DATA-73 Work Around -->
					
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weapon.hilight'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="$damage"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
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
			<fo:block font-size="12pt" font-weight="bold" space-before="1pt">
				<xsl:value-of select="$title"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
			<fo:block font-size="12pt" space-before="1pt">
				<xsl:value-of select="$tohit"/>
			</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.hilight'"/></xsl:call-template>
			<fo:block font-size="12pt" space-before="1pt">
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
		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
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
				<fo:table-row keep-with-next.within-column="always">
					<!-- To hit and Damage titles -->
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" space-before="1pt">To Hit</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" space-before="1pt">Dam</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" space-before="1pt">To Hit</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weapon.title'"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" space-before="1pt">Dam</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="not(w1_h1_p/to_hit = 'N/A' and w1_h1_p/damage = 'N/A' and w2_p_oh/to_hit = 'N/A' and w2_p_oh/damage = 'N/A')">
					<fo:table-row keep-with-next.within-column="always">
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
				<xsl:if test="not(w1_h1_o/to_hit = 'N/A' and w1_h1_o/damage = 'N/A' and w2_p_ol/to_hit = 'N/A' and w2_p_ol/damage = 'N/A')">
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - weapons ranged
====================================
====================================-->
	<xsl:template match="ranges">
		<xsl:param name="column_width" select="0.55 * $pagePrintableWidth - 2"/>
		<fo:table table-layout="fixed">
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
				<xsl:if test="./ammunition">
					<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
						<fo:table-cell number-columns-spanned="6">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'weapon.title'"/>
							</xsl:call-template>
							<fo:block font-size="5pt" font-weight="bold">Ammunition: <xsl:value-of select="ammunition/name"/>
								<xsl:if test="string(./ammunition/special_properties) != ''">
									(<xsl:value-of select="./ammunition/special_properties"/>)
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				<xsl:choose>
				<xsl:when test="count(./range) = 0]">
					<!--  Don't output table rows if there are no ranges -->
				</xsl:when>
				<xsl:otherwise>
	<!-->			<xsl:if test="range[position() &gt; 5]">	-->
	<!-->		<xsl:if test="range[position() &gt; 5 or ../../common/range &gt; 10]">	-->
	<!-->			<xsl:if test="count(./ranges/range) = 6 or count(./ranges/range) = 11">	-->
					<xsl:if test="count(./range) = 6 or count(./range) = 11">
					<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
				<fo:table-row keep-with-next.within-column="always">
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
		</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Protection
====================================
====================================-->
	<xsl:template match="protection">
		<!-- BEGIN Armor table -->
		<fo:table table-layout="fixed" space-before="2mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'protection.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 49" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-header>
				<fo:table-row>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'protection.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt">
						<fo:block font-size="12pt">
							ARMOR
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							TYPE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							AC
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							MAXDEX
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							CHECK
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="3pt">
						<fo:block font-size="4pt">
							SPELL FAILURE
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:for-each select="armor|shield|item">
                    <xsl:if test="(not(contains(fulltype,'BARDING')))or(contains(location,'Equipped'))">
   					<xsl:variable name="shade">
   						<xsl:choose>
   							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
   							<xsl:otherwise>lightline</xsl:otherwise>
   						</xsl:choose>
   					</xsl:variable>
    
   					<fo:table-row>
   						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('protection.', $shade)"/></xsl:call-template>
   						<fo:table-cell>
   							<fo:block font-size="12pt">
   								<xsl:value-of select="name"/>
   							</fo:block>
   						</fo:table-cell>
   						<fo:table-cell text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="type"/>
   							</fo:block>
   						</fo:table-cell>
   						<fo:table-cell text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="totalac"/>
   							</fo:block>
   						</fo:table-cell>
   						<fo:table-cell text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="maxdex"/>
   							</fo:block>
   						</fo:table-cell>
   						<fo:table-cell text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="accheck"/>
   							</fo:block>
   						</fo:table-cell>
   						<fo:table-cell text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="spellfail"/>
   							</fo:block>
   						</fo:table-cell>
   					</fo:table-row>
   					<fo:table-row>
   						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('protection.', $shade)"/></xsl:call-template>
   						<fo:table-cell number-columns-spanned="6" text-align="center">
   							<fo:block font-size="12pt">
   								<xsl:value-of select="special_properties"/>
   							</fo:block>
   						</fo:table-cell>
   					</fo:table-row>
                    </xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURE PER DAY
====================================
====================================-->
	<xsl:template name="class.feature.perday">
		<xsl:param name="attribute"/>
		<xsl:param name="name" />
		<xsl:param name="uses" />
		<xsl:param name="uses.title" select="'Uses per day'" />
		<xsl:param name="description.title" select="''"/>
		<xsl:param name="description" />
		<xsl:param name="width" select="'wide'" />

		<fo:table table-layout="fixed" space-before="2mm" keep-together="always" border-collapse="collapse">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column column-width="18mm"/>
			<fo:table-column>
				<xsl:if test="$width = 'wide' ">
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 20" />mm</xsl:attribute>
                </xsl:if>
				<xsl:if test="$width = 'narrow' ">
                    <xsl:attribute name="column-width"><xsl:value-of select="0.45 * $pagePrintableWidth - 18" />mm</xsl:attribute>
                </xsl:if>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:value-of select="$name"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" text-align="end">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="12pt"><xsl:value-of select="$uses.title"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="3pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
						<fo:block font-size="12pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count">
									<xsl:call-template name="stripLeadingPlus">
										<xsl:with-param name="string" select="$uses"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="$description != '' ">
					<fo:table-row keep-with-next.within-column="always">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
						<xsl:choose>
							<xsl:when test="$description.title != '' ">
								<fo:table-cell padding-top="1pt" text-align="end">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="12pt"><xsl:value-of select="$description.title"/></fo:block>
								</fo:table-cell>
								<fo:table-cell padding-top="1pt" padding-left="3pt">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="12pt"><xsl:value-of select="$description"/></fo:block>
								</fo:table-cell>
							</xsl:when>
							<xsl:otherwise>
								<fo:table-cell padding="3pt" number-columns-spanned="2">
									<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="$attribute"/></xsl:call-template>
									<fo:block font-size="12pt">
										<xsl:value-of select="$description"/>
									</fo:block>
								</fo:table-cell>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - RAGE
====================================
====================================-->
	<xsl:template match="rage">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'rage'"/>
			<xsl:with-param name="name" select="'BARBARIAN RAGE'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="uses.title" select="uses_per_day.title"/>
			<xsl:with-param name="description" select="description"/>
			<xsl:with-param name="description.title" select="' '"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WILDSHAPE
====================================
====================================-->
	<xsl:template match="wildshape">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'wildshape'"/>
			<xsl:with-param name="name" select="'DRUID WILDSHAPE'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="description" select="concat('Duration = ',duration,' Hours')"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PERFORM
====================================
====================================-->
	<xsl:template match="bardic_music">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'BARDIC MUSIC'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
<!--			<xsl:with-param name="description.title" select="effects"/> -->
			<xsl:with-param name="description" select="text"/>
			<xsl:with-param name="width" select="'narrow'"/>
		</xsl:call-template>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling
====================================
====================================-->
	<xsl:template name="eclipse_channeling.intensity">
		<xsl:param name="die"/>
		<xsl:param name="number"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$number mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('turning.', $shade)"/></xsl:call-template>
			<fo:table-cell>
				<fo:block font-size="12pt"><xsl:value-of select="$die"/></fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="12pt"><xsl:value-of select="$number"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling Info
====================================
====================================-->
	<xsl:template name="eclipse_channeling.info">
		<xsl:param name="title"/>
		<xsl:param name="info"/>
		<xsl:param name="info2"/>
		<xsl:param name="info3"/>
		<fo:table-row>
			<fo:table-cell padding-top="1pt" text-align="end">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
				<fo:block font-size="12pt">
					<xsl:value-of select="$title"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block font-size="12pt">
					<xsl:value-of select="$info"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling
====================================
====================================-->
	<xsl:template match="eclipse_channeling">
		<xsl:param name="column_width" select="0.45 * $pagePrintableWidth"/>
		<xsl:variable name="channel_intensity">
			<xsl:value-of select="/channel_intensity"/>
		</xsl:variable>
		<!-- BEGIN Channeling Table -->
		<fo:table table-layout="fixed" space-before="1mm" keep-together="always"  border-collapse="collapse" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.60 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.40 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:value-of select="concat(@type, ' ', @kind)"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell>
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt">INTENSITY CHECK</fo:block>
										<fo:block font-size="12pt">RESULT</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt"><xsl:value-of select="@kind"/> Intensity</fo:block>
										<fo:block font-size="12pt">(Level)</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Intensity Check'"/>
									<xsl:with-param name="info" select="channeling_check" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
						<fo:table table-layout="fixed"  border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row height="1pt">
									<fo:table-cell/>
								</fo:table-row>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'Up to 0'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-8"/>
									
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'1 - 3'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-7" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'4 - 6'"/>
									<xsl:with-param name="number" select="(channel_intensity)-6" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'7 - 9'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-5" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'10 - 12'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-4" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'13 - 15'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-3" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'16 - 18'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-2" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'19 - 21'"/>
									<xsl:with-param name="number" select="number(channel_intensity)-1" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'22 - 25'"/>
									<xsl:with-param name="number" select="channel_intensity" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.intensity">
									<xsl:with-param name="die" select="'26+'"/>
									<xsl:with-param name="number" select="number(channel_intensity)+1" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Channeling level'"/>
									<xsl:with-param name="info" select="level" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Magnitude'"/>
									<xsl:with-param name="info" select="damage" />
									<xsl:with-param name="info2" select="damage_bonus" />
									<xsl:with-param name="info3" select="factor" />
								</xsl:call-template>
								<xsl:call-template name="eclipse_channeling.info">
									<xsl:with-param name="title" select="'Range'"/>
									<xsl:with-param name="info" select="range" />
								</xsl:call-template>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2" padding-top="1pt" text-align="end">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt" padding-top="2pt">
											<xsl:value-of select="notes"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="2">
						<fo:table border-collapse="collapse" padding="0.5pt" table-layout="fixed">
							<fo:table-column column-width="22mm"/>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 22" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="eclipse_channeling.per.day">
									<xsl:with-param name="title" select="concat(@type, '/DAY')"/>
									<xsl:with-param name="value" select="uses_per_day"/>
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END Eclipse Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Eclipse Channeling - Uses Per Day
====================================
====================================-->

	<xsl:template name="eclipse_channeling.per.day">
		<xsl:param name="title" />
		<xsl:param name="value"/>
		<fo:table-row>
			<fo:table-cell  padding-top="2pt" padding-right="2pt">
				<fo:block text-align="end" display-align="center" font-size="12pt"><xsl:value-of select="$title"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block text-align="start" font-size="12pt" font-family="ZapfDingbats">
					<xsl:call-template name="for.loop">
						<xsl:with-param name="count" select="$value"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template name="turning.hitdice">
		<xsl:param name="die"/>
		<xsl:param name="number"/>

		<xsl:variable name="shade">
			<xsl:choose>
				<xsl:when test="$number mod 2 = 0">darkline</xsl:when>
				<xsl:otherwise>lightline</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:table-row>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('turning.', $shade)"/></xsl:call-template>
			<fo:table-cell>
				<fo:block font-size="12pt"><xsl:value-of select="$die"/></fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="12pt"><xsl:value-of select="$number"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template name="turning.info">
		<xsl:param name="title"/>
		<xsl:param name="info"/>

		<fo:table-row>
			<fo:table-cell padding-top="1pt" text-align="end">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
				<fo:block font-size="12pt">
					<xsl:value-of select="$title"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block font-size="12pt">
					<xsl:value-of select="$info"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TURNING
====================================
====================================-->
	<xsl:template match="turning">
		<xsl:param name="column_width" select="0.45 * $pagePrintableWidth"/>
		<!-- BEGIN Turning Table -->
		<fo:table table-layout="fixed" space-before="1mm" keep-together="always"  border-collapse="collapse" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.60 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.40 * $column_width" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:value-of select="concat(@type, ' ', @kind)"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
					<fo:table-cell>
						<fo:table table-layout="fixed">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt">TURNING CHECK</fo:block>
										<fo:block font-size="12pt">RESULT</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt"><xsl:value-of select="@kind"/> AFFECTED</fo:block>
										<fo:block font-size="12pt">(MAXIMUM HIT DICE)</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turning Check'"/>
									<xsl:with-param name="info" select="turn_check" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell>
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
						<fo:table table-layout="fixed"  border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.30 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<fo:table-row height="1pt">
									<fo:table-cell/>
								</fo:table-row>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'Up to 0'"/>
									<xsl:with-param name="number" select="number(level)-4" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'1 - 3'"/>
									<xsl:with-param name="number" select="number(level)-3" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'4 - 6'"/>
									<xsl:with-param name="number" select="number(level)-2" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'7 - 9'"/>
									<xsl:with-param name="number" select="number(level)-1" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'10 - 12'"/>
									<xsl:with-param name="number" select="level" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'13 - 15'"/>
									<xsl:with-param name="number" select="number(level)+1" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'16 - 18'"/>
									<xsl:with-param name="number" select="number(level)+2" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'19 - 21'"/>
									<xsl:with-param name="number" select="number(level)+3" />
								</xsl:call-template>
								<xsl:call-template name="turning.hitdice">
									<xsl:with-param name="die" select="'22+'"/>
									<xsl:with-param name="number" select="number(level)+4" />
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
					<fo:table-cell>
						<fo:table table-layout="fixed" border-collapse="collapse" padding="0.5pt">
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $column_width" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turn level'"/>
									<xsl:with-param name="info" select="level" />
								</xsl:call-template>
								<xsl:call-template name="turning.info">
									<xsl:with-param name="title" select="'Turn damage'"/>
									<xsl:with-param name="info" select="damage" />
								</xsl:call-template>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2" padding-top="1pt" text-align="end">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning.title'"/></xsl:call-template>
										<fo:block font-size="12pt" padding-top="2pt">
											<xsl:value-of select="notes"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="2">
						<fo:table border-collapse="collapse" padding="0.5pt" table-layout="fixed">
							<fo:table-column column-width="22mm"/>
							<fo:table-column>
								<xsl:attribute name="column-width"><xsl:value-of select="$column_width - 22" />mm</xsl:attribute>
							</fo:table-column>
							<fo:table-body>
								<xsl:call-template name="turns.per.day">
									<xsl:with-param name="title" select="concat(@type, '/DAY')"/>
									<xsl:with-param name="value" select="uses_per_day"/>
								</xsl:call-template>
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END Turning Table -->
	</xsl:template>



	<xsl:template name="turns.per.day">
		<xsl:param name="title" />
		<xsl:param name="value"/>
		<fo:table-row>
			<fo:table-cell  padding-top="2pt" padding-right="2pt">
				<fo:block text-align="end" display-align="center" font-size="12pt"><xsl:value-of select="$title"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'turning'"/></xsl:call-template>
				<fo:block text-align="start" font-size="12pt" font-family="ZapfDingbats">
					<xsl:call-template name="for.loop">
						<xsl:with-param name="count" select="$value"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>

	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CHANNEL ENERGY
====================================
====================================-->
	<xsl:template match="channel_energy">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'CHANNEL ENERGY'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="uses.title" select="uses_per_day.title"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
			<xsl:with-param name="width" select="'narrow'"/>
		</xsl:call-template>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Stunning Fist
====================================
====================================-->
	<xsl:template match="stunning_fist">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'stunningfist'"/>
			<xsl:with-param name="name" select="'STUNNING FIST'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - KI Pool
====================================
====================================-->
	<xsl:template match="ki_pool">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'stunningfist'"/>
			<xsl:with-param name="name" select="'ki Pool'"/>
			<xsl:with-param name="uses" select="uses_per_day"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WHOLENESS OF BODY
====================================
====================================-->
	<xsl:template match="wholeness_of_body">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'wholeness'"/>
			<xsl:with-param name="name" select="'WHOLENESS OF BODY'"/>
			<xsl:with-param name="uses" select="hp_per_day"/>
			<xsl:with-param name="uses.title" select="'HP per day'"/>
		</xsl:call-template>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - LAY ON HANDS
====================================
====================================-->
	<xsl:template match="layonhands">
		<xsl:call-template name="class.feature.perday">
			<xsl:with-param name="attribute" select="'bard'"/>
			<xsl:with-param name="name" select="'LAY ON HANDS'"/>
			<xsl:with-param name="uses" select="hp_per_day"/>
			<xsl:with-param name="uses.title" select="hp_per_day.title"/>
			<xsl:with-param name="description.title" select="' '"/>
			<xsl:with-param name="description" select="description"/>
		</xsl:call-template>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - CHECKLISTS
====================================
====================================-->
	<xsl:template match="checklists">
	
	<xsl:for-each select="checklist">
		<!-- BEGIN Use Per Day Ability table -->
		<fo:table table-layout="fixed" space-before="2mm" keep-together="always" border-collapse="collapse" >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'checklist.border'"/></xsl:call-template>
			<fo:table-column column-width="23mm"/>
			<fo:table-column column-width="63mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" text-align="center">
							<xsl:value-of select="header"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" text-align="end">
							<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" text-align="center"><xsl:value-of select="check_type"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="9pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="check_count"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
						<xsl:if test="name != ''"> <xsl:value-of select="name"/>:</xsl:if>
							<fo:inline font-size="12pt" font-weight="normal"><xsl:value-of select="description"/><xsl:if test="source != ''"> [<xsl:value-of select="source"/>]</xsl:if></fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
|%|
		<!-- END Checklists table -->
	</xsl:for-each>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - RACIAL TRAITS
====================================
====================================-->
	<xsl:template match="racial_traits">
	
	<xsl:for-each select="racial_traits">
		<!-- BEGIN Use Per Day Ability table -->
		<fo:table table-layout="fixed" space-before="2mm" keep-together="always" border-collapse="collapse" >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'checklist.border'"/></xsl:call-template>
			<fo:table-column column-width="23mm"/>
			<fo:table-column column-width="63mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold" text-align="center">
							<xsl:value-of select="header"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" text-align="end">
							<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" text-align="center"><xsl:value-of select="check_type"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="9pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="check_count"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding="3pt" number-columns-spanned="2">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
						<xsl:if test="name != ''"> <xsl:value-of select="name"/>:</xsl:if>
							<fo:inline font-size="12pt" font-weight="normal"><xsl:value-of select="description"/><xsl:if test="source != ''"> [<xsl:value-of select="source"/>]</xsl:if></fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
|%|
		<!-- END Checklists table -->
	</xsl:for-each>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - PSIONICS ATTACK / DEFENCE TABLE
====================================
====================================-->
	<Psionics:attacks>
		<attack name="ego.whip" title="EGO WHIP" damage="1d4 DEX" pp="3"/>
		<attack name="id.insinuation" title="ID INSINUATION" damage="1d2 STR" pp="3"/>
		<attack name="mind.blast" title="MIND BLAST" damage="1d4 CHA" pp="9"/>
		<attack name="mind.thrust" title="MIND THRUST" damage="1d2 INT" pp="1"/>
		<attack name="psychic.crush" title="PSYCHIC CRUSH" damage="2d4 WIS" pp="5"/>
		<defences>
			<defence name="Empty Mind" pp="1" mentalhardness="">
				<attack name="ego.whip" value="+1" />
				<attack name="id.insinuation" value="-2" />
				<attack name="mind.blast" value="+3" />
				<attack name="mind.thrust" value="-3" />
				<attack name="psychic.crush" value="-5" />
			</defence>
			<defence name="Intellect Fortress" pp="5" mentalhardness="3">
				<attack name="ego.whip" value="-2" />
				<attack name="id.insinuation" value="+1" />
				<attack name="mind.blast" value="+0" />
				<attack name="mind.thrust" value="+6" />
				<attack name="psychic.crush" value="+4" />
			</defence>
			<defence name="Mental Barrier" pp="3" mentalhardness="2">
				<attack name="ego.whip" value="-1" />
				<attack name="id.insinuation" value="+4" />
				<attack name="mind.blast" value="-3" />
				<attack name="mind.thrust" value="+1" />
				<attack name="psychic.crush" value="+3" />
			</defence>
			<defence name="Thought Shield" pp="1" mentalhardness="1">
				<attack name="ego.whip" value="-4" />
				<attack name="id.insinuation" value="-1" />
				<attack name="mind.blast" value="-2" />
				<attack name="mind.thrust" value="+4" />
				<attack name="psychic.crush" value="+2" />
			</defence>
			<defence name="Tower of Iron Will" pp="5" mentalhardness="2">
				<attack name="ego.whip" value="+3" />
				<attack name="id.insinuation" value="+0" />
				<attack name="mind.blast" value="-1" />
				<attack name="mind.thrust" value="+5" />
				<attack name="psychic.crush" value="-3" />
			</defence>
			<defence name="Nonpsionic Buffer" pp="" mentalhardness="">
				<attack name="ego.whip" value="-8" />
				<attack name="id.insinuation" value="-9" />
				<attack name="mind.blast" value="+4" />
				<attack name="mind.thrust" value="-8" />
				<attack name="psychic.crush" value="-8" />
			</defence>
			<defence name="Flat-footed or out of Power Points" pp="" mentalhardness="">
				<attack name="ego.whip" value="+8" />
				<attack name="id.insinuation" value="+7" />
				<attack name="mind.blast" value="+8" />
				<attack name="mind.thrust" value="+8" />
				<attack name="psychic.crush" value="+8" />
			</defence>
		</defences>
	</Psionics:attacks>

	<!--
====================================
====================================
	TEMPLATE - PSIONICS
====================================
====================================-->
	<xsl:template name="psionic.entry">
		<xsl:param name="title"/>
		<xsl:param name="title.cols" select="1"/>
		<xsl:param name="value"/>
		<xsl:param name="value.cols" select="1"/>

		<fo:table-cell padding-top="1pt" text-align="end">
			<xsl:attribute name="number-columns-spanned"><xsl:value-of select="$title.cols"/></xsl:attribute>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
			<fo:block font-size="12pt"><xsl:value-of select="$title"/>:</fo:block>
		</fo:table-cell>
		<fo:table-cell padding-top="1pt" text-align="center">
			<xsl:attribute name="number-columns-spanned"><xsl:value-of select="$value.cols"/></xsl:attribute>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
			<fo:block font-size="12pt"><xsl:value-of select="$value"/></fo:block>
		</fo:table-cell>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PSIONICS
====================================
====================================-->
	<xsl:template match="psionics">
		<!-- BEGIN psionicsTable -->
        <xsl:variable name="endpoints" select="7"/>
		<fo:table table-layout="fixed" space-before="2mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
            <fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
            <fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
            <fo:table-column column-width="0.5mm"/>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * ($pagePrintableWidth - $endpoints) div 12" />mm</xsl:attribute>
			</fo:table-column>
            <fo:table-column column-width="0.5mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="12">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">Psionics</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Base PP'"/>
						<xsl:with-param name="value" select="base_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Bonus  PP'"/>
						<xsl:with-param name="value" select="bonus_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Total PP'"/>
						<xsl:with-param name="value" select="total_pp"/>
					</xsl:call-template>
					<fo:table-cell/>
					<xsl:call-template name="psionic.entry">
						<xsl:with-param name="title" select="'Current PP'"/>
						<xsl:with-param name="value" select="''"/>
					</xsl:call-template>
					<fo:table-cell/>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	<xsl:if test = "type = '3.0'">
		<!-- Attack / Defence table -->
		<fo:table table-layout="fixed" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.55 * $pagePrintableWidth - 70" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column column-width="34mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="12pt"/>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
						<fo:block font-size="12pt">Mental Hardness</fo:block>
					</fo:table-cell>
					<xsl:variable name="attacks" select="document('')/*/Psionics:attacks/attack"/>
					<xsl:for-each select="$attacks">
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
							<fo:block font-size="12pt"><xsl:value-of select="@title"/></fo:block>
							<fo:block font-size="4pt">(<xsl:value-of select="@damage"/>) <xsl:value-of select="@pp"/>pp</fo:block>
						</fo:table-cell>
					</xsl:for-each>
				</fo:table-row>

				<xsl:variable name="defences" select="document('')/*/Psionics:attacks/defences/defence"/>
				<xsl:for-each select="$defences">
					<fo:table-row keep-with-previous.within-column="always">
						<fo:table-cell padding-top="1pt" padding-left="3pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics.title'"/></xsl:call-template>
							<fo:block font-size="12pt"><xsl:value-of select="@name"/>
								<xsl:if test="@pp != ''"> (<xsl:value-of select="@pp"/>pp)</xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
							<fo:block font-size="12pt"><xsl:value-of select="@mentalhardness"/></fo:block>
						</fo:table-cell>
						<xsl:for-each select="attack">
							<fo:table-cell padding-top="1pt">
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'psionics'"/></xsl:call-template>
								<fo:block font-size="12pt"><xsl:value-of select="@value"/></fo:block>
							</fo:table-cell>
						</xsl:for-each>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
		<!-- END psionicsTable -->
	</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - DOMAINS
====================================
====================================-->
	<xsl:template match="domains">
		<!-- BEGIN Domains Table -->
		<xsl:call-template name="bold.list">
			<xsl:with-param name="attribute" select="'domains'" />
			<xsl:with-param name="title" select="'DOMAINS'" />
			<xsl:with-param name="list" select="domain"/>
			<xsl:with-param name="name.tag" select="'name'"/>
			<xsl:with-param name="desc.tag" select="'power'"/>
		</xsl:call-template>
		<!-- END Domains Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WEAPON PROFICIENCIES
====================================
====================================-->
	<xsl:template match="weapon_proficiencies">
		<!-- BEGIN weapon_proficiencies Table -->
		<xsl:call-template name="list">
			<xsl:with-param name="attribute" select="'proficiencies'"/>
			<xsl:with-param name="title" select="'PROFICIENCIES'"/>
			<xsl:with-param name="value" select="." />
		</xsl:call-template>
		<!-- END weapon_proficiencies Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - LANGUAGES
====================================
====================================-->
	<xsl:template match="languages">
		<!-- BEGIN Languages Table -->
		<xsl:call-template name="list">
			<xsl:with-param name="attribute" select="'languages'"/>
			<xsl:with-param name="title" select="'LANGUAGES'"/>
			<xsl:with-param name="value" select="." />
		</xsl:call-template>
		<!-- END Languages Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TEMPLATES
====================================
====================================-->
	<xsl:template match="templates">
		<!-- BEGIN Templates Table -->
		<xsl:call-template name="stripped.list">
			<xsl:with-param name="attribute" select="'templates'" />
			<xsl:with-param name="title" select="'TEMPLATES'" />
			<xsl:with-param name="list" select="template"/>
			<xsl:with-param name="name.tag" select="'name'"/>
		</xsl:call-template>
		<!-- END Templates Table -->
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PROHIBITED
====================================
====================================-->
	<xsl:template match="prohibited_schools">
		<xsl:if test=". != ''">
			<xsl:call-template name="list">
				<xsl:with-param name="attribute" select="'prohibited'"/>
				<xsl:with-param name="title" select="'PROHIBITED'"/>
				<xsl:with-param name="value" select="." />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - COMPANIONS
====================================
====================================-->
	<xsl:template match="companions">
		<!-- BEGIN Companions Table -->
		<xsl:apply-templates select="familiar"/>
		<xsl:apply-templates select="mount"/>
		<xsl:apply-templates select="companion"/>
		<xsl:call-template name="followers.list"/>
		<!-- END Companions Table -->
	</xsl:template>
	<xsl:template match="familiar">
		<!-- BEGIN Familiar Table -->
		<xsl:call-template name="show_companion">
			<xsl:with-param name="followerType" select="'Familiar'"/>
		</xsl:call-template>
		<!-- END Familiar Table -->
	</xsl:template>
	<xsl:template match="mount">
		<!-- BEGIN Familiar Table -->
		<xsl:call-template name="show_companion">
			<xsl:with-param name="followerType" select="'Special Mount'"/>
		</xsl:call-template>
		<!-- END Familiar Table -->
	</xsl:template>
	<xsl:template match="companion">
		<!-- BEGIN Familiar Table -->
		<xsl:call-template name="show_companion">
			<xsl:with-param name="followerType" select="'Animal Companion'"/>
		</xsl:call-template>
		<!-- END Familiar Table -->
	</xsl:template>
	<xsl:template name="followers.list">
		<xsl:if test="count(follower) &gt; 0">
			<fo:table table-layout="fixed" space-after.optimum="2mm">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2)" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt" font-weight="bold">Followers: </fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions'"/>
							</xsl:call-template>
							<xsl:for-each select="follower">
								<fo:block font-size="12pt">
									<xsl:value-of select="name"/>
								</fo:block>
							</xsl:for-each>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<xsl:template name="show_companion">
		<xsl:param name="followerType" select="Follower"/>
		<fo:table table-layout="fixed" space-before.optimum="2mm">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2) - 69" />mm</xsl:attribute>
				</fo:table-column>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="14mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="14mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="6">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-weight="bold">
							<xsl:value-of select="$followerType"/>: <xsl:value-of select="name"/> (<xsl:value-of select="race"/>)</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">HP:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="hp"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">AC:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="ac"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">INIT:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="initiative_mod"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">FORT:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="fortitude"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">REF:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="reflex"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">WILL:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="will"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="attacks/attack">
					<xsl:if test="string-length(common/name/long) &gt; 0">
						<fo:table-row keep-with-next.within-column="always">
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:variable name="name" select="substring-before(common/name/long,'(')"/>
									<xsl:variable name="description" select="substring-after(common/name/long,'(')"/>
									<xsl:value-of select="$name"/>
									<xsl:if test="string-length($name) = 0">
										<xsl:value-of select="common/name/long"/>
									</xsl:if>
									<xsl:if test="string-length($description) &gt; 0">
										<fo:inline font-size="12pt">
											<xsl:text>(</xsl:text>
											<xsl:value-of select="$description"/>
										</fo:inline>
									</xsl:if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:value-of select="simple/to_hit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">DAM:</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:value-of select="simple/damage"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">CRIT:</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions'"/>
								</xsl:call-template>
								<fo:block font-size="12pt">
									<xsl:value-of select="common/critical/range"/>/x<xsl:value-of select="common/critical/multiplier"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">Special:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="5">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">
							<xsl:value-of select="special_properties"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			<!-->	<xsl:if test="count(companion/trick) &gt; 0">	-->
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell text-align="end">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">Tricks:</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="5">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">
								<xsl:value-of select="trick"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
		<!-->		</xsl:if> -->
			</fo:table-body>
		</fo:table>
	</xsl:template>








<!-- Disable Previous Equipment Block> -->
	<!--
====================================
====================================
	TEMPLATE - Equipment
====================================
====================================-->
	<xsl:template match="equipment">
		<fo:block>
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'equipment.border'"/>
				</xsl:call-template>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2) - 43" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-column column-width="19mm"/>
				<fo:table-column column-width="6mm"/>
				<fo:table-column column-width="8mm"/>
				<fo:table-column column-width="10mm"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell padding-top="1pt" number-columns-spanned="5">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'equipment.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">EQUIPMENT</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'equipment.title'"/>
						</xsl:call-template>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt">ITEM</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt">LOCATION</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt">QTY</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt"  number-columns-spanned="2">
							<fo:block font-size="12pt">WT / COST</fo:block>
						</fo:table-cell>
<!-->						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt"></fo:block>
						</fo:table-cell>	-->
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'equipment.title'"/>
						</xsl:call-template>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt">TOTAL WEIGHT CARRIED/VALUE</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="12pt">
								<xsl:value-of select="total/weight"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" number-columns-spanned="2">
							<fo:block font-size="12pt">
								<xsl:variable name="TotalValue">
									<xsl:call-template name="Total">
										<xsl:with-param name="Items" select="item[contains(type, 'COIN')=false and contains(type, 'GEM')=false]"/>
										<xsl:with-param name="RunningTotal" select="0"/>
									</xsl:call-template>
								</xsl:variable>
								<xsl:value-of select="format-number($TotalValue, '##,##0.#')"/>gp
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-footer>
				<fo:table-body>
					<xsl:for-each select="item[contains(type, 'COIN')=false and contains(type, 'GEM')=false]">
						<xsl:variable name="shade">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0 ">darkline</xsl:when>
								<xsl:otherwise>lightline</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>

						<fo:table-row>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('equipment.', $shade)"/></xsl:call-template>
							<fo:table-cell>
								<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:if test="contains(type, 'MAGIC') or contains(type, 'PSIONIC')">
										<xsl:attribute name="font-weight">bold</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="name"/>
								</fo:block>
			<!-->					<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="contents"/>
								</fo:block>	-->
			<!-->					<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="special_properties"/>
									<xsl:value-of select="quality"/>
								</fo:block>	-->
								<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="note"/>
								</fo:block>
								<!-- Display the number of charges left if any -->
								<xsl:if test="charges &gt; 0">
									<fo:block font-size="12pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="charges"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<!-- Display the ammunition as a series of checkboxes -->
						<!-->		<xsl:if test="contains(type, 'POTION') and quantity &gt; 1">
									<fo:block font-size="12pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
Potion is Consumable											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>	-->
								<xsl:if test="contains(type, 'AMMUNITION') and quantity &gt; 1">
									<fo:block font-size="12pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="contains(type, 'CONSUMABLE') and quantity &gt; 1">
									<fo:block font-size="12pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
							</fo:table-cell>
							<fo:table-cell text-align="center">
								<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="location"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="quantity"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell  number-columns-spanned="2">
								<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="format-number(weight, '##,##0.#')"/>
									<xsl:if test="quantity &gt; 1">
										(<xsl:value-of select="format-number(weight * quantity, '##,##0.#')"/>)
									</xsl:if>
									<xsl:text> / </xsl:text>
									<xsl:value-of select="format-number(cost, '##,##0.#')"/>
									<xsl:if test="quantity &gt; 1">
										(<xsl:value-of select="format-number(cost * quantity, '##,##0.#')"/>)
									</xsl:if>
								</fo:block>
							</fo:table-cell>
<!-->							<fo:table-cell>
								<fo:block text-align="center" space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="format-number(cost, '##,##0.#')"/>
									<xsl:if test="quantity &gt; 1">
										(<xsl:value-of select="format-number(cost * quantity, '##,##0.#')"/>)
									</xsl:if>
								</fo:block>
							</fo:table-cell>	-->
						</fo:table-row>
<!-- Special Properties Now Span entire row -->
						<fo:table-row>
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('equipment.', $shade)"/></xsl:call-template>
							<fo:table-cell number-columns-spanned="5">
								<fo:block space-before.optimum="1pt" font-size="12pt">
									<xsl:value-of select="special_properties"/>
									<xsl:value-of select="quality"/>
									<xsl:value-of select="contents"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<!-- END Equipment table -->
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - WEIGHT ALLOWANCE SINGLE ENTRY
====================================
====================================-->
	<xsl:template name="weight.entry">
		<xsl:param name="title"/>
		<xsl:param name="value"/>

		<fo:table-cell padding-top="1pt" padding-right="1mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weight.darkline'"/></xsl:call-template>
			<fo:block font-size="12pt" text-align="end"><xsl:value-of select="$title"/></fo:block>
		</fo:table-cell>
		<fo:table-cell padding-top="1pt" padding-left="1mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weight.lightline'"/></xsl:call-template>
			<fo:block font-size="12pt"><xsl:value-of select="$value"/></fo:block>
		</fo:table-cell>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - WEIGHT ALLOWANCE
====================================
====================================-->
	<xsl:template match="weight_allowance">
		<!-- BEGIN Weight table -->
		<fo:table table-layout="fixed" space-before.optimum="2mm" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weight.border'"/></xsl:call-template>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.65 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.35 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.65 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.35 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.65 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="0.35 * 0.5 * ($pagePrintableWidth - 2) div 3" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="6">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'weight.title'"/>
						</xsl:call-template>
						<fo:block font-size="12pt">WEIGHT ALLOWANCE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Light'"/>
						<xsl:with-param name="value" select="light"/>
					</xsl:call-template>
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Medium'"/>
						<xsl:with-param name="value" select="medium"/>
					</xsl:call-template>
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Heavy'"/>
						<xsl:with-param name="value" select="heavy"/>
					</xsl:call-template>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Lift over head'"/>
						<xsl:with-param name="value" select="lift_over_head"/>
					</xsl:call-template>
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Lift off ground'"/>
						<xsl:with-param name="value" select="lift_off_ground"/>
					</xsl:call-template>
					<xsl:call-template name="weight.entry">
						<xsl:with-param name="title" select="'Push / Drag'"/>
						<xsl:with-param name="value" select="push_drag"/>
					</xsl:call-template>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Money
====================================
====================================-->
	<xsl:template name="money">
		<xsl:if test="count (misc/funds/fund|equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]) &gt; 0">
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'money.border'"/>
				</xsl:call-template>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2)" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-header>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'money.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">MONEY</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'money.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt" text-align="end">
								<xsl:variable name="TotalValue">
									<xsl:call-template name="Total">
										<xsl:with-param name="Items" select="equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]"/>
										<xsl:with-param name="RunningTotal" select="0"/>
									</xsl:call-template>
								</xsl:variable>
								Total   = <xsl:value-of select="format-number($TotalValue, '##,##0.#')"/> gp
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-footer>
				<fo:table-body>
					<!-- dump coins -->
					<xsl:for-each select="equipment/item[contains(type, 'COIN')]">
						<xsl:sort order="descending" select="cost" data-type="number"/>
						<xsl:variable name="shade">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
								<xsl:otherwise>lightline</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('money.', $shade)"/></xsl:call-template>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="name"/>: <xsl:value-of select="quantity"/>
									 <fo:inline font-size="12pt">[<xsl:value-of select="location"/>]</fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
					<xsl:variable name="coin_count" select="count( equipment/item[contains(type, 'COIN')] )"/>

					<!-- dump gems -->
					<xsl:for-each select="equipment/item[contains(type, 'GEM')]">
						<xsl:sort order="descending" select="cost" data-type="number"/>
						<xsl:variable name="shade">
							<xsl:choose>
								<xsl:when test="($coin_count + position()) mod 2 = 0">darkline</xsl:when>
								<xsl:otherwise>lightline</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('money.', $shade)"/></xsl:call-template>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="quantity"/> x <xsl:value-of select="name"/> (<xsl:value-of select="cost"/>)
									 <fo:inline font-size="12pt">[<xsl:value-of select="location"/>]</fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
					<xsl:variable name="gem_count" select="count( equipment/item[contains(type, 'GEM')] )"/>

					<!-- misc funds -->
					<xsl:for-each select="misc/funds">
						<xsl:variable name="shade">
							<xsl:choose>
								<xsl:when test="($coin_count + $gem_count + position()) mod 2 = 0 ">darkline</xsl:when>
								<xsl:otherwise>lightline</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('money.', $shade)"/></xsl:call-template>
							<fo:table-cell>
								<fo:block font-size="12pt">
									<xsl:call-template name="paragraghlist">
										<xsl:with-param name="tag" select="'fund'"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Misc Magic
====================================
====================================-->
	<xsl:template match="magics">
		<xsl:if test="count(magic) &gt; 0">
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'magic.border'"/>
				</xsl:call-template>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2)" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-header>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">MAGIC</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.lightline'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">
								<xsl:call-template name="paragraghlist">
									<xsl:with-param name="tag" select="'magic'"/>
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>

<!-- Diable Previous Equipment Stuff -->

	
	
	
	
	
	
	
	<!--
====================================
====================================
	TEMPLATE - Other Companions
====================================
====================================-->
	<xsl:template match="misc/companions">
		<xsl:if test="count(companion) &gt; 0">
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'magic.border'"/>
				</xsl:call-template>
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2)" />mm</xsl:attribute>
				</fo:table-column>
				<fo:table-header>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell padding-top="1pt">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.title'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">OTHER COMPANIONS</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.lightline'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">
								<xsl:call-template name="paragraghlist">
									<xsl:with-param name="tag" select="'companion'"/>
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Temporary Bonuses
====================================
====================================-->
	<xsl:template match="tempbonuses">
		<xsl:if test="count(tempbonus) &gt; 0">
			<xsl:call-template name="stripped.list">
				<xsl:with-param name="attribute" select="'tempbonuses'" />
				<xsl:with-param name="title" select="'TEMPORARY BONUS'" />
				<xsl:with-param name="list" select="tempbonus" />
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="''"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Special Abilities
====================================
====================================-->
	<xsl:template match="special_abilities">
		<xsl:if test="count(ability) &gt; 0">
			<xsl:call-template name="stripped.list">
				<xsl:with-param name="attribute" select="'special_abilities'" />
				<xsl:with-param name="title" select="'SPECIAL ABILITIES'" />
				<xsl:with-param name="list" select="ability" />
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="''"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPECIAL ATTACKS
====================================
====================================-->
	<xsl:template match="special_attacks">
		<xsl:if test="count(special_attack) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_attacks'" />
				<xsl:with-param name="title" select="'Special Attacks'" />
				<xsl:with-param name="list" select="special_attack"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - SPECIAL QUALITIES
====================================
====================================-->
	<xsl:template match="special_qualities">
		<xsl:if test="count(special_quality) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Special Qualities'" />
				<xsl:with-param name="list" select="special_quality"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--	
====================================
====================================
	TEMPLATE - ARCHETYPES
====================================
====================================-->
	<xsl:template match="archetypes">
		<xsl:if test="count(archetype) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'archetypes'" />
				<xsl:with-param name="title" select="'Archetypes'" />
				<xsl:with-param name="list" select="archetype"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--	
====================================
====================================
	TEMPLATE - AFFLICTIONS
====================================
====================================-->
	<xsl:template match="afflictions">
		<xsl:if test="count(affliction) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'afflictions'" />
				<xsl:with-param name="title" select="'Afflictions'" />
				<xsl:with-param name="list" select="affliction"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--	
====================================
====================================
	TEMPLATE - ANIMAL TRICKS
====================================
====================================-->
	<xsl:template match="animal_tricks">
		<xsl:if test="count(animal_trick) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'animal_tricks'" />
				<xsl:with-param name="title" select="'Animal Tricks'" />
				<xsl:with-param name="list" select="animal_trick"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
			<!--
====================================
====================================
	TEMPLATE - Intelligent Magic Item
====================================
====================================-->
	<xsl:template match="intelligent_items">
		<xsl:if test="count(intelligent_item) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'intelligent_items'"/>
				<xsl:with-param name="title" select="'Intelligent Item'"/>
				<xsl:with-param name="list" select="intelligent_item"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

<!--> ECLIPSE Addons -->
	<!--
====================================
====================================
	TEMPLATE - Disadvantages
====================================
====================================-->
	<xsl:template match="disadvantages">
		<xsl:if test="count(disadvantage) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'disadvantages'"/>
				<xsl:with-param name="title" select="'DISADVANTAGES'"/>
				<xsl:with-param name="list" select="disadvantage"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Martial Arts
====================================
====================================-->
	<xsl:template match="martial_arts">
		<xsl:if test="count(martial_art) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'martial_arts'"/>
				<xsl:with-param name="title" select="'Martial Arts'"/>
				<xsl:with-param name="list" select="martial_art"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Mystic Artist
====================================
====================================-->
	<xsl:template match="mystic_artists">
		<xsl:if test="count(mystic_artist) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'mystic_artists'"/>
				<xsl:with-param name="title" select="'Mystic Artist Abilities'"/>
				<xsl:with-param name="list" select="mystic_artist"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Witchcraft
====================================
====================================-->
	<xsl:template match="witchcrafts">
		<xsl:if test="count(witchcraft) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'witchcrafts'"/>
				<xsl:with-param name="title" select="'Witchcraft Abilities'"/>
				<xsl:with-param name="list" select="witchcraft"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
		<!--
====================================
====================================
	TEMPLATE - Channeling
====================================
====================================-->
	<xsl:template match="channelings">
		<xsl:if test="count(channeling) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'channelings'"/>
				<xsl:with-param name="title" select="'Channeling'"/>
				<xsl:with-param name="list" select="channeling"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Dominion
====================================
====================================-->
	<xsl:template match="dominions">
		<xsl:if test="count(dominion) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'dominions'"/>
				<xsl:with-param name="title" select="'Dominion'"/>
				<xsl:with-param name="list" select="dominion"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - The Path of the Dragon
====================================
====================================-->
	<xsl:template match="path_dragons">
		<xsl:if test="count(path_dragon) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'path_dragons'"/>
				<xsl:with-param name="title" select="'The Path of the Dragon'"/>
				<xsl:with-param name="list" select="path_dragon"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Recurring Bonuses
====================================
====================================-->
	<xsl:template match="charcreations">
		<xsl:if test="count(charcreation) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'charcreations'"/>
				<xsl:with-param name="title" select="'Recurring Bonuses'"/>
				<xsl:with-param name="list" select="charcreation"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Caster Level Outputs
====================================
====================================-->
	<xsl:template match="spellcasteroutputs">
		<xsl:if test="count(spellcasteroutput) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'spellcasteroutputs'"/>
				<xsl:with-param name="title" select="'Spell Caster Information'"/>
				<xsl:with-param name="list" select="spellcasteroutput"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--	
====================================
====================================
	TEMPLATE - Eclipse Abilities
====================================
====================================-->
	<xsl:template match="eclipse_abilities">
		<xsl:if test="count(eclipse_ability) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'eclipse_abilities'" />
				<xsl:with-param name="title" select="'Eclipse Abilities'" />
				<xsl:with-param name="list" select="eclipse_ability"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TALENTS
====================================
====================================-->
	<xsl:template match="talents">
		<xsl:if test="count(talent) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'talents'"/>
				<xsl:with-param name="title" select="'Talents'"/>
				<xsl:with-param name="list" select="talent"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Demon Cants
====================================
====================================-->
	<xsl:template match="demon_cants">
		<xsl:if test="count(demon_cant) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'demon_cants'" />
				<xsl:with-param name="title" select="'Demon Cants'" />
				<xsl:with-param name="list" select="demon_cant"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--	
====================================
====================================
	TEMPLATE - Mage Gnosis
====================================
====================================-->
	<xsl:template match="mage_gnosises">
		<xsl:if test="count(mage_gnosis) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'mage_gnosises'" />
				<xsl:with-param name="title" select="'Mage Gnosis'" />
				<xsl:with-param name="list" select="mage_gnosis"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Vampire Disciplines
====================================
====================================-->
	<xsl:template match="vampire_disciplines">
		<xsl:if test="count(vampire_discipline) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'vampire_disciplines'" />
				<xsl:with-param name="title" select="'Vampire Disciplines'" />
				<xsl:with-param name="list" select="vampire_discipline"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Werewolf Rites
====================================
====================================-->
	<xsl:template match="werewolf_rites">
		<xsl:if test="count(werewolf_rite) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'werewolf_rites'" />
				<xsl:with-param name="title" select="'Werewolf Rites'" />
				<xsl:with-param name="list" select="werewolf_rite"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	
	<!--
====================================
====================================
	TEMPLATE - Force Powers
====================================
====================================-->
	<xsl:template match="force_powers">
		<xsl:if test="count(force_power) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'force_powers'"/>
				<xsl:with-param name="title" select="'Force Powers'"/>
				<xsl:with-param name="list" select="force_power"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Force Techniques
====================================
====================================-->
	<xsl:template match="force_techniques">
		<xsl:if test="count(force_technique) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'force_techniques'"/>
				<xsl:with-param name="title" select="'Force Techniques'"/>
				<xsl:with-param name="list" select="force_technique"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
		<!--
====================================
====================================
	TEMPLATE - Force Secrets
====================================
====================================-->
	<xsl:template match="force_secrets">
		<xsl:if test="count(force_secret) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'force_secrets'"/>
				<xsl:with-param name="title" select="'Force Secrets'"/>
				<xsl:with-param name="list" select="force_secret"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

<!-- 4e Section -->

<!--
====================================
====================================
	TEMPLATE - CLASSFEATURE POWERS
====================================
====================================-->
	<xsl:template match="powers_classfeatures">
		<xsl:if test="count(power_classfeature) &gt; 0">
			<xsl:call-template name="power.list">
				<xsl:with-param name="attribute" select="'powers_classfeatures'" />
				<xsl:with-param name="title" select="'CLASSFEATURE POWERS'" />
				<xsl:with-param name="list" select="power_classfeature"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="action_type.tag" select="'action_type'"/>
				<xsl:with-param name="power_type.tag" select="'power_type'"/>
				<xsl:with-param name="power_use.tag" select="'power_use'"/>
				<xsl:with-param name="attack.tag" select="'attack'"/>
				<xsl:with-param name="trigger.tag" select="'trigger'"/>
				<xsl:with-param name="special.tag" select="'special'"/>
				<xsl:with-param name="target.tag" select="'target'"/>
				<xsl:with-param name="hit.tag" select="'hit'"/>
				<xsl:with-param name="miss.tag" select="'miss'"/>
				<xsl:with-param name="effect.tag" select="'effect'"/>		
				<xsl:with-param name="sustain.tag" select="'sustain'"/>		
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
<!--
====================================
====================================
	TEMPLATE - FEATPOWERS POWERS
====================================
====================================-->
	<xsl:template match="powers_featpowers">
		<xsl:if test="count(power_featpower) &gt; 0">
			<xsl:call-template name="power.list">
				<xsl:with-param name="attribute" select="'powers_featpowers'" />
				<xsl:with-param name="title" select="'FEAT POWERS'" />
				<xsl:with-param name="list" select="power_featpower"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="action_type.tag" select="'action_type'"/>
				<xsl:with-param name="power_type.tag" select="'power_type'"/>
				<xsl:with-param name="power_use.tag" select="'power_use'"/>
				<xsl:with-param name="attack.tag" select="'attack'"/>
				<xsl:with-param name="trigger.tag" select="'trigger'"/>
				<xsl:with-param name="special.tag" select="'special'"/>
				<xsl:with-param name="target.tag" select="'target'"/>
				<xsl:with-param name="hit.tag" select="'hit'"/>
				<xsl:with-param name="miss.tag" select="'miss'"/>
				<xsl:with-param name="effect.tag" select="'effect'"/>		
				<xsl:with-param name="sustain.tag" select="'sustain'"/>		
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - AT-WILL POWERS
====================================
====================================-->
	<xsl:template match="powers_atwills">
		<xsl:if test="count(powers_atwill) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'powers_atwills'" />
				<xsl:with-param name="title" select="'At-will Powers'" />
				<xsl:with-param name="list" select="powers_atwill"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - ENCOUNTER POWERS
====================================
====================================-->
	<xsl:template match="powers_encounters">
		<xsl:if test="count(powers_encounter) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'powers_encounters'" />
				<xsl:with-param name="title" select="'Encounter Powers'" />
				<xsl:with-param name="list" select="powers_encounter"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - DAILY POWERS
====================================
====================================-->
	<xsl:template match="powers_dailies">
		<xsl:if test="count(powers_daily) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'powers_dailies'" />
				<xsl:with-param name="title" select="'Daily Powers'" />
				<xsl:with-param name="list" select="powers_daily"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - UTILITY POWERS
====================================
====================================-->
	<xsl:template match="powers_utilities">
		<xsl:if test="count(powers_utility) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'powers_utilities'" />
				<xsl:with-param name="title" select="'Utility Powers'" />
				<xsl:with-param name="list" select="powers_utility"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - TRAITS
====================================
====================================-->
	<xsl:template match="traits">
		<xsl:if test="count(trait) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'traits'" />
				<xsl:with-param name="title" select="'Traits'" />
				<xsl:with-param name="list" select="trait"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - SALIENT DIVINE ABILITIES
====================================
====================================-->
	<xsl:template match="salient_divine_abilities">
		<xsl:if test="count(salient_divine_ability) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'salient_divine_abilities'" />
				<xsl:with-param name="title" select="'Salient Divine Abilities'" />
				<xsl:with-param name="list" select="salient_divine_ability"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - FEATS
====================================
====================================-->
	<xsl:template match="feats">
		<xsl:if test="count(feat[hidden != 'T' and name != '']) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'feats'" />
				<xsl:with-param name="title" select="'Feats'" />
				<xsl:with-param name="list" select="feat[hidden != 'T' and name != '']"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
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
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body"  font-size="12pt">
					<xsl:apply-templates select="spells_innate/racial_innate"/>
					<xsl:apply-templates select="spells_innate/class_innate"/>
					<xsl:apply-templates select="known_spells"/>
					<xsl:apply-templates select="memorized_spells"/>
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
				<fo:table table-layout="fixed">
					<xsl:call-template name="spells.known.header.row">
						<xsl:with-param name="columnOne" select="''"/>
						<xsl:with-param name="title" select="'Innate Racial Spells'"/>
					</xsl:call-template>
					<fo:table-body>
						<xsl:apply-templates select="spell" mode="details">
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
				<fo:table table-layout="fixed" space-before="5mm">
					<xsl:call-template name="spells.known.header.row">
						<xsl:with-param name="columnOne" select="''"/>
						<xsl:with-param name="title" select="concat(@name, ' Spell-like Abilities')"/>
					</xsl:call-template>
					<fo:table-body>
						<xsl:apply-templates select="spell" mode="details">
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
			<fo:table table-layout="fixed">
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
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="9">
							<xsl:apply-templates select="." mode="spell.level.table"/>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2mm">
						<fo:table-cell/>
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
		<fo:table table-layout="fixed" border-collapse="collapse">
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
			<fo:table-cell/>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="12pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt"> LEVEL</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
					</xsl:call-template>
					<fo:block space-before="2pt" space-after="1pt" font-size="12pt">
						<xsl:value-of select="@number"/>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell/>
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
			<fo:table-cell/>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="12pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt"> KNOWN</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.known'"/>
					</xsl:call-template>
					<fo:block font-size="12pt" space-before="2pt" space-after="1pt">
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
			<fo:table-cell/>
		</fo:table-row>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS (SPELL.LEVEL.CAST)
====================================
====================================-->
	<xsl:template match="class" mode="spell.level.cast">
		<fo:table-row padding-bottom="2mm">
			<fo:table-cell/>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
				<fo:block font-size="12pt" font-weight="bold" space-start="2pt" space-before="3pt" space-after="1pt">PER DAY</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell>
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.known.perday'"/>
					</xsl:call-template>
					<fo:block font-size="12pt" space-before="2pt" space-after="1pt">
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
			<fo:table-cell/>
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
			<fo:table-cell/>
			<fo:table-cell>	
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/>
				</xsl:call-template>
		<!-->	xsl:use-attribute-sets="spelllist.known.header">-->
				<fo:block font-size="12pt" font-weight="bold" space-start="2pt" space-before="2pt" space-after="1pt">Concentration</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'spelllist.known.header.centre'"/> 
				</xsl:call-template>
		<!--	 xsl:use-attribute-sets="spelllist.known.header centre">	-->
				<fo:block space-before="2pt" space-after="1pt" font-size="12pt"><xsl:value-of select="@concentration"/></fo:block>
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
				<fo:table-cell number-columns-spanned="9" padding-top="1pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.header'"/>
					</xsl:call-template>
					<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="@cast"/>
									</xsl:call-template>
					<fo:block font-size="12pt">
						LEVEL <xsl:value-of select="@number"/> / Per Day:<xsl:value-of select="@cast"/> / Caster Level:<xsl:value-of select="spell/casterlevel"/>
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
				<fo:table-cell/>
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
		<fo:table-column>
			<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth - 134" />mm</xsl:attribute>
		</fo:table-column>
		<!-- name ^ -->
		<fo:table-column column-width="38mm"/> <!-- Name -->
		<!-- SR, DC, Save = 18+15+9  42--> <!-- SR/DC/Save -->
<!-->		<fo:table-column column-width="9mm"/>	-->
		<!-- saving throw -->
		<fo:table-column column-width="18mm"/>	<!-- SR/DC/Save -->
		<!-- time -->
		<fo:table-column column-width="34mm"/>	<!-- Time -->
		<!-- duration -->
		<fo:table-column column-width="18mm"/>	<!-- Duration -->
		<!-- range -->
		<fo:table-column column-width="18mm"/>	<!-- Range -->
		<!-- comp -->
		<fo:table-column column-width="13mm"/>	<!-- Comps -->
		<!-- SR 
		<fo:table-column column-width="15mm"/>	-->
		<!-- school -->
		<fo:table-column column-width="6mm"/>	<!-- Source -->
		<!-- source -->

		<!-- Titles Columns Goes Here -->
		<fo:table-header>
			<fo:table-row>
				<fo:table-cell number-columns-spanned="9" padding-top="1pt">
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
				<fo:table-cell number-columns-spanned="9" padding-top="1pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spelllist.footer'"/>
					</xsl:call-template>
					<fo:block font-size="12pt">* =Domain/Speciality Spell
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-footer>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - KNOWN SPELL HEADER COLUMN TITLES
====================================
====================================-->
	<xsl:template name="spells.header.column.titles">
		<xsl:param name="columnOne" select="''"/>
		<fo:table-row keep-with-next.within-column="always">
			<xsl:call-template name="attrib">
				<xsl:with-param name="attribute" select="'spelllist.levelheader'"/>
			</xsl:call-template>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">
					<xsl:value-of select="$columnOne"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Name</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">School</fo:block>
			</fo:table-cell>
<!-->			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Saving Throw</fo:block>
			</fo:table-cell> -->
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Time</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Duration</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Range</fo:block>
			</fo:table-cell>
<!-->			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Target</fo:block>		Caster Level	
			</fo:table-cell>	<-->
<!-->			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">Spell Resistance</fo:block>
			</fo:table-cell>	-->
<!-->			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt" font-weight="bold">School</fo:block>
			</fo:table-cell>	<-->
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="right" font-size="12pt" font-weight="bold">Source</fo:block>		<!--> Source / Now target is taking both blocks-->
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
		<fo:table-row keep-with-next.within-column="always">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/></xsl:call-template>
				
			<xsl:choose>
				<xsl:when test="$columnOne = 'Times'">
					<xsl:choose>
						<xsl:when test="times_memorized &gt;= 0">
							<fo:table-cell padding-top="0pt">
								<fo:block text-align="start" font-size="12pt" font-family="ZapfDingbats">
									<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="times_memorized"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</xsl:when>
						<xsl:otherwise>
							<fo:table-cell padding-top="1pt" text-align="start">
								<fo:block text-align="start" font-size="12pt">At Will</fo:block>
							</fo:table-cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$columnOne = 'Boxes'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="12pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="5"/>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:when test="$columnOne = 'PowerPoints'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="12pt">
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
				<fo:block text-align="start" font-size="12pt" font-weight="bold">
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
						<fo:block text-align="start" font-size="12pt" font-weight="bold">
<!-->							<fo:inline font-style="italic">School: </fo:inline>	-->
							<xsl:value-of select="school/fullschool"/>
						</fo:block>
			</fo:table-cell>
<!-->			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt">
				</fo:block>
			</fo:table-cell>	-->
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt">
					<xsl:value-of select="castingtime"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt">
					<xsl:value-of select="duration"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="12pt">
					<xsl:value-of select="range"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="right" font-size="12pt" number-columns-spanned="1">
					<xsl:value-of select="source/sourceshort"/>
					<xsl:text>:</xsl:text>
					<xsl:value-of select="source/sourcepage"/>
				</fo:block>
			</fo:table-cell>	
		</fo:table-row>
<!-- Second Row -->
<!-->		<fo:table-row>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/></xsl:call-template>	-->
<!-->			<fo:table-cell padding-top="1pt" number-columns-spanned="6">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-style="italic">Effect: </fo:inline>
					<fo:block text-align="justify" text-indent="5pt">
						<xsl:call-template name="paragraghlist">
							<xsl:with-param name="tag" select="'effect'"/>
						</xsl:call-template>
					</fo:block>
				</fo:block>
			</fo:table-cell>	-->
<!-->			<fo:table-cell padding-top="1pt" number-columns-spanned="2">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-style="italic">School: </fo:inline>
					<xsl:value-of select="school/fullschool"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-style="italic">SR: </fo:inline>
					<xsl:value-of select="spell_resistance"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="3">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-style="italic">Target: </fo:inline>
					<xsl:value-of select="target"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="1">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-style="italic">Caster Level: </fo:inline>
					<xsl:value-of select="casterlevel"/>
				</fo:block>
			</fo:table-cell>	-->
			<!-- Placeholder for future concentration for spells -->
<!--			<fo:table-cell padding-top="1pt" number-columns-spanned="2">
				<fo:block text-align="start" font-size="12pt">
					<xsl:if test="concentration != ''">
						<fo:inline font-style="italic">Concentration: </fo:inline>
						<xsl:value-of select="concentration"/>
					</xsl:if>
				</fo:block>
			</fo:table-cell>	
		</fo:table-row>		-->
<!-- Third Row = For Spell Descriptions -->
		<fo:table-row>
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat('spelllist.', $shade)"/></xsl:call-template>
			<fo:table-cell padding-top="1pt" number-columns-spanned="7">
				<fo:block text-align="start" font-size="12pt">
					<fo:inline font-weight="bold">[<xsl:value-of select="components"/>]</fo:inline>
					<fo:inline> </fo:inline>
					<fo:inline font-weight="bold"> TARGET: </fo:inline><xsl:value-of select="target"/>
					<fo:inline>; </fo:inline>
					<fo:inline font-style="italic" font-weight="bold">EFFECT: </fo:inline>

					<xsl:value-of select="effect"/>
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
		<!-->			<xsl:if test="concentration != ''">	-->
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
	TEMPLATE - MEMORIZED SPELLS
====================================
====================================-->
	<xsl:template match="memorized_spells">
		<xsl:if test="count(.//spell) &gt; 0">
	<!-->		<fo:block break-before="page">	-->
			<fo:block>
				<xsl:apply-templates mode="spells.memorized"/>
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
		<fo:table table-layout="fixed">
			<fo:table-column>
				<xsl:attribute name="column-width"><xsl:value-of select="$pagePrintableWidth" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row>
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
			<fo:table table-layout="fixed" space-after="5mm">
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
			<fo:table table-layout="fixed">
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
			<fo:table table-layout="fixed" space-before="4mm">
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
					<fo:table-row>
						<fo:table-cell padding-top="1pt" number-columns-spanned="5">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'spells.memorized.header'"/>
							</xsl:call-template>
							<fo:block font-size="12pt">
								Spellbook: <xsl:value-of select="@name"/>
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
			<fo:table-row>
				<fo:table-cell padding-top="1pt" number-columns-spanned="5">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spells.memorized.header'"/>
					</xsl:call-template>
					<fo:block font-size="12pt">
						<xsl:value-of select="@spelllistclass"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<xsl:apply-templates select="level[@number &lt; 5]" mode="spells.memorized"/>
			</fo:table-row>
			<fo:table-row>
				<xsl:apply-templates select="level[@number &gt;= 5]" mode="spells.memorized"/>
			</fo:table-row>
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
			<fo:block font-size="12pt">
				<xsl:if test="count(.//spell) &gt; 0">
					<fo:table table-layout="fixed">
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.20 * $pagePrintableWidth div 5" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-column>
							<xsl:attribute name="column-width"><xsl:value-of select="0.80 * $pagePrintableWidth div 5" />mm</xsl:attribute>
						</fo:table-column>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell padding-top="1pt" number-columns-spanned="2">
									<xsl:call-template name="attrib">
										<xsl:with-param name="attribute" select="'spells.memorized.level'"/>
									</xsl:call-template>
									<fo:block font-size="12pt">
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
		<fo:table-row>
			<xsl:choose>
				<xsl:when test="times_memorized &gt;= 0">
					<fo:table-cell padding-top="0pt" text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'spells.memorized'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" font-family="ZapfDingbats">
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
						<fo:block font-size="12pt">At Will</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-cell>
				<fo:block font-size="12pt">
					<xsl:call-template name="attrib">
						<xsl:with-param name="attribute" select="'spells.memorized'"/>
					</xsl:call-template>
					<xsl:value-of select="bonusspell"/>
					<xsl:value-of select="name"/> (DC:<xsl:value-of select="dc"/>)
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>


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
			<fo:table-cell padding-top="1pt" height="9pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio'"/>
				</xsl:call-template>
				<fo:block font-size="12pt">
					<xsl:value-of select="$value"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell padding-top="0.5pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio.title'"/>
				</xsl:call-template>
				<fo:block font-size="12pt"><xsl:value-of select="$title"/></fo:block>
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
				<fo:flow flow-name="body" font-size="12pt">
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
									<fo:table-cell>
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio'"/>
										</xsl:call-template>
										<fo:block font-size="12pt">
											<xsl:value-of select="race"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell number-rows-spanned="36"/>
									<xsl:if test="string-length(portrait) &gt; 0">
										<fo:table-cell display-align="before" number-rows-spanned="36">
											<xsl:call-template name="attrib">
												<xsl:with-param name="attribute" select="'picture'"/>
											</xsl:call-template>
											<fo:block start-indent="1mm" height="100mm">
												<xsl:variable name="portrait_file" select="portrait/portrait"/>
												<fo:external-graphic src="file:{$portrait_file}" width="92mm" scaling="uniform"/>
											</fo:block>
										</fo:table-cell>
										
									</xsl:if>
									
								</fo:table-row>





								<fo:table-row>
									<fo:table-cell padding-top="1pt">
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio.title'"/>
										</xsl:call-template>
										<fo:block font-size="12pt">RACE</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:call-template name="bio.entry">
									<xsl:with-param name="title" select="'AGE'"/>
									<xsl:with-param name="value" select="age"/>
								</xsl:call-template>
										<fo:table-row>
			<fo:table-cell padding-top="1pt" height="9pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio'"/>
				</xsl:call-template>
				<fo:block font-size="12pt">
					Vision Test:
					<xsl:value-of select="vision/all"/>
					<xsl:if test="vision/all = ''">Normal</xsl:if>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell padding-top="0.5pt">
				<xsl:call-template name="attrib">
					<xsl:with-param name="attribute" select="'bio.title'"/>
				</xsl:call-template>
				<fo:block font-size="12pt"></fo:block>
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
								<!-- Attempt to change Style -->
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="12pt" padding-top="3pt">
							<xsl:value-of select="vision/all"/>
							<xsl:if test="vision/all = ''">Normal</xsl:if>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>


							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="14pt" font-weight="bold" space-before="5mm" span="all">
						Description:
					</fo:block>
					<fo:block font-size="12pt" text-indent="5mm" span="all">
						<xsl:call-template name="paragraghlist">
							<xsl:with-param name="tag" select="'description'"/>
						</xsl:call-template>
					</fo:block>
					<fo:block font-size="14pt" font-weight="bold" span="all">
						Biography:
					</fo:block>
					<fo:block font-size="12pt" text-indent="5mm" span="all">
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
				<fo:flow flow-name="body" font-size="12pt">
					<fo:block font-size="14pt" font-weight="bold" space-after.optimum="2mm" break-before="page" span="all">
						Notes:
					</fo:block>
					<xsl:for-each select="note">
						<fo:block font-size="12pt" space-after.optimum="2mm" space-before.optimum="5mm">
							<xsl:value-of select="name"/>:
						</fo:block>
						<fo:block font-size="12pt" text-indent="5mm">
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
	<!-- End Character -->
</xsl:stylesheet>
