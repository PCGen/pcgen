package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HIDDENEQUIPTYPES Token
 */
public class HiddenfeattypesToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "HIDDENFEATTYPES";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            gameMode.addHiddenType(Ability.class, st.nextToken());
        }
        return true;
    }
}
