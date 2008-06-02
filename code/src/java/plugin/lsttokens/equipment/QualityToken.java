package plugin.lsttokens.equipment;

import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Quality;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ACCHECK token
 */
public class QualityToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "QUALITY";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " expecting '|', format is: "
					+ "QualityType|Quality value was: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " expecting only one '|', "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String key = value.substring(0, pipeLoc);
		if (key.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " expecting non-empty type, "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		String val = value.substring(pipeLoc + 1);
		if (val.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " expecting non-empty value, "
					+ "format is: QualityType|Quality value was: " + value);
			return false;
		}
		context.getObjectContext().addToList(eq, ListKey.QUALITY,
				new Quality(key, val));
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Quality> changes = context.getObjectContext().getListChanges(
				eq, ListKey.QUALITY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Quality q : changes.getAdded())
		{
			set.add(new StringBuilder().append(q.getQuality()).append(
					Constants.PIPE).append(q.getValue()).toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
