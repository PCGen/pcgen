package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.core.UnitSet;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.util.Logging;

/**
 * Class deals with UNITSET Token
 */
public class UnitsetToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "UNITSET";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            SimpleLoader<UnitSet> unitSetLoader = new SimpleLoader<>(UnitSet.class);
            unitSetLoader.parseLine(gameMode.getModeContext(), value, source);
            return true;
        } catch (Exception e)
        {
            Logging.errorPrint(e.getMessage());
            return false;
        }
    }
}
