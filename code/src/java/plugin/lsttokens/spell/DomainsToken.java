package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLoader;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "DOMAINS";
	}

	public boolean parse(Spell spell, String value)
	{
		if (value.equals(".CLEAR"))
		{
			// 514 abbreviation cleanup
//			Logging.errorPrint(".CLEAR is deprecated in " + getTokenName()
//				+ " because it has side effects on CLASSES:");
//			Logging.errorPrint("  please use .CLEARALL to clear only DOMAINS");
			spell.clearLevelInfo();
			return true;
		}
		else if (value.equals(".CLEARALL"))
		{
			spell.clearLevelInfo("DOMAIN");
			return true;
		}
		try
		{
			SpellLoader.setLevelList(spell, "DOMAIN", value);
			return true;
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error in DOMAIN token: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
