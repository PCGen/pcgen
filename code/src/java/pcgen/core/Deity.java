/*
 * Copyright 2001 (C) Bryan McRoberts (merton_monk@yahoo.com)
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
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.facade.core.DeityFacade;

/**
 * {@code Deity}.
 */
public final class Deity extends PObject implements DeityFacade
{
	public static final CDOMReference<DomainList> DOMAINLIST;

	static
	{
		DomainList wpl = new DomainList();
		wpl.setName("*Domains");
		DOMAINLIST = CDOMDirectSingleRef.getRef(wpl);
	}

	@Override
	public List<String> getDomainNames()
	{
		List<String> domains = new ArrayList<>();
		for (CDOMReference<Domain> ref : getSafeListMods(Deity.DOMAINLIST))
		{
			for (Domain d : ref.getContainedObjects())
			{
				domains.add(String.valueOf(d));
			}
		}
		return domains;
	}

	@Override
	public PCAlignment getAlignment()
	{
		CDOMSingleRef<PCAlignment> ref = get(ObjectKey.ALIGNMENT);
		if (ref == null)
		{
			return null;
		}
		return ref.get();
	}

	@Override
	public Collection<String> getPantheons()
	{
		Set<String> charDeityPantheon = new TreeSet<>();
		FactSetKey<String> fk = FactSetKey.valueOf("Pantheon");
		for (Indirect<String> indirect : getSafeSetFor(fk))
		{
			charDeityPantheon.add(indirect.get());
		}
		return charDeityPantheon;
	}

}
