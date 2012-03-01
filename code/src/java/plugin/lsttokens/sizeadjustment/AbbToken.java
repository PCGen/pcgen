/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken extends AbstractNonEmptyToken<SizeAdjustment> implements
		CDOMPrimaryToken<SizeAdjustment>, DeferredToken<SizeAdjustment>
{

	/**
	 * Return token name
	 *
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "ABB";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, SizeAdjustment size, String value)
	{
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional SizeAdjustments being added in Campaigns (vs. Game
		 * Modes)
		 */
		context.ref.registerAbbreviation(size, value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, SizeAdjustment size)
	{
		String abb = context.ref.getAbbreviation(size);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

	@Override
	public Class<SizeAdjustment> getTokenClass()
	{
		return SizeAdjustment.class;
	}

	@Override
	public Class<SizeAdjustment> getDeferredTokenClass()
	{
		return SizeAdjustment.class;
	}

	@Override
	public boolean process(LoadContext context, SizeAdjustment size)
	{
		String abb = size.get(StringKey.ABB);
		if (abb == null)
		{
			Logging.errorPrint("Expected SizeAdjustment to "
					+ "have an Abbreviation, but " + size.getDisplayName()
					+ " did not");
			return false;
		}
		if (abb.length() > 1)
		{
			Logging.errorPrint("Expected SizeAdjustment to have a "
					+ "single character Abbreviation, but "
					+ size.getDisplayName() + " had: " + abb);
			return false;
		}
		return false;
	}
}
