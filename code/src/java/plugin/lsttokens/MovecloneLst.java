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
public class MovecloneLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVECLONE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);

		if (moves.countTokens() != 3)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Invalid Version of MOVECLONE detected: " + value
							+ "\n  MOVECLONE has 3 arguments: "
							+ "SourceMove,DestinationMove,Modifier");
			return false;
		}

		String oldType = moves.nextToken();
		String newType = moves.nextToken();
		String formulaString = moves.nextToken();

		if (formulaString.startsWith("/"))
		{
			int denom = Integer.parseInt(formulaString.substring(1));
			if (denom <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " was expecting a Positive Integer "
						+ "for dividing Movement, was : "
						+ formulaString.substring(1));
				return false;
			}
		}
		else if (formulaString.startsWith("*"))
		{
			int mult = Integer.parseInt(formulaString.substring(1));
			if (mult < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " was expecting an "
						+ "Integer >= 0 for multiplying Movement, was : "
						+ formulaString.substring(1));
				return false;
			}
		}
		else if (formulaString.startsWith("+"))
		{
			int add = Integer.parseInt(formulaString.substring(1));
			if (add < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " was expecting a Non-Negative "
						+ "Integer for adding Movement, was : "
						+ formulaString.substring(1));
				return false;
			}
		}
		Movement cm = new Movement(2);
		cm.assignMovement(0, oldType, "0");
		cm.assignMovement(1, newType, formulaString);
		cm.setMoveRatesFlag(2);
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
