/*
 * Copyright 2014-18 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMInterfaceToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Implements the MODIFYOTHER token for remotely modifying variables in the new variable
 * system.
 */
public class ModifyOtherLst extends AbstractNonEmptyToken<VarHolder>
		implements CDOMInterfaceToken<VarContainer, VarHolder>, CDOMPrimaryToken<VarHolder>
{

	@Override
	public String getTokenName()
	{
		return "MODIFYOTHER";
	}

	//MODIFYOTHER:EQUIPMENT|GROUP=Martial|EqCritRange|ADD|1
	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, VarHolder obj, String value)
	{
		//TODO These instanceof checks will fail - the VarHolder is a proxy :(
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(getTokenName() + " may not be used in Ungranted objects.");
		}
		if (obj instanceof Campaign)
		{
			return new ParseResult.Fail(
				getTokenName() + " may not be used in Campaign Files.  " + "Please use the Global Modifier file");
		}
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String scopeName = sep.next();
		PCGenScope lvs = context.getVariableContext().getScope(scopeName);
		if (lvs == null)
		{
			return new ParseResult.Fail(
				getTokenName() + " found illegal variable scope: " + scopeName + " as first argument: " + value);
		}
		if (lvs.getParentScope() == null)
		{
			return new ParseResult.Fail(
				getTokenName() + " found illegal variable scope: " + scopeName + " is a global scope");
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " needed 2nd argument: " + value);
		}
		String fullName = LegalScope.getFullName(lvs);
		LoadContext subContext = context.dropIntoContext(fullName);
		return continueParsing(subContext, lvs, obj, value, sep);
	}

	private <GT extends VarScoped> ParseResult continueParsing(LoadContext context, PCGenScope lvs, VarHolder obj,
		String value, ParsingSeparator sep)
	{
		PCGenScope scope = context.getActiveScope();
		String groupingName = sep.next();

		GroupingCollection<? extends Loadable> group = context.getGrouping(lvs, groupingName);
		if (group == null)
		{
			return new ParseResult.Fail(getTokenName() + " unable to build group from: " + groupingName);
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " needed 3rd argument: " + value);
		}
		String varName = sep.next();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(getTokenName() + " found invalid var name: " + varName + "(scope: "
				+ LegalScope.getFullName(scope) + ")");
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " needed 4th argument: " + value);
		}
		String modIdentification = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " needed 5th argument: " + value);
		}
		String modInstructions = sep.next();
		FormulaModifier<?> modifier;
		try
		{
			FormatManager<?> format = context.getVariableContext().getVariableFormat(scope, varName);
			modifier = context.getVariableContext().getModifier(modIdentification, modInstructions, scope, format);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " Modifier " + modIdentification + " had value "
				+ modInstructions + " but it was not valid: " + iae.getMessage());
		}
		Set<Object> associationsVisited = Collections.newSetFromMap(new CaseInsensitiveMap<>());
		while (sep.hasNext())
		{
			String assoc = sep.next();
			int equalLoc = assoc.indexOf('=');
			if (equalLoc == -1)
			{
				return new ParseResult.Fail(
					getTokenName() + " was expecting = in an ASSOCIATION but got " + assoc + " in " + value);
			}
			String assocName = assoc.substring(0, equalLoc);
			if (associationsVisited.contains(assocName))
			{
				return new ParseResult.Fail(
					getTokenName() + " does not allow multiple asspociations with the same name.  " + "Found multiple: "
						+ assocName + " in " + value);
			}
			associationsVisited.add(assocName);
			modifier.addAssociation(assoc);
		}
		VarModifier<?> vm = new VarModifier<>(varName, scope, modifier);
		RemoteModifier<?> rm = new RemoteModifier<>(group, vm);
		obj.addRemoteModifier(rm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, VarHolder obj)
	{
		RemoteModifier<?>[] added = obj.getRemoteModifierArray();
		List<String> modifiers = new ArrayList<>();
		for (RemoteModifier<?> rm : added)
		{
			VarModifier<?> vm = rm.getVarModifier();
			StringBuilder sb = new StringBuilder();
			GroupingCollection<?> og = rm.getGrouping();
			sb.append(LegalScope.getFullName(vm.getLegalScope()));
			sb.append(Constants.PIPE);
			sb.append(og.getInstructions());
			sb.append(Constants.PIPE);
			sb.append(vm.getVarName());
			sb.append(Constants.PIPE);
			sb.append(unparseModifier(vm));
			modifiers.add(sb.toString());
		}
		if (modifiers.isEmpty())
		{
			//Legal
			return null;
		}
		return modifiers.toArray(new String[modifiers.size()]);
	}

	private String unparseModifier(VarModifier<?> vm)
	{
		FormulaModifier<?> modifier = vm.getModifier();
		String type = modifier.getIdentification();
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(Constants.PIPE);
		sb.append(modifier.getInstructions());
		Collection<String> assocs = modifier.getAssociationInstructions();
		if (assocs != null && assocs.size() > 0)
		{
			sb.append(Constants.PIPE);
			sb.append(StringUtil.join(assocs, Constants.PIPE));
		}
		return sb.toString();
	}

	@Override
	public Class<VarHolder> getTokenClass()
	{
		return VarHolder.class;
	}

	@Override
	public Class<VarContainer> getReadInterface()
	{
		return VarContainer.class;
	}
}
