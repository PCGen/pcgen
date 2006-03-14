/*
 * SpellProhibitor.java
 * Copyright 2005 (c) Stefan Raderamcher <radermacher@netcologne.de>
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
 * Created on March 3, 2005, 16:30 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author stefan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpellProhibitor {

	public static final String[] typeTags =
	{
		"ALIGNMENT",
		"DESCRIPTOR",
		"SCHOOL"
	};
	public final static int TYPE_UNDEFINED = -1;
	public final static int TYPE_ALIGNMENT = 0;
	public final static int TYPE_DESCRIPTOR = 1;
	public final static int TYPE_SCHOOL = 2;

	private int type = TYPE_UNDEFINED;
	private List valueList = null;
	private List prereqList = null;

	public SpellProhibitor(String prohibitString)
	{
		final StringTokenizer aTok = new StringTokenizer(prohibitString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken().toUpperCase();

			if (aString.startsWith("!PRE") || aString.startsWith("PRE"))
			{
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					addPreReq(factory.parse(aString) );
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				final StringTokenizer elements = new StringTokenizer(aString, ".", false);
				final String aType = elements.nextToken();

				for (int idx = 0; idx < typeTags.length; idx++)
				{
					if (typeTags[idx].equals(aType))
					{
						type = idx;
						while (elements.hasMoreTokens())
						{
							String aValue = elements.nextToken();
							if (type == TYPE_ALIGNMENT && (! aValue.equals("GOOD")) && (! aValue.equals("EVIL")) &&
									(! aValue.equals("LAWFUL")) && (! aValue.equals("CHAOTIC")))
							{
								Logging.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '" + aValue + "'");
							}
							else
							{
								if (valueList == null)
								{
									valueList = new ArrayList();
								}
								valueList.add(aValue);
							}
						}
					}
				}
				if (type == TYPE_UNDEFINED)
				{
					Logging.errorPrint("Illegal PROHIBITSPELL subtag '" + aString + "'");
				}
			}
		}
	}

	public void addPreReq(final Prerequisite prereq)
	{
		if (prereqList == null)
		{
			prereqList = new ArrayList();
		}

		if (!prereqList.contains(prereq))
		{
			prereqList.add(prereq);
		}
	}

	public List getPrereqList()
	{
		return prereqList;
	}

	public int getType()
	{
		return type;
	}

	public List getValueList()
	{
		return valueList;
	}
}
