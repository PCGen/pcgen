package plugin.exporttokens.deprecated;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Class deals with MISC Token
 */
public class MiscToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "MISC";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String[] tokens = tokenSource.split(",", 2);
		String tokenHead = tokens[0];
		String sourceText = "\n";

		if (tokens.length == 2)
		{
			sourceText = tokens[1];
		}

		String[] headTokens = tokenHead.split("\\.");
		String subToken = headTokens[1];

		PCStringKey key;
		if ("FUNDS".equals(subToken))
		{
			key = PCStringKey.ASSETS;
		}
		else if ("COMPANIONS".equals(subToken))
		{
			key = PCStringKey.COMPANIONS;
		}
		else if ("MAGIC".equals(subToken))
		{
			key = PCStringKey.MAGIC;
		}
		else
		{
			return Constants.EMPTY_STRING;
		}

		StringBuilder buf = new StringBuilder();
		String[] stringList = pc.getSafeStringFor(key).split("\r?\n");
		if (3 == headTokens.length)
		{
			buf.append(stringList[Integer.parseInt(headTokens[2])]);
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

		return buf.toString().trim();
	}
}
