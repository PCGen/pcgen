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

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.analysis.FormulaValidity;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.solver.IndividualSetup;
import pcgen.base.solver.SplitFormulaSetup;
import pcgen.base.util.FormatManager;

public abstract class AbstractFormulaTestCase extends TestCase
{

	protected FormatManager<Number> numberManager = new NumberManager();
	protected FormatManager<Boolean> booleanManager = new BooleanManager();

	private SplitFormulaSetup setup;
	private IndividualSetup localSetup;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setup = new SplitFormulaSetup();
		setup.getLegalScopeLibrary().registerScope(
			new SimpleLegalScope(null, "Global"));
		localSetup = new IndividualSetup(setup, "Global");
	}

	public void isValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, Class<?> assertedFormat)
	{
		FormulaSemantics semantics =
				localSetup.getFormulaManager().isValid(node, getGlobalScope(),
					formatManager, assertedFormat);
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			TestCase.fail("Expected Valid Formula: "
				+ formula
				+ " but was told: "
				+ semantics.getInfo(FormulaSemanticsUtilities.SEM_REPORT)
					.getReport());
		}
	}

	public void isStatic(String formula, SimpleNode node, boolean b)
	{
		if (localSetup.getScopeInfo().isStatic(node) != b)
		{
			TestCase.fail("Expected Static (" + b + ") Formula: " + formula);
		}
	}

	public void evaluatesTo(String formula, SimpleNode node, Object valueOf)
	{
		Object result = localSetup.getScopeInfo().evaluate(node, Number.class, null);
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
		TestCase.fail("Expected " + valueOf.getClass().getSimpleName() + " ("
			+ valueOf + ") for Formula: " + formula + ", was " + result + " ("
			+ result.getClass().getSimpleName() + ")");
	}

	protected void isNotValid(String formula, SimpleNode node,
		FormatManager<?> formatManager, Class<?> assertedFormat)
	{
		FormulaSemantics semantics =
				localSetup.getFormulaManager().isValid(node, getGlobalScope(),
					formatManager, assertedFormat);
		FormulaValidity isValid =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID);
		if (isValid.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula
				+ " but was valid");
		}
	}

	protected List<VariableID<?>> getVariables(SimpleNode node)
	{
		DependencyManager fdm =
				DependencyManager.generate(getFormulaManager(),
					getGlobalScopeInst(), null);
		new DependencyVisitor().visit(node, fdm);
		return fdm.getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			localSetup.getGlobalScope(), numberManager);
		return (VariableID<Number>) variableLibrary.getVariableID(
			localSetup.getGlobalScopeInst(), formula);
	}

	protected VariableID<Boolean> getBooleanVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			localSetup.getGlobalScope(), booleanManager);
		return (VariableID<Boolean>) variableLibrary.getVariableID(
			localSetup.getGlobalScopeInst(), formula);
	}

	protected FunctionLibrary getFunctionLibrary()
	{
		return localSetup.getFormulaManager().getLibrary();
	}

	protected OperatorLibrary getOperatorLibrary()
	{
		return localSetup.getFormulaManager().getOperatorLibrary();
	}

	protected VariableLibrary getVariableLibrary()
	{
		return localSetup.getFormulaManager().getFactory();
	}

	protected WriteableVariableStore getVariableStore()
	{
		return (WriteableVariableStore) localSetup.getFormulaManager()
			.getResolver();
	}

	protected LegalScope getGlobalScope()
	{
		return localSetup.getGlobalScope();
	}

	protected ScopeInstance getGlobalScopeInst()
	{
		return localSetup.getGlobalScopeInst();
	}

	protected FormulaManager getFormulaManager()
	{
		return localSetup.getFormulaManager();
	}

	protected LegalScopeLibrary getScopeLibrary()
	{
		return setup.getLegalScopeLibrary();
	}
	
	protected ScopeInformation getScopeInfo()
	{
		return localSetup.getScopeInfo();
	}
}
