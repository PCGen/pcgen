package pcgen.persistence.lst;

import java.util.Map;

import pcgen.core.kit.BaseKit;
import pcgen.util.Logging;

public class BaseKitLoader {
	public static boolean parseCommonTags(BaseKit obj, final String tag)
	{
		Map tokenMap = TokenStore.inst().getTokenMap(BaseKitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = tag.indexOf(':');
		String key = "";
		try
		{
			key = tag.substring(0, idxColon);
		}
		catch(StringIndexOutOfBoundsException e) {
			// TODO Handle Exception
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
			obj.addPreReq(tag);
			return true;
		}

		return true;
	}
}
