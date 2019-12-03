/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.DefaultVarValue;
import pcgen.cdom.content.UserFunction;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.cdom.inst.DynamicCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * A CDOMControlLoader is a loader that processes the Data Definitions
 * (initially FACT/FACTSET)
 */
public class CDOMControlLoader extends LstLineFileLoader
{
	private final Map<String, CDOMSubLineLoader<?>> loadMap = new HashMap<>();

	public CDOMControlLoader()
	{
		//CONSIDER better way to load these?
		addLineLoader(new CDOMSubLineLoader<>("FACTDEF", FactDefinition.class));
		addLineLoader(new CDOMSubLineLoader<>("FACTSETDEF", FactSetDefinition.class));
		addLineLoader(new CDOMSubLineLoader<>("DEFAULTVARIABLEVALUE", DefaultVarValue.class));
		addLineLoader(new CDOMSubLineLoader<>("FUNCTION", UserFunction.class));
		addLineLoader(new CDOMSubLineLoader<>("DYNAMICSCOPE", DynamicCategory.class));
	}

	private void addLineLoader(CDOMSubLineLoader<?> loader)
	{
		Objects.requireNonNull(loader, "Cannot add null loader to Control Loader");
		String prefix = loader.getPrefix();
		if (loadMap.containsKey(prefix))
		{
			throw new IllegalArgumentException("Cannot add a second loader for prefix: " + prefix);
		}
		loadMap.put(loader.getPrefix(), loader);
	}

	private boolean parseSubLine(LoadContext context, String val, URI source)
	{
		int sepLoc = val.indexOf('\t');
		String firstToken = (sepLoc == -1) ? val : val.substring(0, sepLoc);
		int colonLoc = firstToken.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Unsure what to do with line without " + "a colon in first token: " + val + " in file: " + source);
			return false;
		}

		String prefix = firstToken.substring(0, colonLoc);
		CDOMSubLineLoader<?> loader = loadMap.get(prefix);
		if (loader == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Unsure what to do with line with prefix: " + prefix + ".  Line was: " + val + " in file: " + source);
			return false;
		}
		try
		{
			if (!subParse(context, loader, val))
			{
				return false;
			}
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint("Exception in Load: ", ple);
			return false;
		}
		return true;
	}

	private <CC extends Loadable> boolean subParse(LoadContext context, CDOMSubLineLoader<CC> loader, String line)
		throws PersistenceLayerException
	{
		int tabLoc = line.indexOf(SystemLoader.TAB_DELIM);
		String lineIdentifier;
		if (tabLoc == -1)
		{
			lineIdentifier = line;
		}
		else
		{
			lineIdentifier = line.substring(0, tabLoc);
		}

		int colonLoc = lineIdentifier.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("First token on line had no colon: " + line, context);
			return false;
		}
		String name = lineIdentifier.substring(colonLoc + 1);
		if ((name == null) || name.isEmpty())
		{
			Logging.errorPrint("First token on line had no content: " + line, context);
			return false;
		}

		AbstractReferenceContext refContext = context.getReferenceContext();
		CC obj = refContext.constructNowIfNecessary(loader.getLoadedClass(), name.replace('|', ' ').replace(',', ' '));
		return loader.parseLine(context, obj, line);
	}

	@Override
	public void parseLine(LoadContext context, String inputLine, URI sourceURI) {
		context.rollback();
		if (parseSubLine(context, inputLine, sourceURI))
		{
			Logging.clearParseMessages();
			context.commit();
		}
		else
		{
			context.rollback();
			Logging.replayParsedMessages();
			Logging.clearParseMessages();
		}
	}
}
