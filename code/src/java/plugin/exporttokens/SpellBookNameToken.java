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

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "SPELLBOOKNAME";
	}

	/**
	 * @see pcgen.io.exporttoken.AbstractExportToken#getToken(java.lang.String, pcgen.core.display.CharacterDisplay, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		String bString = aTok.nextToken();
		final int bookNum;

		if ("SPELLBOOKNAME".equals(bString))
		{
			bookNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			Logging
				.errorPrint("Old syntax SPELLBOOKNAMEx will be replaced for SPELLBOOKNAME.x");
			bookNum = Integer.parseInt(tokenSource.substring(13));
		}

		return display.getSpellBookNames().get(bookNum);
	}

}
