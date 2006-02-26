package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.util.Logging;

import java.net.URL;
import java.util.Properties;

/**
 * Class deals with OPTION Token
 */
public class OptionToken implements CampaignLstToken {

	public String getTokenName() {
		return "OPTION";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		// We store a set of options with the campaign, so add this one in now.
		// That way when the campaign is selected the options can be set too.
		Properties options = campaign.getOptions();

		if (options == null) {
			options = new Properties();
			campaign.setOptions(options);
		}

		final int equalsPos = value.indexOf("=");

		if (equalsPos >= 0) {
			String optName = value.substring(0, equalsPos);

			if (optName.toLowerCase().startsWith("pcgen.options.")) {
				optName = optName.substring("pcgen.options.".length());
			}

			final String optValue = value.substring(equalsPos + 1);
			options.setProperty(optName, optValue);
		}
		else {
			Logging.errorPrint("Invalid option line in source file " + sourceUrl.toString() + " : " + value);
			return false;
		}
		return true;
	}
}
