package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.FeatToken;

/**
 * @author karianna
 * Class deals with FEATALL Token
 */
public class DFeatAllToken extends FeatToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "FEATALL";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		setVisibility(FEAT_ALL);
		return super.getToken(tokenSource, pc, eh);
	}

	/**
	 * @see pcgen.io.exporttoken.FeatToken#getFeatList(pcgen.core.PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.featAutoList();
	}
}
