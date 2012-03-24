package plugin.exporttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityListToken;

/**
 * <code>VFeatListToken</code> deals with VFEATLIST output token.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author karianna
 * @version $Revision$
 */
public class VFeatListToken extends AbilityListToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VFEATLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
						   ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		// Skip the token itself
		final String tokenString = aTok.nextToken();
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory("FEAT");

		return getTokenForCategory(pc, aTok, tokenString, aCategory);
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityListToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected List<Ability> getAbilityList(PlayerCharacter pc,
										   AbilityCategory aCategory)
	{
		return new ArrayList<Ability>(pc.getAbilityList(aCategory, Nature.VIRTUAL));
	}

}
