/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.core;

import java.util.StringTokenizer;

import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * {@code RollInfo}.
 *
 * Structure representing dice rolls
 *
 */
public final class RollInfo
{
	public int getSides()
	{
		return sides;
	}

	public int getTimes()
	{
		return times;
	}

	/** What shape dice to roll. */
	protected int sides = 0;

	/** Number of dice to roll. */
	protected int times = 0;

	/** Which specific rolls to keep after rolls have been sorted
	 * in ascending order.  {@code null} means to keep all
	 * rolls.  Example, [1,3] means to keep the first and third
	 * lowest rolls, which would be {true, false true} for 3 dice.
	 * keepTop and keepBottom are implemented as special kinds of
	 * this array. */
	private boolean[] keepList = null;

	/** Amount to add to the final roll. */
	private int modifier = 0;

	/** Rerolls rolls above this amount. */
	private int rerollAbove = Integer.MAX_VALUE;

	/** Rerolls rolls below this amount. */
	private int rerollBelow = Integer.MIN_VALUE;

	/** Total result never greater than this. */
	private int totalCeiling = Integer.MAX_VALUE;

	/** Total result never less than this. */
	private int totalFloor = Integer.MIN_VALUE;

	/**
	 * Check that the rollString is valid.
	 * @param rollString The string to be checked
	 * @return An empty string if the string is valid, an error message if not.
	 */
	public static String validateRollString(String rollString)
	{
		return parseRollInfo(new RollInfo(), rollString);
	}

