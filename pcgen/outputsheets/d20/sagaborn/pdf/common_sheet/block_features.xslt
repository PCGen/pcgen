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
	TEMPLATE - Race Features
====================================
====================================-->
	<xsl:template match="race_features">
		<xsl:if test="count(race_feature) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Race Features'" />
				<xsl:with-param name="list" select="race_feature"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Class Features
====================================
====================================-->
	<xsl:template match="class_features">
		<xsl:if test="count(class_feature) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Class Features'" />
				<xsl:with-param name="list" select="class_feature"/>
				<xsl:with-param name="name.tag" select="'name'"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Personality Traits
====================================
====================================-->
	<xsl:template match="personality_traits">
		<xsl:if test="count(personality_trait) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Personality Traits'" />
				<xsl:with-param name="list" select="personality_trait"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Bond
====================================
====================================-->
	<xsl:template match="bonds">
		<xsl:if test="count(bond) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Bonds'" />
				<xsl:with-param name="list" select="bond"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Ideal
====================================
====================================-->
	<xsl:template match="ideals">
		<xsl:if test="count(ideal) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Ideals'" />
				<xsl:with-param name="list" select="ideal"/>
				<xsl:with-param name="desc.tag" select="'description'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
====================================
====================================
	TEMPLATE - Flaw
====================================
====================================-->
	<xsl:template match="flaws">
		<xsl:if test="count(flaw) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
				<xsl:with-param name="title" select="'Flaws'" />
				<xsl:with-param name="list" select="flaw"/>
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
	TEMPLATE - TALENTS
====================================
====================================-->
	<xsl:template match="talents">
		<xsl:if test="count(talent) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="benefit.tag" select="'benefit'"/>

			</xsl:call-template>
		</xsl:if>
	</xsl:template>




</xsl:stylesheet>