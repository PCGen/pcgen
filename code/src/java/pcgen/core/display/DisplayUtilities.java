/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */
package pcgen.core.display;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;

@Deprecated
public final class DisplayUtilities
{
	private DisplayUtilities()
	{
		//Can't instantiate
	}

	/**
	 * Concatenates the Display Name of given Collection of CDOMObjects into a String
	 * using the separator as the delimiter.
	 *
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 *
	 * This method is value-semantic. DisplayUtilities will not maintain a
	 * reference to or modify the given Collection.
	 *
	 * @param cdoCollection
	 *            An Collection of CDOMObjects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the Display Name of the
	 *         given Collection of CDOMObject objects
	 */
	@Deprecated
	public static String joinDisplayName(Collection<? extends CDOMObject> cdoCollection, String separator)
	{
		if (cdoCollection == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(cdoCollection.size() * 10);

		boolean needjoin = false;

		for (CDOMObject obj : cdoCollection)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.getDisplayName());
		}

		return result.toString();
	}

}
