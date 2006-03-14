package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with ITEM Token
 */
public class ItemToken implements SpellLstToken {

	public String getTokenName() {
		return "ITEM";
	}

	public boolean parse(Spell spell, String value) {
		spell.setCreatableItem(value);
		return true;
	}
}
