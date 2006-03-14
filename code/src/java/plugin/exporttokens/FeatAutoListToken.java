package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatListToken;

/**
 * @author karianna
 * Class deals with FEATAUTOLIST Token
 */
public class FeatAutoListToken extends FeatListToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "FEATAUTOLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getDelimiter(String)
	 */
	protected String getDelimiter(final String tokenSource)
	{
		return tokenSource.substring(12);
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getFeatList(PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.featAutoList();
	}
}
