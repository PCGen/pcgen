package pcgen.cdom.facet;

import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class AbilitySelectionApplication implements
		DataFacetChangeListener<CharID, CNAbilitySelection>
{
	private final PlayerCharacterTrackingFacet pcFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
	{
		CharID id = dfce.getCharID();
		PlayerCharacter pc = pcFacet.getPC(id);
		CNAbilitySelection cnas = dfce.getCDOMObject();
		Ability ability = cnas.getCNAbility().getAbility();
		String selection = cnas.getSelection();
		if (selection != null)
		{
			ChooseInformation<?> chooseInfo =
					ability.get(ObjectKey.CHOOSE_INFO);
			if (chooseInfo != null)
			{
				applySelection(pc, chooseInfo, ability, selection);
			}
		}
	}

	private <T> void applySelection(PlayerCharacter pc,
		ChooseInformation<T> chooseInfo, Ability ability, String selection)
	{
		T obj = chooseInfo.decodeChoice(Globals.getContext(), selection);
		chooseInfo.getChoiceActor().applyChoice(ability, obj, pc);
	}

	@Override
	public void dataRemoved(
		DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
	{
		CharID id = dfce.getCharID();
		CNAbilitySelection cnas = dfce.getCDOMObject();
		PlayerCharacter pc = pcFacet.getPC(id);
		Ability ability = cnas.getCNAbility().getAbility();
		String selection = cnas.getSelection();
		if (selection != null)
		{
			ChooseInformation<?> chooseInfo =
					ability.get(ObjectKey.CHOOSE_INFO);
			if (chooseInfo != null)
			{
				removeSelection(pc, chooseInfo, ability, selection);
			}
		}
	}

	private <T> void removeSelection(PlayerCharacter pc,
		ChooseInformation<T> chooseInfo, Ability ability, String selection)
	{
		T obj = chooseInfo.decodeChoice(Globals.getContext(), selection);
		chooseInfo.getChoiceActor().removeChoice(pc, ability, obj);
	}

}
