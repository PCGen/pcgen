<?xml version="1.0" encoding="UTF-8"?>

<!-- Removed Domains and prohibited schools, as they do not exist in Modern gameMode - Frank Kliewe
	2006/02/28 implemented FREQ [ 1411525 ] [MSRD] display offhand penalties for ranged weap - Frank Kliewe
 -->



<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:str="http://xsltsl.sourceforge.net/string.html"
	xmlns:Psionics="my:Psionics"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs Psionics">

	<xsl:import href="../../fantasy/pdf/leadership.xsl"/>

	<xsl:output indent="yes"/>
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

	<xsl:variable name="vAttribs_tree">
		<myAttribs:myAttribs>
			<xsl:copy-of select="$vAttribs/*"/>
			<xsl:copy-of select="document('../../fantasy/pdf/leadership.xsl')/*/myAttribs:*/*"/>
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

	<!--
====================================
====================================
	TEMPLATE - PARAGRAGH LIST
====================================
====================================-->
	<xsl:template name="paragraghlist">
		<xsl:param name="tag"/>
		<xsl:if test="count(./*[name()=$tag]/*[name()='para']) = 0">
			<xsl:value-of select="./*[name()=$tag]"/>
		</xsl:if>
		<xsl:if test="count(./*[name()=$tag]/*[name()='para']) &gt; 0">
			<xsl:for-each select="./*[name()=$tag]/*[name()='para']">
				<xsl:if test="count(./*[name()='table']) &gt; 0">
					<xsl:call-template name="paragraghlist.table"/>
				</xsl:if>
				<xsl:if test="count(./*[name()='table']) = 0">
					<xsl:if test="string-length(.) &gt; 0">
						<fo:block text-indent="5pt">
							<xsl:value-of select="." />
						</fo:block> 
					</xsl:if>
					<xsl:if test="string-length(.) = 0">
						<fo:block text-indent="5pt">
							&#160;
						</fo:block> 
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - PARAGRAGH LIST
====================================
====================================-->
	<xsl:template name="paragraghlist.table">
		<xsl:for-each select="./table">
			<fo:table table-layout="fixed" inline-progression-dimension="auto">
				<xsl:for-each select="./table-column">
					<fo:table-column>
						<xsl:attribute name="column-width">
							<xsl:value-of select="@column-width" />
						</xsl:attribute> 
					</fo:table-column>
				</xsl:for-each>
				<xsl:for-each select="./table-body">
					<fo:table-body>
						<xsl:for-each select="./table-row">
							<fo:table-row>
								<xsl:for-each select="./table-cell">
									<fo:table-cell>
										<fo:block text-indent="5pt">
											<xsl:value-of select="." />
										</fo:block> 
									</fo:table-cell>
								</xsl:for-each>
							</fo:table-row>
						</xsl:for-each>
					</fo:table-body>
				</xsl:for-each>
			</fo:table>
		</xsl:for-each>
	</xsl:template>


	<myAttribs:myAttribs>
		<border border-width="0.5pt" border-style="solid" />
		<centre text-align="center" />
		<border.temp border-width="2pt" border-style="solid" border-color="lightgrey"><subattrib centre=""/></border.temp>

		<normal color="black" background-color="white" border-color="black"/>
		<light color="black" background-color="white" border-color="black"/>
		<medium color="black" background-color="lightgrey" border-color="black"/>
		<dark color="black" background-color="lightgrey" border-color="black"/>
		<very.dark color="black" background-color="lightgrey" border-color="black"/>
		<inverse color="white" background-color="black" border-color="black"/>

		<bio display-align="after" color="black" background-color="transparent" border-color="black"></bio>
		<bio.title border-top-width="0.5pt" border-top-style="solid"><subattrib normal=""/></bio.title>

		<picture><subattrib normal="" border=""/></picture>

		<stat.title><subattrib border="" centre="" inverse="" /></stat.title>
		<stat.score><subattrib border="" centre="" light="" /></stat.score>
		<stat.modifier><subattrib stat.score="" /></stat.modifier>
		<stat.base.score><subattrib border="" centre="" normal="" /></stat.base.score>
		<stat.base.modifier><subattrib stat.base.score="" /></stat.base.modifier>
		<stat.temp.score color="lightgrey"><subattrib  centre="" border.temp=""/></stat.temp.score>
		<stat.temp.modifier><subattrib stat.temp.score=""/></stat.temp.modifier>

		<hp.title><subattrib border="" centre="" inverse=""/></hp.title>
		<hp.total><subattrib border="" centre="" light=""/></hp.total>
		<hp.current><subattrib border="" centre="" normal=""/></hp.current>
		<hp.subdual><subattrib border="" centre="" normal=""/></hp.subdual>
		<damage.reduction><subattrib hp.current=""/></damage.reduction>
		<speed><subattrib border="" centre="" normal=""/></speed>

		<ac.title><subattrib border="" centre="" inverse=""/></ac.title>
		<ac.total><subattrib border="" centre="" light=""/></ac.total>
		<ac.flatfooted><subattrib border="" centre="" light=""/></ac.flatfooted>
		<ac.touch><subattrib border="" centre="" light=""/></ac.touch>
		<ac><subattrib border="" centre="" normal=""/></ac>
		<miss_chance><subattrib border="" centre="" normal=""/></miss_chance>
		<spell_failure><subattrib border="" centre="" light=""/></spell_failure>
		<ac_check><subattrib border="" centre="" light=""/></ac_check>
		<spell_resistance><subattrib border="" centre="" light=""/></spell_resistance>

		<initiative.title><subattrib border="" centre="" inverse=""/></initiative.title>
		<initiative.total><subattrib border="" centre="" light=""/></initiative.total>
		<initiative.general><subattrib border="" centre="" normal=""/></initiative.general>

		<bab.title><subattrib border="" centre="" inverse=""/></bab.title>
		<bab.total><subattrib border="" centre="" light=""/></bab.total>

		<skills.header><subattrib centre="" inverse=""/></skills.header>
		<skills.border><subattrib border="" inverse=""/></skills.border>
		<skills.darkline><subattrib medium="" /></skills.darkline>
		<skills.lightline><subattrib light="" /></skills.lightline>
		<skills.darkline.total><subattrib dark="" /></skills.darkline.total>
		<skills.lightline.total><subattrib medium="" /></skills.lightline.total>
		<skills.footer border-bottom-width="0.5pt" border-bottom-style="solid"></skills.footer>

		<saves.title><subattrib border="" centre="" inverse=""/></saves.title>
		<saves.total><subattrib border="" centre="" light=""/></saves.total>
		<saves><subattrib border="" centre="" normal=""/></saves>

		<tohit.title><subattrib border="" centre="" inverse=""/></tohit.title>
		<tohit.total><subattrib border="" centre="" light=""/></tohit.total>
		<tohit><subattrib border="" centre="" normal=""/></tohit>

		<weapon.title><subattrib border="" centre="" inverse=""/></weapon.title>
		<weapon.border><subattrib border="" inverse=""/></weapon.border>
		<weapon.hilight><subattrib border="" centre="" light=""/></weapon.hilight>
		<weapon><subattrib border="" centre="" normal=""/></weapon>

		<protection.title><subattrib border="" centre="" inverse=""/></protection.title>
		<protection.border padding="0.5pt"><subattrib border="" inverse=""/></protection.border>
		<protection.darkline><subattrib  centre="" medium="" /></protection.darkline>
		<protection.lightline><subattrib  centre="" light="" /></protection.lightline>

		<rage.title><subattrib  centre="" inverse=""/></rage.title>
		<rage.border padding="0.5pt"><subattrib border="" inverse=""/></rage.border>
		<rage><subattrib normal=""/></rage>

		<checklist.title><subattrib  centre="" inverse=""/></checklist.title>
		<checklist.border padding="0.5pt"><subattrib border="" inverse=""/></checklist.border>
		<checklist><subattrib normal=""/></checklist>

		<wildshape.title><subattrib centre="" inverse=""/></wildshape.title>
		<wildshape.border padding="0.5pt"><subattrib border="" inverse=""/></wildshape.border>
		<wildshape><subattrib normal=""/></wildshape>

		<bard.title><subattrib centre="" inverse=""/></bard.title>
		<bard.border padding="0.5pt"><subattrib border="" inverse=""/></bard.border>
		<bard><subattrib  normal=""/></bard>

		<psionics.title><subattrib  centre="" inverse=""/></psionics.title>
		<psionics.border padding="0.5pt"><subattrib border="" inverse=""/></psionics.border>
		<psionics><subattrib border="" centre="" normal=""/></psionics>

		<turning.title><subattrib centre="" inverse=""/></turning.title>
		<turning.border padding="0.5pt"><subattrib border="" inverse=""/></turning.border>
		<turning><subattrib  centre="" normal=""/></turning>
		<turning.lightline><subattrib centre="" light=""/></turning.lightline>
		<turning.darkline><subattrib centre="" medium=""/></turning.darkline>

		<stunningfist.title><subattrib centre="" inverse=""/></stunningfist.title>
		<stunningfist.border padding="0.5pt"><subattrib border="" inverse=""/></stunningfist.border>
		<stunningfist><subattrib normal=""/></stunningfist>

		<wholeness.title><subattrib  centre="" inverse=""/></wholeness.title>
		<wholeness.border padding="0.5pt"><subattrib border="" inverse=""/></wholeness.border>
		<wholeness><subattrib  normal=""/></wholeness>

		<layonhands.title><subattrib centre="" inverse=""/></layonhands.title>
		<layonhands.border padding="0.5pt"><subattrib border="" inverse=""/></layonhands.border>
		<layonhands><subattrib  normal=""/></layonhands>

		<domains.title><subattrib  centre="" inverse=""/></domains.title>
		<domains.border padding="0.5pt"><subattrib border="" inverse=""/></domains.border>
		<domains.lightline><subattrib  light=""/></domains.lightline>
		<domains.darkline><subattrib  medium=""/></domains.darkline>

		<proficiencies.title><subattrib centre="" inverse=""/></proficiencies.title>
		<proficiencies.border padding="0.5pt"><subattrib border="" inverse=""/></proficiencies.border>
		<proficiencies><subattrib centre="" normal=""/></proficiencies>

		<prohibited.title><subattrib centre="" inverse=""/></prohibited.title>
		<prohibited.border padding="0.5pt"><subattrib border="" inverse=""/></prohibited.border>
		<prohibited><subattrib centre="" normal=""/></prohibited>

		<languages.title><subattrib centre="" inverse=""/></languages.title>
		<languages.border padding="0.5pt"><subattrib border="" inverse=""/></languages.border>
		<languages><subattrib  centre="" normal=""/></languages>

		<templates.title><subattrib centre="" inverse=""/></templates.title>
		<templates.border padding="0.5pt"><subattrib border="" inverse=""/></templates.border>
		<templates.lightline><subattrib light=""/></templates.lightline>
		<templates.darkline><subattrib medium=""/></templates.darkline>

		<companions.title><subattrib border="" centre="" inverse=""/></companions.title>
		<companions><subattrib border="" centre="" normal=""/></companions>

		<equipment.title><subattrib centre="" inverse=""/></equipment.title>
		<equipment.border padding="0.5pt"><subattrib border="" inverse=""/></equipment.border>
		<equipment.lightline><subattrib light=""/></equipment.lightline>
		<equipment.darkline><subattrib medium=""/></equipment.darkline>

		<weight.title><subattrib centre="" inverse=""/></weight.title>
		<weight.border padding="0.5pt"><subattrib border="" inverse=""/></weight.border>
		<weight.lightline><subattrib light=""/></weight.lightline>
		<weight.darkline><subattrib  medium=""/></weight.darkline>

		<money.title><subattrib  centre="" inverse=""/></money.title>
		<money.border padding="0.5pt"><subattrib border="" inverse=""/></money.border>
		<money.lightline><subattrib light=""/></money.lightline>
		<money.darkline><subattrib medium=""/></money.darkline>

		<magic.title><subattrib centre="" inverse=""/></magic.title>
		<magic.border padding="0.5pt"><subattrib border="" inverse=""/></magic.border>
		<magic.lightline><subattrib light=""/></magic.lightline>
		<magic.darkline><subattrib medium=""/></magic.darkline>

		<special_abilities.title><subattrib centre="" inverse=""/></special_abilities.title>
		<special_abilities.border padding="0.5pt"><subattrib border="" inverse=""/></special_abilities.border>
		<special_abilities.lightline><subattrib light=""/></special_abilities.lightline>
		<special_abilities.darkline><subattrib medium=""/></special_abilities.darkline>

		<special_attacks.title><subattrib centre="" inverse=""/></special_attacks.title>
		<special_attacks.border padding="0.5pt"><subattrib border="" inverse=""/></special_attacks.border>
		<special_attacks.lightline><subattrib light=""/></special_attacks.lightline>
		<special_attacks.darkline><subattrib medium=""/></special_attacks.darkline>

		<talents.title><subattrib centre="" inverse=""/></talents.title>
		<talents.border padding="0.5pt"><subattrib border="" inverse=""/></talents.border>
		<talents.lightline><subattrib light=""/></talents.lightline>
		<talents.darkline><subattrib medium=""/></talents.darkline>

		<occupations.title><subattrib centre="" inverse=""/></occupations.title>
		<occupations.border padding="0.5pt"><subattrib border="" inverse=""/></occupations.border>
		<occupations.lightline><subattrib light=""/></occupations.lightline>
		<occupations.darkline><subattrib medium=""/></occupations.darkline>



		<archetypes.title><subattrib centre="" inverse=""/></archetypes.title>
		<archetypes.border padding="0.5pt"><subattrib border="" inverse=""/></archetypes.border>
		<archetypes.lightline><subattrib light=""/></archetypes.lightline>
		<archetypes.darkline><subattrib medium=""/></archetypes.darkline>

		<animal_tricks.title><subattrib centre="" inverse=""/></animal_tricks.title>
		<animal_tricks.border padding="0.5pt"><subattrib border="" inverse=""/></animal_tricks.border>
		<animal_tricks.lightline><subattrib light=""/></animal_tricks.lightline>
		<animal_tricks.darkline><subattrib medium=""/></animal_tricks.darkline>

		<special_qualities.title><subattrib centre="" inverse=""/></special_qualities.title>
		<special_qualities.border padding="0.5pt"><subattrib border="" inverse=""/></special_qualities.border>
		<special_qualities.lightline><subattrib light=""/></special_qualities.lightline>
		<special_qualities.darkline><subattrib medium=""/></special_qualities.darkline>

		<afflictions.title><subattrib centre="" inverse=""/></afflictions.title>
		<afflictions.border padding="0.5pt"><subattrib border="" inverse=""/></afflictions.border>
		<afflictions.lightline><subattrib light=""/></afflictions.lightline>
		<afflictions.darkline><subattrib medium=""/></afflictions.darkline>

		<tempbonuses.title><subattrib centre="" inverse=""/></tempbonuses.title>
		<tempbonuses.border padding="0.5pt"><subattrib border="" inverse=""/></tempbonuses.border>
		<tempbonuses.lightline><subattrib light=""/></tempbonuses.lightline>
		<tempbonuses.darkline><subattrib medium=""/></tempbonuses.darkline>



		<intelligent_items.title><subattrib centre="" inverse=""/></intelligent_items.title>
		<intelligent_items.border padding="0.5pt"><subattrib border="" inverse=""/></intelligent_items.border>
		<intelligent_items.lightline><subattrib light=""/></intelligent_items.lightline>
		<intelligent_items.darkline><subattrib medium=""/></intelligent_items.darkline>

		<traits.title><subattrib centre="" inverse=""/></traits.title>
		<traits.border padding="0.5pt"><subattrib border="" inverse=""/></traits.border>
		<traits.lightline><subattrib light=""/></traits.lightline>
		<traits.darkline><subattrib medium=""/></traits.darkline>

		<salient_divine_abilities.title><subattrib centre="" inverse=""/></salient_divine_abilities.title>
		<salient_divine_abilities.border padding="0.5pt"><subattrib border="" inverse=""/></salient_divine_abilities.border>
		<salient_divine_abilities.lightline><subattrib light=""/></salient_divine_abilities.lightline>
		<salient_divine_abilities.darkline><subattrib medium=""/></salient_divine_abilities.darkline>

		<feats.title><subattrib centre="" inverse=""/></feats.title>
		<feats.border padding="0.5pt"><subattrib border="" inverse=""/></feats.border>
		<feats.lightline><subattrib light=""/></feats.lightline>
		<feats.darkline><subattrib medium=""/></feats.darkline>

		<spelllist.known.header><subattrib border="" inverse="" very.dark=""/></spelllist.known.header>
		<spelllist.known.header.centre><subattrib border="" inverse="" very.dark="" centre="" /></spelllist.known.header.centre>
		<spelllist.known.known><subattrib border="" centre="" dark=""/></spelllist.known.known>
		<spelllist.known.perday><subattrib border="" centre="" light=""/></spelllist.known.perday>
		<spelllist.header><subattrib centre="" inverse=""/></spelllist.header>
		<spelllist.footer><subattrib centre="" inverse=""/></spelllist.footer>
		<spelllist.levelheader><subattrib centre="" dark=""/></spelllist.levelheader>
		<spelllist.darkline><subattrib medium=""/></spelllist.darkline>
		<spelllist.lightline><subattrib light=""/></spelllist.lightline>
		<spells.memorized.header><subattrib centre="" very.dark=""/></spells.memorized.header>
		<spells.memorized.level border-bottom-width="0.5pt" border-bottom-style="solid"><subattrib centre="" normal=""/></spells.memorized.level>
		<spells.memorized><subattrib normal=""/></spells.memorized>

	</myAttribs:myAttribs>
	<xsl:variable name="vAttribs" select="document('')/*/myAttribs:*"/>

	<xsl:attribute-set name="talents.title" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border">
		<xsl:attribute name="border-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="centre">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border.temp">
		<xsl:attribute name="border-top-width">2pt</xsl:attribute>
		<xsl:attribute name="border-left-width">2pt</xsl:attribute>
		<xsl:attribute name="border-right-width">2pt</xsl:attribute>
		<xsl:attribute name="border-bottom-width">2pt</xsl:attribute>
		<xsl:attribute name="border-style">solid</xsl:attribute>
		<xsl:attribute name="border-color">lightgrey</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bio">
		<xsl:attribute name="display-align">after</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bio.title">
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-top-color">black</xsl:attribute>
 		<xsl:attribute name="border-top-width">0.5pt</xsl:attribute>
 		<xsl:attribute name="border-top-style">solid</xsl:attribute>
 	</xsl:attribute-set>

	<xsl:attribute-set name="picture" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="stat.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="stat.score" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="stat.modifier" use-attribute-sets="stat.score" />

	<xsl:attribute-set name="hp.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="hp.total" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="hp.current" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="hp.subdual" use-attribute-sets="hp.current" />

	<xsl:attribute-set name="damage.reduction" use-attribute-sets="hp.current" />

 	<xsl:attribute-set name="speed" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac.total" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac.flatfooted" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac.touch" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="miss_chance" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="spell_failure" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="ac_check" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

 	<xsl:attribute-set name="spell_resistance" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="initiative.title" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="initiative.total" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="initiative.general" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="bab.title" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bab.total" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.header" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.darkline.total">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.lightline.total">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="skills.footer">
		<xsl:attribute name="border-bottom-color">black</xsl:attribute>
		<xsl:attribute name="border-bottom-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="saves.title" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="saves.total" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="saves" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="tohit.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="tohit.total" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="tohit" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>



	<xsl:attribute-set name="weapon.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="weapon" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="weapon.hilight" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="protection.title" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="protection" use-attribute-sets="border centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="protection.darkline" use-attribute-sets="centre">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="protection.lightline" use-attribute-sets="centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="domains.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="domains.lightline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="domains.darkline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="proficiencies.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="proficiencies" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="languages.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="languages" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="templates.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="templates.lightline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="templates.darkline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="companions.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="companions" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="equipment.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="equipment.lightline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="equipment.darkline" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="weight.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="weight" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="weight.solid" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="money.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="money.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="money.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="magic.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="magic.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="magic.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>



	<xsl:attribute-set name="special_abilities.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="special_abilities.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="special_abilities.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>



	<xsl:attribute-set name="feats.title" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="feats.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="feats.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>



	<xsl:attribute-set name="spelllist.known.header" use-attribute-sets="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.known.known" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.known.perday" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="spelllist.header" use-attribute-sets="centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.footer" use-attribute-sets="centre">
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.levelheader" use-attribute-sets="centre">
		<xsl:attribute name="background-color">darkgrey</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
		<xsl:attribute name="border-color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.darkline">
		<xsl:attribute name="background-color">lightgrey</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spelllist.lightline">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="spells.memorized.header" use-attribute-sets="centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spells.memorized.level" use-attribute-sets="centre">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="border-bottom-width">0.5pt</xsl:attribute>
		<xsl:attribute name="border-bottom-color">black</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="spells.memorized">
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="reputation.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="reputation" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="occupation.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="occupation" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="allegiances.title" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">black</xsl:attribute>
		<xsl:attribute name="color">white</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="allegiances" use-attribute-sets="border centre">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>



	<xsl:template match="character">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<!--	PAGE DEFINITIONS	-->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Portrait 2 Column"
						page-height="297mm"
						page-width="210mm"
						margin-top="10mm"
						margin-bottom="15mm"
						margin-left="6mm"
						margin-right="6mm">
					<fo:region-body region-name="body" column-count="2" column-gap="2mm" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="0.25in"/>
				</fo:simple-page-master>

				<fo:simple-page-master master-name="Portrait"
						page-height="297mm"
						page-width="210mm"
						margin-top="10mm"
						margin-bottom="15mm"
						margin-left="6mm"
						margin-right="6mm">
					<fo:region-body region-name="body" margin-bottom="7mm"/>
					<fo:region-after region-name="footer" extent="0.25in"/>
				</fo:simple-page-master>

			</fo:layout-master-set>



			<fo:page-sequence master-reference="Portrait">
				<xsl:call-template name="page.footer" />

				<!--	CHARACTER BODY STARTS HERE !!!	-->
				<fo:flow flow-name="body">
					<!--	CHARACTER HEADER	-->
					<fo:block span="all" space-after.optimum="3pt">
						<xsl:apply-templates select="basics"/>
					</fo:block>
					<fo:block span="all">
						<fo:table table-layout="fixed">
							<fo:table-column column-width="52mm"/>
							<fo:table-column column-width="52mm"/>
							<fo:table-column column-width="86mm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell number-rows-spanned="2">
										<xsl:apply-templates select="abilities" />
									</fo:table-cell>
									<fo:table-cell number-columns-spanned="2">
										<xsl:apply-templates select="." mode="hp_table"/>
										<xsl:apply-templates select="armor_class"/>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<xsl:apply-templates select="initiative" />
										<xsl:apply-templates select="basics/bab" mode="bab" />
									</fo:table-cell>
									<fo:table-cell number-rows-spanned="2">
										<xsl:apply-templates select="skills">
											<xsl:with-param name="first_skill" select="0"/>
											<xsl:with-param name="last_skill" select="55"/>
											<xsl:with-param name="column_width" select="'narrow'"/>
										</xsl:apply-templates>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2">
										<fo:table table-layout="fixed">
											<fo:table-column column-width="70mm"/>
											<fo:table-column column-width="2mm"/>
											<fo:table-column column-width="30mm"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<xsl:apply-templates select="saving_throws" />
													</fo:table-cell>
													<fo:table-cell />
													<fo:table-cell>
														<xsl:call-template name="reputation" />
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
										<xsl:apply-templates select="attack" mode="ranged_melee" />
										<xsl:apply-templates select="weapons/unarmed" />
										<xsl:apply-templates select="weapons">
											<xsl:with-param name="first_weapon" select="1"/>
											<xsl:with-param name="last_weapon" select="3"/>
											<xsl:with-param name="column_width" select="'wide'"/>
										</xsl:apply-templates>
										<xsl:apply-templates select="protection/armor[1]"/>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>


			<fo:page-sequence master-reference="Portrait 2 Column">
				<xsl:call-template name="page.footer" />
				<fo:flow flow-name="body">
					<fo:block>
						<xsl:apply-templates select="weapons">
							<xsl:with-param name="first_weapon" select="4"/>
							<xsl:with-param name="last_weapon" select="9999"/>
							<xsl:with-param name="column_width" select="'narrow'"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="skills">
							<xsl:with-param name="first_skill" select="56"/>
							<xsl:with-param name="last_skill" select="9999"/>
							<xsl:with-param name="column_width" select="'wide'"/>
						</xsl:apply-templates>
						<xsl:apply-templates select="equipment" />
						<xsl:apply-templates select="weight_allowance" />
						<xsl:call-template name="money" />
						<xsl:apply-templates select="misc/magics" />
						<xsl:apply-templates select="special_abilities" />
						<xsl:apply-templates select="special_qualities" />
						<xsl:apply-templates select="feats" />
