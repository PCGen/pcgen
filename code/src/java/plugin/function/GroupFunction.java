/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

/**
 * This is a function that gets a list of objects that match the given Group specification
 * 
 * This function requires 2 arguments: (1) The Scope name (2) String representation of
 * the group
 */
public class GroupFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "Group";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		//This is a shortcut since allowArgs enforces both are ASTQuotString
		return true;
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 2)
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 2 got "
				+ args.length + ' ' + Arrays.asList(args));
		}
		if (!(args[0] instanceof ASTQuotString))
		{
			//Error
			throw new SemanticsFailureException(
				"Parse Error: Invalid first argument: Must be a String");
		}
		if (!(args[1] instanceof ASTQuotString))
		{
			//Error
			throw new SemanticsFailureException(
				"Parse Error: Invalid first argument: Must be a String");
		}

		//This will be a scope name
		String scopeName = ((ASTQuotString) args[0]).getText();
		LoadContext context = semantics.get(ManagerKey.CONTEXT);
		PCGenScope scope = context.getVariableContext().getScope(scopeName);
		Optional<FormatManager<?>> possibleFormatManager = scope.getFormatManager(context);
		if (possibleFormatManager.isEmpty())
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid first argument: Scope: " + scopeName
					+ " does not support Groups (no format)");
		}
		FormatManager<?> formatManager = possibleFormatManager.get();
		if (!(formatManager instanceof ReferenceManufacturer))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid first argument: Scope: " + scopeName
					+ " does not support Groups");
		}
		if (!(PCGenScoped.class
			.isAssignableFrom(formatManager.getManagedClass())))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid first argument: Scope: " + scopeName
					+ " must be Scoped");
		}

		return formatManager;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		@SuppressWarnings("PMD.PrematureDeclaration")
		String scopeName = (String) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED,
				Optional.of(FormatUtilities.STRING_MANAGER)));
		LoadContext context = manager.get(ManagerKey.CONTEXT);
		PCGenScope scope = context.getVariableContext().getScope(scopeName);
		//Was checked in allowArgs
		@SuppressWarnings("unchecked")
		ReferenceManufacturer<? extends PCGenScoped> refMfg =
				(ReferenceManufacturer<? extends PCGenScoped>) scope
					.getFormatManager(context).get();

		String groupingName = (String) args[1].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED,
				Optional.of(FormatUtilities.STRING_MANAGER)));

		Collection<? extends PCGenScoped> all = refMfg.getAllObjects();
		LoadContext subContext = context.dropIntoContext(scopeName);
		//Was checked in allowArgs
		@SuppressWarnings("unchecked")
		GroupingCollection<PCGenScoped> group =
				(GroupingCollection<PCGenScoped>) subContext.getGrouping(scope,
					"GROUP=" + groupingName);
		List<PCGenScoped> available = new ArrayList<>();
		all.forEach(object -> group.process(object, available::add));
		return available.toArray(new PCGenScoped[0]);
	}

	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		@SuppressWarnings("PMD.PrematureDeclaration")
		String format = ((ASTQuotString) args[0]).getText();
		AbstractReferenceContext refContext =
				manager.get(ManagerKey.CONTEXT).getReferenceContext();
		FormatManager<?> formatManager = refContext.getFormatManager(format);
		return Optional.of(formatManager);
	}

}
