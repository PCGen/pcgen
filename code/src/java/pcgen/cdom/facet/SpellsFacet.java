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

import java.util.Collection;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.content.SpellLikeAbility;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.spell.Spell;

/**
 * SpellsFacet is a Facet that tracks the PCTemplates that have been granted
 * to a Player Character through SPELLS
 */
public class SpellsFacet extends AbstractQualifiedListFacet<SpellLikeAbility>
		implements DataFacetChangeListener<CDOMObject>
{

	private CDOMObjectSourceFacet cdomSourceFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();

		Collection<CDOMReference<Spell>> mods = cdo.getListMods(Spell.SPELLS);
		if (mods == null)
		{
			return;
		}

		for (CDOMReference<Spell> ref : mods)
		{
			Collection<AssociatedPrereqObject> assocs =
					cdo.getListAssociations(Spell.SPELLS, ref);
			Collection<Spell> spells = ref.getContainedObjects();
			for (AssociatedPrereqObject apo : assocs)
			{
				Formula times =
						apo.getAssociation(AssociationKey.TIMES_PER_UNIT);
				String timeunit = apo.getAssociation(AssociationKey.TIME_UNIT);
				// The timeunit needs to default to day as per the docs
				if (timeunit == null)
				{
					timeunit = "Day";
				}
				String casterlevel =
						apo.getAssociation(AssociationKey.CASTER_LEVEL);
				String dcformula =
						apo.getAssociation(AssociationKey.DC_FORMULA);
				String book = apo.getAssociation(AssociationKey.SPELLBOOK);
				String ident = cdo.getQualifiedKey();
				for (Spell sp : spells)
				{
					SpellLikeAbility sla =
							new SpellLikeAbility(sp, times, timeunit, book,
								casterlevel, dcformula, ident);
					sla.addAllPrerequisites(apo.getPrerequisiteList());
					add(id, sla, cdo);
				}
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getSource());
	}

	public void setCdomSourceFacet(CDOMObjectSourceFacet cdomSourceFacet)
	{
		this.cdomSourceFacet = cdomSourceFacet;
	}

	public void init()
	{
		cdomSourceFacet.addDataFacetChangeListener(this);
	}
}
