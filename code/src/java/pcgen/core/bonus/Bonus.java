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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj.StackType;
import pcgen.core.utils.ParsingSeparator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;

/**
 * <code>Bonus</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class Bonus
{
	static final String BONUS_UNDEFINED = "*UNDEFINED";

	private Bonus() {
		// Constructor
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
	 * @param context TODO
	 * @param bonusString
	 * @return BonusObj
	 * 
	 * TODO - This is doing all manner of string parsing.  It really belongs in
	 * the persistence layer.
	 */
	public static BonusObj newBonus(LoadContext context, final String bonusString)
	{
		ParsingSeparator sep = new ParsingSeparator(bonusString, '|');

		if ((bonusString.indexOf(Constants.PIPE) == bonusString
				.lastIndexOf(Constants.PIPE))
				&& bonusString.indexOf('%') < 0)
		{
			Logging.errorPrint("Illegal bonus format: " + bonusString);

			return null;
		}

		String bonusName = sep.next();

		try
		{
			//Throw away old level value if present
			Integer.parseInt(bonusName);
			bonusName = sep.next().toUpperCase();
		}
		catch (NumberFormatException exc)
		{
			bonusName = bonusName.toUpperCase();
		}

		int equalOffset = -1;
		Class<? extends BonusObj> bEntry = TokenLibrary.getBonus(bonusName);
		String typeOfBonus = bonusName;
		if (bEntry == null)
		{
			equalOffset = bonusName.indexOf('=');
			if (equalOffset >= 0)
			{
				typeOfBonus = bonusName.substring(0, equalOffset + 1);
				bEntry = TokenLibrary.getBonus(typeOfBonus);
			}
			if (bEntry == null)
			{
				typeOfBonus = Bonus.BONUS_UNDEFINED;
				Logging.errorPrint("Unrecognized bonus: " + bonusString);
				return null;
			}
		}

		final String bonusInfo = sep.next().toUpperCase();
		String bValue = "0";

		if (sep.hasNext())
		{
			bValue = sep.next();
		}
		
		if (bValue.startsWith("PRE") || bValue.startsWith("!PRE"))
		{
			Logging.errorPrint("Invalid BONUS has no value: " + bonusString);
			return null;
		}

		bValue = bValue.toUpperCase();
		
		BonusObj aBonus = null;
		try
		{
			aBonus = bEntry.newInstance();
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Could not create bonusObj for:" + bonusString);
			return null;
		}

		aBonus.putOriginalString(bonusString);
		aBonus.setBonusName(bonusName);
		aBonus.setTypeOfBonus(typeOfBonus);
		Formula val = aBonus.setValue(bValue);
		if (!val.isValid())
		{
			Logging.errorPrint("Could not create bonusObj for:" + bonusString
					+ " since Formula " + bValue + " is not valid: " + val.toString());
			return null;
		}

		while (sep.hasNext())
		{
			final String aString = sep.next().toUpperCase();

			if (PreParserFactory.isPreReqString(aString))
			{
				// Logging.errorPrint("Why is this not parsed in loading: " +
				// aString + " rather than in Bonus.newBonus()");

				try
				{
					final PreParserFactory factory = PreParserFactory
							.getInstance();
					aBonus.addPrerequisite(factory.parse(aString));
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
					Logging.reportSource(Logging.ERROR, context);
				}
			}
			else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				String bonusType = aString.substring(5);
				int dotLoc = bonusType.indexOf('.');
				if (dotLoc != -1)
				{
					final String stackingFlag = bonusType.substring(dotLoc + 1);
					// TODO - Need to reset bonusType to exclude this but
					// there is too much dependancy on it being there
					// built into the code.
					if (stackingFlag.startsWith("REPLACE")) //$NON-NLS-1$
					{
						aBonus.setStackingFlag(StackType.REPLACE);
					}
					else if (stackingFlag.startsWith("STACK")) //$NON-NLS-1$
					{
						aBonus.setStackingFlag(StackType.STACK);
					}
				}
				final boolean result = aBonus.addType(bonusType);

				if (!result)
				{
					Logging.debugPrint(new StringBuffer().append(
							"Could not add type ").append(aString.substring(5))
							.append(" to bonusType ").append(typeOfBonus)
							.append(" in Bonus.newBonus").toString());
					Logging.reportSource(Logging.DEBUG, context);
				}
			}
		}

		if (equalOffset >= 0)
		{
			aBonus.setVariable(bonusName.substring(equalOffset + 1));
		}

		StringTokenizer aTok = new StringTokenizer(bonusInfo, ",");

		LstUtils.deprecationCheck(aBonus, bonusName, bonusString);
		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();
			final boolean result = aBonus.parseToken(context, token);

			if (!result)
			{
				Logging.debugPrint(new StringBuffer().append(
						"Could not parse token ").append(token).append(
						" from bonusInfo ").append(bonusInfo).append(
						" in BonusObj.newBonus.").toString());
				Logging.reportSource(Logging.DEBUG, context);
			}
		}

		return aBonus;
	}
}
