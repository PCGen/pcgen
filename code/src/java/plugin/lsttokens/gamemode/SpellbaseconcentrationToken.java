package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SPELLBASECONCENTRATION Token
 */
public class SpellbaseconcentrationToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SPELLBASECONCENTRATION";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setSpellBaseConcentration(value);
        return true;
    }
}
