package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLoader;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainToken implements SpellLstToken {

	public String getTokenName() {
		return "DOMAINS";
	}

	public boolean parse(Spell spell, String value) {
		try {
			SpellLoader.setLevelList(spell, "DOMAIN", value);
			return true;
		}
		catch(Exception e) {
			Logging.errorPrint("Error in DOMAIN token: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
