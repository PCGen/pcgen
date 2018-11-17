/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import pcgen.base.lang.StringUtil;

/**
 * A Set of utilities related to pcgen.base.util.MapToList.
 */
public final class TupleUtil
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private TupleUtil()
	{
	}

	/**
	 * Consumes a Stream of Tuple objects and places them into a String in a
	 * right-associative intelligent fashion.
	 * 
	 * Any time the second objects of two Tuples match, the first items are eligible to be
	 * combined using the given separator. If the second objects do not match, then those
	 * Tuples cannot be condensed and the contents of those Tuples will be in different
	 * Strings in the returned List.
	 * 
	 * @param unconverted
	 *            The Stream of Tuples containing the String representations to be
	 *            combined
	 * @param separator
	 *            The separator to use to combine the list of the left side values on the
	 *            given Tuples (combined when the right side value matches)
	 * @return A List of String objects, with the appropriate combinations of left and
	 *         right values
	 */
	public static List<String> arrayLeftAndCombine(
		Stream<Tuple<String, String>> unconverted, char separator)
	{
		HashMapToList<String, String> hml = new HashMapToList<>();
		unconverted.forEach(t -> hml.addToListFor(t.getSecond(), t.getFirst()));
		List<String> results = new ArrayList<String>();
		hml.getKeySet().forEach(right -> results
			.add(StringUtil.join(hml.getListFor(right), separator) + right));
		return results;
	}

}
