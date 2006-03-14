package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatToken;

/**
 * @author karianna
 * Class deals with FEATAUTO Token
 */
public class FeatAutoToken extends FeatToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "FEATAUTO";
	}

	/**
	 * @see pcgen.io.exporttoken.FeatToken#getFeatList(pcgen.core.PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.featAutoList();
	}
}
