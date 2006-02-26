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
 * Current Ver: $Revision: 1.9 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 03:57:14 $
 *
 */
package pcgen.persistence.lst.prereq;

import java.util.Iterator;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

/**
 * <code>PreFeatParser</code> parses PREFEAT prerequisite tokens.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 03:57:14 $
 *
 * @author Chris Ward <frugal@purplewombat.co.uk>
 * @version $Revision: 1.9 $
 */
public class PreFeatParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{
	/**
	 * Create a new PreFeatParser instance.
	 */
	public PreFeatParser()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "feat" };
	}

	/**
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean, boolean)
	 */
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		
		// Convert all of the key="feat (subfeat)" to key="feat" subkey="subfeat"
		convertKeysToSubKeys(prereq, "feat");

		//Removed invert stuff because super.parse already does this, and these lines re-invert.
		prereq.setOverrideQualify(overrideQualify);

		//
		// Negate the feat names wrapped in []'s. Then need to bump up the required number of matches
		//
		if (formula.indexOf('[') >= 0)
		{
			negateFeatChoice(prereq);
		}

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
		for (Iterator iter = prereq.getPrerequisites().iterator(); iter.hasNext(); )
		{
			final Prerequisite p = (Prerequisite) iter.next();

			if (p.getKind() == null)		// PREMULT
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
