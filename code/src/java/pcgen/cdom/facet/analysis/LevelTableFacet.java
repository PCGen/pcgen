/*
 * Copyright (c) Thomas Parker, 2009.
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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.XPTableFacet;
import pcgen.core.LevelInfo;

/**
 * LevelTableFacet stores the Experience Point table used to identify how many
 * experience points are required at a certain character level.
 */
public class LevelTableFacet
{
    private XPTableFacet xpTableFacet;

    private FormulaResolvingFacet resolveFacet;

    /**
     * Returns the (minimum) number of total Experience Points needed for a
     * given level
     *
     * @param level The character level for which to calculate the minimum
     *              required experience to reach that character level
     * @param id    The CharID representing the Player Character for which to
     *              calculate the minimum experience for a given level
     * @return The experience points needed for the given level
     */
    public int minXPForLevel(int level, CharID id)
    {
        LevelInfo info = xpTableFacet.getLevelInfo(id, level);
        if (info != null)
        {
            Formula f = FormulaFactory.getFormulaFor(info.getMinXPVariable(level));
            return resolveFacet.resolve(id, f, "").intValue();
        }

        /*
         * TODO Should this be a warning/error?
         */
        return 0;
    }

    /**
     * @param resolveFacet the resolveFacet to set
     */
    public void setResolveFacet(FormulaResolvingFacet resolveFacet)
    {
        this.resolveFacet = resolveFacet;
    }

    public void setXpTableFacet(XPTableFacet xpTableFacet)
    {
        this.xpTableFacet = xpTableFacet;
    }
}
