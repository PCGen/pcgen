/*
 * TemplateToken.java
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

import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.util.Logging;

/**
 * This class parses a TEMPLATE line from a Kit file. It handles the TEMPLATE
 * tag as well as all common tags.
 * <p>
 * <strong>Tag Name:</strong> TEMPLATE:x|x <br>
 * <strong>Variables Used (x):</strong> Text (Name of template)<br>
 * <strong>What it does:</strong><br>
 * &nbsp;&nbsp;This is a | (pipe) delimited list of templates that are granted
 * by the feat.<br>
 * <strong>Example:</strong><br>
 * &nbsp;&nbsp;<code>TEMPLATE:Celestial</code><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Adds the "Celestial" template to the character.<br>
 * </p>
 */
public class TemplateToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "TEMPLATE";
	}

	/**
	 * Parse the TEMPLATE line. Handles the TEMPLATE tag as well as all common
	 * tags.
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
		final StringTokenizer colToken = new StringTokenizer(value,
				SystemLoader.TAB_DELIM);
		KitTemplate kTemplate = new KitTemplate(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("TEMPLATE:"))
			{
				Logging.errorPrint("Ignoring second TEMPLATE tag \""
						+ colString + "\" in TemplateToken.parse");
			}
			else
			{
				if (parseCommonTags(kTemplate, colString) == false)
				{
					throw new PersistenceLayerException(
							"Unknown KitTemplate info " + " \"" + colString
									+ "\"");
				}
			}
		}
		aKit.addObject(kTemplate);
		return true;
	}
}
