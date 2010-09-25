/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.weaponprof;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class SpellCasterToken implements QualifierToken<WeaponProf>
{
	private PrimitiveChoiceFilter<WeaponProf> pcs = null;
	
	private CDOMReference<WeaponProf> allRef = null;

	public String getTokenName()
	{
		return "SPELLCASTER";
	}

	public Class<WeaponProf> getChoiceClass()
	{
		return WeaponProf.class;
	}

	public Set<WeaponProf> getSet(PlayerCharacter pc)
	{
		Set<WeaponProf> profs = new HashSet<WeaponProf>();
		if (pc.isSpellCaster(1))
		{
			for (WeaponProf wp : allRef.getContainedObjects())
			{
				boolean allow = pcs == null || pcs.allow(pc, wp);
				if (allow)
				{
					profs.add(wp);
				}
			}
		}
		return profs;
	}

	public String getLSTformat(boolean b)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('[').append(pcs.getLSTformat())
				.append(']');
		return sb.toString();
	}

	public boolean initialize(LoadContext context,
			SelectionCreator<WeaponProf> sc, String condition, String value,
			boolean negate)
	{
		if (negate)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName() + " into a negated Qualifier, remove !");
			return false;
		}
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (value == null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot use "
					+ getTokenName()
					+ " as an unconditional Qualifier, requires brackets");
			return false;
		}
		ReferenceManufacturer<WeaponProf> erm = context.ref
				.getManufacturer(WeaponProf.class);
		pcs = context.getPrimitiveChoiceFilter(erm, value);
		allRef = erm.getAllReference();
		return pcs != null;
	}

	@Override
	public int hashCode()
	{
		return pcs == null ? 0 : pcs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof SpellCasterToken)
		{
			SpellCasterToken other = (SpellCasterToken) o;
			if (pcs == null)
			{
				return other.pcs == null;
			}
			return pcs.equals(other.pcs);
		}
		return false;
	}

	public GroupingState getGroupingState()
	{
		return pcs.getGroupingState().reduce();
	}
}
