/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.util;

import java.util.Stack;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;

/**
 * AbstractCountCommand is the base for the CountCommand and
 * CountDistinctCommand. It is a container for common behavior between the
 * two commands.
 */
public abstract class AbstractCountCommand extends PCGenCommand
{

    public enum JepAbilityCountEnum
    {
        CATEGORY, NAME, NATURE, TYPE, EXCLUDETYPE, VISIBILITY, ASPECT, CAT, NAM, NAT, TYP, VIS, KEY
    }

    public enum JepEquipmentCountEnum
    {
        TYPE, WIELDCATEGORY, LOCATION, TYP, WDC, LOC
    }

    /**
     * Get the PC that will be used to do the counting.
     *
     * @return the pc
     */
    protected PlayerCharacter getPC()
    {
        PlayerCharacter pc = null;
        if (parent instanceof VariableProcessor)
        {
            pc = ((VariableProcessor) parent).getPc();
        } else if (parent instanceof PlayerCharacter)
        {
            pc = (PlayerCharacter) parent;
        }
        return pc;
    }

    /**
     * pop maxParam parameters off the stack and populate the array.  Note, this method
     * leaves one parameter on the stack
     *
     * @param inStack  the stack of Objects
     * @param maxParam number of entries to pop from the stack
     * @return an array of Objects in reverse order, i.e. the last param popped is element
     * 0 of the array.
     */
    protected static Object[] paramStackToArray(final Stack inStack, final int maxParam)
    {
        final Object[] par = new Object[maxParam];

        if (maxParam > 0)
        {
            for (int i = maxParam - 1;i >= 0;i--)
            {
                par[i] = inStack.pop();
            }
        }

        return par;
    }

}
