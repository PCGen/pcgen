package plugin.lsttokens.ability;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken implements CDOMPrimaryToken<Ability>,
		DeferredToken<Ability>
{

	public String getTokenName()
	{
		return "CATEGORY";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
			throws PersistenceLayerException
	{
		AbilityCategory cat = SettingsHandler.getGame().getAbilityCategory(
				value);
		if (cat == null)
		{
			Logging.log(Logging.LST_ERROR, "Cannot find Ability Category: " + value);
			return false;
		}
		context.ref.reassociateCategory(cat, ability);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		//TODO this is wrong! (different from logic in parse!)
		Category<Ability> cat = context.getObjectContext().getObject(ability,
				ObjectKey.ABILITY_CAT);
		if (cat == null)
		{
			return null;
		}
		return new String[] { cat.getKeyName() };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

	public boolean process(LoadContext context, Ability obj)
	{
		if (obj.get(ObjectKey.ABILITY_CAT) == null)
		{
			Logging.errorPrint("Ability " + obj.getKeyName()
					+ " did not have a Category specified.  "
					+ "A Category is required for an Ability");
			return false;
		}
		return true;
	}

}
