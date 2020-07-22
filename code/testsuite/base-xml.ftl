<#ftl encoding="UTF-8" strip_whitespace=true >
<?xml version="1.0" encoding="UTF-8"?>
<#-- 
# Freemarker template for the character integration tests.
# Copyright James Dempsey, 2013
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#
-->
<character>
	<!--<#t>
	  ====================================<#t>
	  <#lt>====================================
		BIO<#lt>
	  ====================================<#t>
	  ====================================--><#lt>
	<basics>
		<bonuses><@pcstring tag="BONUSLIST.STAT.STR"/></bonuses>
		<bonuses><@pcstring tag="BONUSLIST.STAT.STR.TOTAL"/></bonuses>
		<bonuses><@pcstring tag="BONUSLIST.CHECK.BASE"/></bonuses>
		<bonuses><@pcstring tag="BONUSLIST.CHECK.BASE.TOTAL"/></bonuses>
		<name><@pcstring tag="NAME"/></name>
		<followerof><@pcstring tag="FOLLOWEROF"/></followerof>
		<playername><@pcstring tag="PLAYERNAME"/></playername>
		<action_points><@pcstring tag="VAR.ACTION.INTVAL"/></action_points>
		<age><@pcstring tag="AGE"/></age>
		<alignment>
			<long><@pcstring tag="ALIGNMENT"/></long>
			<short><@pcstring tag="ALIGNMENT.SHORT"/></short>
		</alignment>
		<bab><@pcstring tag="ATTACK.MELEE.BASE"/></bab>
		<bio><@pcstring tag="BIO"/></bio>
		<birthplace><@pcstring tag="BIRTHPLACE"/></birthplace>
		<catchphrase><@pcstring tag="CATCHPHRASE"/></catchphrase>
		<classes>
			<#assign max=pcvar('countdistinct("CLASSES")')-1>
			<#if (max>=0)>
			<#list 0..max as class>
			<class>
				<name><@pcstring tag="CLASS.${class}"/></name>
				<abbreviation><@pcstring tag="CLASSABB.${class}"/></abbreviation>
				<level><@pcstring tag="CLASS.${class}.LEVEL"/></level>
				<bonuslist><@pcstring tag="CLASS.${class}.BONUSLIST"/></bonuslist>
				<sequence>${class}</sequence>
			</class>
			</#list>
			</#if>		
			<levels_total><@pcstring tag="TOTALLEVELS"/></levels_total>
			<levels_ecl><@pcstring tag="ECL"/></levels_ecl>
			<!-- shortform below should be removed - it can be derived from class info above -->
			<shortform><@loop from=0 to=pcvar('countdistinct("CLASSES")')-1 ; class , class_has_next ><#rt>
				<#t><@pcstring tag="CLASSABB.${class}"/><@pcstring tag="CLASS.${class}.LEVEL"/><#if class_has_next>,</#if>
			</@loop></shortform><#lt>
			<!-- CLASSLIST is not extracted because we can derive it from the information above -->
		</classes>
		<deity>
			<name>${pcstring('DEITY')}</name>
			<alignment>${pcstring('DEITY.ALIGNMENT')}</alignment>
			<description>${pcstring('DEITY.DESCRIPTION')}</description>
			<domainlist>${pcstring('DEITY.DOMAINLIST')}</domainlist>
			<favoredweapon>${pcstring('DEITY.FAVOREDWEAPON')}</favoredweapon>
			<holyitem>${pcstring('DEITY.HOLYITEM')}</holyitem>
			<pantheonlist>${pcstring('DEITY.PANTHEONLIST')}</pantheonlist>
			<source>${pcstring('DEITY.SOURCE')}</source>
			<special_abilities>${pcstring('DEITY.SA')}</special_abilities>
			<appearance>${pcstring('DEITY.APPEARANCE')}</appearance>
			<title>${pcstring('DEITY.TITLE')}</title>
			<worshippers>${pcstring('DEITY.WORSHIPPERS')}</worshippers>
		</deity>
		<description><@pcstring tag="DESC"/></description>
		<experience>
			<current><@pcstring tag="EXP.CURRENT"/></current>
			<next_level><@pcstring tag="EXP.NEXT"/></next_level>
			<factor><@pcstring tag="EXP.FACTOR"/></factor>
			<penalty><@pcstring tag="EXP.PENALTY"/></penalty>
		</experience>
		<eyes>
			<color><@pcstring tag="COLOR.EYE"/></color>
		</eyes>
		<hair>
			<color><@pcstring tag="COLOR.HAIR"/></color>
			<length><@pcstring tag="LENGTH.HAIR"/></length>
		</hair>
		<skin>
			<color><@pcstring tag="COLOR.SKIN"/></color>
		</skin>
		<cr><@pcstring tag="CR"/></cr>
		<face>
		<face><@pcstring tag="FACE"/></face>
			<short><@pcstring tag="FACE.SHORT"/></short>
			<squares><@pcstring tag="FACE.SQUARES"/></squares>
		</face>
		<favoredlist><@pcstring tag="FAVOREDLIST"/></favoredlist>
		<followerlist><@pcstring tag="FOLLOWERLIST"/></followerlist>
		<gender>
			<long><@pcstring tag="GENDER.LONG"/></long>
			<short><@pcstring tag="GENDER.SHORT"/></short>
		</gender>
		<handed><@pcstring tag="HANDED"/></handed>
		<height>
			<total><@pcstring tag="HEIGHT"/></total>
			<feet><@pcstring tag="HEIGHT.FOOTPART"/></feet>
			<inches><@pcstring tag="HEIGHT.INCHPART"/></inches>
		</height>
		<hitdice><@pcstring tag="HITDICE"/></hitdice>
		<interests><@pcstring tag="INTERESTS"/></interests>
		<languages>
		<@loop to=pcvar('COUNT[LANGUAGES]')-1 ; lang>
			<language><@pcstring tag="LANGUAGES.${lang}"/></language>
		</@loop>
			<all><@pcstring tag="LANGUAGES"/></all>
		</languages>
		<location><@pcstring tag="LOCATION"/></location>
		<move>
		<@loop to=pcvar('COUNT[MOVE]')-1 ; move>
			<move>
				<name><@pcstring tag="MOVE.${move}.NAME"/></name>
				<rate><@pcstring tag="MOVE.${move}.RATE"/></rate>
				<squares><@pcstring tag="MOVE.${move}.SQUARES"/></squares>
			</move>
		</@loop>
			<all><@pcstring tag="MOVEMENT"/></all>
		</move>
		<personality>
			<trait><@pcstring tag="PERSONALITY1"/></trait>
			<trait><@pcstring tag="PERSONALITY2"/></trait>
		</personality>
		<portrait><@pcstring tag="PORTRAIT"/></portrait>
		<phobias><@pcstring tag="PHOBIAS"/></phobias>
		<race><@pcstring tag="RACE"/></race>
		<reach>
		<reach><@pcstring tag="REACH"/></reach>
			<squares><@pcstring tag="REACH.SQUARES"/></squares>
		</reach>
		<region><@pcstring tag="REGION"/></region>
		<reputation><@pcstring tag="VAR.REPUTATION.INTVAL"/></reputation>
		<residence><@pcstring tag="RESIDENCE"/></residence>
		<size>
			<long><@pcstring tag="SIZELONG"/></long>
			<short><@pcstring tag="SIZE"/></short>
		</size>
		<speechtendency><@pcstring tag="SPEECHTENDENCY"/></speechtendency>
		<type><@pcstring tag="TYPE"/></type>
		<vision>
		<@loop to=pcvar('COUNT[VISION]')-1 ; vision>
			<vision><@pcstring tag="VISION.${vision}"/></vision>
		</@loop>
			<all><@pcstring tag="VISION"/></all>
		</vision>
		<wealth><@pcstring tag="VAR.WEALTH.INTVAL"/></wealth>
		<weight>
			<weight_unit><@pcstring tag="WEIGHT"/></weight_unit>
			<weight_nounit><@pcstring tag="WEIGHT.NOUNIT"/></weight_nounit>
		</weight>
		<poolpoints>
			<cost><@pcstring tag="POOL.COST"/></cost>
			<current><@pcstring tag="POOL.CURRENT"/></current>
		</poolpoints>
		<notes>
		<@loop to=pcvar('COUNT[NOTES]')-1 ; note>
			<note>
				<name><@pcstring tag="NOTE.${note}.NAME"/></name>
				<value><@pcstring tag="NOTE.${note}.VALUE"/></value>
			</note>
		</@loop>
		</notes>
	</basics>
	<!--
	  ====================================
	  ====================================
			ABILITIES
	  ====================================
	  ====================================-->
	<abilities>
