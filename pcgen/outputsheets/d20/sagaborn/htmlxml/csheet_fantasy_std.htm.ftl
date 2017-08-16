<#ftl encoding="UTF-8" strip_whitespace=true >
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<!--
PCGen Character Sheet Template
==============================
Author: Richard O'Doherty-Gregg
Email: OdGregg@bigpond.com
Modifications by: arcady
Email: arcady0@yahoo.com
Revisions: Barak  10/15/02
Email: barak@voyager.net
Revisions: Tir-Gwaith 10/11/09
Email: tir.gwaith@gmail.com

$Revision: 24109 $
$Author: amaitland $
$Date: 2014-06-12 11:36:12 +1000 (Thu, 12 Jun 2014) $
-->
<head>
<meta http-equiv="content-type" content="text-html; charset=utf-8" />
<title>${pcstring('NAME')} [${pcstring('POOL.COST')} Points]</title>
<style type="text/css">
	.pcgen {
		font-size:xx-small;
	}
	.ab {
		font-family: Scala Sans, sans-serif;
	}
	.abb, .abt, .h {
		font-family: sans-serif;
	}
	.font6, .border6, .topline {  font-size:xx-small; }
	.font7 {  font-family: Arial, sans-serif; font-size:x-small; }
	.font8 {  font-family: Arial, sans-serif; font-size:x-small; }
	.font9, .font10 {  font-family: Arial, sans-serif; font-size:small; }
	.font14 {  font-family: Arial, sans-serif; font-size:large; }
	.h { font-size:small; vertical-align: bottom; }
	.ab { font-size:medium; text-align:center; color:white; background:black; }
	.sa-table th { font-size:9pt; text-align:center; color:white; background:black; text-transform:uppercase;}
	.abb { font-size:medium; text-align:center; font-weight:bold; border: 1px solid black;}
	.abt { font-size:medium; text-align:center; font-weight:bold; border: 5px solid lightgray;}
	.sptab, .sptab1, .sptab2 {
		font-size:x-small;
		vertical-align:top;
	}
	.sptab { text-align:center; }
	.sptab1 { text-align:left; }
	.sptab2 { text-align:right; }
	.notetab { font-family: Arial, sans-serif; vertical-align:top;border: 1px solid black; text-align:left;font-size:x-small;}
	.skl { font-family: Arial, sans-serif; font-size: small; font-weight:bold; text-align:center; vertical-align:middle; border: 4px solid black;}
	.spname { font-family: Arial, sans-serif; vertical-align:top; text-align:left;font-size:x-small;}
	.sptop { font-family: Arial, sans-serif; vertical-align:top;border: 1px solid black; text-align:center;font-size:x-small; color:white;}
	.splevel {  font-family: Arial, sans-serif; background:black; text-align:center;font-size:small; color:white;}
	.sphead {  font-family: Arial, sans-serif; background:black; text-align:center;font-size:medium; color:white;}
	.notehead {  font-family: Arial, sans-serif; background:black; text-align:center;font-size:medium; color:white;}
	.biodata { font-family: Arial, sans-serif; vertical-align:bottom; text-align:left;font-size:small;}
	.topline {
		border-top-width:1px;
		border-top: 1pt solid black;
		font-variant: small-caps;
	}
	.border {
		border: 1px solid black;
	}
	.border6 {
		border: 1px solid black;
	}
	.border7 {
		border: 1px solid black;
		font-size:x-small;
	}
	.border8 {
		border: 1px solid black;
		font-size:x-small;
	}
	.border9 {
		border: 1px solid black;
		font-size:small;
	}
	.pcgen {
		text-align:center;
	}
	.border10 {
		border: 1px solid black;
		font-size:small;
	}
	.tempborder {
		border: 5px solid lightgray;
	}
	.borderbottom {
		border-bottom-width:1px;
		border-bottom: 1pt solid black;
	}
	.borderbottom8 {
		border-bottom-width:1px;
		border-bottom: 1pt solid black;
		font-size:x-small;
	}
	span.notes p {margin-top:0; margin-bottom:0;}

	.epic {
		display:none;
	}
</style>
</head>

<body bgcolor="white">
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')}</div>
<!-- START Top Character Data -->
<table cellpadding="0" cellspacing="4" border="0" width="100%" summary="Character Info">
 <tr>
  <td colspan="2" class="h">${pcstring('NAME')} <#if (pcstring("FOLLOWEROF") != "") >- ${pcstring('FOLLOWEROF')} </#if></td>
  <td colspan="2" class="h">${pcstring('PLAYERNAME')}</td>
  <td colspan="1" class="h">${pcstring('ALIGNMENT')}</td>
  <td colspan="1" class="h">${pcstring('DEITY')}</td>
  <td class="h">${pcstring('POOL.COST')}</td>
  <td rowspan="6" align="center" width="1%" class="border" class="font10"><a href="#Bio"><img src="file://localhost/${pcstring('PORTRAIT.THUMB')}" height="100%" alt="Click for Bio" border="0" /></a><br /></td>
 </tr>
 <tr>
  <td colspan="2" class="topline">Character Name</td>
  <td colspan="2" class="topline">PLAYER</td>
  <td colspan="1" class="topline">ALIGNMENT</td>
  <td colspan="1" class="topline">DEITY</td>
  <td class="topline">POINTS</td>
 </tr>
 <tr>
 <!-- 	<@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next>>-->
 <!-- 	<#if (pcvar(pcstring('CLASS.${class}.LEVEL')) > 0) >>-->
 <!-- <td colspan="1" class="h">${pcstring('CLASS.${class}')}>-->
 <!-- 	</#if>>-->
 <!-- 	</@loop>-->
 <td colspan="1" class="h">${pcstring('CLASSLIST')}
	<#if (pcvar('count("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
	(${pcstring('ABILITYLIST.Archetype.TYPE=Archetype')})
	</#if>
	</td>

  <td colspan="1" class="h">${pcstring('EXP.CURRENT')}</td>
  		<#if (pcstring("ABILITYALL.ANY.0.TYPE=RaceName.HASASPECT.RaceName") = "Y")>
			  <td colspan="1" class="h">${pcstring('ABILITYALL.ANY.0.ASPECT=RaceName.ASPECT.RaceName')}</td>
		<#else>
			  <td colspan="1" class="h">${pcstring('RACE')}</td>
		</#if>

  <td class="h">${pcstring('SIZELONG')} / ${pcstring('FACE')}</td>
  <td class="h">${pcstring('HEIGHT')}</td>
  <td class="h">${pcstring('WEIGHT')}</td>
<#if (pcvar("COUNT[VISION]") > 0) >
  <td colspan="1" class="h">${pcstring('VISION')}</td>
<#else>
  <td colspan="1" class="h">Normal</td>
</#if>

 </tr>
 <tr>
  <td colspan="1" class="topline">CLASS</td>
  <td colspan="1" class="topline">EXPERIENCE</td>
  <td colspan="1" class="topline">RACE</td>
  <td class="topline">SIZE / FACE</td>
  <td class="topline">HEIGHT</td>
  <td class="topline">WEIGHT</td>
  <td colspan="1" class="topline">VISION</td>
 </tr>
 <tr>
  <td class="h">${pcstring('TOTALLEVELS')}</td>
  <td class="h">${pcstring('EXP.NEXT')}</td>
  <td class="h">${pcstring('AGE')}</td>
  <td class="h">${pcstring('GENDER')}</td>
  <td class="h">${pcstring('COLOR.EYE')}</td>
  <td colspan="2" class="h">${pcstring('COLOR.HAIR')}, ${pcstring('LENGTH.HAIR')}</td>
 </tr>
 <tr>
  <td class="topline">LEVEL</td>
  <td class="topline">NEXT LEVEL</td>
  <td class="topline">AGE</td>
  <td class="topline">GENDER</td>
  <td class="topline">EYES</td>
  <td colspan="2" class="topline">HAIR</td>
 </tr>
</table>
<!-- STOP Top Character Data -->
<!-- START Page Master Table -->
<table width="100%" border="0" cellpadding="0" cellspacing="0" summary="Page Master Table">
  <tr>
    <td rowspan="2" valign="top">
	<!-- START Abilities Table -->
	<table summary="Stat Block">
        <tr>
          <td align="center" width="20%" class="font6">ABILITY<br />NAME</td>
          <td align="center" width="20%" class="font6">ABILITY<br />SCORE</td>
          <td align="center" width="20%" class="font6">ABILITY<br />MODIFIER</td>
          <td align="center" width="20%" class="font6">TEMPORARY<br />SCORE</td>
          <td align="center" width="20%" class="font6">TEMPORARY<br />MODIFIER</td>
          </tr>
<@loop from=0 to=pcvar('COUNT[STATS]-1') ; stat , stat_has_next><#-- TODO: Loop was of early exit type 1 -->
        <tr>
          <td class="ab"><b>${pcstring('STAT.${stat}.NAME')}</b><br />
			<span class="font6">${pcstring('STAT.${stat}.LONGNAME')}</span></td>
          <td class="abb">${pcstring('STAT.${stat}.NOTEMP.NOEQUIP')}</td>
          <td class="abb">${pcstring('STAT.${stat}.MOD.NOTEMP.NOEQUIP')}</td>
          <td class="abt">${pcstring('STAT.${stat}')}</td>
          <td class="abt">${pcstring('STAT.${stat}.MOD')}</td>
          </tr>
</@loop>
    </table>
	<!-- STOP Abilities Table -->
      </td>
    <td colspan="3" valign="top">
<#if (pcvar("UseAlternateDamage") = 0 )>
<!-- START Hit Point Table -->
	<table summary="Hit Point Table">
        <tr>
          <td align="center" width="50"></td>
          <td align="center" width="25" valign="bottom"></td>
          <td align="center"></td>
          <td align="center" width="130" class="font6"><br />WOUNDS/CURRENT HP</td>
          <td align="center"></td>
          <td align="center" width="130" class="font6"><br />SUBDUAL DAMAGE</td>
          <td align="center"></td>
          <td align="center" width="130" class="font6"><br />DAMAGE<br />REDUCTION</td>
          <td align="center"></td>
          <td align="center" width="130" class="font6"><br />SPEED</td>
          </tr>
        <tr>
          <td align="center" bgcolor="black" style="color:white;"><span class="font9"><b>HP</b></span>
            <span class="font6" ><br />Hit Points</span></td>
          <td align="center" class="border9"><b>${pcstring('HP')}</b></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><br /></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><br /></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><b>&nbsp;${pcstring('DR')}&nbsp;</b></td>
          <td align="center"><br /></td>
          <td align="center" class="border9">
