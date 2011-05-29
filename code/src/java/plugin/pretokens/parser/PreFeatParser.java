/*
 * PreFeatParser.java
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
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre feat tokens.
 *
 */
public class PreFeatParser extends AbstractPrerequisiteListParser implements
		PrerequisiteParserInterface
{
	/**
	 * Create a new PreFeatParser instance.
	 */
	public PreFeatParser()
	{
		super();
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String[] kindsHandled()
	{
		return new String[]{"feat"};
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
	public Prerequisite parse(String kind,
	                          String formula,
	                          boolean invertResult,
	                          boolean overrideQualify) throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		
		prereq.setOriginalCheckmult(formula.indexOf(",CHECKMULT,") != -1);

		//
		// Negate the feat names wrapped in []'s. Then need to bump up the required number of matches
		//
		if (formula.indexOf('[') >= 0)
		{
			negateFeatChoice(prereq);
		}

		// Convert all of the key="feat (subfeat)" to key="feat" subkey="subfeat"
		convertKeysToSubKeys(prereq, "feat");

		//Removed invert stuff because super.parse already does this, and these lines re-invert.
		prereq.setOverrideQualify(overrideQualify);

		return prereq;
	}

	/**
	 * Process prereq keys wrapped in []. If the key is wrapped in [], the
	 * prereq will be negated to check that the prereq is not passed, and
	 * the number of required matches is increased by the number of negated
	 * tests. Can handle nested prereqs.
	 *
	 * @param prereq The prereq to be negated.
	 */
	private void negateFeatChoice(Prerequisite prereq)
	{
		int modified = 0;
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (p.getKind() == null) // PREMULT
			{
				negateFeatChoice(p);
			}
			else
			{
				String preKey = p.getKey();
				if (preKey.startsWith("[") && preKey.endsWith("]"))
				{
					preKey = preKey.substring(1, preKey.length() - 1);
					p.setKey(preKey);
					p.setOperator(p.getOperator().invert());
					++modified;
				}
			}
		}
		if (modified > 0)
		{
			String oper = prereq.getOperand();
			try
			{
				oper = Integer.toString(Integer.parseInt(oper) + modified);
			}
			catch (NumberFormatException nfe)
			{
				oper = "(" + oper + ")+" + Integer.toString(modified);
			}
			prereq.setOperand(oper);
		}
	}

}
