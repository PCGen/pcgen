package plugin.lsttokens.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with REPLACES token
 */
public class ReplacesToken extends AbstractToken implements
		CDOMPrimaryToken<EquipmentModifier>
{

	@Override
	public String getTokenName()
	{
		return "REPLACES";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		context.getObjectContext().removeList(mod, ListKey.REPLACED_KEYS);

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			CDOMSingleRef<EquipmentModifier> ref = context.ref
					.getCDOMReference(EquipmentModifier.class, tok.nextToken());
			context.getObjectContext().addToList(mod, ListKey.REPLACED_KEYS,
					ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Changes<CDOMSingleRef<EquipmentModifier>> changes = context
				.getObjectContext().getListChanges(mod, ListKey.REPLACED_KEYS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(changes
				.getAdded(), Constants.COMMA) };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
