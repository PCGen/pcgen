package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLRANGE Token
 */
public class SpellrangeToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SPELLRANGE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer aTok = new StringTokenizer(value, "|");

        if (aTok.countTokens() != 2)
        {
            Logging.errorPrint("Invalid SPELLRANGE: " + value);
            return false;
        }

        String aRange = aTok.nextToken().toUpperCase();
        String aFormula = aTok.nextToken();
        gameMode.addSpellRange(aRange, aFormula);
        return true;
    }
}
