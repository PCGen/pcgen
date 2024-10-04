<#ftl encoding="UTF-8" strip_whitespace=true >
<?xml version="1.0" encoding="UTF-8"?>
<!--
-->
<character>
	<export>
		<date>${pcstring('EXPORT.DATE')}</date>
		<time>${pcstring('EXPORT.TIME')}</time>
		<version>${pcstring('EXPORT.VERSION')}</version>
		<paperinfo>
			<name>${pcstring('PAPERINFO.NAME')}</name>
			<height>${pcstring('PAPERINFO.HEIGHT')}</height>
			<width>${pcstring('PAPERINFO.WIDTH')}</width>
			<margins>
				<top>${pcstring('PAPERINFO.MARGINTOP')}</top>
				<bottom>${pcstring('PAPERINFO.MARGINBOTTOM')}</bottom>
				<left>${pcstring('PAPERINFO.MARGINLEFT')}</left>
				<right>${pcstring('PAPERINFO.MARGINRIGHT')}</right>
			</margins>
		</paperinfo>
		<directories>
			<pcgen>${pcstring('DIR.PCGEN')}</pcgen>
			<templates>${pcstring('DIR.TEMPLATES')}</templates>
			<pcg>${pcstring('DIR.PCG')}</pcg>
			<html>${pcstring('DIR.HTML')}</html>
			<temp>${pcstring('DIR.TEMP')}</temp>
		</directories>
		<invalidtext>
			<tohit>${pcstring('INVALIDTEXT.TOHIT')}</tohit>
			<damage>${pcstring('INVALIDTEXT.DAMAGE')}</damage>
		</invalidtext>
	</export>
	<unit_set>
		<name>${pcstring('UNITSET')}</name>
		<height_unit>${pcstring('UNITSET.HEIGHTUNIT')}</height_unit>
		<distance_unit>${pcstring('UNITSET.DISTANCEUNIT')}</distance_unit>
		<weight_unit>${pcstring('UNITSET.WEIGHTUNIT')}</weight_unit>
	</unit_set>
	<!--
	  ====================================
	  ====================================
			BIO
	  ====================================
	  ====================================-->
	<basics>
		<rules>
			<pfs>
				<os>${pcstring('VAR.PFS_System.INTVAL')}</os>
				<id_number>${pcstring('ABILITYALL.ANY.0.ASPECT=PFS_ID.ASPECT.PFS_ID')}</id_number>
				<faction>${pcstring('ABILITYALL.ANY.0.TYPE=Society Faction.NAME')}</faction>
			</pfs>
		</rules>
		<bonuses>${pcstring('BONUSLIST.STAT.STR')}</bonuses>
		<bonuses>${pcstring('BONUSLIST.STAT.STR.TOTAL')}</bonuses>
		<bonuses>${pcstring('BONUSLIST.CHECK.BASE')}</bonuses>
		<bonuses>${pcstring('BONUSLIST.CHECK.BASE.TOTAL')}</bonuses>
		<name>${pcstring('NAME')}</name>
		<followerof>${pcstring('FOLLOWEROF')}</followerof>
		<playername>${pcstring('PLAYERNAME')}</playername>
		<charactertype>${pcstring('CHARACTERTYPE')}</charactertype>
		<hero_points>${pcstring('VAR.HEROPOINTS.INTVAL')}</hero_points>
		<remaining_action_points>${pcstring('VAR.Action.INTVAL')}</remaining_action_points>
		<age>${pcstring('AGE')}</age>
		<alignment>
			<long>${pcstring('ALIGNMENT')}</long>
			<short>${pcstring('ALIGNMENT.SHORT')}</short>
		</alignment>
		<archetypes>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; trait , trait_has_next>
			<archetype>
				<name>${pcstring('ABILITY.Archetype.VISIBLE.${trait}.TYPE=Archetype')}</name>
				<type>${pcstring('ABILITY.Archetype.VISIBLE.${trait}.TYPE=Archetype.TYPE')}</type>
			</archetype>
		</@loop>
		</archetypes>
		<bab>${pcstring('ATTACK.MELEE.BASE')}</bab>
		<bio>${pcstring('BIO')}</bio>
		<birthday>${pcstring('BIRTHDAY')}</birthday>
		<birthplace>${pcstring('BIRTHPLACE')}</birthplace>
		<catchphrase>${pcstring('CATCHPHRASE')}</catchphrase>
		<classes>
			<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
			<#if (pcvar(pcstring('CLASS.${class}.LEVEL')) > 0) >
			<class>
				<name>${pcstring('CLASS.${class}')}</name>
				<abbreviation>${pcstring('CLASSABB.${class}')}</abbreviation>
				<level>${pcstring('CLASS.${class}.LEVEL')}</level>
				<bonuslist>${pcstring('CLASS.${class}.BONUSLIST')}</bonuslist>
				<sequence>${class}</sequence>
				<sequence_shortform><@pcstring tag="CLASSABB.${class}"/><@pcstring tag="CLASS.${class}.LEVEL"/></sequence_shortform>
			</class>
			</#if>
			</@loop><#-- Classes -->
			<levels_total>${pcstring('TOTALLEVELS')}</levels_total>
			<levels_ecl>${pcstring('ECL')}</levels_ecl>
			<!-- shortform below should be removed - it can be derived from class info above -->
			<shortform><@loop from=0 to=pcvar('countdistinct("CLASSES")')-1 ; class , class_has_next ><#rt>
				<#t><@pcstring tag="CLASSABB.${class}"/><@pcstring tag="CLASS.${class}.LEVEL"/><#if class_has_next> </#if>
			<#t></@loop></shortform>
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
		<description>${pcstring('DESC')}</description>
		<experience>
			<current>${pcstring('EXP.CURRENT')}</current>
			<next_level>${pcstring('EXP.NEXT')}</next_level>
			<factor>${pcstring('EXP.FACTOR')}</factor>
			<penalty>${pcstring('EXP.PENALTY')}</penalty>
		</experience>
		<eyes>
			<color>${pcstring('COLOR.EYE')}</color>
		</eyes>
		<hair>
			<color>${pcstring('COLOR.HAIR')}</color>
			<length>${pcstring('LENGTH.HAIR')}</length>
		</hair>
		<skin>
			<color>${pcstring('COLOR.SKIN')}</color>
		</skin>
		<cr>${pcstring('CR')}</cr>
		<face>
			<face>${pcstring('FACE')}</face>
			<short>${pcstring('FACE.SHORT')}</short>
			<squares>${pcstring('FACE.SQUARES')}</squares>
		</face>
		<favoredlist>${pcstring('FAVOREDLIST')}</favoredlist>
		<followerlist>${pcstring('FOLLOWERLIST')}</followerlist>
		<gender>
			<long>${pcstring('GENDER.LONG')}</long>
			<short>${pcstring('GENDER.SHORT')}</short>
		</gender>
		<handed>${pcstring('HANDED')}</handed>
		<height>
			<total>${pcstring('HEIGHT')}</total>
			<feet>${pcstring('HEIGHT.FOOTPART')}</feet>
			<inches>${pcstring('HEIGHT.INCHPART')}</inches>
		</height>
		<hitdice>${pcstring('HITDICE')}</hitdice>
		<image>file:${pcstring('DIR.PCG')}/${pcstring('NAME')}.jpg</image>
		<interests>${pcstring('INTERESTS')}</interests>
		<languages>
		<@loop from=0 to=pcvar('COUNT[LANGUAGES]-1') ; lang , lang_has_next>
			<language>${pcstring('LANGUAGES.${lang}')}</language>
		</@loop>
			<language>${pcstring('ABILITYALL.ANY.0.ASPECT=Language.ASPECT.Language')}</language>
			<all>${pcstring('LANGUAGES')}</all>
		</languages>
		<location>${pcstring('LOCATION')}</location>
		<move>
		<@loop from=0 to=pcvar('COUNT[MOVE]-1') ; move , move_has_next>
			<move>
				<name>${pcstring('MOVE.${move}.NAME')}</name>
				<rate>${pcstring('MOVE.${move}.RATE')}</rate>
				<squares>${pcstring('MOVE.${move}.SQUARES')}</squares>
		<#if (pcstring("MOVE.${move}.NAME") = "Fly")>
				<maneuverability>(${pcstring('ABILITYALL.Special Ability.HIDDEN.0.TYPE=Maneuverability.ASPECT.Maneuverability')})</maneuverability>
		</#if>
			</move>
		</@loop>
			<all>${pcstring('MOVEMENT')}</all>
		</move>
		<personality>
			<trait>${pcstring('PERSONALITY1')}</trait>
			<trait>${pcstring('PERSONALITY2')}</trait>
		</personality>
		<portrait>
			<portrait>${pcstring('PORTRAIT')?url_path('utf-8')}</portrait>
			<portrait_thumb>${pcstring('PORTRAIT.THUMB')?url_path('utf-8')}</portrait_thumb>
		</portrait>
		<phobias>${pcstring('PHOBIAS')}</phobias>
		<#if (pcstring("ABILITYALL.ANY.0.TYPE=RaceName.HASASPECT.RaceName") = "Y")>
			<race>${pcstring('ABILITYALL.ANY.0.ASPECT=RaceName.ASPECT.RaceName')}</race>
		<#else>
			<race>${pcstring('RACE')}</race>
		</#if>
		<race>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Internal","ASPECT=RaceExtra")-1') ; ability , ability_has_next>
			<raceextra>${pcstring('ABILITYALL.Internal.HIDDEN.${ability}.ASPECT.RaceExtra')}</raceextra>
		</@loop>
			<racetype>${pcstring('RACETYPE')}</racetype>
		<@loop from=0 to=pcvar('COUNT[RACESUBTYPE]-1') ; racesubtype , racesubtype_has_next>
			<racesubtype>${pcstring('RACESUBTYPE.${racesubtype}')}</racesubtype>
		</@loop>
		</race>
		<reach>
			<reach>${pcstring('REACH')}</reach>
			<squares>${pcstring('REACH.SQUARES')}</squares>
		</reach>
		<region>${pcstring('REGION')}</region>
		<reputation>${pcstring('VAR.REPUTATION.INTVAL')}</reputation>
		<residence>${pcstring('RESIDENCE')}</residence>
		<size>
			<long>${pcstring('SIZELONG')}</long>
			<short>${pcstring('SIZE')}</short>
		</size>
		<speechtendency>${pcstring('SPEECHTENDENCY')}</speechtendency>
		<type>${pcstring('TYPE')}</type>
		<vision>
		<@loop from=0 to=pcvar('COUNT[VISION]-1') ; vision , vision_has_next>
			<vision>${pcstring('VISION.${vision}')}</vision>
		</@loop>
			<all>${pcstring('VISION')}</all>
		</vision>
		<wealth>${pcstring('VAR.WEALTH.INTVAL')}</wealth>
		<gold>${pcstring('GOLD')}</gold>
		<weight>
			<weight_unit>${pcstring('WEIGHT')}</weight_unit>
			<weight_nounit>${pcstring('WEIGHT.NOUNIT')}</weight_nounit>
		</weight>
		<poolpoints>
			<cost>${pcstring('POOL.COST')}</cost>
			<current>${pcstring('POOL.CURRENT')}</current>
		</poolpoints>
		<notes>
		<@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note , note_has_next>
		<#if (pcstring("NOTE.${note}.NAME") = "DM Notes")>
		<#else>
			<note>
				<name>${pcstring('NOTE.${note}.NAME')}</name>
				<value>${pcstring('NOTE.${note}.VALUE.')}</value>
			</note>

		</#if>
		</@loop>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","ASPECT=NotesSection")-1') ; ability , ability_has_next>
			<note>
				<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT.NotesSection')}</name>
				<value>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT.Notes')}</value>
			</note>
		</@loop>
		</notes>
		<campaign_histories>
			<@loop from=0 to=pcvar('count("CAMPAIGNHISTORY")-1') ; campaignhistory , campaignhistory_has_next>	
			<campaign_history>
				<campaign>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.CAMPAIGN')}</campaign>
				<adventure>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.ADVENTURE')}</adventure>
				<party>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.PARTY')}</party>
				<date>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.DATE')}</date>
				<xp>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.XP')}</xp>
				<gm>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.GM')}</gm>
				<text>${pcstring('CAMPAIGNHISTORY.VISIBLE.${campaignhistory}.TEXT')}</text>
			</campaign_history>
			</@loop>	
		</campaign_histories>
	</basics>

	<!-- Use ASPECT.NotesSection.x and ASPECT.Notes.y	-->
	<!--
	  ====================================
	  ====================================
			ABILITIES
	  ====================================
	  ====================================-->
	<abilities>
	<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next>
		<ability>
			<name>
				<long>${pcstring('STAT.${stat}.LONGNAME')}</long>
				<short>${pcstring('STAT.${stat}.NAME')}</short>
			</name>
			<score>${pcstring('STAT.${stat}')}</score>
			<modifier>${pcstring('STAT.${stat}.MOD')}</modifier>
		<!--
		Old BASE tag does not give stats with racial, and other permentant adjustments.
		Use NOTEMP.NOEQUIP instead of BASE gives the correct results.

			<base>${pcstring('STAT.${stat}.BASE')}</base>
			<basemod>${pcstring('STAT.${stat}.BASEMOD')}</basemod>
		-->
			<base>${pcstring('STAT.${stat}.NOTEMP.NOEQUIP')}</base>
			<basemod>${pcstring('STAT.${stat}.MOD.NOTEMP.NOEQUIP')}</basemod>

			<noequip>${pcstring('STAT.${stat}.NOEQUIP')}</noequip>
			<noequip_mod>${pcstring('STAT.${stat}.MOD.NOEQUIP')}</noequip_mod>
			<no_temp_score>${pcstring('STAT.${stat}.NOTEMP')}</no_temp_score>
			<no_temp_modifier>${pcstring('STAT.${stat}.MOD.NOTEMP')}</no_temp_modifier>
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
		<usealternatedamage>${pcstring('VAR.UseAlternateDamage.INTVAL')}</usealternatedamage>
		<points>${pcstring('HP')}</points>
		<alternate>${pcstring('ALTHP')}</alternate>
		<die>${pcstring('HITDICE')}</die>
		<die_short>${pcstring('HITDICE.SHORT')}</die_short>
		<current/>
		<subdual/>
		<damage_reduction>${pcstring('DR')}</damage_reduction>
		<damage_threshold>${pcstring('VAR.DAMAGETHRESHOLD.INTVAL')}</damage_threshold>
		<history>
		<@loop from=1 to=pcvar('ECL') ; level , level_has_next>
			<roll>
				<level>${level}</level>
				<roll>${pcstring('HPROLL.${level}')}</roll>
				<stat>${pcstring('HPROLL.${level}.STAT')}</stat>
				<total>${pcstring('HPROLL.${level}.TOTAL')}</total>
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
		<eac>${pcstring('AC.EAC')}</eac>
		<kac>${pcstring('AC.KAC')}</kac>
		<eac_armor>${pcstring('AC.EAC_Armor')}</eac_armor>
		<kac_armor>${pcstring('AC.KAC_Armor')}</kac_armor>
		<total>${pcstring('AC.Total')}</total>
		<listing>${pcstring('BONUS.COMBAT.AC.LISTING')}</listing>
		<flat>${pcstring('AC.Flatfooted')}</flat>
		<touch>${pcstring('AC.Touch')}</touch>
		<base>${pcstring('AC.Base')}</base>
		<armor_bonus>${pcstring('AC.Armor')}</armor_bonus>
		<shield_bonus>${pcstring('AC.Shield')}</shield_bonus>
		<stat_mod>${pcstring('AC.Ability')}</stat_mod>
		<size_mod>${pcstring('AC.Size')}</size_mod>
		<natural>${pcstring('AC.NaturalArmor')}</natural>
		<deflection>${pcstring('AC.Deflection')}</deflection>
		<dodge>${pcstring('AC.Dodge')}</dodge>
		<dodge_bonus>${pcstring('AC.Dodge')}</dodge_bonus>
		<class_bonus>${pcstring('AC.ClassDefense')}</class_bonus>
		<#if (gamemodename = "Modern" || gamemodename = "Darwins_World_2" || gamemodename = "Sidewinder") >
		<equipment_bonus>${pcstring('AC.Equipment')}</equipment_bonus>
		</#if>
		<misc>${pcstring('AC.Misc')}</misc>
		<insight>${pcstring('AC.Insight')}</insight>
		<morale>${pcstring('AC.Morale')}</morale>
		<sacred>${pcstring('AC.Sacred')}</sacred>
		<profane>${pcstring('AC.Profane')}</profane>
		<miss_chance/>
		<max_dex>${pcstring('MAXDEX')}</max_dex>
		<spell_failure>${pcstring('SPELLFAILURE')}</spell_failure>
		<check_penalty>${pcstring('ACCHECK')}</check_penalty>
		<spell_resistance>${pcstring('SR')}</spell_resistance>
		<resistance>
			<psionic/>
			<spell/>
		</resistance>
	</armor_class>
	<!--
	  ====================================
	  ====================================
			INITIATIVE
	  ====================================
	  ====================================-->
	<initiative>
		<total>${pcstring('INITIATIVEMOD')}</total>
		<dex_mod>${pcstring('STAT.1.MOD')}</dex_mod>
		<misc_mod>${pcstring('INITIATIVEMISC')}</misc_mod>
		<spell_failure>${pcstring('SPELLFAILURE')}</spell_failure>
		<check_penalty>${pcstring('ACCHECK')}</check_penalty>
		<spell_resistance>${pcstring('SR')}</spell_resistance>
		<hero_points>${pcstring('VAR.HEROPOINTS.INTVAL')}</hero_points>
		<resistances>
			<acid>${pcstring('VAR.AcidResistanceBonus.INTVAL')}</acid>
			<cold>${pcstring('VAR.ColdResistanceBonus.INTVAL')}</cold>
			<electricity>${pcstring('VAR.ElectricityResistanceBonus.INTVAL')}</electricity>
			<fire>${pcstring('VAR.FireResistanceBonus.INTVAL')}</fire>
			<force/>
			<sonic/>
		</resistances>
	</initiative>
	<!--
	  ====================================
	  ====================================
			SKILLS
	  ====================================
	  ====================================-->
	<skillinfo>
		<conditional_modifiers>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SkillBonus")-1') ; ability , ability_has_next>
			<skillbonus>
				<description>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SkillBonus.ASPECT.SkillBonus')}</description>
			</skillbonus>
		</@loop>
		</conditional_modifiers>
	</skillinfo>
	<skills>
		<conditional_modifiers>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SkillBonus")-1') ; ability , ability_has_next>
			<skillbonus>
				<description>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SkillBonus.ASPECT.SkillBonus')}</description>
			</skillbonus>
		</@loop>
		</conditional_modifiers>
		<skillpoints>
			<total>${pcstring('SKILLPOINTS.TOTAL')}</total>
			<used>${pcstring('SKILLPOINTS.USED')}</used>
			<unused>${pcstring('SKILLPOINTS.UNUSED')}</unused>
			<eclipse_total>${pcstring('VAR.CharacterSkillPts.INTVAL')}</eclipse_total>
		</skillpoints>
		<list_mods>${pcstring('SKILLLISTMODS')}</list_mods>
		<#if (pcvar("VAR.Max_Rank_Display") > 0)>
			<max_class_skill_level>${pcstring('VAR.Max_Rank_Display')}</max_class_skill_level>
		<#else>
			<max_class_skill_level>${pcstring('MAXSKILLLEVEL')}</max_class_skill_level>
		</#if>
		<max_cross_class_skill_level>${pcstring('MAXCCSKILLLEVEL')}</max_cross_class_skill_level>
	<@loop from=0 to=pcvar('count("SKILLSIT", "VIEW=VISIBLE_EXPORT")')-1 ; skill ,skill_has_next>
		<skill>
			<name>${pcstring('SKILLSIT.${skill}')}</name>
			<ranks>${pcstring('SKILLSIT.${skill}.RANK')}</ranks>
			<mod>${pcstring('SKILLSIT.${skill}.MOD')}<!-- Mods from abilities, equipment, etc -->
			</mod>
			<skill_mod>${pcstring('SKILLSIT.${skill}.TOTAL')}</skill_mod>
			<ability_mod>${pcstring('SKILLSIT.${skill}.ABMOD')}<!-- Mod from the key ability -->
			</ability_mod>
			<misc_mod>${pcstring('SKILLSIT.${skill}.MISC')}<!-- This is a calc value of TOTAL-RANK-ABMOD -->
			</misc_mod>
			<ability>${pcstring('SKILLSIT.${skill}.ABILITY')}</ability>
			<synergy>${pcstring('SKILLSIT.${skill}.SYNERGY')}</synergy>
			<untrained>${pcstring('SKILLSIT.${skill}.UNTRAINED')}</untrained>
			<exclusive>${pcstring('SKILLSIT.${skill}.EXCLUSIVE')}</exclusive>
			<trained_total>${pcstring('SKILLSIT.${skill}.TRAINED_TOTAL')}</trained_total>
			<exclusive_total>${pcstring('SKILLSIT.${skill}.EXCLUSIVE_TOTAL')}</exclusive_total>
			<classes>${pcstring('SKILLSIT.${skill}.CLASSES')}</classes>
			<type>${pcstring('SKILLSIT.${skill}.TYPE')}</type>
		</skill>
	</@loop>

	<!-- Skills -->
	</skills>
	<!--
	====================================
	====================================
			SAVING THROWS
	====================================
	====================================-->
	<saving_throws>
		<conditional_modifiers>
			<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SaveBonus")-1') ; ability , ability_has_next>
				<savebonus>
					<description>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus.ASPECT.SaveBonus')}</description>
				</savebonus>
			</@loop>
		</conditional_modifiers>
		<#assign checknum = 0 />
		<#list pc.checks as check>
		<#assign checkName = pcstring('CHECK.${checknum}.NAME')?lower_case />
		<#assign checkShortName = checkName />
		<#if (checkName = 'reflex')>
			<#assign checkShortName = checkName?substring(0,3) />
		<#elseif (checkName?length >= 4) >
			<#assign checkShortName = checkName?substring(0,4) />
		</#if>
		<saving_throw>
			<name>
				<long>${checkName}</long>
				<short>${checkShortName}</short>
			</name>
			<#if (checkName = 'fortitude')>
				<ability>constitution</ability>
			<#elseif (checkName = 'reflex')>
				<ability>dexterity</ability>
			<#elseif (checkName = 'will')>
				<ability>wisdom</ability>
			<#else>
				<ability></ability>
			</#if>
			<total>${pcstring('CHECK.${checknum}.TOTAL')}</total>
			<base>${pcstring('CHECK.${checknum}.BASE')}</base>
			<abil_mod>${pcstring('CHECK.${checknum}.STATMOD')}</abil_mod>
			<feats>${pcstring('CHECK.${checknum}.FEAT')}</feats>
			<magic_mod>${pcstring('CHECK.${checknum}.MAGIC')}</magic_mod>
			<misc_mod>${pcstring('CHECK.${checknum}.MISC.NOMAGIC.NOSTAT')}</misc_mod>
			<misc_w_magic_mod>${pcstring('CHECK.${checknum}.MISC.NOSTAT')}</misc_w_magic_mod>
			<race>${pcstring('CHECK.${checknum}.RACE')}</race>
			<epic_mod>${pcstring('CHECK.${checknum}.EPIC')}</epic_mod>
			<temp_mod/>
		</saving_throw>
		<#assign checknum = checknum + 1 />
		</#list>
	</saving_throws>
	<!--
	  ====================================
	  ====================================
			ATTACK
	  ====================================
	  ====================================-->
	<attack>
			<conditional_modifiers>
				<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=CombatBonus")-1') ; ability , ability_has_next>
				<combatbonus>
					<name>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=CombatBonus')}</name>
					<type>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=CombatBonus.TYPE')}</type>
					<description>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=CombatBonus.ASPECT.CombatBonus')}</description>
				</combatbonus>
				</@loop>
				<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SaveBonus")-1') ; ability , ability_has_next>
				<savebonus>
					<name>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus')}</name>
					<type>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus.TYPE')}</type>
					<description>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus.ASPECT.SaveBonus')}</description>
				</savebonus>
				</@loop>
			</conditional_modifiers>

		<melee>
			<total>${pcstring('ATTACK.MELEE.TOTAL')}</total>
			<total_short>${pcstring('ATTACK.MELEE.TOTAL.SHORT')}</total_short>
			<bab>${pcstring('ATTACK.MELEE.BASE')}</bab>
			<!-- ${pcstring('ATTACK.MELEE.BASE')} -->
			<base_attack_bonus>${pcstring('ATTACK.MELEE')}</base_attack_bonus>
			<stat_mod>${pcstring('ATTACK.MELEE.STAT')}</stat_mod>
			<size_mod>${pcstring('ATTACK.MELEE.SIZE')}</size_mod>
			<misc_mod>${pcstring('ATTACK.MELEE.MISC')}</misc_mod>
			<epic_mod>${pcstring('VAR.charbonusto("COMBAT","EPICAB").INTVAL')}</epic_mod>
			<!-- ${pcstring('ATTACK.MELEE.EPIC')} -->
			<temp_mod/>
		</melee>
		<ranged>
			<total>${pcstring('ATTACK.RANGED.TOTAL')}</total>
			<bab>${pcstring('ATTACK.RANGED.BASE')}</bab>
			<!-- ${pcstring('ATTACK.RANGED.BASE')} -->
			<base_attack_bonus>${pcstring('ATTACK.RANGED')}</base_attack_bonus>
			<stat_mod>${pcstring('ATTACK.RANGED.STAT')}</stat_mod>
			<size_mod>${pcstring('ATTACK.RANGED.SIZE')}</size_mod>
			<misc_mod>${pcstring('ATTACK.RANGED.MISC')}</misc_mod>
			<epic_mod>${pcstring('VAR.charbonusto("COMBAT","EPICAB").INTVAL')}</epic_mod>
			<!-- ${pcstring('ATTACK.RANGED.EPIC')} -->
			<temp_mod/>
		</ranged>
			<!-- Either CMB block, or Grapple Block -->
			<#-- |IIF(HASVAR:CMB.OR.HASFEAT:CMB Output)| -->
			<#if (pchasvar('CMB') || pcboolean('VAR.HASFEAT:CMB Output')) >
			<cmb>
				<!-- Base stuff for standard block -->
				<title>CMB</title>
				<total>${pcstring('VAR.CMB.INTVAL.SIGN')}</total>
				<bab>${pcstring('ATTACK.MELEE.BASE')}</bab>
				<base_attack_bonus>${pcstring('ATTACK.MELEE')}</base_attack_bonus>
				<stat_mod>${pcstring('VAR.CMB_STAT.INTVAL.SIGN')}</stat_mod>
				<size_mod>${pcstring('VAR.CM_SizeMod.INTVAL.SIGN')}</size_mod>
				<misc_mod></misc_mod>	<#--	This formula is giving incorrect results - STAT/STAT	${pcstring('VAR.CMB-ATTACK.MELEE.BASE-VAR.CMB_STAT-VAR.CM_SizeMod.INTVAL.SIGN')}	-->
				<epic_mod/>
				<temp_mod/>
				<!-- Base values (not yet used) -->
				<grapple_base>${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')}</grapple_base>
				<trip_base>${pcstring('VAR.CMB_Trip.INTVAL.SIGN')}</trip_base>
				<disarm_base>${pcstring('VAR.CMB_Disarm.INTVAL.SIGN')}</disarm_base>
				<sunder_base>${pcstring('VAR.CMB_Sunder.INTVAL.SIGN')}</sunder_base>
				<bullrush_base>${pcstring('VAR.CMB_Bull.INTVAL.SIGN')}</bullrush_base>
				<overrun_base>${pcstring('VAR.CMB_Overrun.INTVAL.SIGN')}</overrun_base>
				<!-- Defense values -->
				<#if (pchasvar('CMB'))>
				<!-- Pathfinder (final release) -->
				<!-- Attack values -->
				<grapple_attack>${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')}</grapple_attack>
				<trip_attack>${pcstring('VAR.CMB_Trip.INTVAL.SIGN')}</trip_attack>
				<disarm_attack>${pcstring('VAR.CMB_Disarm.INTVAL.SIGN')}</disarm_attack>
				<sunder_attack>${pcstring('VAR.CMB_Sunder.INTVAL.SIGN')}</sunder_attack>
				<bullrush_attack>${pcstring('VAR.CMB_BullRush.INTVAL.SIGN')}</bullrush_attack>
				<overrun_attack>${pcstring('VAR.CMB_Overrun.INTVAL.SIGN')}</overrun_attack>
				<!-- Defense values -->
				<defense>${pcstring('VAR.CMD.INTVAL')}</defense>
				<grapple_defense>${pcstring('VAR.CMD_Grapple.INTVAL')}</grapple_defense>
				<trip_defense>
				<#if (pchasvar("CantBeTripped"))>
				Immune
				<#else>
				${pcstring('VAR.CMD_Trip.INTVAL')}
				</#if>
				</trip_defense>
				<disarm_defense>${pcstring('VAR.CMD_Disarm.INTVAL')}</disarm_defense>
				<sunder_defense>${pcstring('VAR.CMD_Sunder.INTVAL')}</sunder_defense>
				<bullrush_defense>${pcstring('VAR.CMD_BullRush.INTVAL')}</bullrush_defense>
				<overrun_defense>${pcstring('VAR.CMD_Overrun.INTVAL')}</overrun_defense>

				<#else>
				<!-- Pathfinder Beta version -->
				<!-- Attack values -->
				<grapple_attack>${pcstring('VAR.CMB_Grapple_OFF.INTVAL.SIGN')}</grapple_attack>
				<trip_attack>${pcstring('VAR.CMB_Trip_OFF.INTVAL.SIGN')}</trip_attack>
				<disarm_attack>${pcstring('VAR.CMB_Disarm_OFF.INTVAL.SIGN')}</disarm_attack>
				<sunder_attack>${pcstring('VAR.CMB_Sunder_OFF.INTVAL.SIGN')}</sunder_attack>
				<bullrush_attack>${pcstring('VAR.CMB_Bull_OFF.INTVAL.SIGN')}</bullrush_attack>
				<overrun_attack>${pcstring('VAR.CMB_Overrun_OFF.INTVAL.SIGN')}</overrun_attack>
				<!-- Defense values -->
				<defense>${pcvar('VAR.CMB+VAR.CMB_DEF.INTVAL')}</defense>
				<grapple_defense>${pcstring('VAR.CMB_Grapple_DEF.INTVAL')}</grapple_defense>
				<trip_defense>${pcstring('VAR.CMB_Trip_DEF.INTVAL')}</trip_defense>
				<disarm_defense>${pcstring('VAR.CMB_Disarm_DEF.INTVAL')}</disarm_defense>
				<sunder_defense>${pcstring('VAR.CMB_Sunder_DEF.INTVAL')}</sunder_defense>
				<bullrush_defense>${pcstring('VAR.CMB_Bull_DEF.INTVAL')}</bullrush_defense>
				<overrun_defense>${pcstring('VAR.CMB_Overrun_DEF.INTVAL')}</overrun_defense>
				</#if>
			</cmb>
			<#else>
			<grapple>
				<total>${pcstring('ATTACK.GRAPPLE.TOTAL')}</total>
				<bab>${pcstring('ATTACK.GRAPPLE.BASE')}</bab>
				<base_attack_bonus>${pcstring('ATTACK.GRAPPLE')}</base_attack_bonus>
				<stat_mod>${pcstring('ATTACK.GRAPPLE.STAT')}</stat_mod>
				<size_mod>${pcstring('ATTACK.GRAPPLE.SIZE')}</size_mod>
				<misc_mod>${pcstring('ATTACK.GRAPPLE.MISC')}</misc_mod>
				<epic_mod>${pcstring('ATTACK.GRAPPLE.EPIC')}</epic_mod>
				<temp_mod/>
			</grapple>
			</#if>
		<!-- End CMB / Grapple Block -->
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
					<short>${pcstring('WEAPON.${weap}.NAME')}</short>
					<long>${pcstring('WEAPON.${weap}.LONGNAME')}</long>
					<output>${pcstring('WEAPON.${weap}.OUTPUTNAME')}</output>
				</name>
				<category>${pcstring('WEAPON.${weap}.CATEGORY')}</category>
				<critical>
					<range>${pcstring('WEAPON.${weap}.CRIT')}</range>
					<multiplier>${pcstring('WEAPON.${weap}.MULT')}</multiplier>
				</critical>
				<to_hit>
					<hit>${pcstring('WEAPON.${weap}.HIT')}</hit>
					<magic_hit>${pcstring('WEAPON.${weap}.MAGICHIT')}</magic_hit>
					<total_hit>${pcstring('WEAPON.${weap}.TOTALHIT')}</total_hit>
				</to_hit>
				<feat>
					<hit>${pcstring('WEAPON.${weap}.FEATHIT')}</hit>
					<damage>${pcstring('WEAPON.${weap}.FEATDAMAGE')}</damage>
				</feat>
				<magic>
					<hit>${pcstring('WEAPON.${weap}.MAGICHIT')}</hit>
					<damage>${pcstring('WEAPON.${weap}.MAGICDAMAGE')}</damage>
				</magic>
				<template>
					<hit>${pcstring('WEAPON.${weap}.TEMPLATEHIT')}</hit>
					<damage>${pcstring('WEAPON.${weap}.TEMPLATEDAMAGE')}</damage>
				</template>
				<hand>${pcstring('WEAPON.${weap}.HAND')}</hand>
				<num_attacks>${pcstring('WEAPON.${weap}.NUMATTACKS')}</num_attacks>
				<reach>${pcstring('WEAPON.${weap}.REACH')}</reach>
				<reachunit>${pcstring('WEAPON.${weap}.REACHUNIT')}</reachunit>
				<size>${pcstring('WEAPON.${weap}.SIZE')}</size>
				<#if (pcstring("WEAPON.${weap}.ISTYPE.Natural.OR.WEAPON.${weap}.CATEGORY") = "NATURAL")>
					<special_properties>${pcstring('ABILITYALL.Special Ability.${weap}.ASPECT.UnarmedNotes')}</special_properties>
				<#else>
					<special_properties>${pcstring('WEAPON.${weap}.SPROP')}</special_properties>
				</#if>
				<type>${pcstring('WEAPON.${weap}.TYPE')}</type>
				<weight>${pcstring('WEAPON.${weap}.WT')}</weight>
				<attacks>${pcstring('WEAPON.${weap}.ATTACKS')}</attacks>
				<heft>${pcstring('WEAPON.${weap}.HEFT')}</heft>
				<range>${pcstring('WEAPON.${weap}.RANGE')}</range>
				<sizemod>${pcstring('WEAPON.${weap}.SIZEMOD')}</sizemod>
				<basehit>${pcstring('WEAPON.${weap}.BASEHIT')}</basehit>
				<misc>${pcstring('WEAPON.${weap}.MISC')}</misc>
				<damage>${pcstring('WEAPON.${weap}.DAMAGE')}</damage>
				<damagebonus>${pcstring('WEAPON.${weap}.DAMAGEBONUS')}</damagebonus>
				<basedamagebonus>${pcstring('WEAPON.${weap}.BASEDAMAGEBONUS')}</basedamagebonus>
				<thdamagebonus>${pcstring('WEAPON.${weap}.THDAMAGEBONUS')}</thdamagebonus>
				<ohdamagebonus>${pcstring('WEAPON.${weap}.OHDAMAGEBONUS')}</ohdamagebonus>
				<rateoffire>${pcstring('WEAPON.${weap}.RATEOFFIRE')}</rateoffire>
				<islight>${pcstring('WEAPON.${weap}.ISLIGHT')}</islight>
				<quality>${pcstring('WEAPON.${weap}.QUALITY')}</quality>
				<charges>${pcstring('WEAPON.${weap}.CHARGES')}</charges>
				<sequence>${weap}</sequence>
			</common>
			</#macro>
			<#macro weapMeleeBlock weap>
			<melee>
				<invalidtext>
					<tohit>${pcstring('INVALIDTEXT.TOHIT')}</tohit>
					<damage>${pcstring('INVALIDTEXT.DAMAGE')}</damage>
				</invalidtext>
				<hand>${pcstring('WEAPON.${weap}.HAND')}</hand>
				<w1_h1_p>
					<!-- One weapon, 1 hand, primary hand -->
					<to_hit>${pcstring('WEAPON.${weap}.BASEHIT')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.BASICDAMAGE')}</damage>
				</w1_h1_p>
				<w1_h1_o>
					<!-- One weapon, 1 handed, offhand -->
					<to_hit>${pcstring('WEAPON.${weap}.OHHIT')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.OHDAMAGE')}</damage>
				</w1_h1_o>
				<w1_h2>
					<!-- One weapon, 2 handed -->
					<to_hit>${pcstring('WEAPON.${weap}.THHIT')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.THDAMAGE')}</damage>
				</w1_h2>
				<w2_p_oh>
					<!-- Two weapons, this weapon in primary hand, other hand with heavy weapon -->
					<to_hit>${pcstring('WEAPON.${weap}.TWPHITH')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.BASICDAMAGE')}</damage>
				</w2_p_oh>
				<w2_p_ol>
					<!-- Two weapons, this weapon in primary hand, other hand with light weapon -->
					<to_hit>${pcstring('WEAPON.${weap}.TWPHITL')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.BASICDAMAGE')}</damage>
				</w2_p_ol>
				<w2_o>
					<!-- Two weapons, this weapon in off-hand -->
					<to_hit>${pcstring('WEAPON.${weap}.TWOHIT')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.OHDAMAGE')}</damage>
				</w2_o>
			</melee>
			</#macro>
			<#macro weapRangeBlock weap range>
				<#if (pcstring('WEAPON.${weap}.RANGELIST.${range}') != "") >
				<range>
					<distance>${pcstring('WEAPON.${weap}.RANGELIST.${range}')}</distance>
					<to_hit>${pcstring('WEAPON.${weap}.RANGELIST.${range}.TOTALHIT')}</to_hit>
					<damage>${pcstring('WEAPON.${weap}.RANGELIST.${range}.DAMAGE')}</damage>
					<basehit>${pcstring('WEAPON.${weap}.RANGELIST.${range}.BASEHIT')}</basehit>
					<tohit_offhand>${pcstring('WEAPON.${weap}.RANGELIST.${range}.OHHIT')}</tohit_offhand>
					<tohit_twohand>${pcstring('WEAPON.${weap}.RANGELIST.${range}.BASEHIT')}</tohit_twohand>
					<tohit_2weap_heavy>${pcstring('WEAPON.${weap}.RANGELIST.${range}.TWPHITH')}</tohit_2weap_heavy>
					<tohit_2weap_light>${pcstring('WEAPON.${weap}.RANGELIST.${range}.TWPHITL')}</tohit_2weap_light>
					<tohit_2weap_offhand>${pcstring('WEAPON.${weap}.RANGELIST.${range}.TWOHIT')}</tohit_2weap_offhand>
					<@loop from=0 to=pcvar('WEAPON.${weap}.CONTENTS-1') ; ammo , ammo_has_next>
					<ammunition>
						<name>${pcstring('WEAPON.${weap}.CONTENTS.${ammo}')}</name>
						<special_properties>${pcstring('WEAPON.${weap}.CONTENTS.${ammo}.SPROP')}</special_properties>
						<quantity>${pcstring('EQ.IS.WEAPON.${weap}.CONTENTS.${ammo}.QTY')}</quantity>
						<to_hit>${pcstring('WEAPON.${weap}.RANGELIST.${range}.CONTENTS.${ammo}.TOTALHIT')}</to_hit>
						<damage>${pcstring('WEAPON.${weap}.RANGELIST.${range}.CONTENTS.${ammo}.DAMAGE')}</damage>
					</ammunition>
					</@loop>
				</range>
				</#if>
			</#macro>

	<weapons>
		<#if (pcvar("VAR.UseMartialArts") = 1)>
		<martialarts>
			<total>${pcstring('WEAPONH.TOTALHIT')}</total>
			<#if (pcvar("VAR.MartialArtsBonusDamage") < 0)>
			<damage>${pcstring('VAR.MartialArtsDie.INTVAL')}d${pcstring('VAR.MartialArtsDieSize.INTVAL')}-${pcstring('VAR.MartialArtsBonusDamage.INTVAL')}</damage>
			<#else>
			<damage>${pcstring('VAR.MartialArtsDie.INTVAL')}d${pcstring('VAR.MartialArtsDieSize.INTVAL')}+${pcstring('VAR.MartialArtsBonusDamage.INTVAL')}</damage>
			</#if>
			<critical>${pcstring('WEAPONH.CRIT')}/x${pcstring('WEAPONH.MULT')}</critical>
			<!-- Should be changed to a variable due to improved crit -->
			<reach>${pcstring('REACH')}</reach>
			<special_property>
			<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=UnarmedDisplay")-1') ; ability , ability_has_next>
			${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=UnarmedDisplay.ASPECT.UnarmedNotes')}
			</@loop>
			</special_property>
		</martialarts>
		<#else>
		<unarmed>
			<#assign fab = pcstring('WEAPONH.TOTALHIT')?keep_before("/")?number>
			<flurry_level>${pcvar('VAR.FlurryLVL.INTVAL')}</flurry_level>
			<flurry_attacks>${pcvar('VAR.FlurryAttacks.INTVAL')}</flurry_attacks>
			<fab_1>${pcvar('VAR.FAB_1.INTVAL')}</fab_1>
			<fab_2>${pcvar('VAR.FAB_2.INTVAL')}</fab_2>
			<fab_3>${pcvar('VAR.FAB_3.INTVAL')}</fab_3>
			<fab_4>${pcvar('VAR.FAB_4.INTVAL')}</fab_4>
			<fab_5>${pcvar('VAR.FAB_5.INTVAL')}</fab_5>
			<fab_6>${pcvar('VAR.FAB_6.INTVAL')}</fab_6>
			<fab_7>${pcvar('VAR.FAB_7.INTVAL')}</fab_7>
			<fab_8>${pcvar('VAR.FAB_8.INTVAL')}</fab_8>
			<fab_9>${pcvar('VAR.FAB_9.INTVAL')}</fab_9>
			<total>${pcstring('WEAPONH.TOTALHIT')}</total>
			<to_hit>${fab}</to_hit>
			<damage>${pcstring('WEAPONH.DAMAGE')}</damage>
			<critical>${pcstring('WEAPONH.CRIT')}/x${pcstring('WEAPONH.MULT')}</critical>
			<!-- Should be changed to a variable due to improved crit -->
			<reach>${pcstring('REACH')}</reach>
			<special_property>
			<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=UnarmedDisplay")-1') ; ability , ability_has_next>
			${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=UnarmedDisplay.ASPECT.UnarmedNotes')}
			</@loop>
			</special_property>
						<!-- Commenting this out (will need a test as well)
			3.0 uses "Subdual", 3.5 uses "nonlethal".  We'll need a separate node for both.	-->
			<#if (gamemodename = "3e")>
				<#if (pcvar('VAR.Unarmed') > 0)>
					<type>(subdual or normal)</type>
				<#else>
					<type>(subdual only)</type>
				</#if>
			<#else>
				<#if (pcvar('VAR.UnarmedLethal') > 0)>
					<type>(lethal or nonlethal)</type>
				<#else>
					<type>(nonlethal only)</type>
				</#if>
			</#if>
		</unarmed>
		</#if>


		<#-- Equipped weapon loop -->
		<@loop from=0 to=pcvar('COUNT[EQTYPE.WEAPON]-1') ; weap , weap_has_next><#-- TODO: Loop was of early exit type 1 -->
		<#assign weaponCategory>
			${pcstring('WEAPON.${weap}.CATEGORY')?lower_case}
		</#assign>
		<#if (weaponCategory?contains('both'))>

		<#if (weaponCategory?contains('ranged'))>
		<#else><#-- IIF(WEAPON.${weap}.CATEGORY:Ranged) -->
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<@weapMeleeBlock weap="${weap}" />
			</#if><#-- IIF(WEAPON.${weap}.CATEGORY:Ranged) -->
			<#if (weaponCategory?contains('ranged'))><#-- We work out now whether this is a Ranged Only or Thrown -->
			<#if (pcboolean('WEAPON.${weap}.ISTYPE.Thrown'))><#-- Valid only if we find the Thrown Value -->
			<ranges>
				<rangetype>Thrown</rangetype>
			<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
				<@loop from=0 to=5 ; range , range_has_next>
				<@weapRangeBlock weap="${weap}" range="${range}" />
				</@loop><#-- Range -->
			</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->
			</ranges>
			<#else><#-- IIF(WEAPON.${weap}.ISTYPE.Thrown) but IS Ranged -->

			<!-- New Ranges Section -->
			<ranges>
			<rangetype>Ranged</rangetype><!-- ranged first -->
			<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
				<@loop from=0 to=10 ; range , range_has_next>
				<@weapRangeBlock weap="${weap}" range="${range}" />
				</@loop><#-- Range -->
			</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->
			</ranges>

	</#if><#-- IIF(WEAPON.${weap}.ISTYPE.Thrown) -->
		</weapon>
	<#else><#-- CATEGORY:Ranged) -->
	</#if><#-- CATEGORY:Ranged) -->
	<!-- End New Ranges Section -->
	<#else><#-- IIF(WEAPON.${weap}.CATEGORY:BOTH) -->
	<#if (weaponCategory?contains('ranged'))>
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<#if (pcstring("WEAPON.${weap}.CONTENTS") = "0")>
			<ranges>
			<#if (pcboolean('WEAPON.${weap}.ISTYPE.Thrown'))>

				<rangetype>Thrown</rangetype>
				<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
					<@loop from=0 to=5 ; range , range_has_next>
						<@weapRangeBlock weap="${weap}" range="${range}" />
					</@loop><!-- Range -->
				</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->
				<#else><#-- Thrown -->
				<rangetype>Ranged</rangetype><!-- ranged second -->
			<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
				<@loop from=0 to=10 ; range , range_has_next>
				<@weapRangeBlock weap="${weap}" range="${range}" />
				</@loop><#-- Range -->
			</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->
			</#if><#-- Thrown -->
			</ranges>
			<#else><#-- IIF(WEAPON.${weap}.CONTENTS:0) -->
			<@loop from=0 to=pcvar('WEAPON.${weap}.CONTENTS-1') ; ammo , ammo_has_next>
			<ranges>
					<ammunition>
						<name>${pcstring('WEAPON.${weap}.CONTENTS.${ammo}')}</name>
						<special_properties>${pcstring('WEAPON.${weap}.CONTENTS.${ammo}.SPROP')}</special_properties>
						<quantity>${pcstring('EQ.IS.WEAPON.${weap}.CONTENTS.${ammo}.QTY')}</quantity>
						<to_hit>${pcstring('WEAPON.${weap}.RANGELIST.${weap}.CONTENTS.${ammo}.TOTALHIT')}</to_hit>
						<damage>${pcstring('WEAPON.${weap}.RANGELIST.${weap}.CONTENTS.${ammo}.DAMAGE')}</damage>
					</ammunition>
				<ammunition>
						<name>${pcstring('WEAPON.${weap}.CONTENTS.${ammo}')}</name>
				</ammunition>
				<#if (pcboolean('WEAPON.${weap}.ISTYPE.Thrown'))>
				<rangetype>Thrown</rangetype>
				<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
					<@loop from=0 to=5 ; range , range_has_next>
					<@weapRangeBlock weap="${weap}" range="${range}" />
					</@loop><#-- Range -->
				</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->
				<#else><#--IIF(WEAPON.%weap.ISTYPE.Thrown) -->
				<rangetype>Ranged</rangetype><!-- Ranged third -->
				<#if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0)>
					<@loop from=0 to=10 ; range , range_has_next>
					<@weapRangeBlock weap="${weap}" range="${range}" />
					</@loop><#-- Range -->
				</#if><#-- if (pcvar("WEAPON.${weap}.RANGE.NOUNITS") > 0) -->

			</#if><#--IIF(WEAPON.%weap.ISTYPE.Thrown) -->

			</ranges>
		</@loop><#-- FOR,${ammo},0,WEAPON.${weap}.CONTENTS-1,1,1 -->
		</#if><#-- IIF(WEAPON.${weap}.CONTENTS:0) -->
		</weapon>

		<#else><#-- IIF(WEAPON.${weap}.CATEGORY:Ranged) -->
	<!--	Request to remove the Double Weapon Block OS-133		-->
		<#if (pcboolean('WEAPON.${weap}.ISTYPE.Double') || pcboolean('WEAPON.${weap}.ISTYPE.TwoHanded') || weaponCategory?contains('non-standard-melee') || weaponCategory?contains('natural'))>
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<simple>
				<to_hit>${pcstring('WEAPON.${weap}.TOTALHIT')}</to_hit>
				<damage>${pcstring('WEAPON.${weap}.DAMAGE')}</damage>
				<range>${pcstring('WEAPON.${weap}.RANGE')}</range>
				<!-- This is an Addition by Itwally for the Monk Flurry of Blows Fix per DATA-73 -->
				<name>${pcstring('WEAPON.${weap}.NAME')}</name>
				<class><@loop from=0 to=pcvar('countdistinct("CLASSES")')-1 ; class , class_has_next ><#rt>
				<#t><@pcstring tag="CLASSABB.${class}"/><@pcstring tag="CLASS.${class}.LEVEL"/><#if class_has_next> </#if>
			<#t></@loop></class>
		<!-- End DATA-73 Work Around-->
			</simple>
		</weapon>
		<#else><#-- IIF(WEAPON.${weap}.ISTYPE.Double.OR.WEAPON.${weap}.CATEGORY:Non-Standard-Melee) -->
		<weapon>
			<@weapCommonBlock weap="${weap}" />
			<@weapMeleeBlock weap="${weap}" />
		</weapon>
		</#if><#-- IIF(WEAPON.weap}.ISTYPE.Double.OR.WEAPON.${weap}.CATEGORY:Non-Standard-Melee) -->
		</#if><#-- IIF(WEAPON.weap}.CATEGORY:Ranged) -->
		</#if><#-- IIF(WEAPON.weap}.CATEGORY:BOTH) -->
	</@loop><#-- FOR,weap},0,COUNT[EQTYPE.WEAPON]-1,1,1 -->
	</weapons>
	<!--
	  ====================================
	  ====================================
			ARMOR
	  ====================================
	  ====================================-->
	<protection>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.Armor]-1') ; armor , armor_has_next>
		<armor>
			<name>${pcstring('ARMOR.Armor.ALL.${armor}.NAME')}</name>
			<acbonus>${pcstring('ARMOR.Armor.ALL.${armor}.ACBONUS')}</acbonus>
			<accheck>${pcstring('ARMOR.Armor.ALL.${armor}.ACCHECK')}</accheck>
			<baseac>${pcstring('ARMOR.Armor.ALL.${armor}.BASEAC')}</baseac>
			<edr>${pcstring('ARMOR.Armor.ALL.${armor}.EDR')}</edr>
			<maxdex>${pcstring('ARMOR.Armor.ALL.${armor}.MAXDEX')}</maxdex>
			<move>${pcstring('ARMOR.Armor.ALL.${armor}.MOVE')}</move>
			<spellfail>${pcstring('ARMOR.Armor.ALL.${armor}.SPELLFAIL')}</spellfail>
			<special_properties>${pcstring('ARMOR.Armor.ALL.${armor}.SPROP')}</special_properties>
			<totalac>${pcstring('ARMOR.Armor.ALL.${armor}.TOTALAC')}</totalac>
			<type>${pcstring('ARMOR.Armor.ALL.${armor}.TYPE')}</type>
			<wt>${pcstring('ARMOR.Armor.ALL.${armor}.WT')}</wt>
			<fulltype>${pcstring('EQTYPE.Armor.${armor}.TYPE')}</fulltype>
			<location>${pcstring('EQTYPE.Armor.${armor}.LOCATION')}</location>
		</armor>
		</@loop>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.SHIELD]-1') ; armor , armor_has_next>
		<shield>
			<name>${pcstring('ARMOR.SHIELD.ALL.${armor}.NAME')}</name>
			<acbonus>${pcstring('ARMOR.SHIELD.ALL.${armor}.ACBONUS')}</acbonus>
			<accheck>${pcstring('ARMOR.SHIELD.ALL.${armor}.ACCHECK')}</accheck>
			<baseac>${pcstring('ARMOR.SHIELD.ALL.${armor}.BASEAC')}</baseac>
			<edr>${pcstring('ARMOR.SHIELD.ALL.${armor}.EDR')}</edr>
			<maxdex>${pcstring('ARMOR.SHIELD.ALL.${armor}.MAXDEX')}</maxdex>
			<move>${pcstring('ARMOR.SHIELD.ALL.${armor}.MOVE')}</move>
			<spellfail>${pcstring('ARMOR.SHIELD.ALL.${armor}.SPELLFAIL')}</spellfail>
			<special_properties>${pcstring('ARMOR.SHIELD.ALL.${armor}.SPROP')}</special_properties>
			<totalac>${pcstring('ARMOR.SHIELD.ALL.${armor}.TOTALAC')}</totalac>
			<type>${pcstring('ARMOR.SHIELD.ALL.${armor}.TYPE')}</type>
			<wt>${pcstring('ARMOR.SHIELD.ALL.${armor}.WT')}</wt>
		</shield>
		</@loop>
		<@loop from=0 to=pcvar('COUNT[EQTYPE.ACITEM]-1') ; armor , armor_has_next>
		<item>
			<name>${pcstring('ARMOR.ACITEM.${armor}.NAME')}</name>
			<acbonus>${pcstring('ARMOR.ACITEM.${armor}.ACBONUS')}</acbonus>
			<accheck>${pcstring('ARMOR.ACITEM.${armor}.ACCHECK')}</accheck>
			<baseac>${pcstring('ARMOR.ACITEM.${armor}.BASEAC')}</baseac>
			<edr>${pcstring('ARMOR.ACITEM.${armor}.EDR')}</edr>
			<maxdex>${pcstring('ARMOR.ACITEM.${armor}.MAXDEX')}</maxdex>
			<move>${pcstring('ARMOR.ACITEM.${armor}.MOVE')}</move>
			<spellfail>${pcstring('ARMOR.ACITEM.${armor}.SPELLFAIL')}</spellfail>
			<special_properties>${pcstring('ARMOR.ACITEM.${armor}.SPROP')}</special_properties>
			<totalac>${pcstring('ARMOR.ACITEM.${armor}.TOTALAC')}</totalac>
			<type>${pcstring('ARMOR.ACITEM.${armor}.TYPE')}</type>
			<wt>${pcstring('ARMOR.ACITEM.${armor}.WT')}</wt>
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
		<!-- D&D 3.0 -->
		<#if (pcvar("VAR.BardicMusicLevel") >= 1) >
		<bardic_music>
			<uses_per_day>${pcstring('VAR.BardicMusicLevel.INTVAL')}</uses_per_day>
			<effects>Effects (Perform ranks required)</effects>
			<text>Inspire Courage(3), Countersong(3), Fascinate(3),Inspire Competence(6), Suggestion(9), Inspire Greatness(12)</text>
		</bardic_music>
		</#if>
		<!-- D&D 3.5 -->
		<#if (pcvar("VAR.BardicMusicTimes") >= 1) >
		<bardic_music>
			<uses_per_day>${pcstring('VAR.BardicMusicTimes.INTVAL')}</uses_per_day>
			<text>
			</#if>
			<#if (pcvar("VAR.CountersongDuration") >= 1) >
			Countersong(duration = ${pcstring('VAR.CountersongDuration.INTVAL')} rounds)
			</#if>
			<#if (pcvar("VAR.FascinateCreatures.INTVAL") >= 1) >
			Fascinate(up to ${pcstring('VAR.FascinateCreatures.INTVAL')} creatures for up to ${pcstring('VAR.FacinateDuration.INTVAL')} rounds)
			</#if>
			<#if (pcvar("VAR.InspireCourageSaves") >= 1) >
			Inspire Courage(save bonus = ${pcstring('VAR.InspireCourageSaves.INTVAL.SIGN')}, attack and damage bonus = ${pcstring('VAR.InspireCourageAttack.INTVAL.SIGN')})
			</#if>
			<#if (pcvar("VAR.InspireCompetenceBonus.INTVAL") >= 1) >
			Inspire Competence(skill check bonus = ${pcstring('VAR.InspireCompetenceBonus.INTVAL.SIGN')} for up to ${pcstring('VAR.InspireCompetenceDuration.INTVAL')} minutes)
			</#if>
			<#if (pcvar("VAR.SingleSuggestionDC.INTVAL") >= 1) >
			Suggestion(DC: ${pcstring('VAR.SingleSuggestionDC.INTVAL')})
			</#if>
			<#if (pcvar("VAR.InspireGreatnessAllies.INTVAL") >= 1) >
			Inspire Greatness(number of allies = ${pcstring('VAR.InspireGreatnessAllies.INTVAL')}, bonus HD = ${pcstring('VAR.InspireGreatnessHD.INTVAL')}, attack bonus = ${pcstring('VAR.InspireGreatnessAttack.INTVAL.SIGN')}, Fortitude bonus = ${pcstring('VAR.InspireGreatnessSaves.INTVAL.SIGN')})
			</#if>
			<#if (pcvar("VAR.SongOfFreedomLVL.INTVAL") >= 1) >
			Song of Freedom(effective caster level = ${pcstring('VAR.SongOfFreedomLVL.INTVAL')})
			</#if>
			<#if (pcvar("VAR.InspireHeroicsAllies.INTVAL") >= 1) >
			Inspire Greatness(number of allies = ${pcstring('VAR.InspireHeroicsAllies.INTVAL')}, save bonus = ${pcstring('VAR.InspireHeroicsSaves.INTVAL.SIGN')}, dodge bonus = ${pcstring('VAR.InspireHeroicsDodge.INTVAL.SIGN')})
			</#if>
			<#if (pcvar("VAR.MassSuggestionDC.INTVAL") >= 1) >
			Mass Suggestion(DC: ${pcstring('VAR.MassSuggestionDC.INTVAL')})
			</#if>
			<#if (pcvar("VAR.BardicMusicTimes") >= 1) >
			</text>
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

	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","ASPECT=TurnType")-1') ; turncount , turncount_has_next>
	<turning kind="${pcstring('TEXT.UPPER.ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnKind')}" type="${pcstring('TEXT.UPPER.ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnType')}">
		<level>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel.INTVAL')}</level>
		<turn_check>1d20${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnCheck.INTVAL.SIGN')}</turn_check>
		<damage>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnDamage')}</damage>
		<uses_per_day>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnTimes.INTVAL')}</uses_per_day>
		<notes>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnNotes')}</notes>
	</turning>
	</@loop>

	<!-- Channel Energy -->
	<#if (pcvar("VAR.ChannelEnergyLVL") >= 1) >
	<channel_energy>
		<level>${pcstring('VAR.ChannelEnergyLVL.INTVAL')}</level>
		<uses_per_day>${pcstring('VAR.ChannelEnergyTimes.INTVAL')}</uses_per_day>
		<uses_per_day.title>Uses per day</uses_per_day.title>
		<save_dc>${pcstring('VAR.ChannelEnergyDC.INTVAL')}</save_dc>
		<dice>${pcstring('VAR.ChannelEnergyDice.INTVAL')}</dice>
		<die_size>${pcstring('VAR.ChannelEnergyDieSize.INTVAL')}</die_size>
		<#if (pcstring("VAR.ChannelPositiveEnergyDC") = "1")>
		<description>You can unleash a wave of positive energy dealing ${pcstring('VAR.ChannelEnergyDice.INTVAL')}d${pcstring('VAR.ChannelEnergyDieSize.INTVAL')} (DC ${pcstring('VAR.ChannelEnergyDC.INTVAL')} for half)</description>
		<#else>
		<description>You can unleash a wave of negative energy dealing ${pcstring('VAR.ChannelEnergyDice.INTVAL')}d${pcstring('VAR.ChannelEnergyDieSize.INTVAL')} (DC ${pcstring('VAR.ChannelEnergyDC.INTVAL')} for half)</description>
		</#if>
	</channel_energy>
	</#if>

		<#if (pcvar("VAR.KiPoolLVL") >= 1) >
		<ki_pool>
			<uses_per_day>${pcstring('VAR.KiPoints.INTVAL')}</uses_per_day>
		</ki_pool>
		</#if>	<!-- 3.0 stunning fist -->

		<#if (pcvar("VAR.StunningAttack") >= 1) >
		<stunning_fist>
			<save_dc>${pcstring('VAR.StunDC.INTVAL')}</save_dc>
			<uses_per_day>${pcstring('VAR.StunningAttack.INTVAL')}</uses_per_day>
		</stunning_fist>
		</#if>	<!-- 3.0 stunning fist -->

		<#if (pcvar("VAR.StunningFistAttack") >= 1) >
		<stunning_fist>
			<save_dc>${pcstring('VAR.StunningFistDC.INTVAL')}</save_dc>
			<uses_per_day>${pcstring('VAR.StunningFistAttack.INTVAL')}</uses_per_day>
			<description>You know just where to strike to temporarily stun a foe. ${pcstring('VAR.StunningFistAttack.INTVAL')}/day (DC ${pcstring('VAR.StunningFistDC.INTVAL')})</description>
		</stunning_fist>
		</#if>	<!-- 3.5 stunning fist -->

		<#if (pcvar("VAR.WholenessHpLVL") >= 1) >
		<wholeness_of_body>
			<hp_per_day>${pcstring('VAR.WholenessHpLVL.INTVAL*2')}</hp_per_day>
		</wholeness_of_body>
		</#if>	<!-- 3.0 wholeness of body -->

		<#if (pcvar("VAR.WholenessBody") >= 1) >
		<wholeness_of_body>
			<hp_per_day>${pcstring('VAR.WholenessBody.INTVAL')}</hp_per_day>
		</wholeness_of_body>
		</#if>	<!-- 3.5 wholeness of body -->

		<#if (pcvar("VAR.TOTALPOWERPOINTS") >= 1) >	<!-- Psionics -->
		<psionics>
			<#if (pchasvar("Manifester") || pchasvar("PsychicWarriorManifester"))>
			<type>3.0</type>
			<#else>
			<type>3.5</type>
			</#if>
			<base_pp>${pcstring('VAR.BASEPOWERPOINTS.INTVAL')}</base_pp>
			<bonus_pp>${pcstring('VAR.BONUSPOWERPOINTS.INTVAL')}</bonus_pp>
			<total_pp>${pcstring('VAR.TOTALPOWERPOINTS.INTVAL')}</total_pp>
		</psionics>
		</#if>	<!-- Psionics -->

		<#if (pcvar("VAR.LayOnHands") >= 1) > <!-- D&D 3.0 and 3.5 -->
		<layonhands>
			<hp_per_day>${pcstring('VAR.LayOnHands.INTVAL')}</hp_per_day>
			<hp_per_day.title>HP per day</hp_per_day.title>
		</layonhands>
		</#if>

<!-- Pathfinder 
		<#if (pcvar("VAR.LayOnHandsTimes") >= 1) > 
		<layonhands>
			<hp_per_day>${pcstring('VAR.LayOnHandsTimes.INTVAL')}</hp_per_day>
			<hp_per_day.title>Uses per day</hp_per_day.title>
			<description>cure ${pcstring('VAR.LayOnHandsDice.INTVAL')}d6 per use</description>
		</layonhands>
		</#if>
		-->

		<#if (pcvar("VAR.WildshapeTimes") >= 1) >
		<wildshape>
			<uses_per_day>${pcstring('VAR.WildShapeTimes.INTVAL')}</uses_per_day>
			<elemental_uses_per_day>${pcstring('VAR.WildShapeElementalTimes.INTVAL')}</elemental_uses_per_day>
			<duration>${pcstring('VAR.WildShapeDuration.INTVAL')}</duration>
		</wildshape>
		</#if>


		<#if (pcvar("VAR.LeadershipScore") >= 1) >
		<leadership>
			<score>${pcstring('VAR.LeadershipScore.INTVAL')}</score>
			<max_cohort_level>${pcstring('VAR.LeadershipMaxCohortLvl')}</max_cohort_level>
		</leadership>
		</#if>
	</class_features>

	<!-- Abilites with check lists - master/child abilities -->
	<checklists>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","ASPECT=CheckType")-1') ; ability , ability_has_next>
		<checklist>
		<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.TYPE")?lower_case?contains("extraordinary"))>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType')} (Ex)</name>
		<#else>
		<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.TYPE")?lower_case?contains("supernatural"))>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType')} (Su)</name>
		<#else>
		<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.TYPE")?lower_case?contains("spelllike"))>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType')} (Sp)</name>
		<#else>
		</#if>
		</#if>
		</#if>
			<header>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType')}</header>
			<description>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.ASPECT.DESC')}</description>
			<type>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.TYPE')}</type>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${ability}.ASPECT=CheckType.SOURCE')}</source>
			<check_count>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.ASPECT.CheckCount.INTVAL')}</check_count>
			<check_type>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.ASPECT.CheckType')}</check_type>
			<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.HASASPECT.MasterAbility") = "Y")>
			<master>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.ASPECT.MasterAbility')}</master>
			<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability")-1') ; subability , subability_has_next>
			<#if (pcstring("ABILITYALL.Special Ability.${subability}.ASPECT.ChildAbility") = pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=CheckType.ASPECT.MasterAbility"))>
			<subability>
				<name>${pcstring('ABILITYALL.Special Ability.${subability}')}</name>
				<description>${pcstring('ABILITYALL.Special Ability.${subability}.DESC')}</description>
				<type>${pcstring('ABILITYALL.Special Ability.${subability}.TYPE')}</type>
				<source>${pcstring('ABILITYALL.Special Ability.${subability}.SOURCE')}</source>
				<child>${pcstring('ABILITYALL.Special Ability.${subability}.ASPECT.ChildAbility')}</child>
			</subability>
			</#if>
			</@loop>
			</#if>
		</checklist>
	</@loop>
	</checklists>

	<!-- Proficiency lists -->
	<proficiency_specials>
	<#if (pcvar('countdistinct("ABILITIES";"CATEGORY=Special Ability";"TYPE=ProfOutput")') = 0)>
	<#else>
	<!--  -->
	</#if>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=ProfOutput")-1') ; ability , ability_has_next>
		<proficiency>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=ProfOutput')}</name>
			<proficient>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=ProfOutput.ASPECT.Proficiency')}</proficient>
			<forte>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=ProfOutput.ASPECT.Forte')}</forte>
		</proficiency>
	</@loop>
	</proficiency_specials>

	<!--
	  ====================================
	  ====================================
			EQUIPMENT
	  ====================================
	  ====================================-->
	<equipment>
		<@loop from=0 to=pcvar('COUNT[EQUIPMENT.MERGELOC]-1') ; equip , equip_has_next><#lt><#-- TODO: Loop was of early exit type 1 -->
		<item>
			<longname>${pcstring('EQ.MERGELOC.${equip}.LONGNAME')}</longname>
			<id>${pcstring('EQ.MERGELOC.${equip}.ID')}</id>
			<name>${pcstring('EQ.MERGELOC.${equip}.NAME')}</name>
			<carried>${pcstring('EQ.MERGELOC.${equip}.CARRIED')}</carried>
			<charges>${pcstring('EQ.MERGELOC.${equip}.CHARGES')}</charges>
			<charges_used>${pcstring('EQ.MERGELOC.${equip}.CHARGESUSED')}</charges_used>
			<contents>${pcstring('EQ.MERGELOC.${equip}.CONTENTS')}</contents>
			<contents_num>${pcstring('EQ.MERGELOC.${equip}.CONTENTSNUM')}</contents_num>
			<content_weight>${pcstring('EQ.MERGELOC.${equip}.CONTENTWEIGHT')}</content_weight>
			<cost>${pcstring('EQ.MERGELOC.${equip}.COST')}</cost>
			<equipped>${pcstring('EQ.MERGELOC.${equip}.EQUIPPED')}</equipped>
			<location>${pcstring('EQ.MERGELOC.${equip}.LOCATION')}</location>
			<locationId>${pcstring('EQ.MERGELOC.${equip}.LOCATIONID')}</locationId>
			<maxcharges>${pcstring('EQ.MERGELOC.${equip}.MAXCHARGES')}</maxcharges>
			<note>${pcstring('EQ.MERGELOC.${equip}.NOTE')}</note>
			<quantity>${pcstring('EQ.MERGELOC.${equip}.QTY')}</quantity>
			<checkbox>${pcstring('EQ.MERGELOC.${equip}.CHECKBOXES')}</checkbox>
			<size>
				<long>${pcstring('EQ.MERGELOC.${equip}.SIZELONG')}</long>
				<short>${pcstring('EQ.MERGELOC.${equip}.SIZE')}</short>
			</size>
			<special_properties>${pcstring('EQ.MERGELOC.${equip}.SPROP')}</special_properties>
			<type>${pcstring('EQ.MERGELOC.${equip}.TYPE')}</type>
			<weight>${pcstring('EQ.MERGELOC.${equip}.WT')}</weight>
			<total_wt>${pcstring('EQ.MERGELOC.${equip}.TOTALWT')}</total_wt>
			<total_weight>${pcstring('EQ.MERGELOC.${equip}.TOTALWEIGHT')}</total_weight>
			<bonuslist>${pcstring('EQ.MERGELOC.${equip}.BONUSLIST')}</bonuslist>
			<acmod>${pcstring('EQ.MERGELOC.${equip}.ACMOD')}</acmod>
			<maxdex>${pcstring('EQ.MERGELOC.${equip}.MAXDEX')}</maxdex>
			<accheck>${pcstring('EQ.MERGELOC.${equip}.ACCHECK')}</accheck>
			<edr>${pcstring('EQ.MERGELOC.${equip}.EDR')}</edr>
			<move>${pcstring('EQ.MERGELOC.${equip}.MOVE')}</move>
			<spell_failure>${pcstring('EQ.MERGELOC.${equip}.SPELLFAILURE')}</spell_failure>
			<damage>${pcstring('EQ.MERGELOC.${equip}.DAMAGE')}</damage>
			<damage_alt>${pcstring('EQ.MERGELOC.${equip}.ALTDAMAGE')}</damage_alt>
			<crit_range>${pcstring('EQ.MERGELOC.${equip}.CRITRANGE')}</crit_range>
			<crit_mult>${pcstring('EQ.MERGELOC.${equip}.CRITMULT')}</crit_mult>
			<crit_range_alt>${pcstring('EQ.MERGELOC.${equip}.ALTCRITRANGE')}</crit_range_alt>
			<crit_mult_alt>${pcstring('EQ.MERGELOC.${equip}.ALTCRITMULT')}</crit_mult_alt>
			<range>${pcstring('EQ.MERGELOC.${equip}.RANGE')}</range>
			<attacks>${pcstring('EQ.MERGELOC.${equip}.ATTACKS')}</attacks>
			<prof>${pcstring('EQ.MERGELOC.${equip}.PROF')}</prof>
			<source>${pcstring('EQ.MERGELOC.${equip}.SOURCE')}</source>
			<quality>${pcstring('EQ.MERGELOC.${equip}.QUALITY')}</quality>
		</item>
		</@loop><#lt><#-- Equipment -->
		<total>
			<weight>${pcstring('TOTAL.WEIGHT')}</weight>
			<value>${pcstring('TOTAL.VALUE')}</value>
			<load>${pcstring('TOTAL.LOAD')}</load>
			<capacity>${pcstring('TOTAL.CAPACITY')}</capacity>
		</total>
		<equipmentsets>
		<@equipsetloop><#lt>
		<equipmentset name="${pcstring('EQSET.NAME')}">
			<@loop from=0 to=pcvar('COUNT[EQUIPMENT.MERGELOC]-1') ; equip , equip_has_next>
				<item>
					<longname>${pcstring('EQ.MERGELOC.${equip}.LONGNAME')}</longname>
					<name>${pcstring('EQ.MERGELOC.${equip}.NAME')}</name>
					<carried>${pcstring('EQ.MERGELOC.${equip}.CARRIED')}</carried>
					<charges>${pcstring('EQ.MERGELOC.${equip}.CHARGES')}</charges>
					<charges_used>${pcstring('EQ.MERGELOC.${equip}.CHARGESUSED')}</charges_used>
					<contents>${pcstring('EQ.MERGELOC.${equip}.CONTENTS')}</contents>
					<contents_num>${pcstring('EQ.MERGELOC.${equip}.CONTENTSNUM')}</contents_num>
					<content_weight>${pcstring('EQ.MERGELOC.${equip}.CONTENTWEIGHT')}</content_weight>
					<cost>${pcstring('EQ.MERGELOC.${equip}.COST')}</cost>
					<equipped>${pcstring('EQ.MERGELOC.${equip}.EQUIPPED')}</equipped>
					<location>${pcstring('EQ.MERGELOC.${equip}.LOCATION')}</location>
					<maxcharges>${pcstring('EQ.MERGELOC.${equip}.MAXCHARGES')}</maxcharges>
					<note>${pcstring('EQ.MERGELOC.${equip}.NOTE')}</note>
					<quantity>${pcstring('EQ.MERGELOC.${equip}.QTY')}</quantity>
					<checkbox>${pcstring('EQ.MERGELOC.${equip}.CHECKBOXES')}</checkbox>
					<size>
						<long>${pcstring('EQ.MERGELOC.${equip}.SIZELONG')}</long>
						<short>${pcstring('EQ.MERGELOC.${equip}.SIZE')}</short>
					</size>
					<special_properties>${pcstring('EQ.MERGELOC.${equip}.SPROP')}</special_properties>
					<type>${pcstring('EQ.MERGELOC.${equip}.TYPE')}</type>
					<weight>${pcstring('EQ.MERGELOC.${equip}.WT')}</weight>
					<total_wt>${pcstring('EQ.MERGELOC.${equip}.TOTALWT')}</total_wt>
					<total_weight>${pcstring('EQ.MERGELOC.${equip}.TOTALWEIGHT')}</total_weight>
					<bonuslist>${pcstring('EQ.MERGELOC.${equip}.BONUSLIST')}</bonuslist>
					<acmod>${pcstring('EQ.MERGELOC.${equip}.ACMOD')}</acmod>
					<maxdex>${pcstring('EQ.MERGELOC.${equip}.MAXDEX')}</maxdex>
					<accheck>${pcstring('EQ.MERGELOC.${equip}.ACCHECK')}</accheck>
					<edr>${pcstring('EQ.MERGELOC.${equip}.EDR')}</edr>
					<move>${pcstring('EQ.MERGELOC.${equip}.MOVE')}</move>
					<spell_failure>${pcstring('EQ.MERGELOC.${equip}.SPELLFAILURE')}</spell_failure>
					<damage>${pcstring('EQ.MERGELOC.${equip}.DAMAGE')}</damage>
					<damage_alt>${pcstring('EQ.MERGELOC.${equip}.ALTDAMAGE')}</damage_alt>
					<crit_range>${pcstring('EQ.MERGELOC.${equip}.CRITRANGE')}</crit_range>
					<crit_mult>${pcstring('EQ.MERGELOC.${equip}.CRITMULT')}</crit_mult>
					<crit_range_alt>${pcstring('EQ.MERGELOC.${equip}.ALTCRITRANGE')}</crit_range_alt>
					<crit_mult_alt>${pcstring('EQ.MERGELOC.${equip}.ALTCRITMULT')}</crit_mult_alt>
					<range>${pcstring('EQ.MERGELOC.${equip}.RANGE')}</range>
					<attacks>${pcstring('EQ.MERGELOC.${equip}.ATTACKS')}</attacks>
					<prof>${pcstring('EQ.MERGELOC.${equip}.PROF')}</prof>
					<source>${pcstring('EQ.MERGELOC.${equip}.SOURCE')}</source>
					<quality>EQ.0.QUALITY</quality>
				</item>
			</@loop><#lt><#-- Equipment -->
			</equipmentset>
			</@equipsetloop><#lt>
		</equipmentsets>
	</equipment>
	<weight_allowance>
		<light>${pcstring('WEIGHT.LIGHT')}</light>
		<medium>${pcstring('WEIGHT.MEDIUM')}</medium>
		<heavy>${pcstring('WEIGHT.HEAVY')}</heavy>
		<lift_over_head>${pcstring('WEIGHT.OVERHEAD')}</lift_over_head>
		<lift_off_ground>${pcstring('WEIGHT.OFFGROUND')}</lift_off_ground>
		<!-- And loses Dex bonus to AC and can only move 5 feet per round as a full-round action -->
		<push_drag>${pcstring('WEIGHT.PUSHDRAG')}</push_drag>
	</weight_allowance>
	<!--
	  ====================================
	  ====================================
			SPECIAL ABILITIES
	  ====================================
	  ====================================-->
	<special_abilities>
		<@loop from=0 to=pcvar('COUNT[SA]-1') ; sa , sa_has_next>
		<ability>
			<name>${pcstring('SPECIALABILITY.${sa}')}</name>
			<description>${pcstring('SPECIALABILITY.${sa}.DESCRIPTION')}</description>
		</ability>
		</@loop>
		<race>${pcstring('RACE.ABILITYLIST')}</race>
		<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
		<class>
			<class>${pcstring('CLASS.${class}')}</class>
			<ability>${pcstring('CLASS.${class}.SALIST')}</ability>
		</class>
		</@loop>
	</special_abilities>
	<!--
	  ====================================
	  ====================================
			FEAT
	  ====================================
	  ====================================-->
	<feats>
		<!-- Visible standard feats (not including the auto feats) -->
		<@loop from=0 to=pcvar('COUNT[FEATS.VISIBLE]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('FEAT.VISIBLE.${feat}')}</name>
			<description>${pcstring('FEAT.VISIBLE.${feat}.DESC')}</description>
			<benefit>${pcstring('FEAT.VISIBLE.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('FEAT.VISIBLE.${feat}.TYPE')}</type>
			<associated>${pcstring('FEAT.VISIBLE.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('FEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>F</auto>
			<hidden>F</hidden>
			<virtual>F</virtual>
			<source>${pcstring('FEAT.VISIBLE.${feat}.SOURCE')}</source>
		</feat>
		</@loop>

		<!-- Auto feats -->
		<@loop from=0 to=pcvar('COUNT[FEATSAUTO.VISIBLE]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('FEATAUTO.VISIBLE.${feat}')}</name>
			<description>${pcstring('FEATAUTO.VISIBLE.${feat}.DESC')}</description>
			<benefit>${pcstring('FEATAUTO.VISIBLE.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('FEATAUTO.VISIBLE.${feat}.TYPE')}</type>
			<associated>${pcstring('FEATAUTO.VISIBLE.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('FEATAUTO.VISIBLE.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>T</auto>
			<hidden>F</hidden>
			<virtual>F</virtual>
			<source>${pcstring('FEATAUTO.VISIBLE.${feat}.SOURCE')}</source>
		</feat>
		</@loop>

		<!-- Virtual Feats -->
		<@loop from=0 to=pcvar('COUNT[VFEATS.VISIBLE]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('VFEAT.VISIBLE.${feat}')} (Granted)</name>
			<description>${pcstring('VFEAT.VISIBLE.${feat}.DESC')}</description>
			<benefit>${pcstring('VFEAT.VISIBLE.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('VFEAT.VISIBLE.${feat}.TYPE')}</type>
			<associated>${pcstring('VFEAT.VISIBLE.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('VFEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>F</auto>
			<hidden>F</hidden>
			<virtual>T</virtual>
			<source>${pcstring('VFEAT.VISIBLE.${feat}.SOURCE')}</source>
		</feat>
		</@loop>
		<!-- End Virtual Feats -->
		<!-- Hidden feats (all feats less the virtual, automatic and visible ones) -->
		<@loop from=0 to=pcvar('COUNT[FEATS.HIDDEN]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('FEAT.HIDDEN.${feat}')}</name>
			<description>${pcstring('FEAT.HIDDEN.${feat}.DESC')}</description>
			<benefit>${pcstring('FEAT.HIDDEN.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('FEAT.HIDDEN.${feat}.TYPE')}</type>
			<associated>${pcstring('FEAT.HIDDEN.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('FEAT.HIDDEN.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>F</auto>
			<hidden>T</hidden>
			<virtual>F</virtual>
			<source>${pcstring('FEAT.HIDDEN.${feat}.SOURCE')}</source>
		</feat>
		</@loop>
		<!-- Hidden VFEAT -->
		<@loop from=0 to=pcvar('COUNT[VFEATS.HIDDEN]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('VFEAT.HIDDEN.${feat}')}</name>
			<description>${pcstring('VFEAT.HIDDEN.${feat}.DESC')}</description>
			<benefit>${pcstring('VFEAT.HIDDEN.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('VFEAT.HIDDEN.${feat}.TYPE')}</type>
			<associated>${pcstring('VFEAT.HIDDEN.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('VFEAT.HIDDEN.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>F</auto>
			<hidden>T</hidden>
			<virtual>T</virtual>
			<source>${pcstring('VFEAT.HIDDEN.${feat}.SOURCE')}</source>
		</feat>
		</@loop>
		<!-- END Hidden VFEAT -->
		<@loop from=0 to=pcvar('COUNT[FEATSAUTO.HIDDEN]-1') ; feat , feat_has_next>
		<feat>
			<name>${pcstring('FEATAUTO.HIDDEN.${feat}')}</name>
			<description>${pcstring('FEATAUTO.HIDDEN.${feat}.DESC')}</description>
			<benefit>${pcstring('FEATAUTO.HIDDEN.${feat}.BENEFIT')}</benefit>
			<type>${pcstring('FEATAUTO.HIDDEN.${feat}.TYPE')}</type>
			<associated>${pcstring('FEATAUTO.HIDDEN.${feat}.ASSOCIATED')}</associated>
			<count>${pcstring('FEATAUTO.HIDDEN.${feat}.ASSOCIATEDCOUNT')}</count>
			<auto>T</auto>
			<hidden>T</hidden>
			<virtual>F</virtual>
			<source>${pcstring('FEATAUTO.HIDDEN.${feat}.SOURCE')}</source>
		</feat>
		</@loop>
	</feats>

	<#-- ABILITY OBJECTS -->
	<#macro abilityBlock category nature hidden typeName nodeName >
	<#if hidden>
		<#assign visCriteria = 'VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY' />
		<#assign visName = 'HIDDEN' />
	<#else>
		<#assign visCriteria = 'VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY' />
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
	<#elseif nature='ALL' >
		<#assign abilityToken = 'ABILITYALL' />
	</#if>
	<#assign typeFilter = "" />
	<#assign typeCountFilter = "" />
	<#if typeName!=''>
		<#assign typeFilter = ".TYPE=${typeName}" />
		<#assign typeCountFilter = ',"TYPE=${typeName}"' />
	</#if>
	<#if (nature='ALL') >
		<#assign numAbilities =
			pcvar('countdistinct("ABILITIES","CATEGORY=${category}","${visCriteria}"${typeCountFilter})') />
	<#else>
		<#assign numAbilities =
			pcvar('countdistinct("ABILITIES","CATEGORY=${category}","${visCriteria}","NATURE=${nature}"${typeCountFilter})') />
	</#if>
	<#if (numAbilities > 0) >
		<!-- ${visName?capitalize} ${nature?capitalize} "${category}" Ability Objects -->
	</#if>
	<@loop from=0 to=numAbilities-1 ; abilityIdx >
		<#assign abilityExportToken = "${abilityToken}.${category}.${visName}.${abilityIdx}${typeFilter}" />
		<#assign typeOfAbility =
			pcstring("${abilityExportToken}.TYPE")?lower_case />
		<#if (pcstring("${abilityExportToken}.HASASPECT.Name") = "Y")>
			<#assign abilityName = pcstring("${abilityExportToken}.ASPECT.Name") />
		<#else>
			<#assign abilityName = pcstring("${abilityExportToken}") />
		</#if>
		<${nodeName}>
		<#if (typeOfAbility?contains("extraordinary"))>
			<name>${abilityName} (Ex)</name>
		<#elseif (typeOfAbility?contains("supernatural"))>
			<name>${abilityName} (Su)</name>
		<#elseif (typeOfAbility?contains("spelllike"))>
			<name>${abilityName} (Sp)</name>
		<#elseif (typeOfAbility?contains("psilike"))>
			<name>${abilityName} (Ps)</name>
		<#else>
			<name>${abilityName}</name>
		</#if>
			<description>${pcstring("${abilityExportToken}.DESC")}</description>
			<type>${pcstring("${abilityExportToken}.TYPE")}</type>
			<associated>${pcstring("${abilityExportToken}.ASSOCIATED")}</associated>
			<count>${pcstring("${abilityExportToken}.ASSOCIATEDCOUNT")}</count>
			<aspect>${pcstring('${abilityExportToken}.ASPECT')}</aspect>
			<auto>${isAuto}</auto>
			<hidden><#if hidden>T<#else>F</#if></hidden>
			<virtual>${isVirtual}</virtual>
			<category>${category}</category>
			<source>${pcstring('${abilityExportToken}.SOURCE')}</source>
		</${nodeName}>
	</@loop>
	</#macro>
	<!--
	  ====================================
	  ====================================
			ARCHETYPES
	  ====================================
	  ====================================-->
	<archetypes>
	<#if (pcvar('countdistinct("ABILITIES";"CATEGORY=Archetype";"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY";"TYPE=Archetype")') = 0)> <!-- " -->
	<#else>
	<!-- Archetypes -->
	</#if>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","TYPE=Archetype")-1') ; archetype , archetype_has_next>
		<archetype>
			<name>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype')}</name>
			<description>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.DESC')}</description>
			<type>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.TYPE')}</type>
			<associated>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.ASSOCIATED')}</associated>
			<count>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.ASSOCIATEDCOUNT')}</count>
			<category>Archetype</category>
			<source>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.SOURCE')}</source>
		</archetype>
	</@loop>
	</archetypes>
	<!--
	  ====================================
	  ====================================
			SPECIAL QUALITIES
	  ====================================
	  ==================================== -->
	<special_qualities>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="SpecialQuality" nodeName="special_quality" />
	</special_qualities>
	<!--
	  ====================================
	  ====================================
			SPECIAL ATTACKS
	  ====================================
	  ====================================-->
	<special_attacks>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="SpecialAttack" nodeName="special_attack" />
	</special_attacks>

		<!--
	  ====================================
	  ====================================
			Resistance Table
	  ====================================
	  ====================================-->
		<resistances>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","ASPECT=ResistanceOutput")-1') ; resistance , resistance_has_next>
			<resistance>${pcstring('ABILITYALL.Special Ability.${resistance}.TYPE=Resistance.ASPECT.ResistanceOutput')}</resistance>
		</@loop>
		</resistances>

		<!--
	  ====================================
	  ====================================
			Racial Traits
	  ====================================
	  ====================================-->

	<racial_traits>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=RaceTrait")-1') ; ability , ability_has_next>
	<#if (pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.HASASPECT.RaceTraitMaster") = "Y")>
		<racial_trait>
	<#if (pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.TYPE") = "Extraordinary")>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait')} (Ex)</name>
	<#else>
	<#if (pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.TYPE") = "Supernatural")>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait')} (Su)</name>
	<#else>
	<#if (pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.TYPE") = "SpellLike")>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait')} (Sp)</name>
	<#else>
	</#if>
	</#if>
	</#if>
			<header>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait')}</header>
			<description>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.DESC')}</description>
			<type>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.TYPE')}</type>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${ability}.TYPE=RaceTrait.SOURCE')}</source>
			<check_count>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.ASPECT.CheckCount.INTVAL')}</check_count>
			<check_type>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.ASPECT.CheckType')}</check_type>
	<#if (pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.HASASPECT.MasterAbility") = "Y")>
			<master>${pcstring('ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.ASPECT.MasterAbility')}</master>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability")-1') ; subability , subability_has_next>
	<#if (pcstring("ABILITYALL.Special Ability.${subability}.ASPECT.ChildAbility") = pcstring("ABILITYALL.Special Ability.${ability}.TYPE=RaceTrait.ASPECT.MasterAbility"))>
			<subability>
				<name>${pcstring('ABILITYALL.Special Ability.${subability}')}</name>
				<description>${pcstring('ABILITYALL.Special Ability.${subability}.DESC')}</description>
				<type>${pcstring('ABILITYALL.Special Ability.${subability}.TYPE')}</type>
				<source>${pcstring('ABILITYALL.Special Ability.${subability}.SOURCE')}</source>
				<child>${pcstring('ABILITYALL.Special Ability.${subability}.ASPECT.ChildAbility')}</child>
			</subability>
	</#if>
	</@loop>
	</#if>
		</racial_trait>
	</#if>
	</@loop>
	</racial_traits>

		<!--
	  ====================================
	  ====================================
			Animal Trick
	  ====================================
	  ====================================-->
	<animal_tricks>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="AnimalTrick" nodeName="animal_trick" />
	</animal_tricks>

		<!--
	  ====================================
	  ====================================
			Racial Trait
	  ====================================
	  ====================================-->
	<racial_traits>
	<@abilityBlock category="Racial Trait" nature="ALL" hidden=false typeName="Racial Trait" nodeName="racial_trait" />
	</racial_traits>

		<!--
	  ====================================
	  ====================================
			Class Feature
	  ====================================
	  ====================================-->
	<class_features>
	<@abilityBlock category="Class Feature" nature="ALL" hidden=false typeName="Class Feature" nodeName="class_feature" />
	</class_features>

	<!--
	  ====================================
	  ====================================
			TALENTS
	  ====================================
	  ====================================-->
	<talents>
	<@abilityBlock category="Talent" nature="ALL" hidden=false typeName="Talent" nodeName="talent" />
	</talents>

	<!--
	  ====================================
	  ====================================
			Intelligent Item
	  ====================================
	  ====================================-->
	<intelligent_items>
	<@abilityBlock category="Intelligent Item" nature="ALL" hidden=false typeName="IntelligentItemOutput" nodeName="intelligent_item" />
	</intelligent_items>

	<!--
	  ====================================
	  ====================================
			Words of Power
	  ====================================
	  ====================================-->
	<words_of_powers>
	<@abilityBlock category="Words of Power" nature="ALL" hidden=false typeName="WordsOfPowerOutput" nodeName="words_of_power" />
	</words_of_powers>

	
	<!--
	  ====================================
	  ====================================
			Channeling
	  ====================================
	  ====================================-->
	<channelings>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="ChannelingOutput" nodeName="channeling" />
	</channelings>
	<!-- END ECLIPSE SECION -->


	<!--
	====================================
	====================================
			MUTATIONS
	====================================
	====================================	-->
	<mutations>
	<@abilityBlock category="Mutation" nature="ALL" hidden=false typeName="" nodeName="mutation" />
	</mutations>

	<!--
	  ====================================
	  ====================================
			Prestige Awards
	  ====================================
	  ====================================-->
	<prestige_awards>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="Prestige Award Display" nodeName="prestige_award" />
	</prestige_awards>



	<!--
	  ====================================
	  ====================================
			TRAITS
	  ====================================
	  ====================================-->
	<traits>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Trait","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; trait , trait_has_next>
		<trait>
			<#if (pcstring("VABILITY.Special Ability.VISIBLE.${trait}.TYPE=Trait") != "")><#-- TODO: THis doesn't work unless all the virtual abilities are at the start of the loop. Need a new subtag on ABILITYALL of nature -->
			<name>${pcstring('VABILITY.Special Ability.VISIBLE.${trait}.TYPE=Trait')} (Granted)</name>
			<#else>
			<name>${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait')}</name>
			</#if>
			<description>${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait.DESC')}</description>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait.SOURCE')}</source>
		</trait>
	</@loop>
	</traits>

	<!--
	  ====================================
	  ====================================
			Drawbacks
	  ====================================
	  ====================================-->
	<drawbacks>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=drawback","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; drawback , drawback_has_next>
		<drawback>
			<#if (pcstring("VABILITY.Special Ability.VISIBLE.${drawback}.TYPE=drawback") != "")><#-- TODO: THis doesn't work unless all the virtual abilities are at the start of the loop. Need a new subtag on ABILITYALL of nature -->
			<name>${pcstring('VABILITY.Special Ability.VISIBLE.${drawback}.TYPE=drawback')} (Granted)</name>
			<#else>
			<name>${pcstring('ABILITYALL.Special Ability.VISIBLE.${drawback}.TYPE=drawback')}</name>
			</#if>
			<description>${pcstring('ABILITYALL.Special Ability.VISIBLE.${drawback}.TYPE=drawback.DESC')}</description>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${drawback}.TYPE=drawback.SOURCE')}</source>
		</drawback>
	</@loop>
	</drawbacks>
	<!--
	====================================
	====================================
	MASTER ABILITY
	====================================
	====================================-->
	<master_abilities>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","ASPECT=MasterAbility")-1') ; ability , ability_has_next>
		<master_ability>
			<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.TYPE") = "Extraordinary")>
			<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility')} (Ex)</name>
			<#else>
				<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.TYPE") = "Supernatural")>
				<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility')} (Su)</name>
				<#else>
					<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.TYPE") = "SpellLike")>
					<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility')} (Sp)</name>
					<#else>
					<name>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility')}</name>
					</#if>
				</#if>
			</#if>
			<header>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility')}</header>
			<description>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.DESC')}</description>
			<type>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.TYPE')}</type>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${ability}.ASPECT=MasterAbility.SOURCE')}</source>
			<check_count>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.ASPECT.CheckCount.INTVAL')}</check_count>
			<check_type>${pcstring('ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.ASPECT.CheckType')}</check_type>

			<#if (pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.HASASPECT.MasterAbility") = "Y")>
				<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability")-1') ; subability , subability_has_next>
					<#if (pcstring("ABILITYALL.Special Ability.${subability}.ASPECT.ChildAbility") = pcstring("ABILITYALL.Special Ability.${ability}.ASPECT=MasterAbility.ASPECT.MasterAbility"))>
					<subability>
						<name>${pcstring('ABILITYALL.Special Ability.${subability}')}</name>
						<description>${pcstring('ABILITYALL.Special Ability.${subability}.DESC')}</description>
						<type>${pcstring('ABILITYALL.Special Ability.${subability}.TYPE')}</type>
						<source>${pcstring('ABILITYALL.Special Ability.${subability}.SOURCE')}</source>
					</subability>
					</#if>
				</@loop>
			</#if>
		</master_ability>
	</@loop>
	</master_abilities>

		<!--
	  ====================================
	  ====================================
			Afflictions
	  ====================================
	  ====================================-->
	<afflictions>
	<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Affliction","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; affliction , affliction_has_next>
		<affliction>
			<name>${pcstring('ABILITYALL.Special Ability.VISIBLE.${affliction}.TYPE=Affliction')}</name>
			<description>${pcstring('ABILITYALL.Special Ability.VISIBLE.${affliction}.TYPE=Affliction.DESC')}</description>
			<source>${pcstring('ABILITYALL.Special Ability.VISIBLE.${affliction}.TYPE=Affliction.SOURCE')}</source>
		</affliction>
	</@loop>
	</afflictions>

	<!--
	  ====================================
	  ====================================
			PFS Chronicles
	  ====================================
	  ====================================-->
	<pfs_chronicles>
	<@abilityBlock category="PFS Chronicle" nature="ALL" hidden=false typeName="PFSChronicle" nodeName="pfs_chronicle" />
	</pfs_chronicles>
	<!--
	  ====================================
	  ====================================
			PFS Boons
	  ====================================
	  ====================================-->
	<pfs_boons>
	<@abilityBlock category="Special Ability" nature="ALL" hidden=false typeName="PFSBoon" nodeName="pfs_boon" />
	</pfs_boons>
	<!--
	  ====================================
	  ====================================
			Conditions
	  ====================================
	  ==================================== -->
	<conditions>
	<@abilityBlock category="Condition" nature="ALL" hidden=false typeName="Condition" nodeName="condition" />
	</conditions>

	<!--
	====================================
	  ====================================
			Temporary Bonus
	  ====================================
	  ====================================-->
	<tempbonuses>
	<@loop from=0 to=pcvar('COUNT[TEMPBONUSNAMES]-1') ; tempbonus , tempbonus_has_next>
		<tempbonus>
			<name>${pcstring('TEMPBONUS.${tempbonus}.NAME')}</name>
			<description>${pcstring('TEMPBONUS.${tempbonus}.DESC')}</description>
		</tempbonus>
	</@loop>
	</tempbonuses>

	<!--
	  ====================================
	  ====================================
			MISCELLANEOUS
	  ====================================
	  ====================================-->


	<#if (pcvar("COUNT[DOMAINS]") > 0) >
	<domains>
	<@loop from=1 to=pcvar('COUNT[DOMAINS]') ; domain , domain_has_next>
		<domain>
			<name>${pcstring('DOMAIN.${domain}')}</name>
			<power>${pcstring('DOMAIN.${domain}.POWER')}</power>
		</domain>
	</@loop>	<!-- Domains -->
	</domains>
	</#if>	<!-- Domains -->
	<weapon_proficiencies>${pcstring('WEAPONPROFS')}</weapon_proficiencies>
	<languages>${pcstring('LANGUAGES')}</languages>
	<#if (pcvar("COUNT[TEMPLATES]") > 0) >
	<templates>
		<list>${pcstring('TEMPLATELIST')}</list>
		<@loop from=0 to=pcvar('COUNT[TEMPLATES]-1') ; template , template_has_next><#-- TODO: Loop was of early exit type 1 -->
		<template>
			<name>${pcstring('TEMPLATE.${template}.NAME')}</name>
			<strmod>${pcstring('TEMPLATE.${template}.STRMOD')}</strmod>
			<dexmod>${pcstring('TEMPLATE.${template}.DEXMOD')}</dexmod>
			<conmod>${pcstring('TEMPLATE.${template}.CONMOD')}</conmod>
			<intmod>${pcstring('TEMPLATE.${template}.INTMOD')}</intmod>
			<wismod>${pcstring('TEMPLATE.${template}.WISMOD')}</wismod>
			<chamod>${pcstring('TEMPLATE.${template}.CHAMOD')}</chamod>
			<cr>${pcstring('TEMPLATE.${template}.CR')}</cr>
			<dr>${pcstring('TEMPLATE.${template}.DR')}</dr>
			<feat>${pcstring('TEMPLATE.${template}.FEAT')}</feat>
			<sa>${pcstring('TEMPLATE.${template}.SA')}</sa>
			<sr>${pcstring('TEMPLATE.${template}.SR')}</sr>
			<bonuslist>${pcstring('TEMPLATE.${template}.BONUSLIST')}</bonuslist>
		</template>
		</@loop>
	</templates>
	</#if>

	<#if (pcvar("PROHIBITEDLIST") > 0) >
	<prohibited_schools>${pcstring('PROHIBITEDLIST')}</prohibited_schools>
	</#if>

	<misc>
			<gold>${pcstring('GOLD')}</gold>

		<#if (pcvar("MISC.FUNDS") > 0) >
		</#if>
		<funds>
			<fund>${pcstring('MISC.FUNDS')}</fund>
		</funds>
		<companions>
			<companion>${pcstring('MISC.COMPANIONS')}</companion>
		</companions>
<!--	This doesn't work - AM
	<#if (pcvar("MISC.COMPANIONS") > 0) >	-->
<!--		</#if>	-->
		<#if (pcvar("MISC.MAGIC") > 0) >
		</#if>
		<magics>
			<magic>${pcstring('MISC.MAGIC')}</magic>
		</magics>
	</misc>
	<!--
	  ====================================
	  ====================================
			COMPANIONS
	  ====================================
	  ====================================-->
	<companions>
		<#if (pcvar("FOLLOWERTYPE.FAMILIAR") > 0) >
		<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FAMILIAR]-1') ; companion , companion_has_next>
		<familiar>
			<name>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.NAME')}</name>
			<race>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.RACE')}</race>
			<hp>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.HP')}</hp>
			<ac>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.BONUS.COMBAT.AC.TOTAL')}</ac>
			<fortitude>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.CHECK.FORTITUDE.TOTAL')}</fortitude>
			<reflex>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.CHECK.REFLEX.TOTAL')}</reflex>
			<will>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.CHECK.WILL.TOTAL')}</will>
			<initiative_mod>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.INITIATIVEMOD')}</initiative_mod>
			<special_properties>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialAttack')}${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialQuality')}</special_properties>

			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FAMILIAR.${companion}.EQTYPE.WEAPON]-1') ; weap , weap_has_next>
				<attack>
					<common>
						<name>
							<short>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.NAME')}</short>
							<long>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.LONGNAME')}</long>
						</name>
						<category>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.CATEGORY')}</category>
						<critical>
							<range>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.CRIT')}</range>
							<multiplier>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.MULT')}</multiplier>
						</critical>
						<to_hit>
							<hit>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.HIT')}</hit>
							<magic_hit>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.MAGICHIT')}</magic_hit>
							<total_hit>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.TOTALHIT')}</total_hit>
						</to_hit>
						<feat>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.FEAT')}</feat>
						<hand>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.HAND')}</hand>
						<num_attacks>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.NUMATTACKS')}</num_attacks>
						<reach>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.REACH')}</reach>
						<reachunit>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.REACHUNIT')}</reachunit>
						<size>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.SIZE')}</size>
						<special_properties>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.SPROP')}</special_properties>
						<template>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.TEMPLATE')}</template>
						<type>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.TYPE')}</type>
						<weight>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.WT')}</weight>
						<sequence>${weap}</sequence>
					</common>
					<simple>
						<to_hit>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.TOTALHIT')}</to_hit>
						<damage>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.DAMAGE')}</damage>
						<range>${pcstring('FOLLOWERTYPE.FAMILIAR.${companion}.WEAPON.${weap}.RANGE')}</range>
					</simple>
				</attack>
			</@loop>
			</attacks>
		</familiar>
		</@loop>

		</#if>
		<#if (pcvar("FOLLOWERTYPE.Psicrystal") > 0) >
		<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.Psicrystal]-1') ; companion , companion_has_next>
		<psicrystal>
			<name>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.NAME')}</name>
			<race>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.RACE')}</race>
			<hp>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.HP')}</hp>
			<ac>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.BONUS.COMBAT.AC.TOTAL')}</ac>
			<fortitude>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.CHECK.FORTITUDE.TOTAL')}</fortitude>
			<reflex>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.CHECK.REFLEX.TOTAL')}</reflex>
			<will>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.CHECK.WILL.TOTAL')}</will>
			<initiative_mod>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.INITIATIVEMOD')}</initiative_mod>
			<special_properties>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialAttack')}${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialQuality')}</special_properties>

			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.Psicrystal.${companion}.EQTYPE.WEAPON]-1') ; weap , weap_has_next>
				<attack>
					<common>
						<name>
							<short>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.NAME')}</short>
							<long>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.LONGNAME')}</long>
						</name>
						<category>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.CATEGORY')}</category>
						<critical>
							<range>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.CRIT')}</range>
							<multiplier>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.MULT')}</multiplier>
						</critical>
						<to_hit>
							<hit>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.HIT')}</hit>
							<magic_hit>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.MAGICHIT')}</magic_hit>
							<total_hit>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.TOTALHIT')}</total_hit>
						</to_hit>
						<feat>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.FEAT')}</feat>
						<hand>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.HAND')}</hand>
						<num_attacks>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.NUMATTACKS')}</num_attacks>
						<reach>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.REACH')}</reach>
						<reachunit>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.REACHUNIT')}</reachunit>
						<size>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.SIZE')}</size>
						<special_properties>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.SPROP')}</special_properties>
						<template>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.TEMPLATE')}</template>
						<type>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.TYPE')}</type>
						<weight>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.WT')}</weight>
						<sequence>${weap}</sequence>
					</common>
					<simple>
						<to_hit>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.TOTALHIT')}</to_hit>
						<damage>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.DAMAGE')}</damage>
						<range>${pcstring('FOLLOWERTYPE.Psicrystal.${companion}.WEAPON.${weap}.RANGE')}</range>
					</simple>
				</attack>
			</@loop>
			</attacks>
		</psicrystal>
		</@loop>
		</#if>


		<#if (pcvar("FOLLOWERTYPE.SPECIAL MOUNT") > 0) >
		<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.SPECIAL MOUNT]-1') ; companion , companion_has_next>
		<mount>
			<!-- Note that only one mount is allowed, so no support for multiple mounts
			Added Support as Eclipse allows for more than one Mount; also handy if you have a shapeshifting
			Mount - AM-->
			<name>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.NAME')}</name>
			<race>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.RACE')}</race>
			<hp>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.HP')}</hp>
			<ac>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.BONUS.COMBAT.AC.TOTAL')}</ac>
			<fortitude>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.CHECK.FORTITUDE.TOTAL')}</fortitude>
			<reflex>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.CHECK.REFLEX.TOTAL')}</reflex>
			<will>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.CHECK.WILL.TOTAL')}</will>
			<initiative_mod>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.INITIATIVEMOD')}</initiative_mod>
			<special_properties>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialAttack')}${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialQuality')}</special_properties>
			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.SPECIAL MOUNT.EQTYPE.WEAPON]-1') ; weap , weap_has_next>
				<attack>
					<common>
						<name>
							<short>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.NAME')}</short>
							<long>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.LONGNAME')}</long>
						</name>
						<category>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.CATEGORY')}</category>
						<critical>
							<range>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.CRIT')}</range>
							<multiplier>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.MULT')}</multiplier>
						</critical>
						<to_hit>
							<hit>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.HIT')}</hit>
							<magic_hit>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.MAGICHIT')}</magic_hit>
							<total_hit>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.TOTALHIT')}</total_hit>
						</to_hit>
						<feat>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.FEAT')}</feat>
						<hand>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.HAND')}</hand>
						<num_attacks>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.NUMATTACKS')}</num_attacks>
						<reach>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.REACH')}</reach>
						<reachunit>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.REACH')}</reachunit>
						<size>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.SIZE')}</size>
						<special_properties>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.SPROP')}</special_properties>
						<template>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.TEMPLATE')}</template>
						<type>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.TYPE')}</type>
						<weight>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.WT')}</weight>
						<sequence>${weap}</sequence>
					</common>
					<simple>
						<to_hit>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.TOTALHIT')}</to_hit>
						<damage>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.DAMAGE')}</damage>
						<range>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${weap}.RANGE')}</range>
					</simple>
				</attack>
			</@loop>
			</attacks>
		</mount>
		</@loop>
		</#if>
		<#if (pcvar("FOLLOWERTYPE.ANIMAL COMPANION") > 0) >
		<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.ANIMAL COMPANION]-1') ; companion , companion_has_next>
		<companion>
			<name>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.NAME')}</name>
			<race>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.RACE')}</race>
			<hp>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.HP')}</hp>
			<ac>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.BONUS.COMBAT.AC.TOTAL')}</ac>
			<fortitude>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.CHECK.FORTITUDE.TOTAL')}</fortitude>
			<reflex>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.CHECK.REFLEX.TOTAL')}</reflex>
			<will>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.CHECK.WILL.TOTAL')}</will>
			<initiative_mod>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.INITIATIVEMOD')}</initiative_mod>
			<special_properties>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialAttack')}${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialQuality')}</special_properties>
			<trick>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=AnimalTrick')}</trick>
			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.ANIMAL COMPANION.${companion}.EQTYPE.WEAPON]-1') ; weap , weap_has_next>
				<attack>
					<common>
						<name>
							<short>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.NAME')}</short>
							<long>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.LONGNAME')}</long>
						</name>
						<category>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.CATEGORY')}</category>
						<critical>
							<range>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.CRIT')}</range>
							<multiplier>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.MULT')}</multiplier>
						</critical>
						<to_hit>
							<hit>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.HIT')}</hit>
							<magic_hit>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.MAGICHIT')}</magic_hit>
							<total_hit>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.TOTALHIT')}</total_hit>
						</to_hit>
						<feat>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.FEAT')}</feat>
						<hand>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.HAND')}</hand>
						<num_attacks>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.NUMATTACKS')}</num_attacks>
						<reach>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.REACH')}</reach>
						<reachunit>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.REACHUNIT')}</reachunit>
						<size>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.SIZE')}</size>
						<special_properties>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.SPROP')}</special_properties>
						<template>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.TEMPLATE')}</template>
						<type>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.TYPE')}</type>
						<weight>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.WT')}</weight>
						<sequence>${weap}</sequence>
					</common>
					<simple>
						<to_hit>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.TOTALHIT')}</to_hit>
						<damage>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.DAMAGE')}</damage>
						<range>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${companion}.WEAPON.${weap}.RANGE')}</range>
					</simple>
				</attack>
			</@loop>
			</attacks>
		</companion>
		</@loop>	<!-- Followertype Animal -->
		</#if>
		<#if (pcvar("FOLLOWERTYPE.FOLLOWER") > 0) >
		<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FOLLOWER]') ; companion , companion_has_next>
		<follower>
			<name>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.NAME')}</name>
			<race>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.RACE')}</race>
			<hp>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.HP')}</hp>
			<ac>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.BONUS.COMBAT.AC.TOTAL')}</ac>
			<fortitude>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.CHECK.FORTITUDE.TOTAL')}</fortitude>
			<reflex>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.CHECK.REFLEX.TOTAL')}</reflex>
			<will>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.CHECK.WILL.TOTAL')}</will>
			<initiative_mod>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.INITIATIVEMOD')}</initiative_mod>
			<special_properties>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialAttack')}${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.ABILITYALLLIST.Special Ability.VISIBLE.TYPE=SpecialQuality')}</special_properties>
			<attacks>
			<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FOLLOWER.${companion}.EQTYPE.WEAPON]-1') ; weap , weap_has_next>
				<attack>
					<common>
						<name>
							<short>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.NAME')}</short>
							<long>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.LONGNAME')}</long>
						</name>
						<attacks>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.ATTACKS')}</attacks>
						<damage>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.BASICDAMAGE')}</damage>
						<bonusdamage>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.BONUSDAMAGE')}</bonusdamage>
						<category>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.CATEGORY')}</category>
						<critical>
							<range>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.CRIT')}</range>
							<multiplier>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.MULT')}</multiplier>
						</critical>
						<damage>
							<primary_hand>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.DAMAGE')}</primary_hand>
							<magic_damage>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.MAGICDAMAGE')}</magic_damage>
						</damage>
						<to_hit>
							<hit>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.HIT')}</hit>
							<magic_hit>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.MAGICHIT')}</magic_hit>
							<total_hit>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.TOTALHIT')}</total_hit>
						</to_hit>
						<feat>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.FEAT')}</feat>
						<hand>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.HAND')}</hand>
						<num_attacks>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.NUMATTACKS')}</num_attacks>
						<base_range>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.RANGENOUNITS')}</base_range>
						<reach>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.REACH')}</reach>
						<reachunit>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.REACHUNIT')}</reachunit>
						<size>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.SIZE')}</size>
						<special_properties>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.SPROP')}</special_properties>
						<template>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.TEMPLATE')}</template>
						<type>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.TYPE')}</type>
						<weight>${pcstring('FOLLOWERTYPE.FOLLOWER.${companion}.WEAPON.${weap}.WT')}</weight>
						<sequence>${weap}</sequence>
					</common>
				</attack>
			</@loop>
			</attacks>
		</follower>
		</@loop>	<!-- Followertype Follower -->
		</#if>
	</companions>
	<!--
	  ====================================
	  ====================================
			SPELLS
	  ====================================
	  ====================================-->
	<spells>
		<!-- ### BEGIN Innate spells ### -->
	<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]') ; spellrace , spellrace_has_next>
	<#if (spellrace = 0)>
	<spells_innate number="none"/>
	<#else>
	<spells_innate>
	<#assign spellbook = 1 /> 
	<#assign class = 0 /> 
	<#assign level = 0 />
	<#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
		<racial_innate>
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
			<spell>
					<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
					<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
					<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
					<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
					<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
					<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
					<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
					<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
					<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
					<times_unit>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}</times_unit>
					<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
					<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
					<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
					<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
					<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
					<school>
						<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
						<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
						<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
						<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
					</school>
					<source>
						<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
						<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
						<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
						<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
						<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
					</source>
					<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
					<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
					<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
			</spell>
			</@loop>
		</racial_innate>
		</#if>

		<class_innate>
			<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
			<#assign class = 0 /> 
			<#assign level = 0 /> 
			<#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
			<spellbook number="${spellbook}" name="${pcstring('SPELLBOOKNAME.${spellbook}')}">
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
				<spell>
						<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
						<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
						<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
						<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
						<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
						<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
						<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
						<times_unit>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}</times_unit>
						<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
						<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
						<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
						<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
						<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
						<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
						<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
						<school>
							<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
							<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
							<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
							<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
						</school>
						<source>
							<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
							<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
							<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
							<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
							<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
						</source>
						<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
						<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
						<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
					</spell>
			</@loop>
			</spellbook>
			</#if>
			</@loop>
		</class_innate>
		</spells_innate>
		</#if>
		<!-- ### END Innate spells ### -->
		</@loop>
		<!-- ### BEGIN Known spells ### -->
		<known_spells>
		<@loop from=0 to=0 ; spellbook , spellbook_has_next>
		<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next><#-- TODO: Loop was of early exit type 1 -->
		<#if (pcstring("SPELLLISTCLASS.${class}") != '') >
		<class number="${class}" spelllistclass="${pcstring('SPELLLISTCLASS.${class}')}" spellcasterlevel="${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}" spellcastertype="${pcstring('SPELLLISTTYPE.${class}')}" memorize="${pcstring('SPELLLISTMEMORIZE.${class}')}" concentration="${pcstring('SPELLLISTCLASS.${class}.CONCENTRATION')}">
			<@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
			<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spellcount , spellcount_has_next>
			<level number="${level}" known="${pcstring('SPELLLISTKNOWN.${class}.${level}')}" cast="${pcstring('SPELLLISTCAST.${class}.${level}')}">
			<#if (spellcount = 0)>
			<#else>
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
				<spell>
						<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
						<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
						<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
						<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
						<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
						<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
						<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
						<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
						<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
						<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
						<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
						<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
						<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
						<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
						<school>
							<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
							<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
							<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
							<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
						</school>
						<source>
							<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
							<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
							<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
							<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
							<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
						</source>
						<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
						<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
						<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
				</spell>
			</@loop>
			</#if>
			</level>
			</@loop>
			</@loop>
		</class>
		</#if>
		</@loop>
		</@loop>
	</known_spells>
		<!-- ### END Known spells ### -->

		<!-- ### BEGIN memorized spells ### -->
	<@loop from=pcvar('COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2') to=pcvar('COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2') ; memorised , memorised_has_next>
	<#if (memorised = 0)>
	<memorized_spells/>
	<#else>
	<memorized_spells>
			<!-- ### BEGIN innate memorized spell section -->
	<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]') ; spellrace , spellrace_has_next>
	<#if (spellrace = 0)>
	<#else>

	<!-- ### BEGIN innate memorized spells ### -->
	 <#assign spellbook = 1 />
	 <#assign class = 0 /> 
	 <#assign level = 0 /> 
	 <#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
		<racial_innate_memorized>
		  <@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
			<spell>
					<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
					<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
					<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
					<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
					<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
					<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
					<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
					<times_unit>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}</times_unit>
					<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
					<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
					<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
					<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
					<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
					<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
					<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
					<school>
						<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
						<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
						<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
						<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
					</school>
					<source>
						<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
						<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
						<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
						<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
						<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
					</source>
					<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
					<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
					<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
				</spell>
		  </@loop>
		</racial_innate_memorized>
	  </#if>
			<!-- ### END innate memorized spells ### -->
			<!-- ### BEGIN class innate memorized spells ### -->
			<class_innate_memorized>
		  <@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
		   <#assign class = 0 /> 
		   <#assign level = 0 /> 
		   <#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
			<spellbook number="${spellbook}" name="${pcstring('SPELLBOOKNAME.${spellbook}')}">
			<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
				<spell>
						<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
						<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
						<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
						<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
						<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
						<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
						<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
						<times_unit>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}</times_unit>
						<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
						<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
						<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
						<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
						<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
						<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
						<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
						<school>
							<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
							<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
							<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
							<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
						</school>
						<source>
							<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
							<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
							<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
							<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
							<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
						</source>
						<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
						<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
						<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
					</spell>
			</@loop>
			</spellbook>
		   </#if>
		  </@loop>
		</class_innate_memorized>
			<!-- ### END class innate memorized spells ### -->
		 </#if>
		</@loop>
			<!-- ### END innate memorized spell section -->
			<!-- ### BEGIN class Spellbook memorized spells ### -->
			<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
			<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]') ; foo , foo_has_next><#-- TODO: Loop was of early exit type 1 -->
			<!-- FOR,${foo},COUNT[SPELLRACE],COUNT[SPELLRACE],1,1 -->
			<@loop from=pcvar('COUNT[SPELLSINBOOK0.${spellbook}.0]') to=pcvar('COUNT[SPELLSINBOOK0.${spellbook}.0]') ; bar , bar_has_next><#-- TODO: Loop was of early exit type 1 -->
			<!-- FOR,${bar},COUNT[SPELLSINBOOK0.${spellbook}.0],COUNT[SPELLSINBOOK0.${spellbook}.0],1,1 -->
			<#if (foo = 0 || bar = 0) >
			<!-- Either we do not have a innate race, or if we do we do not have any 0 level spell for the innate race -->
			<spellbook number="${spellbook}" name="${pcstring('SPELLBOOKNAME.${spellbook}')}" type="${pcstring('SPELLBOOK.${spellbook}.TYPE')}">
			<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next><#-- TODO: Loop was of early exit type 1 -->
			<class number="${class}" spelllistclass="${pcstring('SPELLLISTCLASS.${class}')}" spellcasterlevel="${pcstring('SPELLLISTCLASS.${class}.LEVEL')}" spellcastertype="${pcstring('SPELLLISTTYPE.${class}')}" memorize="${pcstring('SPELLLISTMEMORIZE.${class}')}">
			 <@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
			  <@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spelllevelcount , spelllevelcount_has_next>
			   <#if (spelllevelcount = 0)>
				<level number="${level}" spellcount="${spelllevelcount}"/>
			   <#else>
				<level number="${level}" spellcount="${spelllevelcount}">
				   <@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
					<spell>
							<basecasterlevel>${pcstring('SPELLLISTCLASS.${class}.CASTERLEVEL')}</basecasterlevel>
							<name>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</name>
							<outputname>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.OUTPUTNAME')}</outputname>
							<times_memorized>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}</times_memorized>
							<range>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}</range>
							<components>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}</components>
							<castingtime>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}</castingtime>
							<times_unit>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}</times_unit>
							<casterlevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}</casterlevel>
							<concentration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CONCENTRATION')}</concentration>
							<dc>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}</dc>
							<duration>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}</duration>
							<effect>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}</effect>
							<target>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}</target>
							<saveinfo>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}</saveinfo>
							<school>
								<school>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}</school>
								<subschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SUBSCHOOL')}</subschool>
								<descriptor>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTOR')}</descriptor>
								<fullschool>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.FULLSCHOOL')}</fullschool>
							</school>
							<source>
								<source>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCE')}</source>
								<sourcelevel>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELEVEL')}</sourcelevel>
								<sourcelink>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}</sourcelink>
								<sourcepage>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}</sourcepage>
								<sourceshort>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sourceshort>
							</source>
							<spell_resistance>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}</spell_resistance>
							<description>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DESCRIPTION')}</description>
							<bonusspell>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</bonusspell>
						</spell>
				   </@loop>
				</level>
			   </#if>
			  </@loop>
			 </@loop>
			</class>
		</@loop>
		</spellbook>
	<#else>
	</#if>
	<!-- END FOR,${bar},COUNT[SPELLSINBOOK0.${spellbook}.0],COUNT[SPELLSINBOOK0.${spellbook}.0],1,1 -->
	</@loop>
	<!-- END FOR,${foo},COUNT[SPELLRACE],COUNT[SPELLRACE],1,1 -->
	</@loop>
	</@loop>
	</memorized_spells>
	</#if>
	</@loop>
	<!-- ### END class Spellbook memorized spells ### -->
	</spells>
	<!-- ### Additional House Rules for Variables in the xsl:fo code ### -->
	<house_var>
		<spelldisplaydc>${pcstring('VAR.SpellDisplayDC')}</spelldisplaydc>
		<OldStyleAbilityStatBlockDisplay>${pcstring('VAR.OldStyleAbilityStatBlockDisplay')}</OldStyleAbilityStatBlockDisplay>
	</house_var>
</character>
