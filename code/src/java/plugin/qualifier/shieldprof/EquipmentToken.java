/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.shieldprof;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.ShieldProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class EquipmentToken implements QualifierToken<ShieldProf>
{
	private static Type SHIELD_TYPE = Type.getConstant("SHIELD");

	private PrimitiveChoiceFilter<Equipment> pcs = null;

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public Class<ShieldProf> getChoiceClass()
	{
		return ShieldProf.class;
	}

	public Set<ShieldProf> getSet(PlayerCharacter pc)
	{
		Set<ShieldProf> profs = new HashSet<ShieldProf>();
		for (Equipment e : pc.getEquipmentSet())
		{
			if (e.getListFor(ListKey.TYPE).contains(SHIELD_TYPE))
			{
				if ((pcs == null) || pcs.allow(pc, e))
				{
					CDOMSingleRef<ShieldProf> prof = e
							.get(ObjectKey.SHIELD_PROF);
					if (prof != null)
					{
						profs.add(prof.resolvesTo());
					}
				}
			}
		}
		return profs;
	}

	public String getLSTformat(boolean b)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName());
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
		}
		return sb.toString();
	}

	public boolean initialize(LoadContext context,
			SelectionCreator<ShieldProf> sc, String condition, String value,
			boolean negate)
	{
		if (negate)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a negated Qualifier, remove !");
			return false;
		}
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (value != null)
		{
			ReferenceManufacturer<Equipment> erm = context.ref
					.getManufacturer(Equipment.class);
			pcs = context.getPrimitiveChoiceFilter(erm, value);
			return pcs != null;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return pcs == null ? 0 : pcs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof EquipmentToken)
		{
			EquipmentToken other = (EquipmentToken) o;
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
		return (pcs == null) ? GroupingState.ANY : pcs.getGroupingState()
			.reduce();
	}
}
