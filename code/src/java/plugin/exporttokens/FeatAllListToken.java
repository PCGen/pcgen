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
import pcgen.io.exporttoken.AbilityListToken;

/**
 * Class deals with FEATALLLIST Token
 */
public class FeatAllListToken extends AbilityListToken
{

    @Override
    public String getTokenName()
    {
        return "FEATALLLIST"; //$NON-NLS-1$
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        // Skip the token itself
        final String tokenString = aTok.nextToken();

        return getTokenForCategory(pc, aTok, tokenString, AbilityCategory.FEAT);
    }

    @Override
    protected MapToList<Ability, CNAbility> getAbilityList(PlayerCharacter pc, final AbilityCategory aCategory)
    {
        final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<>();
        Collection<AbilityCategory> allCats = SettingsHandler.getGame().getAllAbilityCategories();
        for (AbilityCategory aCat : allCats)
        {
            if (aCat.getParentCategory().equals(aCategory))
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
            }
        }
        return listOfAbilities;
    }

}
