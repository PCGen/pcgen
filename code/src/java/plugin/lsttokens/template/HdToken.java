package plugin.lsttokens.template;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
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
					value.substring(hdStr.length() + 1) + "|PREHD:MIN=" + hdStr;
			try
			{
				return PObjectLoader.parseTag(template, tagValue);
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
