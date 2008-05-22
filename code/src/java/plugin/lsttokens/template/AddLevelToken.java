package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * New Token to support Adding Levels to say a Lycanthorpe template
 */
public class AddLevelToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "ADDLEVEL";
	}

	public boolean parse(LoadContext context, PCTemplate template,
			String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("No | found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
					+ " requires at format: Class|LevelCount");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint("Two | found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
					+ " requires at format: Class|LevelCount");
			return false;
		}
		String classString = value.substring(0, pipeLoc);
		if (classString.length() == 0)
		{
			Logging.errorPrint("Empty Class found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
					+ " requires at format: Class|LevelCount");
			return false;
		}
		CDOMSingleRef<PCClass> cl = context.ref.getCDOMReference(
				PCClass.class, classString);
		String numLevels = value.substring(pipeLoc + 1);
		if (numLevels.length() == 0)
		{
			Logging.errorPrint("Empty Level Count found in " + getTokenName());
			Logging.errorPrint("  " + getTokenName()
					+ " requires at format: Class|LevelCount");
			return false;
		}
		Formula f;
		try
		{
			int lvls = Integer.parseInt(numLevels);
			if (lvls <= 0)
			{
				Logging.errorPrint("Number of Levels granted in "
						+ getTokenName() + " must be greater than zero");
				return false;
			}
			f = FormulaFactory.getFormulaFor(lvls);
		}
		catch (NumberFormatException nfe)
		{
			f = FormulaFactory.getFormulaFor(numLevels);
		}
		LevelCommandFactory cf = new LevelCommandFactory(cl, f);
		context.getObjectContext().addToList(template, ListKey.ADD_LEVEL, cf);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<LevelCommandFactory> changes = context.getObjectContext()
				.getListChanges(pct, ListKey.ADD_LEVEL);
		Collection<LevelCommandFactory> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (LevelCommandFactory lcf : added)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(lcf.getLSTformat()).append(Constants.PIPE).append(
					lcf.getLevelCount().toString());
			list.add(sb.toString());
		}

		return list.toArray(new String[list.size()]);
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
