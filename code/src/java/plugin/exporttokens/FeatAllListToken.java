package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatListToken;

/**
 * @author karianna
 * Class deals with FEATALLLIST Token
 */
public class FeatAllListToken extends FeatListToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "FEATALLLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getDelimiter(String tokenSource)
	 */
	protected String getDelimiter(final String tokenSource)
	{
		return tokenSource.substring(11);
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getFeatList(PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.aggregateVisibleFeatList();
	}
}