<!-->						<xsl:apply-templates select="feats/feat[contains(., 'OCCUPATION')]" mode="starting_occupation" />	-->
						<xsl:apply-templates select="occupations" />
						<xsl:apply-templates select="talents" />

						<xsl:apply-templates select="mutations" />
						<xsl:apply-templates select="weapon_proficiencies" />
						<xsl:apply-templates select="languages" />
						<xsl:call-template name="allegiances" />
						<xsl:apply-templates select="templates" />
						<xsl:apply-templates select="companions" />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>

			<xsl:apply-templates select="spells" />
			<xsl:apply-templates select="basics" mode="bio" />
			<xsl:apply-templates select="basics/notes" mode="bio" />

		</fo:root>
	</xsl:template>

<!-->
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
	TEMPLATE - PAGE FOOTER
====================================
====================================-->
	<xsl:template name="page.footer">
		<fo:static-content flow-name="footer" font-family="sans-serif">
			<fo:table table-layout="fixed">
				<fo:table-column column-width="1.875in"/>
				<fo:table-column column-width="3.75in"/>
				<fo:table-column column-width="1.875in"/>
				<fo:table-body>
					<fo:table-row keep-with-next="always" keep-together="always">
						<fo:table-cell text-align="start" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="white" padding-top="2pt">
							<fo:block font-size="5pt">
								<xsl:value-of select="export/date" />
								<xsl:text> </xsl:text>
								<xsl:value-of select="export/time" />
							</fo:block>
							<fo:block font-size="5pt" font-weight="bold">Created using <fo:basic-link external-destination="http://pcgen.org/" show-destination="true" color="blue" text-decoration="underline">PCGen</fo:basic-link> <xsl:value-of select="export/version"/></fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" wrap-option="no-wrap" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="white" padding-top="2pt">
							<fo:block text-align="center" font-size="5pt">PCGen Character Template by Frugal, based on work by ROG, Arcady, Barak &amp; Dimrill.</fo:block>
							<fo:block text-align="center" font-size="5pt">For suggestions please post to pcgen@yahoogroups.com with "OS Suggestion" in the subject line.</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="end" border-top-color="black" border-top-style="solid" border-top-width="0.1pt" background-color="white" padding-top="2pt">
							<fo:block font-size="7pt">Page <fo:page-number/></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:static-content>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - CHARACTER HEADER
