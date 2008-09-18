package pcgen.rules.context;

import java.util.Map;


public class MapChanges<K, V>
{
	private final Map<K, V> positive;
	private final Map<K, V> negative;
	private final boolean clear;

	public MapChanges(Map<K, V> added, Map<K, V> removed,
		boolean globallyCleared)
	{
		positive = added;
		negative = removed;
		clear = globallyCleared;
	}

	public boolean includesGlobalClear()
	{
		return clear;
	}

	public boolean isEmpty()
	{
		return !clear && !hasAddedItems() && !hasRemovedItems();
	}

	public Map<K, V> getAdded()
	{
		return positive;
	}

	public boolean hasAddedItems()
	{
		return positive != null && !positive.isEmpty();
	}

	public Map<K, V> getRemoved()
	{
		return negative;
	}

	public boolean hasRemovedItems()
	{
		return negative != null && !negative.isEmpty();
	}
}
