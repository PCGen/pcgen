/**
 * 
 */
package plugin.bonustokens;

import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.BonusObj;
import pcgen.util.Logging;

/**
 * @author Joe.Frazier
 *
 */
public class SpellPointCosts extends BonusObj
{
	private static final String[] bonusHandled = {"SPELLPOINTCOST" };
	
	protected boolean parseToken(final String token)
	{
		if (token == null)
		{
			Logging.errorPrint("Malformed BONUS:SPELLPOINTCOST.");
			return false;
		}
		addBonusInfo(token);

		return true;
	}
	

	protected String unparseToken(Object obj)
	{
		return (String) obj;
	}

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
	public String toString()
	{
		return super.toString();
	}
	
}
