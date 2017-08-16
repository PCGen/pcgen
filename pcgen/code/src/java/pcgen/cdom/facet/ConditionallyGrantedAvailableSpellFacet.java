/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.AvailableSpell;

public class ConditionallyGrantedAvailableSpellFacet
{
	private ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet;

	private AvailableSpellFacet availableSpellFacet;

	public void update(CharID id)
	{
		Collection<AvailableSpell> set =
				conditionallyAvailableSpellFacet.getQualifiedSet(id);
		for (AvailableSpell as : set)
		{
			Collection<Object> sources =
					conditionallyAvailableSpellFacet.getSources(id, as);
			for (Object source : sources)
			{
				availableSpellFacet.add(id, as.getSpelllist(), as.getLevel(),
					as.getSpell(), source);
			}
		}
	}

	public void setConditionallyAvailableSpellFacet(
		ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet)
	{
		this.conditionallyAvailableSpellFacet =
				conditionallyAvailableSpellFacet;
	}

	public void setAvailableSpellFacet(AvailableSpellFacet availableSpellFacet)
	{
		this.availableSpellFacet = availableSpellFacet;
	}

}
