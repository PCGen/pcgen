package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with BASEITEM token 
 */
public class BaseitemToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{
	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	@Override
	public String getTokenName()
	{
		return "BASEITEM";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(eq, ObjectKey.BASE_ITEM,
			context.ref.getCDOMReference(EQUIPMENT_CLASS, value));
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		CDOMSingleRef<Equipment> ref =
				context.getObjectContext().getObject(eq, ObjectKey.BASE_ITEM);
		if (ref == null)
		{
			return null;
		}
		return new String[]{ref.getLSTformat()};
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
