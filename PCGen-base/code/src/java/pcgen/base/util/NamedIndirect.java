/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Objects;

/**
 * A NamedIndirect is an Indirect Object that also has a String name associated with the
 * Indirect.
 * 
 * @param <T>
 *            The format of the Object returned by the underlying Indirect
 */
public class NamedIndirect<T>
{

	/**
	 * The underlying Indirect of the NamedIndirect.
	 * 
	 * This is stored as an indirect, since it is possible that it's a reference to an
	 * object that may not have been present/constructed when the instructions were
	 * processed from the persistent file format.
	 */
	private final Indirect<T> object;

	/**
	 * The name of the NamedIndirect.
	 */
	private final String name;

	/**
	 * Constructs a new NamedIndirect with the given name, Format of the value, and value
	 * of the NamedIndirect.
	 * 
	 * @param name
	 *            The name of this NamedIndirect
	 * @param manager
	 *            The FormatManager used to unconvert the value
	 * @param object
	 *            The value of this NamedIndirect
	 */
	public NamedIndirect(String name, FormatManager<T> manager, T object)
	{
		this.name = Objects.requireNonNull(name);
		this.object = new BasicIndirect<>(manager, object);
	}

	/**
	 * Constructs a new NamedIndirect with the given name, Format of the value, and
	 * instructions for the underlying value of the NamedIndirect.
	 * 
	 * @param name
	 *            The name of this NamedIndirect
	 * @param manager
	 *            The FormatManager used to convert the instructions
	 * @param instructions
	 *            The instructions to be used to determine the value of this NamedIndirect
	 */
	public NamedIndirect(String name, FormatManager<T> manager, String instructions)
	{
		this.name = Objects.requireNonNull(name);
		object = manager.convertIndirect(instructions);
	}

	/**
	 * Returns the name of the NamedIndirect.
	 * 
	 * @return The name of the NamedIndirect
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the String representation of the value of this NamedIndirect.
	 * 
	 * This String representation must be able to be provided as the instructions to a
	 * FormatManager that manages the type of Indirect contained by this NamedIndirect.
	 * 
	 * @return the String representation of the value of this NamedIndirect
	 */
	public String getUnconverted()
	{
		return object.getUnconverted();
	}

	/**
	 * Returns the value of the underlying Indirect.
	 * 
	 * @return The value of the underlying Indirect
	 */
	public T getValue()
	{
		return object.get();
	}

	@Override
	public String toString()
	{
		return getName() + '=' + getUnconverted();
	}

	@Override
	public int hashCode()
	{
		return 31 * (31 + name.hashCode()) + object.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof NamedIndirect)
		{
			NamedIndirect<?> other = (NamedIndirect<?>) obj;
			return name.equalsIgnoreCase(other.name) && object.equals(other.object);
		}
		return false;
	}
}
