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
import java.util.Objects;
import java.util.Set;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ObjectGrouping;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class ModifyOtherLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MODIFYOTHER";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	//MODIFYOTHER:EQUIPMENT|GROUP=Martial|EqCritRange|ADD|1
	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (obj instanceof Campaign)
		{
			return new ParseResult.Fail(getTokenName()
				+ " may not be used in Campaign Files.  "
				+ "Please use the Global Modifier file");
		}
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String scopeName = sep.next();
		/*
		 * Note lvs is implicitly defined as "not global" since the global scope
		 * is "" and thus would have failed the tests imposed by
		 * AbstractTokenWithSeparator
		 */
		final LegalScope lvs = context.getVariableContext().getScope(scopeName);
		if (lvs == null)
		{
			return new ParseResult.Fail(getTokenName()
				+ " found illegal variable scope: " + scopeName
				+ " as first argument: " + value);
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed 2nd argument: " + value);
		}
		String fullName = LegalScope.getFullName(lvs);
		LoadContext subContext = context.dropIntoContext(fullName);
		return continueParsing(subContext, obj, value, sep);
	}

	private <GT extends VarScoped> ParseResult continueParsing(
		LoadContext context, CDOMObject obj, String value, ParsingSeparator sep)
	{
		final LegalScope scope = context.getActiveScope();
		final String groupingName = sep.next();
		ObjectGrouping group;
		if (groupingName.startsWith("GROUP="))
		{
			final String groupName = groupingName.substring(6);

			group = new ObjectGrouping()
			{
				@Override
				public boolean contains(VarScoped item)
				{
					return Objects.equals(item.getLocalScopeName(),
						LegalScope.getFullName(scope)) && (item instanceof CDOMObject)
						&& ((CDOMObject) item).containsInList(ListKey.GROUP, groupName);
				}

				@Override
				public LegalScope getScope()
				{
					return scope;
				}

				@Override
				public String getIdentifier()
				{
					return "GROUP=" + groupName;
				}
			};
		}
		else if ("ALL".equals(groupingName))
		{
			group = new ObjectGrouping()
			{
				@Override
				public boolean contains(VarScoped item)
				{
					return Objects.equals(item.getLocalScopeName(),
						LegalScope.getFullName(scope));
				}

				@Override
				public LegalScope getScope()
				{
					return scope;
				}

				@Override
				public String getIdentifier()
				{
					return "ALL";
				}
			};
		}
		else
		{
			group = new ObjectGrouping()
			{
				@Override
				public boolean contains(VarScoped item)
				{
					return Objects.equals(item.getLocalScopeName(),
						LegalScope.getFullName(scope))
						&& item.getKeyName().equalsIgnoreCase(groupingName);
				}

				@Override
				public LegalScope getScope()
				{
					return scope;
				}

				@Override
				public String getIdentifier()
				{
					return groupingName;
				}
			};
		}

		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed 3rd argument: " + value);
		}
		String varName = sep.next();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(getTokenName() + " found invalid var name: "
				+ varName + "(scope: " + LegalScope.getFullName(scope) + ") Modified on "
				+ obj.getClass().getSimpleName() + ' ' + obj.getKeyName());
		}
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed 4th argument: " + value);
		}
		String modIdentification = sep.next();
		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName()
				+ " needed 5th argument: " + value);
		}
		String modInstructions = sep.next();
		FormulaModifier<?> modifier;
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
				+ " but it was not valid: " + iae.getMessage());
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
					+ " in " + value);
			}
			String assocName = assoc.substring(0, equalLoc);
			if (associationsVisited.contains(assocName))
			{
				return new ParseResult.Fail(
					getTokenName()
						+ " does not allow multiple asspociations with the same name.  "
						+ "Found multiple: " + assocName + " in " + value);
			}
			associationsVisited.add(assocName);
			modifier.addAssociation(assoc);
		}
		VarModifier<?> vm = new VarModifier<>(varName, scope, modifier);
		RemoteModifier<?> rm = new RemoteModifier<>(group, vm);
		context.getObjectContext().addToList(obj, ListKey.REMOTE_MODIFIER, rm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<RemoteModifier<?>> changes =
				context.getObjectContext().getListChanges(obj,
					ListKey.REMOTE_MODIFIER);
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
		Collection<RemoteModifier<?>> added = changes.getAdded();
		List<String> modifiers = new ArrayList<>();
		if (added != null && !added.isEmpty())
		{
			for (RemoteModifier<?> rm : added)
			{
				VarModifier<?> vm = rm.getVarModifier();
				StringBuilder sb = new StringBuilder();
				ObjectGrouping og = rm.getGrouping();
				sb.append(LegalScope.getFullName(og.getScope()));
				sb.append(Constants.PIPE);
				sb.append(og.getIdentifier());
				sb.append(Constants.PIPE);
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
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
