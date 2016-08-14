package pcgen.base.calculation;

import pcgen.base.solver.Modifier;

public interface PCGenModifier<T> extends Modifier<T>
{
	public int getUserPriority();
}
