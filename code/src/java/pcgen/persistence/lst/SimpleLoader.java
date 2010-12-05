/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Loadable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class SimpleLoader<T extends Loadable> extends LstLineFileLoader
{
	private final Class<T> loadClass;

	public SimpleLoader(Class<T> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Loaded Class cannot be null");
		}
		loadClass = cl;
	}

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM, false);
		String firstToken = colToken.nextToken().trim();
		T loadable = getLoadable(context, firstToken, sourceURI);

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
								+ loadable.getClass().getSimpleName()
								+ " "
								+ loadable.getDisplayName()
								+ " of "
								+ sourceURI);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: '"
						+ token + "' in " + loadable.getClass().getSimpleName()
						+ " " + loadable.getDisplayName() + " of " + sourceURI);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(loadable, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.errorPrint("Error found loading " + loadable.getClass()
						+ " " + loadable.getDisplayName() + " from "
						+ loadable.getSourceURI());
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
		context.ref.importObject(loadable);
	}

	protected T getLoadable(LoadContext context, String firstToken, URI sourceURI)
	{
		try
		{
			T loadable = loadClass.newInstance();
			processFirstToken(firstToken, loadable);
			loadable.setSourceURI(sourceURI);
			return loadable;
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError(e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError(e);
		}
	}

	protected void processFirstToken(String token, T loadable)
	{
		loadable.setName(token);
	}

	public Class<T> getLoadClass()
	{
		return loadClass;
	}
}
