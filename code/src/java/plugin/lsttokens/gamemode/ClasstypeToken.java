package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.ClassType;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.util.Logging;

/**
 * Class deals with CLASSTYPE Token
 */
public class ClasstypeToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "CLASSTYPE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            SimpleLoader<ClassType> methodLoader = new SimpleLoader<>(ClassType.class);
            methodLoader.parseLine(gameMode.getModeContext(), value, source);
            return true;
        } catch (Exception e)
        {
            Logging.errorPrint(e.getMessage());
            return false;
        }
    }
}
