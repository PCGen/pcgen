package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CURRENCYUNITABBREV Token
 */
public class CurrencyunitabbrevToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "CURRENCYUNITABBREV";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setCurrencyUnitAbbrev(value);
        return true;
    }
}
