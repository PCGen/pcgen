<#function typeOfAbilitySuffix typeOfAbility >
	<#local outputString = ""/>
	<#local typeSeq = [""]/>
	<#if (typeOfAbility?contains("extraordinary"))><#local typeSeq += ["Ex"]/></#if>
	<#if (typeOfAbility?contains("supernatural"))><#local typeSeq += ["Su"]/></#if>
	<#if (typeOfAbility?contains("spelllike"))><#local typeSeq += ["Sp"]/></#if>
	<#if (typeOfAbility?contains("psilike"))><#local typeSeq += ["Ps"]/></#if>
	<#local outputString = typeSeq[1..]?join(", ")/>
	<#if outputString != "">
		<#local outputString = outputString?replace(", (\\w+)$"," and $1","r")/>
		<#local outputString = outputString?ensure_starts_with(" (")?ensure_ends_with(")")/>
	</#if>
	<#return outputString/>
</#function>

<#function spellNameReplace name>
	<#if name?contains("(Acid)") || name?contains("(Cold)") || name?contains("(Electricity)") || name?contains("(Fire)") || name?contains("(Sonic)")>
		<#return name/>
	<#else>
		<#return name?replace("(.*?) [(](.*?)[)]","$2 $1","r")/>
	</#if>
</#function>

<#function sourceNameReplace name>
	<#if name?contains(", p.")>
		<#return name?replace(", p\\.(.*?)"," $1","r")?remove_beginning("Custom - ")/>
	<#else>
		<#return name?remove_beginning("Custom - ")/>
	</#if>
</#function>

<#function magicEquipReplace name>
<#if name?matches(".*? [(]Type .*?[)]")>
	<#return name?replace("(.*?) [(](Type .*?)[)]","$1, $2","r")/>/>
<#elseif name?matches(".*? [(].*?[/].*?[/].*?[)]")>
	<#return spellNameReplace(name?replace("(.*?) [(](.*?)[/](.*?)[/](.*?)[)]","$1 of $2","r"))/>
<#--<#elseif name?matches("(.*?) [(].*?[)]")>
	<#return name?replace("(.*?) [(](.*?)[)]","$2 $1","r")/>-->
<#else>
	<#return name/>
</#if>
</#function>

<#function equipReplace name>
<#--<#if name?matches("(.*?)[,] (.*?)")>
	<#return name?replace("(.*?)[,] (.*?)","$2 $1","r")/>-->
<#if name?matches(".*? [(]Type .*?[)]")>
	<#return name?replace("(.*?) [(](Type .*?)[)]","$1, $2","r")/>/>
<#elseif name?matches("(.*?) [(](.*?)[/]Per Day[)]")>
	<#return name?replace("(.*?) [(](.*?)[/]Per Day[)]","$2 $1","r")/>
<#elseif name?matches("(.*?) [(](.*?)[/]Per Day[/](Colossal|Gargantuan|Huge|Large|Medium|Small|Tiny|Diminutive|Fine)[)]")>
	<#return name?replace("(.*?) [(](.*?)[/]Per Day[/](Colossal|Gargantuan|Huge|Large|Medium|Small|Tiny|Diminutive|Fine)[)]","$3 $2 $1","r")/>
<#elseif name?matches("(.*?) [(]per .*?[)]")>
	<#return name?replace("(.*?) [(]per .*?[)]","$1","r")/>
<#elseif name?matches("(.*?) [(](.*?)[/].*? oz.*?[)]")>
	<#return name?replace("(.*?) [(](.*?)[/].*? oz.*?[)]","$2 $1","r")/>
<#elseif name?matches("(.*?) [(].*? oz.*?[)]")>
	<#return name?replace("(.*?) [(].*? oz.*?[)]","$1","r")/>
<#elseif name?matches("(.*?) [(]Flask[)]")>
	<#return name?replace("(.*?) [(]Flask[)]","$1","r")/>
<#elseif name?matches("(.*?) [(](.*?)[/](.*?)[)]")>
	<#return name?replace("(.*?) [(](.*?)[/](.*?)[)]","$2 $3 $1","r")/>
<#elseif name?matches("(.*?) [(].*?[)]")>
	<#return name?replace("(.*?) [(](.*?)[)]","$1, $2","r")/>
