package plugin.exporttokens;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.FeatToken;
import pcgen.core.Ability;

/**
 * <code>VFeatToken</code> deals with VFEAT output token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author karianna
 * @version $Revision$
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
	protected List<Ability> getFeatList(PlayerCharacter pc)
	{
		return pc.getVirtualFeatList();
	}
}
