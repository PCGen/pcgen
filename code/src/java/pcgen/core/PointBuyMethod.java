/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.Loadable;
import pcgen.core.bonus.BonusObj;

/**
 * {@code PointBuyMethod}.
 */
public final class PointBuyMethod implements BonusContainer, Loadable
{
    private URI sourceURI;
    private String methodName = "";
    private String pointFormula = "0";
    private List<BonusObj> bonusList;

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    public String getPointFormula()
    {
        return pointFormula;
    }

    public void setPointFormula(final String argFormula)
    {
        pointFormula = argFormula;
    }

    @Override
    public String toString()
    {
        return methodName;
    }

    public String getDescription()
    {
        String desc = methodName;
        if (!pointFormula.equals("0"))
        {
            desc += " (" + pointFormula + ')';
        }
        return desc;
    }

    public void addBonus(BonusObj bon)
    {
        if (bonusList == null)
        {
            bonusList = new ArrayList<>();
        }
        bonusList.add(bon);
    }

    public Collection<BonusObj> getBonuses()
    {
        if (bonusList == null)
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(bonusList);
    }

    /**
     * returns all BonusObj's that are "active"
     *
     * @param pc TODO
     * @return active bonuses
     */
    @Override
    public List<BonusObj> getActiveBonuses(PlayerCharacter pc)
    {
        final List<BonusObj> aList = new ArrayList<>();
        for (BonusObj bonus : getBonuses())
        {
            if (pc.isApplied(bonus))
            {
                aList.add(bonus);
            }
        }
        return aList;
    }

    /**
     * Sets all the BonusObj's to "active"
     *
     * @param aPC
     */
    @Override
    public void activateBonuses(final PlayerCharacter aPC)
    {
        for (BonusObj bonus : getBonuses())
        {
            aPC.setApplied(bonus, bonus.qualifies(aPC, null));
        }
    }

    @Override
    public String getDisplayName()
    {
        return methodName;
    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public void setName(String name)
    {
        methodName = name;
    }
}