<#else>
	<#return name/>
</#if>
</#function>

<#function equipName equip>
<#local equipment = ""/>
<#if (pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.TYPE')?lower_case?contains("magic"))>
	<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.TYPE')?lower_case?contains("wand")>
		<#if pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CHARGES') lt pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.MAXCHARGES')>
			<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?contains("wand of")>
				<#local equipment = "${pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')} (${pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CHARGES')} charges)"/>
			<#else>
				<#local spellName = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","$1","r")/>
				<#local spellName = spellNameReplace(spellName)/>
				<#local metamagic = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","($2)","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","","r")/>
				<#if metamagic != ""><#local metamagic += " "/></#if>
				<#local casterLevel = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$4","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","$3","r")/>
				<#local charges = pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.CHARGES')/>
				<#local equipment = "Wand of ${metamagic}${spellName} (CL ${casterLevel}, ${charges} charges)"/>
			</#if>
		<#else>
			<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?contains("wand of")>
				<#local equipment = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')/>
			<#else>
				<#local spellName = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","$1","r")/>
				<#local spellName = spellNameReplace(spellName)/>
				<#local metamagic = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","($2)","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","","r")/>
				<#if metamagic != ""><#local metamagic += " "/></#if>
				<#local casterLevel = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Wand [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$4","r")?replace("Wand [(](.*?)[/](.*?)[/](.*?)[)]","$3","r")/>
				<#local equipment = "wand of ${metamagic}${spellName} (CL ${casterLevel})"/>
			</#if>
		</#if>
	<#elseif pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.TYPE')?lower_case?contains("scroll")>
			<#local spellName = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Scroll [(](.*?)[)]{2}","$1)","r")?replace("Scroll [(](.*?)[)]","$1","r")/>
			<#local spellName = spellNameReplace(spellName)/>
			<#local metamagic = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$2","r")?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","","r")?replace("Scroll [(](.*?)[)]{2}","","r")?replace("Scroll [(](.*?)[)]","","r")/>
			<#if metamagic != ""><#local metamagic += " "/></#if>
			<#local casterLevel = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$4","r")?replace("Scroll [(](.*?)[/](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$3","r")?replace("Scroll [(](.*?)[)]{2}","","r")?replace("Scroll [(](.*?)[)]","","r")/>
			<#if casterLevel = "" && metamagic = ""><#local itemString = "Scroll of ${spellName}"/><#elseif casterLevel = ""><#local itemString = "Scroll of ${metamagic}${spellName}"/><#else><#local itemString = "Scroll of ${metamagic}${spellName} (CL ${casterLevel})"/></#if>
			<#local equipment = itemString/>
	<#elseif pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.TYPE')?lower_case?contains("potion")>
			<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?keep_before(" ")?contains("potion")>
				<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?contains("potion of")>
					<#local equipment = spellNameReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME'))/>
				<#else>
					<#local spellName = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Potion [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Potion [(](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Potion [(](.*?)[)]{2}","$1)","r")?replace("Potion [(](.*?)[)]","$1","r")/>
					<#local spellName = spellNameReplace(spellName)/>
					<#local metamagic = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Potion [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$2","r")?replace("Potion [(](.*?)[/](.*?)[/](.*?)[)]","","r")?replace("Potion [(](.*?)[)]{2}","","r")?replace("Potion [(](.*?)[)]","","r")/>
					<#if metamagic != ""><#local metamagic += " "/></#if>
					<#local casterLevel = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Potion [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$4","r")?replace("Potion [(](.*?)[/](.*?)[/](.*?)[)]","$3","r")?replace("Potion [(](.*?)[)]{2}","","r")?replace("Potion [(](.*?)[)]","","r")/>
					<#if casterLevel = "" && metamagic = ""><#local itemString = "Potion of ${spellName}"/><#elseif casterLevel = ""><#local itemString = "Potion of ${metamagic}${spellName}"/><#else><#local itemString = "Potion of ${metamagic}${spellName} (CL ${casterLevel})"/></#if>
					<#local equipment = itemString/>
				</#if>
			</#if>
			<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?keep_before(" ")?contains("oil")>
				<#if pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?lower_case?contains("oil of")>
					<#local equipment = spellNameReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME'))/>
				<#else>
					<#local spellName = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Oil [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Oil [(](.*?)[/](.*?)[/](.*?)[)]","$1","r")?replace("Oil [(](.*?)[)]{2}","$1)","r")?replace("Oil [(](.*?)[)]","$1","r")/>
					<#local spellName = spellNameReplace(spellName)/>
					<#local metamagic = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Oil [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$2","r")?replace("Oil [(](.*?)[/](.*?)[/](.*?)[)]","","r")?replace("Oil [(](.*?)[)]{2}","","r")?replace("Oil [(](.*?)[)]","","r")/>
					<#if metamagic != ""><#local metamagic += " "/></#if>
					<#local casterLevel = pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME')?replace("Oil [(](.*?)[/](.*?)[/](.*?)[/](.*?)[)]","$4","r")?replace("Oil [(](.*?)[/](.*?)[/](.*?)[)]","$3","r")?replace("Oil [(](.*?)[)]{2}","","r")?replace("Oil [(](.*?)[)]","","r")/>
					<#if casterLevel = "" && metamagic = ""><#local itemString = "Oil of ${spellName}"/><#elseif casterLevel = ""><#local itemString = "Oil of ${metamagic}${spellName}"/><#else><#local itemString = "Oil of ${metamagic}${spellName} (CL ${casterLevel})"/></#if>
					<#local equipment = itemString/>
				</#if>
			</#if>
	<#else>
		<#local equipment = magicEquipReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME'))/>
	</#if>
