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

package plugin.lsttokens.kit.ability;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.kit.KitAbilities;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ABILITY lst token within KitAbilities
 */
public class AbilityToken extends AbstractNonEmptyToken<KitAbilities> implements CDOMPrimaryToken<KitAbilities>
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	@Override
	public Class<KitAbilities> getTokenClass()
	{
		return KitAbilities.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitAbilities kitAbil, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(
				"No pipe found.  ABILITY token " + "in a Kit requires CATEGORY=<cat>|<ability>,<ability>");
		}
		String catString = value.substring(0, pipeLoc);
		if (!catString.startsWith("CATEGORY="))
		{
			return new ParseResult.Fail(
				"No CATEGORY= found.  ABILITY token " + "in a Kit requires CATEGORY=<cat>|<abilities>");
		}
		if (catString.length() < 10)
		{
			return new ParseResult.Fail(
				"No category found.  ABILITY token " + "in a Kit requires CATEGORY=<cat>|<abilities>");
		}
		String acName = catString.substring(9);

		CDOMSingleRef<AbilityCategory> acRef =
				context.getReferenceContext().getCDOMReference(ABILITY_CATEGORY_CLASS, acName);
		/*
		 * CONSIDER In the future it would be nice to not have to do this cast,
		 * but that should be reserved for the time when the Pool nature of
		 * AbilityCategory is separated from the Organizational nature of
		 * AbilityCategory
		 */
		kitAbil.setCategory(acRef);

		String rest = value.substring(pipeLoc + 1);
		ParseResult pr = checkSeparatorsAndNonEmpty('|', rest);
		if (!pr.passed())
		{
			return new ParseResult.Fail(
				"No abilities found.  ABILITY token " + "in a Kit requires CATEGORY=<cat>|<abilities>");
		}
		StringTokenizer st = new StringTokenizer(rest, Constants.PIPE);

		ReferenceManufacturer<Ability> rm =
				context.getReferenceContext().getManufacturerByFormatName("ABILITY=" + acName, ABILITY_CLASS);
		if (rm == null)
		{
			return new ParseResult.Fail("Could not get Reference Manufacturer for Category: " + acName);
		}

		while (st.hasMoreTokens())
		{
			String token = st.nextToken();

			if (token.startsWith("CATEGORY="))
			{
				return new ParseResult.Fail("Attempting to change the Category to '" + token + "': " + value);
			}
			CDOMReference<Ability> ref = TokenUtilities.getTypeOrPrimitive(rm, token);
			if (ref == null)
			{
				return ParseResult.INTERNAL_ERROR;
			}
			kitAbil.addAbility(ref);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitAbilities kitAbil)
	{
		Collection<CDOMReference<Ability>> references = kitAbil.getAbilityKeys();
		if (references.isEmpty())
		{
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append("CATEGORY=");
		result.append(kitAbil.getCategory().getLSTformat(false));
		for (CDOMReference<Ability> ref : references)
		{
			result.append(Constants.PIPE);
			result.append(ref.getLSTformat(false));
		}
		return new String[]{result.toString()};
	}
}
