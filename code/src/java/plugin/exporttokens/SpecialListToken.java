package plugin.exporttokens;

import java.util.Iterator;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * @author karianna
 * Class deals with SPECIALLIST Token
 */
public class SpecialListToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "SPECIALLIST";

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
		String retString = "";
		
		String delim = tokenSource.substring(11);

		if ("".equals(delim))
		{
			delim = ", ";
		}

		int i = 0;
		pc.getSpecialAbilityTimesList().size();

		for (Iterator e = pc.getSpecialAbilityTimesList().iterator(); e.hasNext();)
		{
			if (i++ > 0)
			{
				retString += delim;
			}

			retString += (String) e.next();
		}

		return retString;
	}
	
}
