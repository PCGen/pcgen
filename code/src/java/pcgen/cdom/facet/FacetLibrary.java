/*
 * Copyright (c) Thomas Parker, 2009.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.helper.SpringHelper;
import pcgen.util.Logging;

/**
 * FacetLibrary is a container for the Facets that process information about
 * PlayerCharacters
 */
public final class FacetLibrary
{

    private FacetLibrary()
    {
        //Do not instantiate
    }

    private static final Map<Class<?>, Object> facets = new HashMap<>();

    public static <T> T getFacet(Class<T> cl)
    {
        T facet = (T) facets.get(cl);
        if (facet == null)
        {
            // First check for the facet being defined by Spring
            facet = SpringHelper.getBean(cl);
            if (facet == null)
            {
                // Fall back to the old hardcoded system
                Logging.errorPrint("Using Legacy Load for Facet: " + cl.getName(),
                        (Object) Thread.currentThread().getStackTrace());
                try
                {
                    facet = cl.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
                {
                    Logging.errorPrint("unable to construct old style Facet", e);
                }
            }
            facets.put(cl, facet);
        }
        return facet;
    }

}
