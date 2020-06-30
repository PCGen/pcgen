/*
 * Copyright 2014-15 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.FactKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.output.publish.OutputDB;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.rules.persistence.token.ParseResult;

/**
 * A prerequisite parser class that handles the parsing of pre fact tokens.
 */
public class PreFactParser extends AbstractPrerequisiteListParser
{
	/**
	 * Get the type of prerequisite handled by this token.
	 * 
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"fact"};
	}

	/**
	 * Parse the pre req list
	 * 
	 * @param kind
	 *            The kind of the prerequisite (less the "PRE" prefix)
	 * @param formula
	 *            The body of the prerequisite.
	 * @param invertResult
	 *            Whether the prerequisite should invert the result.
	 * @param overrideQualify
	 *            if set true, this prerequisite will be enforced in spite of
	 *            any "QUALIFY" tag that may be present.
	 * @return PreReq
	 * @throws PersistenceLayerException
	 */
	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		if (prereq.isCountMultiples())
		{
			throw new PersistenceLayerException("PREFACT does not support CHECKMULT");
		}

		prereq.setOverrideQualify(overrideQualify);

		return prereq;
	}

	@Override
	protected void parsePrereqListType(Prerequisite prereq, String kind, String formula)
		throws PersistenceLayerException
	{
		// Sanity checking
		ParseResult parseResult = checkForIllegalSeparator(kind, ',', formula);
		if (!parseResult.passed())
		{
			throw new PersistenceLayerException(parseResult.toString());
		}
		if (formula.contains("[") || formula.contains("]"))
		{
			throw new PersistenceLayerException("Prerequisite " + kind + " can not contain []: " + formula);
		}
		if (formula.contains("|"))
		{
			throw new PersistenceLayerException("Prerequisite " + kind + " can not contain |: " + formula);
		}

		String[] elements = formula.split(",");
		try
		{
			Integer.parseInt(elements[0]);
			if (elements.length == 1)
			{
				throw new PersistenceLayerException("Prerequisite " + kind + " can not have only a count: " + formula);
			}
		}
		catch (NumberFormatException nfe)
		{
			throw new PersistenceLayerException('\'' + elements[0] + "' is not a valid integer", nfe);
		}

		String filetype = elements[1];
		String[] fileElements = filetype.split("\\.");
		if (!OutputDB.isLegal(fileElements[0]))
		{
			throw new PersistenceLayerException('\'' + elements[1] + "' is not a valid location to check for a FACT");
		}

		for (int i = 2; i < elements.length; i++)
		{
			if (elements[i].indexOf('=') == -1)
			{
				throw new PersistenceLayerException(
					"PREFACT require a target value, e.g. Key=Value, found: " + elements[i]);
			}
		}
		prereq.setOperand(elements[0]);

		if (elements.length == 3)
		{
			// We only have a number of prereqs to pass, and a single prereq so we do not want a
			// wrapper prereq around a list of 1 element.
			// i.e. 2,TYPE=ItemCreation
			checkFactKey(elements[2]);
			prereq.setKey(elements[2]);
		}
		else
		{
			// Token now contains all of the possible matches,
			// min contains the target number (if there is one)
			// number contains the number of 'tokens' that be at least 'min'
			prereq.setOperator(PrerequisiteOperator.GTEQ);
			// we have more than one option, so use a group
			prereq.setKind(null);
			for (int i = 2; i < elements.length; i++)
			{
				Prerequisite subreq = new Prerequisite();
				subreq.setKind(kind.toLowerCase());
				subreq.setCountMultiples(true);
				// The element is either of the form "TYPE=foo" or "DEX=9"
				// if it is the later, we need to extract the '9'
				subreq.setOperator(PrerequisiteOperator.GTEQ);
				subreq.setCategoryName(filetype);
				checkFactKey(elements[i]);
				subreq.setKey(elements[i]);
				subreq.setOperand("1");
				prereq.addPrerequisite(subreq);
			}
		}
		setLocation(prereq, filetype);
	}

	/**
	 * Check that the referenced fact key exists somewhere in the loaded data. 
	 * This does not guarantee that the object tested will have a value for this 
	 * fact, or that the fact is appropriate for the type of object being 
	 * tested. However it will catch simple typos.
	 *    
	 * @param factTest The key=value test to be checked.
	 * @throws PersistenceLayerException If the fact key is not defined in the data.
	 */
	private static void checkFactKey(String factTest) throws PersistenceLayerException
	{
		String[] parts = factTest.split("=");
		try
		{
			FactKey.valueOf(parts[0]);
		}
		catch (IllegalArgumentException e)
		{
			throw new PersistenceLayerException("Unknown FACT in PREFACT. Test was: " + factTest, e);
		}
	}

	private static void setLocation(Prerequisite prereq, String location)
    {
		if (prereq.getPrerequisiteCount() == 0)
		{
			prereq.setCategoryName(location);
		}

		// Copy to a temporary list as we will be adjusting the main one.
		Iterable<Prerequisite> prereqList = new ArrayList<>(prereq.getPrerequisites());
		for (Prerequisite p : prereqList)
		{
			if (p.getKind() == null) // PREMULT
			{
				setLocation(p, location);
			}
			else
			{
				prereq.setCategoryName(location);
			}
		}
	}

	@Override
	protected boolean requiresValue()
	{
		return true;
	}

}
