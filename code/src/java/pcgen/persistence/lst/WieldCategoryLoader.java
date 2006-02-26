package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.character.WieldCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class WieldCategoryLoader  {
	public WieldCategoryLoader() {
	}
	
	public void parseLine(GameMode gameMode, String lstLine) throws PersistenceLayerException {
		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		WieldCategory cat = null;
		String preKey = null;
		String preVal = null;

		Map tokenMap = TokenStore.inst().getTokenMap(WieldCategoryLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
			}
			
			WieldCategoryLstToken token = (WieldCategoryLstToken) tokenMap.get(key);

			if (key.equals("WIELDCATEGORY")) {
				final String value = colString.substring(idxColon + 1).trim();
				cat = gameMode.getWieldCategory(value);

				if (cat == null) {
					cat = new WieldCategory(value);
					gameMode.addWieldCategory(cat);
				}
			}
			else if (colString.startsWith("PREVAR")) {
				// a PREVARxx formula used to switch
				// weapon categories based on size
				preKey = colString;
			}
			else if (key.equals("SWITCH")) {
				// If matches PRE, switch category to this
				preVal = colString.substring(7);
			}
			else if (token != null) {
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Wield Category", "miscinfo.lst from the " + gameMode.getName() + " Game Mode", value);
				if (!token.parse(cat, value)) {
					Logging.errorPrint("Error parsing wield catrgory:" + "miscinfo.lst from the " + gameMode.getName() + " Game Mode" + ':' + colString + "\"");
				}
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on WIELDCATEGORY line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on WIELDCATEGORY line");
			}
		}
		if ((cat != null) && (preVal != null) && (preKey != null)) {
			cat.addSwitchMap(preKey, preVal);
		}
	}
}
