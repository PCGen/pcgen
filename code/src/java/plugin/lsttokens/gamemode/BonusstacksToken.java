package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUSSTACKS Token
 */
public class BonusstacksToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "BONUSSTACKS";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
        while (tok.hasMoreTokens())
        {
            final String type = tok.nextToken();
            if ("CLEAR".equals(type))
            {
                gameMode.clearBonusStacksList();
            } else
            {
                gameMode.addToBonusStackList(type.toUpperCase());
            }
        }
        return true;
    }
}
