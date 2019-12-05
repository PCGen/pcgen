package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUSFEATLEVELSTARTINTERVAL Token
 */
public class BonusfeatlevelstartintervalToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "BONUSFEATLEVELSTARTINTERVAL";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setBonusFeatLevels(value);
        return true;
    }
}
