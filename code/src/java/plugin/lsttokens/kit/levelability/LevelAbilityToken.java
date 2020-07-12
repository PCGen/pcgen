/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.levelability;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitLevelAbility;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Level Ability token (a component of Kits)
 */
public class LevelAbilityToken extends AbstractNonEmptyToken<KitLevelAbility>
		implements CDOMPrimaryToken<KitLevelAbility>, DeferredToken<Kit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "LEVELABILITY";
	}

	@Override
	public Class<KitLevelAbility> getTokenClass()
	{
		return KitLevelAbility.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitLevelAbility kitLA, String value)
	{
		int equalLoc = value.indexOf('=');
		if (equalLoc == -1)
		{
			return new ParseResult.Fail(getTokenName() + " requires an =: " + value);
		}
		if (equalLoc != value.lastIndexOf('='))
		{
			return new ParseResult.Fail(getTokenName() + " requires a single =: " + value);
		}
		String className = value.substring(0, equalLoc);
		if (className.isEmpty())
		{
			return new ParseResult.Fail(getTokenName() + " requires a class name before =: " + value);
		}
		String level = value.substring(equalLoc + 1);
		CDOMSingleRef<PCClass> cl = context.getReferenceContext().getCDOMReference(PCClass.class, className);
		try
		{
			int lvl = Integer.parseInt(level);
			if (lvl <= 0)
			{
				return new ParseResult.Fail(getTokenName() + " expected an integer > 0");
			}
			kitLA.setLevel(lvl);
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
		kitLA.setClass(cl);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitLevelAbility kitLA)
	{
		CDOMReference<PCClass> cl = kitLA.getPCClass();
		if (cl == null)
		{
			return null;
		}
		int lvl = kitLA.getLevel();
		return new String[]{cl.getLSTformat(false) + '=' + lvl};
	}

	@Override
	public Class<Kit> getDeferredTokenClass()
	{
		return Kit.class;
	}

	@Override
	public boolean process(LoadContext context, Kit obj)
	{
		for (BaseKit bk : obj.getSafeListFor(ListKey.KIT_TASKS))
		{
			if (bk instanceof KitLevelAbility)
			{
				obj.setDoLevelAbilities(false);
			}
		}
		return true;
	}

	/*
	 * KitLevelAbility kla = (KitLevelAbility) bk; PersistentTransitionChoice<?>
	 * add = kla.getAdd(); CDOMSingleRef<PCClass> ref = kla.getPCClass();
	 * PCClass pcc = ref.resolvesTo(); List<PersistentTransitionChoice<?>>
	 * addList = pcc.getListFor(ListKey.ADD); if (addList == null) { //Error }
	 * else if (!addList.contains(add)) { //Error }
	 */
}
