<#-- Common code for the spells block in HTML sheets -->
<#-- This file is included by the other sheets that need a standard output of spells -->

<#macro spellBlock class spellbook level isKnownList=false>
  <#if (!isKnownList) >
	<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
  </#if>
	<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
	 <#if (spell % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
	  <td class="spname">
	      <b><#if (isKnownList) >${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}</#if>
	  <#if (pcstring("TEXT.LENGTH.SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK") = "0")>
	      ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}
	  <#else>
	      <a href="${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCELINK')}">${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</a>
	  </#if>
	      </b><br />
	  </td>
	  <td class="sptab">
	  <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO") = "None")>
	  <#else>
	  <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE") = "Personal")>
	  <#else>
	  <i>DC:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')}<br />
	  </#if>
	 </#if>
	  	</td>
	  <td class="sptab"><i>Save:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SAVEINFO')}<br /></td>
	  <td class="sptab"><i>Time:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTINGTIME')}<#rt>
	  <#if (!isKnownList) ><#lt>, 
	    <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "At Will")>
	at will
	    <#else>
	${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES')}/${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMEUNIT')}
	    </#if>
	  </#if><#t>
	   <br /></td><#lt>
	  <td class="sptab"><i>Duration:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DURATION')}<br /></td>
	  <td class="sptab"><i>Rng:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.RANGE')}<br /></td>
	  <td class="sptab"><i>Comp:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.COMPONENTS')}<br /></td>
	  <td class="sptab"><i>SR:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SR')}<br /></td>
	  <td class="sptab"><i>School:</i> ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SCHOOL')}<br /></td>
	  <#if (isKnownList) >
	  <td class="sptab">
		<#if (pcstring("SPELLLISTTYPE.${class}") = "Psionic")>
			<#assign ppcost = (level*2)-1 />
			<#if (ppcost = -1)>
			<i>PP:</i> 0/1
			<#else>
			<i>PP:</i> ${ppcost}
			</#if>
	  </td>
		<#elseif pcboolean("SPELLLISTMEMORIZE.${class}") >
	  <font style="font-size: medium">&#9744;&#9744;&#9744;</font></td>
		</#if>
	  </#if>
	 </tr>
	 <#assign numCols=7/><#if (isKnownList) ><#assign numCols=8/></#if>
	 <#if (spell % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
	  <td colspan="${numCols}" class="sptab1"><i>Effect:</i>&nbsp;&nbsp;${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.EFFECT')}<br /></td>
	  <td colspan="2" class="sptab2"><i>Source:</i>&nbsp;&nbsp;${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCESHORT')} ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.SOURCEPAGE')}<br /></td>
	 </tr>
	 <#if (spell % 2 = 0)><tr bgcolor="#DDDDDD"><#else><tr bgcolor="white"></#if>
	  <td colspan="${numCols}" class="sptab1"><i>Target Area:</i>&nbsp;&nbsp; ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.TARGET')}<br /></td>
	  <td colspan="2" class="sptab2"><i>Caster Level:</i>&nbsp;&nbsp; ${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.CASTERLEVEL')}<br /></td>
	   </tr>
	</@loop>
	<#if (!isKnownList) >
	 <tr>
	  <td colspan="9" bgcolor="black"><br /></td>
	 </tr>
	</table>
	</#if>
</#macro>
 
<!-- Start Racial Innate Spells -->
<#assign spellrace = pcvar('COUNT[SPELLRACE]') />
<#if (spellrace = 0)>
	<!-- No innate spells -->
<#else>
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>
<#assign spellbook = 1 />
<#assign class = 0 />
<#assign level = 0 />
<#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
<!-- START Spell list Header Table (Racial Innate) -->
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
 <tr>
  <td colspan="11" class="sphead"><b>Innate Spell-like Abilities</b></td>
 </tr>
</table>
<!-- End Spell List Header Table (Racial Innate) -->
<!-- Start Racial Innate Spell listing -->
  <@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" />
</#if>
<!-- End Racial Innate Spells -->

<!-- Start Other Innate Spells -->
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
	<#assign class = 0 />
	<#assign level = 0 />
	<#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
		<!-- START Spell list Header Table (Other Innate) --><#lt>
		<br />
		<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
		 <tr>
		  <td colspan="9" class="sphead"><b>${pcstring('SPELLBOOKNAME.${spellbook}')} Spell-like Abilities</b></font></td>
		 </tr>
		</table>
		<@spellBlock class="${class}" spellbook="${spellbook}" level="${level}" />
	</#if>
</@loop>
<!-- End Other Innate Spells -->
</#if>
<!-- End Innate Spells -->

<@loop from=0 to=0 ; spellbook , spellbook_has_next>
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("SPELLLISTCLASS.${class}") != '') >
<!-- START Spell list Header Table (Known) -->
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
 <tr>
  <td colspan="${pcvar('MAXSPELLLEVEL.${class}')+2}" class="sphead"><b>${pcstring('SPELLLISTCLASS.${class}')}