<#else>
	<#local equipment = equipReplace(pcstring('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.OUTPUTNAME'))/>
</#if>
<#return equipment?lower_case?cap_first/>
</#function>

<#macro abilityOutput count="TYPE=SpecialQuality" countExclude="" typeList=".TYPE=SpecialQuality" typeExclude="" visibility="default">
<#if visibility = "default">
	<#local countVisible = "VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY"/>
	<#local listVisible = "VISIBLE."/>
<#elseif visibility = "all">
	<#local countVisible = "VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY[or]VISIBILITY=DISPLAY_ONLY[or]VISIBILITY=HIDDEN"/>
	<#local listVisible = "ALL."/>
</#if>
<#if countExclude = ""><#local specialAbilityCount=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","${count}","${countVisible}")-1')/>
<#else><#local specialAbilityCount=pcvar('countdistinct("ABILITIES","CATEGORY=Special Ability","${count}","${countExclude}","${countVisible}")-1')/>
</#if>
<#local specialAbilityTypeList=typeList + typeExclude/>
<@loop from=0 to=specialAbilityCount ; specialAbility , specialAbility_has_next>
<#if pcstring('ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.DESC') = "">
<#else>
<#local typeOfAbility = pcstring("ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.TYPE")?lower_case />
			<id-${(specialAbility+1)?left_pad(5, "0")}>
				<name type="string"><#if (pcstring("ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.HASASPECT.Name") = "Y")>${pcstring('ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.ASPECT.Name')}${typeOfAbilitySuffix(typeOfAbility)}<#else>${pcstring('ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}')}${typeOfAbilitySuffix(typeOfAbility)}</#if></name>
				<text type="formattedtext">
					<p>${stringCleanup(pcstring('ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.DESC'))}</p>
				</text>
				<sourcebook type="string">${sourceNameReplace(pcstring('ABILITYALL.Special Ability.${listVisible}${specialAbility}${specialAbilityTypeList}.SOURCE'))}</sourcebook>
				<locked type="number">1</locked>
			</id-${(specialAbility+1)?left_pad(5, "0")}>
</#if>
</@loop>
</#macro>

<#function plusBonus type>
	<#if type?contains("plus1")>
		<#return "1"/>
	<#elseif type?contains("plus2")>
		<#return "2"/>
	<#elseif type?contains("plus3")>
		<#return "3"/>
	<#elseif type?contains("plus4")>
		<#return "4"/>
	<#elseif type?contains("plus5")>
		<#return "5"/>
	<#else>
		<#return ""/>
	</#if>
</#function>

