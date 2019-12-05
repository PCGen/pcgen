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

public class NegateFilterConverter<B, R> implements Converter<B, R>
{

    private final Converter<B, R> converter;

    public NegateFilterConverter(Converter<B, R> conv)
    {
        Objects.requireNonNull(conv, "Converter cannot be null");
        converter = conv;
    }

    @SuppressWarnings("rawtypes")
    private static final PrimitiveFilter PREVENT = (pc, obj) -> false;

    @SuppressWarnings("unchecked")
    private static final <T> PrimitiveFilter<T> getPrevent()
    {
        return PREVENT;
    }

    @Override
    public Collection<? extends R> convert(ObjectContainer<B> orig)
    {
        return converter.convert(orig, getPrevent());
    }

    @Override
    public Collection<? extends R> convert(ObjectContainer<B> orig, PrimitiveFilter<B> lim)
    {
        return converter.convert(orig, new InvertingFilter<>(lim));
    }

    private static final class InvertingFilter<T> implements PrimitiveFilter<T>
    {

        private final PrimitiveFilter<T> filter;

        private InvertingFilter(PrimitiveFilter<T> fil)
        {
            Objects.requireNonNull(fil, "PrimitiveFilter cannot be null");
            filter = fil;
        }

        @Override
        public boolean allow(PlayerCharacter pc, T obj)
        {
            return !filter.allow(pc, obj);
        }

    }

    /**
     * Returns the consistent-with-equals hashCode for this
     * NegateFilterConverter
     */
    @Override
    public int hashCode()
    {
        return converter.hashCode();
    }

    /**
     * Returns true if this NegateFilterConverter is equal to the given Object.
     * Equality is defined as being another NegateFilterConverter object with
     * equal underlying contents.
     */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof NegateFilterConverter)
                && ((NegateFilterConverter<?, ?>) obj).converter.equals(converter);
    }
}
