/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import pcgen.base.formula.base.DeleteMeWithSetupFactory;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleVariableStore;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.util.ValueStore;

/**
 * FormulaSetupFactory provides a single location to quickly build the necessary items for
 * processing a formula.
 * 
 * When generate is called, FormulaSetupFactory generates a set of "common information"
 * (operators, functions) and provides a relevant FormulaManager.
 */
public class FormulaSetupFactory
{
	/**
	 * The ValueStore for this FormulaSetupFactory.
	 */
	private Supplier<SupplierValueStore> valueStoreSupplier =
			() -> new SupplierValueStore();

	/**
	 * The ScopeManager for this FormulaSetupFactory.
	 */
	private Supplier<DeleteMeWithSetupFactory> scopeManagerSupplier;

	/**
	 * The SimpleFunctionLibrary for this FormulaSetupFactory.
	 */
	private Supplier<FunctionLibrary> functionLibrarySupplier =
			() -> FormulaUtilities.loadBuiltInFunctions(new SimpleFunctionLibrary());

	/**
	 * The VariableLibrary for this FormulaSetupFactory.
	 */
	private BiFunction<DeleteMeWithSetupFactory, ValueStore, VariableLibrary> variableLibraryFunction =
			(lsl, vs) -> new VariableManager(lsl, vs);

	/**
	 * The ScopeInstanceFactory for this FormulaSetupFactory.
	 */
	private Function<ScopeImplementer, ScopeInstanceFactory> scopeInstanceFactoryFunction =
			lsl -> new SimpleScopeInstanceFactory(lsl);

	/**
	 * The VariableStore for this FormulaSetupFactory.
	 */
	private Supplier<VariableStore> variableStoreSupplier =
			() -> new SimpleVariableStore();

	/**
	 * Generates a new FormulaManager with the Suppliers/Functions contained in this
	 * FormulaSetupFactory.
	 * 
	 * @return A new FormulaManager generated using the Suppliers/Functions contained in
	 *         this FormulaSetupFactory
	 */
	public FormulaManager generate()
	{
		SupplierValueStore valueStore = valueStoreSupplier.get();
		DeleteMeWithSetupFactory scopeManager = scopeManagerSupplier.get();
		FunctionLibrary functionLibrary = functionLibrarySupplier.get();
		VariableStore variableStore = variableStoreSupplier.get();
		VariableLibrary variableLibrary =
				variableLibraryFunction.apply(scopeManager, valueStore);
		ScopeInstanceFactory scopeInstanceFactory =
				scopeInstanceFactoryFunction.apply(scopeManager);
		SimpleFormulaManager fManager = new SimpleFormulaManager(
			variableLibrary, scopeInstanceFactory, variableStore, valueStore);
		return fManager.getWith(FormulaManager.FUNCTION, functionLibrary);
	}

	/**
	 * Sets the Supplier that will generate a ModifierValueStore for this FormulaSetupFactory.
	 * 
	 * @param valueStoreSupplier
	 *            The Supplier that will generate a ModifierValueStore for this
	 *            FormulaSetupFactory
	 */
	public void setValueStoreSupplier(
		Supplier<SupplierValueStore> valueStoreSupplier)
	{
		this.valueStoreSupplier = valueStoreSupplier;
	}

	/**
	 * Sets the Supplier that will generate a ScopeManager for this
	 * FormulaSetupFactory.
	 * 
	 * @param scopeManagerSupplier
	 *            The Supplier that will generate a ScopeManager for this
	 *            FormulaSetupFactory
	 */
	public void setScopeManagerSupplier(
		Supplier<DeleteMeWithSetupFactory> scopeManagerSupplier)
	{
		this.scopeManagerSupplier = scopeManagerSupplier;
	}

	/**
	 * Sets the Supplier that will generate a FunctionLibrary for this
	 * FormulaSetupFactory.
	 * 
	 * @param functionLibrarySupplier
	 *            The Supplier that will generate a FunctionLibrary for this
	 *            FormulaSetupFactory
	 */
	public void setFunctionLibrarySupplier(
		Supplier<FunctionLibrary> functionLibrarySupplier)
	{
		this.functionLibrarySupplier = functionLibrarySupplier;
	}

	/**
	 * Sets the BiFunction that will generate a VariableLibrary for this
	 * FormulaSetupFactory.
	 * 
	 * @param variableLibraryFunction
	 *            The BiFunction that will generate a VariableLibrary for this
	 *            FormulaSetupFactory
	 */
	public void setVariableLibraryFunction(
		BiFunction<DeleteMeWithSetupFactory, ValueStore, VariableLibrary> variableLibraryFunction)
	{
		this.variableLibraryFunction = variableLibraryFunction;
	}

	/**
	 * Sets the Function that will generate a ScopeInstanceFactory for this
	 * FormulaSetupFactory.
	 * 
	 * @param scopeInstanceFactoryFunction
	 *            The Function that will generate a ScopeInstanceFactory for this
	 *            FormulaSetupFactory
	 */
	public void setScopeInstanceFactoryFunction(
		Function<ScopeImplementer, ScopeInstanceFactory> scopeInstanceFactoryFunction)
	{
		this.scopeInstanceFactoryFunction = scopeInstanceFactoryFunction;
	}

	/**
	 * Sets the Supplier that will generate a VariableStore for this FormulaSetupFactory.
	 * 
	 * @param variableStoreSupplier
	 *            The Supplier that will generate a VariableStore for this
	 *            FormulaSetupFactory
	 */
	public void setVariableStoreSupplier(Supplier<VariableStore> variableStoreSupplier)
	{
		this.variableStoreSupplier = variableStoreSupplier;
	}

}
