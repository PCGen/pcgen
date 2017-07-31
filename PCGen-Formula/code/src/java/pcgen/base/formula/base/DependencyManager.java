/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pcgen.base.util.NonNullValueKey;
import pcgen.base.util.OptionalItemKey;
import pcgen.base.util.ValidatingKey;

/**
 * A DependencyManager is a class to capture Formula dependencies.
 * 
 * In order to capture specific dependencies, a specific dependency should be loaded into
 * this DependencyManager.
 */
public class DependencyManager
{

	/**
	 * A NonNullValueKey used for a location to write messages while processing
	 * dependencies.
	 */
	public static final NonNullValueKey<List<String>> LOG =
			new NonNullValueKey<List<String>>(true);

	/**
	 * A ValidatingKey used for storing the FormulaManager contained in this
	 * DependencyManager
	 */
	public static final NonNullValueKey<FormulaManager> FMANAGER =
			new NonNullValueKey<FormulaManager>(true);

	/**
	 * A ValidatingKey used for storing the ScopeInstance contained in this
	 * DependencyManager
	 */
	public static final OptionalItemKey<ScopeInstance> INSTANCE =
			new OptionalItemKey<ScopeInstance>();

	/**
	 * A OptionalItemKey used for storing the (static) Variables contained in this
	 * DependencyManager.
	 */
	public static final NonValidatingKey<VariableList> VARIABLES =
			new NonValidatingKey<VariableList>();

	/**
	 * A OptionalItemKey used for determining how encountered variables are processed.
	 * This can be a VariableStrategy that simply provides static behavior, or one that is
	 * aware of dynamic variables.
	 */
	public static final DependentKey<VariableStrategy> VARSTRATEGY =
			new DependentKey<VariableStrategy>(VARIABLES);

	/**
	 * A OptionalItemKey used for storing the Format currently asserted for the formula
	 * served by this DependencyManager
	 */
	public static final NonValidatingKey<Class<?>> ASSERTED =
			new NonValidatingKey<Class<?>>();

	/**
	 * A OptionalItemKey used for storing the dynamic variables for the formula served by
	 * this DependencyManager.
	 */
	public static final NonValidatingKey<DynamicManager> DYNAMIC =
			new NonValidatingKey<DynamicManager>();

	/**
	 * The underlying map for this DependencyManager that contains the target objects.
	 */
	private final ValidatingMap<Object> map = new ValidatingMap<Object>();

	/**
	 * Constructs a new DependencyManager object.
	 */
	public DependencyManager(FormulaManager formulaManager)
	{
		map.put(FMANAGER, formulaManager);
		//TODO Make this write only?
		map.put(LOG, new WriteOnceReadManyList(new ArrayList<>()));
	}

	/**
	 * Returns a new DependencyManager that has all the characteristics of this
	 * DependencyManager, except the given key set to the given value.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be set in the returned
	 *            DependencyManager
	 * @param value
	 *            The value to be set in the DependencyManager for the given TypeKey
	 */
	public <T> DependencyManager getWith(ValidatingKey<T> key, T value)
	{
		DependencyManager replacement = new DependencyManager();
		replacement.map.putAll(map);
		replacement.map.put(Objects.requireNonNull(key), value);
		return replacement;
	}

	/**
	 * Returns the value of the DependencyManager for the given ValidatingKey.
	 * 
	 * Note that this method will not throw an error if the DependencyManager is empty. It
	 * will simply return the "Default Value" for the given TypeKey. Note null is a legal
	 * default value.
	 * 
	 * @param key
	 *            The TypeKey for which the value should be returned
	 * @return The value of the DependencyManager for the given ValidatingKey
	 */
	public <T> T get(ValidatingKey<T> key)
	{
		Object value = map.get(Objects.requireNonNull(key));
		return (value == null) ? key.getDefaultValue() : key.cast(value);
	}
}
