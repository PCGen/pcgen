package pcgen.persistence.lst;

import java.util.Map;

import pcgen.core.kit.BaseKit;
import pcgen.util.Logging;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

/**
 * Loads the Base Kit
 */
public class BaseKitLoader {

	/**
	 * Parse the common tags
	 * @param obj
	 * @param tag
	 * @return true if parse OK
	 * @throws PersistenceLayerException 
	 */
	public static boolean parseCommonTags(BaseKit obj, final String tag)
		throws PersistenceLayerException
	{
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(BaseKitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = tag.indexOf(':');
		String key = "";
		try
		{
			key = tag.substring(0, idxColon);
		}
		catch(StringIndexOutOfBoundsException e) {
			throw new PersistenceLayerException();
		}
		BaseKitLstToken token = (BaseKitLstToken) tokenMap.get(key);

		if (token != null)
		{
			final String value = tag.substring(idxColon + 1);
			LstUtils.deprecationCheck(token, obj.toString(), obj.toString(), value);
			if (!token.parse(obj, value))
			{
				Logging.errorPrint("Error parsing Kit tag " + obj.toString() + ':' + tag + "\"");
			}
		}
		else if (key.startsWith("PRE") || key.startsWith("!PRE"))
		{
			final PreParserFactory factory = PreParserFactory.getInstance();
			Prerequisite prereq = factory.parse( tag );

			obj.addPreReq(prereq);
			return true;
		}

		return true;
	}
}
