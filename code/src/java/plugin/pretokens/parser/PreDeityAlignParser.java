/*
 * PreDeityAlignParser.java
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
 * Current Ver: $Revision: 1.13 $
 *
 * Last Editor: $Author: byngl $
 *
 * Last Edited: $Date: 2005/11/05 17:46:20 $
 *
 */
package plugin.pretokens.parser;

import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

import java.util.StringTokenizer;

/**
 * @author wardc
 *
 */
public class PreDeityAlignParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "DEITYALIGN" };
	}

	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKind(null);		// PREMULT

		final StringTokenizer inputTokenizer = new StringTokenizer(formula, ",");

		while (inputTokenizer.hasMoreTokens())
		{
			Prerequisite subprereq = new Prerequisite();
			prereq.addPrerequisite(subprereq);
			subprereq.setKind("deityalign");
			subprereq.setOperator(PrerequisiteOperator.EQ);

			String token = inputTokenizer.nextToken();

			try
			{
				subprereq.setOperand( SettingsHandler.getGame().getShortAlignmentAtIndex(Integer.parseInt(token)) );
			}
			catch (NumberFormatException e)
			{
				subprereq.setOperand( token.trim() );
			}
		}

		if ((prereq.getPrerequisites().size() == 1) &&
			prereq.getOperator().equals(PrerequisiteOperator.GTEQ) &&
			prereq.getOperand().equals("1"))
		{
			prereq = (Prerequisite) prereq.getPrerequisites().get(0);
		}

		if (invertResult)
		{
			prereq.setOperator( prereq.getOperator().invert());
		}
		return prereq;
	}
}
