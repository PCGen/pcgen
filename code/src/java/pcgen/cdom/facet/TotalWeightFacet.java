package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

public class TotalWeightFacet
{
	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);
	private EquipmentFacet equipmentFacet = FacetLibrary
			.getFacet(EquipmentFacet.class);

	public Float getTotalWeight(CharID id)
	{
		float totalWeight = 0;
		final Float floatZero = Float.valueOf(0);
		boolean firstClothing = true;

		PlayerCharacter pc = trackingFacet.getPC(id);
		for (Equipment eq : equipmentFacet.getSet(id))
		{
			// Loop through the list of top
			if ((eq.getCarried().compareTo(floatZero) > 0)
					&& (eq.getParent() == null))
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += (eq.getWeightAsDouble(pc) + eq
							.getContainedWeight(pc).floatValue());
				}
				else
				{
					if (firstClothing && eq.isEquipped()
							&& eq.isType("CLOTHING"))
					{
						// The first equipped set of clothing should have a
						// weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += (eq.getWeightAsDouble(pc) * Math.max(eq
								.getCarried().floatValue() - 1, 0));
					}
					else
					{
						totalWeight += (eq.getWeightAsDouble(pc) * eq
								.getCarried().floatValue());
					}
				}
			}
		}

		return Float.valueOf(totalWeight);
	}

}
