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

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
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
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public PCClass parseLine(PCClass target, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
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
				SubClassLoader.parseLine(subClass, lstLine, source);
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
					&& !pcClass.getSubstitutionClassList().isEmpty())
				{
					substitutionClass =
							(SubstitutionClass) pcClass
								.getSubstitutionClassList()
								.get(
									pcClass.getSubstitutionClassList().size() - 1);
					substitutionClass.addToLevelArray(lstLine.substring(18));

					return pcClass;
				}
			}

			if (substitutionClass != null)
			{
				SubstitutionClassLoader.parseLine(substitutionClass, lstLine,
					source);
			}

			return pcClass;
		}

		return parseClassLine(lstLine, source, pcClass, false);
	}

	private PCClass parseClassLine(String lstLine, CampaignSourceEntry source,
		PCClass pcClass, boolean bRepeating) throws PersistenceLayerException
	{

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int iLevel = 0;
		boolean isNumber = true;

		String repeatTag = null;

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCClassLstToken.class);
		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			PCClassLstToken token = (PCClassLstToken) tokenMap.get(key);

			if (colString.startsWith("CLASS:"))
			{
				isNumber = false;

				String name = colString.substring(6);

				if ((!name.equals(pcClass.getKeyName()))
					&& (name.indexOf(".MOD") < 0))
				{
					// TODO - This should never happen
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
			}
			else if (!(pcClass instanceof SubClass)
				&& !(pcClass instanceof SubstitutionClass) && (isNumber))
			{
				try
				{
					iLevel = Integer.parseInt(colString);
				}
				catch (NumberFormatException nfe)
				{
					// I think we can ignore this, as
					// it's supposed to be the level #
					// but could be almost anything else
					Logging.errorPrint("Expected a level value, but got '"
						+ colString + "' instead in " + source.getURI(), nfe);
				}

				isNumber = false;

				continue;
			}
			else if (colString.startsWith("CHECK"))
			{
				continue;
			}
			else if (colString.equals("HASSUBCLASS"))
			{
				pcClass.setHasSubClass(true);
			}
			else if (colString.equals("HASSUBSTITUTIONLEVEL"))
			{
				pcClass.setHasSubstitutionClass(true);
			}
			else if (colString.startsWith("MULTIPREREQS"))
			{
				//Deprecated in 5.11 Alpha cycle - thpr 12/7/06
				Logging
					.errorPrint("In: "
						+ pcClass.getDisplayName()
						+ ':'
						+ source.getURI()
						+ ':'
						+ colString
						+ ", The MULTIPREREQS tag has been deprecated.  "
						+ "Use PREMULT with !PRECLASS:1,Any instead. "
						+ "(e.g. PREMULT:1,[PRECLASS:1,Noble=1],[!PRECLASS:1,Any] )");
				pcClass.setMultiPreReqs(true);
			}
			else if (colString.startsWith("REPEATLEVEL:"))
			{
				if (!bRepeating)
				{
					repeatTag = colString.substring(12);
				}
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, pcClass, value);
				if (!token.parse(pcClass, value, iLevel))
				{
					Logging.errorPrint("Error parsing ability "
						+ pcClass.getDisplayName() + ':' + source.getURI()
						+ ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTagLevel(pcClass, colString, iLevel))
			{
				continue;
			}
			else
			{
				if (!(pcClass instanceof SubClass)
					&& !(pcClass instanceof SubstitutionClass))
				{
					Logging.errorPrint("Illegal class info tag '" + colString
						+ "' in " + source.getURI());
				}
			}

			isNumber = false;
		}

		//
		// Process after all other tokens so 'order' is preserved
		//
		if ((repeatTag != null) && (iLevel > 0))
		{
			parseRepeatClassLevel(lstLine, source, pcClass, iLevel, repeatTag);
		}
		return pcClass;
	}

	private void parseRepeatClassLevel(String lstLine,
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
		int maxLevel = pcClass.getMaxLevel();
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
		if (tokenCount > 1)
		{
			try
			{
				consecutive = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Consecutive Level info '"
					+ colString + "' in " + source.getURI(), nfe);
			}
		}
		if (tokenCount > 2)
		{
			try
			{
				maxLevel = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Max Level info '" + colString
					+ "' in " + source.getURI(), nfe);
			}
		}

		final int tabIndex = lstLine.indexOf(SystemLoader.TAB_DELIM);
		int count = consecutive - 1; // first one already added by processing of lstLine, so skip it
		for (int lvl = iLevel + lvlIncrement; lvl <= maxLevel; lvl +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				parseClassLine(Integer.toString(lvl)
					+ lstLine.substring(tabIndex), source, pcClass, true);
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
