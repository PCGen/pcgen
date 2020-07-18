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
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Domain;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Deal with FEAT token
 */
public class DomainFeatToken extends AbstractNonEmptyToken<Domain>
		implements CDOMCompatibilityToken<Domain>, DeprecatedToken
{
	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Domain obj, String value)
	{
		if (!context.processToken(obj, "ABILITY", "FEAT|AUTOMATIC|" + value))
		{
			Logging.replayParsedMessages();
			return new ParseResult.Fail("Delegation Error from Domain's FEAT");
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String getMessage(CDOMObject obj, String value)
	{
		return "Feat-based tokens have been deprecated - use ABILITY based functions";
	}

	@Override
	public Class<Domain> getTokenClass()
	{
		return Domain.class;
	}

	@Override
	public int compatibilityLevel()
	{
		return 6;
	}

	@Override
	public int compatibilitySubLevel()
	{
		return 4;
	}

	@Override
	public int compatibilityPriority()
	{
		return 3;
	}
}
