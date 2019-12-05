/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 *
 */
package pcgen.core;

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;

/**
 * {@code SubClass}.
 */
public final class SubClass extends PCClass implements Categorized<SubClass>
{
    /**
     * Get the choice
     *
     * @return choice
     */
    public String getChoice()
    {
        SpellProhibitor sp = get(ObjectKey.CHOICE);
        if (sp == null)
        {
            return "";
        }

        return sp.getValueList().get(0);
    }

    /**
     * Returns the prohibitCost. If the prohibited cost has not already
     * been set, then the sub-classes cost will be returned. This preserves
     * the previous behaviour where the prohibited cost and cost were the same.
     *
     * @return int The prohibit cost for the sub-class.
     */
    public int getProhibitCost()
    {
        Integer prohib = get(IntegerKey.PROHIBIT_COST);
        if (prohib != null)
        {
            return prohib;
        }
        return getSafe(IntegerKey.COST);
    }

    @Override
    public Category<SubClass> getCDOMCategory()
    {
        return get(ObjectKey.SUBCLASS_CATEGORY);
    }

    @Override
    public void setCDOMCategory(Category<SubClass> cat)
    {
        put(ObjectKey.SUBCLASS_CATEGORY, cat);
    }

    @Override
    public String getFullKey()
    {
        return getCDOMCategory() + "." + super.getFullKey();
    }

}
