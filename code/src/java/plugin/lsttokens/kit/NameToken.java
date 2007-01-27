/*
 * NameToken.java
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

package plugin.lsttokens.kit;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitBio;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;

/**
 * Handles the NAME tag for a Kit. Also can accept a GENDER tag on the same line
 * for historical reasons.
 */
public class NameToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "NAME";
	}

	/**
	 * Handles the NAME tag for a Kit. Also can accept a GENDER tag on the same
	 * line for historical reasons.
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	@Override
	public boolean parse(Kit aKit, String value, URI source)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(value, SystemLoader.TAB_DELIM);

		KitBio kBio = new KitBio();
		kBio.setCharacterName(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("GENDER:"))
			{
				kBio.setGender(colString.substring(7));
			}
		}
		aKit.addObject(kBio);

		return true;
	}
}
