package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with BONUS token 
 */
public class BonusToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "BONUS";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		final BonusObj aBonus = Bonus.newBonus(value);
		
		if (aBonus != null)
		{
			aBonus.setCreatorObject(mod);
			mod.addToListFor(ListKey.BONUS, aBonus);
		}
		return (aBonus != null);
	}
}
