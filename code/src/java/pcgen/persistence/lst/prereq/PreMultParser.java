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
package pcgen.persistence.lst.prereq;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public class PreMultParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"MULT"};
	}

	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKind(null);
		prereq.setCharacterRequired(false);

		int commaIndex = formula.indexOf(',');

		if (commaIndex > 0)
		{
			String minFormula = formula.substring(0, commaIndex);
			formula = formula.substring(commaIndex + 1);
			prereq.setOperator(PrerequisiteOperator.GTEQ);
			prereq.setOperand(minFormula);
		}

		// [PREARMORPROF:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]
		PreParserFactory parser = PreParserFactory.getInstance();
		for (String s : splitOnTopLevelToken(formula, '[', ']'))
		{
			prereq.addPrerequisite(parser.parse(s));
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}

	protected static List<String> splitOnTopLevelToken(String input, char startDelimiter, char endDelimiter)
		throws PersistenceLayerException
	{
		int nesting = 0;
		int startIndex = 0;
		int currIndex;
		boolean expectComma = false;
		boolean expectStart = true;
		List<String> subList = new ArrayList<>();

		for (currIndex = 0; currIndex < input.length(); currIndex++)
		{
			char currChar = input.charAt(currIndex);

			if ((currChar == ',') && (nesting == 0))
			{
				String subPre = input.substring(startIndex + 1, currIndex - 1);
				startIndex = currIndex + 1;

				// subPre = PREARMORPROF:1,TYPE.Medium
				subList.add(subPre);
			}
			else if (expectComma)
			{
				Logging.log(Logging.LST_WARNING, "Found close bracket without trailing comma in PREMULT, "
					+ "trailing character was: " + currChar + " in " + input);
			}
			expectComma = false;

			if (currChar == startDelimiter)
			{
				nesting++;
			}
			else if (expectStart)
			{
				String loc = (currIndex == 0) ? "at start" : "after comma";
				Logging.log(Logging.LST_WARNING,
					"Expected Open Bracket " + loc + " in PREMULT, " + "character was: " + currChar + " in " + input);
			}
			expectStart = false;

			if (currChar == endDelimiter)
			{
				nesting--;
				if (nesting == 0)
				{
					expectComma = true;
				}
			}
		}

		if (nesting != 0)
		{
			throw new PersistenceLayerException(
				"Unbalanced " + startDelimiter + endDelimiter + " in PREMULT '" + input + "'.");
		}
		if (currIndex - startIndex <= 1)
		{
			throw new PersistenceLayerException("Found empty or unbracketed section in PREMULT '" + input + "'.");
		}
		subList.add(input.substring(startIndex + 1, currIndex - 1));

		return subList;
	}
}
