package plugin.exporttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * @author karianna
 * Class deals with MISC Token
 */
public class MiscToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "MISC";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		int i = -1;

		if (tokenSource.substring(5).startsWith("FUNDS"))
		{
			i = 0;
		}
		else if (tokenSource.substring(5).startsWith("COMPANIONS"))
		{
			i = 1;
		}
		else if (tokenSource.substring(5).startsWith("MAGIC"))
		{
			i = 2;
		}

		// For tags like the following in FOR loops
		// will add after the ',' at end of each line
		// |MISC.MAGIC,</fo:block><fo:block font-size="7pt">|
		final int k = tokenSource.lastIndexOf(',');

		String sourceText;
		if (k >= 0)
		{
			sourceText = tokenSource.substring(k + 1);
		}
		else
		{
			sourceText = "";
		}

		StringBuffer buf = new StringBuffer();
		if (i >= 0)
		{
			final List<String> stringList = getLineForMiscList(i, pc);

			for ( String str : stringList )
			{
				buf.append( str );
				buf.append( sourceText );
			}
		}
		return buf.toString();
	}

	/**
	 * Helper method for getToken of MISC
	 * @param index
	 * @param aPC
	 * @return a Line to process
	 */
	private List<String> getLineForMiscList(int index, PlayerCharacter aPC)
	{
		final List<String> aArrayList = new ArrayList<String>();
		final StringTokenizer aTok = new StringTokenizer(aPC.getMiscList().get(index), "\r\n", false);

		while (aTok.hasMoreTokens())
		{
			aArrayList.add(aTok.nextToken());
		}

		return aArrayList;
	}

}
