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
package pcgen.base.formula.base;

import pcgen.base.util.FormatManager;
import pcgen.base.util.TypedKey;

/**
 * A FormulaManager exists as compound object to simplify those things that
 * require context to be resolved (legal functions, variables). This provides a
 * convenient, single location for consolidation of these capabilities (and thus
 * keeps the number of parameters that have to be passed around to a reasonable
 * level).
 * 
 * This is also an object used to "cache" the SemanticsVisitor (since the
 * visitor needs to know some of the contents in the FormulaManager, it can be
 * lazily instantiated but then effectively cached as long as that
 * FormulaManager is reused - especially valuable for things like the global
 * context which in the future we can create once for the PC and never have to
 * recreate...)
 */
public interface FormulaManager
{

	/**
	 * A TypedKey for containing a FunctionLibrary in the FormulaManager
	 */
	public final TypedKey<FunctionLibrary> FUNCTION = new TypedKey<>();

	/**
	 * A TypedKey for containing a VariableStore in the FormulaManager
	 */
	public final TypedKey<VariableStore> RESULTS = new TypedKey<>();

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	public VariableLibrary getFactory();

	/**
	 * Returns the default value for a given FormatManager.
	 * 
	 * @param <T>
	 *            The format (class) of object managed by the given FormatManager
	 * @param formatManager
	 *            The FormatManager for which the default value should be returned
	 * @return The default value for a given FormatManager
	 */
	public <T> T getDefault(FormatManager<T> formatManager);

	/**
	 * Returns a new FormulaManager that has all the characteristics of this
	 * FormulaManager, except the given key set to the given value.
	 * 
	 * @param <T>
	 *            The format (class) of object stored by the given TypedKey
	 * @param key
	 *            The TypeKey for which the given value should be set in the returned
	 *            FormulaManager
	 * @param value
	 *            The value to be set in the FormulaManager for the given TypeKey
	 * @return A new FormulaManager that has all the characteristics of this
	 *         FormulaManager, except the given key set to the given value
	 */
	public <T> FormulaManager getWith(TypedKey<T> key, T value);

	/**
	 * Gets the value in the FormulaManager for the given TypedKey.
	 * 
	 * Note that this method will not throw an error if the FormulaManager is empty or has
	 * no value for that TypedKey. It will simply return the "Default Value" for the given
	 * TypeKey. Note null is a legal default value.
	 * 
	 * @param <T>
	 *            The format (class) of object stored by the given TypedKey
	 * @param key
	 *            The TypeKey for which the value should be returned
	 * @return The value in the FormulaManager for the given TypedKey
	 */
	public <T> T get(TypedKey<T> key);

	/**
	 * Returns the ScopeInstanceFactory used to return ScopeInstance objects.
	 * 
	 * @return The ScopeInstanceFactory used to return ScopeInstance objects
	 */
	public ScopeInstanceFactory getScopeInstanceFactory();

	/**
	 * Returns the VariableIDResolver for this FormulaManager.
	 * 
	 * @return The VariableIDResolver for this FormulaManager
	 */
	public VarIDResolver getResolver();

}
