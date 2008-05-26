/*
 * PCClassLoader.java
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
 *
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PCClassLoader extends LstObjectFileLoader<PCClass>
{
	/** Creates a new instance of PCClassLoader */
	public PCClassLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public PCClass parseLine(LoadContext context, PCClass target,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		PCClass pcClass = target;

		/*
		 * FIXME TODO This should probably be done AFTER SUB*CLASS string checking,
		 * as a null PCClass with SUB* items is meaningless... and an error that should
		 * be flagged to the user - thpr 1/10/07
		 */
		if (pcClass == null)
		{
			pcClass = new PCClass();
		}

		if (lstLine.startsWith("SUBCLASS:")
			|| lstLine.startsWith("SUBCLASSLEVEL:"))
		{
			SubClass subClass = null;

			if (lstLine.startsWith("SUBCLASS:"))
			{
				if (lstLine.indexOf("\t") == -1)
				{
					Logging.errorPrint("Expected SUBCLASS to have "
						+ "additional Tags in " + source.getURI()
						+ " (e.g. COST is a required Tag in a SUBCLASS)");
				}
				final String n = lstLine.substring(9, lstLine.indexOf("\t"));
				subClass = pcClass.getSubClassKeyed(n);

				if (subClass == null)
				{
					subClass = new SubClass();
					subClass.setSourceCampaign(source.getCampaign());
					subClass.setSourceURI(source.getURI());
					pcClass.addSubClass(subClass);
				}
			}
			else
			{
				if ((pcClass.getSubClassList() != null)
					&& !pcClass.getSubClassList().isEmpty())
				{
					subClass =
							pcClass.getSubClassList().get(
								pcClass.getSubClassList().size() - 1);
					subClass.addToLevelArray(lstLine.substring(14));

					return pcClass;
				}
			}

			if (subClass != null)
			{
				SubClassLoader.parseLine(context, subClass, lstLine, source);
			}

			return pcClass;
		}

		if (lstLine.startsWith("SUBSTITUTIONCLASS:")
			|| lstLine.startsWith("SUBSTITUTIONLEVEL:"))
		{
			SubstitutionClass substitutionClass = null;

			if (lstLine.startsWith("SUBSTITUTIONCLASS:"))
			{
				if (lstLine.indexOf("\t") > 0)
				{
					substitutionClass =
							pcClass.getSubstitutionClassKeyed(lstLine
								.substring(18, lstLine.indexOf("\t")));
				}
				else
				{
					substitutionClass =
							pcClass.getSubstitutionClassKeyed(lstLine
								.substring(18));
				}

				if (substitutionClass == null)
				{
					substitutionClass = new SubstitutionClass();
					substitutionClass.setSourceCampaign(source.getCampaign());
					substitutionClass.setSourceURI(source.getURI());
					pcClass.addSubstitutionClass(substitutionClass);
				}
			}
			else
			{
				if ((pcClass.getSubstitutionClassList() != null)
					&& !pcClass.getSubstitutionClassList().isEmpty()
					&& lstLine.length() > 18)
				{
					substitutionClass = pcClass.getSubstitutionClassList().get(
							pcClass.getSubstitutionClassList().size() - 1);
					substitutionClass.addToLevelArray(lstLine.substring(18));

					return pcClass;
				}
			}

			if (substitutionClass != null)
			{
				SubstitutionClassLoader.parseLine(context, substitutionClass, lstLine,
					source);
			}

			return pcClass;
		}

		return parseClassLine(context, lstLine, source, pcClass);
	}

	private PCClass parseClassLine(LoadContext context, String lstLine,
			CampaignSourceEntry source, PCClass pcClass)
			throws PersistenceLayerException
	{
		int tabLoc = lstLine.indexOf(SystemLoader.TAB_DELIM);
		String lineIdentifier;
		String restOfLine;
		if (tabLoc == -1)
		{
			lineIdentifier = lstLine;
			restOfLine = null;
		}
		else
		{
			lineIdentifier = lstLine.substring(0, tabLoc);
			restOfLine = lstLine.substring(tabLoc + 1);
		}
		
		if (lineIdentifier.startsWith("CLASS:"))
		{
			String name = lineIdentifier.substring(6);

			if (!name.equals(pcClass.getKeyName())
					&& (name.indexOf(".MOD") < 0))
			{
				completeObject(source, pcClass);
				pcClass = new PCClass();
				pcClass.setName(name);
				pcClass.setSourceURI(source.getURI());
				pcClass.setSourceCampaign(source.getCampaign());
			}
			// need to grab PCClass instance for this .MOD minus the .MOD part of the name
			else if (name.endsWith(".MOD"))
			{
				pcClass =
						Globals.getClassKeyed(name.substring(0, name
							.length() - 4));
			}
			parseLineIntoClass(context, pcClass, source, restOfLine);
		}
		else 
		{
			try
			{
				String repeatTag = null;
				String thisLevel;
				int rlLoc = lineIdentifier.indexOf(":REPEATLEVEL:");
				if (rlLoc == -1)
				{
					thisLevel = lineIdentifier;
				}
				else
				{
					thisLevel = lineIdentifier.substring(0, rlLoc);
					repeatTag = lineIdentifier.substring(rlLoc + 13);
				}
				int iLevel = Integer.parseInt(thisLevel);
				if (iLevel == 0)
				{
					/*
					 * This is for backwards compatibility with PCGen 5.14
					 * getPCCText which writes out things to level 0 :P
					 */
					parseLineIntoClass(context, pcClass, source, restOfLine);
				}
				else if (iLevel > 0)
				{
					parseClassLevelLine(context, pcClass, iLevel, source, restOfLine);
					if (repeatTag != null)
					{
						parseRepeatClassLevel(context, restOfLine, source, pcClass, iLevel, repeatTag);
					}
				}
				else
				{
					Logging.errorPrint("Invalid Level Identifier: " + thisLevel
							+ " for " + pcClass.getDisplayName()
							+ ". Value must be greater than zero");
				}
			}
			catch (NumberFormatException nfe)
			{
				// I think we can ignore this, as
				// it's supposed to be the level #
				// but could be almost anything else
				Logging.errorPrint("Expected a level value, but got '"
					+ lineIdentifier + "' instead in " + source.getURI(), nfe);
			}

		}
		return pcClass;
	}

	public void parseClassLevelLine(LoadContext context, PCClass pcClass,
			int lvl, CampaignSourceEntry source,
			String restOfLine) throws PersistenceLayerException
	{
		if (restOfLine == null)
		{
			return;
		}
		PCClassLevel classlevel = pcClass.getClassLevel(lvl);
		final StringTokenizer colToken = new StringTokenizer(restOfLine,
				SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				PCClassLstToken.class);
		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			String token = colToken.nextToken().trim();
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(classlevel, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				PCClassLstToken tok = (PCClassLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, pcClass, value);
				if (!tok.parse(pcClass, value, lvl))
				{
					Logging.errorPrint("Error parsing class "
							+ pcClass.getDisplayName() + ':' + source.getURI()
							+ ':' + token + "\"");
				}
				Logging.clearParseMessages();
				continue;
			}
			else if (PObjectLoader.parseTagLevel(pcClass, token, lvl))
			{
				Logging.clearParseMessages();
				continue;
			}
			else
			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
			}
		}
	}

	public void parseLineIntoClass(LoadContext context, PCClass pcClass,
			CampaignSourceEntry source, String restOfLine) throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(restOfLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCClassLstToken.class);
		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			String token = colToken.nextToken().trim();
			if (token.startsWith("CHECK"))
			{
				Logging.deprecationPrint("Class " + pcClass.getDisplayName()
						+ " has token " + token + " which is ignored");
				continue;
			}
			else if (token.equals("HASSUBCLASS"))
			{
				Logging.deprecationPrint("Class " + pcClass.getDisplayName()
						+ " has token HASSUBCLASS which should be "
						+ "changed to HASSUBCLASS:YES");
				pcClass.setHasSubClass(true);
			}
			else if (token.equals("HASSUBSTITUTIONLEVEL"))
			{
				Logging.deprecationPrint("Class " + pcClass.getDisplayName()
						+ " has token HASSUBSTITUTIONLEVEL which should be "
						+ "changed to HASSUBSTITUTIONLEVEL:YES");
				pcClass.setHasSubstitutionClass(true);
			}
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
 			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
 			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(pcClass, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				PCClassLstToken tok = (PCClassLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, pcClass, value);
				if (!tok.parse(pcClass, value, 0))
				{
					Logging.errorPrint("Error parsing class "
						+ pcClass.getDisplayName() + ':' + source.getURI() + ':'
						+ token + "\"");
				}
				Logging.clearParseMessages();
 				continue;
			}
			else if (PObjectLoader.parseTagLevel(pcClass, token, 0))
 			{
				Logging.clearParseMessages();
 				continue;
 			}
 			else
 			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
 			}
		}
	}

	private void parseRepeatClassLevel(LoadContext context, String restOfLine,
		CampaignSourceEntry source, PCClass pcClass, int iLevel,
		String colString) throws PersistenceLayerException
	{
		//
		// REPEAT:<level increment>|<consecutive>|<max level>
		//
		final StringTokenizer repeatToken = new StringTokenizer(colString, "|");
		final int tokenCount = repeatToken.countTokens();
		int lvlIncrement = 1000; // an arbitrarily large number...
		int consecutive = 0; // 0 means don't skip any
		int maxLevel = 100; // an arbitrarily large number...
		if (pcClass.hasMaxLevel())
		{
			maxLevel = pcClass.getMaxLevel();
		}
		if (tokenCount > 0)
		{
			try
			{
				lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Level Increment info '"
					+ colString + "' in " + source.getURI(), nfe);
			}
		}
		boolean oldSyntax = false;
		if (tokenCount > 1)
		{
			boolean consumed = false;
			String tokenTwo = repeatToken.nextToken();
			if (tokenTwo.startsWith("SKIP="))
			{
				tokenTwo = tokenTwo.substring(5);
			}
			else if (tokenTwo.startsWith("MAX="))
			{
				if (tokenCount > 2)
				{
					Logging.errorPrint("MAX= cannot be followed by another item in REPEATLEVEL.  SKIP= must appear before MAX=");
				}
				String maxString = tokenTwo.substring(4);
				try
				{
					maxLevel = Integer.parseInt(maxString);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Non-Numeric Max Level info MAX='" + maxLevel
						+ "' in " + source.getURI(), nfe);
				}
				consumed = true;
			}
			else
			{
				oldSyntax = true;
			}
			if (!consumed)
			{
				try
				{
					consecutive = Integer.parseInt(tokenTwo);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Non-Numeric Consecutive Level info '"
						+ colString + "' in " + source.getURI(), nfe);
				}
			}
		}
		if (tokenCount > 2)
		{
			String tokenThree = repeatToken.nextToken();
			String maxString;
			if (!oldSyntax && tokenThree.startsWith("MAX="))
			{
				maxString = tokenThree.substring(4);
			}
			else
			{
				maxString = tokenThree;
			}
			try
			{
				maxLevel = Integer.parseInt(maxString);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Max Level info '" + colString
					+ "' in " + source.getURI(), nfe);
			}
		}

		int count = consecutive - 1; // first one already added by processing of lstLine, so skip it
		for (int lvl = iLevel + lvlIncrement; lvl <= maxLevel; lvl +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				parseClassLevelLine(context, pcClass, lvl, source, restOfLine);
			}
			if (consecutive != 0)
			{
				if (count == 0)
				{
					count = consecutive;
				}
				else
				{
					--count;
				}
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	@Override
	protected PCClass getObjectKeyed(String aKey)
	{
		return Globals.getClassKeyed(aKey.startsWith("CLASS:") ? aKey
			.substring(6) : aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	@Override
	protected void finishObject(final PObject target)
	{
		final List<Prerequisite> preReqList = target.getPreReqList();
		if (preReqList != null)
		{
			for (Prerequisite preReq : preReqList)
			{
				if ("VAR".equalsIgnoreCase(preReq.getKind()))
				{
					preReq.setSubKey("CLASS:" + target.getKeyName());
				}

			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final PCClass objToForget)
	{
		Globals.getClassList().remove(objToForget);
	}

	public static String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer().append(aInt).append("|").append(colString)
			.toString();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		// TODO - Create Globals.addClass( final PCClass aClass )
		Globals.getClassList().add((PCClass) pObj);
	}
}
