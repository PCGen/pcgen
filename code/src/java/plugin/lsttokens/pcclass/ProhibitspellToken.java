package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken implements PCClassLstToken {

	public String getTokenName() {
		return "PROHIBITSPELL";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		SpellProhibitor spellProb = new SpellProhibitor();
		
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken().toUpperCase();

			if (aString.startsWith("!PRE") || aString.startsWith("PRE"))
			{
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					spellProb.addPreReq(factory.parse(aString) );
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				final StringTokenizer elements = new StringTokenizer(aString, ".", false);
				final String aType = elements.nextToken();

				for (ProhibitedSpellType type : ProhibitedSpellType.values())
				{
					if (type.toString().equalsIgnoreCase(aType))
					{
						spellProb.setType(type);
						while (elements.hasMoreTokens())
						{
							String aValue = elements.nextToken();
							if (type.equals(ProhibitedSpellType.ALIGNMENT)
									&& (!aValue.equals("GOOD"))
									&& (!aValue.equals("EVIL"))
									&& (!aValue.equals("LAWFUL"))
									&& (!aValue.equals("CHAOTIC")))
							{
								Logging.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '" + aValue + "'");
							}
							else
							{
								spellProb.addValue(aValue);
							}
						}
					}
				}
				if (spellProb.getType() == null)
				{
					Logging.errorPrint("Illegal PROHIBITSPELL subtag '" + aString + "'");
				}
			}
		}
		pcclass.setProhibitSpell(spellProb);
		return true;
	}
}
