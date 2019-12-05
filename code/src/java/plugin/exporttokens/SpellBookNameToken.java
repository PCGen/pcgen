package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLBOOKNAME Token
 */
public class SpellBookNameToken extends AbstractExportToken
{

    @Override
    public String getTokenName()
    {
        return "SPELLBOOKNAME";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        String bString = aTok.nextToken();
        final int bookNum;

        if ("SPELLBOOKNAME".equals(bString))
        {
            bookNum = Integer.parseInt(aTok.nextToken());
        } else
        {
            Logging.errorPrint("Old syntax SPELLBOOKNAMEx will be replaced for SPELLBOOKNAME.x");
            bookNum = Integer.parseInt(tokenSource.substring(13));
        }

        return display.getSpellBookNames().get(bookNum);
    }

}
