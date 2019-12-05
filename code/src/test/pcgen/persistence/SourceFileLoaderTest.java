/*
 * Copyright (c) 2019 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Campaign;

import org.junit.jupiter.api.Test;

public final class SourceFileLoaderTest
{
    @Test
    public void testSort()
    {
        /*
         * Lowest Rank wins, in a tie, added first wins
         */
        Campaign first = new Campaign();
        first.put(IntegerKey.CAMPAIGN_RANK, 0);
        Campaign second = new Campaign();
        second.put(IntegerKey.CAMPAIGN_RANK, 1);
        Campaign third = new Campaign();
        third.put(IntegerKey.CAMPAIGN_RANK, 1);
        Campaign fourth = new Campaign();
        fourth.put(IntegerKey.CAMPAIGN_RANK, 2);
        List<Campaign> list = new ArrayList<>();
        list.add(fourth);
        list.add(second);
        list.add(third);
        list.add(first);
        SourceFileLoader.sortCampaignsByRank(list);
        assertEquals(first, list.get(0));
        assertEquals(second, list.get(1));
        assertEquals(third, list.get(2));
        assertEquals(fourth, list.get(3));
    }
}
