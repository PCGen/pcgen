/**
 * 
 */
package pcgen.core.chooser;

import java.util.List;

class ChooseController<T>
{
	public ChooseController()
	{
		// Nothing to build here
	}

	public int getPool()
	{
		return 1;
	}

	public boolean isMultYes()
	{
		return false;
	}

	public boolean isStackYes()
	{
		return false;
	}

	public double getCost()
	{
		return 1.0;
	}

	public int getTotalChoices()
	{
		return 1;
	}

	public void adjustPool(List<? extends T> selected)
	{
		// Ignore
	}
}