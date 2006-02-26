package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with TARGETAREA Token
 */
public class TargetareaToken implements SpellLstToken {

	public String getTokenName() {
		return "TARGETAREA";
	}

	public boolean parse(Spell spell, String value) {
		spell.setTarget(value);
		return true;
	}
}
