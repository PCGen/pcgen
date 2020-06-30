/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Kit;
import pcgen.core.kit.BaseKit;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class CDOMKitLoader
{
	private final Map<String, CDOMSubLineLoader<? extends BaseKit>> loadMap = new HashMap<>();

	private final Class<Kit> targetClass = Kit.class;

	public void addLineLoader(CDOMSubLineLoader<? extends BaseKit> loader)
	{
		// TODO check null
		// TODO check duplicate!
		loadMap.put(loader.getPrefix(), loader);
	}

	public boolean parseSubLine(LoadContext context, Kit obj, String val, URI source)
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
		CDOMSubLineLoader<? extends BaseKit> loader = loadMap.get(prefix);
		if (loader == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Unsure what to do with line with prefix: " + prefix + ".  Line was: " + val + " in file: " + source);
			return false;
		}
		return subParse(context, obj, loader, val);
	}

	private <CC extends BaseKit> boolean subParse(LoadContext context, Kit kit, CDOMSubLineLoader<CC> loader,
		String line)
    {
		CC obj = loader.getCDOMObject();
		context.getObjectContext().addToList(kit, ListKey.KIT_TASKS, obj);
		return loader.parseLine(context, obj, line);
	}

	protected Kit getCDOMObject(LoadContext context, String name)
	{
		Kit obj = context.getReferenceContext().silentlyGetConstructedCDOMObject(targetClass, name);
		if (obj == null)
		{
			obj = context.getReferenceContext().constructCDOMObject(targetClass, name);
		}
		return obj;
	}

	public Class<Kit> getTargetClass()
	{
		return targetClass;
	}
}
