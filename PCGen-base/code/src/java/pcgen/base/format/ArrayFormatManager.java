/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.format;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * An ArrayFormatManager wraps an underlying FormatManager to produce arrays of
 * objects.
 * 
 * @param <T>
 *            The format (class) of object contained within the array that this
 *            ArrayFormatManager manages.
 */
public class ArrayFormatManager<T> implements FormatManager<T[]>
{

	/**
	 * The separator character used to parse instructions and separate items
	 * that will be part of the array.
	 */
	private final char separator;

	/**
	 * The FormatManager representing objects contained within the array.
	 */
	private final FormatManager<T> componentManager;

	/**
	 * The class showing the class of array managed by this ArrayFormatManager.
	 */
	private final Class<T[]> formatClass;

	/**
	 * Constructs a new ArrayFormatManager with the given underlying component
	 * FormatManager and separator.
	 * 
	 * @param underlying
	 *            The FormatManager representing objects contained within the
	 *            array
	 * @param separator
	 *            The separator character used to parse instructions and
	 *            separate items that will be part of the array
	 */
	public ArrayFormatManager(FormatManager<T> underlying, char separator)
	{
		@SuppressWarnings("unchecked")
		Class<T[]> fClass = (Class<T[]>) Array
			.newInstance(underlying.getManagedClass(), 0).getClass();
		this.separator = separator;
		formatClass = fClass;
		componentManager = underlying;
	}

	/**
	 * Converts the instructions into an array of objects. The objects referred
	 * to in the instructions should be separated by the separator provided at
	 * construction of this ArrayFormatManager.
	 */
	@Override
	public T[] convert(String instructions)
	{
		Class<T> componentClass = componentManager.getManagedClass();
		if ((instructions == null) || instructions.isEmpty())
		{
			@SuppressWarnings("unchecked")
			T[] toSet = (T[]) Array.newInstance(componentClass, 0);
			return toSet;
		}
		if (!hasValidSeparators(instructions))
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (bad separator location): "
					+ instructions);
		}
		String[] items = instructions.split(Character.toString(separator));
		@SuppressWarnings("unchecked")
		T[] toSet = (T[]) Array.newInstance(componentClass, items.length);
		for (int i = 0; i < items.length; i++)
		{
			T obj = componentManager.convert(items[i]);
			toSet[i] = obj;
		}
		return toSet;
	}

	private boolean hasValidSeparators(String value)
	{
		//assume not empty due to checks on instructions
		return (value.charAt(0) != separator)
			&& (value.charAt(value.length() - 1) != separator)
			&& (!value.contains(String.valueOf(new char[]{separator, separator})));
	}

	/**
	 * Converts the instructions into an Indirect array of objects. The objects
	 * referred to in the instructions should be separated by the separator
	 * provided at construction of this ArrayFormatManager.
	 */
	@Override
	public Indirect<T[]> convertIndirect(String instructions)
	{
		/*
		 * Common code with convertObjectContainer may be a surprise, but
		 * consider that the instructions may not just be a,b,c. They could be
		 * things like: a,GROUP=b. The GROUP= will return more than one object,
		 * so on everything here, we want to check ObjectContainer to get the
		 * full set of items.
		 */
		return convertInstructions(instructions);
	}

	private ArrayIndirect convertInstructions(String instructions)
	{
		if ((instructions == null) || instructions.isEmpty())
		{
			@SuppressWarnings("unchecked")
			Indirect<T>[] toSet =
					(Indirect<T>[]) Array.newInstance(Indirect.class, 0);
			return new ArrayIndirect(toSet);
		}
		if (!hasValidSeparators(instructions))
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (bad separator location): "
					+ instructions);
		}
		String[] items = instructions.split(Character.toString(separator));
		@SuppressWarnings("unchecked")
		Indirect<T>[] toSet =
				(Indirect<T>[]) Array.newInstance(Indirect.class, items.length);
		for (int i = 0; i < items.length; i++)
		{
			Indirect<T> indirect = componentManager.convertIndirect(items[i]);
			toSet[i] = indirect;
		}
		return new ArrayIndirect(toSet);
	}

	/**
	 * Returns the FormatManager for the class of object within the array
	 * managed by this ArrayFormatManager.
	 */
	@Override
	public FormatManager<?> getComponentManager()
	{
		return componentManager;
	}

	/**
	 * Returns the identifier type for this ArrayFormatManager.
	 */
	@Override
	public String getIdentifierType()
	{
		return "ARRAY[" + componentManager.getIdentifierType() + "]";
	}

	/**
	 * Returns the class for this ArrayFormatManager (will be an Array.class
	 * with the appropriate component class).
	 */
	@Override
	public Class<T[]> getManagedClass()
	{
		return formatClass;
	}

	/**
	 * Unconverts the given array using this ArrayFormatManager.
	 */
	@Override
	public String unconvert(T[] array)
	{
		StringBuilder result = new StringBuilder();

		boolean needjoin = false;

		for (T obj : array)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(componentManager.unconvert(obj));
		}
		return result.toString();
	}

	@Override
	public int hashCode()
	{
		return componentManager.hashCode() * separator;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ArrayFormatManager)
		{
			ArrayFormatManager<?> other = (ArrayFormatManager<?>) o;
			return componentManager.equals(other.componentManager)
				&& (separator == other.separator);
		}
		return false;
	}

	/**
	 * ArrayIndirect is a facade that can convert an ObjectContainer<T>[] into
	 * an ObjectContainer<T[]>.
	 * 
	 * This is necessary since the underlying componentManager will return
	 * ObjectContainer<T> objects (which is more than one ObjectContainer, and
	 * can be put into an array), but the interface for FormatManager is
	 * ObjectContainer<T[]> (a single ObjectContainer resolving to an array).
	 */
	private final class ArrayIndirect implements Indirect<T[]>
	{
		/**
		 * The array of ObjectContainer objects used to resolve this
		 * ArrayObjectContainer.
		 */
		private final Indirect<T>[] array;

		/**
		 * Constructs a new ArrayObjectContainer with the given underlying
		 * underlying ObjectContainer containing the objects for this
		 * ArrayObjectContainer.
		 * 
		 * @param toSet
		 *            The underlying ObjectContainer with the objects contained
		 *            in this ArrayObjectContainer
		 */
		private ArrayIndirect(Indirect<T>[] toSet)
		{
			array = toSet;
		}

		@Override
		public T[] get()
		{
			Class<T> arrayClass = componentManager.getManagedClass();
			List<T> returnList = new ArrayList<>(array.length * 5);
			for (Indirect<T> indirect : array)
			{
				returnList.add(indirect.get());
			}
			@SuppressWarnings("unchecked")
			T[] toReturn =
					(T[]) Array.newInstance(arrayClass, returnList.size());
			for (int i = 0; i < array.length; i++)
			{
				toReturn[i] = returnList.get(i);
			}
			return toReturn;
		}

		@Override
		public String getUnconverted()
		{
			StringBuilder result = new StringBuilder();
			boolean needjoin = false;
			for (Indirect<T> indirect : array)
			{
				if (needjoin)
				{
					result.append(separator);
				}
				needjoin = true;
				result.append(indirect.getUnconverted());
			}
			return result.toString();
		}

	}

	@Override
	public boolean isDirect()
	{
		return componentManager.isDirect();
	}
}
