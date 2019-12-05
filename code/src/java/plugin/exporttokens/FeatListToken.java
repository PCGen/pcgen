package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityListToken;

/**
 * {@code FeatListToken} deals with FEATLIST Token
 */
public class FeatListToken extends AbilityListToken
{

    /**
     * Get the TOKENNAME
     *
     * @return TOKENNAME
     */
    @Override
    public String getTokenName()
    {
        return "FEATLIST";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        // Skip the ABILITYLIST token itself
        final String tokenString = aTok.nextToken();

        return getTokenForCategory(pc, aTok, tokenString, AbilityCategory.FEAT);
    }

}