<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next >	
		<ability>
			<name>
				<long><@pcstring tag="STAT.${stat}.LONGNAME"/></long>
				<short><@pcstring tag="STAT.${stat}.NAME"/></short>
			</name>
			<score><@pcstring tag="STAT.${stat}"/></score>
			<modifier><@pcstring tag="STAT.${stat}.MOD"/></modifier>
			<base><@pcstring tag="STAT.${stat}.BASE"/></base>
			<basemod><@pcstring tag="STAT.${stat}.BASEMOD"/></basemod>
			<noequip><@pcstring tag="STAT.${stat}.NOEQUIP"/></noequip>
			<noequip_mod><@pcstring tag="STAT.${stat}.MOD.NOEQUIP"/></noequip_mod>
			<no_temp_score><@pcstring tag="STAT.${stat}.NOTEMP"/></no_temp_score>
			<no_temp_modifier><@pcstring tag="STAT.${stat}.MOD.NOTEMP"/></no_temp_modifier>
		</ability>
</@loop>		
	</abilities>
	<!--
	  ====================================
	  ====================================
			HIT POINTS
	  ====================================
	  ====================================-->
	<hit_points>
		<points><@pcstring tag="HP"/></points>
		<alternate><@pcstring tag="ALTHP"/></alternate>
		<die><@pcstring tag="HITDICE"/></die>
		<current/>
		<subdual/>
		<damage_reduction><@pcstring tag="DR"/></damage_reduction>
		<damage_threshold><@pcstring tag="VAR.DAMAGETHRESHOLD.INTVAL"/></damage_threshold>
		<history>
<@loop from=1 to=pcvar('ECL') ; level , level_has_next >
			<roll>
				<level>${level}</level>
				<roll><@pcstring tag="HPROLL.${level}"/></roll>
				<stat><@pcstring tag="HPROLL.${level}.STAT"/></stat>
				<total><@pcstring tag="HPROLL.${level}.TOTAL"/></total>
			</roll>
</@loop>		
		</history>
	</hit_points>
	<!--
	  ====================================
	  ====================================
			ARMOR CLASS
	  ====================================
	  ====================================-->
	<armor_class>
		<total><@pcstring tag="AC.Total"/></total>
		<listing><@pcstring tag="BONUS.COMBAT.AC.LISTING"/></listing>
		<flat><@pcstring tag="AC.Flatfooted"/></flat>
		<touch><@pcstring tag="AC.Touch"/></touch>
		<base><@pcstring tag="AC.Base"/></base>
		<armor_bonus><@pcstring tag="AC.Armor"/></armor_bonus>
		<shield_bonus><@pcstring tag="AC.Shield"/></shield_bonus>
		<stat_mod><@pcstring tag="AC.Ability"/></stat_mod>
		<size_mod><@pcstring tag="AC.Size"/></size_mod>
		<natural><@pcstring tag="AC.NaturalArmor"/></natural>
		<class_bonus><@pcstring tag="AC.ClassDefense"/></class_bonus>
		<dodge_bonus><@pcstring tag="AC.Dodge"/></dodge_bonus>
		<misc><@pcstring tag="AC.Misc"/></misc>
		<miss_chance/>
		<max_dex><@pcstring tag="MAXDEX"/></max_dex>
		<spell_failure><@pcstring tag="SPELLFAILURE"/></spell_failure>
		<check_penalty><@pcstring tag="ACCHECK"/></check_penalty>
		<spell_resistance><@pcstring tag="SR"/></spell_resistance>
	</armor_class>
	<!--
	  ====================================
	  ====================================
			INITIATIVE
	  ====================================
	  ====================================-->
	<initiative>
		<total><@pcstring tag="INITIATIVEMOD"/></total>
		<dex_mod><@pcstring tag="STAT.1.MOD"/></dex_mod>
		<misc_mod><@pcstring tag="INITIATIVEMISC"/></misc_mod>
	</initiative>
	<!--
	  ====================================
	  ====================================
			SKILLS
	  ====================================
	  ====================================-->
	<skills>
		<skillpoints>
			<total><@pcstring tag="SKILLPOINTS.TOTAL"/></total>
			<used><@pcstring tag="SKILLPOINTS.USED"/></used>
			<unused><@pcstring tag="SKILLPOINTS.UNUSED"/></unused>
		</skillpoints>
		<list_mods><@pcstring tag="SKILLLISTMODS"/></list_mods>
		<max_class_skill_level><@pcstring tag="MAXSKILLLEVEL"/></max_class_skill_level>
		<max_cross_class_skill_level><@pcstring tag="MAXCCSKILLLEVEL"/></max_cross_class_skill_level>
		<@loop from=0 to=pcvar('COUNT[SKILLS]-1') ; skill , skill_has_next >
		<skill>
			<name><@pcstring tag="SKILL.${skill}"/></name>
			<ranks><@pcstring tag="SKILL.${skill}.RANK"/></ranks>
			<mod><@pcstring tag="SKILL.${skill}.MOD"/><!-- Mods from abilities, equipment, etc -->
			</mod>
			<skill_mod><@pcstring tag="SKILL.${skill}.TOTAL"/></skill_mod>
			<ability_mod><@pcstring tag="SKILL.${skill}.ABMOD"/><!-- Mod from the key ability -->
			</ability_mod>
			<misc_mod><@pcstring tag="SKILL.${skill}.MISC"/><!-- This is a calc value of TOTAL-RANK-ABMOD -->
			</misc_mod>
			<ability><@pcstring tag="SKILL.${skill}.ABILITY"/></ability>
			<synergy><@pcstring tag="SKILL.${skill}.SYNERGY"/></synergy>
			<untrained><@pcstring tag="SKILL.${skill}.UNTRAINED"/></untrained>
			<exclusive><@pcstring tag="SKILL.${skill}.EXCLUSIVE"/></exclusive>
			<trained_total><@pcstring tag="SKILL.${skill}.TRAINED_TOTAL"/></trained_total>
			<exclusive_total><@pcstring tag="SKILL.${skill}.EXCLUSIVE_TOTAL"/></exclusive_total>
		</skill>
		</@loop><#lt>
	</skills>
	<!--
	  ====================================
	  ====================================
			SAVING THROWS
	  ====================================
	  ====================================-->
	<saving_throws>
		<conditional_modifiers/>
		<saving_throw>
			<name>
				<long>fortitude</long>
				<short>fort</short>
			</name>
			<ability>constitution</ability>
			<total><@pcstring tag="CHECK.0.TOTAL"/></total>
			<base><@pcstring tag="CHECK.0.BASE"/></base>
			<abil_mod><@pcstring tag="STAT.2.MOD"/></abil_mod>
			<feats><@pcstring tag="CHECK.0.FEATS"/></feats>
			<magic_mod><@pcstring tag="CHECK.0.MAGIC"/></magic_mod>
			<misc_mod><@pcstring tag="CHECK.0.MISC.NOMAGIC.NOSTAT"/></misc_mod>
			<race><@pcstring tag="CHECK.0.RACE"/></race>
			<epic_mod><@pcstring tag="CHECK.0.EPIC"/></epic_mod>
			<temp_mod/>
		</saving_throw>
		<saving_throw>
			<name>
				<long>reflex</long>
				<short>ref</short>
			</name>
			<ability>dexterity</ability>
			<total><@pcstring tag="CHECK.1.TOTAL"/></total>
			<base><@pcstring tag="CHECK.1.BASE"/></base>
			<feats><@pcstring tag="CHECK.1.FEATS"/></feats>
			<abil_mod><@pcstring tag="STAT.1.MOD"/></abil_mod>
			<magic_mod><@pcstring tag="CHECK.1.MAGIC"/></magic_mod>
			<misc_mod><@pcstring tag="CHECK.1.MISC.NOMAGIC.NOSTAT"/></misc_mod>
			<race><@pcstring tag="CHECK.1.RACE"/></race>
			<epic_mod><@pcstring tag="CHECK.1.EPIC"/></epic_mod>
			<temp_mod/>
		</saving_throw>
		<saving_throw>
			<name>
				<long>will</long>
				<short>will</short>
			</name>
			<ability>wisdom</ability>
			<total><@pcstring tag="CHECK.2.TOTAL"/></total>
			<base><@pcstring tag="CHECK.2.BASE"/></base>
			<abil_mod><@pcstring tag="STAT.4.MOD"/></abil_mod>
			<feats><@pcstring tag="CHECK.2.FEATS"/></feats>
			<magic_mod><@pcstring tag="CHECK.2.MAGIC"/></magic_mod>
			<misc_mod><@pcstring tag="CHECK.2.MISC.NOMAGIC.NOSTAT"/></misc_mod>
			<race><@pcstring tag="CHECK.2.RACE"/></race>
			<epic_mod><@pcstring tag="CHECK.2.EPIC"/></epic_mod>
			<temp_mod/>
		</saving_throw>
	</saving_throws>
	<!--
	  ====================================
	  ====================================
			ATTACK
	  ====================================
	  ====================================-->
	<attack>
		<melee>
			<total><@pcstring tag="ATTACK.MELEE.TOTAL"/></total>
			<bab><@pcstring tag="ATTACK.MELEE.BASE"/></bab>
			<base_attack_bonus><@pcstring tag="ATTACK.MELEE"/></base_attack_bonus>
			<stat_mod><@pcstring tag="ATTACK.MELEE.STAT"/></stat_mod>
			<size_mod><@pcstring tag="ATTACK.MELEE.SIZE"/></size_mod>
			<misc_mod><@pcstring tag="ATTACK.MELEE.MISC"/></misc_mod>
			<epic_mod><@pcstring tag="ATTACK.MELEE.EPIC"/></epic_mod>
			<temp_mod/>
		</melee>
		<ranged>
			<total><@pcstring tag="ATTACK.RANGED.TOTAL"/></total>
			<bab><@pcstring tag="ATTACK.RANGED.BASE"/></bab>
			<base_attack_bonus><@pcstring tag="ATTACK.RANGED"/></base_attack_bonus>
			<stat_mod><@pcstring tag="ATTACK.RANGED.STAT"/></stat_mod>
			<size_mod><@pcstring tag="ATTACK.RANGED.SIZE"/></size_mod>
			<misc_mod><@pcstring tag="ATTACK.RANGED.MISC"/></misc_mod>
			<epic_mod><@pcstring tag="ATTACK.RANGED.EPIC"/></epic_mod>
			<temp_mod/>
		</ranged>
		<grapple>
			<total><@pcstring tag="ATTACK.GRAPPLE.TOTAL"/></total>
			<bab><@pcstring tag="ATTACK.GRAPPLE.BASE"/></bab>
			<base_attack_bonus><@pcstring tag="ATTACK.GRAPPLE"/></base_attack_bonus>
			<stat_mod><@pcstring tag="ATTACK.GRAPPLE.STAT"/></stat_mod>
			<size_mod><@pcstring tag="ATTACK.GRAPPLE.SIZE"/></size_mod>
			<misc_mod><@pcstring tag="ATTACK.GRAPPLE.MISC"/></misc_mod>
			<epic_mod><@pcstring tag="ATTACK.GRAPPLE.EPIC"/></epic_mod>
			<temp_mod/>
		</grapple>
	</attack>
	<!--
	  ====================================
	  ====================================
			WEAPONS
	  ====================================
	  ====================================-->
