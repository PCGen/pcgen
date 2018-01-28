/*
 * Copyright 2014-16 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * The MODIFY token defined by ModifyLst defines a calculation to be performed in the
 * (new) formula system.
 */
public class ModifyLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MODIFY";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (obj instanceof Campaign)
		{
			return new ParseResult.Fail(getTokenName()
				+ " may not be used in Campaign Files.  "
				+ "Please use the Global Modifier file", context);
		}
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + " may not be empty",
				context);
		}

		ScopeInstance scopeInst = context.getActiveScope();
		LegalScope scope = scopeInst.getLegalScope();
		String varName = sep.next();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(getTokenName()
				+ " found invalid var name: " + varName + " Modified on "
				+ obj.getClass().getSimpleName() + " " + obj.getKeyName(),
				context);
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed 2nd argument: " + value, context);
		}
		String modIdentification = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed third argument: " + value, context);
		}
		String modInstructions = sep.next();
		PCGenModifier<?> modifier;
		try
		{
			FormatManager<?> format = context.getVariableContext()
				.getVariableFormat(scope, varName);
			modifier = context.getVariableContext().getModifier(
				modIdentification, modInstructions.toString(), scope, format);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " Modifier "
				+ modIdentification + " had value " + modInstructions
				+ " but it was not valid: " + iae.getMessage(), context);
		}
		Set<Object> associationsVisited =
				Collections.newSetFromMap(new CaseInsensitiveMap<>());
		while (sep.hasNext())
		{
			String assoc = sep.next();
			int equalLoc = assoc.indexOf('=');
			if (equalLoc == -1)
			{
				return new ParseResult.Fail(getTokenName()
					+ " was expecting = in an ASSOCIATION but got " + assoc
					+ " in " + value, context);
			}
			String assocName = assoc.substring(0, equalLoc);
			if (associationsVisited.contains(assocName))
			{
				return new ParseResult.Fail(
					getTokenName()
						+ " does not allow multiple asspociations with the same name.  "
						+ "Found multiple: " + assocName + " in " + value,
					context);
			}
			associationsVisited.add(assocName);
			modifier.addAssociation(assoc);
		}
		VarModifier<?> vm = new VarModifier<>(varName, scope, modifier);
		context.getObjectContext().addToList(obj, ListKey.MODIFY, vm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<VarModifier<?>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.MODIFY);
		if (changes.hasRemovedItems())
		{
			Logging.errorPrint(getTokenName()
				+ " does not support removed items");
			return null;
		}
		if (changes.includesGlobalClear())
		{
			Logging.errorPrint(getTokenName() + " does not support .CLEAR");
			return null;
		}
		Collection<VarModifier<?>> added = changes.getAdded();
		List<String> modifiers = new ArrayList<>();
		if (added != null && !added.isEmpty())
		{
			for (VarModifier<?> vm : added)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(vm.getVarName());
				sb.append(Constants.PIPE);
				sb.append(unparseModifier(vm));
				modifiers.add(sb.toString());
			}
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
		PCGenModifier<?> modifier = vm.getModifier();
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
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
