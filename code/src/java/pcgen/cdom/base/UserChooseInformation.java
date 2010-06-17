package pcgen.cdom.base;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.UserInputManager;

public class UserChooseInformation implements ChooseInformation<String>,
		PersistentChoiceActor<String>
{

	public static final String UCI_NAME = "User Input";
	
	/**
	 * The title (presented to the user) of this ChoiceSet
	 */
	private String title = null;

	public Class<String> getChoiceClass()
	{
		return String.class;
	}

	public ChoiceManagerList getChoiceManager(CDOMObject owner, int cost)
	{
		return new UserInputManager(owner, this, cost);
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ALLOWS_NONE;
	}

	public String getLSTformat()
	{
		return "*USERINPUT";
	}

	public String getName()
	{
		return UCI_NAME;
	}

	public Collection<String> getSet(PlayerCharacter pc)
	{
		return Collections.singletonList("USERINPUT");
	}

	public String getTitle()
	{
		return title == null ? "Provide User Input" : title;
	}

	public CharSequence getDisplay(PlayerCharacter pc, CDOMObject owner)
	{
		return StringUtil.joinToStringBuffer(pc.getExpandedAssociations(owner),
			",");
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		String choice)
	{
		pc.addAssoc(owner, AssociationListKey.CHOOSE_NOCHOICE, choice);
		pc.addAssociation(owner, choice);
	}

	public List<String> getCurrentlySelected(CDOMObject owner,
		PlayerCharacter pc)
	{
		return pc.getAssocList(owner, AssociationListKey.CHOOSE_NOCHOICE);
	}

	public void applyChoice(CDOMObject owner, String st, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, st);
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				applyChoice(owner, pc, st, ca);
			}
		}
	}

	private void applyChoice(CDOMObject owner, PlayerCharacter pc, String st,
		ChooseSelectionActor<String> ca)
	{
		ca.applyChoice(owner, st, pc);
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner, String choice)
	{
		pc.removeAssoc(owner, AssociationListKey.CHOOSE_NOCHOICE, choice);
		pc.removeAssociation(owner, choice);
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, choice, pc);
			}
		}
	}

	public PersistentChoiceActor<String> getChoiceActor()
	{
		return this;
	}

	public void setChoiceActor(ChoiceActor<String> ca)
	{
		// ignore
	}

	public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}

	public String decodeChoice(String choice)
	{
		return choice;
	}

	public String encodeChoice(String choice)
	{
		return choice;
	}

	public void setTitle(String s)
	{
		title = s;
	}

}
