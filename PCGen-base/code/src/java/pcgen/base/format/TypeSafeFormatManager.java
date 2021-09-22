/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * A TypeSafeFormatManager is a FormatManager that defines the format of an underlying
 * TypeSafeConstant.
 * 
 * @param <T>
 *            The TypeSafeConstant Format underlying this TypeSafeFormatManager
 */
public class TypeSafeFormatManager<T extends TypeSafeConstant>
		implements FormatManager<T>
{

	/**
	 * The Class of TypeSafeConstant objects referred to by this TypeSafeFormatManager.
	 */
	private final Class<T> underlyingClass;

	/**
	 * The identifier of the type of objects referred to by this TypeSafeFormatManager.
	 */
	private final String identifier;

	/**
	 * Constructs a new TypeSafeFormatManager for the given TypeSafeConstant class.
	 * 
	 * @param underlyingClass
	 *            The TypeSafeConstant Class referred to by this TypeSafeFormatManager
	 * @param identifier
	 *            The identifier of the TypeSafeConstant referred to by this
	 *            TypeSafeFormatManager
	 */
	public TypeSafeFormatManager(Class<T> underlyingClass, String identifier)
	{
		this.underlyingClass = Objects.requireNonNull(underlyingClass);
		this.identifier = Objects.requireNonNull(identifier);
		if (identifier.length() == 0)
		{
			throw new IllegalArgumentException(
				"Cannot construct TypeSafeFormatManager for "
					+ underlyingClass.getCanonicalName()
					+ " with an empty identifier");
		}
		try
		{
			getItemsInClass();
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException(
				"Cannot construct TypeSafeFormatManager for "
					+ underlyingClass.getCanonicalName()
					+ " because it doesn't have an accessible getAllConstants method"
					+ " that returns a Collection",
				e);
		}
	}

	@Override
	public T convert(String name)
	{
		Collection<T> items = getItemsInClass();
		for (T each : items)
		{
			if (each.toString().compareToIgnoreCase(name) == 0)
			{
				return each;
			}
		}
		throw new IllegalArgumentException(
			name + " does not represent a constant in " + identifier + " ("
				+ underlyingClass.getCanonicalName() + ")");
	}

	@SuppressWarnings("unchecked")
	private Collection<T> getItemsInClass()
	{
		try
		{
			return (Collection<T>) underlyingClass.getMethod("getAllConstants")
				.invoke(null);
		}
		catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			throw new IllegalArgumentException(
				"Error encountred fetching a constant in " + identifier + " ("
					+ underlyingClass.getCanonicalName() + ")",
				e);
		}
	}

	@Override
	public Indirect<T> convertIndirect(String name)
	{
		return new BasicIndirect<>(this, convert(name));
	}

	@Override
	public String unconvert(T obj)
	{
		return obj.toString();
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
		if (o instanceof TypeSafeFormatManager)
		{
			TypeSafeFormatManager<?> other = (TypeSafeFormatManager<?>) o;
			return underlyingClass.equals(other.underlyingClass)
				&& identifier.equals(other.identifier);
		}
		return false;
	}

	@Override
	public boolean isDirect()
	{
		return true;
	}
}
