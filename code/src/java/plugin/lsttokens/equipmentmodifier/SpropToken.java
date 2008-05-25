package plugin.lsttokens.equipmentmodifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.core.SpecialProperty;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with SPROP token
 */
public class SpropToken extends AbstractToken implements
		CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "SPROP";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getObjectContext().removeList(mod,
					ListKey.SPECIAL_PROPERTIES);
			return true;
		}

		SpecialProperty sa = SpecialProperty.createFromLst(value);
		if (sa == null)
		{
			return false;
		}
		context.getObjectContext().addToList(mod, ListKey.SPECIAL_PROPERTIES,
				sa);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Changes<SpecialProperty> changes = context.getObjectContext()
				.getListChanges(mod, ListKey.SPECIAL_PROPERTIES);
		Collection<SpecialProperty> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (LSTWriteable ab : added)
		{
			SpecialProperty sp = (SpecialProperty) ab;
			StringBuilder sb = new StringBuilder();
			sb.append(sp.getDisplayName());
			if (sp.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, sp
						.getPrerequisiteList()));
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
