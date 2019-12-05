/*
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core.kit;

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * {@code KitFunds}.
 */
public final class KitFunds extends BaseKit
{
    private String name;
    private Formula quantity;

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private int theQty = 0;

    @Override
    public String toString()
    {
        return quantity.toString() + ' ' + name;
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        theQty = -1;
        if (quantity == null)
        {
            return false;
        }
        theQty = quantity.resolve(aPC, "").intValue();
        return true;
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        aPC.adjustGold(theQty);
    }

    @Override
    public String getObjectName()
    {
        return "Funds";
    }

    public void setQuantity(Formula formula)
    {
        quantity = formula;
    }

    @Override
    public void setName(String value)
    {
        name = value;
    }

    public String getName()
    {
        return name;
    }

    public Formula getQuantity()
    {
        return quantity;
    }
}
