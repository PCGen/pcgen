/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision: 2135 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-02-10 11:55:15 -0500 (Sat, 10 Feb 2007) $
 */
package pcgen.core.utils;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a Typesafe enumeration of legal List Characteristics of an object.
 */
public final class MapKey<K, V>
{

	public static final MapKey<String, String> AUTO_ARRAY = new MapKey<String, String>();

	/** Private constructor to prevent instantiation of this class */
	private MapKey()
	{
		//Only allow instantation here
	}
}
