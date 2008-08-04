/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;
import pcgen.core.Movement;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MoveLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVE";
	}

	private void validateMove(String value, String mod)
	{
		try
		{
			if (Integer.parseInt(mod) < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Invalid movement (cannot be negative): " + mod
								+ " in MOVE: " + value);
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Invalid movement (must be an integer >= 0): " + mod
							+ " in MOVE: " + value);
		}
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Equipment)
		{
			return false;
		}
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 1)
		{
			cm = new Movement(1);
			String mod = moves.nextToken();
			validateMove(value, mod);
			cm.assignMovement(0, "Walk", mod);
		}
		else
		{
			cm = new Movement(moves.countTokens() / 2);

			int x = 0;
			while (moves.countTokens() > 1)
			{
				String type = moves.nextToken();
				String mod = moves.nextToken();
				validateMove(value, mod);
				cm.assignMovement(x++, type, mod);
			}
			if (moves.countTokens() != 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Badly formed MOVE token "
								+ "(extra value at end of list): " + value);
			}
		}
		cm.setMoveRatesFlag(0);
		context.obj.addToList(obj, ListKey.MOVEMENT, cm);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Movement> changes = context.getObjectContext().getListChanges(
				obj, ListKey.MOVEMENT);
		Collection<Movement> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Movement m : added)
		{
			StringBuilder sb = new StringBuilder();
			m.addTokenContents(sb);
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
