/*
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
 */
package plugin.pretokens.parser;

import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre align tokens.
 */
public class PreAlignParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"align"};
	}

	/**
	 * Parse the pre req list
	 *
	 * @param kind The kind of the prerequisite (less the "PRE" prefix)
	 * @param formula The body of the prerequisite.
	 * @param invertResult Whether the prerequisite should invert the result.
	 * @param overrideQualify
	 *           if set true, this prerequisite will be enforced in spite
	 *           of any "QUALIFY" tag that may be present.
	 * @return PreReq
	 * @throws PersistenceLayerException
	 */
	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		String[] alignments = formula.split(",");

		final GameMode gm = SettingsHandler.getGameAsProperty().get();
		if (gm == null)
		{
			prereq.setKey("");
			prereq.setOperator(PrerequisiteOperator.EQ);
			return prereq;
		}

		if (Globals.getContext().getReferenceContext().getConstructedObjectCount(PCAlignment.class) == 0)
		{
			// There are no alignments for this game mode, so we
			// do not do prereqs.
			prereq.setKey("");
			prereq.setOperator(PrerequisiteOperator.EQ);
		}
		else
		{
			if (alignments.length == 1)
			{
				prereq.setKey(formula);
				prereq.setOperator(PrerequisiteOperator.EQ);
			}
			else
			{
				prereq.setKind(null);
				prereq.setOperator(PrerequisiteOperator.GTEQ);
				prereq.setOperand("1");

				for (String alignment : alignments)
				{
					Prerequisite subreq = new Prerequisite();
					subreq.setKind("align");
					subreq.setKey(alignment);
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
}
