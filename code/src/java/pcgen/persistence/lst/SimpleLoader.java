/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.cdom.base.Loadable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;

public class SimpleLoader<T extends Loadable> extends LstLineFileLoader
{
    private final Class<T> loadClass;

    public SimpleLoader(Class<T> cl)
    {
        Objects.requireNonNull(cl, "Loaded Class cannot be null");
        loadClass = cl;
    }

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI) throws PersistenceLayerException
    {
        StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
        String firstToken = colToken.nextToken().trim();
        Loadable loadable = getLoadable(context, firstToken.intern(), sourceURI);
        if (loadable == null)
        {
            return;
        }

        while (colToken.hasMoreTokens())
        {
            processNonFirstToken(context, sourceURI, colToken.nextToken(), loadable);
        }
    }

    protected void processNonFirstToken(LoadContext context, URI sourceURI,
            String token, Loadable loadable) throws PersistenceLayerException
    {
        LstUtils.processToken(context, loadable, sourceURI, token);
    }

    protected T getLoadable(LoadContext context, String firstToken, URI sourceURI)
    {
        String name = processFirstToken(context, firstToken);
        if (name == null)
        {
            return null;
        }
        T loadable = context.getReferenceContext().constructCDOMObject(loadClass, name.intern());
        loadable.setSourceURI(sourceURI);
        return loadable;
    }

    protected String processFirstToken(LoadContext context, String token)
    {
        return token;
    }

    public Class<T> getLoadClass()
    {
        return loadClass;
    }
}
