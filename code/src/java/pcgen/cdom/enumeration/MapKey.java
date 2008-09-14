/*
 * MapKey.java
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 11/09/2008 19:28:45
 *
 * $Id: $
 */
package pcgen.cdom.enumeration;

import pcgen.cdom.helper.Aspect;

/**
 * This is a Typesafe enumeration of legal Map Characteristics of an object. It
 * is designed to act as an index to a specific Object items within a
 * CDOMObject.
 * 
 * ListKeys are designed to store items in a CDOMObject in a type-safe
 * fashion. Note that it is possible to use the MapKey to cast the object to
 * the type of object stored by the ListKey. (This assists with Generics)
 * 
 * @param <T>
 *            The class of object stored by this MapKey.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public final class MapKey<K, V>
{

	/** ASPECT - a map key. */
	public static final MapKey<AspectName, Aspect> ASPECT = new MapKey<AspectName, Aspect>();
	/** TEST - a test map key. May be deleted and its usage replaced when a second Map Key is created. */
	public static final MapKey<String, String> TEST = new MapKey<String, String>();

	/**
	 * Private constructor to prevent instantiation of this class.
	 */
	private MapKey()
	{
		//Only allow instantation here
	}

	/**
	 * Cast an object into the MapKey's value type
	 * 
	 * @param o the object to cast
	 * 
	 * @return the object as the MapKey's value type
	 */
	public V cast(Object o)
	{
		return (V) o;
	}
}
