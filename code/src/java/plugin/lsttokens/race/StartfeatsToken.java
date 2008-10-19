package plugin.lsttokens.race;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "STARTFEATS";
	}

	public boolean parse(LoadContext context, Race race, String value)
			throws PersistenceLayerException
	{
		int bonusValue;

		try
		{
			bonusValue = Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Error encountered in "
					+ getTokenName()
					+ " was expecting value to be an integer, found: " + value);
			return false;
		}

		BonusObj bon = Bonus.newBonus("FEAT|POOL|" + bonusValue);
		if (bon == null)
		{
			Logging.errorPrint("Internal Error: " + getTokenName()
					+ " had invalid bonus");
			return false;
		}
		Prerequisite prereq = getPrerequisite("PREMULT:1,[PREHD:MIN=1],[PRELEVEL:MIN=1]");
		if (prereq == null)
		{
			Logging.errorPrint("Internal Error: " + getTokenName()
					+ " had invalid prerequisite");
			return false;
		}
		bon.addPrerequisite(prereq);
		bon.setCreatorObject(race);
		bon.setTokenSource(getTokenName());
		context.obj.addToList(race, ListKey.BONUS, bon);
		return true;
	}

	public String[] unparse(LoadContext context, Race obj)
	{
		Changes<BonusObj> changes = context.obj.getListChanges(obj,
				ListKey.BONUS);
		if (changes == null || changes.isEmpty())
		{
			// Empty indicates no token present
			return null;
		}
		// CONSIDER need to deal with removed...
		Collection<BonusObj> added = changes.getAdded();
		String tokenName = getTokenName();
		Set<String> bonusSet = new TreeSet<String>();
		for (BonusObj bonus : added)
		{
			if (tokenName.equals(bonus.getTokenSource()))
			{
				bonusSet.add(bonus.getValue());
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[bonusSet.size()]);
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

}
