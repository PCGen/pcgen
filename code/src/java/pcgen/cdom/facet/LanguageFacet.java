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

import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.Language;

/**
 * LanguageFacet is a Facet that tracks the Languages that have been granted to
 * a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class LanguageFacet extends AbstractSourcedListFacet<Language> implements DataFacetChangeListener<Language> {
	private AutoLanguageFacet autoLanguageFacet;

	/**
	 * Adds the Language object identified in the DataFacetChangeEvent to this
	 * LanguageFacet for the Player Character identified by the CharID in the
	 * DataFacetChangeEvent.
	 * 
	 * Triggered when one of the Facets to which LanguageFacet listens fires a
	 * DataFacetChangeEvent to indicate a Language was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<Language> dfce)
	{
		add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	/**
	 * Removes the Language object identified in the DataFacetChangeEvent from
	 * this LanguageFacet for the Player Character identified by the CharID in
	 * the DataFacetChangeEvent.
	 * 
	 * Triggered when one of the Facets to which LanguageFacet listens fires a
	 * DataFacetChangeEvent to indicate a Language was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<Language> dfce)
	{
		remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	public void setAutoLanguageFacet(AutoLanguageFacet autoLanguageFacet)
	{
		this.autoLanguageFacet = autoLanguageFacet;
	}

	@Override
	public boolean contains(CharID id, Language lang)
	{
		return super.contains(id, lang) || autoLanguageFacet.getAutoLanguage(id).contains(lang);
	}

	@Override
	public Set<Language> getSet(CharID id)
	{
		final Set<Language> ret = new WrappedMapSet<Language>(IdentityHashMap.class);
		ret.addAll(super.getSet(id));
		ret.addAll(autoLanguageFacet.getAutoLanguage(id));
		return ret;
	}

	@Override
	public boolean isEmpty(CharID id)
	{
		if (super.isEmpty(id))
		{
			return autoLanguageFacet.getAutoLanguage(id).isEmpty();
		}
		return false;
	}

	@Override
	public int getCount(CharID id)
	{
		return super.getCount(id) + autoLanguageFacet.getAutoLanguage(id).size();
	}
}
