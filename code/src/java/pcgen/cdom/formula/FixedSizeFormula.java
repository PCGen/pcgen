package pcgen.cdom.formula;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;

public class FixedSizeFormula implements Formula
{

	private final SizeAdjustment size;

	public FixedSizeFormula(SizeAdjustment s)
	{
		size = s;
	}

	@Override
	public String toString()
	{
		return size.get(StringKey.ABB);
	}

	@Override
	public int hashCode()
	{
		return size.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof FixedSizeFormula
			&& size.equals(((FixedSizeFormula) o).size);
	}

	public Integer resolve(PlayerCharacter pc, String source)
	{
		return Globals.sizeInt(size.getAbbreviation());
	}
}
