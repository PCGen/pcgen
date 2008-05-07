package pcgen.rules.context;

import java.util.Collection;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;

public class AssociatedCollectionChanges<T> implements AssociatedChanges<T>
{
	private MapToList<T, AssociatedPrereqObject> positive;
	private MapToList<T, AssociatedPrereqObject> negative;
	private boolean clear;

	public AssociatedCollectionChanges(
		MapToList<T, AssociatedPrereqObject> added,
		MapToList<T, AssociatedPrereqObject> removed,
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

	public Collection<T> getAdded()
	{
		return positive.getKeySet();
	}

	public boolean hasAddedItems()
	{
		return positive != null && !positive.isEmpty();
	}

	public Collection<T> getRemoved()
	{
		return negative == null ? null : negative.getKeySet();
	}

	public boolean hasRemovedItems()
	{
		return negative != null && !negative.isEmpty();
	}

	public MapToList<T, AssociatedPrereqObject> getAddedAssociations()
	{
		return positive;
	}

	public MapToList<T, AssociatedPrereqObject> getRemovedAssociations()
	{
		return negative;
	}
}
