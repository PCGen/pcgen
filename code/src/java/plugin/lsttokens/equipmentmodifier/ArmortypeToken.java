package plugin.lsttokens.equipmentmodifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.modifier.ChangeArmorType;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ARMORTYPE token
 */
public class ArmortypeToken extends AbstractToken implements
		CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "ARMORTYPE";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		ChangeArmorType cat;
		if (pipeLoc == -1)
		{
			Logging.deprecationPrint(getTokenName()
					+ " has no PIPE character: Must be of the form old|new");
			cat = new ChangeArmorType(value, null);
		}
		else if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName()
					+ " has too many PIPE characters: "
					+ "Must be of the form old|new");
			return false;
		}
		else
		{
			/*
			 * TODO Are the ArmorTypes really a subset of Encumbrence?
			 */
			try
			{
				String oldType = value.substring(0, pipeLoc);
				String newType = value.substring(pipeLoc + 1);
				/*
				 * TODO Need some check if the Armor Types in value are not valid...
				 */
				cat = new ChangeArmorType(oldType, newType);
			}
			catch (IllegalArgumentException e)
			{
				return false;
			}
		}
		context.getObjectContext().addToList(mod, ListKey.ARMORTYPE, cat);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Changes<ChangeArmorType> changes = context.getObjectContext()
				.getListChanges(mod, ListKey.ARMORTYPE);
		Collection<ChangeArmorType> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		TreeMap<String, String> m = new TreeMap<String, String>();
		for (LSTWriteable ab : added)
		{
			ChangeArmorType cat = (ChangeArmorType) ab;
			String source = cat.getSourceType();
			String result = cat.getResultType();
			m.put(source, result);
		}
		List<String> list = new ArrayList<String>();
		for (Entry<String, String> me : m.entrySet())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(me.getKey());
			String value = me.getValue();
			if (value != null)
			{
				sb.append(Constants.PIPE);
				sb.append(value);
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
