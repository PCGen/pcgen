package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Deals with reading in and parsing EQSIZEPENALTY
 */
public class EqSizePenaltyLoader
{

	/** Constructor */
	public EqSizePenaltyLoader()
	{
		// Do Nothing
	}

	/**
	 * Parse the EQSIZEPENALTY line
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameMode, String lstLine, URI source)
		throws PersistenceLayerException
	{
		PObject eqSizePenaltyObj = new PObject();
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(EqSizePenaltyLstToken.class);
		LoadContext context = gameMode.getContext();
		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(eqSizePenaltyObj, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				EqSizePenaltyLstToken tok = (EqSizePenaltyLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, eqSizePenaltyObj, value);
				if (!tok.parse(eqSizePenaltyObj, value))
				{
					Logging.errorPrint("Error parsing EQ Size Penalty:"
							+ "miscinfo.lst from the " + gameMode.getName()
							+ " Game Mode" + ':' + token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(eqSizePenaltyObj, token))
			{
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}

		//Now set the penalty object in this gameMode
		gameMode.setEqSizePenaltyObj(eqSizePenaltyObj);
	}
}
