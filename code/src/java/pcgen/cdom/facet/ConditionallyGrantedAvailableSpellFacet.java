package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.AvailableSpell;

public class ConditionallyGrantedAvailableSpellFacet
{
	private ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet;

	private AvailableSpellFacet availableSpellFacet;

	public void update(CharID id)
	{
		Collection<AvailableSpell> set =
				conditionallyAvailableSpellFacet.getQualifiedSet(id);
		for (AvailableSpell as : set)
		{
			Collection<Object> sources =
					conditionallyAvailableSpellFacet.getSources(id, as);
			for (Object source : sources)
			{
				availableSpellFacet.add(id, as.getSpelllist(), as.getLevel(),
					as.getSpell(), source);
			}
		}
	}

	public void setConditionallyAvailableSpellFacet(
		ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet)
	{
		this.conditionallyAvailableSpellFacet =
				conditionallyAvailableSpellFacet;
	}

	public void setAvailableSpellFacet(AvailableSpellFacet availableSpellFacet)
	{
		this.availableSpellFacet = availableSpellFacet;
	}

}
