<!-- $Id$ -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:Psionics="my:Psionics"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs Psionics">

	<xsl:import href="killshot_common.xsl"/>
	<xsl:import href="leadership.xsl"/>

	<xsl:output indent="yes"/>

	<xsl:variable name="vAttribs_tree">
		<myAttribs:myAttribs>
			<xsl:copy-of select="$vAttribs/*"/>
			<xsl:copy-of select="document('leadership.xsl')/*/myAttribs:*/*"/>
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
						<fo:block font-size="5pt">Character: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/name"/></fo:inline></fo:block>
						<fo:block font-size="5pt">Player: <fo:inline font-weight="bold"><xsl:value-of select="/character/basics/playername"/></fo:inline></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center" wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block text-align="center" font-size="5pt">PCGen Character Template by Frugal, based on work by ROG, Arcady, Barak, Dimrill, Dekker &amp; Andrew Maitland (LegacyKing).</fo:block>
						<fo:block text-align="center" font-size="5pt">Created using <fo:basic-link external-destination="http://pcgen.org/" show-destination="true" color="blue" text-decoration="underline">PCGen</fo:basic-link> <xsl:value-of select="/character/export/version"/> on <xsl:value-of select="/character/export/date"/><xsl:text> at </xsl:text><xsl:value-of select="/character/export/time"/></fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="transparent" padding-top="2pt">
						<fo:block font-size="5pt">
						Level:<xsl:value-of select="/character/basics/classes/levels_total"/> (CR:<xsl:value-of select="/character/basics/cr"/>)</fo:block>
						<fo:block font-size="5pt">Page <fo:page-number/>
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
				<xsl:call-template name="page.footer"/>
				<!--	CHARACTER BODY STARTS HERE !!!	-->
				<fo:flow flow-name="body"  font-size="8pt">
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
									<fo:table-cell number-rows-spanned="2" border-width="1pt" border-color="red">
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
										<xsl:call-template name="encumberance"/>
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
				<xsl:call-template name="page.footer"/>
				<fo:flow flow-name="body"  font-size="8pt">
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
						<xsl:apply-templates select="languages"/>


						<xsl:apply-templates select="killshot_traits"/>
						<xsl:apply-templates select="killshot_focuses"/>
						<xsl:apply-templates select="killshot_options"/>
						<xsl:apply-templates select="killshot_reactions"/>
						<xsl:apply-templates select="killshot_skills"/>
						





						<xsl:apply-templates select="templates"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>


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
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row>
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
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3" font-weight="bold">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="playername"/>
							
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="deity/name"/>	
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt" number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="region"/>	
						</fo:block>
					</fo:table-cell>

					<fo:table-cell/>
					<fo:table-cell padding-top="2.5pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
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
						<fo:block font-size="6pt" padding-top="1pt">Character Name</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Player Name</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Deity</fo:block>		
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="1">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Region</fo:block>	
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">Alignment</fo:block>	
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
				</fo:table-row>

<!-- Second Row -->
				<fo:table-row>
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
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
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
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="size/long"/>
							<xsl:if test="face/face != ''"> / <xsl:value-of select="face/face"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="height/total"/> / 
							<xsl:value-of select="weight/weight_unit"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>	<!-- SPACE -->
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
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
						<fo:block font-size="6pt" padding-top="1pt">CLASS</fo:block>
					</fo:table-cell>
					
				
					<fo:table-cell/>
					<fo:table-cell number-columns-spanned="3">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">RACE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">SIZE / FACE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">HEIGHT / WEIGHT</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">VISION</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>

