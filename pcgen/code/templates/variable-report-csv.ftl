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
"Game Mode","Variable Name","Defined by","Defined In Path","Defined In File","Use"
<#list gameModeVarMap?keys as game>
	<#list gameModeVarMap[game] as varDefine>
"${game}","${varDefine.varName}","${varDefine.definingObject}","${varDefine.definingFile?substring(pathIgnoreLen)}","${varDefine.definingFile.name}","${varDefine.use!}"	
	</#list>
</#list>
