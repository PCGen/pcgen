package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with SCHOOL Token
 */
public class SchoolToken implements SpellLstToken {

	public String getTokenName() {
		return "SCHOOL";
	}

	public boolean parse(Spell spell, String value) {
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);
		String token;

		while (aTok.hasMoreTokens()) {
			token = aTok.nextToken();
			spell.addSchool(token);
			SettingsHandler.getGame().addToSchoolList(token);
		}
		return true;
	}
}
