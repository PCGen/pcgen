package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class KitAbilityLoader {

	public static void parseLine(Kit kit, String colString) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(colString, SystemLoader.TAB_DELIM);
		KitAbilities kitAbility  = null;
		String ability = colToken.nextToken();
		kitAbility = new KitAbilities(ability, "", false);

		Map tokenMap = TokenStore.inst().getTokenMap(KitAbilityLstToken.class);
		while (colToken.hasMoreTokens())
		{

			// We will find the first ":" for the "controlling" line token
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			KitAbilityLstToken token = (KitAbilityLstToken) tokenMap.get(key);
	
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kitAbility, value))
				{
					Logging.errorPrint("Error parsing Kit Ability tag " + kitAbility.getObjectName() + ':' + colString + "\"");
				}
			}
			else if (BaseKitLoader.parseCommonTags(kitAbility, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown Kit Ability info: \"" + colString + "\"");
			}
		}
		kit.addObject(kitAbility);
	}
}