	private static String parseRollInfo(RollInfo rollInfo, String rollString)
	{
		// To really do this right, we change the token string
		// as we go along so that we maintain parser state by
		// means of the tokens rather than something more
		// explicit.  In truth, this is an ideal application
		// of flex and friends for a "mini-language" whose
		// statements evaluate to dice rolls.  Too much LISP
		// on the brain.  --bko

		try
		{
			final StringTokenizer st = new StringTokenizer(rollString, " ", true);
			String tok = st.nextToken("d");

			if ("d".equals(tok))
			{
				rollInfo.times = 1;
			}
			else
			{
				rollInfo.times = Integer.parseInt(tok);

				if (st.hasMoreTokens())
				{
					tok = st.nextToken("d"); // discard the 'd'

					if (!"d".equals(tok))
					{
						return "Bad roll parsing in '" + rollString + "': missing 'd'";
					}
				}
				else
				{
					rollInfo.sides = 1;

					return "";
				}
			}

			String parseChars = "/\\|mM+-tT";
			rollInfo.sides = Integer.parseInt(st.nextToken(parseChars));

			if (rollInfo.sides < 1)
			{
				return "Bad roll parsing in '" + rollString + "': sides < 1: " + rollInfo.sides;
			}

			while (st.hasMoreTokens())
			{
				tok = st.nextToken(parseChars);

				switch (tok.charAt(0))
				{
					case '/' -> {
						parseChars = "mM+-tT";
						final int keepTop = Integer.parseInt(st.nextToken(parseChars));
						if (keepTop > rollInfo.times)
						{
							return "Bad keepTop in '" + rollString + "': times: " + rollInfo.times + "; keepTop: "
									+ keepTop;
						}
						rollInfo.keepList = new boolean[rollInfo.times];

						// Rely on fact boolean is false by default.  --bko
						for (int i = rollInfo.times - keepTop; i < rollInfo.times; ++i)
						{
							rollInfo.keepList[i] = true;
						}
					}
					case '\\' -> {
						parseChars = "mM+-tT";
						final int keepBottom = Integer.parseInt(st.nextToken(parseChars));
						if (keepBottom > rollInfo.times)
						{
							return "Bad keepBottom in '" + rollString + "': times: " + rollInfo.times + "; "
									+ "keepBottom: "
									+ keepBottom;
						}
						rollInfo.keepList = new boolean[rollInfo.times];

						// Rely on fact boolean is false by default.  --bko
						for (int i = 0; i < keepBottom; ++i)
						{
							rollInfo.keepList[i] = true;
						}
					}
					case '|' -> {
						parseChars = "mM+-tT";
						tok = st.nextToken(parseChars);
						rollInfo.keepList = new boolean[rollInfo.times];
						final StringTokenizer keepSt = new StringTokenizer(tok, ",");
						while (keepSt.hasMoreTokens())
						{
							rollInfo.keepList[Integer.parseInt(keepSt.nextToken(",")) - 1] = true;
						}
					}
					case 'm' -> {
						parseChars = "M+-tT";
						rollInfo.rerollBelow = Integer.parseInt(st.nextToken(parseChars));
					}
					case 'M' -> {
						parseChars = "m+-tT";
						rollInfo.rerollAbove = Integer.parseInt(st.nextToken(parseChars));
					}
					case '+' -> {
						parseChars = "tT";
						rollInfo.modifier = Integer.parseInt(st.nextToken(" "));
					}
					case '-' -> {
						parseChars = "tT";
						rollInfo.modifier = -Integer.parseInt(st.nextToken(" "));
					}
					case 't' -> {
						parseChars = "T";
						rollInfo.totalFloor = Integer.parseInt(st.nextToken(" "));
					}
					case 'T' -> {
						parseChars = "t";
						rollInfo.totalCeiling = Integer.parseInt(st.nextToken(" "));
					}
					default -> {
						Logging.errorPrint("Bizarre dice parser error in '" + rollString + "': not a valid delimiter");
						return "Bad roll parsing in '" + rollString + "': invalid delimiter '" + tok.charAt(0) + "'.";
					}
				}
			}
		}

		catch (NumberFormatException ex)
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Bad roll string in '" + rollString + "': " + ex, ex);
			}
			return "Bad roll string in '" + rollString + "': " + ex;
		}
		return "";
	}

	/**
	 * Private constructor for use only when validating a roll string. 
	 */
	private RollInfo()
	{
	}

	/**
	 * Construct a {@code RollInfo} from a string.  The
	 * rules:<ol>
	 *
	 * <li>Optional positive integer, <var>times</var>.</li>
	 *
	 * <li>Literal 'd' followed by positive integer,
	 * <var>sides</var>.</li>
	 *
	 * <li>Optional literal '/' followed by positive integer,
	 * <var>keepTop</var>, or literal '\' followed by positive
	 * integer, <var>keepBottom</var>, or literal '|' followed by
	 * comma-separated list of postitive integers,
	 * <var>keepList</var> (1-indexed after dice have been
	 * sorted).</li>
	 *
	 * <li>Optional literal 'm' (minimum) followed by positive
	 * integer, <var>rerollAbove</var>, or literal 'M' (maximum)
	 * followed by postive integer, <var>rerollBelow</var>.</li>
	 *
	 * <li>Optional literal '+' or '-' followed by positive
	 * integer, <var>modifier</var>.</li>
	 *
	 * <li>Optional literal 't' followed by positive integer,
	 * <var>totalFloor</var>, or literal 'T' followed by a
	 * positive *integer, <var>totalCeiling</var>.</li>
	 *
	 * </ol> Unlike previous versions of this method, it is
	 * <strong>case-sensitive</strong> with respect to the
	 * alphabetic characters, e.g., only {@code d}
	 * (lower-case) is now valid, not also {@code D}
	 * (upper-case).  This is to accommodate the expanded ways to
	 * roll.
	 *
	 * @param rollString String compact representation of dice rolls
	 */
	public RollInfo(final String rollString)
	{
		String errMsg = RollInfo.parseRollInfo(this, rollString);
		if (!StringUtils.isBlank(errMsg))
		{
			Logging.errorPrint(errMsg);
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder(50);

		if (times > 0)
		{
			buf.append(times);
		}

		buf.append('d').append(sides);

		while (keepList != null) // let break work
		{
			int p;
			int i;

			for (i = 0; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}

			if (i == times) // all false
			{
				Logging.errorPrint("Bad rolls: nothing to keep!");

				return "";
			}

			// Note the ordering: by testing for bottom
			// first, we can also test if all the dice are
			// all to be kept, and drop the
			// top/bottom/list specification completely.
			// First test for bottom
			for (i = 0; i < times; ++i)
			{
				if (!keepList[i])
				{
					break;
				}
			}

			if (i == times)
			{
				break; // all true
			}

			p = i;

			for (; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}

			if ((p > 0) && (i == times))
			{
				buf.append('\\').append(p);

				break;
			}

			// Second test for top
			for (i = 0; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}

			p = i;

			for (; i < times; ++i)
			{
				if (!keepList[i])
				{
					break;
				}
			}

			if ((p > 0) && (i == times))
			{
				buf.append('/').append((times - p));

				break;
			}

			// Finally, we have a list
			buf.append('|');

			boolean first = true;

			for (i = 0; i < times; ++i)
			{
				if (!keepList[i])
				{
					continue;
				}

				if (first)
				{
					first = false;
				}
				else
				{
					buf.append(',');
				}

				buf.append(i + 1);
			}
		}

		if (rerollBelow != Integer.MIN_VALUE)
		{
			buf.append('m').append(rerollBelow);
		}

		if (rerollAbove != Integer.MAX_VALUE)
		{
			buf.append('M').append(rerollAbove);
		}

		if (modifier > 0)
		{
			buf.append('+').append(modifier);
		}
		else if (modifier < 0)
		{
			buf.append('-').append(-modifier);
		}

		if (totalFloor != Integer.MIN_VALUE)
		{
			buf.append('t').append(totalFloor);
		}

		if (totalCeiling != Integer.MAX_VALUE)
		{
			buf.append('T').append(totalCeiling);
		}

		return buf.toString();
	}

}
