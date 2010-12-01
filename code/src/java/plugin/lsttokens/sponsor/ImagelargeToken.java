package plugin.lsttokens.sponsor;

import java.net.MalformedURLException;

import pcgen.cdom.content.Sponsor;
import pcgen.core.utils.CoreUtility;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with IMAGELARGE Token
 */
public class ImagelargeToken implements CDOMPrimaryToken<Sponsor>
{

	public String getTokenName()
	{
		return "IMAGELARGE";
	}

	public Class<Sponsor> getTokenClass()
	{
		return Sponsor.class;
	}

	public ParseResult parseToken(LoadContext context, Sponsor s, String value)
	{
		try
		{
			s.setLargeImage(CoreUtility.processFileToURL(value));
			return ParseResult.SUCCESS;
		}
		catch (MalformedURLException e)
		{
			return new ParseResult.Fail("Error in " + getTokenName() + ": "
					+ e.getMessage());
		}
	}

	public String[] unparse(LoadContext context, Sponsor s)
	{
		// TODO Need to unparse
		return null;
	}
}
