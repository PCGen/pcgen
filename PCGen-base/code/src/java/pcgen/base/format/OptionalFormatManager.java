/*
 * Copyright (c) 2019 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.format;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.ValueStore;

/**
 * An OptionalFormatManager wraps an underlying FormatManager to produce Optional of an
 * object.
 * 
 * @param <T>
 *            The format (class) of object contained within the Optional that this
 *            OptionalFormatManager manages.
 */
public class OptionalFormatManager<T> implements FormatManager<Optional<T>>
{

	/**
	 * The FormatManager representing the object potentially contained within the
	 * Optional.
	 */
	private final FormatManager<T> componentManager;

	/**
	 * Constructs a new OptionalFormatManager with the given underlying component
	 * FormatManager.
	 * 
	 * @param underlying
	 *            The FormatManager representing objects potentially contained within the
	 *            Optional
	 */
	public OptionalFormatManager(FormatManager<T> underlying)
	{
		componentManager = Objects.requireNonNull(underlying);
	}

	/**
	 * Converts the instructions into an Optional object.
	 */
	@Override
	public Optional<T> convert(String instructions)
	{
		if ((instructions == null) || instructions.isEmpty())
		{
			return Optional.empty();
		}
		return Optional.of(componentManager.convert(instructions));
	}

	/**
	 * Converts the instructions into an Indirect array of objects. The objects referred
	 * to in the instructions should be separated by the separator provided at
	 * construction of this ArrayFormatManager.
	 */
	@Override
	public Indirect<Optional<T>> convertIndirect(String instructions)
	{
		if ((instructions == null) || instructions.isEmpty())
		{
			return new BasicIndirect<>(this, Optional.empty());
		}
		Indirect<T> indirect = componentManager.convertIndirect(instructions);
		return new OptionalIndirect(indirect);
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.of(componentManager);
	}

	@Override
	public String getIdentifierType()
	{
		return "OPTIONAL[" + componentManager.getIdentifierType() + "]";
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Class<Optional<T>> getManagedClass()
	{
		return (Class) Optional.class;
	}

	/**
	 * OptionalIndirect is a wrapper that converts an Indirect object into an Indirect of
	 * an Optional object.
	 */
	private final class OptionalIndirect implements Indirect<Optional<T>>
	{
		/**
		 * The underlying Indirect object used to resolve this OptionalIndirect.
		 */
		private final Indirect<T> underlying;

		/**
		 * Constructs a new OptionalIndirect with the given underlying underlying Indirect
		 * containing the objects for this OptionalIndirect.
		 * 
		 * @param underlying
		 *            The underlying Indirect with the objects contained in this
		 *            OptionalIndirect
		 */
		private OptionalIndirect(Indirect<T> underlying)
		{
			this.underlying = underlying;
		}

		@Override
		public Optional<T> get()
		{
			return Optional.of(underlying.get());
		}

		@Override
		public String getUnconverted()
		{
			return underlying.getUnconverted();
		}
	}

	@Override
	public String unconvert(Optional<T> optional)
	{
		return optional.map(componentManager::unconvert).orElse("");
	}

	@Override
	public int hashCode()
	{
		return componentManager.hashCode() + 1;
	}

	@Override
	public boolean equals(Object o)
	{
		return (o instanceof OptionalFormatManager) && componentManager
			.equals(((OptionalFormatManager<?>) o).componentManager);
	}

	@Override
	public boolean isDirect()
	{
		return componentManager.isDirect();
	}

	@Override
	public Optional<T> initializeFrom(ValueStore valueStore)
	{
		return Optional.empty();
	}
}
