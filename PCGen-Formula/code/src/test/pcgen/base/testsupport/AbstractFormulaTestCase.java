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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import junit.framework.TestCase;
import pcgen.base.formatmanager.FormatManagerLibrary;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.OptionalFormatFactory;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.ImplementedScope;
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
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.GlobalVarScoped;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.util.FormatManager;

public abstract class AbstractFormulaTestCase
{

	private NaiveScopeManager scopeManager;
	private FormulaManager formulaManager;
	private ScopeInstance globalInstance;
	private SupplierValueStore valueStore;

	private OperatorLibrary opLibrary;
	private ManagerFactory managerFactory;

	@BeforeEach
	protected void setUp()
	{
		/**
		 * The SimpleOperatorLibrary for this FormulaSetupFactory.
		 */
		opLibrary = FormulaUtilities.loadBuiltInOperators(new SimpleOperatorLibrary());
		managerFactory = new ManagerFactory(opLibrary);
		FormulaSetupFactory setup = new FormulaSetupFactory();
		scopeManager = new NaiveScopeManager();
		setup.setScopeManagerSupplier(() -> scopeManager);
		valueStore = new SupplierValueStore();
		setup.setValueStoreSupplier(() -> valueStore);
		formulaManager = setup.generate();
		valueStore.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.addSolverFormat(FormatUtilities.BOOLEAN_MANAGER, () -> false);
		globalInstance = formulaManager.getScopeInstanceFactory().get("Global",
			Optional.of(new GlobalVarScoped("Global")));
	}

	@AfterEach
	protected void tearDown()
	{
		scopeManager = null;
		formulaManager = null;
		globalInstance = null;
		valueStore = null;
		opLibrary = null;
	}

	public void isValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, Optional<FormatManager<?>> assertedFormat)
	{
		Objects.requireNonNull(assertedFormat);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = managerFactory.generateFormulaSemantics(
			getFormulaManager(), getScopeManager().getImplementedScope("Global"));
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
		//And deeply check arrays
		else if (valueOf.getClass().isArray() && result.getClass().isArray())
		{
			if (Arrays.deepEquals((Object[]) valueOf, (Object[]) result))
			{
				return;
			}
		}
		String valueSimpleName = valueOf.getClass().getSimpleName();
		String resultSimpleName = result.getClass().getSimpleName();
		if (valueOf.getClass().isArray())
		{
			valueOf = Arrays.asList((Object[]) valueOf);
		}
		if (result.getClass().isArray())
		{
			result = Arrays.asList((Object[]) result);
		}
		TestCase.fail(
			"Expected " + valueSimpleName + " (" + valueOf + ") for Formula: "
				+ formula + ", was " + result + " (" + resultSimpleName + ")");
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
			getFormulaManager(), getScopeManager().getImplementedScope("Global"));
		FormulaSemantics finSemantics = semantics.getWith(FormulaSemantics.ASSERTED, assertedFormat);
		assertThrows(SemanticsFailureException.class, () -> semanticsVisitor.visit(node, finSemantics));
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
		assertLegalVariable(formula, "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> variableID = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

	protected VariableID<Boolean> getBooleanVariable(String formula)
	{
		assertLegalVariable(formula, "Global", FormatUtilities.BOOLEAN_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Boolean> variableID = (VariableID<Boolean>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

	protected WriteableFunctionLibrary getFunctionLibrary()
	{
		return (WriteableFunctionLibrary) formulaManager.get(FormulaManager.FUNCTION);
	}

	protected OperatorLibrary getOperatorLibrary()
	{
		return opLibrary;
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

	protected void assertLegalVariable(String varName, String scopeName,
		FormatManager<?> manager)
	{
		getVariableLibrary().assertLegalVariableID(varName,
			getImplementedScope(scopeName), manager);
	}

	protected ImplementedScope getImplementedScope(String name)
	{
		return scopeManager.getImplementedScope(name);
	}

	protected ScopeInstanceFactory getInstanceFactory()
	{
		return formulaManager.getScopeInstanceFactory();
	}

	protected FormulaManager getFormulaManager()
	{
		return formulaManager;
	}

	protected NaiveScopeManager getScopeManager()
	{
		return scopeManager;
	}

	protected ManagerFactory getManagerFactory()
	{
		return managerFactory;
	}
	
	protected SupplierValueStore getValueStore()
	{
		return valueStore;
	}

	protected void addFunction(FormulaFunction ff)
	{
		((WriteableFunctionLibrary) getFormulaManager().get(FormulaManager.FUNCTION)).addFunction(ff);
	}

	protected ScopeInstance getScopeInstance(String scopeName, VarScoped vs)
	{
		return getInstanceFactory().get(scopeName, Optional.of(vs));
	}

	public ScopeInstance getGlobalInstance(String name)
	{
		return formulaManager.getScopeInstanceFactory().get("Global",
			Optional.of(new GlobalVarScoped("Global")));
	}

	public FormatManagerLibrary getInitializedFormatManager()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		FormatUtilities.loadDefaultFactories(library);
		return library;
	}

	protected VariableID<Optional<Number>> getOptionalVariable(String formula)
	{
		FormatManager<Optional<Number>> formatManager = getOptionalFormatManager();
		VariableLibrary variableLibrary = getVariableLibrary();
		assertLegalVariable(formula, "Global", formatManager);
		@SuppressWarnings("unchecked")
		VariableID<Optional<Number>> variableID =
				(VariableID<Optional<Number>>) variableLibrary
					.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

	protected FormatManager<Optional<Number>> getOptionalFormatManager()
	{
		FormatManagerLibrary library = getInitializedFormatManager();
		@SuppressWarnings("unchecked")
		FormatManager<Optional<Number>> formatManager =
				(FormatManager<Optional<Number>>) new OptionalFormatFactory()
					.build(Optional.empty(), Optional.of("NUMBER"), library);
		return formatManager;
	}

	protected <T> void setVariable(VariableID<T> varID, T value)
	{
		getVariableStore().put(varID, value);
	}

	protected VariableID<Number[]> getNumberArrayVar(String formula)
	{
		assertLegalVariable(formula, "Global", TestUtilities.NUMBER_ARRAY_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number[]> variableID = (VariableID<Number[]>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}
}