<!--	Third Row  -->
				<fo:table-row>
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
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="experience/current"/> / <xsl:value-of select="experience/next_level"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="age"/>
							<xsl:if test="birthday != ''"> (<xsl:value-of select="birthday"/>)</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="gender/long"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="eyes/color"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
							<xsl:value-of select="hair/color"/>
								<xsl:if test="hair/color != '' and hair/length !=''">, <xsl:value-of select="hair/length"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">	
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
						<fo:block font-size="6pt" padding-top="1pt">
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
						<fo:block font-size="6pt" padding-top="1pt">EXP  /   NEXT LEVEL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">AGE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">GENDER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">EYES</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt">HAIR</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio.title'"/>
						</xsl:call-template>
						<fo:block font-size="6pt" padding-top="1pt"></fo:block>	
					</fo:table-cell>
					<fo:table-cell/>
				</fo:table-row>	
			</fo:table-body>
		</fo:table>
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
<!-- Temp Score and Mod-->
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
					<fo:table-row height="2pt"/>
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
						<fo:block font-size="10pt" font-weight="bold" text-align="center">
							<xsl:value-of select="header"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" text-align="end">
							<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" text-align="center"><xsl:value-of select="check_type"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="9pt">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'checklist'"/>
						</xsl:call-template>
						<fo:block font-size="9pt" font-family="ZapfDingbats">
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
						<fo:block font-size="5pt" font-weight="bold">
						<xsl:if test="name != ''"> <xsl:value-of select="name"/>:</xsl:if>
							<fo:inline font-size="5pt" font-weight="normal"><xsl:value-of select="description"/><xsl:if test="source != ''"> [<xsl:value-of select="source"/>]</xsl:if></fo:inline>
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
							<fo:block font-size="9pt">EQUIPMENT</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'equipment.title'"/>
						</xsl:call-template>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">ITEM</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">LOCATION</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">QTY</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt"  number-columns-spanned="2">
							<fo:block font-size="7pt">WT / COST</fo:block>
						</fo:table-cell>
<!-->						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt"></fo:block>
						</fo:table-cell>	-->
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'equipment.title'"/>
						</xsl:call-template>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">TOTAL WEIGHT CARRIED/VALUE</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">
								<xsl:value-of select="total/weight"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" number-columns-spanned="2">
							<fo:block font-size="7pt">
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
								<fo:block space-before.optimum="1pt" font-size="8pt">
									<xsl:if test="contains(type, 'MAGIC') or contains(type, 'PSIONIC')">
										<xsl:attribute name="font-weight">bold</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="name"/>
								</fo:block>
			<!-->					<fo:block space-before.optimum="1pt" font-size="5pt">
									<xsl:value-of select="contents"/>
								</fo:block>	-->
			<!-->					<fo:block space-before.optimum="1pt" font-size="5pt">
									<xsl:value-of select="special_properties"/>
									<xsl:value-of select="quality"/>
								</fo:block>	-->
								<fo:block space-before.optimum="1pt" font-size="5pt">
									<xsl:value-of select="note"/>
								</fo:block>
								<!-- Display the number of charges left if any -->
								<xsl:if test="charges &gt; 0">
									<fo:block font-size="7pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="charges"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<!-- Display the ammunition as a series of checkboxes -->
						<!-->		<xsl:if test="contains(type, 'POTION') and quantity &gt; 1">
									<fo:block font-size="7pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