<#macro weapCommonBlock weap>
			<common>
				<name>
					<short><@pcstring tag="WEAPON.${weap}.NAME"/></short>
					<long><@pcstring tag="WEAPON.${weap}.LONGNAME"/></long>
				</name>
				<category><@pcstring tag="WEAPON.${weap}.CATEGORY"/></category>
				<critical>
					<range><@pcstring tag="WEAPON.${weap}.CRIT"/></range>
					<multiplier><@pcstring tag="WEAPON.${weap}.MULT"/></multiplier>
				</critical>
				<to_hit>
					<hit><@pcstring tag="WEAPON.${weap}.HIT"/></hit>
					<magic_hit><@pcstring tag="WEAPON.${weap}.MAGICHIT"/></magic_hit>
					<total_hit><@pcstring tag="WEAPON.${weap}.TOTALHIT"/></total_hit>
				</to_hit>
				<feat><@pcstring tag="WEAPON.${weap}.FEAT"/></feat>
				<hand><@pcstring tag="WEAPON.${weap}.HAND"/></hand>
				<num_attacks><@pcstring tag="WEAPON.${weap}.NUMATTACKS"/></num_attacks>
				<reach><@pcstring tag="WEAPON.${weap}.REACH"/></reach>
				<size><@pcstring tag="WEAPON.${weap}.SIZE"/></size>
				<special_properties><@pcstring tag="WEAPON.${weap}.SPROP"/></special_properties>
				<template><@pcstring tag="WEAPON.${weap}.TEMPLATE"/></template>
				<type><@pcstring tag="WEAPON.${weap}.TYPE"/></type>
				<weight><@pcstring tag="WEAPON.${weap}.WT"/></weight>
				<sequence>${weap}</sequence>
			</common>
</#macro>
<#macro weapMeleeBlock weap>
			<melee>
				<w1_h1_p>
					<!-- One weapon, 1 hand, primary hand -->
					<to_hit><@pcstring tag="WEAPON.${weap}.BASEHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.BASICDAMAGE"/></damage>
				</w1_h1_p>
				<w1_h1_o>
					<!-- One weapon, 1 handed, offhand -->
					<to_hit><@pcstring tag="WEAPON.${weap}.OHHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.OHDAMAGE"/></damage>
				</w1_h1_o>
				<w1_h2>
					<!-- One weapon, 2 handed -->
					<to_hit><@pcstring tag="WEAPON.${weap}.THHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.THDAMAGE"/></damage>
				</w1_h2>
				<w2_p_oh>
					<!-- Two weapons, this weapon in primary hand, other hand with heavy weapon -->
					<to_hit><@pcstring tag="WEAPON.${weap}.TWPHITH"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.BASICDAMAGE"/></damage>
				</w2_p_oh>
				<w2_p_ol>
					<!-- Two weapons, this weapon in primary hand, other hand with light weapon -->
					<to_hit><@pcstring tag="WEAPON.${weap}.TWPHITL"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.BASICDAMAGE"/></damage>
				</w2_p_ol>
				<w2_o>
					<!-- Two weapons, this weapon in off-hand -->
					<to_hit><@pcstring tag="WEAPON.${weap}.TWOHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.OHDAMAGE"/></damage>
				</w2_o>
			</melee>
