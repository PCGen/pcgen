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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Language;

/**
 * AutoLanguageFacet is a Facet that tracks the Languages that have been granted
 * to a Player Character through the AUTO:LANG and LANGAUTO tokens
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class AutoLanguageFacet extends AbstractSourcedListFacet<Language>
		implements DataFacetChangeListener<CDOMObject>
{

	/**
	 * Processes CDOMObjects added to a Player Character to extract Languages
	 * granted to the Player Character through the AUTO:LANG: and LANGAUTO:
	 * tokens. The extracted languages are added to the Player Character.
	 * 
	 * Triggered when one of the Facets to which AutoLanguageFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObjectwas added to a Player
	 * Character.
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
		CharID id = dfce.getCharID();
		// LANGAUTO
		List<CDOMReference<Language>> list = cdo
				.getListFor(ListKey.AUTO_LANGUAGES);
		if (list != null)
		{
			for (CDOMReference<Language> ref : list)
			{
				addAll(id, ref.getContainedObjects(), cdo);
			}
		}
		// AUTO:LANG
		list = cdo.getListFor(ListKey.AUTO_LANGUAGE);
		if (list != null)
		{
			for (CDOMReference<Language> ref : list)
			{
				addAll(id, ref.getContainedObjects(), cdo);
			}
		}
	}

	/**
	 * Processes CDOMObjects removed from a Player Character to extract
	 * Languages granted to the Player Character through the AUTO:LANG: and
	 * LANGAUTO: tokens. The extracted languages are removed from a Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which AutoLanguageFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObjectwas removed from a Player
	 * Character.
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

}
