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
package plugin.qualifier.armorprof;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class EquipmentToken implements QualifierToken<ArmorProf>, Converter<Equipment, CDOMReference<ArmorProf>>
{
	private static final Type ARMOR_TYPE = Type.getConstant("Armor");

	private PrimitiveCollection<Equipment> pcs = null;

	private boolean wasRestricted = false;

	@Override
	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	@Override
	public Class<ArmorProf> getReferenceClass()
	{
		return ArmorProf.class;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName());
		if (wasRestricted)
		{
			sb.append('[').append(pcs.getLSTformat(useAny)).append(']');
		}
		return sb.toString();
	}

	@Override
	public boolean initialize(LoadContext context, SelectionCreator<ArmorProf> sc, String condition, String value,
		boolean negate)
	{
		if (negate)
		{
			Logging.addParseMessage(Level.SEVERE,
				"Cannot make " + getTokenName() + " into a negated Qualifier, remove !");
			return false;
		}
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE,
				"Cannot make " + getTokenName() + " into a conditional Qualifier, remove =");
			return false;
		}
		ReferenceManufacturer<Equipment> erm = context.getReferenceContext().getManufacturer(Equipment.class);
		if (value == null)
		{
			pcs = erm.getAllReference();
		}
		else
		{
			pcs = context.getPrimitiveChoiceFilter(erm, value);
			wasRestricted = true;
		}
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
		if (o instanceof EquipmentToken other)
		{
			if (pcs == null)
			{
				return other.pcs == null;
			}
			return pcs.equals(other.pcs);
		}
		return false;
	}

	@Override
	public GroupingState getGroupingState()
	{
		return (pcs == null) ? GroupingState.ANY : pcs.getGroupingState().reduce();
	}

	@Override
	public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<ArmorProf, R> c)
	{
		Set<R> returnSet = new HashSet<>();
		Collection<? extends ObjectContainer<ArmorProf>> intermediate = pcs.getCollection(pc, this);
		for (ObjectContainer<ArmorProf> ref : intermediate)
		{
			returnSet.addAll(c.convert(ref));
		}
		return returnSet;
	}

	@Override
	public Collection<CDOMReference<ArmorProf>> convert(ObjectContainer<Equipment> orig)
	{
		Set<CDOMReference<ArmorProf>> refSet = new HashSet<>();
		for (Equipment e : orig.getContainedObjects())
		{
			if (e.getListFor(ListKey.TYPE).contains(ARMOR_TYPE))
			{
				CDOMSingleRef<ArmorProf> prof = e.get(ObjectKey.ARMOR_PROF);
				if (prof != null)
				{
					refSet.add(prof);
				}
			}
		}
		return refSet;
	}

	@Override
	public Collection<CDOMReference<ArmorProf>> convert(ObjectContainer<Equipment> orig, PrimitiveFilter<Equipment> lim)
	{
		throw new UnsupportedOperationException("Only EquipmentToken should call itself as a Converter");
	}
}
