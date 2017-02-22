package plugin.exporttokens;

import pcgen.base.lang.StringUtil;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Class deals with SPECIALLIST Token
 */
public class SpecialListToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "SPECIALLIST";

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
		String delim = tokenSource.substring(11);

		if ("".equals(delim))
		{
			delim = ", ";
		}

		return StringUtil.join(pc.getSpecialAbilityTimesList(), delim);
	}

}
