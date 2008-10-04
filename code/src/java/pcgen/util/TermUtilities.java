/**
 * pcgen.util.TermUtilities.java
 * Copyright © 2008 Andrew Wilson <nuance@users.sourceforge.net>.
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
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import pcgen.base.term.VariableEvaulatorException;

public class TermUtilities {
	public static String dString = "(?:NOT|ADD|IS)";
	public static Pattern dPat = Pattern.compile(dString);
	public static String sString = "(?:EQUIPPED|NOTEQUIPPED)";
	public static Pattern sPat = Pattern.compile(sString);
	public static String resString = "(?:CONTAINER|WEAPON|ACITEM)";
	public static Pattern restrictEquipmentPat = Pattern.compile(resString);

	public static void checkEqtypesTypesArray(
			String originalText,
			String[] types, 
			int first) throws VariableEvaulatorException
	{
		int cur = first;

		// "(?:CONTAINER|WEAPON|ACITEM)"
		if (restrictEquipmentPat.matcher(types[cur]).matches())
		{
			cur++;
		}

		while (cur < types.length)
		{
			// "(?:NOT|ADD|IS)"
			if (dPat.matcher(types[cur]).matches())
			{
				cur++;
				if (cur >= types.length)
				{
					StringBuilder sB = new StringBuilder();
					sB.append(types[cur-1]);
					sB.append(" must be followed by a type in ");
					sB.append(originalText);
					throw new VariableEvaulatorException(sB.toString());
				}
				cur++;
			}
			// "(?:EQUIPPED|NOTEQUIPPED)"
			else if (sPat.matcher(types[cur]).matches() || 
					 "".equalsIgnoreCase(types[cur]))
			{
				cur++;
			}
			else
			{
				StringBuilder sB = new StringBuilder();
				sB.append("Spurious type \"");
				sB.append(types[cur-1]);
				sB.append("\" in ");
				sB.append(originalText);
				throw new VariableEvaulatorException(sB.toString());
			}
		}
	}

	public static void checkEquipmentTypesArray(
			String originalText,
			String[] types, 
			int first) throws VariableEvaulatorException
	{
		int cur = first;

		// "(?:CONTAINER|WEAPON|ACITEM)"
		
		// Count[EQUIPTYPE takes these but COUNT[EQUIPMENT doesn't for
		// some reason better known to the original writer of the
		// VariableProcessorPC class that this code is based on
		Matcher rMat = restrictEquipmentPat.matcher(types[first]); 
		if (rMat.matches())
		{
			StringBuilder sB = new StringBuilder();
			sB.append("Found \"");
			sB.append(rMat.group());
			sB.append("\" in formula ");
			sB.append(originalText);
			sB.append("\nShould be COUNT[EQTYPE, not COUNT[EQUIPMENT");

			throw new VariableEvaulatorException(sB.toString());
		}

		while (cur < types.length)
		{
			// "(?:NOT|ADD|IS)"			
			if (dPat.matcher(types[cur]).matches())
			{
				cur++;
				if (cur >= types.length)
				{
					StringBuilder sB = new StringBuilder();
					sB.append(types[cur-1]);
					sB.append(" must be followed by a type in ");
					sB.append(originalText);
					throw new VariableEvaulatorException(sB.toString());
				}
				cur++;
			}
			else if ("".equalsIgnoreCase(types[cur]))
			{
				cur++;
			}
			else
			{
				StringBuilder sB = new StringBuilder();
				sB.append("Spurious type \"");
				sB.append(types[cur]);
				sB.append("\" in ");
				sB.append(originalText);
				throw new VariableEvaulatorException(sB.toString());
			}
		}
	}

	public static String extractContentsOfBrackets(
			String expressionString,
			String src,
			int fixed) throws VariableEvaulatorException
	{
		int expEnd = expressionString.lastIndexOf("]");

		if (expEnd != expressionString.length() - 1)
		{
			StringBuilder sB = new StringBuilder();
			sB.append("Badly formed formula ");
			sB.append(expressionString);
			sB.append("\n in ");
			sB.append(src);
			sB.append("\n following \"");
			sB.append(expressionString.substring(0, fixed));
			throw new VariableEvaulatorException(sB.toString());
		}

		// The string inside the brackets
		return expressionString.substring(fixed, expEnd);
	}

	public static int[] splitAndConvertIntegers(
			final String source, int numOfFields)
	{
		int[] fields = new int[numOfFields];

		int index = 0;
		for (String field : source.split("\\.", numOfFields))
		{
			fields[index++] = Integer.parseInt(field);
		}

		return fields;
	}

	public static int[] convertToIntegers(
			String expressionString,
			String intString,
			int fixed,
			int numToExtract) throws VariableEvaulatorException
	{
		int[] nums;
		try
			{
				nums = splitAndConvertIntegers(intString, numToExtract);
		}
		catch (NumberFormatException n)
		{
			StringBuilder sB = new StringBuilder();
			sB.append("Invalid string following ");
			sB.append(expressionString.substring(0, fixed));
			sB.append("\n in ");
			sB.append(expressionString);
			sB.append("\n");
			throw new VariableEvaulatorException(sB.toString());
		}
		return nums;
	}
}
