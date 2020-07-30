/*
 * Copyright (c) Thomas Parker, 2009.
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
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * KitChoiceFacet is a Facet that triggers when an object has a KIT token in
 * order to drive the selection and application of the Kit(s).
 * 
 */
public class KitChoiceFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	private RaceFacet raceFacet;

	private DomainFacet domainFacet;

	private SkillFacet skillFacet;

	private TemplateFacet templateFacet;

	/**
	 * Drives kit selection and adds the selected Kits to the Player Character.
	 * 
	 * Triggered when one of the Facets to which KitChoiceFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		PlayerCharacter aPC = trackingFacet.getPC(id);
		if (!aPC.isImporting())
		{
			CDOMObject cdo = dfce.getCDOMObject();
			for (TransitionChoice<Kit> kit : cdo.getSafeListFor(ListKey.KIT_CHOICE))
			{
				kit.act(kit.driveChoice(aPC), cdo, aPC);
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which KitChoiceFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		/*
		 * Nothing for now?
		 * 
		 * TODO This is another tricky item for symmetry - Kits are often
		 * "fire and forget" - the problem being if the object which granted the
		 * Kit is removed, it is likely that the underlying kit should be
		 * removed as well...
		 */
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setDomainFacet(DomainFacet domainFacet)
	{
		this.domainFacet = domainFacet;
	}

	public void setSkillFacet(SkillFacet skillFacet)
	{
		this.skillFacet = skillFacet;
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	/**
	 * Initializes the connections for KitChoiceFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the KitChoiceFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		domainFacet.addDataFacetChangeListener(this);
		skillFacet.addDataFacetChangeListener(this);
		templateFacet.addDataFacetChangeListener(this);
	}
}
