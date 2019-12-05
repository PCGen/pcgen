package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class Skillcost_classToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SKILLCOST_CLASS";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            gameMode.setSkillCost_Class(Integer.parseInt(value));
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