====================================
====================================-->
	<xsl:template match="basics">
		<!-- Character Header -->
			<fo:table table-layout="fixed" width="190mm">
				<xsl:choose>
					<xsl:when test="string-length(portrait) &gt; 0">
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="22mm"/>
					</xsl:when>
					<xsl:otherwise>
						<fo:table-column column-width="27mm"/> <!-- Class -->
						<fo:table-column column-width="2mm"/>  <!--  -->
						<fo:table-column column-width="25mm"/> <!-- Experience -->
						<fo:table-column column-width="2mm"/>  <!-- -->
						<fo:table-column column-width="25mm"/> <!-- Race -->
						<fo:table-column column-width="2mm"/>  <!-- -->
						<fo:table-column column-width="25mm"/> <!-- Size -->
						<fo:table-column column-width="2mm"/>  <!-- -->
						<fo:table-column column-width="25mm"/> <!-- Height -->
						<fo:table-column column-width="2mm"/>  <!-- -->
						<fo:table-column column-width="24mm"/> <!-- Weight -->
						<fo:table-column column-width="2mm"/>  <!-- -->
						<fo:table-column column-width="27mm"/> <!-- Vision -->
					</xsl:otherwise>
				</xsl:choose>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="5" xsl:use-attribute-sets="bio">
							<fo:block font-size="10pt">
								<xsl:value-of select="name" />
								<xsl:if test="string-length(followerof) &gt; 0">	- <xsl:value-of select="followerof" /></xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell number-columns-spanned="5" xsl:use-attribute-sets="bio">
							<fo:block font-size="10pt"><xsl:value-of select="playername"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="poolpoints/cost"/></fo:block>
						</fo:table-cell>

						<xsl:if test="string-length(portrait) &gt; 0" >
							<fo:table-cell/>
							<fo:table-cell number-rows-spanned="6"  xsl:use-attribute-sets="picture">
								<fo:block>
								<xsl:variable name="portrait_file" select="portrait" />
									<fo:external-graphic src="file:{$portrait_file}" width="22mm"/>
								</fo:block>
							</fo:table-cell>
						</xsl:if>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="5" xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">NAME</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell number-columns-spanned="5" xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">PLAYERNAME</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">POINTS</fo:block>
						</fo:table-cell>
					</fo:table-row>



					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="classes/shortform"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="experience/current"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="race"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="size/long"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="height/feet"/>'<xsl:value-of select="height/inches"/>"</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="weight/weight_unit"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="vision/all"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">CLASS</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">EXPERIENCE</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">RACE</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">SIZE</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">HEIGHT</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">WEIGHT</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">VISION</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					</fo:table-row>




					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt">
								<xsl:value-of select="classes/levels_total"/>
								<xsl:if test="classes/levels_total != classes/levels_ecl">/<xsl:value-of select="classes/levels_ecl"/></xsl:if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="experience/next_level"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="age"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="gender/long"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="eyes/color"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio" number-columns-spanned="3">
							<fo:block font-size="8pt" padding-top="3pt"><xsl:value-of select="hair/color"/>, <xsl:value-of select="hair/length"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">
								<xsl:if test="classes/levels_total != classes/levels_ecl">
									<xsl:text>ECL / </xsl:text>
								</xsl:if>
								<xsl:text>TCL</xsl:text>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">NEXT LEVEL</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">AGE</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">GENDER</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title">
							<fo:block font-size="6pt" padding-top="1pt">EYES</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="bio.title" number-columns-spanned="3">
							<fo:block font-size="6pt" padding-top="1pt">HAIR</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
					</fo:table-row>

				</fo:table-body>
			</fo:table>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - GENERIC OUTPUT-FOR-LOOP
====================================
====================================-->
	<xsl:template name="for.loop">
		<xsl:param name="i" select="1"/>
		<xsl:param name="count" select="0"/>
		<xsl:param name="display" select="'&#x274F;'"/>
		<!-- '&#x274F;' is the Unicode symbol for a 'box' -->
		<xsl:if test="$i &lt;= $count">
			<!-- Show this box -->
			<xsl:value-of select="$display"/>
			<xsl:if test="$i mod 5 = 0">
				<xsl:text> </xsl:text>
			</xsl:if>
			<!-- Show all of the remaining boxes -->
			<xsl:call-template name="for.loop">
				<xsl:with-param name="i" select="$i + 1"/>
				<xsl:with-param name="display" select="$display"/>
				<xsl:with-param name="count" select="$count"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!--
====================================
====================================
	TEMPLATE - GENERIC OUTPUT-FOR-LOOP (inverted)
====================================
====================================-->
	<xsl:template name="for.loop.inverted">
		<xsl:param name="i" select="1"/>
		<xsl:param name="count" select="0"/>
		<xsl:param name="display" select="'&#x25cf;'"/>
		<!-- '&#x274F;' is the Unicode symbol for a 'circle' -->
		<xsl:if test="$i &lt;= $count">
			<!-- Show this box -->
			<xsl:value-of select="$display"/>
			<xsl:if test="$i mod 5 = 0">
				<xsl:text> </xsl:text>
			</xsl:if>
			<!-- Show all of the remaining boxes -->
			<xsl:call-template name="for.loop">
				<xsl:with-param name="i" select="$i + 1"/>
				<xsl:with-param name="display" select="$display"/>
				<xsl:with-param name="count" select="$count"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
<!--
====================================
====================================
	TEMPLATE - GENERIC OUTPUT Cumulative total
====================================
====================================-->
	<xsl:template name="Total">
		<xsl:param name="Items"/>
		<xsl:param name="RunningTotal"/>

		<xsl:choose>
			<xsl:when test="not($Items)">
				<!-- No more Items so return Running Total -->
				<xsl:copy-of select="$RunningTotal"/>
			</xsl:when>

			<xsl:otherwise>
				<!-- Call template for remaining Items -->
				<xsl:variable name="CurrentTotal" select="$RunningTotal + ($Items[1]/quantity * $Items[1]/cost)"/>
				<xsl:call-template name="Total">
					<xsl:with-param name="Items" select="$Items[position()>1]"/>
					<xsl:with-param name="RunningTotal" select="$CurrentTotal"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>





<!--
====================================
====================================
	TEMPLATE - GENERIC Process
====================================
====================================-->
	<xsl:template name="process.attack.string">
		<xsl:param name="bab"/>
		<xsl:param name="string" select="''"/>

		<xsl:choose>
			<xsl:when test="starts-with($bab, '+')">
				<xsl:call-template name="process.attack.string">
					<xsl:with-param name="bab" select="substring($bab, 2)"/>
					<xsl:with-param name="string" select="$string"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$bab &gt; 5">
						<xsl:call-template name="process.attack.string">
							<xsl:with-param name="bab" select="$bab - 5"/>
							<xsl:with-param name="string">
								<xsl:value-of select="$string"/>+<xsl:value-of select="$bab"/>/</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$string"/>+<xsl:value-of select="$bab"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
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
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
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
						<fo:block text-align="center" font-size="4pt">ABILITY MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP SCORE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP MODIFIER</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="ability">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="stat.title">
							<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">
								<xsl:value-of select="name/short"/>
							</fo:block>
							<fo:block line-height="4pt" font-size="4pt">
								<xsl:value-of select="name/long"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="stat.score">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="score"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="stat.modifier">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="modifier"/></fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell height="4pt" xsl:use-attribute-sets="border.temp"/>
						<fo:table-cell/>
						<fo:table-cell height="4pt" xsl:use-attribute-sets="border.temp"/>
					</fo:table-row>
					<fo:table-row height="2pt">
						<fo:table-cell/>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
		<!-- END Ability Block -->
	</xsl:template>




<!--
====================================
====================================
	TEMPLATE - HP  TABLE
====================================
====================================-->
	<xsl:template match="character" mode="hp_table">
		<fo:table table-layout="fixed">
			<fo:table-column column-width="13mm"/> 		<!-- title -->
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="8mm"/>		<!-- total -->
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="23.5mm"/>		<!-- Current -->
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="23.5mm"/>		<!-- subdual -->
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="15mm"/>		<!-- DT -->
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="15mm"/>		<!-- DR -->
			<fo:table-column column-width="3mm"/>
			<fo:table-column column-width="27mm"/>		<!-- SPEED -->
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center"  font-size="6pt"/>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center" font-size="4pt">WOUNDS/CURRENT HP</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center"  font-size="4pt">SUBDUAL DAMAGE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center"  font-size="4pt">DAMAGE THRESHOLD</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center" font-size="4pt">DAMAGE REDUCTION</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell display-align="after">
						<fo:block text-align="center" font-size="4pt">SPEED</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="hp.title">
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">HP</fo:block>
						<fo:block line-height="4pt" font-size="4pt">hit points</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="hp.total">
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:value-of select="hit_points/points"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="hp.current">
						<fo:block space-before.optimum="2pt" font-size="10pt"></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="hp.subdual">
						<fo:block space-before.optimum="2pt" font-size="10pt"></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="damage.reduction">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="hit_points/damage_threshold"/></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="damage.reduction">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="hit_points/damage_reduction"/></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="speed" display-align="center"  >
						<fo:block font-size="8pt"><xsl:value-of select="basics/move/all"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END HP-AC Table -->
	</xsl:template>









<!--
====================================
====================================
	TEMPLATE - HP and AC TABLE
====================================
====================================-->
	<xsl:template match="armor_class">
		<fo:table table-layout="fixed" space-before="2pt">
			<fo:table-column column-width="13mm"/>	<!-- hp -->
			<fo:table-column column-width="2mm"/><!-- space -->
			<fo:table-column column-width="8mm"/><!-- total ac -->
			<fo:table-column column-width="2mm"/><!-- : -->
			<fo:table-column column-width="8mm"/><!-- flat -->
			<fo:table-column column-width="2mm"/><!-- : -->
			<fo:table-column column-width="8mm"/><!-- touch -->
			<fo:table-column column-width="2mm"/><!-- = -->
			<fo:table-column column-width="7mm"/><!-- base -->
			<fo:table-column column-width="2mm"/><!-- + -->
			<fo:table-column column-width="7mm"/><!--armour  -->
			<fo:table-column column-width="2mm"/><!-- + -->
			<fo:table-column column-width="7mm"/><!-- stat -->
			<fo:table-column column-width="2mm"/><!-- + -->
			<fo:table-column column-width="7mm"/><!--  size -->
			<fo:table-column column-width="2mm"/><!-- + -->
			<fo:table-column column-width="7mm"/><!-- natural armour-->
			<fo:table-column column-width="2mm"/><!-- + -->
			<fo:table-column column-width="7mm"/><!-- misc   -->
			<fo:table-column column-width="4mm"/><!-- space -->
			<fo:table-column column-width="7mm"/><!-- miss chance -->
			<fo:table-column column-width="3mm"/><!-- space -->
			<fo:table-column column-width="7mm"/><!-- arcane spell failure -->
			<fo:table-column column-width="3mm"/><!-- space -->
			<fo:table-column column-width="7mm"/><!-- armour check-->
			<fo:table-column column-width="3mm"/><!-- space -->
			<fo:table-column column-width="7mm"/><!-- SR -->
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell  xsl:use-attribute-sets="ac.title">
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">AC</fo:block>
						<fo:block line-height="4pt" font-size="4pt">armor class</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="ac.total">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="total"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">:</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac.flatfooted">
						<fo:block space-before.optimum="3pt" font-size="10pt"><xsl:value-of select="flat"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">:</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac.touch">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="touch"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">=</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="base"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="class_bonus"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="equipment_bonus"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="stat_mod"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell  xsl:use-attribute-sets="ac">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="size_mod"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="ac">
						<fo:block text-align="center" space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="misc"/></fo:block>
<!--						<fo:block text-align="center" space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="misc + competence_bonus + defense_bonus + dodge_bonus + enhancement_bonus + insight_bonus + luck_bonus + monk_bonus + sidestep_bonus + tar_bonus + toughness_bonus"/></fo:block> -->
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="miss_chance">
						<fo:block space-before.optimum="2pt" font-size="10pt"><!-- Miss chance --></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="spell_failure">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="spell_failure"/></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="ac_check">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="check_penalty"/></fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="spell_resistance">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="spell_resistance"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="6pt">FLAT</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="6pt">TOUCH</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">CLASS BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">EQUIP BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">DEX MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SIZE MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISS CHANCE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARCANE SPELL FAILURE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ARMOR CHECK PENALTY</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">SPELL RESISTANCE</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END HP-AC Table -->
	</xsl:template>


