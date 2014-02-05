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
		final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<Ability, CNAbility>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				for (Ability a : pc.getAbilityList(aCat, Nature.NORMAL))
				{
					listOfAbilities.addToListFor(a, new CNAbility(aCat, a, Nature.NORMAL));
				}
				for (Ability a : pc.getAbilityList(aCat, Nature.AUTOMATIC))
				{
					listOfAbilities.addToListFor(a, new CNAbility(aCat, a, Nature.AUTOMATIC));
				}
				for (Ability a : pc.getAbilityList(aCat, Nature.VIRTUAL))
				{
					listOfAbilities.addToListFor(a, new CNAbility(aCat, a, Nature.VIRTUAL));
				}
			}
		}
		return listOfAbilities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Nature getTargetNature()
	{
		return null;
	}

}
