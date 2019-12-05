/*
 * BonusManager
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.bonus;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;

public class BonusPair
{
    private final Formula formula;
    public final String fullyQualifiedBonusType;
    private final Object creatorObj;

    public BonusPair(String key, Formula f, Object source)
    {
        fullyQualifiedBonusType = key;
        formula = f;
        creatorObj = source;
    }

    public Number resolve(PlayerCharacter aPC)
    {
        String source;
        if (creatorObj instanceof CDOMObject)
        {
            source = ((CDOMObject) creatorObj).getQualifiedKey();
        } else
        {
            source = Constants.EMPTY_STRING;
        }
        return formula.resolve(aPC, source);
    }
}
