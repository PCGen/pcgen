/*
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.facade.core;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;

public interface CompanionSupportFacade
{

    /**
     * This adds a companion to this character.
     * A CharacterFacade is used instead of a CompanionFacade to make
     * sure that the added companion is an existing character. This enforces
     * that this method doesn't try to create a new character behind the scenes.
     * To implement this method, the added companion would need to be wrapped in
     * another CompanionFacade such that the backing character can be garbage
     * collected if the character is closed.
     *
     * @param companion     the companion to add
     * @param companionType The type of companion (e.g, Follower, Familiar)
     */
    void addCompanion(CharacterFacade companion, String companionType);

    /**
     * Removes a companion from this character.
     * The companion to removed will be one retrieved from the
     * {@code getCompanions} list.
     *
     * @param companion the companion to remove
     */
    void removeCompanion(CompanionFacade companion);

    /**
     * Returns a list of companions that the character can create.
     * Elements of the list are expected to be bare-bones implementations
     * of CompanionStubFacade
     *
     * @return a list of companion stubs
     */
    ListFacade<CompanionStubFacade> getAvailableCompanions();

    /**
     * Returns a map correlating a type of companion with the number
     * the character may have at most. If a particular companion type
     * can be created without limit then the mapped value should be
     * -1.
     *
     * @return a map of companion types to the maximum number the character
     * may have of them.
     */
    MapFacade<String, Integer> getMaxCompanionsMap();

    /**
     * Returns a ListFacade containing the companions that the character
     * possesses. The returned CompanionFacades should be wrappers for
     * the real implementations. This is to allow Characters to be loaded
     * and unloaded behind the scenes without it affecting the returned list
     * or its contents.
     *
     * @return a list of companions that the character currently has
     */
    ListFacade<? extends CompanionFacade> getCompanions();

}
