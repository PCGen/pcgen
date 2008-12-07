/*
 * AbilityToken.java
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
 *
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.SettingsHandler;
import pcgen.core.kit.KitAbilities;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Deals with ABILITY lst token within KitAbilities 
 */
public class AbilityToken extends AbstractToken implements
		CDOMSecondaryToken<KitAbilities>
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

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

	public Class<KitAbilities> getTokenClass()
	{
		return KitAbilities.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitAbilities kitAbil, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"No pipe found.  ABILITY token "
					+ "in a Kit requires CATEGORY=<cat>|<ability>,<ability>");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Two pipes found.  ABILITY token "
					+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		String catString = value.substring(0, pipeLoc);
		if (!catString.startsWith("CATEGORY="))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"No CATEGORY= found.  ABILITY token "
					+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		if (catString.length() < 10)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"No category found.  ABILITY token "
					+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		AbilityCategory ac =
				SettingsHandler.getGame().getAbilityCategory(
					catString.substring(9));
		kitAbil.setCategory(ac);

		String rest = value.substring(pipeLoc + 1);
		if (isEmpty(rest) || hasIllegalSeparator('|', rest))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"No abilities found.  ABILITY token "
					+ "in a Kit requires CATEGORY=<cat>|<abilities>");
			return false;
		}
		StringTokenizer st = new StringTokenizer(rest, "|");

		while (st.hasMoreTokens())
		{
			String token = st.nextToken();

			if (token.startsWith("CATEGORY="))
			{
				Logging.errorPrint("Attempting to change the Category to '"
					+ token + "': " + value);
				return false;
			}
			CDOMReference<Ability> ref =
					TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						ac, token);
			if (ref == null)
			{
				return false;
			}
			List<String> choices = null;
			if (token.indexOf('(') != -1)
			{
				choices = new ArrayList<String>();
				AbilityUtilities.getUndecoratedName(token, choices);
			}
			kitAbil.addAbility(ref, choices);
		}
		return true;
	}

	public String[] unparse(LoadContext context, KitAbilities KitAbilities)
	{
		Collection<CDOMReference<Ability>> references =
				KitAbilities.getAbilityKeys();
		if (references == null)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();
		for (CDOMReference<Ability> ref : references)
		{
			if (result.length() == 0)
			{
				CategorizedCDOMReference<Ability> ccr =
						(CategorizedCDOMReference<Ability>) ref;
				Category<Ability> cat = ccr.getCDOMCategory();
				result.append("CATEGORY=");
				result.append(cat.toString());
			}
			result.append(Constants.PIPE);
			result.append(ref.getLSTformat());
		}
		return new String[]{result.toString()};
	}
}
