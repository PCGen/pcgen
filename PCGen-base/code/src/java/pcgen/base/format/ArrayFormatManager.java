/*
 * Copyright (c) 2015-7 Tom Parker <thpr@users.sourceforge.net>
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

import static pcgen.base.lang.StringUtil.joining;
import static pcgen.base.util.ArrayUtilities.buildOfClass;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.ArrayUtilities;
import pcgen.base.util.Converter;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.Tuple;
import pcgen.base.util.TupleUtil;
import pcgen.base.util.ValueStore;

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
	 * The list separator character used to parse instructions and separate list items
	 * that will be part of the array.
	 */
	private final char listSeparator;

	/**
	 * The group separator character used to parse instructions and separate groups of
	 * lists that will be part of the array.
	 */
	private final char groupSeparator;

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
	 *            The FormatManager representing objects contained within the array
	 * @param groupSeparator
	 *            The group separator character used to parse instructions and separate
	 *            groups of lists that will be part of the array
	 * @param listSeparator
	 *            The separator character used to parse lists in the instructions and
	 *            separate list items that will be part of the array
	 */
	public ArrayFormatManager(FormatManager<T> underlying, char groupSeparator,
		char listSeparator)
	{
		componentManager = underlying;
		@SuppressWarnings("unchecked")
		Class<T[]> arrayClass = (Class<T[]>) Array
			.newInstance(underlying.getManagedClass(), 0).getClass();
		this.formatClass = arrayClass;
		this.groupSeparator = groupSeparator;
		this.listSeparator = listSeparator;
	}

	/**
	 * Converts the instructions into an array of objects. The objects referred
	 * to in the instructions should be separated by the separator provided at
	 * construction of this ArrayFormatManager.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	@Override
	public T[] convert(String instructions)
	{
		if ((instructions == null) || instructions.isEmpty())
		{
			return ArrayUtilities.buildEmpty(componentManager.getManagedClass());
		}
		Class<T> managedClass = componentManager.getManagedClass();
		if (componentManager instanceof DispatchingFormatManager)
		{
			DispatchingFormatManager<T> dfm =
					(DispatchingFormatManager<T>) componentManager;
			String[] groups = splitInstructions(instructions, groupSeparator);
			T[] returnValue = null;
			for (String group : groups)
			{
				T[] converted = dfm.convertViaDispatch(
					fm -> new ArrayFormatManager<>(fm, groupSeparator, listSeparator), group);
				returnValue =
						ArrayUtilities.mergeArray(managedClass, returnValue, converted);
			}
			return returnValue;
		}
		return convertInternal(componentManager::convert, instructions, managedClass);
	}

	/**
	 * Converts the instructions into an Indirect array of objects. The objects
	 * referred to in the instructions should be separated by the separator
	 * provided at construction of this ArrayFormatManager.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	@Override
	public Indirect<T[]> convertIndirect(String instructions)
	{
		if ((instructions == null) || instructions.isEmpty())
		{
			return new ArrayIndirect(ArrayUtilities.buildEmpty(getIndirectClass()));
		}
		Class<Indirect<T>> managedClass = getIndirectClass();
		Indirect<T>[] array = null;
		if (componentManager instanceof DispatchingFormatManager)
		{
			DispatchingFormatManager<T> dfm =
					(DispatchingFormatManager<T>) componentManager;
			String[] groups = splitInstructions(instructions, groupSeparator);
			for (String group : groups)
			{
				Indirect<T>[] converted =
						dfm.convertViaDispatch(fm -> new Derived(fm), group);
				array = ArrayUtilities.mergeArray(managedClass, array, converted);
			}
		}
		else
		{
			array = convertInternal(componentManager::convertIndirect, instructions,
				managedClass);
		}
		return buildIndirect(array);
	}

	private <R> R[] convertInternal(Function<? super String, R> mapper,
		String instructions, Class<R> managedClass)
	{
		return Arrays.stream(splitInstructions(instructions, listSeparator))
					 .map(mapper)
					 .toArray(buildOfClass(managedClass));
	}

	/**
	 * Builds an ArrayIndirect from the given array of Indirect objects.
	 * 
	 * @param array
	 *            the Array of Indirect objects to be converted to an ArrayIndirect
	 * @return an ArrayIndirect with the contents of the given array.
	 */
	protected ArrayIndirect buildIndirect(Indirect<T>[] array)
	{
		return new ArrayIndirect(array);
	}

	private String[] splitInstructions(String instructions, char separator)
	{
		if (!StringUtil.hasValidSeparators(instructions, separator))
		{
			throw new IllegalArgumentException(
				"Poorly formatted instructions (bad separator location): "
					+ instructions);
		}
		return StringUtil.split(instructions, separator);
	}

	/**
	 * Returns the FormatManager for the class of object within the array
	 * managed by this ArrayFormatManager.
	 */
	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.of(componentManager);
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
		if (componentManager instanceof DispatchingFormatManager)
		{
			DispatchingFormatManager<T> dfm =
					(DispatchingFormatManager<T>) componentManager;
			Stream<Tuple<String, String>> unconverted =
					Arrays.stream(array).map(dfm::unconvertSeparated);
			List<String> results =
					TupleUtil.arrayLeftAndCombine(unconverted, listSeparator);
			return StringUtil.join(results, groupSeparator);
		}
		return Arrays.stream(array)
				 .map(componentManager::unconvert)
				 .collect(joining(listSeparator));
	}

	@Override
	public int hashCode()
	{
		return componentManager.hashCode() * listSeparator * groupSeparator;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ArrayFormatManager)
		{
			ArrayFormatManager<?> other = (ArrayFormatManager<?>) o;
			//skip formatClass because it is derived
			return componentManager.equals(other.componentManager)
				&& (groupSeparator == other.groupSeparator)
				&& (listSeparator == other.listSeparator);
		}
		return false;
	}

	@Override
	public boolean isDirect()
	{
		return componentManager.isDirect();
	}

	/**
	 * Returns a properly-cast version of the Indirect class object.
	 * 
	 * @return A properly-cast version of the Indirect class object.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private Class<Indirect<T>> getIndirectClass()
	{
		return (Class) Indirect.class;
	}

	/**
	 * ArrayIndirect is a wrapper that converts an array of Indirect objects into an
	 * Indirect of an array.
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
		@SuppressWarnings("PMD.ArrayIsStoredDirectly")
		private ArrayIndirect(Indirect<T>[] toSet)
		{
			array = toSet;
		}

		@Override
		public T[] get()
		{
			T[] returnArray =
					buildOfClass(componentManager.getManagedClass()).apply(array.length);
			for (int i = 0; i < array.length; i++)
			{
				returnArray[i] = array[i].get();
			}
			return returnArray;
		}

		@Override
		public String getUnconverted()
		{
			if (componentManager instanceof DispatchingFormatManager)
			{
				Stream<Tuple<String, String>> unconverted = 
						Arrays.stream(array)
							  .map(this::makeDispatched)
							  .map(Dispatched::unconvertSeparated);
				List<String> results =
						TupleUtil.arrayLeftAndCombine(unconverted, listSeparator);
				return StringUtil.join(results, groupSeparator);
			}
			return Arrays.stream(array)
						 .map(Indirect::getUnconverted)
						 .collect(joining(listSeparator));
		}

		private Dispatched makeDispatched(Indirect<T> x)
		{
			return (Dispatched) x;
		}
	}

	/**
	 * DerivedArrayFormatManager is an ArrayFormatManager that builds an Indirect using
	 * the original "parent" ArrayFormatManager, so that the Derived ArrayFormatManager
	 * (and specifically it's componentManager) is not shared.
	 */
	private final class Derived implements Converter<Indirect<T>[]>
	{
		/**
		 * The FormatManager representing objects contained within the array.
		 */
		private final FormatManager<T> derivedComponentMgr;

		private Derived(FormatManager<T> fm)
		{
			derivedComponentMgr = fm;
		}

		@Override
		public Indirect<T>[] convert(String inputStr)
		{
			return ArrayFormatManager.this.convertInternal(
				derivedComponentMgr::convertIndirect, inputStr, getIndirectClass());
		}
	}

	@Override
	public T[] initializeFrom(ValueStore valueStore)
	{
		return buildOfClass(componentManager.getManagedClass()).apply(0);
	}
}