</#macro>
<#t>	  
	<weapons>
		<unarmed>
			<total><@pcstring tag="WEAPONH.TOTALHIT"/></total>
			<damage><@pcstring tag="WEAPONH.DAMAGE"/></damage>
			<critical><@pcstring tag="WEAPONH.CRIT"/>/x<@pcstring tag="WEAPONH.MULT"/></critical>
			<!-- Should be changed to a variable due to improved crit -->
		</unarmed>
		
		<@loop from=0 to=pcvar('COUNT[EQTYPE.WEAPON]-1') ; weap , weap_has_next >
		<#assign weaponCategory>
			<@pcstring tag="WEAPON.${weap}.CATEGORY" /><#t>
		</#assign>
		<#-- For weapons of category 'both' we expect to see two consective 
		   - weapons, one defining the melee use and one the ranged use. -->
		<#if weaponCategory?lower_case?contains('both')>
			<#if !weaponCategory?lower_case?contains('ranged')>
		<weapon>
				<@weapCommonBlock weap="${weap}" />
				<@weapMeleeBlock weap="${weap}" />
			</#if>
			<#if weaponCategory?lower_case?contains('ranged')>
			<ranges>
				<@loop from=0 to=4 ; range , range_has_next >
				<range>
					<distance><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}"/></distance>
					<to_hit><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.TOTALHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.DAMAGE"/></damage>
				</range>
				</@loop><#t>
			</ranges>
		</weapon>
			</#if>
		<#else>
			<#if weaponCategory?lower_case?contains('ranged')>
		<weapon>
				<@weapCommonBlock weap="${weap}" />
				<#if pcvar('WEAPON.'+weap+'.CONTENTS') = 0 >
			<ranges>
				<@loop from=0 to=4 ; range , range_has_next >
				<range>
					<distance><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}"/></distance>
					<to_hit><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.TOTALHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.DAMAGE"/></damage>
				</range>
				</@loop><#t>
			</ranges>
				<#else>
				<@loop from=0 to=pcvar('WEAPON.${weap}.CONTENTS')-1 ; ammo , ammo_has_next >
			<ranges>
				<ammunition>
					<name><@pcstring tag="WEAPON.${weap}.CONTENTS.${ammo}"/></name>
					<special_properties><@pcstring tag="WEAPON.${weap}.CONTENTS.${ammo}.SPROP"/></special_properties>
				</ammunition>
					<@loop from=0 to=4 ; range , range_has_next >
				<range>
					<distance><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}"/></distance>
					<to_hit><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.CONTENTS.${ammo}.TOTALHIT"/></to_hit>
					<damage><@pcstring tag="WEAPON.${weap}.RANGELIST.${range}.CONTENTS.${ammo}.DAMAGE"/></damage>
				</range>
					</@loop><#-- Range -->
			</ranges>
				</@loop><#-- ammo -->
			</#if>
		</weapon>
	<#else>
		<#if pcboolean('WEAPON.${weap}.ISTYPE.Double') || weaponCategory?lower_case?contains('non-standard-melee') || weaponCategory?lower_case?contains('natural') >
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<simple>
				<to_hit><@pcstring tag="WEAPON.${weap}.TOTALHIT"/></to_hit>
				<damage><@pcstring tag="WEAPON.${weap}.DAMAGE"/></damage>
				<range><@pcstring tag="WEAPON.${weap}.RANGE"/></range>
			</simple>
		</weapon>
		<#else>
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<@weapMeleeBlock weap="${weap}" />
		</weapon>
		</#if><#-- double, natural or non-standard-melee -->
	</#if><#-- ranged -->
</#if><#-- both -->
</@loop><#-- weap -->
	</weapons>
	<!--
	  ====================================
	  ====================================
			ARMOR
	  ====================================
	  ====================================-->
	<protection>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.Armor]')-1 ; armor , armor_has_next >
		<armor>
			<name><@pcstring tag="ARMOR.Armor.ALL.${armor}.NAME"/></name>
			<acbonus><@pcstring tag="ARMOR.Armor.ALL.${armor}.ACBONUS"/></acbonus>
			<accheck><@pcstring tag="ARMOR.Armor.ALL.${armor}.ACCHECK"/></accheck>
			<baseac><@pcstring tag="ARMOR.Armor.ALL.${armor}.BASEAC"/></baseac>
			<edr><@pcstring tag="ARMOR.Armor.ALL.${armor}.EDR"/></edr>
			<maxdex><@pcstring tag="ARMOR.Armor.ALL.${armor}.MAXDEX"/></maxdex>
			<move><@pcstring tag="ARMOR.Armor.ALL.${armor}.MOVE"/></move>
			<spellfail><@pcstring tag="ARMOR.Armor.ALL.${armor}.SPELLFAIL"/></spellfail>
			<special_properties><@pcstring tag="ARMOR.Armor.ALL.${armor}.SPROP"/></special_properties>
			<totalac><@pcstring tag="ARMOR.Armor.ALL.${armor}.TOTALAC"/></totalac>
			<type><@pcstring tag="ARMOR.Armor.ALL.${armor}.TYPE"/></type>
			<wt><@pcstring tag="ARMOR.Armor.ALL.${armor}.WT"/></wt>
		</armor>
		</@loop>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.SHIELD]')-1 ; armor , armor_has_next >
		<shield>
			<name><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.NAME"/></name>
			<acbonus><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.ACBONUS"/></acbonus>
			<accheck><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.ACCHECK"/></accheck>
			<baseac><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.BASEAC"/></baseac>
			<edr><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.EDR"/></edr>
			<maxdex><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.MAXDEX"/></maxdex>
			<move><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.MOVE"/></move>
			<spellfail><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.SPELLFAIL"/></spellfail>
			<special_properties><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.SPROP"/></special_properties>
			<totalac><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.TOTALAC"/></totalac>
			<type><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.TYPE"/></type>
			<wt><@pcstring tag="ARMOR.SHIELD.ALL.${armor}.WT"/></wt>
		</shield>
		</@loop>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.ACITEM]')-1 ; armor , armor_has_next >
		<item>
			<name><@pcstring tag="ARMOR.ACITEM.${armor}.NAME"/></name>
			<acbonus><@pcstring tag="ARMOR.ACITEM.${armor}.ACBONUS"/></acbonus>
			<accheck><@pcstring tag="ARMOR.ACITEM.${armor}.ACCHECK"/></accheck>
			<baseac><@pcstring tag="ARMOR.ACITEM.${armor}.BASEAC"/></baseac>
			<edr><@pcstring tag="ARMOR.ACITEM.${armor}.EDR"/></edr>
			<maxdex><@pcstring tag="ARMOR.ACITEM.${armor}.MAXDEX"/></maxdex>
			<move><@pcstring tag="ARMOR.ACITEM.${armor}.MOVE"/></move>
			<spellfail><@pcstring tag="ARMOR.ACITEM.${armor}.SPELLFAIL"/></spellfail>
			<special_properties><@pcstring tag="ARMOR.ACITEM.${armor}.SPROP"/></special_properties>
			<totalac><@pcstring tag="ARMOR.ACITEM.${armor}.TOTALAC"/></totalac>
			<type><@pcstring tag="ARMOR.ACITEM.${armor}.TYPE"/></type>
			<wt><@pcstring tag="ARMOR.ACITEM.${armor}.WT"/></wt>
		</item>
		</@loop>
	</protection>
	<!--
	  ====================================
	  ====================================
			CLASS-DEPENDANT FEATURES
	  ====================================
	  ====================================-->
	<class_features>
<#if (pcvar('BardicMusicLevel') >= 1) >
		<bardic_music>
			<uses_per_day><@pcstring tag="VAR.BardicMusicLevel.INTVAL"/></uses_per_day>
			<effects>Effects (Perform ranks required)</effects>
			<text>Inspire Courage(3), Countersong(3), Fascinate(3),Inspire Competence(6), Suggestion(9), Inspire Greatness(12)</text>
		</bardic_music>
</#if>
<#if (pcvar('BardicMusic') >= 1) >
		<bardic_music>
			<uses_per_day><@pcstring tag="VAR.BardicMusic.INTVAL"/></uses_per_day>
			<effects>Effects (Perform ranks required)</effects>
			<text>Inspire Courage(3), Countersong(3), Fascinate(3),Inspire Competence(6), Suggestion(9), Inspire Greatness(12)</text>
		</bardic_music>