<@loop from=0 to=pcvar('COUNT[MOVE]-1') ; movement , movement_has_next><#-- TODO: Loop was of early exit type 1 -->
${pcstring('MOVE.${movement}.NAME')}&nbsp;${pcstring('MOVE.${movement}.RATE')}
<#if (pcstring("MOVE.${movement}.NAME") = "Fly")>
(${pcstring('ABILITYALL.Special Ability.HIDDEN.0.TYPE=Maneuverability.ASPECT.Maneuverability')})
</#if>
</@loop>
	    </td>
          </tr>
       </table>
<!-- STOP Hit Point Table -->
<#else>
<!-- START Vitality/Wound Point Table-->
       <table summary="Vitality/Wound Point Table">
        <tr>
          <td align="center" width="50"></td>
          <td align="center" width="25" valign="bottom"></td>
          <td align="center"></td>
          <td align="center" width="100" class="font6"><br />CURRENT VITALITY</td>
          <td align="center"></td>
          <td align="center" width="100" class="font6"><br />SUBDUAL DAMAGE</td>
          <td align="center"></td>
          <td align="center" width="50"></td>
          <td align="center" width="25" valign="bottom"></td>
          <td align="center"></td>
          <td align="center" width="75" class="font6"><br />CURRENT WP</td>
          <td align="center"></td>
          <td align="center" width="60" class="font6"><br />DAMAGE<br />REDUCTION</td>
          <td align="center"></td>
          <td align="center" width="120" class="font6"><br />SPEED</td>
          </tr>
        <tr>
          <td align="center" bgcolor="black"><font style="font-size:9pt" color="white"><b>VP</b></font>
            <font style="font-size:5pt" color="white"><br />Vitality</font></td>
          <td align="center" class="border9"><b>${pcstring('HP')}</b></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><br /></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><br /></td>
          <td align="center"><br /></td>
          <td align="center" bgcolor="black"><font style="font-size:9pt" color="white"><b>WP</b></span>
            <font style="font-size:5pt" color="white"><br />Wound Points</font></td>
          <td align="center" class="border9"><b>${pcstring('ALTHP')}</b></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><br /></td>
          <td align="center"><br /></td>
          <td align="center" class="border9"><b>&nbsp;${pcstring('DR')}&nbsp;</b></td>
          <td align="center"><br /></td>
          <td align="center" class="border10">
<@loop from=0 to=pcvar('COUNT[MOVE]-1') ; movement , movement_has_next><#-- TODO: Loop was of early exit type 1 -->
${pcstring('MOVE.${movement}.NAME')}&nbsp;${pcstring('MOVE.${movement}.RATE')}
<#if (pcstring("MOVE.${movement}.NAME") = "Fly")>
(${pcstring('ABILITYALL.Special Ability.HIDDEN.0.TYPE=Maneuverability.ASPECT.Maneuverability')})
</#if>
</@loop>
	    </td>
          </tr>
       </table>
<!-- STOP Vitality/Wound Point Table -->
</#if>
<!-- START Armor Class Table -->
      <table summary="AC Table">
        <tr>
          <td align="center" bgcolor="black"><font style="font-size:9pt" color="white"><b>AC</b></font>
            <font style="font-size:5pt" color="white"><br />Armour Class</font></td>
          <td align="center" class="border9"><b>${pcstring('AC.Total')}</b></td>
          <td align="center" class="font7"><b>:</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Touch')}</b></td>
          <td align="center" class="font7"><b>:</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Flatfooted')}</b></td>
          <td align="center" class="font7"><b>=</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Base')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Armor')}</b></td>

          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Shield')}</b></td>

          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Ability')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Size')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.NaturalArmor')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Dodge')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Deflection')}</b></td>
          <td align="center" class="font7"><b>+</b></td>
          <td align="center" class="border9"><b>${pcstring('AC.Misc')}</b></td>
          </tr>
        <tr>
          <td align="center" width="50"></td>
          <td align="center" width="25" valign="top"><font style="font-size: 6pt"><b>TOTAL</b></font></td>
          <td align="center"></td>
          <td align="center" width="25" valign="top"><font style="font-size: 6pt"><b>TOUCH</b></font></td>
          <td align="center"></td>
          <td align="center" width="25" valign="top"><font style="font-size: 6pt"><b>FLAT</b></font></td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">BASE</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">ARMOR<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">SHIELD<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">STAT<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">SIZE<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">NATURAL<br />ARMOR</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">DODGE<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">DEFLECTION<br />BONUS</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">MISC<br />BONUS</td>
          </tr>
        </table>
<!-- STOP AC Table -->
      </td>
    </tr>
  <tr>
  <td width="20%" valign="top">
<!-- START Initiative Table -->
   <table summary="Initiative Table">
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:9pt" color="white"><b>INITIATIVE</b></font><font style="font-size:5pt" color="white"><br />Modifier</font></td>
     <td align="center" class="border10"><b>${pcstring('INITIATIVEMOD')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('STAT.1.MOD')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('INITIATIVEBONUS')}</b></td>
     <td width="50%" rowspan="2" align="center"><br /></td>
    </tr>
    <tr>
     <td align="center" width="50" class="font7"></td>
     <td align="center" width="25" valign="top" class="font7">TOTAL</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">DEX</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">MISC</td>
    </tr>
   </table>
<!-- STOP Initiative Table -->
  </td>
  <td width="20%" valign="top" >
<!-- START Base Attack Table -->
   <table summary="Base Attack Table">
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:9pt" color="white"><b>BASE ATTACK</b></font><font style="font-size:5pt" color="white"><br />Bonus</font></td>
     <td align="center" width="94" class="border"><font style="font-size: small"><b>${pcstring('ATTACK.MELEE')}<br /></b></font></td>
     <td align="center" width="50%"><br /></td>
    </tr>
   </table>
<!-- STOP Base Attack Table -->
  </td>
  <td width="60%" valign="top">
<!-- START Misc Stat Table -->
      <table summary="Misc Stat Table">
        <tr>
          <td align="center" class="border9"><br /></td>
          <td align="center"></td>
          <td align="center" class="border9"><b>${pcstring('SPELLFAILURE')}</b></td>
          <td align="center"></td>
          <td align="center" class="border9"><b>${pcstring('ACCHECK')}</b></td>
          <td align="center"></td>
          <td align="center" class="border9"><b>${pcstring('MAXDEX')}</b></td>
          <td align="center"></td>
          <td align="center" class="border9"><b>${pcstring('SR')}</b></td>
          <td align="center"></td>
          <td align="center" class="border9"><b>&nbsp;</b></td>
          </tr>
        <tr>
          <td align="center" width="25" class="font6">MISS<br />CHANCE</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">ARCANE<br />FAILURE</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">ARMOR<br />CHECK</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">MAX<br />DEX</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">SPELL<br />RESIST.</td>
          <td align="center"></td>
          <td align="center" width="25" class="font6">TEMP</td>
          </tr>
        </table>
<!-- STOP Misc Stat Table -->
  </td>
  </tr>

 <tr>
  <td colspan="2" valign="top">
<!-- START Saving Throws Table -->
   <table width="100%" summary="Saving Throws">
    <tr>
     <td align="center" width="25" class="font6">PROFICIENT</td>
     <td align="center" class="font6">SAVING THROWS</td>
     <td align="center" width="25" class="font6">TOTAL</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">BASE</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">ABILITY</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">MAGIC</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">MISC</td>
     <td align="center"></td>
     <td align="center" width="25" class="font6">TEMPORARY</td>
    </tr>
<@loop from=0 to=pcvar('COUNT[CHECKS]-1') ; checks , checks_has_next>
    <tr>
	<#if (pcvar("CHECK.${checks}.BASE") > 0)>
		<td align="center" class="border10"><b>&#x25A0;</b></td>
	<#else>
		<td align="center" class="border10"><b>&#x274F;</b></td>
	</#if>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>${pcstring('CHECK.${checks}.NAME')}</b></font><font style="font-size:5pt" color="white"><br />
<#if (pcstring("CHECK.${checks}.NAME") = "Fortitude")>
Constitution
<#else>
</#if>
<#if (pcstring("CHECK.${checks}.NAME") = "Reflex")>
Dexterity
<#else>
</#if>
<#if (pcstring("CHECK.${checks}.NAME") = "Will")>
Wisdom
<#else>
</#if>
     </font></td>
     <td align="center" class="border10"><b>${pcstring('CHECK.${checks}.TOTAL')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('CHECK.${checks}.BASE')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('CHECK.${checks}.STATMOD')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('CHECK.${checks}.MAGIC')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('CHECK.${checks}.MISC.NOMAGIC.NOSTAT')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="tempborder"><br /></td>
    </tr>
</@loop>
   </table>
   <table width="100%" summary="Saving Throws">
     <tr>
	   <td align="left" valign="top" class="border8"><div class="font6">CONDITIONAL MODIFIERS:</div>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SaveBonus")-1') ; ability , ability_has_next>
	 ${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SaveBonus.ASPECT.SaveBonus')} <br />
</@loop>
    </td>
    </tr>
   </table>
<!-- STOP Saving Throws Table -->
<!-- START Melee and Range Attack Table -->
   <table width="100%" summary="Melee ~ Ranged Attacks">
    <tr>
     <td align="center"></td>
     <td align="center" width="60" valign="bottom" class="font6">TOTAL</td>
     <td align="center"></td>
     <td align="center" width="60" valign="bottom" class="font6">BASE ATTACK</td>
     <td align="center"></td>
     <td align="center" width="25" valign="bottom" class="font6">STAT</td>
     <td align="center"></td>
     <td align="center" width="25" valign="bottom" class="font6">SIZE</td>
     <td align="center"></td>
     <td align="center" width="25" valign="bottom" class="font6 epic">EPIC</td>
     <td align="center" class="epic"></td>
     <td align="center" width="25" valign="bottom" class="font6">MISC</td>
     <td align="center"></td>
     <td align="center" width="25" valign="bottom" class="font6">TEMP</td>
    </tr>
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>MELEE</b></font><font style="font-size:5pt" color="white"><br />ATTACK BONUS</font></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE.TOTAL')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE.STAT')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE.SIZE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10 epic"><b>${pcstring('VAR.charbonusto("COMBAT","EPICAB").INTVAL')}<br /></b></td>
     <td align="center" class="font7 epic"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE.MISC')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="tempborder"><br /></td>
    </tr>
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>RANGED</b></font><font style="font-size:5pt" color="white"><br />ATTACK BONUS</font></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.RANGED.TOTAL')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.RANGED')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.RANGED.STAT')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.RANGED.SIZE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10 epic"><b>${pcstring('VAR.charbonusto("COMBAT","EPICAB").INTVAL')}<br /></b></td>
     <td align="center" class="font7 epic"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.RANGED.MISC')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="tempborder"><br /></td>
    </tr>
