package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with RANKMODFORMULA Token
 */
public class RankmodformulaToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "RANKMODFORMULA";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setRankModFormula(value);
        return true;
    }
}
