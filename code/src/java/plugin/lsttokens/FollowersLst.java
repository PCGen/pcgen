/*
 * FollowersLst.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import java.util.StringTokenizer;

/**
 * This class implements support for the FOLLOWERS LST token.<p />
 * <b>Tag Name</b>: <code>FOLLOWERS</code>:x|y<br />
 * <b>Variables Used (x)</b>: Text (The type of companion the limit will apply to).<br />
 * <b>Variables Used (y)</b>: Number, variable or formula (Number of this type of companion the master can have)
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Limits the number of the specified type of companion the master can have.</li>
 * <li>Optional, if this tag is not present no limits are placed on the number of 
 * companions the character can have.</li>
 * <li>If more than one tag is encountered the highest value is used.</li>
 * <li>The value can be adjusted with the <code>BONUS:FOLLOWERS</code> tag</li>
 * </ul>
 * <b>Where it is used:</b><br />
 * Global tag, would most often be used in class and feat (ability) files, 
 * should also be enabled for templates and Domains.
 * <p />
 * <b>Examples:</b><br />
 * <code>FOLLOWERS:Familiar|1</code><br />
 * A character is allowed only 1 companion of type Familiar
 * 
 * @author divaa01
 *
 */
public class FollowersLst implements GlobalLstToken
{
	/**
	 *
	 * @return token name
	 */
	public String getTokenName()
	{
		return "FOLLOWERS"; //$NON-NLS-1$
	}

	/**
	 *
	 * @param obj PObject
	 * @param value String
	 * @param anInt int
	 * @return true if OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(PObject obj, String value, int anInt)
		throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final String followerType;
		if ( tok.hasMoreTokens() )
		{
			followerType = tok.nextToken().toUpperCase();
		}
		else
		{
			throw new PersistenceLayerException("Invalid FOLLOWERS token format");
		}
		final String followerNumber;
		if ( tok.hasMoreTokens() )
		{
			followerNumber = tok.nextToken();
		}
		else
		{
			throw new PersistenceLayerException("Invalid FOLLOWERS token format");
		}

		obj.setNumFollowers(followerType, followerNumber);
		return true;
	}
}
