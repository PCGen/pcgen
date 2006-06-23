package pcgen.persistence.lst;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads SOURCE
 */
public class SourceLoader {

	/**
	 * @param lstLine
	 * @param sourceFile
	 * @return Map
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public static Map parseLine(String lstLine, String sourceFile)
	{
		Map sourceMap = new HashMap();

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(SourceLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			SourceLstToken token = (SourceLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, "SOURCE", sourceFile, value);
				if (!token.parse(sourceMap, value))
				{
					Logging.errorPrint("Error parsing source: " + colString + " in: " + sourceFile);
				}
			}
			else
			{
				Logging.errorPrint("Unknown tag '" + colString + "' in: " + sourceFile);
			}

		}

		return sourceMap;
	}

	/**
	 * This method parses a line in an LST file containing the source information
	 * into the map form used by a PObject.
	 *
	 * @param value String LST formatted source information line
	 * @return Map of source forms
	 */
	public static Map<String, String> parseSource(String value)
	{
		Map<String, String> sourceMap = new HashMap<String, String>();
		if(value.indexOf("|") != -1) {
			LstUtils.deprecationWarning("Use of SOURCELONG:<value>|SOURCESHORT:<value>|SOURCEWEB:<value>|SOURCEPAGE:<value> is deprecated.  These need to be split up into separate tokens");
		}
		StringTokenizer aTok = new StringTokenizer(value, "|");

		while (aTok.hasMoreTokens())
		{
			String arg = aTok.nextToken();
			String key = arg.substring(6, arg.indexOf(':'));
			String val = arg.substring(arg.indexOf(':') + 1);
			sourceMap.put(key, val);
		}

		return sourceMap;
	}
}
