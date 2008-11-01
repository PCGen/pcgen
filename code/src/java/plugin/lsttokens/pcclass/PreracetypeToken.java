package plugin.lsttokens.pcclass;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Class deals with PRERACETYPE Token
 */
public class PreracetypeToken implements CDOMPrimaryToken<PCClass>,
		DeferredToken<PCClass>
{

	private static final String TOKEN_ROOT = "RACETYPE";

	public String getTokenName()
	{
		return "PRERACETYPE";
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
		throws PersistenceLayerException
	{
		Prerequisite p = new Prerequisite();
		p.setKind("RACETYPE");
		p.setOperand(value);
		p.setOperator(PrerequisiteOperator.GTEQ);
		context.obj.put(obj, p);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		Set<String> set = new TreeSet<String>();
		Changes<Prerequisite> changes = context.obj.getPrerequisiteChanges(obj);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		for (Prerequisite p : changes.getAdded())
		{
			String kind = p.getKind();
			if (kind == null
				|| kind.regionMatches(true, 0, TOKEN_ROOT, 0, Math.min(
					TOKEN_ROOT.length(), kind.length())))
			{
				set.add(p.getOperand());
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

	public boolean process(LoadContext context, PCClass obj)
	{
		List<Prerequisite> prerequisiteList = obj.getPrerequisiteList();
		if (prerequisiteList != null)
		{
			for (Prerequisite p : prerequisiteList)
			{
				if ("RACETYPE".equalsIgnoreCase(p.getKind()))
				{
					if (!obj.isMonster())
					{
						Logging.errorPrint("PCClass " + obj.getKeyName()
							+ " is not a Monster, but used PRERACETYPE");
						return false;
					}
				}
			}
		}
		return true;
	}

}