<#if (pcstring("SPELLLISTTYPE.${class}") = "Psionic")>
                Powers
<#else>
                Spells
</#if>
</b></td>
 </tr>
 <tr>
  <td bgcolor="black" class="sptop"><b>LEVEL</b></td>
<@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
  <td bgcolor="black" class="sptop"><b>${level}</b></td>
</@loop>
 </tr>
 <tr>
  <td class="sptab"><b>KNOWN</b></td>
<@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
  <td class="sptab"><b>${pcstring('SPELLLISTKNOWN.${class}.${level}')}</b></td>
</@loop>
 </tr>
 <tr bgcolor="#DDDDDD">
  <td class="sptab"><b>PER DAY</b></td>
<@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
  <td class="sptab"><b>${pcstring('SPELLLISTCAST.${class}.${level}')}</b></td>
</@loop>
 </tr>
</table>
<!-- End Spell List Header Table (Known) -->
<!-- Start Known Spells -->
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
<@loop from=0 to=pcvar('MAXSPELLLEVEL.${class}') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spellcount , spellcount_has_next>
<#if (spellcount = 0)>
<#else>
 <tr>
  <td colspan="10" class="splevel"><b>LEVEL ${level}</b></td>
 </tr>
<#if pcboolean("SPELLLISTMEMORIZE.${class}") >
<#else>
 <tr>
  <td colspan="10" class="splevel"><b>
  <@loop from=1 to=pcvar("COUNT[SPELLLISTCAST.${class}.${level}]")>&#9744;</@loop>
  </b></td>
 </tr>
</#if>
<@spellBlock class=class spellbook=spellbook level=level isKnownList=true />

</#if>
</@loop>
</@loop>
 <tr>
  <td colspan="10" bgcolor="black"><font style="font-size: x-small" color="white">* = Domain/Specialty Spell</font></td>
 </tr>
</table>
<br />
</#if>
</@loop>
</@loop>
<!-- End Known Spells -->
<!-- ================================================================ -->
<!-- Start Prepared Spells -->
<@loop from=pcvar('COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2') to=pcvar('COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2') ; memorised , memorised_has_next>
<#if (memorised = 0)>
<#else>
<!-- Start Innate Prepared -->
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]') ; spellrace , spellrace_has_next>
<#if (spellrace = 0)>
<#else>
<@loop from=1 to=1 ; spellbook , spellbook_has_next>
<@loop from=0 to=0 ; class , class_has_next>
<@loop from=0 to=0 ; level , level_has_next>
<#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
 <tr>
  <td colspan="11" align="center" bgcolor="black"><font color="white"><b>Racial Innate</b></font></td>
 </tr>
 <tr>
  <td valign="top">
   <table>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
<#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "At Will")>
    <tr>
     <td align="right" class="font9">At Will</td>
<#else>
    <tr>
     <td align="right"><font style="font-size: medium">
  <@loop from=1 to=pcvar("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES")>&#9744;</@loop></font><#lt>
     </td>
</#if>
     <td class="font9"><b>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</b> (DC:${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})</td>
    </tr>
</@loop>
   </table>
  </td>
 </tr>
 <tr>
  <td colspan="11" bgcolor="black"><font style="font-size: x-small" color="white"><br /></font></td>
 </tr>
</table>
</#if>
</@loop>
</@loop>
</@loop>
<br />

<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
<#assign class = 0 />
<#assign level = 0 />
 <#if (pcvar("COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]") > 0) >
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
 <tr>
  <td colspan="11" align="center" bgcolor="black"><font color="white"><b>${pcstring('SPELLBOOKNAME.${spellbook}')} Spells</b></font></td>
 </tr>
  <td valign="top">
   <table>
  <@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
   <#if (pcstring("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") = "At Will")>
    <tr>
     <td align="right" class="font9">At Will</td>
   <#else>
    <tr>
     <td align="right"><font style="font-size: medium">
    <@loop from=1 to=pcvar("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") ; slot_num , slot_num_has_next><input target_var="${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}_${slot_num}" type="checkbox"/></@loop></font>
     </td>
   </#if>
     <td class="font9"><b>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</b> (DC:${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})</td>
    </tr>
  </@loop>
   </table>
  </td>
 </tr>
 <tr>
  <td colspan="11" bgcolor="black"><font style="font-size: x-small" color="white"><br /></font></td>
 </tr>
</table>
 </#if>
</@loop>
<br />
</#if>
</@loop>
<!-- End Innate Prepared -->


