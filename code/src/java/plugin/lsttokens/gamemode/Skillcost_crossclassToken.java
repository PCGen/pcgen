package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CROSSCLASS Token
 */
public class Skillcost_crossclassToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SKILLCOST_CROSSCLASS";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            gameMode.setSkillCost_CrossClass(Integer.parseInt(value));
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
