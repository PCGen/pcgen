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
import pcgen.cdom.enumeration.ObjectKey;

/**
 * HasAnyFavoredClassFacet is a Facet that tracks if the Player Character has
 * "ANY" ("HIGHESTCLASS") as a Favored Class.
 */
public class HasAnyFavoredClassFacet extends AbstractSourcedListFacet<Boolean>
		implements DataFacetChangeListener<CDOMObject>
{

	private RaceFacet raceFacet;

	private TemplateFacet templateFacet;
	
	private ConditionalTemplateFacet conditionalTemplateFacet;

	/**
	 * Triggered when one of the Facets to which HasAnyFavoredClassFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		Boolean hdw = cdo.get(ObjectKey.ANY_FAVORED_CLASS);
		if (hdw != null)
		{
			add(dfce.getCharID(), hdw, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which HasAnyFavoredClassFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}
	
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		templateFacet.addDataFacetChangeListener(this);
		conditionalTemplateFacet.addDataFacetChangeListener(this);
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setConditionalTemplateFacet(
		ConditionalTemplateFacet conditionalTemplateFacet)
	{
		this.conditionalTemplateFacet = conditionalTemplateFacet;
	}

}