</#if>	<!-- Bard -->


	<!-- Pathfinder -->
	<#if (pcvar("VAR.RageLVL") >= 1) >	<!-- If character can Rage -->
	<rage>
		<uses_per_day>${pcstring('VAR.RageDuration.INTVAL')}</uses_per_day>
		<uses_per_day.title>Rounds/day</uses_per_day.title>
	</rage>
	</#if>	<!-- Character Rage -->
	<#if (pcvar("VAR.RageTimes") >= 1) >	<!-- If character can Rage -->
	<rage>
		<uses_per_day>${pcstring('VAR.RageTimes.INTVAL')}</uses_per_day>
		<uses_per_day.title>Uses per day</uses_per_day.title>
		<#if (pcboolean('ABILITYALL.Special Ability.0.TYPE=RageDescription.HASASPECT.RageDescription')) >
		<description>${pcstring('ABILITYALL.Special Ability.0.TYPE=RageDescription.ASPECT.RageDescription')}</description>
		<#else>
		<description>The Barbarian gains +${pcstring('VAR.RageStrBonus.INTVAL')} to Strength, +${pcstring('VAR.RageConBonus.INTVAL')} to Constitution, and a +${pcstring('VAR.RageMorale.INTVAL')} morale bonus on Will saves, but suffers a -${pcstring('VAR.RageACPenalty.INTVAL')} penalty to AC for ${pcvar('VAR.RageConBonus.INTVAL')} rounds. At the end of the rage, the barbarian is fatigued (-2 to Strength, -2 to Dexterity, can't charge or run) for the duration of that encounter. The barbarian can only rage once per encounter. Entering a rage takes no time itself, but the barbarian can only do it during his action.</description>
		</#if>
	</rage>
	<!-- this stuff needs a bit of work to display correct info for both 3e and 3.5e properly. - Tir Gwaith -->
	</#if>	<!-- Character Rage -->

	<!-- Turning ability -->
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","ASPECT=TurnType")')-1 ; turncount , turncount_has_next >
	<#assign turnKind>
	  <@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnKind' /><#t>
	</#assign>
	<#assign turnType>
	  <@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnType' /><#t>
	</#assign>
	<turning kind="${turnKind?upper_case}" type="${turnType?upper_case}">
		<level><@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel.INTVAL' /></level>
		<turn_check>1d20<@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnCheck.INTVAL.SIGN' /></turn_check>
		<damage><@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnDamage' /></damage>
		<uses_per_day><@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnTimes.INTVAL' /></uses_per_day>
		<notes><@pcstring tag='ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnNotes' /></notes>
	</turning>
	</@loop>

<#if (pcvar('StunningAttack') >= 1) >
		<stunning_fist>
			<save_dc><@pcstring tag="VAR.StunDC.INTVAL"/></save_dc>
			<uses_per_day><@pcstring tag="VAR.StunningAttack.INTVAL"/></uses_per_day>
		</stunning_fist>
</#if>	<!-- 3.0 stunning fist -->

<#if (pcvar('StunningFistAttack') >= 1) >
		<stunning_fist>
			<save_dc><@pcstring tag="VAR.StunningFistDC.INTVAL"/></save_dc>
			<uses_per_day><@pcstring tag="VAR.StunningFistAttack.INTVAL"/></uses_per_day>
		</stunning_fist>
</#if>	<!-- 3.5 stunning fist -->

<#if (pcvar('WholenessHpLVL') >= 1) >
		<wholeness_of_body>
			<hp_per_day><@pcstring tag="VAR.WholenessHpLVL.INTVAL*2"/></hp_per_day>
		</wholeness_of_body>
</#if>	<!-- 3.0 wholeness of body -->

<#if (pcvar('WholenessBody') >= 1) >
		<wholeness_of_body>
			<hp_per_day><@pcstring tag="VAR.WholenessBody.INTVAL"/></hp_per_day>
		</wholeness_of_body>
</#if>	<!-- 3.5 wholeness of body -->

<#if (pcvar('TOTALPOWERPOINTS') >= 1) >	<!-- Psionics -->
		<psionics>
	<#if (pchasvar("Manifester") || pchasvar('PsychicWarriorManifester')) >
			<type>3.0</type>
	<#else>
			<type>3.5</type>
	</#if>
			<base_pp><@pcstring tag="VAR.BASEPOWERPOINTS.INTVAL"/></base_pp>
			<bonus_pp><@pcstring tag="VAR.BONUSPOWERPOINTS.INTVAL"/></bonus_pp>
			<total_pp><@pcstring tag="VAR.TOTALPOWERPOINTS.INTVAL"/></total_pp>
		</psionics>
</#if>	<!-- Psionics -->

<#if (pcvar('LayOnHands') >= 1) >
		<layonhands>
			<hp_per_day><@pcstring tag="VAR.LayOnHands.INTVAL"/></hp_per_day>
		</layonhands>
</#if>

<#if (pcvar('WildshapeTimes') >= 1) >
		<wildshape>
			<uses_per_day><@pcstring tag="VAR.WildShapeTimes.INTVAL"/></uses_per_day>
			<elemental_uses_per_day><@pcstring tag="VAR.WildShapeElementalTimes.INTVAL"/></elemental_uses_per_day>
			<duration><@pcstring tag="VAR.WildShapeDuration.INTVAL"/></duration>
		</wildshape>
</#if>


<#if (pcvar('LeadershipScore') >= 1) >
		<leadership>
			<score><@pcstring tag="VAR.LeadershipScore.INTVAL"/></score>
			<max_cohort_level><@pcstring tag="VAR.LeadershipMaxCohortLvl"/></max_cohort_level>
		</leadership>
</#if>
	</class_features>
	<!--
	  ====================================
	  ====================================
			EQUIPMENT
	  ====================================
	  ====================================-->
	<equipment>
		<@loop from=0 to=pcvar('COUNT[EQUIPMENT.MERGELOC]')-1 ; equip , equip_has_next >
		<item>
			<longname><@pcstring tag="EQ.MERGELOC.${equip}.LONGNAME"/></longname>
			<id><@pcstring tag="EQ.MERGELOC.${equip}.ID"/></id>
			<name><@pcstring tag="EQ.MERGELOC.${equip}.NAME"/></name>
			<carried><@pcstring tag="EQ.MERGELOC.${equip}.CARRIED"/></carried>
			<charges><@pcstring tag="EQ.MERGELOC.${equip}.CHARGES"/></charges>
			<charges_used><@pcstring tag="EQ.MERGELOC.${equip}.CHARGESUSED"/></charges_used>
			<contents><@pcstring tag="EQ.MERGELOC.${equip}.CONTENTS"/></contents>
			<contents_num><@pcstring tag="EQ.MERGELOC.${equip}.CONTENTSNUM"/></contents_num>
			<content_weight><@pcstring tag="EQ.MERGELOC.${equip}.CONTENTWEIGHT"/></content_weight>
			<cost><@pcstring tag="EQ.MERGELOC.${equip}.COST"/></cost>
			<equipped><@pcstring tag="EQ.MERGELOC.${equip}.EQUIPPED"/></equipped>
			<location><@pcstring tag="EQ.MERGELOC.${equip}.LOCATION"/></location>
			<maxcharges><@pcstring tag="EQ.MERGELOC.${equip}.MAXCHARGES"/></maxcharges>
			<note><@pcstring tag="EQ.MERGELOC.${equip}.NOTE"/></note>
			<quantity><@pcstring tag="EQ.MERGELOC.${equip}.QTY"/></quantity>
			<checkbox><@pcstring tag="EQ.MERGELOC.${equip}.CHECKBOXES"/></checkbox>
			<size>
				<long><@pcstring tag="EQ.MERGELOC.${equip}.SIZELONG"/></long>
				<short><@pcstring tag="EQ.MERGELOC.${equip}.SIZE"/></short>
			</size>
			<special_properties><@pcstring tag="EQ.MERGELOC.${equip}.SPROP"/></special_properties>
			<type><@pcstring tag="EQ.MERGELOC.${equip}.TYPE"/></type>
			<weight><@pcstring tag="EQ.MERGELOC.${equip}.WT"/></weight>
			<bonuslist><@pcstring tag="EQ.MERGELOC.${equip}.BONUSLIST"/></bonuslist>
		</item>
		</@loop><#lt><#-- Equipment -->
		<total>
			<weight><@pcstring tag="TOTAL.WEIGHT"/></weight>
			<value><@pcstring tag="TOTAL.VALUE"/></value>
			<load><@pcstring tag="TOTAL.LOAD"/></load>
			<capacity><@pcstring tag="TOTAL.CAPACITY"/></capacity>
		</total>
		<equipmentsets>
<@equipsetloop>
		<equipmentset name="<@pcstring tag="EQSET.NAME"/>">
			<@loop from=0 to=pcvar('COUNT[EQUIPMENT.MERGELOC]')-1 ; equip , equip_has_next >
			<item>
				<name><@pcstring tag="EQ.MERGELOC.${equip}.NAME"/></name>
				<carried><@pcstring tag="EQ.MERGELOC.${equip}.CARRIED"/></carried>
				<charges><@pcstring tag="EQ.MERGELOC.${equip}.CHARGES"/></charges>
				<charges_used><@pcstring tag="EQ.MERGELOC.${equip}.CHARGESUSED"/></charges_used>
				<contents><@pcstring tag="EQ.MERGELOC.${equip}.CONTENTS"/></contents>
				<cost><@pcstring tag="EQ.MERGELOC.${equip}.COST"/></cost>
				<equipped><@pcstring tag="EQ.MERGELOC.${equip}.EQUIPPED"/></equipped>
				<location><@pcstring tag="EQ.MERGELOC.${equip}.LOCATION"/></location>
				<maxcharges><@pcstring tag="EQ.MERGELOC.${equip}.MAXCHARGES"/></maxcharges>
				<note><@pcstring tag="EQ.MERGELOC.${equip}.NOTE"/></note>
				<quantity><@pcstring tag="EQ.MERGELOC.${equip}.QTY"/></quantity>
				<size>
					<long><@pcstring tag="EQ.MERGELOC.${equip}.SIZELONG"/></long>
					<short><@pcstring tag="EQ.MERGELOC.${equip}.SIZE"/></short>
				</size>
				<special_properties><@pcstring tag="EQ.MERGELOC.${equip}.SPROP"/></special_properties>
				<type><@pcstring tag="EQ.MERGELOC.${equip}.TYPE"/></type>
				<weight><@pcstring tag="EQ.MERGELOC.${equip}.WT"/></weight>
				<bonuslist><@pcstring tag="EQ.MERGELOC.${equip}.BONUSLIST"/></bonuslist>
			</item>
			</@loop><#lt><#-- Equipment -->
			</equipmentset>
</@equipsetloop>
      </equipmentsets>
	</equipment>
	<weight_allowance>
		<light><@pcstring tag="WEIGHT.LIGHT"/></light>
		<medium><@pcstring tag="WEIGHT.MEDIUM"/></medium>
		<heavy><@pcstring tag="WEIGHT.HEAVY"/></heavy>
		<lift_over_head><@pcstring tag="WEIGHT.OVERHEAD"/></lift_over_head>
		<lift_off_ground><@pcstring tag="WEIGHT.OFFGROUND"/></lift_off_ground>
		<!-- And loses Dex bonus to AC and can only move 5 feet per round as a full-round action -->
		<push_drag><@pcstring tag="WEIGHT.PUSHDRAG"/></push_drag>
	</weight_allowance>
	<!--
	  ====================================
	  ====================================
			SPECIAL ABILITIES
	  ====================================
	  ====================================-->
	<special_abilities>
	<@loop from=0 to=pcvar('COUNT[SA]')-1 ; sa , sa_has_next ><#lt>
		<ability>
			<name><@pcstring tag="SPECIALABILITY.${sa}"/></name>
			<description><@pcstring tag="SPECIALABILITY.${sa}.DESCRIPTION"/></description>
		</ability>
	</@loop><#lt><#-- Special Abilities -->
		<race><@pcstring tag="RACE.ABILITYLIST"/></race>
	<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next ><#lt>
		<class>
			<class><@pcstring tag="CLASS.${class}"/></class>
			<ability><@pcstring tag="CLASS.${class}.SALIST"/></ability>
		</class>
	</@loop><#lt>
	</special_abilities>
	<!--
	  ====================================
	  ====================================
			FEATS
	  ====================================
	  ====================================-->
	<feats>
		<!-- Visible standard feats (not including the auto feats) -->
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=FEAT","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="ABILITY.FEAT.VISIBLE.${feat}"/></name>
			<description><@pcstring tag="ABILITY.FEAT.VISIBLE.${feat}.DESC"/></description>
			<type><@pcstring tag="ABILITY.FEAT.VISIBLE.${feat}.TYPE"/></type>
			<associated><@pcstring tag="ABILITY.FEAT.VISIBLE.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="ABILITY.FEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>F</auto>
			<hidden>F</hidden>
			<virtual>F</virtual>
		</feat>
</@loop>	

		<!-- Auto feats -->
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=FEAT","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=AUTOMATIC")-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="ABILITYAUTO.FEAT.VISIBLE.${feat}"/></name>
			<description><@pcstring tag="ABILITYAUTO.FEAT.VISIBLE.${feat}.DESC"/></description>
			<type><@pcstring tag="ABILITYAUTO.FEAT.VISIBLE.${feat}.TYPE"/></type>
			<associated><@pcstring tag="ABILITYAUTO.FEAT.VISIBLE.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="ABILITYAUTO.FEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>T</auto>
			<hidden>F</hidden>
			<virtual>F</virtual>
		</feat>
</@loop>	

		<!-- Virtual Feats -->
<@loop from=0 to=pcvar('COUNT[VFEATS.VISIBLE]-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="VFEAT.VISIBLE.${feat}"/></name>
			<description><@pcstring tag="VFEAT.VISIBLE.${feat}.DESC"/></description>
			<type><@pcstring tag="VFEAT.VISIBLE.${feat}.TYPE"/></type>
			<associated><@pcstring tag="VFEAT.VISIBLE.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="VFEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>F</auto>
			<hidden>F</hidden>
			<virtual>T</virtual>
		</feat>
</@loop>	
		<!-- End Virtual Feats -->
		<!-- Hidden feats (all feats less the virtual, automatic and visible ones) -->
<@loop from=0 to=pcvar('COUNT[FEATS.HIDDEN]-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="FEAT.HIDDEN.${feat}"/></name>
			<description><@pcstring tag="FEAT.HIDDEN.${feat}.DESC"/></description>
			<type><@pcstring tag="FEAT.HIDDEN.${feat}.TYPE"/></type>
			<associated><@pcstring tag="FEAT.HIDDEN.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="FEAT.HIDDEN.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>F</auto>
			<hidden>T</hidden>
			<virtual>F</virtual>
		</feat>
</@loop>
<!-- Hidden VFEATS -->
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=FEAT","VISIBILITY=HIDDEN","NATURE=VIRTUAL")-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="VABILITY.FEAT.HIDDEN.${feat}"/></name>
			<description><@pcstring tag="VABILITY.FEAT.HIDDEN.${feat}.DESC"/></description>
			<type><@pcstring tag="VABILITY.FEAT.HIDDEN.${feat}.TYPE"/></type>
			<associated><@pcstring tag="VABILITY.FEAT.HIDDEN.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="VABILITY.FEAT.HIDDEN.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>F</auto>
			<hidden>T</hidden>
			<virtual>T</virtual>
		</feat>
</@loop>
<!-- END Hidden VFEATS -->
<@loop from=0 to=pcvar('COUNT[FEATSAUTO.HIDDEN]-1') ; feat , feat_has_next >
		<feat>
			<name><@pcstring tag="FEATAUTO.HIDDEN.${feat}"/></name>
			<description><@pcstring tag="FEATAUTO.HIDDEN.${feat}.DESC"/></description>
			<type><@pcstring tag="FEATAUTO.HIDDEN.${feat}.TYPE"/></type>
			<associated><@pcstring tag="FEATAUTO.HIDDEN.${feat}.ASSOCIATED"/></associated>
			<count><@pcstring tag="FEATAUTO.HIDDEN.${feat}.ASSOCIATEDCOUNT"/></count>
			<auto>T</auto>
			<hidden>T</hidden>
			<virtual>F</virtual>
		</feat>
</@loop>
	</feats>
	<!--
	  ====================================
	  ====================================
			ABILITY OBJECTS
	  ====================================
	  ====================================-->
<#macro abilityBlock category nature hidden >
	<#if hidden>
		<#assign visibilityCrit = 'VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY' />
		<#assign visName = 'HIDDEN' />
	<#else>
		<#assign visibilityCrit = 'VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY' />
		<#assign visName = 'VISIBLE' />
	</#if>
	<#assign abilityToken = 'ABILITY' />
	<#assign isAuto = 'F' />
	<#assign isVirtual = 'F' />
	<#if nature='AUTOMATIC'>
		<#assign abilityToken = 'ABILITYAUTO' />
		<#assign isAuto = 'T' />
	<#elseif nature='VIRTUAL' >
		<#assign abilityToken = 'VABILITY' />
		<#assign isVirtual = 'T' />
	</#if>
	<#assign numAbilities =
		pcvar('countdistinct("ABILITIES","CATEGORY=${category}","${visibilityCrit}","NATURE=${nature}")') />
	<#if (numAbilities > 0) >
		<!-- ${visName?capitalize} ${nature?capitalize} "${category}" Ability Objects -->
	</#if>
	<@loop from=0 to=numAbilities-1 ; abilityIdx >
		<ability_object>
			<name><@pcstring tag="${abilityToken}.${category}.${visName}.${abilityIdx}"/></name>
			<description><@pcstring tag="${abilityToken}.${category}.${visName}.${abilityIdx}.DESC"/></description>
			<type><@pcstring tag="${abilityToken}.${category}.${visName}.${abilityIdx}.TYPE"/></type>
			<associated><@pcstring tag="${abilityToken}.${category}.${visName}.${abilityIdx}.ASSOCIATED"/></associated>
			<count><@pcstring tag="${abilityToken}.${category}.${visName}.${abilityIdx}.ASSOCIATEDCOUNT"/></count>
			<auto>${isAuto}</auto>
			<hidden><#if hidden>T<#else>F</#if></hidden>
			<virtual>${isVirtual}</virtual>
			<category>${category}</category>
		</ability_object>
	</@loop>
</#macro>

	<ability_objects>
		<#assign natureList = ['NORMAL', 'AUTOMATIC', 'VIRTUAL' ] />
		<#list natureList as nature>
			<@abilityBlock category="Special Ability" nature="${nature}" hidden=false />
		</#list>
		<#list natureList as nature>
			<@abilityBlock category="Special Ability" nature="${nature}" hidden=true />
		</#list>
		<@abilityBlock category="Salient Divine Ability" nature="NORMAL" hidden=false />
		<@abilityBlock category="Mutation" nature="NORMAL" hidden=false />
		<#list natureList as nature>
			<@abilityBlock category="Talent" nature="${nature}" hidden=false />
		</#list>
		<#list natureList as nature>
			<@abilityBlock category="Talent" nature="${nature}" hidden=true />
		</#list>
		<#list natureList as nature>
			<@abilityBlock category="Internal" nature="${nature}" hidden=false />
		</#list>
		<#list natureList as nature>
			<@abilityBlock category="Internal" nature="${nature}" hidden=true />
		</#list>
	</ability_objects>
	<!--
	  ====================================
	  ====================================
			MISCELLANEOUS
	  ====================================
	  ====================================-->

<#if (pcvar('COUNT[DOMAINS]') > 0) >
	<domains>
	<@loop from=1 to=pcvar('COUNT[DOMAINS]') ; domain >
		<domain>
			<name><@pcstring tag="DOMAIN.${domain}"/></name>
			<power><@pcstring tag="DOMAIN.${domain}.POWER"/></power>
		</domain>
	</@loop>
	</domains>
</#if>
	<weapon_proficiencies><@pcstring tag="WEAPONPROFS"/></weapon_proficiencies>
	<languages><@pcstring tag="LANGUAGES"/></languages>

<#if (pcvar('COUNT[TEMPLATES]') > 0) >
	<templates>
		<list><@pcstring tag="TEMPLATELIST"/></list>
	<@loop from=0 to=pcvar('COUNT[TEMPLATES]')-1 ; template >
		<template>
			<name><@pcstring tag="TEMPLATE.${template}.NAME"/></name>
			<strmod><@pcstring tag="TEMPLATE.${template}.STRMOD"/></strmod>
			<dexmod><@pcstring tag="TEMPLATE.${template}.DEXMOD"/></dexmod>
			<conmod><@pcstring tag="TEMPLATE.${template}.CONMOD"/></conmod>
			<intmod><@pcstring tag="TEMPLATE.${template}.INTMOD"/></intmod>
			<wismod><@pcstring tag="TEMPLATE.${template}.WISMOD"/></wismod>
			<chamod><@pcstring tag="TEMPLATE.${template}.CHAMOD"/></chamod>
			<cr><@pcstring tag="TEMPLATE.${template}.CR"/></cr>
			<dr><@pcstring tag="TEMPLATE.${template}.DR"/></dr>
			<feat><@pcstring tag="TEMPLATE.${template}.FEAT"/></feat>
			<sa><@pcstring tag="TEMPLATE.${template}.SA"/></sa>
			<sr><@pcstring tag="TEMPLATE.${template}.SR"/></sr>
			<bonuslist><@pcstring tag="TEMPLATE.${template}.BONUSLIST"/></bonuslist>
		</template>
	</@loop>
	</templates>
</#if>
<#assign prohibitedlist>
	<@pcstring tag="PROHIBITEDLIST"/>
</#assign>
<#if (prohibitedlist?length > 0) >
	<prohibited_schools>${prohibitedlist}</prohibited_schools>
</#if>

	<misc>
	<#assign miscFunds>
		<@pcstring tag="MISC.FUNDS"/>
	</#assign>
	<#if (miscFunds?length > 0) >
		<funds>
			<fund>${miscFunds}</fund>
		</funds>
	</#if>
	<#assign miscCompanions>
		<@pcstring tag="MISC.COMPANIONS"/>
	</#assign>
	<#if (miscCompanions?length > 0) >
		<companions>
			<companion>${miscCompanions}</companion>
		</companions>
	</#if>
	<#assign miscMagic>
		<@pcstring tag="MISC.MAGIC"/>
	</#assign>
	<#if (miscMagic?length > 0) >
		<magics>
			<magic>${miscMagic}</magic>
		</magics>
	</#if>
	</misc>
	<!--
	  ====================================
	  ====================================
			COMPANIONS
	  ====================================
	  ====================================-->
<#macro companionBlock nodeName followerType >
	<#assign numComp = pcvar('COUNT[FOLLOWERTYPE.${followerType}]') />
	<@loop from=0 to=numComp-1 ; companion >
		<${nodeName}>
			<name><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.NAME"/></name>
			<race><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.RACE"/></race>
			<hp><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.HP"/></hp>
			<ac><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.BONUS.COMBAT.AC.TOTAL"/></ac>
			<fortitude><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.CHECK.0.TOTAL"/></fortitude>
			<reflex><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.CHECK.1.TOTAL"/></reflex>
			<willpower><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.CHECK.2.TOTAL"/></willpower>
			<initiative_mod><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.INITIATIVEMOD"/></initiative_mod>
			<special_properties><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.SPECIALLIST"/></special_properties>
			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.${followerType}.${companion}.EQTYPE.WEAPON]')-1 ; weap >
				<attack>
					<common>
						<name>
							<short><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.NAME"/></short>
							<long><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.LONGNAME"/></long>
						</name>
						<category><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.CATEGORY"/></category>
						<critical>
							<range><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.CRIT"/></range>
							<multiplier><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.MULT"/></multiplier>
						</critical>
						<to_hit>
							<hit><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.HIT"/></hit>
							<magic_hit><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.MAGICHIT"/></magic_hit>
							<total_hit><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.TOTALHIT"/></total_hit>
						</to_hit>
						<feat><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.FEAT"/></feat>
						<hand><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.HAND"/></hand>
						<num_attacks><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.NUMATTACKS"/></num_attacks>
						<reach><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.REACH"/></reach>
						<size><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.SIZE"/></size>
						<special_properties><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.SPROP"/></special_properties>
						<template><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.TEMPLATE"/></template>
						<type><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.TYPE"/></type>
						<weight><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.WT"/></weight>
						<sequence><@pcstring tag="%weap"/></sequence>
					</common>
					<simple>
						<to_hit><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.TOTALHIT"/></to_hit>
						<damage><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.DAMAGE"/></damage>
						<range><@pcstring tag="FOLLOWERTYPE.${followerType}.${companion}.WEAPON.%weap.RANGE"/></range>
					</simple>
				</attack>
			</@loop>
			</attacks>
		</${nodeName}>
	</@loop>
</#macro>

	<companions>
		<@companionBlock nodeName="familiar" followerType="FAMILIAR" />
		<@companionBlock nodeName="psicrystal" followerType="Psicrystal" />
		<@companionBlock nodeName="mount" followerType="SPECIAL MOUNT" />
		<@companionBlock nodeName="companion" followerType="ANIMAL COMPANION" />
		<@companionBlock nodeName="follower" followerType="FOLLOWER" />
	</companions>
	<!--
	  ====================================
	  ====================================
			SPELLS
	  ====================================
	  ====================================-->
<#macro spellBlock class spellbook level spell>
					<spell>
						<name><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME"/></name>
						<outputname><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME"/></outputname>
						<times_memorized><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES"/></times_memorized>
						<range><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE"/></range>
						<components><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS"/></components>
						<castingtime><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME"/></castingtime>
						<casterlevel><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL"/></casterlevel>
						<dc><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC"/></dc>
						<duration><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION"/></duration>
						<effect><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT"/></effect>
						<target><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET"/></target>
						<saveinfo><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO"/></saveinfo>
						<school>
							<school><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL"/></school>
							<subschool><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL"/></subschool>
							<descriptor><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR"/></descriptor>
							<fullschool><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL"/></fullschool>
						</school>
						<source>
							<sourcelevel><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL"/></sourcelevel>
							<source><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE"/></source>
							<sourcepage><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE"/></sourcepage>
							<sourceshort><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT"/></sourceshort>
						</source>
						<spell_resistance><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR"/></spell_resistance>
						<description><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION"/></description>
						<bonusspell><@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL"/></bonusspell>
					</spell>
</#macro>
	<spells>
		<!-- ### BEGIN Innate spells ### -->
<#if pcvar('COUNT[SPELLRACE]') = 0 >
	<spells_innate number="none"/>
<#else>
	<spells_innate>
	<#assign spellbook = 1 />
	<#assign class = 0 />
	<#assign level = 0 />
<#-- |%SPELLLISTBOOK.%class.%level.%spellbook| -->
	<#if (pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') > 0) >
		<racial_innate>
		<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]')-1 ; spell >
			<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
		</@loop>
		</racial_innate>
	</#if>
<#-- |%| -->

		<class_innate>
		<#assign class = 0 />
		<#assign level = 0 />
		<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook >
<#-- |%SPELLLISTBOOK.%class.%level.%spellbook| -->
		<#if (pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') > 0) >
			<spellbook number="${spellbook}" name="${pcstring('SPELLBOOKNAME.${spellbook}')}">
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]')-1 ; spell >
				<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
			</@loop>
			</spellbook>
		</#if>
