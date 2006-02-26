/*
 * Bonus.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.64 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 13:17:34 $
 *
 */
package pcgen.core.bonus;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarFile;

/**
 * <code>Bonus</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class Bonus
{
	static final int BONUS_UNDEFINED = -1;

	private static boolean objectListInitialized;

	private static final HashMap BONUS_TAG_MAP = new HashMap();

	private Bonus() {
		// Constructor
	}

	/**
	 * Get the bonus type given a name
	 * @param bonusName
	 * @return bonus type
	 */
	public static int getBonusTypeFromName(final String bonusName)
	{
		makeObjectList();

		bonusMapEntry bEntry = (bonusMapEntry) BONUS_TAG_MAP.get(bonusName);
		if (bEntry == null)
		{
			final int equalOffset = bonusName.indexOf('=');
			if (equalOffset >= 0)
			{
				bEntry = (bonusMapEntry) BONUS_TAG_MAP.get(bonusName.substring(0, equalOffset + 1));
			}
			if (bEntry == null)
			{
				return BONUS_UNDEFINED;
			}
		}
		return bEntry.getBonusType();
	}

	/**
	 * Get the bonus name given a type
	 * @param bonusType
	 * @return bonus name
	 */
	public static String getBonusNameFromType(final int bonusType)
	{
		for (Iterator e = BONUS_TAG_MAP.keySet().iterator(); e.hasNext();)
		{
			final String aKey = e.next().toString();

			final bonusMapEntry bme = (bonusMapEntry) BONUS_TAG_MAP.get(aKey);
			if (bme.getBonusType() == bonusType)
			{
				return aKey;
			}
		}
		return "";
	}

	/**
	 * Create a new Bonus
	 * @param bonusString
	 * @return BonusObj
	 */
	public static BonusObj newBonus(final String bonusString)
	{
		makeObjectList();

		final int typeOfBonus;
		int aLevel = -1;

		StringTokenizer aTok = new StringTokenizer(bonusString, "|");

		if (aTok.countTokens() < 3 && bonusString.indexOf("%") < 0)
		{
			Logging.errorPrint("Illegal bonus format: " + bonusString);

			return null;
		}

		String bonusName = aTok.nextToken().toUpperCase();

		try
		{
			aLevel = Integer.parseInt(bonusName);
		}
		catch (NumberFormatException exc)
		{
			// not an error, just means that this is not a Level
			// dependent bonus, so don't need to do anything
		}

		//
		// If level-dependent bonus, then next tag is the bonus name
		//
		if (aLevel >= 0)
		{
			bonusName = aTok.nextToken().toUpperCase();
		}


		int equalOffset = -1;
		bonusMapEntry bEntry = (bonusMapEntry) BONUS_TAG_MAP.get(bonusName);
		if (bEntry == null)
		{
			equalOffset = bonusName.indexOf('=');
			if (equalOffset >= 0)
			{
				bEntry = (bonusMapEntry) BONUS_TAG_MAP.get(bonusName.substring(0, equalOffset + 1));
			}
			if (bEntry == null)
			{
				Logging.errorPrint("Unrecognized bonus: " + bonusString);
				return null;
			}
		}

		typeOfBonus = bEntry.getBonusType();


		final String bonusInfo = aTok.nextToken().toUpperCase();
		String bValue = "0";

		if (aTok.hasMoreTokens())
		{
			bValue = aTok.nextToken().toUpperCase();
		}

		BonusObj aBonus = null;
		try
		{
			aBonus = (BonusObj) Class.forName("pcgen.core.bonus." + bEntry.getBonusObjectName()).newInstance();
		}
		catch (Exception exc)
		{
			// Do nothing
		}

		if (aBonus != null)
		{
			aBonus.setBonusName(bonusName);
			aBonus.setTypeOfBonus(typeOfBonus);
			aBonus.setValue(bValue);

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken().toUpperCase();

				if (aString.startsWith("!PRE") || aString.startsWith("PRE"))
				{
					//Logging.errorPrint("Why is this not parsed in loading: " + aString + " rather than in Bonus.newBonus()");

					try
					{
						final PreParserFactory factory = PreParserFactory.getInstance();
						aBonus.addPreReq( factory.parse(aString) );
					}
					catch ( PersistenceLayerException ple)
					{
						Logging.errorPrint(ple.getMessage(), ple);
					}
				}
				else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{
					final boolean result = aBonus.addType(aString.substring(5));

					if (!result)
					{
						Logging.debugPrint(new StringBuffer()
								.append("Could not add type ")
								.append(aString.substring(5))
								.append(" to bonusType ").append(typeOfBonus)
								.append(" in Bonus.newBonus").toString());
					}
				}
			}

			if (equalOffset >= 0)
			{
				aBonus.setVariable(bonusName.substring(equalOffset + 1));
			}

			aTok = new StringTokenizer(bonusInfo, ",");

			while (aTok.hasMoreTokens())
			{
				final String token = aTok.nextToken();
				final boolean result = aBonus.parseToken(token);

				if (!result)
				{
					Logging.debugPrint(new StringBuffer()
							.append("Could not parse token ").append(token)
							.append(" from bonusInfo ").append(bonusInfo)
							.append(" in BonusObj.newBonus.").toString());
				}
			}

			if (aLevel >= 0)
			{
				aBonus.setPCLevel(aLevel);
			}
		}
		else
		{
			Logging.errorPrint("Could not create bonusObj for:" + bonusString);
		}

		return aBonus;
	}

	private static String getJarName()
	{
		try
		{
			final StringBuffer buffer = new StringBuffer(
					System.getProperty("java.class.path", "pcgen.jar"));
			// If we aren't running from the pcgen.jar file, we still need to have it to scan
			if (buffer.indexOf("pcgen.jar") < 0)
			{
				buffer.append(System.getProperty("path.separator"))
						.append("pcgen.jar");
			}
			return buffer.toString();
		}
		catch (Exception exc)
		{
			// Do nothing
		}
		return null;
	}

	private static void makeObjectList()
	{
		if (!objectListInitialized)
		{
			objectListInitialized = true;			// for better or worse, we will only do this once

			final String jarNames = getJarName();
			if (jarNames == null)
			{
				Logging.errorPrint("jar name is null");
				return;
			}

			// Karianna - Much better fix suggested by Byngl
			final StringTokenizer jarTok = new StringTokenizer(jarNames, System.getProperty("path.separator"));
			while(jarTok.hasMoreTokens())
			{
				final String jarName = jarTok.nextToken();
				//
				// Must be a .jar file or ignore it
				//
				if (!jarName.toLowerCase().endsWith(".jar"))
				{
					continue;
				}

				boolean bAdded = false;
				try
				{
					final JarFile jarfile = new JarFile(jarName);
					int iCount = 0;
					for (Enumeration e = jarfile.entries() ; e.hasMoreElements() ;)
					{
						String jarEntry = e.nextElement().toString();
						if (jarEntry.startsWith("pcgen/core/bonus/") && jarEntry.endsWith(".class"))
						{
							jarEntry = jarEntry.substring(17);
							jarEntry = jarEntry.substring(0, jarEntry.length() - 6);

							try
							{
								final Class jarClass = Class.forName(
										new StringBuffer().append(Bonus.class
												.getPackage().getName())
												.append('.')
												.append(jarEntry).toString());

								if (BonusObj.class.isAssignableFrom(jarClass))
								{
									final BonusObj bonusObj = (BonusObj) jarClass.newInstance();
									final String[] handled = bonusObj.getBonusesHandled();
									if (handled != null)
									{
										bAdded = true;
										for (int i = 0; i < handled.length; ++i)
										{
											BONUS_TAG_MAP.put(handled[i], new bonusMapEntry(jarEntry, iCount++));
										}
									}
								}
							}
							catch (Exception exc)
							{
								// TODO Handle Exception
							}
						}
					}
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}

				//
				// Stop looking after we've found a file with the desired classes
				// XXX: do we really want to do this, or would it be better to allow multiple .jar files to define bonus entities?
				//
				if (bAdded)
				{
					break;
				}
			}
		}
	}

	private static class bonusMapEntry
	{
		private int bonusType = BONUS_UNDEFINED;
		private String bonusObjectName = "";

		/**
		 * Constructor
		 * @param argName
		 * @param argType
		 */
		public bonusMapEntry(final String argName, final int argType)
		{
			bonusObjectName = argName;
			bonusType = argType;
		}

		/**
		 * Get the bonus object name
		 * @return bonus object name
		 */
		public final String getBonusObjectName()
		{
			return bonusObjectName;
		}

		/**
		 * Get the bonus type
		 * @return bonus type
		 */
		public final int getBonusType()
		{
			return bonusType;
		}

		public String toString()
		{
			return bonusObjectName + ':' + Integer.toString(bonusType);
		}
	}

}
