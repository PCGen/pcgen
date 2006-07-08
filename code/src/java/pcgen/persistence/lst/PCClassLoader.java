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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.SubClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PCClassLoader extends LstObjectFileLoader
{
	/** Creates a new instance of PCClassLoader */
	public PCClassLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		PCClass pcClass = (PCClass) target;

		if (pcClass == null)
		{
			pcClass = new PCClass();
		}

		if (lstLine.startsWith("SUBCLASS:") || lstLine.startsWith("SUBCLASSLEVEL:"))
		{
			SubClass subClass = null;

			if (lstLine.startsWith("SUBCLASS:"))
			{
				final String n = lstLine.substring(9, lstLine.indexOf("\t"));
				subClass = pcClass.getSubClassKeyed(n);

				if (subClass == null)
				{
					subClass = new SubClass();
					subClass.setSourceCampaign(source.getCampaign());
					subClass.setSourceFile(source.getFile());
					pcClass.addSubClass(subClass);
				}
			}
			else
			{
				if ((pcClass.getSubClassList() != null) && !pcClass.getSubClassList().isEmpty())
				{
					subClass = pcClass.getSubClassList().get(pcClass.getSubClassList().size() - 1);
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
		return parseClassLine(target, lstLine, source, pcClass, false);
	}

	private PObject parseClassLine(PObject target, String lstLine, CampaignSourceEntry source, PCClass pcClass, boolean bRepeating)
		throws PersistenceLayerException
	{

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int iLevel = 0;
		boolean isNumber = true;

		String repeatTag = null;

		Map tokenMap = TokenStore.inst().getTokenMap(PCClassLstToken.class);
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
			catch(Exception e) {
				// TODO Handle Exception
			}
			PCClassLstToken token = (PCClassLstToken) tokenMap.get(key);

			if (colString.startsWith("CLASS:"))
			{
				isNumber = false;

				String name = colString.substring(6);

				if ((!name.equals(pcClass.getKeyName())) && (name.indexOf(".MOD") < 0))
				{
					finishObject(pcClass);
					pcClass = new PCClass();
					pcClass.setName(name);
					pcClass.setSourceFile(source.getFile());
					pcClass.setSourceCampaign(source.getCampaign());
				}
				// need to grab PCClass instance for this .MOD minus the .MOD part of the name
				else if (name.endsWith(".MOD"))
				{
					pcClass = Globals.getClassKeyed(name.substring(0, name.length()-4));
				}
			}
			else if (!(pcClass instanceof SubClass) && (isNumber))
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
						+ colString + "' instead in " + source.getFile(), nfe);
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
			else if (colString.startsWith("MULTIPREREQS"))
			{
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
					Logging.errorPrint("Error parsing ability " + pcClass.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTagLevel(pcClass, colString, iLevel))
			{
				continue;
			}
			else
			{
				if (!(pcClass instanceof SubClass))
				{
					Logging.errorPrint("Illegal class info tag '" + colString + "' in " + source.getFile());
				}
			}

			isNumber = false;
		}

		//
		// Process after all other tokens so 'order' is preserved
		//
		if ((repeatTag != null)  && (iLevel > 0))
		{
			parseRepeatClassLevel(target, lstLine, source, pcClass, iLevel, repeatTag);
		}
		return pcClass;
	}

	private void parseRepeatClassLevel(PObject target, String lstLine, CampaignSourceEntry source, PCClass pcClass, int iLevel, String colString)
		throws PersistenceLayerException
	{
		//
		// REPEAT:<level increment>|<consecutive>|<max level>
		//
		final StringTokenizer repeatToken = new StringTokenizer(colString, "|");
		final int tokenCount = repeatToken.countTokens();
		int lvlIncrement = 1000;					// an arbitrarily large number...
		int consecutive = 0;						// 0 means don't skip any
		int maxLevel = pcClass.getMaxLevel();
		if (tokenCount > 0)
		{
			try
			{
				lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Non-Numeric Level Increment info '" + colString + "' in " + source.getFile(), nfe);
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
				Logging.errorPrint("Non-Numeric Consecutive Level info '" + colString + "' in " + source.getFile(), nfe);
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
				Logging.errorPrint("Non-Numeric Max Level info '" + colString + "' in " + source.getFile(), nfe);
			}
		}

		final int tabIndex = lstLine.indexOf(SystemLoader.TAB_DELIM);
		int count = consecutive - 1;		// first one already added by processing of lstLine, so skip it
		for(int lvl = iLevel + lvlIncrement; lvl <= maxLevel; lvl += lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				parseClassLine(target, Integer.toString(lvl) + lstLine.substring(tabIndex), source, pcClass, true);
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
	protected PObject getObjectKeyed(String aKey)
	{
		if (aKey.startsWith("CLASS:"))
			aKey = aKey.substring(6);
		return Globals.getClassKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (target == null)
		{
			return;
		}
		if (includeObject(target))
		{
			// This class already exists, so lets
			// compare source files to see if its
			// a duplicate named entry
			final PCClass bClass = Globals.getClassKeyed(target.getKeyName());

			if (bClass == null)
			{
				Globals.getClassList().add((PCClass)target);
			}
			else
			{
				if (!bClass.getSourceFile().equals(target.getSourceFile()))
				{
					if (SettingsHandler.isAllowOverride())
					{
						if (target.getSourceDateValue() > bClass.getSourceDateValue())
						{
							Globals.getClassList().remove(bClass);
							Globals.getClassList().add((PCClass)target);
						}
					}
					else
					{
						// Duplicate loading error
						Logging.errorPrint("WARNING: Duplicate class name: " + target.getKeyName());
						Logging.errorPrint("Original : " + bClass.getSourceFile());
						Logging.errorPrint("Duplicate: " + target.getSourceFile());
						Logging.errorPrint("WARNING: Not loading duplicate");
					}
				}
			}

			List preReqList = target.getPreReqList();
			if (preReqList != null)
			{
				for (Iterator iter = preReqList.iterator(); iter.hasNext();)
				{
					Prerequisite preReq = (Prerequisite) iter.next();
					if ("VAR".equalsIgnoreCase(preReq.getKind()))
					{
						preReq.setSubKey("CLASS:" + target.getKeyName());
					}

				}
			}
		}
		else
		{
			excludedObjects.add(target.getKeyName());
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		Globals.getClassList().remove(objToForget);
	}

	public static String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer().append(aInt).append("|").append(colString).toString();
	}
}
