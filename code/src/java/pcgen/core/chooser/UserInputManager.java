package pcgen.core.chooser;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

public class UserInputManager extends CDOMChoiceManager<String>
{
	public UserInputManager(CDOMObject cdo,
		ChooseInformation<String> chooseType, int cost)
	{
		super(cdo, chooseType, null, cost);
	}

	@Override
	protected ChooserInterface getChooserInstance()
	{
		ChooserInterface chooser = ChooserFactory.getUserInputInstance();
		chooser.setTitle(getTitle());
		return chooser;
	}

	@Override
	public void getChoices(PlayerCharacter pc, List<String> availableList, List<String> selectedList)
	{
		super.getChoices(pc, availableList, selectedList);
		availableList.clear();
		availableList.add(Constants.EMPTY_STRING);
	}

	/**
	 * Display a chooser to the user.
	 * 
	 * @param aPc The character the choice is for.
	 * @param availableList The list of possible choices.
	 * @param selectedList The list of existing selections.
	 * @return list The list of the new selections made by the user (unchanged if the dialog was cancelled)
	 */
	public List<String> doChooser(PlayerCharacter aPc, final List<String> availableList,
			final List<String> selectedList, final List<String> reservedList)
	{
		int effectiveChoices = getNumEffectiveChoices(selectedList, reservedList, aPc);

		final ChooserInterface chooser = getChooserInstance();
		boolean dupsAllowed = controller.isMultYes() && controller.isStackYes();
		chooser.setAllowsDups(dupsAllowed);

		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);

		chooser.setChoicesPerUnit(choicesPerUnitCost);
		chooser.setTotalChoicesAvail(effectiveChoices);
		chooser.setPoolFlag(false); // Allow cancel as clicking the x will
		// cancel anyway

		chooser.setVisible(true);

		return chooser.getSelectedList();
		
	}

}