<#function itemValue equip>
	<#local itemValueGP = pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.COST')?int />
	<#local itemValueSP = ((pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.COST')-itemValueGP)/0.1)?int />
	<#local itemValueCP = ((pcvar('EQ.NOT.Coin.NOT.Gem.NOT.Temporary.${equip}.COST')-itemValueGP-(itemValueSP/10))/0.01)?int />
	<#local itemValueString = ""/>
	<#if itemValueGP gt 0>
		<#local itemValueString += "${itemValueGP} gp"/>
		<#if itemValueSP gt 0 || itemValueCP gt 0>
			<#local itemValueString += ", "/>
		</#if>
	</#if>
	<#if itemValueSP gt 0>
		<#local itemValueString += "${itemValueSP} sp"/>
		<#if itemValueCP gt 0>
			<#local itemValueString += ", "/>
		</#if>
	</#if>
	<#if itemValueCP gt 0>
		<#local itemValueString += "${itemValueCP} cp"/>
	</#if>
	<#return itemValueString/>
</#function>

<#function ordinalToCardinal input>
	<#if input?lower_case?matches("[0-9][0-9][st|nd|rd|th]")>
		<#return input?lower_case?replace("([0-9][0-9])[st|nd|rd|th]","$1","r")/>
	<#else>
		<#return input/>
	</#if>
</#function>

<#function stringCleanup input>
	<#return input?replace("<para>","<p>")?replace("</para>","</p>")/>
</#function>

<#function damageType eqtype>
	<#if eqtype?contains("firearm")>
		<#local eqDamageType = eqtype?replace("firearm","")/>
	<#else>
		<#local eqDamageType = eqtype/>
	</#if>
	<#local damageTypeSeq = [""]/>
	<#local damageTypeString = ""/>

	<#if eqDamageType?contains("nonlethal")><#local damageTypeSeq += ["Nonlethal"]/></#if>
	<#if eqDamageType?contains("bludgeoning")><#local damageTypeSeq += ["Blugeoning"]/></#if>
	<#if eqDamageType?contains("piercing")><#local damageTypeSeq += ["Piercing"]/></#if>
	<#if eqDamageType?contains("slashing")><#local damageTypeSeq += ["Slashing"]/></#if>
	<#if eqDamageType?contains("acid")><#local damageTypeSeq += ["Acid"]/></#if>
	<#if eqDamageType?matches("\\w(cold)")><#local damageTypeSeq += ["Cold"]/></#if>
	<#if eqDamageType?contains("electricity")><#local damageTypeSeq += ["Electricity"]/></#if>
	<#if eqDamageType?contains("fire")><#local damageTypeSeq += ["Fire"]/></#if>
	<#if eqDamageType?contains("sonic")><#local damageTypeSeq += ["Sonic"]/></#if>
	<#if eqDamageType?contains("force")><#local damageTypeSeq += ["Force"]/></#if>
	<#if eqDamageType?contains("special")><#local damageTypeSeq += ["Special"]/></#if>
	<#if eqDamageType?contains("negative")><#local damageTypeSeq += ["Negative"]/></#if>
	<#if eqDamageType?contains("positive")><#local damageTypeSeq += ["Positive"]/></#if>
	<#if eqDamageType?contains("adamantine")><#local damageTypeSeq += ["Adamantine"]/></#if>
	<#if eqDamageType?contains("coldiron")><#local damageTypeSeq += ["Cold Iron"]/></#if>
	<#if eqDamageType?contains("silver")><#local damageTypeSeq += ["Silver"]/></#if>
	<#if eqDamageType?contains("epic")><#local damageTypeSeq += ["Epic"]/></#if>
	<#if eqDamageType?contains("chaotic")><#local damageTypeSeq += ["Chaotic"]/></#if>
	<#if eqDamageType?contains("evil")><#local damageTypeSeq += ["Evil"]/></#if>
	<#if eqDamageType?contains("good")><#local damageTypeSeq += ["Good"]/></#if>
	<#if eqDamageType?contains("lawful")><#local damageTypeSeq += ["Lawful"]/></#if>

	<#local damageTypeString += damageTypeSeq[1..]?join(", ")/>
	<#return damageTypeString?lower_case/>
</#function>

