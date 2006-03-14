/*
 * StartpackToken.java
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

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitLstToken;
import pcgen.persistence.lst.KitStartpackLoader;

/**
 * Parses the STARTPACK tag for a Kit. This handles all the tags that effect the
 * kit as a whole. This tag can also accept any Global PObject tag.<br>
 * The additional tags are:
 * <ul>
 * <li><code>EQUIPBUY</code></li>
 * <li><code>EQUIPSELL</code></li>
 * <li><code>APPLY</code></li>
 * <li><code>VISIBLE</code></li>
 */
public class StartpackToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "STARTPACK";
	}

	/**
	 * Parses the STARTPACK tag for a Kit. This handles all the tags that effect
	 * the kit as a whole.
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
			throws PersistenceLayerException
	{
		try
		{
			KitStartpackLoader.parseLine(aKit, value);
		}
		catch (PersistenceLayerException pe)
		{
			return false;
		}
		return true;
	}
}
