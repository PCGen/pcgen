/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCClass.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

public class ClassSpellApplication
{

	public static void chooseClassSpellList(PlayerCharacter pc, PCClass cl)
	{
		TransitionChoice<CDOMListObject<Spell>> csc = cl.get(ObjectKey.SPELLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (pc.getLevel(cl) < 1))
		{
			return;
		}
	
		pc.removeAllAssocs(cl, AssociationListKey.CLASSSPELLLIST);
		for (CDOMListObject<Spell> st : csc.driveChoice(pc))
		{
			cl.addClassSpellList(st, pc);
		}
	}

}
