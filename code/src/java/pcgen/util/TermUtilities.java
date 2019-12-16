/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 13 August 2008
 */

package pcgen.util;

import java.util.regex.Pattern;

import pcgen.core.term.TermEvaulatorException;

public final class TermUtilities
{
	private static final String D_STRING = "(?:NOT|ADD|IS)";
	private static final Pattern D_PAT = Pattern.compile(D_STRING);
	private static final String S_STRING = "(?:EQUIPPED|NOTEQUIPPED)";
	private static final Pattern S_PAT = Pattern.compile(S_STRING);

	private TermUtilities()
	{
	}

	public static void checkEqTypeTypesArray(String originalText, String[] types, int first)
		throws TermEvaulatorException
	{
		int cur = first + 1;

		while (cur < types.length)
		{
			// "(?:NOT|ADD|IS)"
			if (D_PAT.matcher(types[cur]).matches())
			{
				cur++;
				if (cur >= types.length)
				{
                    String sB = types[cur - 1]
                            + " must be followed by a type in "
                            + originalText;
                    throw new TermEvaulatorException(sB);
				}
				cur++;
			}
			// "(?:EQUIPPED|NOTEQUIPPED)"
			else if (S_PAT.matcher(types[cur]).matches() || "".equalsIgnoreCase(types[cur]))
			{
				cur++;
			}
			else
			{
                String sB = "Spurious type \""
                        + types[cur]
                        + "\" in "
                        + originalText;
                throw new TermEvaulatorException(sB);
			}
		}
	}

	public static void checkEquipmentTypesArray(String originalText, String[] types, int first)
		throws TermEvaulatorException
	{
		int cur = first;

		while (cur < types.length)
		{
			// "(?:NOT|ADD|IS)"			
			if (D_PAT.matcher(types[cur]).matches())
			{
				cur++;
				if (cur >= types.length)
				{
                    String sB = types[cur - 1]
                            + " must be followed by a type in "
                            + originalText;
                    throw new TermEvaulatorException(sB);
				}
				cur++;
			}
			else if ("".equalsIgnoreCase(types[cur]))
			{
				cur++;
			}
			else
			{
                String sB = "Spurious type \""
                        + types[cur]
                        + "\" in "
                        + originalText;
                throw new TermEvaulatorException(sB);
			}
		}
	}

	public static String extractContentsOfBrackets(String expressionString, String src, int fixed)
		throws TermEvaulatorException
	{
		int expEnd = expressionString.lastIndexOf(']');

		if (expEnd != expressionString.length() - 1)
		{
			StringBuilder sB = new StringBuilder();
			sB.append("Badly formed formula ");
			sB.append(expressionString);
			if (!"".equals(src))
			{
				sB.append(" in ");
				sB.append(src);
			}
			sB.append(" following \"");
			sB.append(expressionString.substring(0, fixed));
			throw new TermEvaulatorException(sB.toString());
		}

		// The string inside the brackets
		return expressionString.substring(fixed, expEnd);
	}

	static int[] splitAndConvertIntegers(String expressionString, final String clause, int numOfFields)
		throws TermEvaulatorException
	{
		final String[] sA = clause.split("\\.", numOfFields);
		if (sA.length < numOfFields)
		{
            String sB = "Invalid string "
                    + clause
                    + " following "
                    + expressionString
                    + " should be "
                    + numOfFields
                    + " integers separated by dots";
            throw new TermEvaulatorException(sB);
		}

		int[] fields = new int[numOfFields];

		int index = 0;
		for (String field : clause.split("\\.", numOfFields))
		{
			fields[index++] = Integer.parseInt(field);
		}

		return fields;
	}

	public static int[] convertToIntegers(String expressionString, String intString, int fixed, int numToExtract)
		throws TermEvaulatorException
	{
		int[] nums;
		try
		{
			nums = splitAndConvertIntegers(expressionString, intString, numToExtract);
		}
		catch (NumberFormatException n)
		{
			String sB = "Invalid string following "
					+ expressionString.substring(0, fixed)
					+ " in "
					+ expressionString;
			throw new TermEvaulatorException(sB, n);
		}
		return nums;
	}
}
