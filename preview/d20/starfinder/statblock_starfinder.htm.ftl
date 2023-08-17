<#ftl encoding="UTF-8" strip_whitespace=true >
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<!--
Pathfinder Statblock Template
==============================
Author: Stefan Radermacher (Zaister)
Email: stefanATzaisterDOTde

$Revision: 15961 $
$Author: zaister $
$Date: 2012-02-02 13:23:59 +0100 (Do, 02 Feb 2012) $
-->
<#include "common/misc.ftl" />
<head>
	<title>${pcstring('NAME')}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<style type="text/css">
		body  {
			font-family: Noto Sans, Arial, sans-serif;
			font-size: medium;
			text-align: left;
			color: black;
			background: white;
			font-weight: normal;
			margin: 0px;
			padding: 5px;
		}
		p {
			text-indent: -1em;
			margin-left: 1em;
			margin-top: 0;
			margin-bottom: 0;
		}
		p.xp {
			font-size: larger;
		}
		p.spells {
			margin-left: 2em;
		}
		p.sa {
			text-indent: 1em;
		}
		table.section {
			width: 100%;
			font-size: small;
			font-weight: bold;
			border-top-width: 1px;
			border-top-color: black;
			border-top-style: solid;
			border-bottom-width: 1px;
			border-bottom-color: black;
			border-bottom-style: solid;
			margin-top: 2px;
			margin-bottom: 2px;
		}
		table.name {
			width: 100%;
			color: white;
			background: black;
			font-weight: bold;
		}
		td.name {
			font-variant: small-caps;
			padding-left: 5px;
			padding-right: 5px;
		}
	</style>
</head>

<body>
<table class="name">
	<tr>
		<td class="name">${pcstring('TEXT.UPPER.NAME')}</td>
		<td class="name" align="right">CR
<@compress single_line=true>
<#if (pcstring("CR") = "0")>
&mdash;
<#else>
${pcstring('CR')}
</#if>
<#if (pcvar("MR") != 0)>
/MR${" "}${pcstring("VAR.MR.INTVAL")}
</#if>
</@compress>

		</td>
	</tr>
</table>

<!-- xp award -->
<#if (pcstring("XPAWARD") != "0")>
<p class="xp">
<b>XP ${pcstring("XPAWARD")}</b>
</p>
</#if>

<!-- gender, classes -->
<p>
<#if (pcstring("NAME") = pcstring("RACE") && pcvar("COUNT[TEMPLATES]") = 0)><#--  |IIF(NAME:RACE.AND.VAR.COUNT[TEMPLATES]:0.0)| -->
<#else>
${pcstring('GENDER.LONG')}
<@loop from=0 to=pcvar('COUNT[TEMPLATES]-1') ; template , template_has_next>
${pcstring('TEXT.LOWERCASE.TEMPLATE.${template}.APPLIEDNAME')}
</@loop>
<#if (pcstring("AGE.CATEGORY") = "Adult")>
<#else>
${pcstring('TEXT.LOWERCASE.AGE.CATEGORY')}
</#if>
${pcstring('TEXT.LOWERCASE.RACE')}
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
<#if (pcstring("CLASS.${class}.ISMONSTER") = "N")>
${pcstring('TEXT.LOWERCASE.CLASS.${class}')}
<#if (pcstring("CLASS.${class}")?lower_case?contains("cleric"))>
of ${pcstring('DEITY')}
</#if>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; archetype , archetype_has_next>
<#if (pcstring("ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.TYPE")?lower_case?contains(pcstring("CLASS.${class}")?lower_case)) >
(${pcstring("TEXT.LOWERCASE.ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype")})
</#if>
</@loop>

${pcstring('CLASS.${class}.LEVEL')}
<#if (class_has_next) >
/
</#if>
</#if>
</@loop>
</#if>
</p>

<!-- alignment, size, race -->
<p>
<#if (pcstring("ALIGNMENT.SHORT") = "TN")>
N
<#else>
${pcstring('ALIGNMENT.SHORT')}
</#if>
${pcstring('SIZELONG')}
<#if (pcstring("RACETYPE") = "None")>
${pcstring('TEXT.LOWER.TYPE')}
<#else>
${pcstring('TEXT.LOWER.RACETYPE')}
</#if>
<#if (pcvar("COUNT[RACESUBTYPES]")= 0)>
<#else>
(<#t>
<@loop from=0 to=pcvar('COUNT[RACESUBTYPES]-1') ; subtype , subtype_has_next>
${pcstring('TEXT.LOWER.RACESUBTYPE.${subtype}')}<#if (subtype_has_next)>, </#if><#t>
</@loop>
)
</#if>
</p>

<#include "common/common-starfinder.ftl">
</body>
</html>

