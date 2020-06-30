/*
 * Copyright 2008-10 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.persistence.lst;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;

public class GenericLoader<T extends CDOMObject> extends LstObjectFileLoader<T>
{
	private final Class<T> baseClass;

	public GenericLoader(Class<T> cl)
	{
		Objects.requireNonNull(cl, "Class for GenericLoader cannot be null");
		if (Modifier.isAbstract(cl.getModifiers()))
		{
			throw new IllegalArgumentException("Class for GenericLoader must not be abstract");
		}
		try
		{
			if (!Modifier.isPublic(cl.getConstructor().getModifiers()))
			{
				throw new IllegalArgumentException(
					"Class for GenericLoader must have public zero-argument constructor");
			}
		}
		catch (SecurityException | NoSuchMethodException e)
		{
			throw new IllegalArgumentException("Class for GenericLoader must have public zero-argument constructor", e);
		}
		baseClass = cl;
	}

	@Override
	public final T parseLine(LoadContext context, T object, String lstLine, SourceEntry source)
    {
		T po;
		boolean isnew = false;
		if (object == null)
		{
			try
			{
				po = baseClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				throw new UnreachableError(e);
			}
			isnew = true;
		}
		else
		{
			po = object;
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		if (colToken.hasMoreTokens())
		{
			po.setName(colToken.nextToken());
			po.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			po.setSourceURI(source.getURI());
			if (isnew)
			{
				context.addStatefulInformation(po);
				context.getReferenceContext().importObject(po);
			}
		}

		while (colToken.hasMoreTokens())
		{
			LstUtils.processToken(context, po, source, colToken.nextToken());
		}

		// One line each; finish the object and return null
		completeObject(context, source, po);
		return null;
	}

	@Override
	protected final T getObjectKeyed(LoadContext context, String aKey)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(baseClass, aKey);
	}
}
