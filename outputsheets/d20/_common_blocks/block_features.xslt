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
	TEMPLATE - Racial Traits
====================================
====================================-->
	<xsl:template match="racial_traits">
		<xsl:if test="count(racial_trait) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'racial_traits'" />
				<xsl:with-param name="title" select="'Racial Trait'" />
				<xsl:with-param name="list" select="racial_trait"/>
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
	<xsl:template match="class_features">
		<xsl:if test="count(class_feature) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'class_features'" />
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
				<xsl:with-param name="title" select="'Disadvantages'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'"/>
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
				<xsl:with-param name="attribute" select="'special_qualities'" />
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
	TEMPLATE - Demon Cants
====================================
====================================-->
	<xsl:template match="demon_cants">
		<xsl:if test="count(demon_cant) &gt; 0">
			<xsl:call-template name="bold.list">
				<xsl:with-param name="attribute" select="'special_qualities'" />
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
				<xsl:with-param name="attribute" select="'special_qualities'" />
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
				<xsl:with-param name="attribute" select="'special_qualities'" />
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
				<xsl:with-param name="attribute" select="'special_qualities'" />
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
				<xsl:with-param name="title" select="'Class Feature Powers'" />
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
				<xsl:with-param name="title" select="'Feat Powers'" />
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
