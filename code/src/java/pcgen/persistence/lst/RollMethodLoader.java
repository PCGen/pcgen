package pcgen.persistence.lst;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class RollMethodLoader  {
	public static final String ROLLMETHOD = "rollMethod";
	public static final String METHOD = "method";

	public RollMethodLoader() {
	}
	
	public void parseLine(GameMode gameMode, String lstLine) throws PersistenceLayerException {
		Map method = new HashMap();
		method.put(ROLLMETHOD, "");
		method.put(METHOD, "");

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(RollMethodLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
			}
			
			RollMethodLstToken token = (RollMethodLstToken) tokenMap.get(key);

			if (token != null) {
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Roll Method", "miscinfo.lst from the " + gameMode.getName() + " Game Mode", value);
				if (!token.parse(method, value)) {
					Logging.errorPrint("Error parsing Roll Method:" + "miscinfo.lst from the " + gameMode.getName() + " Game Mode" + ':' + colString + "\"");
				}
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on ROLLMETHOD line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on ROLLMETHOD line");
			}

		}

		if(method.get(ROLLMETHOD).equals("") || method.get(METHOD).equals("")) {
			throw new PersistenceLayerException("Missing required information on ROLLMETHOD line");
		}
		//Now set the penalty object in this gameMode
		gameMode.addRollingMethod((String)method.get(ROLLMETHOD), (String)method.get(METHOD));
	}
}
