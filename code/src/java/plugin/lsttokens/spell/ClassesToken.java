package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLoader;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "CLASSES";
	}

	public boolean parse(Spell spell, String value)
	{
		if (value.equals(".CLEAR"))
		{
			Logging.errorPrint(".CLEAR was removed in "
				+ getTokenName() + " because it has side effects on DOMAINS:");
			Logging
				.errorPrint("Use .CLEARALL to clear only CLASSES");
			return false;
		}
		else if (value.equals(".CLEARALL"))
		{
			spell.clearLevelInfo("CLASS");
			return true;
		}
		try
		{
			SpellLoader.setLevelList(spell, "CLASS", value);
			return true;
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error in CLASSES token: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
