/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class ZeroToken extends AbstractNonEmptyToken<WieldCategory> implements CDOMPrimaryToken<WieldCategory>
{

	private static final Class<WieldCategory> WIELD_CATEGORY_CLASS = WieldCategory.class;

	@Override
	public String getTokenName()
	{
		return "ZERO";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, WieldCategory wc, String value)
	{
		CDOMSingleRef<WieldCategory> stepCat =
				context.getReferenceContext().getCDOMReference(WIELD_CATEGORY_CLASS, value);
		wc.setWieldCategoryStep(0, stepCat);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, WieldCategory wc)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<WieldCategory> getTokenClass()
	{
		return WIELD_CATEGORY_CLASS;
	}
}
