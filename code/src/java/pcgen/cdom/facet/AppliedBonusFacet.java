package pcgen.cdom.facet;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.BonusObj;

public class AppliedBonusFacet extends AbstractListFacet<BonusObj> implements
		DataFacetChangeListener<CDOMObject>
{
	private AddedBonusFacet addedBonusFacet = FacetLibrary
			.getFacet(AddedBonusFacet.class);

	private PrerequisiteFacet prereqFacet = FacetLibrary
			.getFacet(PrerequisiteFacet.class);

	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		processAdd(id, cdo, cdo.getSafeListFor(ListKey.BONUS));
		processAdd(id, cdo, addedBonusFacet.getSet(id, cdo));
	}

	private void processAdd(CharID id, CDOMObject cdo,
			List<? extends BonusObj> bonusList)
	{
		for (BonusObj bonus : bonusList)
		{
			if (prereqFacet.qualifies(id, bonus, cdo))
			{
				add(id, bonus);
			}
			else
			{
				// TODO Is this necessary? Shouldn't be present anyway...
				remove(id, bonus);
			}
		}
	}

	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		processRemove(id, cdo, cdo.getSafeListFor(ListKey.BONUS));
		processRemove(id, cdo, addedBonusFacet.getSet(id, cdo));
	}

	private void processRemove(CharID id, CDOMObject cdo,
			List<? extends BonusObj> bonusList)
	{
		for (BonusObj bonus : bonusList)
		{
			remove(id, bonus);
		}
	}

}
