/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject July 22, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.prereq;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a utility class related to PreReq objects.
 */
public final class PrerequisiteUtilities
{
	/**
	 * Private Constructor
	 */
	private PrerequisiteUtilities()
	{
		// Don't allow instantiation of utility class
	}

	public static final String preReqHTMLStringsForList(final PlayerCharacter aPC, final PObject aObj, final List aList, final boolean includeHeader)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(aList.size() * 20);

		final List newList = new ArrayList();
		int iter = 0;

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			newList.clear();
			final Prerequisite p = (Prerequisite) e.next();

			newList.add(p);

			if (iter++ > 0)
			{
				pString.append(" and ");
			}

			final String bString = PrereqHandler.toHtmlString(newList);

			final boolean flag;


			if (aObj instanceof Equipment)
			{
				flag = PrereqHandler.passesAll(newList, (Equipment) aObj, aPC);
			}
			else
			{
				flag = PrereqHandler.passesAll(newList, aPC, null);
			}

			if (!flag)
			{
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlStart());
				pString.append("<i>");
			}

			final StringTokenizer aTok = new StringTokenizer(bString, "&<>", true);

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();

				if (aString.equals("<"))
				{
					pString.append("&lt;");
				}
				else if (aString.equals(">"))
				{
					pString.append("&gt;");
				}
				else if (aString.equals("&"))
				{
					pString.append("&amp;");
				}
				else
				{
					pString.append(aString);
				}
			}

			if (!flag)
			{
				pString.append("</i>");
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlEnd());
			}
		}

		if (pString.toString().indexOf('<') >= 0)
		{
			// seems that ALIGN and STAT have problems in
			// HTML display, so wrapping in <font> tag.
			pString.insert(0, "<font>");
			pString.append("</font>");

			if (includeHeader)
			{
				if (pString.toString().indexOf('<') >= 0)
				{
					pString.insert(0, "<html>");
					pString.append("</html>");
				}
			}
		}

		return pString.toString();
	}
}
