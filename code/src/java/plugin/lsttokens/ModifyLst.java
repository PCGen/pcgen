/*
 * Copyright 2014-15 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import java.util.List;

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class ModifyLst implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MODIFY";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
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
		String varName = sep.next();
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
		int priorityNumber = 0; //Defaults to zero
		if (sep.hasNext())
		{
			String priority = sep.next();
			if (priority.length() < 10)
			{
				return new ParseResult.Fail(getTokenName()
					+ " was expecting PRIORITY= but got " + priority + " in "
					+ value, context);
			}
			if ("PRIORITY=".equalsIgnoreCase(priority.substring(0, 9)))
			{
				try
				{
					priorityNumber = Integer.parseInt(priority.substring(9));
				}
				catch (NumberFormatException e)
				{
					return new ParseResult.Fail(getTokenName()
						+ " requires Priority to be an integer: "
						+ priority.substring(9) + " was not an integer");
				}
				if (priorityNumber < 0)
				{
					return new ParseResult.Fail(getTokenName()
						+ " Priority requires an integer >= 0. "
						+ priorityNumber + " was not positive");
				}
			}
			else
			{
				return new ParseResult.Fail(getTokenName()
					+ " was expecting PRIORITY=x but got " + priority + " in "
					+ value, context);
			}
			if (sep.hasNext())
			{
				return new ParseResult.Fail(getTokenName()
					+ " had too many arguments: " + value, context);
			}
		}
		ScopeInstance scopeInst = context.getActiveScope();
		LegalScope scope = scopeInst.getLegalScope();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(
				getTokenName() + " found invalid var name: " + varName
					+ "(scope: " + scope.getName() + ") Modified on "
					+ obj.getClass().getSimpleName() + " " + obj.getKeyName(),
				context);
		}
		FormatManager<?> format =
				context.getVariableContext().getVariableFormat(scope, varName);
		return finishProcessing(context, obj, format, varName,
			modIdentification, modInstructions, priorityNumber);
	}

	private <T> ParseResult finishProcessing(LoadContext context,
		CDOMObject obj, FormatManager<T> formatManager, String varName,
		String modIdentification, String modInstructions, int priorityNumber)
	{
		ScopeInstance scopeInst = context.getActiveScope();
		LegalScope scope = scopeInst.getLegalScope();
		PCGenModifier<T> modifier;
		try
		{
			modifier =
					context.getVariableContext().getModifier(modIdentification,
						modInstructions, priorityNumber, scope, formatManager);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " Modifier "
				+ modIdentification + " had value " + modInstructions
				+ " but it was not valid: " + iae.getMessage(), context);
		}
		VarModifier<T> vm = new VarModifier<>(varName, scope, modifier);
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
		if (added != null && added.size() > 0)
		{
			for (VarModifier<?> vm : added)
			{
				String modText = unparseModifier(vm);
				StringBuilder sb = new StringBuilder();
				sb.append(vm.getVarName());
				sb.append(Constants.PIPE);
				sb.append(modText);
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
		int userPriority = modifier.getUserPriority();
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(Constants.PIPE);
		sb.append(modifier.getInstructions());
		if (userPriority > 0)
		{
			sb.append(Constants.PIPE);
			sb.append("PRIORITY=");
			sb.append(userPriority);
		}
		return sb.toString();
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
