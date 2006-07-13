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


/**
 * <code>Language</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Language extends PObject implements Comparable<Object>
{
	/** Does this language indicate access to all languages. */
	private boolean isAllLang = false;

	/** The language instance representing access to all bonus languages.
	 * We keep this outside of the global language list so that it does
	 * not appear as a real language. */
	private static Language allLanguage;

	/**
	 * Default constructor.
	 */
	public Language()
	{
		super();
	}

	/**
	 * Constructor for use when creating the ALL placehoder language.
	 *
	 * @param isAllLang Is this the access to all languages placeholder.
	 */
	public Language(boolean isAllLang)
	{
		super();
		this.isAllLang = isAllLang;
	}

	/**
	 * Retrieve the language instance representing access to all bonus languages.
	 * @return Returns the allLanguage.
	 */
	public static final synchronized Language getAllLanguage()
	{
		if (allLanguage == null)
		{
			allLanguage = new Language(true);
			allLanguage.setName("ALL");
			allLanguage.setKeyName("ALL");
		}
		return allLanguage;
	}

	/**
	 * Compares keyName only
	 * @param o1
	 * @return int
	 */
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
	public boolean equals(final Object o1)
	{
		/*
		 * TODO This is behavior of equals could be improved... need to figure
		 * out why this is present in the code (where a language should be
		 * compared to a String) and get RID of it... explicitly grab the key
		 * name and call .equals() on the strings. -thpr 06/18/05
		 */
		if (o1 instanceof String)
		{
			return keyName.equals(o1);
		}

		return keyName.equals(((Language) o1).keyName);
	}

	/**
	 * @return Returns the isAllLang.
	 */
	public final boolean isAllLang()
	{
		return isAllLang;
	}

	/**
	 * Hashcode of the keyName
	 * @return hash code
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
