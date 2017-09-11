<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
	<head>
		<title>starfinder information sheet</title>
	</head>
	<body>
		<#if (pcvar("VAR.Starship") > 0)>
		<table cellpadding="1" cellspacing="0" border="0" width="100%" summary="Character Information">
			<tr style="font-family: Verdana, sans-serif;">
				<td align="center"><font size="4"><b>Encounter</b></font></td>
				<td align="center"><font size="4"><b>Statistics</b></font></td>
				<td align="center"><font size="4"><b>Encumbrance</b></font></td>
			</tr>
			<tr valign="top">
				<td>
					<table>
						<tr><td><img src="images/icon_hp.png" /></td><td>&nbsp;<b>Hull Points:</b>&nbsp;${pcstring('HP')}</td></tr>
						<tr><td><img src="images/icon_hp.png" /></td><td>&nbsp;<b>SP:</b>&nbsp;${pcstring('ALTHP')}</td></tr>
						<tr><td><img src="images/icon_bab.png"/></td><td>&nbsp;<b>BAB:</b>&nbsp;${pcstring('ATTACK.MELEE')}</td></tr>
						<tr><td><img src="images/icon_init.png"/></td><td>&nbsp;<b>INIT:</b>&nbsp;${pcstring('INITIATIVEMOD')}</td></tr>
						<tr><td><img src="images/icon_ac.png"/></td><td>&nbsp;<b>EAC:</b>&nbsp;${pcstring('AC.EAC')}</td></tr>
						<tr><td><img src="images/icon_acf.png"/></td><td>&nbsp;<b>KAC</b>&nbsp;${pcstring('AC.KAC')}</td></tr>
<@loop from=0 to=pcvar('COUNT[CHECKS]-1') ; checks , checks_has_next>
<#if (pcstring("CHECK.${checks}.NAME") = "Fortitude")>
						<tr><td><img src="images/icon_savef.png"/></td><td>&nbsp;<b>FORT:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#elseif (pcstring("CHECK.${checks}.NAME") = "Reflex")>
						<tr><td><img src="images/icon_saver.png"/></td><td>&nbsp;<b>REF:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#elseif (pcstring("CHECK.${checks}.NAME") = "Will")>
						<tr><td><img src="images/icon_savew.png"/></td><td>&nbsp;<b>WILL:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#else>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>${pcstring("CHECK.${checks}.NAME")}:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
