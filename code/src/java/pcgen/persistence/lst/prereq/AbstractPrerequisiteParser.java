/*
 * AbstractPreParser.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author wardc
 *
 */
public abstract class AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	/**
	 * Parses the PreRequisite.
	 * 
	 * @param kind the kind of the prerequisite (less the "PRE" prefix).
	 * @param formula The body of the prerequisite.
	 * @param invertResult Whether the prerequisite should invert the result.
	 * @param overrideQualify 
	 * @return PreReq 
	 * @throws PersistenceLayerException 
	 */
	public Prerequisite parse(
		String kind,
		String formula,
		boolean invertResult,
		boolean overrideQualify)
		throws PersistenceLayerException
	{
		// Check to make sure that this class can parse this token
		boolean foundTag = false;

		for (int i = 0; i < kindsHandled().length; i++)
		{
			String arrayElement = kindsHandled()[i];

			if (arrayElement.equalsIgnoreCase(kind))
			{
				foundTag = true;

				break;
			}
		}

		if (!foundTag)
		{
			throw new PersistenceLayerException(this.getClass().getName()
				+ " can not parse a Prerequisite tag of '" + kind + ":"
				+ formula + "'");
		}

		// If we can parse this token then set the kind and invert flag.
		Prerequisite prereq = new Prerequisite();
		prereq.setKind(kind);

		prereq.setOverrideQualify(overrideQualify);
		return prereq;
	}
}
