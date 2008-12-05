package plugin.lsttokens.equipment;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTTYPE token 
 */
public class AlttypeToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "ALTTYPE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		EquipmentHead head = eq.getEquipmentHead(2);
		if (value.startsWith(".CLEAR"))
		{
			context.getObjectContext().removeList(head, ListKey.TYPE);
			if (value.length() == 6)
			{
				return true;
			}
			else if (value.charAt(6) == '.')
			{
				value = value.substring(7);
				if (isEmpty(value))
				{
					Logging
						.errorPrint(getTokenName()
							+ "started with .CLEAR. but expected to have a Type after .: "
							+ value);
					return false;
				}
			}
			else
			{
				Logging
					.errorPrint(getTokenName()
						+ "started with .CLEAR but expected next character to be .: "
						+ value);
				return false;
			}
		}
		if (hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.DOT);

		boolean bRemove = false;
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if (bRemove)
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().removeFromList(head, ListKey.TYPE,
					type);
				bRemove = false;
			}
			else if ("ADD".equals(aType))
			{
				bRemove = false;
			}
			else if ("REMOVE".equals(aType))
			{
				bRemove = true;
			}
			else if ("CLEAR".equals(aType))
			{
				Logging.errorPrint("Non-sensical use of .CLEAR in "
					+ getTokenName() + ": " + value);
				return false;
			}
			else
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().addToList(head, ListKey.TYPE, type);
			}
		}
		if (bRemove)
		{
			Logging.errorPrint(getTokenName()
				+ "ended with REMOVE, so didn't have any Type to remove: "
				+ value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHead(2);
		Changes<Type> changes =
				context.getObjectContext().getListChanges(head, ListKey.TYPE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Collection<?> added = changes.getAdded();
		boolean globalClear = changes.includesGlobalClear();
		if (globalClear)
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (added != null && !added.isEmpty())
		{
			if (globalClear)
			{
				sb.append(Constants.DOT);
			}
			sb.append(StringUtil.join(added, Constants.DOT));
		}
		if (sb.length() == 0)
		{
			context.addWriteMessage(getTokenName()
				+ " was expecting non-empty changes to include "
				+ "added items or global clear");
			return null;
		}
		return new String[]{sb.toString()};
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