</#if>
</@loop>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>CR:</b>&nbsp;${pcstring('CR')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>Alignment:</b>&nbsp;${pcstring('ALIGNMENT.SHORT')}</td></tr>
<#if (pcstring('RACE') = "<none selected>")>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;None</td></tr>
<#else>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;${pcstring('RACE')}</td></tr>
</#if>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Gender:</b>&nbsp;${pcstring('GENDER.LONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Size:</b>&nbsp;${pcstring('SIZELONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Age:</b>&nbsp;${pcstring('AGE')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Handed:</b>&nbsp;${pcstring('HANDED')}</td></tr>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>Vision:</b>&nbsp;${pcstring('VISION')}</td></tr>
						<tr><td><img src="images/icon_coin.png"/></td><td>&nbsp;<b>Wealth:</b>&nbsp;${pcstring('TOTAL.VALUE')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Load (Unencumbered):</b>&nbsp;${pcstring('WEIGHT.UNENCUMBERED')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Curr. Bulk:</b>&nbsp;${pcstring('TOTAL.WEIGHT')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Armor Check Penalty:</b>&nbsp;${pcstring('ACCHECK')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Max. Dex.:</b>&nbsp;${pcstring('MAXDEX')}</td></tr>
						<tr><td><img src="images/icon_move.png"/></td><td>&nbsp;<b>Move:</b>&nbsp;${pcstring('MOVEMENT')}</td></tr>
						<tr><td><img src="images/icon_unarmed.png"/></td><td>&nbsp;<b>CMB:</b>&nbsp;${pcstring('VAR.CMB.INTVAL.SIGN')}</td></tr>
						<tr><td><img src="images/icon_unarmed.png"/></td><td>&nbsp;<b>CMD:</b>&nbsp;${pcstring('VAR.CMD.INTVAL')}</td></tr>
					</table>
				</td>
			</tr>
		</table>


		<#else>
		<table cellpadding="1" cellspacing="0" border="0" width="100%" summary="Character Information">
			<tr style="font-family: Verdana, sans-serif;">
				<td align="center"><font size="4"><b>Encounter</b></font></td>
				<td align="center"><font size="4"><b>Statistics</b></font></td>
				<td align="center"><font size="4"><b>Encumbrance</b></font></td>
			</tr>
			<tr valign="top">
				<td>
					<table>
						<tr><td><img src="images/icon_hp.png" /></td><td>&nbsp;<b>HP:</b>&nbsp;${pcstring('HP')}</td></tr>
						<tr><td><img src="images/icon_hp.png" /></td><td>&nbsp;<b>SP:</b>&nbsp;${pcstring('ALTHP')}</td></tr>
						<tr><td><img src="images/icon_bab.png"/></td><td>&nbsp;<b>BAB:</b>&nbsp;${pcstring('ATTACK.MELEE')}</td></tr>
						<tr><td><img src="images/icon_init.png"/></td><td>&nbsp;<b>INIT:</b>&nbsp;${pcstring('INITIATIVEMOD')}</td></tr>
						<tr><td><img src="images/icon_ac.png"/></td><td>&nbsp;<b>EAC:</b>&nbsp;${pcstring('AC.EAC')}</td></tr>
						<tr><td><img src="images/icon_acf.png"/></td><td>&nbsp;<b>KAC</b>&nbsp;${pcstring('AC.KAC')}</td></tr>
<@loop from=0 to=pcvar('COUNT[CHECKS]-1') ; checks , checks_has_next>
<#if (pcstring("CHECK.${checks}.NAME") = "Fortitude")>
						<tr><td><img src="images/icon_savef.png"/></td><td>&nbsp;<b>FORT:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#elseif (pcstring("CHECK.${checks}.NAME") = "Reflex")>
						<tr><td><img src="images/icon_saver.png"/></td><td>&nbsp;<b>REF:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#elseif (pcstring("CHECK.${checks}.NAME") = "Will")>
						<tr><td><img src="images/icon_savew.png"/></td><td>&nbsp;<b>WILL:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
<#else>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>${pcstring("CHECK.${checks}.NAME")}:</b>&nbsp;${pcstring("CHECK.${checks}.TOTAL")}</td></tr>
</#if>
</@loop>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>CR:</b>&nbsp;${pcstring('CR')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>Alignment:</b>&nbsp;${pcstring('ALIGNMENT.SHORT')}</td></tr>
<#if (pcstring('RACE') = "<none selected>")>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;None</td></tr>
<#else>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;${pcstring('RACE')}</td></tr>
</#if>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Gender:</b>&nbsp;${pcstring('GENDER.LONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Size:</b>&nbsp;${pcstring('SIZELONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Age:</b>&nbsp;${pcstring('AGE')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Handed:</b>&nbsp;${pcstring('HANDED')}</td></tr>
						<tr><td><img src="images/icon_eye.png"/></td><td>&nbsp;<b>Vision:</b>&nbsp;${pcstring('VISION')}</td></tr>
						<tr><td><img src="images/icon_coin.png"/></td><td>&nbsp;<b>Wealth:</b>&nbsp;${pcstring('TOTAL.VALUE')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Load (Unencumbered):</b>&nbsp;${pcstring('WEIGHT.UNENCUMBERED')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Curr. Bulk:</b>&nbsp;${pcstring('TOTAL.WEIGHT')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Armor Check Penalty:</b>&nbsp;${pcstring('ACCHECK')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Max. Dex.:</b>&nbsp;${pcstring('MAXDEX')}</td></tr>
						<tr><td><img src="images/icon_move.png"/></td><td>&nbsp;<b>Move:</b>&nbsp;${pcstring('MOVEMENT')}</td></tr>
						<tr><td><img src="images/icon_unarmed.png"/></td><td>&nbsp;<b>CMB:</b>&nbsp;${pcstring('VAR.CMB.INTVAL.SIGN')}</td></tr>
						<tr><td><img src="images/icon_unarmed.png"/></td><td>&nbsp;<b>CMD:</b>&nbsp;${pcstring('VAR.CMD.INTVAL')}</td></tr>
					</table>
				</td>
			</tr>
		</table>
		</#if>
	</body>
</html>
