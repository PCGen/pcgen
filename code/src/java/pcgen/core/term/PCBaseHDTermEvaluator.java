/*
 * Copyright (c) 2014 Stefan Radermacher <zaister@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 07-May-2014 22:45:18
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;

/**
 * The Class {@code PCBaseHDTermEvaluator} is responsible for calculating
 * the character's unmodified number of racial hit dice, as specified by the
 * MONSTERCLASS tag in the race definition.
 */
public class PCBaseHDTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    /**
     * Instantiates a new PCBaseHDTermEvaluator.
     *
     * @param expressionString the expression string
     */
    public PCBaseHDTermEvaluator(String expressionString)
    {
        this.originalText = expressionString;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return pc.getDisplay().getBaseHD();
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
