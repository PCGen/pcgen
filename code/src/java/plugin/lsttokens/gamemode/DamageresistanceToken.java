package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DAMAGERESISTANCE Token
 */
public class DamageresistanceToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "DAMAGERESISTANCE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setDamageResistanceText(value);
        return true;
    }
}
