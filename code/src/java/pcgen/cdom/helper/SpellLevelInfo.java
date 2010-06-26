package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

public class SpellLevelInfo
{

	private final PrimitiveChoiceFilter<PCClass> filter;
	private final int minimumLevel;
	private final Formula maximumLevel;

	public SpellLevelInfo(PrimitiveChoiceFilter<PCClass> pcf, int minLevel,
		Formula maxLevel)
	{
		filter = pcf;
		minimumLevel = minLevel;
		maximumLevel = maxLevel;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(filter.getLSTformat());
		sb.append(Constants.PIPE);
		sb.append(minimumLevel);
		sb.append(Constants.PIPE);
		sb.append(maximumLevel);
		return sb.toString();
	}

	public Collection<SpellLevel> getLevels(PlayerCharacter pc)
	{
		List<PCClass> classList = pc.getClassList();
		List<SpellLevel> list = new ArrayList<SpellLevel>();
		for (PCClass cl : classList)
		{
			if (filter.allow(pc, cl))
			{
				int max =
						maximumLevel.resolve(pc, cl.getQualifiedKey())
							.intValue();
				for (int i = minimumLevel; i <= max; ++i)
				{
					list.add(new SpellLevel(cl, i));
				}
			}
		}
		return list;
	}

}
