/*
 * KitAlignment.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Created on October 15, 2005, 10:00 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.RaceAlignment;

/**
 * Deals with the automatic assignment of an Alignment via a Kit
 */
public class KitAlignment extends BaseKit
{
	private List<PCAlignment> alignments;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient PCAlignment align = null;

	/**
	 * Actually applies the alignment to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	@Override
	public void apply(PlayerCharacter aPC)
	{
		aPC.setAlignment(align);
	}

	/**
	 * testApply
	 *
	 * @param k
	 * @param aPC PlayerCharacter
	 * @param warnings List
	 */
	@Override
	public boolean testApply(Kit k, PlayerCharacter aPC, List<String> warnings)
	{
		align = null;
		if (alignments.size() == 1)
		{
			align = alignments.get(0);
		}
		else
		{
			while (true)
			{
				List<PCAlignment> sel = new ArrayList<PCAlignment>(1);
				Globals.getChoiceFromList("Choose alignment", alignments, sel,
					1);
				if (sel.size() == 1)
				{
					align = sel.get(0);
					break;
				}
			}
		}
		apply(aPC);
		return RaceAlignment.canBeAlignment(aPC.getRace(), align);
	}

	@Override
	public String getObjectName()
	{
		return "Alignment";
	}

	@Override
	public String toString()
	{
		if (alignments == null || alignments.isEmpty())
		{
			//CONSIDER can this ever happen and not be an error that should be caught at LST load?
			return "";
		}
		if (alignments.size() == 1)
		{
			return alignments.get(0).getDisplayName();
		}
		else
		{
			// Build the string list.
			StringBuffer buf = new StringBuffer();
			buf.append("One of (");
			boolean needComma = false;
			for (PCAlignment al : alignments)
			{
				if (needComma)
				{
					buf.append(", ");
				}
				needComma = true;
				buf.append(al.getDisplayName());
			}
			buf.append(")");
			return buf.toString();
		}
	}

	public void addAlignment(PCAlignment ref)
	{
		if (alignments == null)
		{
			alignments = new ArrayList<PCAlignment>();
		}
		alignments.add(ref);
	}

	public List<PCAlignment> getAlignments()
	{
		return alignments;
	}
}
