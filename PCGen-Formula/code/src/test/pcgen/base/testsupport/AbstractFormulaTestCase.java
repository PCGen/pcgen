/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.testsupport;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.inst.GlobalVarScoped;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SolverFactory;
import pcgen.base.util.FormatManager;

public abstract class AbstractFormulaTestCase extends TestCase
{

	private ManagerFactory managerFactory = new ManagerFactory(){};
	private ScopeManagerInst legalScopeLibrary;
	private FormulaManager formulaManager;
	private ScopeInstance globalInstance;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		FormulaSetupFactory setup = new FormulaSetupFactory();
		legalScopeLibrary = new ScopeManagerInst();
		legalScopeLibrary.registerScope(new SimpleLegalScope("Global"));
		setup.setLegalScopeManagerSupplier(() -> legalScopeLibrary);
		SolverFactory solverFactory = new SolverFactory();
		solverFactory.addSolverFormat(Number.class, new Modifier<Number>()
		{

			@Override
			public Number process(EvaluationManager manager)
			{
				return 0;
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
			public FormatManager<Number> getVariableFormat()
			{
				return FormatUtilities.NUMBER_MANAGER;
			}

			@Override
			public String getIdentification()
			{
				return "SET";
			}

			@Override
			public String getInstructions()
			{
				return "0";
			}
		});
		solverFactory.addSolverFormat(String.class, new Modifier<String>()
		{

			@Override
			public String process(EvaluationManager manager)
			{
				return "";
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
			public FormatManager<String> getVariableFormat()
			{
				return FormatUtilities.STRING_MANAGER;
			}

			@Override
			public String getIdentification()
			{
				return "SET";
			}

			@Override
			public String getInstructions()
			{
				return "";
			}
		});
		setup.setSolverFactorySupplier(() -> solverFactory);
		formulaManager = setup.generate();
		globalInstance = formulaManager.getScopeInstanceFactory().get("Global",
			Optional.of(new GlobalVarScoped("Global")));
	}

	public void isValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, Optional<FormatManager<?>> assertedFormat)
	{
		Objects.requireNonNull(assertedFormat);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = managerFactory.generateFormulaSemantics(
			getFormulaManager(), getInstanceFactory().getScope("Global"));
		semantics = semantics.getWith(FormulaSemantics.ASSERTED, assertedFormat);
		semanticsVisitor.visit(node, semantics);
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

	public void evaluatesTo(FormatManager<?> formatManager, String formula,
		SimpleNode node, Object valueOf)
	{
		EvaluationManager manager = generateManager();
		performEvaluation(formatManager, formula, node, valueOf, manager);
	}

	public void performEvaluation(FormatManager<?> formatManager, String formula,
		SimpleNode node, Object valueOf, EvaluationManager manager)
	{
		EvaluationManager evalManager =
				manager.getWith(EvaluationManager.ASSERTED, Optional.of(formatManager));
		Object result = new EvaluateVisitor().visit(node, evalManager);
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

	public EvaluationManager generateManager()
	{
		EvaluationManager em =
				managerFactory.generateEvaluationManager(getFormulaManager());
		return em.getWith(EvaluationManager.INSTANCE, getGlobalScopeInst());
	}

	public EvaluationManager generateManager(Object input)
	{
		return generateManager().getWith(EvaluationManager.INPUT, input);
	}

	protected void isNotValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, Optional<FormatManager<?>> assertedFormat)
	{
		Objects.requireNonNull(assertedFormat);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = managerFactory.generateFormulaSemantics(
			getFormulaManager(), getInstanceFactory().getScope("Global"));
		semantics = semantics.getWith(FormulaSemantics.ASSERTED, assertedFormat);
		try
		{
			semanticsVisitor.visit(node, semantics);
			TestCase.fail(
				"Expected Invalid Formula: " + formula + " but was valid");
		}
		catch (SemanticsFailureException e)
		{
			//Expected
		}
	}

	protected List<VariableID<?>> getVariables(SimpleNode node)
	{
		DependencyManager fdm = managerFactory
			.generateDependencyManager(getFormulaManager(), getGlobalScopeInst());
		fdm = managerFactory.withVariables(fdm);
		new DependencyVisitor().visit(node, fdm);
		return fdm.get(DependencyManager.VARIABLES).get().getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> variableID = (VariableID<Number>) variableLibrary
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

	protected VariableID<Boolean> getBooleanVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Boolean> variableID = (VariableID<Boolean>) variableLibrary
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

	protected WriteableFunctionLibrary getFunctionLibrary()
	{
		return (WriteableFunctionLibrary) formulaManager.get(FormulaManager.FUNCTION);
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
		return (WriteableVariableStore) getFormulaManager()
			.get(FormulaManager.RESULTS);
	}

	protected final ScopeInstance getGlobalScopeInst()
	{
		return globalInstance;
	}

	protected ScopeInstanceFactory getInstanceFactory()
	{
		return formulaManager.getScopeInstanceFactory();
	}

	protected FormulaManager getFormulaManager()
	{
		return formulaManager;
	}

	protected ScopeManagerInst getScopeLibrary()
	{
		return legalScopeLibrary;
	}

	protected ManagerFactory getManagerFactory()
	{
		return managerFactory;
	}

	protected ScopeInstance getScopeInstance(String scopeName, VarScoped vs)
	{
		return getInstanceFactory().get(scopeName, Optional.of(vs));
	}
}
