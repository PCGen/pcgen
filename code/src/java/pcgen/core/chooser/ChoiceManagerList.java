/**
 * ChoiceManagerList.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PlayerCharacter;

/**
 * Choice Manager List interface
 * @param <T> 
 */
public interface ChoiceManagerList<T> {

	/**
	 * return handled chooser
	 * @return handled chooser
	 */
	public abstract String typeHandled();

	/**
	 * Get choices
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public abstract void getChoices(
			final PlayerCharacter aPc,
			final List<T> availableList,
			final List<T> selectedList);

	/**
	 * Do chooser
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @return the list of selected items
	 */
	public abstract List<T> doChooser(
			PlayerCharacter aPc,
			final List<T> availableList,
			final List<T> selectedList,
			final List<String> reservedList);

	/**
	 * Do chooser for removing a choice
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public abstract void doChooserRemove (
			PlayerCharacter aPc,
			final List<T> availableList,
			final List<T> selectedList,
			final List<String> reservedList);


	/**
	 * Apply the choices to the Pc
	 *
	 * @param aPC
	 * @param selected
	 */
	public abstract boolean applyChoices(
			final PlayerCharacter aPC,
			final List<T> selected);

	/**
	 * Calculate the number of effective choices the user can make.
	 *  
	 * @param selectedList The list of already selected items.
	 * @param reservedList 
	 * @return The number of choices that may be made 
	 */
	public int getNumEffectiveChoices(final List<T> selectedList,
		final List<String> reservedList);

	public abstract boolean conditionallyApply(PlayerCharacter pc, T item);

	public abstract void restoreChoice(PlayerCharacter pc, CDOMObject owner, String choice);

	public void setController(ChooseController<T> cc);

	public int getPreChooserChoices();

	public int getChoicesPerUnitCost();

}