<#function itemType eqtype typeorsubtype>
	<#if eqtype?contains("shield")>
		<#if typeorsubtype == "type">
			<#local typeString = [""]/>
			<#if eqtype?contains("magic")>
				<#local typeString += ["Magic"]/>
			</#if>
			<#if eqtype?contains("technological")>
				<#local typeString += ["Technological"]/>
			</#if>
			<#local typeString += ["Armor"]/>
			<#return typeString[1..]?join(" ")/>
		<#elseif typeorsubtype == "subtype">
			<#if eqtype?contains("specific")>
				<#return "Specific Shield"/>
			<#else>
				<#return "Shield"/>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("weapon")>
		<#if typeorsubtype == "type">
			<#local typeString = [""]/>
			<#if eqtype?contains("magic")>
				<#local typeString += ["Magic"]/>
			</#if>
			<#if eqtype?contains("technological")>
				<#local typeString += ["Technological"]/>
			</#if>
			<#local typeString += ["Weapon"]/>
			<#return typeString[1..]?join(" ")/>
		<#elseif typeorsubtype == "subtype">
			<#local typeString = [""]/>
			<#if eqtype?contains("specific")>
				<#return "Specific Weapon">
			<#else>
				<#if eqtype?contains("exotic")>
					<#local typeString += ["Exotic"]/>
				<#elseif eqtype?contains("martial")>
					<#local typeString += ["Martial"]/>
				<#elseif eqtype?contains("simple")>
					<#local typeString += ["Simple"]/>
				</#if>
				<#if eqtype?contains("unarmed")>
					<#local typeString += ["Unarmed"]/>
				<#elseif eqtype?contains("light")>
					<#local typeString += ["Light"]/>
				<#elseif eqtype?contains("onehanded")>
					<#local typeString += ["One-Handed"]/>
				<#elseif eqtype?contains("twohanded")>
					<#local typeString += ["Two-Handed"]/>
				</#if>
				<#if eqtype?contains("melee")>
					<#local typeString += ["Melee"]/>
				<#elseif eqtype?contains("ranged")>
					<#local typeString += ["Ranged"]/>
				</#if>
				<#if typeString?size gt 1>
					<#return typeString[1..]?join(" ")/>
				<#else>
					<#return ""/>
				</#if>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("ammunition")>
		<#if typeorsubtype == "type">
			<#return "Weapon"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Ammunition"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("armor")>
		<#if typeorsubtype == "type">
			<#local typeString = [""]/>
			<#if eqtype?contains("magic")>
				<#local typeString += ["Magic"]/>
			</#if>
			<#if eqtype?contains("technological")>
				<#local typeString += ["Technological"]/>
			</#if>
			<#local typeString += ["Armor"]/>
			<#return typeString[1..]?join(" ")/>
		<#elseif typeorsubtype == "subtype">
			<#if eqtype?contains("specific")>
				<#return "Specific Armor"/>
			<#elseif eqtype?contains("heavy")>
				<#return "Heavy"/>
			<#elseif eqtype?contains("medium")>
				<#return "Medium"/>
			<#elseif eqtype?contains("light")>
				<#return "Light"/>
			<#else>
				<#return ""/>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("goods")>
		<#if typeorsubtype == "type">
			<#return "Goods and Services"/>
		<#elseif typeorsubtype == "subtype">
			<#if eqtype?contains("goods") && eqtype?contains("general")>
				<#return "Adventuring Gear"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("alchemical")>
				<#return "Special Substances and Items"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("magic")>
				<#return "Special Substances and Items"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("flask")>
				<#return "Special Substances and Items"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("tools")>
				<#return "Tools and Skill Kits"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("clothing")>
				<#return "Clothing"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("food")>
				<#return "Food, Drink, and Lodging"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("mount")>
				<#return "Mounts and Related Gear"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("transportation") && eqtype?contains("vehicle")>
				<#return "Transport"/>
			<#elseif eqtype?contains("goods") && eqtype?contains("poison")>
				<#return "Poisons"/>
			<#else>
				<#return ""/>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("service") && eqtype?contains("lodging")>
		<#if typeorsubtype == "type">
			<#return "Goods and Services"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Food, Drink, and Lodging"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("service")>
		<#if typeorsubtype == "type">
			<#return "Goods and Services"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Spellcasting and Services"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("spellcomponent")>
		<#if typeorsubtype == "type">
			<#return "Goods and Services"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Spell Components"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("artifact")>
		<#if typeorsubtype == "type">
			<#return "Magic Artifact"/>
		<#elseif typeorsubtype == "subtype">
			<#if eqtype?contains("artifact") && eqtype?contains("major")>
				<#return "Major Artifact"/>
			<#elseif eqtype?contains("artifact") && eqtype?contains("minor")>
				<#return "Minor Artifact"/>
			<#else>
				<#return ""/>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("wondrous")>
		<#if typeorsubtype == "type">
			<#return "Magic Wondrous Item"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Magic Wondrous Item"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("ring")>
		<#if typeorsubtype == "type">
			<#return "Magic Ring"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Magic Ring"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("staff")>
		<#if typeorsubtype == "type">
			<#return "Magic Staff"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Magic Staff"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("rod")>
		<#if typeorsubtype == "type">
			<#return "Magic Rod"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Magic Rod"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("potion")>
		<#if typeorsubtype == "type">
			<#return "Magic Potion"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Potion"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("wand")>
		<#if typeorsubtype == "type">
			<#return "Magic Wand"/>
		<#elseif typeorsubtype == "subtype">
			<#return "Magic Wand"/>
		<#else>
			<#return ""/>
		</#if>
	<#elseif eqtype?contains("magic") && eqtype?contains("scroll")>
		<#if typeorsubtype == "type">
			<#return "Magic Scroll"/>
		<#elseif typeorsubtype == "subtype">
			<#if eqtype?contains("arcane")>
				<#return "Arcane Scroll"/>
			<#elseif eqtype?contains("divine")>
				<#return "Divine Scroll"/>
			<#else>
				<#return "Magic Scroll"/>
			</#if>
		<#else>
			<#return ""/>
		</#if>
	<#else>
		<#return ""/>
	</#if>
