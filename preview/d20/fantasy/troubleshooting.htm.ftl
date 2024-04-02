<#ftl encoding="UTF-8" strip_whitespace=true >
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<!--
PCGen Character Sheet Template
==============================
Author: Riccardo Bernori
Email: iainuki@yahoo.com
Modifications by: ?
Email: 
Revisions: ?  at 02/02/16
Email: 
Revisions: ? at 10/11/09
Email: 

$Revision: 1 $
$Author: iainuki $
$Date: 2016-02-01 14:11:00 +0100 (Mon, 01 Feb 2016) $
-->
<head>
<meta http-equiv="content-type" content="text-html; charset=utf-8" />
<title>${pcstring('NAME')} - ${pcstring('PLAYERNAME')} (${pcstring('POOL.COST')} Points) in Statblock Format</title>
<!--
   This format is designed to provide detailed information on bonuses 
   and other objects for the purpose of troubleshooting characters
-->
<style type="text/css">
   body  { font-family:sans-serif;text-align:left; color:black; background:white; font-weight:normal; margin: 0px; padding: 5px; }
   .header { display:block; font-size:x-small; text-align:center; }
</style>
</head>
<body>
<!--
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')}</div>
-->
<b>${pcstring('NAME')}</b><br>
${pcstring('GENDER')} ${pcstring('RACE')}; 
${pcstring('ALIGNMENT')} ${pcstring('SIZELONG')} 
<#if (pcstring('RACETYPE') = "")> 
${pcstring('TYPE')}
<#else>
${pcstring('RACETYPE')}
${pcstring('ABILITYALL.Internal.HIDDEN.0.TYPE=Maneuverability.ASPECT.RaceExtra')}
</#if>
<#if (pcvar('COUNT[RACESUBTYPES]-1') = 0)>
( ${pcstring('RACESUBTYPE')} )
<#else>
 (
<@loop from=0 to=pcvar('COUNT[RACESUBTYPE]-1') ; racesubtype , racesubtype_has_next>
${pcstring('RACESUBTYPE.${racesubtype}')} 
</@loop> )
</#if>
<br>

<hr />
<b>Region</b> ${pcstring('REGION')}<br>
<b>Experience points</b> ${pcstring('EXP.CURRENT')}<br>
<b>XP multiplying factor for multiclassing</b> ${pcstring('EXP.FACTOR')}<br>
<b>Multiclassing experience penalty</b> ${pcstring('EXP.PENALTY')}<br>
<b>Favored Classes</b> ${pcstring('FAVOREDLIST')}<br>
<b>Total Classes</b> ${pcvar('COUNT[CLASSES]')}<br>
<b>Face</b> ${pcstring('FACE')}  -- Shortened ${pcstring('FACE.SHORT')}<br>
<b>Reach</b> ${pcstring('REACH')}<br>
<b>Encumbrance Category:</b> ${pcstring('TOTAL.LOAD')}<br>

<hr />

<b>Initiative</b> ${pcstring('INITIATIVEMOD')} = ${pcstring('STAT.1.MOD')}[STAT] ${pcstring('INITIATIVEBONUS.SIGN')}[MISC]<br>

<b>Armor Class</b> ${pcvar('AC.Total')} = ${pcstring('AC.Base.SIGN')}[BASE]
<#if (pcvar('AC.Size') != 0)> 
${pcstring('AC.Size.SIGN')}[SIZE] 
</#if>
<#if (pcvar('AC.Ability') != 0)> 
${pcstring('AC.Ability.SIGN')}[STAT] 
</#if>
<#if (pcvar('AC.NaturalArmor') != 0)> 
${pcstring('AC.NaturalArmor.SIGN')}[NATURAL] 
</#if>
<#if (pcvar('AC.Deflection') != 0)> 
${pcstring('AC.Deflection.SIGN')}[DEFLECTION]
</#if>
<#if (pcvar('AC.Misc') != 0)> 
${pcstring('AC.Misc.SIGN')}[MISC]
</#if>
<#if (pcvar('AC.Armor') != 0)> 
${pcstring('AC.Armor.SIGN')}[ARMOR] ${pcstring('EQ.IS.ARMOR.0.NAME')}
</#if>
<#if (pcvar('AC.Shield') != 0)> 
${pcstring('AC.Shield.SIGN')}[SHIELD] ${pcstring('EQ.IS.SHIELD.0.NAME')}
</#if>
<br>

