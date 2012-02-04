package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * @author karianna
 * Class deals with FEATALL Token
 */
public class FeatAllToken extends AbilityToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "FEATALL";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
						   ExportHandler eh)
	{
		setVisibility(ABILITY_ALL);
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory("FEAT");

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
								   aCategory);
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected List<Ability> getAbilityList(PlayerCharacter pc,
										   final AbilityCategory aCategory)
	{
		List<Ability> ret = new ArrayList<Ability>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				ret.addAll(pc.getRealAbilitiesListAnyCat(aCat));
				ret.addAll(pc.getAutomaticAbilityList(aCat));
				ret.addAll(pc.getVirtualAbilityList(aCat));
			}
		}
		return ret;
	}

}