<!-- Start Regular Prepared -->
<@loop from=2 to=pcvar('COUNT[SPELLBOOKS]-1') ; spellbook , spellbook_has_next>
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]') ; foo , foo_has_next><#-- TODO: Loop was of early exit type 1 -->
<@loop from=pcvar('COUNT[SPELLSINBOOK0.${spellbook}.0]') to=pcvar('COUNT[SPELLSINBOOK0.${spellbook}.0]') ; bar , bar_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (foo = 0 || bar = 0) >
<!-- Either we do not have a innate race, or if we do we do not have any 0 level spell for the innate race -->
<br style="page-break-after: always" />
<!--<center><font size="-1">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</font></center><br />
-->
<div class="pcgen">Created using <a href="http://pcgen.org/">PCGen</a> ${pcstring('EXPORT.VERSION')} on ${pcstring('EXPORT.DATE')} <br />Player: ${pcstring('PLAYERNAME')}; Character Name: ${pcstring('NAME')}</div>
<table width="100%" cellspacing="0" cellpadding="2" summary="Spell List">
 <tr>
  <td colspan="11" align="center" bgcolor="black"><font color="white"><b>${pcstring('SPELLBOOKNAME.${spellbook}')}</b></font></td>
 </tr>
<@loop from=pcvar('COUNT[SPELLRACE]') to=pcvar('COUNT[SPELLRACE]+COUNT[CLASSES]-1') ; class , class_has_next><#-- TODO: Loop was of early exit type 1 -->
<#if (pcstring("SPELLLISTCLASS.${class}") != '') >
 <tr>
  <td align="center" bgcolor="#DDDDDD" colspan="11"><font style="font-size: small" color="black">${pcstring('SPELLLISTCLASS.${class}')}</font></td>
 </tr>
 <tr>
<@loop from=0 to=pcvar('4') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
  <td valign="top">
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spelllevelcount , spelllevelcount_has_next>
<#if (spelllevelcount = 0)>
<!-- no memorized spells for SPELLSINBOOK.${class} ${spellbook} ${level} -->
<#else>
   <table summary="Spell List">
    <tr>
     <td valign="top" align="center" colspan="2"><font style="font-size: small" color="black"><u>Level ${level}</u></font></td>
    </tr>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
    <tr>
     <td align="right"><font style="font-size: medium">
	  <@loop from=1 to=pcvar("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES") ; spell_slot , spell_slot_has_next><input target_var="${pcstring('SPELLBOOKNAME.${spellbook}')}_${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}+${spell_slot}" type="checkbox"/></@loop></font>
     </td>
     <td class="font9"><b>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</b> (DC:${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})</td>
    </tr>
</@loop>
   </table>
</#if>
</@loop>
  </td>
</@loop>
<tr>
<@loop from=5 to=pcvar('9') ; level , level_has_next><#-- TODO: Loop was of early exit type 1 -->
  <td valign="top">
<@loop from=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]') ; spelllevelcount , spelllevelcount_has_next>
<#if (spelllevelcount = 0)>
<!-- no memorized spells for SPELLSINBOOK.${class} ${spellbook} ${level} -->
<#else>
   <table summary="Spell List">
    <tr>
     <td valign="top" align="center" colspan="2"><font style="font-size: small" color="black"><u>Level ${level}</u></font></td>
    </tr>
<@loop from=0 to=pcvar('COUNT[SPELLSINBOOK.${class}.${spellbook}.${level}]-1') ; spell , spell_has_next>
    <tr>
     <td align="right"><font style="font-size: medium">
<@loop from=1 to=pcvar("SPELLMEM.${class}.${spellbook}.${level}.${spell}.TIMES")>&#9744;</@loop></font>
     </td>
     <td class="font9"><b>${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.BONUSSPELL')}${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.NAME')}</b> (DC:${pcstring('SPELLMEM.${class}.${spellbook}.${level}.${spell}.DC')})</td>
    </tr>
</@loop>
   </table>
</#if>
</@loop>
  </td>
</@loop>
<!-- END SPELLLISTCLASS.${class} -->
</#if>
<!-- END FOR,${class},COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1 -->
</@loop>
 <tr>
  <td colspan="11" bgcolor="black"><font style="font-size: x-small" color="white">* = Domain/Specialty Spell</font></td>
 </tr>
</table>
<#else>
</#if>
<!-- END FOR,${bar},COUNT[SPELLSINBOOK0.${spellbook}.0],COUNT[SPELLSINBOOK0.${spellbook}.0],1,1 -->
</@loop>
<!-- END FOR,${foo},COUNT[SPELLRACE],COUNT[SPELLRACE],1,1 -->
</@loop>
<!-- END FOR,${spellbook},2,COUNT[SPELLBOOKS]-1,1,0 -->
</@loop>
<!-- ### END class Spellbook memorized spells ### -->
<!-- START FALSE IIF(${memorised}:0) -->
</#if>
</@loop>
<!-- ### END MEMORIZED ### -->

<!-- End Prepared Spells -->
<!-- ================================================================= -->
