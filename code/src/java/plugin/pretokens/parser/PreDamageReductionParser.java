/*
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
 */
package plugin.pretokens.parser;

import java.util.StringTokenizer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre damage reduction tokens.
 */
public class PreDamageReductionParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"DR"};
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
		prereq.setKind(null); // PREMULT

		final StringTokenizer inputTokenizer = new StringTokenizer(formula, ",");

		// the number of DRs which must match
		String tok = inputTokenizer.nextToken();

		try
		{
			prereq.setOperand(Integer.toString(Integer.parseInt(tok)));
		}
		catch (NumberFormatException exc)
		{
			throw new PersistenceLayerException("Badly formed passesPreDR/number of DRs attribute: " + tok, exc);
		}

		// Parse all of the tokens in the input list
		while (inputTokenizer.hasMoreTokens())
		{
			final StringTokenizer inputDRTokenizer = new StringTokenizer(inputTokenizer.nextToken(), "=.");
			final String drType = inputDRTokenizer.nextToken(); // either Good.10 or Good=10
			final int drValue;

			if (inputDRTokenizer.hasMoreTokens())
			{
				try
				{
					drValue = Integer.parseInt(inputDRTokenizer.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					throw new PersistenceLayerException("Badly formed passesPreDR value: " + formula, nfe);
				}
			}
			else
			{
				drValue = 0;
			}

			Prerequisite subprereq = new Prerequisite();
			subprereq.setKind("dr");
			subprereq.setKey(drType);
			subprereq.setOperand(Integer.toString(drValue));
			prereq.addPrerequisite(subprereq);
		}

		if ((prereq.getPrerequisiteCount() == 1) && prereq.getOperator().equals(PrerequisiteOperator.GTEQ)
			&& prereq.getOperand().equals("1"))
		{
			prereq = prereq.getPrerequisites().get(0);
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}
}
