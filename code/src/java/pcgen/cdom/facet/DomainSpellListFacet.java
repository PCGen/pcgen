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

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Domain;
import pcgen.core.spell.Spell;

public class DomainSpellListFacet extends
		AbstractSourcedListFacet<CDOMList<Spell>> implements
		DataFacetChangeListener<Domain>
{

	private SpellListFacet spellListFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<Domain> dfce)
	{
		DomainSpellList list =
				dfce.getCDOMObject().get(ObjectKey.DOMAIN_SPELLLIST);
		//list should never be null??
		spellListFacet.add(dfce.getCharID(), list, dfce.getCDOMObject());
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<Domain> dfce)
	{
		spellListFacet.removeAll(dfce.getCharID(), dfce.getSource());
	}

	public void setSpellListFacet(SpellListFacet spellListFacet)
	{
		this.spellListFacet = spellListFacet;
	}

}
