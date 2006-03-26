package pcgen.persistence.lst;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * This class loads in and parses the BASEDICE tag
 */
public class SponsorLoader extends LstLineFileLoader {
	
	/** Constructor */
	public SponsorLoader() {
	}
	
	/**
	 * Parses a line 
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(String lstLine, URL sourceURL) throws PersistenceLayerException {
		StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		Map sponsor = new HashMap();

		Map tokenMap = TokenStore.inst().getTokenMap(SponsorLstToken.class);
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
			
			SponsorLstToken token = (SponsorLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "sponsors.lst", sourceURL.toString(), value);
				if (!token.parse(sponsor, value)) {
					Logging.errorPrint("Error parsing sponsor: from sponsors.lst ");
				}
			}
			else {
				Logging.errorPrint("Invalid sub tag " + token + " on SPONSOR line");
				throw new PersistenceLayerException("Invalid sub tag " + token + " on SPONSOR line");
			}
		}
		Globals.addSponsor(sponsor);
	}
	
	public static String getConvertedSponsorPath(String file) {
		String convertedPath = SettingsHandler.getPcgenSponsorDir().getAbsolutePath() + File.separator + file;
		// Not a URL; make sure to fix the path syntax
		convertedPath = CoreUtility.fixFilenamePath(convertedPath);
	
		// Make sure the path starts with a separator
		if (!convertedPath.startsWith(File.separator))
		{
			convertedPath = File.separator + convertedPath;
		}
	
		// Return the final result
		try
		{
			return new URL("file:" + convertedPath).toString();
		}
		catch (MalformedURLException e) {}
		return "";
	}

}
