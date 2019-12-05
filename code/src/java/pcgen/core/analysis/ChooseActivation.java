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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;

public final class ChooseActivation
{

    private ChooseActivation()
    {
    }

    /**
     * Check if an object has a new style choose. As at Oct 2013 this meant any
     * CHOOSE other than for an equipment modifier or a number (i.e. temporary
     * bonus).
     *
     * @param po The object to be checked.
     * @return true if the object has a new style choice.
     */
    public static boolean hasNewChooseToken(CDOMObject po)
    {
        return po.get(ObjectKey.CHOOSE_INFO) != null;
    }

}
