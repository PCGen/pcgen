package pcgen.core.chooser;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserFactory;

public class UserInputManager extends CDOMChoiceManager<String>
{
	public UserInputManager(ChooseDriver cdo,
		ChooseInformation<String> chooseType, int cost)
	{
		super(cdo, chooseType, null, cost);
	}

	/**
	 * Display a chooser to the user.
	 * 
	 * @param aPc The character the choice is for.
	 * @param availableList The list of possible choices.
	 * @param selectedList The list of existing selections.
	 * @return list The list of the new selections made by the user (unchanged if the dialog was cancelled)
	 */
    @Override
	public List<String> doChooser(PlayerCharacter aPc, final List<String> availableList,
			final List<String> selectedList, final List<String> reservedList)
	{
		int effectiveChoices = getNumEffectiveChoices(selectedList, reservedList, aPc);

		boolean dupsAllowed = controller.isMultYes() && controller.isStackYes();

		Globals.sortChooserLists(availableList, selectedList);

		String title = StringUtils.isBlank(info.getTitle()) ? "in_chooser" :  info.getTitle();
		if (title.startsWith("in_"))
		{
			title = LanguageBundle.getString(title);
		}
		
		CDOMChooserFacadeImpl<String> chooserFacade =
				new CDOMChooserFacadeImpl<String>(
						title, availableList, 
					selectedList, effectiveChoices);
		chooserFacade.setAllowsDups(dupsAllowed);
		chooserFacade.setInfoFactory(new Gui2InfoFactory(aPc));
		chooserFacade.setUserInput(true);
		ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
		
		return chooserFacade.getFinalSelected();
	}

}