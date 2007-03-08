package plugin.exporttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
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
		String tokens[] = tokenSource.split(",", 2);
		String tokenHead = tokens[0];
		String sourceText = "\n";

		if(tokens.length == 2)
		{
			sourceText = tokens[1];
		}

		String headTokens[] = tokenHead.split("\\.");
		String subToken = headTokens[1];

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
		if (-1 != index)
		{
			List<String> stringList = getLineForMiscList(index, pc);
			if(3 == headTokens.length)
			{
				buf.append(stringList.get(Integer.parseInt(headTokens[2])));
			}
			else
			{
				// This should be deprecated now
				// For tags like the following in FOR loops
				// will add after the ',' at end of each line
				// |MISC.MAGIC,</fo:block><fo:block font-size="7pt">|
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
		return new ArrayList<String>(Arrays.asList(aPC.getMiscList().get(index).split("\r?\n")));
	}

}
