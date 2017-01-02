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

import java.util.List;

import pcgen.base.util.TypedKey;

/**
 * A DependencyManager is a class to capture Formula dependencies.
 * 
 * In order to capture specific dependencies, a specific dependency should be loaded into
 * this DependencyManager.
 */
public interface DependencyManager
{

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
	public <T> DependencyManager getWith(TypedKey<T> key, T value);

	/**
	 * Returns the value of the DependencyManager for the given TypedKey.
	 * 
	 * Note that this method will not throw an error if the DependencyManager is empty. It
	 * will simply return the "Default Value" for the given TypeKey. Note null is a legal
	 * default value.
	 * 
	 * @param key
	 *            The TypeKey for which the value should be returned
	 * @return The value of the DependencyManager for the given TypedKey
	 */
	public <T> T get(TypedKey<T> key);

	/**
	 * A TypedKey used for storing the FormulaManager contained in this DependencyManager
	 */
	public static final TypedKey<FormulaManager> FMANAGER =
			new TypedKey<FormulaManager>();

	/**
	 * A TypedKey used for storing the ScopeInstance contained in this DependencyManager
	 */
	public static final TypedKey<ScopeInstance> INSTANCE = new TypedKey<ScopeInstance>();

	/**
	 * A TypedKey used for storing the Format currently asserted for the formula served by
	 * this DependencyManager
	 */
	public static final TypedKey<Class<?>> ASSERTED = new TypedKey<Class<?>>();

	/**
	 * A TypedKey used for storing the dynamic variables for the formula served by this
	 * DependencyManager.
	 */
	public static final TypedKey<DynamicManager> DYNAMIC = new TypedKey<DynamicManager>();

	/**
	 * Adds a Variable (identified by the VariableID) to the list of dependencies for a
	 * Formula.
	 * 
	 * @param varID
	 *            The VariableID to be added as a dependency of the Formula this
	 *            VariableDependencyManager represents
	 */
	public void addVariable(String s);

	/**
	 * Returns a non-null list of VariableID objects that identify the list of
	 * dependencies of the Formula this VariableDependencyManager represents.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. The contents
	 * of the List will not be modified as a result of the VariableDependencyManager
	 * maintaining or otherwise transferring a reference to the List to another object
	 * (and the VariableDependencyManager cannot be modified if the returned list is
	 * modified).
	 * 
	 * @return A non-null list of VariableID objects that identify the list of
	 *         dependencies of the Formula this VariableDependencyManager represents
	 */
	public List<VariableID<?>> getVariables();

	public DependencyManager getDynamicTrainer();

	public DependencyManager getDynamic(String string);
}
