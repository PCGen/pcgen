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

import java.util.Collection;
import java.util.Objects;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.DynamicSolverManager;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SolverFactory;
import pcgen.base.solver.SolverManager;
import pcgen.base.util.ComplexResult;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.cdom.formula.VariableChannel;
import pcgen.cdom.formula.VariableChannelFactory;
import pcgen.cdom.formula.VariableChannelFactoryInst;
import pcgen.cdom.formula.scope.LegalScopeUtilities;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.persistence.MasterModifierFactory;
import pcgen.util.Logging;

/**
 * A VariableContext is responsible for managing variable items during the load of data
 * and (in some cases) subsequently while the data set associated with the parent
 * LoadContext is operating.
 */
public class VariableContext implements VariableChannelFactory, VariableLibrary
{
	/**
	 * This is the FormulaSetupFactory for this VariableContext. This is used to generate
	 * a FormulaManager for each PC.
	 */
	private final FormulaSetupFactory formulaSetupFactory = new FormulaSetupFactory();

	/**
	 * This is the ManagerFactory for this VariableContext. This is used to generate an
	 * EvaluationManager, FormulaSemantics and other relevant items provided to the
	 * formula system during formula processing.
	 */
	private final ManagerFactory managerFactory;

	/*
	 * There are a series of items we override in the FormulaSetupFactory, and keep
	 * locally for certain types of processing.
	 */
	/**
	 * The FunctionLibrary for the FormulaSetupFactory and this VariableContext. Local so
	 * that we can add functions based on plugins and data.
	 */
	private final WriteableFunctionLibrary myFunctionLibrary = new SimpleFunctionLibrary();

	/**
	 * The SolverFactory for the FormulaSetupFactory and this VariableContext. Local so
	 * that we can set the defaults for variable formats from data.
	 */
	private final SolverFactory solverFactory = new SolverFactory();

	/**
	 * The LegalScopeManager for the FormulaSetupFactory and this VariableContext. Local
	 * so that we can load scopes based on data (like DYNAMICSCOPE:) and provide it to the
	 * VariableManager.
	 */
	private final ScopeManagerInst legalScopeManager = new ScopeManagerInst();

	/**
	 * The VariableManager for the FormulaSetupFactory and this VariableContext. Local so
	 * the data can assert legal variables.
	 */
	private final VariableManager variableManager = new VariableManager(legalScopeManager);

	/**
	 * The MasterModifierFactory for this VariableContext.
	 * 
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it is loaded
	 * from plugins.
	 */
	private MasterModifierFactory modFactory = null;

	/**
	 * The naive FormulaManager for this VariableContext. This serves only as a base item
	 * for loading formulas, not for evaluation.
	 * 
	 * Each PC will also have its own FormulaManager (which will contain a dedicated
	 * VariableStore for that PC). This specific FormulaManager is derived from this
	 * FormulaManager in generateSolverManager().
	 * 
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it is loaded
	 * from plugins.
	 */
	private FormulaManager loadFormulaManager = null;

	/**
	 * Contains a VariableChannelFactory used to develop VariableChannels for this
	 * VariableContext.
	 */
	private VariableChannelFactoryInst variableChannelFactory = new VariableChannelFactoryInst();

	/**
	 * Constructs a new VariableContext with the given ManagerFactory.
	 * 
	 * @param managerFactory
	 *            The ManagerFactory used to generate managers for formula evaluation by
	 *            all items loaded while this VariableContext is active
	 */
	public VariableContext(ManagerFactory managerFactory)
	{
		this.managerFactory = Objects.requireNonNull(managerFactory);
		for (FormulaFunction f : PluginFunctionLibrary.getInstance().getFunctions())
		{
			myFunctionLibrary.addFunction(f);
		}
		FormulaUtilities.loadBuiltInFunctions(myFunctionLibrary);
		LegalScopeUtilities.loadLegalScopeLibrary(legalScopeManager);
		formulaSetupFactory.setFunctionLibrarySupplier(() -> myFunctionLibrary);
		formulaSetupFactory.setSolverFactorySupplier(() -> solverFactory);
		formulaSetupFactory.setLegalScopeManagerSupplier(() -> legalScopeManager);
		formulaSetupFactory.setVariableLibraryFunction(lsl -> variableManager);
	}

