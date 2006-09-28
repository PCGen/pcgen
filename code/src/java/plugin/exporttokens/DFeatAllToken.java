package plugin.exporttokens;

import java.util.List;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.FeatToken;
import java.util.ArrayList;
import pcgen.core.Ability;

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
	protected List<Ability> getFeatList(PlayerCharacter pc)
	{
		List<Ability> ret = new ArrayList<Ability>();
		ret.addAll(pc.getRealAbilityList(AbilityCategory.FEAT));
		ret.addAll(pc.featAutoList());
		ret.addAll(pc.getVirtualFeatList());
		return ret;
	}
}
