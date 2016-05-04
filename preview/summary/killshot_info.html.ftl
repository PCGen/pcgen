<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
	<head>
		<title>Killshot information sheet</title>
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
						<!--TODO-->
					</table>
				</td>
				<td>
					<table>
<#if (pcstring('RACE') = "<none selected>")>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;Human</td></tr>
<#else>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Race:</b>&nbsp;${pcstring('RACE')}</td></tr>
</#if>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Gender:</b>&nbsp;${pcstring('GENDER.LONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Size:</b>&nbsp;${pcstring('SIZELONG')}</td></tr>
						<tr><td><img src="images/icon_size.png"/></td><td>&nbsp;<b>Age:</b>&nbsp;${pcstring('AGE')}</td></tr>
						<tr><td><img src="images/icon_bod.png"/></td><td>&nbsp;<b>Handed:</b>&nbsp;${pcstring('HANDED')}</td></tr>
					</table>
				</td>
				<td>
					<table>
						<!--TODO-->
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>