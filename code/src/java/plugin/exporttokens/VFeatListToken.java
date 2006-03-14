package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatListToken;

/**
 * <code>VFeatListToken</code> deals with VFEATLIST output token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author karianna
 * @version $Revision$
 */
public class VFeatListToken extends FeatListToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "VFEATLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getFeatList(PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.getVirtualFeatList();
	}

	/**
	 * @see pcgen.io.exporttoken.FeatListToken#getDelimiter(String)
	 */
	protected String getDelimiter(final String tokenSource)
	{
		return tokenSource.substring(9);
	}

}
