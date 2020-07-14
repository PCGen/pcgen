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
package pcgen.base.formatmanager;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * A EnumFormatManager is a FormatManager that defines the format of an underlying Enum.
 * 
 * @param <T>
 *            The Enum Format underlying this EnumFormatManager
 */
public class EnumFormatManager<T extends Enum<T>> implements FormatManager<T>
{

	/**
	 * The Class of Enum objects referred to by this EnumFormatManager.
	 */
	private final Class<T> underlyingClass;

	/**
	 * The identifier of the type of objects referred to by this EnumFormatManager.
	 */
	private final String identifier;

	/**
	 * Constructs a new EnumFormatManager for the given Enum class.
	 * 
	 * @param underlyingEnum
	 *            The Enum referred to by this EnumFormatManager
	 * @param identifier
	 *            The identifier of the Enum referred to by this EnumFormatManager
	 */
	public EnumFormatManager(Class<T> underlyingEnum, String identifier)
	{
		this.underlyingClass = Objects.requireNonNull(underlyingEnum);
		this.identifier = Objects.requireNonNull(identifier);
	}

	@Override
	public T convert(String name)
	{
		for (T each : underlyingClass.getEnumConstants())
		{
			if (each.name().compareToIgnoreCase(name) == 0)
			{
				return each;
			}
		}
		throw new IllegalArgumentException(
			name + " does not represent a constant in " + identifier + " ("
				+ underlyingClass.getCanonicalName() + ")");
	}

	@Override
	public Indirect<T> convertIndirect(String name)
	{
		return new BasicIndirect<>(this, convert(name));
	}

	@Override
	public String unconvert(T obj)
	{
		return obj.name();
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
		if (o instanceof EnumFormatManager)
		{
			EnumFormatManager<?> other = (EnumFormatManager<?>) o;
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