<!--
====================================
====================================
	TEMPLATE - Initiative TABLE
====================================
====================================-->
	<xsl:template match="initiative">
		<!-- BEGIN ini-base table -->
		<fo:table table-layout="fixed"> <!-- 48mm -->
			<fo:table-column column-width="21mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-body>
				<fo:table-row height="2pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="initiative.title">
						<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt">INITIATIVE</fo:block>
						<fo:block line-height="4pt" font-size="4pt">modifier</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="initiative.total">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="total"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">=</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="initiative.general">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="dex_mod"/></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="initiative.general">
						<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="misc_mod"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="0.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">DEX MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC MODIFIER</fo:block>
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
	<xsl:template match="bab" mode="bab" >
		<!-- BEGIN ini-base table -->
		<fo:table table-layout="fixed"> <!-- 48mm -->
			<fo:table-column column-width="21mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="27mm"/>
			<fo:table-body>
				<fo:table-row height="2pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="bab.title">
						<fo:block line-height="10pt" font-weight="bold" font-size="8pt">BASE ATTACK</fo:block>
						<fo:block line-height="4pt" font-size="4pt">bonus</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="bab.total">
						<fo:block space-before.optimum="2pt" font-size="10pt">
							<xsl:call-template name="process.attack.string">
								<xsl:with-param name="bab" select="."/>
							</xsl:call-template>
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
	TEMPLATE - SKILLS TABLE
====================================
====================================-->
	<xsl:template match="skills">
		<xsl:param name="first_skill" select="0" />
		<xsl:param name="last_skill" select="0" />
		<xsl:param name="column_width" select="'wide'" />

		<!-- begin skills table -->
		<xsl:if test="count(skill) &gt;= $first_skill" >
			<fo:table table-layout="fixed" border-collapse="collapse">
				<xsl:choose>
					<xsl:when test="$column_width='wide'">
						<fo:table-column column-width="4mm"/>
						<fo:table-column column-width="38mm"/>
						<fo:table-column column-width="3mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="8mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="8mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="8mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="8mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="8mm"/>
					</xsl:when>
					<xsl:otherwise>
						<fo:table-column column-width="4mm"/>
						<fo:table-column column-width="38mm"/>
						<fo:table-column column-width="2mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="7mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="7mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="6mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="6mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="1mm"/>
						<fo:table-column column-width="7mm"/>
					</xsl:otherwise>
				</xsl:choose>
				<fo:table-header>
					<fo:table-row height="2pt">
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="skills.header" border-top-width="1pt" border-left-width="1pt" border-right-width="0pt" border-bottom-width="0pt"/>
						<fo:table-cell xsl:use-attribute-sets="skills.header" number-columns-spanned="6" padding="1pt" border-top-width="1pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="0pt" border-style="solid">
							<fo:block text-align="end" line-height="10pt" font-weight="bold" font-size="10pt">
								SKILLS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="8"	padding="1pt" xsl:use-attribute-sets="skills.header" border-top-width="1pt" border-left-width="0pt"	border-right-width="0pt" border-bottom-width="0pt">
							<fo:block text-align="end" space-before.optimum="4pt" line-height="4pt" font-size="4pt">
								MAX RANKS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="2" xsl:use-attribute-sets="skills.header" border-top-width="1pt" border-left-width="0pt" border-right-width="1pt" border-bottom-width="0pt">
							<fo:table table-layout="fixed" space-before.optimum="0.2mm" >
								<fo:table-column column-width="7.625mm"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell >
											<fo:block space-before.optimum="1pt" line-height="8pt" font-size="6pt">
												<xsl:value-of select="max_class_skill_level"/>/<xsl:value-of select="max_cross_class_skill_level"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="skills.header" border-top-width="0pt" 	border-left-width="1pt" border-right-width="0pt" border-bottom-width="1pt" />
						<fo:table-cell number-columns-spanned="2" xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block font-weight="bold" font-size="8pt" >
								SKILL NAME
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3" xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block font-size="4pt">
								KEY ABILITY
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3" xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block text-align="center" font-size="4pt">
								SKILL MODIFIER
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3" xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block text-align="center" font-size="4pt">
								ABILITY MODIFIER
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="3" xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block text-align="center" space-before.optimum="5pt" font-size="4pt" >
								RANKS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="2"  xsl:use-attribute-sets="skills.header" padding="1pt" border-top-width="0pt" border-left-width="0pt" border-right-width="0pt" border-bottom-width="1pt">
							<fo:block text-align="center" font-size="4pt">
								MISC MODIFIER
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>

					<xsl:for-each select="skill">
						<xsl:if test="position() &gt;= $first_skill and position() &lt;= $last_skill" >
							<xsl:variable name="skills.row">
								<fo:table-cell>
									<fo:block font-size="6pt" font-family="ZapfDingbats">
										<xsl:if test="translate( substring(untrained,1,1), 'Y', 'y')='y'">&#x2713;</xsl:if>
										<xsl:if test="translate( substring(exclusive,1,1), 'Y', 'y')='y'">&#x2717;</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block space-before.optimum="1pt" font-size="8pt">
										<xsl:value-of select="name"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"/>
								<fo:table-cell>
									<fo:block space-before.optimum="1pt" font-size="8pt">
										<xsl:value-of select="ability"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"/>
								<xsl:choose>
									<xsl:when test="position() mod 2 = 0"><xsl:apply-templates select="skill_mod" mode="skills.total.darkline" /></xsl:when>
									<xsl:otherwise><xsl:apply-templates select="skill_mod" mode="skills.total.lightline" /></xsl:otherwise>
								</xsl:choose>
								<fo:table-cell number-columns-spanned="2"><fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">=</fo:block></fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:value-of select="ability_mod"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"><fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block></fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:if test="ranks>0">
											<xsl:choose>
												<xsl:when test="round(ranks) = ranks">
													<xsl:value-of select="round(ranks)"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="ranks"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2"><fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block></fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt">
										<xsl:if test="misc_mod!=0">
											<xsl:value-of select="misc_mod"/>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
							</xsl:variable>

							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									<xsl:apply-templates select="." mode="skills.darkline">
										<xsl:with-param name="skills.row" select="$skills.row"/>
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="." mode="skills.lightline">
										<xsl:with-param name="skills.row" select="$skills.row"/>
									</xsl:apply-templates>
								</xsl:otherwise>
							</xsl:choose>

						</xsl:if>
					</xsl:for-each>

					<fo:table-row height="9pt">
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
						<fo:table-cell number-columns-spanned="2"/>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
						<fo:table-cell number-columns-spanned="2"/>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
						<fo:table-cell number-columns-spanned="2">
							<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">=</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
						<fo:table-cell number-columns-spanned="2">
							<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
						<fo:table-cell number-columns-spanned="2">
							<fo:block text-align="center" space-before.optimum="3pt" line-height="6pt" font-size="6pt">+</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="skills.footer"/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="17">
							<fo:block text-align="center" font-size="6pt"><fo:inline font-family="ZapfDingbats">&#x2713;</fo:inline>: can be used untrained. <fo:inline font-family="ZapfDingbats">&#x2717;</fo:inline>: exclusive skills</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			</xsl:if>
		<!-- END Skills table -->
	</xsl:template>




	<xsl:template match="skill" mode="skills.darkline">
		<xsl:param name="skills.row" />
		<fo:table-row xsl:use-attribute-sets="skills.darkline">
			<xsl:copy-of select="$skills.row"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template match="skill" mode="skills.lightline">
		<xsl:param name="skills.row" />
		<fo:table-row xsl:use-attribute-sets="skills.lightline">
			<xsl:copy-of select="$skills.row"/>
		</fo:table-row>
	</xsl:template>


	<xsl:template match="skill_mod" mode="skills.total.darkline">
		<fo:table-cell xsl:use-attribute-sets="skills.darkline.total">
			<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt"><xsl:value-of select="."/></fo:block>
		</fo:table-cell>
	</xsl:template>


	<xsl:template match="skill_mod" mode="skills.total.lightline">
		<fo:table-cell xsl:use-attribute-sets="skills.lightline.total">
			<fo:block text-align="center" space-before.optimum="1pt" font-size="8pt"><xsl:value-of select="."/></fo:block>
		</fo:table-cell>
	</xsl:template>





<!--
====================================
====================================
	TEMPLATE - SAVES TABLE
