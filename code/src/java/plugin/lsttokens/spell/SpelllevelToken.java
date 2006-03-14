package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLLEVEL Token
 */
public class SpelllevelToken implements SpellLstToken {

	public String getTokenName() {
		return "SPELLLEVEL";
	}

	public boolean parse(Spell spell, String value) {
	    //TODO: When will this deprecated tag be removed? Or will it remain indefinitely?
		Logging.errorPrint("Warning: tag 'SPELLLEVEL' has been deprecated. Use CLASSES or DOMAINS tag instead.");

		final StringTokenizer slTok = new StringTokenizer(value, "|");

		while (slTok.countTokens() >= 3) {
			final String typeString = slTok.nextToken();
			final String mainString = slTok.nextToken();
			spell.setLevelInfo(typeString + "|" + mainString, slTok.nextToken());
		}
		return true;
	}
}
