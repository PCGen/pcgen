<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
	<head>
		<title>3.5 information sheet</title>
	</head>
	<body>
		<table cellpadding="1" cellspacing="0" border="0" width="100%" summary="Character Information">
			<tr style="font-family: Verdana, sans-serif;">
				<td align="center"><font size="4"><b>Encounter</b></font></td>
				<td align="center"><font size="4"><b>Statistics</b></font></td>
				<td align="center"><font size="4"><b>Encumbrance</b></font></td>
			</tr>
			<tr valign="top">
				<td>
					<table>
						<tr><td><img src="images/icon_hp.png"/></td><td>&nbsp;<b>HP:</b>&nbsp;${pcstring('HP')}</td></tr>
						<tr><td><img src="images/icon_bab.png"/></td><td>&nbsp;<b>BAB:</b>&nbsp;${pcstring('ATTACK.MELEE')}</td></tr>
						<tr><td><img src="images/icon_init.png"/></td><td>&nbsp;<b>INIT:</b>&nbsp;${pcstring('INITIATIVEMOD')}</td></tr>
						<tr><td><img src="images/icon_ac.png"/></td><td>&nbsp;<b>AC:</b>&nbsp;${pcstring('AC.Total')}</td></tr>
						<tr><td><img src="images/icon_acf.png"/></td><td>&nbsp;<b>Flatfooted:</b>&nbsp;${pcstring('AC.Flatfooted')}</td></tr>
						<tr><td><img src="images/icon_act.png"/></td><td>&nbsp;<b>Touch:</b>&nbsp;${pcstring('AC.Touch')}</td></tr>
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
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Favored:</b>&nbsp;${pcstring('FAVOREDLIST')}</td></tr>
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
						<tr><td><img src="images/icon_move.png"/></td><td>&nbsp;<b>Move:</b>&nbsp;${pcstring('MOVEMENT')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Load (Light):</b>&nbsp;${pcstring('WEIGHT.LIGHT')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Load (Medium):</b>&nbsp;${pcstring('WEIGHT.MEDIUM')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Load (Heavy):</b>&nbsp;${pcstring('WEIGHT.HEAVY')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Lift Over Head:</b>&nbsp;${pcstring('WEIGHT.OVERHEAD')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Lift Off Ground:</b>&nbsp;${pcstring('WEIGHT.OFFGROUND')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Push or Drag:</b>&nbsp;${pcstring('WEIGHT.PUSHDRAG')} ${pcstring('UNITSET.WEIGHTUNIT')}</td></tr>
						<tr><td><img src="images/icon_load.png"/></td><td>&nbsp;<b>Curr. Load:</b>&nbsp;${pcstring('TOTAL.LOAD')} ${pcstring('TOTAL.WEIGHT')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Armor Check Penalty:</b>&nbsp;${pcstring('ACCHECK')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Max. Dex.:</b>&nbsp;${pcstring('MAXDEX')}</td></tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>

