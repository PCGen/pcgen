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

	public final TypedKey<FunctionLibrary> FUNCTION = new TypedKey<>();

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	public VariableLibrary getFactory();

	/**
	 * Returns the VariableStore used to hold variables values for items
	 * processed through this FormulaManager.
	 * 
	 * @return The VariableStore used to hold variables values for items
	 *         processed through this FormulaManager
	 */
	public VariableStore getResolver();

	/**
	 * Returns the OperatorLibrary used to store valid operations in this
	 * FormulaManager.
	 * 
	 * @return The OperatorLibrary used to store valid operations in this
	 *         FormulaManager
	 */
	public OperatorLibrary getOperatorLibrary();

	/**
	 * Returns the default value for a given format (class).
	 * 
	 * @param format
	 *            The format for which the default value should be returned
	 * @return The default value for a given format (class)
	 */
	public <T> T getDefault(Class<T> format);

	/**
	 * Pushes a new value into the FormulaManager for the given TypedKey.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be pushed into the
	 *            FormulaManager
	 * @param value
	 *            The value to be pushed into the FormulaManager for the given TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> void push(TypedKey<T> key, T value);

	/**
	 * Pops a value from the FormulaManager for the given TypedKey.
	 * 
	 * Note that this method will not block or throw an error if the FormulaManager is
	 * empty. It will simply return the "Default Value" for the given TypeKey. Note null
	 * is a legal default value.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be popped from the
	 *            FormulaManager
	 * @return The value popped from the FormulaManager for the given TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> T pop(TypedKey<T> key);

	/**
	 * Returns the top value of the FormulaManager for the given TypedKey, without
	 * performing a pop.
	 * 
	 * Note that this method will not block or throw an error if the FormulaManager is
	 * empty. It will simply return the "Default Value" for the given TypeKey. Note null
	 * is a legal default value.
	 * 
	 * @param key
	 *            The TypeKey for which the top value should be returned
	 * @return The top value of the FormulaManager for the given TypedKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> T peek(TypedKey<T> key);

	/**
	 * Sets a new value into the FormulaManager for the given TypedKey.
	 * 
	 * This is effectively a shortcut for calling pop(key) followed by push(key, value).
	 * This has the same effects as pop of not throwing an error if the FormulaManager is
	 * currently empty.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be set as the top value on
	 *            the FormulaManager
	 * @param value
	 *            The value to be set as the top value on the FormulaManager for the given
	 *            TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> void set(TypedKey<T> key, T value);

}
