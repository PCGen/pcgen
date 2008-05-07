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

import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
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
 * CAUTION: This is a convenience method for use in Java 1.4 and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate)
 */
public class GenericMapToList<K, V> extends AbstractMapToList<K, V>
{

	private final Class<? extends Map> underlyingClass;

	/**
	 * Creates a new GenericMapToList
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public GenericMapToList(Class<? extends Map> cl)
			throws InstantiationException, IllegalAccessException
	{
		super(cl.newInstance());
		underlyingClass = cl;
	}

	public static <K, V> GenericMapToList<K, V> getMapToList(
			Class<? extends Map> cl)
	{
		try
		{
			return new GenericMapToList<K, V>(cl);
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Class for GenericMapToList must possess a zero-argument constructor",
					e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Class for GenericMapToList must possess a public zero-argument constructor",
					e);
		}
	}

	@Override
	protected Set<K> getEmptySet()
	{
		return new WrappedMapSet<K>(underlyingClass);
	}
}