<#if (pcvar("UseCombatManueverBonus") = 1)>
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>CMB</b></font><font style="font-size:5pt" color="white"><br />ATTACK BONUS</font></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB.INTVAL.SIGN')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.MELEE.BASE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_STAT.INTVAL.SIGN')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CM_SizeMod.INTVAL.SIGN')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10 epic"><b>+0<br /></b></td>
     <td align="center" class="font7 epic"><b>+</b></td>
     <td align="center" class="border10"><b>+0<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="tempborder"><br /></td>
    </tr>
   </table>
   <table width="100%" summary="CMB block">
<#if (pcvar("CMD") > 0)>
   <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>CMB</b></font></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Trip.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Disarm.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Sunder.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_BullRush.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Overrun.INTVAL.SIGN')}</b></td>
   </tr>
   <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>CMD</b></font></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMD_Grapple.INTVAL')}</b></td>
     <td align="center" class="border10"><b>
<#if (pcvar("CantBeTripped") != 0)>
Immune
<#else>
${pcstring('VAR.CMD_Trip.INTVAL')}
</#if>
	</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMD_Disarm.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMD_Sunder.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMD_BullRush.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMD_Overrun.INTVAL')}</b></td>
    </tr>
<#else>
   <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>OFFENSE</b></font></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Grapple.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Trip.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Disarm.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Sunder.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Bull.INTVAL.SIGN')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Overrun.INTVAL.SIGN')}</b></td>
    </tr>
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>DEFENSE</b></font></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Grapple_DEF.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Trip_DEF.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Disarm_DEF.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Sunder_DEF.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Bull_DEF.INTVAL')}</b></td>
     <td align="center" class="border10"><b>${pcstring('VAR.CMB_Overrun_DEF.INTVAL')}</b></td>
    </tr>
</#if>
    <tr>
     <td align="center" width="28%"></td>
     <td align="center" width="12%" valign="bottom" class="font6">GRAPPLE</td>
     <td align="center" width="12%" valign="bottom" class="font6">TRIP</td>
     <td align="center" width="12%" valign="bottom" class="font6">DISARM</td>
     <td align="center" width="12%" valign="bottom" class="font6">SUNDER</td>
     <td align="center" width="12%" valign="bottom" class="font6">BULL RUSH</td>
     <td align="center" width="12%" valign="bottom" class="font6">OVERRUN</td>
    </tr>
   </table>
   <br />
<#else>
    <tr>
     <td align="center" bgcolor="black"><font style="font-size:10pt" color="white"><b>GRAPPLE</b></font><font style="font-size:5pt" color="white"><br />ATTACK BONUS</font></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.GRAPPLE.TOTAL')}</b></td>
     <td align="center" class="font7"><b>=</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.GRAPPLE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.GRAPPLE.STAT')}</b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.GRAPPLE.SIZE')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="border10 epic"><b>${pcstring('ATTACK.GRAPPLE.EPIC')}<br /></b></td>
     <td align="center" class="font7 epic"><b>+</b></td>
     <td align="center" class="border10"><b>${pcstring('ATTACK.GRAPPLE.MISC')}<br /></b></td>
     <td align="center" class="font7"><b>+</b></td>
     <td align="center" class="tempborder"><br /></td>
    </tr>
    <tr><td></td></tr>
   </table>
</#if>
   <table width="100%" summary="Saving Throws">
     <tr>
	   <td align="left" valign="top" class="border8"><div class="font6">CONDITIONAL MODIFIERS:</div>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=CombatBonus")-1') ; ability , ability_has_next>
	 ${pcstring('ABILITYALL.ANY.${ability}.ASPECT=CombatBonus.ASPECT.CombatBonus')} <br />
</@loop>
    </td>
    </tr>
   </table>
<!-- STOP Melee and Range Attack Table -->

<!-- Add Martial Arts and Natural Attack Block Here -->

<!-- START Unarmed Attack Table
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Unarmed Attack">
    <tr>
     <td align="center" height="25" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>UNARMED</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>TOTAL ATTACK BONUS</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>DAMAGE</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>CRITICAL</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>REACH</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border"><font style="font-size:8pt" color="black"><b>${pcstring('WEAPONH.TOTALHIT')}<br /></b></font></td>
     <td align="center" bgcolor="white" class="border"><font style="font-size:8pt" color="black"><b>${pcstring('WEAPONH.DAMAGE')}<br /></b></font></td>
     <td align="center" bgcolor="white" class="border"><font style="font-size:8pt" color="black"><b>${pcstring('WEAPONH.CRIT')}/x${pcstring('WEAPONH.MULT')}<br /></b></font></td>
     <td align="center" bgcolor="white" class="border"><font style="font-size:8pt" color="black"><b>${pcstring('REACH')}<br /></b></font></td>
    </tr>
   </table>
   <font style="font-size:2pt"><br /></font>	 -->
<!-- STOP Unarmed Attack Table -->




<#macro weaponHandedToHitDmgTable weap>
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Weapon Table">
    <tr>
     <td align="center" height="15" bgcolor="black" width="8%"></td>
     <td align="right" height="15" bgcolor="black" width="17%"><font style="font-size:8pt" color="white"><b>TO HIT</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.BASEHIT')}<br /></b></td>
     <td align="right" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>DAMAGE</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.BASICDAMAGE')}<br /></b></td>

    </tr>
    <tr>
    </tr>
    <#if (pcstring('WEAPON.${weap}.SPROP') != "")>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Special Properties</b></font></td>
     <td align="left" bgcolor="white" class="border8" colspan="6"><b>&nbsp;${pcstring('WEAPON.${weap}.SPROP')}<br /></b></td>
    </tr>
    </#if>
   </table>
</#macro>

<#macro weaponBlock weap>
   <table cellpadding="0" width="100%" cellspacing="0" border="0" summary="Weapon Table">
	<#assign weaponCategory>
		${pcstring('WEAPON.${weap}.CATEGORY')?lower_case}
	</#assign>
   	<#-- Weapon ${weap} is ${pcstring("WEAPON.${weap}.NAME")} Cat: ${weaponCategory}  -->
<#if (weaponCategory?contains('both'))>
<#if (weaponCategory?contains('ranged'))>
    <tr>
     <td align="left" height="15" bgcolor="black" width="8%"><font style="font-size:8pt" color="white"><b>&nbsp;&nbsp;Range</b></font></td>
     <td width="18%" align="center" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>${pcstring('WEAPON.${weap}.RANGELIST.1')}'<br /></b></font></td>
     <td width="18%" align="center" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>${pcstring('WEAPON.${weap}.RANGELIST.3')}'<br /></b></font></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Bonus</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.TOTALHIT')}<br /></b></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.TOTALHIT')} (Disadvantage)<br /></b></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Dam</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')}</b></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')}</b></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Ammunition Used<br /></b></font></td>
     <td align="center" valign="bottom" bgcolor="white" class="border" colspan="6"><font style="font-size: x-small">&#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744;</font></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Special Properties<br /></b></font></td>
     <td align="left" bgcolor="white" class="border8" colspan="6"><b>&nbsp;${pcstring('WEAPON.${weap}.SPROP')}<br /></b></td>
    </tr>
   </table>
   <font style="font-size:2pt"><br /></font>
<#else>
    <tr>
     <td align="center" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>${pcstring('WEAPON.${weap}.NAME')}<br /></b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>HAND</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>TYPE</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>SIZE</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>CRITICAL</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>REACH</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border8 font8"><b>${pcstring('WEAPON.${weap}.HAND')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8 font8"><b>${pcstring('WEAPON.${weap}.TYPE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8 font8"><b>${pcstring('WEAPON.${weap}.SIZE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8 font8"><b>${pcstring('WEAPON.${weap}.CRIT')}/x${pcstring('WEAPON.${weap}.MULT')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8 font8"><b>${pcstring('WEAPON.${weap}.REACH')}${pcstring('WEAPON.${weap}.REACHUNIT')}<br /></b></td>
    </tr>
   </table>
   <@weaponHandedToHitDmgTable weap=weap />
</#if>
<#else>
<#if (weaponCategory?contains('ranged'))>
    <tr>
     <td align="center" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>${pcstring('WEAPON.${weap}.NAME')}<br /></b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>HAND</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>TYPE</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>SIZE</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>CRITICAL</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.HAND')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.TYPE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.SIZE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.CRIT')}/x${pcstring('WEAPON.${weap}.MULT')}<br /></b></td>
    </tr>
   </table>
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Weapon Table">
<#if (pcstring("WEAPON.${weap}.CONTENTS") = "0")>
    <tr>
     <td align="left" height="15" bgcolor="black" width="8%"><font style="font-size:8pt" color="white"><b>&nbsp;&nbsp;Range</b></font></td>
     <td width="18%" align="center" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>${pcstring('WEAPON.${weap}.RANGELIST.1')}'<br /></b></font></td>
     <td width="18%" align="center" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>${pcstring('WEAPON.${weap}.RANGELIST.4')}'<br /></b></font></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Bonus</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.1.TOTALHIT')}<br /></b></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.1.TOTALHIT')} (Diadvantage)<br /></b></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Dam</b></font></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')}</b></td>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.0.DAMAGE')}</b></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Ammunition Used<br /></b></font></td>
     <td align="center" valign="bottom" bgcolor="white" class="border" colspan="6"><font style="font-size: x-small">&#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744;</font></td>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Special Properties<br /></b></font></td>
     <td align="left" bgcolor="white" class="border8" colspan="6"><b>&nbsp;${pcstring('WEAPON.${weap}.SPROP')}<br /></b></td>
    </tr>
   </table>
   <font style="font-size:2pt"><br /></font>
