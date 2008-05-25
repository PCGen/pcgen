package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag
 * in the definition of an Equipment Modifier.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements CDOMPrimaryToken<EquipmentModifier>
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, EquipmentModifier eqm,
			String value)
	{
		Visibility vis;
		if (value.equals("QUALIFY"))
		{
			vis = Visibility.QUALIFY;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.getObjectContext().put(eqm, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier eqm)
	{
		Visibility vis = context.getObjectContext().getObject(eqm,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.QUALIFY))
		{
			visString = "QUALIFY";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for a EqMod");
			return null;
		}
		return new String[] { visString };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
