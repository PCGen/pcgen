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
package pcgen.rules.context;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.SimpleVariableStore;
import pcgen.base.solver.IndividualSetup;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SplitFormulaSetup;
import pcgen.base.util.ComplexResult;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.cdom.formula.scope.LegalScopeUtilities;
import pcgen.rules.persistence.MasterModifierFactory;
import pcgen.util.Logging;

public class VariableContext
{

	private final SplitFormulaSetup formulaSetup = new SplitFormulaSetup();
	private final ManagerFactory managerFactory;

	private MasterModifierFactory modFactory = null;
	private IndividualSetup dummySetup = null;

	public VariableContext(ManagerFactory fac)
	{
		formulaSetup.loadBuiltIns();
		managerFactory = Objects.requireNonNull(fac);
		PluginFunctionLibrary pfl = PluginFunctionLibrary.getInstance();
		List<Function> functions = pfl.getFunctions();
		for (Function f : functions)
		{
			formulaSetup.getFunctionLibrary().addFunction(f);
		}
		LegalScopeUtilities.loadLegalScopeLibrary(formulaSetup
			.getLegalScopeLibrary());
	}

	/*
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it
	 * is loaded from plugins
	 */
	private IndividualSetup getDummySetup()
	{
		if (dummySetup == null)
		{
			dummySetup = new IndividualSetup(formulaSetup, "Global",
				new SimpleVariableStore());
		}
		return dummySetup;
	}

	/*
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it
	 * is loaded from plugins
	 */
	private MasterModifierFactory getModFactory()
	{
		if (modFactory == null)
		{
			modFactory = new MasterModifierFactory(getDummySetup().getFormulaManager());
		}
		return modFactory;
	}

	/*
	 * For Tokens
	 */
	public <T> FormulaModifier<T> getModifier(String modType, String modValue,
		LegalScope varScope, FormatManager<T> formatManager)
	{
		return getModFactory().getModifier(modType, modValue, managerFactory,
			varScope, formatManager);
	}

	public Set<LegalScope> getKnownLegalScopes(String varName)
	{
		return formulaSetup.getVariableLibrary().getKnownLegalScopes(varName);
	}

	public boolean assertLegalVariableID(LegalScope varScope,
		FormatManager<?> formatManager, String varName)
	{
		return formulaSetup.getVariableLibrary().assertLegalVariableID(varName,
			varScope, formatManager);
	}

	public boolean isLegalVariableID(LegalScope varScope, String varName)
	{
		return formulaSetup.getVariableLibrary().isLegalVariableID(varScope,
			varName);
	}

	public <T> void addDefault(Class<T> varFormat, Modifier<T> defaultModifier)
	{
		formulaSetup.getSolverFactory().addSolverFormat(varFormat, defaultModifier);
	}

	public FormatManager<?> getVariableFormat(LegalScope varScope,
		String varName)
	{
		return formulaSetup.getVariableLibrary().getVariableFormat(varScope,
			varName);
	}

	public void addFunction(Function function)
	{
		formulaSetup.getFunctionLibrary().addFunction(function);
	}

	public LegalScope getScope(String name)
	{
		return formulaSetup.getLegalScopeLibrary().getScope(name);
	}

	/**
	 * Intended for Facets only...
	 */
	public SplitFormulaSetup getFormulaSetup()
	{
		return formulaSetup;
	}

	public ManagerFactory getManagerFactory()
	{
		return managerFactory;
	}

	/**
	 * Validates the default values provided to the formula system. Effectively ensures
	 * that each default value is not dependent on variables or in other ways can't be
	 * directly calculated. Will report to the error system any results from the analysis.
	 */
	public void validateDefaults()
	{
		ComplexResult<Boolean> result = formulaSetup.getSolverFactory().validateDefaults();
		if (!result.get())
		{
			result.getMessages().stream().forEach(Logging::errorPrint);
		}
	}

}
