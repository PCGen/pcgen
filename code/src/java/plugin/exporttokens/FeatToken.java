package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * @author karianna
 *
 * Class deals with FEAT Token
 */
public class FeatToken extends AbilityToken
{

	/** Token Name */
	public static final String TOKENNAME = "FEAT";

	/**
	 * Get the TOKENNAME
	 * @return TOKENNAME
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String,
	 *      pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{

		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();
		final AbilityCategory aCategory = SettingsHandler.getGame()
			.getAbilityCategory("FEAT");

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
			aCategory);
	}
}
