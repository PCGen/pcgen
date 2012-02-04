/*
 *  RPGeneration - A role playing utility generate interesting things
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * Class.java
 *
 * Created on November 1, 2002, 2:15 PM
 */
package pcgen.core.doomsdaybook;

/**
 * <code>Operation</code> encapsulates an action that can be performed 
 * on a variable. These are actions such as setting, adding or 
 * multiplying the variable's current value.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon D Jones
 * @version $Revision$
 */
public class Operation implements Comparable
{
	/** The identifying key of the variable the operation is to affect. */
	private String key = "";
	/** The name of the operation. */
	private String name = "";
	/** The type of action to take on the variable. */
	private String type = "";
	/** The value to be used in the operation. */
	private String value = "";

	/**
	 * Create a new Operation instance.
	 * 
	 * @param type The type of action to take on the variable.
	 * @param key The identifying key of the variable the operation is to affect.
	 * @param value The value to be used in the operation.
	 * @param name The name of the operation.
	 */
	public Operation(String type, String key, String value, String name)
	{
		this.type = type;
		this.key = key;
		this.value = value;
		this.name = name;
	}

	/**
	 * Create a new unnamed Operation instance.
	 * 
	 * @param type The type of action to take on the variable.
	 * @param key The identifying key of the variable the operation is to affect.
	 * @param value The value to be used in the operation.
	 */
	public Operation(String type, String key, String value)
	{
		this(type, key, value, "");
	}

	/**
	 * @return The current value of the key.
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @return The current value of the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return The current value of the value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj)
	{
		String title = this.toString();
		String compared = obj.toString();

		return title.compareTo(compared);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
