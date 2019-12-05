/*
 * Copyright 2003 (C) Devon Jones
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
package pcgen.core.doomsdaybook;

import java.util.ArrayList;
import java.util.List;

public class HyphenRule implements DataElement
{
    private final List<DataValue> retList = new ArrayList<>();

    /**
     * Creates a new instance of SpaceRule
     */
    public HyphenRule()
    {
        retList.add(new DataValue("-"));
    }

    @Override
    public List<DataValue> getData()
    {
        return retList;
    }

    @Override
    public String getId()
    {
        return "-";
    }

    @Override
    public List<DataValue> getLastData()
    {
        return retList;
    }

    @Override
    public String getTitle()
    {
        return null;
    }

    @Override
    public int getWeight()
    {
        return 1;
    }
}
