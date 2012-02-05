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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.character.CharacterSpell;

/**
 * ActiveSpellsFacet is a Facet that tracks the active SPELLS for the PlayerCharacter
 */
public class ActiveSpellsFacet extends AbstractSourcedListFacet<CharacterSpell>
		implements DataFacetChangeListener<CDOMObject>
{
	private RaceFacet raceFacet;

	@Override
	protected Map<CharacterSpell, Set<Object>> getComponentMap()
	{
		return new TreeMap<CharacterSpell, Set<Object>>();
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		//Nothing right now, handled in SpellsFacet
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}
	
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
	}
}