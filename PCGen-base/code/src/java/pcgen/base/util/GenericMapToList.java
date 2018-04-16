/*
 * Copyright 2004, 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * Created on Aug 29, 2004 Imported into PCGen on June 18, 2005.
 */
package pcgen.base.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import pcgen.base.lang.UnreachableError;

/**
 * Represents a Map of objects to Lists. List management is done internally to
 * this class (while copies are accessible, the lists are kept private to this
 * class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), GenericMapToList will maintain a reference to the given
 * Object. GenericMapToList will not modify any of the Objects it is passed;
 * however, it reserves the right to return references to Objects it contains to
 * other Objects.
 * 
 * However, when any method in which GenericMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * Since GenericMapToList leverages existing classes that implement
 * java.util.Map, it also inherits any limitations on those classes. For
 * example, if the underlying Map is a java.util.HashMap, then modifying an
 * object in this set to alter the hashCode of that object may result in
 * unpredictable behavior from the GenericMapToList. Be careful to read the
 * documentation on the underlying Map class to ensure appropriate treatment of
 * objects placed in the GenericMapToList.
 * 
 * CAUTION: This is a convenience method for use in Java 1.4 and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate)
 * 
 * @param <K>
 *            The Class of the key for this GenericMapToList
 * @param <V>
 *            The Class of the Value for this GenericMapToList
 */
public class GenericMapToList<K, V> extends AbstractMapToList<K, V>
{

	/**
	 * Stores the Class to be used as the underlying Map for the map from the
	 * key to the contained lists.
	 */
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> underlyingClass;

	/**
	 * Creates a new GenericMapToList, using the given Class as the underlying
	 * class for construction of the Map.
	 * 
	 * @param cl
	 *            The Class (extending Map) to be used as the underlying Map of
	 *            this GenericMapToList
	 * @throws ReflectiveOperationException 
	 *             if there is a problem in constructing the Map
	 * @throws NullPointerException
	 *             if the given Class is null
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public GenericMapToList(Class<? extends Map> cl) throws ReflectiveOperationException
	{
		super(cl.getConstructor().newInstance());
		underlyingClass = cl;
	}

	/**
	 * A convenience method for constructing a new GenericMapToList. This is
	 * generally used to avoid catching InstantiationException and
	 * IllegalAccessException (both thrown by GenericMapToList's constructor) as
	 * well as reducing typing in construction of a parameterized
	 * GenericMapToList
	 * 
	 * @param <K>
	 *            The type used as the Key in the GenericMapToList
	 * @param <V>
	 *            The type used as values in the GenericMapToList
	 * @param cl
	 *            The class to be used as the underlying class in the
	 *            GenericMapToList
	 * @return A new, empty GenericMapToList with the given Class as the
	 *         underlying Class
	 * @throws IllegalArgumentException
	 *             if the given Class does not have a public, zero argument
	 *             constructor
	 * @throws NullPointerException
	 *             if the given Class is null
	 */
	@SuppressWarnings("rawtypes")
	public static <K, V> GenericMapToList<K, V> getMapToList(
		Class<? extends Map> cl)
	{
		try
		{
			return new GenericMapToList<>(cl);
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalArgumentException(
				"Class for GenericMapToList must possess "
					+ "a public zero-argument constructor", e);
		}
	}

	/**
	 * Creates a new Set for use by AbstractMapToList. It is intended that this
	 * will only be used by AbstractMapToList.
	 * 
	 * Ownership of the constructed Set is transferred to the calling object,
	 * and no reference to it is maintained by GenericMapToList due to this
	 * method call.
	 */
	@Override
	protected Set<K> getEmptySet()
	{
		try
		{
			@SuppressWarnings("unchecked")
			Map<K, Boolean> map = underlyingClass.getConstructor().newInstance();
			return Collections.newSetFromMap(map);
		}
		catch (ReflectiveOperationException e)
		{
			throw new UnreachableError("Constructor should have caught this", e);
		}
	}
}
