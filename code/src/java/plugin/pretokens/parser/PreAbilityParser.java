/*
 * PreAbilityParser.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on January 23, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package plugin.pretokens.parser;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.PropertyFactory;

/**
 * <code>PreAbilityParser</code> parses PREABILITY prerequisite tokens.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreAbilityParser extends AbstractPrerequisiteListParser implements
		PrerequisiteParserInterface
{
	private static final String CATEGORY = "CATEGORY.";
	private static final String CATEGORY_EQUALS = "CATEGORY=";

	/**
	 * Create a new PreFeatParser instance.
	 */
	public PreAbilityParser()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"ability"};
	}

	/**
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);

		// Extract category
		extractCategory(prereq);
		
		//
		// Negate the feat names wrapped in []'s. Then need to bump up the required number of matches
		//
		if (formula.indexOf('[') >= 0)
		{
			negateAbilityChoice(prereq);
		}

		// Convert all of the key="feat (subfeat)" to key="feat" subkey="subfeat"
		convertKeysToSubKeys(prereq, "ability");

		//Removed invert stuff because super.parse already does this, and these lines re-invert.
		prereq.setOverrideQualify(overrideQualify);
		
		return prereq;
	}

	/**
	 * Extract a category restriction from the list of entries and 
	 * ensure it is applied to all keys. 
	 * @param prereq The prereq to be processed.
	 * @throws PersistenceLayerException If more than one category entry is found 
	 */
	private void extractCategory(Prerequisite prereq)
		throws PersistenceLayerException
	{
		String categoryName = "";
		if (prereq.getPrerequisites().size() == 0)
		{
			String preKey = prereq.getKey();
			if (preKey.toUpperCase().startsWith(CATEGORY)
				|| preKey.toUpperCase().startsWith(CATEGORY_EQUALS))
			{
				String tempCat = preKey.substring((CATEGORY.length()));
				if (!tempCat.toUpperCase().trim().equals("ANY"))
				{
					categoryName = tempCat;
				}
				prereq.setKey("ANY");
				prereq.setCategoryName(categoryName);
			}
		}
		
		// Copy to a temporary list as we wil be adjusting the main one.
		List<Prerequisite> prereqList =
				new ArrayList<Prerequisite>(prereq.getPrerequisites());
		for (Prerequisite p : prereqList)
		{
			if (p.getKind() == null) // PREMULT
			{
				extractCategory(p);
			}
			else
			{
				String preKey = p.getKey();
				if (preKey.toUpperCase().startsWith(CATEGORY)
					|| preKey.toUpperCase().startsWith(CATEGORY_EQUALS))
				{
					String tempCat = preKey.substring((CATEGORY.length()));
					if (categoryName.length() > 0)
					{
						throw new PersistenceLayerException(PropertyFactory
							.getFormattedString(
								"Errors.PreAbility.MultipleCategory",
								categoryName, tempCat));
					}
					else if (p != prereqList.get(0))
					{
						throw new PersistenceLayerException(PropertyFactory
							.getFormattedString(
								"Errors.PreAbility.CategoryNotFirst",
								tempCat));
					}
					
					if (!tempCat.toUpperCase().trim().equals("ANY"))
					{
						categoryName = tempCat;
					}
					prereq.getPrerequisites().remove(p);
				}
			}
		}
		if (categoryName.length() > 0)
		{
			for (Prerequisite p : prereq.getPrerequisites())
			{
				p.setCategoryName(categoryName);
			}
		}
	}

	/**
	 * Process prereq keys wrapped in []. If the key is wrapped in [], the
	 * prereq will be negated to check that the prereq is not passed, and
	 * the number of required matches is increased by the number of negated
	 * tests. Can handle nested prereqs.
	 *
	 * @param prereq The prereq to be negated.
	 */
	private void negateAbilityChoice(Prerequisite prereq)
	{
		int modified = 0;
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (p.getKind() == null) // PREMULT
			{
				negateAbilityChoice(p);
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
