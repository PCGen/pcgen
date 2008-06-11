/*
 * StatnameToken.java
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
 * Created on September 2, 2002, 8:02 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.statsandchecks;

import java.net.URI;

import pcgen.persistence.lst.PCStatLoader;
import pcgen.persistence.lst.StatsAndChecksLstToken;
import pcgen.rules.context.LoadContext;

/**
 * Class deals with STATNAME Token
 */
public class StatnameToken implements StatsAndChecksLstToken
{
	public String getTokenName()
	{
		return "STATNAME";
	}

	public boolean parse(LoadContext context, String lstLine, URI sourceURI)
	{
		try
		{
			PCStatLoader statLoader = new PCStatLoader();
			statLoader.parseLine(context, lstLine, sourceURI);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
