/*
 * Language.java
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
 * Created on November 18, 2001, 9:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.list.LanguageList;
import pcgen.cdom.reference.CDOMDirectSingleRef;


/**
 * <code>Language</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Language extends PObject implements Comparable<Object>
{
	public static final CDOMReference<LanguageList> STARTING_LIST = new CDOMDirectSingleRef<LanguageList>(
			new LanguageList());

	/**
	 * Compares keyName only
	 * @param o1
	 * @return int
	 */
	@Override
	public int compareTo(final Object o1)
	{
		/*
		 * TODO This behavior of compareTo could be improved... need to figure
		 * out why this is present in the code (where a language should be
		 * compared to a String) and get RID of it... explicitly grab the key
		 * name and compare the strings. -thpr 06/18/05
		 */
		if (o1 instanceof String)
		{
			return keyName.compareToIgnoreCase((String) o1);
		}

		return keyName.compareToIgnoreCase(((Language) o1).keyName);
	}

	/**
	 * Compares keyName only
	 * @param o1
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object o1)
	{
		/*
		 * TODO This is behavior of equals could be improved... need to figure
		 * out why this is present in the code (where a language should be
		 * compared to a String) and get RID of it... explicitly grab the key
		 * name and call .equals() on the strings. -thpr 06/18/05
		 */
		if (o1 == null)
		{
			return false;
		}
		if (o1 instanceof String)
		{
			return keyName.equals(o1);
		}
		if (!o1.getClass().equals(Language.class)) {
			return false;
		}

		return keyName.equals(((Language) o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
