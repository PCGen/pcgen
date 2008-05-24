package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with ALTDAMAGE token
 */
public class AltdamageToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ALTDAMAGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		context.getObjectContext().put(eq.getEquipmentHead(2),
				StringKey.DAMAGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(2);
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
