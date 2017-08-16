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
			font-size: large;
		}
		p.spells {
			margin-left: 2em;
		}
		p.sa {
			text-indent: 1em;
		}
		table.section {
			width: 100%;
			color: white;
			background: black;
			font-weight: bold;
			font-size: small;
			margin-top: 1px;
			margin-bottom: 1px;
		}
		td.section {
			padding-left: 5px;
			padding-right: 5px;
		}
		table.header {
			width: 100%;
			border: 1px solid black;
			border-collapse: collapse;
			margin-bottom: 2px;
		}
		td.name {
			font-family: Oleandra, Gandhi Sans, Arial, sans-serif;
			font-size: large;
			font-weight: bold;
			color: white;
			background: black;
			border: 1px solid black;
			padding-left: 5px;
		}
		td.cr, td.xp {
			font-weight: bold;
			border: 1px solid black;
		}
		td.raceclass {
			font-weight: bold;
			background: lightgrey;
			border: 1px solid black;
			padding-left: 5px;
		}
		td.sizetype {
			background: lightgrey;
			border: 1px solid black;
			padding-left: 5px;
		}
		td.alignment {
			font-weight: bold;
			font-size: larger;
			color: white;
			background: black;
			border: 1px solid black;
		}
		table.description {
			font-family: Nexus Serif, Gandhi Serif, Times New Roman, serif;
			margin-top: 20px;
		}
	</style>
</head>

<body>
<table class="header">
	<tr>
		<td class="name">${pcstring('TEXT.UPPER.NAME')}</td>
		<td class="cr" align="center">CR
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
	<tr>
		<td class="raceclass">
<#if (pcstring("NAME") = pcstring("RACE") && pcvar("COUNT[TEMPLATES]") = 0)><#--  |IIF(NAME:RACE.AND.VAR.COUNT[TEMPLATES]:0.0)| -->
<#else>
<!-- ${pcstring('GENDER.LONG')} -->
<@loop from=0 to=pcvar('COUNT[TEMPLATES]-1') ; template , template_has_next>
${pcstring('TEXT.UPPERCASE.TEMPLATE.${template}.APPLIEDNAME')}
</@loop>
<#if (pcstring("AGE.CATEGORY") = "Adult")>
<#else>
${pcstring('TEXT.UPPERCASE.AGE.CATEGORY')}
</#if>
${pcstring('TEXT.UPPERCASE.RACE')}
<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>
<#if (pcstring("CLASS.${class}.ISMONSTER") = "N")>
${pcstring('TEXT.UPPERCASE.CLASS.${class}')}
<#if (pcstring("CLASS.${class}")?lower_case?contains("cleric"))>
of ${pcstring('DEITY')}
</#if>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; archetype , archetype_has_next>
<#if (pcstring("ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.TYPE")?lower_case?contains(pcstring("CLASS.${class}")?lower_case)) >
(${pcstring("TEXT.UPPERCASE.ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype")})
</#if>
</@loop>

${pcstring('CLASS.${class}.LEVEL')}
<#if (class_has_next) >
/
</#if>
</#if>
</@loop>
</#if>
		</td>
		<td class="xp" align="center">
<#if (pcstring("XPAWARD") != "0")>
XP ${pcstring("XPAWARD")}
</#if>
		</td>
	</tr>
	<tr>
		<td class="sizetype">
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
		</td>
		<td class="alignment" align="center">
<#if (pcstring("ALIGNMENT.SHORT") = "TN")>
N
<#else>
${pcstring('ALIGNMENT.SHORT')}
</#if>
		</td>
	</tr>
</table>

<#include "common/common-pathfinder.ftl">

<!-- Start of Description -->
<#if (pcstring('DESC')) != "">
<p></p>
<table class="description">
  <tr>
    <td>${pcstring('DESC')}</td>
  </tr>
</table>
</#if>
<!-- End of Description -->

</body>
</html>

