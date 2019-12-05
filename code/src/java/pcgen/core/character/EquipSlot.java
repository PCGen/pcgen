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
 */
package pcgen.core.character;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.util.Logging;

/**
 * EquipSlot
 * Contains the possible slots that equipment can go into
 * and what type of equipment can go into each slot
 **/
public final class EquipSlot implements Cloneable
{

    /**
     * the Structure of each EquipSlot is as follows:
     * <p>
     * EQSLOT:Neck     CONTAINS:PERIAPT,AMULET=1    NUMBER:HEAD
     * EQSLOT:Fingers  CONTAINS:RING=2      NUMBER:HANDS
     * <p>
     * slotName:    Name of this equipment slot (Neck, Body, etc)
     * containEqList:     What type of equipment it can contain
     * containNum:     The number of items each slot can hold
     * slotNumType:    The type of slot (used to get total number of slots)
     **/
    private String slotName = "";
    private Set<String> containEqList = new HashSet<>();
    private String slotNumType = "";
    private int containNum = 1;

    @Override
    public String toString()
    {
        return slotName;
    }

    @Override
    public EquipSlot clone()
    {
        EquipSlot newSlot;
        try
        {
            newSlot = (EquipSlot) super.clone();
        } catch (CloneNotSupportedException e)
        {
            Logging.errorPrint("Clone is not supported!", e);
            return null;
        }
        newSlot.containEqList = new HashSet<>(containEqList);

        return newSlot;
    }

    /**
     * Set container number
     *
     * @param i
     */
    public void setContainNum(final int i)
    {
        containNum = i;
    }

    /**
     * Get container type
     *
     * @return container type
     */
    public Set<String> getContainType()
    {
        return containEqList;
    }

    /**
     * Get number of slots
     *
     * @return number of slots
     */
    public int getSlotCount()
    {
        final int multi = Globals.getEquipSlotTypeCount(slotNumType);

        return multi * containNum;
    }

    /**
     * Set slot name
     *
     * @param x
     */
    public void setSlotName(final String x)
    {
        slotName = x;
    }

    /**
     * Get slot name
     *
     * @return slot name
     */
    public String getSlotName()
    {
        return slotName;
    }

    /**
     * Set slot number type
     *
     * @param x
     */
    public void setSlotNumType(final String x)
    {
        slotNumType = x;
    }

    /**
     * @return The name of the body structure this slot is located within
     */
    public String getBodyStructureName()
    {
        return slotNumType;
    }

    /**
     * TRUE if it can contain a type
     *
     * @param aTypeList
     * @return TRUE if it can contain a type
     */
    public boolean canContainType(final String aTypeList)
    {
        final StringTokenizer aTok = new StringTokenizer(aTypeList, ".", false);

        while (aTok.hasMoreTokens())
        {
            final String aType = aTok.nextToken();

            for (String allowed : containEqList)
            {
                if (aType.equalsIgnoreCase(allowed))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a contained type to the map of valid types.
     *
     * @param type The allowed item type
     */
    public void addContainedType(String type)
    {
        containEqList.add(type);
    }
}
