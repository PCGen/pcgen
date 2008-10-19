package plugin.lsttokens.equipmentmodifier;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with BONUS token
 */
public class BonusToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(LoadContext context, EquipmentModifier obj,
			String value) throws PersistenceLayerException
	{
		BonusObj bon = Bonus.newBonus(value);
		if (bon == null)
		{
			Logging.errorPrint(getTokenName() + " was given invalid bonus: "
					+ value);
			return false;
		}
		bon.setCreatorObject(obj);
		bon.setTokenSource(getTokenName());
		context.obj.addToList(obj, ListKey.BONUS, bon);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier obj)
	{
		Changes<BonusObj> changes = context.obj.getListChanges(obj,
				ListKey.BONUS);
		if (changes == null || changes.isEmpty())
		{
			// Empty indicates no token present
			return null;
		}
		// CONSIDER need to deal with removed...
		Collection<BonusObj> added = changes.getAdded();
		String tokenName = getTokenName();
		Set<String> bonusSet = new TreeSet<String>();
		for (BonusObj bonus : added)
		{
			if (tokenName.equals(bonus.getTokenSource()))
			{
				bonusSet.add(bonus.toString());
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[bonusSet.size()]);
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
