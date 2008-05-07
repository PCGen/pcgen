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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.bonus;

import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj.StackType;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>Bonus</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class Bonus
{
	static final int BONUS_UNDEFINED = -1;

	private static int bonusTagMapNum = 0;

	private static final HashMap<String, bonusMapEntry> BONUS_TAG_MAP = new HashMap<String, bonusMapEntry>();

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
		bonusMapEntry bEntry = BONUS_TAG_MAP.get(bonusName);
		if (bEntry == null)
		{
			final int equalOffset = bonusName.indexOf('=');
			if (equalOffset >= 0)
			{
				bEntry = BONUS_TAG_MAP.get(bonusName.substring(0, equalOffset + 1));
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
		for ( String key : BONUS_TAG_MAP.keySet() )
		{
			final bonusMapEntry bme = BONUS_TAG_MAP.get(key);
			if (bme.getBonusType() == bonusType)
			{
				return key;
			}
		}
		return Constants.EMPTY_STRING;
	}

	/**
	 * Sorts a list of <tt>BonusObj</tt> objects so that dependant bonuses come
	 * after the bonuses they depend on.
	 * 
	 * @param listToSort The <tt>List</tt> of bonuses to sort.
	 * @return The sorted list.
	 */
	public static List<BonusObj> sortBonusList(List<BonusObj> listToSort)
	{
		final List<BonusObj> tempList = new ArrayList<BonusObj>();

		// 'BONUS:blah|blah|Foo' depends on
		// 'BONUS:VAR|Foo|MyGoo' which depends on
		// 'BONUS:VAR|MyGoo|2'

		// BONUS: type      | info           | value

		// BONUS:COMBAT     |TOHIT           |STR
		// BONUS:STAT       |STR             |rage
		// BONUS:VAR        |rage            |2

		for ( final BonusObj bonus : listToSort )
		{
			int iFound = 0;
			for (int ii = 0; ii < tempList.size(); ii++)
			{
				final BonusObj tempBonus = tempList.get(ii);
				if (tempBonus.getDependsOn(bonus.getBonusInfo()))
				{
					iFound = ii;
				}
			}
			tempList.add(iFound, bonus);
		}
		
		int iCount = tempList.size();
		for (int i = 0; i < iCount; )
		{
			final BonusObj bonus = tempList.get(i);
			//
			// Move to end of list
			//
			if (bonus.getDependsOn("JEPFORMULA")) //$NON-NLS-1$
			{
				tempList.remove(i);
				tempList.add(bonus);
				--iCount;
			}
			else
			{
				++i;
			}
		}

		listToSort = tempList;

		final ArrayList<BonusObj> tempList2 = new ArrayList<BonusObj>();

		// go through and move all the static bonuses to the front
		final int aSize = listToSort.size();
		for (int i = 0; i < aSize; i++)
		{
			final BonusObj bonus = listToSort.get(i);
			if (bonus.isValueStatic())
			{
				tempList2.add(0, bonus);
			}
			else
			{
				tempList2.add(bonus);
			}
		}
		return tempList2;
	}

	/**
	 * Create a new Bonus
	 * @param bonusString
	 * @return BonusObj
	 * 
	 * TODO - This is doing all manner of string parsing.  It really belongs in
	 * the persistence layer.
	 */
	public static BonusObj newBonus(final String bonusString)
	{
		final int typeOfBonus;
		int aLevel = -1;

		StringTokenizer aTok = new StringTokenizer(bonusString, Constants.PIPE);

		if ((bonusString.indexOf(Constants.PIPE) == bonusString
				.lastIndexOf(Constants.PIPE))
				&& bonusString.indexOf('%') < 0)
		{
			Logging.errorPrint("Illegal bonus format: " + bonusString);

			return null;
		}

		String bonusName = aTok.nextToken();

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
		else
		{
			bonusName = bonusName.toUpperCase();
		}


		int equalOffset = -1;
		bonusMapEntry bEntry = BONUS_TAG_MAP.get(bonusName);
		if (bEntry == null)
		{
			equalOffset = bonusName.indexOf('=');
			if (equalOffset >= 0)
			{
				bEntry = BONUS_TAG_MAP.get(bonusName.substring(0, equalOffset + 1));
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
			aBonus = (BonusObj) bEntry.getBonusClass().newInstance();
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

				if (PreParserFactory.isPreReqString(aString))
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
					String bonusType = aString.substring(5);
					int dotLoc = bonusType.indexOf('.');
					if ( dotLoc != -1 )
					{
						final String stackingFlag = bonusType.substring(dotLoc + 1);
						// TODO - Need to reset bonusType to exclude this but
						// there is too much dependancy on it being there
						// built into the code.
						if ( stackingFlag.startsWith("REPLACE") ) //$NON-NLS-1$
						{
							aBonus.setStackingFlag( StackType.REPLACE );
						}
						else if ( stackingFlag.startsWith("STACK") ) //$NON-NLS-1$
						{
							aBonus.setStackingFlag( StackType.STACK );
						}
					}
					final boolean result = aBonus.addType(bonusType);

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

	/**
	 * Add a CLASS via a BONUS
	 * @param bonusClass
	 * @param bonusName
	 * @return true if successful
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static boolean addBonusClass(Class bonusClass, String bonusName) throws InstantiationException, IllegalAccessException {
		boolean added = false;
		if (BonusObj.class.isAssignableFrom(bonusClass))
		{
			final BonusObj bonusObj = (BonusObj) bonusClass.newInstance();
			final String[] handled = bonusObj.getBonusesHandled();
			if (handled != null)
			{
				added = true;
				for (int i = 0; i < handled.length; ++i)
				{
					BONUS_TAG_MAP.put(handled[i], new bonusMapEntry(bonusName, bonusTagMapNum++, bonusClass));
				}
			}
		}
		return added;
	}

	private static class bonusMapEntry
	{
		private int bonusType = BONUS_UNDEFINED;
		private String bonusObjectName = "";
		private Class bonusClass;

		/**
		 * Constructor
		 * @param bonusObjectName
		 * @param bonusType
		 * @param bonusClass
		 */
		public bonusMapEntry(final String bonusObjectName, final int bonusType, final Class bonusClass)
		{
			this.bonusObjectName = bonusObjectName;
			this.bonusType = bonusType;
			this.bonusClass = bonusClass;
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

		/**
		 * Return the bonus class
		 * @return the bonus class
		 */
		public final Class getBonusClass()
		{
			return bonusClass;
		}

        /**
         * toString function bonusname:bonustype
         * @return String bonusname:bonustype
         */
		public String toString()
		{
			return bonusObjectName + ':' + Integer.toString(bonusType);
		}
	}

}
