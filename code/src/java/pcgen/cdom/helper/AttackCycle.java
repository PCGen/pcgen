package pcgen.cdom.helper;

import pcgen.cdom.base.Constants;
import pcgen.util.enumeration.AttackType;

public class AttackCycle
{

	private final AttackType type;
	private final int value;

	public AttackCycle(AttackType key, int val)
	{
		type = key;
		value = val;
	}

	public AttackType getAttackType()
	{
		return type;
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() ^ value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AttackCycle)
		{
			AttackCycle other = (AttackCycle) o;
			return type.equals(other.type) && value == other.value;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return type.getIdentifier() + Constants.PIPE + value;
	}
}
