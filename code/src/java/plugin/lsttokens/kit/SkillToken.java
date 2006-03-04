/*
 * SkillToken.java
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
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package plugin.lsttokens.kit;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitLstToken;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.core.Constants;
import java.util.StringTokenizer;

public class SkillToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "SKILL";
	}

	/**
	 * Handles the SKILL and RANK tags for a Kit.
	 * @param aKit the Kit object to add this information to
	 * @param value the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value, SystemLoader.TAB_DELIM);
		KitSkill kSkill = new KitSkill(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("SKILL:"))
			{
				Logging.errorPrint("Ignoring second SKILL tag \"" + colString + "\" in SkillToken.parse");
			}
			else if (colString.startsWith("RANK:"))
			{
				kSkill.setRank(colString.substring(5));
			}
			else if (colString.startsWith("FREE:"))
			{
				kSkill.setFree(colString.substring(5).startsWith("Y"));
			}
			else if (colString.startsWith("CLASS:"))
			{
				kSkill.setClassName(colString.substring(6));
			}
			else if (colString.startsWith("COUNT:"))
			{
				kSkill.setChoiceCount(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kSkill, colString) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitSkill info " + " \"" + value + "\"");
				}
			}
		}

		aKit.addObject(kSkill);
		return true;
	}
}
