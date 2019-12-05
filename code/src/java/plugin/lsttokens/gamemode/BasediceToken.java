package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.cdom.content.BaseDice;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.util.Logging;

/**
 * Class deals with BASEDICE Token
 */
public class BasediceToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "BASEDICE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            SimpleLoader<BaseDice> baseDiceLoader = new SimpleLoader<>(BaseDice.class);
            baseDiceLoader.parseLine(gameMode.getModeContext(), value, source);
            return true;
        } catch (Exception e)
        {
            Logging.errorPrint(e.getMessage());
            return false;
        }
    }
}
