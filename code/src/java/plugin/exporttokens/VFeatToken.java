package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatToken;

/**
 * <code>VFeatToken</code> deals with VFEAT output token.
 *
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/07 15:40:51 $
 *
 * @author karianna
 * @version $Revision: 1.4 $
 */
public class VFeatToken extends FeatToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return "VFEAT";
	}

	/**
	 * @see pcgen.io.exporttoken.FeatToken#getFeatList(PlayerCharacter)
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		return pc.getVirtualFeatList();
	}
}
