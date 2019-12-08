<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:myAttribs="my:Attribs"
	exclude-result-prefixes="myAttribs"
	>
	<xsl:import href="fantasy_master_spell_list_only.xslt"/>
	<xsl:output indent="yes"/>

	<myAttribs:myAttribs>
		<border border-width="0.5pt" border-style="solid" />
		<centre text-align="center" />
		<border.temp border-width="2pt" border-style="solid" border-color="lightgrey"><subattrib centre=""/></border.temp>

		<normal color="black" background-color="white" border-color="black"/>
		<light color="black" background-color="white" border-color="black"/>
		<medium color="black" background-color="lightgrey" border-color="black"/>
		<dark color="black" background-color="lightgrey" border-color="black"/>
		<very.dark color="black" background-color="white" border-color="black"/>
		<inverse color="black" background-color="white" border-color="black"/>

		<bio display-align="after" color="black" background-color="transparent" border-color="black"></bio>
		<bio.title border-top-width="0.5pt" border-top-style="solid"><subattrib normal=""/></bio.title>

		<picture><subattrib normal="" border=""/></picture>

		<stat.title><subattrib border="" centre="" inverse="" /></stat.title>
		<stat.score><subattrib border="" centre="" light="" /></stat.score>
		<stat.modifier><subattrib stat.score="" /></stat.modifier>
		<stat.base.score><subattrib border="" centre="" normal="" /></stat.base.score>
		<stat.base.modifier><subattrib stat.base.score="" /></stat.base.modifier>
		<stat.temp.score color="lightgrey"><subattrib centre="" border.temp=""/></stat.temp.score>
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
		<protection.border><subattrib border="" inverse=""/></protection.border>
		<protection.darkline><subattrib  centre="" medium="" /></protection.darkline>
		<protection.lightline><subattrib  centre="" light="" /></protection.lightline>

		<rage.title><subattrib  centre="" inverse=""/></rage.title>
		<rage.border><subattrib border="" inverse=""/></rage.border>
		<rage><subattrib normal=""/></rage>

		<checklist.title><subattrib  centre="" inverse=""/></checklist.title>
		<checklist.border><subattrib border="" inverse=""/></checklist.border>
		<checklist><subattrib normal=""/></checklist>

		<wildshape.title><subattrib centre="" inverse=""/></wildshape.title>
		<wildshape.border><subattrib border="" inverse=""/></wildshape.border>
		<wildshape><subattrib normal=""/></wildshape>

		<bard.title><subattrib centre="" inverse=""/></bard.title>
		<bard.border><subattrib border="" inverse=""/></bard.border>
		<bard><subattrib  normal=""/></bard>

		<psionics.title><subattrib  centre="" inverse=""/></psionics.title>
		<psionics.border><subattrib border="" inverse=""/></psionics.border>
		<psionics><subattrib border="" centre="" normal=""/></psionics>

		<turning.title><subattrib centre="" inverse=""/></turning.title>
		<turning.border><subattrib border="" inverse=""/></turning.border>
		<turning><subattrib  centre="" normal=""/></turning>
		<turning.lightline><subattrib centre="" light=""/></turning.lightline>
		<turning.darkline><subattrib centre="" medium=""/></turning.darkline>

		<stunningfist.title><subattrib centre="" inverse=""/></stunningfist.title>
		<stunningfist.border><subattrib border="" inverse=""/></stunningfist.border>
		<stunningfist><subattrib normal=""/></stunningfist>

		<wholeness.title><subattrib  centre="" inverse=""/></wholeness.title>
		<wholeness.border><subattrib border="" inverse=""/></wholeness.border>
		<wholeness><subattrib  normal=""/></wholeness>

		<layonhands.title><subattrib centre="" inverse=""/></layonhands.title>
		<layonhands.border><subattrib border="" inverse=""/></layonhands.border>
		<layonhands><subattrib  normal=""/></layonhands>

		<domains.title><subattrib  centre="" inverse=""/></domains.title>
		<domains.border><subattrib border="" inverse=""/></domains.border>
		<domains.lightline><subattrib  light=""/></domains.lightline>
		<domains.darkline><subattrib  medium=""/></domains.darkline>

		<proficiencies.title><subattrib centre="" inverse=""/></proficiencies.title>
		<proficiencies.border><subattrib border="" inverse=""/></proficiencies.border>
		<proficiencies><subattrib centre="" normal=""/></proficiencies>

		<prohibited.title><subattrib centre="" inverse=""/></prohibited.title>
		<prohibited.border><subattrib border="" inverse=""/></prohibited.border>
		<prohibited><subattrib centre="" normal=""/></prohibited>

		<languages.title><subattrib centre="" inverse=""/></languages.title>
		<languages.border><subattrib border="" inverse=""/></languages.border>
		<languages><subattrib  centre="" normal=""/></languages>

		<templates.title><subattrib centre="" inverse=""/></templates.title>
		<templates.border><subattrib border="" inverse=""/></templates.border>
		<templates.lightline><subattrib light=""/></templates.lightline>
		<templates.darkline><subattrib medium=""/></templates.darkline>

		<companions.title><subattrib border="" centre="" inverse=""/></companions.title>
		<companions><subattrib border="" centre="" normal=""/></companions>

		<equipment.title><subattrib centre="" inverse=""/></equipment.title>
		<equipment.border><subattrib border="" inverse=""/></equipment.border>
		<equipment.lightline><subattrib light=""/></equipment.lightline>
		<equipment.darkline><subattrib medium=""/></equipment.darkline>

		<weight.title><subattrib centre="" inverse=""/></weight.title>
		<weight.border><subattrib border="" inverse=""/></weight.border>
		<weight.lightline><subattrib light=""/></weight.lightline>
		<weight.darkline><subattrib  medium=""/></weight.darkline>

		<money.title><subattrib  centre="" inverse=""/></money.title>
		<money.border><subattrib border="" inverse=""/></money.border>
		<money.lightline><subattrib light=""/></money.lightline>
		<money.darkline><subattrib medium=""/></money.darkline>

		<magic.title><subattrib centre="" inverse=""/></magic.title>
		<magic.border><subattrib border="" inverse=""/></magic.border>
		<magic.lightline><subattrib light=""/></magic.lightline>
		<magic.darkline><subattrib medium=""/></magic.darkline>

		<special_abilities.title><subattrib centre="" inverse=""/></special_abilities.title>
		<special_abilities.border><subattrib border="" inverse=""/></special_abilities.border>
		<special_abilities.lightline><subattrib light=""/></special_abilities.lightline>
		<special_abilities.darkline><subattrib medium=""/></special_abilities.darkline>

		<archetypes.title><subattrib centre="" inverse=""/></archetypes.title>
		<archetypes.border><subattrib border="" inverse=""/></archetypes.border>
		<archetypes.lightline><subattrib light=""/></archetypes.lightline>
		<archetypes.darkline><subattrib medium=""/></archetypes.darkline>

		<special_attacks.title><subattrib centre="" inverse=""/></special_attacks.title>
		<special_attacks.border><subattrib border="" inverse=""/></special_attacks.border>
		<special_attacks.lightline><subattrib light=""/></special_attacks.lightline>
		<special_attacks.darkline><subattrib medium=""/></special_attacks.darkline>

		<special_qualities.title><subattrib centre="" inverse=""/></special_qualities.title>
		<special_qualities.border><subattrib border="" inverse=""/></special_qualities.border>
		<special_qualities.lightline><subattrib light=""/></special_qualities.lightline>
		<special_qualities.darkline><subattrib medium=""/></special_qualities.darkline>

		<afflictions.title><subattrib centre="" inverse=""/></afflictions.title>
		<afflictions.border><subattrib border="" inverse=""/></afflictions.border>
		<afflictions.lightline><subattrib light=""/></afflictions.lightline>
		<afflictions.darkline><subattrib medium=""/></afflictions.darkline>

		<tempbonuses.title><subattrib centre="" inverse=""/></tempbonuses.title>
		<tempbonuses.border><subattrib border="" inverse=""/></tempbonuses.border>
		<tempbonuses.lightline><subattrib light=""/></tempbonuses.lightline>
		<tempbonuses.darkline><subattrib medium=""/></tempbonuses.darkline>



		<animal_tricks.title><subattrib centre="" inverse=""/></animal_tricks.title>
		<animal_tricks.border><subattrib border="" inverse=""/></animal_tricks.border>
		<animal_tricks.lightline><subattrib light=""/></animal_tricks.lightline>
		<animal_tricks.darkline><subattrib medium=""/></animal_tricks.darkline>


		<intelligent_items.title><subattrib centre="" inverse=""/></intelligent_items.title>
		<intelligent_items.border><subattrib border="" inverse=""/></intelligent_items.border>
		<intelligent_items.lightline><subattrib light=""/></intelligent_items.lightline>
		<intelligent_items.darkline><subattrib medium=""/></intelligent_items.darkline>

		<traits.title><subattrib centre="" inverse=""/></traits.title>
		<traits.border><subattrib border="" inverse=""/></traits.border>
		<traits.lightline><subattrib light=""/></traits.lightline>
		<traits.darkline><subattrib medium=""/></traits.darkline>


		<salient_divine_abilities.title><subattrib centre="" inverse=""/></salient_divine_abilities.title>
		<salient_divine_abilities.border><subattrib border="" inverse=""/></salient_divine_abilities.border>
		<salient_divine_abilities.lightline><subattrib light=""/></salient_divine_abilities.lightline>
		<salient_divine_abilities.darkline><subattrib medium=""/></salient_divine_abilities.darkline>

		<feats.title><subattrib centre="" inverse=""/></feats.title>
		<feats.border><subattrib border="" inverse=""/></feats.border>
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

</xsl:stylesheet>

