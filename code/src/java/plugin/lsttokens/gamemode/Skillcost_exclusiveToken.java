package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_EXCLUSIVE Token
 */
public class Skillcost_exclusiveToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SKILLCOST_EXCLUSIVE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            gameMode.setSkillCost_Exclusive(Integer.parseInt(value));
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
