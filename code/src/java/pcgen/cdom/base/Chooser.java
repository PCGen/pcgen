/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.List;

import pcgen.core.PlayerCharacter;

/**
 * A Chooser is designed to be saved and restored with a PlayerCharacter. This
 * is used in situations where certain relationship information (e.g.
 * associations) needs to be uniquely restored when a PlayerCharacter is loaded
 * from a persistent state (such as a save file)
 *
 * @param <T> The type of object that this Chooser can apply to a
 *            PlayerCharacter
 */
public interface Chooser<T> extends Persistent<T>
{

    /**
     * Restores a choice to a PlayerCharacter. This method re-applies a choice
     * when a PlayerCharacter is restored from a persistent state (the
     * applyChoice method of ChoiceActor having been used to first apply the
     * choice to a PlayerCharacter).
     *
     * @param pc    The PlayerCharacter to which the choice should be restored.
     * @param owner The owning object of the choice being restored.
     * @param item  The choice being restored to the given PlayerCharacter.
     */
    void restoreChoice(PlayerCharacter pc, ChooseDriver owner, T item);

    /**
     * Removes a choice from a PlayerCharacter.
     *
     * @param pc    The PlayerCharacter from which the choice should be removed.
     * @param owner The owning object of the choice being removed.
     * @param item  The choice being removed from the given PlayerCharacter.
     */
    void removeChoice(PlayerCharacter pc, ChooseDriver owner, T item);

    /**
     * Applies the given choice to the given PlayerCharacter.
     *
     * @param owner The owning object for this choice.
     * @param item  The choice being applied to the given PlayerCharacter
     * @param pc    The PlayerCharacter to which the given choice should be
     *              applied.
     */
    void applyChoice(ChooseDriver owner, T item, PlayerCharacter pc);

    /**
     * Returns true if the given choice should be allowed for the
     * PlayerCharacter under the provided stacking conditions.
     *
     * @param item       The choice being tested to see if it should be allowed for the
     *                   given PlayerCharacter
     * @param pc         The PlayerCharacter to be used in determining if the given
     *                   choice is allowed.
     * @param allowStack True if the given choice should be allowed to stack (meaning
     *                   the PC can have more than one instance of the choice); false
     *                   otherwise
     * @return true if the given choice should be allowed for the
     * PlayerCharacter under the provided stacking conditions.
     */
    boolean allow(T item, PlayerCharacter pc, boolean allowStack);

    /**
     * Returns a list of the items *for this ChoiceActor* that have been
     * previously selected. Note that this does not identify whether a PC has
     * previously taken an item through another means (that is resolved by the
     * allow method) This returns what has previously been selected and what
     * should be placed in the 'selected' section of a chooser that is presented
     * to the user.
     *
     * @param owner The owning object for this choice.
     * @param pc    The PlayerCharacter for which the currently selected items are
     *              being returned.
     */
    List<? extends T> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc);
}
