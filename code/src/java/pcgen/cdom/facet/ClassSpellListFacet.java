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
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

public class ClassSpellListFacet extends
		AbstractSourcedListFacet<CDOMList<Spell>>
{
	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private SpellListFacet spellListFacet;

	/*
	 * While it would be ideal to listen to ClassLevelFacet and trigger off that
	 * change, it is actually a problem to do so today because subclasses need
	 * to be deployed before that can occur, so this process has to be
	 * "out of facet events" for the moment - thpr
	 */

	public void process(CharID id, PCClass pcc)
	{
		TransitionChoice<CDOMListObject<Spell>> csc =
				pcc.get(ObjectKey.SPELLLIST_CHOICE);
		if (csc == null)
		{
			addDefaultSpellList(id, pcc);
		}
		else
		{
			PlayerCharacter pc = trackingFacet.getPC(id);
			for (CDOMListObject<Spell> st : csc.driveChoice(pc))
			{
				spellListFacet.add(id, st, pcc);
			}
		}
	}

	public void addDefaultSpellList(CharID id, PCClass pcc)
	{
		spellListFacet.add(id, pcc.get(ObjectKey.CLASS_SPELLLIST), pcc);
	}

	public void setSpellListFacet(SpellListFacet spellListFacet)
	{
		this.spellListFacet = spellListFacet;
	}

}
