package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class SkillmultiplierToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SKILLMULTIPLIER";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
        while (aTok.hasMoreTokens())
        {
            gameMode.addSkillMultiplierLevel(aTok.nextToken());
        }
        return true;
    }
}
