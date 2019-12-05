package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.cdom.enumeration.Type;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with PLUSCOST Token
 */
public class PluscostToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "PLUSCOST";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        final int idx = value.indexOf('|');

        if (idx <= 0)
        {
            return false;
        }
        Type type = Type.getConstant(value.substring(0, idx).toUpperCase());
        String formula = value.substring(idx + 1);
        gameMode.addPlusCalculation(type, formula);
        return true;
    }
}