<#-- |%| -->
		</@loop>
		</class_innate>
		</spells_innate>
</#if>
<!-- ### END Innate spells ### -->
		<!-- ### BEGIN Known spells ### -->
		<known_spells>
	<#assign spellbook = 0 />
	<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]')-1 ; class , class_has_next >
<#-- |%SPELLLISTCLASS.%class| -->
		<#if (pcstring('SPELLLISTCLASS.${class}')?length > 0) >
		<class number="${class}" spelllistclass="${pcstring('SPELLLISTCLASS.${class}')}" spellcastertype="${pcstring('SPELLLISTTYPE.${class}')}">
		<@loop from=0 to=9 ; level >
		<#if pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') = 0 >
			<level number="${level}" known="0" cast="0"/>
		<#else>
			<level number="${level}" known="<@pcstring tag="SPELLLISTKNOWN.${class}.${level}"/>" cast="<@pcstring tag="SPELLLISTCAST.${class}.${level}"/>">
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]')-1 ; spell >
				<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
			</@loop>
			</level>
		</#if>
		</@loop>
		</class>
		</#if>

<#-- |%| -->
	</@loop>
	</known_spells>
		<!-- ### END Known spells ### -->
		<!-- ### BEGIN memorized spells ### -->
<#if pcvar('COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2') = 0 >
	<memorized_spells/>