	/**
	 * Returns the FormulaManager for this VariableContext.
	 * 
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it is loaded
	 * from plugins (so care should be taken to only call this method after the "Global"
	 * scope is loaded).
	 * 
	 * @return The FormulaManager for this VariableContext
	 */
	public FormulaManager getFormulaManager()
	{
		if (loadFormulaManager == null)
		{
			loadFormulaManager = formulaSetupFactory.generate();
		}
		return loadFormulaManager;
	}

	/*
	 * Lazy instantiation to avoid trying to pull the "Global" scope before it is loaded
	 * from plugins
	 */
	private MasterModifierFactory getModFactory()
	{
		if (modFactory == null)
		{
			modFactory = new MasterModifierFactory(getFormulaManager());
		}
		return modFactory;
	}

	/**
	 * Returns a FormulaModifier based on the given information.
	 * 
	 * @param modType
	 *            The type of the modifier to be generated (e.g. "ADD")
	 * @param instructions
	 *            The instructions for the modifier
	 * @param varScope
	 *            The PCGenScope in which the FormulaModifier is operating
	 * @param formatManager
	 *            The FormatManager indicating the format of the object being operated on
	 *            by the FormulaModifier
	 * @return a FormulaModifier based on the given information
	 */
	public <T> FormulaModifier<T> getModifier(String modType, String instructions, PCGenScope varScope,
		FormatManager<T> formatManager)
	{
		return getModFactory().getModifier(modType, instructions, managerFactory, varScope, formatManager);
	}

	/**
	 * Adds a FormulaFunction to the VariableContext.
	 * 
	 * Behavior is not defined if an an attempt is made to add null or a FormulaFunction
	 * with a null name. An exception may be thrown.
	 * 
	 * @param function
	 *            The FormulaFunction to be added to the VariableContext
	 */
	public void addFunction(FormulaFunction function)
	{
		myFunctionLibrary.addFunction(function);
	}

	/**
	 * Returns the PCGenScope for the given name.
	 * 
	 * @param name
	 *            The name of the PCGenScope to be returned
	 * @return The PCGenScope for the given name
	 */
	public PCGenScope getScope(String name)
	{
		return (PCGenScope) legalScopeManager.getScope(name);
	}

	/**
	 * Registers the given PCGenScope.
	 * 
	 * @param scope
	 *            The PCGenScope to be registered
	 */
	public void registerScope(PCGenScope scope)
	{
		legalScopeManager.registerScope(scope);
	}

	/**
	 * Returns a Collection of the LegalScope objects for this Context.
	 * 
	 * @return A Collection of the LegalScope objects for this Context
	 */
	public Collection<LegalScope> getScopes()
	{
		return legalScopeManager.getLegalScopes();
	}

	/**
	 * Validates the default values provided to the formula system. Effectively ensures
	 * that each default value is not dependent on variables or in other ways can't be
	 * directly calculated. Will report to the error system any results from the analysis.
	 */
	public void validateDefaults()
	{
		ComplexResult<Boolean> result = solverFactory.validateDefaults();
		if (!result.get())
		{
			result.getMessages().stream().forEach(Logging::errorPrint);
		}
	}

	/**
	 * Generates a SolverManager with a FormulaManager using the given
	 * WriteableVariableStore.
	 * 
	 * @param varStore
	 *            The WriteableVariableStore to be used for the returned SolverManager
	 * @return A SolverManager with a FormulaManager using the given
	 *         WriteableVariableStore
	 */
	public SolverManager generateSolverManager(WriteableVariableStore varStore)
	{
		FormulaManager derived = getFormulaManager().getWith(FormulaManager.RESULTS, varStore);
		return new DynamicSolverManager(derived, managerFactory, solverFactory, varStore);
	}

