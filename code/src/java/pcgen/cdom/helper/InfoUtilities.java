/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.helper;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.ResultFacet;

/**
 * InfoUtilities is a set of utilities related to the INFO and INFOVARS tokens.
 */
public final class InfoUtilities
{

    private InfoUtilities()
    {
        //Do not instantiate utility class
    }

    /**
     * Returns array of the value of the variables identified by an INFOVARS token for the
     * given identifier (CaseInsensitiveString).
     *
     * @param id  The CharID identifying the PlayerCharacter for which the array is being
     *            retrieved
     * @param cdo The CDOMObject on which the INFOVARS item is being retrieved
     * @param cis The CaseInsensitiveString identifying the specific INFOVARS item on the
     *            given CDOMObject that should be retrieved
     * @return An array of the value of the variables identified by the appropriate
     * INFOVARS item
     */
    public static Object[] getInfoVars(CharID id, CDOMObject cdo, CaseInsensitiveString cis)
    {
        String[] vars = cdo.get(MapKey.INFOVARS, cis);
        int varCount = (vars != null) ? vars.length : 0;
        Object[] replacedvars = new Object[varCount];
        if (varCount == 0)
        {
            return replacedvars;
        }
        ResultFacet resultFacet = FacetLibrary.getFacet(ResultFacet.class);
        for (int i = 0;i < varCount;i++)
        {
            replacedvars[i] = resultFacet.getLocalVariable(id, cdo, vars[i]);
        }
        return replacedvars;
    }

}
