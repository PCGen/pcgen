package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMSingleRef;

public class FollowerLimit
{

	private final CDOMSingleRef<CompanionList> ref;
	private final Formula f;

	public FollowerLimit(CDOMSingleRef<CompanionList> cl, Formula limit)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Reference for FollowerLimit cannot be null");
		}
		if (limit == null)
		{
			throw new IllegalArgumentException(
					"Formula for FollowerLimit cannot be null");
		}
		ref = cl;
		f = limit;
	}

	public CDOMSingleRef<CompanionList> getCompanionList()
	{
		return ref;
	}

	public Formula getValue()
	{
		return f;
	}

	@Override
	public int hashCode()
	{
		return ref.hashCode() * 31 + f.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FollowerLimit)
		{
			FollowerLimit other = (FollowerLimit) o;
			return ref.equals(other.ref) && f.equals(other.f);
		}
		return false;
	}

}
