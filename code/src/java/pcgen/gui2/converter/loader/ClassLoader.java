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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.gui2.converter.Loader;
import pcgen.gui2.converter.TokenConverter;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.util.Logging;

public class ClassLoader implements Loader
{
	public static final String FIELD_SEPARATOR = "\t"; //$NON-NLS-1$
	private final EditorLoadContext context;

	public ClassLoader(EditorLoadContext lc)
	{
		context = lc;
	}

	@Override
	public List<CDOMObject> process(StringBuilder sb, int line,
			String lineString, ConversionDecider decider)
			throws PersistenceLayerException, InterruptedException
	{
		String[] tokens = lineString.split(FIELD_SEPARATOR);
		String firstToken = tokens[0];
		sb.append(firstToken);

		Class<? extends CDOMObject> buildClass;
		Class<? extends CDOMObject> buildParent = null;
		if (firstToken.startsWith("SUBCLASS:"))
		{
			buildClass = SubClass.class;
		}
		else if (firstToken.startsWith("SUBCLASSLEVEL:"))
		{
			buildClass = PCClassLevel.class;
			buildParent = SubClass.class;
		}
		else if (firstToken.startsWith("SUBSTITUTIONCLASS:"))
		{
			buildClass = SubstitutionClass.class;
		}
		else if (firstToken.startsWith("SUBSTITUTIONLEVEL:"))
		{
			buildClass = PCClassLevel.class;
			buildParent = SubstitutionClass.class;
		}
		else if (firstToken.startsWith("CLASS:"))
		{
			buildClass = PCClass.class;
		}
		else
		{
			buildClass = PCClassLevel.class;
			buildParent = PCClass.class;
		}
		List<CDOMObject> list = new ArrayList<CDOMObject>();
		for (int tok = 1; tok < tokens.length; tok++)
		{
			String token = tokens[tok];
			sb.append(FIELD_SEPARATOR);
			if (token.length() == 0)
			{
				continue;
			}

			CDOMObject obj = context.ref.constructCDOMObject(buildClass, line
					+ "Test" + tok);
			CDOMObject parent = null;
			if (obj instanceof PCClassLevel)
			{
				obj.put(IntegerKey.LEVEL, 1);
				parent = context.ref.constructCDOMObject(buildParent, line
						+ "Test" + tok);
				obj.put(ObjectKey.TOKEN_PARENT, parent);
			}
			List<CDOMObject> injected = processToken(sb, firstToken, obj, parent, token,
					decider);
			if (injected != null)
			{
				list.addAll(injected);
			}
			context.purge(obj);
			if (parent != null)
			{
				context.purge(parent);
			}
			TokenConverter.clearConstants();
		}
		return list;
	}

	private List<CDOMObject> processToken(StringBuilder sb, String firstToken, CDOMObject obj,
			CDOMObject alt, String token, ConversionDecider decider)
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
				value, firstToken, obj);
		String error = TokenConverter.process(tpe);
		if (!tpe.isConsumed() && alt != null)
		{
			tpe = new TokenProcessEvent(context, decider, key, value, firstToken, alt);
			error += TokenConverter.process(tpe);
		}
		if (tpe.isConsumed())
		{
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
		return c.getSafeListFor(ListKey.FILE_CLASS);
	}

	public String getLoadName()
	{
		return "Class";
	}

}
