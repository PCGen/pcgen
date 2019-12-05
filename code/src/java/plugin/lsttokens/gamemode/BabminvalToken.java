package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABMINVAL Token
 */
public class BabminvalToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "BABMINVAL";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            gameMode.setBabMinVal(Integer.parseInt(value));
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
