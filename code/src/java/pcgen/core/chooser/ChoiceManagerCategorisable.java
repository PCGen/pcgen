/**
 * 
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.CategorisableStore;

/**
 * @author andrew
 *
 */
public interface ChoiceManagerCategorisable {

	/**
	 * @param inNumberOfChoices
	 * @param inRequestedSelections
	 * @param inMaxNewSelections
	 */
	public abstract void initialise(int inNumberOfChoices,
			int inRequestedSelections, int inMaxNewSelections);

	/**
	 * Choose some objects out of a CategorisableStore.  The previousSelections
	 * List should only contain objects that are in the CategorisableStore   
	 *
	 * @param store
	 * @param previousSelections
	 * @return list
	 */
	public abstract List doChooser(final CategorisableStore store,
			final List previousSelections);

}