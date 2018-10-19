/*
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
 */
package pcgen.core.bonus;

import java.util.ArrayList; //NOPMD
import java.util.List; //NOPMD
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj.StackType;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;

@SuppressWarnings("PMD")
public final class Bonus
{
	static final String BONUS_UNDEFINED = "*UNDEFINED";

	private Bonus()
	{
		// Constructor
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
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		if ((bonusString.indexOf(Constants.PIPE) == bonusString.lastIndexOf(Constants.PIPE))
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

		String bonusInfo = sep.next();
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

		aBonus.putOriginalString(bonusString.intern());
		aBonus.setBonusName(bonusName.intern());
		aBonus.setTypeOfBonus(typeOfBonus.intern());
		Formula val = aBonus.setValue(bValue.intern());
		if (!val.isValid())
		{
			Logging.errorPrint(
				"Could not create bonusObj for:" + bonusString + " since Formula " + bValue + " is not valid: " + val);
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
					final PreParserFactory factory = PreParserFactory.getInstance();
					aBonus.addPrerequisite(factory.parse(aString));
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
					Logging.reportSource(Logging.ERROR, context);
					return null;
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
				final boolean result = aBonus.addType(bonusType.intern());

				if (!result)
				{
					Logging.log(Logging.LST_ERROR,
						new StringBuilder().append("Could not add type ").append(aString.substring(5))
							.append(" to bonusType ").append(typeOfBonus).append(" in Bonus.newBonus").toString());
					Logging.reportSource(Logging.LST_ERROR, context);
					return null;
				}
			}
		}

		if (equalOffset >= 0)
		{
			aBonus.setVariable(bonusName.substring(equalOffset + 1).intern());
		}

		if (!aBonus.requiresRealCaseTarget())
		{
			bonusInfo = bonusInfo.toUpperCase();
		}
		StringTokenizer aTok = new StringTokenizer(bonusInfo, ",");

		if (!aTok.hasMoreTokens())
		{
			Logging.log(Logging.LST_ERROR, new StringBuilder().append("Could not parse empty target ")
				.append(" from BONUS:").append(bonusString).toString());
			Logging.reportSource(Logging.LST_ERROR, context);
			return null;
		}
		LstUtils.deprecationCheck(aBonus, bonusName, bonusString);
		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();
			final boolean result = aBonus.parseToken(context, token);

			if (!result)
			{
				Logging.log(Logging.LST_ERROR, new StringBuilder().append("Could not parse token ").append(token)
					.append(" from BONUS:").append(bonusString).toString());
				Logging.reportSource(Logging.LST_ERROR, context);
				return null;
			}
		}

		return aBonus;
	}
}
