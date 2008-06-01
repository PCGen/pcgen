package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.PlayerCharacter;
import pcgen.rules.persistence.TokenUtilities;

public class SpellReferenceChoiceSet implements PrimitiveChoiceSet<CDOMListObject>
{
	private final Set<CDOMReference<? extends CDOMListObject>> set;

	public SpellReferenceChoiceSet(Collection<CDOMReference<? extends CDOMListObject>> col)
	{
		if (col == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (col.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		set = new HashSet<CDOMReference<? extends CDOMListObject>>(col);
	}

	public String getLSTformat()
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				TokenUtilities.REFERENCE_SORTER);
		sortedSet.addAll(set);
		StringBuilder sb = new StringBuilder();
		List<CDOMReference<?>> domainList = new ArrayList<CDOMReference<?>>();
		boolean needComma = false;
		for (CDOMReference<?> ref : sortedSet)
		{
			if (DomainSpellList.class.equals(ref.getReferenceClass()))
			{
				domainList.add(ref);
			}
			else
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append(ref.getLSTformat());
				needComma = true;
			}
		}
		for (CDOMReference<?> ref : domainList)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			sb.append("DOMAIN.");
			sb.append(ref.getLSTformat());
			needComma = true;
		}
		return sb.toString();
	}

	public Class<CDOMListObject> getChoiceClass()
	{
		return CDOMListObject.class;
	}

	public Set<CDOMListObject> getSet(PlayerCharacter pc)
	{
		Set<CDOMListObject> returnSet = new HashSet<CDOMListObject>();
		for (CDOMReference<? extends CDOMListObject> ref : set)
		{
			returnSet.addAll(ref.getContainedObjects());
		}
		return returnSet;
	}

	@Override
	public int hashCode()
	{
		return set.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof SpellReferenceChoiceSet)
		{
			SpellReferenceChoiceSet other = (SpellReferenceChoiceSet) o;
			return set.equals(other.set);
		}
		return false;
	}

}
