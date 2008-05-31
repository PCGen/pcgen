package plugin.lsttokens.spell;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.PointCost;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DURATION Token
 */
public class SpellPointCostToken implements CDOMPrimaryToken<Spell>
{

	public String getTokenName()
	{
		return "SPELLPOINTCOST";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		boolean hasTotal = false;
		boolean hasNonTotal = false;

		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tok))
			{
				if (!first)
				{
					Logging.errorPrint("Non-sensical use of .CLEAR in "
							+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().removeList(spell,
						ListKey.SPELL_POINT_COST);
			}
			else
			{
				int equalLoc = tok.indexOf(Constants.EQUALS);
				String type;
				String cost;

				if (equalLoc == -1)
				{
					// Total
					hasTotal = true;
					type = "TOTAL";
					cost = tok;
				}
				else
				{
					hasNonTotal = true;
					if (tok.lastIndexOf(Constants.EQUALS) != equalLoc)
					{
						Logging.errorPrint("Invalid number of Arguments in "
								+ getTokenName() + "(" + spell.getDisplayName()
								+ "): " + value);
						return false;
					}
					type = tok.substring(0, equalLoc);
					cost = tok.substring(equalLoc + 1);
				}
				int costInt;
				try
				{
					costInt = Integer.parseInt(cost);
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Invalid Value in " + getTokenName()
							+ "(" + spell.getDisplayName() + "): " + value
							+ ".  Value must be an integer.");
					return false;
				}
				context.getObjectContext().addToList(spell,
						ListKey.SPELL_POINT_COST, new PointCost(type, costInt));
			}
			first = false;
		}
		if (hasTotal && hasNonTotal)
		{
			// TODO Error here?
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell eq)
	{
		Changes<PointCost> changes = context.getObjectContext().getListChanges(
				eq, ListKey.SPELL_POINT_COST);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (PointCost q : changes.getAdded())
		{
			set.add(new StringBuilder().append(q.getType()).append(
					Constants.PIPE).append(q.getCost()).toString());
		}
		return new String[] { StringUtil.join(changes.getAdded(),
				Constants.PIPE) };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