<hr />
<b>Ability Scores</b>

<blockquote>
<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next><#-- TODO: Loop was of early exit type 1 -->
<b>
${pcstring('STAT.${stat}.NAME')}
${pcvar('STAT.${stat}')}
</b>
&nbsp;(${pcstring('STAT.${stat}.MOD')}) = 
<!-- Report User Values -->
${pcstring('STAT.${stat}.BASE.SIGN-(STAT.${stat}.LEVEL.(VAR.TL)-STAT.${stat}.LEVEL.1)')}[USER]
<!-- Report MISC Bonuses -->
${pcstring('STAT.${stat}.LEVEL.1.NOPOST.NOEQUIP.NOTEMP-(STAT.${stat}.BASE-(STAT.${stat}.LEVEL.(VAR.TL)-STAT.${stat}.LEVEL.1)).SIGN')}[MISC]

<br>
</@loop>
</blockquote>

<hr />
<b>Saving Throws</b>

<blockquote>
<@loop from=0 to=pcvar('COUNT[CHECKS]-1') ; check, check_has_next><#-- TODO: Loop was of early exit type 1 -->
<b>${pcstring('CHECK.${check}.NAME')}</b>: ${pcstring('CHECK.${check}.TOTAL')} = 
${pcstring('CHECK.${check}.BASE')}[BASE] 
<#if (pcvar('CHECK.${check}.STATMOD') != 0)> 
${pcstring('CHECK.${check}.STATMOD')}[STAT]
</#if>
<#if (pcvar('CHECK.${check}.MAGIC') != 0)> 
${pcstring('CHECK.${check}.MAGIC')}[MAGIC]
</#if>
<#if (pcvar('CHECK.${check}.FEAT') != 0)> 
${pcstring('CHECK.${check}.FEAT')}[FEAT]
</#if>
<#if (pcvar('CHECK.${check}.EPIC') != 0)> 
${pcstring('CHECK.${check}.EPIC')}[EPIC]
</#if>
<#if (pcvar('CHECK.${check}.MISC.NOMAGIC.NOSTAT') != 0)> 
${pcstring('CHECK.${check}.MISC.NOMAGIC.NOSTAT')}[MISC]
</#if>
<br>
</@loop>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SaveBonus")-1') ; ability , ability_has_next><#-- TODO: Loop was of early exit type 1 -->
<b>Conditionals:</b>${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus.ASPECT.SaveBonus')} <br />
</@loop>
<br>


</blockquote>

<hr />
<b>Attacks</b>
<br>

<@loop from=0 to=pcvar('COUNT[EQTYPE.Weapon]-1') ; weap , weap_has_next><#-- TODO: Loop was of early exit type 1 -->
<b>${pcstring('WEAPON.${weap}.NAME.NOSTAR')} </b>
<#assign weaponCategory>
${pcstring('WEAPON.${weap}.CATEGORY')?lower_case}
</#assign>
<#if (weaponCategory?contains('ranged'))>
<i>Ranged; </i>
<#else>
<i>Melee; </i>
</#if>
Number of Attacks: ${pcstring('WEAPON.${weap}.NUMATTACKS')}; 
Crit: ${pcstring('WEAPON.${weap}.CRIT')}; 
Crit Multiplier: x${pcstring('WEAPON.${weap}.MULT')}; 
<#if (weaponCategory?contains('ranged'))>
Range Increment: ${pcstring('WEAPON.${weap}.RANGE')}
<#else>
Reach: ${pcstring('WEAPON.${weap}.REACH')} ft.
</#if>
<blockquote>
<#if (weaponCategory?contains('ranged'))>
To Hit: ${pcstring('WEAPON.${weap}.BASEHIT.0')}
= ${pcstring('ATTACK.RANGED.BASE')}[BAB] 
<#if (pcvar('ATTACK.RANGED.STAT') != 0)>
${pcstring('ATTACK.RANGED.STAT')}[STAT]
</#if>
${pcstring('ATTACK.RANGED.SIZE')}[SIZE]
<#if (pcvar('WEAPON.${weap}.FEATHIT') != 0)>
${pcstring('WEAPON.${weap}.FEATHIT')}[FEAT] 
</#if>
<#if (pcvar('WEAPON.${weap}.MAGICHIT') != 0)>
${pcstring('WEAPON.${weap}.MAGICHIT')}[MAGIC] 
</#if>
<#if (pcvar('WEAPON.${weap}.MISC') != 0)>
${pcstring('WEAPON.${weap}.MISC')}[MISC] 
</#if>
<#else>
To Hit: ${pcstring('WEAPON.${weap}.BASEHIT.0')}
= ${pcstring('ATTACK.MELEE.BASE')}[BAB] 
<#if (pcstring('ATTACK.MELEE.STAT') != "+0")>
${pcstring('ATTACK.MELEE.STAT')}[STAT]
</#if>
${pcstring('ATTACK.RANGED.SIZE')}[SIZE] 
<#if (pcstring('WEAPON.${weap}.FEATHIT') != "+0")>
${pcstring('WEAPON.${weap}.FEATHIT')}[FEAT] 
</#if>
<#if (pcstring('WEAPON.${weap}.MAGICHIT') != "+0")>
${pcstring('WEAPON.${weap}.MAGICHIT')}[MAGIC] 
</#if>
<#if (pcstring('WEAPON.${weap}.MISC') != "+0")>
${pcstring('WEAPON.${weap}.MISC')}[MISC] 
</#if>
</#if>

<#if (weaponCategory?contains('ranged'))>
<br>Damage: <b>${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')} </b>; 
<#else>
<br>Damage: <b>${pcstring('WEAPON.${weap}.DAMAGE')} </b>; 
</#if>
</blockquote>
<br>
</@loop>

<hr />
<b>Skills</b>

<blockquote>
<@loop from=0 to=pcvar('count("SKILLSIT", "VIEW=VISIBLE_EXPORT")')-1; skill , skill_has_next >
  <#if (pcvar('SKILLSIT.${skill}.RANK') = 0)>
    <#if (pcvar('SKILLSIT.${skill}.TOTAL') != 0)>
      <font color="#999999"><b>${pcstring('SKILLSIT.${skill}')}</b> ${pcstring('SKILLSIT.${skill}.TOTAL.INTVAL.SIGN')} = ${pcstring('SKILLSIT.${skill}.EXPLAIN_LONG')} </font><br>
    </#if>
  <#else>
    <b>${pcstring('SKILLSIT.${skill}')}</b> ${pcstring('SKILLSIT.${skill}.TOTAL.INTVAL.SIGN')} = ${pcstring('SKILLSIT.${skill}.RANK.INTVAL.SIGN')}[RANK] ${pcstring('SKILLSIT.${skill}.EXPLAIN_LONG')} <br>
  </#if>
</@loop>
</blockquote>

<b>Skill Points</b>
<blockquote>

<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next><#-- TODO: Loop was of early exit type 1 -->
&nbsp;${pcstring('CLASS.${class}.LEVEL')}&nbsp; ${pcstring('TEXT.NUMSUFFIX.CLASS.${class}.LEVEL')}&nbsp;level ${pcstring('CLASS.${class}')}: <b>${pcstring('SKILLPOINTS.TOTAL.${class}')}</b>
<#if (pcvar('SKILLPOINTS.UNUSED.${class}') != 0)>
<font color="#FF0000"> Unspent: ${pcstring('SKILLPOINTS.UNUSED.${class}')}</font>
</#if>
<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[FEATSALL.VISIBLE]') != 0)>
<hr />
</#if>

<#if (pcvar('COUNT[FEATS.VISIBLE]') != 0)>
<b>Feats</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[FEATS.VISIBLE]-1') ; feat , feat_has_next>
<b>${pcstring('FEAT.VISIBLE.${feat}')}</b> [<i>${pcstring('FEAT.VISIBLE.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[FEATSAUTO.VISIBLE]') != 0)>
<b>Feats (Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[FEATSAUTO.VISIBLE]-1') ; feat , feat_has_next>
<b>${pcstring('FEATAUTO.VISIBLE.${feat}')}</b> [<i>${pcstring('FEATAUTO.VISIBLE.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[VFEATS.VISIBLE]') != 0)>
<b>Feats (Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[VFEATS.VISIBLE]-1') ; feat , feat_has_next>
<b>${pcstring('VFEAT.VISIBLE.${feat}')}</b> [<i>${pcstring('VFEAT.VISIBLE.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[FEATSALL.HIDDEN]') != 0)>
<hr />
</#if>

<#if (pcvar('COUNT[FEATS.HIDDEN]') != 0)>
<b>Feats (Hidden)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[FEATS.HIDDEN]-1') ; feat , feat_has_next>
<b>${pcstring('FEAT.HIDDEN.${feat}')}</b> [<i>${pcstring('FEAT.HIDDEN.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[FEATSAUTO.HIDDEN]') != 0)>
<b>Feats (Hidden Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[FEATSAUTO.HIDDEN]-1') ; feat , feat_has_next>
<b>${pcstring('FEATAUTO.HIDDEN.${feat}')}</b> [<i>${pcstring('FEATAUTO.HIDDEN.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('COUNT[VFEATS.HIDDEN]') != 0)>
<b>Feats (Hidden Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('COUNT[VFEATS.VISIBLE]-1') ; feat , feat_has_next>
<b>${pcstring('VFEAT.HIDDEN.${feat}')}</b> [<i>${pcstring('VFEAT.HIDDEN.${feat}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') != 0)>
<hr />
</#if>


<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")') != 0)>
<b>Special Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITY.Special Ability.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('ABILITY.Special Ability.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=AUTOMATIC")') != 0)>
<b>Special Abilities (Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=AUTOMATIC")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYAUTO.Special Ability.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('ABILITYAUTO.Special Ability.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=VIRTUAL")') != 0)>
<b>Special Abilities (Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=VIRTUAL")')-1 ; ability , ability_has_next>
<b>${pcstring('VABILITY.Special Ability.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('VABILITY.Special Ability.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY")') != 0)>
<hr />
</#if>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=NORMAL")') != 0)>
<b>Special Abilities (Hidden)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=NORMAL")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITY.Special Ability.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('ABILITY.Special Ability.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=AUTOMATIC")') != 0)>
<b>Special Abilities (Hidden Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=AUTOMATIC")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYAUTO.Special Ability.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('ABILITYAUTO.Special Ability.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=VIRTUAL")') != 0)>
<b>Special Abilities (Hidden Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=VIRTUAL")')-1 ; ability , ability_has_next>
<b>${pcstring('VABILITY.Special Ability.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('VABILITY.Special Ability.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') != 0)>
<hr />
</#if>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")') != 0)>
<b>Internal Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITY.Internal.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('ABILITY.Internal.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=AUTOMATIC")') != 0)>
<b>Internal Abilities (Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=AUTOMATIC")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYAUTO.Internal.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('ABILITYAUTO.Internal.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=VIRTUAL")') != 0)>
<b>Internal Abilities (Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=VIRTUAL")')-1 ; ability , ability_has_next>
<b>${pcstring('VABILITY.Internal.VISIBLE.${ability}.KEY')}</b> [<i>${pcstring('VABILITY.Internal.VISIBLE.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY")') != 0)>
<hr />
</#if>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=NORMAL")') != 0)>
<b>Internal Abilities (Hidden)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=NORMAL")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITY.Internal.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('ABILITY.Internal.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=AUTOMATIC")') != 0)>
<b>Internal Abilities (Hidden Automatic)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=AUTOMATIC")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYAUTO.Internal.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('ABILITYAUTO.Internal.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=VIRTUAL")') != 0)>
<b>Internal Abilities (Hidden Virtual)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=VIRTUAL")')-1 ; ability , ability_has_next>
<b>${pcstring('VABILITY.Internal.HIDDEN.${ability}.KEY')}</b> [<i>${pcstring('VABILITY.Internal.HIDDEN.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Skill")') != 0)>
<b>Skill Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Skill")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Skill.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Skill.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Language")') != 0)>
<b>Language Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Language")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Language.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Language.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Weapon")') != 0)>
<b>Weapon Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Weapon")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Weapon.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Weapon.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Class")') != 0)>
<b>Class Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Class")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Class.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Class.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Natural Attack")') != 0)>
<b>Natural Attack Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Natural Attack")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Natural Attack.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Natural Attack.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Domain")') != 0)>
<b>Domain Abilities</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Domain")')-1 ; domain , domain_has_next>
<b>${pcstring('ABILITYALL.Domain.${ability}.KEY')}</b> [<i>${pcstring('ABILITYALL.Domain.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>

<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability")') != 0)>
<b>Special Abilities (Everything)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Internal.ALL.${ability}.KEY')}</b> ${pcstring('ABILITYALL.Special Ability.ALL.${ability}.ASSOCIATED')}</b> [<i>${pcstring('ABILITYALL.Special Ability.ALL.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>




<#if (pcvar('count("ABILITIES","CATEGORY=Internal")') != 0)>
<b>Internal Abilities (Everything)</b>
</#if>
<blockquote>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Internal")')-1 ; ability , ability_has_next>
<b>${pcstring('ABILITYALL.Internal.ALL.${ability}.KEY')}</b> ${pcstring('ABILITYALL.Internal.ALL.${ability}.ASSOCIATED')}</b> [<i>${pcstring('ABILITYALL.Internal.ALL.${ability}.SOURCE')}</i>]<br>
</@loop>
</blockquote>






<!-- START Template Table -->
<#if (pcvar("COUNT[TEMPLATES]") > 0) >
   <table width="100%" cellspacing="0" cellpadding="3" summary="Template Table">
     <tr>
       <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: 9pt"><b>TEMPLATES</b></font></td>
     </tr>
     <tr>
       <td valign="top" width="70%" class="border8">${pcstring('TEMPLATELIST')}<br /></td>
     </tr>
     <@loop from=0 to=pcvar('COUNT[TEMPLATES]-1') ; template , template_has_next><#-- TODO: Loop was of early exit type 1 -->
	 <tr>
	   <td >
	     ${pcstring('TEMPLATE.${template}.NAME')}
	   </td>
	 </tr>
	 </@loop>
    </table>
    <font style="font-size:2pt"><br /></font>
</#if>
<!-- STOP Template Table -->

<!--   Trouble-shooting stuff   -->
 
<hr />

<#if (pcvar('FEATPOINTS') != 0)>
<font color="#FF0000" size="+1"><strong>Unspent Feat Points: ${pcstring('FEATPOINTS.INTVAL')}</strong></font>
</#if>
<#if (pcvar('SKILLPOINTS.UNUSED') != 0)>
<font color="#FF0000" size="+1"><strong>Unspent Skill Points: ${pcstring('SKILLPOINTS.UNUSED.INTVAL')}</strong></font>
</#if>

<p>Skill Breakdown</p>
<p>
<@loop from=0 to=pcvar('count("SKILLSIT", "VIEW=VISIBLE_EXPORT")')-1; skill , skill_has_next >
  <#if (pcvar('SKILL.${skill}.RANK') = 0)>
    <#if (pcvar('SKILL.${skill}.TOTAL') != 0)>
      <font color="#FF6600"><b>${pcstring('SKILL.${skill}')} ${pcstring('SKILL.${skill}.TOTAL.INTVAL.SIGN')} =</b> ${pcstring('SKILL.${skill}.EXPLAIN_LONG')} </font><br />
    </#if>
  </#if>
</@loop>
</p>


<p>Test New Formula Export</p>
<!--   uses *val.   -->




<p>Test New FACT Export</p>
<!--   uses *fact.   -->



<p>Test New INFO Export</p>
<!--   uses *info.   -->
<p>Test for INFO using 'Wildness' = 
</p>

<font size="-2">Created using PCGen ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} </font>


<!--   End Trouble-shooting stuff   -->
<hr />

</body>
</html>
