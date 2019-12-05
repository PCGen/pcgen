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
package pcgen.rules.persistence;

import java.net.URI;
import java.util.Optional;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.inst.Dynamic;
import pcgen.cdom.inst.DynamicCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * This class is a LstFileLoader that processes the dynamic lst files
 */
public class DynamicLoader extends SimpleLoader<Dynamic>
{

    private static final Class<Dynamic> DYNAMIC_CLASS = Dynamic.class;

    public DynamicLoader()
    {
        super(DYNAMIC_CLASS);
    }

    @Override
    protected Dynamic getLoadable(LoadContext context, String token, URI sourceURI)
    {
        final int colonLoc = token.indexOf(':');
        if (colonLoc == -1)
        {
            Logging.errorPrint("Invalid Token - does not contain a colon: '" + token + "' in " + sourceURI);
            return null;
        } else if (colonLoc == 0)
        {
            Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' in " + sourceURI);
            return null;
        } else if (colonLoc == (token.length() - 1))
        {
            Logging.errorPrint("Invalid Token - " + "ends with a colon (no value): '" + token + "' in " + sourceURI);
            return null;
        }
        String key = token.substring(0, colonLoc);
        DynamicCategory dynamicCategory =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(DynamicCategory.class, key);
        if (dynamicCategory == null)
        {
            Logging.addParseMessage(Logging.LST_ERROR,
                    "Unsure what to do with line with prefix " + "(no DYNAMICSCOPE of this type was defined): " + key
                            + ".  Line started with: " + token + " in file: " + sourceURI);
            return null;
        }
        String name = token.substring(colonLoc + 1);
        if ((name == null) || (name.isEmpty()))
        {
            Logging.errorPrint("Invalid Token '" + key + "' had no value in " + sourceURI);
            return null;
        }
        //Only load once / allow duplicates in different files
        Dynamic d = context.getReferenceContext().getManufacturerId(dynamicCategory).getActiveObject(name);
        if (d == null)
        {
            d = new Dynamic();
            d.setName(name);
            d.setCDOMCategory(dynamicCategory);
            d.setSourceURI(sourceURI);
            context.getReferenceContext().importObject(d);
        }
        return d;
    }

    @Override
    protected void processNonFirstToken(LoadContext context, URI sourceURI,
            String token, Loadable loadable) throws PersistenceLayerException
    {
        Dynamic d = (Dynamic) loadable;
        Optional<String> localScopeName = d.getLocalScopeName();
        if (localScopeName.isPresent())
        {
            context = context.dropIntoContext(localScopeName.get());
        }
        super.processNonFirstToken(context, sourceURI, token, loadable);
    }
}
