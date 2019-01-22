/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ValueStore;

/**
 * A SupplierValueStore is a centralized location to define a shared default value for a
 * format of formats for Solvers.
 */
public class SupplierValueStore implements ValueStore
{

	/**
	 * The underlying Map for this SupplierValueStore that stores the default values by
	 * their FormatManager.
	 */
	private final Map<FormatManager<?>, Supplier<?>> formatMap =
			new HashMap<>();

	/**
	 * The underlying Map for this SupplierValueStore that stores the default values by
	 * their identifier.
	 */
	private final CaseInsensitiveMap<Supplier<?>> identifierMap =
			new CaseInsensitiveMap<>();

	/**
	 * Adds a new default value to this SupplierValueStore for the given FormatManager.
	 * 
	 * @param formatManager
	 *            The FormatManager for which the given value should be added
	 * @param defaultValue
	 *            The Supplier that used to set the value for the given FormatManager
	 * @return The previous default for the given FormatManager, if any
	 */
	public Object addValueFor(FormatManager<?> formatManager,
		Supplier<?> defaultValue)
	{
		identifierMap.put(formatManager.getIdentifierType(), defaultValue);
		return formatMap.put(formatManager, defaultValue);
	}

	@Override
	public Object getValueFor(String identifier)
	{
		Supplier<?> defaultValue = identifierMap.get(identifier);
		Objects.requireNonNull(defaultValue,
			() -> "ModifierValueStore did not have a default value for: "
				+ identifier);
		return defaultValue.get();
	}

	/**
	 * Returns the default value (unresolved) for the given FormatManager.
	 * 
	 * @param formatManager
	 *            The FormatManager for which the default value should be returned
	 * @return The (unresolved) default value for the given FormatManager
	 */
	@SuppressWarnings("unchecked")
	public <T> Supplier<T> get(FormatManager<T> formatManager)
	{
		return (Supplier<T>) formatMap.get(formatManager);
	}

	/**
	 * Returns a Set of the FormatManager objects representing the formats for which this
	 * SupplierValueStore has a default value.
	 * 
	 * @return A Set of the FormatManager objects representing the formats for which this
	 *         SupplierValueStore has a default value
	 */
	public Set<FormatManager<?>> getStoredFormats()
	{
		return Collections.unmodifiableSet(formatMap.keySet());
	}
}
