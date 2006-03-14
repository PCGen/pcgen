/*
 * PreAlignParser.java
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
package plugin.pretokens.parser;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author wardc
 *
 */
public class PreAlignParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "align" };
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#parse(java.lang.String)
	 */
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		String[] alignments = formula.split(",");

		final GameMode gm = SettingsHandler.getGame();
		if (gm == null)
		{
			prereq.setKey("");
			prereq.setOperator(PrerequisiteOperator.EQ);
			return prereq;
		}

		String[] validAlignments = gm.getAlignmentListStrings(false);
		if (validAlignments.length == 0)
		{
		    // There are no alignments for this game mode, so we
		    // do not do prereqs.
			prereq.setKey("");
			prereq.setOperator(PrerequisiteOperator.EQ);
		}
		else {
			if (alignments.length == 1)
			{
				prereq.setKey(convertFromNumber(formula, validAlignments));
				prereq.setOperator(PrerequisiteOperator.EQ);
			}
			else
			{
				prereq.setKind(null);
				prereq.setOperator(PrerequisiteOperator.GTEQ);
				prereq.setOperand("1");
	
				for (int i = 0; i < alignments.length; i++)
				{
					Prerequisite subreq = new Prerequisite();
					subreq.setKind("align");
					subreq.setKey(convertFromNumber(alignments[i], validAlignments));
					subreq.setOperator(PrerequisiteOperator.EQ);
					prereq.addPrerequisite(subreq);
				}
			}
		}
		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}

    /**
     * @param string
     * @param validAlignments
     * @return String
     */
    private String convertFromNumber(String string, String[] validAlignments) {
        try {
            int alignInt = Integer.parseInt(string);
            return validAlignments[alignInt];
        }
        catch (NumberFormatException e) {
            return string;
        }
    }
}
