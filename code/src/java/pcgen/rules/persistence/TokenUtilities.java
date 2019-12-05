/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class TokenUtilities
{

    private TokenUtilities()
    {
        // Can't instantiate utility classes
    }

    public static <T extends Loadable> CDOMReference<T> getTypeOrPrimitive(LoadContext context, Class<T> cl, String s)
    {
        return getTypeOrPrimitive(context.getReferenceContext().getManufacturer(cl), s);
    }

    public static <T extends CDOMObject> CDOMGroupRef<T> getTypeReference(LoadContext context, Class<T> cl,
            String subStr)
    {
        return getTypeReference(context.getReferenceContext().getManufacturer(cl), subStr);
    }

    public static <T extends Loadable> CDOMReference<T> getTypeOrPrimitive(ReferenceManufacturer<T> rm, String s)
    {
        if (s.startsWith(Constants.LST_TYPE_DOT) || s.startsWith(Constants.LST_TYPE_EQUAL))
        {
            return getTypeReference(rm, s.substring(5));
        } else if (s.startsWith(Constants.LST_NOT_TYPE_DOT) || s.startsWith(Constants.LST_NOT_TYPE_EQUAL))
        {
            Logging.errorPrint("!TYPE not supported in token, found: " + s);
            return null;
        } else
        {
            return rm.getReference(new String(s));
        }
    }

    public static <T extends Loadable> CDOMGroupRef<T> getTypeReference(SelectionCreator<T> rm, String s)
    {
        if (s.isEmpty())
        {
            Logging.errorPrint("Type may not be empty in: " + s);
            return null;
        }
        if (s.charAt(0) == '.' || s.charAt(s.length() - 1) == '.')
        {
            Logging.errorPrint("Type may not start or end with . in: " + s);
            return null;
        }
        String[] types = s.split("\\.");
        for (String type : types)
        {
            if (type.isEmpty())
            {
                Logging.errorPrint("Attempt to acquire empty Type in: " + s);
                return null;
            }
        }
        return rm.getTypeReference(types);
    }

    public static <T extends CDOMObject> CDOMReference<T> getReference(LoadContext context, Class<T> cl, String tokText)
    {
        CDOMReference<T> lang;
        if (Constants.LST_ALL.equals(tokText))
        {
            lang = context.getReferenceContext().getCDOMAllReference(cl);
        } else
        {
            lang = getTypeOrPrimitive(context, cl, tokText);
        }
        return lang;
    }
}
