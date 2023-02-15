/*
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
 */
package plugin.pretokens.parser;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * A prerequisite parser class that handles the parsing of pre ability tokens.
 */
public class PreAbilityParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{
	private static final String CATEGORY = "CATEGORY.";
	private static final String CATEGORY_EQUALS = "CATEGORY=";

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"ability", "feat"};
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
		if ("feat".equalsIgnoreCase(kind))
		{
			Logging.deprecationPrint("PREFEAT has been deprecated, please use PREABILITY");
		}
		boolean extract = "ability".equalsIgnoreCase(kind);
		kind = "ability";
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setOriginalCheckmult(formula.contains(",CHECKMULT,"));

		if (extract)
		{
			// Extract category
			extractCategory(prereq);
		}
		else
		{
			setCategory(prereq, "FEAT");
		}

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

	private static void setCategory(Prerequisite prereq, String string)
	{
		prereq.setCategoryName(string);
		for (Prerequisite element : prereq.getPrerequisites())
		{
			setCategory(element, string);
		}
	}

	/**
	 * Extract a category restriction from the list of entries and 
	 * ensure it is applied to all keys. 
	 * @param prereq The prereq to be processed.
	 * @throws PersistenceLayerException If more than one category entry is found 
	 */
	private static void extractCategory(Prerequisite prereq) throws PersistenceLayerException
	{
		String categoryName = "";
		if (prereq.getPrerequisiteCount() == 0)
		{
			String preKey = prereq.getKey();
			if (preKey.toUpperCase().startsWith(CATEGORY) || preKey.toUpperCase().startsWith(CATEGORY_EQUALS))
			{
				String tempCat = preKey.substring((CATEGORY.length()));
				if (tempCat.trim().equalsIgnoreCase("ANY"))
				{
					Logging.errorPrint("ANY no longer allowed as an Ability Category in PREABILITY");
				}
				else
				{
					categoryName = tempCat;
				}
				prereq.setKey("ANY");
				prereq.setCategoryName(categoryName);
			}
		}

		// Copy to a temporary list as we wil be adjusting the main one.
		List<Prerequisite> prereqList = new ArrayList<>(prereq.getPrerequisites());
		for (Prerequisite p : prereqList)
		{
			if (p.getKind() == null) // PREMULT
			{
				extractCategory(p);
			}
			else
			{
				String preKey = p.getKey();
				if (preKey.toUpperCase().startsWith(CATEGORY) || preKey.toUpperCase().startsWith(CATEGORY_EQUALS))
				{
					String tempCat = preKey.substring((CATEGORY.length()));
					if (!categoryName.isEmpty())
					{
						throw new PersistenceLayerException(LanguageBundle
							.getFormattedString("Errors.PreAbility.MultipleCategory", categoryName, tempCat));
					}
					else if (p != prereqList.get(0))
					{
						throw new PersistenceLayerException(
							LanguageBundle.getFormattedString("Errors.PreAbility.CategoryNotFirst", tempCat));
					}

					if (tempCat.trim().equalsIgnoreCase("ANY"))
					{
						Logging.errorPrint("ANY no longer allowed as an Ability Category in PREABILITY");
					}
					else
					{
						categoryName = tempCat;
					}
					prereq.removePrerequisite(p);
				}
			}
		}
		/*
		 * TODO There is a special case here where
		 * prereq.getPrerequisiteList().size() == 1 That can be consolidated
		 * into one prereq ... question is how (and keep the operator, etc.
		 * correct)
		 */
		if (!categoryName.isEmpty())
		{
			for (Prerequisite p : prereq.getPrerequisites())
			{
				p.setCategoryName(categoryName);
			}
		}
		else
		{
			String preKey;
			if (prereq.getPrerequisiteCount() == 0)
			{
				preKey = prereq.getKey();
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				for (Prerequisite p : prereq.getPrerequisites())
				{
					sb.append(p.getKey()).append(',');
				}
				sb.setLength(sb.length() - 1);
				preKey = sb.toString();
			}
			Logging.errorPrint("PREABILITY: found without CATEGORY: " + preKey);
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
	private static void negateAbilityChoice(Prerequisite prereq)
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
				oper = '(' + oper + ")+" + Integer.toString(modified);
			}
			prereq.setOperand(oper);
		}
	}

	@Override
	protected boolean allowsNegate()
	{
		return true;
	}

}
