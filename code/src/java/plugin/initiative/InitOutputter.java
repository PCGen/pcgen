/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  AboutBox.java
 *
 *  Created on September 18, 2002, 5:38 PM
 */
package plugin.initiative;

import org.jdom.output.XMLOutputter;

/**
 *@author     devon
 */
public class InitOutputter extends XMLOutputter
{
	/**
	 *  <p>
	 *
	 *  This will take the pre-defined entities in XML 1.0 and convert their
	 *  character representation to the appropriate entity reference, suitable for
	 *  XML attributes. It does no converstion for ' because it's not necessary as
	 *  the outputter writes attributes surrounded by double-quotes. This method
	 *  overrides the one from XMLOutputter to also transform any character over
	 *  ASCII 127</p>
	 *
	 *@param  str  <code>String</code> input to escape.
	 *@return      <code>String</code> with escaped content.
	 */
    @Override
	public String escapeAttributeEntities(String str)
	{
		StringBuilder buffer;
		char ch;
		String entity;

		buffer = null;

		for (int i = 0; i < str.length(); i++)
		{
			ch = str.charAt(i);

			switch (ch)
			{
				case '<':
					entity = "&lt;";

					break;

				case '>':
					entity = "&gt;";

					break;

				case '\"':
					entity = "&quot;";

					break;

				case '&':
					entity = "&amp;";

					break;

				default:

					int charnum = Character.valueOf(ch).hashCode();

					if (charnum <= 127)
					{
						entity = null;
					}
					else
					{
						entity = "&#" + charnum + ";";
					}

					break;
			}

			if (buffer == null)
			{
				if (entity != null)
				{
					// An entity occurred, so we'll have to use StringBuilder
					// (allocate room for it plus a few more entities).
					buffer = new StringBuilder(str.length() + 20);

					// Copy previous skipped characters and fall through
					// to pickup current character
					buffer.append(str.substring(0, i));
					buffer.append(entity);
				}
			}
			else
			{
				if (entity == null)
				{
					buffer.append(ch);
				}
				else
				{
					buffer.append(entity);
				}
			}
		}

		// If there were any entities, return the escaped characters
		// that we put in the StringBuilder. Otherwise, just return
		// the unmodified input string.
		return (buffer == null) ? str : buffer.toString();
	}

	/**
	 *  <p>
	 *
	 *  This will take the three pre-defined entities in XML 1.0 (used
	 *  specifically in XML elements) and convert their character representation
	 *  to the appropriate entity reference, suitable for XML element content. This method
	 *  overrides the one from XMLOutputter to also transform any character over
	 *  ASCII 127
	 *  </p>
	 *
	 *@param  str  Description of the Parameter
	 *@return      <code>String</code> with escaped content.
	 */
    @Override
	public String escapeElementEntities(String str)
	{
		StringBuilder buffer;
		char ch;
		String entity;

		buffer = null;

		for (int i = 0; i < str.length(); i++)
		{
			ch = str.charAt(i);

			switch (ch)
			{
				case '<':
					entity = "&lt;";

					break;

				case '>':
					entity = "&gt;";

					break;

				case '&':
					entity = "&amp;";

					break;

				default:

					int charnum = Character.valueOf(ch).hashCode();

					if (charnum <= 127)
					{
						entity = null;
					}
					else
					{
						entity = "&#" + charnum + ";";
					}

					break;
			}

			if (buffer == null)
			{
				if (entity != null)
				{
					// An entity occurred, so we'll have to use StringBuilder
					// (allocate room for it plus a few more entities).
					buffer = new StringBuilder(str.length() + 20);

					// Copy previous skipped characters and fall through
					// to pickup current character
					buffer.append(str.substring(0, i));
					buffer.append(entity);
				}
			}
			else
			{
				if (entity == null)
				{
					buffer.append(ch);
				}
				else
				{
					buffer.append(entity);
				}
			}
		}

		// If there were any entities, return the escaped characters
		// that we put in the StringBuilder. Otherwise, just return
		// the unmodified input string.
		return (buffer == null) ? str : buffer.toString();
	}
}
