package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Deals with reading in and parsing EQSIZEPENALTY
 */
public class EqSizePenaltyLoader  {

	/** Constructor */
	public EqSizePenaltyLoader() {
		// Do Nothing
	}
	
	/**
	 * Parse the EQSIZEPENALTY line
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameMode, String lstLine) throws PersistenceLayerException {
		PObject eqSizePenaltyObj = new PObject();
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(EqSizePenaltyLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Deal with exception
			}
			
			EqSizePenaltyLstToken token = (EqSizePenaltyLstToken) tokenMap.get(key);

			if (token != null) {
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "EQ Size Penalty", "miscinfo.lst from the " + gameMode.getName() + " Game Mode", value);
				if (!token.parse(eqSizePenaltyObj, value)) {
					Logging.errorPrint("Error parsing EQ Size Penalty:" + "miscinfo.lst from the " + gameMode.getName() + " Game Mode" + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(eqSizePenaltyObj, colString)) {
				continue;
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on EQSIZEPENALTY line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on EQSIZEPENALTY line");
			}

		}

		//Now set the penalty object in this gameMode
		gameMode.setEqSizePenaltyObj(eqSizePenaltyObj);
	}
}
