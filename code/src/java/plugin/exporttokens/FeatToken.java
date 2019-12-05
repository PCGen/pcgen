package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbilityToken
{

    /**
     * Get the TOKENNAME
     *
     * @return TOKENNAME
     */
    @Override
    public String getTokenName()
    {
        return "FEAT";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {

        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        final String fString = aTok.nextToken();

        return getTokenForCategory(tokenSource, pc, eh, aTok, fString, AbilityCategory.FEAT);
    }
}
