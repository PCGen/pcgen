package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;

/**
 * @author karianna
 * Class deals with FEATAUTO Token
 */
public class FeatAutoToken extends AbilityAutoToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "FEATAUTO";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();
		final AbilityCategory aCategory = SettingsHandler.getGame()
			.getAbilityCategory("FEAT");

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
			aCategory);
	}
}
