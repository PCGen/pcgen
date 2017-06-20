package plugin.exporttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;
import pcgen.util.enumeration.View;

/**
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
		setView(View.ALL);
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();

		return getTokenForCategory(tokenSource, pc, eh, aTok, fString,
			AbilityCategory.FEAT);
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected MapToList<Ability, CNAbility> getAbilityList(PlayerCharacter pc,
										   final AbilityCategory aCategory)
	{
		final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		allCats.stream().filter(aCat -> aCat.getParentCategory().equals(aCategory)).forEach(aCat ->
		{
			for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.NORMAL))
			{
				listOfAbilities.addToListFor(cna.getAbility(), cna);
			}
			for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.AUTOMATIC))
			{
				listOfAbilities.addToListFor(cna.getAbility(), cna);
			}
			for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.VIRTUAL))
			{
				listOfAbilities.addToListFor(cna.getAbility(), cna);
			}
		});
		return listOfAbilities;
	}

	@Override
	protected Nature getTargetNature()
	{
		return null;
	}

}
