/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
package pcgen.core;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.util.Logging;

public final class BenefitFormatting
{

    private BenefitFormatting()
    {
    }

    public static String getBenefits(PlayerCharacter aPC, List<?> objList)
    {
        if (objList.isEmpty())
        {
            return "";
        }
        PObject sampleObject;
        Object b = objList.get(0);
        if (b instanceof PObject)
        {
            sampleObject = (PObject) b;
        } else if (b instanceof CNAbility)
        {
            sampleObject = ((CNAbility) b).getAbility();
        } else
        {
            Logging.errorPrint("Unable to resolve Description with object of type: " + b.getClass().getName());
            return "";
        }
        List<Description> theBenefits = sampleObject.getListFor(ListKey.BENEFIT);
        if (theBenefits == null)
        {
            return Constants.EMPTY_STRING;
        }
        final StringBuilder buf = new StringBuilder(250);
        boolean needSpace = false;
        for (final Description desc : theBenefits)
        {
            final String str = desc.getDescription(aPC, objList);
            if (!str.isEmpty())
            {
                if (needSpace)
                {
                    buf.append(' ');
                }
                buf.append(str);
                needSpace = true;
            }
        }
        return buf.toString();
    }

}
