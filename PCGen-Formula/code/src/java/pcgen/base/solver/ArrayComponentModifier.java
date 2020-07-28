/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.Objects;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.util.FormatManager;

/**
 * An ArrayComponentModifier applies an underlying Modifier to a specific
 * location in an Array.
 *
 * @param <T>
 *            The format of object of a component of the array that this
 *            ArrayComponentModifier is modifying
 */
public class ArrayComponentModifier<T> implements Modifier<T[]>
{
	/**
	 * The location of the component in the array to be modified.
	 */
	private final int location;

	/**
	 * The Modifier to be applied to the component in the array.
	 */
	private final Modifier<T> modifier;

	/**
	 * The Format of this Modifier (an array of the format of the underlying
	 * modifier).
	 */
	private final FormatManager<T[]> format;

	/**
	 * Constructs a new ArrayComponentModifier that will modify the component in the array
	 * at the given location using the given Modifier.
	 * 
	 * @param formatManager
	 *            The format of the array this Modifier can act upon
	 * @param location
	 *            The location of the component in the array to be modified
	 * @param modifier
	 *            The Modifier to be applied to the component in the array
	 */
	public ArrayComponentModifier(FormatManager<T[]> formatManager,
		int location, Modifier<T> modifier)
	{
		if (location < 0)
		{
			throw new IllegalArgumentException("Array Location must be >= 0");
		}
		this.location = location;
		this.modifier = Objects.requireNonNull(modifier);
		format = Objects.requireNonNull(formatManager);
		FormatManager<?> componentManager = format.getComponentManager().get();
		//CONSIDER Leniency here?  Does FormatManager need something like isAssignableFrom?
		if (!componentManager.equals(modifier.getVariableFormat()))
		{
			throw new IllegalArgumentException("FormatManager manages array of "
				+ componentManager.getManagedClass().getCanonicalName()
				+ " but Modifier was "
				+ modifier.getVariableFormat().getManagedClass().getCanonicalName());
		}
	}

	/**
	 * Modifies the array, IF AND ONLY IF the array is long enough to have the
	 * modification applied. If the location given at construction of this
	 * ArrayComponentModifier is not already present in the input array, nothing
	 * will be added and the underlying modifier will be ignored.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public T[] process(EvaluationManager manager)
	{
		@SuppressWarnings("unchecked")
		T[] input = (T[]) manager.get(EvaluationManager.INPUT);
		int length = input.length;
		//Safely handle out of bounds if larger
		if (location > (length - 1))
		{
			return input;
		}
		//We create a new array as defensive (ownership not transferred on INPUT)
		T[] result = Arrays.copyOf(input, length);
		EvaluationManager subManager =
				manager.getWith(EvaluationManager.INPUT, input[location]);
		subManager = subManager.getWith(EvaluationManager.ASSERTED,
			format.getComponentManager());
		result[location] = modifier.process(subManager);
		return result;
	}

	@Override
	public void getDependencies(DependencyManager fdm)
	{
		modifier.getDependencies(fdm);
	}

	@Override
	public String getIdentification()
	{
		return modifier.getIdentification() + " (component)";
	}

	@Override
	public FormatManager<T[]> getVariableFormat()
	{
		return format;
	}

	@Override
	public long getPriority()
	{
		return modifier.getPriority();
	}

	@Override
	public String getInstructions()
	{
		return modifier.getInstructions();
	}
	
	@Override
	public String toString()
	{
		return "To [" + location + "]: +" + getInstructions();
	}
}
