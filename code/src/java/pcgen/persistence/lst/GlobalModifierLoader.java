/*
 * LstObjecttFileLoader and GenericLoader Copyright 2008-10 (C) Tom Parker
 * <thpr@users.sourceforge.net> Copyright 2003 (C) David Hibbs
 * <sage_sam@users.sourceforge.net> Copyright 2001 (C) Bryan McRoberts
 * <merton_monk@yahoo.com>
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
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.cdom.inst.GlobalModifiers;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class GlobalModifierLoader extends LstLineFileLoader
{

    /**
     * The name of the GlobalModifiers object to be loaded with the Global Modifiers
     */
    public static final String GLOBAL_MODIFIERS = "Global Modifiers";

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI) throws PersistenceLayerException
    {
        if (lstLine.indexOf('\t') != -1)
        {
            Logging.errorPrint(
                    "Global Modifier File prohibits multiple tokens per line, ignoring: " + lstLine + " in " + sourceURI);
            return;
        }
        GlobalModifiers gm =
                context.getReferenceContext().constructNowIfNecessary(GlobalModifiers.class, GLOBAL_MODIFIERS);
        String tok = lstLine.trim();
        final String token = tok.trim();
        final int colonLoc = token.indexOf(':');
        if (colonLoc == -1)
        {
            Logging.errorPrint("Invalid Token - does not contain a colon: '" + token + "' in " + sourceURI);
            return;
        } else if (colonLoc == 0)
        {
            Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' in " + sourceURI);
            return;
        }
        String tokenName = token.substring(0, colonLoc);
        if ("MODIFY".equalsIgnoreCase(tokenName) || "MODIFYOTHER".equalsIgnoreCase(tokenName))
        {
            boolean passed = LstUtils.processToken(context, gm, sourceURI, token);
            if (!passed)
            {
                Logging.errorPrint("Failed to process line: " + lstLine + " in " + sourceURI);
            }
        } else
        {
            Logging.errorPrint("Ignored line: " + lstLine + " in " + sourceURI + " due to invalid token");
        }
    }

}
