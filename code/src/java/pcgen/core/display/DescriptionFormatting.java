/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.display;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;

public final class DescriptionFormatting
{

    private DescriptionFormatting()
    {
    }

    public static String piWrapDesc(PObject cdo, String desc, boolean useHeader)
    {
        if (cdo.getSafe(ObjectKey.DESC_PI) && !desc.isEmpty())
        {
            final StringBuilder sb = new StringBuilder(desc.length() + 30);

            if (useHeader)
            {
                sb.append("<html>");
            }

            sb.append("<b><i>").append(desc).append("</i></b>");

            if (useHeader)
            {
                sb.append("</html>");
            }

            return sb.toString();
        }

        return desc;
    }

}
