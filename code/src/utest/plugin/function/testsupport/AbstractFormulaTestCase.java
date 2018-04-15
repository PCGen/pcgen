/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.function.testsupport;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.MonitorableVariableStore;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.context.VariableContext;

public abstract class AbstractFormulaTestCase extends TestCase
{

	protected FormatManager<Number> numberManager = new NumberManager();
	protected FormatManager<String> stringManager = new StringManager();

	protected LoadContext context;
	private FormulaManager formulaManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		FacetLibrary.getFacet(LoadContextFacet.class).set(context.getDataSetID(),
			new WeakReference<>(context));
		VariableContext varContext = context.getVariableContext();
		varContext.addDefault(Number.class, getDMod(0, numberManager));
		varContext.addDefault(String.class, getDMod("", stringManager));
	}

	/**
	 * Force a given formula to be valid.
	 * 
	 * @param formula
	 *            The formula instructions to be checked
	 * @param node
	 *            The root node for processing the formula
	 * @param formatManager
	 *            The FormatManager indicating the format of the result of the formula
	 * @param assertedFormat
	 *            The asserted format
	 */
	public void isValid(String formula, SimpleNode node, FormatManager<?> formatManager,
		FormatManager<?> assertedFormat)
	{
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(assertedFormat);
		FormatManager<?> resultFormat =
				(FormatManager<?>) semanticsVisitor.visit(node, semantics);
		if (!formatManager.equals(resultFormat))
		{
			TestCase
				.fail("Expected Formula to return: " + formatManager.getIdentifierType()
					+ " but it returned: " + resultFormat.getIdentifierType());
		}
	}

	public void isStatic(String formula, SimpleNode node, boolean b)
	{
		StaticVisitor staticVisitor =
				new StaticVisitor(getFormulaManager().get(FormulaManager.FUNCTION));
		boolean isStat = ((Boolean) staticVisitor.visit(node, null)).booleanValue();
		if (isStat != b)
		{
			TestCase.fail("Expected Static (" + b + ") Formula: " + formula);
		}
	}

	public void evaluatesTo(String formula, SimpleNode node, Object valueOf)
	{
		EvaluationManager manager = generateManager();
		Object result = new EvaluateVisitor().visit(node, manager);
		if (result.equals(valueOf))
		{
			return;
		}
		//Try ints as double as well just in case (temporary)
		if (valueOf instanceof Integer)
		{
			if (result.equals(valueOf))
			{
				return;
			}
		}
		//Give Doubles a bit of fuzz
		else if (valueOf instanceof Double)
		{
			if (TestUtilities.doubleEqual(((Double) valueOf).doubleValue(),
				((Number) result).doubleValue(), TestUtilities.SMALL_ERROR))
			{
				return;
			}
		}
		TestCase.fail("Expected " + valueOf.getClass().getSimpleName() + " (" + valueOf
			+ ") for Formula: " + formula + ", was " + result + " ("
			+ result.getClass().getSimpleName() + ")");
	}

	protected void isNotValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, FormatManager<?> assertedFormat)
	{
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(assertedFormat);
		try
		{
			semanticsVisitor.visit(node, semantics);
			TestCase.fail("Expected Invalid Formula: " + formula + " but was valid");
		}
		catch (SemanticsFailureException e)
		{
			//Expected
		}
	}

	protected List<VariableID<?>> getVariables(SimpleNode node)
	{
		DependencyManager fdm = generateDependencyManager(null);
		new DependencyVisitor().visit(node, fdm);
		return fdm.get(DependencyManager.VARIABLES).get().getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getGlobalScopeInst().getLegalScope(), numberManager);
		return (VariableID<Number>) variableLibrary.getVariableID(getGlobalScopeInst(),
			formula);
	}

	protected VariableID<Boolean> getBooleanVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getGlobalScopeInst().getLegalScope(), FormatUtilities.BOOLEAN_MANAGER);
		return (VariableID<Boolean>) variableLibrary.getVariableID(getGlobalScopeInst(),
			formula);
	}

	protected WriteableFunctionLibrary getFunctionLibrary()
	{
		return (WriteableFunctionLibrary) getFormulaManager()
			.get(FormulaManager.FUNCTION);
	}

	protected OperatorLibrary getOperatorLibrary()
	{
		return getFormulaManager().getOperatorLibrary();
	}

	protected VariableLibrary getVariableLibrary()
	{
		return getFormulaManager().getFactory();
	}

	protected WriteableVariableStore getVariableStore()
	{
		return (WriteableVariableStore) getFormulaManager().get(FormulaManager.RESULTS);
	}

	protected LegalScope getGlobalScope()
	{
		return getGlobalScopeInst().getLegalScope();
	}

	protected ScopeInstance getGlobalScopeInst()
	{
		return getFormulaManager().getScopeInstanceFactory()
			.getGlobalInstance(GlobalScope.GLOBAL_SCOPE_NAME);
	}

	protected FormulaManager getFormulaManager()
	{
		if (formulaManager == null)
		{
			formulaManager = context.getVariableContext().getFormulaManager()
				.getWith(FormulaManager.RESULTS, new MonitorableVariableStore());
		}
		return formulaManager;
	}

	protected FormulaSemantics generateFormulaSemantics(FormatManager<?> assertedFormat)
	{
		Optional<FormatManager<?>> format =
				(assertedFormat == null) ? Optional.empty() : Optional.of(assertedFormat);
		return new FormulaSemantics()
			.getWith(FormulaSemantics.FMANAGER, formulaManager)
			.getWith(FormulaSemantics.SCOPE, getGlobalScope())
			.getWith(FormulaSemantics.ASSERTED, format)
			.getWith(ManagerKey.CONTEXT, context);
	}

	private DependencyManager generateDependencyManager(FormatManager<?> assertedFormat)
	{
		Optional<FormatManager<?>> format =
				(assertedFormat == null) ? Optional.empty() : Optional.of(assertedFormat);
		return new DependencyManager(formulaManager)
			.getWith(DependencyManager.INSTANCE, getGlobalScopeInst())
			.getWith(DependencyManager.ASSERTED, format);
	}

	public EvaluationManager generateManager()
	{
		return new EvaluationManager()
			.getWith(EvaluationManager.FMANAGER, getFormulaManager())
			.getWith(EvaluationManager.INSTANCE, getGlobalScopeInst())
			.getWith(EvaluationManager.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER))
			.getWith(ManagerKey.CONTEXT, context);
	}

	private <T> Modifier<T> getDMod(T o, final FormatManager<T> f)
	{
		return new Modifier<T>()
		{

			@Override
			public T process(EvaluationManager manager)
			{
				return o;
			}

			@Override
			public void getDependencies(DependencyManager fdm)
			{
			}

			@Override
			public long getPriority()
			{
				return 0;
			}

			@Override
			public FormatManager<T> getVariableFormat()
			{
				return f;
			}

			@Override
			public String getIdentification()
			{
				return "SET";
			}

			@Override
			public String getInstructions()
			{
				return "DEFAULT";
			}
		};
	}

}
