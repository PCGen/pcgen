package pcgen.persistence.lst;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads and parses TAB Lst token
 */
public class TabLoader  {

	/** TAB = "tab" */
	public static final String TAB = "tab";

	/**
	 * Parses TAB LST Token
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameMode, String lstLine) throws PersistenceLayerException {
		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		Map<String, String> tab = new HashMap<String, String>();
		tab.put(TAB, "");

		Map tokenMap = TokenStore.inst().getTokenMap(TabLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Deal with Exception
			}

			TabLstToken token = (TabLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "TAB", "miscinfo.lst from the " + gameMode.getName() + " Game Mode", value);
				if (!token.parse(gameMode, tab, value)) {
					Logging.errorPrint("Error parsing tabs:" + "miscinfo.lst from the " + gameMode.getName() + " Game Mode" + ':' + colString + "\"");
				}
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on TAB line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on TAB line");
			}
		}
	}
}
