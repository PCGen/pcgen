<#-- 
# Freemarker template for the PCGen variable report.
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
<#macro outputVarOrNbsp strVar>
	<#if strVar?trim?length == 0>
		&nbsp;
	<#else>
		${strVar}
	</#if>
</#macro>  
		
	

<#escape x as x?html>
<!DOCTYPE html>
<html>
<head>
<title>PCGen Variable Definition Report - ${.now?date}</title>
<style>
table.summary  {border: 1px solid #bbb; font-family: Verdana,Helvetica,sans serif;
font-size: 11px;}
.summary th {font-weight: bold; background-color: rgb(240, 240, 240);}
.summary td {border-bottom: 1px solid #bbb; }
.lastRow td {border-bottom: none; }
td.right {text-align: right;}
table.variable  {border: 1px solid #bbb; font-family: Verdana,Helvetica,sans serif;
font-size: 11px; width:99%;}
.variable th {font-weight: bold; background-color: rgb(240, 240, 240);}
.variable td {border-top: 1px solid #bbb; }
</style>
</head>
<body>
<!-- Produced on ${.now?date} at ${.now?time} using template ${.template_name} -->
<h1>PCGen Variable Definition Report - ${.now?date}</h1>
<h2>Summary</h2>
<table class="summary">
<tr><th>Game Mode</th><th>Num Variables</th><th>Num Defines</th></tr>
<#list gameModeVarMap?keys as game>
	<#if game_has_next>
		<tr>
	<#else>
		<tr class="lastRow">
	</#if>
	<td><a href="#${game}">${game}</a></td><td class="right">${gameModeVarCountMap[game]}</td><td class="right">${gameModeVarMap[game]?size}</td></tr>
</#list>  
</table>
<#list gameModeVarMap?keys as game>
	<#assign lastVar = "">
	<a name="${game}"></a>
	<h2>Variables for game mode ${game}</h2>
	<p>Found ${gameModeVarMap[game]?size} defines of ${gameModeVarCountMap[game]} variables</p>
	<#list gameModeVarMap[game] as varDefine>
		<#if lastVar != varDefine.varName>
			<#if (varDefine_index > 0)>
				</table><br>
			</#if>
			<b>${varDefine.varName}</b><br>
			<#assign lastVar = varDefine.varName>
			<table class="variable">
				<tr><th width="30%">Defined by</th><th width="30%">Defined In</th><th width="40%">Use</th></tr>
		</#if>
		<tr><td>${varDefine.definingObject}</td>
			<td title="${varDefine.definingFile?substring(pathIgnoreLen)}">${varDefine.definingFile.name}</td>
			<td><@outputVarOrNbsp strVar="${varDefine.use!}" /></td>
		</tr>
		<#if !varDefine_has_next>
			</table><br>
		</#if>
	</#list>
</#list>
<p><b>End of report.</b></p>
</body>
</html>
</#escape>
