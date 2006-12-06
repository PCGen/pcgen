/*
 * PreDeityParser.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import java.util.Iterator;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * <code>PreDeityParser</code> parses PREDEITY prerequisites. It handles both 
 * new (PREDEITY:1,Odin) and old (PREDEITY:Odin) format syntax along with the
 * hasdeity syntax (PREDEITY:Y or PREDEITY:No). 
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PreDeityParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{
	/**
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "DEITY" };
	}

	/**
	 * @see pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser#parse(java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		
		// Scan for any has deity options
		replaceHasDeityPrereqs(prereq);
		return prereq;
	}

	/**
	 * Scan a predeity prerequisite and its children, converting any yes or no deity 
	 * entries into hasdeity prereqs.
	 *   
	 * @param prereq The prereq to be scanned.
	 */
	private void replaceHasDeityPrereqs(Prerequisite prereq)
	{
		String key = prereq.getKey();
		if ("deity".equalsIgnoreCase(prereq.getKind()) && key != null
			&& (key.equalsIgnoreCase("y") || key.equalsIgnoreCase("n")
				|| key.equalsIgnoreCase("yes") || key.equalsIgnoreCase("no")))
		{
			if (key.toLowerCase().startsWith("y"))
			{
				prereq.setKey("Y");
			}
			else
			{
				prereq.setKey("N");
			}
			prereq.setKind("has.deity");
		}

		for (Iterator<Prerequisite> iter = prereq.getPrerequisites().iterator(); iter.hasNext();)
		{
			Prerequisite subprereq = iter.next();
			replaceHasDeityPrereqs(subprereq);
		}
	}
}
