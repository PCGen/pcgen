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

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.IgnoreVariables;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.DynamicSolverManager;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.base.solver.SimpleSolverFactory;
import pcgen.base.solver.SolverFactory;
import pcgen.base.solver.SolverManager;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.util.ComplexResult;
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
import pcgen.util.Logging;

/**
 * A VariableContext is responsible for managing variable items during the load of data
 * and (in some cases) subsequently while the data set associated with the parent
 * LoadContext is operating.
 */
public class VariableContext implements VariableChannelFactory,
        VariableWrapperFactory, VariableLibrary
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
     * The ValueStore for the FormulaSetupFactory and this VariableContext. Local so
     * that we can set the defaults for variable formats from data.
     */
    private final SupplierValueStore myValueStore = new SupplierValueStore();

    /**
     * The SolverFactory for the FormulaSetupFactory and this VariableContext. Local so
     * that we can set the defaults for variable formats from data.
     */
    private final SolverFactory solverFactory = new SimpleSolverFactory(myValueStore);

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
    private final VariableManager variableManager =
            new VariableManager(legalScopeManager, myValueStore);

    /**
     * The naive FormulaManager for this VariableContext. This serves only as a base item
     * for loading formulas, not for evaluation.
     * <p>
     * Each PC will also have its own FormulaManager (which will contain a dedicated
     * VariableStore for that PC). This specific FormulaManager is derived from this
     * FormulaManager in generateSolverManager().
     * <p>
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
     * Contains a VariableWrapperFactory used to develop VariableWrappers for this
     * VariableContext.
     */
    private VariableWrapperFactoryInst variableWrapperFactory =
            new VariableWrapperFactoryInst();

    /**
     * Constructs a new VariableContext with the given ManagerFactory.
     *
     * @param managerFactory The ManagerFactory used to generate managers for formula evaluation by
     *                       all items loaded while this VariableContext is active
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
        formulaSetupFactory.setValueStoreSupplier(() -> myValueStore);
        formulaSetupFactory.setLegalScopeManagerSupplier(() -> legalScopeManager);
        formulaSetupFactory.setFunctionLibrarySupplier(() -> myFunctionLibrary);
        formulaSetupFactory.setVariableLibraryFunction((lsm, vs) -> variableManager);
    }

    /**
     * Returns the FormulaManager for this VariableContext.
     * <p>
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

    /**
     * Returns a new FormulaManager; method is designed to be used once with each PC.
     */
    public FormulaManager getPCFormulaManager()
    {
        return formulaSetupFactory.generate();
    }

    /**
     * Returns a FormulaModifier based on the given information.
     *
     * @param modType       The type of the modifier to be generated (e.g. "ADD")
     * @param instructions  The instructions for the modifier
     * @param varScope      The PCGenScope in which the FormulaModifier is operating
     * @param formatManager The FormatManager indicating the format of the object being operated on
     *                      by the FormulaModifier
     * @return a FormulaModifier based on the given information
     */
    public <T> FormulaModifier<T> getModifier(String modType,
            String instructions, FormulaManager formulaManager, PCGenScope varScope,
            FormatManager<T> formatManager)
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

        FormulaSemantics semantics = managerFactory.generateFormulaSemantics(formulaManager, varScope);
        semantics = semantics.getWith(FormulaSemantics.INPUT_FORMAT, Optional.of(formatManager));
        try
        {
            modifier.isValid(semantics);
        } catch (SemanticsException e)
        {
            throw new IllegalArgumentException("Invalid Semantics on Formula: "
                    + modType + " ... " + e.getLocalizedMessage(), e);
        }

        /*
         * getDependencies needs to be called during LST load, so that object references are captured
         */
        DependencyManager fdm = managerFactory.generateDependencyManager(formulaManager, null);
        fdm = fdm.getWith(DependencyManager.SCOPE, Optional.of(varScope));
        fdm = fdm.getWith(DependencyManager.VARSTRATEGY, Optional.of(new IgnoreVariables()));
        fdm = fdm.getWith(ManagerKey.REFERENCES, new ReferenceDependency());
        modifier.getDependencies(fdm);
        modifier.addReferences(fdm.get(ManagerKey.REFERENCES).getReferences());
        return modifier;
    }

    /**
     * Adds a FormulaFunction to the VariableContext.
     * <p>
     * Behavior is not defined if an an attempt is made to add null or a FormulaFunction
     * with a null name. An exception may be thrown.
     *
     * @param function The FormulaFunction to be added to the VariableContext
     */
    public void addFunction(FormulaFunction function)
    {
        myFunctionLibrary.addFunction(function);
    }

    /**
     * Returns the PCGenScope for the given name.
     *
     * @param name The name of the PCGenScope to be returned
     * @return The PCGenScope for the given name
     */
    public PCGenScope getScope(String name)
    {
        return (PCGenScope) legalScopeManager.getScope(name);
    }

    /**
     * Registers the given PCGenScope.
     *
     * @param scope The PCGenScope to be registered
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
     * @param varStore The WriteableVariableStore to be used for the returned SolverManager
     * @return A SolverManager with a FormulaManager using the given
     * WriteableVariableStore
     */
    public SolverManager generateSolverManager(WriteableVariableStore varStore)
    {
        FormulaManager derived = getFormulaManager().getWith(FormulaManager.RESULTS, varStore);
        return new DynamicSolverManager(derived, managerFactory, solverFactory, varStore);
    }

    /**
     * Returns a "valid" NEPFormula for the given expression.
     * <p>
     * If the given expression does not represent a valid formula, then this
     * will throw an IllegalArgumentException.
     * <p>
     * If the given expression does not return an object of the type in the
     * given FormatManager, then this will throw an IllegalArgumentException.
     *
     * @param activeScope   The PCGenScope in which the NEPFormula is established and
     *                      checked
     * @param formatManager The FormatManager in which the NEPFormula is established and
     *                      checked
     * @param instructions  The String representation of the formula to be converted to a
     *                      NEPFormula
     * @return a "valid" NEPFormula for the given expression
     */
    public <T> NEPFormula<T> getValidFormula(PCGenScope activeScope, FormatManager<T> formatManager,
            String instructions)
    {
        return FormulaFactory.getValidFormula(instructions, managerFactory, getFormulaManager(), activeScope,
                formatManager);
    }

    /**
     * Adds a relationship between a Solver format and a default Supplier for that format
     * of Solver to this VariableContext.
     * <p>
     * The default Supplier for a format of Solver may not be redefined for a
     * SolverFactory. Once a given default Supplier has been established for a format of
     * Solver, this method MUST NOT be called a second time for that format of Solver.
     *
     * @param <T>          The format (class) of object changed by the given Supplier
     * @param varFormat    The format (as a FormatManager) of Solver for which the given Supplier
     *                     should be the default value
     * @param defaultValue The Supplier to be used to provide the default value for the given
     *                     Solver format
     * @throws IllegalArgumentException if the given Solver format already has a default Supplier defined for
     *                                  this SolverFactory
     */
    public <T> void addDefault(FormatManager<T> varFormat, Supplier<T> defaultValue)
    {
        solverFactory.addSolverFormat(varFormat, defaultValue);
    }

    /**
     * Returns the default value for a given Format (provided as a FormatManager).
     *
     * @param <T>            The format (class) of object for which the default value should be
     *                       returned
     * @param variableFormat The FormatManager for which the default value should be returned
     * @return The default value for the given Format
     */
    public <T> T getDefaultValue(FormatManager<T> variableFormat)
    {
        return solverFactory.getDefault(variableFormat);
    }

    /**
     * Returns true if there is a default modifier set for the given FormatManager.
     * <p>
     * Warning: This is NOT whether there is a Default Value for the given FormatManager.
     * This is a much simpler test that checks if there is a specifically provided Default
     * Modifier. The distinction here is that a format like ARRAY[NUMBER] will return
     * false from this; while it is legal, it never have a specifically defined default,
     * as it is a derived default value.
     *
     * @param formatManager The FormatManager indicating the format to check for a default modifier
     * @return true if there is a default modifier set for the given FormatManager; false
     * otherwise
     */
    public boolean hasDefaultModifier(FormatManager<?> formatManager)
    {
        return myValueStore.get(formatManager) != null;
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

    @Override
    public List<FormatManager<?>> getInvalidFormats()
    {
        return variableManager.getInvalidFormats();
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
