/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.base;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;

/**
 * ChoiceFilterUtilities is a set of utility methods for use with objects that
 * implement pcgen.cdom.base.PrimitiveChoiceFilter<?>
 * 
 * @see pcgen.cdom.base.PrimitiveChoiceFilter<?>
 */
public final class ChoiceFilterUtilities
{

	/**
	 * A COLLATOR used to sort Strings in a locale-aware method.
	 */
	private static final Collator COLLATOR = Collator.getInstance();

	private ChoiceFilterUtilities()
	{
		// Cannot construct utility class
	}

	/**
	 * A Comparator to consistently sort PrimitiveChoiceFilter<?> objects. This
	 * is done using the ChoiceFilterUtilities.compareChoiceSets method.
	 */
	public static final Comparator<PrimitiveChoiceFilter<?>> FILTER_SORTER = new Comparator<PrimitiveChoiceFilter<?>>()
	{
		public int compare(PrimitiveChoiceFilter<?> lstw1,
				PrimitiveChoiceFilter<?> lstw2)
		{
			return COLLATOR.compare(lstw1.getLSTformat(), lstw2.getLSTformat());
		}
	};

	/**
	 * Concatenates the LST format of the given Collection of
	 * PrimitiveChoiceFilter<?> objects into a String using the separator as
	 * the delimiter.
	 * 
	 * The LST format for each PrimitiveChoiceFilter<?> is determined by
	 * calling the getLSTformat() method on the PrimitiveChoiceFilter<?>.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 * 
	 * Ownership of the Collection provided to this method is not transferred
	 * and this constructor will not modify the given Collection.
	 * 
	 * @param pcfCollection
	 *            An Collection of PrimitiveChoiceFilter<?> objects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the LST format of the
	 *         given Collection of PrimitiveChoiceFilter<?> objects
	 */
	public static String joinLstFormat(
			Collection<? extends PrimitiveChoiceFilter<?>> pcfCollection,
			String separator)
	{
		if (pcfCollection == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(
				pcfCollection.size() * 10);

		boolean needjoin = false;

		for (PrimitiveChoiceFilter<?> pcf : pcfCollection)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(pcf.getLSTformat());
		}

		return result.toString();
	}

}
