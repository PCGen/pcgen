/**
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
 * <p>
 * <p>
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.cdom.base.ChooseDriver;
import pcgen.core.PlayerCharacter;

/**
 * Choice Manager List interface
 *
 * @param <T>
 */
public interface ChoiceManagerList<T>
{

    /**
     * Get choices
     *
     * @param aPc
     * @param availableList
     * @param selectedList
     */
    void getChoices(final PlayerCharacter aPc, final List<T> availableList, final List<T> selectedList);

    /**
     * Do chooser
     *
     * @param aPc
     * @param availableList
     * @param selectedList
     * @return the list of selected items
     */
    List<T> doChooser(PlayerCharacter aPc, final List<T> availableList, final List<T> selectedList,
            final List<String> reservedList);

    /**
     * Do chooser for removing a choice
     *
     * @param aPc
     * @param availableList
     * @param selectedList
     * @param reservedList
     */
    List<T> doChooserRemove(PlayerCharacter aPc, final List<T> availableList,
            final List<T> selectedList, final List<String> reservedList);

    /**
     * Apply the choices to the Pc
     *
     * @param aPC
     * @param selected
     */
    boolean applyChoices(final PlayerCharacter aPC, final List<T> selected);

    /**
     * Calculate the number of effective choices the user can make.
     *
     * @param selectedList The list of already selected items.
     * @param reservedList The list of options which cannot be offered.
     * @param aPc          The character the choice applies to.
     * @return The number of choices that may be made
     */
    int getNumEffectiveChoices(final List<? extends T> selectedList, final List<String> reservedList,
            PlayerCharacter aPc);

    boolean conditionallyApply(PlayerCharacter pc, T item);

    void restoreChoice(PlayerCharacter pc, ChooseDriver owner, String choice);

    void setController(ChooseController<T> cc);

    int getPreChooserChoices();

    int getChoicesPerUnitCost();

    void removeChoice(PlayerCharacter pc, ChooseDriver owner, T selection);

    void applyChoice(PlayerCharacter pc, ChooseDriver owner, T selection);

    T decodeChoice(String choice);

    String encodeChoice(T obj);

}
