package pcgen.rules.context;

import java.util.Collection;

public interface Changes<T>
{
	public boolean hasAddedItems();

	public boolean hasRemovedItems();

	public boolean includesGlobalClear();

	public Collection<T> getAdded();

	public Collection<T> getRemoved();

	public boolean isEmpty();

}
