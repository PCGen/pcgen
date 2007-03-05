package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with SPELLLEVEL Token
 */
public class SpelllevelToken implements SpellLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer slTok = new StringTokenizer(value, "|");

		while (slTok.countTokens() >= 3)
		{
			final String typeString = slTok.nextToken();
			final String mainString = slTok.nextToken();
			spell
				.setLevelInfo(typeString + "|" + mainString, slTok.nextToken());
		}
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "Warning: tag 'SPELLLEVEL' has been deprecated. Use CLASSES or DOMAINS tag instead.";
	}
}
