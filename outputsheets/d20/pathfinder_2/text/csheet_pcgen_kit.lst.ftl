<#ftl encoding="UTF-8" strip_whitespace=true >
<#--
# Freemarker template for exporting a character as a kit.
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
<#macro abilityBlock parentCat abilityIdx visible>
<#if visible>
<#assign visibility = 'VISIBLE' />
<#else>
<#assign visibility = 'HIDDEN' />
</#if>
<#if (pcvar('ABILITY.${parentCat}.${visibility}.${abilityIdx}.ASSOCIATEDCOUNT') > 0)>
<@loop from=0 to=pcvar('ABILITY.${parentCat}.${visibility}.${abilityIdx}.ASSOCIATEDCOUNT')-1 ; assocIdx>
<#assign assoc = pcstring('ABILITY.${parentCat}.${visibility}.${abilityIdx}.ASSOCIATED.${assocIdx}') />
ABILITY:CATEGORY=${pcstring('ABILITY.${parentCat}.${visibility}.${abilityIdx}.CATEGORY')}|${pcstring('ABILITY.${parentCat}.${visibility}.${abilityIdx}.KEY')}<#if (assoc?length > 0)> (${assoc})</#if>
</@loop>
<#else>
ABILITY:CATEGORY=${pcstring('ABILITY.${parentCat}.${visibility}.${abilityIdx}.CATEGORY')}|${pcstring('ABILITY.${parentCat}.${visibility}.${abilityIdx}.KEY')}
</#if>
</#macro>

# This is a first pass kit to reproduce ${pcstring('NAME')} and will need manual tweaking
STARTPACK:${pcstring('NAME')}	TYPE:NPC	VISIBLE:QUALIFY	EQUIPBUY:0	PREMULT:1,[PRERACE:1,${pcstring('RACE')}],[!PRERACE:1,%]	SOURCEPAGE:TODO
RACE:${pcstring('RACE')}	!PRERACE:1,%
NAME:${pcstring('NAME')}
AGE:${pcstring('AGE')}
GENDER:${pcstring('GENDER')}
<#if (pcstring('ALIGNMENT.SHORT')?length > 0 && pcstring('ALIGNMENT.SHORT') != 'None')>
ALIGN:${pcstring('ALIGNMENT.SHORT')}
</#if>
STAT:<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next >${pcstring('STAT.${stat}.NAME')}=${pcstring('STAT.${stat}.BASE')}<#if stat_has_next>|</#if></@loop>
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next >
CLASS:<@pcstring tag="CLASS.${class}"/>		LEVEL:<@pcstring tag="CLASS.${class}.LEVEL"/>
</@loop>
<#if (pcstring('DEITY')?length > 0)>
<#assign numDomains = pcvar('COUNT[DOMAINS]') />
DEITY:${pcstring('DEITY')}	<#if (numDomains>0)>DOMAIN:<@loop from=0 to=numDomains-1 ; dmnIdx, dmnIdx_has_next>${pcstring('DOMAIN.${dmnIdx}')}<#if dmnIdx_has_next>|</#if></@loop></#if>
</#if>
<@loop from=0 to=pcvar('COUNT[SKILL]-1') ; skill , skill_has_next >
<#if ((pcvar('SKILL.${skill}.RANK') > 0) && pcstring('SKILL.${skill}.TYPE')?contains('Base'))><#-- We only want to see base skills here -->
SKILL:<@pcstring tag="SKILL.${skill}"/>			RANK:<@pcstring tag="SKILL.${skill}.RANK"/>
</#if>
</@loop>
<#assign catList = ['Feat', 'Special Ability'] />
<#list catList as parentCat> 
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=${parentCat}","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","NATURE=NORMAL")-1') ; abilityIdx >
<@abilityBlock parentCat=parentCat abilityIdx=abilityIdx visible=true />
</@loop>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=${parentCat}","VISIBILITY=HIDDEN[or]VISIBILITY=DISPLAY_ONLY","NATURE=NORMAL")-1') ; abilityIdx >
<@abilityBlock parentCat=parentCat abilityIdx=abilityIdx visible=false />
</@loop>
</#list>
<@loop from=0 to=pcvar('COUNT[EQUIPMENT.MERGELOC]')-1 ; equip >
GEAR:<@pcstring tag="EQ.MERGELOC.${equip}.NAME"/>	<#if (pcvar('EQ.MERGELOC.${equip}.QTY') != 1)>QTY:<@pcstring tag="EQ.MERGELOC.${equip}.QTY"/></#if>		LOCATION:<@pcstring tag="EQ.MERGELOC.${equip}.LOCATION"/><#if (pcstring("EQ.MERGELOC.${equip}.LOCATION")?length = 0)>Not Carried</#if>
</@loop>
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook >
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class >
<@loop from=9 to=0 step=-1 ; level ><@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]')-1 ; spell >
<#assign times = pcvar('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES') />
SPELLS:SPELLBOOK=${pcstring('SPELLBOOKNAME.${spellbook}')}|CLASS=${pcstring('SPELLLISTCLASS.${class}')}|<@pcstring tag="SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME"/><#if times != 1>=${times}</#if>
</@loop></@loop>
</@loop>
</@loop>

