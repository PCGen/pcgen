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
	TEMPLATE - Special Abilities
====================================
====================================-->
	<xsl:template match="special_abilities">
		<xsl:if test="count(ability) &gt; 0">
			<xsl:call-template name="stripped.list">
				<xsl:with-param name="attribute" select="'special_abilities'" />
				<xsl:with-param name="title" select="'Special Abilities'" />
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
	TEMPLATE - Theme
====================================
====================================-->
	<xsl:template match="scr_themes">
		<xsl:if test="count(scr_theme) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'feats'" />
				<xsl:with-param name="title" select="'Theme'" />
				<xsl:with-param name="list" select="scr_theme"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - Racial Traits
====================================
====================================-->

	<xsl:template match="scr_racial_traits">
		<xsl:if test="count(scr_racial_trait) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'feats'" />
				<xsl:with-param name="title" select="'Racial Traits'" />
				<xsl:with-param name="list" select="scr_racial_trait"/>

				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - CLASS FEATURES
====================================
====================================-->

	<xsl:template match="scr_class_features">
		<xsl:if test="count(scr_class_feature) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'feats'" />
				<xsl:with-param name="title" select="'Class Features'" />
				<xsl:with-param name="list" select="scr_class_feature"/>

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
	<!--
====================================
====================================
	TEMPLATE - Words of Power
====================================
====================================-->
	<xsl:template match="words_of_powers">
		<xsl:if test="count(words_of_power) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Words of Power'" />
				<xsl:with-param name="list" select="words_of_power"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>




	<!--
====================================
====================================
	TEMPLATE - PRESTIGE AWARDS
====================================
====================================-->
	<xsl:template match="prestige_awards">
		<xsl:if test="count(prestige_award) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'prestige_awards'" />
				<xsl:with-param name="title" select="'Prestige Awards'" />
				<xsl:with-param name="list" select="prestige_award"/>
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
	TEMPLATE - Drawbacks
====================================
====================================-->
	<xsl:template match="drawbacks">
		<xsl:if test="count(drawback) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'drawbacks'" />
				<xsl:with-param name="title" select="'Drawbacks'" />
				<xsl:with-param name="list" select="drawback"/>
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
				<xsl:with-param name="benefit.tag" select="'benefit'"/>

			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PFS CHRONICLES
====================================
====================================-->
	<xsl:template match="pfs_chronicles">
		<xsl:if test="count(pfs_chronicle) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'pfs_chronicles'" />
				<xsl:with-param name="title" select="'PFS Chronicles'" />
				<xsl:with-param name="list" select="pfs_chronicle"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - PFS BOONS
====================================
====================================-->
	<xsl:template match="pfs_boons">
		<xsl:if test="count(pfs_boon) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'pfs_boons'" />
				<xsl:with-param name="title" select="'PFS Boons'" />
				<xsl:with-param name="list" select="pfs_boon"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
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
		<fo:table table-layout="fixed" width="100%" space-before="2mm" keep-together="always" border-collapse="collapse" >
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="'checklist.border'"/></xsl:call-template>
			<fo:table-column column-width="23mm"/>
			<fo:table-column column-width="63mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
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
											<xsl:message>Test</xsl:message>
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
											<xsl:message>Test END</xsl:message>
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
		<!-- END Racial Traits table -->
	</xsl:for-each>
	</xsl:template>


</xsl:stylesheet>
