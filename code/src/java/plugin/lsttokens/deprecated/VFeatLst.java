/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class VFeatLst extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMCompatibilityToken<CDOMObject>, DeprecatedToken
{

	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
	{
		try
		{
			if (!context.processToken(obj, "ABILITY", "FEAT|VIRTUAL|" + value))
			{
				Logging.replayParsedMessages();
				return new ParseResult.Fail("Delegation Error from VFEAT");
			}
		}
		catch (PersistenceLayerException e)
		{
			return new ParseResult.Fail("Delegation Error from VFEAT: " + e.getLocalizedMessage());
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public String getMessage(CDOMObject obj, String value)
	{
		return "Feat-based tokens have been deprecated - use ABILITY based functions";
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
		return 7;
	}
}
