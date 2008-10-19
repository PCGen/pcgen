package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
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
 * Class deals with MONSKILL Token
 */
public class MonskillToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "MONSKILL";
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		BonusObj bon = Bonus.newBonus("0|MONSKILLPTS|NUMBER|" + value);
		if (bon == null)
		{
			Logging.errorPrint(getTokenName()
					+ " was given invalid bonus value: " + value);
			return false;
		}
		Prerequisite prereq = getPrerequisite("PRELEVELMAX:1");
		if (prereq == null)
		{
			Logging.errorPrint("Internal Error: " + getTokenName()
					+ " had invalid prerequisite");
			return false;
		}
		bon.addPrerequisite(prereq);
		bon.setCreatorObject(obj);
		bon.setTokenSource(getTokenName());
		context.obj.addToList(obj, ListKey.BONUS, bon);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
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
				StringBuilder sb = new StringBuilder();
				sb.append(bonus.getValue());
				List<Prerequisite> prereqList = new ArrayList<Prerequisite>(
						bonus.getPrerequisiteList());
				Prerequisite prereq = getPrerequisite("PRELEVELMAX:1");
				prereqList.remove(prereq);
				if (!prereqList.isEmpty())
				{
					sb.append('|');
					sb.append(getPrerequisiteString(context, prereqList));
				}
				bonusSet.add(sb.toString());
			}
		}
		if (bonusSet.isEmpty())
		{
			// This is okay - just no BONUSes from this token
			return null;
		}
		return bonusSet.toArray(new String[bonusSet.size()]);
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

}