<#else>
<@loop from=0 to=pcvar('WEAPON.${weap}.CONTENTS-1') ; ammo , ammo_has_next>
    <tr>
     <td colspan="6" align="left" height="15" bgcolor="black" width="8%"><font style="font-size:8pt" color="white"><b>&nbsp;AMMUNITION:  ${pcstring('WEAPON.${weap}.CONTENTS.${ammo}')}
<#if (pcstring("WEAPON.${weap}.CONTENTS.${ammo}.SPROP") = " ")>
(${pcstring('WEAPON.${weap}.CONTENTS.${ammo}.SPROP')})
</#if>
     </b></font></td>
    </tr>
    <tr>
     <td align="left" height="15" bgcolor="black" width="8%"><font style="font-size:8pt" color="white"><b>&nbsp;Range</b></font></td>
<@loop from=0 to=4 ; range , range_has_next>
     <td width="18%" align="center" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>${pcstring('WEAPON.${weap}.RANGELIST.${range}')}'<br /></b></font></td>
</@loop>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Bonus</b></font></td>
<@loop from=0 to=4 ; range1 , range1_has_next>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.${range1}.CONTENTS.${ammo}.TOTALHIT')}<br /></b></td>
</@loop>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border"><font style="font-size:8pt" color="white"><b>&nbsp;Dam</b></font></td>
<@loop from=0 to=4 ; range2 , range2_has_next>
     <td align="center" bgcolor="white" class="border7"><b>${pcstring('WEAPON.${weap}.RANGELIST.${range2}.CONTENTS.${ammo}.DAMAGE')}</b></td>
</@loop>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Ammunition Used<br /></b></font></td>
     <td align="center" valign="bottom" bgcolor="white" class="border" colspan="6"><font style="font-size: x-small">&#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744; &#9744;&#9744;&#9744;&#9744;&#9744;</font></td>
</@loop>
    </tr>
    <tr>
     <td align="left" bgcolor="black" class="border" colspan="1"><font style="font-size:8pt" color="white"><b>&nbsp;Special Properties<br /></b></font></td>
     <td align="left" bgcolor="white" class="border8" colspan="6"><b>&nbsp;${pcstring('WEAPON.${weap}.SPROP')}<br /></b></td>
    </tr>
   </table>
   <font style="font-size:2pt"><br /></font>
</#if>
<#else>
	<#if (pcboolean("WEAPON.${weap}.ISTYPE.Double") || weaponCategory?contains('non-standard-melee') || weaponCategory?contains('natural'))>
    <tr>
     <td align="center" height="15" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>${pcstring('WEAPON.${weap}.NAME')}<br /></b></font></td>
     <td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>TOTAL ATTACK BONUS</b></font></td>
     <td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>DAMAGE</b></font></td>
     <td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>CRITICAL</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.TOTALHIT')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.DAMAGE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.CRIT')}/x${pcstring('WEAPON.${weap}.MULT')}<br /></b></td>
    </tr>
   </table>
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Weapon Table">
    <tr>
     <td align="center" height="15" bgcolor="black" width="15%"><font style="font-size:6pt" color="white"><b>HAND</b></font></td>
     <td align="center" height="15" bgcolor="black" width="15%"><font style="font-size:6pt" color="white"><b>RANGE</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>TYPE</b></font></td>
     <td align="center" bgcolor="black" width="15%" height="15"><font style="font-size:6pt" color="white"><b>SIZE</b></font></td>
     <td align="center" bgcolor="black" width="40%" height="15"><font style="font-size:6pt" color="white"><b>SPECIAL PROPERTIES</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.HAND')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.RANGE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.TYPE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.SIZE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>&nbsp;${pcstring('WEAPON.${weap}.SPROP')}<br /></b></td>
    </tr>
   </table>
   <font style="font-size:2pt"><br /></font>
	<#else>
    <tr>
     <td align="center" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>${pcstring('WEAPON.${weap}.NAME')}<br /></b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>HAND</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>TYPE</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>SIZE</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>CRITICAL</b></font></td>
     <td align="center" bgcolor="black" width="12%" height="15"><font style="font-size:6pt" color="white"><b>REACH</b></font></td>
    </tr>
    <tr>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.HAND')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.TYPE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.SIZE')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.CRIT')}/x${pcstring('WEAPON.${weap}.MULT')}<br /></b></td>
     <td align="center" bgcolor="white" class="border8"><b>${pcstring('WEAPON.${weap}.REACH')}${pcstring('WEAPON.${weap}.REACHUNIT')}<br /></b></td>
    </tr>
   </table>
   <@weaponHandedToHitDmgTable weap=weap />
   <font style="font-size:2pt"><br /></font>
	</#if>
</#if>
</#if>
</#macro>


<!-- START Weapon Table -->
<@loop from=0 to=2 ; weap , weap_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("WEAPON.${weap}.NAME") != "") >
  <@weaponBlock weap=weap />
</#if>
</@loop>
<#assign nextWeaponIdx = 3 />
<#-- If the 3rd weapon was a 'both' weapon, tack its ranged block on this page -->
<#if (pcstring("WEAPON.3.NAME") != "") >
	<#assign weaponCategory>
		${pcstring('WEAPON.3.CATEGORY')?lower_case}
	</#assign>
	<#if (weaponCategory?contains('both') && weaponCategory?contains('ranged'))>
		<@weaponBlock weap=3 />
		<#assign nextWeaponIdx = 4 />
	</#if>
</#if>

   <table cellpadding="0" width="100%" cellspacing="0" border="0" summary="Weapon Abbreviations">
    <tr>
     <td><font style="font-size:5pt"><b>1H-P:</b> One handed, primary hand.&nbsp;<b>1H-O:</b> One handed, off hand.&nbsp;<b>2H:</b> Two handed.&nbsp;<b>2W-P-(OH):</b> 2 weapons, primary hand (off hand weapon is heavy).&nbsp;<b>2W-P-(OL):</b> 2 weapons, primary hand (off hand weapon is light).&nbsp;<b>2W-OH:</b>2 weapons, off hand.</font></td>
    </tr>
   </table>
<!-- STOP Weapon Table -->
<!-- START Armor Table -->
<@loop from=0 to=2 ; count , count_has_next>
<#if (pcstring("ARMOR.${count}.NAME") != "") >
<table cellpadding="0" cellspacing="0" width="100%" border="0" summary="Armor Table">
<tr><td align="center" height="20" bgcolor="black" rowspan="2" width="40%"><font style="font-size:10pt" color="white"><b>${pcstring("ARMOR.EQUIPPED.${count}.NAME")}<br /></b></font></td><td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>TYPE</b></font></td><td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>ARMOR BONUS</b></font></td><td align="center" bgcolor="black" width="20%" height="15"><font style="font-size:6pt" color="white"><b>MAX DEX BONUS</b></font></td></tr>
<tr><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.TYPE")}<br /></b></td><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.TOTALAC")}<br /></b></td><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.MAXDEX")}<br /></b></td></tr>
</table>
<table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Armor Table">
<tr><td align="center" height="15" bgcolor="black" width="60"><font style="font-size:5pt" color="white"><b>CHECK PENALTY</b></font></td><td align="center" bgcolor="black" width="60" height="15"><font style="font-size:5pt" color="white"><b>SPELL FAILURE</b></font></td><td align="center" bgcolor="black" width="237" height="15"><font style="font-size:6pt" color="white"><b>SPECIAL PROPERTIES</b></font></td></tr>
<tr><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.ACCHECK")}<br /></b></td><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.SPELLFAIL")}<br /></b></td><td align="center" bgcolor="white" class="border8"><b>${pcstring("ARMOR.EQUIPPED.${count}.SPROP")}<br /></b></td></tr>
</table>
</#if>
</@loop>
</td>
<!-- STOP Armor Table -->

 <td colspan="2" valign="top">
<#if (pcvar("VAR.TOTALPOWERPOINTS") >= 1) >
<!-- START PSI Power Points Table -->
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Power Point Table">
    <tr>
     <td colspan="6" bgcolor="black" align="center"><font style="font-size: small" color="white"><b>PSI POWER POINTS</b></font></td>
    </tr>
    <tr>
     <td bgcolor="#000000"><font color="#FFFFFF" style="font-size:9pt"><b>&nbsp;Base PP</b></font></td>
     <td bgcolor="#FFFFFF" class="border" align="center"><font style="font-size:9pt">${pcstring('VAR.BASEPOWERPOINTS.INTVAL')}</font></td>
     <td bgcolor="#000000"><font color="#FFFFFF" style="font-size:9pt"><b>&nbsp;Bonus PP</b></font></td>
     <td bgcolor="#FFFFFF" class="border" align="center"><font style="font-size:9pt">${pcstring('VAR.BONUSPOWERPOINTS.INTVAL')}</font></td>
     <td bgcolor="#000000"><font color="#FFFFFF" style="font-size:9pt"><b>&nbsp;Total PP</b></font></td>
     <td bgcolor="#FFFFFF" class="border" align="center"><font style="font-size:9pt">${pcstring('VAR.TOTALPOWERPOINTS.INTVAL')}</font></td>
    </tr>
    <tr>
     <td>&nbsp;</td>
    </tr>
   </table>
