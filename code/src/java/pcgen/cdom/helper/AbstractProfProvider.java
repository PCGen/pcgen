package pcgen.cdom.helper;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Equipment;

public abstract class AbstractProfProvider<T extends CDOMObject> extends
		ConcretePrereqObject implements ProfProvider<T>
{

	private final List<CDOMReference<T>> direct;

	private final List<CDOMReference<Equipment>> byEquipType;

	public AbstractProfProvider(List<CDOMReference<T>> profs,
			List<CDOMReference<Equipment>> equipTypes)
	{
		direct = profs;
		byEquipType = equipTypes;
	}

	public abstract boolean providesProficiencyFor(Equipment eq);

	public boolean providesProficiency(T sp)
	{
		for (CDOMReference<T> ref : direct)
		{
			if (ref.contains(sp))
			{
				return true;
			}
		}
		return false;
	}

	public boolean providesEquipmentType(String typeString)
	{
		if (typeString == null || typeString.length() == 0)
		{
			return false;
		}
		Set<String> types = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		for (String s : typeString.split("\\."))
		{
			types.add(s);
		}
		REF: for (CDOMReference<Equipment> ref : byEquipType)
		{
			StringTokenizer tok = new StringTokenizer(ref.getLSTformat()
					.substring(5), ".");
			while (tok.hasMoreTokens())
			{
				if (!types.contains(tok.nextToken()))
				{
					continue REF;
				}
			}
			return true;
		}
		return false;
	}

	public String getLstFormat()
	{
		StringBuilder sb = new StringBuilder();
		boolean typeEmpty = byEquipType.isEmpty();
		if (!direct.isEmpty())
		{
			sb.append(ReferenceUtilities.joinLstFormat(direct, Constants.PIPE));
			if (!typeEmpty)
			{
				sb.append(Constants.PIPE);
			}
		}
		if (!typeEmpty)
		{
			sb.append(ReferenceUtilities.joinLstFormat(byEquipType,
					Constants.PIPE).replaceAll("TYPE=ARMOR.", "ARMORTYPE="));
		}
		return sb.toString();
	}
}
