/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ValueStore;

/**
 * A ModifierValueStore is a centralized location to define a shared default value for a
 * format of formats for Solvers.
 */
public class ModifierValueStore implements ValueStore
{

	/**
	 * The underlying Map for this ModifierValueStore that stores the default values by
	 * their FormatManager.
	 */
	private final Map<FormatManager<?>, Modifier<?>> defaultModifierMap =
			new HashMap<FormatManager<?>, Modifier<?>>();

	/**
	 * The underlying Map for this ModifierValueStore that stores the default values by
	 * their identifier.
	 */
	private final CaseInsensitiveMap<Modifier<?>> identifierMap =
			new CaseInsensitiveMap<>();

	/**
	 * Adds a new modifier to this ValueStore for the given Identifier.
	 * 
	 * @param identifier
	 *            The Identifier for which the given value should be added
	 * @param modifier
	 *            The Modifier used to set the value for the given Identifier
	 * @return The previous modifier for the given identifier, if any
	 */
	public Object addValueFor(FormatManager<?> formatManager,
		Modifier<?> modifier)
	{
		identifierMap.put(formatManager.getIdentifierType(), modifier);
		return defaultModifierMap.put(formatManager, modifier);
	}

	@Override
	public Object getValueFor(String identifier)
	{
		Modifier<?> defaultModifier = identifierMap.get(identifier);
		Objects.requireNonNull(defaultModifier,
			() -> "ModifierValueStore did not have a default value for: "
				+ identifier);
		return defaultModifier.process(null);
	}

	@SuppressWarnings("unchecked")
	public <T> Modifier<T> get(FormatManager<T> formatManager)
	{
		return (Modifier<T>) defaultModifierMap.get(formatManager);
	}

	/**
	 * Returns a Set of the FormatManager objects representing the formats for which this
	 * MidifierValueStore has a default value.
	 * 
	 * @return A Set of the FormatManager objects representing the formats for which this
	 *         MidifierValueStore has a default value
	 */
	public Set<FormatManager<?>> getStoredFormats()
	{
		return Collections.unmodifiableSet(defaultModifierMap.keySet());
	}
}