<!-- STOP PSI Power Points Table -->
</#if>
<!-- START Skills Table -->
   <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Skills Table">
    <tr>
     <td height="30" bgcolor="black"></td>
     <td height="30" bgcolor="black" align="center"><font style="font-size: small" color="white"><b>SKILLS</b></font></td>
     <td colspan="4" height="30" bgcolor="black" align="center"></td>
     <td height="30" bgcolor="black" align="right" ></td>
     <td height="30" bgcolor="black" align="center"><font style="font-size: x-small" color="white" >MAX<br />RANKS</font></td>
     <td colspan="2" height="30"  bgcolor="white" align="center" class="skl"><b>${pcstring('MAXSKILLLEVEL')}/${pcstring('MAXCCSKILLLEVEL')}</b></td>
    </tr>
    <tr>
     <td colspan="2" align="center" width="40%" class="border6">SKILL NAME</td>
     <td align="center" width="5%" class="border6">ABILITY</td>
     <td align="center" width="13%" colspan="1" class="border6">SKILL<br />MODIFIER</td>
     <td align="center" width="13%" colspan="2" class="border6">ABILITY<br />MODIFIER</td>
     <td align="center" width="13%" colspan="2" class="border6">PROFICIENCY <br />MODIFIER</td>
     <td align="center" width="13%" colspan="2" class="border6">MISC<br />MODIFIER</td>
    </tr>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","CATEGORY=SkillExport","TYPE=SkillExport")-1') ; ability , ability_has_next>
<#if (ability % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td align="left" ><font style="font-size: x-small"><#if (pcvar("ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.ProfBonus") >= 1)>&#9670;</#if></font></td>	
     <td align="left" class="font8">&nbsp;&nbsp;${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport')}</td>
     <td align="center" class="font8">${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.KEYSTAT')}</td>
     <td align="center" class="borderbottom8" valign="bottom"><b>${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.Total')}</b></td>
     <td align="center" valign="bottom" class="font8"><b>=</b></td>
     <td align="center" class="borderbottom8" valign="bottom">${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.AbilityMod')}<br /></td>
     <td align="center" valign="bottom" class="font8"><b>+</b></td>
     <td align="center" class="borderbottom8" valign="bottom">${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.ProfBonus')}<br /></td>
     <td align="center" valign="bottom" class="font8"><b>+</b></td>
     <td align="center" class="borderbottom8" valign="bottom">${pcstring('ABILITYALL.SkillExport.${ability}.TYPE=SkillExport.ASPECT.MiscBonus')}<br /></td>
    </tr>
</@loop>
   </table>
<div class="font6">&#9670; = Proficient</div>
<div class="font7"></div>
   <table width="100%" summary="Saving Throws">
     <tr>
	   <td align="left" valign="top" class="border8"><div class="font6">CONDITIONAL MODIFIERS:</div>
<@loop from=0 to=pcvar('countdistinct("ABILITIES","ASPECT=SkillBonus")-1') ; ability , ability_has_next>
	 ${pcstring('ABILITYALL.ANY.${ability}.ASPECT=SkillBonus.ASPECT.SkillBonus')}<br />
</@loop>
    </td>
    </tr>
   </table>

<!-- STOP Skills Table -->
  </td>
 </tr>
</table>
<hr /><center><font style="font-size: x-small">PCGen Character Template by ROG, mods/maint by Arcady, Barak &amp; Dimrill.  For suggestions please post to pcgen@yahoogroups.com with "OS Suggestion" in the subject line.</font></center>
<!-- ================================================================ -->
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>

<table width="100%" border="0" summary="Page 2 Master Table"> <!-- Master Page Table -->
 <tr>
  <td colspan="2" width="100%" valign="top">
<!-- START Equipment Table -->
<#macro equipmentRow equip1>
 <#if (equip1 % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
        <td valign="top" class="border8">&nbsp;${pcstring("EQ.Not.Coin.NOT.Gem.${equip1}.NAME.MAGIC~<b>~</b>")}<br />
 <#if (pcstring("EQ.Not.Coin.NOT.Gem.${equip1}.SPROP") != "")>
        <font style="font-size: 5pt">(${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.SPROP')})</font>
 </#if>
 <#if (pcstring("EQ.Not.Coin.NOT.Gem.${equip1}.NOTE") != "")>
        <span class="lt5">(${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.NOTE')})</span><br />
 </#if>
 <@loop from=1 to=pcvar('EQ.Not.Coin.NOT.Gem.${equip1}.CHARGES') ; charges , charges_has_next>
        <font style="font-size: x-small">&#9744;</font>
 </@loop>

 <#assign eqType = pcstring("EQ.Not.Coin.NOT.Gem.${equip1}.TYPE")?lower_case />
 <#if (eqType?contains("consumable") || eqType?contains("potion") || eqType?contains("ammunition")) >
   <@loop from=1 to=pcvar('EQ.Not.Coin.NOT.Gem.${equip1}.CHECKBOXES') ; consumable , consumable_has_next>
        <font style="font-size: x-small">&#9744;</font>
   </@loop>
 </#if>
        </td>
        <td valign="top" class="border8" align="center">${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.LOCATION')}<br /></td>
        <td valign="top" class="border8" align="center">${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.QTY')}<br /></td>
        <td valign="top" class="border8" align="center">${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.WT')}<br /></td>
        <td valign="top" class="border8" align="center">${pcstring('EQ.Not.Coin.NOT.Gem.${equip1}.COST')}<br /></td>
       </tr>
</#macro>
   <table width="100%" cellspacing="0" cellpadding="0" border="0" summary="Master Equipment Table"> <!-- Master Equipment Table -->
    <tr>
     <td bgcolor="black" align="center" colspan="10"><font color="white" style="font-size: small"><b>EQUIPMENT</b></font></td>
    </tr>
    <tr>
     <td width="50%" valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0" summary="Equipment Table Left Column"> <!-- Equipment Table left pane -->
       <tr>
        <td valign="top" width="70%" class="border8"><b>ITEM</b></td>
        <td valign="top" width="12%" class="border8" align="center"><b>LOCATION</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>QTY</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>WT.</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>COST</b></td>
       </tr>
<@loop from=0 to=(pcvar("COUNT[EQUIPMENT.Not.Coin.NOT.Gem]")-1)/2 ; equip1, equip1_has_next >
	<@equipmentRow equip1=equip1 />
</@loop>
      </table>
     </td>
     <td width="50%" valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0" summary="Equipment Table Right Column"> <!-- Equipment Table right pane -->
       <tr>
        <td valign="top" width="70%" class="border8"><b>ITEM</b></td>
        <td valign="top" width="12%" class="border8" align="center"><b>LOCATION</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>QTY</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>WT.</b></td>
        <td valign="top" width="6%" class="border8" align="center"><b>COST</b></td>
       </tr>
<@loop from=((pcvar("COUNT[EQUIPMENT.Not.Coin.NOT.Gem]")-1)/2)+1 to=pcvar("COUNT[EQUIPMENT.Not.Coin.NOT.Gem]")-1 ; equip2, equip2_has_next >
	<@equipmentRow equip1=equip2 />
</@loop>
       <tr>
        <td valign="top" width="41%" class="border" align="right" colspan="1"><font style="font-size: x-small">TOTAL WEIGHT CARRIED/VALUE&nbsp;&nbsp;</font></td>
        <td valign="top" width="3%" class="border" align="center" colspan="2" nowrap><font style="font-size: x-small">${pcstring('TOTAL.WEIGHT')}</font></td>
        <td valign="top" width="3%" class="border" align="center" colspan="2" nowrap><font style="font-size: x-small">${pcstring('TOTAL.VALUE')}</font></td>
       </tr>
      </table>
     </td>
    </tr>
   </table>
<!-- STOP Equipment Table -->
   </td>
  </tr>
  <tr>
   <td width="50%" valign="top">
<!-- START Weight Table -->
    <table width="100%" cellspacing="0" cellpadding="3" summary="Weight Allowance Table">
     <tr>
      <td bgcolor="black" align="center" colspan="6"><font color="white" style="font-size: small"><b>WEIGHT ALLOWANCE</b></font></td>
     </tr>
     <tr>
      <td valign="top" class="border8" align="right"><b>Light</b></td>
      <td valign="top" class="border8">${pcstring('WEIGHT.LIGHT')}</td>
      <td valign="top" class="border8" align="right"><b>Medium</b></td>
      <td valign="top" class="border8">${pcstring('WEIGHT.MEDIUM')}</td>
      <td valign="top" class="border8" align="right"><b>Heavy</b></td>
      <td valign="top" class="border8">${pcstring('WEIGHT.HEAVY')}</td>
     </tr>
    </table>
<!-- STOP Weight Table -->
<!-- START Illumination Table -->
<#if (pcvar("COUNT[EQTYPE.LightSource]") > 0)>
<br/>
    <table width="100%" cellspacing="0" cellpadding="3" summary="Illumination">
     <tr>
      <td bgcolor="black" align="center" colspan="4"><font color="white" style="font-size: small"><b>ILLUMINATION</b></font></td>
     </tr>
     <tr>
      	<td width="45%" class="border8"><b>Light Source</b></td>
      	<td width="15%" class="border8"><b>Bright</b></td>
       	<td width="15%" class="border8"><b>Shadowy</b></td>
     	<td width="15%" class="border8"><b>Duration</b></td>
     </tr>
<@loop from=0 to=pcvar('COUNT[EQTYPE.LightSource]-1') ; light , light_has_next>
<#if (light % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
      	<td class="border8">${pcstring('EQTYPE.LightSource.${light}.NAME')}</td>
      	<td class="border8">${pcstring('EQTYPE.LightSource.${light}.QUALITY.Bright Illumination')}</td>
       	<td class="border8">${pcstring('EQTYPE.LightSource.${light}.QUALITY.Shadowy Illumination')}</td>
     	<td class="border8">${pcstring('EQTYPE.LightSource.${light}.QUALITY.Duration')}</td>
     </tr>
</@loop>
    </table>
</#if>
<!-- STOP Illumination Table -->
<!-- START Money Table -->
<br />
    <table width="100%" cellspacing="0" cellpadding="3" summary="Money">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>MONEY</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border"><font style="font-size: x-small">
<@loop from=0 to=pcvar('COUNT[EQTYPE.Coin]')-1 ; count , count_has_next>
  ${pcstring('EQTYPE.Coin.${count}.NAME')}: ${pcstring('EQTYPE.Coin.${count}.QTY')}<br/>
</@loop>
<@loop from=0 to=pcvar('COUNT[EQTYPE.Gem]')-1 ; count , count_has_next>
  ${pcstring('EQTYPE.Gem.%.QTY')}x${pcstring('EQTYPE.Gem.${count}.NAME')} (${pcstring('EQTYPE.Gem.${count}.COST')})<br/>
</@loop>
<span class="notes">${pcstring('MISC.FUNDS')} Unspent Funds = ${pcstring('GOLD.TRUNC')}</span></font></td>
     </tr>
    </table>
<!-- STOP Money Table -->
    <font style="font-size:2pt"><br /></font>
<!-- START Companions Tables -->
<#if (pcvar("FOLLOWERTYPE.Familiar") > 0) >
    <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Familiar Table">
     <tr>
      <td bgcolor="black" align="left" class="border" colspan="12"><font color="white" style="font-size: medium"><b>Familiar: ${pcstring('FOLLOWERTYPE.FAMILIAR.0.NAME')} (${pcstring('FOLLOWERTYPE.FAMILIAR.0.RACE')})</b></font></td>
     </tr>
     <tr>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>FORT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.CHECK.FORTITUDE.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>REF:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.CHECK.REFLEX.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>WILL:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.CHECK.2.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>HP:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.HP')}</td>
      <td width="8%" bgcolor="black" align="center" class="border" ><font color="white" style="font-size: x-small"><b>AC:</b></font></td>
      <td width="8%" align="center" class="border" >${pcstring('FOLLOWERTYPE.FAMILIAR.0.AC.Total')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>INIT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.INITIATIVEMOD')}</td>
     </tr>
<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FAMILIAR.0.EQTYPE.WEAPON]-1') ; wep , wep_has_next><#-- TODO: Loop was of early exit type 1 -->
     <tr>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>${pcstring('FOLLOWERTYPE.FAMILIAR.0.WEAPON.${wep}.NAME')}</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.WEAPON.${wep}.TOTALHIT')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>DAMAGE:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.WEAPON.${wep}.DAMAGE')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>CRITICAL:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.FAMILIAR.0.WEAPON.${wep}.CRIT')}/x${pcstring('FOLLOWERTYPE.FAMILIAR.0.WEAPON.${wep}.MULT')}</td>
      </tr>
</@loop>
     <tr>
      <td bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>Special:</b></font></td>
      <td align="left" class="border" colspan="12"><font style="font-size: small">&nbsp;&nbsp;${pcstring('FOLLOWERTYPE.FAMILIAR.0.SPECIALLIST')}</font></td>
     </tr>
     <tr>
     </tr>
    </table>
   <font style="font-size:2pt"><br /></font>
</#if>
<#if (pcvar("FOLLOWERTYPE.SPECIAL MOUNT") > 0) >
    <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Special Mount Table">
     <tr>
      <td bgcolor="black" align="left" class="border" colspan="12"><font color="white" style="font-size: medium"><b>Special Mount: ${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.NAME')} (${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.RACE')})</b></font></td>
     </tr>
     <tr>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>FORT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.CHECK.FORTITUDE.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>REF:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.CHECK.REFLEX.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>WILL:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.CHECK.2.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>HP:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.HP')}</td>
      <td width="8%" bgcolor="black" align="center" class="border" ><font color="white" style="font-size: x-small"><b>AC:</b></font></td>
      <td width="8%" align="center" class="border" >${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.AC.Total')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>INIT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.INITIATIVEMOD')}</td>
     </tr>
<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.SPECIAL MOUNT.0.EQTYPE.WEAPON]-1') ; wep , wep_has_next><#-- TODO: Loop was of early exit type 1 -->
     <tr>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${wep}.NAME')}</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${wep}.TOTALHIT')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>DAMAGE:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${wep}.DAMAGE')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>CRITICAL:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${wep}.CRIT')}/x${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.WEAPON.${wep}.MULT')}</td>
      </tr>
</@loop>
     <tr>
      <td bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>Special:</b></font></td>
      <td align="left" class="border" colspan="12"><font style="font-size: small">&nbsp;&nbsp;${pcstring('FOLLOWERTYPE.SPECIAL MOUNT.0.SPECIALLIST')}</font></td>
     </tr>
     <tr>
     </tr>
    </table>
   <font style="font-size:2pt"><br /></font>
</#if>
<#if (pcvar("FOLLOWERTYPE.ANIMAL COMPANION") > 0) >
<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.ANIMAL COMPANION]-1') ; anm , anm_has_next>
    <table cellpadding="0" cellspacing="0" border="0" width="100%" summary="Animal Companion Table">
     <tr>
      <td bgcolor="black" align="left" class="border" colspan="12"><font color="white" style="font-size: medium"><b>Animal Companion: ${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.NAME')} (${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.RACE')})</b></font></td>
     </tr>
     <tr>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>FORT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.CHECK.FORTITUDE.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>REF:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.CHECK.REFLEX.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>WILL:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.CHECK.2.TOTAL')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>HP:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.HP')}</td>
      <td width="8%" bgcolor="black" align="center" class="border" ><font color="white" style="font-size: x-small"><b>AC:</b></font></td>
      <td width="8%" align="center" class="border" >${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.AC.Total')}</td>
      <td width="8%" bgcolor="black" align="center" class="border"><font color="white" style="font-size: x-small"><b>INIT:</b></font></td>
      <td width="8%" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.INITIATIVEMOD')}</td>
     </tr>
<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.ANIMAL COMPANION.${anm}.EQTYPE.WEAPON]-1') ; wep , wep_has_next><#-- TODO: Loop was of early exit type 1 -->
     <tr>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.WEAPON.${wep}.NAME')}</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.WEAPON.${wep}.TOTALHIT')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>DAMAGE:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.WEAPON.${wep}.DAMAGE')}</td>
      <td colspan="2" bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>CRITICAL:</b></font></td>
      <td colspan="2" align="center" class="border">${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.WEAPON.${wep}.CRIT')}/x${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.WEAPON.${wep}.MULT')}</td>
      </tr>
