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
import pcgen.base.util.ComplexResult;
import pcgen.base.util.FailureResult;
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
	 * @param <T>
	 *            The format (class) of object managed by the given FormatManager
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

	/**
	 * Adds a relationship between a Solver format and a default value for that format of
	 * Solver to this SupplierValueStore.
	 * 
	 * The default value for a format of Solver may not be redefined for a
	 * SupplierValueStore. Once a given default Supplier has been established for a format
	 * of Solver, this method MUST NOT be called a second time for that format of Solver.
	 * 
	 * @param <T>
	 *            The format (class) of object changed by the given Supplier
	 * @param formatManager
	 *            The FormatManager of the Solver format for which the given Supplier
	 *            should provide the default value
	 * @param defaultModifier
	 *            The Supplier to be used to get the default value for the given Solver
	 *            format
	 * @throws IllegalArgumentException
	 *             If the given Solver format already has a default value defined for this
	 *             SupplierValueStore
	 */
	public <T> void addSolverFormat(FormatManager<T> formatManager,
		Supplier<? extends T> defaultModifier)
	{
		Objects.requireNonNull(formatManager,
			"Variable/Solve FormatManager cannot be null");
		Objects.requireNonNull(defaultModifier,
			() -> "Default Modifier for Format: "
				+ formatManager.getIdentifierType() + " cannot be null");
		Supplier<T> existing = get(formatManager);
		if (existing == null)
		{
			identifierMap.put(formatManager.getIdentifierType(),
				defaultModifier);
			formatMap.put(formatManager, defaultModifier);
		}
		else if (!defaultModifier.equals(existing))
		{
			throw new IllegalArgumentException(
				"Cannot set different default values for Format: "
					+ formatManager.getIdentifierType());
		}
	}

	/**
	 * Returns a ComplexResult indicating the status of validating the defaults added to
	 * the addSolverFormat method. If all of the defaults are usable, this will return a
	 * ComplexResult indicating TRUE. If not, it will return a ComplexResult indicating
	 * FALSE and an appropriate message.
	 * 
	 * @return A ComplexResult indicating the status of validating the defaults added to
	 *         the addSolverFormat method
	 */
	@SuppressWarnings({"PMD.AvoidCatchingNPE",
		"PMD.AvoidCatchingGenericException"})
	public ComplexResult<Boolean> validateDefaults()
	{
		for (FormatManager<?> formatManager : getStoredFormats())
		{
			try
			{
				try
				{
					roundRobinDefault(formatManager);
				}
				catch (ClassCastException e)
				{
					//Generics were violated during addSolverFormat if we got here
					return new FailureResult<>("Format: " + formatManager
						+ " cannot use default Modifier that produces: "
						+ get(formatManager).get().getClass());
				}
			}
			catch (NullPointerException e)
			{
				return new FailureResult<>(
					"Default Modifier for Format: " + formatManager
						+ " cannot be null or rely on terms/functions");
			}
		}
		return ComplexResult.ofSuccess(Boolean.TRUE);
	}

	private <T> void roundRobinDefault(FormatManager<T> formatManager)
	{
		//Lack of assignment is not useless - it is detecting if this will throw an exception
		formatManager.unconvert(get(formatManager).get());
	}

}
