/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PObject.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.analysis;

import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.Logging;

public class LanguageSupport
{

	/**
	 * Returns a list of Language objects from a string of choices.  The method
	 * will expand "ALL" or "ANY" into all languages and TYPE= into all
	 * languages of that type
	 * @param stringList Pipe separated list of language choices
	 * @return Sorted list of Language objects
	 */
	public static SortedSet<Language> getLanguagesFromString(final String stringList)
	{
		SortedSet<Language> list = new TreeSet<Language>();
	
		final StringTokenizer tokens = new StringTokenizer(stringList,	"|", false);
	
		AbstractReferenceContext ref = Globals.getContext().ref;
		while (tokens.hasMoreTokens())
		{
			final String aLang = tokens.nextToken();
			if ("ALL".equals(aLang))
			{
				list.addAll(ref.getConstructedCDOMObjects(Language.class));
				return list;
			}
			else if (aLang.startsWith("TYPE=") || aLang.startsWith("TYPE."))
			{
				list.addAll(Globals.getPObjectsOfType(ref
						.getConstructedCDOMObjects(Language.class), aLang
						.substring(5)));
			}
			else
			{
				Language languageKeyed = ref
						.silentlyGetConstructedCDOMObject(Language.class, aLang);
				if (languageKeyed == null)
				{
					Logging.debugPrint("Someone expected Language: " + aLang + " to exist: it doesn't");
				}
				else
				{
					list.add(languageKeyed);
				}
			}
		}
		return list;
	}

}
