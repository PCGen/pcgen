/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 03-Aug-2008 22:45:18
 */

package pcgen.core.term;

import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.HandsFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class PCHandsTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    public PCHandsTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        if (pc.hasControl(CControl.CREATUREHANDS))
        {
            Logging
                    .errorPrint("HANDS term is deprecated (does not function) " + "when CREATUREHANDS CodeControl is used");
        }
        return (float) FacetLibrary.getFacet(HandsFacet.class).getHands(pc.getCharID());
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
