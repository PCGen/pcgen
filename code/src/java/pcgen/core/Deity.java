/*
 * Deity.java
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
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.DeityFacade;

/**
 * <code>Deity</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
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

	public String getSource()
	{
		return SourceFormat.getFormattedString(this,
			Globals.getSourceDisplay(), true);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getDisplayName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getDomainNames()
	{
		List<String> domains = new ArrayList<String>();
		for (CDOMReference<Domain> ref : getSafeListMods(Deity.DOMAINLIST))
		{
			for (Domain d : ref.getContainedObjects())
			{
				domains.add(String.valueOf(d));
			}
		}
		return domains;
	}

	public AlignmentFacade getAlignment()
	{
		return get(ObjectKey.ALIGNMENT);
	}

	public List<String> getPantheons()
	{
		List<String> pantheons = new ArrayList<String>();
		for (Pantheon pantheon : getSafeListFor(ListKey.PANTHEON))
		{
			String string = pantheon.toString();
			pantheons.add(string);
		}
		
		return pantheons;
	}

}
