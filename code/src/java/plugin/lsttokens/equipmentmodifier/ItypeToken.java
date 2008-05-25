package plugin.lsttokens.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with ITYPE token
 */
public class ItypeToken extends AbstractToken implements
		CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "ITYPE";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}
		context.getObjectContext().removeList(mod, ListKey.ITEM_TYPES);

		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
		while (tok.hasMoreTokens())
		{
			context.getObjectContext().addToList(mod, ListKey.ITEM_TYPES,
					tok.nextToken());
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				mod, ListKey.ITEM_TYPES);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil
				.join(changes.getAdded(), Constants.DOT) };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
