/*
 * TemplateListToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.Iterator;

/**
 * Deals with returning value of TEMPLATELIST token
 */
public class TemplateListToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "TEMPLATELIST";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getTemplateListToken(pc);
	}

	/**
	 * Get value of TEMPALTELIST token
	 * @param pc
	 * @return value of TEMPALTELIST token
	 */
	public static String getTemplateListToken(PlayerCharacter pc)
	{
		StringBuffer retString = new StringBuffer();
		String del = "";

		for (Iterator it = pc.getOutputVisibleTemplateList().iterator(); it
			.hasNext();)
		{
			PCTemplate template = (PCTemplate) it.next();

			retString.append(del).append(template.toString());
			del = ", ";
		}

		return retString.toString();
	}
}
