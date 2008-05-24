package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with DAMAGE token
 */
public class DamageToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "DAMAGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		context.getObjectContext().put(eq.getEquipmentHead(1),
				StringKey.DAMAGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(1);
		if (head == null)
		{
			return null;
		}
		String damage = context.getObjectContext().getString(head,
				StringKey.DAMAGE);
		if (damage == null)
		{
			return null;
		}
		return new String[] { damage };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
