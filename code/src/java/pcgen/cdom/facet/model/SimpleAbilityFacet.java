package pcgen.cdom.facet.model;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;

public class SimpleAbilityFacet extends AbstractListFacet<CharID, Ability>
		implements DataFacetChangeListener<CharID, CNAbilitySelection>
{

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
	{
		add(dfce.getCharID(), dfce.getCDOMObject().getCNAbility().getAbility());
	}

	@Override
	public void dataRemoved(
		DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
	{
		remove(dfce.getCharID(), dfce.getCDOMObject().getCNAbility().getAbility());
	}

}
