/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * {@code KitSelect}.
 */
public final class KitSelect extends BaseKit
{
    private Formula theFormula;

    /**
     * Get formula
     *
     * @return formula
     */
    public Formula getFormula()
    {
        return theFormula;
    }

    /**
     * Set formula
     *
     * @param aFormula
     */
    public void setFormula(Formula aFormula)
    {
        theFormula = aFormula;
    }

    @Override
    public String toString()
    {
        return theFormula.toString();
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        aKit.setSelectValue(theFormula.resolve(aPC, "").intValue());
        return true;
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        // Nothing to do.
    }

    @Override
    public String getObjectName()
    {
        return "Select";
    }
}
