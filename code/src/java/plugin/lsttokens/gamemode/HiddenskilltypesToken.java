package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.core.Skill;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HIDDENSKILLTYPES Token
 */
public class HiddenskilltypesToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "HIDDENSKILLTYPES";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            gameMode.addHiddenType(Skill.class, st.nextToken());
        }
        return true;
    }
}
