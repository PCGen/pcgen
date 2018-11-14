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
package plugin.primitive.domain;

import java.util.Collection;
import java.util.HashSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.list.DomainList;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * DeityToken is a Primitive that includes the Domains granted by the PC's Deity.
 */
public class DeityToken implements PrimitiveToken<Domain>
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public boolean initialize(LoadContext context, Class<Domain> cl, String value, String args)
	{
		return (value == null) && (args == null);
	}

	@Override
	public String getTokenName()
	{
		return "DEITY";
	}

	@Override
	public Class<Domain> getReferenceClass()
	{
		return DOMAIN_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return getTokenName();
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DeityToken;
	}

	@Override
	public int hashCode()
	{
		return 8635;
	}

	@Override
	public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<Domain, R> c)
	{
		HashSet<R> returnSet = new HashSet<>();
		Deity deity = DeityCompat.getCurrentDeity(pc.getCharID());
		if (deity == null)
		{
			return returnSet;
		}
		CDOMReference<DomainList> list = Deity.DOMAINLIST;
		Collection<CDOMReference<Domain>> mods = deity.getListMods(list);
		for (CDOMReference<Domain> ref : mods)
		{
			Collection<AssociatedPrereqObject> assoc = deity.getListAssociations(list, ref);
			for (AssociatedPrereqObject apo : assoc)
			{
				if (PrereqHandler.passesAll(apo, pc, deity))
				{
					returnSet.addAll(c.convert(ref));
					break;
				}
			}
		}
		return returnSet;
	}
}
