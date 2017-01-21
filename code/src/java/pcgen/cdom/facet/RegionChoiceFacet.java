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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PlayerCharacter;

/**
 * RegionChoiceFacet is a Facet that triggers when an object has a REGION token
 * 
 */
public class RegionChoiceFacet implements DataFacetChangeListener<CharID, CDOMObject>
{
	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private RaceFacet raceFacet;

	private DeityFacet deityFacet;

	private DomainFacet domainFacet;

	private SkillFacet skillFacet;

	private TemplateFacet templateFacet;

	/**
	 * Drives selection of a Region (reacting to a REGION: token on a
	 * CDOMObject) for a CDOMObject which was added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which RegionChoiceFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		PlayerCharacter aPC = trackingFacet.getPC(id);
		if (!aPC.isImporting())
		{
			CDOMObject cdo = dfce.getCDOMObject();
			TransitionChoice<Region> region = cdo.get(ObjectKey.REGION_CHOICE);
			if (region != null)
			{
				region.act(region.driveChoice(aPC), cdo, aPC);
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		//Nothing for now?
		/*
		 * TODO Need to look into symmetry here. pc.setRegion was called in the
		 * act() method above, so we need to unset that when the owning object
		 * is removed?
		 */
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setDeityFacet(DeityFacet deityFacet)
	{
		this.deityFacet = deityFacet;
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
	 * Initializes the connections for RegionChoiceFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the RegionChoiceFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		deityFacet.addDataFacetChangeListener(this);
		domainFacet.addDataFacetChangeListener(this);
		skillFacet.addDataFacetChangeListener(this);
		templateFacet.addDataFacetChangeListener(this);
	}
}
