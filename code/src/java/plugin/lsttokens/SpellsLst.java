/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.PCSpell;

/**
 * @author djones4
 *
 */
public class SpellsLst implements GlobalLstToken
{
	public String getTokenName()
	{
		return "SPELLS";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!(obj instanceof Campaign))
		{
			obj.getSpellSupport().addSpells(anInt, createSpellsList(value));
			return true;
		}
		return false;
	}

	/**
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>]
	 * |<spell name>[,<formula for DC>]
	 * |<spell name2>[,<formula2 for DC>]
	 * |PRExxx
	 * |PRExxx
	 *
	 * CASTERLEVEL=<formula> Casterlevel of spells
	 * TIMES=<formula> Cast Times per day, -1=At Will
	 * @param sourceLine Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	private static List<PCSpell> createSpellsList(final String sourceLine)
	{
		List<PCSpell> spellList = new ArrayList<PCSpell>();
		StringTokenizer tok = new StringTokenizer(sourceLine, "|");
		if (tok.countTokens() > 1)
		{
			String spellBook = tok.nextToken();
			String casterLevel = null;
			String times = "1";
			List<String> preParseSpellList = new ArrayList<String>();
			List<Prerequisite> preList = new ArrayList<Prerequisite>();
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				if (token.startsWith("CASTERLEVEL="))
				{
					casterLevel = token.substring(12);
				}
				else if (token.startsWith("TIMES="))
				{
					times = token.substring(6);
				}
				else if (token.startsWith("PRE") || token.startsWith("!PRE"))
				{
					try
					{
						PreParserFactory factory =
								PreParserFactory.getInstance();
						preList.add(factory.parse(token));
					}
					catch (PersistenceLayerException ple)
					{
						Logging.errorPrint(ple.getMessage(), ple);
					}
				}
				else
				{
					preParseSpellList.add(token);
				}
			}
			for (int i = 0; i < preParseSpellList.size(); i++)
			{
				StringTokenizer spellTok =
						new StringTokenizer(preParseSpellList.get(i), ",");
				String name = spellTok.nextToken();
				String dcFormula = null;
				if (spellTok.hasMoreTokens())
				{
					dcFormula = spellTok.nextToken();
				}
				PCSpell spell = new PCSpell();
				spell.setName(name);
				spell.setKeyName(spell.getKeyName());
				spell.setSpellbook(spellBook);
				spell.setCasterLevelFormula(casterLevel);
				spell.setTimesPerDay(times);
				spell.setDcFormula(dcFormula);
				for (Prerequisite prereq : preList)
				{
					spell.addPreReq(prereq);
				}
				spellList.add(spell);
			}
		}
		else
		{
			Logging
				.errorPrint("SPELLS: line minimally requires SPELLS:<spellbook name>|<spell name>");
		}
		return spellList;
	}
}
