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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.levelability;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.core.kit.KitLevelAbility;
import pcgen.io.Compatibility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ABILITY lst token within KitLevelAbility
 */
public class AbilityToken extends AbstractToken implements
		CDOMPrimaryToken<KitLevelAbility>
{
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
	public Class<KitLevelAbility> getTokenClass()
	{
		return KitLevelAbility.class;
	}

	@Override
	public ParseResult parseToken(LoadContext context, KitLevelAbility kitAbility,
		String value)
	{
		if (!value.startsWith("PROMPT:"))
		{
			return new ParseResult.Fail("Expected " + getTokenName()
				+ " to start with PROMPT: " + value);
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String first = st.nextToken();
		PersistentTransitionChoice<?> ptc;
		try
		{
			ptc = Compatibility.processOldAdd(
					context, first);
		}
		catch (PersistenceLayerException e)
		{
			return new ParseResult.Fail(e.getMessage());
		}
		if (ptc == null)
		{
			return new ParseResult.Fail("Error was in " + getTokenName() + " " + value);
		}
		kitAbility.setAdd(ptc);

		while (st.hasMoreTokens())
		{
			String choiceString = st.nextToken();
			if (!choiceString.startsWith("CHOICE:"))
			{
				return new ParseResult.Fail("Expected " + getTokenName()
					+ " choice string to start with CHOICE: " + value);
			}
			String choice = choiceString.substring(7);
			if (ptc.decodeChoice(choice) == null)
			{
				return new ParseResult.Fail("Choice: " + choice
					+ " is not a valid selection for ADD:" + first);
			}
			kitAbility.addChoice(choice);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitLevelAbility kitAbility)
	{
		return new String[]{"PROMPT:"};
	}
}
