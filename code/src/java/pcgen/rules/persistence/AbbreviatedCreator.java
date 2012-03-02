/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.rules.context.LoadContext;

public class AbbreviatedCreator<T extends CDOMObject> implements
		SelectionCreator<T>
{
	private final SelectionCreator<T> creator;
	private final LoadContext context;

	public AbbreviatedCreator(LoadContext lc, SelectionCreator<T> sc)
	{
		creator = sc;
		context = lc;
	}

	public static <T extends CDOMObject> SelectionCreator<T> get(
			LoadContext context, SelectionCreator<T> sc)
	{
		return new AbbreviatedCreator<T>(context, sc);
	}

	@Override
	public CDOMGroupRef<T> getAllReference()
	{
		return creator.getAllReference();
	}

	@Override
	public CDOMSingleRef<T> getReference(String key)
	{
		T ao = context.ref.getAbbreviatedObject(creator.getReferenceClass(),
				key);
		if (ao == null)
		{
			return null;
		}
		return CDOMDirectSingleRef.getRef(ao);
	}

	@Override
	public Class<T> getReferenceClass()
	{
		return creator.getReferenceClass();
	}

	@Override
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		return creator.getTypeReference(types);
	}
}
