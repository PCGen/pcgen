/*
 * Copyright 2008-16 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.base.util.OneToOneMap;

/**
 * ParsingSeparator is a relative of StringTokenizer that is aware of certain
 * types of grouping characters, so that each item returned is well structured
 * relative to the grouping characters. Note that a ParsingSeparator without any
 * grouping pairs will behave exactly like StringTokenizer.
 * 
 * As an example, if a base String was "a,b(c,d)", a naive StringTokenizer would
 * return "a", "b(c", and "d)". ParsingSeparator, if loaded with a grouping pair
 * of '(' and ')' would return "a" and "b(c,d)". It does this because it
 * recognizes that the comma that is within the grouping pair is not a top level
 * separator.
 * 
 * Note that it is possible to have a grouping pair of matching characters - as
 * an example, ParsingSeparator can handle quotes as the open and close
 * characters, even though they are the same character.
 */
public class ParsingSeparator implements Iterator<String>
{

	/**
	 * The "base" String that this ParsingSeparator is separating into separate
	 * components.
	 */
	private final String baseString;

	/**
	 * The Character (stored as a String) that this ParsingSeparator is using to
	 * split the base String.
	 */
	private final String separator;

	/**
	 * Contains the "grouping pairs" for this ParsingSeparator. These are the
	 * characters that indicate beginning and end of subsections that should be
	 * combined and returned in one group. An example of these might be '(' and
	 * ')'.
	 */
	private OneToOneMap<String, String> groupingPairs;

	/**
	 * The underlying StringTokenizer used to naively split the base String
	 */
	private StringTokenizer baseTokenizer;

	/**
	 * Indicates there is an unconsumed blank String at the end of the base
	 * String. This can occur if there is a separator character that ends the
	 * base String.
	 */
	private boolean unconsumedEndBlank = false;

	/**
	 * Indicates if the ParsingSeparator has been started. Certain capabilities
	 * (such as adding Grouping Pairs) is prohibited once analysis is started.
	 */
	private boolean started = false;

	/**
	 * Constructs a new ParsingSeparator from the given base String and
	 * separator character.
	 * 
	 * @param baseString
	 *            The base String that will be parsed and separated based on the
	 *            given separator and defined grouping pairs
	 * @param separator
	 *            The separator for this ParsingSeparator that indicates a new
	 *            section of the String
	 */
	public ParsingSeparator(String baseString, char separator)
	{
		this.baseString = Objects.requireNonNull(baseString);
		this.separator = Character.toString(separator);
	}

	/**
	 * Conditionally adds a new Grouping Pair to this ParsingSeparator.
	 * 
	 * The pair will not be added if hasNext() or next() has already been called
	 * on this ParsingSeparator.
	 * 
	 * The pair will not be added if either character already exists in any
	 * grouping pair previously added to this ParsingSeparator (as start or
	 * ending member). Note that this does NOT prevent the user of an identical
	 * start and end character. For example, ignoring separator characters in
	 * embedded quotes is possible.
	 * 
	 * @param start
	 *            The starting character for a grouping pair
	 * @param end
	 *            The ending character for a grouping pair
	 * @throws IllegalStateException
	 *             if the given grouping pair cannot be added to this
	 *             ParsingSeparator
	 */
	public void addGroupingPair(char start, char end)
	{
		//Can't change after we've started parsing
		if (started)
		{
			throw new IllegalStateException(
				"Cannot add grouping pairs to the ParsingSeparator "
					+ "once parsing has been started");
		}
		if (groupingPairs == null)
		{
			groupingPairs = new OneToOneMap<>();
		}
		String startString = Character.toString(start);
		String endString = Character.toString(end);
		//Can't use a character as start and end
		if (groupingPairs.containsKey(endString)
			|| groupingPairs.containsValue(startString))
		{
			throw new IllegalStateException(
				"Cannot add grouping pairs to the ParsingSeparator "
					+ "if a key or value has already been added as a separator");
		}
		String oldEnd = groupingPairs.get(startString);
		String oldStart = groupingPairs.getKeyFor(endString);
		//Can't use characters twice
		if ((oldStart != null) && !oldStart.equals(startString)
			|| (oldEnd != null) && !oldEnd.equals(endString))
		{
			throw new IllegalStateException(
				"Cannot add grouping pairs to the ParsingSeparator "
					+ "if a key or value has already been added as a separator");
		}
		groupingPairs.put(startString, endString);
	}

