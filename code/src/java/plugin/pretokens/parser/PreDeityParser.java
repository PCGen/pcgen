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

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author wardc
 *
 */
public class PreDeityParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "DEITY" };
	}

	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);

		String[] tokens = formula.split(",");
		
		if (tokens.length != 1)
		{
			prereq.setKind(null);		// PREMULT
		}


		for (int i = 0; i < tokens.length; i++)
		{
			String token = tokens[i];

			Prerequisite subprereq;
			if (tokens.length == 1)
			{
				subprereq = prereq;
			}
			else
			{
				subprereq = new Prerequisite();
				prereq.addPrerequisite(subprereq);
			}
			subprereq.setOperator(PrerequisiteOperator.EQ);
			subprereq.setOperand(token);

			if (token.equalsIgnoreCase("y") || token.equalsIgnoreCase("n"))
			{
				subprereq.setKind("has.deity");
			}
			else
			{
				subprereq.setKind("deity");
			}
		}
		
		if (invertResult) {
			prereq.setOperator( prereq.getOperator().invert());
		}
		return prereq;
	}
}
