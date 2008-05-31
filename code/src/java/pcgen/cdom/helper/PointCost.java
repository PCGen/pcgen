package pcgen.cdom.helper;

import pcgen.cdom.base.Constants;

public class PointCost
{

	private final String type;
	private final int pointCost;

	public PointCost(String key, int cost)
	{
		type = key;
		pointCost = cost;
	}

	public String getType()
	{
		return type;
	}

	public int getCost()
	{
		return pointCost;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() ^ pointCost;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof PointCost)
		{
			PointCost other = (PointCost) o;
			return type.equals(other.type) && pointCost == other.pointCost;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return getType() + Constants.EQUALS + getCost();
	}
}
