/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterLevelFacade;

/**
 * The Class {@code CharacterLevelFacadeImpl} is an implementation of
 * the CharacterLevelFacade interface for the new user interface. It provides
 * a container for information about a particular level of the character.
 */
public class CharacterLevelFacadeImpl implements CharacterLevelFacade
{

    private final int characterLevel;
    private final PCClass pcClass;

    public CharacterLevelFacadeImpl(PCClass pcClass, int level)
    {
        this.pcClass = pcClass;
        this.characterLevel = level;
    }

    /**
     * @return the characterLevel
     */
    public int getCharacterLevel()
    {
        return characterLevel;
    }

    /**
     * @return The class taken for this level
     */
    PCClass getSelectedClass()
    {
        return pcClass;
    }

    @Override
    public String toString()
    {
        return characterLevel + " - " + String.valueOf(pcClass);
    }

}
