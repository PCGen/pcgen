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
package pcgen.base.formula.inst;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.util.FormatManager;
import pcgen.base.util.TypedKey;
import pcgen.base.util.ValueStore;

/**
 * A FormulaManager exists as compound object to simplify those things that require
 * context to be resolved (legal functions, variables). This provides a convenient, single
 * location for consolidation of these capabilities (and thus keeps the number of
 * parameters that have to be passed around to a reasonable level).
 */
public class SimpleFormulaManager implements FormulaManager
{

	/**
	 * The underlying map for this DependencyManager that contains the target objects.
	 */
	private final Map<TypedKey<?>, Object> map = new HashMap<TypedKey<?>, Object>();

	/**
	 * The ValueStore used to know the default values for a format.
	 */
	private final ValueStore defaultStore;

	/**
	 * The VariableLibrary used to get VariableIDs.
	 */
	private final VariableLibrary varLibrary;

	/**
	 * The ScopeInstanceFactory used to get ScopeInstance objects.
	 */
	private final ScopeInstanceFactory siFactory;

	/**
	 * Constructs a new FormulaManager from the provided FunctionLibrary, OperatorLibrary,
	 * VariableLibrary, and VariableStore.
	 * 
	 * @param varLibrary
	 *            The VariableLibrary used to get VariableIDs
	 * @param siFactory
	 *            The ScopeInstanceFactory used to get ScopeInstance objects
	 * @param resultStore
	 *            The VariableStore used to hold variables values for items processed
	 *            through this FormulaManager
	 * @param defaultStore
	 *            The ValueStore used to know default values for each format (class)
	 */
	public SimpleFormulaManager(VariableLibrary varLibrary,
		ScopeInstanceFactory siFactory, VariableStore resultStore,
		ValueStore defaultStore)
	{
		this.varLibrary = Objects.requireNonNull(varLibrary);
		this.siFactory = Objects.requireNonNull(siFactory);
		map.put(RESULTS, Objects.requireNonNull(resultStore));
		this.defaultStore = Objects.requireNonNull(defaultStore);
	}
	
	private SimpleFormulaManager(SimpleFormulaManager original, Map<TypedKey<?>, Object> map)
	{
		this.varLibrary = original.varLibrary;
		this.siFactory = original.siFactory;
		this.defaultStore = original.defaultStore;
		this.map.putAll(map);
	}

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	@Override
	public VariableLibrary getFactory()
	{
		return varLibrary;
	}

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return formatManager.initializeFrom(defaultStore);
	}

	@Override
	public <T> FormulaManager getWith(TypedKey<T> key, T value)
	{
		SimpleFormulaManager replacement = new SimpleFormulaManager(this, map);
		replacement.map.put(Objects.requireNonNull(key), value);
		return replacement;
	}

	@Override
	public <T> T get(TypedKey<T> key)
	{
		Object value = map.get(Objects.requireNonNull(key));
		return (value == null) ? key.getDefaultValue() : key.cast(value);
	}

	@Override
	public ScopeInstanceFactory getScopeInstanceFactory()
	{
		return siFactory;
	}

}
