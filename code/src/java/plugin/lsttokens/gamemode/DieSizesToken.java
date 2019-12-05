package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.Logging;

public class DieSizesToken implements GameModeLstToken
{

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        StringTokenizer aTok = new StringTokenizer(value, Constants.COMMA);
        List<Integer> dieSizes = new ArrayList<>();
        while (aTok.hasMoreTokens())
        {
            // trim in case there is training\leading whitespace after the comma split
            String aString = aTok.nextToken().trim();

            try
            {
                if (aString.contains("MIN="))
                {
                    int die = Integer.parseInt(aString.substring(4));
                    gameMode.setMinDieSize(die);
                    dieSizes.add(die);
                } else if (aString.contains("MAX="))
                {
                    int die = Integer.parseInt(aString.substring(4));
                    gameMode.setMaxDieSize(die);
                    dieSizes.add(die);
                } else
                {
                    int die = Integer.parseInt(aString);
                    dieSizes.add(die);
                }
            } catch (NumberFormatException e)
            {
                Logging.errorPrint(
                        "Invalid integer value for DIESIZES: " + aString + ".  Original value: DIESIZES:" + value);
            }

        }
        if (dieSizes.isEmpty())
        {
            Logging.errorPrint("Invalid DIESIZES contained no values:" + value);
            return false;
        }

        gameMode.setDieSizes(dieSizes.stream().mapToInt(i -> i).toArray());
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "DIESIZES";
    }

}
