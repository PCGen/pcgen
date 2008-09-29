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
