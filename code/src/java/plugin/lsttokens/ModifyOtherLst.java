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
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.formula.local.DefinedWrappingLibrary;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.rules.context.AbstractObjectContext.DummyCDOMObject;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMInterfaceToken;
import pcgen.rules.persistence.token.ParseResult;

import plugin.lsttokens.ModifyLst.ModifyException;

/**
 * Implements the MODIFYOTHER token for remotely modifying variables in the new variable
 * system.
 */
public class ModifyOtherLst extends AbstractNonEmptyToken<VarHolder>
		implements CDOMInterfaceToken<VarContainer, VarHolder>
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

		String groupingName = sep.next();
		GroupingCollection<?> group = subContext.getGrouping(lvs, groupingName);
		if (group == null)
		{
			return new ParseResult.Fail(getTokenName() + " unable to build group from: " + groupingName);
		}
		StringJoiner sb = new StringJoiner("|");
		sep.forEachRemaining(sb::add);
		PCGenScope scope = subContext.getActiveScope();
		try
		{
			VarModifier<?> vm = ModifyLst.parseModifyInfo(subContext, sb.toString(),
				scope, generateFormulaManager(context, scope), getTokenName(), 2);
			obj.addRemoteModifier(new RemoteModifier<>(group, vm));
		}
		catch (ModifyException e)
		{
			return new ParseResult.Fail(e.getMessage());
		}
		return ParseResult.SUCCESS;
	}

	private final FormulaManager generateFormulaManager(LoadContext context, PCGenScope scope)
	{
		FormulaManager formulaManager =
				context.getVariableContext().getFormulaManager();
		FunctionLibrary functionManager = formulaManager.get(FormulaManager.FUNCTION);
		boolean modified = false;
		Optional<FormatManager<?>> sourceFormatManager = scope.getFormatManager(context);
		if (sourceFormatManager.isPresent())
		{
			functionManager = new DefinedWrappingLibrary(functionManager,
				"source", new DummyCDOMObject(), sourceFormatManager.get());
			modified = true;
		}
		Optional<FormatManager<?>> targetFormatManager = scope.getFormatManager(context);
		if (targetFormatManager.isPresent())
		{
			functionManager = new DefinedWrappingLibrary(functionManager,
				"target", new DummyCDOMObject(), targetFormatManager.get());
			modified = true;
		}
		if (!modified)
		{
			//Fine then :P
			return formulaManager;
		}
		return formulaManager.getWith(FormulaManager.FUNCTION, functionManager);
	}

	@Override
	public String[] unparse(LoadContext context, VarContainer obj)
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
			sb.append(ModifyLst.unparseModifier(vm));
			modifiers.add(sb.toString());
		}
		if (modifiers.isEmpty())
		{
			//Legal
			return null;
		}
		return modifiers.toArray(new String[0]);
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
