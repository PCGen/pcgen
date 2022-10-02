/**
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
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.cdom.base.ChooseDriver;
import pcgen.core.PlayerCharacter;

/**
 * Choice Manager List interface
 * @param <T> 
 */
public interface ChoiceManagerList<T>
{

	/**
	 * Get choices
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public abstract void getChoices(PlayerCharacter aPc, List<T> availableList, List<T> selectedList);

	/**
	 * Do chooser
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @return the list of selected items
	 */
	public abstract List<T> doChooser(PlayerCharacter aPc, List<T> availableList, List<T> selectedList, List<String> reservedList);

	/**
	 * Do chooser for removing a choice
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @param reservedList 
	 */
	public abstract List<T> doChooserRemove(PlayerCharacter aPc, List<T> availableList, List<T> selectedList, List<String> reservedList);

	/**
	 * Apply the choices to the Pc
	 *
	 * @param aPC
	 * @param selected
	 */
	public abstract boolean applyChoices(PlayerCharacter aPC, List<T> selected);

	/**
	 * Calculate the number of effective choices the user can make.
	 *  
	 * @param selectedList The list of already selected items.
	 * @param reservedList The list of options which cannot be offered.
	 * @param aPc The character the choice applies to.
	 * @return The number of choices that may be made 
	 */
	public int getNumEffectiveChoices(List<? extends T> selectedList, List<String> reservedList,
		PlayerCharacter aPc);

	public abstract boolean conditionallyApply(PlayerCharacter pc, T item);

	public abstract void restoreChoice(PlayerCharacter pc, ChooseDriver owner, String choice);

	public void setController(ChooseController<T> cc);

	public int getPreChooserChoices();

	public int getChoicesPerUnitCost();

	public void removeChoice(PlayerCharacter pc, ChooseDriver owner, T selection);

	public void applyChoice(PlayerCharacter pc, ChooseDriver owner, T selection);

	public abstract T decodeChoice(String choice);

	public abstract String encodeChoice(T obj);

}
