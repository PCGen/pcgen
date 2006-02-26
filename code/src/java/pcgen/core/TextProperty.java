/*
 * TextProperty.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;

import pcgen.core.prereq.PrereqHandler;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * <code>TextProperty</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.12 $
 */
public abstract class TextProperty extends PObject implements Serializable, Comparable
{
	protected String propDesc = "";

	/** Constructor */
	public TextProperty()
	{
	    // Empty Constructor
	}

	/**
	 * Constructor
	 * @param name
	 */
	public TextProperty(final String name)
	{
		this.name = name;
	}

	/**
	 * Constructor
	 * @param name
	 * @param propDesc
	 */
	public TextProperty(final String name, final String propDesc)
	{
		this.name = name;
		this.propDesc = propDesc;
	}

	/**
	 * Set the property description
	 * @param propDesc
	 */
	public void setPropDesc(final String propDesc)
	{
		this.propDesc = propDesc;
	}

	public int compareTo(final Object obj)
	{
		if (obj instanceof TextProperty)
		{
			if (name.equals(obj.toString()))
			{
				return propDesc.compareToIgnoreCase(((SpecialAbility) obj).propDesc);
			}
		}

		return name.compareToIgnoreCase(obj.toString());
	}

	public String toString()
	{
		return name;
	}

	String getPropDesc()
	{
		return propDesc;
	}

	boolean pcQualifiesFor(final PlayerCharacter pc)
	{
		if (!PrereqHandler.passesAll(getPreReqList(), pc, null))
		{
			return false;
		}
		return true;
	}

	/**
	 * Get the property text (name, value pair)
	 * @return the property text (name, value pair)
	 */
	public String getText()
	{
		final String text;
		if ((getPropDesc() == null) || "".equals(getPropDesc()))
		{
			text = getName();
		}
		else
		{
			text = getName() + " (" + getPropDesc() + ")";
		}
		return text;
	}
	
	/**
	 * Parse the property, replace the %CHOICE 
	 * @param text
	 * @return Parsed property, with replaced the %CHOICE
	 */
	public String parse(String text) {
		for (int i = 0; i < getAssociatedCount(); i++) {
			text = text.replaceFirst("%CHOICE", getAssociated(i));
		}
		return text;
	}

	/**
	 * Get the parsed text (%CHOICEs replaced)
	 * @return the parsed text (%CHOICEs replaced)
	 */
	public String getParsedText()
	{
		return parse(getText());
	}

	/**
	 * Get the parsed text (%CHOICEs replaced)
	 * @param pc
	 * @return Get the parsed text (%CHOICEs replaced)
	 */
	public String getParsedText(final PlayerCharacter pc)
	{
		return getParsedText(pc, parse(getText()));
	}

	protected String getParsedText(final PlayerCharacter pc, final String fullDesc)
	{
	    if (fullDesc==null || fullDesc.equals("")) {
	        return "";
	    }

		String retString = "";
		if(pcQualifiesFor(pc))
		{
		    // full desc will look like "description|var1|var2|var3|..."
			StringTokenizer varTok = new StringTokenizer(fullDesc, "|");
		    // take the description as the first token
			final String description = varTok.nextToken();
			if(varTok.hasMoreTokens()) {
				// Create an array of all of the variables
				boolean atLeastOneNonZero = false;
				int[] varValue = null;
				if (varTok.countTokens() != 0)
				{
					varValue = new int[varTok.countTokens()];

					for (int j = 0; j < varValue.length; ++j)
					{
						final String varToken = varTok.nextToken();
						final int value = pc.getVariable(varToken, true, true, "", "", 0).intValue();
						if (value != 0)
						{
						    atLeastOneNonZero = true;
						}
						varValue[j] = value;
					}
				}

				if (atLeastOneNonZero)
				{
					final StringBuffer newAbility = new StringBuffer();
					varTok = new StringTokenizer(description, "%", true);
					int varCount = 0;

					while (varTok.hasMoreTokens())
					{
						final String nextTok = varTok.nextToken();

						if ("%".equals(nextTok))
						{
							if ((varValue != null) && (varCount < varValue.length))
							{
								newAbility.append(varValue[varCount++]);
							}
							else
							{
								newAbility.append('%');
							}
						}
						else
						{
							newAbility.append(nextTok);
						}
					}
					retString = newAbility.toString();
				}
				else {
				    retString = "";
				}
			}
			else {
				retString = description;
			}
		}
		return retString;
	}
}
