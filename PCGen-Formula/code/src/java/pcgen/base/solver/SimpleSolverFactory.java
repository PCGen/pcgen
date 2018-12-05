/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Objects;

import pcgen.base.util.ComplexResult;
import pcgen.base.util.FailureResult;
import pcgen.base.util.FormatManager;
import pcgen.base.util.PassResult;

/**
 * A SimpleSolverFactory is a centralized location to define a shared default value for a
 * format of Solver, and then construct Solver objects loaded with that common default.
 * 
 * The format of Solver is represented by a Class object.
 */
public class SimpleSolverFactory implements SolverFactory
{

	/**
	 * The map containing the relationship between a format of Solver and the default
	 * Modifier for that format of Solver.
	 */
	private final ModifierValueStore valueStore;

	public SimpleSolverFactory(ModifierValueStore valueStore)
	{
		this.valueStore = Objects.requireNonNull(valueStore);
	}

	@Override
	public <T> void addSolverFormat(FormatManager<T> formatManager,
		Modifier<? extends T> defaultModifier)
	{
		Objects.requireNonNull(formatManager,
			"Variable/Solve FormatManager cannot be null");
		Objects.requireNonNull(defaultModifier,
			() -> "Default Modifier for Format: "
				+ formatManager.getIdentifierType() + " cannot be null");
		Modifier<?> existing = valueStore.get(formatManager);
		if (existing == null)
		{
			valueStore.addValueFor(formatManager, defaultModifier);
		}
		else if (!defaultModifier.equals(existing))
		{
			throw new IllegalArgumentException(
				"Cannot set different default values for Format: "
					+ formatManager.getIdentifierType());
		}
	}

	@Override
	@SuppressWarnings({"PMD.AvoidCatchingNPE",
		"PMD.AvoidCatchingGenericException"})
	public ComplexResult<Boolean> validateDefaults()
	{
		for (FormatManager<?> formatManager : valueStore.getStoredFormats())
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
					return new FailureResult("Format: " + formatManager
						+ " cannot use default Modifier that produces: "
						+ valueStore.get(formatManager).process(null)
							.getClass());
				}
			}
			catch (NullPointerException e)
			{
				return new FailureResult(
					"Default Modifier for Format: " + formatManager
						+ " cannot be null or rely on terms/functions");
			}
		}
		return PassResult.SUCCESS;
	}

	private <T> void roundRobinDefault(FormatManager<T> formatManager)
	{
		//Lack of assignment is not useless - it is detecting if this will throw an exception
		formatManager.unconvert(valueStore.get(formatManager).process(null));
	}

	@Override
	public <T> Solver<T> getSolver(FormatManager<T> formatManager)
	{
		return new Solver<T>(formatManager, formatManager.initializeFrom(valueStore));
	}

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return formatManager.initializeFrom(valueStore);
	}
}