</#function>

<#function convertDice dSize>
	<#if dSize == "1d4">
		<#return "d4"/>
	<#elseif dSize == "1d6">
		<#return "d6"/>
	<#elseif dSize == "1d8">
		<#return "d8"/>
	<#elseif dSize == "1d10">
		<#return "d10"/>
	<#elseif dSize == "1d12">
		<#return "d12"/>
	<#elseif dSize == "1d20">
		<#return "d20"/>
	<#elseif dSize == "2d4">
		<#return "d4,d4"/>
	<#elseif dSize == "2d6">
		<#return "d6,d6"/>
	<#elseif dSize == "2d8">
		<#return "d8,d8"/>
	<#elseif dSize == "2d10">
		<#return "d10,d10"/>
	<#elseif dSize == "2d12">
		<#return "d12,d12"/>
	<#elseif dSize == "2d20">
		<#return "d20,d20"/>
	<#else>
		<#return ""/>
	</#if>
</#function>

<#function weaponDamageType eqtype>
	<#if eqtype?contains("firearm")>
		<#local eqDamageType = eqtype?replace("firearm","")/>
	<#else>
		<#local eqDamageType = eqtype/>
	</#if>
	<#local damageTypeSeq = [""]/>
	<#local damageTypeString = ""/>

	<#if eqDamageType?contains("B")><#local damageTypeSeq += ["Blugeoning"]/></#if>
	<#if eqDamageType?contains("P")><#local damageTypeSeq += ["Piercing"]/></#if>
	<#if eqDamageType?contains("S")><#local damageTypeSeq += ["Slashing"]/></#if>
	<#if eqDamageType?contains("A")><#local damageTypeSeq += ["Acid"]/></#if>
	<#if eqDamageType?contains("C")><#local damageTypeSeq += ["Cold"]/></#if>
	<#if eqDamageType?contains("E")><#local damageTypeSeq += ["Electricity"]/></#if>
	<#if eqDamageType?contains("F")><#local damageTypeSeq += ["Fire"]/></#if>
	<#if eqDamageType?contains("So")><#local damageTypeSeq += ["Sonic"]/></#if>

	<#local damageTypeString += damageTypeSeq[1..]?join(", ")/>
	<#return damageTypeString?lower_case/>
</#function>

