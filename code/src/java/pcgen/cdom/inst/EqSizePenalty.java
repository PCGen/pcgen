/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.Loadable;
import pcgen.core.bonus.BonusObj;

public class EqSizePenalty implements Loadable
{

    private URI sourceURI;
    private String penaltyName;
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

    @Override
    public void setName(String name)
    {
        penaltyName = name;
    }

    @Override
    public String getDisplayName()
    {
        return penaltyName;
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

}
