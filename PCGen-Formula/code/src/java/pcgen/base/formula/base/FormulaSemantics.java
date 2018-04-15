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
package pcgen.base.formula.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.TypedKey;

/**
 * A FormulaSemantics is a class to capture Formula semantics.
 * 
 * This is designed, among other things, to report on whether a formula is valid, and if
 * valid the semantics of the Formula (what format it will return).
 * 
 * In order to capture specific dependencies, a specific set of semantics information
 * should be loaded into this FormulaSemantics.
 * 
 * If a formula is valid, then the isValid() method will return true. In such a case, the
 * contents of getReport() should be ignored.
 * 
 * If a formula is not valid, then this must contain Report string. This value should
 * indicate with some precision the issue with the Formula. Note that if there is more
 * than one issue, only one issue needs to be returned (fast fail is acceptable).
 */
public class FormulaSemantics
{

	/**
	 * The underlying map for this FormulaSemantics that contains the target objects.
	 */
	private final Map<TypedKey<?>, Object> map = new HashMap<TypedKey<?>, Object>();

	/**
	 * A TypedKey used for storing the FormulaManager contained in this FormulaSemantics.
	 */
	public static final TypedKey<FormulaManager> FMANAGER = new TypedKey<>();

	/**
	 * A TypedKey used for storing the LegalScope contained in this FormulaSemantics.
	 */
	public static final TypedKey<LegalScope> SCOPE = new TypedKey<>();

	/**
	 * A TypedKey used for storing the Format currently asserted for the formula served by
	 * this FormulaSemantics.
	 */
	public static final TypedKey<Optional<FormatManager<?>>> ASSERTED =
			new TypedKey<>(Optional.empty());

	/**
	 * A TypedKey used for storing the Format of the input object for the formula served
	 * by this FormulaSemantics.
	 */
	public static final TypedKey<Optional<FormatManager<?>>> INPUT_FORMAT =
			new TypedKey<>(Optional.empty());

	/**
	 * Constructs a new FormulaSemantics object.
	 */
	public FormulaSemantics()
	{
		//Allow empty Map
	}

	/**
	 * Constructs a new FormulaSemantics object with the provided map used to initialize
	 * the underlying map for the FormulaSemantics.
	 * 
	 * @param inputs
	 *            The Map used to initialize the underlying map for this FormulaSemantics
	 */
	private FormulaSemantics(Map<TypedKey<?>, Object> inputs)
	{
		map.putAll(inputs);
	}

	/**
	 * Returns a new FormulaSemantics that has all the characteristics of this
	 * FormulaSemantics, except the given key set to the given value.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be set in the returned
	 *            FormulaSemantics
	 * @param value
	 *            The value to be set in the FormulaSemantics for the given TypeKey
	 */
	public <T> FormulaSemantics getWith(TypedKey<T> key, T value)
	{
		FormulaSemantics replacement = new FormulaSemantics(map);
		replacement.map.put(Objects.requireNonNull(key), value);
		return replacement;
	}

	/**
	 * Returns the value of the FormulaSemantics for the given TypedKey.
	 * 
	 * Note that this method will not throw an error if the FormulaSemantics is empty. It
	 * will simply return the "Default Value" for the given TypeKey. Note null is a legal
	 * default value.
	 * 
	 * @param key
	 *            The TypeKey for which the value should be returned
	 * @return The value of the FormulaSemantics for the given TypedKey
	 */
	public <T> T get(TypedKey<T> key)
	{
		Object value = map.get(Objects.requireNonNull(key));
		return (value == null) ? key.getDefaultValue() : key.cast(value);
	}
}
