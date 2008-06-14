package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Logging;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "CATEGORY";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setCategory(value);
		AbilityCategory cat = SettingsHandler.getGame().getAbilityCategory(value);
		if (cat == null)
		{
			Logging.errorPrint("Cannot find Ability Category: " + value);
			return false;
		}
		ability.setCDOMCategory(cat);
		return true;
	}
}
