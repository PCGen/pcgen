package plugin.lsttokens.template;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken implements PCTemplateLstToken
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "HD";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate, java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (".CLEAR".equals(value))
		{
			template.clearHitDiceStrings();
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, ":");
		String hdStr = tok.nextToken();
		String typeStr = tok.nextToken();
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCTemplateLstToken.class);
		PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(typeStr);

		if (token != null)
		{
			template.addHitDiceString(value);
		}
		else
		{
			String tagValue =
					value.substring(hdStr.length() + 1) + "|PREHDSILENT:" + hdStr; 
			try
			{
				final int colonLoc = tagValue.indexOf(':');
				if (colonLoc == -1)
				{
					Logging.errorPrint("Invalid HD Token - does not contain a colon: "
							+ tagValue);
					return false;
				}
				else if (colonLoc == 0)
				{
					Logging.errorPrint("Invalid HD Token - starts with a colon: "
							+ tagValue);
					return false;
				}

				String key = tagValue.substring(0, colonLoc);
				String val = (colonLoc == tagValue.length() - 1) ? null : tagValue
						.substring(colonLoc + 1);
				LoadContext context = Globals.getContext();
				if (context.processToken(template, key, val))
				{
					context.commit();
				}
				else if (!PObjectLoader.parseTag(template, tagValue))
				{
					Logging.replayParsedMessages();
				}
				Logging.clearParseMessages();
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Failed to parse " + value + ".", e);
				return false;
			}
		}
		return true;
	}
}
