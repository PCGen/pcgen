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
package pcgen.gui2.converter.loader;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.gui2.converter.Loader;
import pcgen.gui2.converter.TokenConverter;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.util.Logging;

public class BasicLoader<T extends CDOMObject> implements Loader
{

	public static final String FIELD_SEPARATOR = "\t"; //$NON-NLS-1$
	private final Class<T> cdomClass;
	private final ListKey<CampaignSourceEntry> listkey;
	private final EditorLoadContext context;
	private final Writer changeLogWriter;

	public BasicLoader(EditorLoadContext lc, Class<T> cl,
			ListKey<CampaignSourceEntry> lk, Writer changeLogWriter)
	{
		context = lc;
		cdomClass = cl;
		listkey = lk;
		this.changeLogWriter = changeLogWriter;
	}

	@Override
	public List<CDOMObject> process(StringBuilder sb, int line,
			String lineString, ConversionDecider decider)
			throws PersistenceLayerException, InterruptedException
	{
		String[] tokens = lineString.split(FIELD_SEPARATOR);
		if (tokens.length == 0)
		{
			return null;
		}
		String objectName = tokens[0];
		sb.append(objectName);
		List<CDOMObject> list = new ArrayList<>();
		for (int tok = 1; tok < tokens.length; tok++)
		{
			String token = tokens[tok];
			sb.append(FIELD_SEPARATOR);
			if (token.isEmpty())
			{
				continue;
			}

			T obj = context.getReferenceContext().constructCDOMObject(cdomClass, line + "Test"
					+ tok + " " + token);
			obj.put(StringKey.CONVERT_NAME, tokens[0]);
			List<CDOMObject> injected = processToken(sb, objectName, obj,
					token, decider, line);
			if (injected != null)
			{
				list.addAll(injected);
			}
			context.purge(obj);
			TokenConverter.clearConstants();
		}
		return list;
	}

	private List<CDOMObject> processToken(StringBuilder sb, String objectName,
			CDOMObject obj, String token, ConversionDecider decider, int line)
			throws PersistenceLayerException, InterruptedException
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: "
					+ token);
			return null;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: " + token);
			return null;
		}

		String key = token.substring(0, colonLoc);
		String value = (colonLoc == token.length() - 1) ? null : token
				.substring(colonLoc + 1);
		TokenProcessEvent tpe = new TokenProcessEvent(context, decider, key,
				value, objectName, obj);
		String error = TokenConverter.process(tpe);
		if (tpe.isConsumed())
		{
			if (!token.equals(tpe.getResult()))
			{
				try
				{
					changeLogWriter.append("Line " + line + " converted '"+token+"' to '" + tpe.getResult() +"'.\n");
				}
				catch (IOException e)
				{
					Logging.errorPrint("Unable to log change", e);
				}
			}
			sb.append(tpe.getResult());
		}
		else
		{
			Logging.errorPrint(error);
		}
		return tpe.getInjected();
	}

	@Override
	public List<CampaignSourceEntry> getFiles(Campaign c)
	{
		return c.getSafeListFor(listkey);
	}

	public String getLoadName()
	{
		return cdomClass.getSimpleName();
	}

}
