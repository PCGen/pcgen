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
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMInterfaceToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The MODIFY token defined by ModifyLst defines a calculation to be performed in the
 * (new) formula system.
 */
public class ModifyLst extends AbstractNonEmptyToken<VarHolder>
		implements CDOMInterfaceToken<VarContainer, VarHolder>
{

	@Override
	public String getTokenName()
	{
		return "MODIFY";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, VarHolder obj, String value)
	{
		try
		{
			PCGenScope scope = context.getActiveScope();
			VarModifier<?> varModifier =
					parseModifyInfo(context, value, scope, getTokenName(), 0);
			obj.addModifier(varModifier);
		}
		catch (ModifyException e)
		{
			return new ParseResult.Fail(e.getMessage());
		}
		return ParseResult.SUCCESS;
	}

	/**
	 * Parses the 3 arguments plus associations that are part of a token doing a
	 * modification.
	 * 
	 * @param context
	 *            The LoadContext for processing
	 * @param value
	 *            The instructions of the modification
	 * @param scope
	 *            The scope in which the modification should be analyzed
	 * @param tokenName
	 *            The token name asking for this process (used for error messages)
	 * @param argsConsumed
	 *            The number of arguments consumed before delegating to this method
	 * @return a VarModifier containing the information in the given instructions
	 * @throws ModifyException
	 *             if the parsing failed. The message contains information about the error
	 */
	public static VarModifier<?> parseModifyInfo(LoadContext context,
		String value, PCGenScope scope, String tokenName, int argsConsumed) throws ModifyException
	{
		/*
		 * TODO CODE-3299 Need to check the object type of the VarHolder to make sure it
		 * is legal. Note it's a proxy, so a @ReadOnly method needs to be used to support
		 * the analysis.
		 */
		ParsingSeparator sep = new ParsingSeparator(value, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		if (!sep.hasNext())
		{
			throw new ModifyException(tokenName + " may not be empty");
		}

		String varName = sep.next();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			throw new ModifyException(tokenName + " found invalid var name: " + varName
				+ "(scope: " + scope.getName() + ")");
		}
		if (!sep.hasNext())
		{
			throw new ModifyException(
				tokenName + " needed argument #" + (argsConsumed + 2) + ": " + value);
		}
		String modIdentification = sep.next();
		if (!sep.hasNext())
		{
			throw new ModifyException(
				tokenName + " needed argument # " + (argsConsumed + 3) + ": " + value);
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
			throw new ModifyException(
				tokenName + " Modifier " + modIdentification + " had value "
					+ modInstructions + " but it was not valid: " + iae.getMessage());
		}
		Set<Object> associationsVisited = Collections.newSetFromMap(new CaseInsensitiveMap<>());
		while (sep.hasNext())
		{
			String assoc = sep.next();
			int equalLoc = assoc.indexOf('=');
			if (equalLoc == -1)
			{
				throw new ModifyException(
					tokenName + " was expecting = in an ASSOCIATION but got " + assoc
						+ " in " + value);
			}
			String assocName = assoc.substring(0, equalLoc);
			if (associationsVisited.contains(assocName))
			{
				throw new ModifyException(tokenName
					+ " does not allow multiple asspociations with the same name.  "
					+ "Found multiple: " + assocName + " in " + value);
			}
			associationsVisited.add(assocName);
			modifier.addAssociation(assoc);
		}
		return new VarModifier<>(varName, scope, modifier);
	}

	@Override
	public String[] unparse(LoadContext context, VarContainer obj)
	{
		List<String> modifiers = new ArrayList<>();
		for (VarModifier<?> vm : obj.getModifierArray())
		{
			StringBuilder sb = new StringBuilder();
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

	/**
	 * Unparses a VarModifier into the string of instructions used to produce it.
	 * 
	 * @param varModifier
	 *            The VarModifier to be unparsed
	 * @return The string of instructions for the given VarModifier
	 */
	public static String unparseModifier(VarModifier<?> varModifier)
	{
		FormulaModifier<?> modifier = varModifier.getModifier();
		String type = modifier.getIdentification();
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(Constants.PIPE);
		sb.append(modifier.getInstructions());
		Collection<String> assocs = modifier.getAssociationInstructions();
		if (assocs != null && !assocs.isEmpty())
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

	/**
	 * Exception to indicate something went wrong in the static processing of the 3
	 * arguments + associations on a modification token.
	 */
	public static class ModifyException extends Exception
	{

		/**
		 * Constructs a new ModifyException with the given message
		 * 
		 * @param message
		 *            The message indicating the error encountered
		 */
		public ModifyException(String message)
		{
			super(message);
		}
		
	}

}
