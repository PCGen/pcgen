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
package pcgen.base.calculation;

import java.lang.reflect.Array;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.inst.ScopeInformation;

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
	private final Class<T[]> format;

	/**
	 * Constructs a new ArrayComponentModifier that will modify the component in
	 * the array at the given location using the given Modifier.
	 * 
	 * @param loc
	 *            The location of the component in the array to be modified
	 * @param mod
	 *            The Modifier to be applied to the component in the array
	 */
	public ArrayComponentModifier(int loc, Modifier<T> mod)
	{
		if (loc < 0)
		{
			throw new IllegalArgumentException("Array Location must be >= 0");
		}
		if (mod == null)
		{
			throw new IllegalArgumentException("Modifier must not be null");
		}
		location = loc;
		modifier = mod;
		@SuppressWarnings("unchecked")
		Class<T[]> fmt =
				(Class<T[]>) Array.newInstance(mod.getVariableFormat(), 0)
					.getClass();
		format = fmt;
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
	public T[] process(T[] input, ScopeInformation scopeInfo, Object source)
	{
		if (location > input.length - 1)
		{
			return input;
		}
		@SuppressWarnings("unchecked")
		T[] newArray =
				(T[]) Array.newInstance(modifier.getVariableFormat(),
					input.length);
		System.arraycopy(input, 0, newArray, 0, input.length);
		newArray[location] = modifier.process(input[location], scopeInfo, source);
		return newArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getDependencies(DependencyManager fdm)
	{
		modifier.getDependencies(fdm);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentification()
	{
		return modifier.getIdentification() + " (component)";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<T[]> getVariableFormat()
	{
		return format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInherentPriority()
	{
		return modifier.getInherentPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getUserPriority()
	{
		return modifier.getUserPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInstructions()
	{
		return modifier.getInstructions();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "To [" + location + "]: +" + getInstructions();
	}
}
