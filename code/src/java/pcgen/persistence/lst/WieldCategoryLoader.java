/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.persistence.lst;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.GameMode;
import pcgen.core.QualifiedObject;
import pcgen.core.character.WieldCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Deals with reading in and parsing WIELDCATEGORY tag 
 */
public class WieldCategoryLoader
{

	private final PreParserFactory prereqParser;

	/** Constructor */
	public WieldCategoryLoader()
	{
		try
		{
			prereqParser = PreParserFactory.getInstance();
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint("Error Initializing PreParserFactory");
			Logging.errorPrint("  " + ple.getMessage(), ple);
			throw new UnreachableError(ple);
		}
	}

	/**
	 * Parse the WIELDCATEGORY line.
	 *
	 * @param gameMode the game mode
	 * @param lstLine the lst line
	 * @param source the source
     */
	public void parseLine(GameMode gameMode, String lstLine, URI source) {
		LoadContext context = gameMode.getModeContext();

		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		WieldCategory cat = null;
		String preKey = null;
		CDOMSingleRef<WieldCategory> preVal = null;

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// TODO Deal with Exception
			}

			if (key.equals("WIELDCATEGORY"))
			{
				final String value = colString.substring(idxColon + 1).trim();
				cat = context.getReferenceContext().silentlyGetConstructedCDOMObject(WieldCategory.class, value);

				if (cat == null)
				{
					cat = new WieldCategory();
					cat.setName(value.intern());
					gameMode.addWieldCategory(cat);
				}
			}
			else if (colString.startsWith("PREVAR"))
			{
				if (ControlUtilities.hasControlToken(context, CControl.WIELDCAT))
				{
					Logging.errorPrint(
						"PREVAR is disabled when WIELDCAT control is used: "
							+ colString);
				}
				else
				{
					//TODO ensure preKey is null
					// a PREVARxx formula used to switch
					// weapon categories based on size
					preKey = colString;
				}
			}
			else if (key.equals("SWITCH"))
			{
				if (ControlUtilities.hasControlToken(context, CControl.WIELDCAT))
				{
					Logging.errorPrint(
						"SWITCH is disabled when WIELDCAT control is used: "
							+ key);
				}
				else
				{
					//TODO ensure preVal is null
					// If matches PRE, switch category to this
					preVal = context.getReferenceContext().getCDOMReference(WieldCategory.class, colString.substring(7));
				}
			}
			else
			{
				final String value = colString.substring(idxColon + 1).trim();
				if (context.processToken(cat, key, value))
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
		}
		//TODO Error checking if preVal w/o preKey, vice versa, etc.
		if ((cat != null) && (preVal != null) && (preKey != null))
		{
			try
			{
				QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(preVal);
				qo.addPrerequisite(prereqParser.parse(preKey));
				cat.addCategorySwitch(qo);
			}
			catch (PersistenceLayerException ple)
			{
				Logging
					.errorPrint("Error parsing Prerequisite in " + source + ": " + preKey + "\n  " + ple.getMessage());
			}
		}
	}
}
