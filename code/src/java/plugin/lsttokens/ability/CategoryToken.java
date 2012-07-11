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
package plugin.lsttokens.ability;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken extends AbstractNonEmptyToken<Ability> implements
		CDOMPrimaryToken<Ability>, DeferredToken<Ability>
{
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	@Override
	public String getTokenName()
	{
		return "CATEGORY";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, Ability ability, String value)
	{
		final Category<Ability> cat = context.ref
				.silentlyGetConstructedCDOMObject(ABILITY_CATEGORY_CLASS, value);
		if (cat == null)
		{
			return new ParseResult.Fail("Cannot find Ability Category: " + value, context);
		}
		context.ref.reassociateCategory(cat, ability);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Ability ability)
	{
		//TODO this is wrong! (different from logic in parse!)
		Category<Ability> cat = context.getObjectContext().getObject(ability,
				ObjectKey.ABILITY_CAT);
		if (cat == null)
		{
			return null;
		}
		return new String[] { cat.getKeyName() };
	}

	@Override
	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

	@Override
	public boolean process(LoadContext context, Ability ability)
	{
		if (ability.get(ObjectKey.ABILITY_CAT) == null)
		{
			Logging.errorPrint("Ability " + ability.getKeyName()
					+ " did not have a Category specified.  "
					+ "A Category is required for an Ability");
			return false;
		}
		return true;
	}

	@Override
	public Class<Ability> getDeferredTokenClass()
	{
		return getTokenClass();
	}

}
