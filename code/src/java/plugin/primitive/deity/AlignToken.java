/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.deity;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class AlignToken implements PrimitiveToken<Deity>
{

	private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;
	private static final Class<Deity> DEITY_CLASS = Deity.class;
	private PCAlignment ref;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getAbbreviatedObject(ALIGNMENT_CLASS, value);
		return ref != null;
	}

	public String getTokenName()
	{
		return "ALIGN";
	}

	public Class<Deity> getReferenceClass()
	{
		return DEITY_CLASS;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(PlayerCharacter pc, Deity deity)
	{
		return ref.equals(deity.get(ObjectKey.ALIGNMENT));
	}

	public Set<Deity> getSet(PlayerCharacter pc)
	{
		HashSet<Deity> deitySet = new HashSet<Deity>();
		for (Deity deity : Globals.getContext().ref
				.getConstructedCDOMObjects(DEITY_CLASS))
		{
			if (ref.equals(deity.get(ObjectKey.ALIGNMENT)))
			{
				deitySet.add(deity);
			}
		}
		return deitySet;
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

}
