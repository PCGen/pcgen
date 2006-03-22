/**
 * 
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.CategorisableStore;
import pcgen.core.PlayerCharacter;

/**
 * @author andrew
 *
 */
public interface ChoiceManagerList {

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
			final List availableList,
			final List selectedList);

	/**
	 * Do chooser
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 * @return the list of selected items
	 */
	public abstract List doChooser(
			PlayerCharacter aPc,
			final List availableList,
			final List selectedList,
			final List selectedBonusList);

	
	/**
	 * Apply the choices to the Pc
	 * 
	 * @param aPC
	 * @param selected
	 * @param selectedBonusList
	 */
	public abstract void applyChoices(
			final PlayerCharacter aPC,
			final List selected,
			List selectedBonusList);

}