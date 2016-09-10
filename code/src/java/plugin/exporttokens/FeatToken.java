package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacterImpl;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * @author karianna
 *
 * Class deals with FEAT Token
 */
public class FeatToken extends AbilityToken
{

	/**
	 * Get the TOKENNAME
	 * @return TOKENNAME
	 */
	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String,
	 *      pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacterImpl pc,
		ExportHandler eh)
	{

		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
			AbilityCategory.FEAT);
	}
}
