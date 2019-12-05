/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.proxy.ItemProcessor;
import pcgen.base.proxy.ListProcessor;
import pcgen.base.proxy.MapProcessor;
import pcgen.base.proxy.StagingInfoFactory;

/**
 * A Utility class related to Proxy (pcgen.base.proxy) objects.
 */
public final class ProxyUtilities
{

    private ProxyUtilities()
    {
        //Do not construct utility class
    }

    private static StagingInfoFactory stagingFactory;

    /**
     * Returns a singleton (and initialized) StagingInfoFactory object.
     */
    public static StagingInfoFactory getStagingFactory()
    {
        if (stagingFactory == null)
        {
            stagingFactory = new StagingInfoFactory();
            stagingFactory.addProcessor(new ItemProcessor());
            stagingFactory.addProcessor(new ListProcessor());
            stagingFactory.addProcessor(new MapProcessor());
        }
        return stagingFactory;
    }
}
