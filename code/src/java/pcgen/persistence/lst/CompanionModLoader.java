/*
 * Copyright 2012 (C) Tom Parker
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
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.list.CompanionList;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Loads the level based Mount and Familiar benefits
 *
 **/
public class CompanionModLoader extends SimpleLoader<CompanionMod>
{

	/**
	 * An incrementing value to ensure each CompanionMod always have a unique name (even
	 * if not referred to in the data).
	 */
	private static int COMPANION_MOD_ID = 1;

	public CompanionModLoader()
	{
		super(CompanionMod.class);
	}

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
    {
		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		List<String> allTokens = new ArrayList<>();

		//Intentionally a dummy object with no Category - shouldn't be usable
		CompanionMod loadable = new CompanionMod();

		boolean foundType = false;
		while (colToken.hasMoreTokens())
		{
			String token = colToken.nextToken();
			if (token.startsWith("TYPE:"))
			{
				if (foundType)
				{
					//Found twice
					Logging.errorPrint("Ignoring line: Found TYPE: twice: " + lstLine, context);
					return;
				}
				foundType = true;
				String clName = token.substring(5);
				loadable = buildCompanionMod(context, clName);
				loadable.setSourceURI(sourceURI);
				context.getReferenceContext().importObject(loadable);
			}
			allTokens.add(token);
		}

		for (String token : allTokens)
		{
			LstUtils.processToken(context, loadable, sourceURI, token);
		}
	}

	private CompanionMod buildCompanionMod(LoadContext context, String clName)
	{
		//Always create a new CompanionMod (no Copy Mod or Forget)
		//But we need to create a unique name (and do it with something that is unique-ish)
		//Note there is currently no risk of name conflict here since they cannot be uniquely named
		String uniqueName = "COMPANIONMOD_" + COMPANION_MOD_ID++;
		CompanionList cat = context.getReferenceContext().constructNowIfNecessary(CompanionList.class, clName);
		CompanionMod loadable = cat.newInstance();
		loadable.setDisplayName(uniqueName);
		return loadable;
	}
}
