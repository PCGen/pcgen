/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.TypedKey;

/**
 * An EvaluationManager is a class to capture information used to support Evaluation of a
 * formula.
 * 
 * For this to be viable with a formula of any complexity, it must have a FormulaManager
 * and ScopeInstance loaded. Certain situations may also require knowing the input value,
 * asserted format or other characteristics that can be added to the EvaluationManager.
 */
public final class EvaluationManager
{

	/**
	 * The underlying map for this EvaluationManager that contains the target objects.
	 */
	private final Map<TypedKey<?>, Object> map = new HashMap<TypedKey<?>, Object>();

	/**
	 * A TypedKey used for storing the FormulaManager contained in this EvaluationManager.
	 */
	public static final TypedKey<FormulaManager> FMANAGER = new TypedKey<>();

	/**
	 * The OperatorLibrary used to store valid operators.
	 */
	public static final TypedKey<OperatorLibrary> OPLIB = new TypedKey<>();

	/**
	 * A TypedKey used for storing the ScopeInstance contained in this EvaluationManager.
	 */
	public static final TypedKey<ScopeInstance> INSTANCE = new TypedKey<>();

	/**
	 * A TypedKey used for storing the Format (as a Class) asserted by the current context
	 * of a formula.
	 */
	public static final TypedKey<Optional<FormatManager<?>>> ASSERTED =
			new TypedKey<>(Optional.empty());

	/**
	 * A TypedKey used for storing the Input Object contained in this EvaluationManager.
	 */
	public static final TypedKey<Object> INPUT = new TypedKey<>();


	/**
	 * Returns a new EvaluationManager that has all the characteristics of this
	 * EvaluationManager, except the given key set to the given value.
	 * 
	 * @param <T>
	 *            The format (class) of object stored by the given TypedKey
	 * @param key
	 *            The TypeKey for which the given value should be set in the returned
	 *            EvaluationManager
	 * @param value
	 *            The value to be set in the EvaluationManager for the given TypeKey
	 * @return A new EvaluationManager that has all the characteristics of this
	 *         EvaluationManager, except the given key set to the given value
	 */
	public <T> EvaluationManager getWith(TypedKey<T> key, T value)
	{
		EvaluationManager replacement = new EvaluationManager();
		replacement.map.putAll(map);
		replacement.map.put(Objects.requireNonNull(key), value);
		return replacement;
	}

	/**
	 * Returns the value of the EvaluationManager for the given TypedKey.
	 * 
	 * Note that this method will not throw an error if the EvaluationManager is empty. It
	 * will simply return the "Default Value" for the given TypeKey. Note null is a legal
	 * default value.
	 * 
	 * @param <T>
	 *            The format (class) of object stored by the given TypedKey
	 * @param key
	 *            The TypeKey for which the value should be returned
	 * @return The value of the EvaluationManager for the given TypedKey
	 */
	public <T> T get(TypedKey<T> key)
	{
		Object value = map.get(Objects.requireNonNull(key));
		return (value == null) ? key.getDefaultValue() : key.cast(value);
	}

}
