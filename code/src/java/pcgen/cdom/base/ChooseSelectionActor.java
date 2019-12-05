/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * A ChooseSelectionActor is an object that can apply and remove choices (based
 * on the CHOOSE token) to a PlayerCharacter. This is an object that will act
 * after a selection has been made by a user through the chooser system.
 *
 * @param <T> The Type of object chosen with this ChooseSelectionActor
 */
public interface ChooseSelectionActor<T>
{

    /**
     * Applies the given choice to the given PlayerCharacter.
     *
     * @param obj  The CDOMObject to which the choice was applied (the CDOMObject
     *             on which the CHOOSE token was present)
     * @param item The choice being applied to the given PlayerCharacter
     * @param pc   The PlayerCharacter to which the given choice should be
     *             applied.
     */
    void applyChoice(ChooseDriver obj, T item, PlayerCharacter pc);

    /**
     * Removes the given choice from the given PlayerCharacter.
     *
     * @param obj  The CDOMObject to which the choice was applied (the CDOMObject
     *             on which the CHOOSE token was present)
     * @param item The choice being removed from the given PlayerCharacter
     * @param pc   The PlayerCharacter from which the given choice should be
     *             removed.
     */
    void removeChoice(ChooseDriver obj, T item, PlayerCharacter pc);

    /**
     * Returns the source of this ChooseSelectionActor. Provided primarily to
     * allow the Token/Loader system to properly identify the source of
     * ChooseSelectionActors for purposes of unparsing.
     *
     * @return The source of this ChooseSelectionActor
     */
    String getSource();

    /**
     * Returns the LST format for this ChooseSelectionActor. Provided primarily
     * to allow the Token/Loader system to properly unparse the
     * ChooseSelectionActor.
     *
     * @return The LST format of this ChooseSelectionActor
     * @throws PersistenceLayerException if an error occurs trying to get the LST format for this
     *                                   ChooseSelectionActor
     */
    String getLstFormat() throws PersistenceLayerException;

    /**
     * Returns the class that this ChooseSelectionActor can act upon
     *
     * @return The class that this ChooseSelectionActor can act upon
     */
    Class<T> getChoiceClass();

}
