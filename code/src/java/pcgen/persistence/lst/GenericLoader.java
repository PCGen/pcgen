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
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * 
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 6504 $
 */
public final class GenericLoader<T extends CDOMObject> extends
		LstObjectFileLoader<T>
{
	private final Class<T> baseClass;

	public GenericLoader(Class<T> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Class for GenericLoader cannot be null");
		}
		if (Modifier.isAbstract(cl.getModifiers()))
		{
			throw new IllegalArgumentException(
					"Class for GenericLoader must not be abstract");
		}
		try
		{
			if (!Modifier.isPublic(cl.getConstructor().getModifiers()))
			{
				throw new IllegalArgumentException(
						"Class for GenericLoader must have public zero-argument constructor");
			}
		}
		catch (SecurityException e)
		{
			throw new IllegalArgumentException(
					"Class for GenericLoader must have public zero-argument constructor");
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					"Class for GenericLoader must have zero-argument constructor");
		}
		baseClass = cl;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, CDOMObject, String, SourceEntry)
	 */
	@Override
	public T parseLine(LoadContext context, T aWP, String lstLine,
			SourceEntry source) throws PersistenceLayerException
	{
		T po;
		boolean isnew = false;
		if (aWP == null)
		{
			try
			{
				po = baseClass.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError(e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError(e);
			}
			isnew = true;
		}
		else
		{
			po = aWP;
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);
		if (colToken.hasMoreTokens())
		{
			po.setName(colToken.nextToken());
			po.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			po.setSourceURI(source.getURI());
			if (isnew)
			{
				context.addStatefulInformation(po);
				context.ref.importObject(po);
			}
		}

		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging
						.errorPrint("Invalid Token - does not contain a colon: '"
								+ token
								+ "' in "
								+ po.getClass().getSimpleName()
								+ " "
								+ po.getDisplayName() + " of " + source);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: '"
						+ token + "' in " + po.getClass().getSimpleName() + " "
						+ po.getDisplayName() + " of " + source);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(po, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}

		// One line each; finish the object and return null
		completeObject(context, source, po);
		return null;
	}

	/**
	 * Get the object with key aKey
	 * @param aKey
	 * 
	 * @return PObject
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(LoadContext, java.lang.String)
	 */
	@Override
	protected T getObjectKeyed(LoadContext context, String aKey)
	{
		return context.ref.silentlyGetConstructedCDOMObject(
				baseClass, aKey);
	}
}
