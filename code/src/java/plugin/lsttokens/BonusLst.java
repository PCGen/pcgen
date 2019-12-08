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
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.AbilityCategory;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.Changes;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class BonusLst implements CDOMPrimaryToken<CDOMObject>, DeferredToken<CDOMObject>
{
	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	/**
	 * Returns token name
	 *
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "BONUS";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		if (value.contains("PREAPPLY:"))
		{
			return new ParseResult.Fail(
				"Use of PREAPPLY prohibited on a BONUS , " + "please use TEMPBONUS with: " + value);
		}
		final String v = value.replaceAll(Pattern.quote("<this>"), obj.getKeyName());
		BonusObj bon = Bonus.newBonus(context, v);
		if (bon == null)
		{
			return new ParseResult.Fail(getTokenName() + " was given invalid bonus: " + value);
		}
		bon.setTokenSource(getTokenName());
		context.getObjectContext().addToList(obj, ListKey.BONUS, bon);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		if (obj instanceof EquipmentModifier)
		{
			// EquipmentModifier bonuses are handled by plugin.lsttokens.equipmentmodifier.BonusToken
			return null;
		}

		Changes<BonusObj> changes = context.getObjectContext().getListChanges(obj, ListKey.BONUS);
		if (changes == null || changes.isEmpty())
		{
			// Empty indicates no token present
			return null;
		}
		// CONSIDER need to deal with removed...
		Collection<BonusObj> added = changes.getAdded();
		String tokenName = getTokenName();
		Set<String> bonusSet = new TreeSet<>();
		for (BonusObj bonus : added)
		{
			if (tokenName.equals(bonus.getTokenSource()))
			{
				String bonusString = bonus.getLSTformat();
				bonusSet.add(bonusString);
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
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
						if (context.getReferenceContext().silentlyGetConstructedCDOMObject(ABILITY_CATEGORY_CLASS,
							o.toString()) == null)
						{
							LoadContext dummyCtx = new RuntimeLoadContext(context.getReferenceContext(),
								new ConsolidatedListCommitStrategy());
							dummyCtx.setSourceURI(obj.getSourceURI());
							Logging.errorPrint("BONUS: " + bonus + " in " + obj.getClass().getSimpleName() + ' '
								+ obj.getKeyName() + " contained an invalid AbilityCategory " + o.toString(), dummyCtx);
							returnValue = false;
						}
					}
				}
				else if ("UDAM".equals(bonusName))
				{
					for (Object o : bonus.getBonusInfoList())
					{
						String classKey = o.toString();
						final PCClass aClass =
								context.getReferenceContext().silentlyGetConstructedCDOMObject(PCCLASS_CLASS, classKey);
						if (aClass == null)
						{
							Logging.errorPrint("Could not find class '" + classKey + "' for UDAM token", context);
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

	@Override
	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}
}
