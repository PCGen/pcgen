<#-- Output the number with a appropriate suffix e.g. 1st -->
<#macro suffixnum num>
${num}<#t>
<#switch num>
<#case 1>
st<#t>
<#break>
<#case 2>
nd<#t>
<#break>
<#case 3>
rd<#t>
<#break>
<#default>
th<#t>
</#switch>
</#macro>

<#-- Output an abbreviation for the type fo the speicval ability. -->
<#macro typeOfAbilitySuffix typeOfAbility >
<#if (typeOfAbility?contains("extraordinary"))> 
 (Ex)
</#if>
<#if (typeOfAbility?contains("supernatural"))> 
 (Su)
</#if>
<#if (typeOfAbility?contains("spelllike"))> 
 (Sp)
</#if>
<#if (typeOfAbility?contains("psilike"))> 
 (Ps)
</#if>
</#macro>
