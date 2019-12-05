package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.WieldCategoryLoader;
import pcgen.util.Logging;

/**
 * Class deals with WIELDCATEGORY Token
 */
public class WieldcategoryToken implements GameModeLstToken
{

    /**
     * Get token name
     *
     * @return token name
     */
    @Override
    public String getTokenName()
    {
        return "WIELDCATEGORY";
    }

    /**
     * Parse WIELDCATEGORY token
     *
     * @param gameMode
     * @param value
     * @return true if successful
     */
    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            WieldCategoryLoader catDiceLoader = new WieldCategoryLoader();
            catDiceLoader.parseLine(gameMode, "WIELDCATEGORY:" + value, source);
            return true;
        } catch (Exception e)
        {
            Logging.errorPrint(e.getMessage());
            return false;
        }

    }
}
