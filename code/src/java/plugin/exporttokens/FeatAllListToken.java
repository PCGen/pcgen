package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;

/**
 * @author karianna
 * Class deals with FEATALLLIST Token
 */
public class FeatAllListToken extends AbilityAllListToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "FEATALLLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		// Skip the token itself
		final String tokenString = aTok.nextToken();
		final AbilityCategory aCategory = SettingsHandler.getGame()
			.getAbilityCategory("FEAT");

		return getTokenForCategory(pc, aTok, tokenString, aCategory);
	}
}
