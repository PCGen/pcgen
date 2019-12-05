/*
 * Copyright 2018-9 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.function.testsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class TransparentFormatManager<T> implements FormatManager<T>
{

    public Map<String, T> map = new HashMap<>();

    private final Class<T> underlying;
    private final String identifier;

    public TransparentFormatManager(Class<T> cl, String identifier)
    {
        underlying = cl;
        this.identifier = identifier;
    }

    @Override
    public T convert(String inputStr)
    {
        return map.get(inputStr);
    }

    @Override
    public Indirect<T> convertIndirect(String inputStr)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirect()
    {
        return true;
    }

    @Override
    public String unconvert(T obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<T> getManagedClass()
    {
        return underlying;
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
}