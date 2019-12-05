package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYORDER Token
 */
public class DisplayorderToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "DISPLAYORDER";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setDisplayOrder(value);
        return true;
    }
}
