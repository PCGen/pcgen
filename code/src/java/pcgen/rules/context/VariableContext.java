/*
 * Copyright 2014-9 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import pcgen.cdom.calculation.FormulaModifier;
import pcgen.cdom.calculation.IgnoreVariables;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.DependencyStrategy;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.ImplementedScopeLibrary;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.SimpleSolverManager;
import pcgen.base.solver.SolverManager;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.cdom.formula.VariableChannel;
import pcgen.cdom.formula.VariableChannelFactory;
import pcgen.cdom.formula.VariableChannelFactoryInst;
import pcgen.cdom.formula.VariableWrapper;
import pcgen.cdom.formula.VariableWrapperFactory;
import pcgen.cdom.formula.VariableWrapperFactoryInst;
import pcgen.cdom.formula.scope.LegalScopeUtilities;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.helper.ReferenceDependency;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.ModifierFactory;

/**
 * A VariableContext is responsible for managing variable items during the load of data
 * and (in some cases) subsequently while the data set associated with the parent
 * LoadContext is operating.
 */
public class VariableContext implements VariableChannelFactory,
		VariableWrapperFactory, VariableLibrary
{

	/**
	 * The FunctionLibrary for this VariableContext. Local so that we can add functions
	 * based on plugins and data.
	 */
	private final WriteableFunctionLibrary myFunctionLibrary = new SimpleFunctionLibrary();

	/**
	 * The ValueStore for this VariableContext. Local so that we can set the defaults for
	 * variable formats from data.
	 */
	private final SupplierValueStore myValueStore = new SupplierValueStore();

	/**
	 * The ImplementedScopeLibrary for this VariableContext. Local so that we can load
	 * scopes based on data and provide it to the VariableManager.
	 */
	private final ImplementedScopeLibrary scopeLibrary = new ImplementedScopeLibrary();

	/**
	 * The OperatorLibrary for this VariableContext.
	 */
	private final OperatorLibrary operatorLibrary;

	/**
	 * The ScopeInstanceFactory for this VariableContext.
	 */
	private final ScopeInstanceFactory scopeInstanceFactory;

	/**
	 * The VariableManager for this VariableContext. Local so the data can assert legal
	 * variables.
	 */
	private final VariableManager variableManager;

	/**
	 * The ManagerFactory for this VariableContext. Lazily initialized because it needs
	 * a VariableStore, which isn't available until a PC is created.
	 */
	private PCGenManagerFactory managerFactory;

	/**
	 * The LoadContext that owns this VariableContext.
	 */
	private final LoadContext loadContext;

	/**
	 * Contains a VariableChannelFactory used to develop VariableChannels for this
	 * VariableContext.
	 */
	private VariableChannelFactoryInst variableChannelFactory = new VariableChannelFactoryInst();

	/**
	 * Contains a VariableWrapperFactory used to develop VariableWrappers for this
	 * VariableContext.
	 */
	private VariableWrapperFactoryInst variableWrapperFactory =
			new VariableWrapperFactoryInst();

	/**
	 * Constructs a new VariableContext for the given LoadContext.
	 *
	 * @param loadContext
	 *            The LoadContext that owns this VariableContext
	 */
	public VariableContext(LoadContext loadContext)
	{
		this.loadContext = Objects.requireNonNull(loadContext);
		for (FormulaFunction f : PluginFunctionLibrary.getInstance().getFunctions())
		{
			myFunctionLibrary.addFunction(f);
		}
		FormulaUtilities.loadBuiltInFunctions(myFunctionLibrary);
		LegalScopeUtilities.loadLegalScopeLibrary(scopeLibrary);
		operatorLibrary = FormulaUtilities.loadBuiltInOperators(new SimpleOperatorLibrary());
		scopeInstanceFactory = new SimpleScopeInstanceFactory(scopeLibrary);
		variableManager = new VariableManager(scopeLibrary, scopeLibrary,
			scopeInstanceFactory, myValueStore);
	}

	/**
	 * Returns the ManagerFactory for this VariableContext. Creates a base ManagerFactory
	 * using a default (empty) variable store for load-time formula checking.
	 *
	 * @return The ManagerFactory for this VariableContext
	 */
	public ManagerFactory getManagerFactory()
	{
		if (managerFactory == null)
		{
			managerFactory = new PCGenManagerFactory(loadContext, scopeLibrary,
				operatorLibrary, variableManager, myFunctionLibrary,
				new pcgen.base.formula.inst.SimpleVariableStore(), scopeInstanceFactory);
		}
		return managerFactory;
	}

	/**
	 * Returns a new ManagerFactory for a specific PC, using the given WriteableVariableStore.
	 *
	 * @param varStore
	 *            The WriteableVariableStore for the PC
	 * @return A new ManagerFactory for a PC
	 */
	public ManagerFactory getPCManagerFactory(WriteableVariableStore varStore)
	{
		return new PCGenManagerFactory(loadContext, scopeLibrary,
			operatorLibrary, variableManager, myFunctionLibrary,
			varStore, scopeInstanceFactory);
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
	public <T> FormulaModifier<T> getModifier(String modType,
		String instructions, PCGenScope varScope, FormatManager<T> formatManager)
	{
		Class<T> varClass = formatManager.getManagedClass();
		ModifierFactory<T> factory = TokenLibrary.getModifier(varClass, modType);
		if (factory == null)
		{
			throw new IllegalArgumentException(
				"Requested unknown ModifierType: " + varClass.getSimpleName() + " " + modType);
		}
		FormulaModifier<T> modifier =
				factory.getModifier(instructions, formatManager);

		ManagerFactory mf = getManagerFactory();
		FormulaSemantics semantics = mf.generateFormulaSemantics(varScope);
		semantics = semantics.getWith(FormulaSemantics.INPUT_FORMAT, Optional.of(formatManager));
		Optional<FormatManager<?>> scopeFormat = varScope.getFormatManager(loadContext);
		if (scopeFormat.isPresent())
		{
			pcgen.base.formula.base.FunctionLibrary functionLib =
				semantics.get(FormulaSemantics.FUNCTION);
			functionLib = new pcgen.cdom.formula.local.DefinedWrappingLibrary(
				functionLib, "this", new Object(), scopeFormat.get());
			semantics = semantics.getWith(FormulaSemantics.FUNCTION, functionLib);
		}
		try
		{
			modifier.isValid(semantics);
		}
		catch (SemanticsException e)
		{
			throw new IllegalArgumentException("Invalid Semantics on Formula: "
				+ modType + " ... " + e.getLocalizedMessage(), e);
		}

		/*
		 * getDependencies needs to be called during LST load, so that object references
		 * are captured
		 */
		DependencyManager fdm = mf.generateDependencyManager();
		fdm = fdm.getWith(DependencyManager.SCOPE, Optional.of(varScope));
		fdm = fdm.getWith(DependencyManager.VARSTRATEGY, Optional.<DependencyStrategy>of(new IgnoreVariables()));
		fdm = fdm.getWith(ManagerKey.REFERENCES, new ReferenceDependency());
		modifier.getDependencies(fdm);
		modifier.addReferences(fdm.get(ManagerKey.REFERENCES).getReferences());
		return modifier;
	}

	/**
	 * Adds a FormulaFunction to the VariableContext.
	 *
	 * @param function
	 *            The FormulaFunction to be added to the VariableContext
	 */
	public void addFunction(FormulaFunction function)
	{
		myFunctionLibrary.addFunction(function);
	}

	public WriteableFunctionLibrary getFunctionLibrary()
	{
		return myFunctionLibrary;
	}

	public OperatorLibrary getOperatorLibrary()
	{
		return operatorLibrary;
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
		return (PCGenScope) scopeLibrary.getImplementedScope(name);
	}

	/**
	 * Registers the given PCGenScope.
	 *
	 * @param scope
	 *            The PCGenScope to be registered
	 */
	public void registerScope(PCGenScope scope)
	{
		scopeLibrary.addScope(scope);
	}

	/**
	 * Returns the ImplementedScopeLibrary for this VariableContext.
	 *
	 * @return The ImplementedScopeLibrary for this VariableContext
	 */
	public ImplementedScopeLibrary getScopeLibrary()
	{
		return scopeLibrary;
	}

	/**
	 * Returns a Collection of all ImplementedScope objects registered in this
	 * VariableContext.
	 *
	 * @return A Collection of all ImplementedScope objects
	 */
	public Collection<ImplementedScope> getScopes()
	{
		return scopeLibrary.getScopes();
	}

	/**
	 * Generates a SolverManager with the given WriteableVariableStore.
	 *
	 * @param varStore
	 *            The WriteableVariableStore to be used for the returned SolverManager
	 * @return A SolverManager using the given WriteableVariableStore
	 */
	public SolverManager generateSolverManager(WriteableVariableStore varStore)
	{
		ManagerFactory mf = getPCManagerFactory(varStore);
		return new SimpleSolverManager(variableManager::isLegalVariableID,
			mf, myValueStore, varStore);
	}

	/**
	 * Returns a "valid" NEPFormula for the given expression.
	 *
	 * @param activeScope
	 *            The PCGenScope in which the NEPFormula is established and checked
	 * @param formatManager
	 *            The FormatManager in which the NEPFormula is established and checked
	 * @param instructions
	 *            The String representation of the formula to be converted to a NEPFormula
	 * @return a "valid" NEPFormula for the given expression
	 */
	public <T> NEPFormula<T> getValidFormula(PCGenScope activeScope, FormatManager<T> formatManager,
		String instructions)
	{
		return FormulaFactory.getValidFormula(instructions, getManagerFactory(), activeScope,
			formatManager);
	}

	/**
	 * Adds a relationship between a Solver format and a default Supplier for that format.
	 *
	 * @param <T>
	 *            The format (class) of object changed by the given Supplier
	 * @param varFormat
	 *            The format (as a FormatManager) of Solver for which the given Supplier
	 *            should be the default value
	 * @param defaultValue
	 *            The Supplier to be used to provide the default value for the given
	 *            Solver format
	 */
	public <T> void addDefault(FormatManager<T> varFormat, Supplier<T> defaultValue)
	{
		myValueStore.addSolverFormat(varFormat, defaultValue);
	}

	/**
	 * Returns the default value for a given Format (provided as a FormatManager).
	 *
	 * @param <T>
	 *            The format (class) of object for which the default value should be
	 *            returned
	 * @param variableFormat
	 *            The FormatManager for which the default value should be returned
	 * @return The default value for the given Format
	 */
	public <T> T getDefaultValue(FormatManager<T> variableFormat)
	{
		return variableFormat.initializeFrom(myValueStore);
	}

	/**
	 * Validates that all defaults in the ValueStore are consistent.
	 */
	public void validateDefaults()
	{
		myValueStore.validateDefaults();
	}

	/**
	 * Returns true if there is a default modifier set for the given FormatManager.
	 *
	 * @param formatManager
	 *            The FormatManager indicating the format to check for a default modifier
	 * @return true if there is a default modifier set for the given FormatManager; false
	 *         otherwise
	 */
	public boolean hasDefaultModifier(FormatManager<?> formatManager)
	{
		return myValueStore.get(formatManager) != null;
	}

	/**
	 * Returns the ScopeInstanceFactory for this VariableContext.
	 *
	 * @return The ScopeInstanceFactory
	 */
	public ScopeInstanceFactory getScopeInstanceFactory()
	{
		return scopeInstanceFactory;
	}

	/*
	 * Begin: (Delegated) Items part of VariableLibrary interface
	 */
	@Override
	public boolean isLegalVariableID(ImplementedScope varScope, String varName)
	{
		return variableManager.isLegalVariableID(varScope, varName);
	}

	@Override
	public Optional<FormatManager<?>> getVariableFormat(ImplementedScope varScope, String varName)
	{
		return variableManager.getVariableFormat(varScope, varName);
	}

	@Override
	public VariableID<?> getVariableID(ScopeInstance instance, String varName)
	{
		return variableManager.getVariableID(instance, varName);
	}

	@Override
	public void assertLegalVariableID(String varName, ImplementedScope varScope, FormatManager<?> formatManager)
	{
		variableManager.assertLegalVariableID(varName, varScope, formatManager);
	}

	@Override
	public List<FormatManager<?>> getInvalidFormats()
	{
		return variableManager.getInvalidFormats();
	}

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return variableManager.getDefault(formatManager);
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

	/*
	 * Begin: (Delegated) Items part of VariableWrapperFactory interface
	 */
	@Override
	public VariableWrapper<?> getWrapper(CharID id, VarScoped owner, String name)
	{
		return variableWrapperFactory.getWrapper(id, owner, name);
	}

	@Override
	public VariableWrapper<?> getGlobalWrapper(CharID id, String name)
	{
		return variableWrapperFactory.getGlobalWrapper(id, name);
	}

	@Override
	public void disconnect(VariableWrapper<?> variableWrapper)
	{
		variableWrapperFactory.disconnect(variableWrapper);
	}
	/*
	 * End: (Delegated) Items part of VariableWrapperFactory interface
	 */
}
