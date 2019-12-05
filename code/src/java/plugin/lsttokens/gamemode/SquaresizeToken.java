package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SQUARESIZE Token
 */
public class SquaresizeToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "SQUARESIZE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            gameMode.setSquareSize(Double.parseDouble(value));
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}
