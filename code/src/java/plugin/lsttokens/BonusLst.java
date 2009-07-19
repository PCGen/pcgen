/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * @author djones4
 */
public class BonusLst implements CDOMPrimaryToken<CDOMObject>,
		DeferredToken<CDOMObject>
{
	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/**
	 * Returns token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		final String v = value.replaceAll(Pattern.quote("<this>"), obj
				.getKeyName());
		BonusObj bon = Bonus.newBonus(obj.bonusStringPrefix() + v);
		if (bon == null)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " was given invalid bonus: " + value);
			return false;
		}
		bon.setTokenSource(getTokenName());
		context.obj.addToList(obj, ListKey.BONUS, bon);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<BonusObj> changes = context.obj.getListChanges(obj,
				ListKey.BONUS);
		if (changes == null || changes.isEmpty())
		{
			// Empty indicates no token present
			return null;
		}
		// CONSIDER need to deal with removed...
		Collection<BonusObj> added = changes.getAdded();
		String tokenName = getTokenName();
		Set<String> bonusSet = new TreeSet<String>();
		String prefix = obj.bonusStringPrefix();
		int prefixLength = prefix.length();
		for (BonusObj bonus : added)
		{
			if (tokenName.equals(bonus.getTokenSource()))
			{
				String bonusString = bonus.getLSTformat();
				if (prefixLength != 0)
				{
					if (bonusString.startsWith(prefix))
					{
						bonusString = bonusString.substring(prefixLength);
					}
					else
					{
						Logging.errorPrint("Expected BONUS to start with "
								+ prefix);
					}
				}
				bonusSet.add(bonusString);
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[bonusSet.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public boolean process(LoadContext context, CDOMObject obj)
	{
		List<BonusObj> bonusList = obj.getListFor(ListKey.BONUS);
		boolean returnValue = true;
		if (bonusList != null)
		{
			for (BonusObj bonus : bonusList)
			{
				String bonusName = bonus.getBonusName();
				if ("ABILITYPOOL".equalsIgnoreCase(bonusName))
				{
					for (Object o : bonus.getBonusInfoList())
					{
						if (context.ref.getCategoryFor(ABILITY_CLASS, o.toString()) == null)
						{
							Logging.errorPrint("BONUS: " + bonus + " in "
									+ obj.getClass().getSimpleName() + " "
									+ obj.getKeyName()
									+ " contained an invalid AbilityCategory");
							returnValue = false;
						}
					}
				}
				else if ("UDAM".equals(bonusName))
				{
					for (Object o : bonus.getBonusInfoList())
					{
						String classKey = o.toString();
						final PCClass aClass = context.ref
								.silentlyGetConstructedCDOMObject(
										PCCLASS_CLASS, classKey);
						if (aClass == null)
						{
							Logging.errorPrint("Could not find class '"
									+ classKey + "' for UDAM token");
						}
					}
				}
			}
		}
		try
		{
			obj.ownBonuses(obj);
		}
		catch (CloneNotSupportedException e)
		{
			Logging.errorPrint(e.getLocalizedMessage());
			return false;
		}
		return returnValue;
	}

	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}
}