Potion is Consumable											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>	-->
								<xsl:if test="contains(type, 'AMMUNITION') and quantity &gt; 1">
									<fo:block font-size="7pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
								<xsl:if test="contains(type, 'CONSUMABLE') and quantity &gt; 1">
									<fo:block font-size="7pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop">
											<xsl:with-param name="count" select="checkbox"/>
										</xsl:call-template>
									</fo:block>
								</xsl:if>
							</fo:table-cell>
							<fo:table-cell text-align="center">
								<fo:block space-before.optimum="1pt" font-size="7pt">
									<xsl:value-of select="location"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
									<xsl:value-of select="quantity"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell  number-columns-spanned="2">
								<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
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
								<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
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
								<fo:block space-before.optimum="1pt" font-size="5pt">
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
			<fo:block font-size="7pt" text-align="end"><xsl:value-of select="$title"/></fo:block>
		</fo:table-cell>
		<fo:table-cell padding-top="1pt" padding-left="1mm">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'weight.lightline'"/></xsl:call-template>
			<fo:block font-size="7pt"><xsl:value-of select="$value"/></fo:block>
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
						<fo:block font-size="9pt">WEIGHT ALLOWANCE</fo:block>
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


		<xsl:if test="count (misc/funds/fund|equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]) or (misc/gold) &gt; 0">	
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
							<fo:block font-size="9pt">MONEY</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'money.title'"/>
							</xsl:call-template>
							<fo:block font-size="7pt" text-align="end">
								<xsl:variable name="TotalValue">
									<xsl:call-template name="Total">
										<xsl:with-param name="Items" select="equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]"/>
										<xsl:with-param name="RunningTotal" select="0"/>
									</xsl:call-template>
								</xsl:variable>
								Total   = <xsl:value-of select="format-number($TotalValue, '##,##0.#')"/> gp
								<xsl:if test="misc/gold &gt; 0">
								[Unspent Funds = <xsl:value-of select="misc/gold"/> gp]
								</xsl:if>
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
									 <fo:inline font-size="6pt">[<xsl:value-of select="location"/>]</fo:inline>
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
									 <fo:inline font-size="6pt">[<xsl:value-of select="location"/>]</fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
					<xsl:variable name="gem_count" select="count( equipment/item[contains(type, 'GEM')] )"/>

					<!-- misc gold -->
					<xsl:for-each select="misc/gold">
						<fo:table-row keep-with-next.within-column="always">
		
							<fo:table-cell>
								<fo:block font-size="7pt">
									<xsl:call-template name="paragraghlist">
										<xsl:with-param name="tag" select="'gold'"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>

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
								<fo:block font-size="7pt">
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
							<fo:block font-size="9pt">MAGIC</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.lightline'"/>
							</xsl:call-template>
							<fo:block font-size="7pt">
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
							<fo:block font-size="9pt">OTHER COMPANIONS</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'magic.lightline'"/>
							</xsl:call-template>
							<fo:block font-size="7pt">
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

<!-- Begin Killshot Specific Entries! -->

	<!--
====================================
====================================
	TEMPLATE - Focuses
====================================
====================================-->
	<xsl:template match="killshot_focuses">
		<xsl:if test="count(killshot_focus) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'ability_block'" />
				<xsl:with-param name="title" select="'Focuses'" />
				<xsl:with-param name="list" select="killshot_focus"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - killshot_Reactions
====================================
====================================-->
	<xsl:template match="killshot_reactions">
		<xsl:if test="count(killshot_reaction) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'ability_block'" />
				<xsl:with-param name="title" select="'Reactions'" />
				<xsl:with-param name="list" select="killshot_reaction"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - killshot_Skills
====================================
====================================-->
	<xsl:template match="killshot_skills">
		<xsl:if test="count(killshot_skill) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'ability_block'" />
				<xsl:with-param name="title" select="'Skills'" />
				<xsl:with-param name="list" select="killshot_skill"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - killshot_Options
====================================
====================================-->
	<xsl:template match="killshot_options">
		<xsl:if test="count(killshot_option) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'ability_block'" />
				<xsl:with-param name="title" select="'Options'" />
				<xsl:with-param name="list" select="killshot_option"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

		<!--
====================================
====================================
	TEMPLATE - killshot_Traits
====================================
====================================-->
	<xsl:template match="killshot_traits">
		<xsl:if test="count(killshot_trait) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'ability_block'" />
				<xsl:with-param name="title" select="'Traits'" />
				<xsl:with-param name="list" select="killshot_trait"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
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
				<xsl:with-param name="benefit.tag" select="'benefit'"/>
			</xsl:call-template>
		</xsl:if>
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
									<fo:table-cell>
										<xsl:call-template name="attrib">
											<xsl:with-param name="attribute" select="'bio'"/>
										</xsl:call-template>
										<fo:block font-size="9pt">
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
										<fo:block font-size="6pt">RACE</fo:block>
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
				<fo:block font-size="9pt">
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
								<!-- Attempt to change Style -->
				<fo:table-row>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'bio'"/>
						</xsl:call-template>
						<fo:block font-size="8pt" padding-top="3pt">
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
	<!-- End Character -->
</xsl:stylesheet>