	/**
	 * Returns a "valid" NEPFormula for the given expression.
	 * 
	 * If the given expression does not represent a valid formula, then this
	 * will throw an IllegalArgumentException.
	 * 
	 * If the given expression does not return an object of the type in the
	 * given FormatManager, then this will throw an IllegalArgumentException.
	 * 
	 * @param activeScope
	 *            The PCGenScope in which the NEPFormula is established and
	 *            checked
	 * @param formatManager
	 *            The FormatManager in which the NEPFormula is established and
	 *            checked
	 * @param instructions
	 *            The String representation of the formula to be converted to a
	 *            NEPFormula
	 * @return a "valid" NEPFormula for the given expression
	 */
	public <T> NEPFormula<T> getValidFormula(PCGenScope activeScope, FormatManager<T> formatManager,
		String instructions)
	{
		return FormulaFactory.getValidFormula(instructions, managerFactory, getFormulaManager(), activeScope,
			formatManager);
	}

	/**
	 * Adds a relationship between a Solver format and a default Modifier for that format
	 * of Solver to this VariableContext.
	 * 
	 * The default Modifier MUST NOT depend on anything (it must be able to accept both a
	 * null ScopeInformation and null input value to its process method). (See
	 * SetNumberModifier for an example of this)
	 * 
	 * The default Modifier for a format of Solver may not be redefined for a
	 * SolverFactory. Once a given default Modifier has been established for a format of
	 * Solver, this method MUST NOT be called a second time for that format of Solver.
	 * 
	 * @param <T>
	 *            The format (class) of object changed by the given Modifier
	 * @param varFormat
	 *            The format of Solver for which the given Modifier should be the default
	 *            value
	 * @param defaultModifier
	 *            The Modifier to be used as the default Modifier for the given Solver
	 *            format
	 * @throws IllegalArgumentException
	 *             if either parameter is null, if the given Modifier has dependencies, or
	 *             if the given Solver format already has a default Modifier defined for
	 *             this SolverFactory
	 */
	public <T> void addDefault(Class<T> varFormat, Modifier<T> defaultModifier)
	{
		solverFactory.addSolverFormat(varFormat, defaultModifier);
	}

	/**
	 * Returns the default value for a given Format (provided as a Class).
	 * 
	 * @param <T>
	 *            The format (class) of object for which the default value should be
	 *            returned
	 * @param variableFormat
	 *            The Class (data format) for which the default value should be returned
	 * @return The default value for the given Format
	 */
	public <T> T getDefaultValue(Class<T> variableFormat)
	{
		return solverFactory.getDefault(variableFormat);
	}

	/**
	 * Returns true if there is a default value set for the given FormatManager.
	 * 
	 * @param formatManager
	 *            The FormatManager indicating the format for which the default value
	 *            should be returned
	 * @return true if there is a default value set for the given FormatManager; false
	 *         otherwise
	 */
	public boolean hasSolver(FormatManager<?> formatManager)
	{
		/*
		 * TODO This is an ugly hack. Note a fix has been pushed upstream in -formula, but
		 * will require a significant new library update that I don't want to mix in here.
		 */
		try
		{
			solverFactory.getSolver(formatManager);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}

	/*
	 * Begin: (Delegated) Items part of VariableLibrary interface
	 */
	@Override
	public boolean isLegalVariableID(LegalScope varScope, String varName)
	{
		return variableManager.isLegalVariableID(varScope, varName);
	}

	@Override
	public FormatManager<?> getVariableFormat(LegalScope varScope, String varName)
	{
		return variableManager.getVariableFormat(varScope, varName);
	}

	@Override
	public VariableID<?> getVariableID(ScopeInstance instance, String varName)
	{
		return variableManager.getVariableID(instance, varName);
	}

	@Override
	public void assertLegalVariableID(String varName, LegalScope varScope, FormatManager<?> formatManager)
	{
		variableManager.assertLegalVariableID(varName, varScope, formatManager);
	}
	/*
	 * End: (Delegated) Items part of VariableLibrary interface
	 */

	/*
	 * Begin: (Delegated) Items part of VariableChannelFactory interface
	 */
	@Override
	public VariableChannel<?> getChannel(CharID id, VarScoped owner, String name)
	{
		return variableChannelFactory.getChannel(id, owner, name);
	}

	@Override
	public VariableChannel<?> getGlobalChannel(CharID id, String name)
	{
		return variableChannelFactory.getGlobalChannel(id, name);
	}

	@Override
	public void disconnect(VariableChannel<?> variableChannel)
	{
		variableChannelFactory.disconnect(variableChannel);
	}
	/*
	 * End: (Delegated) Items part of VariableChannelFactory interface
	 */
}
