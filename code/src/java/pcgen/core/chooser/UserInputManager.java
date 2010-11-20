package pcgen.core.chooser;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
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

}