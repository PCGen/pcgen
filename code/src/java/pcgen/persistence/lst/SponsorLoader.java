/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.persistence.lst;

import pcgen.cdom.content.Sponsor;
import pcgen.util.Logging;

public class SponsorLoader extends SimpleLoader<Sponsor>
{
	public SponsorLoader()
	{
		super(Sponsor.class);
	}

	@Override
	protected void processFirstToken(String token, Sponsor loadable)
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: '"
					+ token + "'");
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: '" + token
					+ "'");
		}
		else if (colonLoc == token.length() - 1)
		{
			Logging.errorPrint("Invalid Token - no value: '" + token + "'");
		}
		else if (!"SPONSOR".equals(token.substring(0, colonLoc)))
		{
			Logging.errorPrint("Invalid Entry "
					+ "- Sponsor item must be SPONSOR, found: '" + token);
		}
		else
		{
			String value = token.substring(colonLoc + 1);
			super.processFirstToken(value, loadable);
		}
	}

}
