package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * @author karianna
 * Class deals with SPELLBOOKNAME Token
 */
public class SpellBookNameToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "SPELLBOOKNAME";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		String bString = aTok.nextToken();
		final int bookNum;

		if (TOKENNAME.equals(bString))
		{
			bookNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			Logging
				.errorPrint("Old syntax SPELLBOOKNAMEx will be replaced for SPELLBOOKNAME.x");
			bookNum = Integer.parseInt(tokenSource.substring(13));
		}

		//		final List<String> spellBooks = pc.getSpellBooks();
		//		// TODO - Remove SLAs from Spellbook "logic"
		//		spellBooks.addAll(1, pc.getSpellLikeAbilityCategories());
		//		if ( bookNum < spellBooks.size() )
		//		{
		//			return spellBooks.get(bookNum);
		//		}

		return pc.getDisplay().getSpellBookNames().get(bookNum);
	}

}