<#function getArmorProfs>
	<#local armorProfs = ""/>
	<#local armorProfsTest = ""/>
	<#local armorProfsTest += pcstring("ABILITYALLLIST.Internal") + ", "/>
	<#local armorProfsTest += pcstring("ABILITYALLLIST.FEAT")/>

	<#if (armorProfsTest?contains("Armor Prof ~ Light") || armorProfsTest?contains("Armor Proficiency (Light)")) &&
	(armorProfsTest?contains("Armor Prof ~ Medium") || armorProfsTest?contains("Armor Proficiency (Medium)")) &&
	(armorProfsTest?contains("Armor Prof ~ Heavy") || armorProfsTest?contains("Armor Proficiency (Heavy)"))>
		<#local armorProfs += "all, "/>
	<#else>
		<#if (armorProfsTest?contains("Armor Prof ~ Light") || armorProfsTest?contains("Armor Proficiency (Light)"))>
			<#local armorProfs += "light, "/>
		</#if>
		<#if (armorProfsTest?contains("Armor Prof ~ Medium") || armorProfsTest?contains("Armor Proficiency (Medium)"))>
			<#local armorProfs += "medium, "/>
		</#if>
		<#if (armorProfsTest?contains("Armor Prof ~ Heavy") || armorProfsTest?contains("Armor Proficiency (Heavy)"))>
			<#local armorProfs += "heavy, "/>
		</#if>
	</#if>
	<#if (armorProfsTest?contains("Shield Prof") || armorProfsTest?contains("Shield Proficiency"))>
		<#local armorProfs += "shields"/>
		<#if (armorProfsTest?contains("Shield Prof ~ Tower") || armorProfsTest?contains("Tower Shield Proficiency"))>
			<#local armorProfs += " (including tower shields)"/>
		<#else>
			<#local armorProfs += " (except tower shields)"/>
		</#if>
	</#if>
	<#local armorProfs = armorProfs?remove_ending(", ")/>
	<#if armorProfs = ""><#local armorProfs = "none"/></#if>
	<#return armorProfs/>
</#function>

<#function getWeaponProfs>
	<#local weaponProfs = [""]/>
	<#local weaponProfsTest = ""/>
	<#local weaponProfsTest += pcstring("ABILITYALLLIST.Internal") + ", "/>
	<#local weaponProfsTest += pcstring("ABILITYALLLIST.FEAT")/>
	<#local featWeaponProfs = [""]/>
	<#local outputString = ""/>

	<#if (weaponProfsTest?contains("Weapon Prof ~ Simple"))>
		<#local weaponProfs += ["simple"]/>
	</#if>
	<#if (weaponProfsTest?contains("Weapon Prof ~ Martial"))>
		<#local weaponProfs += ["martial"]/>
	</#if>
	<#if (weaponProfsTest?contains("Simple Weapon Proficiency")) || (weaponProfsTest?contains("Martial Weapon Proficiency")) || (weaponProfsTest?contains("Exotic Weapon Proficiency"))>
		<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=FEAT","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; feat , feat_has_next>
			<#if (pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')?contains("Simple Weapon Proficiency")) || (pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')?contains("Martial Weapon Proficiency")) || (pcstring('ABILITYALL.FEAT.VISIBLE.${feat}')?contains("Exotic Weapon Proficiency"))>
				<@loop from=0 to=pcvar('ABILITYALL.FEAT.VISIBLE.${feat}.ASSOCIATEDCOUNT') ; featProf , featProf_has_next>
					<#local featWeaponProfs += [pcstring('ABILITYALL.FEAT.VISIBLE.${feat}.ASSOCIATED.${featProf}')?lower_case?replace("(.*?) [(](.*?)[)]","$2 $1","r")]/>
				</@loop>
			</#if>
		</@loop>
	</#if>
	<#local outputString += weaponProfs[1..]?join(", ")/>
	<#local outputString += featWeaponProfs[1..]?sort?join(", ")/>
	<#if outputString = ""><#local outputString = "none"/></#if>
	<#return outputString/>
</#function>

<#function getLegsMult legs size>
	<#if legs gte 4>
		<#if size = "F"><#return 0.125/></#if>
		<#if size = "D"><#return 0.25/></#if>
		<#if size = "T"><#return 0.25/></#if>
		<#if size = "S"><#return 0.25/></#if>
		<#if size = "M"><#return 0.5/></#if>
		<#if size = "L"><#return 1/></#if>
		<#if size = "H"><#return 2/></#if>
		<#if size = "G"><#return 4/></#if>
		<#if size = "C"><#return 8/></#if>
	<#else>
		<#return 0/>
	</#if>
</#function>
