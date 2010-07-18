package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.core.bonus.BonusObj;

public class AgeSet
{

	private List<BonusObj> bonuses = null;
	private List<TransitionChoice<Kit>> kits = null;
	private final String name;
	private final int index;

	public AgeSet(String ageName, int currentAgeSetIndex)
	{
		name = ageName;
		index = currentAgeSetIndex;
	}

	public void addBonuses(List<BonusObj> list)
	{
		if (bonuses == null)
		{
			bonuses = new ArrayList<BonusObj>(list);
		}
	}

	public boolean hasBonuses()
	{
		return bonuses != null && !bonuses.isEmpty();
	}

	public int getIndex()
	{
		return index;
	}

	public String getName()
	{
		return name;
	}

	public List<BonusObj> getBonuses()
	{
		if (bonuses == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(bonuses);
	}

	public void addKits(List<TransitionChoice<Kit>> list)
	{
		if (kits == null)
		{
			kits = new ArrayList<TransitionChoice<Kit>>(list);
		}
	}

	public List<TransitionChoice<Kit>> getKits()
	{
		if (kits == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(kits);
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(index).append('|').append(name);
		if (bonuses != null)
		{
			for (BonusObj bo : bonuses)
			{
				sb.append('\t').append(bo.getLSTformat());
			}
		}
		if (kits != null)
		{
			for (TransitionChoice<Kit> tc : kits)
			{
				sb.append('\t').append(tc.getCount()).append(Constants.PIPE);
				sb.append(tc.getChoices().getLSTformat().replaceAll(
						Constants.COMMA, Constants.PIPE));
			}
		}
		return sb.toString();
	}

}
