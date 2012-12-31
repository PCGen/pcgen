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

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.facet.base.AbstractConditionalSpellFacet;
import pcgen.cdom.facet.base.AbstractSpellInputFacet;
import pcgen.cdom.facet.base.AbstractSpellStorageFacet;

public class KnownSpellInputFacet extends AbstractSpellInputFacet
{

	private ConditionallyKnownSpellFacet conditionallyKnownSpellFacet;

	private KnownSpellFacet knownSpellFacet;

	@Override
	protected AbstractConditionalSpellFacet getConditionalFacet()
	{
		return conditionallyKnownSpellFacet;
	}

	@Override
	protected boolean meetsAddConditions(AssociatedPrereqObject apo)
	{
		Boolean known = apo.getAssociation(AssociationKey.KNOWN);
		return (known != null) && known;
	}

	@Override
	protected AbstractSpellStorageFacet getUnconditionalFacet()
	{
		return knownSpellFacet;
	}

	public void setConditionallyKnownSpellFacet(
		ConditionallyKnownSpellFacet conditionallyKnownSpellFacet)
	{
		this.conditionallyKnownSpellFacet = conditionallyKnownSpellFacet;
	}

	public void setKnownSpellFacet(KnownSpellFacet knownSpellFacet)
	{
		this.knownSpellFacet = knownSpellFacet;
	}
}
