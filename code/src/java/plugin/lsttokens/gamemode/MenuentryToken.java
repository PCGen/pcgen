package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with MENUENTRY Token
 */
public class MenuentryToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "MENUENTRY";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setModeName(value.replace('|', '\n'));
        return true;
    }
}