</@loop>
     <tr>
      <td bgcolor="black" align="center" class="border"><font color="white" style="font-size: small"><b>Special:</b></font></td>
      <td align="left" class="border" colspan="11"><font style="font-size: small">&nbsp;&nbsp;${pcstring('FOLLOWERTYPE.ANIMAL COMPANION.${anm}.SPECIALLIST')}<br /></font></td>
     </tr>
    </table>
   <font style="font-size:2pt"><br /></font>
</@loop>
<!-- STOP Companions Table1 -->
</#if>
<#if (pcvar("FOLLOWERTYPE.FOLLOWERS") > 0) >
    <table width="100%" cellspacing="0" cellpadding="3" summary="Henchmen Table">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>HENCHMEN</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border8">
<@loop from=0 to=pcvar('COUNT[FOLLOWERTYPE.FOLLOWERS]-1') ; follower , follower_has_next><#-- TODO: Loop was of early exit type 1 -->
${pcstring('FOLLOWERTYPE.FOLLOWERS.${follower}.NAME')},&nbsp;
</@loop>
      </td>
     </tr>
    </table>
<!-- STOP Companions Table1 -->
</#if>
<#if (pcvar("MISC.COMPANIONS") > 0) >
<!-- START Misc Companions Table -->
    <table width="100%" cellspacing="0" cellpadding="3" summary="Misc. Magic Table">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>OTHER COMPANIONS</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border8"><span class="notes">${pcstring('MISC.COMPANIONS')}<span class="notes"></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
<!-- STOP Misc Companions Table -->
</#if>
<!-- START Template Table -->
<#if (pcstring("TEMPLATELIST") != '') >
    <table width="100%" cellspacing="0" cellpadding="3" summary="Template Table">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>TEMPLATES</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border8">${pcstring('TEMPLATELIST')}<br /></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
</#if>
<!-- STOP Template Table -->
<!-- START Salient Divine Ability Table -->
<#if (pchasvar("DivineRank"))>
    <table width="100%" cellspacing="0" cellpadding="3" summary="Template Table">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>SALIENT DIVINE ABILITIES</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border8">${pcstring('ABILITYLIST.Salient Divine Ability')}<br /></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
<#else>
</#if>
<!-- STOP Salient Divine Ability Table -->
<#if (pcvar("MISC.MAGIC") > 0) >
<!-- START Misc Magic Table -->
    <table width="100%" cellspacing="0" cellpadding="3" summary="Misc. Magic Table">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>MAGIC</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="70%" class="border8"><span class="notes">${pcstring('MISC.MAGIC')}</span></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
<!-- STOP Misc Magic Table -->
</#if>
<#if (pcvar("PROHIBITEDLIST") > 0) >
<!-- START Prohibited Table -->
    <table width="100%" cellspacing="0" cellpadding="3" summary="Prohiubited List">
     <tr>
      <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>PROHIBITED</b></font></td>
     </tr>
     <tr>
      <td valign="top" width="100%" class="border8">${pcstring('PROHIBITEDLIST')}<br /></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
<!-- STOP Prohibited Table -->
</#if>
<#if (pcstring("DOMAIN.1") != "") >
<!-- START Domain Table -->
    <table width="100%" cellspacing="0" cellpadding="3" summary="Domain List">
     <tr>
      <td bgcolor="black" align="center" colspan="2"><font color="white" style="font-size: small"><b>Cleric of ${pcstring('DEITY')}</b></font></td>
     </tr>
     <tr>
      <td valign="top" class="border8"><b>Domain</b><br /></td>
      <td valign="top" class="border8"><b>Granted Power</b><br /></td>
     </tr>
<@loop from=1 to=pcvar('COUNT[DOMAINS]') ; count , count_has_next>
<tr><td valign="top" class="border7">${pcstring('DOMAIN.${count}')}<br /></td><td valign="top" class="border7">${pcstring('DOMAIN.${count}.POWER')}<br /></td></tr>
</@loop>
    </table>
