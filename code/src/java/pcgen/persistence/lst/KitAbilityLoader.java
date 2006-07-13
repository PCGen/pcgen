
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Deals with loading Abilities for Kits
 */
public class KitAbilityLoader
{
	/**
	 * Parse the line
	 * 
	 * @param kit
	 * @param colString
	 * @param isFeat
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(Kit kit, String colString, boolean isFeat)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(colString,
				SystemLoader.TAB_DELIM);
		KitAbilities kitAbility = null;
		String ability = colToken.nextToken();
		if (isFeat)
		{
			kitAbility = new KitAbilities(ability, "FEAT", true);
		}
		else
		{
			kitAbility = new KitAbilities(ability, "", false);
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(KitAbilityLstToken.class);
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

			// We will find the first ":" for the "controlling" line token
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new PersistenceLayerException();
			}
			KitAbilityLstToken token = (KitAbilityLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kitAbility, value))
				{
					Logging.errorPrint("Error parsing Kit Ability tag "
							+ kitAbility.getObjectName() + ':' + colString
							+ "\"");
				}
			}
			else if (BaseKitLoader.parseCommonTags(kitAbility, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown Kit Ability info: \"" + colString
						+ "\"");
			}
		}
		kit.addObject(kitAbility);
	}
}