	/**
	 * Indicates if this ParsingSeparator (acting as an Iterator) has an
	 * additional item that can be retrieved with a call to the next() method.
	 * If this returns true, next() will not throw a NoSuchElementException. If
	 * this returns false, calling next() will throw a NoSuchElementException.
	 * 
	 * @return a boolean indicating if this ParsingSeparator has an additional
	 *         item that can be retrieved with a call to the next() method
	 */
	@Override
	public boolean hasNext()
	{
		if (!started)
		{
			start();
		}
		return unconsumedEndBlank || baseTokenizer.hasMoreTokens();
	}

	/**
	 * 
	 * If hasNext() returns true, this will not throw a NoSuchElementException.
	 * If hasNext() returns false, calling this method will throw a
	 * NoSuchElementException. Note that a true response from hasNext() will not
	 * prevent this method from throwing a GroupingMismatchException.
	 * 
	 * @return A String containing the next section to be returned from the base
	 *         String.
	 */
	@SuppressWarnings({"PMD.CyclomaticComplexity",
		"PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
		"PMD.NPathComplexity"})
	@Override
	public String next()
	{
		if (!started)
		{
			start();
		}
		if (!hasNext())
		{
			throw new NoSuchElementException();
		}
		if (unconsumedEndBlank)
		{
			unconsumedEndBlank = false;
			return "";
		}
		StringBuilder compilation = new StringBuilder(baseString.length());
		Deque<String> expected = new ArrayDeque<>();
		while (baseTokenizer.hasMoreTokens())
		{
			String currentToken = baseTokenizer.nextToken();
			if (separator.equals(currentToken) && expected.isEmpty())
			{
				unconsumedEndBlank = !baseTokenizer.hasMoreTokens();
				return compilation.toString();
			}
			compilation.append(currentToken);
			if (groupingPairs == null)
			{
				continue;
			}
			String matchedOpening = groupingPairs.getKeyFor(currentToken);
			boolean possibleClose = (matchedOpening != null);
			String matchedClosing = groupingPairs.get(currentToken);
			boolean possibleOpen = (matchedClosing != null);
			if (possibleOpen && possibleClose)
			{
				possibleOpen = expected.isEmpty()
					|| !expected.peek().equals(matchedClosing);
				possibleClose = !possibleOpen;
			}
			if (possibleOpen)
			{
				expected.push(matchedClosing);
			}
			else if (possibleClose)
			{
				if (expected.isEmpty())
				{
					throw new GroupingMismatchException(baseString
						+ " did not have " + matchedOpening + "before "
						+ currentToken + ": " + compilation);
				}
				String closeWanted = expected.pop();
				if (!currentToken.equals(closeWanted))
				{
					throw new GroupingMismatchException(
						baseString + " did not have " + closeWanted
							+ " but instead encountered " + currentToken
							+ " in: " + compilation);
				}
			}
		}
		if (!expected.isEmpty())
		{
			throw new GroupingMismatchException(baseString
				+ " reached end of String while attempting to match: "
				+ expected.pop());
		}
		return compilation.toString();
	}

	private void start()
	{
		String separatorString;
		if (groupingPairs == null)
		{
			separatorString = separator;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			groupingPairs.keySet().forEach(item -> sb.append(item));
			groupingPairs.values().forEach(item -> sb.append(item));
			separatorString = sb.append(separator).toString();
		}
		baseTokenizer = new StringTokenizer(baseString, separatorString, true);
		started = true;
	}

	/**
	 * Not supported on ParsingSeparator.
	 * 
	 * @throws UnsupportedOperationException
	 *             unconditionally (remove is not supported)
	 */
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException(
			"ParsingSeparator does nor support remove()");
	}

	/**
	 * The exception thrown by a ParsingSeparator when there is a mismatch of
	 * grouping characters in the incoming String
	 */
	public static class GroupingMismatchException extends IllegalStateException
	{

		/**
		 * Constructs a new GroupingMismatchException with the given message.
		 * 
		 * @param message
		 *            The message used to indicate the exact issue triggering
		 *            the GroupingMismatchException
		 */
		public GroupingMismatchException(String message)
		{
			super(message);
		}

	}
}