<font style="font-size:2pt"><br /></font>
</#if>
<!-- STOP Domain Table -->
<!-- Start Turning Table -->
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","ASPECT=TurnType")-1') ; turncount , turncount_has_next>
   <table width="100%" cellspacing="0" cellpadding="3" summary="Clerical Turning Table">
     <tr>
      <td bgcolor="black" align="center" colspan="4"><font color="white" style="font-size: small"><b>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnType')} ${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnKind')}</b></font></td>
     </tr>
     <tr >
      <td rowspan="2" width="25%" bgcolor="black" align="center"><font color="white" style="font-size: x-small">TURNING CHECK<br />RESULT</font></td>
      <td rowspan="2" width="25%" bgcolor="black" align="center"><font color="white" style="font-size: x-small">AFFECTED<br />(MAX HIT DICE)</font></td>
      <td width="25%" bgcolor="black" align="right"><font color="white" style="font-size: x-small">TURN LEVEL</font></td>
      <td width="25%" class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel.INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">TURN DAMAGE</font></td>
      <td class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnDamage')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>Up to 0</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)-4).INTVAL')}</b></td>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">TURNING CHECK</font></td>
      <td class="border9" align="center"><b>1d20${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnCheck.INTVAL.SIGN.NOZERO')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>1 - 3</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)-3).INTVAL')}</b></td>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">TURNS/DAY</font></td>
      <td class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnTimes.INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>4 - 6</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)-2).INTVAL')}</b></td>
      <td colspan="2" class="border" align="center"><font style="font-size: small">
        <@loop from=0 to=pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnTimes.INTVAL')?number-1> &#9744; </@loop></font>
      </td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>7 - 9</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)-1).INTVAL')}</b></td>
      <td colspan="2" rowspan="6" class="border8" valign="top">${pcstring('ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnNotes')}
     </tr>
     <tr>
      <td align="center" class="font8"><b>10 - 12</b></td>
      <td align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)+0).INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>13 - 15</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)+1).INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>16 - 18</b></td>
      <td align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)+2).INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>19 - 21</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)+3).INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>22+</b></td>
      <td align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${turncount}.ASPECT=TurnType.ASPECT.TurnLevel)+4).INTVAL')}</b></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
</@loop>


<!-- Stop Turning Table -->

<!-- Channeling Table -->
<!-- Start Channeling Table -->
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","ASPECT=ChannelingType")-1') ; channelingcount , channelingcount_has_next>
   <table width="100%" cellspacing="0" cellpadding="3" summary="Channeling Table">
     <tr>
      <td bgcolor="black" align="center" colspan="4"><font color="white" style="font-size: small"><b>${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingType')} ${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingKind')}</b></font></td>
     </tr>
     <tr >
      <td rowspan="2" width="25%" bgcolor="black" align="center"><font color="white" style="font-size: x-small">CHANNELING CHECK<br />RESULT</font></td>
      <td rowspan="2" width="25%" bgcolor="black" align="center"><font color="white" style="font-size: x-small">AFFECTED<br />(MAX HIT DICE)</font></td>
      <td width="25%" bgcolor="black" align="right"><font color="white" style="font-size: x-small">CHANNELING LEVEL</font></td>
      <td width="25%" class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel.INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">MAGNITUDE</font></td>
      <td class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.Magnitude')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>Up to 0</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-8).INTVAL')}</b></td>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">CHANNELING CHECK</font></td>
      <td class="border9" align="center"><b>1d20${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingCheck.INTVAL.SIGN.NOZERO')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>1 - 3</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-7).INTVAL')}</b></td>
      <td bgcolor="black" align="right"><font color="white" style="font-size: x-small">USES/DAY</font></td>
      <td class="border9" align="center"><b>${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingUses.INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>4 - 6</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-6).INTVAL')}</b></td>
      <td colspan="2" class="border" align="center"><font style="font-size: small">
        <@loop from=0 to=pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingUses.INTVAL')?number-1> &#9744; </@loop></font>
     </td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>7 - 9</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-5).INTVAL')}</b></td>
      <td colspan="2" rowspan="6" class="border8" valign="top">${pcstring('ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingNotes')}
     </tr>
     <tr>
      <td align="center" class="font8"><b>10 - 12</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-4).INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>13 - 15</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-3).INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>16 - 18</b></td>
      <td align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-2).INTVAL')}</b></td>
     </tr>
     <tr>
      <td bgcolor="gray" align="center" class="font8"><b>19 - 21</b></td>
      <td bgcolor="gray" align="center" class="font8"><b>${pcstring('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)-1).INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>22 - 25</b></td>
      <td align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)+0).INTVAL')}</b></td>
     </tr>
     <tr>
      <td align="center" class="font8"><b>26+</b></td>
      <td align="center" class="font8"><b>${pcvar('((ABILITYALL.Special Ability.${channelingcount}.ASPECT=ChannelingType.ASPECT.ChannelingLevel)+1).INTVAL')}</b></td>
     </tr>
    </table>
<font style="font-size:2pt"><br /></font>
</@loop>


<!-- Stop Channeling Table -->


<!-- Start Overflow Weapons table -->
<@loop from=nextWeaponIdx to=pcvar('COUNT[EQTYPE.Weapon]-1') ; weap , weap_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("WEAPON.${weap}.NAME") != "") >
  <@weaponBlock weap=weap />
</#if>
</@loop>

   <table cellpadding="0" width="100%" cellspacing="0" border="0" summary="Weapon Abbreviations">
    <tr>
     <td><font style="font-size:5pt"><b>1H-P:</b> One handed, primary hand.&nbsp;<b>1H-O:</b> One handed, off hand.&nbsp;<b>2H:</b> Two handed.&nbsp;<b>2W-P-(OH):</b> 2 weapons, primary hand (off hand weapon is heavy).&nbsp;<b>2W-P-(OL):</b> 2 weapons, primary hand (off hand weapon is light).&nbsp;<b>2W-OH:</b>2 weapons, off hand.</font></td>
    </tr>
   </table>
<!-- End Overflow Weapons Table -->
  </td>
  <td width="50%" valign="top">
<!-- START Language Table -->
   <table width="100%" cellspacing="0" cellpadding="3" summary="Language Table">
    <tr>
     <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>LANGUAGES</b></font></td>
    </tr>
    <tr>
     <td valign="top" width="100%" class="border8">${pcstring('LANGUAGES')} ${pcstring('ABILITYALL.ANY.0.ASPECT=Language.ASPECT.Language')}<br /></td>
    </tr>
   </table>
<!-- STOP Language Table -->
<!-- Start Proficiency Table -->
<#if (pcstring("WEAPONPROFS") != "") >
   <table width="100%" cellspacing="0" cellpadding="3" summary="Proficiency Table">
    <tr>
     <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>PROFICIENCIES</b></font></td>
    </tr>
    <tr>
     <td valign="top" width="100%" class="border8">${pcstring('WEAPONPROFS')}<br /></td>
    </tr>
   </table>
<!-- STOP Proficiency Table -->
</#if>
<!-- START Archetypes Table -->
<#if (pcvar('count("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Archetype Table">
    <tr>
     <td bgcolor="black" align="center"><font color="white" style="font-size: small"><b>Archetypes</b></font></td>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Archetype","TYPE=Archetype","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; archetype , archetype_has_next>
<#if (archetype % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8"><b>${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype')}
</b>
${pcstring('ABILITYALL.Archetype.VISIBLE.${archetype}.TYPE=Archetype.DESC')}</td>
    </tr>
</@loop>
   </table>
</#if>
<!-- STOP Archetypes Table -->

<!-- START Afflictions Table -->
<#if (pcvar('count("ABILITIES","CATEGORY=Afflictions","TYPE=Affliction","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Afflictions">
    <tr>
     <th bgcolor="black" align="center" class="ab" colspan="2"><b>Afflictions</b></th>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Afflictions","TYPE=Affliction","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; affliction , affliction_has_next>
<#if (affliction % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8" width="70%"><b>${pcstring('ABILITYALL.Afflictions.VISIBLE.${affliction}.TYPE=Affliction')}</b></td>
	<td valign="top" class="font8" width="30%" align="right">[${pcstring('ABILITYALL.Afflictions.VISIBLE.${affliction}.TYPE=Affliction.SOURCE')}]</td>
	<tr>
<#if (afflictions % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
<td class="font8" valign="top"	align="indent" colspan="2">&#160;&#160;&#160;&#160;
${pcstring('ABILITYALL.Afflictions.VISIBLE.${affliction}.TYPE=Affliction.DESC')}
	</td>
	</tr>
</@loop>
	</table>
</#if>
<!-- STOP Afflictions Table -->

<!-- START Traits Table -->
<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=Trait","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Traits Table" class="sa-table">
    <tr>
     <th colspan="2">Traits</th>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=Trait","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; trait , trait_has_next>
