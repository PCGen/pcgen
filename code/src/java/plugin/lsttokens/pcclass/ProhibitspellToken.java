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
public class ProhibitspellToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		SpellProhibitor spellProb = new SpellProhibitor();

		final String spString = aTok.nextToken().toUpperCase();

		final StringTokenizer elements = new StringTokenizer(spString, ".",
				false);
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
						Logging
								.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
										+ aValue + "'");
					}
					else
					{
						if (type.equals(ProhibitedSpellType.SPELL))
						{
							for (String spell : aValue.split(","))
							{
								spellProb.addValue(spell);
							}
						}
						else
						{
							spellProb.addValue(aValue);
						}
					}
				}
			}
		}
		if (spellProb.getType() == null)
		{
			Logging.errorPrint("Illegal PROHIBITSPELL subtag '" + spString
					+ "'");
		}

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken().toUpperCase();

			if (PreParserFactory.isPreReqString(aString))
			{
				try
				{
					final PreParserFactory factory = PreParserFactory
							.getInstance();
					spellProb.addPreReq(factory.parse(aString));
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			else
			{
				Logging.errorPrint("Invalid " + getTokenName() + ": " + value);
				Logging.errorPrint("  PRExxx must be at the END of the Token");
			}
		}
		if (spellProb.getValueList() == null)
		{
			Logging.errorPrint("Invalid Spell Prohibitor, "
					+ "nothing found to prohibit: " + value);
			return false;
		}
		pcclass.setProhibitSpell(spellProb);
		return true;
	}
}
