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
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ",");
		String tokenHead = aTok.nextToken();

		StringTokenizer aHeadTok = new StringTokenizer(tokenHead, ".");
		aHeadTok.nextToken();
		String subToken = aHeadTok.nextToken();

		int index = -1;

		if ("FUNDS".equals(subToken))
		{
			index = 0;
		}
		else if ("COMPANIONS".equals(subToken))
		{
			index = 1;
		}
		else if ("MAGIC".equals(subToken))
		{
			index = 2;
		}

		StringBuffer buf = new StringBuffer();
		if (index >= 0)
		{
			final List<String> stringList = getLineForMiscList(index, pc);

			if(aHeadTok.hasMoreTokens())
			{
				int subIndex = Integer.parseInt(aHeadTok.nextToken());
				buf.append(stringList.get(subIndex));
			}
			else
			{
				// This should be deprecated now
				// For tags like the following in FOR loops
				// will add after the ',' at end of each line
				// |MISC.MAGIC,</fo:block><fo:block font-size="7pt">|
				String sourceText = "";
				if(aTok.hasMoreTokens())
					sourceText = aTok.nextToken();
				
				for (String str : stringList)
				{
					buf.append(str);
					buf.append(sourceText);
				}
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
		final StringTokenizer aTok =
				new StringTokenizer(aPC.getMiscList().get(index), "\r\n", false);

		while (aTok.hasMoreTokens())
		{
			aArrayList.add(aTok.nextToken());
		}

		return aArrayList;
	}

}
