package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.SpellFilter;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "KNOWNSPELLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer pipeTok;

		if (value.startsWith(".CLEAR"))
		{
			pcclass.clearKnownSpellsList();

			if (".CLEAR".equals(value))
			{
				Logging.errorPrint(getTokenName()
					+ " uses deprecated syntax.  "
					+ "Use .CLEARALL (not .CLEAR) to clear the values");
				return true;
			}
			else if (".CLEARALL".equals(value))
			{
				return true;
			}

			String rest;
			if (value.startsWith(".CLEARALL|"))
			{
				rest = value.substring(7);
			}
			else
			{
				Logging.errorPrint("Invalid KNOWNSPELLS Syntax using .CLEAR");
				Logging
					.errorPrint("Please separate .CLEARALL from the rest of the token with a |");
				rest = value.substring(6);
			}
			pipeTok = new StringTokenizer(rest, Constants.PIPE);
		}
		else
		{
			pipeTok = new StringTokenizer(value, Constants.PIPE);
		}

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();
			StringTokenizer commaTok = new StringTokenizer(totalFilter, ",");
			SpellFilter sf = new SpellFilter();

			// must satisfy all elements in a comma delimited list
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				/*
				 * CONSIDER Want to add deprecation during 5.11 alpha cycle,
				 * thus, can be removed in 5.14 or 6.0 - thpr 11/4/06
				 */
				if (filterString.startsWith("LEVEL."))
				{
					// Logging.errorPrint("LEVEL. format deprecated in
					// KNOWNSPELLS. Please use LEVEL=");
					filterString = "LEVEL=" + filterString.substring(6);
				}
				if (filterString.startsWith("TYPE."))
				{
					// Logging.errorPrint("TYPE. format deprecated in
					// KNOWNSPELLS. Please use TYPE=");
					filterString = "TYPE=" + filterString.substring(5);
				}

				if (filterString.startsWith("LEVEL="))
				{
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					sf.setSpellLevel(Integer
						.parseInt(filterString.substring(6)));
				}
				else if (filterString.startsWith("TYPE="))
				{
					// if it starts with TYPE=, compare it to the spells type
					// list
					sf.setSpellType(filterString.substring(5));
				}
				else
				{
					// otherwise it must be the spell's name
					sf.setSpellName(filterString);
				}
			}
			if (sf.isEmpty())
			{
				Logging.errorPrint("Illegal (empty) KNOWNSPELLS Filter: "
					+ totalFilter);
			}
			pcclass.addKnownSpell(sf);
		}
		return true;
	}
}
