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
package pcgen.base.solver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import pcgen.base.formula.base.DefaultStore;
import pcgen.base.util.FormatManager;

/**
 * A SolverFactory is a centralized location to define a shared default value
 * for a format of Solver, and then construct Solver objects loaded with that
 * common default.
 * 
 * The format of Solver is represented by a Class object.
 */
public class SolverFactory implements DefaultStore
{

	/**
	 * The map containing the relationship between a format of Solver and the
	 * default Modifier for that format of Solver.
	 */
	private final Map<Class<?>, Modifier<?>> defaultModifierMap =
			new HashMap<Class<?>, Modifier<?>>();

	/**
	 * Adds a relationship between a Solver format and a default Modifier for
	 * that format of Solver to this SolverFactory.
	 * 
	 * The default Modifier MUST NOT depend on anything (it must be able to
	 * accept both a null ScopeInformation and null input value to its process
	 * method). (See SetNumberModifier for an example of this)
	 * 
	 * The default Modifier for a format of Solver may not be redefined for a
	 * SolverFactory. Once a given default Modifier has been established for a
	 * format of Solver, this method MUST NOT be called a second time for that
	 * format of Solver.
	 * 
	 * @param <T>
	 *            The format (class) of object changed by the given Modifier
	 * @param varFormat
	 *            The format of Solver for which the given Modifier should be
	 *            the default value
	 * @param defaultModifier
	 *            The Modifier to be used as the default Modifier for the given
	 *            Solver format
	 * @throws IllegalArgumentException
	 *             if either parameter is null, if the given Modifier has
	 *             dependencies, or if the given Solver format already has a
	 *             default Modifier defined for this SolverFactory
	 */
	@SuppressWarnings({"PMD.AvoidCatchingNPE", "PMD.AvoidCatchingGenericException"})
	public <T> void addSolverFormat(Class<T> varFormat,
		Modifier<? extends T> defaultModifier)
	{
		Objects.requireNonNull(varFormat, "Variable/Solve Format Class cannot be null");
		Objects.requireNonNull(defaultModifier,
			"Default Modifier for Format: " + varFormat + " cannot be null");
		Modifier<?> existing = defaultModifierMap.get(varFormat);
		if (existing == null)
		{
			defaultModifierMap.put(varFormat, defaultModifier);
		}
		else if (!defaultModifier.equals(existing))
		{
			throw new IllegalArgumentException(
				"Cannot set different default values for Format: " + varFormat);
		}
	}

	/**
	 * Validates the defaults added to the addSolverFormat method.  
	 */
	public ComplexResult validateDefaults()
	{
		for (Entry<Class<?>, Modifier<?>> entry : defaultModifierMap.entrySet())
		{
			try
			{
				Object defaultValue = entry.getValue().process(null);
				if (!entry.getKey().isAssignableFrom(defaultValue.getClass()))
				{
					//Generics were violated during addSolverFormat if we got here
					return new FailureResult("Default Modifier for Format: "
							+ entry.getKey() + " cannot produce: " + defaultValue.getClass());
				}
			}
			catch (NullPointerException e)
			{
				return new FailureResult("Default Modifier for Format: "
					+ entry.getKey() + " cannot be null or rely on terms/functions");
			}
		}
		return Result.SUCCESS;
	}

	/**
	 * Returns a new Solver for the given format, which will use the given
	 * ScopeInformation. The default value of the Solver is loaded based on
	 * values previously provided to the addSolverType() method of the
	 * SolverFactory.
	 * 
	 * @param <T>
	 *            The format (class) of object managed by the given
	 *            FormatManager
	 * @param formatManager
	 *            The FormatManager used to manage items in this generated
	 *            Solver
	 * @return A new Solver with default Modifier stored in this SolverFactory
	 *         and with the given ScopeInformation
	 * @throws IllegalArgumentException
	 *             if no default Modifier for the given format has been provided
	 *             with the addSolverType method on SolverFactory
	 */
	public <T> Solver<T> getSolver(FormatManager<T> formatManager)
	{
		@SuppressWarnings("unchecked")
		Modifier<T> defaultModifier =
				(Modifier<T>) defaultModifierMap.get(formatManager
					.getManagedClass());
		if (defaultModifier == null)
		{
			throw new IllegalArgumentException(
				"Cannot create Solver of format " + formatManager
					+ " because no default was provided for that format");
		}
		return new Solver<T>(defaultModifier);
	}

	/**
	 * Returns the default value for a given Format (provided as a Class).
	 * 
	 * @param <T>
	 *            The format (class) of object for which the default value
	 *            should be returned
	 * @param varFormat
	 *            The Class (data format) for which the default value should be
	 *            returned
	 * @return The default value for the given Format
	 */
	@Override
	public <T> T getDefault(Class<T> varFormat)
	{
		@SuppressWarnings("unchecked")
		Modifier<T> defaultModifier =
				(Modifier<T>) defaultModifierMap.get(varFormat);
		return defaultModifier.process(null);
	}
}
