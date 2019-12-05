/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.analysis;

import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.util.Logging;

public final class AlignmentConverter
{
    private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;

    private AlignmentConverter()
    {
    }

    public static PCAlignment getPCAlignment(String alignKey)
    {
        PCAlignment desiredAlign;
        desiredAlign =
                Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(ALIGNMENT_CLASS, alignKey);
        if (desiredAlign == null)
        {
            Logging.errorPrint("Unable to find alignment that matches: " + alignKey);
        }
        return desiredAlign;
    }
}
