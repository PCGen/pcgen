<?xml version="1.0" encoding="iso-8859-1"?>
<#include "common/PF-FG.ftl" />
<root version="3.3" release="1|3.5E:17|CoreRPG:3" pcgenversion="${pcstring('EXPORT.VERSION')}">
	<character>
		<abilities>
			<charisma>
				<bonus type="number">${pcstring('STAT.5.MOD.INTVAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.5')}</score>
			</charisma>
			<constitution>
				<bonus type="number">${pcstring('STAT.2.MOD.INTVAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.2')}</score>
			</constitution>
			<dexterity>
				<bonus type="number">${pcstring('STAT.1.MOD.INTVAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.1')}</score>
			</dexterity>
			<intelligence>
				<bonus type="number">${pcstring('STAT.3.MOD.INTAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.3')}</score>
			</intelligence>
			<strength>
				<bonus type="number">${pcstring('STAT.0.MOD.INTVAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.0')}</score>
			</strength>
			<wisdom>
				<bonus type="number">${pcstring('STAT.4.MOD.INTVAL')}</bonus>
				<bonusmodifier type="number">0</bonusmodifier>
				<damage type="number">0</damage>
				<score type="number">${pcstring('STAT.4')}</score>
			</wisdom>
		</abilities>
		<ac>
			<sources>
				<abilitymod type="number">${pcstring('STAT.1.MOD.INTVAL')}</abilitymod>
				<abilitymod2 type="number">0</abilitymod2>
				<armor type="number">${pcstring('AC.Armor')}</armor>
				<cmdabilitymod type="number">${pcstring('STAT.0.MOD.INTVAL')}</cmdabilitymod>
				<cmdbasemod type="number">${pcstring('STAT.1.MOD.INTVAL')}</cmdbasemod>
				<cmdmisc type="number">0</cmdmisc>
				<deflection type="number">${pcstring('AC.Deflection')}</deflection>
				<dodge type="number">${pcstring('AC.Dodge')}</dodge>
				<ffmisc type="number">0</ffmisc>
				<misc type="number">${pcstring('AC.Misc')}</misc>
				<naturalarmor type="number">${pcstring('AC.NaturalArmor')}</naturalarmor>
				<shield type="number">${pcstring('AC.Shield')}</shield>
				<size type="number">${pcstring('AC.Size')}</size>
				<temporary type="number">0</temporary>
				<touchmisc type="number">${pcstring('AC.TouchMisc')}</touchmisc>
			</sources>
			<totals>
				<cmd type="number">${pcstring('AC.CMD')}</cmd>
				<flatfooted type="number">${pcstring('AC.Flatfooted')}</flatfooted>
				<general type="number">${pcstring('AC.Total')}</general>
				<touch type="number">${pcstring('AC.Touch')}</touch>
			</totals>
		</ac>
		<activeskillset type="number">1</activeskillset>
		<age type="string">${pcstring('AGE')}</age>
		<alignment type="string">${pcstring('ALIGNMENT')}</alignment>
		<appearance type="string">${pcstring('DESC')?replace("<para>", "")?replace("</para>", " ")?keep_before_last(" ")}</appearance>
		<attackbonus>
			<base type="number">${pcstring('ATTACK.MELEE')}</base>
			<grapple>
				<misc type="number">0</misc>
				<size type="number">${pcstring('VAR.CM_SizeMod.INTVAL.SIGN')}</size>
				<temporary type="number">0</temporary>
				<total type="number">${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')}</total>
			</grapple>
			<melee>
				<misc type="number">${pcstring('ATTACK.MELEE.MISC')}</misc>
				<size type="number">${pcstring('ATTACK.MELEE.SIZE')}</size>
				<temporary type="number">0</temporary>
				<size type="number">${pcstring('ATTACK.MELEE.SIZE')}</size>
			</melee>
			<ranged>
				<misc type="number">${pcstring('ATTACK.RANGED.MISC')}</misc>
				<size type="number">${pcstring('ATTACK.RANGED.SIZE')}</size>
				<temporary type="number">0</temporary>
				<size type="number">${pcstring('ATTACK.RANGED.SIZE')}</size>
			</ranged>
		</attackbonus>
		<castingmode type="number">0</castingmode>
		<classes>
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
	<#if (pcvar(pcstring('CLASS.${class}.LEVEL')) > 0)>
			<id-${(class+1)?left_pad(5, "0")}>
				<level type="number">${pcstring('CLASS.${class}.LEVEL')}</level>
				<name type="string">${pcstring('CLASS.${class}.NAME')}<#if (pcvar('count("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)><#assign classname=pcstring('CLASS.${class}.NAME')/> (${pcstring('ABILITYLIST.Archetype.TYPE=${classname}Archetype')})</#if></name>
			</id-${(class+1)?left_pad(5, "0")}>
	</#if>
</@loop>
		</classes>
		<coinother type="string">
<#if (pcvar(pcstring('GOLD.TRUNC')) > 0)>
			Unspent Funds = ${pcstring('GOLD')}\n
</#if>
<@loop from=0 to=pcvar('COUNT[EQTYPE.Coin]')-1 ; count , count_has_next>
	<#if !pcstring('EQTYPE.Coin.${count}.NAME')?contains("Coin")>
			${pcstring('EQTYPE.Coin.${count}.NAME')}: ${pcstring('EQTYPE.Coin.${count}.QTY')}<#if count_has_next>\n</#if>
	</#if>
</@loop>
<@loop from=0 to=pcvar('COUNT[EQTYPE.Gem]')-1 ; count , count_has_next>
			${pcstring('EQTYPE.Gem.${count}.QTY')}x ${pcstring('EQTYPE.Gem.${count}.NAME')}: ${pcstring('EQTYPE.Gem.${count}.COST')}<#if count_has_next>\n</#if>
</@loop>
		</coinother>
		<coins>
			<#if pcvar('COUNT[EQTYPE.Coin]') gt 0>
			<#assign has_pp=0 has_gp=0 has_sp=0 has_cp=0>
			<@loop from=0 to=pcvar('COUNT[EQTYPE.Coin]')-1; count, count_has_next>
				<#if (pcstring('EQTYPE.Coin.${count}.NAME') == "Coin (Platinum Piece)") && (pcvar('EQTYPE.Coin.${count}.QTY') gte 1)><#assign has_pp=count+1></#if>
				<#if (pcstring('EQTYPE.Coin.${count}.NAME') == "Coin (Gold Piece)") && (pcvar('EQTYPE.Coin.${count}.QTY') gte 1)><#assign has_gp=count+1></#if>
				<#if (pcstring('EQTYPE.Coin.${count}.NAME') == "Coin (Silver Piece)") && (pcvar('EQTYPE.Coin.${count}.QTY') gte 1)><#assign has_sp=count+1></#if>
				<#if (pcstring('EQTYPE.Coin.${count}.NAME') == "Coin (Copper Piece)") && (pcvar('EQTYPE.Coin.${count}.QTY') gte 1)><#assign has_cp=count+1></#if>
			</@loop>
			<slot1>
				<#if has_pp gt 0>
				<amount type="number">${pcstring('EQTYPE.Coin.${has_pp-1}.QTY')}</amount>
				<#else>
				<amount type="number">0</amount>
				</#if>
				<name type="string">PP</name>
			</slot1>
			<slot2>
				<#if has_gp gt 0>
				<amount type="number">${pcstring('EQTYPE.Coin.${has_gp-1}.QTY')}</amount>
				<#else>
				<amount type="number">0</amount>
				</#if>
				<name type="string">GP</name>
			</slot2>
			<slot3>
				<#if has_sp gt 0>
				<amount type="number">${pcstring('EQTYPE.Coin.${has_sp-1}.QTY')}</amount>
				<#else>
				<amount type="number">0</amount>
				</#if>
				<name type="string">SP</name>
			</slot3>
			<slot4>
				<#if has_cp gt 0>
				<amount type="number">${pcstring('EQTYPE.Coin.${has_cp-1}.QTY')}</amount>
				<#else>
				<amount type="number">0</amount>
				</#if>
				<name type="string">CP</name>
			</slot4>
			<#else>
			<slot1>
				<amount type="number">0</amount>
				<name type="string">PP</name>
			</slot1>
			<slot2>
				<amount type="number">0</amount>
				<name type="string">GP</name>
			</slot2>
			<slot3>
				<amount type="number">0</amount>
				<name type="string">SP</name>
			</slot3>
			<slot4>
				<amount type="number">0</amount>
				<name type="string">CP</name>
			</slot4>
			</#if>
		</coins>
		<defenses>
			<damagereduction type="string">${pcstring('DR')}</damagereduction>
			<sr>
				<base type="number">${pcstring('SR')}</base>
				<misc type="number"/>
				<temporary type="number"/>
				<total type="number">${pcstring('SR')}</total>
			</sr>
		</defenses>
		<deity type="string">${pcstring('DEITY')}</deity>
		<encumbrance>
			<armorcheckpenalty type="number">${pcstring('ACCHECK')}</armorcheckpenalty>
			<armormaxstatbonus type="number">${pcstring('MAXDEX')}</armormaxstatbonus>
		<#if pcvar('ACCHECK') != 0 || pcvar('MAXDEX') != 0 || pcvar('SPELLFAILURE') != 0>
			<armormaxstatbonusactive type="number">1</armormaxstatbonusactive>
		<#else>
			<armormaxstatbonusactive type="number">0</armormaxstatbonusactive>
		</#if>
			<carrymult type="number">${1+getLegsMult(pcvar("VAR.LEGS") pcstring("SIZE"))}</carrymult>
			<heavyload type="number">${pcstring('WEIGHT.HEAVY')}</heavyload>
			<liftoffground type="number">${pcstring('WEIGHT.OFFGROUND')}</liftoffground>
			<liftoverhead type="number">${pcstring('WEIGHT.OVERHEAD')}</liftoverhead>
			<lightload type="number">${pcstring('WEIGHT.LIGHT')}</lightload>
			<load type="number">${pcstring('TOTAL.WEIGHT')}</load>
			<mediumload type="number">${pcstring('WEIGHT.MEDIUM')}</mediumload>
			<pushordrag type="number">${pcstring('WEIGHT.PUSHDRAG')}</pushordrag>
			<spellfailure type="number">${pcstring('SPELLFAILURE')}</spellfailure>
			<stradj type="number">${pcvar("VAR.LOADSCORE-VAR.STRSCORE")}</stradj>
		</encumbrance>
		<exp type="number">${pcstring('EXP.CURRENT')}</exp>
		<expneeded type="number">${pcstring('EXP.NEXT')}</expneeded>
		<featlist>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=FEAT","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; feat , feat_has_next>
			<id-${(feat+1)?left_pad(5, "0")}>
				<name type="string">${pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')}</name>
				<description type="formattedtext">${pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.DESC')}</description>
				<sourcebook type="string">${sourceNameReplace(pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.SOURCE'))}</sourcebook>
				<benefit type="formattedtext"><p>${pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.BENEFIT')}</p></benefit>
	<#if (pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.TYPE')?lower_case?contains("combat"))>
				<type type="string">Combat</type>
	<#elseif (pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.TYPE')?lower_case?contains("metamagic"))>
				<type type="string">Metamagic</type>
	<#elseif (pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.TYPE')?lower_case?contains("mythic"))>
				<type type="string">Mythic</type>
	<#else>
				<type type="string">General</type>
	</#if>
				<normal type="formattedtext"><#list pc.abilities as cnas><#if cnas.pool == "Feat"><#if cnas.ability == pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')>${cnas.ability.info.Normal}</#if></#if></#list></normal>
				<special type="formattedtext"><#list pc.abilities as cnas><#if cnas.pool == "Feat"><#if cnas.ability == pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')>${cnas.ability.info.Special}</#if></#if></#list></special>
				<locked type="number">1</locked>
			</id-${(feat+1)?left_pad(5, "0")}>
</@loop>
		</featlist>
<#--		<mythicfeatlist>
<#if (pcvar('countdistinct("ABILITIES","CATEGORY=Mythic Feat","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Mythic Feat","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; mythicFeat , mythicFeat_has_next>
			<id-${(mythicFeat+1)?left_pad(5, "0")}>
				<name type="string">${pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}')}</name>
				<description type="string">${pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.DESC')}</description>
				<sourcebook type="string">${sourceNameReplace(pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.SOURCE'))}</sourcebook>
				<benefit type="formattedtext"><p>${pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.BENEFIT')}</p></benefit>
	<#if (pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.TYPE')?lower_case?contains("general"))>
				<type type="string">General</type>
	<#elseif (pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.TYPE')?lower_case?contains("combat"))>
				<type type="string">Combat</type>
	<#elseif (pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.TYPE')?lower_case?contains("metamagic"))>
				<type type="string">Metamagic</type>
	<#elseif (pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}.TYPE')?lower_case?contains("mythic"))>
				<type type="string">Mythic</type>
	</#if>
				<normal type="formattedtext"><#list pc.abilities as cnas><#if cnas.pool == "Mythic Feat"><#if cnas.ability == pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}')>${cnas.ability.info.Normal}</#if></#if></#list></normal>
				<special type="formattedtext"><#list pc.abilities as cnas><#if cnas.pool == "Mythic Feat"><#if cnas.ability == pcstring('ABILITYALL.Mythic Feat.VISIBLE.${mythicFeat}')>${cnas.ability.info.Special}</#if></#if></#list></special>
				<locked type="number">1</locked>
			</id-${(mythicFeat+1)?left_pad(5, "0")}>
</@loop>
</#if>
		</mythicfeatlist>-->
		<gender type="string">${pcstring('GENDER')}</gender>
		<height type="string">${pcstring('HEIGHT')}</height>
		<hp>
			<nonlethal type="number">0</nonlethal>
			<surgesused type="number">0</surgesused>
			<temporary type="number">0</temporary>
			<total type="number">${pcstring('HP')}</total>
			<wounds type="number">0</wounds>
		</hp>
		<initiative>
			<abilitymod type="number">${pcstring('STAT.1.MOD')}</abilitymod>
			<misc type="number">${pcstring('INITIATIVEBONUS')}</misc>
			<temporary type="number">0</temporary>
			<total type="number">${pcstring('INITIATIVEMOD')}</total>
		</initiative>
		<inventorylist>
<#assign weaponLinkSeq = [{"Name":"Test","Type":"Test","ID":0}]/>
<@loop from=0 to=(pcvar("COUNT[EQUIPMENT.NOT.Coin.NOT.Gem.NOT.Temporary]")-1) ; equip, equip_has_next >
<#assign eqtype = pcstring("EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.TYPE")?lower_case />
			<id-${(equip+1)?left_pad(5, "0")}>
				<ac type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ACMOD')}</ac>
				<aura type="string">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QUALITY.Aura')}</aura>
				<bonus type="number">${plusBonus(eqtype)}</bonus>
				<carried type="number"><#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.LOCATION')?lower_case?contains("carried")>1<#elseif pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.LOCATION')?lower_case?contains("equipped")>2<#else>0</#if></carried>
				<checkpenalty type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ACCHECK')}</checkpenalty>
				<cl type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QUALITY.Caster Level')}</cl>
				<cost type="string">${itemValue(equip)}</cost>
				<count type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QTY')}</count>
				<critical type="string"><#if (pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CRITRANGE') > 1)>${21-pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CRITRANGE')}-20/</#if>${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CRITMULT')}<#if (pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ALTCRITMULT') != "")>/${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ALTCRITMULT')}</#if></critical>
				<damage type="string">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.DAMAGE')}<#if (pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ALTDAMAGE') != "")>/${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.ALTDAMAGE')}</#if></damage>
				<damagetype type="string">${damageType(eqtype)}</damagetype>
				<description type="formattedtext">
					<p>${stringCleanup(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.DESC'))}</p>
					<p>${stringCleanup(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.NOTE'))}</p>
				</description>
				<isidentified type="number">1</isidentified>
				<location type="string">${equipReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.LOCATION'))}</location>
				<locked type="number">1</locked>
				<maxstatbonus type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.MAXDEX')}</maxstatbonus>
				<name type="string">${equipName(equip)}</name>
				<nonid_name type="string"></nonid_name>
				<nonidentified type="string"></nonidentified>
				<prerequisites type="string">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QUALITY.Construction Requirements')}<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QUALITY.Construction Craft DC') != "">, Craft DC ${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.QUALITY.Construction Craft DC')}</#if></prerequisites>
				<properties type="string">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.SPROP')}</properties>
				<range type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.RANGE')}</range>
				<showonminisheet type="number">1</showonminisheet>
				<sourcebook type="string">${sourceNameReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.SOURCE'))}</sourcebook>
				<speed20 type="number"><#if eqtype?contains("heavy") || eqtype?contains("medium") && eqtype?contains("armor")>15<#else>20</#if></speed20>
				<speed30 type="number"><#if eqtype?contains("heavy") || eqtype?contains("medium") && eqtype?contains("armor")>20<#else>30</#if></speed30>
				<spellfailure type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.SPELLFAILURE')}</spellfailure>
				<type type="string">${itemType(eqtype "type")}</type>
				<subtype type="string">${itemType(eqtype "subtype")}</subtype>
				<weight type="number">${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.WT')}</weight>
			</id-${(equip+1)?left_pad(5, "0")}>
<#if eqtype?contains("weapon")>
	<#assign weaponLinkSeq += [{"Name":equipName(equip),"Type":damageType(eqtype),"ID":(equip+1)}]/>
</#if>
</@loop>
		</inventorylist>
		<languagelist>
<@loop from=0 to=(pcvar("COUNT[LANGUAGES]")-1) ; lang, lang_has_next >
			<id-${(lang+1)?left_pad(5, "0")}>
				<name type="string">${pcstring('LANGUAGES.${lang}')}</name>
			</id-${(lang+1)?left_pad(5, "0")}>
</@loop>
		</languagelist>
		<level type="number">${pcstring('TOTALLEVELS')}</level>
		<name type="string">${pcstring('NAME')}<#if (pcstring("FOLLOWEROF") != "") > - ${pcstring('FOLLOWEROF')}</#if></name>
		<notes type="string"><#if (pcstring("BIO") != "" && pcstring("BIO") != "None")>${pcstring('BIO')?replace("<para>", "")?replace("</para>", "\\n")?keep_before_last(" ")}\n</#if><#if (pcvar("COUNT[NOTES]") > 0) ><@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note , note_has_next>${pcstring('NOTE.${note}.NAME')?replace("<para>", "")?replace("</para>", "\\n")?keep_before_last(" ")} - ${pcstring('NOTE.${note}.VALUE')?replace("<para>", "")?replace("</para>", "\\n")?keep_before_last(" ")}\n</@loop></#if></notes>
		<proficiencylist>
			<id-00001>
				<locked type="number">1</locked>
				<name type="string">Armor: ${getArmorProfs()}</name>
			</id-00001>
			<id-00002>
				<locked type="number">1</locked>
				<name type="string">Weapon: ${getWeaponProfs()}</name>
				<text type="formattedtext">
				<#list pcstring('WEAPONPROFS')?split(", ") as prof>
					<p>${prof}</p>
				</#list>
				</text>
			</id-00002>
		</proficiencylist>
		<race type="string"><@loop from=0 to=pcvar('COUNT[TEMPLATES]-1') ; template , template_has_next>${pcstring('TEMPLATE.${template}.APPLIEDNAME')} </@loop><#if (pcstring("ABILITYALL.ANY.0.TYPE=RaceName.HASASPECT.RaceName") = "Y")>${pcstring('ABILITYALL.ANY.0.ASPECT=RaceName.ASPECT.RaceName')}<#else>${pcstring('RACE')}</#if></race>
		<saves>
			<fortitude>
				<abilitymod type="number">${pcstring('CHECK.FORTITUDE.STATMOD')}</abilitymod>
				<base type="number">${pcstring('CHECK.FORTITUDE.BASE')}</base>
				<misc type="number">${pcstring('CHECK.FORTITUDE.MISC.NOSTAT')}</misc>
				<temporary type="number">0</temporary>
				<total type="number">${pcstring('CHECK.FORTITUDE.TOTAL')}</total>
			</fortitude>
			<reflex>
				<abilitymod type="number">${pcstring('CHECK.REFLEX.STATMOD')}</abilitymod>
				<base type="number">${pcstring('CHECK.REFLEX.BASE')}</base>
				<misc type="number">${pcstring('CHECK.REFLEX.MISC.NOSTAT')}</misc>
				<temporary type="number">0</temporary>
				<total type="number">${pcstring('CHECK.REFLEX.TOTAL')}</total>
			</reflex>
			<will>
				<abilitymod type="number">${pcstring('CHECK.WILL.STATMOD')}</abilitymod>
				<base type="number">${pcstring('CHECK.WILL.BASE')}</base>
				<misc type="number">${pcstring('CHECK.WILL.MISC.NOSTAT')}</misc>
				<temporary type="number">0</temporary>
				<total type="number">${pcstring('CHECK.WILL.TOTAL')}</total>
			</will>
		</saves>
		<senses type="string"><#if (pcvar("COUNT[VISION]") > 0) >${pcstring('VISION')}<#else>Normal</#if></senses>
		<size type="string">${pcstring('SIZELONG')}</size>
		<skilllist>
<@loop from=0 to=pcvar('count("SKILLSIT", "VIEW=VISIBLE_EXPORT")')-1; skill , skill_has_next >
			<id-${(skill+1)?left_pad(5, "0")}>
<#assign skillname = pcstring('SKILLSIT.${skill}.NAME')?lower_case>
<#if skillname?contains('acrobatics') || skillname?contains('climb') || skillname?contains('disable device') || skillname?contains('escape artist') || skillname?contains('fly') || skillname?contains('ride') || skillname?contains('sleight of hand') || skillname?contains('stealth') || skillname?contains('swim')>
	<#assign acCheckSkill = pcvar('ACCHECK')>
<#else>
	<#assign acCheckSkill = 0>
</#if>
<#if acCheckSkill != 0>
				<armorcheckmultiplier type="number">1</armorcheckmultiplier>
<#else>
				<armorcheckmultiplier type="number">0</armorcheckmultiplier>
</#if>
<#if pcstring('SKILLSIT.${skill}')?contains("Craft")>
				<label type="string">Craft</label>
				<sublabel type="string">${pcstring('SKILLSIT.${skill}')?remove_beginning("Craft (")?remove_ending(")")}</sublabel>
<#elseif pcstring('SKILLSIT.${skill}')?contains("Knowledge")>
				<label type="string">Knowledge</label>
				<sublabel type="string">${pcstring('SKILLSIT.${skill}')?remove_beginning("Knowledge (")?remove_ending(")")}</sublabel>
<#elseif pcstring('SKILLSIT.${skill}')?contains("Perform")>
				<label type="string">Perform</label>
				<sublabel type="string">${pcstring('SKILLSIT.${skill}')?remove_beginning("Perform (")?remove_ending(")")}</sublabel>
<#elseif pcstring('SKILLSIT.${skill}')?contains("Profession")>
				<label type="string">Profession</label>
				<sublabel type="string">${pcstring('SKILLSIT.${skill}')?remove_beginning("Profession (")?remove_ending(")")}</sublabel>
<#elseif pcstring('SKILLSIT.${skill}')?contains("Linguistics")>
				<label type="string">Linguistics</label>
<#else>
				<label type="string">${pcstring('SKILLSIT.${skill}')}</label>
</#if>
				<halfranks type="number">0</halfranks>
<#if (pcstring('SKILLSIT.${skill}.EXPLAIN_LONG')?contains("cskill"))>
				<misc type="number">${pcvar('SKILLSIT.${skill}.MISC-3')-acCheckSkill}</misc>
<#else>
				<misc type="number">${pcvar('SKILLSIT.${skill}.MISC')-acCheckSkill}</misc>
</#if>
				<plannedhalfranks type="number">0</plannedhalfranks>
				<plannedranks type="number">0</plannedranks>
				<ranks type="number">${pcstring('SKILLSIT.${skill}.RANK')?replace("\\.0", "", "rf")}</ranks>
				<stat type="number">${pcstring('SKILLSIT.${skill}.ABMOD')}</stat>
<#if (pcstring('SKILLSIT.${skill}.EXPLAIN_LONG')?contains("cskill"))>
				<state type="number">1</state>
<#else>
				<state type="number">0</state>
</#if>
<#if (pcstring('SKILLSIT.${skill}.ABILITY')) = "STR">
				<statname type="string">strength</statname>
<#elseif (pcstring('SKILLSIT.${skill}.ABILITY')) = "DEX">
				<statname type="string">dexterity</statname>
<#elseif (pcstring('SKILLSIT.${skill}.ABILITY')) = "CON">
				<statname type="string">constitution</statname>
<#elseif (pcstring('SKILLSIT.${skill}.ABILITY')) = "INT">
				<statname type="string">intelligence</statname>
<#elseif (pcstring('SKILLSIT.${skill}.ABILITY')) = "WIS">
				<statname type="string">wisdom</statname>
<#elseif (pcstring('SKILLSIT.${skill}.ABILITY')) = "CHA">
				<statname type="string">charisma</statname>
</#if>
				<description type="string">${pcstring('SKILLSIT.${skill}.EXPLAIN_LONG')}</description>
				<total type="number">${pcstring('SKILLSIT.${skill}.TOTAL')}</total>
			</id-${(skill+1)?left_pad(5, "0")}>
</@loop>
		</skilllist>
		<skillpoints>
			<unspent type="number">${pcstring('SKILLPOINTS.UNUSED')}</unspent>
		</skillpoints>
		<specialabilitylist fgapi="CC.PG.SPECIALABILITYLIST(classes=classes;name=name)">
<#--<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
	<#if (pcvar(pcstring('CLASS.${class}.LEVEL')) > 0)>
		<#assign classTempSpaces = pcstring('CLASS.${class}.NAME')/>
		<#assign classTemp = pcstring('CLASS.${class}.NAME')?replace(" ","")/>
		<#if class = 0>
			<#assign classNameSpaces = ["TYPE=${classTempSpaces} Class Feature"]/>
			<#assign className = ["TYPE=${classTemp}ClassFeatures"]/>
			<#assign classNameSpacesExclude = ["EXCLUDETYPE=${classTempSpaces} Class Feature"]/>
			<#assign classNameExclude = ["EXCLUDETYPE=${classTemp}ClassFeatures"]/>
		<#else>
			<#assign classNameSpaces += ["TYPE=${classTempSpaces} Class Feature"]/>
			<#assign className += ["TYPE=${classTemp}ClassFeatures"]/>
			<#assign classNameSpacesExclude += ["EXCLUDETYPE=${classTempSpaces} Class Feature"]/>
			<#assign classNameExclude += ["EXCLUDETYPE=${classTemp}ClassFeatures"]/>
		</#if>
	</#if>
</@loop>
<#assign typeCountSpaces>${classNameSpaces?join("[or]")}</#assign>
<#assign typeCount>${className?join("[or]")}</#assign>
<#assign typeListSpaces>${classNameSpaces?join(".")}</#assign>
<#assign typeList>${className?join(".")}</#assign>
<#assign typeCountSpacesExclude>${classNameSpacesExclude?join("[and]")}</#assign>
<#assign typeCountExclude>${classNameExclude?join("[and]")}</#assign>
<#assign typeListSpacesExclude>${classNameSpacesExclude?join(".")}</#assign>
<#assign typeListExclude>${classNameExclude?join(".")}</#assign>
<@abilityOutput count="${typeCountSpaces}[or]${typeCount}" typeList=".${typeListSpaces}.${typeList}"/>-->
<@abilityOutput count="TYPE=SpecialQuality[or]TYPE=SpecialAttack[or]TYPE=Trait[or]TYPE=Drawback" countExclude="EXCLUDETYPE=RacialTraits" typeList=".TYPE=SpecialQuality.TYPE=SpecialAttack.TYPE=Trait.TYPE=Drawback" typeExclude=".EXCLUDETYPE=RacialTraits"/>
		</specialabilitylist>
<#--		<traitabilitylist>
<@abilityOutput count="TYPE=Trait[or]TYPE=Drawback" typeList=".TYPE=Trait.TYPE=Drawback"/>
		</traitabilitylist>
		<mythicpathlist>
<@abilityOutput count="TYPE=Mythic Path[or]TYPE=Dual Path" typeList=".TYPE=Mythic Path.TYPE=Dual Path"/>
		</mythicpathlist>
		<mythictier>
			<tier type="number">${pcvar('VAR.MythicTierLevel.INTVAL')}</tier>
		</mythictier>
		<mythicpathfeaturelist>
<@abilityOutput count="TYPE=Mythic Path Feature" typeList=".TYPE=Mythic Path Feature"/>
		</mythicpathfeaturelist>
		<mythicabilitylist>
<@abilityOutput count="TYPE=Mythic Ability" typeList=".TYPE=Mythic Ability"/>
		</mythicabilitylist>
		<mythicpathabilitylist>
<@abilityOutput count="TYPE=Mythic Path Ability" typeList=".TYPE=Mythic Path Ability"/>
		</mythicpathabilitylist>
		<specialqualitylist>
<@abilityOutput count="TYPE=SpecialQuality[or]TYPE=SpecialAttack" countExclude="${typeCountSpacesExclude}[and]${typeCountExclude}[and]EXCLUDETYPE=RacialTraits[and]EXCLUDETYPE=Trait[and]EXCLUDETYPE=Drawback[and]EXCLUDETYPE=Mythic Path[and]EXCLUDETYPE=Mythic Path Feature[and]EXCLUDETYPE=Mythic Ability[and]EXCLUDETYPE=Mythic Path Ability" typeList=".TYPE=SpecialQuality.TYPE=SpecialAttack" typeExclude=".${typeListSpacesExclude}.${typeListExclude}.EXCLUDETYPE=RacialTraits.EXCLUDETYPE=Trait.EXCLUDETYPE=Drawback.EXCLUDETYPE=Mythic Path.EXCLUDETYPE=Mythic Path Feature.EXCLUDETYPE=Mythic Ability.EXCLUDETYPE=Mythic Path Ability"/>
		</specialqualitylist>-->
		<speed>
			<armor type="number">0</armor>
			<base type="number">${pcstring('MOVE.0.RATE.INTVAL')}</base>
			<final type="number">${pcstring('MOVE.0.RATE.INTVAL')}</final>
			<misc type="number">0</misc>
			<special type="string"><@loop from=1 to=pcvar('COUNT[MOVE]-1') ; movement , movement_has_next>${pcstring('MOVE.${movement}.NAME')} ${pcstring('MOVE.${movement}.RATE')}<#if (pcstring("MOVE.${movement}.NAME") = "Fly")> (${pcstring('ABILITYALL.Special Ability.HIDDEN.0.TYPE=Maneuverability.ASPECT.Maneuverability')})</#if><#if movement_has_next>, </#if></@loop></special>
			<temporary type="number">0</temporary>
			<total type="number">${pcstring('MOVE.0.RATE.INTVAL')}</total>
		</speed>
		<spellset>
<@loop from=0 to=pcvar('COUNT[CLASSES]') ; spellclassno , spellclassno_has_next>
<#if (pcvar('SPELLLISTCLASS.${spellclassno}.LEVEL') > 0)>
			<id-${(spellclassno)?left_pad(5, "0")}>
				<cl type="number">${pcstring('SPELLLISTCLASS.${spellclassno}.CASTERLEVEL')}</cl>
				<dc>
	<#if (pcstring('SPELLLISTDCSTAT.${spellclassno}.0') = "INT")>
					<ability type="string">intelligence</ability>
	<#elseif (pcstring('SPELLLISTDCSTAT.${spellclassno}.0') = "WIS")>
					<ability type="string">wisdom</ability>
	<#elseif (pcstring('SPELLLISTDCSTAT.${spellclassno}.0') = "CHA")>
					<ability type="string">charisma</ability>
	<#else>
					<ability type="string">${pcstring('SPELLLISTDCSTAT.${spellclassno}.0')}</ability>
	</#if>
					<abilitymod type="number">0</abilitymod>
					<misc type="number">0</misc>
					<total type="number">0</total>
				</dc>
	<#if (pcvar('SPELLLISTCLASS.${spellclassno}.LEVEL') > 0)>
				<label type="string">${pcstring('SPELLLISTCLASS.${spellclassno}')}</label>
	<#if (pcboolean('SPELLLISTMEMORIZE.${spellclassno}'))>
				<spontaneous type="number">0</spontaneous>
	<#else>
				<spontaneous type="number">1</spontaneous>
	</#if>
	<@loop from=0 to=pcvar('MAXSPELLLEVEL.${spellclassno}') ; spellavailablelevel , spellavailablelevel_has_next>
				<availablelevel${spellavailablelevel} type="number">${pcstring('((SPELLLISTCAST.${spellclassno}.${spellavailablelevel})+0).INTVAL')}</availablelevel${spellavailablelevel}>
	</@loop>
				<levels>
	<@loop from=0 to=pcvar('MAXSPELLLEVEL.${spellclassno}') ; spellknownlevel , spellknownlevel_has_next>
					<level${spellknownlevel}>
						<dc type="number">${pcstring('SPELLLISTDC.${spellclassno}.${spellknownlevel}')}</dc>
						<dcstat type="string">${pcstring('SPELLLISTDCSTAT.${spellclassno}.${spellknownlevel}')}</dcstat>
						<spells>
		<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${spellclassno}.0.${spellknownlevel}]-1') ; spell , spell_has_next>
							<id-${(spell+1)?left_pad(5, "0")}>
								<cast type="number">0</cast>
								<castingtime type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.CASTINGTIME')}</castingtime>
								<components type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.COMPONENTS')}</components>
								<description type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.DESC')?replace("<para>", "")?replace("</para>", " ")}</description>
								<duration type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.DURATION')}</duration>
								<effect type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.TARGET')}</effect>
								<level type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SOURCELEVEL')}</level>
								<name type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.NAME')}<#if (pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.BONUSSPELL') = "*")> [*]</#if></name>
								<parse type="number">1</parse>
								<range type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.RANGE')}</range>
								<save type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SAVEINFO')}</save>
								<school type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SCHOOL')}:${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SUBSCHOOL')}:${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.DESCRIPTOR')}</school>
								<shortdescription type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.DESCRIPTION')?replace("<para>", "")?replace("</para>", " ")}</shortdescription>
								<sourcebook type="string">${sourceNameReplace(pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SOURCE'))}</sourcebook>
								<sr type="string">${pcstring('SPELLMEM.${spellclassno}.0.${spellknownlevel}.${spell}.SR')}</sr>
							</id-${(spell+1)?left_pad(5, "0")}>
		</@loop>
						</spells>
					</level${spellknownlevel}>
	</@loop>
				</levels>
	</#if>
			</id-${(spellclassno)?left_pad(5, "0")}>
</#if>
</@loop>
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; classtype , classtype_has_next>
<#if (pcstring('CLASS.${classtype}') = "Cleric")>
          <id-00099>
          <availablelevel0 type="number">0</availablelevel0>
          <availablelevel1 type="number">${pcstring('(STAT.5.MOD+3).INTVAL')}</availablelevel1>
          <castertype type="string">spontaneous</castertype>
          <cc>
            <misc type="number">0</misc>
          </cc>
            <cl type="number">${pcstring('CLASS.${classtype}.LEVEL')}</cl>
          <dc>
            <ability type="string">wisdom</ability>
            <abilitymod type="number">0</abilitymod>
            <misc type="number">0</misc>
            <total type="number">0</total>
          </dc>
          <label type="string">Cleric Burst</label>
          <levels>
            <level0>
              <level type="number">0</level>
              <maxprepared type="number">0</maxprepared>
              <spells>
              </spells>
              <totalcast type="number">0</totalcast>
              <totalprepared type="number">0</totalprepared>
            </level0>           <level1>
              <level type="number">1</level>
              <maxprepared type="number">0</maxprepared>
              <spells>
                <id-00001>
                  <actions>
                    <id-00001>
                      <atkmod type="number">0</atkmod>
                      <clcbase type="number">${pcstring('CLASS.${classtype}.LEVEL')}</clcbase>
                      <clcmod type="number">0</clcmod>
                      <dmaxstat type="number">0</dmaxstat>
                      <dmgdice type="dice"></dmgdice>
                      <dmgdicemultmax type="number">0</dmgdicemultmax>
                      <dmgmaxstat type="number">0</dmgmaxstat>
                      <dmgmod type="number">0</dmgmod>
                      <dmgnotspell type="number">0</dmgnotspell>
                      <dmgstatmult type="number">1</dmgstatmult>
                      <durdice type="dice"></durdice>
                      <durmod type="number">0</durmod>
                      <durmult type="number">0</durmult>
                      <hdice type="dice">d6</hdice>
                      <hdicemult type="string">oddcl</hdicemult>
                      <hdicemultmax type="number">0</hdicemultmax>
                      <hmaxstat type="number">0</hmaxstat>
                      <hmod type="number">0</hmod>
                      <hstatmult type="number">1</hstatmult>
                      <savedcbase type="number">12</savedcbase>
                      <savedcmod type="number">0</savedcmod>
                      <srnotallowed type="number">0</srnotallowed>
                      <type type="string">heal</type>
                    </id-00001>
                  </actions>
                  <cast type="number">0</cast>
                  <cost type="number">0</cost>
                  <name type="string">Healing Burst</name>
                  <prepared type="number">0</prepared>
                </id-00001>
                <id-00002>
                  <actions>
                    <id-00001>
                      <atkmod type="number">0</atkmod>
                      <clcbase type="number">${pcstring('CLASS.${classtype}.LEVEL')}</clcbase>
                      <clcmod type="number">0</clcmod>
                      <dmaxstat type="number">0</dmaxstat>
                      <dmgdice type="dice"></dmgdice>
                      <dmgdicemultmax type="number">0</dmgdicemultmax>
                      <dmgmaxstat type="number">0</dmgmaxstat>
                      <dmgmod type="number">0</dmgmod>
                      <dmgnotspell type="number">0</dmgnotspell>
                      <dmgstatmult type="number">1</dmgstatmult>
                      <durdice type="dice"></durdice>
                      <durmod type="number">0</durmod>
                      <durmult type="number">0</durmult>
                      <hdice type="dice"></hdice>
                      <hdicemultmax type="number">0</hdicemultmax>
                      <hmaxstat type="number">0</hmaxstat>
                      <hmod type="number">0</hmod>
                      <hstatmult type="number">1</hstatmult>
                      <savedcbase type="number">${pcstring('(CLASS.${classtype}.LEVEL/2+STAT.5.MOD+10).INTVAL')}</savedcbase>
                      <savedcmod type="number">0</savedcmod>
                      <savetype type="string">will</savetype>
                      <srnotallowed type="number">1</srnotallowed>
                      <type type="string">cast</type>
                    </id-00001>
                    <id-00002>
                      <atkmod type="number">0</atkmod>
                      <clcbase type="number">${pcstring('CLASS.${classtype}.LEVEL')}</clcbase>
                      <clcmod type="number">0</clcmod>
                      <dmaxstat type="number">0</dmaxstat>
                      <dmgdice type="dice">d6</dmgdice>
                      <dmgdicemult type="string">oddcl</dmgdicemult>
                      <dmgdicemultmax type="number">0</dmgdicemultmax>
                      <dmgmaxstat type="number">0</dmgmaxstat>
                      <dmgmod type="number">0</dmgmod>
                      <dmgnotspell type="number">0</dmgnotspell>
                      <dmgstatmult type="number">1</dmgstatmult>
                      <durdice type="dice"></durdice>
                      <durmod type="number">0</durmod>
                      <durmult type="number">0</durmult>
                      <hdice type="dice"></hdice>
                      <hdicemultmax type="number">0</hdicemultmax>
                      <hmaxstat type="number">0</hmaxstat>
                      <hmod type="number">0</hmod>
                      <hstatmult type="number">1</hstatmult>
                      <savedcbase type="number">${pcstring('(CLASS.${classtype}.LEVEL/2+STAT.5.MOD+10).INTVAL')}</savedcbase>
                      <savedcmod type="number">0</savedcmod>
                      <srnotallowed type="number">0</srnotallowed>
                      <type type="string">damage</type>
                    </id-00002>
                  </actions>
                  <cast type="number">0</cast>
                  <cost type="number">0</cost>
                  <linkedspells>
                  </linkedspells>
                  <name type="string">Damage Burst</name>
                  <prepared type="number">0</prepared>
                </id-00002>
              </spells>
              <totalcast type="number">0</totalcast>
              <totalprepared type="number">0</totalprepared>
            </level1>
          </levels>
          <points type="number">0</points>
          <pointsused type="number">0</pointsused>
          <sp type="number">0</sp>
        </id-00099>
</#if>
</@loop>
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; classtype , classtype_has_next>
<#if (pcstring('CLASS.${classtype}') = "Monk")>
          <id-00100>
          <availablelevel0 type="number">0</availablelevel0>
          <availablelevel1 type="number">${pcstring('CLASS.${classtype}.LEVEL')}</availablelevel1>
          <castertype type="string">spontaneous</castertype>
          <dc>
              <ability type="string">wisdom</ability>
              <abilitymod type="number">0</abilitymod>
              <misc type="number">0</misc>
              <total type="number">0</total>
          </dc>
          <label type="string">Stunning Fist </label>
          <levels>
            <level1>
              <level type="number">1</level>
              <maxprepared type="number">0</maxprepared>
              <spells>
                <id-00001>
                  <actions>
                    <id-00001>
                      <atkmod type="number">${pcstring('(ATTACK.MELEE.TOTAL.SHORT).INTVAL')}</atkmod>
                      <atktype type="string">melee</atktype>
                      <clcbase type="number">0</clcbase>
                      <clcmod type="number">0</clcmod>
                      <dmaxstat type="number">0</dmaxstat>
                      <dmgdice type="dice"></dmgdice>
                      <dmgdicemultmax type="number">0</dmgdicemultmax>
                      <dmgmaxstat type="number">0</dmgmaxstat>
                      <dmgmod type="number">0</dmgmod>
                      <dmgnotspell type="number">0</dmgnotspell>
                      <dmgstatmult type="number">1</dmgstatmult>
                      <durdice type="dice"></durdice>
                      <durmod type="number">0</durmod>
                      <durmult type="number">0</durmult>
                      <hdice type="dice"></hdice>
                      <hdicemultmax type="number">0</hdicemultmax>
                      <hmaxstat type="number">0</hmaxstat>
                      <hmod type="number">0</hmod>
                      <hstatmult type="number">1</hstatmult>
                      <savedcbase type="number">9</savedcbase>
                      <savedcmod type="number">${pcstring('(CLASS.${classtype}.LEVEL/2-1).INTVAL')}</savedcmod>
                      <savetype type="string">will</savetype>
                      <srnotallowed type="number">0</srnotallowed>
                      <type type="string">cast</type>
                    </id-00001>
                    <id-00003>
                      <atkmod type="number">0</atkmod>
                      <clcbase type="number">1</clcbase>
                      <clcmod type="number">0</clcmod>
                      <dmaxstat type="number">0</dmaxstat>
                      <dmgdice type="dice"></dmgdice>
                      <dmgdicemultmax type="number">0</dmgdicemultmax>
                      <dmgmaxstat type="number">0</dmgmaxstat>
                      <dmgmod type="number">0</dmgmod>
                      <dmgnotspell type="number">0</dmgnotspell>
                      <dmgstatmult type="number">1</dmgstatmult>
                      <durdice type="dice"></durdice>
                      <durmod type="number">0</durmod>
                      <durmult type="number">1</durmult>
                      <hdice type="dice"></hdice>
                      <hdicemultmax type="number">0</hdicemultmax>
                      <hmaxstat type="number">0</hmaxstat>
                      <hmod type="number">0</hmod>
                      <hstatmult type="number">1</hstatmult>
                      <label type="string">Stunned</label>
                      <savedcbase type="number">9</savedcbase>
                      <savedcmod type="number">0</savedcmod>
                      <srnotallowed type="number">0</srnotallowed>
                      <type type="string">effect</type>
                    </id-00003>
                  </actions>
                  <cast type="number">0</cast>
                  <cost type="number">1</cost>
                  <name type="string">Stunning Fist Attack</name>
                  <prepared type="number">0</prepared>
                </id-00001>
              </spells>
              <totalcast type="number">0</totalcast>
              <totalprepared type="number">0</totalprepared>
            </level1>
            </levels>
            </id-00100>
</#if>
</@loop>
		</spellset>
		<traitlist>
<#--<@abilityOutput count="TYPE=RacialTraits" typeList=".TYPE=RacialTraits" visibility="all"/>-->
<@abilityOutput count="TYPE=RacialTraits" typeList=".TYPE=RacialTraits" />
		</traitlist>
		<weaponlist>
<@loop from=0 to=pcvar('COUNT[EQTYPE.WEAPON]-1') ; weapno , weapno_has_next>
<#assign weaponHash = {"Name":"","Type":"","ID":""}/>
<@loop from=0 to=weaponLinkSeq?size-1; i, i_has_next>
	<#if weaponLinkSeq[i].Type?lower_case?contains("magic")>
		<#if weaponLinkSeq[i].Name == magicEquipReplace(pcstring('WEAPON.${weapno}.OUTPUTNAME')?remove_beginning("*")?keep_before(" [")?lower_case?cap_first)>
			<#assign weaponHash = {"Name":weaponLinkSeq[i].Name,"Type":weaponLinkSeq[i].Type,"ID":weaponLinkSeq[i].ID}/>
		</#if>
	<#else>
		<#if weaponLinkSeq[i].Name == equipReplace(pcstring('WEAPON.${weapno}.OUTPUTNAME')?remove_beginning("*")?keep_before(" [")?lower_case?cap_first)>
			<#assign weaponHash = {"Name":weaponLinkSeq[i].Name,"Type":weaponLinkSeq[i].Type,"ID":weaponLinkSeq[i].ID}/>
		</#if>
	</#if>
</@loop>
			<id-${(weapno+1)?left_pad(5, "0")}>
				<ammo type="number">0</ammo>
				<attack1 type="number">${pcstring('WEAPON.${weapno}.TOTALHIT.0')}</attack1>
				<attack1modifier type="number">0</attack1modifier>
				<attack2 type="number">${pcstring('WEAPON.${weapno}.TOTALHIT.1')}</attack2>
				<attack2modifier type="number">0</attack2modifier>
				<attack3 type="number">${pcstring('WEAPON.${weapno}.TOTALHIT.2')}</attack3>
				<attack3modifier type="number">0</attack3modifier>
				<attack4 type="number">${pcstring('WEAPON.${weapno}.TOTALHIT.3')}</attack4>
				<attack4modifier type="number">0</attack4modifier>
				<attacks type="number">${pcstring('WEAPON.${weapno}.NUMATTACKS')}</attacks>
				<critatkrange type="number">${pcstring('WEAPON.${weapno}.CRIT')?substring(0, 2)}</critatkrange>
				<damagelist>
					<id-00001>
						<bonus type="number">${pcstring('WEAPON.${weapno}.MAGICDAMAGE')}</bonus>
						<critmult type="number">${pcstring('WEAPON.${weapno}.MULT')}</critmult>
						<dice type="dice">${convertDice(pcstring('WEAPON.${weapno}.BASEDAMAGE'))}</dice>
<#if (pcstring('WEAPON.${weapno}.CATEGORY')?lower_case?contains("ranged"))>
						<stat type="string"></stat>
<#else>
						<stat type="string">strength</stat>
</#if>
						<statmax type="number">0</statmax>
						<statmult type="number">1</statmult>
						<type type="string"><#if weaponHash.Type != "">${weaponHash.Type}<#else>${weaponDamageType(pcstring('WEAPON.${weapno}.TYPE'))}</#if></type>
					</id-00001>
				</damagelist>
				<maxammo type="number">0</maxammo>
				<name type="string">${pcstring('WEAPON.${weapno}.OUTPUTNAME')?remove_beginning("*")?lower_case?cap_first}</name>
				<properties type="string">${pcstring('WEAPON.${weapno}.SPROP')}</properties>
<#if (pcstring('WEAPON.${weapno}.CATEGORY')?lower_case?contains("ranged"))>
				<type type="number">1</type>
				<bonus type="number">${pcstring('WEAPON.${weapno}.TOTALHIT.0-ATTACK.RANGED.BASE-ATTACK.RANGED.STAT-ATTACK.RANGED.SIZE-ATTACK.RANGED.MISC.INTVAL')}</bonus>
				<rangeincrement type="number">${pcstring('WEAPON.${weapno}.RANGE.NOUNITS')}</rangeincrement>
<#else>
				<type type="number">0</type>
				<bonus type="number">${pcstring('WEAPON.${weapno}.MAGICHIT')}</bonus>
				<rangeincrement type="number">0</rangeincrement>
</#if>
<#if weaponHash.Name != "">
				<shortcut type="windowreference">
					<class>item</class>
					<recordname>....inventorylist.id-${weaponHash.ID?left_pad(5, "0")}</recordname>
				</shortcut>
</#if>
			</id-${(weapno+1)?left_pad(5, "0")}>
</@loop>
		</weaponlist>
		<weight type="string">${pcstring('WEIGHT')}</weight>
	</character>
</root>