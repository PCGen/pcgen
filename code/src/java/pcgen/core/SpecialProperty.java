/*
 * Copyright 2004 (C) Devon Jones
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
 */
package pcgen.core;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * {@code SpecialProperty}.
 */
public final class SpecialProperty extends TextProperty
{
    public SpecialProperty()
    {
        super();
    }

    public SpecialProperty(final String name)
    {
        super(name);
    }

    //DJ: This will be the same everywhere this gets used....and currently that is spread across the code.
    //It really shouldn't be in the core layer, but it's this, or have the same code in 10 places.....
    //TODO: get this into the persistance layer
    public static SpecialProperty createFromLst(final String input)
    {
        final StringTokenizer tok = new StringTokenizer(input, Constants.PIPE, false);
        final SpecialProperty sp = new SpecialProperty();

        if (!tok.hasMoreTokens())
        {
            return sp;
        }

        String spName = tok.nextToken();
        if (PreParserFactory.isPreReqString(spName))
        {
            Logging.errorPrint("Leading PRExxx found in SPROP: " + input);
            return null;
        }

        StringBuilder sb = new StringBuilder(100);
        sb.append(spName);
        boolean hitPre = false;
        boolean warnedPre = false;
        while (tok.hasMoreTokens())
        {
            final String cString = tok.nextToken();

            // Check to see if it's a PRExxx: tag
            if (PreParserFactory.isPreReqString(cString))
            {
                hitPre = true;
                try
                {
                    final PreParserFactory factory = PreParserFactory.getInstance();
                    final Prerequisite prereq = factory.parse(cString);
                    sp.addPrerequisite(prereq);
                } catch (PersistenceLayerException ple)
                {
                    Logging.errorPrint(ple.getMessage(), ple);
                    return null;
                }
            } else
            {
                if (hitPre && !warnedPre)
                {
                    warnedPre = true;
                    Logging.deprecationPrint("Found PRExxx in middle of" + "SPROP value: " + input);
                    Logging.deprecationPrint("PRExxx should be at the end");
                }
                sb.append(Constants.PIPE);
                sb.append(cString);
            }

            if (Constants.LST_DOT_CLEAR.equals(cString))
            {
                Logging.errorPrint("Invalid/Embedded .CLEAR found in SPROP: " + input);
                return null;
            }
        }

        sp.setName(sb.toString());
        return sp;
    }
}
