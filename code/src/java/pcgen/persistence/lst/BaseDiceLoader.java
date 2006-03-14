package pcgen.persistence.lst;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * This class loads in and parses the BASEDICE tag
 */
public class BaseDiceLoader  {
	
	/** BASEDICE = "baseDice" */
	public static final String BASEDICE = "baseDice";
	/** UP = "up" */
	public static final String UP = "up";
	/** DOWN = "down" */
	public static final String DOWN = "down";

	/** Constructor */
	public BaseDiceLoader() {
		// Do Nothing
	}
	
	/**
	 * Parses a line 
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameMode, String lstLine) throws PersistenceLayerException {
		//BASEDICE:1d6	UP:1d8,2d6,3d6,4d6	DOWN:1d4,1d3,1d2,1,0
		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		Map bonus = new HashMap();
		bonus.put(BASEDICE, "");
		bonus.put(UP, "");
		bonus.put(DOWN, "");

		Map tokenMap = TokenStore.inst().getTokenMap(BaseDiceLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Deal with this exception
			}
			
			BaseDiceLstToken token = (BaseDiceLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Base Dice", "miscinfo.lst from the " + gameMode.getName() + " Game Mode", value);
				if (!token.parse(bonus, value)) {
					Logging.errorPrint("Error parsing base dice:" + "miscinfo.lst from the " + gameMode.getName() + " Game Mode" + ':' + colString + "\"");
				}
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on BASEDICE line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on BASEDICE line");
			}
		}

		if (!gameMode.getDamageUpMap().containsKey(bonus.get(BASEDICE))) {
			gameMode.getDamageUpMap().put(bonus.get(BASEDICE), bonus.get(UP));
			gameMode.getDamageDownMap().put(bonus.get(BASEDICE), bonus.get(DOWN));
		}
		else {
			Logging.errorPrint("Duplicate BASEDICE: tag on gamemode.");
		}
	}
}