<#else>
	<memorized_spells>
		<!-- ### BEGIN innate memorized spell section -->	
	<#if (pcvar('COUNT[SPELLRACE]') > 0) >
		<!-- ### BEGIN innate memorized spells ### -->
		<#assign spellbook = 1 />
		<#assign class = 0 />
		<#assign level = 0 />
		<#-- |SPELLLISTBOOK.%class.%level.%spellbook| -->
		<#assign spellcount = pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') />
		<#if (spellcount>0) >
		<racial_innate_memorized>
			<@loop from=0 to=spellcount-1 ; spell >
				<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
			</@loop>
		</racial_innate_memorized>
		</#if>
		<#-- |%| -->
		<!-- ### END innate memorized spells ### -->
		<!-- ### BEGIN class innate memorized spells ### -->
		<class_innate_memorized>
		<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook >
			<#assign class = 0 />
			<#assign level = 0 />
			<#-- |SPELLLISTBOOK.%class.%level.%spellbook| -->
			<#assign spellcount = pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') />
			<#if (spellcount>0) >
			<spellbook number="${spellbook}" name="<@pcstring tag="SPELLBOOKNAME.${spellbook}"/>">
				<@loop from=0 to=spellcount-1 ; spell >
					<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
				</@loop>
			</spellbook>
			</#if>
			<#-- |%| -->
		</@loop>
		</class_innate_memorized>
			<!-- ### END class innate memorized spells ### -->
	</#if>
		<!-- ### END innate memorized spell section -->
	
		<!-- ### BEGIN class Spellbook memorized spells ### -->
	<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook >
		<#assign foo = pcvar('COUNT[SPELLRACE]') />
		<#assign bar = pcvar('COUNT[SPELLSINBOOK0.${spellbook}.0]') />
		<#if (foo = 0 || bar = 0) >
<!-- Either we do not have a innate race, or if we do we do not have any 0 level spell for the innate race -->
		<spellbook number="${spellbook}" name="<@pcstring tag="SPELLBOOKNAME.${spellbook}"/>">
		<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class >
			<#-- |%SPELLLISTCLASS.%class| -->
			<class number="${class}" spelllistclass="<@pcstring tag="SPELLLISTCLASS.${class}"/>">
			<@loop from=0 to=9 ; level >
			<#assign spelllevelcount = pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') />
			<#if spelllevelcount = 0 >
				<level number="${level}" spellcount="${spelllevelcount}"/>
			<#else>
				<level number="${level}" spellcount="${spelllevelcount}">
				<@loop from=0 to=spelllevelcount-1 ; spell >
					<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" spell="${spell}" />
				</@loop>
				</level>
			</#if>
			</@loop>
			</class>
			<#-- |%| -->
		</@loop>
		</spellbook>
		</#if>
	</@loop>
	</memorized_spells>
</#if>
<!-- ### END class Spellbook memorized spells ### -->
	</spells>
</character>
