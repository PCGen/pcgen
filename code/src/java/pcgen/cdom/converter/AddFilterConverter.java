/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.converter;

import java.util.Collection;
import java.util.Objects;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.core.PlayerCharacter;

public class AddFilterConverter<B, R> implements Converter<B, R>
{

	private final Converter<B, R> converter;
	private final PrimitiveFilter<B> filter;

	public AddFilterConverter(Converter<B, R> conv, PrimitiveFilter<B> fil)
	{
		converter = Objects.requireNonNull(conv);
		filter = Objects.requireNonNull(fil);
	}

	@Override
	public Collection<? extends R> convert(ObjectContainer<B> orig)
	{
		return converter.convert(orig, filter);
	}

	@Override
	public Collection<? extends R> convert(ObjectContainer<B> orig, PrimitiveFilter<B> lim)
	{
		return converter.convert(orig, new CompoundFilter<>(filter, lim));
	}

	private static final class CompoundFilter<T> implements PrimitiveFilter<T>
	{

		private final PrimitiveFilter<T> filter1;
		private final PrimitiveFilter<T> filter2;

		CompoundFilter(PrimitiveFilter<T> fil1, PrimitiveFilter<T> fil2)
		{
			filter1 = fil1;
			filter2 = fil2;
		}

		@Override
		public boolean allow(PlayerCharacter pc, T obj)
		{
			return filter1.allow(pc, obj) == filter2.allow(pc, obj);
		}

	}

	/**
	 * Returns the consistent-with-equals hashCode for this AddFilterConverter
	 */
	@Override
	public int hashCode()
	{
		return converter.hashCode();
	}

	/**
	 * Returns true if this AddFilterConverter is equal to the given Object.
	 * Equality is defined as being another AddFilterConverter object with equal
	 * underlying contents.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof AddFilterConverter<?, ?> other)
		{
			return other.filter.equals(filter) && other.converter.equals(converter);
		}
		return false;
	}
}
