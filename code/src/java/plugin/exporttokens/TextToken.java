/*
 * TextToken.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * {@code TextToken} produces the output for the output token TEXT.
 * 
 * Possible tag formats are:<pre>
 * TEXT.x.y
 * </pre>
 * 
 * Where x is the action and y is the export tag to be processed.
 */
public class TextToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "TEXT";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";

		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken(); //this should be VAR

		String action = "";
		StringBuilder varName = new StringBuilder();
		if (aTok.hasMoreElements())
		{
			action = aTok.nextToken();
			if (action.startsWith("REPLACE"))
			{
				// Make sure that any "." in the token itself stay together
				while (action.charAt(action.length() - 1) != '}')
				{
					action += '.' + aTok.nextToken();
				}
			}
		}
		if (aTok.hasMoreElements())
		{
			varName.append(aTok.nextToken());
		}
		while (aTok.hasMoreElements())
		{
			varName.append('.').append(aTok.nextToken());
		}

		StringWriter writer = new StringWriter();
		BufferedWriter bw = new BufferedWriter(writer);
		eh.replaceToken(varName.toString(), bw, pc);
		try
		{
			bw.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		retString = writer.getBuffer().toString();

		if (action.equalsIgnoreCase("UPPER") || action.equalsIgnoreCase("UPPERCASE"))
		{
			retString = retString.toUpperCase();
		}
		else if (action.equalsIgnoreCase("LOWER") || action.equalsIgnoreCase("LOWERCASE"))
		{
			retString = retString.toLowerCase();
		}
		else if (action.equalsIgnoreCase("SENTENCE") || action.equalsIgnoreCase("SENTENCECASE"))
		{
			retString = changeToSentenceCase(retString);
		}
		else if (action.equalsIgnoreCase("TITLE") || action.equalsIgnoreCase("TITLECASE"))
		{
			retString = changeToTitleCase(retString);
		}
		else if (action.equalsIgnoreCase("NUMSUFFIX"))
		{
			retString = buildNumSuffix(retString);
		}
		else if (action.equalsIgnoreCase("LENGTH"))
		{
			retString = String.valueOf(retString.length());
		}
		// TEXT.REPLACEALL{regex,newtext} or
		// TEXT.REPLACEFIRST{regex,newtext}
		else if (action.startsWith("REPLACE"))
		{
			final String replaceType = action.substring(7, action.indexOf('{'));
			String args = action.substring(action.indexOf('{') + 1, action.length() - 1);
			int patternEnd = 0;

			for (;;)
			{
				patternEnd = args.indexOf(',', patternEnd);
				if (patternEnd <= 0)
				{
					break;
				}
				if (args.charAt(patternEnd - 1) != '\\')
				{
					break;
				}
				String temp = args.substring(0, patternEnd - 1);
				args = temp + args.substring(patternEnd, args.length());
			}
			if (patternEnd <= 0)
			{
				Logging.errorPrint("Invalid REPLACE token");
			}
			String pattern = args.substring(0, patternEnd);
			pattern = pattern.replaceAll("__LP__", "\\(").replaceAll("__RP__", "\\)").replaceAll("__PLUS__", "+");
			final String replacement =
					args.substring(patternEnd + 1).trim().replaceFirst("^\"", "").replaceFirst("\"$", "");
			if (replaceType.equalsIgnoreCase("ALL"))
			{
				retString = retString.replaceAll(pattern, replacement);
			}
			else if (replaceType.equalsIgnoreCase("FIRST"))
			{
				retString = retString.replaceFirst(pattern, replacement);
			}
		}
		return retString;
	}

	/**
	 * Change the supplied string to sentence case.
	 * 
	 * @param value The value to be modified. 
	 * @return The value in sentence case.
	 */
	private String changeToSentenceCase(String value)
	{
		String temp = value.toLowerCase();
		String[] sentence = temp.split("\\.");
		StringBuilder res = new StringBuilder(value.length());
		Pattern p = Pattern.compile("\\s*");
		for (int i = 0; i < sentence.length; i++)
		{
			if (i > 0)
			{
				res.append('.');
			}
			if (!sentence[i].trim().isEmpty())
			{
				Matcher m = p.matcher(sentence[i]);
				int pos = 0;
				if (m.find())
				{
					pos = m.end();
				}
				if (pos > 0)
				{
					res.append(sentence[i].substring(0, pos));
				}
				res.append(sentence[i].substring(pos, pos + 1).toUpperCase());
				res.append(sentence[i].substring(pos + 1));
			}
			else
			{
				res.append(sentence[i]);
			}
		}
		return res.toString();
	}

	/**
	 * Change the supplied string to sentence case.
	 * 
	 * @param value The value to be modified. 
	 * @return The value in sentence case.
	 */
	private String changeToTitleCase(String value)
	{
		String temp = value.toLowerCase();
		char[] chars = temp.toCharArray();
		StringBuilder res = new StringBuilder(value.length());
		boolean start = true;
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			boolean whiteSpace = (c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r');
			if (start && !whiteSpace)
			{
				res.append(Character.toUpperCase(c));
				start = false;
			}
			else
			{
				start = whiteSpace;
				res.append(c);
			}
		}
		return res.toString();
	}

	/**
	 * Build the suffix for the provided number.
	 * 
	 * @param number The number to generate the suffix for.
	 * @return The suffix (or an empty string if not a number)
	 */
	private String buildNumSuffix(String number)
	{
		String result = "";
		int intVal = 0;
		try
		{
			intVal = new BigDecimal(number).intValue();
		}
		catch (Exception e)
		{
			// Not a number, so no suffix
			return "";
		}
		if (intVal % 10 == 1 && intVal % 100 != 11)
		{
			result = "st";
		}
		else if (intVal % 10 == 2 && intVal % 100 != 12)
		{
			result = "nd";
		}
		else if (intVal % 10 == 3 && intVal % 100 != 13)
		{
			result = "rd";
		}
		else
		{
			result = "th";
		}
		return result;
	}

	/**
	 * Never encode the plain text output
	 * @return false
	 */
	@Override
	public boolean isEncoded()
	{
		return false;
	}
}
