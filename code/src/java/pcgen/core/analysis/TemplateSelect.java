package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;

public class TemplateSelect
{

	public static PCTemplate chooseTemplate(CDOMObject anOwner, List<PCTemplate> list,
			boolean forceChoice, PlayerCharacter aPC)
	{
		final List<PCTemplate> availableList = new ArrayList<PCTemplate>();
		for (PCTemplate pct : list)
		{
			if (PrereqHandler.passesAll(pct.getPreReqList(), aPC, pct))
			{
				availableList.add(pct);
			}
		}
	
		final List<PCTemplate> selectedList = new ArrayList<PCTemplate>(1);
		String title = "Template Choice";
		if (anOwner != null)
		{
			title += " (" + anOwner.getDisplayName() + ")";
		}
	
		if (availableList.size() == 1)
		{
			return availableList.get(0);
		}
		// If we are left without a choice, don't show the chooser.
		if (availableList.size() < 1)
		{
			return null;
		}
		Globals.getChoiceFromList(title, availableList, selectedList, 1,
			forceChoice);
		if (selectedList.size() == 1)
		{
			return selectedList.get(0);
		}
	
		return null;
	}

}
