package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with DESCRIPTOR Token
 */
public class DescriptorToken implements SpellLstToken {

	public String getTokenName() {
		return "DESCRIPTOR";
	}

	public boolean parse(Spell spell, String value) {
		final StringTokenizer tok = new StringTokenizer(value, "|", false);

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			spell.addDescriptor(token);
			Globals.addSpellDescriptorSet(token);
		}
		return true;
	}
}
