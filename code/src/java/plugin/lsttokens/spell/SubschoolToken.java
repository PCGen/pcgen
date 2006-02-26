package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with SUBSCHOOL Token
 */
public class SubschoolToken implements SpellLstToken {

	public String getTokenName() {
		return "SUBSCHOOL";
	}

	public boolean parse(Spell spell, String value) {
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);
		String token;

		while (aTok.hasMoreTokens()) {
			token = aTok.nextToken();
			spell.addSubschool(token);
		}
		return true;
	}
}
