package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with INFOTEXT Token
 */
public class InfotextToken implements CDOMPrimaryToken<Campaign>, InstallLstToken
{

	public String getTokenName()
	{
		return "INFOTEXT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.put(StringKey.INFO_TEXT, value);
		return true;
	}

	public boolean parse(LoadContext context, Campaign camp, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(camp, StringKey.INFO_TEXT, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign camp)
	{
		String infotext =
				context.getObjectContext().getString(camp, StringKey.INFO_TEXT);
		if (infotext == null)
		{
			return null;
		}
		return new String[]{infotext};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
