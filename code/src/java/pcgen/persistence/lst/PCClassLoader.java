/*
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
 *
 */
package pcgen.persistence.lst;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.analysis.PCClassKeyChange;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.utils.DeferredLine;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class PCClassLoader extends LstObjectFileLoader<PCClass>
{
	@Override
	public PCClass parseLine(LoadContext context, PCClass target, String lstLine, SourceEntry source)
		throws PersistenceLayerException
	{
		if (lstLine.startsWith("SUBCLASS:") || lstLine.startsWith("SUBCLASSLEVEL:"))
		{
			if (target == null)
			{
				Logging.errorPrint("Ignoring line: " + lstLine + " as SUBCLASS* type line appeared before CLASS: line");
				return null;
			}
			SubClass subClass = null;

			if (lstLine.startsWith("SUBCLASS:"))
			{
				int tabLoc = lstLine.indexOf('\t');
				if (tabLoc == -1)
				{
					Logging.errorPrint("Expected SUBCLASS to have " + "additional Tags in " + source.getURI()
						+ " (e.g. COST is a required Tag in a SUBCLASS)");
				}
				final String n = lstLine.substring(9, tabLoc);
				String restOfLine = lstLine.substring(tabLoc);
				subClass = target.getSubClassKeyed(n);

				if (subClass == null)
				{
					subClass = new SubClass();
					subClass.setName(n);
					subClass.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
					subClass.setSourceURI(source.getURI());
					target.addSubClass(subClass);
				}
				parseLineIntoClass(context, subClass, source, restOfLine);
			}
			else
			{
				List<SubClass> subClassList = target.getListFor(ListKey.SUB_CLASS);
				if (subClassList != null)
				{
					subClass = subClassList.get(subClassList.size() - 1);
					subClass.addToListFor(ListKey.SUB_CLASS_LEVEL,
						new DeferredLine(source, lstLine.substring(14)));
				}
			}
			return target;
		}

		if (lstLine.startsWith("SUBSTITUTIONCLASS:") || lstLine.startsWith("SUBSTITUTIONLEVEL:"))
		{
			if (target == null)
			{
				Logging.errorPrint(
					"Ignoring line: " + lstLine + " as SUBSTITUTIONCLASS* type line appeared before CLASS: line");
				return null;
			}
			SubstitutionClass substitutionClass = null;

			if (lstLine.startsWith("SUBSTITUTIONCLASS:"))
			{
				int tabLoc = lstLine.indexOf('\t');
				String name;
				String restOfLine;
				if (tabLoc > 0)
				{
					name = lstLine.substring(18, tabLoc);
					restOfLine = lstLine.substring(tabLoc);
				}
				else
				{
					name = lstLine.substring(18);
					restOfLine = null;
				}
				substitutionClass = target.getSubstitutionClassKeyed(name);

				if (substitutionClass == null)
				{
					substitutionClass = new SubstitutionClass();
					substitutionClass.setName(name);
					substitutionClass.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
					substitutionClass.setSourceURI(source.getURI());
					target.addSubstitutionClass(substitutionClass);
				}
				parseLineIntoClass(context, substitutionClass, source, restOfLine);
			}
			else
			{
				if (lstLine.indexOf('\t') == -1)
				{
					Logging.errorPrint("Ignoring line: " + lstLine + " as SUBSTITUTIONLEVEL line was empty");
					return null;
				}
				List<SubstitutionClass> substitutionClassList = target.getListFor(ListKey.SUBSTITUTION_CLASS);
				if (substitutionClassList != null && !substitutionClassList.isEmpty() && lstLine.length() > 18)
				{
					substitutionClass = substitutionClassList.get(substitutionClassList.size() - 1);
					substitutionClass.addToListFor(ListKey.SUB_CLASS_LEVEL,
						new DeferredLine(source, lstLine.substring(18)));
				}
			}
			return target;
		}

		return parseClassLine(context, lstLine, source, target);
	}

	private PCClass parseClassLine(LoadContext context, String lstLine, SourceEntry source, PCClass pcClass)
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

			if (pcClass == null || !name.equals(pcClass.getKeyName()) && (!name.contains(".MOD")))
			{
				if (pcClass != null)
				{
					completeObject(context, source, pcClass);
				}
				pcClass = new PCClass();
				pcClass.setName(name);
				pcClass.setSourceURI(source.getURI());
				pcClass.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
				context.addStatefulInformation(pcClass);
				context.getReferenceContext().importObject(pcClass);
			}
			// need to grab PCClass instance for this .MOD minus the .MOD part of the name
			else if (name.endsWith(".MOD"))
			{
				pcClass = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
					name.substring(0, name.length() - 4));
			}
			parseLineIntoClass(context, pcClass, source, restOfLine);
		}
		else
		{
			parseFullClassLevelLine(context, source, pcClass, lineIdentifier, restOfLine);
		}
		return pcClass;
	}

	private void parseFullClassLevelLine(LoadContext context, SourceEntry source, PCClass pcClass,
		String lineIdentifier, String restOfLine) throws PersistenceLayerException
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
				Logging.errorPrint("Invalid Level Identifier: " + thisLevel + " for " + pcClass.getDisplayName()
					+ ". Value must be greater than zero");
			}
		}
		catch (NumberFormatException nfe)
		{
			// I think we can ignore this, as
			// it's supposed to be the level #
			// but could be almost anything else
			Logging.errorPrint("Expected a level value, but got '" + lineIdentifier + "' instead (as a level line in "
				+ (pcClass == null ? "no class" : pcClass.getKeyName()) + ") in source " + source.getURI());
			Logging.errorPrint("  Rest of line was: " + restOfLine);
		}
	}

	public void parseClassLevelLine(LoadContext context, PCClass pcClass, int lvl, SourceEntry source,
		String restOfLine) throws PersistenceLayerException
	{
		if (restOfLine == null)
		{
			return;
		}
		PCClassLevel classlevel = pcClass.getOriginalClassLevel(lvl);
		final StringTokenizer colToken = new StringTokenizer(restOfLine, SystemLoader.TAB_DELIM);

		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			String token = colToken.nextToken().trim();
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: '" + token + "' in Class "
					+ pcClass.getDisplayName() + " of " + source);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' in Class "
					+ pcClass.getDisplayName() + " of " + source);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token.substring(colonLoc + 1);
			if (context.processToken(classlevel, key, value))
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

	public void parseLineIntoClass(LoadContext context, PCClass pcClass, SourceEntry source, String restOfLine)
		throws PersistenceLayerException
	{
		if (restOfLine == null)
		{
			return;
		}
		final StringTokenizer colToken = new StringTokenizer(restOfLine, SystemLoader.TAB_DELIM);

		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			String token = colToken.nextToken().trim();
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: '" + token + "' in Class "
					+ pcClass.getDisplayName() + " of " + source);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' in Class "
					+ pcClass.getDisplayName() + " of " + source);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token.substring(colonLoc + 1);
			if (context.processToken(pcClass, key, value))
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

	private void parseRepeatClassLevel(LoadContext context, String restOfLine, SourceEntry source, PCClass pcClass,
		int iLevel, String colString) throws PersistenceLayerException
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
			maxLevel = pcClass.getSafe(IntegerKey.LEVEL_LIMIT);
		}
		if (tokenCount > 0)
		{
			try
			{
				lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Level Increment info '" + colString + "' in " + source.getURI(), nfe);
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
					Logging.errorPrint(
						"MAX= cannot be followed by another item in REPEATLEVEL.  SKIP= must appear before MAX=");
				}
				String maxString = tokenTwo.substring(4);
				try
				{
					maxLevel = Integer.parseInt(maxString);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("Non-Numeric Max Level info MAX='" + maxLevel + "' in " + source.getURI(), nfe);
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
					Logging.errorPrint("Non-Numeric Consecutive Level info '" + colString + "' in " + source.getURI(),
						nfe);
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
				Logging.errorPrint("Non-Numeric Max Level info '" + colString + "' in " + source.getURI(), nfe);
			}
		}

		int count = consecutive - 1; // first one already added by processing of lstLine, so skip it
		for (int lvl = iLevel + lvlIncrement; lvl <= maxLevel; lvl += lvlIncrement)
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

	@Override
	protected PCClass getObjectKeyed(LoadContext context, String aKey)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
			aKey.startsWith("CLASS:") ? aKey.substring(6) : aKey);
	}

	public void loadSubLines(LoadContext context)
	{
		Collection<PCClass> allClasses = context.getReferenceContext().getConstructedCDOMObjects(PCClass.class);
		for (PCClass cl : allClasses)
		{
			List<SubClass> subClasses = cl.getListFor(ListKey.SUB_CLASS);
			if (subClasses != null)
			{
				for (SubClass sc : subClasses)
				{
					sc.copyLevelsFrom(cl);
					processSubLevelLines(context, cl, sc);
				}
			}
			List<SubstitutionClass> substClasses = cl.getListFor(ListKey.SUBSTITUTION_CLASS);
			if (substClasses != null)
			{
				for (SubstitutionClass sc : substClasses)
				{
					processSubLevelLines(context, cl, sc);
				}
			}
		}
	}

	private void processSubLevelLines(LoadContext context, PCClass cl, PCClass sc)
	{
		for (DeferredLine dl : sc.getSafeListFor(ListKey.SUB_CLASS_LEVEL))
		{
			context.setSourceURI(dl.source.getURI());
			String lstLine = dl.lstLine;
			try
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
				parseFullClassLevelLine(context, dl.source, sc, lineIdentifier, restOfLine);
			}
			catch (PersistenceLayerException ple)
			{
				Logging.log(Logging.LST_ERROR, "Error parsing " + sc.getClass().getSimpleName() + " line: "
					+ cl.getKeyName() + " " + sc.getKeyName() + " " + lstLine, ple);
			}
		}
	}

	@Override
	public PCClass getCopy(LoadContext context, String baseName, String copyName, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		PCClass copy = super.getCopy(context, baseName, copyName, source);
		PCClassKeyChange.changeReferences(baseName, copy);
		return copy;
	}

}
