<#-- Common code for the pathfinder statblocks -->
<#-- This file is included by pathfinder statblocks sheets -->

<!-- initiative, senses -->
<p>
<b>Init</b> ${pcstring('INITIATIVEMOD')};
<b>Senses</b>
<!-- Sense and Vision TYPE Abilities --><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Sense[or]TYPE=Vision","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.ASPECT.Ability Bonus')} <#t>
</#if>
<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.HASASPECT.Vision") = "Y")>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.ASPECT.Vision')}<#t>
<#else>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.TYPE")?lower_case?contains("spelllike"))>
<i>${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision')}</i><#t>
<#else>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision')}<#t>
</#if>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Sense.TYPE=Vision.ASPECT.Ability Benefit')}<#t>
</#if>
, <#t>
</@loop>
<!-- End Sense TYPE Abilities -->
Perception ${pcstring('SKILL.Perception.TOTAL.SIGN')}<#lt>
</p>

<!-- auras -->
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Aura","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<p>
<!-- Aura TYPE Abilities -->
<b>Aura </b><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Aura","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura.ASPECT.Ability Bonus')} <#t>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura.TYPE")?lower_case?contains("spelllike"))>
<i>${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura')}</i><#t>
<#else>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura')}<#t>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Aura.ASPECT.Ability Benefit')}<#t>
</#if>
<#if (specialAbilities_has_next) >
,${' '}<#t>
</#if>
</@loop>

<!-- End Aura TYPE Abilities -->
</p>
</#if>

<table class="section">
  <tr>
    <td class="section">DEFENSE</td>
  </tr>
</table>

<!-- armor class -->
<p>
<b>EAC</b> ${pcstring('AC.EAC')}(<#t>
<#t>
${pcstring('AC.BASE')} Base<#t>
<#t>
${pcstring('AC.Ability.SIGN')} Dex<#t>
<#if (pcstring("AC.EAC_Armor") = "0")>
<#else>
${pcstring('AC.EAC_Armor.SIGN')} EAC Armor<#t>
</#if>
<#t>
${pcstring('AC.Misc.SIGN')} Misc<#t>
)<#lt>

</p>
<p>
<b>KAC</b> ${pcstring('AC.KAC')}(<#t>
<#t>
${pcstring('AC.BASE')} Base<#t>
<#t>
${pcstring('AC.Ability.SIGN')} Dex<#t>
<#t>
<#if (pcstring("AC.KAC_Armor") = "0")>
<#else>
${pcstring('AC.KAC_Armor.SIGN')} EAC Armor<#t>
</#if>
<#t>
${pcstring('AC.Misc.SIGN')} Misc<#t>
)<#lt>
</p>


<!-- hit points -->
<p><b>Stamina</b> ${pcstring('ALTHP')}</p>
<b>hp</b> ${pcstring('HP')} (${pcstring('HITDICE.MEDIUM')})<#t>	Resolve ${pcvar('VAR.Resolve')}
<#t>

<!-- ModifyHP TYPE Abilities --><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=ModifyHP","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialHitPoints , specialHitPoints_has_next>
<#if (specialHitPoints_has_next) >
; <#lt>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialHitPoints}.TYPE=ModifyHP.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialHitPoints}.TYPE=ModifyHP.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialHitPoints}.TYPE=ModifyHP')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialHitPoints}.TYPE=ModifyHP.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialHitPoints}.TYPE=ModifyHP.ASPECT.Ability Benefit')}<#t>
</#if>
<#if (specialHitPoints_has_next) >
; <#lt>
</#if>
</@loop>
<!-- End ModifyHP TYPE Abilities -->
<br>
<#t>
<b>Fort</b> ${pcstring('CHECK.FORTITUDE.TOTAL')}, <b>Ref</b> ${pcstring('CHECK.REFLEX.TOTAL')}, <b>Will</b> ${pcstring('CHECK.2.TOTAL')}<#t>
<#t>
<!-- SaveBonus TYPE Abilities --><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SaveBonus","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; savebonus , savebonus_has_next>
, ${pcstring('ABILITYALL.ANY.VISIBLE.${savebonus}.ASPECT=SaveBonus.ASPECT.SaveBonus')}<#t>
</@loop>
<br>
<!-- End SaveBonus TYPE Abilities -->

<!--- defensive abilities, damager reduction, immune, resist, SR --->
<p>
<!-- Defensive TYPE Abilities -->
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Defensive","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<@compress single_line=true>
<b>Defensive Abilities</b>${" "}<#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Defensive","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; defensiveAbilities , defensiveAbilities_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${defensiveAbilities}.TYPE=Defensive.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${defensiveAbilities}.TYPE=Defensive.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${defensiveAbilities}.TYPE=Defensive')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${defensiveAbilities}.TYPE=Defensive.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${defensiveAbilities}.TYPE=Defensive.ASPECT.Ability Benefit')}<#t>
</#if>
<#if (defensiveAbilities_has_next) >
,${' '}<#t>
</#if>
</@loop>
</@compress>
; 
</#if>
<!-- End Defensive TYPE Abilities -->

<@compress single_line=true>
<#assign dr = pcstring('DR') />
<#if (dr != '')>
<b>DR</b> ${dr?lower_case}<#t>
<#if (pcvar('countdistinct("ABILITIES","TYPE=Immunity","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0 || pcvar('countdistinct("ABILITIES","TYPE=Resistance","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0 || pcvar('SR') > 0) >
;${" "}<#t>
</#if>
</#if>

<!-- Immunity TYPE Abilities -->
<@compress single_line=true>
<#if (pcvar('countdistinct("ABILITIES","TYPE=Immunity","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<b>Immune </b><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Immunity","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; immunities , immunities_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.ASPECT.Ability Bonus')} <#t>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.HASASPECT.Immunity") = "Y")>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.ASPECT.Immunity')}<#t>
<#else>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity')}<#t>
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${immunities}.TYPE=Immunity.ASPECT.Ability Benefit')}<#t>
</#if>
<#if (immunities_has_next) >
,${" "}
<#else>
<#if (pcvar('countdistinct("ABILITIES","TYPE=Resistance","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0 || pcvar('SR') > 0) >
;${" "}
</#if>
</#if>
</@loop>
</#if>
</@compress>

<!-- End Immunity TYPE Abilities -->

<!-- Resistance TYPE Abilities -->
<#if (pcvar('countdistinct("ABILITIES","TYPE=Resistance","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<b>Resist </b>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Resistance","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; resistances , resistances_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.ASPECT.Ability Bonus')} 
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.HASASPECT.Resistance") = "Y")>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.ASPECT.Resistance')}
<#else>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance')}
</#if>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.HASASPECT.Ability Benefit") = "Y")>
  ${pcstring('ABILITYALL.Special Ability.VISIBLE.${resistances}.TYPE=Resistance.ASPECT.Ability Benefit')}
</#if>
<#if (resistances_has_next) >
,${' '}
<#else>
<#if (pcstring("SR") = "0")>
<#else>
;${' '}
</#if>
</#if>
</@loop>
</#if>
<!-- End Resistance TYPE Abilities -->

<#if (pcstring("SR") = "0")>
<#else>
<b>SR</b> ${pcstring('SR')}
</#if>
</@compress>

</p>

<!-- Weakness TYPE Abilities -->
<#if (pcvar('countdistinct("ABILITIES","TYPE=Weakness","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<@compress single_line=true>
<b>Weaknesses </b><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Weakness","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; weaknesses , weaknesses_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${weaknesses}.TYPE=Weakness.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${weaknesses}.TYPE=Weakness.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${weaknesses}.TYPE=Weakness')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${weaknesses}.TYPE=Weakness.HASASPECT.Ability Benefit") = "Y")>
  ${pcstring('ABILITYALL.Special Ability.VISIBLE.${weaknesses}.TYPE=Weakness.ASPECT.Ability Benefit')}<#t>
</#if>
,${' '}<#t>
</@loop>
<br><#t>
</@compress>
</#if>
<!-- End Weakness TYPE Abilities -->

<!-- Defensive Gear -->
<#if (pcvar('COUNT[EQTYPE.DefensiveGear]') > 0) >
<b>Defensive Gear </b>
<@loop from=0 to=pcvar('COUNT[EQTYPE.DefensiveGear]-1') ; defensiveGear , defensiveGear_has_next> <#-- TODO: Loop was of early exit type 1 -->
<i>${pcstring('TEXT.LOWER.EQ.IS.DefensiveGear.${defensiveGear}.NAME')}; </i>
</@loop>
<br>
</#if>
<!-- End Defensive Gear -->

<table class="section">
  <tr>
    <td class="section">OFFENSE</td>
  </tr>
</table>
<#t>
<@compress single_line=true>
<b>Speed</b>${" "}<#t>
<@loop from=0 to=pcvar('COUNT[MOVE]-1') ; movement , movement_has_next>
<#if (pcstring("MOVE.0.NAME") = "Walk")>
<#if (pcstring("MOVE.${movement}.NAME") = "Walk")>
${pcstring('MOVE.${movement}.RATE')}<#t>
<#else>
${pcstring('TEXT.LOWERCASE.MOVE.${movement}.NAME')} ${pcstring('MOVE.${movement}.RATE')}<#t>
</#if>
<#else>
${pcstring('TEXT.LOWERCASE.MOVE.${movement}.NAME')} ${pcstring('MOVE.${movement}.RATE')}<#t>
</#if>
<#if (pcstring("MOVE.${movement}.NAME") = "Fly")>
${" "}(${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.HIDDEN.0.TYPE=Maneuverability.ASPECT.Maneuverability')})<#t>
</#if>
<#if movement_has_next>
,${" "}<#t>
</#if>
</@loop>
<#t>
<!-- ModifyMovement TYPE Abilities --><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Feat","TYPE=ModifyMovement","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; movementAbilities , movementAbilities_has_next>
,${' '}<#t>
<#if (pcstring("ABILITYALL.Feat.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Feat.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Feat.VISIBLE.${movementAbilities}.TYPE=ModifyMovement')}
<#if (pcstring("ABILITYALL.Feat.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Feat.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.ASPECT.Ability Benefit')}<#t>
</#if>
</@loop>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=ModifyMovement","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; movementAbilities , movementAbilities_has_next>
,${' '}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${movementAbilities}.TYPE=ModifyMovement')}
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${movementAbilities}.TYPE=ModifyMovement.ASPECT.Ability Benefit')}<#t>
</#if>
</@loop>
</@compress>

<!-- End ModifyMovement TYPE Abilities -->

<!-- Attacks -->
<br>
<@compress single_line=true>
<@loop from=0 to=pcvar('COUNT[EQTYPE.Weapon]-1') ; weap , weap_has_next>
<#if (pcstring("WEAPON.${weap}.HAND") != "Equipped")>
<#if (pcstring("WEAPON.${weap}.NAME") = "Swarm")>
<b>Melee </b><i>${pcstring('TEXT.LOWER.WEAPON.${weap}.NAME.NOSTAR')} </i> (${pcstring('WEAPON.${weap}.DAMAGE')})<#t>
<#else>
<#if (pcboolean("WEAPON.${weap}.ISTYPE.Ranged")) >
<b>Ranged </b><#t>
<#else>
<b>Melee </b><#t>
</#if>
<#if (pcstring("WEAPON.${weap}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.WEAPON.${weap}.NAME.NOSTAR')} </i><#t>
<#else>
${pcstring('TEXT.LOWER.WEAPON.${weap}.NAME.NOSTAR')} <#t>
</#if>
<#if (pcstring("WEAPON.${weap}.BASEHIT") = pcstring("INVALIDTEXT.TOHIT"))>
(two handed) ${pcstring('WEAPON.${weap}.THHIT')} ((two handed) ${pcstring('WEAPON.${weap}.THDAMAGE')}<#t>
<#if (pcstring('WEAPON.${weap}.CRIT')?length = 2)>
<#else>
/${pcstring('WEAPON.${weap}.CRIT')}<#t>
</#if>
<#if (pcstring("WEAPON.${weap}.MULT") = "2")>
<#else>
/x${pcstring('WEAPON.${weap}.MULT')}<#t>
</#if>
)<#t>
<#else>
${pcstring('WEAPON.${weap}.BASEHIT')} <#t>

<#if (pcstring("WEAPON.${weap}.TYPE")?lower_case?contains("ranged")) >
(${pcstring('WEAPON.${weap}.RANGELIST.1.DAMAGE')}<#t>
<#else>
(${pcstring('WEAPON.${weap}.DAMAGE')}<#t>
</#if>
<#if (pcstring('WEAPON.${weap}.CRIT')?length = 2)>
<#else>
/${pcstring('WEAPON.${weap}.CRIT')}<#t>
</#if>
<#if (pcstring("WEAPON.${weap}.MULT") = "2")>
<#else>
/x${pcstring('WEAPON.${weap}.MULT')}<#t>
</#if>
)<#t>
<#if (pcstring("WEAPON.${weap}.TYPE")?lower_case?contains("ranged") && (pcboolean('VAR.HASFEAT:Point-Blank Shot') || pcboolean('VAR.HASFEAT:Point Blank Shot'))) >
, within 30 ft. ${pcstring('WEAPON.${weap}.RANGELIST.0.BASEHIT')} (${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')})<#t>
</#if>
</#if>
</#if>
<br><#t>
</#if>
</@loop>
</@compress>

<!-- End Attacks -->

<!-- Space and Reach -->
<#if (pcstring("FACE") != "5 ft." || pcstring("REACH") != "5 ft.") >
<p>
<b>Space</b> ${pcstring('FACE')}; <b>Reach</b> ${pcstring('REACH')}
</p>
</#if>

<!-- SpecialAttack TYPE Abilities -->
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialAttack","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<p>
<@compress single_line=true>
<b>Special Attacks </b><#t>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialAttack","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialActions , specialActions_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpecialAttack.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpecialAttack.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpecialAttack')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpecialAttack.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpecialAttack.ASPECT.Ability Benefit')}<#t>
</#if>
<#if (specialActions_has_next) >
,${' '}<#t>
</#if>
</@loop>
</p>
</@compress>
</#if>

<!-- End SpecialAttack TYPE Abilities -->

<!-- Innate Spell-Like Abilities -->
<#if (pcvar("COUNT[SPELLSINBOOK.0.1.0]") > 0)>
<b>Innate Spell-Like Abilities:</b>
<#assign spellbook=1/>
<#assign class=0/>
<#assign level=0/>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</i> (
<#if (!pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO")?contains("None")
 && !pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO")?contains("Harmless"))>
DC ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')},
</#if>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "At Will")>
at will)
<#else>
${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}/${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')})
</#if>
</@loop>
</#if>
<!-- End Innate Spell-Like Abilities -->

<!-- Other Spell-Like Abilities -->
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
<#if (pcstring("SPELLBOOK.${spellbook}.TYPE") = "Innate Spell List")>
<#if (pcvar("COUNT[SPELLSINBOOK.0.${spellbook}.0]") > 0)>
<b>${pcstring('SPELLBOOK.${spellbook}.NAME')} Spell-Like Abilities:</b>
<@compress single_line=true>
<#assign class = 0/>
<#assign level = 0/>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</i> (<#t>
<#assign saveinfo = pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO")?lower_case />
<#if (saveinfo?contains("none") || saveinfo?contains("harmless") || pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE")?lower_case?contains("personal"))>
<#else>
DC ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')},<#t>
</#if>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES")?contains("Will"))>
at will)<#t>
<#else>
${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}/${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')})<#t>
</#if>
<#if (spell_has_next) >
,${" "}<#t>
</#if>
</@loop>
</@compress>
</#if>
</#if>
</@loop>
<!-- End Other Spell-Like Abilities -->

<!-- Domain Power Spell-Like Abilities -->
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpellLike.DomainPower","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<br><b>Domain Power Spell-Like Abilities</b>${' '}
<@compress single_line=true>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpellLike.DomainPower","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialActions , specialActions_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpellLike.DomainPower.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpellLike.DomainPower.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpellLike.DomainPower')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpellLike.DomainPower.HASASPECT.Ability Benefit") = "Y")>
 ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialActions}.TYPE=SpellLike.DomainPower.ASPECT.Ability Benefit')}<#t>
</#if>
<#if specialActions_has_next >
,${' '}<#t>
</#if>
</@loop>
</@compress>
</#if>


<!-- Known Spells -->
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next>
<#if (pcstring("SPELLLISTMEMORIZE.${class}") = "false")>
<br><b>Known ${pcstring('SPELLLISTCLASS.${class}')} Spells</b>
<#if (pcstring('SPELLLISTCLASS.${class}.CONCENTRATION') = "")>
(CL <@suffixnum num=pcvar('SPELLLISTCLASS.${class}.CASTERLEVEL') />
<#else>
(CL <@suffixnum num=pcvar('SPELLLISTCLASS.${class}.CASTERLEVEL') />, concentration ${pcstring('SPELLLISTCLASS.${class}.CONCENTRATION')}):
</#if>
<@loop from=9 to=0 step=-1; level, level_has_next >
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.0.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.0.${level}]') ; spelllevelcount , spelllevelcount_has_next>
<#if (spelllevelcount = 0)>
<!-- no memorized spells for SPELLSINBOOK.${class} 0 ${level} -->
<#else>
<p class="spells"><@compress single_line=true>
<#if (level = 0)>
0 (at will)<#t>
<#else>
<@suffixnum num=level/> (${pcstring('SPELLLISTCAST.${class}.${level}')}/day)<#t>
</#if>
&mdash;<#t>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.0.${level}]-2') ; spell , spell_has_next>
<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.0.${level}.${spell}.NAME')}</i><#t>
 <#assign saveinfo = pcstring('SPELLMEM.${class}.0.${level}.${spell}.SAVEINFO')?lower_case /> 
 <#if (saveinfo?contains("none") || saveinfo?contains("harmless") || pcstring("SPELLMEM.${class}.0.${level}.${spell}.RANGE")?lower_case?contains("personal"))>
 <#else>
 (DC ${pcstring('SPELLMEM.${class}.0.${level}.${spell}.DC')})<#t>
 </#if>
, <#t>
</@loop>
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.0.${level}]-1') to=pcvar('COUNT[SPELLSINBOOK.${class}.0.${level}]-1') ; spell , spell_has_next>
${pcstring('SPELLMEM.${class}.0.${level}.${spell}.BONUSSPELL')}<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.0.${level}.${spell}.NAME')}</i><#t>
 <#assign saveinfo = pcstring('SPELLMEM.${class}.0.${level}.${spell}.SAVEINFO')?lower_case /> 
 <#if (saveinfo?contains("none") || saveinfo?contains("harmless") || pcstring("SPELLMEM.${class}.0.${level}.${spell}.RANGE")?lower_case?contains("personal"))>
 <#else>
 (DC ${pcstring('SPELLMEM.${class}.0.${level}.${spell}.DC')})<#t>
 </#if>
</@loop>
</@compress>

</p>
</#if>
</@loop>
</@loop>
</#if>
</@loop>
<!-- End Known Spells -->

<!-- Prepared Spells -->
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
<#if (pcstring("SPELLBOOK.${spellbook}.TYPE") = "Prepared Spell List" && pcvar("SPELLBOOK.${spellbook}.NUMPAGES") = 0) >
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next>
<#if (pcboolean("SPELLLISTMEMORIZE.${class}"))>
<p><b>${pcstring('SPELLLISTCLASS.${class}')} Spells Prepared</b>
(CL <@suffixnum num=pcvar('SPELLLISTCLASS.${class}.LEVEL') />, concentration ${pcstring('SPELLLISTCLASS.${class}.CONCENTRATION')}):
</p>
<@loop from=9 to=0 step=-1; level, level_has_next >
 <#assign spelllevelcount = pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') />
 <#if (spelllevelcount = 0)>
<!-- no memorized spells for SPELLSINBOOK.${class}.${spellbook}.${level} -->
 <#else>
<p class="spells"><@compress single_line=true>
  <#if (level = 0)>
0${' '}(at will)<#t>
  <#else>
<@suffixnum num=level /><#t>
  </#if>
&mdash;<#t>
  <@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.APPLIEDNAME')}<#t>
<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.BASENAME')}</i><#t>
   <#if (pcstring("DOMAIN.1") != "") >
<sup>&#8201;${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELLD')}</sup><#t>
   </#if>
   <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT") = "CR")>
   <#else>
<sup>&#8201;${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')}</sup><#t>
   </#if>
   <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "1")>
   <#else>
${' '}(${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')})<#t>
   </#if>
   <#assign saveinfo = pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')?lower_case /> 
   <#if (saveinfo?contains("none") || saveinfo?contains("harmless") || pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE")?lower_case?contains("personal"))>
   <#else>
 ${' '}(DC ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})<#t>
   </#if>
   <#if (spell_has_next)>
,${' '}<#t>
   </#if>
  </@loop>
 </@compress>

</p>
</#if>
</@loop>
</#if>
</@loop>
<#else>
</#if>
</@loop>
<!-- End Prepared Spells -->

<!-- domains -->
<#if (pcvar("COUNT[DOMAINS]") > 0) >
<p class="spells">
<b>D</b> Domain spell;
<b>Domains</b>
<@compress single_line=true>
<@loop from=1 to=pcvar('COUNT[DOMAINS]') ; domain , domain_has_next>
${pcstring('DOMAIN.${domain}')}<#t>
<#if (domain_has_next)>
,${' '}<#t>
</#if>
</@loop>
</@compress>
</p>
</#if>
<!-- End Domain list -->


<p></p>
<#if (pcvar("COUNT[NOTES]") > 0) >
<table class="section">
  <tr>
    <td class="section">TACTICS</td>
  </tr>
</table>
<!-- Before Combat -->
<p>
<@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note, note_has_next>
<#if (pcstring("NOTE.${note}.NAME") = "Before Combat")>
<b>Before Combat</b>${' '}${pcstring('NOTE.${note}.VALUE')}
</#if>
</@loop>
</p>
<!-- During Combat -->
<p>
<@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note, note_has_next>
<#if (pcstring("NOTE.${note}.NAME") = "During Combat")>
<b>During Combat</b>${' '}${pcstring('NOTE.${note}.VALUE')}
</#if>
</@loop>
</p>
<!-- Morale -->
<p>
<@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note, note_has_next>
<#if (pcstring("NOTE.${note}.NAME") = "Morale")>
<b>Morale</b>${' '}${pcstring('NOTE.${note}.VALUE')}
</#if>
</@loop>
</#if>
</p>

<table class="section">
  <tr>
    <td class="section">STATISTICS</td>
  </tr>
</table>

<@compress single_line=true>
<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next> <#-- TODO: Loop was of early exit type 1 -->
<b>${pcstring('TEXT.TITLECASE.STAT.${stat}.NAME')}</b>${' '}<#t>
<#if (pcstring("STAT.${stat}.ISNONABILITY") = "Y")>
&mdash;<#t>
<#else>
${pcstring('STAT.${stat}')}<#t>
</#if>
<#if (stat_has_next)>
,${' '}<#t>
</#if>
</@loop>
</@compress>

<br>

<@compress single_line=true>
<b>Base Atk</b> ${pcstring('ATTACK.MELEE.BASE')}; <#t>
<#if (pcvar("UseCombatManueverBonus") = 1)>
 <#assign useAPGCombatManuevers = (pcvar("UseAPGCombatManuevers") = 1) />
 <b>CMB</b> ${pcstring('VAR.CMB.INTVAL.SIGN')}<#t>
 <#if (pcvar("CMB") != pcvar("CMB_BullRush")) >
 (${pcstring('VAR.CMB_BullRush.INTVAL.SIGN')} bull rush)<#t>
</#if>
 <#if (useAPGCombatManuevers && pcvar("CMB") != pcvar("CMB_DirtyTricks")) >
 (${pcstring('VAR.CMB_DirtyTricks.INTVAL.SIGN')} dirty tricks)<#t>
 </#if>
 <#if (pcvar("CMB") != pcvar("CMB_Disarm")) >
 (${pcstring('VAR.CMB_Disarm.INTVAL.SIGN')} disarm)<#t>
 </#if>
 <#if (useAPGCombatManuevers && pcvar("CMB") != pcvar("CMB_Drag")) >
 (${pcstring('VAR.CMB_Drag.INTVAL.SIGN')} drag)<#t>
 </#if>
 <#if (pcvar("CMB") != pcvar("CMB_Grapple")) >
 (${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')} grapple)<#t>
 </#if>
 <#if (pcvar("CMB") != pcvar("CMB_Overrun")) >
 (${pcstring('VAR.CMB_Overrun.INTVAL.SIGN')} overrun)<#t>
 </#if>
 <#if (useAPGCombatManuevers && pcvar("CMB") != pcvar("CMB_Reposition"))>
 (${pcstring('VAR.CMB_Reposition.INTVAL.SIGN')} reposition)<#t>
 </#if>
 <#if (useAPGCombatManuevers && pcvar("CMB") != pcvar("CMB_Steal"))>
 (${pcstring('VAR.CMB_Steal.INTVAL.SIGN')} steal)<#t>
 </#if>
 <#if (pcvar("CMB") != pcvar("CMB_Sunder")) >
 (${pcstring('VAR.CMB_Sunder.INTVAL.SIGN')} sunder)<#t>
 </#if>
 <#if (pcvar("CMB") != pcvar("CMB_Trip")) >
 (${pcstring('VAR.CMB_Trip.INTVAL.SIGN')} trip)<#t>
 </#if>
 <#if (pcvar("CMD") > 0) >
<b>; CMD</b> ${pcstring('VAR.CMD.INTVAL')}<#t>
  <#if (pcvar("CMD") != pcvar("CMD_BullRush")) >
 (${pcstring('VAR.CMD_BullRush.INTVAL')} vs. bull rush)<#t>
  </#if>
  <#if (useAPGCombatManuevers && pcvar("CMD") != pcvar("CMD_DirtyTricks")) >
 (${pcstring('VAR.CMD_DirtyTricks.INTVAL')} vs. dirty tricks)<#t>
  </#if>
  <#if (pcvar("CMD") != pcvar("CMD_Disarm")) >
 (${pcstring('VAR.CMD_Disarm.INTVAL')} vs. disarm)<#t>
  </#if>
  <#if (useAPGCombatManuevers && pcvar("CMD") != pcvar("CMD_Drag")) >
 (${pcstring('VAR.CMD_Drag.INTVAL')} vs. drag)<#t>
  </#if>
  <#if (pcvar("CMD") != pcvar("CMD_Grapple")) >
 (${pcstring('VAR.CMD_Grapple.INTVAL')} vs. grapple)<#t>
  </#if>
  <#if (pcvar("CMD") != pcvar("CMD_Overrun")) >
 (${pcstring('VAR.CMD_Overrun.INTVAL')} vs. overrun)<#t>
  </#if>
  <#if (useAPGCombatManuevers && pcvar("CMD") != pcvar("CMD_Reposition")) >
 (${pcstring('VAR.CMD_Reposition.INTVAL')} vs. reposition)<#t>
  </#if>
  <#if (useAPGCombatManuevers && pcvar("CMD") != pcvar("CMD_Steal")) >
 (${pcstring('VAR.CMD_Steal.INTVAL')} vs. steal)<#t>
  </#if>
  <#if (pcvar("CMD") != pcvar("CMD_Sunder")) >
 (${pcstring('VAR.CMD_Sunder.INTVAL')} vs. sunder)<#t>
  </#if>
  <#if (pcvar("CantBeTripped") != 0) >
 (can't be tripped)<#t>
  <#elseif (pcvar("CMD") != pcvar("CMD_Trip")) >
 (${pcstring('VAR.CMD_Trip.INTVAL')} vs. trip)<#t>
  </#if>
 </#if>
<#else>
<b>Grp</b>
 <#if (pcvar("CanNotGrapple")=0)>
${pcvar('VAR.(STR+BAB+(SIZE-4)+(SIZE-4)+(SIZE-4)+(SIZE-4)+(ATTACK.GRAPPLE.MISC)).INTVAL.SIGN')}<#t>
 <#else>
&mdash;<#t>
 </#if>
</#if>
</@compress>

<br>

<p>
<@compress single_line=true>
<#if (pcvar("COUNT[FEATSALL.VISIBLE]") > 0) >
<b>Feats </b><#t>
<@loop from=0 to=pcvar('COUNT[FEATSALL.VISIBLE]-1') ; feat , feat_has_next>
${pcstring("FEATALL.VISIBLE.${feat}")}<#t>
<#if (feat_has_next) >
,${" "}
</#if>
</@loop>
</#if>
</@compress>
</p>

<p>
<b>Skills</b>
<@compress single_line=true>
<@loop from=0 to=pcvar('count("SKILLSIT","NONDEFAULT")')-1 ; skill, skill_has_next >
${pcstring('SKILLSIT.${skill}.NONDEFAULT')}${' '}<#t>
${pcstring('SKILLSIT.${skill}.NONDEFAULT.TOTAL.INTVAL.SIGN')}${' '}<#t>
<#if (skill_has_next)>
,${' '}<#t>
</#if>
</@loop>
</@compress>
</p>

<!-- Trait TYPE Abilities -->
<p>
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Trait","EXCLUDETYPE=Implicit.Immunity.Defensive.Weakness.Communicate.Vision","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
 <@compress single_line=true>
<b>Traits</b>${' '}<#t>
  <@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Trait","EXCLUDETYPE=Implicit.Immunity.Defensive.Weakness.Communicate.Vision","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
   <#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Trait.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;Vision.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Trait.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;VisionASPECT.Ability Bonus')}<#t>
   </#if>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Trait.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;Vision')}<#t>
   <#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Trait.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;Vision.HASASPECT.Ability Benefit") = "Y")>
${' '}${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Trait.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;Vision.ASPECT.Ability Benefit')}<#t>
   </#if>
   <#if (specialAbilities_has_next) >
,${' '}<#t>
   </#if>
  </@loop>
 </@compress>
</#if>
</p>
<!-- End SpecialQuality TYPE Abilities -->




<#macro communicationBlock>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=Communicate","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Communicate.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Communicate.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Communicate')}<#t>
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Communicate.HASASPECT.Ability Benefit") = "Y")>
  ${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=Communicate.ASPECT.Ability Benefit')}<#t>
</#if>
,${' '}<#t>
</@loop>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Feat","TYPE=Communicate","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
<#if (pcstring("ABILITYALL.Feat.VISIBLE.${specialAbilities}.TYPE=Communicate.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Feat.VISIBLE.${specialAbilities}.TYPE=Communicate.ASPECT.Ability Bonus')} <#t>
</#if>
${pcstring('ABILITYALL.Feat.VISIBLE.${specialAbilities}.TYPE=Communicate')}<#t>
<#if (pcstring("ABILITYALL.Feat.VISIBLE.${specialAbilities}.TYPE=Communicate.HASASPECT.Ability Benefit") = "Y")>
  ${pcstring('ABILITYALL.Feat.VISIBLE.${specialAbilities}.TYPE=Communicate.ASPECT.Ability Benefit')}<#t>
</#if>
,${' '}<#t>
</@loop>
</#macro>

<!-- Languages and Communicate TYPE Abilities -->
<@compress single_line=true>
<#if pcstring("LANGUAGES")?length = 0 >
 <#if (pcvar('countdistinct("ABILITIES","TYPE=Communicate","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<b>Communication</b> <#t>
 <@communicationBlock />
<br><#t>
 </#if>
<#else>
<p><#t>
<b>Languages</b> ${pcstring('LANGUAGES')}<#t>
 <#if (pcvar('countdistinct("ABILITIES","TYPE=Communicate","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
,${' '}<#t>
 </#if>
 <@communicationBlock />
</p>
</#if>
</@compress>

<!-- End Languages Communicate TYPE Abilities -->

<!-- SpecialQuality TYPE Abilities -->
<p>
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialQuality","EXCLUDETYPE=Trait.Implicit.Immunity.Defensive.Weakness.Communicate.Vision","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
 <@compress single_line=true>
<b>SQ</b>${' '}<#t>
  <@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialQuality","EXCLUDETYPE=Trait.Implicit.Immunity.Defensive.Weakness.Communicate.Vision","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAbilities , specialAbilities_has_next>
   <#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=SpecialQuality.EXCLUDETYPE=Trait;Implicit;Immunity;Defensive;Weakness;Communicate;Vision.HASASPECT.Ability Bonus") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=SpecialQuality.EXCLUDETYPE=Implicit;Immunity;Defensive;Weakness;Communicate;VisionASPECT.Ability Bonus')}<#t>
   </#if>
${pcstring('TEXT.LOWERCASE.ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=SpecialQuality.EXCLUDETYPE=Trait.Implicit;Immunity;Defensive;Weakness;Communicate;Vision')}<#t>
   <#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=SpecialQuality.EXCLUDETYPE=Trait;Implicit;Immunity;Defensive;Weakness;Communicate;Vision.HASASPECT.Ability Benefit") = "Y")>
${' '}${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAbilities}.TYPE=SpecialQuality.EXCLUDETYPE=Trait;Implicit;Immunity;Defensive;Weakness;Communicate;Vision.ASPECT.Ability Benefit')}<#t>
   </#if>
   <#if (specialAbilities_has_next) >
,${' '}<#t>
   </#if>
  </@loop>
 </@compress>
</#if>
</p>
<!-- End SpecialQuality TYPE Abilities -->

<!-- Animal Tricks TYPE Abilities -->
<@compress single_line=true>
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","TYPE=AnimalTrick","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<b>Animal Tricks</b>${' '}${pcstring('ABILITYALLLIST.Special Ability.VISIBLE.TYPE=AnimalTrick')}<br><#t>
</#if>
</@compress>
<!-- End Animal Tricks TYPE Abilities -->

<#if (pcvar("COUNT[SA]") > 0) >
<b>Special Abilities </b> ${pcstring('SPECIALLIST')}<br>
</#if>

<!-- Afflictions Start -->
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Afflictions","TYPE=Affliction","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<b>Afflictions </b>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Afflictions","TYPE=Affliction","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; affliction , affliction_has_next>
${pcstring('ABILITYALL.Afflictions.VISIBLE.${affliction}.TYPE=Affliction')}
(${pcstring('ABILITYALL.Afflictions.VISIBLE.${affliction}.TYPE=Affliction.DESC')}),
</@loop>
<br>
</#if>
<!-- Afflictions End -->

<!-- Equipment -->
<#if (pcvar('COUNT[EQUIPMENT]') > 0)>
<p>
<@compress single_line=true><#-- TODO: Add <#t> at the end of each following line with output to ensure no extra spaces are output. -->
<!-- Combat Gear --><#t>
<#if (pcvar('var("COUNT[EQUIPMENT.IS.Consumable]")') > 0)>
<b>Combat Gear</b>${' '}<#t>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.IS.Consumable]-1') ; equip , equip_has_next> 
<#if (pcstring("EQTYPE.Consumable.${equip}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.EQTYPE.Consumable.${equip}.NAME')}</i><#t>
<#else>
${pcstring('TEXT.LOWER.EQTYPE.Consumable.${equip}.NAME')}<#t>
</#if>
<#if (pcvar('EQTYPE.Consumable.${equip}.QTY') > 1)>
${' '}(${pcstring('EQTYPE.Consumable.${equip}.QTY')})<#t>
</#if>
<#if (equip_has_next)>
,${' '}<#t>
</#if>
</@loop>
;${' '}<b>Other </b><#t>
</#if>
<b>Gear</b>${' '}<#t>
<!-- Armor -->
<#if (pcvar('COUNT[EQUIPMENT.IS.Armor]') > 0)>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.IS.Armor]-1') ; equip , equip_has_next>
<#if (pcstring("EQTYPE.Armor.${equip}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.EQTYPE.Armor.${equip}.NAME')}</i><#t>
<#else>
${pcstring('TEXT.LOWER.EQTYPE.Armor.${equip}.NAME')}<#t>
</#if>
<#if (equip_has_next)>
,${' '}<#t>
</#if>
</@loop>
<#if (pcvar('COUNT[EQTYPE.Weapon]') > 0 || pcvar('COUNT[EQUIPMENT.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]') > 0)>
,${' '}<#t>
</#if>
</#if>
<!-- Weapons -->
<#if (pcvar('var("COUNT[EQUIPMENT.IS.Weapon.NOT.Unarmed]")') > 0)>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.IS.Weapon.NOT.Unarmed]-1') ; equip , equip_has_next>
<#if (pcstring("EQTYPE.Weapon.NOT.Unarmed.${equip}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.EQ.IS.Weapon.NOT.Unarmed.${equip}.NAME')}</i><#t>
<#else>
${pcstring('TEXT.LOWER.EQ.IS.Weapon.NOT.Unarmed.${equip}.NAME')}<#t>
</#if>
<#if (pcvar('EQ.IS.Weapon.NOT.Unarmed.${equip}.QTY') > 1)>
${' '}(${pcstring('EQ.IS.Weapon.NOT.Unarmed.${equip}.QTY')})<#t>
</#if>
<#if (equip_has_next)>
,${' '}<#t>
</#if>
</@loop>
<#if (pcvar('COUNT[EQUIPMENT.NOT.Armor.NOT.Weapon.NOT.Consumable]') > 0)>
,${' '}<#t>
</#if>
</#if>
<!-- Magic Items -->
<#if (pcvar("COUNT[EQUIPMENT.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]") > 0)>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]-1') ; equip , equip_has_next> <#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("EQ.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.EQ.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.NAME')}</i><#t>
<#else>
${pcstring('TEXT.LOWER.EQ.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.NAME')}<#t>
</#if>
<#if (pcvar('EQ.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.QTY') > 1)>
${' '}(${pcstring('EQ.IS.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.QTY')})<#t>
</#if>
<#if (equip_has_next)>
,${' '}<#t>
</#if>
</@loop>
<#if (pcvar('COUNT[EQUIPMENT.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]') > 0)>
,${' '}<#t>
</#if>
</#if>
<!-- Remaining Equipment -->
<#if (pcvar('COUNT[EQUIPMENT.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]') > 0)>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable]-1') ; equip , equip_has_next> <#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("EQ.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.TYPE")?lower_case?contains("magic"))>
<i>${pcstring('TEXT.LOWER.EQ.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.NAME')}</i><#t>
<#else>
${pcstring('TEXT.LOWER.EQ.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.NAME')}<#t>
</#if>
<#if (pcvar('EQ.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.QTY') > 1)>
${' '}(${pcstring('EQ.NOT.Magic.NOT.Armor.NOT.Weapon.NOT.Consumable.${equip}.QTY')})<#t>
</#if>
<#if (equip_has_next)>
,${' '}<#t>
</#if>
</@loop>
</#if>
</@compress>
</p>
</#if>
<!-- Equipment End -->

<!-- SpellBooks -->
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
<#if (pcstring("SPELLBOOK.${spellbook}.TYPE") = "Spell Book")>
<br><b>${pcstring('SPELLBOOKNAME.${spellbook}')}</b>
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]') ; class , class_has_next> <#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("SPELLLISTMEMORIZE.${class}") = "false")>
<#else>
<@loop from=0 to=9 ; level , level_has_next>
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spelllevelcount , spelllevelcount_has_next>
<#if (spelllevelcount = 0)>
<!-- no memorized spells for SPELLSINBOOK.${class} ${spellbook} ${level} -->
<#else>
<br><@suffixnum num=level />
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-2') ; spell , spell_has_next>
<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</i>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "1")>
<#else>
(${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')})
</#if>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO")?contains("None"))>
<#else>
(DC ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})
</#if>
,
</@loop>
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}<i>${pcstring('TEXT.LOWER.SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</i>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "1")>
<#else>
(${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')})
</#if>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO")?contains("None"))>
<#else>
(DC ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})
</#if>
</@loop>
</#if>
</@loop>
</@loop>
</#if>
</@loop>
</#if>
</@loop>
<!-- End SpellBooks -->

<p></p>
<table class="section">
  <tr>
    <td class="section">SPECIAL ABILITIES</td>
  </tr>
</table>

<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; allAbilities , allAbilities_has_next>
<#assign typeOfAbility = pcstring("ABILITYALL.Special Ability.VISIBLE.${allAbilities}.TYPE")?lower_case />
<#if (typeOfAbility = "Implicit")>
<#else>
<p><b>${pcstring('ABILITYALL.Special Ability.VISIBLE.${allAbilities}')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
</b> ${pcstring('TEXT.REPLACEALL{<p>,<p class="sa">}.TEXT.REPLACEFIRST{<p>,}.ABILITYALL.Special Ability.VISIBLE.${allAbilities}.DESC')}</p>
</#if>
</@loop>

<!-- Start of Temporary Bonuses Added -->
<p></p>
<#if (pcvar("COUNT[TEMPBONUSNAMES]") > 0) >
<table class="section">
  <tr>
    <td class="section">TEMPORARY BONUSES</td>
  </tr>
</table>
<b>Temporary Bonuses Applied</b>
<@loop from=0 to=pcvar('COUNT[TEMPBONUSNAMES]-1') ; temp , temp_has_next>
${pcstring('TEMPBONUS.${temp}')},
</@loop>
<br>
</#if>
<!-- End of Temporary Bonuses Added -->

<!-- ================================================================= -->
