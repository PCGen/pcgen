package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUSSTATLEVELSTARTINTERVAL Token
 */
public class BonusstatlevelstartintervalToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "BONUSSTATLEVELSTARTINTERVAL";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setBonusStatLevels(value);
        return true;
    }
}