====================================
====================================-->
	<xsl:template match="saving_throws">
		<!-- BEGIN Saves table -->
		<fo:table table-layout="fixed">
			<fo:table-column column-width="25mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">SAVING THROWS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" space-before.optimum="1pt" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">BASE SAVE</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">ABILITY MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">MISC MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="center" font-size="4pt">TEMP MODIFIER</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<xsl:for-each select="saving_throw">
					<fo:table-row height="2pt">
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="saves.title">
							<fo:block line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt"><xsl:value-of select="translate(name/long, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/></fo:block>
							<fo:block line-height="4pt" font-size="4pt">(<xsl:value-of select="ability"/>)</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets="saves.total">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="total"/></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">=</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="saves">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="base"/></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
						</fo:table-cell>
						<fo:table-cell  xsl:use-attribute-sets="saves">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="abil_mod"/></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
						</fo:table-cell>
						<fo:table-cell  xsl:use-attribute-sets="saves">
							<fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="misc_w_magic_mod"/></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center" space-before.optimum="5pt" font-size="6pt">+</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="border.temp"/>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>





	<xsl:template name="reputation">
		<fo:table table-layout="fixed">
			<fo:table-column column-width="20mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-body>
				<fo:table-row height="11pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell xsl:use-attribute-sets="reputation.title"><fo:block display-align="center" line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">REPUTATION</fo:block></fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="reputation"><fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="basics/reputation"/></fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row height="3pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="reputation.title"><fo:block display-align="center" line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">ACTION POINTS</fo:block></fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="reputation"><fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="basics/remaining_action_points"/></fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row height="3pt">
					<fo:table-cell/>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="reputation.title"><fo:block display-align="center" line-height="10pt" font-weight="bold" font-size="7pt" space-before="1pt">WEALTH</fo:block></fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets="reputation"><fo:block space-before.optimum="2pt" font-size="10pt"><xsl:value-of select="basics/wealth"/></fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
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
			<fo:table-column column-width="17mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="22mm"/>
			<fo:table-column column-width="3mm"/>
			<fo:table-column column-width="21mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-column column-width="2mm"/>
			<fo:table-column column-width="7mm"/>
			<fo:table-body>
				<xsl:call-template name="to_hit.header">
					<xsl:with-param name="dalign" select="'after'"/>
					<xsl:with-param name="stat" select="'STR'"/>
				</xsl:call-template>
				<xsl:apply-templates select="melee" mode="to_hit">
					<xsl:with-param name="title" select="'MELEE'" />
				</xsl:apply-templates>
				<fo:table-row height="2.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<xsl:apply-templates select="ranged" mode="to_hit">
					<xsl:with-param name="title" select="'RANGED'" />
				</xsl:apply-templates>

				<fo:table-row height="0.5pt">
					<fo:table-cell/>
				</fo:table-row>
				<xsl:call-template name="to_hit.header">
					<xsl:with-param name="dalign" select="'before'"/>
					<xsl:with-param name="stat" select="'DEX'"/>
				</xsl:call-template>
			</fo:table-body>
		</fo:table>
		<!-- END Attack table -->
	</xsl:template>


	<xsl:template name="to_hit.header">
		<xsl:param name="dalign" select="'after'" />
		<xsl:param name="stat" select="'after'" />
				<fo:table-row >
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center" font-size="6pt">TOTAL</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center" font-size="4pt">BASE ATTACK BONUS</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center"  font-size="4pt"><xsl:value-of select="$stat"/> MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center" font-size="4pt">SIZE MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center" font-size="4pt">MISC MODIFIER</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell>
						<xsl:attribute name="display-align"><xsl:value-of select="$dalign"/></xsl:attribute>
						<fo:block text-align="center"  font-size="4pt">TEMP MODIFIER</fo:block>
					</fo:table-cell>
				</fo:table-row>
	</xsl:template>

	<xsl:template match="melee|ranged" mode="to_hit">
		<xsl:param name="title" />
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="tohit.title">
				<fo:block space-before.optimum="0.5pt" line-height="10pt" font-weight="bold" font-size="10pt" space-before="1pt"><xsl:value-of select="$title"/></fo:block>
				<fo:block line-height="4pt" font-size="4pt">attack bonus</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
			<fo:table-cell xsl:use-attribute-sets="tohit.total">
				<fo:block space-before.optimum="3pt" font-size="8pt"><xsl:value-of select="total" /></fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit" border-bottom="0pt" border-top="0pt">
				<fo:block space-before.optimum="5pt" font-size="6pt">=</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit">
				<fo:block space-before.optimum="3pt" font-size="8pt"><xsl:value-of select="base_attack_bonus" /></fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit" border-bottom="0pt" border-top="0pt">
				<fo:block space-before.optimum="5pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit">
				<fo:block space-before.optimum="3pt" font-size="8pt"><xsl:value-of select="stat_mod" /></fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit" border-bottom="0pt" border-top="0pt">
				<fo:block space-before.optimum="5pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit">
				<fo:block space-before.optimum="3pt" font-size="8pt"><xsl:value-of select="size_mod" /></fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit" border-bottom="0pt" border-top="0pt">
				<fo:block space-before.optimum="5pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit">
				<fo:block space-before.optimum="3pt" font-size="8pt"><xsl:value-of select="misc_mod" /></fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tohit" border-bottom="0pt" border-top="0pt">
				<fo:block space-before.optimum="5pt" font-size="6pt">+</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="border.temp"/>
		</fo:table-row>
	</xsl:template>




<!--
====================================
====================================
	TEMPLATE - Unarmed ATTACK TABLE
====================================
====================================-->
	<xsl:template match="weapons/unarmed">
		<!-- START Unarmed Attack Table -->
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="35mm"/>
			<fo:table-column column-width="35mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell number-rows-spanned="2" xsl:use-attribute-sets="weapon.title"><fo:block font-weight="bold" font-size="10pt">UNARMED</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt">DAMAGE</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt">CRITICAL</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="8pt"><xsl:value-of select="total"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="8pt"><xsl:value-of select="damage"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="8pt"><xsl:value-of select="critical"/></fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- STOP Unarmed Attack Table -->
	</xsl:template>






<!--
====================================
====================================
	TEMPLATE - First 3 weapons
====================================
====================================-->
	<xsl:template match="weapons">
		<xsl:param name="first_weapon" select="0" />
		<xsl:param name="last_weapon" select="0" />
		<xsl:param name="column_width" select="'wide'" />

		<xsl:for-each select="weapon">
			<xsl:if test="(position() &gt;= $first_weapon) and (position() &lt;= $last_weapon)" >
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
			<fo:block font-size="5pt" space-before="1pt" color="black">
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
		<xsl:param name="column_width" select="'wide'" />

		<fo:table table-layout="fixed" space-before="2mm" keep-with-next="always" keep-together="always">
			<xsl:choose>
				<xsl:when test="$column_width='wide'">
					<fo:table-column column-width="50mm"/>
					<fo:table-column column-width="21mm"/>
					<fo:table-column column-width="9mm"/>
					<fo:table-column column-width="9mm"/>
					<fo:table-column column-width="13mm"/>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-column column-width="47mm"/>
					<fo:table-column column-width="19mm"/>
					<fo:table-column column-width="8mm"/>
					<fo:table-column column-width="8mm"/>
					<fo:table-column column-width="12mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<!-- Name row (including Hand, Type, Size and Crit -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title" number-rows-spanned="2">
						<fo:block font-weight="bold" font-size="10pt">
							<xsl:variable name="name" select="substring-before(name/short,'(')"/>
							<xsl:variable name="description" select="substring-after(name/short,'(')"/>
							<xsl:value-of select="$name"/>
							<xsl:if test="string-length($name) = 0">
								<xsl:value-of select="name/short"/>
							</xsl:if>
							<xsl:if test="string-length($description) &gt; 0">
								<fo:inline font-size="6pt"><xsl:text>(</xsl:text><xsl:value-of select="$description"/></fo:inline>
							</xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title">
						<fo:block font-size="6pt">CURRENT HAND</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title">
						<fo:block font-size="6pt">TYPE</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title">
						<fo:block font-size="6pt">SIZE</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title">
						<fo:block  font-size="6pt">CRITICAL</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<!-- Hand, Type, Size and Crit -->
					<fo:table-cell xsl:use-attribute-sets="weapon">
						<fo:block font-size="7pt"><xsl:value-of select="hand"/></fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon">
						<fo:block font-size="7pt"><xsl:value-of select="type"/></fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon">
						<fo:block font-size="7pt"><xsl:value-of select="size"/></fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight">
						<fo:block font-size="7pt">
							<xsl:value-of select="critical/range"/>
							<xsl:text>/x</xsl:text>
							<xsl:value-of select="critical/multiplier"/>
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
		<xsl:param name="column_width" select="'wide'" />
		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
		<xsl:if test="contains(category, 'Ranged') or contains(category, 'Both')">
			<xsl:choose>
				<xsl:when test="$column_width='wide'"> <!-- 102mm -->
					<fo:table-column column-width="15mm"/>
					<fo:table-column column-width="15mm"/>
					<fo:table-column column-width="21mm"/>
					<fo:table-column column-width="51mm"/>
				</xsl:when>
				<xsl:otherwise> <!-- 94mm -->
					<fo:table-column column-width="15mm"/>
					<fo:table-column column-width="15mm"/>
					<fo:table-column column-width="21mm"/>
					<fo:table-column column-width="43mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold">Rate of Fire</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="rateoffire"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold">Special Properties</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="special_properties"/></fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</xsl:if>
		<xsl:if test="false=contains(category, 'Ranged') and false=contains(category, 'Both')">
			<xsl:choose>
				<xsl:when test="$column_width='wide'"> <!-- 102mm -->
					<fo:table-column column-width="21mm"/>
					<fo:table-column column-width="81mm"/>
				</xsl:when>
				<xsl:otherwise> <!-- 94mm -->
					<fo:table-column column-width="21mm"/>
					<fo:table-column column-width="73mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold">Special Properties</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="special_properties"/></fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</xsl:if>
		</fo:table>
	</xsl:template>


<!--
====================================
====================================
	TEMPLATE - weapons - simple
====================================
====================================-->
	<xsl:template match="simple">
		<xsl:param name="column_width" select="'wide'" />
		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
			<xsl:choose>
				<xsl:when test="$column_width='wide'"> <!-- 102mm -->
					<fo:table-column column-width="51mm"/>
					<fo:table-column column-width="51mm"/>
				</xsl:when>
				<xsl:otherwise> <!-- 94mm -->
					<fo:table-column column-width="47mm"/>
					<fo:table-column column-width="47mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt">TOTAL ATTACK BONUS</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt">DAMAGE</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="8pt"><xsl:value-of select="to_hit"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="8pt"><xsl:value-of select="damage"/></fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>




<!--
====================================
====================================
	TEMPLATE - weapons - melee
====================================
====================================-->
	<xsl:template match="melee">
		<xsl:param name="column_width" select="'wide'" />

		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
			<xsl:choose>
				<xsl:when test="$column_width='wide'">
					<fo:table-column column-width="8mm"/>
					<fo:table-column column-width="29mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="11mm"/>
					<fo:table-column column-width="28mm"/>
					<fo:table-column column-width="13mm"/>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-column column-width="8mm"/>
					<fo:table-column column-width="26mm"/>
					<fo:table-column column-width="12mm"/>
					<fo:table-column column-width="11mm"/>
					<fo:table-column column-width="25mm"/>
					<fo:table-column column-width="12mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<!-- To hit and Damage titles -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"/>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold" space-before="1pt">To Hit</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold" space-before="1pt">Dam</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"/>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold" space-before="1pt">To Hit</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold" space-before="1pt">Dam</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<!-- 1HP, 2WP-OH -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold" space-before="1pt">1H-P</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt"  space-before="1pt"><xsl:value-of select="w1_h1_p/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w1_h1_p/damage" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold" space-before="1pt">2W-P-(OH)</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_p_oh/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_p_oh/damage" /></fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<!-- 1HO, 2WPOL -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold" space-before="1pt">1H-O</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w1_h1_o/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w1_h1_o/damage" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold" space-before="1pt">2W-P-(OL)</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_p_ol/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_p_ol/damage" /></fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<!-- 2H, OH -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold" space-before="1pt">2H</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w1_h2/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w1_h2/damage" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block  font-size="5pt" font-weight="bold" space-before="1pt">2W-OH</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_o/to_hit" /></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="7pt" space-before="1pt"><xsl:value-of select="w2_o/damage" /></fo:block></fo:table-cell>
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
		<xsl:param name="column_width" select="'wide'" />

		<fo:table table-layout="fixed" keep-with-next="always" keep-together="always">
			<xsl:choose>
				<xsl:when test="$column_width='wide'">
					<fo:table-column column-width="7mm"/>
					<fo:table-column column-width="14mm"/>
					<fo:table-column column-width="14mm"/>
					<fo:table-column column-width="14mm"/>
					<fo:table-column column-width="14mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="13mm"/>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-column column-width="7mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="13mm"/>
					<fo:table-column column-width="12mm"/>
					<fo:table-column column-width="12mm"/>
					<fo:table-column column-width="12mm"/>
					<fo:table-column column-width="12mm"/>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-body>

				<xsl:if test="./ammunition">
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell xsl:use-attribute-sets="weapon.title" text-align="start" number-columns-spanned="6">
							<fo:block font-size="5pt" font-weight="bold">Ammunition: <xsl:value-of select="ammunition/name" />
								<xsl:if test="string(./ammunition/special_properties) != ''">
									(<xsl:value-of select="./ammunition/special_properties" />)
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>

				<fo:table-row keep-with-next.within-column="always">
					<!-- Handedness -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">To Hit</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">1H-P</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">1H-O</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">2H</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">-2W-P-(OH)</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">-2W-P-(OL)</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">-2W-OH</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="5pt" font-weight="bold">Damage</fo:block></fo:table-cell>
				</fo:table-row>
			<xsl:for-each select="range">
				<fo:table-row keep-with-next.within-column="always">
					<!-- Range To-Hits -->
					<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold"><xsl:value-of select="distance"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="basehit"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="tohit_offhand"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="tohit_twohand"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="tohit_2weap_heavy"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="tohit_2weap_light"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="tohit_2weap_offhand"/></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="weapon.hilight"><fo:block font-size="6pt" space-before="1pt"><xsl:value-of select="damage"/></fo:block></fo:table-cell>
				</fo:table-row>
			</xsl:for-each>
				<xsl:if test="./ammunition">
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell xsl:use-attribute-sets="weapon.title"><fo:block font-size="6pt" font-weight="bold">Used:</fo:block></fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="weapon.title" text-align="start" number-columns-spanned="7">
							<fo:block font-size="7pt" font-family="ZapfDingbats">
										<xsl:call-template name="for.loop.inverted">
											<xsl:with-param name="count" select="ammunition/quantity"/>
										</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
	</xsl:template>




	<xsl:template match="armor">
		<xsl:if test="string(.) != ''">
			<fo:table table-layout="fixed" space-before="2mm">
				<fo:table-column column-width="42mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell display-align="center" number-rows-spanned="2" xsl:use-attribute-sets="protection.title">
							<fo:block font-size="10pt" font-weight="bold"><xsl:value-of select="name"/></fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">TYPE</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">ARMOR BONUS</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">MAX DEX BONUS</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="type"/></fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="totalac"/></fo:block>
						</fo:table-cell>
						<fo:table-cell  xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="maxdex"/></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed">
				<fo:table-column column-width="20mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-column column-width="62mm"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">CHECK PENALTY</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">SPELL FAILURE</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection.title">
							<fo:block space-before="1pt" font-size="6pt" font-weight="bold">SPECIAL PROPERTIES</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="accheck"/></fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="spellfail"/></fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="protection">
							<fo:block space-before="1pt" font-size="8pt"><xsl:value-of select="special_properties"/></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:if>
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
			<fo:table-column column-width="55mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="8mm"/>
			<fo:table-column column-width="6mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-header>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="protection.title" padding-top="1pt">
						<fo:block font-size="7pt">
							ARMOR
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="protection.title" padding-top="3pt">
						<fo:block font-size="4pt">
							TYPE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="protection.title"  padding-top="3pt">
						<fo:block font-size="4pt">
							AC
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="protection.title" padding-top="3pt">
						<fo:block font-size="4pt">
							MAXDEX
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="protection.title"  padding-top="3pt">
						<fo:block font-size="4pt">
							CHECK
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="protection.title"  padding-top="3pt">
						<fo:block font-size="4pt">
							SPELL FAILURE
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:for-each select="armor|shield|item">
					<xsl:variable name="content">
						<fo:table-cell><fo:block font-size="8pt"><xsl:value-of select="name"/></fo:block></fo:table-cell>
						<fo:table-cell text-align="center"><fo:block font-size="8pt"><xsl:value-of select="type"/></fo:block></fo:table-cell>
						<fo:table-cell text-align="center"><fo:block font-size="8pt"><xsl:value-of select="totalac"/></fo:block></fo:table-cell>
						<fo:table-cell text-align="center"><fo:block font-size="8pt"><xsl:value-of select="maxdex"/></fo:block></fo:table-cell>
						<fo:table-cell text-align="center"><fo:block font-size="8pt"><xsl:value-of select="accheck"/></fo:block></fo:table-cell>
						<fo:table-cell text-align="center"><fo:block font-size="8pt"><xsl:value-of select="spellfail"/></fo:block></fo:table-cell>
					</xsl:variable>
					<xsl:variable name="special">
						<fo:table-cell number-columns-spanned="6" text-align="center"><fo:block font-size="6pt"><xsl:value-of select="special_properties"/></fo:block></fo:table-cell>
					</xsl:variable>

					<xsl:if test="position() mod 2 = 0">
						<xsl:call-template name="protection.item.darkline">
							<xsl:with-param name="content" select="$content"/>
							<xsl:with-param name="special" select="$special"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="position() mod 2 = 1">
						<xsl:call-template name="protection.item.lightline">
							<xsl:with-param name="content" select="$content"/>
							<xsl:with-param name="special" select="$special"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>



	<xsl:template name="protection.item.darkline">
		<xsl:param name="content"/>
		<xsl:param name="special"/>
		<fo:table-row xsl:use-attribute-sets="protection.darkline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
		<fo:table-row xsl:use-attribute-sets="protection.darkline">
			<xsl:copy-of select="$special"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="protection.item.lightline">
		<xsl:param name="content"/>
		<xsl:param name="special"/>
		<fo:table-row xsl:use-attribute-sets="protection.lightline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
		<fo:table-row xsl:use-attribute-sets="protection.lightline">
			<xsl:copy-of select="$special"/>
		</fo:table-row>
	</xsl:template>




<!--
====================================
====================================
	TEMPLATE - WEAPON PROFICIENCIES
====================================
====================================-->
	<xsl:template match="weapon_proficiencies">
		<!-- BEGIN weapon_proficiencies Table -->
		<fo:table table-layout="fixed" space-before.optimum="2mm">
			<fo:table-column column-width="94mm"/>
			<fo:table-body>
			<fo:table-row keep-with-next.within-column="always">
				<fo:table-cell xsl:use-attribute-sets="proficiencies.title" padding-top="1pt">
					<fo:block font-size="9pt">PROFICIENCIES</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row keep-with-next.within-column="always">
				<fo:table-cell xsl:use-attribute-sets="proficiencies" padding-top="1pt">
					<fo:block font-size="7pt"><xsl:value-of select="."/></fo:block>
				</fo:table-cell>
			</fo:table-row>
			</fo:table-body>
		</fo:table>
		<!-- END weapon_proficiencies Table -->
	</xsl:template>


<!--
====================================
====================================
	TEMPLATE - LANGUAGES
====================================
====================================-->
	<xsl:template match="languages">
		<xsl:if test="string(.) != ''">
			<!-- BEGIN Languages Table -->
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<fo:table-column column-width="94mm"/>
				<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="languages.title" padding-top="1pt">
						<fo:block font-size="9pt">LANGUAGES</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="languages" padding-top="1pt">
						<fo:block font-size="7pt"><xsl:value-of select="."/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				</fo:table-body>
			</fo:table>
			<!-- END Languages Table -->
		</xsl:if>
	</xsl:template>


<!--
====================================
====================================
	TEMPLATE - TEMPLATES
====================================
====================================-->
	<xsl:template match="templates">
		<!-- BEGIN Templates Table -->
		<fo:table table-layout="fixed" space-before.optimum="2mm">
			<fo:table-column column-width="94mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="templates.title" padding-top="1pt" number-columns-spanned="1">
						<fo:block font-size="9pt">
							TEMPLATES
						</fo:block>
					</fo:table-cell>
				</fo:table-row>

				<xsl:for-each select="template">
					<xsl:variable name="content">
						<fo:table-cell padding="1pt">
							<fo:block font-size="7pt"><xsl:value-of select="name"/></fo:block>
						</fo:table-cell>
					</xsl:variable>

					<xsl:if test="position() mod 2 = 0">
						<xsl:call-template name="templates.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
					</xsl:if>
					<xsl:if test="position() mod 2 = 1">
						<xsl:call-template name="templates.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
		<!-- END Templates Table -->
	</xsl:template>

	<xsl:template name="templates.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="templates.darkline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="templates.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="templates.lightline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>



<!--
====================================
====================================
	TEMPLATE - Equipment
====================================
====================================-->
	<xsl:template match="equipment">
		<fo:block>
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<fo:table-column column-width="51mm"/>
				<fo:table-column column-width="19mm"/>
				<fo:table-column column-width="6mm"/>
				<fo:table-column column-width="8mm"/>
				<fo:table-column column-width="10mm"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="equipment.title" padding-top="1pt" number-columns-spanned="5">
							<fo:block font-size="9pt">EQUIPMENT</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row xsl:use-attribute-sets="equipment.title">
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt" >ITEM</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">LOCATION</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt">
							<fo:block font-size="7pt">QTY</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" >
							<fo:block font-size="7pt">WT</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" >
							<fo:block font-size="7pt">COST</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row xsl:use-attribute-sets="equipment.title">
						<fo:table-cell padding-top="1pt" number-columns-spanned="3" >
							<fo:block font-size="7pt">TOTAL WEIGHT CARRIED/VALUE</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" >
							<fo:block font-size="7pt"><xsl:value-of select="total/weight" /></fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="1pt" >
							<fo:block font-size="7pt">
								<xsl:variable name="TotalValue">
									<xsl:call-template name="Total">
										<xsl:with-param name="Items" select="item[contains(type, 'COIN')=false and contains(type, 'GEM')=false]"/>
										<xsl:with-param name="RunningTotal" select="0"/>
									</xsl:call-template>
								</xsl:variable>
								<xsl:value-of select="format-number($TotalValue, '####0.0#')"/> gp
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-footer>
				<fo:table-body>

					<xsl:for-each select="item">
						<xsl:if test="false=contains(type, 'GEM') and false=contains(type, 'VIRTUAL')">
							<xsl:variable name="content">
								<fo:table-cell>
									<fo:block space-before.optimum="1pt" font-size="8pt">
										<xsl:if test="contains(type, 'MAGIC') or contains(type, 'PSIONIC')">
											<xsl:attribute name="font-weight">bold</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="name"/>
									</fo:block>

									<fo:block space-before.optimum="1pt" font-size="5pt">
										<xsl:value-of select="contents" />
									</fo:block>
									<fo:block space-before.optimum="1pt" font-size="5pt">
										<xsl:value-of select="special_properties" />
									</fo:block>
									<fo:block space-before.optimum="1pt" font-size="5pt">
										<xsl:value-of select="note" />
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
									<xsl:if test="contains(type, 'POTION') or contains(type, 'AMMUNITION') or contains(type, 'CONSUMABLE')">
										<fo:block font-size="7pt" font-family="ZapfDingbats">
											<xsl:call-template name="for.loop">
												<xsl:with-param name="count" select="quantity"/>
											</xsl:call-template>
										</fo:block>
									</xsl:if>
								</fo:table-cell>
								<fo:table-cell text-align="center">
									<fo:block space-before.optimum="1pt" font-size="7pt">
										<xsl:value-of select="location" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
										<xsl:value-of select="quantity" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
										<xsl:value-of select="format-number(weight, '####0.0#')" />
										<xsl:if test="quantity &gt; 1">
											(<xsl:value-of select="format-number(weight * quantity, '####0.0#')" />)
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" space-before.optimum="1pt" font-size="7pt">
										<xsl:value-of select="format-number(cost, '####0.0#')" />
										<xsl:if test="quantity &gt; 1">
											(<xsl:value-of select="format-number(cost * quantity, '####0.0#')" />)
										</xsl:if>
									</fo:block>
								</fo:table-cell>
							</xsl:variable>

							<xsl:if test="position() mod 2 = 0">
								<xsl:call-template name="equipment.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
							</xsl:if>
							<xsl:if test="position() mod 2 = 1">
								<xsl:call-template name="equipment.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
			<!-- END Equipment table -->
	</xsl:template>


	<xsl:template name="equipment.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="equipment.darkline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="equipment.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="equipment.lightline">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - WEIGHT ALLOWANCE
====================================
====================================-->
	<xsl:template match="weight_allowance">

		<!-- BEGIN Weight table -->
		<fo:table table-layout="fixed" space-before.optimum="2mm">
			<fo:table-column column-width="20mm"/>
			<fo:table-column column-width="11mm"/>
			<fo:table-column column-width="20mm"/>
			<fo:table-column column-width="11mm"/>
			<fo:table-column column-width="20mm"/>
			<fo:table-column column-width="12mm"/>
			<fo:table-body>
				<fo:table-row  keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="weight.title" padding-top="1pt"  number-columns-spanned="6">
						<fo:block font-size="9pt">WEIGHT ALLOWANCE</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row  keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" padding-right="1mm" xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Light	</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm"  xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="light" /></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-right="1mm" xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Medium</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm" xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="medium" /></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-right="1mm"  xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Heavy</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm"  xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="heavy" /></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" padding-right="1mm" xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Lift over head</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm"  xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="lift_over_head" /></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-right="1mm" xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Lift off ground</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm"  xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="lift_off_ground" /></fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-right="1mm"  xsl:use-attribute-sets="weight.solid">
						<fo:block font-size="7pt" text-align="end">Push / Drag</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-top="1pt" padding-left="1mm" xsl:use-attribute-sets="weight">
						<fo:block font-size="7pt"><xsl:value-of select="push_drag" /></fo:block>
					</fo:table-cell>
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
		<xsl:if test="count (misc/funds/fund|equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]) &gt; 0" >
			<fo:table table-layout="fixed" space-before.optimum="2mm">
				<fo:table-column column-width="94mm"/>
				<fo:table-header>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell xsl:use-attribute-sets="money.title" padding-top="1pt" >
							<fo:block font-size="9pt">MONEY</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-footer>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="money.title" >
							<fo:block font-size="7pt" text-align="end">
								<xsl:variable name="TotalValue">
									<xsl:call-template name="Total">
										<xsl:with-param name="Items" select="equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]"/>
										<xsl:with-param name="RunningTotal" select="0"/>
									</xsl:call-template>
								</xsl:variable>
								Total   = <xsl:value-of select="format-number($TotalValue, '####0.0#')"/> gp
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-footer>
				<fo:table-body>
					<xsl:for-each select="misc/funds/fund|equipment/item[contains(type, 'COIN') or contains(type, 'GEM')]">
						<xsl:variable name="content">
							<fo:table-cell>
								<fo:block font-size="7pt">
									<xsl:choose>
										<xsl:when test="name(.) = 'fund'">
											<xsl:value-of select="." />
										</xsl:when>
										<xsl:otherwise>
											<xsl:if test="contains(type, 'COIN')" >
												<xsl:value-of select="name" />: <xsl:value-of select="quantity" />
											</xsl:if>
											<xsl:if test="contains(type, 'GEM')" >
												<xsl:value-of select="quantity" /> x <xsl:value-of select="name" /> (<xsl:value-of select="cost" />)
											</xsl:if>
										</xsl:otherwise>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
						</xsl:variable>
						<xsl:if test="position() mod 2 = 0">
							<xsl:call-template name="money.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
						<xsl:if test="position() mod 2 = 1">
							<xsl:call-template name="money.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>



	<xsl:template name="money.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="money.darkline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="money.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="money.lightline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>



<!--
====================================
====================================
	TEMPLATE - Misc Magic
====================================
====================================-->
	<xsl:template match="misc/magics">
		<xsl:if test="count(magic) &gt; 0" >
			<fo:table table-layout="fixed" space-before="2mm">
				<fo:table-column column-width="94mm"/>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always" >
						<fo:table-cell xsl:use-attribute-sets="magic.title" padding-top="1pt" >
							<fo:block font-size="9pt">MAGIC</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<xsl:for-each select="magic">
						<xsl:variable name="content">
							<fo:table-cell padding-top="1pt">
								<fo:block font-size="7pt"><xsl:value-of select="."/></fo:block>
							</fo:table-cell>
						</xsl:variable>

						<xsl:if test="position() mod 2 = 0">
							<xsl:call-template name="magic.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
						<xsl:if test="position() mod 2 = 1">
							<xsl:call-template name="magic.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>



	<xsl:template name="magic.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="magic.darkline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="magic.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="magic.lightline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Mutatations
====================================
====================================-->
	<xsl:template match="mutations">
		<xsl:if test="count(mutation) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'mutations'" />
				<xsl:with-param name="title" select="'MUTATION'" />
				<xsl:with-param name="list" select="mutation"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="mutation.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="mutation.darkline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="mutation.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="mutation.lightline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - Special Abilities
====================================
====================================-->
	<xsl:template match="special_abilities">
		<xsl:if test="count(ability) &gt; 0" >
			<fo:table table-layout="fixed" space-before="2mm">
				<fo:table-column column-width="30mm"/>
				<fo:table-column column-width="64mm"/>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell xsl:use-attribute-sets="special_abilities.title" padding-top="1pt" number-columns-spanned="2">
							<fo:block font-size="9pt">TALENTS / SPECIAL ABILITIES</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<xsl:for-each select="ability">
						<xsl:variable name="content">
							<fo:table-cell number-columns-spanned="2" padding="1pt">
								<fo:block font-size="7pt"><xsl:value-of select="name" /></fo:block>
							</fo:table-cell>
						</xsl:variable>
						<xsl:if test="position() mod 2 = 0">
							<xsl:call-template name="special_abilities.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
						<xsl:if test="position() mod 2 = 1">
							<xsl:call-template name="special_abilities.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>


	<xsl:template name="special_abilities.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="special_abilities.darkline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="special_abilities.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="special_abilities.lightline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

<!--
====================================
====================================
	TEMPLATE - STARTING OCCUPATION
====================================
====================================-->
	<xsl:template match="feat" mode="starting_occupation">
	<xsl:if test="string-length(type)=10">
	<!-- the string-lenght test was added because the selection from apply-templates cannot differentiate between
		the TYPEs occupation and occupation_skill  -->
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="94mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="occupation.title" padding-top="1pt">
						<fo:block font-size="9pt">STARTING OCCUPATION</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="occupation" padding-top="1pt">
						<fo:block font-size="7pt"><xsl:value-of select="name"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:if>
	</xsl:template>



	<!--
====================================
====================================
	TEMPLATE - Talents
====================================
====================================-->
	<xsl:template match="talents">
		<xsl:if test="count(talent) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'talents'" />
				<xsl:with-param name="title" select="'Talents'" />
				<xsl:with-param name="list" select="talent"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - Occupations
====================================
====================================-->
	<xsl:template match="occupations">
		<xsl:if test="count(occupation) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'occupations'" />
				<xsl:with-param name="title" select="'Occupations'" />
				<xsl:with-param name="list" select="occupation"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


<!--
====================================
====================================
	TEMPLATE - Allegiances
====================================
====================================-->
	<xsl:template name="allegiances">
		<fo:table table-layout="fixed" space-before="2mm">
			<fo:table-column column-width="94mm"/>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell xsl:use-attribute-sets="allegiances.title" padding-top="1pt">
						<fo:block font-size="9pt">ALLEGIANCES</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always" height="8mm">
					<fo:table-cell xsl:use-attribute-sets="allegiances" padding-top="1pt">
						<fo:block font-size="9pt"></fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>



<!--
====================================
====================================
	TEMPLATE - FEATS
====================================
====================================-->
	<xsl:template match="feats">
		<xsl:if test="count(feat[hidden != 'T' and name != '']) &gt; 0" >
			<fo:table table-layout="fixed" space-before="2mm">
				<fo:table-column column-width="34mm"/>
				<fo:table-column column-width="60mm"/>
				<fo:table-body>
					<fo:table-row keep-with-next.within-column="always">
						<fo:table-cell xsl:use-attribute-sets="feats.title" padding-top="1pt" number-columns-spanned="2">
							<fo:block font-size="9pt">FEATS</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<xsl:for-each select="feat[hidden != 'T' and name != '']">
						<xsl:sort select="name" />
						<xsl:variable name="content">
							<fo:table-cell padding="1pt">
								<fo:block font-size="7pt"><xsl:value-of select="name"/></fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1pt">
								<fo:block font-size="7pt" text-align="justify"><xsl:value-of select="description"/></fo:block>
							</fo:table-cell>
						</xsl:variable>
						<xsl:if test="position() mod 2 = 0">
							<xsl:call-template name="feats.darkline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
						<xsl:if test="position() mod 2 = 1">
							<xsl:call-template name="feats.lightline"><xsl:with-param name="content" select="$content"/></xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>



	<xsl:template name="feats.darkline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="feats.darkline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="feats.lightline">
		<xsl:param name="content"/>
		<fo:table-row xsl:use-attribute-sets="feats.lightline" keep-with-next.within-column="always">
			<xsl:copy-of select="$content"/>
		</fo:table-row>
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
		<xsl:apply-templates select="psicrystal"/>
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
	<xsl:template match="psicrystal">
		<!-- BEGIN Psicrystal Table -->
		<xsl:call-template name="show_companion">
			<xsl:with-param name="followerType" select="'Psicrystal'"/>
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
												<xsl:message>Test</xsl:message>
					<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions.title'"/>
							</xsl:call-template>
							<fo:block font-size="10pt" font-weight="bold">Followers: </fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
						<fo:table-cell>
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions'"/>
							</xsl:call-template>
							<xsl:for-each select="follower">
								<fo:block font-size="8pt">
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
		<fo:table table-layout="fixed" space-before.optimum="2mm" keep-together="always">
				<fo:table-column>
					<xsl:attribute name="column-width"><xsl:value-of select="0.5 * ($pagePrintableWidth - 2) - 69" />mm</xsl:attribute>
				</fo:table-column>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="14mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="14mm"/>
			<fo:table-body keep-together="always">
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell number-columns-spanned="6">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="10pt" font-weight="bold">
							<xsl:value-of select="$followerType"/>: <xsl:value-of select="name"/> (<xsl:value-of select="race"/>)</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">HP:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="hp"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">AC:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="ac"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">INIT:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="initiative_mod"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">FORT:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="fortitude"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">REF:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="reflex"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="end">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">WILL:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">
							<xsl:value-of select="will"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="attacks/attack">
					<xsl:if test="string-length(common/name/long) &gt; 0">
						<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:variable name="name" select="substring-before(common/name/long,'(')"/>
									<xsl:variable name="description" select="substring-after(common/name/long,'(')"/>
									<xsl:value-of select="$name"/>
									<xsl:if test="string-length($name) = 0">
										<xsl:value-of select="common/name/long"/>
									</xsl:if>
									<xsl:if test="string-length($description) &gt; 0">
										<fo:inline font-size="5pt">
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
								<fo:block font-size="8pt">
									<xsl:value-of select="simple/to_hit"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">DAM:</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="simple/damage"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="end">
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions.title'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">CRIT:</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<xsl:call-template name="attrib">
									<xsl:with-param name="attribute" select="'companions'"/>
								</xsl:call-template>
								<fo:block font-size="8pt">
									<xsl:value-of select="common/critical/range"/>/x<xsl:value-of select="common/critical/multiplier"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
				<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
					<fo:table-cell text-align="left">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions.title'"/>
						</xsl:call-template>
						<fo:block font-size="8pt">Special:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="5">
						<xsl:call-template name="attrib">
							<xsl:with-param name="attribute" select="'companions'"/>
						</xsl:call-template>
						<fo:block font-size="7pt" text-align="left">
							<xsl:value-of select="special_properties"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:if test="count(companion/trick) &gt; 0">
					<fo:table-row keep-with-next.within-column="always">
											<xsl:message>Test</xsl:message>
						<fo:table-cell text-align="left">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions.title'"/>
							</xsl:call-template>
							<fo:block font-size="8pt" text-align="left">Tricks:</fo:block>
						</fo:table-cell>
						<fo:table-cell number-columns-spanned="5">
							<xsl:call-template name="attrib">
								<xsl:with-param name="attribute" select="'companions'"/>
							</xsl:call-template>
							<fo:block font-size="7pt">
								<xsl:value-of select="trick"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
			</fo:table-body>
		</fo:table>
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
			<fo:page-sequence master-reference="Portrait">
				<xsl:call-template name="page.footer" />
				<fo:flow flow-name="body">
					<xsl:apply-templates select="spells_innate/racial_innate" />
					<xsl:apply-templates select="spells_innate/class_innate" />
					<xsl:apply-templates select="known_spells" />
					<xsl:apply-templates select="memorized_spells" />
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
				<fo:table >
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
						<xsl:with-param name="title" select="concat(@name, ' Innate Spells')"/>
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
			<fo:block break-before="page" />
			<fo:table >
				<xsl:variable name="titletext">
					<xsl:choose>
						<xsl:when test="@spellcastertype = 'Psionic'">
							<xsl:value-of select="concat(@spelllistclass, ' Powers')" />
						</xsl:when>
						<xsl:otherwise><xsl:value-of select="concat(@spelllistclass, ' Spells')" /></xsl:otherwise>
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
						<xsl:otherwise></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:call-template name="spells.known.header.row">
					<xsl:with-param name="columnOne" select="$columnOneTitle"/>
					<xsl:with-param name="title" select="$titletext"/>
					<xsl:with-param name="details" select="'false'"/>
				</xsl:call-template>
				<fo:table-body>
					<fo:table-row height="2mm" >
						<fo:table-cell/>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="11">
							<xsl:apply-templates select="." mode="spell.level.table"/>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="2mm" >
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




	<xsl:template match="class" mode="spell.level.table">
		<fo:table table-layout="fixed" border-collapse="collapse">
			<fo:table-column column-width="40mm" />
			<fo:table-column column-width="20mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="9mm" />
			<fo:table-column column-width="40mm" />
			<fo:table-body>
				<xsl:apply-templates select="." mode="spell.level.count"/>
				<xsl:apply-templates select="." mode="spell.level.known"/>
				<xsl:apply-templates select="." mode="spell.level.cast"/>
			</fo:table-body>
		</fo:table>
	</xsl:template>



	<xsl:template match="class" mode="spell.level.count">
		<fo:table-row keep-with-next.within-column="always">
			<fo:table-cell />
			<fo:table-cell xsl:use-attribute-sets="spelllist.known.header">
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="2pt" space-after="1pt">LEVEL</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell xsl:use-attribute-sets="spelllist.known.header centre">
					<fo:block space-before="2pt" space-after="1pt" font-size="6pt">
						<xsl:value-of select="@number"/>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell />
		</fo:table-row>
	</xsl:template>

	<xsl:template match="class" mode="spell.level.known">
		<fo:table-row keep-with-next.within-column="always">
			<fo:table-cell />
			<fo:table-cell  xsl:use-attribute-sets="spelllist.known.header">
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="2pt" space-after="1pt">KNOWN</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell  xsl:use-attribute-sets="spelllist.known.known">
					<fo:block font-size="6pt" space-before="2pt" space-after="1pt">
						<xsl:value-of select="@known"/>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell />
		</fo:table-row>
	</xsl:template>



	<xsl:template match="class" mode="spell.level.cast">
		<fo:table-row padding-bottom="2mm">
			<fo:table-cell />
			<fo:table-cell  xsl:use-attribute-sets="spelllist.known.header">
				<fo:block font-size="6pt" font-weight="bold" space-start="2pt" space-before="2pt" space-after="1pt">PER DAY</fo:block>
			</fo:table-cell>
			<xsl:for-each select="level">
				<fo:table-cell xsl:use-attribute-sets="spelllist.known.perday">
					<fo:block font-size="6pt" space-before="2pt" space-after="1pt">
						<xsl:value-of select="@cast"/>
					</fo:block>
				</fo:table-cell>
			</xsl:for-each>
			<fo:table-cell />
		</fo:table-row>
	</xsl:template>



<!--
====================================
====================================
	TEMPLATE - KNOWN SPELL LEVEL
====================================
====================================-->
	<xsl:template match="level" mode="known.spells">
		<xsl:param name="columnOne" select="'Boxes'" />
		<xsl:param name="columnOneTitle" select="''" />

		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table-row keep-with-next.within-column="always">
				<fo:table-cell number-columns-spanned="11" padding-top="1pt" xsl:use-attribute-sets="spelllist.header">
					<fo:block font-size="12pt">
						LEVEL <xsl:value-of select="@number"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<xsl:call-template name="spells.header.column.titles">
				<xsl:with-param name="columnOne" select="$columnOneTitle"/>
			</xsl:call-template>
			<xsl:apply-templates select="spell" mode="details">
				<xsl:with-param name="columnOne" select="$columnOne"/>
			</xsl:apply-templates>
			<fo:table-row height="1mm">
				<fo:table-cell/>
			</fo:table-row>
		</xsl:if>
	</xsl:template>




<!--
====================================
====================================
	TEMPLATE - KNOWN SPELL HEADER ROW
====================================
====================================-->
	<xsl:template name="spells.known.header.row">
		<xsl:param name="title" select="''"/>
		<xsl:param name="columnOne" select="''"/>
		<xsl:param name="details" select="'true'" />
		<fo:table-column column-width="11mm" />
		<fo:table-column column-width="37mm" /><!-- name -->
		<fo:table-column column-width="6mm" /><!-- dc -->
		<fo:table-column column-width="18mm" /><!-- saving throw -->
		<fo:table-column column-width="8mm" /><!-- time -->
		<fo:table-column column-width="32mm" /><!-- duration -->
		<fo:table-column column-width="16mm" /><!-- range -->
		<fo:table-column column-width="9mm" /><!-- comp -->
		<fo:table-column column-width="18mm" /><!-- SR -->
		<fo:table-column column-width="15mm" /><!-- school -->
		<fo:table-column column-width="20mm" /><!-- source -->
		<fo:table-header>
			<fo:table-row>
				<fo:table-cell number-columns-spanned="11" xsl:use-attribute-sets="spelllist.header" padding-top="1pt">
					<fo:block font-size="12pt">
						<xsl:value-of select="$title"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<xsl:if test="$details = 'true'">
				<xsl:call-template name="spells.header.column.titles">
					<xsl:with-param name="columnOne" select="$columnOne" />
				</xsl:call-template>
			</xsl:if>
		</fo:table-header>
		<fo:table-footer>
			<fo:table-row>
				<fo:table-cell number-columns-spanned="11" xsl:use-attribute-sets="spelllist.footer" padding-top="1pt" >
					<fo:block font-size="5pt">* =Domain/Speciality Spell
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
		<fo:table-row xsl:use-attribute-sets="spelllist.levelheader" keep-with-next.within-column="always">
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold"><xsl:value-of select="$columnOne"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Name</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">DC</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Saving Throw</fo:block>
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
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Comp.</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Spell Resistance</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">School</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt" font-weight="bold">Source</fo:block>
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
		<xsl:variable name="row1">
			<xsl:choose>
				<xsl:when test="$columnOne = 'Times'">
					<xsl:choose>
						<xsl:when test="times_memorized &gt;= 0">
							<fo:table-cell padding-top="0pt">
								<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
									<xsl:call-template name="for.loop">
										<xsl:with-param name="count" select="times_memorized" />
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
						</xsl:when>
						<xsl:otherwise>
							<fo:table-cell padding-top="1pt" text-align="start" >
								<fo:block font-size="7pt">At Will</fo:block>
							</fo:table-cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$columnOne = 'Boxes'">
					<fo:table-cell padding-top="0pt">
						<fo:block text-align="start" font-size="8pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="5" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:when test="$columnOne = 'PowerPoints'">
					<fo:table-cell padding-top="0pt">
						<fo:block font-size="8pt" text-align="start" >
							<xsl:variable name="ppcount" select="((../@number)*2)-1"/>
							<xsl:choose>
								<xsl:when test="number($ppcount) &gt; 0"><xsl:value-of select="$ppcount"/></xsl:when>
								<xsl:otherwise>0/1</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
			</xsl:choose>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="7pt">
					<xsl:value-of select="bonusspell"/> <xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="dc"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="saveinfo"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="castingtime"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="duration"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="range"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="components"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="spell_resistance"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt"><xsl:value-of select="school/fullname"/></fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt">
				<fo:block text-align="start" font-size="5pt">
					<xsl:value-of select="source/sourceshort"/>
					<xsl:text>: </xsl:text>
					<xsl:value-of select="source/sourcepage"/>
				</fo:block>
			</fo:table-cell>
		</xsl:variable>
		<xsl:variable name="row2">
			<fo:table-cell padding-top="1pt" />
			<fo:table-cell padding-top="1pt" number-columns-spanned="5">
				<fo:block text-align="start" font-size="5pt">
					<fo:inline font-style="italic">Effect: </fo:inline><xsl:value-of select="effect"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding-top="1pt" number-columns-spanned="5">
				<fo:block text-align="start" font-size="5pt">
					<fo:inline font-style="italic">Target: </fo:inline><xsl:value-of select="target"/>
				</fo:block>
			</fo:table-cell>
		</xsl:variable>
		<xsl:if test="position() mod 2 = 0">
			<xsl:call-template name="spelllist.darkline">
				<xsl:with-param name="row1" select="$row1"/>
				<xsl:with-param name="row2" select="$row2"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="position() mod 2 = 1">
			<xsl:call-template name="spelllist.lightline">
				<xsl:with-param name="row1" select="$row1"/>
				<xsl:with-param name="row2" select="$row2"/>
			</xsl:call-template>
		</xsl:if>

	</xsl:template>

	<xsl:template name="spelllist.darkline">
		<xsl:param name="row1"/>
		<xsl:param name="row2"/>
		<fo:table-row xsl:use-attribute-sets="spelllist.darkline" >
			<xsl:copy-of select="$row1"/>
		</fo:table-row>
		<fo:table-row xsl:use-attribute-sets="spelllist.darkline" >
			<xsl:copy-of select="$row2"/>
		</fo:table-row>
	</xsl:template>

	<xsl:template name="spelllist.lightline">
		<xsl:param name="row1"/>
		<xsl:param name="row2"/>
		<fo:table-row xsl:use-attribute-sets="spelllist.lightline" >
			<xsl:copy-of select="$row1"/>
		</fo:table-row>
		<fo:table-row xsl:use-attribute-sets="spelllist.lightline" >
			<xsl:copy-of select="$row2"/>
		</fo:table-row>
	</xsl:template>


	<xsl:template match="memorized_spells">
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:block break-before="page">
				<xsl:apply-templates mode="spells.memorized"/>
			</fo:block>
		</xsl:if>
	</xsl:template>



	<xsl:template name="spells.memorized.header">
		<xsl:param name="title" select="'Unknown'"/>
		<fo:table >
			<fo:table-column column-width="190mm"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="spells.memorized.header" padding-top="1pt" >
						<fo:block font-size="12pt">
							<xsl:value-of select="$title"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template match="racial_innate_memorized" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:call-template name="spells.memorized.header">
				<xsl:with-param name="title" select="'Innate'"/>
			</xsl:call-template>
			<fo:table table-layout="fixed" space-after="5mm">
				<fo:table-column column-width="8mm"/>
				<fo:table-column column-width="30mm"/>
				<fo:table-body>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="class_innate_memorized" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:apply-templates mode="spells.memorized.innate"/>
		</xsl:if>
	</xsl:template>


	<xsl:template match="spellbook" mode="spells.memorized.innate">
		<xsl:if test="count(.//spell) &gt; 0">
			<xsl:call-template name="spells.memorized.header">
				<xsl:with-param name="title" select="concat(@name, ' Innate Spells')"/>
			</xsl:call-template>
			<fo:table >
				<fo:table-column column-width="8mm"/>
				<fo:table-column column-width="30mm"/>
				<fo:table-body>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>


	<xsl:template match="spellbook" mode="spells.memorized">
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table table-layout="fixed" space-before="4mm">
				<fo:table-column column-width="38mm"/>
				<fo:table-column column-width="38mm"/>
				<fo:table-column column-width="38mm"/>
				<fo:table-column column-width="38mm"/>
				<fo:table-column column-width="38mm"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="spells.memorized.header" padding-top="1pt" number-columns-spanned="5">
							<fo:block font-size="10pt">
								Spellbook: <xsl:value-of select="@name"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<xsl:apply-templates mode="spells.memorized"/>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>


	<xsl:template match="class" mode="spells.memorized" >
		<xsl:if test="count(.//spell) &gt; 0">
			<fo:table-row>
				<fo:table-cell xsl:use-attribute-sets="spells.memorized.header" padding-top="1pt" number-columns-spanned="5">
					<fo:block font-size="8pt">
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


	<xsl:template match="level" mode="spells.memorized">
		<fo:table-cell padding-top="1pt">
			<fo:block font-size="5pt">
				<xsl:if test="count(.//spell) &gt; 0">
					<fo:table >
						<fo:table-column column-width="8mm"/>
						<fo:table-column column-width="30mm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets="spells.memorized.level" padding-top="1pt" number-columns-spanned="2">
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



	<xsl:template match="spell" mode="spells.memorized">
		<fo:table-row>
			<xsl:choose>
				<xsl:when test="times_memorized &gt;= 0">
					<fo:table-cell padding-top="0pt" text-align="end" xsl:use-attribute-sets="spells.memorized">
						<fo:block font-size="7pt" font-family="ZapfDingbats">
							<xsl:call-template name="for.loop">
								<xsl:with-param name="count" select="times_memorized" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:otherwise>
					<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="spells.memorized">
						<fo:block font-size="6pt">At Will</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
			<fo:table-cell>
				<fo:block font-size="7pt" xsl:use-attribute-sets="spells.memorized">
					<xsl:value-of select="bonusspell"/> <xsl:value-of select="name"/> (DC:<xsl:value-of select="dc"/>)
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
	<xsl:template match="basics" mode="bio">
		<!-- BEGIN BIO Pages -->
		<xsl:if test="string-length(translate(normalize-space(concat(description,bio)), ' ', '')) &gt; 0">
			<fo:page-sequence master-reference="Portrait">
				<xsl:call-template name="page.footer" />
				<fo:flow flow-name="body">
					<fo:block font-size="14pt" xsl:use-attribute-sets="bio" break-before="page" span="all">
						<xsl:value-of select="name" />
						<xsl:if test="string-length(followerof) &gt; 0" >- <xsl:value-of select="followerof" /></xsl:if>
					</fo:block>
					<fo:block>
						<fo:table >
							<fo:table-column column-width="94mm" />
							<xsl:if test="string-length(portrait) &gt; 0">
								<fo:table-column column-width="2mm" />
								<fo:table-column column-width="94mm" />
							</xsl:if>
							<fo:table-body>

								<fo:table-row>
									<xsl:if test="string-length(portrait) &gt; 0">
										<fo:table-cell display-align="center" xsl:use-attribute-sets="picture" number-rows-spanned="36">
											<fo:block start-indent="1mm" height="100mm">
												<xsl:variable name="portrait_file" select="portrait" />
												<fo:external-graphic src="file:{$portrait_file}" width="92mm" scaling="uniform" />
											</fo:block>
										</fo:table-cell>
										<fo:table-cell number-rows-spanned="36" />
									</xsl:if>
									<fo:table-cell>
										<fo:block xsl:use-attribute-sets="bio" font-size="9pt"><xsl:value-of select="race" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title" >
										<fo:block font-size="6pt">RACE</fo:block>
									</fo:table-cell>
								</fo:table-row>


								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="age" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">AGE</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="gender/long" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">GENDER</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="vision/all" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">VISION</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="alignment/long" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">ALIGNMENT</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="handed" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">DOMINANT HAND</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="height/total" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="6pt">HEIGHT</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="weight/weight_unit" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">WEIGHT</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="eyes/color" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">EYE COLOR</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="skin/color" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">SKIN COLOR</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt">
											<xsl:value-of select="hair/color" />
											<xsl:value-of select="hair/length" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">HAIR</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt"><xsl:value-of select="phobias" /></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											PHOBIAS
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt">
											<xsl:for-each select="personality/trait">
												<xsl:if test="position() &gt; 0">,</xsl:if>
												<xsl:value-of select="." />
											</xsl:for-each>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											PERSONALITY TRAITS
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt">
											<xsl:value-of select="interests" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											INTERESTS
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt" color="black">
											<xsl:value-of select="speechtendency" />, <xsl:value-of select="catchphrase" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											SPOKEN STYLE
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt" >
											<xsl:value-of select="residence" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											RESIDENCE
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt" >
											<xsl:value-of select="location" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											LOCATION
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell padding-top="1pt" height="9pt" xsl:use-attribute-sets="bio">
										<fo:block font-size="9pt">
											<xsl:value-of select="region" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding-top="1pt" xsl:use-attribute-sets="bio.title">
										<fo:block font-size="6pt">
											REGION
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>

					<fo:block font-size="14pt" font-weight="bold" space-before="5mm" span="all">
						Description:
					</fo:block>
					<xsl:for-each select="description/para">
						<fo:block font-size="9pt" text-indent="5mm" space-after.optimum="2mm"  span="all">
							<xsl:value-of select="." />
						</fo:block>
					</xsl:for-each>

					<fo:block font-size="14pt" font-weight="bold" span="all">
						Biography:
					</fo:block>
					<xsl:for-each select="bio/para">
						<fo:block font-size="9pt" text-indent="5mm" space-after.optimum="5mm" span="all">
							<xsl:value-of select="." />
						</fo:block>
					</xsl:for-each>
				</fo:flow>
			</fo:page-sequence>
		</xsl:if>
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
			<fo:page-sequence master-reference="Portrait">
				<xsl:call-template name="page.footer" />
				<fo:flow flow-name="body">
					<fo:block font-size="14pt" font-weight="bold" space-after.optimum="2mm" break-before="page" span="all">
						Notes:
					</fo:block>
					<xsl:for-each select="note">
						<xsl:if test="not(name = 'DM Notes')">
							<fo:block font-size="12pt" space-after.optimum="2mm" space-before.optimum="5mm">
								<xsl:value-of select="name" />:
							</fo:block>
							<xsl:for-each select="value/para">
								<fo:block font-size="9pt" text-indent="5mm">
									<xsl:value-of select="." />
								</fo:block>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</fo:flow>
			</fo:page-sequence>
		</xsl:if>
		<!-- END CHARACTER NOTES Pages -->
	</xsl:template>

	<!--
====================================
====================================
	TEMPLATE - BOLD LIST
====================================
====================================-->
	<xsl:template name="bold.list">
		<xsl:param name="attribute"/>
		<xsl:param name="title" />
		<xsl:param name="list" />
		<xsl:param name="name.tag" />
		<xsl:param name="desc.tag" select="''" />
		<fo:table table-layout="fixed" space-before="2mm" border-collapse="collapse" padding="0.5pt">
			<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.border')"/></xsl:call-template>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-column>
			    <xsl:attribute name="column-width"><xsl:value-of select="($pagePrintableWidth - 2) div 6" />mm</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<fo:table-row keep-with-next.within-column="always">
					<fo:table-cell padding-top="1pt" number-columns-spanned="3">
						<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.title')"/></xsl:call-template>
						<fo:block font-size="9pt"><xsl:value-of select="$title"/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<xsl:for-each select="$list">
					<xsl:variable name="shade">
						<xsl:choose>
							<xsl:when test="position() mod 2 = 0">darkline</xsl:when>
							<xsl:otherwise>lightline</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="string-length(./*[name()=$name.tag]) &gt; 1">
						<fo:table-row keep-with-next.within-column="always">
							<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
							<xsl:choose>
								<xsl:when test="source!=''">
									<fo:table-cell padding="0pt" number-columns-spanned="2">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
										<fo:block font-size="7pt" font-weight="bold"><xsl:value-of select="./*[name()=$name.tag]"/></fo:block>
									</fo:table-cell>
									<fo:table-cell padding="0pt" text-align="end">
										<fo:block  font-size="7pt" font-weight="bold">[<xsl:value-of select="source"/>]</fo:block>
									</fo:table-cell>
								</xsl:when>
								<xsl:otherwise>
									<fo:table-cell number-columns-spanned="3" padding="0pt">
										<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
										<fo:block font-size="7pt" font-weight="bold"><xsl:value-of select="./*[name()=$name.tag]"/></fo:block>
									</fo:table-cell>
								</xsl:otherwise>
							</xsl:choose>
						</fo:table-row>
						<xsl:if test="$desc.tag!=''">
							<fo:table-row keep-with-next.within-column="always">
								<xsl:call-template name="attrib"><xsl:with-param name="attribute" select="concat($attribute, '.', $shade)"/></xsl:call-template>
								<fo:table-cell padding="1pt" number-columns-spanned="3">
									<fo:block font-size="7pt" text-align="justify" text-indent="5pt">
										<xsl:call-template name="paragraghlist">
											<xsl:with-param name="tag" select="$desc.tag"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>

</xsl:stylesheet>

