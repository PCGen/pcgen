/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class SizeAdjustmentLoader extends LstLineFileLoader
{
    private OverlapLoader<SizeAdjustment> loader = new OverlapLoader<>(SizeAdjustment.class);

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI) throws PersistenceLayerException
    {
        final int colonLoc = lstLine.indexOf(':');
        if (colonLoc == -1)
        {
            Logging.errorPrint("Invalid Line - does not contain a colon: '" + lstLine + "' in " + sourceURI);
            return;
        } else if (colonLoc == 0)
        {
            Logging.errorPrint("Invalid Line - starts with a colon: '" + lstLine + "' in " + sourceURI);
            return;
        } else if (colonLoc == (lstLine.length() - 1))
        {
            Logging.errorPrint("Invalid Line - ends with a colon: '" + lstLine + "' in " + sourceURI);
            return;
        }
        String key = lstLine.substring(0, colonLoc);
        String value = lstLine.substring(colonLoc + 1);
        if (!"SIZENAME".equals(key))
        {
            Logging.errorPrint(
                    "Invalid Line - " + "expected 'SIZENAME' key to start the line: '" + lstLine + "' in " + sourceURI);
            return;
        }
        loader.parseLine(context, value, sourceURI);
    }
}