<#if (trait % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8" width="70%"><b>${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait')}</b></td>
	<td valign="top" class="font8" width="30%" align="right">[${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait.SOURCE')}]</td>
	<tr>
<#if (trait % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
<td class="font8" valign="top"	align="indent" colspan="2">&#160;&#160;&#160;&#160;
${pcstring('ABILITYALL.Special Ability.VISIBLE.${trait}.TYPE=Trait.DESC')}
	</td>
	</tr>
</@loop>
	</table>
</#if>
<!-- STOP Traits Table -->

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
<!-- START Special Attacks Table -->
<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialAttack","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Special Attacks Table">
    <tr>
     <th bgcolor="black" align="center" colspan="2"><font color="white" style="font-size: small"><b>SPECIAL ATTACKS</b></font></th>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialAttack","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialAttack , specialAttack_has_next>
<#if (specialAttack % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8"><b>
<#assign typeOfAbility = pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack.TYPE")?lower_case />
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack.HASASPECT.Name") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack.ASPECT.Name')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
<#else>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
</#if>
</b>
</td>
<td class="font8" valign="top"	width="30%" align="right">[${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack.SOURCE')}]</td>
<tr>
<#if (specialAttack % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
<td class="font8" valign="top"	align="indent" colspan="2">&#160;&#160;&#160;&#160;
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialAttack}.TYPE=SpecialAttack.DESC')}</td>
    </tr>
</@loop>
   </table>
</#if>
<!-- STOP Special Attacks Table -->
<!-- Start Animal Tricks -->
<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","TYPE=AnimalTrick")') = 0)>
<#else>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Animal Tricks Table">
    <tr>
     <td bgcolor="black" align="center"><font color="white" style="font-size: small"><b>ANIMAL TRICKS</b></font></td>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY","TYPE=AnimalTrick")-1') ; animalTrick , animalTrick_has_next>
<#if (animalTrick % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8"><b>${pcstring('ABILITYALL.Special Ability.VISIBLE.${animalTrick}.TYPE=AnimalTrick')}</b>
    ${pcstring('ABILITYALL.Special Ability.VISIBLE.${animalTrick}.TYPE=AnimalTrick.DESC')}</td>
    </tr>
</@loop>
   </table>
</#if>
<!-- Stop Animal Tricks -->
<!-- START Special Qualities Table -->
<#if (pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialQuality","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Feat Table">
    <tr>
     <th bgcolor="black" align="center" colspan="2"><font color="white" style="font-size: small"><b>SPECIAL QUALITIES</b></font></th>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=Special Ability","TYPE=SpecialQuality","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; specialQuality , specialQuality_has_next>
<#if (specialQuality % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" width="70%" class="font8"><b>
<#assign typeOfAbility = pcstring("ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality.TYPE")?lower_case />
<#if (pcstring("ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality.HASASPECT.Name") = "Y")>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality.ASPECT.Name')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
<#else>
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
</#if>
</b>
</td>
<td class="font8" valign="top"	width="30%" align="right">[${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality.SOURCE')}]</td>
<tr>
<#if (specialQuality % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
<td class="font8" valign="top"	align="indent" colspan="2">&#160;&#160;&#160;&#160;
${pcstring('ABILITYALL.Special Ability.VISIBLE.${specialQuality}.TYPE=SpecialQuality.DESC')}</td>
    </tr>
</@loop>
   </table>
</#if>
<!-- STOP Special Qualities Table -->
<!-- START Special Abilities Table -->
<#if (pcvar("COUNT[SA]") > 0) >
   <table width="100%" cellspacing="0" cellpadding="2" summary="Special Abilities Table">
    <tr>
     <td bgcolor="black" align="center" colspan="1"><font color="white" style="font-size: small"><b>SPECIAL ABILITIES</b></font></td>
    </tr>

<@loop from=0 to=pcvar('COUNT[SA]-1') ; sa , sa_has_next>
<tr>
<td valign="top" width="100%" class="border10">${pcstring('SPECIALABILITY.${sa}')}</td>
</tr>
</@loop>
    <tr>
     <td valign="top" width="100%" class="border8">${pcstring('SPECIALLIST')}</td>
    </tr>
   </table>
</#if>
<!-- STOP Special Abilities Table -->
<table width="100%" cellspacing="0" cellpadding="2" summary="Feat Table">
  <tr>
     <td bgcolor="black" align="center" colspan="2"><font color="white" style="font-size: small"><b>FEATS</b></font></td>
  </tr>
<@loop from=0 to=pcvar('COUNT[FEATSALL.VISIBLE]-1') ; feat , feat_has_next>
<#if (feat % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
    <td valign="top" width="70%" class="font8" color="white"><b>${pcstring('FEATALL.VISIBLE.${feat}')}</b></td>
    <td class="font8" valign="top" width="30%" align="right">[${pcstring('FEATALL.VISIBLE.${feat}.SOURCE')}]</td>
  <tr>
<#if (feat % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
    <td class="font8" valign="top" colspan="2">&#160;&#160;&#160;&#160;${pcstring('FEATALL.VISIBLE.${feat}.DESC')}</td>
  </tr>
</@loop>
</table>
  </td>
 </tr>
<!-- STOP Feat Table -->

<!-- START PFS Chronicle Table -->
 <tr>
  <td>
<#if (pcvar('count("ABILITIES","CATEGORY=PFS Chronicle","TYPE=PFSChronicle","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")') > 0)>
   <table width="100%" cellspacing="0" cellpadding="2" summary="Feat Table">
    <tr>
     <th bgcolor="black" align="center" colspan="2"><font color="white" style="font-size: small"><b>PFS Chronicles</b></font></th>
    </tr>
<@loop from=0 to=pcvar('count("ABILITIES","CATEGORY=PFS Chronicle","TYPE=PFSChronicle","VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY")-1') ; PFSChronicle , PFSChronicle_has_next>
<#if (PFSChronicle % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
     <td valign="top" class="font8"><b>
<#assign typeOfAbility = pcstring("ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle.TYPE")?lower_case />
<#if (pcstring("ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle.HASASPECT.Name") = "Y")>
${pcstring('ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle.ASPECT.Name')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
<#else>
${pcstring('ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle')}
<@typeOfAbilitySuffix typeOfAbility=typeOfAbility />
</#if>
:</b>
${pcstring('ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle.DESC')}
<i>&#8212; [${pcstring('ABILITYALL.PFS Chronicle.VISIBLE.${PFSChronicle}.TYPE=PFSChronicle.SOURCE')}]</i>
    </td>
    </tr>
</@loop>
   </table>
</#if>
  </td>
 </tr>
<!-- STOP PFS Chronicle Table -->

</table>

<!-- ================================================================ -->



<!-- Start of Temporary Bonuses table -->
<#if (pcvar("COUNT[TEMPBONUSNAMES]") > 0) >
<tr><td>
<table width="100%" cellspacing="0" cellpadding="2">
<tr>
<td class="c9wB">Temporary Bonuses Applied</td>
</tr>
<tr>
<td width="100%" class="l8">
<@loop from=0 to=pcvar('COUNT[TEMPBONUSNAMES]-1') ; temp , temp_has_next>
<#if (temp = 0)>
<#else>
&bull;&nbsp;
</#if>
${pcstring('TEMPBONUS.${temp}')}
</@loop>
</td>
</tr>
</table>
</td></tr>
</#if>


<#include "common/common-spells.ftl">

<#if (pcstring("BIO") != '' || pcstring('DESC') != '') >
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>
<!-- START Bio Table -->
<table width="100%" cellspacing="0" cellpadding="2" id="Bio">
 <tr>
  <td colspan="2" class="font14">${pcstring('NAME')} <#if (pcstring("FOLLOWEROF") != "") >- ${pcstring('FOLLOWEROF')} </#if></td>
 </tr>
 <tr>
  <td class="border" height="400" width="1%"><img src="file://localhost/${pcstring('PORTRAIT')}" height="400" alt="${pcstring('NAME')}'s portrait" /></td>
  <td valign="top" width="99%">
   <table width="100%" cellspacing="0" cellpadding="2">
    <tr><td class="biodata">${pcstring('RACE')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">RACE</font></td></tr>
    <tr><td class="biodata">${pcstring('AGE')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">AGE</font></td></tr>
    <tr><td class="biodata">${pcstring('HEIGHT')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">HEIGHT</font></td></tr>
    <tr><td class="biodata">${pcstring('WEIGHT')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">WEIGHT</font></td></tr>
    <tr><td class="biodata">${pcstring('COLOR.EYE')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">EYE COLOR</font></td></tr>
    <tr><td class="biodata">${pcstring('COLOR.SKIN')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">SKIN COLOR</font></td></tr>
    <tr><td class="biodata">${pcstring('COLOR.HAIR')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">HAIR COLOR</font></td></tr>
    <tr><td class="biodata">${pcstring('LENGTH.HAIR')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">HAIR LENGTH</font></td></tr>
    <tr><td class="biodata">${pcstring('PHOBIAS')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">PHOBIAS</font></td></tr>
    <tr><td class="biodata">${pcstring('PERSONALITY1')} ${pcstring('PERSONALITY2')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">PERSONALITY TRAITS</font></td></tr>
    <tr><td class="biodata">${pcstring('INTERESTS')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">INTERESTS</font></td></tr>
    <tr><td class="biodata">${pcstring('SPEECHTENDENCY')}, ${pcstring('CATCHPHRASE')}<br/></td></tr>
    <tr><td class="topline"><font style="font-size: 6pt">SPOKEN STYLE</font></td></tr>
   </table>
<!-- STOP BIO Table -->
  </td>
 </tr>
 <tr><td class="borderbottom" colspan="2"><font style="font-size: large">Description</font></td></tr>
 <tr><td colspan="2"><font style="font-size: small"><span class="notes">${pcstring('DESC')}</span></font></td></tr>
 <tr><td class="borderbottom" colspan="2"><font style="font-size: large">Home</font></td></tr>
 <tr><td colspan="2"><font style="font-size: small"><#if (pcstring("REGION") != "" && pcstring("REGION") != "None") > From ${pcstring('REGION')}. </#if> ${pcstring('RESIDENCE')}, ${pcstring('LOCATION')}<br/></font></td></tr>
 <tr><td class="borderbottom" colspan="2"><font style="font-size: large">Biography</font></td></tr>
 <tr><td colspan="2"><font style="font-size: small"><span class="notes">${pcstring('BIO')}</span></font></td></tr>
</table>
</#if>
<#if (pcvar("COUNT[NOTES]") > 0) >
<br style="page-break-after: always" />
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')}<br/>Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>

<table width="100%" cellspacing="0" cellpadding="5">
 <tr>
  <th colspan="2" class="notehead">Notes</th>
 </tr>
<@loop from=0 to=pcvar('COUNT[NOTES]-1') ; note , note_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("NOTE.${note}.NAME") = "DM Notes")>
<#else>
	<tr>
		<td class="notetab" width="20%"><span class="notes">${pcstring('NOTE.${note}.NAME')}</span></td>
		<td class="notetab" width="80%"><span class="notes">${pcstring('NOTE.${note}.VALUE')}</span></td>
	</tr>
</#if>
</@loop>
</table>
</#if>
</body>
</html>
