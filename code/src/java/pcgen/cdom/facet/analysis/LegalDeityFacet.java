/*
 * Copyright (c) Thomas Parker, 2010.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.analysis;

import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.PrerequisiteFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.core.Deity;
import pcgen.core.PCClass;

/**
 * LegalDeityFacet tracks the Deity objects which the Player Character may
 * select.
 */
public class LegalDeityFacet
{
    private ClassFacet classFacet;

    private PrerequisiteFacet prerequisiteFacet;

    /*
     * Note this facet makes no sense to turn into a "push" facet that is a
     * listener to classes. The reason it makes no sense is that no token
     * defaults to ANY, and loading the default ANY reference into a cache and
     * running contains against it would probably be slower than just testing
     * each Class every time.
     */

    /**
     * Returns true if selection of the given Deity is allowed by the Player
     * Character identified by the given CharID.
     *
     * @param id     The CharID identifying the Player Character to be checked to
     *               see if the given Deity is an allowed selection
     * @param aDeity The Deity to be checked to see if it is an allowed selection
     *               by the Player Character identified by the given CharID
     * @return true if selection of the given Deity is allowed by the Player
     * Character identified by the given CharID; false otherwise
     */
    public boolean allows(CharID id, Deity aDeity)
    {
        if (aDeity == null)
        {
            return false;
        }
        boolean result;
        if (classFacet.isEmpty(id))
        {
            result = true;
        } else
        {
            result = false;
            CLASS:
            for (PCClass aClass : classFacet.getSet(id))
            {
                List<CDOMReference<Deity>> deityList = aClass.getListFor(ListKey.DEITY);
                if (deityList == null)
                {
                    result = true;
                    break;
                } else
                {
                    for (CDOMReference<Deity> deity : deityList)
                    {
                        if (deity.contains(aDeity))
                        {
                            result = true;
                            break CLASS;
                        }
                    }
                }
            }
        }

        return result && prerequisiteFacet.qualifies(id, aDeity, aDeity);
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
    {
        this.prerequisiteFacet = prerequisiteFacet;
    }

}
