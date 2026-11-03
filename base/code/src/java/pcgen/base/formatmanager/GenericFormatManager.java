/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formatmanager;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * A GenericFormatManager is a FormatManager that defines the format of an
 * underlying class.
 * 
 * @param <T>
 *            The Format of the contents of a object defined by this
 *            GenericFormatManager
 */
public class GenericFormatManager<T> implements FormatManager<T>
{

	/**
	 * The ObjectDatabase used to construct or look up objects.
	 */
	private final ObjectDatabase database;

	/**
	 * The class of objects referred to by this GenericFormatManager.
	 */
	private final Class<T> underlyingClass;

	/**
	 * The identifier of the type of objects referred to by this
	 * GenericFormatManager.
	 */
	private final String identifier;

	/**
	 * Constructs a new GenericFormatManager that will use the underlying
	 * ObjectDatabase to construct and look up objects of the class of the given
	 * underlyingClass.
	 * 
	 * @param objDatabase
	 *            The ObjectDatabase used to construct or look up TableColumn
	 *            objects
	 * @param underlyingClass
	 *            The class of objects referred to by this GenericFormatManager
	 * @param identifier
	 *            The identifier of the class of objects referred to by this
	 *            GenericFormatManager
	 */
	public GenericFormatManager(ObjectDatabase objDatabase,
		Class<T> underlyingClass, String identifier)
	{
		database = Objects.requireNonNull(objDatabase);
		this.underlyingClass = Objects.requireNonNull(underlyingClass);
		this.identifier = Objects.requireNonNull(identifier);
	}

	@Override
	public T convert(String name)
	{
		return database.get(underlyingClass, Objects.requireNonNull(name));
	}

	@Override
	public Indirect<T> convertIndirect(String name)
	{
		return database.getIndirect(underlyingClass,
			Objects.requireNonNull(name));
	}

	@Override
	public String unconvert(T obj)
	{
		return database.getName(Objects.requireNonNull(obj));
	}

	@Override
	public Class<T> getManagedClass()
	{
		return underlyingClass;
	}

	@Override
	public String getIdentifierType()
	{
		return identifier;
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.empty();
	}

	@Override
	public int hashCode()
	{
		return underlyingClass.hashCode() ^ identifier.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof GenericFormatManager)
		{
			GenericFormatManager<?> other = (GenericFormatManager<?>) o;
			return underlyingClass.equals(other.underlyingClass)
				&& identifier.equals(other.identifier)
				&& database.equals(other.database);
		}
		return false;
	}

	@Override
	public boolean isDirect()
	{
		return database.isDirect();
	}
}
