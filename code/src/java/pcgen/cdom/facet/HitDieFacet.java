/*
 * Copyright (c) Thomas Parker, 2012.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

public class HitDieFacet implements DataFacetChangeListener<CDOMObject>
{

	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);

	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

	private TemplateFacet templateFacet = FacetLibrary
		.getFacet(TemplateFacet.class);

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (!pc.isImporting())
		{
			boolean first = true;
			for (PCClass pcClass : classFacet.getClassSet(id))
			{
				//
				// Recalculate HPs in case HD have changed.
				//
				Modifier<HitDie> dieLock = cdo.get(ObjectKey.HITDIE);
				if (dieLock != null)
				{
					for (int level = 1; level <= classFacet.getLevel(id,
						pcClass); level++)
					{
						HitDie baseHD = pcClass.getSafe(ObjectKey.LEVEL_HITDIE);
						if (!baseHD.equals(getLevelHitDie(id, pcClass, level)))
						{
							// If the HD has changed from base reroll
							pc.rollHP(pcClass, level, first);
						}
					}
				}
				first = false;
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		// TODO Auto-generated method stub

	}

	public HitDie getLevelHitDie(CharID id, PCClass pcClass, int classLevel)
	{
		// Class Base Hit Die
		HitDie currDie = pcClass.getSafe(ObjectKey.LEVEL_HITDIE);
		Modifier<HitDie> dieLock = raceFacet.get(id).get(ObjectKey.HITDIE);
		if (dieLock != null)
		{
			currDie = dieLock.applyModifier(currDie, pcClass);
		}

		// Templates
		for (PCTemplate template : templateFacet.getSet(id))
		{
			if (template != null)
			{
				Modifier<HitDie> lock = template.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, pcClass);
				}
			}
		}

		// Levels
		PCClassLevel cl = classFacet.getClassLevel(id, pcClass, classLevel);
		if (cl != null)
		{
			if (cl.get(ObjectKey.DONTADD_HITDIE) != null)
			{
				currDie = HitDie.ZERO; //null;
			}
			else
			{
				Modifier<HitDie> lock = cl.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, pcClass);
				}
			}
		}

		return currDie;
	}
}
