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
 * Class deals with FEATAUTO Token
 */
public class FeatAutoToken extends AbilityToken
{

    @Override
    public String getTokenName()
    {
        return "FEATAUTO";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        final String fString = aTok.nextToken();

        return getTokenForCategory(tokenSource, pc, eh, aTok, fString, AbilityCategory.FEAT);
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
                for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.AUTOMATIC))
                {
                    listOfAbilities.addToListFor(cna.getAbility(), cna);
                }
            }
        }
        return listOfAbilities;
    }

}
