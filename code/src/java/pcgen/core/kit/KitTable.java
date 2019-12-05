/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

public class KitTable extends BaseKit
{
    private String tableName;
    private final List<TableEntry> list = new ArrayList<>();

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public void addGear(KitGear optionInfo, Formula min, Formula max)
    {
        list.add(new TableEntry(optionInfo, min, max));
    }

    public static class TableEntry
    {
        public final KitGear gear;
        public final Formula lowRange;
        public final Formula highRange;

        public TableEntry(KitGear optionInfo, Formula min, Formula max)
        {
            gear = optionInfo;
            lowRange = min;
            highRange = max;
        }

        /**
         * True if value falls within a range
         *
         * @param pc      the PC this Kit is being applied to
         * @param inValue the value to test.
         * @return True if value falls within a range
         */
        public boolean isIn(PlayerCharacter pc, int inValue)
        {
            int lv = lowRange.resolve(pc, "").intValue();
            int hv = highRange.resolve(pc, "").intValue();
            return inValue >= lv && inValue <= hv;
        }
    }

    public List<TableEntry> getList()
    {
        return Collections.unmodifiableList(list);
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getObjectName()
    {
        return "Table";
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        throw new UnsupportedOperationException();
    }

    public KitGear getEntry(PlayerCharacter pc, int value)
    {
        for (TableEntry entry : list)
        {
            if (entry.isIn(pc, value))
            {
                return entry.gear;
            }
        }
        return null;
    }
}
