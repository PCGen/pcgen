/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter.loader;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.gui.converter.Loader;
import pcgen.gui.converter.TokenConverter;
import pcgen.gui.converter.event.TokenProcessEvent;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class BasicLoader<T extends CDOMObject> implements Loader
{

	public static final String FIELD_SEPARATOR = "\t"; //$NON-NLS-1$
	private final Class<T> cdomClass;
	private final ListKey<CampaignSourceEntry> listkey;
	private final LoadContext context;

	public BasicLoader(LoadContext lc, Class<T> cl,
			ListKey<CampaignSourceEntry> lk)
	{
		context = lc;
		cdomClass = cl;
		listkey = lk;
	}

	public void process(StringBuilder sb, int line, String lineString)
			throws PersistenceLayerException, InterruptedException
	{
		String[] tokens = lineString.split(FIELD_SEPARATOR);
		if (tokens.length == 0)
		{
			return;
		}
		sb.append(tokens[0]);
		for (int tok = 1; tok < tokens.length; tok++)
		{
			String token = tokens[tok];
			if (token.length() == 0)
			{
				sb.append(FIELD_SEPARATOR);
				continue;
			}

			T obj = context.ref.constructCDOMObject(cdomClass, line + "Test"
					+ tok);
			processToken(sb, obj, token);
		}
	}

	private void processToken(StringBuilder sb, CDOMObject obj, String token)
			throws PersistenceLayerException, InterruptedException
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: "
					+ token);
			return;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: " + token);
			return;
		}

		String key = token.substring(0, colonLoc);
		String value = (colonLoc == token.length() - 1) ? null : token
				.substring(colonLoc + 1);
		TokenProcessEvent tpe = new TokenProcessEvent(context, key, value, obj);
		String error = TokenConverter.process(tpe);
		if (tpe.isConsumed())
		{
			sb.append(tpe.getResult());
		}
		else
		{
			Logging.errorPrint(error);
		}
	}

	public List<CampaignSourceEntry> getFiles(Campaign c)
	{
		return c.getSafeListFor(listkey);
	}

	public String getLoadName()
	{
		return cdomClass.getSimpleName();
	}

}
