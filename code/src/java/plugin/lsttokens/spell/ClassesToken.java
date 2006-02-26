package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLoader;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken implements SpellLstToken {

	public String getTokenName() {
		return "CLASSES";
	}

	public boolean parse(Spell spell, String value) {
		try {
			SpellLoader.setLevelList(spell, "CLASS", value);
			return true;
		}
		catch(Exception e) {
			Logging.errorPrint("Error in CLASSES token: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
